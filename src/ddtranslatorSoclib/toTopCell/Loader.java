
/* authors: v1.0 Daniela GENIUS, Julien HENON 2015 */


package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Loader {

	static private String loader;
	private final static String NAME_CLK = "signal_clk";

    private final static String CR = "\n";
	private final static String CR2 = "\n\n";

    public Loader(){
    }

	public static String  getLoader() {

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
		for (AvatarChannel channel : TopCellGenerator.avatardd.getAllMappedChannels()) {    			  
			loader = loader + ".channel" + j + ";";
			j++;
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
		loader = loader + "  const size_t xicu_n_irq = "+(1+nb_tty+3)+";" + CR2 ;
        return loader;
	}

    String getNAME_CLK(){
      return NAME_CLK;
    }
}
