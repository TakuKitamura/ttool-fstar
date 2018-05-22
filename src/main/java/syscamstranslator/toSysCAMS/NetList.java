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




/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package syscamstranslator.toSysCAMS;

import ddtranslatorSoclib.AvatarCPU;
import ddtranslatorSoclib.AvatarCoproMWMR;
import ddtranslatorSoclib.AvatarRAM;
import ddtranslatorSoclib.AvatarTTY;

public class NetList {
    public static final String NAME_CLK = "signal_clk";
    public static final String CR = "\n";
    public static final String CR2 = "\n\n";
    private static final String NAME_RST = "signal_resetn";
    private static boolean tracing;

    public static String getNetlist(String icn, boolean _tracing) {
	int nb_clusters = TopCellGenerator.syscams.getAllCrossbar().size();
	
	tracing = _tracing;
	
	String netlist;

	netlist = CR2 + "//------------------------------Netlist---------------------------------" + CR2;
       
	netlist = netlist + "// icu" + CR2;
	netlist = netlist + "  vcifdtrom.add_property(\"interrupt-parent\", vcifdtrom.get_device_phandle(\"vci_xicu\"));" + CR2;

	netlist = netlist + "  vcixicu.p_clk(signal_clk);" + CR;
	netlist = netlist + "  vcixicu.p_resetn(signal_resetn);" + CR2;		
	netlist = netlist + "  vcixicu.p_vci(signal_vci_xicu);" + CR2;	
       
	netlist = netlist + "  vcifdtrom.begin_device_node(\"vci_rttimer\", \"soclib:vci_rttimer\");" + CR;

	netlist = netlist + "  vcifdtrom.add_property(\"interrupts\", 4);" + CR;
	netlist = netlist + "  vcifdtrom.add_property(\"frequency\", 1000000);" + CR;	
	netlist = netlist + "  vcifdtrom.end_node();" + CR2;

	netlist = netlist + "  vcifdtrom.begin_device_node(\"vci_xicu\", \"soclib:vci_xicu\");" + CR2;
	netlist = netlist + "  int irq_map[cpus.size() * 3];" + CR;
	netlist = netlist + "  for ( size_t i = 0; i < cpus.size(); ++i )" + CR;
	netlist = netlist + "    {" + CR;
	netlist = netlist + "      irq_map[i*3 + 0] = i;" + CR;
	netlist = netlist + "      irq_map[i*3 + 1] = vcifdtrom.get_cpu_phandle(i);" + CR;
	netlist = netlist + "      irq_map[i*3 + 2] = 0;" + CR;
	netlist = netlist + "    }" + CR2;
	netlist = netlist + "  vcifdtrom.add_property(\"interrupt-map\", irq_map, cpus.size() * 3);" + CR;
	netlist = netlist + "  vcifdtrom.add_property(\"frequency\", 1000000);" + CR2;

	netlist = netlist + "  vcifdtrom.add_property(\"param-int-pti-count\", 1);" + CR;
	netlist = netlist + "  vcifdtrom.add_property(\"param-int-hwi-count\", xicu_n_irq);" + CR;
	netlist = netlist + "  vcifdtrom.add_property(\"param-int-wti-count\", cpus.size());" + CR;
	netlist = netlist + "  vcifdtrom.add_property(\"param-int-irq-count\", cpus.size());" + CR;
	netlist = netlist + "  vcifdtrom.end_node();" + CR2;

	netlist = netlist + "  for ( size_t i = 0; i < xicu_n_irq; ++i )" + CR;
	netlist = netlist + "    vcixicu.p_hwi[i](signal_xicu_irq[i]);" + CR2;

	netlist = netlist + "///////////////// cpus" + CR2;

	netlist = netlist + "vcifdtrom.begin_cpus();" + CR2;
	netlist = netlist + "for ( size_t i = 0; i < cpus.size(); ++i ){" + CR;
	netlist = netlist + "   // configure het_rom" + CR;
	netlist = netlist + "  vcihetrom.add_srcid(*cpus[i]->text_ldr, IntTab(i));" + CR;

	netlist = netlist + "  // add cpu node to device tree" + CR;
	netlist = netlist + "  vcifdtrom.begin_cpu_node(std::string(\"cpu:\") + cpus[i]->type, i);" + CR;
	netlist = netlist + "  vcifdtrom.add_property(\"freq\", 1000000);" + CR;
	netlist = netlist + "  vcifdtrom.end_node();" + CR2;

	netlist = netlist + "// connect cpu" + CR;
	netlist = netlist + "  cpus[i]->connect(cpus[i], signal_clk, signal_resetn, signal_vci_m[i]);" + CR;	             
	if(icn=="vgmn")
	    {
		netlist = netlist + "vgmn.p_to_initiator[i](signal_vci_m[i]);" + CR;
	    }
	else{
	    netlist = netlist + "vgsb.p_to_initiator[i](signal_vci_m[i]);" + CR;
	}
	
	netlist = netlist + "vcixicu.p_irq[i](cpus[i]->irq_sig[0]);" + CR;
	netlist = netlist + " }" + CR;
	netlist = netlist + " vcifdtrom.end_node();" + CR2;

	netlist = netlist + "  vcihetrom.p_clk(signal_clk);" + CR;
	netlist = netlist + "  vcifdtrom.p_clk(signal_clk);" + CR;
	netlist = netlist + "  vcirom.p_clk(signal_clk);" + CR;
	netlist = netlist + "  vcisimhelper.p_clk(signal_clk);" + CR;
	netlist = netlist + "  vcirttimer.p_clk(signal_clk);" + CR;

	netlist = netlist + "  vcihetrom.p_resetn(signal_resetn);" + CR;	
	netlist = netlist + "  vcifdtrom.p_resetn(signal_resetn);" + CR;
	netlist = netlist + "  vcirom.p_resetn(signal_resetn);" + CR;
	netlist = netlist + "  vcisimhelper.p_resetn(signal_resetn);" + CR;
	netlist = netlist + "  vcirttimer.p_resetn(signal_resetn);" + CR;
	netlist = netlist + "  vcihetrom.p_vci(signal_vci_vcihetrom);" + CR;	
		
	netlist = netlist + "  vcifdtrom.p_vci(signal_vci_vcifdtrom);" + CR;
	netlist = netlist + "  vcirom.p_vci(signal_vci_vcirom);" + CR;
	netlist = netlist + "  vcisimhelper.p_vci(signal_vci_vcisimhelper);" + CR;
	netlist = netlist + "  vcirttimer.p_vci(signal_vci_vcirttimer);" + CR;
	netlist = netlist + "  vcirttimer.p_irq[0](signal_xicu_irq[4]);" + CR2;

	if(icn=="vgmn"){
	    netlist = netlist + " vgmn.p_clk(signal_clk);" + CR;
	    netlist = netlist + "  vgmn.p_resetn(signal_resetn);" + CR;
	    netlist = netlist + "  vgmn.p_to_target[0](signal_vci_vcihetrom);" + CR;
	    netlist = netlist + "  vgmn.p_to_target[1](signal_vci_vcirom);" + CR;		       
	    netlist = netlist + "  vgmn.p_to_target[3](signal_vci_vcisimhelper);" + CR2;
	    netlist = netlist + "  vgmn.p_to_target[4](signal_vci_xicu);" + CR;
	    netlist = netlist + "  vgmn.p_to_target[5](signal_vci_vcirttimer);" + CR2;
	    netlist = netlist + "  vgmn.p_to_target[6](signal_vci_vcifdtrom);" + CR2;	
	    netlist = netlist + "  vgmn.p_to_initiator[cpus.size()](signal_vci_bdi);" + CR;
	    netlist = netlist + "  vgmn.p_to_initiator[cpus.size()+1](signal_vci_vcifdaccessi);" + CR;
	    netlist = netlist + "  vgmn.p_to_initiator[cpus.size()+2](signal_vci_etherneti);" + CR2;
	}
	else{	
	    netlist = netlist + " vgsb.p_clk(signal_clk);" + CR;
	    netlist = netlist + "  vgsb.p_resetn(signal_resetn);" + CR;
	    netlist = netlist + "  vgsb.p_to_target[0](signal_vci_vcihetrom);" + CR;
	    netlist = netlist + "  vgsb.p_to_target[1](signal_vci_vcirom);" + CR;		       
	    netlist = netlist + "  vgsb.p_to_target[3](signal_vci_vcisimhelper);" + CR2;
	    netlist = netlist + "  vgsb.p_to_target[4](signal_vci_xicu);" + CR;
	    netlist = netlist + "  vgsb.p_to_target[5](signal_vci_vcirttimer);" + CR2;
	    netlist = netlist + "  vgsb.p_to_target[6](signal_vci_vcifdtrom);" + CR2;	
	    netlist = netlist + "  vgsb.p_to_initiator[cpus.size()](signal_vci_bdi);" + CR;
	    netlist = netlist + "  vgsb.p_to_initiator[cpus.size()+1](signal_vci_vcifdaccessi);" + CR;
	    netlist = netlist + "  vgsb.p_to_initiator[cpus.size()+2](signal_vci_etherneti);" + CR2;
	}
			
	if(nb_clusters==0){
	    netlist = netlist + "// RAM netlist" + CR2;
	    for (AvatarRAM ram : TopCellGenerator.syscams.getAllRAM()) {    
     
		netlist = netlist + ram.getMemoryName()+".p_clk(" + NAME_CLK + ");" + CR;
		netlist = netlist + ram.getMemoryName()+".p_resetn(" + NAME_RST + ");" + CR;
		netlist = netlist + ram.getMemoryName()+".p_vci(signal_vci_vciram"+ram.getIndex()+");" + CR2;
		if(icn=="vgmn"){
		    netlist = netlist + "vgmn.p_to_target["+(ram.getNo_target())+"](signal_vci_vciram"+ram.getIndex()+");" + CR2;
		}
		else{
		    netlist = netlist + "vgsb.p_to_target["+(ram.getNo_target())+"](signal_vci_vciram"+ram.getIndex()+");" + CR2;
		}		   
	    }		  
	}

	/* clustered version */
	/* one or several ram, one locks engine, one mwmr ram and one mwmrd ram per cluster*/
	else{		   
	    int i;  
	    netlist = netlist + "// RAM netlist" + CR2;
	    for (AvatarRAM ram : TopCellGenerator.syscams.getAllRAM()) {    
     
		netlist = netlist + ram.getMemoryName()+".p_clk(" + NAME_CLK + ");" + CR;
		netlist = netlist + ram.getMemoryName()+".p_resetn(" + NAME_RST + ");" + CR;
		netlist = netlist + ram.getMemoryName()+".p_vci(signal_vci_vciram"+ram.getIndex()+");" + CR2;
		//target number for local cluster: this is set at avatardd creation	      		    
		netlist = netlist + "crossbar"+ram.getNo_cluster()+".p_to_target["+ram.getNo_target()+"](signal_vci_vciram"+ram.getIndex()+");" + CR2;		  	   
	    }
	
	    //one mwmr ram and one mwmrdram per cluster are added transparently

	/*convention for local target ids on cluster :
	  channel: 0
	  mwmr_ram: 1
	  mwmrd_ram: 2
          locks: 3
          ram: 4
          tty: 5
	 */

	    /* for(i=0;i<nb_clusters;i++){
		netlist = netlist + "crossbar"+i+".p_to_target["+1+"](signal_vci_mwmr_ram"+i+");" + CR2;
		//netlist = netlist +"mwmr_ram"+i+".p_irq[0](signal_xicu_irq[0]);" + CR2;		  
		netlist = netlist + "crossbar"+i+".p_to_target["+2+"](signal_vci_mwmrd_ram"+i+");" + CR2;
		//netlist = netlist +"mwmr_ram"+i+".p_irq[0](signal_xicu_irq[0]);" + CR2;		  
	    }	 */
	}
		   	
	if(nb_clusters==0){
	    // we can have several TTYs and each is associated to the fdtrom 

	    /*	    if(icn=="vgmn"){
		netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target())+"](signal_vci_vcifdaccesst);" + CR; 
		netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+1)+"](signal_vci_ethernett);" + CR;	
		netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+2)+"](signal_vci_bdt);" + CR;	
	netlist = netlist + "vgmn.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+3)+"](signal_vci_vcilocks);" + CR;	
	    }else{ //vgsb 
		netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target())+"](signal_vci_vcifdaccesst);" + CR; 
		netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+1)+"](signal_vci_ethernett);" + CR;	
		netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+2)+"](signal_vci_bdt);" + CR;	
		   		netlist = netlist + "vgsb.p_to_target["+(TopCellGenerator.avatardd.getNb_target()+3)+"](signal_vci_vcilocks);" + CR;	
		   }*/

	    int l=8; //number of last tty ToDo
	    if(icn=="vgmn"){
		netlist = netlist + "vgmn.p_to_target["+(l)+"](signal_vci_vcifdaccesst);" + CR; 
		netlist = netlist + "vgmn.p_to_target["+(l+1)+"](signal_vci_ethernett);" + CR;	
		netlist = netlist + "vgmn.p_to_target["+(l+2)+"](signal_vci_bdt);" + CR;	
	netlist = netlist + "vgmn.p_to_target["+(l+3)+"](signal_vci_vcilocks);" + CR;	
	int i;
	//DG 14.09.
	int coproc_count=0;
	for(i=0;i<coproc_count;i++){

	    netlist = netlist + "vgmn.p_to_target["+(l+4+i)+"](signal_mwmr_"+i+"_target);" + CR;
	}
	    }else{ //vgsb 
		netlist = netlist + "vgsb.p_to_target["+(l)+"](signal_vci_vcifdaccesst);" + CR; 
		netlist = netlist + "vgsb.p_to_target["+(l+1)+"](signal_vci_ethernett);" + CR;	
		netlist = netlist + "vgsb.p_to_target["+(l+2)+"](signal_vci_bdt);" + CR;	
		   		netlist = netlist + "vgsb.p_to_target["+(l+3)+"](signal_vci_vcilocks);" + CR;	

	//DG 14.09.
				int coproc_count=0;
				int i;
	  for(i=0;i<coproc_count;i++){
	      netlist = netlist + "vgmn.p_to_target["+(l+4+i)+"](signal_mwmr_"+i+"_target);" + CR;
	  }
	    }

	}else{
	    /* cluster case */
	    if(icn=="vgmn"){
		netlist = netlist + "vgmn.p_to_target["+5+"](signal_vci_vcifdaccesst);" + CR; 						
		netlist = netlist + "vgmn.p_to_target["+6+"](signal_vci_ethernett);" + CR;    
													     netlist = netlist + "vgmn.p_to_target["+7+"](signal_vci_bdt);" + CR;	
	    }else{
		netlist = netlist + "vgsb.p_to_target["+5+"](signal_vci_vcifdaccesst);" + CR; 
		netlist = netlist + "vgsb.p_to_target["+6+"](signal_vci_ethernett);" + CR;      
		netlist = netlist + "vgsb.p_to_target["+7+"](signal_vci_bdt);" + CR;	                   
	    }
	}
	//netlist = netlist + "vcifdtrom.begin_device_node(\"vci_multi_tty\",\"soclib:vci_multi_tty\");" + CR2;

	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 0);" + CR2;
	netlist = netlist + "vcifdtrom.end_node();;" + CR2;

	netlist = netlist + "// TTY netlist" + CR2;
	int i=0;

	for (AvatarTTY tty : TopCellGenerator.syscams.getAllTTY()){
	    netlist = netlist + tty.getTTYName()+".p_clk(signal_clk);" + CR;
	    netlist = netlist + tty.getTTYName()+".p_resetn(signal_resetn);" + CR;
	    netlist = netlist + tty.getTTYName()+".p_vci(signal_vci_tty"+i+");" + CR2; 		    
	    int no_irq_tty=0;
	    if(nb_clusters==0){
		
		if(icn=="vgmn"){
		    netlist = netlist + "vcifdtrom.begin_device_node(\"vci_multi_tty"+i+"\",\"soclib:vci_multi_tty"+i+"\");" + CR2;
		    netlist = netlist + "vgmn.p_to_target["+tty.getNo_target()+"](signal_vci_tty"+i+");" + CR2;
		    netlist = netlist + tty.getTTYName()+".p_irq[0](signal_xicu_irq["+no_irq_tty+"]);" + CR2;	
		}else{
		    netlist = netlist + "vcifdtrom.begin_device_node(\"vci_multi_tty"+i+"\",\"soclib:vci_multi_tty"+tty.getNo_tty()+"\");" + CR2;
		    netlist = netlist + "vgsb.p_to_target["+tty.getNo_target()+"](signal_vci_tty"+i+");" + CR2;		    
		    netlist = netlist + tty.getTTYName()+".p_irq[0](signal_xicu_irq["+no_irq_tty+"]);" + CR2;	
		}
	    }	
	    
	    //we have a clustered architecture: identify local crossbar	
	    else{ 
		int j;		   
		for(j=0;j<nb_clusters;j++){
		    netlist = netlist + "crossbar"+j+".p_to_target["+tty.getNo_target()+"](signal_vci_tty"+j+");" + CR2;
		    //recalculate irq addresses, 5 devices generating irq per cluster
		    netlist = netlist + tty.getTTYName()+".p_irq[0](signal_xicu_irq["+(tty.getNo_cluster()*5)+"]);" + CR2;		      
		}	 
	    }
	    i++;
	    //One ICU per cluster per default
	    no_irq_tty+=6;//if there is more than one tty, irq >5
	}

	//////////////// fdrom
	   
       	netlist = netlist + "{" + CR2;
       	netlist = netlist + "  vcifdtrom.begin_node(\"aliases\");" + CR;		netlist = netlist + "  vcifdtrom.add_property(\"timer\", vcifdtrom.get_device_name(\"vci_rttimer\") + \"[0]\");" + CR;
       	netlist = netlist + "  vcifdtrom.add_property(\"console\", vcifdtrom.get_device_name(\"vci_multi_tty0\") + \"[0]\");" + CR;
       	netlist = netlist + "  vcifdtrom.end_node();" + CR;
       	netlist = netlist + "}" + CR2;

	//////////////// ethernet

 	netlist = netlist + "vcieth.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcieth.p_resetn(signal_resetn);" + CR;
  	netlist = netlist + "vcieth.p_irq(signal_xicu_irq[3]);" + CR;
  	netlist = netlist + "vcieth.p_vci_target(signal_vci_ethernett);" + CR;
  	netlist = netlist + "vcieth.p_vci_initiator(signal_vci_etherneti);" + CR;

  	netlist = netlist + "vcifdtrom.begin_device_node(\"vci_ethernet\", \"soclib:vci_ethernet\");" + CR;
  	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 3);" + CR;
  	netlist = netlist + "vcifdtrom.end_node();" + CR;

	//////////////// block device

 	netlist = netlist + "vcibd.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcibd.p_resetn(signal_resetn);" + CR;
  	netlist = netlist + "vcibd.p_irq(signal_xicu_irq[1]);" + CR;
  	netlist = netlist + "vcibd.p_vci_target(signal_vci_bdt);" + CR;
  	netlist = netlist + "vcibd.p_vci_initiator(signal_vci_bdi);" + CR;

  	netlist = netlist + "vcifdtrom.begin_device_node(\"vci_block_device\", \"soclib:vci_block_device\");" + CR;
  	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 1);" + CR;
  	netlist = netlist + "vcifdtrom.end_node();" + CR;

	//////////////// fd access
 	netlist = netlist + "vcihetrom.add_srcid(*cpus[0]->text_ldr, IntTab(cpus.size()+1));" + CR; /* allows dma read in rodata */

  	netlist = netlist + "vcifd.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcifd.p_resetn(signal_resetn);" + CR;
  	netlist = netlist + "vcifd.p_irq(signal_xicu_irq[2]);" + CR;
  	netlist = netlist + "vcifd.p_vci_target(signal_vci_vcifdaccesst);" + CR;
  	netlist = netlist + "vcifd.p_vci_initiator(signal_vci_vcifdaccessi);" + CR;

  	netlist = netlist + "vcifdtrom.begin_device_node(\"vci_fd_access\", \"soclib:vci_fd_access\");" + CR;
  	netlist = netlist + "vcifdtrom.add_property(\"interrupts\", 2);" + CR;
  	netlist = netlist + "vcifdtrom.end_node();" + CR2;

	//////////////// locks
 
  	netlist = netlist + "vcilocks.p_clk(signal_clk);" + CR;
  	netlist = netlist + "vcilocks.p_resetn(signal_resetn);" + CR;     
  	netlist = netlist + "vcilocks.p_vci(signal_vci_vcilocks);" + CR;	

	////////////////MWMR controller; hypothesis 1 per coprocessor
	//	if(nb_cluster == 0){
	    i=0;
	    int coproc_count=0;

	    for (AvatarCoproMWMR copro : TopCellGenerator.syscams.getAllCoproMWMR()){
		//a coprocessor with its FIFO interface built from HWA 
		netlist = netlist +"hwa"+i+".p_clk(signal_clk);" + CR;
		netlist = netlist +"hwa"+i+".p_resetn(signal_resetn);" + CR;	      	
                netlist = netlist +"hwa"+i+".p_from_ctrl["+i+"](signal_fifo_"+i+"_from_ctrl);" + CR;
		netlist = netlist +"hwa"+i+".p_to_ctrl["+i+"](signal_fifo_"+i+"_to_ctrl);" + CR2;
	
		//MWMR controller of the HWA
		netlist = netlist +copro.getCoprocName() +".p_clk(signal_clk);" + CR;
		netlist = netlist +copro.getCoprocName() +".p_resetn(signal_resetn);" + CR;
		netlist = netlist +copro.getCoprocName() +".p_vci_initiator(signal_mwmr_"+i+"_initiator);" + CR;
		netlist = netlist + copro.getCoprocName()+".p_vci_target(signal_mwmr_"+i+"_target);" + CR2;
		netlist = netlist +copro.getCoprocName() +".p_from_coproc["+i+"](signal_fifo_"+i+"_from_ctrl);" + CR;
netlist = netlist +copro.getCoprocName() +".p_to_coproc["+i+"](signal_fifo_"+i+"_to_ctrl);" + CR;
//DG 5.9.
//	netlist = netlist +copro.getCoprocName() +".status();" + CR;
//	netlist = netlist +copro.getCoprocName() +".config();" + CR;
i++;
coproc_count++;
          }
	   
	
   //If there is a spy, add logger or stats to vci interface

	    i=0;
   for (AvatarCPU cpu : TopCellGenerator.syscams.getAllCPU()) { 
       int number = cpu.getNo_proc();	
	  if(cpu.getMonitored()==1){
	  netlist=netlist+CR+
	  "logger"+i+".p_clk(signal_clk);" +CR+
	  "logger"+i+".p_resetn(signal_resetn);" +CR+	     
	  "logger"+i+".p_vci(signal_vci_m["+number+"]);" +CR2;
	  i++;
	  }	
	  
   }
   int j=0;
  
	for (AvatarRAM ram : TopCellGenerator.syscams.getAllRAM()) { 
	    if (ram.getMonitored()==1){	
		int number = number = ram.getIndex();
		netlist += "logger"+i+".p_clk(signal_clk);" + CR;
		netlist += "logger"+i+".p_resetn(signal_resetn);" + CR; 
		netlist += "logger"+i+".p_vci(signal_vci_vciram"+number+");" + CR2;  	  
		i++;   
	    }	
	    else{
		
		if (ram.getMonitored()==2){
		    int number = number = ram.getIndex();	
		    netlist += "mwmr_stats"+j+".p_clk(signal_clk);" + CR;
		    netlist += "mwmr_stats"+j+".p_resetn(signal_resetn);" + CR; 
		    netlist += "mwmr_stats"+j+".p_vci(signal_vci_vciram"+number+");" + CR2;		    
		    j++;	      
		}	 		
	    }	   	   
	}

	//if (with_hw_accellerator>0){  

	int p=0;
	//for testing:   vci_synthetic_initiator.h and vci_synthetic_target.h    
	//for (AvatarCoproMWMR HWAccelerator : TopCellGenerator.avatardd.getAllCoproMWMR()) {
    /*    netlist += "mwmr"+HWAccelerator.getNo()+".p_clk(signal_clk);" + CR;
    netlist += "mwmr"+HWAccelerator.getNo()+".p_resetn(signal_resetn);" + CR;
    netlist += "mwmr"+HWAccelerator.getNo()+".p_vci_initiator(signal_mwmr"+HWAccelerator.getNo()+"_initiator);" + CR;
    netlist += "	mwmr"+HWAccelerator.getNo()+".p_vci_target(signal_mwmr"+HWAccelerator.getNo()+"_target);" + CR;
    netlist += "	mwmr"+HWAccelerator.getNo()+".p_from_coproc["+HWAccelerator.getNo()+"](signal_fifo_to_ctrl);" + CR;
    netlist += "	mwmr"+HWAccelerator.getNo()+".p_to_coproc["+HWAccelerator.getNo()+"](signal_fifo_from_ctrl);" + CR;*/

	/*  netlist += "mwmr"+p+".p_clk(signal_clk);" + CR;
    netlist += "mwmr"+p+".p_resetn(signal_resetn);" + CR;
    netlist += "mwmr"+p+".p_vci_initiator(signal_mwmr"+p+"_initiator);" + CR;
    netlist += "	mwmr"+p+".p_vci_target(signal_mwmr"+p+"_target);" + CR;
    netlist += "	mwmr"+p+".p_from_coproc["+p+"](signal_fifo_"+p+"_to_ctrl);" + CR;
    netlist += "	mwmr"+p+".p_to_coproc["+p+"](signal_fifo_"+p+"_from_ctrl);" + CR;
    }	 */   
//	   }
		
	//generate trace file if marked trace option 
	
	if(tracing){
		netlist += "sc_trace_file *tf;" + CR;
	netlist += "tf=sc_create_vcd_trace_file(\"mytrace\");" + CR;
	netlist += "sc_trace(tf,signal_clk,\"CLK\");" + CR;
	netlist += "sc_trace(tf,signal_resetn,\"RESETN\");" + CR;

	netlist += "sc_trace(tf, signal_vci_xicu,\"signal_vci_xicu\");" + CR;
	netlist += "sc_trace(tf, signal_vci_vcifdtrom,\"signal_vci_vcifdtrom\");" + CR;
	netlist += "sc_trace(tf, signal_vci_vcihetrom,\"signal_vci_vcihetrom\");" + CR;
	netlist += "sc_trace(tf, signal_vci_vcirom ,\"signal_vci_vcirom\");" + CR;
	netlist += "sc_trace(tf, signal_vci_vcisimhelper,\"signal_vci_vcisimhelper\");" + CR;
	netlist += "sc_trace(tf, signal_vci_vcirttimer ,\"signal_vci_vcirttimer\");" + CR;	    
	netlist += "sc_trace(tf, signal_vci_vcifdaccessi,\"signal_vci_vcifdaccessi\");" + CR;
	netlist += "sc_trace(tf,signal_vci_vcifdaccesst ,\"signal_vci_vcifdaccesst\");" + CR;
	netlist += "sc_trace(tf,signal_vci_bdi ,\"signal_vci_bdi\");" + CR;
	netlist += "sc_trace(tf, signal_vci_bdt,\"signal_vci_bdt\");" + CR;
	netlist += "sc_trace(tf, signal_vci_etherneti,\"signal_vci_etherneti\");" + CR;
	netlist += "sc_trace(tf,signal_vci_ethernett ,\"signal_vci_ethernett\");" + CR;

	for(i=0;i<TopCellGenerator.syscams.getNb_init();i++){ 
	    netlist += "sc_trace(tf,signal_vci_m["+ i +"] ,\"signal_vci_m["+ i +"]\");" + CR;
	}

	i=0;
	for (AvatarTTY tty : TopCellGenerator.syscams.getAllTTY()) { 
       
	    netlist += "sc_trace(tf,signal_vci_tty"+tty.getNo_tty()+",\"TTY"+tty.getNo_tty()+"\");" + CR; 
	    netlist += "sc_trace(tf,signal_xicu_irq["+ i +"] ,\"signal_xicu_irq["+ i +"]\");" + CR;    
	    i++;
	}
    
	netlist += "sc_trace(tf,signal_xicu_irq["+i+"] ,\"signal_xicu_irq["+i+"]\");" + CR;  
	netlist += "sc_trace(tf,signal_xicu_irq["+i+"] ,\"signal_xicu_irq["+i+"]\");" + CR; 
	netlist += "sc_trace(tf,signal_xicu_irq["+i+"] ,\"signal_xicu_irq["+i+"]\");" + CR; 
    
	for (AvatarRAM ram : TopCellGenerator.syscams.getAllRAM()) { 
	    if (ram.getMonitored()==0){			
		netlist += "sc_trace(tf,signal_vci_vciram"+ram.getIndex()+",\"Memory"+ram.getIndex()+"\");" + CR;
	    }
	    }	   
    	}
    netlist = netlist + "  sc_core::sc_start(sc_core::sc_time(0, sc_core::SC_NS));" + CR;
    netlist = netlist + "  signal_resetn = false;" + CR;
    netlist = netlist + "  sc_core::sc_start(sc_core::sc_time(1, sc_core::SC_NS));" + CR;
    netlist = netlist + "  signal_resetn = true;" + CR;
    netlist = netlist + "  sc_core::sc_start();" + CR;
    if(tracing){
    	netlist += "sc_close_vcd_trace_file(tf);" + CR;
    	}
    netlist = netlist + CR + "  return EXIT_SUCCESS;"+ CR;
    netlist = netlist +"}" + CR;
    return netlist;		
    }
}