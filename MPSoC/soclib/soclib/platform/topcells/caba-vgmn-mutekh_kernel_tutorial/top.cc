//-------------------------------Header------------------------------------

#include <iostream>
#include <cstdlib>
#include <vector>
#include <string>
#include <stdexcept>
#include <cstdarg>

#define CONFIG_GDB_SERVER
#define CONFIG_SOCLIB_MEMCHECK

#include "iss_memchecker.h"
#include "gdbserver.h"

#include "ppc405.h"
#include "niosII.h"
#include "mips32.h"
#include "arm.h"
#include "sparcv8.h"
#include "lm32.h"

#include "mapping_table.h"
#include "vci_fdt_rom.h"
#include "vci_xcache_wrapper.h"
#include "vci_ram.h"
#include "vci_heterogeneous_rom.h"
#include "vci_multi_tty.h"
#include "vci_locks.h"
#include "vci_xicu.h"
#include "vci_mwmr_stats.h"
#include "vci_vgsb.h"
#include "mwmr_controller.h"
#include "vci_mwmr_controller.h"
#include "fifo_virtual_copro_wrapper.h"
#include "vci_block_device.h"
#include "vci_simhelper.h"
#include "vci_fd_access.h"
#include "vci_ethernet.h"
#include "vci_rttimer.h"
#include "vci_logger.h"
#include "vci_local_crossbar.h"

namespace {
std::vector<std::string> stringArray(
	const char *first, ... )
{
	std::vector<std::string> ret;
	va_list arg;
	va_start(arg, first);
	const char *s = first;
	while(s) {
		ret.push_back(std::string(s));
		s = va_arg(arg, const char *);
	};
	va_end(arg);
	return ret;
}

std::vector<int> intArray(
	const int length, ... )
{
	int i;
	std::vector<int> ret;
	va_list arg;
	va_start(arg, length);

	for (i=0; i<length; ++i) {
		ret.push_back(va_arg(arg, int));
	};
	va_end(arg);
	return ret;
}
}

using namespace soclib;
using common::IntTab;
using common::Segment;

static common::MappingTable maptab(32, IntTab(8), IntTab(8), 0xfff00000);

typedef caba::VciParams<4,9,32,1,1,1,8,1,1,1> vci_param;// Define our VCI parameters

struct CpuEntry;



#if defined(CONFIG_GDB_SERVER)
#  if defined(CONFIG_SOCLIB_MEMCHECK)
#    warning Using GDB and memchecker
#    define ISS_NEST(T) common::GdbServer<common::IssMemchecker<T> >
#  else
#    warning Using GDB
#    define ISS_NEST(T) common::GdbServer<T>
#  endif
#elif defined(CONFIG_SOCLIB_MEMCHECK)
#  warning Using Memchecker
#  define ISS_NEST(T) common::GdbServer<common::IssMemchecker<T> 
#else
#  warning Using raw processor
#  define ISS_NEST(T) T
#endif

//**********************************************************************
//               Processor entry and connection code
//**********************************************************************

#define CPU_CONNECT(n) void (n)(CpuEntry *e, sc_core::sc_clock &clk, \
sc_core::sc_signal<bool> &rstn, caba::VciSignals<vci_param> &m)

#define INIT_TOOLS(n) void (n)(const common::Loader &ldr)

#define NEW_CPU(n) caba::BaseModule * (n)(CpuEntry *e)

struct CpuEntry { 
  caba::BaseModule *cpu; 
  common::Loader *text_ldr;
  sc_core::sc_signal<bool> *irq_sig;
  size_t irq_sig_count;
  std::string type;
  std::string name;
  int id;
  CPU_CONNECT(*connect);
  INIT_TOOLS(*init_tools);
  NEW_CPU(*new_cpu);
};

 template <class Iss_>
 CPU_CONNECT(cpu_connect){
   typedef ISS_NEST(Iss_) Iss;
   caba::VciXcacheWrapper<vci_param, Iss> *cpu = static_cast<caba::VciXcacheWrapper<vci_param, Iss> *>(e->cpu);
   cpu->p_clk(clk);
   cpu->p_resetn(rstn);
   e->irq_sig_count = Iss::n_irq; 
   e->irq_sig = new sc_core::sc_signal<bool>[Iss::n_irq];
  for ( size_t irq = 0; irq < (size_t)Iss::n_irq; ++irq )
     cpu->p_irq[irq](e->irq_sig[irq]); 
     cpu->p_vci(m);
  }

template <class Iss>
INIT_TOOLS(initialize_tools){
#if defined(CONFIG_GDB_SERVER)
ISS_NEST(Iss)::set_loader(ldr);
#endif
#if defined(CONFIG_SOCLIB_MEMCHECK)
 common::IssMemchecker<Iss>::init(maptab, ldr, "vci_multi_tty0,vci_xicu,vci_block_device,vci_fd_acccess,vci_ethernet,vci_fdt_rom,vci_rttimer");
#endif
}

template <class Iss>
NEW_CPU(new_cpu){
return new caba::VciXcacheWrapper<vci_param, ISS_NEST(Iss)>(e->name.c_str(), e->id, maptab, IntTab(e->id),4,4,4,4,4,4);
}

/***************************************************************************
--------------------Processor creation code-------------------------
***************************************************************************/

template <class Iss> 
  CpuEntry * newCpuEntry_(CpuEntry *e){
  e->new_cpu = new_cpu<Iss>;
  e->connect = cpu_connect<Iss>;
  e->init_tools = initialize_tools<Iss>;
  return e;
}

 struct CpuEntry * newCpuEntry(const std::string &type, int id, common::Loader *ldr) {
  CpuEntry *e = new CpuEntry;
  std::ostringstream o;
  o << type << "_" << id; 

  e->cpu = 0;
  e->text_ldr = ldr;
  e->type = type;
  e->name = o.str();
  e->id = id; 

  switch (type[0]) {
    case 'm':
      if (type == "mips32el")
      return newCpuEntry_<common::Mips32ElIss>(e);
      else if (type == "mips32eb")
      return newCpuEntry_<common::Mips32EbIss>(e);

    case 'a':
      if (type == "arm")
	     return newCpuEntry_<common::ArmIss>(e);
   case 'n':
     if (type == "nios2")
	    return newCpuEntry_<common::Nios2fIss>(e);

   case 'p':
     if (type == "ppc")return newCpuEntry_<common::Ppc405Iss>(e);

    case 's':
      if (type == "sparc")
	     return newCpuEntry_<common::Sparcv8Iss<8> >(e);
      else if (type == "sparc_2wins")
	     return newCpuEntry_<common::Sparcv8Iss<2> >(e);

    case 'l':
      if (type == "lm32")
	     return newCpuEntry_<common::LM32Iss<true> >(e);
 } 

 throw std::runtime_error(type + ": wrong processor type"); 
}

//**********************************************************************
//                     Args parsing and netlist

//**********************************************************************

int _main(int argc, char **argv)
{

 // Avoid repeating these everywhere
  std::vector<CpuEntry*> cpus;
  common::Loader data_ldr;
  data_ldr.memory_default(0x5a);


//-----------------------mapping table------------------------

// ppc segments

maptab.add(Segment("resetppc",  0xffffff80, 0x0080, IntTab(1), true));
maptab.add(Segment("resetnios", 0x00802000, 0x1000, IntTab(1), true));
maptab.add(Segment("resetzero", 0x00000000, 0x1000, IntTab(1), true));
maptab.add(Segment("resetmips", 0xbfc00000, 0x1000, IntTab(1), true));


// RAM segments

maptab.add(Segment("text", 0x60000000, 0x00100000, IntTab(0), true));
maptab.add(Segment("rodata", 0x80000000, 0x01000000, IntTab(1), true));
maptab.add(Segment("data", 0x7f000000, 0x01000000, IntTab(2), false)); 

maptab.add(Segment("simhelper", 0xd3200000, 0x00000100, IntTab(3), false));
 maptab.add(Segment("vci_xicu", 0xd2200000, 0x00001000, IntTab(4), false));
maptab.add(Segment("vci_rttimer", 0xd6000000, 0x00000100, IntTab(5), false));

maptab.add(Segment("vci_fdt_rom", 0xe0000000, 0x00001000, IntTab(6), false));

maptab.add(Segment("cram0", 0x10000000, 0x80000, IntTab(2), true));
maptab.add(Segment("uram0", 0x10280000, 0x80000, IntTab(2), false));
maptab.add(Segment("vci_multi_tty0" , 0xd0200000, 0x00000010, IntTab(7), false));
maptab.add(Segment("vci_fd_access", 0xd4200000, 0x00000100, IntTab(8), false));
maptab.add(Segment("vci_ethernet",  0xd5000000, 0x00000020, IntTab(9), false));
maptab.add(Segment("vci_block_device", 0xd1200000, 0x00000020, IntTab(10), false));

maptab.add(Segment("vci_locks", 0xC0200000, 0x00000100, IntTab(11), false));



//-------------------------Call Loader---------------------------------

std::cerr << "caba-vgmn-mutekh_kernel_tutorial SoCLib simulator for MutekH" << std::endl;

if ( (argc < 2) || ((argc % 2) == 0) ) {
exit(0);   }
  argc--;
  argv++;

bool heterogeneous = (argc > 2);

  for (int i = 0; i < (argc - 1); i += 2){
    char *cpu_p = argv[i];
    const char *kernel_p = argv[i+1];
    const char *arch_str = strsep(&cpu_p, ":");
    int count = cpu_p ? atoi(cpu_p) : 1;
    common::Loader *text_ldr; 
    if (heterogeneous) {
	 text_ldr = new common::Loader(std::string(kernel_p) + ";.text");
	 text_ldr->memory_default(0x5a);;
	 data_ldr.load_file(std::string(kernel_p) + ";.rodata;.boot;.excep");
	 if (i == 0)
	    data_ldr.load_file(std::string(kernel_p) + ";.data;.channel0;.channel1;.channel2;.channel3;.channel4;.channel5;.channel6;.channel7;.channel8;.channel9;.channel10;.channel11;.channel12;.channel13;.channel14;.channel15;.channel16;.channel17;.channel18;.channel19;.channel20;.channel21;.channel22;.channel23;.channel24;.channel25;.channel26;.channel27;.channel28;.cpudata;.contextdata");
      } else {
	  text_ldr = new common::Loader(std::string(kernel_p));
	  text_ldr->memory_default(0x5a);
	  data_ldr.load_file(std::string(kernel_p));
      }

      common::Loader tools_ldr(kernel_p);
     tools_ldr.memory_default(0x5a);

      for (int j = 0; j < count; j++) {
	int id = cpus.size();
	std::cerr << "***" << cpus.size() << std::endl;
	CpuEntry *e = newCpuEntry(arch_str, id, text_ldr);
	if (j == 0)
	  e->init_tools(tools_ldr);
	e->cpu = e->new_cpu(e);
	cpus.push_back(e);
      }
    }

  const size_t xicu_n_irq = 5;

//----------------------------Instantiation-------------------------------


caba::VciHeterogeneousRom<vci_param> vcihetrom("vcihetrom",  IntTab(0), maptab);
caba::VciRam<vci_param> vcirom("vcirom", IntTab(1), maptab, data_ldr);
 caba::VciSimhelper<vci_param> vcisimhelper    ("vcisimhelper", IntTab(3), maptab);
caba::VciXicu<vci_param> vcixicu("vci_xicu", maptab, IntTab(4), 1, xicu_n_irq, cpus.size(), cpus.size());
caba::VciRtTimer<vci_param> vcirttimer    ("vcirttimer", IntTab(5), maptab, 1, true);

caba::VciFdtRom<vci_param> vcifdtrom("vci_fdt_rom", IntTab(6), maptab);
caba::VciMultiTty<vci_param> TTY0("TTY0", IntTab(7), maptab, "vci_multi_tty0", NULL);
caba::VciLocks<vci_param> vcilocks("vcilocks", IntTab(11), maptab);
soclib::caba::VciRam<vci_param>Memory0("Memory0", IntTab(2), maptab);
caba::VciFdAccess<vci_param> vcifd("vcifd", maptab, IntTab(cpus.size()+1), IntTab(8));
caba::VciEthernet<vci_param> vcieth("vcieth", maptab, IntTab(cpus.size()+2), IntTab(9), "soclib0");
caba::VciBlockDevice<vci_param> vcibd("vcibd", maptab, IntTab(cpus.size()), IntTab(10),"block0.iso", 2048);
soclib::caba::VciVgsb<vci_param> vgsb("Bus0" , maptab,5,12);

soclib::caba::VciMwmrStats<vci_param> mwmr_stats0("mwmr_stats0",maptab, data_ldr, "mwmr0.log",stringArray("TCPIP_receiveTCP__Application_receiveTCP","TCPPacketManager_storePacket__TCPPacketManager_retrieve","TCPIP_timeoutPacket__TCPPacketManager_timeoutPacket","TCPIP_emptyListOfPackets__TCPPacketManager_empty","TCPIP_addPacket__TCPPacketManager_addPacket","TCPIP_ackPacket__TCPPacketManager_ackPacket","Application_open__TCPIP_open","Application_close__TCPIP_close","Application_abort__TCPIP_abort","Application_sendTCP__TCPIP_send_TCP","SmartCardController_fromTtoP__TCPIP_fromTtoP","SmartCardController_fromPtoT__TCPIP_fromPtoT","SmartCardController_start_TCPIP__TCPIP_start","SmartCardController_reset__InterfaceDevice_reset","SmartCardController_pTS__InterfaceDevice_pTS","SmartCardController_dataReady__InterfaceDevice_data_Ready","SmartCardController_activation__InterfaceDevice_activation","SmartCardController_fromDtoSC__InterfaceDevice_fromDtoSC","SmartCardController_answerToReset__InterfaceDevice_answerToReset","SmartCardController_pTSCConfirm__InterfaceDevice_pTSConfirm","SmartCardController_fromSCtoD__InterfaceDevice_fromSCtoD","SmartCardController_data_Ready_SC__InterfaceDevice_dataReady","SmartCardController_start_Application__Application_startApplication","TCPIP_set__mainTimer__Timer__mainTimer__TCPIP_set","TCPIP_reset__mainTimer__Timer__mainTimer__TCPIP_reset","TCPIP_expire__mainTimer__Timer__mainTimer__TCPIP_expire","TCPPacketManager_set__timerP__Timer__timerP__TCPPacketManager_set","TCPPacketManager_reset__timerP__Timer__timerP__TCPPacketManager_reset","TCPPacketManager_expire__timerP__Timer__timerP__TCPPacketManager_expire",NULL));



//-------------------------------signaux------------------------------------

caba::VciSignals<vci_param> signal_vci_m[cpus.size() + 1];
caba::VciSignals<vci_param> signal_vci_xicu("signal_vci_xicu");
caba::VciSignals<vci_param> signal_vci_vcifdtrom("signal_vci_vcifdtrom");
 caba::VciSignals<vci_param> signal_vci_vcihetrom("signal_vci_vcihetrom");
 caba::VciSignals<vci_param> signal_vci_vcirom("signal_vci_vcirom");
 caba::VciSignals<vci_param> signal_vci_vcisimhelper("signal_vci_vcisimhelper");
caba::VciSignals<vci_param> signal_vci_vcirttimer("signal_vci_vcirttimer");
caba::VciSignals<vci_param> signal_vci_vcilocks("signal_vci_vcilocks");
caba::VciSignals<vci_param> signal_vci_vcifdaccessi;
caba::VciSignals<vci_param> signal_vci_vcifdaccesst;
caba::VciSignals<vci_param> signal_vci_bdi;
caba::VciSignals<vci_param> signal_vci_bdt;
caba::VciSignals<vci_param> signal_vci_etherneti;
caba::VciSignals<vci_param> signal_vci_ethernett;

sc_clock signal_clk("signal_clk");
sc_signal<bool>  signal_resetn("signal_resetn");

 sc_core::sc_signal<bool> signal_xicu_irq[xicu_n_irq];

soclib::caba::VciSignals<vci_param> signal_vci_vciram0("signal_vci_vciram0");

soclib::caba::VciSignals<vci_param> signal_vci_tty0("signal_vci_tty0");



//------------------------------Netlist---------------------------------

// icu

  vcifdtrom.add_property("interrupt-parent", vcifdtrom.get_device_phandle("vci_xicu"));

  vcixicu.p_clk(signal_clk);
  vcixicu.p_resetn(signal_resetn);

  vcixicu.p_vci(signal_vci_xicu);

  vcifdtrom.begin_device_node("vci_rttimer", "soclib:vci_rttimer");
  vcifdtrom.add_property("interrupts", 4);
  vcifdtrom.add_property("frequency", 1000000);
  vcifdtrom.end_node();

  vcifdtrom.begin_device_node("vci_xicu", "soclib:vci_xicu");

  int irq_map[cpus.size() * 3];
  for ( size_t i = 0; i < cpus.size(); ++i )
    {
      irq_map[i*3 + 0] = i;
      irq_map[i*3 + 1] = vcifdtrom.get_cpu_phandle(i);
      irq_map[i*3 + 2] = 0;
    }

  vcifdtrom.add_property("interrupt-map", irq_map, cpus.size() * 3);
  vcifdtrom.add_property("frequency", 1000000);

  vcifdtrom.add_property("param-int-pti-count", 1);
  vcifdtrom.add_property("param-int-hwi-count", xicu_n_irq);
  vcifdtrom.add_property("param-int-wti-count", cpus.size());
  vcifdtrom.add_property("param-int-irq-count", cpus.size());
  vcifdtrom.end_node();

  for ( size_t i = 0; i < xicu_n_irq; ++i )
    vcixicu.p_hwi[i](signal_xicu_irq[i]);

///////////////// cpus

vcifdtrom.begin_cpus();

for ( size_t i = 0; i < cpus.size(); ++i ){
   // configure het_rom
  vcihetrom.add_srcid(*cpus[i]->text_ldr, IntTab(i));
  // add cpu node to device tree
  vcifdtrom.begin_cpu_node(std::string("cpu:") + cpus[i]->type, i);
  vcifdtrom.add_property("freq", 1000000);
  vcifdtrom.end_node();

// connect cpu
  cpus[i]->connect(cpus[i], signal_clk, signal_resetn, signal_vci_m[i]);
vgsb.p_to_initiator[i](signal_vci_m[i]);
vcixicu.p_irq[i](cpus[i]->irq_sig[0]);
 }
 vcifdtrom.end_node();

  vcihetrom.p_clk(signal_clk);
  vcifdtrom.p_clk(signal_clk);
  vcirom.p_clk(signal_clk);
  vcisimhelper.p_clk(signal_clk);
  vcirttimer.p_clk(signal_clk);
  vcihetrom.p_resetn(signal_resetn);
  vcifdtrom.p_resetn(signal_resetn);
  vcirom.p_resetn(signal_resetn);
  vcisimhelper.p_resetn(signal_resetn);
  vcirttimer.p_resetn(signal_resetn);
  vcihetrom.p_vci(signal_vci_vcihetrom);
  vcifdtrom.p_vci(signal_vci_vcifdtrom);
  vcirom.p_vci(signal_vci_vcirom);
  vcisimhelper.p_vci(signal_vci_vcisimhelper);
  vcirttimer.p_vci(signal_vci_vcirttimer);
  vcirttimer.p_irq[0](signal_xicu_irq[4]);

 vgsb.p_clk(signal_clk);
  vgsb.p_resetn(signal_resetn);
  vgsb.p_to_target[0](signal_vci_vcihetrom);
  vgsb.p_to_target[1](signal_vci_vcirom);
  vgsb.p_to_target[3](signal_vci_vcisimhelper);

  vgsb.p_to_target[4](signal_vci_xicu);
  vgsb.p_to_target[5](signal_vci_vcirttimer);

  vgsb.p_to_target[6](signal_vci_vcifdtrom);

  vgsb.p_to_initiator[cpus.size()](signal_vci_bdi);
  vgsb.p_to_initiator[cpus.size()+1](signal_vci_vcifdaccessi);
  vgsb.p_to_initiator[cpus.size()+2](signal_vci_etherneti);

// RAM netlist

Memory0.p_clk(signal_clk);
Memory0.p_resetn(signal_resetn);
Memory0.p_vci(signal_vci_vciram0);

vgsb.p_to_target[2](signal_vci_vciram0);

vgsb.p_to_target[8](signal_vci_vcifdaccesst);
vgsb.p_to_target[9](signal_vci_ethernett);
vgsb.p_to_target[10](signal_vci_bdt);
vcifdtrom.add_property("interrupts", 0);

vcifdtrom.end_node();;

// TTY netlist

TTY0.p_clk(signal_clk);
TTY0.p_resetn(signal_resetn);
TTY0.p_vci(signal_vci_tty0);

vcifdtrom.begin_device_node("vci_multi_tty0","soclib:vci_multi_tty0");

vgsb.p_to_target[7](signal_vci_tty0);

TTY0.p_irq[0](signal_xicu_irq[0]);

{

  vcifdtrom.begin_node("aliases");
  vcifdtrom.add_property("timer", vcifdtrom.get_device_name("vci_rttimer") + "[0]");
  vcifdtrom.add_property("console", vcifdtrom.get_device_name("vci_multi_tty0") + "[0]");
  vcifdtrom.end_node();
}

vcieth.p_clk(signal_clk);
vcieth.p_resetn(signal_resetn);
vcieth.p_irq(signal_xicu_irq[3]);
vcieth.p_vci_target(signal_vci_ethernett);
vcieth.p_vci_initiator(signal_vci_etherneti);
vcifdtrom.begin_device_node("vci_ethernet", "soclib:vci_ethernet");
vcifdtrom.add_property("interrupts", 3);
vcifdtrom.end_node();
vcibd.p_clk(signal_clk);
vcibd.p_resetn(signal_resetn);
vcibd.p_irq(signal_xicu_irq[1]);
vcibd.p_vci_target(signal_vci_bdt);
vcibd.p_vci_initiator(signal_vci_bdi);
vcifdtrom.begin_device_node("vci_block_device", "soclib:vci_block_device");
vcifdtrom.add_property("interrupts", 1);
vcifdtrom.end_node();
vcihetrom.add_srcid(*cpus[0]->text_ldr, IntTab(cpus.size()+1));
vcifd.p_clk(signal_clk);
vcifd.p_resetn(signal_resetn);
vcifd.p_irq(signal_xicu_irq[2]);
vcifd.p_vci_target(signal_vci_vcifdaccesst);
vcifd.p_vci_initiator(signal_vci_vcifdaccessi);
vcifdtrom.begin_device_node("vci_fd_access", "soclib:vci_fd_access");
vcifdtrom.add_property("interrupts", 2);
vcifdtrom.end_node();

mwmr_stats0.p_clk(signal_clk);
mwmr_stats0.p_resetn(signal_resetn);
mwmr_stats0.p_vci(signal_vci_vciram0);

  sc_core::sc_start(sc_core::sc_time(0, sc_core::SC_NS));
  signal_resetn = false;
  sc_core::sc_start(sc_core::sc_time(1, sc_core::SC_NS));
  signal_resetn = true;
  sc_core::sc_start();

  return EXIT_SUCCESS;
}




/***************************************************************************
----------------------------simulation-------------------------
***************************************************************************/

int sc_main (int argc, char *argv[])
{
       try {
         return _main(argc, argv);
    }

       catch (std::exception &e) {
            std::cout << e.what() << std::endl;
            throw;
    } catch (...) {
std::cout << "Unknown exception occured" << std::endl;
throw;
}

       return 1;
}