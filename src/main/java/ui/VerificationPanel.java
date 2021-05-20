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

package ui;

import myutil.GraphicLib;
import ui.avatarmad.AvatarMADPanel;
import ui.avatarmad.AvatarMADToolBar;
import ui.util.IconManager;
import ui.verificationpd.VerificationPropertyDiagramPanel;
import ui.verificationpd.VerificationPropertyDiagramToolbar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class VerificationPanel Management of verifications with a Panel Creation:
 * 10/04/2019
 * 
 * @version 1.0 10/04/2019
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class VerificationPanel extends TURTLEPanel {
  public VerificationPropertyDiagramPanel vpdp;

  public VerificationPanel(MainGUI _mgui) {
    super(_mgui);

    tabbedPane = GraphicLib.createTabbedPane();// new JTabbedPane();
    UIManager.put("TabbedPane.tabAreaBackground", MainGUI.BACK_COLOR);
    UIManager.put("TabbedPane.selected", MainGUI.BACK_COLOR);
    SwingUtilities.updateComponentTreeUI(tabbedPane);
    // tabbedPane.setOpaque(true);

    cl = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        mgui.paneDiplodocusMethodologyAction(e);
      }
    };

    tabbedPane.addChangeListener(cl);
    tabbedPane.addMouseListener(new TURTLEPanelPopupListener(this, mgui));

  }

  public void initElements() {
  }

  public void init() {
    init("Verification tracking");
  }

  public void init(String name) {
    addVerificationPanel(name);

  }

  public boolean addVerificationPanel(String s) {
    VerificationPropertyDiagramToolbar vpdt = new VerificationPropertyDiagramToolbar(mgui);
    toolbars.add(vpdt);

    toolBarPanel = new JPanel();
    // toolBarPanel.setBackground(Color.red);
    toolBarPanel.setLayout(new BorderLayout());
    // toolBarPanel.setBackground(ColorManager.MainTabbedPaneSelect);

    vpdp = new VerificationPropertyDiagramPanel(mgui, vpdt);
    vpdp.setName(s);
    vpdp.tp = this;
    tdp = vpdp;
    panels.add(vpdp);
    JScrollDiagramPanel jsp = new JScrollDiagramPanel(vpdp);
    vpdp.jsp = jsp;
    jsp.setWheelScrollingEnabled(true);
    jsp.getVerticalScrollBar().setUnitIncrement(MainGUI.INCREMENT);
    toolBarPanel.add(vpdt, BorderLayout.NORTH);
    toolBarPanel.add(jsp, BorderLayout.CENTER);
    DateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
    Date date = new Date();
    String dateAndTime = dateFormat.format(date);
    tabbedPane.addTab("VerificationTracking" + dateAndTime, IconManager.imgic99, toolBarPanel,
        "VerificationTracking" + dateAndTime);
    tabbedPane.setSelectedIndex(0);
    JPanel toolBarPanel = new JPanel();
    toolBarPanel.setLayout(new BorderLayout());

    mgui.changeMade(vpdp, TDiagramPanel.NEW_COMPONENT);

    return true;
  }

  public String saveHeaderInXml(String extensionToName) {
    if (extensionToName == null) {
      return "<Modeling type=\"VerificationPropertyPanel\" nameTab=\"" + mgui.getTabName(this) + "\" >\n";
    }
    return "<Modeling type=\"VerificationPropertyPanel\" nameTab=\"" + mgui.getTabName(this) + extensionToName
        + "\" >\n";
  }

  public String saveTailInXml() {
    return "</Modeling>\n\n\n";
  }

  public String toString() {
    return mgui.getTitleAt(this) + " (VerificationPropertyPanel)";
  }

  public boolean removeEnabled(int index) {
    return panels.size() > 1;
  }

  public boolean renameEnabled(int index) {
    return panels.size() != 0;
  }

}
