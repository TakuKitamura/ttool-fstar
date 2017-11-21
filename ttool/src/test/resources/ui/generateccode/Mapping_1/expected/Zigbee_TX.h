#ifndef Zigbee_TX_H
#define Zigbee_TX_H
#include <stdio.h>
#include <stdint.h>
#include <embb/fep.h>
#include <embb/intl.h>
#include <embb/mapper.h>
#include <embb/adaif.h>
#include <embb/memory.h>

extern char fep_mss[];
extern char adaif_mss[];
extern char intl_mss[];
extern char mapper_mss[];

struct SIG_TYPE	{
	bool f;
	void *pBuff;
};

typedef struct SIG_TYPE SIG_TYPE;
extern SIG_TYPE sig[];



/**** Buffers ****/
struct FEP_BUFFER_TYPE {
	uint8_t num_samples;
	uint64_t base_address;
	uint8_t bank;
	uint8_t data_type;
};

typedef FEP_BUFFER_TYPE FEP_BUFFER_TYPE;

struct MAPPER_BUFFER_TYPE {
	uint16_t num_symbols;
	uint16_t input_base_address;
	uint16_t num_bits_per_symbol;
	bool symmetrical_value;
	uint16_t output_base_address;
	uint16_t lut_base_address;
};

typedef MAPPER_BUFFER_TYPE MAPPER_BUFFER_TYPE;

struct INTERLEAVER_BUFFER_TYPE {
	bool packed_binary_input_mode;
	uint8_t samples_width;
	uint8_t bit_input_offset;
	uint16_t input_offset;
	bool packed_binary_output_mode;
	uint8_t bit_output_offset;
	uint16_t output_offset;
	uint16_t permutation_offset;
	uint16_t permutation_length;
};

typedef INTERLEAVER_BUFFER_TYPE INTERLEAVER_BUFFER_TYPE;


struct MM_BUFFER_TYPE {
	uint8_t num_samples;
	uint32_t* base_address;
};

typedef MM_BUFFER_TYPE MM_BUFFER_TYPE;


struct ADAIF_BUFFER_TYPE {
	uint8_t num_samples;
	uint32_t* base_address;
};

typedef ADAIF_BUFFER_TYPE ADAIF_BUFFER_TYPE;

/**** prototypes *****/
extern int Zigbee_TX_exec(void);
extern void init_buffers(void);
extern bool exit_rule(void);
extern void register_operations(void);
extern void register_dataTransfers(void);
extern void register_fire_rules(void);
extern void init_signals(void);
extern void init_operations(void);
extern void init_CPs(void);
extern void cleanup_operations_context(void);
extern void cleanup_CPs_context(void);

/**** Buffers *****/
extern FEP_BUFFER_TYPE buff_cwpI_ch_in;
extern FEP_BUFFER_TYPE buff_cwpI_ch_out;
extern FEP_BUFFER_TYPE buff_cwpQ_ch_in;
extern FEP_BUFFER_TYPE buff_cwpQ_ch_out;
extern FEP_BUFFER_TYPE buff_cwl_ch_in;
extern FEP_BUFFER_TYPE buff_cwl_ch_out;
extern MM_BUFFER_TYPE buff_sink_ch_in;
extern INTERLEAVER_BUFFER_TYPE buff_chip2octet_ch_in;
extern INTERLEAVER_BUFFER_TYPE buff_chip2octet_ch_out;
extern MAPPER_BUFFER_TYPE buff_symbol2ChipSeq_ch_in;
extern MAPPER_BUFFER_TYPE buff_symbol2ChipSeq_ch_out;
extern MM_BUFFER_TYPE buff_Source_ch_out;

/**** Operations Data Structures *****/
extern FEP_CONTEXT X_CWP_I_ctx;
extern FEP_CONTEXT X_CWP_Q_ctx;
extern FEP_CONTEXT X_CWL_ctx;
extern ADAIF_CONTEXT X_Sink_ctx;
extern INTL_CONTEXT X_Chip2Octet_ctx;
extern MAPPER_CONTEXT X_Symbol2ChipSeq_ctx;
extern EMBB_CONTEXT X_Source_ctx;


/**** Data Transfers Instructions ****/
extern INTL_CONTEXT CP_Symbol2ChipSeq_Chips2Octet_ctx;
extern FEP_CONTEXT CP_Chip2Octet_CWL_ctx;
extern ADAIF_CONTEXT CP_sink_ctx_0;
extern ADAIF_CONTEXT CP_sink_ctx_1;
extern MAPPER_CONTEXT CP_Source_to_Bits2Symbol_ctx;


enum sigs_enu {
	Source_ch_out,
	chip2octet_ch_out,
	cwl_ch_out,
	cwpQ_ch_out,
	cwpI_ch_out,
	symbol2ChipSeq_ch_out,
	symbol2ChipSeq_ch_out_CP,
	chip2octet_ch_out_CP,
	cwpQ_ch_out_CP,
	cwpI_ch_out_CP,
	Source_ch_out_CP,
	sink_ch_in,
NUM_SIGS };

enum ops_enu   {
	F_CWP_I,
	F_CWP_Q,
	F_CWL,
	F_Sink,
	F_Chip2Octet,
	F_Symbol2ChipSeq,
	F_Source,
	CP_Symbol2ChipSeq_Chips2Octet,
	CP_Chip2Octet_CWL,
	CP_sink,
	CP_Source_to_Bits2Symbol,
NUM_OPS };

#endif