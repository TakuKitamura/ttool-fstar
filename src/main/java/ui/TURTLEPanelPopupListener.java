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
import ui.ucd.UseCaseDiagramPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Class TURTLEPanelPopupListener
 * Management of TURTLE panels
 * Creation: 14/01/2005
 * @version 1.0 14/01/2005
 * @author Ludovic APVRILLE
 * @see MainGUI
 */
public class TURTLEPanelPopupListener extends MouseAdapter /* popup menus onto tabs */ {
    private TURTLEPanel tp;
    private JPopupMenu menu;
    protected MainGUI mgui;

    private JMenuItem rename, remove, moveRight, moveLeft, sort, newucd, newsd, newsdzv, newsdfromucd, newreq,
        newebrdd, newprosmd, newavatarrd, newavatarpd, newavatarcd, newavatarad, newavatarmad;
    private JMenuItem newatd, newftd;

    public TURTLEPanelPopupListener(TURTLEPanel _tp, MainGUI _mgui) {
        tp = _tp;
        mgui = _mgui;
        createMenu();
    }

    public void mousePressed(MouseEvent e) {
        if (mgui.getCurrentTDiagramPanel() != null)
            mgui.getCurrentTDiagramPanel().getMouseManager().setSelection(-1, -1);
        checkForPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        if (mgui.getCurrentTDiagramPanel() != null)
            mgui.getCurrentTDiagramPanel().getMouseManager().setSelection(-1, -1);
        checkForPopup(e);
    }

    public void mouseClicked(MouseEvent e) {
        if (mgui.getCurrentTDiagramPanel() != null)
            mgui.getCurrentTDiagramPanel().getMouseManager().setSelection(-1, -1);
        checkForPopup(e);
    }

    private void checkForPopup(MouseEvent e) {
        if(e.isPopupTrigger()) {
            Component c = e.getComponent();
            updateMenu(tp.tabbedPane.getSelectedIndex());
            menu.show(c, e.getX(), e.getY());
        }
    }

    private void createMenu() {
        rename = createMenuItem("Rename");
        remove = createMenuItem("Remove");
        moveLeft = createMenuItem("Move to the left");
        moveRight = createMenuItem("Move to the right");
        sort = createMenuItem("Sort");
        newucd = createMenuItem("New Use Case Diagram");
        newsd = createMenuItem("New Sequence Diagram (old version)");
        newsdzv = createMenuItem("New Sequence Diagram");
        newsdfromucd = createMenuItem("New Sequence Diagram (from Use Case Diagram)");
        newreq = createMenuItem("New Requirement Diagram");
        newebrdd = createMenuItem("New Event-Based Requirement Description Diagram");
        newprosmd = createMenuItem("New ProActive State Machine Diagram");
        newatd = createMenuItem("New Attack Tree Diagram");
        newftd = createMenuItem("New Fault Tree Diagram");
        newavatarrd = createMenuItem("New AVATAR Requirement Diagram");
        newavatarpd = createMenuItem("New AVATAR Property Diagram");
        newavatarcd = createMenuItem("New Context Diagram");
        newavatarad = createMenuItem("New Activity Diagram");
        newavatarmad = createMenuItem("New AVATAR Modeling Assumptions Diagram");

        menu = new JPopupMenu("TURTLE panel");
        menu.add(moveLeft);
        menu.add(moveRight);
        menu.addSeparator();
        menu.add(rename);
        menu.add(remove);
        menu.addSeparator();
        menu.add(sort);
        menu.addSeparator();
        menu.add(newucd);

        if (mgui.isAvatarOn()) {
            menu.add(newavatarcd);
            menu.add(newavatarad);
        }
        menu.add(newsd);
        menu.add(newsdzv);

        menu.add(newsdfromucd);
        menu.addSeparator();
        menu.add(newreq);
        menu.add(newebrdd);
        menu.add(newatd);
	menu.add(newftd);
        menu.addSeparator();
        menu.add(newprosmd);
        if (mgui.isAvatarOn()) {
            menu.addSeparator();
            menu.add(newavatarmad);
            menu.add(newavatarrd);
            menu.add(newavatarpd);
        }
    }

    private JMenuItem createMenuItem(String s) {
        JMenuItem item = new JMenuItem(s);
        item.setActionCommand(s);
        item.addActionListener(listener);
        return item;
    }

    private void updateMenu(int index) {
        //System.out.println("UpdateMenu index=" + index);
        if (tp.canFirstDiagramBeMoved()) {
            if (index < 1) {
                moveLeft.setEnabled(false);
            } else {
                moveLeft.setEnabled(true);
            }

            if (index + 1 < tp.panels.size()) {
                moveRight.setEnabled(true);
            } else {
                moveRight.setEnabled(false);
            }
        } else {
            if (index < 2) {
                moveLeft.setEnabled(false);
            } else {
                moveLeft.setEnabled(true);
            }

            if ((index + 1 < tp.panels.size()) && (index > 0)) {
                moveRight.setEnabled(true);
            } else {
                moveRight.setEnabled(false);
            }
        }

        // remove!

        remove.setEnabled(tp.removeEnabled(index));
        rename.setEnabled(tp.renameEnabled(index));

        if (tp.tabbedPane.getTabCount() < 3) {
            sort.setEnabled(false);
        } else {
            sort.setEnabled(true);
        }

        newucd.setEnabled(tp.isUCDEnabled());
        newsd.setEnabled(tp.isSDEnabled());
        newsdzv.setEnabled(tp.isSDEnabled());
        newsdfromucd.setEnabled(tp.isSDEnabled() && (mgui.getCurrentTDiagramPanel() instanceof UseCaseDiagramPanel));
        newreq.setEnabled(tp.isReqEnabled());
        newebrdd.setEnabled(tp.isReqEnabled());
        newprosmd.setEnabled(tp.isProSMDEnabled());
        newatd.setEnabled(tp.isATDEnabled());
	newftd.setEnabled(tp.isFTDEnabled());
        newavatarrd.setEnabled(tp.isAvatarRDEnabled());
        newavatarpd.setEnabled(tp.isAvatarPDEnabled());
        newavatarcd.setEnabled(tp.isAvatarCDEnabled());
        newavatarad.setEnabled(tp.isAvatarADEnabled());
        newavatarmad.setEnabled(tp.isAvatarMADEnabled());
    }

    private Action listener = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem)e.getSource();
                String ac = item.getActionCommand();
                if(ac.equals("Rename")) {
                    tp.requestRenameTab(tp.tabbedPane.getSelectedIndex());
                } else if (ac.equals("Remove")) {
                    tp.requestRemoveTab(tp.tabbedPane.getSelectedIndex());
                } else if (ac.equals("Move to the left")) {
                    tp.requestMoveLeftTab(tp.tabbedPane.getSelectedIndex());
                } else if (ac.equals("Move to the right")) {
                    tp.requestMoveRightTab(tp.tabbedPane.getSelectedIndex());
                } else if (ac.equals("Sort")) {
                    GraphicLib.sortJTabbedPane(tp.tabbedPane, tp.panels, 1, tp.tabbedPane.getTabCount());
                    mgui.changeMade(null, -1);
                } else if (ac.equals("New Use Case Diagram")) {
                    mgui.createUniqueUseCaseDiagram(tp, "Use Case Diagram");
                    mgui.changeMade(null, -1);
                } else if (item == newsd) {
                    mgui.createUniqueSequenceDiagram(tp, "MyScenario");
                    mgui.changeMade(null, -1);
                } else if (item == newsdzv) {
                    mgui.createUniqueSequenceDiagramZV(tp, "MyScenario");
                    mgui.changeMade(null, -1);
                } else if (item == newsdfromucd) {
                    mgui.createSequenceDiagramFromUCD(tp, "ScenarioFromUCD", (UseCaseDiagramPanel)(mgui.getCurrentTDiagramPanel()));
                    mgui.changeMade(null, -1);
                } else if (ac.equals("New Requirement Diagram")) {
                    mgui.createRequirementDiagram(tp, "Requirement Diagram");
                    mgui.changeMade(null, -1);
                } else if (ac.equals("New Attack Tree Diagram")) {
                    mgui.createAttackTreeDiagram(tp, "Attack Tree");
                    mgui.changeMade(null, -1);
		} else if (e.getSource() == newftd) {
                    mgui.createFaultTreeDiagram(tp, "Fault Tree");
                    mgui.changeMade(null, -1);
                } else if (ac.equals("New Event-Based Requirement Description Diagram")) {
                    mgui.createEBRDD(tp, "EBRDD");
                    mgui.changeMade(null, -1);
                } else if (ac.equals("New ProActive State Machine Diagram")) {
                    mgui.createProActiveSMD(tp, "ProActive SMD");
                    mgui.changeMade(null, -1);
                } else if (e.getSource() == newavatarrd) {
                    mgui.createAvatarRD(tp, "AVATAR RD");
                    mgui.changeMade(null, -1);
                } else if (e.getSource() == newavatarpd) {
                    mgui.createAvatarPD(tp, "AVATAR PD");
                    mgui.changeMade(null, -1);
                } else if (e.getSource() == newavatarcd) {
                    mgui.createUniqueAvatarCD(tp, "Context Diagram");
                    mgui.changeMade(null, -1);
                } else if (e.getSource() == newavatarad) {
                    mgui.createUniqueAvatarAD(tp, "Activity Diagram");
                    mgui.changeMade(null, -1);
                } else if (e.getSource() == newavatarmad) {
                    mgui.createAvatarMAD(tp, "Modeling Assumptions Diagram");
                    mgui.changeMade(null, -1);
                }
            }
        };
}
