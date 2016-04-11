/* This class generates the lines of the topcell where the signals are declared*/

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import java.util.*;

public class Signal {

    private final static String CR = "\n";
    private final static String CR2 = "\n\n";
    private final static String NAME_CLK = "signal_clk";
    private static final String NAME_RST = "signal_resetn";

	public static String getSignal() {
	    int nb_clusters=TopCellGenerator.avatardd.getAllCrossbar().size();	

		String signal = CR2 + "//-------------------------------signaux------------------------------------" + CR2;
		
		signal = signal + "caba::VciSignals<vci_param> signal_vci_m[cpus.size() + 1];"+ CR;
		signal = signal + "caba::VciSignals<vci_param> signal_vci_xicu(\"signal_vci_xicu\");"+ CR;

		signal = signal + "caba::VciSignals<vci_param> signal_vci_vcifdtrom(\"signal_vci_vcifdtrom\");"+ CR;
		signal = signal +" caba::VciSignals<vci_param> signal_vci_vcihetrom(\"signal_vci_vcihetrom\");"+ CR;
		signal = signal +" caba::VciSignals<vci_param> signal_vci_vcirom(\"signal_vci_vcirom\");"+ CR;
		signal = signal +" caba::VciSignals<vci_param> signal_vci_vcisimhelper(\"signal_vci_vcisimhelper\");"+ CR;
		signal = signal +"caba::VciSignals<vci_param> signal_vci_vcirttimer(\"signal_vci_vcirttimer\");"+ CR;
signal = signal +"caba::VciSignals<vci_param> signal_vci_vcilocks(\"signal_vci_vcilocks\");"+ CR;
signal = signal +"caba::VciSignals<vci_param> signal_vci_mwmr_ram(\"signal_vci_mwmr_ram\");"+ CR;
signal = signal +"caba::VciSignals<vci_param> signal_vci_mwmrd_ram(\"signal_vci_mwmrd_ram\");"+ CR;
		signal = signal +"caba::VciSignals<vci_param> signal_vci_vcifdaccessi;"+ CR;
		signal = signal +"caba::VciSignals<vci_param> signal_vci_vcifdaccesst;"+ CR;
		signal = signal +"caba::VciSignals<vci_param> signal_vci_bdi;"+ CR;
		signal = signal +"caba::VciSignals<vci_param> signal_vci_bdt;"+ CR;	
		signal = signal +"caba::VciSignals<vci_param> signal_vci_etherneti;"+ CR;
		signal = signal +"caba::VciSignals<vci_param> signal_vci_ethernett;"+ CR;
		signal = signal +""+ CR;
		signal = signal + "sc_clock signal_clk(\"signal_clk\");" + CR;
		signal = signal + "sc_signal<bool>  signal_resetn(\"" + NAME_RST + "\");" + CR2;		

if(TopCellGenerator.avatardd.getAllCrossbar().size()==0){
		for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM())
					signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_vciram" + ram.getNo_ram()
							+ "(\"signal_vci_vciram" + ram.getNo_ram() + "\");" + CR2;															
		for (AvatarTTY  tty :  TopCellGenerator.avatardd.getAllTTY())
		    signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_tty"+tty.getNo_tty()+"(\"signal_vci_tty"+tty.getNo_tty()+"\");" + CR2;			
			
		signal = signal + " sc_core::sc_signal<bool> signal_xicu_irq[xicu_n_irq];" + CR2;
		System.out.print("number of processors : " + TopCellGenerator.avatardd.getNbCPU()+"\n");
}

else{
    for (AvatarRAM ram : TopCellGenerator.avatardd.getAllRAM())
	signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_vciram" + ram.getNo_ram()
	    + "(\"signal_vci_vciram" + ram.getNo_ram() + "\");" + CR2;															
		for (AvatarTTY  tty :  TopCellGenerator.avatardd.getAllTTY())
		    signal = signal + "soclib::caba::VciSignals<vci_param> signal_vci_tty"+tty.getNo_tty()+"(\"signal_vci_tty"+tty.getNo_tty()+"\");" + CR2;						
		signal = signal + " sc_core::sc_signal<bool> signal_xicu_irq[xicu_n_irq];" + CR2;
		//System.out.print("number of processors : " + TopCellGenerator.avatardd.getNbCPU()+"\n");
		System.out.print("number of clusters : " + TopCellGenerator.avatardd.getNbClusters()+"\n");

}
		return signal;
	}
}
