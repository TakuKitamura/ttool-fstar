/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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




/* this class produces the lines containing essentially the initial #includes; we include all potential components event if they are not used in the deployment diagram*/

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;

import ddtranslatorSoclib.*; //DG 23.08.

public class Header {
	
    static private String header;
int nb_clusters=5;
    private final static String CR = "\n";
	private final static String CR2 = "\n\n";

    Header(){
    }
    public static  String getHeader() {
	int with_vgsb=TopCellGenerator.avatardd.getAllBus().size();

	header = "//-------------------------------Header------------------------------------" + CR2
		    + "#include <iostream>" + CR 
		    + "#include <cstdlib>"  + CR 
		    + "#include <vector>" + CR
		    + "#include <string>" + CR 
		    + "#include <stdexcept>" + CR 
		    + "#include <cstdarg>" +CR2
		    + "#define CONFIG_GDB_SERVER" + CR 
		    + "#define CONFIG_SOCLIB_MEMCHECK" + CR2;

	header = header + "#include \"iss_memchecker.h\"" + CR 
	    +"#include \"gdbserver.h\""+ CR2 
	    +"#include \"ppc405.h\"" + CR
	    +"#include \"niosII.h\"" + CR
	    +"#include \"mips32.h\"" + CR
	    +"#include \"arm.h\"" + CR
	    +"#include \"sparcv8.h\"" + CR
	    +"#include \"lm32.h\"" + CR2
	    + "#include \"mapping_table.h\"" + CR
				+ "#include \"vci_fdt_rom.h\"" + CR + "#include \"vci_xcache_wrapper.h\"" + CR
				+ "#include \"vci_ram.h\"" + CR + "#include \"vci_heterogeneous_rom.h\"" + CR
	    + "#include \"vci_multi_tty.h\"" + CR + "#include \"vci_locks.h\"" + CR + "#include \"vci_xicu.h\""+ CR
	    + "#include \"vci_mwmr_stats.h\""+ CR;//DG 20.09.
	    if (with_vgsb>0){
		header +="#include \"vci_vgsb.h\""+ CR;
	    }
	    else{
		header +="#include \"vci_vgmn.h\""+ CR;
	    }
	   int with_hw_accellerator = 1; //DG 23.08. a la main
	   if (with_hw_accellerator>0){
	       header +="#include \"mwmr_controller.h\""+ CR;
	       header +="#include \"vci_mwmr_controller.h\""+ CR;
	   }
	   //include statements for all coprocessors found
	   //The user must ensure that there is a SoCLib component corresponding to this coprocessor
	   // if (with_hw_accellerator>0){  
	   //DG 23.08. actuellement il ne les trouve pas!
	   int hwas=0;
	   header +="#include \"fifo_virtual_copro_wrapper.h\""+ CR;

	   for (AvatarCoproMWMR HWAccelerator : TopCellGenerator.avatardd.getAllCoproMWMR()) {
	       //	   String name = HWAccelerator.getCoprocName();
	       //	   header +="#include \""+name+"\""+ CR;

	       //Per default for testing
	       //  header +="#include \"input_coproc.h\""+ CR;
	       //header +="#include \"output_coproc.hh\""+ CR;

	       /* can be found in /users/outil/soc/soclib/soclib/module/internal_component/fifo* */
	       //header +="#include \"fifo_virtual_copro_wrapper.h\""+ CR;
	       if(HWAccelerator.getCoprocType()==0)
		   { header +="#include \"vci_input_engine.h\""+ CR;
		       header +="#include \"papr_slot.h\""+ CR;
		   header +="#include \"generic_fifo.h\""+ CR;
		   header +="#include \"network_io.h\""+ CR;}
	       else {
		   if(HWAccelerator.getCoprocType()==1)
		       { header +="#include \"vci_output_engine.h\""+ CR;}
	       
	       
	       else{
	       header +="#include \"my_hwa"+hwas+".h\""+ CR;
	       hwas++;
	       }
	   }
	   //  }
	   }
	    header+= "#include \"vci_block_device.h\"" + CR
		+ "#include \"vci_simhelper.h\"" + CR + "#include \"vci_fd_access.h\"" + CR
		+ "#include \"vci_ethernet.h\"" + CR
				+ "#include \"vci_rttimer.h\"" + CR		
		+ "#include \"vci_logger.h\"" + CR
		+ "#include \"vci_local_crossbar.h\"" + CR2;
	
	header = header +"namespace {" + CR
+"std::vector<std::string> stringArray(" + CR
+"	const char *first, ... )" + CR
+"{" + CR
+"	std::vector<std::string> ret;" + CR
+"	va_list arg;" + CR
+"	va_start(arg, first);" + CR
+"	const char *s = first;" + CR
+"	while(s) {" + CR
+"		ret.push_back(std::string(s));" + CR
+"		s = va_arg(arg, const char *);" + CR
+"	};" + CR
+"	va_end(arg);" + CR
+"	return ret;" + CR
+"}" + CR2
+"std::vector<int> intArray(" + CR
+"	const int length, ... )" + CR
+"{" + CR
+"	int i;" + CR
+"	std::vector<int> ret;" + CR
+"	va_list arg;" + CR
+"	va_start(arg, length);" + CR2
+"	for (i=0; i<length; ++i) {" + CR
+"		ret.push_back(va_arg(arg, int));" + CR
+"	};" + CR
+"	va_end(arg);" + CR
+"	return ret;" + CR
+"}" + CR
	    +"}" + CR2;

	header = header + "using namespace soclib;" + CR + "using common::IntTab;" + CR + "using common::Segment;";


  		if(TopCellGenerator.avatardd.getNbClusters()==0){
		header = header + CR2 + "static common::MappingTable maptab(32, IntTab(8), IntTab(8), 0xfff00000);";
		}
		else{
header = header + CR2 + "static common::MappingTable maptab(32, IntTab(8,4), IntTab(8,4), 0xfff00000);";
		}
		return header;
	}
}
