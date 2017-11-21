#include "Zigbee_TX.h"

int (*operation[NUM_OPS])();
bool (*fire_rule[NUM_OPS])();
SIG_TYPE sig[NUM_SIGS]={{0}};

/******** Zigbee_TX_exec function *********/
int Zigbee_TX_exec(void)    {
	bool valid_signal = false;
	bool blocked = true;
	int status = 0;
	register_operations();
	register_dataTransfers();
	register_fire_rules();
	init_buffers();
	init_signals();
	init_operations();
	init_CPs();

	/********* INIT PREX OPs signals ********/
	sig[ Source_ch_out ].f = false;

	/********* OPERATIONS scheduling ***************/
		while( !exit_rule() )	{
		for( int n_op = 0; n_op < NUM_OPS; ++n_op )	{
			valid_signal = (*fire_rule[n_op])();
			if( valid_signal )	{
				status = (*operation[n_op])();
				blocked = false;
			}
		}
		if( blocked )	{
			printf("ERROR: the system got blocked, no new signals\n");
			return 1;
		}
		blocked = true;
	}
	cleanup_operations_context();
	cleanup_CPs_context();
	return status;
}

int op_F_CWP_I()	{

	int status = 0;
	sig[cwl_ch_out].f = false;
	/*firm instruction*/
	fep_set_l(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->num_samples);
	fep_set_qx(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->bank);
	fep_set_bx(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->base_address);
	fep_set_tx(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->data_type);
	/*start execution*/
	status = fep_do(&X_CWP_I_ctx);
	sig[cwpI_ch_out].f = true;
	return status;
}

int op_F_CWP_Q()	{

	int status = 0;
	sig[cwl_ch_out].f = false;
	/*firm instruction*/
	fep_set_l(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->num_samples);
	fep_set_qx(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->bank);
	fep_set_bx(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->base_address);
	fep_set_tx(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->data_type);
	/*start execution*/
	status = fep_do(&X_CWP_Q_ctx);
	sig[cwpQ_ch_out].f = true;
	return status;
}

int op_F_CWL()	{

	int status = 0;
	sig[chip2octet_ch_out_CP].f = false;
	/*firm instruction*/
	fep_set_l(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[chip2octet_ch_out_CP].pBuff)->num_samples);
	fep_set_qx(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[chip2octet_ch_out_CP].pBuff)->bank);
	fep_set_bx(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[chip2octet_ch_out_CP].pBuff)->base_address);
	fep_set_tx(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[chip2octet_ch_out_CP].pBuff)->data_type);
	/*start execution*/
	status = fep_do(&X_CWL_ctx);
	sig[cwl_ch_out].f = true;
	return status;
}

int op_F_Sink()	{

	int status = 0;
	sig[cwpQ_ch_out_CP].f = false;
	sig[cwpI_ch_out_CP].f = false;
	adaif_wait(&X_Sink_ctx);
	status = adaif_wait(&X_Sink_ctx);
	sig[sink_ch_in].f = true;
	return status;
}

int op_F_Chip2Octet()	{

	int status = 0;
	sig[symbol2ChipSeq_ch_out_CP].f = false;
	/*start execution*/
	intl_start(&X_Chip2Octet_ctx);
	status = intl_wait(&X_Chip2Octet_ctx);
	sig[chip2octet_ch_out].f = true;
	return status;
}

int op_F_Symbol2ChipSeq()	{

	int status = 0;
	sig[Source_ch_out_CP].f = false;
	
	/*start execution*/
	mapper_start(&X_Symbol2ChipSeq_ctx);
	status = mapper_wait(&X_Symbol2ChipSeq_ctx);
	sig[symbol2ChipSeq_ch_out].f = true;
	return status;
}

int op_F_Source()	{

	int status = 0;
	sig[Source_ch_out].f = true;
	return status;
}

int op_CP_Symbol2ChipSeq_Chips2Octet()	{
	int status = 0;
	sig[ symbol2ChipSeq_ch_out ].f = false;
	embb_mem2ip((EMBB_CONTEXT *)&CP_Symbol2ChipSeq_Chips2Octet_ctx, (uintptr_t) mapper_mss, 0/* USER TO DO */, 0/* USER TO DO */ );
	sig[ symbol2ChipSeq_ch_out_CP ].f = true;
	return status;
}

int op_CP_Chip2Octet_CWL()	{
	int status = 0;
	sig[ chip2octet_ch_out ].f = false;
	embb_mem2ip((EMBB_CONTEXT *)&CP_Chip2Octet_CWL_ctx, (uintptr_t) intl_mss, 0/* USER TO DO */, 0/* USER TO DO */ );
	sig[ chip2octet_ch_out_CP ].f = true;
	return status;
}

int op_CP_sink()	{
	int status = 0;
	sig[ cwpQ_ch_out ].f = false;
	sig[ cwpI_ch_out ].f = false;
	embb_mem2ip((EMBB_CONTEXT *)&CP_sink_ctx_0, (uintptr_t) fep_mss, 0/* USER TO DO */, 0/* USER TO DO */ );
	embb_mem2ip((EMBB_CONTEXT *)&CP_sink_ctx_1, (uintptr_t) fep_mss, 0/* USER TO DO */, 0/* USER TO DO */ );
	sig[ cwpQ_ch_out_CP ].f = true;
	sig[ cwpI_ch_out_CP ].f = true;
	return status;
}

int op_CP_Source_to_Bits2Symbol()	{
	int status = 0;
	sig[ Source_ch_out ].f = false;
	embb_mem2ip((EMBB_CONTEXT *)&CP_Source_to_Bits2Symbol_ctx, (uintptr_t) adaif_mss, 0/* USER TO DO */, 0/* USER TO DO */ );
	sig[ Source_ch_out_CP ].f = true;
	return status;
}

void register_operations( void )	{
	operation[F_CWP_I] = op_F_CWP_I;
	operation[F_CWP_Q] = op_F_CWP_Q;
	operation[F_CWL] = op_F_CWL;
	operation[F_Sink] = op_F_Sink;
	operation[F_Chip2Octet] = op_F_Chip2Octet;
	operation[F_Symbol2ChipSeq] = op_F_Symbol2ChipSeq;
	operation[F_Source] = op_F_Source;
}

void register_dataTransfers( void )	{
	operation[CP_Symbol2ChipSeq_Chips2Octet] = op_CP_Symbol2ChipSeq_Chips2Octet;
	operation[CP_Chip2Octet_CWL] = op_CP_Chip2Octet_CWL;
	operation[CP_sink] = op_CP_sink;
	operation[CP_Source_to_Bits2Symbol] = op_CP_Source_to_Bits2Symbol;
}

/**** OPERATIONS FIRE RULES ****/
bool fr_F_CWP_I( void )	{
	return (( sig[ cwl_ch_out ].f ) && ( !sig[ cwpI_ch_out ].f ));
}

bool fr_F_CWP_Q( void )	{
	return (( sig[ cwl_ch_out ].f ) && ( !sig[ cwpQ_ch_out ].f ));
}

bool fr_F_CWL( void )	{
	return (( sig[ chip2octet_ch_out_CP ].f ) && ( !sig[ cwl_ch_out ].f ));
}

bool fr_F_Sink( void )	{
	return ( ( sig[ cwpQ_ch_out_CP ].f ) && ( sig[ cwpI_ch_out_CP ].f ));
}

bool fr_F_Chip2Octet( void )	{
	return (( sig[ symbol2ChipSeq_ch_out_CP ].f ) && ( !sig[ chip2octet_ch_out ].f ));
}

bool fr_F_Symbol2ChipSeq( void )	{
	return (( sig[ Source_ch_out_CP ].f ) && ( !sig[ symbol2ChipSeq_ch_out ].f ));
}

bool fr_F_Source( void )	{
	return (( !sig[ Source_ch_out ].f ));
}


/**** DATA TRANSFERS FIRE RULES ****/
bool fr_CP_Symbol2ChipSeq_Chips2Octet( void )	{
	return ( ( sig[ symbol2ChipSeq_ch_out ].f ) &&( !sig[ symbol2ChipSeq_ch_out_CP ].f ) );
}

bool fr_CP_Chip2Octet_CWL( void )	{
	return ( ( sig[ chip2octet_ch_out ].f ) &&( !sig[ chip2octet_ch_out_CP ].f ) );
}

bool fr_CP_sink( void )	{
	return ( ( sig[ cwpQ_ch_out ].f ) &&( sig[ cwpI_ch_out ].f ) &&( !sig[ cwpQ_ch_out_CP ].f ) &&( !sig[ cwpI_ch_out_CP ].f ) );
}

bool fr_CP_Source_to_Bits2Symbol( void )	{
	return ( ( sig[ Source_ch_out ].f ) &&( !sig[ Source_ch_out_CP ].f ) );
}


void register_fire_rules( void )	{
	fire_rule[F_CWP_I] = fr_F_CWP_I;
	fire_rule[F_CWP_Q] = fr_F_CWP_Q;
	fire_rule[F_CWL] = fr_F_CWL;
	fire_rule[F_Sink] = fr_F_Sink;
	fire_rule[F_Chip2Octet] = fr_F_Chip2Octet;
	fire_rule[F_Symbol2ChipSeq] = fr_F_Symbol2ChipSeq;
	fire_rule[F_Source] = fr_F_Source;
	fire_rule[CP_Symbol2ChipSeq_Chips2Octet] = fr_CP_Symbol2ChipSeq_Chips2Octet;
	fire_rule[CP_Chip2Octet_CWL] = fr_CP_Chip2Octet_CWL;
	fire_rule[CP_sink] = fr_CP_sink;
	fire_rule[CP_Source_to_Bits2Symbol] = fr_CP_Source_to_Bits2Symbol;
}

bool exit_rule(void)	{
	return ( sig[ sink_ch_in ].f == true );
}