/* This class generates the platform_desc file t*/


/* authors: v1.0 Daniela GENIUS auguts 2016 */

package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import java.util.*;

public class Platforminfo {

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";
  
    public static String getPlatformInfo() {
	//determine if the platform is vgsb or vgmn based (mutually exclusive)
    int with_vgsb=TopCellGenerator.avatardd.getAllBus().size();
    //bus can be other than VGSB (CAN...), for the moment restricted to VGSB
        String platforminfo = CR;
        platforminfo +="use =  ["+CR
	+"Uses('caba:vci_locks'),"+CR
	+"Uses('caba:vci_ram'),"+CR
	+"Uses('caba:vci_fdt_rom'),"+CR
	+"Uses('caba:vci_heterogeneous_rom'),"+CR
	+"Uses('caba:vci_multi_tty'),"+CR
	+"Uses('caba:vci_xicu'),"+CR
	+"Uses('caba:vci_block_device'),"+CR
	+"Uses('caba:vci_ethernet'),"+CR
	+"Uses('caba:vci_rttimer'),"+CR
	+"Uses('caba:vci_fd_access'),"+CR
	    +"Uses('caba:vci_simhelper'),"+CR;

	if(with_vgsb>0){
	    platforminfo+="Uses('caba:vci_vgsb'),"+CR;
	}
	else {
	    platforminfo+="Uses('caba:vci_vgmn'),"+CR;
	}

        platforminfo+="Uses('caba:vci_mwmr_stats'),"+CR
        +"Uses('caba:vci_logger'),"+CR
        +"Uses('caba:vci_local_crossbar'),"+CR     
        +"Uses('common:elf_file_loader'),"+CR
	+"Uses('common:plain_file_loader'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:ppc405'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:arm'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:mips32eb'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:mips32el'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:niosII'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:lm32'),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:sparcv8', NWIN=8),"+CR
        +"Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:sparcv8', NWIN=2),"+CR
      +"  ]"+CR2
+"todo = Platform('caba', 'top.cc',"+CR
+"        uses=use,"+CR
+"	cell_size = 4,"+CR
+"	plen_size = 9,"+CR
+"	addr_size = 32,"+CR
+"	rerror_size = 1,"+CR
+"	clen_size = 1,"+CR
+"	rflag_size = 1,"+CR
+"	srcid_size = 8,"+CR
+"	pktid_size = 1,"+CR
+"	trdid_size = 1,"+CR
+"	wrplen_size = 1"+CR
+")"+CR2;

return platforminfo;	
    }
}