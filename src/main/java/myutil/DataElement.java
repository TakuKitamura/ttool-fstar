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


package myutil;

import avatartranslator.*;
import avatartranslator.directsimulation.*;
import common.ConfigurationTTool;
import myutil.*;
import myutilsvg.SVGGeneration;
import ui.*;
import ui.avatarbd.AvatarBDPortConnector;
import ui.interactivesimulation.LatencyTableModel;
import ui.interactivesimulation.SimulationLatency;
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Class DataElement
 * To be used with JFrameStatistics
 * Creation: 11/01/2021
 * version 1.0 11/01/2021
 *
 * @author Ludovic APVRILLE
 */

public class DataElement implements GenericTree {
    public ArrayList<DataElement> childs;

    public String title;
    public double data[];
    public long times[];


    public DataElement(String _title) {
        title = _title;
        childs = new ArrayList<>();
    }

    public void addChild(DataElement de) {
        childs.add(de);
    }

    public int getChildCount() {
        return childs.size();
    }


    public Object getChild(int index) {
        if (index < getChildCount()) {
            return childs.get(index);
        }
        return null;
    }

    public int getIndexOfChild(Object child) {
        if (child == null) {
            return -1;
        }
        return childs.indexOf(child);
    }

    public boolean isLeaf() {
        return (getChildCount() == 0);
    }

    public String toString() {
        return title;
    }





} // Class