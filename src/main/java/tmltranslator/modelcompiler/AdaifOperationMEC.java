/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package tmltranslator.modelcompiler;

/**
 * Class AdaifOperationMEC, Model Extension Construct (MEC) class for Adaif
 * operations Creation: 05/02/2014
 * 
 * @version 1.0 05/02/2014
 * @author Andrea ENRICI
 */
public class AdaifOperationMEC extends OperationMEC {

  public AdaifOperationMEC(final String ctxName) {
    name = "AdaifOperationMEC";

    // Issue #98: Status already declared
    exec_code = TAB + // "int status;" + CR + TAB +
        "adaif_wait(&" + ctxName + ");" + CR + TAB + "status = adaif_wait(&" + ctxName + ");" + CR;

    // Issue #98: Provide default value for compilation
    init_code = "void init_" + ctxName.split("_ctx")[0] + "( void )\t{" + CR + TAB + "adaif_ctx_init(&" + ctxName
        + ", (uintptr_t) adaif_mss );" + CR + TAB + "adaif_set_tdd(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL
        + USER_TO_DO + " );" + CR + TAB + "adaif_set_st(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO
        + " );" + CR + TAB + "adaif_set_ldt0(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR
        + TAB + "adaif_set_ldr0(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_ldt1(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_ldr1(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_ldt2(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_ldr2(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_ldt3(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_ldr3(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startt0(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopt0(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startr0(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopr0(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startt1(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopt1(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startr1(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopr1(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startt2(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopt2(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startr2(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopr2(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startt3(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopt3(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_startr3(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_stopr3(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_op(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_src(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_dst(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB
        + "adaif_set_data(&" + ctxName + ", (uint64_t) " + DEFAULT_NUM_VAL + USER_TO_DO + " );" + CR + TAB + "}" + CR;
    cleanup_code = TAB + "adaif_ctx_cleanup( " + DEFAULT_NUM_VAL + USER_TO_DO + " );";
    context = "ADAIF_CONTEXT";
  }

} // End of class
