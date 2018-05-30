#ifndef SLOT_H
#define	SLOT_H

#define SLOT_SIZE 128
#define DESC_SIZE sizeof(DESC)
#define PAYLOAD (SLOT_SIZE - DESC_SIZE)
#define OFFSET_INT 40
#define PAYLOAD_INT (PAYLOAD - OFFSET_INT)
typedef struct descripteur{
	unsigned int address;
	unsigned int TT:	11;
	unsigned int TS:	7;
	unsigned int OFFSET:	7;
	unsigned int INTERNAL:	1;
	unsigned int DATE:	6;
#ifdef __CPLUSPLUS
	friend bool operator == (const descripteur& a, const descripteur& b);
	friend bool operator != (const descripteur& a, const descripteur& b);
#endif
} __attribute__((packed)) DESC;

typedef struct slot{
DESC desc;
unsigned char data[PAYLOAD];
}slot;


#ifdef __CPLUSPLUS
bool operator == (const descripteur& a, const descripteur& b)
{
	return ((a.address == b.address)
		&& (a.TT == b.TT)
		&& (a.TS == b.TS)
		&& (a.OFFSET == b.OFFSET)
		&& (a.INTERNAL == b.INTERNAL)
		&& (a.DATE == b.DATE));
}

bool operator != (const descripteur& a, const descripteur& b)
{
	return !(a == b);
}
#endif

#endif

