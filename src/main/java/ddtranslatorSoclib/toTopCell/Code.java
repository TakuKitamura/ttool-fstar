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

package ddtranslatorSoclib.toTopCell;

import ddtranslatorSoclib.AvatarCPU;
import common.ConfigurationTTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import ui.TGComponentManager;
//import ui.TDiagramPanel;
//import ui.TURTLEPanel;

public class Code {

    static private String creation;
    static private String creation2;

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";
    private final static String GENERATED_PATH = "generated_topcell" + File.separator;

    public static String getCode() {

        creation = CR + "//**********************************************************************" + CR
                + "//               Processor entry and connection code" + CR
                + "//**********************************************************************" + CR2 +

                "#define CPU_CONNECT(n) void (n)(CpuEntry *e, sc_core::sc_clock &clk, \\" + CR
                + "sc_core::sc_signal<bool> &rstn, caba::VciSignals<vci_param> &m)" + CR2
                + "#define INIT_TOOLS(n) void (n)(const common::Loader &ldr)" + CR2
                + "#define NEW_CPU(n) caba::BaseModule * (n)(CpuEntry *e)" + CR2 + "struct CpuEntry { " + CR
                + "  caba::BaseModule *cpu; " + CR + "  common::Loader *text_ldr;" + CR
                + "  sc_core::sc_signal<bool> *irq_sig;" + CR + "  size_t irq_sig_count;" + CR + "  std::string type;"
                + CR + "  std::string name;" + CR + "  int id;" + CR + "  CPU_CONNECT(*connect);" + CR
                + "  INIT_TOOLS(*init_tools);" + CR + "  NEW_CPU(*new_cpu);" + CR + "};" + CR2
                + " template <class Iss_>" + CR + " CPU_CONNECT(cpu_connect){" + CR + "   typedef ISS_NEST(Iss_) Iss;"
                + CR
                + "   caba::VciXcacheWrapper<vci_param, Iss> *cpu = static_cast<caba::VciXcacheWrapper<vci_param, Iss> *>(e->cpu);"
                + CR + "   cpu->p_clk(clk);" + CR + "   cpu->p_resetn(rstn);" + CR
                + "   e->irq_sig_count = Iss::n_irq; " + CR
                + "   e->irq_sig = new sc_core::sc_signal<bool>[Iss::n_irq];" + CR +

                "  for ( size_t irq = 0; irq < (size_t)Iss::n_irq; ++irq )" + CR
                + "     cpu->p_irq[irq](e->irq_sig[irq]); " + CR + "     cpu->p_vci(m);" + CR + "  }" + CR2;

        creation = creation + "template <class Iss>" + CR + "INIT_TOOLS(initialize_tools){" + CR;

        // Mips based platform requires a special bootstrap where only cpu 0 starts
        // first

        int isMipsArchitecture = 0;

        try {
            String path = ConfigurationTTool.AVATARMPSoCCodeDirectory;
            BufferedReader in = new BufferedReader(new FileReader(path + "/Makefile.forsoclib"));
            String line = null;
            while ((line = in.readLine()) != null) {

                if (line.equals("SOCLIB_CPU=mips32el")) {
                    isMipsArchitecture = 1;
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isMipsArchitecture == 1) {
            creation = creation + "Iss::setBoostrapCpuId(0);" + CR + "/* Only processor 0 starts execution on reset */"
                    + CR;
        }
        creation = creation + "#if defined(CONFIG_GDB_SERVER)" + CR + "ISS_NEST(Iss)::set_loader(ldr);" + CR + "#endif"
                + CR + "#if defined(CONFIG_SOCLIB_MEMCHECK)" + CR
                + " common::IssMemchecker<Iss>::init(maptab, ldr, \"vci_multi_tty0,vci_xicu,vci_block_device,vci_fd_acccess,vci_ethernet,vci_fdt_rom,vci_rttimer\");"
                + CR + "#endif" + CR + "}" + CR2;

        // currently, all caches must have the same parameters : take one
        AvatarCPU cpu = TopCellGenerator.avatardd.getAllCPU().getFirst();

        int nb_clusters = TopCellGenerator.avatardd.getAllCrossbar().size();

        if (nb_clusters == 0) {
            creation = creation + "template <class Iss>" + CR + "NEW_CPU(new_cpu){" + CR
                    + "return new caba::VciXcacheWrapper<vci_param, ISS_NEST(Iss)>(e->name.c_str(), e->id, maptab, IntTab(e->id),"
                    + cpu.getICacheWays() + "," + cpu.getICacheSets() + "," + cpu.getICacheWords() + ","
                    + cpu.getDCacheWays() + "," + cpu.getDCacheSets() + "," + cpu.getDCacheWords() + ")" + ";" + CR
                    + "}" + CR2;
        } else {
            creation = creation + "template <class Iss>" + CR + "NEW_CPU(new_cpu){" + CR
                    + "return new caba::VciXcacheWrapper<vci_param, ISS_NEST(Iss)>(e->name.c_str(), e->id, maptab, IntTab(e->id,e->id),"
                    + cpu.getICacheWays() + "," + cpu.getICacheSets() + "," + cpu.getICacheWords() + ","
                    + cpu.getDCacheWays() + "," + cpu.getDCacheSets() + "," + cpu.getDCacheWords() + ")" + ";" + CR
                    + "}" + CR2;
        }

        creation = creation + "/***************************************************************************" + CR
                + "--------------------Processor creation code-------------------------" + CR
                + "***************************************************************************/" + CR2 +

                "template <class Iss> " + CR + "  CpuEntry * newCpuEntry_(CpuEntry *e){" + CR
                + "  e->new_cpu = new_cpu<Iss>;" + CR + "  e->connect = cpu_connect<Iss>;" + CR
                + "  e->init_tools = initialize_tools<Iss>;" + CR + "  return e;" + CR + "}" + CR2 +

                " struct CpuEntry * newCpuEntry(const std::string &type, int id, common::Loader *ldr) {" + CR
                + "  CpuEntry *e = new CpuEntry;" + CR + "  std::ostringstream o;" + CR + "  o << type << \"_\" << id; "
                + CR2 + "  e->cpu = 0;" + CR + "  e->text_ldr = ldr;" + CR + "  e->type = type;" + CR
                + "  e->name = o.str();" + CR + "  e->id = id; " + CR2 + "  switch (type[0]) {" + CR + "    case 'm':"
                + CR + "      if (type == \"mips32el\")" + CR + "      return newCpuEntry_<common::Mips32ElIss>(e);"
                + CR + "      else if (type == \"mips32eb\")" + CR
                + "      return newCpuEntry_<common::Mips32EbIss>(e);" + CR2 + "    case 'a':" + CR
                + "      if (type == \"arm\")" + CR + "	     return newCpuEntry_<common::ArmIss>(e);" + CR
                + "   case 'n':" + CR + "     if (type == \"nios2\")" + CR
                + "	    return newCpuEntry_<common::Nios2fIss>(e);" + CR2 + "   case \'p\':" + CR
                + "     if (type == \"ppc\")return newCpuEntry_<common::Ppc405Iss>(e);" + CR2 + "    case 's':" + CR
                + "      if (type == \"sparc\")" + CR + "	     return newCpuEntry_<common::Sparcv8Iss<8> >(e);" + CR
                + "      else if (type == \"sparc_2wins\")" + CR
                + "	     return newCpuEntry_<common::Sparcv8Iss<2> >(e);" + CR2 + "    case 'l':" + CR
                + "      if (type == \"lm32\")" + CR + "	     return newCpuEntry_<common::LM32Iss<true> >(e);" + CR
                + " } " + CR2 + " throw std::runtime_error(type + \": wrong processor type\"); " + CR + "}" + CR2
                + "//**********************************************************************" + CR
                + "//                     Args parsing and netlist" + CR2
                + "//**********************************************************************" + CR2
                + "int _main(int argc, char **argv)" + CR + "{" + CR2 + " // Avoid repeating these everywhere" + CR
                + "  std::vector<CpuEntry*> cpus;" + CR + "  common::Loader data_ldr;" + CR
                + "  data_ldr.memory_default(0x5a);" + CR;
        return creation;
    }
}
