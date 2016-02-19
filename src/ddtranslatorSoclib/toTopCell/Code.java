
/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */


package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Code {
    
    static private  String creation;
    static private  String creation2;
   
    private final static String CR = "\n";
	private final static String CR2 = "\n\n";       

    Code(){
    }
    
    public static String getCode(){
		 
      creation =      CR +	
	  "//**********************************************************************" + CR + 
	  "//               Processor entry and connection code"	+ CR + 
	  "//**********************************************************************" + CR2 + 

	  "#define CPU_CONNECT(n) void (n)(CpuEntry *e, sc_core::sc_clock &clk, \\"+ CR +	
	  "sc_core::sc_signal<bool> &rstn, caba::VciSignals<vci_param> &m)" + CR2 +
	  "#define INIT_TOOLS(n) void (n)(const common::Loader &ldr)" + CR2 +
	  "#define NEW_CPU(n) caba::BaseModule * (n)(CpuEntry *e)" + CR2 +
	  "struct CpuEntry { " + CR +
	  "  caba::BaseModule *cpu; " + CR +
	  "  common::Loader *text_ldr;" + CR +
	  "  sc_core::sc_signal<bool> *irq_sig;" + CR +
	  "  size_t irq_sig_count;" + CR +
	  "  std::string type;" + CR +
	  "  std::string name;" + CR +
	  "  int id;" + CR +
	  "  CPU_CONNECT(*connect);" + CR +
	  "  INIT_TOOLS(*init_tools);" + CR +
	  "  NEW_CPU(*new_cpu);" + CR +
	  "};" +CR2 + 
	  " template <class Iss_>" +CR +
	  " CPU_CONNECT(cpu_connect){" + CR +
	  "   typedef ISS_NEST(Iss_) Iss;" +CR +		
	  "   caba::VciXcacheWrapper<vci_param, Iss> *cpu = static_cast<caba::VciXcacheWrapper<vci_param, Iss> *>(e->cpu);" + CR +
	  "   cpu->p_clk(clk);" + CR +
	  "   cpu->p_resetn(rstn);" + CR +
	  "   e->irq_sig_count = Iss::n_irq; " + CR +
	  "   e->irq_sig = new sc_core::sc_signal<bool>[Iss::n_irq];" + CR +

	  "  for ( size_t irq = 0; irq < (size_t)Iss::n_irq; ++irq )" + CR +
	  "     cpu->p_irq[irq](e->irq_sig[irq]); " + CR +
	  "     cpu->p_vci(m);" +CR +
	  "  }" + CR2 +

	  "template <class Iss>" + CR +
	  "INIT_TOOLS(initialize_tools){" + CR +
	  "Iss::setBoostrapCpuId(0);" + CR +
	  "/* Only processor 0 starts execution on reset */" + CR +
	  "#if defined(CONFIG_GDB_SERVER)" + CR +
	  "ISS_NEST(Iss)::set_loader(ldr);" + CR +
	  "#endif" + CR +
	  "#if defined(CONFIG_SOCLIB_MEMCHECK)" +CR +	
	  " common::IssMemchecker<Iss>::init(maptab, ldr, \"vci_multi_tty,vci_xicu,vci_block_device,vci_fd_acccess,vci_ethernet,vci_fdt_rom,vci_rttimer\");" + CR +
	  "#endif" + CR +
	  "}" +CR2 ;

	  // currently, all caches must have the same parameters : take one
      AvatarCPU cpu = TopCellGenerator.avatardd.getAllCPU().getFirst();

      /* System.out.println("*ICACHEWAYS taken into account*"+cpu.getICacheWays());
	  System.out.println("*ICACHESETS taken into account*"+cpu.getICacheSets());
	  System.out.println("*ICACHEWORDS taken into account*"+cpu.getICacheWords());
	  System.out.println("*DCACHEWAYS taken into account*"+cpu.getDCacheWays());
	  System.out.println("*DCACHESETS taken into account*"+cpu.getDCacheSets());
	  System.out.println("*DCACHEWORDS taken into account*"+cpu.getDCacheWords());*/
	  
	  creation=creation +"template <class Iss>" + CR +	  
	  "NEW_CPU(new_cpu){" + CR +
	  "return new caba::VciXcacheWrapper<vci_param, ISS_NEST(Iss)>(e->name.c_str(), e->id, maptab, IntTab(e->id),"+
	  cpu.getICacheWays()+","+cpu.getICacheSets()+","+cpu.getICacheWords()+","+cpu.getDCacheWays()+","+cpu.getDCacheSets()+","+cpu.getDCacheWords()+")"+";"+
	    CR + "}" + CR2;

	  creation = creation +
	  "/***************************************************************************"+ CR +
	  "--------------------Processor creation code-------------------------" + CR+
	  "***************************************************************************/" +CR2 +
	  
	  "template <class Iss> " + CR +
	  "  CpuEntry * newCpuEntry_(CpuEntry *e){" + CR +
	  "  e->new_cpu = new_cpu<Iss>;" +CR +
	  "  e->connect = cpu_connect<Iss>;" + CR +
	  "  e->init_tools = initialize_tools<Iss>;" + CR +
	  "  return e;" + CR +
	  "}" + CR2 +

	  " struct CpuEntry * newCpuEntry(const std::string &type, int id, common::Loader *ldr) {" +CR +	
	  "  CpuEntry *e = new CpuEntry;" + CR +
	  "  std::ostringstream o;" + CR +
	  "  o << type << \"_\" << id; " + CR2 +	  
	  "  e->cpu = 0;" + CR +
	  "  e->text_ldr = ldr;" + CR +
	  "  e->type = type;" +CR +
	  "  e->name = o.str();" + CR +
	  "  e->id = id; " + CR2 +
	  "  switch (type[0]) {" + CR +
	  "    case 'm':" + CR +
	  "      if (type == \"mips32el\")" + CR +
	  "      return newCpuEntry_<common::Mips32ElIss>(e);" + CR +
	  "      else if (type == \"mips32eb\")" + CR +
	  "      return newCpuEntry_<common::Mips32EbIss>(e);" + CR2 +
	  "    case 'a':" + CR +
	  "      if (type == \"arm\")" + CR +
	  "	     return newCpuEntry_<common::ArmIss>(e);" + CR +
	  "   case 'n':" + CR +
	  "     if (type == \"nios2\")" + CR +
	  "	    return newCpuEntry_<common::Nios2fIss>(e);" + CR2 +
	  "   case \'p\':" + CR +
	  "     if (type == \"ppc\")return newCpuEntry_<common::Ppc405Iss>(e);" + CR2+
	  "    case 's':" + CR +
	  "      if (type == \"sparc\")" + CR +
	  "	     return newCpuEntry_<common::Sparcv8Iss<8> >(e);" + CR +
	  "      else if (type == \"sparc_2wins\")" + CR +
	  "	     return newCpuEntry_<common::Sparcv8Iss<2> >(e);" + CR2 +
	  "    case 'l':" + CR +
	  "      if (type == \"lm32\")" + CR +
	  "	     return newCpuEntry_<common::LM32Iss<true> >(e);" + CR +
	  " } " +CR2 +
	  " throw std::runtime_error(type + \": wrong processor type\"); " +CR +
	  "}" +CR2 +
	  "//**********************************************************************" + CR +
	  "//                     Args parsing and netlist" + CR2 +
	  "//**********************************************************************" + CR2	  
	  + "int _main(int argc, char **argv)" + CR + "{" + CR2 +
	  " // Avoid repeating these everywhere" + CR +
	  "  std::vector<CpuEntry*> cpus;" + CR +
	  "  common::Loader data_ldr;" + CR +
	  "  data_ldr.memory_default(0x5a);" + CR;		
      return creation;	        	   	
    }	
}