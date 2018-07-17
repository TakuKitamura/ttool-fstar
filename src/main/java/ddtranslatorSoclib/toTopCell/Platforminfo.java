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




/* This class generates the platform_desc file t*/


/* authors: v1.0 Daniela GENIUS august 2016 */

package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;

public class Platforminfo
{

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";

    public static String getPlatformInfo ()
    {
	//determine if the platform is vgsb or vgmn based (mutually exclusive)
	int with_vgsb = TopCellGenerator.avatardd.getAllBus ().size ();
	int nb_hwa = TopCellGenerator.avatardd.getAllCoproMWMR ().size ();

    //Determine if AMS Cluster is present in Avatar DD
    int with_amsCluster = TopCellGenerator.avatardd.getNbAmsCluster();

	//bus can be other than VGSB (CAN...), for the moment restricted to VGSB
	String platforminfo = CR;
	  platforminfo += "use =  [" + CR
	    //          +"Uses('caba:vci_locks'),"+CR       
	+ "Uses('caba:vci_ram')," + CR
	    + "Uses('caba:vci_fdt_rom')," + CR
	    + "Uses('caba:vci_heterogeneous_rom')," + CR
	    + "Uses('caba:vci_multi_tty')," + CR
	    + "Uses('caba:vci_xicu')," + CR
	    + "Uses('caba:vci_block_device')," + CR
	    + "Uses('caba:vci_ethernet')," + CR
	    + "Uses('caba:vci_rttimer')," + CR
	    + "Uses('caba:vci_fd_access')," + CR
	    + "Uses('caba:vci_simhelper')," + CR;

	if (with_vgsb > 0)
	  {
	      platforminfo += "Uses('caba:vci_vgsb')," + CR;
	  }
	else
	  {
	      platforminfo += "Uses('caba:vci_vgmn')," + CR;
	  }

	//DG 23.08. added virtual coprocessor
	platforminfo += "Uses('caba:vci_mwmr_stats')," + CR
	    + "Uses('caba:vci_logger')," + CR
	    + "Uses('caba:vci_local_crossbar')," + CR
	    + "Uses('caba:fifo_virtual_copro_wrapper')," + CR;

	int hwa=1; // at least one HWA present
	
      for (AvatarCoproMWMR copro:TopCellGenerator.avatardd.
	     getAllCoproMWMR ())
	  {
	      if (copro.getCoprocType () == 0)
		{
		    platforminfo += "Uses('caba:vci_input_engine')," + CR
			+ "Uses('common:papr_slot')," + CR
			+ "Uses('caba:generic_fifo')," + CR
			+ "Uses('common:network_io')," + CR;
		    
		}

	      else
		{
		    if (copro.getCoprocType () == 1)
		      {
			  platforminfo +=
			      "Uses('caba:vci_output_engine')," + CR;
			 
		      }


		    else
		      {
			  int i;
			  for (i = 0; i < nb_hwa; i++)
			      {if (hwa>0){
				platforminfo +=
				    "Uses('caba:my_hwa')," + CR;
				hwa=0; }
			    }
		      }
		}
	  }

    if (with_amsCluster > 0) {
        platforminfo += "Uses('caba:gpio2vci')," + CR;
    }

	platforminfo += "Uses('common:elf_file_loader')," + CR
	    + "Uses('common:plain_file_loader')," + CR
	    +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:ppc405'),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:arm'),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:mips32eb'),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:mips32el'),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:niosII'),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:lm32'),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:sparcv8', NWIN=8),"
	    + CR +
	    "Uses('caba:vci_xcache_wrapper', iss_t = 'common:gdb_iss', gdb_iss_t = 'common:iss_memchecker', iss_memchecker_t = 'common:sparcv8', NWIN=2),"
	    + CR + "  ]" + CR2 + "todo = Platform('caba', 'top.cc'," + CR +
	    "        uses=use," + CR + "	cell_size = 4," + CR +
	    "	plen_size = 9," + CR + "	addr_size = 32," +
	    CR + "	rerror_size = 1," + CR +
	    "	clen_size = 1," + CR +
	    "	rflag_size = 1," + CR +
	    "	srcid_size = 8," +
	    CR +
	    "	pktid_size = 1,"
	    + CR +
	    "	trdid_size = 1,"
	    + CR + "	wrplen_size = 1" + CR + ")" + CR2;

	return platforminfo;
    }
}
