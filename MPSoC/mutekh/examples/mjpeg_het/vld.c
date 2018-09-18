
#include "srl.h"
#include "mjpeg_tasks.h"

#include "jpeg.h"
#include "block_io.h"
#include "srl_endianness.h"

#define VLD_ASSERT(x) //assert(x)

/*************************************************************
               Bit reading
**************************************************************/

typedef struct {
    srl_mwmr_t io;
    size_t available;
    uint32_t current;
    size_t len;
    uint32_t buf[8];
} bitreader_context;

static inline void bitreader_refill(bitreader_context *cont)
{
    if (cont->len == 0) {
        srl_mwmr_read( cont->io, cont->buf, 32 );
        cont->len = 8;
    }
    cont->current = endian_be32(cont->buf[8 - cont->len--]);
}

static uint32_t bitreader_get(bitreader_context *cont, size_t number)
{
    uint32_t ret = 0;
    ssize_t diff = number - cont->available;

    if ( diff >= 0 ) {
        ret = cont->current << diff;
        bitreader_refill(cont);
        cont->available = 32 - diff;

        if ( diff )
            ret |= cont->current >> (32 - diff);

    } else {

        ret = cont->current >> -diff;
        cont->available = -diff;
    }

    return ret & ((1 << number) - 1);
}

__attribute__ ((always_inline))
static inline uint8_t bitreader_get_one(bitreader_context *cont)
{
    if ( cont->available == 0 ) {
        bitreader_refill(cont);
        cont->available = 32;
    }

    return (cont->current >> --cont->available) & 1;
}

static void bitreader_init(bitreader_context *cont, srl_mwmr_t io)
{
    cont->available = 0;
    cont->current = 0;
    cont->len = 0;
    cont->io = io;
}

/*************************************************************
               Huffman
**************************************************************/

#define HUFF_MAX_SIZE(class)         ((class)?162:14) /* Memory size of HTables */

typedef struct {
    int32_t MinCode;
    int32_t MaxCode;
    int32_t ValPtr;
} huff_code;

typedef struct {
    uint8_t *HTable[4];
    huff_code code[4][16];
    uint8_t DC_Table0[HUFF_MAX_SIZE(DC_CLASS)];
    uint8_t DC_Table1[HUFF_MAX_SIZE(DC_CLASS)];
    uint8_t AC_Table0[HUFF_MAX_SIZE(AC_CLASS)];
    uint8_t AC_Table1[HUFF_MAX_SIZE(AC_CLASS)];
} huff_context;

/* component descriptor structure */
typedef struct {
	uint8_t	CID;	/* component ID */
	char QT;	/* QTable index, 2bits 	*/
	char DC_HT;	/* DC table index, 1bit */
	char AC_HT;	/* AC table index, 1bit */
	int32_t PRED;	/* DC predictor value */
} cd_t;

void huff_tables_init( huff_context *cont )
{
#if 1
    memset(cont, 0, sizeof(*cont));
#else
    int32_t i, j;

    for ( j=0; j<16; ++j ) {
        for ( i=0; i<4; ++i ) {
            cont->code[i][j].MinCode = 0;
            cont->code[i][j].MaxCode = 0;
            cont->code[i][j].ValPtr = 0;
        }
        cont->DC_Table0[j] = 0;
        cont->AC_Table0[j] = 0;
        cont->DC_Table1[j] = 0;
        cont->AC_Table1[j] = 0;
    }
#endif
    cont->HTable[0] = cont->DC_Table0;
    cont->HTable[1] = cont->DC_Table1;
    cont->HTable[2] = cont->AC_Table0;
    cont->HTable[3] = cont->AC_Table1;
}

/* Loading of Huffman table, with leaves drop ability       */
int32_t huff_load_tables( huff_context *cont, block_io *input )
{
    uint8_t aux, buf;
    int32_t size, class, id, max;
    int32_t LeavesN, LeavesT, i;
    int32_t AuxCode;

    size = block_io_read_int16(input)-2;
    //srl_log_printf(DEBUG, "size: %d\n", size);
    while ( size > 0 ) {
        aux = block_io_read_int8(input);
        //srl_log_printf(DEBUG, "aux: %02x\n",aux);
        /* AC or DC */
        class = first_quad(aux);
        /* table no */
        id = second_quad(aux);
        //srl_log_printf(DEBUG, "class: %d, id: %d\n", class, id);
        VLD_ASSERT (id <= 1);
        id = HUFF_ID(class, id);
        VLD_ASSERT(id <= 3);
        //srl_log_printf(TRACE, "Loading Table %d\n", id);
        --size;
        LeavesT = 0;
        AuxCode = 0;
        for ( i=0; i<16; i++ ) {
            buf = block_io_read_int8(input);
            LeavesN = buf;
            cont->code[id][i].ValPtr = LeavesT;
            cont->code[id][i].MinCode = AuxCode<<1;
            AuxCode = cont->code[id][i].MinCode + LeavesN;
            cont->code[id][i].MaxCode = (LeavesN) ? (AuxCode - 1) : (-1);
            LeavesT += LeavesN;
        }
        size -= 16;
        VLD_ASSERT( LeavesT <= HUFF_MAX_SIZE(class) );
        max = LeavesT;
        //srl_log_printf(DEBUG, "pass2 max: %d, LeavesT: %d\n", max, LeavesT);
        //srl_log_printf(DEBUG, "pass2: %d->%d\n", 0, max);
        for ( i=0; i<max; ++i ) {
            /* get huffman table */
            buf = block_io_read_int8(input);
            //srl_log_printf(DEBUG, "pass2 buffer=%02x\n", buf);
            cont->HTable[id][i] = buf;
            /* load in raw order */
        }
        //srl_log_printf(DEBUG, "pass2/2: %d->%d\n", max, LeavesT);
        for ( i=max; i<LeavesT; ++i ) {
            block_io_read_int8(input); // DROP
        }
        size -= LeavesT;
        //srl_log_printf(DEBUG, "VLD: Using %d words of table memory\n", LeavesT);
        //srl_log_printf(DEBUG, "new size: %d\n", size);
    }
    /* loop on tables */
    return 0;
}

/* extract a single symbol from file 
   using specified huffman table ... */
__attribute__ ((always_inline))
static inline int32_t huff_get_symbol( bitreader_context *cont, const huff_code *hc, const uint8_t *table)
{
    int32_t code = 0;
    int32_t index;

    hc--;
    do {
        code = (code<<1) | bitreader_get_one( cont );
        hc++;
    } while (code > hc->MaxCode);

    index = hc->ValPtr + code - hc->MinCode;

    VLD_ASSERT(index < HUFF_MAX_SIZE(select / 2));
    return table[index];
}


inline static int32_t intceil(int32_t N, int32_t D)
{
    return (N+D-1)/D;
}

/* transform JPEG number format into usual 2's complement format */
__attribute__ ((always_inline))
inline static long reformat(uint32_t S, int32_t good)
{
    uint32_t ext, sign;

    sign = !((S >> (good - 1)) & 1);
    ext = - (sign << good);
    return (S | ext) + sign;
}

/*
 * private huffman.c defines and macros
 */
#define HUFF_EOB                0x00
#define HUFF_ZRL                0xF0

/*************************************************************
               Jpeg VLD task
**************************************************************/

/*
 * here we unpack a 8x8 DCT block
 */
static inline int32_t
vld_decode_unpack_block(bitreader_context *bits, srl_mwmr_t output,
                        huff_context *huff, cd_t *comp)
{
    uint32_t temp;
    uint32_t i, run, cat;
    int32_t value;
    uint8_t symbol;
    int16_t T[BLOCK_SIZE];

    memset(T, 0, sizeof(T));

    /* first get the DC coefficient */
    const uint8_t *dc_table = huff->HTable[HUFF_ID(DC_CLASS, comp->DC_HT)];
    const huff_code *dc_code = huff->code[HUFF_ID(DC_CLASS, comp->DC_HT)];
    symbol = huff_get_symbol( bits, dc_code, dc_table);
    temp = bitreader_get( bits, symbol );
    value = reformat( temp, symbol );
    value += comp->PRED;
    comp->PRED = value;

	/* reoganize and unquantify -> move to ZZ and IQ  */
    T[0] = value;
	/* then the AC ones
     * if symbol found is EOB and process not finish, missing values
     * are replaced by zero
     */
    const uint8_t *ac_table = huff->HTable[HUFF_ID(AC_CLASS, comp->AC_HT)];
    const huff_code *ac_code = huff->code[HUFF_ID(AC_CLASS, comp->AC_HT)];

    for ( i=1; i<BLOCK_SIZE; i++ ) {
        symbol = huff_get_symbol( bits, ac_code, ac_table);
        //srl_log_printf(DEBUG, "huff_symbol %d: %x\n", i, symbol);
        if (symbol == HUFF_EOB) {
            //srl_log(DEBUG, "HUFF_EOB\n");
            break;
        }
        if (symbol == HUFF_ZRL) {
            //srl_log(DEBUG, "HUFF_ZRL\n");
            i += 15;
            continue;
        }
        cat = symbol & 0xf;
        run = symbol >> 4;
        i += run;
        temp = bitreader_get( bits, cat );
        value = reformat( temp, cat );
        T[i] = value;
    }
    srl_mwmr_write(output, (uint8_t *)T, 2*BLOCK_SIZE);
    return 0;
}

void vld_func_vld(struct _vld_args_t *_func_args)
{
    srl_mwmr_t input_mwmr = _func_args->input;
    srl_mwmr_t output = _func_args->output;
    srl_mwmr_t huffman_mwmr = _func_args->huffman;
    cd_t comp;
    huff_context huff;
    int32_t left;
    int32_t x_size, y_size;
    int32_t mx_size, my_size;
    uint8_t tmp;
    bitreader_context bits;

    uint8_t huffman_buffer[32];
    block_io huffman;

    bitreader_init(&bits, input_mwmr);
    block_io_init_in(&huffman, huffman_buffer, 32, huffman_mwmr);

    //srl_log(TRACE, "VLD thread is alive\n");
    huff_tables_init(&huff);
    /* DHT */
    huff_load_tables(&huff, &huffman);
    block_io_discard(&huffman);
    huff_load_tables(&huff, &huffman);
    block_io_discard(&huffman);

    //srl_log(TRACE, "huffman tables loaded\n");
    //    huff_dump( &huff );

    bitreader_get(&bits, 16);
    uint32_t magic = bitreader_get(&bits, 16);
    assert ( magic == DATA_MAGIC );

    x_size = bitreader_get(&bits, 16);
    y_size = bitreader_get(&bits, 16);

    bitreader_get(&bits, 8);
    //srl_log_printf(DEBUG, "picture size: %dx%d\n", x_size, y_size);

    mx_size = intceil(x_size, BLOCK_WIDTH);
    my_size = intceil(y_size, BLOCK_HEIGHT);

    /* SOS */
    bitreader_get(&bits, 16);
    bitreader_get(&bits, 16);

    tmp = bitreader_get(&bits, 8);
    comp.DC_HT = first_quad(tmp);
    comp.AC_HT = second_quad(tmp);

    //srl_log_printf(DEBUG,
    //              "DC: %d, AC: %d\n",
    //              comp.DC_HT, comp.AC_HT);

    bitreader_get(&bits, 24);

    comp.PRED = 0;
    left = mx_size*my_size;

    while (left > 0) {
        vld_decode_unpack_block(&bits, output, &huff, &comp);
        //srl_log_printf(DEBUG, "Put uncompressed block no %d\n", left);
        --left;
    }
}

