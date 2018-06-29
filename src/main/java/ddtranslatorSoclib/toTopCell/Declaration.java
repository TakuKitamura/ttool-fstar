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
            v2.0 Daniela GENIUS, Julien HENON 2015 
	    v2.1 Daniela GENIUS, 2016, 2017 */

package ddtranslatorSoclib.toTopCell;

import ddtranslatorSoclib.*;
import avatartranslator.AvatarRelation;
import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSignal;
import avatartranslator.AvatarSpecification;

public class Declaration {
    public static AvatarSpecification avspec;
    private static String CR = "\n";
    private static String CR2 = "\n\n";   
    
    public static String generateName(AvatarRelation _ar, int _index) {
        return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
    }
         
	public static String getDeclarations(AvatarSpecification _avspec) {
	        avspec =_avspec;
	   
		String declaration = "//----------------------------Instantiation-------------------------------" + CR2;	
			
	
		int nb_clusters = TopCellGenerator.avatardd.getAllCrossbar().size();	
		
		boolean trace_caba=true; 

	if(nb_clusters==0){
		declaration += CR
		    + "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0), maptab);" + CR;
	}
	else{
	    declaration += CR
		+ "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0,0), maptab);" + CR;
	}
	if(nb_clusters==0){
	    declaration += "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(1), maptab, data_ldr);" + CR;
	}
	else{
	    declaration += "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(0,1), maptab, data_ldr);" + CR;
	}

	if(nb_clusters==0){
	    declaration += " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(3), maptab);" + CR;	
}
	else{
	    declaration += " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(0,3), maptab);" + CR;	
	}

	if(nb_clusters==0){
	    declaration = declaration + "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(4), 1, xicu_n_irq, cpus.size(), cpus.size());" + CR;
	}
	else{	
	    declaration = declaration + "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(0,4), 1, xicu_n_irq, cpus.size(), cpus.size());" + CR;
	}

	if(nb_clusters==0){
	    declaration = declaration + "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(5), maptab, 1, true);" + CR2;
	}
	else{
	    declaration = declaration + "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(0,5), maptab, 1, true);" + CR2;
	}

	if(nb_clusters==0){
	    declaration +=  "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(6), maptab);" + CR;
	}
	else{
	    declaration +=  "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(0,6), maptab);" + CR;
	}

	int last_tty=0;	   			    	
	if(nb_clusters==0){
	    int i=0;
	    for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){
		declaration += "caba::VciMultiTty<vci_param> " + tty.getTTYName()+ "(\"" + tty.getTTYName()+ "\", IntTab(" + tty.getNo_target()+ "), maptab, \"vci_multi_tty"+i+"\", NULL);"+ CR;
		i++;
		last_tty=tty.getNo_target()+1;
	    }

	 //target address depends on number of TTYs and RAMs
	
	if(nb_clusters==0){
	    // declaration +=  "caba::VciLocks<vci_param> vcilocks(\"vcilocks\", IntTab("+(TopCellGenerator.avatardd.getNb_target()+3)+"), maptab);" + CR;
	    // declaration +=  "caba::VciLocks<vci_param> vcilocks(\"vcilocks\", IntTab("+(last_tty+3)+"), maptab);" + CR;
	}	
	else{
	    //   declaration +=  "caba::VciLocks<vci_param> vcilocks(\"vcilocks\", IntTab(0,8), maptab);" + CR;
	}

	for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) 
	     if(ram.getIndex()==0){
		 declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab(2), maptab);" + CR; 
	     }
	     else{
	     declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab("
		 + ram.getNo_target() + "), maptab);" + CR; 
	     }
	}
	else{
	    int i=0;
	    for (AvatarTTY tty : TopCellGenerator.avatardd.getAllTTY()){
		declaration += "caba::VciMultiTty<vci_param> " + tty.getTTYName()+ "(\"" + tty.getTTYName()+ "\", IntTab("+ tty.getNo_cluster()+"," + tty.getNo_target()+ "), maptab, \"vci_multi_tty"+i+"\", NULL);"+ CR;
		i++;}

	    for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) 
		declaration += "soclib::caba::VciRam<vci_param>" + ram.getMemoryName()+ "(\"" + ram.getMemoryName()+ "\"" + ", IntTab("+ram.getNo_cluster()+","
		    + ram.getNo_target() + "), maptab);" + CR2; 
	}
	if(nb_clusters==0){
	    /*declaration +=  "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(cpus.size()+1), IntTab("+(TopCellGenerator.avatardd.getNb_target())+"));" + CR;
	declaration +=  "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(cpus.size()+2), IntTab("+(TopCellGenerator.avatardd.getNb_target()+1)+"), \"soclib0\");" + CR;
	declaration +=  "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(cpus.size()), IntTab("+(TopCellGenerator.avatardd.getNb_target()+2)+"),\"block0.iso\", 2048);" + CR;*/

declaration +=  "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(cpus.size()+1), IntTab("+last_tty +"));" + CR;
	declaration +=  "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(cpus.size()+2), IntTab("+(last_tty +1)+"), \"soclib0\");" + CR;
	declaration +=  "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(cpus.size()), IntTab("+(last_tty +2)+"),\"block0.iso\", 2048);" + CR;

	  //only non-clustered version
         int hwa_no=0;
	 //int target_no = TopCellGenerator.avatardd.getNb_target();
	 int target_no = (last_tty +4);//DG 5.9.
	 int init_no = TopCellGenerator.avatardd.getNb_init();
	  for (AvatarCoproMWMR copro : TopCellGenerator.avatardd.getAllCoproMWMR()){
	      //	      declaration += "caba::VciMwmrController<vci_param> " + copro.getCoprocName()+ "(\"" + copro.getCoprocName()+ "\", maptab, IntTab("+copro.getSrcid() + "), IntTab("+copro.getTgtid() + "),copro.getPlaps(),copro.getFifoToCoProcDepth(),copro.getNToCopro(),copro.getNFromCopro(),copro.getNConfig(),copro.getNStatus(), copro.getUseLLSC());"+ CR;
	
	      declaration += "caba::VciMwmrController<vci_param> " + copro.getCoprocName()+ "_wrapper(\"" + copro.getCoprocName()+ "_wrapper\", maptab, IntTab("+(init_no-1)+"), IntTab("+target_no+ "),"+copro.getPlaps()+","+copro.getFifoToCoprocDepth()+","+copro.getFifoFromCoprocDepth()+","+copro.getNToCopro()+","+copro.getNFromCopro()+","+copro.getNConfig()+","+copro.getNStatus()+","+ copro.getUseLLSC()+");"+ CR2;

//one virtual component for each hardware accellerator, info from diplodocus (not yet implemented)

//DG 28.08. 
	      //   declaration += "soclib::caba::FifoVirtualCoprocessorWrapper hwa"+hwa_no+"(\"hwa"+hwa_no+"\",1,1,1,1);"+ CR2;
	      if(copro.getCoprocType()==0){
		  declaration += "soclib::caba::VciInputEngine<vci_param>"  + copro.getCoprocName()+"(\"" + copro.getCoprocName()+ "\", 1 , maptab,\"input.txt\",1024,1,8);"+ CR;
	      }
	      /*VciInputEngine(
		 sc_core::sc_module_name insname,
		 const soclib::common::IntTab &index, //???channel index
		 const soclib::common::MappingTable &mt,
		 const std::string &finput,
		 uint32_t dext,
		 uint32_t nchannels,
		 uint32_t burstlength);*/	      
	      //DG 27.04.2018 attention a la numerotation!faut il incrementer ici?
	      else {
		   if(copro.getCoprocType()==1)
		       {
			   declaration += "soclib::caba::VciOutputEngine<vci_param>"+ copro.getCoprocName()+"(\"" + copro.getCoprocName()+ "\", 1 , maptab,1,1,1,\"output.txt\",\"throw.txt\");"+ CR;
			   /*VciOutputEngine(
		  sc_core::sc_module_name insname,
		  const soclib::common::IntTab &index,
		  const soclib::common::MappingTable &mt,
                  uint32_t advance,
                  uint32_t delay,
                  uint32_t size,
		  const std::string &foutput,
		  const std::string &fthrow);*/			   
		       }
	      

	      else{	      
declaration += "dsx::caba::MyHWA"+hwa_no+" hwa"+hwa_no+"(\"hwa"+hwa_no+"\");"+ CR2;
	      hwa_no++;  
	      // init_no++; DG 27.04.2018
	      // target_no++; DG 27.04.2018
	      }
	  }
	      init_no++;
	       target_no++;
	  }
	}else{
	declaration +=  "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(0,cpus.size()+1), IntTab(0,7));" + CR;
	declaration +=  "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(0,cpus.size()+2), IntTab(0,8), \"soclib0\");" + CR;
	declaration +=  "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(0,cpus.size()), IntTab(0,9),\"block0.iso\", 2048);" + CR;

int hwa_no=0;
	 //int target_no = TopCellGenerator.avatardd.getNb_target();
	 int target_no = (last_tty +4);//DG 5.9.
	 int init_no = TopCellGenerator.avatardd.getNb_init();
	  for (AvatarCoproMWMR copro : TopCellGenerator.avatardd.getAllCoproMWMR()){
	      //	      declaration += "caba::VciMwmrController<vci_param> " + copro.getCoprocName()+ "(\"" + copro.getCoprocName()+ "\", maptab, IntTab("+copro.getSrcid() + "), IntTab("+copro.getTgtid() + "),copro.getPlaps(),copro.getFifoToCoProcDepth(),copro.getNToCopro(),copro.getNFromCopro(),copro.getNConfig(),copro.getNStatus(), copro.getUseLLSC());"+ CR;
	
	      declaration += "caba::VciMwmrController<vci_param> " + copro.getCoprocName()+ "_wrapper(\"" + copro.getCoprocName()+ "_wrapper\", maptab, IntTab("+(init_no-1)+"), IntTab("+target_no+ "),"+copro.getPlaps()+","+copro.getFifoToCoprocDepth()+","+copro.getFifoFromCoprocDepth()+","+copro.getNToCopro()+","+copro.getNFromCopro()+","+copro.getNConfig()+","+copro.getNStatus()+","+ copro.getUseLLSC()+");"+ CR2;

//one virtual component for each hardware accellerator, info from diplodocus (not yet implemented)

//DG 28.08. 
	      //   declaration += "soclib::caba::FifoVirtualCoprocessorWrapper hwa"+hwa_no+"(\"hwa"+hwa_no+"\",1,1,1,1);"+ CR2;
	      if(copro.getCoprocType()==0){
		  declaration += "soclib::caba::VciInputEngine<vci_param>" + copro.getCoprocName()+ "(\"" + copro.getCoprocName()+ "\", 1 , maptab,\"input.txt\",1024,1,8);"+ CR;
	      }
	      /*VciInputEngine(
		 sc_core::sc_module_name insname,
		 const soclib::common::IntTab &index, //???channel index
		 const soclib::common::MappingTable &mt,
		 const std::string &finput,
		 uint32_t dext,
		 uint32_t nchannels,
		 uint32_t burstlength);*/	      
	      //DG 27.04.2018 attention a la numerotation!faut il incrementer ici?
	      else {
		   if(copro.getCoprocType()==1)
		       {
			   declaration += "soclib::caba::VciOutputEngine<vci_param>" + copro.getCoprocName()+"(\"" + copro.getCoprocName()+ "\", 1 , maptab,1,1,1,\"output.txt\",\"throw.txt\");"+ CR;
			   /*VciOutputEngine(
		  sc_core::sc_module_name insname,
		  const soclib::common::IntTab &index,
		  const soclib::common::MappingTable &mt,
                  uint32_t advance,
                  uint32_t delay,
                  uint32_t size,
		  const std::string &foutput,
		  const std::string &fthrow);*/			   
		       }
	      

	      else{	      
declaration += "dsx::caba::MyHWA"+hwa_no+" hwa"+hwa_no+"(\"hwa"+hwa_no+"\");"+ CR2;
//target_no++; DG 27.04.
//init_no++; DG 27.04.
	      hwa_no++;
	      }
	      }
	      target_no++;
	      init_no++;
	  }
	
	}

if(nb_clusters==0){

	  for  (AvatarBus bus : TopCellGenerator.avatardd.getAllBus()) {
          
          
	  
	  //declaration += "soclib::caba::VciVgsb<vci_param> vgsb(\"" + bus.getBusName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+4)+");" + CR2;
	  //	  declaration += "soclib::caba::VciVgsb<vci_param> vgsb(\"" + bus.getBusName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+4)+ ");" + CR2;

	  declaration += "soclib::caba::VciVgsb<vci_param> vgsb(\"" + bus.getBusName() + "\"" + " , maptab,"+(3+TopCellGenerator.avatardd.getNb_init())+"," + (TopCellGenerator.avatardd.getNb_target()+3)+ ");" + CR2;//DG 17.05.2018
	  int i=0;

          //if BUS was not last in input file, update here

	  //DG 12.10. enleve
          //bus.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          //bus.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());
	  }	

         for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
	     //
	     //
	  /* The user might have forgotten to specify the following, thus set default values */
	     
	     
	     

	  if(vgmn.getMinLatency()<2)
	  vgmn.setMinLatency(10); //default value; must be > 2
	  if(vgmn.getFifoDepth()<2)
          vgmn.setFifoDepth(8); //default value; must be > 2


	  //	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNb_target()+4)+  "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;
	  //	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, " +(3+TopCellGenerator.avatardd.getNb_init())+"," + (TopCellGenerator.avatardd.getNb_target()+4)+  "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;//DG 28.08.

	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, " +(3+TopCellGenerator.avatardd.getNb_init())+"," + (TopCellGenerator.avatardd.getNb_target()+3)+  "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;//DG 17.05.2018

	  // declaration += "soclib::caba::VciVgmn<vci_param> vgmn(\"" + vgmn.getVgmnName() + "\"" + " , maptab, cpus.size()+3," + (TopCellGenerator.avatardd.getNbRAM()+TopCellGenerator.avatardd.getNbTTY()+4)+  "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;

	  //DG 12.10. enleve 
	  // if VGMN was not last in input file, update here 
	  //   vgmn.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          //vgmn.setnbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target()+4);

	 }



	  /*	VciMwmrController(
		sc_module_name name,
		const MappingTable &mt,
		const IntTab &srcid,
		const IntTab &tgtid,
		const size_t plaps,
		const size_t fifo_to_coproc_depth,
		const size_t fifo_from_coproc_depth,
		const size_t n_to_coproc,
		const size_t n_from_coproc,
		const size_t n_config,
		const size_t n_status,
		const bool use_llsc );
	  */


}
else {

    /***************************************/
    /* clustered interconnect architecture */
    /***************************************/

       
    for  (AvatarBus bus : TopCellGenerator.avatardd.getAllBus()) {
	
	declaration += "soclib::caba::VciVgsb<vci_param>  vgsb(\"" + bus.getBusName() + "\"" + " , maptab, "+ +nb_clusters+"," + nb_clusters + ");" + CR2;
	  
          //if BUS was not last in input file, update here	 
	  int i=0;	
    }	
         
    for  (AvatarVgmn vgmn : TopCellGenerator.avatardd.getAllVgmn()) {
          
          
      	 
	  declaration += "soclib::caba::VciVgmn<vci_param> vgmn (\"" + vgmn.getVgmnName() + "\"" + " , maptab, "+ nb_clusters +"," + nb_clusters +
	      "," + vgmn.getMinLatency() + "," + vgmn.getFifoDepth() + ");" + CR2;
	 	
	 }
	
	 int i=0;
	for  (AvatarCrossbar crossbar : TopCellGenerator.avatardd.getAllCrossbar()) {	    
	  
	    //currently the number on initiators and targets is fixed

	  crossbar.setClusterIndex(i);

	  if (crossbar.getClusterIndex()==0){
		  crossbar.setNbOfAttachedInitiators(nb_clusters);		  
		  crossbar.setNbOfAttachedTargets(13);
	      }
	  else{ 
	      //processor(s) and link to central interconnect are initiators
	      //crossbar.setNbOfAttachedInitiators(2);	 
	      //crossbar.setNbOfAttachedTargets(2);
	      crossbar.setNbOfAttachedInitiators(1);
	      crossbar.setNbOfAttachedTargets(1);
	  }

          
          
	
	  declaration += "soclib::caba::VciLocalCrossbar<vci_param> crossbar"+crossbar.getClusterIndex()+"(\"" + crossbar.getCrossbarName() + "\"" + " , maptab, IntTab("+ crossbar.getClusterIndex()+"),IntTab("+crossbar.getClusterIndex()+"), "+crossbar.getNbOfAttachedInitiators()+", "+crossbar.getNbOfAttachedTargets()+");" + CR2;

          //if CROSSBAR was not last in input file, update here 
          crossbar.setNbOfAttachedInitiators(TopCellGenerator.avatardd.getNb_init()); 
          crossbar.setNbOfAttachedTargets(TopCellGenerator.avatardd.getNb_target());	 
	  i++;
	}
}
int  i=0;
	 //monitoring CPU by logger(1)
	  for (AvatarCPU cpu : TopCellGenerator.avatardd.getAllCPU()) { 
	     
	      if (cpu.getMonitored()==1){
		  
		  declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
		  i++;	      
	      }		    
	  }	 
  
	  int j=0;
	  //monitoring RAM either by logger(1) ou stats (2) 
	  for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM()) { 
	     if (ram.getMonitored()==0){
 
	     }
	      if (ram.getMonitored()==1){
		  
		  declaration += "soclib::caba::VciLogger<vci_param> logger"+i+"(\"logger" + i+"\",maptab);" + CR2;
		  i++;	      
	      }	
	      else{
		  if (ram.getMonitored()==2){		      
		      
		      String strArray="";
		    
		      for(AvatarRelation ar: avspec.getRelations()) {
			 
				  for(i=0; i<ar.nbOfSignals() ; i++) {
			      
				      AvatarSignal as1=ar.getSignal1(i);
				      AvatarSignal as2=ar.getSignal2(i);
				      
				      String chname = generateName(ar,i);
				      strArray=strArray+"\""+chname+"\",";
				  }
				 
		      }
		         
		
		      declaration += "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"+j+"(\"mwmr_stats" + j+"\",maptab, data_ldr, \"mwmr"+j+".log\",stringArray("+strArray+"NULL));" + CR2;
		      j++;	      
		  }	
	     }
	  }	 
 	
return declaration;
	}

}
