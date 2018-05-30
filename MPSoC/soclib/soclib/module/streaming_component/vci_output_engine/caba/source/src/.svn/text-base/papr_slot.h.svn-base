
#ifndef PAPR_SLOT_H
#define	PAPR_SLOT_H

#define PAPR_SLOT_SIZE 128
#define PAPR_OFFSET 40
#define PAPR_PAYLOAD_SIZE (PAPR_SLOT_SIZE-sizeof(papr_desc_t))
#define PAPR_PAYLOAD_FIRST_SIZE (PAPR_PAYLOAD_SIZE-PAPR_OFFSET)

#define ROUDED_UP_DIV(num, denum) (((num)+((denum)-1))/(denum))

#include <stdint.h>

typedef struct {
  uint32_t address;
  uint32_t total_size:11;
  uint32_t slot_size:7;
  uint32_t offset:7;
  uint32_t is_internal:1;
  uint32_t date:6;
} __attribute__((packed)) papr_desc_t;

typedef union {
  papr_desc_t desc;
  uint32_t binary[ROUDED_UP_DIV(sizeof(papr_desc_t),sizeof(uint32_t))];
} papr_desc_or_data_t;

/*
papr_desc_or_data_t toto;

toto.binary[0] = 0xdeadbeef;
toto.binary[1] = 0xdecafbad;
toto.desc.address; (0xdeadbeef)
toto.desc.date; (0x2d)
*/

typedef struct {
  papr_desc_t desc;
  uint8_t data[PAPR_PAYLOAD_SIZE];
} papr_slot_t;

#endif

