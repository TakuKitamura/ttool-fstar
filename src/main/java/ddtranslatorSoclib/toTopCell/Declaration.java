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
//import ddtranslatorSoclib.AvatarCoproMWMR;
import ddtranslatorSoclib.AvatarRAM;
import ddtranslatorSoclib.AvatarTTY;
import ddtranslatorSoclib.AvatarCrossbar;

import myutil.TraceManager;

public class Declaration
{
    public static AvatarSpecification avspec;
    private static String CR = "\n";
    private static String CR2 = "\n\n";
    public static AvatarddSpecification avatardd;
    
    public static String generateName (AvatarRelation _ar, int _index)
    {
	return _ar.block1.getName () + "_" +
	    _ar.getSignal1 (_index).getName () + "__" +
	    _ar.block2.getName () + "_" + _ar.getSignal2 (_index).getName ();
    }


    public static int cpus_in_cluster(AvatarddSpecification dd,int cluster_no){
	avatardd = dd;
	int cpus=0;
	for  (AvatarConnector connector : avatardd.getConnectors()){		
	    AvatarConnectingPoint my_p1= connector.get_p1(); 
	    AvatarConnectingPoint my_p2= connector.get_p2(); 
				
	    AvatarComponent comp1 = my_p1.getComponent();
	    AvatarComponent comp2 = my_p2.getComponent(); 
	    if (comp1 instanceof AvatarCPU){ 
		AvatarCPU comp1cpu = (AvatarCPU)comp1;
		if(comp1cpu.getClusterIndex()==cluster_no)
		    cpus++;
	    }		    		
	    
	}
	return cpus; 
    }
    
    public static int rams_in_cluster(AvatarddSpecification dd,int cluster_no){
	avatardd = dd;
	int rams=0;
	for  (AvatarConnector connector : avatardd.getConnectors()){		
	    AvatarConnectingPoint my_p1= connector.get_p1(); 
	    AvatarConnectingPoint my_p2= connector.get_p2(); 
				
	    AvatarComponent comp1 = my_p1.getComponent();
	    AvatarComponent comp2 = my_p2.getComponent(); 
	    if (comp1 instanceof AvatarRAM){ 
		AvatarRAM comp1ram = (AvatarRAM)comp1;
		if(comp1ram.getClusterIndex()==cluster_no)
		    rams++;			
	    }		    		
	    
	}
	return rams; 
    }
    

    
    public static String getDeclarations (AvatarddSpecification _dd, AvatarSpecification _avspec)
    {
	avspec = _avspec;
	avatardd = _dd;
	String declaration =
	    "//----------------------------Instantiation-------------------------------"
	    + CR2;


	int nb_clusters = TopCellGenerator.avatardd.getAllCrossbar ().size ();

	boolean trace_caba = true;

	if (nb_clusters == 0)
	    {
		declaration += CR
		    +
		    "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0), maptab);"
		    + CR;
	    }
	else
	    {
		declaration += CR
		    +
		    "caba::VciHeterogeneousRom<vci_param> vcihetrom(\"vcihetrom\",  IntTab(0,0), maptab);"
		    + CR;
	    }
	if (nb_clusters == 0)
	    {
		declaration +=
		    "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(1), maptab, data_ldr);"
		    + CR;
	    }
	else
	    {
		declaration +=
		    "caba::VciRam<vci_param> vcirom(\"vcirom\", IntTab(0,1), maptab, data_ldr);"
		    + CR;
	    }

	if (nb_clusters == 0)
	    {
		declaration +=
		    " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(3), maptab);"
		    + CR;
	    }
	else
	    {
		declaration +=
		    " caba::VciSimhelper<vci_param> vcisimhelper    (\"vcisimhelper\", IntTab(0,3), maptab);"
		    + CR;
	    }

	if (nb_clusters == 0)
	    {
		declaration =
		    declaration +
		    "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(5), 1, xicu_n_irq, cpus.size(), cpus.size());"
		    + CR;
	    }
	else
	    {
		declaration =
		    declaration +
		    "caba::VciXicu<vci_param> vcixicu(\"vci_xicu\", maptab, IntTab(0,5), 1, xicu_n_irq, cpus.size(), cpus.size());"
		    + CR;
	    }

	if (nb_clusters == 0)
	    {
		declaration =
		    declaration +
		    "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(4), maptab, 1, true);"
		    + CR2;
	    }
	else
	    {
		declaration =
		    declaration +
		    "caba::VciRtTimer<vci_param> vcirttimer    (\"vcirttimer\", IntTab(0,4), maptab, 1, true);"
		    + CR2;
	    }

	//No DMA yet; planned to make it optional depending on deployment diagram
		if (nb_clusters == 0)
	    {
		declaration +=
		    "caba::VciDma<vci_param> vcidma(\"vci_dma\", maptab,6,6,8);"
		    + CR;
	    }
	else
	    {
		declaration +=
		    "caba::VciDma<vci_param> vcifdma(\"vci_dma\", IntTab(0,6), maptab);"
		    + CR;
	    }
	
	if (nb_clusters == 0)
	    {
		declaration +=
		    "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(7), maptab);"
		    + CR;
	    }
	else
	    {
		declaration +=
		    "caba::VciFdtRom<vci_param> vcifdtrom(\"vci_fdt_rom\", IntTab(0,7), maptab);"
		    + CR;
	    }
	
	int nb_ram=0;
	int nb_tty=0;
	if (nb_clusters == 0)
	    {
		int i = 0;
		//target address depends on number of TTYs and RAMs	     
		for (AvatarRAM ram:TopCellGenerator.avatardd.
			 getAllRAM ()){
		    if (ram.getIndex () == 0)
			{
			    declaration +=
				"soclib::caba::VciRam<vci_param>" +
				ram.getMemoryName () + "(\"" +
				ram.getMemoryName () + "\"" +
				", IntTab(2), maptab);" + CR;
			}
		    else
			{
			    declaration +=
				"soclib::caba::VciRam<vci_param>" +
				ram.getMemoryName () + "(\"" +
				ram.getMemoryName () + "\"" + ", IntTab(" +
				ram.getNo_target () + "), maptab);" + CR;
			}
		    nb_ram++;
		}
		int first_tty = 10+nb_ram;
		for (AvatarTTY tty:TopCellGenerator.avatardd.
			 getAllTTY ())
		    {
			declaration +=
			    "caba::VciMultiTty<vci_param> " + tty.getTTYName () +
			    "(\"" + tty.getTTYName () + "\", IntTab(" + first_tty
			    + "), maptab, \"vci_multi_tty" +
			    i + "\", NULL);" + CR;		  
			first_tty++;
			nb_tty++;
		    }

	    }
	else{//clustered	      	  
	    for (AvatarRAM ram:TopCellGenerator.avatardd.
		     getAllRAM ()){
		declaration +=
		    "soclib::caba::VciRam<vci_param>" +
		    ram.getMemoryName () + "(\"" + ram.getMemoryName () +
		    "\"" + ", IntTab(" + ram.getClusterIndex () + "," +
		    ram.getNo_target () + "), maptab);" + CR2;	
	    }
	    nb_tty=0;
	    for (AvatarTTY tty:TopCellGenerator.avatardd.getAllTTY ())		
		{   int cluster_no=tty.getClusterIndex();
		    nb_ram =
			rams_in_cluster(avatardd, cluster_no);
		    nb_tty=0;
		    if(cluster_no==0){
			declaration +=
			    "caba::VciMultiTty<vci_param> " + tty.getTTYName () +
			    "(\"" + tty.getTTYName () + "\", IntTab(" +
			    tty.getClusterIndex () + "," +  (nb_ram+10)  +
			    "), maptab, \"vci_multi_tty" + cluster_no + "\", NULL);" + CR;
		    }
		    else{
			declaration +=
			    "caba::VciMultiTty<vci_param> " + tty.getTTYName () +
			    "(\"" + tty.getTTYName () + "\", IntTab(" +
			    tty.getClusterIndex () + "," + (nb_ram+nb_tty)  +
			    "), maptab, \"vci_multi_tty" + cluster_no + "\", NULL);" + CR;
			nb_tty++;
		    }
		
		}

	    
	}
	if (nb_clusters == 0)
	    {

		declaration +=
		    "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(cpus.size()+1), IntTab("
		    + 8 + "));" + CR;
		declaration +=
		    "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(cpus.size()+2), IntTab("
		    + 9 + "), \"soclib0\");" + CR;
		declaration +=
		    "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(cpus.size()), IntTab("
		    + 10 + "),\"block0.iso\", 2048);" + CR;

		//non-clustered version
		int hwa_no = 0;
	     
		int target_no = (10+ nb_ram + nb_tty);
		int init_no = TopCellGenerator.avatardd.getNb_init ();
		for (AvatarCoproMWMR copro:TopCellGenerator.avatardd.
			 getAllCoproMWMR ())
		    {
			nb_clusters = TopCellGenerator.avatardd.getAllCrossbar ().size ();
			if(nb_clusters==0){
			    declaration +=
				"caba::VciMwmrController<vci_param> " +
				copro.getCoprocName () + "_wrapper(\"" +
				copro.getCoprocName () +
				"_wrapper\", maptab, IntTab(" + (init_no - 1) +
				"), IntTab(" + target_no + ")," + copro.getPlaps () +
				"," + copro.getFifoToCoprocDepth () + "," +
				copro.getFifoFromCoprocDepth () + "," +
				copro.getNToCopro () + "," + copro.getNFromCopro () +
				"," + copro.getNConfig () + "," +
				copro.getNStatus () + "," + copro.getUseLLSC () +
				");" + CR2;
			}
			else{
			    declaration +=
				"caba::VciMwmrController<vci_param> " +
				copro.getCoprocName () + "_wrapper(\"" +
				copro.getCoprocName () +
				"_wrapper\", IntTab(" + copro.getClusterIndex () + "," +
				(init_no - 1) +" ,IntTab(" + copro.getClusterIndex () + "," +
				target_no + ")," + copro.getPlaps () +
				"," + copro.getFifoToCoprocDepth () + "," +
				copro.getFifoFromCoprocDepth () + "," +
				copro.getNToCopro () + "," + copro.getNFromCopro () +
				"," + copro.getNConfig () + "," +
				copro.getNStatus () + "," + copro.getUseLLSC () +
				");" + CR2;
			}

		    
			//one virtual component for each hardware accellerator, info from diplodocus (not yet implemented)

			if (copro.getCoprocType () == 0)
			    {
				declaration +=
				    "soclib::caba::VciInputEngine<vci_param>" +
				    copro.getCoprocName () + "(\"" +
				    copro.getCoprocName () +
				    "\", 1 , maptab,\"input.txt\",1024,1,8);" + CR;
			    }
			else
			    {
				if (copro.getCoprocType () == 1)
				    {
					declaration +=
					    "soclib::caba::VciOutputEngine<vci_param>"
					    + copro.getCoprocName () + "(\"" +
					    copro.getCoprocName () +
					    "\", 1 , maptab,1,1,1,\"output.txt\",\"throw.txt\");"
					    + CR;
				    }


				else
				    {
					declaration +=
					    //  "dsx::caba::MyHWA" + hwa_no + " hwa" +
					    //  hwa_no + "(\"hwa" + hwa_no + "\");" + CR2;
					    "dsx::caba::MyHWA(\""+copro.getCoprocName ()+ "\");" + CR2;
			
				    }
			    }
			init_no++;
			target_no++;
		    }
	    }
	else
	    {
		declaration +=
		    "caba::VciFdAccess<vci_param> vcifd(\"vcifd\", maptab, IntTab(0,cpus.size()+1), IntTab(0,8));"
		    + CR;
		declaration +=
		    "caba::VciEthernet<vci_param> vcieth(\"vcieth\", maptab, IntTab(0,cpus.size()+2), IntTab(0,9), \"soclib0\");"
		    + CR;
		declaration +=
		    "caba::VciBlockDevice<vci_param> vcibd(\"vcibd\", maptab, IntTab(0,cpus.size()), IntTab(0,10),\"block0.iso\", 2048);"
		    + CR;
		
		//int cluster_no;  cluster_no = copro.getClusterIndex ();	
		//nb_ram = rams_in_cluster(avatardd, cluster_no);
		
		nb_tty = 0;
		
		int init_no = TopCellGenerator.avatardd.getNb_init ();
		int hwa_no = 0;
		for (AvatarCoproMWMR copro:TopCellGenerator.avatardd.
			 getAllCoproMWMR ())
		    {
			int cluster_no = copro.getClusterIndex ();	
			nb_ram = rams_in_cluster(avatardd, cluster_no);
			int target_no = nb_ram+nb_tty;
			declaration +=
			    "caba::VciMwmrController<vci_param> " +
			    copro.getCoprocName () + "_wrapper(\"" +
			    copro.getCoprocName () +
			    "_wrapper\", maptab, IntTab(" + (init_no - 1) +
			    "), IntTab(" + target_no + ")," + copro.getPlaps () +
			    "," + copro.getFifoToCoprocDepth () + "," +
			    copro.getFifoFromCoprocDepth () + "," +
			    copro.getNToCopro () + "," + copro.getNFromCopro () +
			    "," + copro.getNConfig () + "," +
			    copro.getNStatus () + "," + copro.getUseLLSC () +
			    ");" + CR2;

			//future work on virtual coprocessors : one virtual component for each hardware accellerator, info from diplodocus (not yet implemented)
			//   declaration += "soclib::caba::FifoVirtualCoprocessorWrapper hwa"+hwa_no+"(\"hwa"+hwa_no+"\",1,1,1,1);"+ CR2;

			if (copro.getCoprocType () == 0)
			    {
				declaration +=
				    "soclib::caba::VciInputEngine<vci_param>" +
				    copro.getCoprocName () + "(\"" +
				    copro.getCoprocName () +
				    "\", 1 , maptab,\"input.txt\",1024,1,8);" + CR;
			    }
			else
			    {
				if (copro.getCoprocType () == 1)
				    {
					declaration +=
					    "soclib::caba::VciOutputEngine<vci_param>"
					    + copro.getCoprocName () + "(\"" +
					    copro.getCoprocName () +
					    "\", 1 , maptab,1,1,1,\"output.txt\",\"throw.txt\");"
					    + CR;
				    }


				else
				    {
					declaration +=
					    "dsx::caba::MyHWA" + hwa_no + " hwa" +
					    hwa_no + "(\"hwa" + hwa_no + "\");" + CR2;

					hwa_no++;
				    }
			    }
			target_no++;
			init_no++;
		    }

	    }

	if (nb_clusters == 0)
	    {

		for (AvatarBus bus:TopCellGenerator.avatardd.
			 getAllBus ())
		    {
			/*	TraceManager.addDev ("initiators: " +
					     TopCellGenerator.avatardd.
					     getNb_init ());
			TraceManager.addDev ("targets: " +
					     TopCellGenerator.avatardd.
					     getNb_target ());

			declaration +=
			    "soclib::caba::VciVgsb<vci_param> vgsb(\"" +
			    bus.getBusName () + "\"" + " , maptab," + (3 +
								       TopCellGenerator.
								       avatardd.getNb_init
								       ()) + "," +
			    (TopCellGenerator.avatardd.getNb_target () + 3) +
			    ");" + CR2;
			    int i = 0;*/
			TraceManager.addDev ("initiators: " +
					     TopCellGenerator.avatardd.
					     getNb_init ());
			TraceManager.addDev ("targets: " +
					     TopCellGenerator.avatardd.
					     getNb_target ());

			declaration +=
			    "soclib::caba::VciVgsb<vci_param> vgsb(\"" +
			    bus.getBusName () + "\"" + " , maptab," + (3 +
								       TopCellGenerator.
								       avatardd.getNbCPU()) + "," +
			    (TopCellGenerator.avatardd.getNbRAM() + TopCellGenerator.avatardd.getNbTTY()
				 + 10) +
			    ");" + CR2;
			    int i = 0;

		    }

		for (AvatarVgmn vgmn:TopCellGenerator.avatardd.
			 getAllVgmn ())
		    {			
			/*	TraceManager.addDev ("initiators: " +
					     TopCellGenerator.avatardd.
					     getNb_init ());
			TraceManager.addDev ("targets: " +
					     TopCellGenerator.avatardd.
					     getNb_target ());*/
					

			if (vgmn.getMinLatency () < 2)
			    vgmn.setMinLatency (10);	//default value; must be > 2
			if (vgmn.getFifoDepth () < 2)
			    vgmn.setFifoDepth (8);	//default value; must be > 2

			/*	declaration +=
			    "soclib::caba::VciVgmn<vci_param> vgmn(\"" +
			    vgmn.getVgmnName () + "\"" + " , maptab, " + (3 +
									  TopCellGenerator.
									  avatardd.
									  getNb_init
									  ()) +
			    "," + (TopCellGenerator.avatardd.getNb_target () +
				   3) + "," + vgmn.getMinLatency () + "," +
				   vgmn.getFifoDepth () + ");" + CR2;*/
			
			declaration +=
			    "soclib::caba::VciVgmn<vci_param> vgmn(\"" +
			    vgmn.getVgmnName () + "\"" + " , maptab, " + (3 +
									  TopCellGenerator.
									  avatardd.
									  getNbCPU
									  ()) +
			    "," + (TopCellGenerator.avatardd.getNbRAM() + TopCellGenerator.avatardd.getNbTTY()
				 + 10) + "," + vgmn.getMinLatency () + "," +
				   vgmn.getFifoDepth () + ");" + CR2;
		    }

	    }
	else
	    {

		/***************************************/
		/* clustered interconnect architecture */
		/***************************************/


		for (AvatarBus bus:TopCellGenerator.avatardd.
			 getAllBus ())
		    {

			TraceManager.addDev ("VGSB initiators: " +
					     TopCellGenerator.avatardd.
					     getNb_init ());
			TraceManager.addDev ("VGSB targets: " +
					     TopCellGenerator.avatardd.
					     getNb_target ());
		    
			declaration +=
			    "soclib::caba::VciVgsb<vci_param>  vgsb(\"" +
			    bus.getBusName () + "\"" + " , maptab, " +
			    +nb_clusters + "," + nb_clusters + ");" + CR2;

			//if BUS was not last in input file, update here       
			int i = 0;
		    }

		for (AvatarVgmn vgmn:TopCellGenerator.avatardd.
			 getAllVgmn ())
		    {
			TraceManager.addDev ("VGMN initiators: " + nb_clusters);
			TraceManager.addDev ("VGMN targets: " +nb_clusters);

			declaration +=
			    "soclib::caba::VciVgmn<vci_param> vgmn (\"" +
			    vgmn.getVgmnName () + "\"" + " , maptab, " +
			    nb_clusters + "," + nb_clusters + "," +
			    vgmn.getMinLatency () + "," + vgmn.getFifoDepth () +
			    ");" + CR2;
		    }

	     
		for (AvatarCrossbar crossbar:TopCellGenerator.avatardd.
			 getAllCrossbar())
		    {		  	   
		    
			int cluster_no  = crossbar.getClusterIndex ();	
			TraceManager.addDev ("CROSSBAR" +cluster_no+" cpus: " +
					     cpus_in_cluster(avatardd, cluster_no)					 );
			TraceManager.addDev ("CROSSBAR " +cluster_no+" rams: " +
					     rams_in_cluster(avatardd, cluster_no) 		 );

			declaration +=
			    "soclib::caba::VciLocalCrossbar<vci_param> crossbar" +
			    crossbar.getClusterIndex () + "(\"" +
			    crossbar.getCrossbarName () + "\"" +
			    " , maptab, " + crossbar.getClusterIndex () +
			    "," + crossbar.getClusterIndex () + ", " +
			    cpus_in_cluster(avatardd, cluster_no) + ", " +
			    (rams_in_cluster(avatardd, cluster_no)+1) + ");" + CR2;
			//if CROSSBAR was not last in input file, update here
		 
		    }
	    }
	int i = 0;
	//monitoring CPU by logger(1)
	for (AvatarCPU cpu:TopCellGenerator.avatardd.getAllCPU ())
	    {

		if (cpu.getMonitored () == 1)
		    {

			declaration +=
			    "soclib::caba::VciLogger<vci_param> logger" + i +
			    "(\"logger" + i + "\",maptab);" + CR2;
			i++;
		    }
	    }

	int j = 0;
	//monitoring RAM either by logger(1) or stats (2) 
	for (AvatarRAM ram:TopCellGenerator.avatardd.getAllRAM ())
	    {
		if (ram.getMonitored () == 0)
		    {

		    }
		if (ram.getMonitored () == 1)
		    {

			declaration +=
			    "soclib::caba::VciLogger<vci_param> logger" + i +
			    "(\"logger" + i + "\",maptab);" + CR2;
			i++;
		    }
		else
		    {
			if (ram.getMonitored () == 2)
			    {

				String strArray = "";

				for (AvatarRelation ar:avspec.
					 getRelations
					 ())
				    {

					for (i = 0; i < ar.nbOfSignals (); i++)
					    {

						AvatarSignal as1 = ar.getSignal1 (i);
						AvatarSignal as2 = ar.getSignal2 (i);

						String chname = generateName (ar, i);
						strArray =
						    strArray + "\"" + chname + "\",";
					    }

				    }

				declaration +=
				    "soclib::caba::VciMwmrStats<vci_param> mwmr_stats"
				    + j + "(\"mwmr_stats" + j +
				    "\",maptab, data_ldr, \"mwmr" + j +
				    ".log\",stringArray(" + strArray + "NULL));" +
				    CR2;
				j++;
			    }
		    }
	    }
	
	return declaration;
    }

}
