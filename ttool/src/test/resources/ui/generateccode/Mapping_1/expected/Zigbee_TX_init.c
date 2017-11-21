#include "Zigbee_TX.h"

/**** variables ****/

char fep_mss[0x10000];
char adaif_mss[0x10000];
char intl_mss[0x41000];
char mapper_mss[0x8000];
/**** Buffers *****/
FEP_BUFFER_TYPE buff_cwpI_ch_in;
FEP_BUFFER_TYPE buff_cwpI_ch_out;
FEP_BUFFER_TYPE buff_cwpQ_ch_in;
FEP_BUFFER_TYPE buff_cwpQ_ch_out;
FEP_BUFFER_TYPE buff_cwl_ch_in;
FEP_BUFFER_TYPE buff_cwl_ch_out;
MM_BUFFER_TYPE buff_sink_ch_in;
INTERLEAVER_BUFFER_TYPE buff_chip2octet_ch_in;
INTERLEAVER_BUFFER_TYPE buff_chip2octet_ch_out;
MAPPER_BUFFER_TYPE buff_symbol2ChipSeq_ch_in;
MAPPER_BUFFER_TYPE buff_symbol2ChipSeq_ch_out;
MM_BUFFER_TYPE buff_Source_ch_out;

/**** Operations Data Structures *****/
FEP_CONTEXT X_CWP_I_ctx;
FEP_CONTEXT X_CWP_Q_ctx;
FEP_CONTEXT X_CWL_ctx;
ADAIF_CONTEXT X_Sink_ctx;
INTL_CONTEXT X_Chip2Octet_ctx;
MAPPER_CONTEXT X_Symbol2ChipSeq_ctx;
EMBB_CONTEXT X_Source_ctx;


/**** Data Transfers Instructions ****/
INTL_CONTEXT CP_Symbol2ChipSeq_Chips2Octet_ctx;
FEP_CONTEXT CP_Chip2Octet_CWL_ctx;
ADAIF_CONTEXT CP_sink_ctx_0;
ADAIF_CONTEXT CP_sink_ctx_1;
MAPPER_CONTEXT CP_Source_to_Bits2Symbol_ctx;


void init_buffers()	{
	buff_cwpI_ch_in.num_samples = (uint8_t)0/* USER TO DO */;
	buff_cwpI_ch_in.base_address = (uint64_t)0/* USER TO DO */;
	buff_cwpI_ch_in.bank = (uint8_t)0/* USER TO DO */;
	buff_cwpI_ch_in.data_type = (uint8_t)0/* USER TO DO */;

	buff_cwpI_ch_out.num_samples = (uint8_t)0/* USER TO DO */;
	buff_cwpI_ch_out.base_address = (uint64_t)0/* USER TO DO */;
	buff_cwpI_ch_out.bank = (uint8_t)0/* USER TO DO */;
	buff_cwpI_ch_out.data_type = (uint8_t)0/* USER TO DO */;

	buff_cwpQ_ch_in.num_samples = (uint8_t)0/* USER TO DO */;
	buff_cwpQ_ch_in.base_address = (uint64_t)0/* USER TO DO */;
	buff_cwpQ_ch_in.bank = (uint8_t)0/* USER TO DO */;
	buff_cwpQ_ch_in.data_type = (uint8_t)0/* USER TO DO */;

	buff_cwpQ_ch_out.num_samples = (uint8_t)0/* USER TO DO */;
	buff_cwpQ_ch_out.base_address = (uint64_t)0/* USER TO DO */;
	buff_cwpQ_ch_out.bank = (uint8_t)0/* USER TO DO */;
	buff_cwpQ_ch_out.data_type = (uint8_t)0/* USER TO DO */;

	buff_cwl_ch_in.num_samples = (uint8_t)0/* USER TO DO */;
	buff_cwl_ch_in.base_address = (uint64_t)0/* USER TO DO */;
	buff_cwl_ch_in.bank = (uint8_t)0/* USER TO DO */;
	buff_cwl_ch_in.data_type = (uint8_t)0/* USER TO DO */;

	buff_cwl_ch_out.num_samples = (uint8_t)0/* USER TO DO */;
	buff_cwl_ch_out.base_address = (uint64_t)0/* USER TO DO */;
	buff_cwl_ch_out.bank = (uint8_t)0/* USER TO DO */;
	buff_cwl_ch_out.data_type = (uint8_t)0/* USER TO DO */;

	buff_sink_ch_in.num_samples = (uint8_t)123;
	buff_sink_ch_in.base_address = (uint32_t*)0x123;

	buff_chip2octet_ch_in.packed_binary_input_mode = (bool)0/* USER TO DO */;
	buff_chip2octet_ch_in.samples_width =  (uint8_t)0/* USER TO DO */;
	buff_chip2octet_ch_in.bit_input_offset =  (uint8_t)0/* USER TO DO */;
	buff_chip2octet_ch_in.input_offset =  (uint16_t)0/* USER TO DO */;
	buff_chip2octet_ch_in.packed_binary_output_mode =  (bool)0/* USER TO DO */;
	buff_chip2octet_ch_in.bit_output_offset =  (uint8_t)0/* USER TO DO */;
	buff_chip2octet_ch_in.output_offset =  (uint16_t)0/* USER TO DO */;
	buff_chip2octet_ch_in.permutation_offset =  (uint16_t)0/* USER TO DO */;
	buff_chip2octet_ch_in.permutation_length =  (uint16_t)0/* USER TO DO */;

	buff_chip2octet_ch_out.packed_binary_input_mode = (bool)0/* USER TO DO */;
	buff_chip2octet_ch_out.samples_width =  (uint8_t)0/* USER TO DO */;
	buff_chip2octet_ch_out.bit_input_offset =  (uint8_t)0/* USER TO DO */;
	buff_chip2octet_ch_out.input_offset =  (uint16_t)0/* USER TO DO */;
	buff_chip2octet_ch_out.packed_binary_output_mode =  (bool)0/* USER TO DO */;
	buff_chip2octet_ch_out.bit_output_offset =  (uint8_t)0/* USER TO DO */;
	buff_chip2octet_ch_out.output_offset =  (uint16_t)0/* USER TO DO */;
	buff_chip2octet_ch_out.permutation_offset =  (uint16_t)0/* USER TO DO */;
	buff_chip2octet_ch_out.permutation_length =  (uint16_t)0/* USER TO DO */;

	buff_symbol2ChipSeq_ch_in.num_symbols = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_in.input_base_address = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_in.num_bits_per_symbol = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_in.symmetrical_value = (bool)false/* USER TO DO */;
	buff_symbol2ChipSeq_ch_in.output_base_address = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_in.lut_base_address = (uint16_t)0/* USER TO DO */;

	buff_symbol2ChipSeq_ch_out.num_symbols = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_out.input_base_address = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_out.num_bits_per_symbol = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_out.symmetrical_value = (bool)false/* USER TO DO */;
	buff_symbol2ChipSeq_ch_out.output_base_address = (uint16_t)0/* USER TO DO */;
	buff_symbol2ChipSeq_ch_out.lut_base_address = (uint16_t)0/* USER TO DO */;

	buff_Source_ch_out.num_samples = (uint8_t)0/* USER TO DO */;
	buff_Source_ch_out.base_address = (uint32_t*)0/* USER TO DO */;

}

void init_signals()	{
	sig[Source_ch_out].f = false;
	sig[Source_ch_out].pBuff = (MM_BUFFER_TYPE*) &buff_Source_ch_out;

	sig[chip2octet_ch_out].f = false;
	sig[chip2octet_ch_out].pBuff = (INTERLEAVER_BUFFER_TYPE*) &buff_chip2octet_ch_out;

	sig[cwl_ch_out].f = false;
	sig[cwl_ch_out].pBuff = (FEP_BUFFER_TYPE*) &buff_cwl_ch_out;

	sig[cwpQ_ch_out].f = false;
	sig[cwpQ_ch_out].pBuff = (FEP_BUFFER_TYPE*) &buff_cwpQ_ch_out;

	sig[cwpI_ch_out].f = false;
	sig[cwpI_ch_out].pBuff = (FEP_BUFFER_TYPE*) &buff_cwpI_ch_out;

	sig[symbol2ChipSeq_ch_out].f = false;
	sig[symbol2ChipSeq_ch_out].pBuff = (MAPPER_BUFFER_TYPE*) &buff_symbol2ChipSeq_ch_out;

	sig[symbol2ChipSeq_ch_out_CP].f = false;
	sig[symbol2ChipSeq_ch_out_CP].pBuff = (MAPPER_BUFFER_TYPE*) &buff_symbol2ChipSeq_ch_out;

	sig[chip2octet_ch_out_CP].f = false;
	sig[chip2octet_ch_out_CP].pBuff = (INTERLEAVER_BUFFER_TYPE*) &buff_chip2octet_ch_out;

	sig[cwpQ_ch_out_CP].f = false;
	sig[cwpQ_ch_out_CP].pBuff = (FEP_BUFFER_TYPE*) &buff_cwpQ_ch_out;

	sig[cwpI_ch_out_CP].f = false;
	sig[cwpI_ch_out_CP].pBuff = (FEP_BUFFER_TYPE*) &buff_cwpI_ch_out;

	sig[Source_ch_out_CP].f = false;
	sig[Source_ch_out_CP].pBuff = (MM_BUFFER_TYPE*) &buff_Source_ch_out;

}

/**** init code ****/
/***** INIT X_CWP_I *******/
void init_X_CWP_I(void){
	fep_ctx_init(&X_CWP_I_ctx, (uintptr_t) fep_mss );
	// initialize context
	fep_set_op(&X_CWP_I_ctx,FEP_OP_CWP );
	// X vector configuration => Zk=Y[Xi]
	fep_set_wx(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sx(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_nx(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_mx(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_px(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_dx(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vrx(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vix(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	// Y vector configuration
	fep_set_by(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_qy(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_my(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ny(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sy(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_py(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_wy(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ty(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vry(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_dy(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	// Z vector addressing configuration
	fep_set_qz(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwpI_ch_out].pBuff)->bank);
	fep_set_bz(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwpI_ch_out].pBuff)->base_address);
	fep_set_tz(&X_CWP_I_ctx, ((FEP_BUFFER_TYPE*)sig[cwpI_ch_out].pBuff)->data_type);
	fep_set_wz(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sz(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_nz(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_mz(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	// Operation configuration
	fep_set_r(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ri(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sma(&X_CWP_I_ctx, (uint64_t)0/* USER TO DO */ );
	}

/***** INIT X_CWP_Q *******/
void init_X_CWP_Q(void){
	fep_ctx_init(&X_CWP_Q_ctx, (uintptr_t) fep_mss );
	// initialize context
	fep_set_op(&X_CWP_Q_ctx,FEP_OP_CWP );
	// X vector configuration => Zk=Y[Xi]
	fep_set_wx(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sx(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_nx(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_mx(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_px(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_dx(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vrx(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vix(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	// Y vector configuration
	fep_set_by(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_qy(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_my(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ny(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sy(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_py(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_wy(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ty(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vry(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_dy(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	// Z vector addressing configuration
	fep_set_qz(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwpQ_ch_out].pBuff)->bank);
	fep_set_bz(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwpQ_ch_out].pBuff)->base_address);
	fep_set_tz(&X_CWP_Q_ctx, ((FEP_BUFFER_TYPE*)sig[cwpQ_ch_out].pBuff)->data_type);
	fep_set_wz(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sz(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_nz(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_mz(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	// Operation configuration
	fep_set_r(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ri(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sma(&X_CWP_Q_ctx, (uint64_t)0/* USER TO DO */ );
	}

/***** INIT X_CWL *******/
void init_X_CWL(void){
	fep_ctx_init(&X_CWL_ctx, (uintptr_t) fep_mss );
	// initialize context
	fep_set_op(&X_CWL_ctx, FEP_OP_CWL );
	// X vector configuration => Zk=Y[Xi]
	fep_set_wx(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sx(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_nx(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_mx(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_px(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_dx(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vrx(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vix(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	// Y vector configuration
	fep_set_by(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_qy(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_my(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ny(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sy(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_py(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_wy(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ty(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_vry(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_dy(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	// Z vector addressing configuration
	fep_set_qz(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->bank);
	fep_set_bz(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->base_address);
	fep_set_tz(&X_CWL_ctx, ((FEP_BUFFER_TYPE*)sig[cwl_ch_out].pBuff)->data_type);
	fep_set_wz(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sz(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_nz(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_mz(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	// Operation configuration
	fep_set_li(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ls(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ll(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_r(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ri(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_ml(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	fep_set_sma(&X_CWL_ctx, (uint64_t)0/* USER TO DO */ );
	}

void init_X_Sink( void )	{
	adaif_ctx_init(&X_Sink_ctx, (uintptr_t) adaif_mss );
	adaif_set_tdd(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_st(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldt0(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldr0(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldt1(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldr1(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldt2(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldr2(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldt3(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_ldr3(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startt0(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopt0(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startr0(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopr0(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startt1(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopt1(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startr1(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopr1(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startt2(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopt2(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startr2(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopr2(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startt3(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopt3(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_startr3(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_stopr3(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_op(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_src(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_dst(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	adaif_set_data(&X_Sink_ctx, (uint64_t) 0/* USER TO DO */ );
	}

/***** INIT X_Chip2Octet *******/
void init_X_Chip2Octet(void){
	intl_ctx_init(&X_Chip2Octet_ctx, (uintptr_t) intl_mss );
	// initialize context
	intl_set_sv(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_arm(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_arm(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_re(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_se(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_fe(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_pbo(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->packed_binary_output_mode));
	intl_set_pbi(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->packed_binary_input_mode));
	intl_set_widm1(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->samples_width));
	intl_set_biof(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->bit_input_offset));
	intl_set_boof(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->bit_output_offset));
	intl_set_fz(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_fo(&X_Chip2Octet_ctx, (uint64_t)0/* USER TO DO */ );
	intl_set_iof(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->output_offset));
	intl_set_oof(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->input_offset));
	intl_set_pof(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->permutation_offset));
	intl_set_lenm1(&X_Chip2Octet_ctx, (((INTERLEAVER_BUFFER_TYPE*)sig[symbol2ChipSeq_ch_out_CP].pBuff)->permutation_length));
	}

/***** INIT X_Symbol2ChipSeq *******/
void init_X_Symbol2ChipSeq(void){
	mapper_ctx_init(&X_Symbol2ChipSeq_ctx, (uintptr_t) mapper_mss );
	// initialize context
	mapper_set_lenm1(&X_Symbol2ChipSeq_ctx, (((MAPPER_BUFFER_TYPE*)sig[Source_ch_out_CP].pBuff)->num_symbols));
	mapper_set_lba(&X_Symbol2ChipSeq_ctx, (((MAPPER_BUFFER_TYPE*)sig[Source_ch_out_CP].pBuff)->lut_base_address));
	mapper_set_oba(&X_Symbol2ChipSeq_ctx, (((MAPPER_BUFFER_TYPE*)sig[Source_ch_out_CP].pBuff)->output_base_address));
	mapper_set_iba(&X_Symbol2ChipSeq_ctx, (((MAPPER_BUFFER_TYPE*)sig[Source_ch_out_CP].pBuff)->input_base_address));
	mapper_set_mult(&X_Symbol2ChipSeq_ctx, (uint64_t)0/* USER TO DO */ );
	mapper_set_men(&X_Symbol2ChipSeq_ctx, (uint64_t)0/* USER TO DO */ );
	mapper_set_sym(&X_Symbol2ChipSeq_ctx, (((MAPPER_BUFFER_TYPE*)sig[Source_ch_out_CP].pBuff)->symmetrical_value));
	mapper_set_bpsm1(&X_Symbol2ChipSeq_ctx, (((MAPPER_BUFFER_TYPE*)sig[Source_ch_out_CP].pBuff)->num_bits_per_symbol));
	mapper_set_m(&X_Symbol2ChipSeq_ctx, (uint64_t)0/* USER TO DO */ );
	mapper_set_n(&X_Symbol2ChipSeq_ctx, (uint64_t)0/* USER TO DO */ );
	mapper_set_s(&X_Symbol2ChipSeq_ctx, (uint64_t)0/* USER TO DO */ );
	}

/***** INIT X_Source *******/
void init_X_Source(void){
	/* USER TO DO */
}
void init_CP_Symbol2ChipSeq_Chips2Octet()	{
	intl_ctx_init(&CP_Symbol2ChipSeq_Chips2Octet_ctx, (uintptr_t) intl_mss );
}

void init_CP_Chip2Octet_CWL()	{
	fep_ctx_init(&CP_Chip2Octet_CWL_ctx, (uintptr_t) fep_mss );
}

void init_CP_sink()	{
	adaif_ctx_init(&CP_sink_ctx_0, (uintptr_t) adaif_mss );
	adaif_ctx_init(&CP_sink_ctx_1, (uintptr_t) adaif_mss );
}

void init_CP_Source_to_Bits2Symbol()	{
	mapper_ctx_init(&CP_Source_to_Bits2Symbol_ctx, (uintptr_t) mapper_mss );
}

/**** init contexts ****/
void init_operations(void)	{
	init_X_CWP_I();
	init_X_CWP_Q();
	init_X_CWL();
	init_X_Sink();
	init_X_Chip2Octet();
	init_X_Symbol2ChipSeq();
}

/**** init CPs ****/
void init_CPs(void)	{
	init_CP_Symbol2ChipSeq_Chips2Octet();
	init_CP_Chip2Octet_CWL();
	init_CP_sink();
}

/**** cleanup contexts ****/
void cleanup_operations_context( void )	{
	fep_ctx_cleanup( &X_CWP_I_ctx );
	fep_ctx_cleanup( &X_CWP_Q_ctx );
	fep_ctx_cleanup( &X_CWL_ctx );
	adaif_ctx_cleanup( &X_Sink_ctx );
	intl_ctx_cleanup( &X_Chip2Octet_ctx );
	mapper_ctx_cleanup( &X_Symbol2ChipSeq_ctx );
}

void cleanup_CPs_context( void )	{
	intl_ctx_cleanup(&CP_Symbol2ChipSeq_Chips2Octet_ctx);
	fep_ctx_cleanup(&CP_Chip2Octet_CWL_ctx);
	adaif_ctx_cleanup(&CP_sink_ctx_0);
	adaif_ctx_cleanup(&CP_sink_ctx_1);
	mapper_ctx_cleanup(&CP_Source_to_Bits2Symbol_ctx);
}
