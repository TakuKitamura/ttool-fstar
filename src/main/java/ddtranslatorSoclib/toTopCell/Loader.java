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



/* authors: v1.0 Daniela GENIUS, Julien HENON 2015 */


package ddtranslatorSoclib.toTopCell;
import avatartranslator.AvatarRelation;//DG 23.06.
import avatartranslator.AvatarSpecification;//DG 23.06.
import ddtranslatorSoclib.AvatarChannel;

public class Loader {
public static AvatarSpecification avspec;
	static private String loader;
	private final static String NAME_CLK = "signal_clk";

    private final static String CR = "\n";
	private final static String CR2 = "\n\n";

    public Loader(AvatarSpecification _avspec){
	
		avspec =_avspec;
    }

	public static String  getLoader(AvatarSpecification _avspec) {//DG 23.06.
	    avspec =_avspec;//DG 23.06.
	    int nb_clusters=TopCellGenerator.avatardd.getAllCrossbar().size();		
	    //nb_clusters=2;

		loader = CR2 + "//-------------------------Call Loader---------------------------------" + CR2 ;
		loader = loader + "std::cerr << \"caba-vgmn-mutekh_kernel_tutorial SoCLib simulator for MutekH\" << std::endl;"
				+ CR2 ;

		loader = loader + "if ( (argc < 2) || ((argc % 2) == 0) ) {" + CR ;

		loader = loader + "exit(0);   }" + CR ;

		loader = loader + "  argc--;" + CR ;
		loader = loader + "  argv++;" + CR2 ;
		loader = loader + "bool heterogeneous = (argc > 2);" + CR2 ;

		loader = loader + "  for (int i = 0; i < (argc - 1); i += 2){" + CR ;	
		loader = loader + "    char *cpu_p = argv[i];" + CR ;
		loader = loader + "    const char *kernel_p = argv[i+1];" + CR ;
		loader = loader + "    const char *arch_str = strsep(&cpu_p, \":\");" + CR ;
		loader = loader + "    int count = cpu_p ? atoi(cpu_p) : 1;" + CR ;

		loader = loader + "    common::Loader *text_ldr; " + CR ;

		loader = loader + "    if (heterogeneous) {" + CR ;
		loader = loader + "	 text_ldr = new common::Loader(std::string(kernel_p) + \";.text\");" + CR ;
		loader = loader + "	 text_ldr->memory_default(0x5a);;" + CR ;
		loader = loader + "	 data_ldr.load_file(std::string(kernel_p) + \";.rodata;.boot;.excep\");" + CR ;
		loader = loader + "	 if (i == 0)" + CR ;
		loader = loader + "	    data_ldr.load_file(std::string(kernel_p) + \";.data;";	
      // We generated so far until arriving at first channel segment, if any
		//current hypothesis : one segment per channel
		int j=0;
		//for (AvatarChannel channel : TopCellGenerator.avatardd.getAllMappedChannels()) {    	
		//DG 23.06. per signal!!hack pour l'instant
		int i=0;
		//for (i=0;i<30;i++){ 

		for(AvatarRelation ar: avspec.getRelations()) {

       		for(i=0; i<ar.nbOfSignals() ; i++) {

			loader = loader + ".channel" + j + ";";
			j++;
		}
}
		// We resume the generation of the fixed code
		loader = loader + ".cpudata;.contextdata\");" + CR ;
		loader = loader + "      } else {" + CR ;
		loader = loader + "	  text_ldr = new common::Loader(std::string(kernel_p));" + CR ;
		loader = loader + "	  text_ldr->memory_default(0x5a);" + CR ;
		loader = loader + "	  data_ldr.load_file(std::string(kernel_p));" + CR ;
		loader = loader + "      }" + CR2 ;

		loader = loader + "      common::Loader tools_ldr(kernel_p);" + CR ;
		loader = loader + "     tools_ldr.memory_default(0x5a);" + CR2 ;

		loader = loader + "      for (int j = 0; j < count; j++) {" + CR ;
		loader = loader + "	int id = cpus.size();" + CR ;
		loader = loader + "	std::cerr << \"***\" << cpus.size() << std::endl;" + CR ;

		loader = loader + "	CpuEntry *e = newCpuEntry(arch_str, id, text_ldr);" + CR ;

		loader = loader + "	if (j == 0)" + CR ;
		loader = loader + "	  e->init_tools(tools_ldr);" + CR ;

		loader = loader + "	e->cpu = e->new_cpu(e);" + CR ;
		loader = loader + "	cpus.push_back(e);" + CR ;
		loader = loader + "      }" + CR ;
		loader = loader + "    }" + CR2 ;
		int nb_tty =1; //DG currently only one (multi) tty

if(nb_clusters==0){
    loader = loader + "  const size_t xicu_n_irq = "+(1+nb_tty+3)+";" + CR2 ;
}else{
    loader = loader + "  const size_t xicu_n_irq = "+(5*nb_clusters)+";" + CR2 ;
}
        return loader;
	}

    String getNAME_CLK(){
      return NAME_CLK;
    }
}
