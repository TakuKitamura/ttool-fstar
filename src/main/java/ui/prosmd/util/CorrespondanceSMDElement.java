package ui.prosmd.util;

import translator.ADComponent;
import ui.TGComponent;

public class CorrespondanceSMDElement {

	private ADComponent adComp;
	private TGComponent tgComp;
	private String panelName;
	private String machineCode;

	public CorrespondanceSMDElement(ADComponent adComp, TGComponent tgComp, String panelName, String machineCode) {
		super();
		// TODO Auto-generated constructor stub
		this.adComp = adComp;
		this.tgComp = tgComp;
		this.panelName = panelName;
		this.machineCode = machineCode;
	}

	public ADComponent getAdComp() {
		return adComp;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public String getPanelName() {
		return panelName;
	}

	public TGComponent getTgComp() {
		return tgComp;
	}
	
	
	
	
	public String toString()
	{
		String out="";
		out+="{"+adComp.toString()+" ; "+tgComp.toString()+" ; "+panelName+" ; "+machineCode+"}";
		return out;
	}
	

}
