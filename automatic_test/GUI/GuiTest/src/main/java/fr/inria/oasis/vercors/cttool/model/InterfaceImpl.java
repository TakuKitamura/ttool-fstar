/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
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



package fr.inria.oasis.vercors.cttool.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

	

public class InterfaceImpl implements Interface{
	
	private String name;
	private Collection<Message> messages=new LinkedList<Message>();
	
	private boolean isMandatory;
	
	
	public InterfaceImpl(String name, boolean isMandatory)
	{
		this.name=name;
		this.isMandatory=isMandatory;
	}
	
	public InterfaceImpl(String name,Collection<Message> messages, boolean isMandatory )
	{
		this.name=name;
		this.messages=messages;
		this.isMandatory=isMandatory;
	}
	
	
	
	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
	public Collection<Message> getMessages() {
		return messages;
	}

	public void addMessage(Message m) {
		this.messages.add(m);
		
	}

	public void removeMessage(Message m) {
		this.messages.remove(m);
	}

	public void removeMessage(String messageName) {
	
		Message m=getMessageByName(messageName);
		messages.remove(m);
	}

	public Message getMessageByName(String messageName) {
		Iterator<Message> it = this.messages.iterator();
		while(it.hasNext())
		{
			Message m=it.next();
			if (m.getName().equals(messageName))
				return m;
		}
	 return null;
	}

	/*
	 * First Implemenation of is compatible
	 * Two interfaces are compatible if the have exactly the same messages
	 * in the same order
	 */
	/*public boolean isCompatibleWith(Interface otherInterface)
	{
		Iterator <Message> it=messages.iterator();
		while (it.hasNext())
		{
			
		}
		
		
		
		return true;		
	}
	*/
	
	
}
