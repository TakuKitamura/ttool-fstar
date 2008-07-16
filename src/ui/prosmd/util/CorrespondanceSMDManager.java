package ui.prosmd.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import translator.ADComponent;
import ui.TGComponent;
import ui.prosmd.ProSMDState;

public class CorrespondanceSMDManager {

	List <CorrespondanceSMDElement> elements;

	public CorrespondanceSMDManager() {
		elements=new LinkedList<CorrespondanceSMDElement>();
	}
	
	
	public void addCorrespondance(ADComponent adComp, TGComponent tgComp, String panelName, String machineCode)
	{
		CorrespondanceSMDElement ce=new CorrespondanceSMDElement(adComp,tgComp,panelName,machineCode);
		elements.add(ce);
	}
	

	public ADComponent getADComp(TGComponent tgComp, String panelName, String machineCode)
		{
			Iterator<CorrespondanceSMDElement> i=elements.iterator();
			ADComponent adComp=null;
			
		while (i.hasNext())
		{
			CorrespondanceSMDElement ce=i.next();
			if ( (ce.getTgComp().equals(tgComp))&& (ce.getPanelName().equals(panelName))&&(ce.getMachineCode().equals(machineCode)))
				adComp=ce.getAdComp();					
		}					
		return adComp;
		}				
	
	/*
	 * we just look for name
	 * because we only have one junction for all the states with the same value
	 */
	public ADComponent getADComp(ProSMDState stateComp, String panelName, String machineCode)
	{
		Iterator<CorrespondanceSMDElement> i=elements.iterator();
		ADComponent adComp=null;
		String stateName=stateComp.getValue();
		
	while (i.hasNext())
	{
		CorrespondanceSMDElement ce=i.next();
		String cState=ce.getTgComp().getValue();
		String cMachineCode=ce.getMachineCode();
		if ( (cState!=null) && (cMachineCode!=null) )
		if ( (cState.equals(stateName))&& (ce.getPanelName().equals(panelName))&&(cMachineCode.equals(machineCode)))
			adComp=ce.getAdComp();					
	}					
	return adComp;
	}				

	
	
}
