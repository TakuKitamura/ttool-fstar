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

package tpndescription;

import myutil.IntMatrix;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Class TPN - Time Petri Net Creation: 04/07/2006
 * 
 * @version 1.1 04/07/2006
 * @author Ludovic APVRILLE
 */
public class TPN {
    public static int INDEX = 0;

    private LinkedList<Place> places;
    private LinkedList<Transition> transitions;
    private LinkedList attributes;

    public TPN() {
        places = new LinkedList<>();
        transitions = new LinkedList<>();
    }

    public LinkedList<Place> getPlaces() {
        return places;
    }

    public void addPlace(Place p) {
        places.add(p);
    }

    public void addTransition(Transition tr) {
        transitions.add(tr);
    }

    public int getNbOfPlaces() {
        return places.size();
    }

    public int getNbOfTransitions() {
        return transitions.size();
    }

    public String toNDRFormat() {
        Place p;
        String tpn = "";
        int cpt = 0;
        int cpty;
        int nbOfPlaces;
        ListIterator iterator;
        String beg;
        int index;

        int stepx = 250;
        int stepy = 125;

        // Compute the x and y position of each element
        // First init all to 0, and then, compute the position
        for (Place p0 : places) {
            p0.x = 0;
            p0.y = 0;
        }
        for (Transition t0 : transitions) {
            t0.x = 0;
            t0.y = 0;
        }

        // Determine an x for each task, with a step of 400 between each, starting at
        // 100
        // Vertical placement for the same task name.
        cpt = 100;
        nbOfPlaces = 0;
        for (Place p1 : places) {
            // Place already met?
            if (p1.x == 0) {
                // Not met!
                index = p1.name.indexOf("_");
                if (index == -1) {
                    beg = p1.name;
                } else {
                    beg = p1.name.substring(0, index);
                }
                cpty = 100;
                p1.x = cpt;
                p1.y = cpty;
                cpty += stepy;
                iterator = places.listIterator();
                while (iterator.hasNext()) {
                    p = (Place) (iterator.next());
                    if (p.x == 0) {
                        if (p.name.startsWith(beg)) {
                            p.x = cpt;
                            p.y = cpty;
                            cpty += stepy;
                        }
                    }
                }
                cpt += stepx + (nbOfPlaces * 100);
                nbOfPlaces++;
            }
        }

        // For transitions, we use the barycenter of all connected places
        for (Transition t : transitions) {
            t.x = t.getXBarycenterOfPlaces() + 100;
            t.y = t.getYBarycenterOfPlaces() - 50;
        }

        // Generate text of places and transitions
        iterator = places.listIterator();
        while (iterator.hasNext()) {
            p = (Place) (iterator.next());
            tpn += p.toNDRFormat();
        }
        iterator = transitions.listIterator();
        while (iterator.hasNext()) {
            tpn += ((Transition) (iterator.next())).toNDRFormat() + "\n";

        }

        return tpn;
    }

    public String toTINAString() {
        Place p;
        String tpn = "net generatedWithTTool\n\n";
        ListIterator iterator = transitions.listIterator();
        while (iterator.hasNext()) {
            tpn += ((Transition) (iterator.next())).toTINAString() + "\n";
        }
        ListIterator iterator0 = places.listIterator();
        while (iterator0.hasNext()) {
            p = (Place) (iterator0.next());
            if (p.nbOfTokens > 0)
                tpn += p.toTINAString() + "\n";
        }
        return tpn;
    }

    public String toString() {
        String tpn = "net generatedWithTTool\n\n";
        ListIterator iterator = transitions.listIterator();
        while (iterator.hasNext()) {
            tpn += "transition " + iterator.next().toString() + "\n";
        }
        tpn += " Nb of places: " + places.size() + "\n";
        for (Place p : places) {

            tpn += "place " + p.toString() + "\n";
        }
        return tpn;
    }

    public String getCString() {
        return "";
    }

    public void saveInFile(String path) {

    }

    public void optimize() {
        // Remove epsilon transitions

        // Rename places and transitions

    }

    public IntMatrix getIncidenceMatrix() {
        IntMatrix im = new IntMatrix(places.size(), transitions.size());

        int i, j;

        // putting names of lines;
        i = 0;
        for (Place p : places) {
            im.setNameOfLine(i, p.toString());
            i++;
        }

        j = 0;
        for (Transition tr : transitions) {
            for (Place pl0 : tr.getDestinationPlaces()) {
                im.setValue(places.indexOf(pl0), j, 1);
            }
            for (Place pl1 : tr.getOriginPlaces()) {
                im.setValue(places.indexOf(pl1), j, -1);
            }
            j++;
        }

        return im;
    }

}