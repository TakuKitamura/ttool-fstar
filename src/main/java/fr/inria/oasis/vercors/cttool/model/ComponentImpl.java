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

/*
 * @author Emil Salageanu
 */

public class ComponentImpl implements Component {

    private Collection<Component> subComponents = new LinkedList<Component>();
    private Collection<Port> ports = new LinkedList<Port>();
    private Collection<Attribute> attributes = new LinkedList<Attribute>();
    private Component father;
    private boolean isPrimitive;
    private String name;
    private Behaviour behaviour;

    public ComponentImpl(String name) {
        this.name = name;
    }

    public ComponentImpl(String name, boolean isPrimitive) {
        this(name);

        this.isPrimitive = isPrimitive;
    }

    public ComponentImpl(String name, Component father, boolean isPrimitive) {
        this(name);
        this.father = father;
        this.isPrimitive = isPrimitive;
    }

    public ComponentImpl(String name, Component father, Collection<Component> subComponents, Collection<Port> ports,
            Collection<Attribute> attributes, boolean isPrimitive) {
        this(name, father, isPrimitive);
        this.subComponents = new LinkedList<Component>(subComponents);
        this.ports = new LinkedList<Port>(ports);
        this.attributes = new LinkedList<Attribute>(attributes);
    }

    public Collection<Component> getSubComponents() {
        return subComponents;
    }

    public boolean addSubComponent(Component c) {

        if (this.getSubComponentByName(c.getName()) != null)
            return false;

        subComponents.add(c);

        return true;
    }

    public boolean removeSubComponent(Component c) {
        return subComponents.remove(c);

    }

    public boolean removeSubCompoenentByName(String compName) {

        Component c = getSubComponentByName(compName);
        if (c == null)
            return false;
        return subComponents.remove(c);
    }

    public Component getSubComponentByName(String compName) {

        Iterator<Component> it = subComponents.iterator();
        while (it.hasNext()) {
            Component c = it.next();
            if (c.getName().equals(compName))
                return c;
        }
        return null;
    }

    public Collection<Port> getPorts() {
        return ports;
    }

    public boolean addPort(Port p) {
        return ports.add(p);
    }

    public boolean removePort(Port p) {
        return ports.remove(p);
    }

    public boolean removePortByName(String portName) {
        Port p = getPortByName(portName);
        return ports.remove(p);
    }

    public Port getPortByName(String portName) {
        Iterator<Port> it = ports.iterator();
        while (it.hasNext()) {
            Port p = it.next();
            if (p.getName().equals(portName))
                return p;
        }
        return null;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public boolean addAttribute(Attribute a) {
        return attributes.add(a);
    }

    public boolean removeAttribute(Attribute a) {
        return attributes.remove(a);
    }

    public boolean removeAttributeByName(String attributeName) {
        Attribute a = getAttributeByName(attributeName);
        return attributes.remove(a);
    }

    public Attribute getAttributeByName(String attributeName) {
        Iterator<Attribute> it = attributes.iterator();
        while (it.hasNext()) {
            Attribute a = it.next();
            if (a.getName().equals(attributeName))
                return a;
        }
        return null;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    public Component getFather() {
        return this.father;
    }

    public void setFather(Component father) {
        this.father = father;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBehaviour(Behaviour b) {
        this.behaviour = b;

    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public String prettyPrint() {
        String out = "";
        out += "-----> Component " + this.getPath() + "---->\n";

        if (attributes.size() > 0) {
            out += "Attributes:\n";
            Iterator<Attribute> it = attributes.iterator();
            while (it.hasNext()) {
                Attribute a = it.next();
                out += a.toString() + " ; ";
            }
        }

        if (ports.size() > 0) {
            out += "\n            --->" + this.name + "'s Ports:\n";
            Iterator<Port> itp = ports.iterator();
            while (itp.hasNext()) {
                Port p = itp.next();
                out += p.toString() + " \n ";
            }
            out += "\n            <---" + this.name + "' Ports:\n";
        }

        if (subComponents.size() > 0) {
            out += "\n   -->" + this.name + "'s Subcomponents: \n";
            Iterator<Component> itc = subComponents.iterator();
            while (itc.hasNext()) {
                Component c = itc.next();
                out += c.prettyPrint();
            }
            out += "\n   <--" + this.name + "'s Subcomponents: \n";
        }

        out += "\n <------ Component " + this.name + " <-----\n";

        return out;
    }

    public String toString() {
        String out = "";
        out += this.name + " [";
        Iterator<Component> itc = subComponents.iterator();
        while (itc.hasNext()) {
            Component c = itc.next();
            out += c.toString();
        }
        out += "]; ";
        return out;
    }

    public String getPath() {
        String out = this.getName();
        Component father = this.getFather();
        while (father != null) {
            out = father.getName() + "." + out;
            father = father.getFather();
        }

        return out;
    }

    public Collection<Component> getAllPrimitives() {

        LinkedList<Component> primitives = new LinkedList<Component>();
        Iterator<Component> it = this.subComponents.iterator();
        while (it.hasNext()) {
            Component c = it.next();
            if (c.isPrimitive()) {
                primitives.add(c);
            } else
                primitives.addAll(c.getAllPrimitives());
        }
        return primitives;
    }

    public boolean isDescendant(Component comp) {
        Component father = comp;
        while (father.getFather() != null) {
            if (father.getSubComponents().contains(this))
                return true;
            father = father.getFather();
        }
        return false;

    }

}
