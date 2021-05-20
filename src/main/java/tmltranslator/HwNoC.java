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

package tmltranslator;

import myutil.TraceManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class HwNoC Creation: 07/01/2019
 * 
 * @version 1.0 07/01/2019
 * @author Ludovic APVRILLE
 */
public class HwNoC extends HwCommunicationNode {

    public static final int DEFAULT_BUFFER_BYTE_DATA_SIZE = 4;

    public int latency = 0;
    public int bufferByteSize = DEFAULT_BUFFER_BYTE_DATA_SIZE; // In bytes. Should more than 0
    public int size = 2; // 2x2 by default
    public HashMap<String, Point> placementMap;

    public HwNoC(String _name) {
        super(_name);
    }

    public String toXML() {
        String s = "<NOC name=\"" + name + "\" clockRatio=\"" + clockRatio + "\"  size=\"" + size
                + "\"  bufferByteSize=\"" + bufferByteSize + "\"";
        s += " infos=\"";
        String infos = "";
        if (placementMap != null) {
            for (String elt : placementMap.keySet()) {
                Point p = placementMap.get(elt);
                infos += elt + "[" + p.getX() + "," + p.getY() + "] ; ";
            }
        }
        s += infos + "\"";
        s += "/>\n";
        return s;
    }

    public boolean makePlacement(String placement, int size) {
        return (placementMap = makePlacementMap(placement, size)) != null;
    }

    // Find a free place for a node
    public boolean map(String name) {
        if (placementMap == null) {
            return false;
        }

        boolean[][] mapB = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mapB[i][j] = false;
            }
        }

        for (Point p : placementMap.values()) {
            mapB[p.x][p.y] = true;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (mapB[i][j] == false) {
                    placementMap.put(name, new Point(i, j));
                    return true;
                }
            }
        }

        return false;

    }

    public String toString() {
        String ret = "";
        if (placementMap == null) {
            return ret;
        }

        String[][] mapS = new String[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mapS[i][j] = "     ";
            }
        }

        for (String s : placementMap.keySet()) {
            Point p = placementMap.get(s);
            mapS[p.x][p.y] = s;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ret += mapS[i][j] + "\t";
            }
            ret += "\n";
        }
        return ret;

    }

    public static HashMap<String, Point> makePlacementMap(String placement, int size) {
        if (placement == null) {
            return null;
        }

        HashMap<String, Point> map = new HashMap<>();
        placement = placement.trim();

        TraceManager.addDev("Placement:" + placement);

        if (placement.length() == 0) {
            return map;
        }

        String[] byElt = placement.split(";");
        for (String tmp : byElt) {
            tmp = tmp.trim();
            String[] elt = tmp.split(" ");
            if (elt.length != 3) {
                return null;
            }
            try {
                int x = Integer.decode(elt[1]);
                int y = Integer.decode(elt[2]);
                if ((x > size) || (y > size)) {
                    return null;
                }
                map.put(elt[0], new Point(x, y));

            } catch (Exception e) {
                TraceManager.addDev("Invalid number in " + tmp);
                return null;
            }
        }

        // Must now verify that two nodes are not mapped at the same coordinate
        boolean[][] mapB = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mapB[i][j] = false;
            }
        }

        for (Point p : map.values()) {
            if (mapB[p.x][p.y] == true) {
                return null;
            }
            mapB[p.x][p.y] = true;
        }

        return map;
    }

    public String getHwExecutionNode(int x, int y) {
        for (String s : placementMap.keySet()) {
            Point p = placementMap.get(s);
            if (p.x == x && p.y == y) {
                // TraceManager.addDev("Found " + s + " for x=" + x + " y=" +y);
                return s;
            }
        }
        // TraceManager.addDev("Returning null for x=" + x + " y=" +y);
        return null;
    }

    public boolean equalSpec(Object o) {
        if (!(o instanceof HwNoC))
            return false;
        if (!super.equalSpec(o))
            return false;
        HwNoC hwNoC = (HwNoC) o;

        if (placementMap != null) {
            if (!placementMap.equals(hwNoC.placementMap))
                return false;
        } else {
            if (hwNoC.placementMap != null)
                return false;
        }
        return bufferByteSize == hwNoC.bufferByteSize && size == hwNoC.size && latency == hwNoC.latency;
    }

}
