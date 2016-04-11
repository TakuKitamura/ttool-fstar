/* this class produces the lines containing essentially the initial #includes; we include all potential components event if they are not used in the deployment diagram*/

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Header {
	
    static private String header;
int nb_clusters=5;
    private final static String CR = "\n";
	private final static String CR2 = "\n\n";

    Header(){
    }
    public static  String getHeader() {

		header = "//-------------------------------Header------------------------------------" + CR2
				+ " #include <iostream>" + CR + "#include <cstdlib>" + CR + "#include <stdexcept>" + CR2
				+ "#define CONFIG_GDB_SERVER" + CR + "#define CONFIG_SOCLIB_MEMCHECK" + CR2;

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
				+ "#include \"vci_multi_tty.h\"" + CR + "#include \"vci_locks.h\"" + CR + "#include \"vci_xicu.h\""
				+ CR + "#include \"vci_vgmn.h\""+ CR + "#include \"vci_vgsb.h\"" + CR 
	    //+ "#include \"vci_local_crossbar.h\"" + CR 
+ "#include \"vci_block_device.h\"" + CR
				+ "#include \"vci_simhelper.h\"" + CR + "#include \"vci_fd_access.h\"" + CR
+ "#include \"vci_ethernet.h\"" + CR
				+ "#include \"vci_rttimer.h\"" + CR
	+ "#include \"vci_logger.h\"" + CR2;
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
