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

import common.ConfigurationTTool;
import common.SpecConfigTTool;
import myutil.TraceManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CancellationException;

/**
 * Class JToolBarMainTurtle Main toolbar of the ttool main window Creation:
 * 09/12/2003
 * 
 * @author Ludovic APVRILLE
 */
public class JToolBarMainTurtle extends JToolBar implements ActionListener {
  private static int COMMON_ACTIONS[] = { TGUIAction.ACT_NEW, TGUIAction.ACT_NEW_PROJECT, TGUIAction.ACT_OPEN,
      TGUIAction.ACT_OPEN_PROJECT, TGUIAction.ACT_SAVE, -1, TGUIAction.ACT_CUT, TGUIAction.ACT_COPY,
      TGUIAction.ACT_PASTE, TGUIAction.ACT_DELETE, TGUIAction.MOVE_ENABLED, TGUIAction.FIRST_DIAGRAM, -1,
      TGUIAction.ACT_BACKWARD, TGUIAction.ACT_FORWARD, -1, TGUIAction.ACT_ZOOM_LESS, TGUIAction.ACT_SHOW_ZOOM,
      TGUIAction.ACT_ZOOM_MORE, -1, -1, TGUIAction.ACT_MODEL_CHECKING };

  // Avatar
  private JButton avatarSimu, avatarFVUPPAAL, avatarFVProVerif, avatarFVStaticAnalysis, avatarCodeGeneration, avatarMC;
  private JMenuItem avatarSimuMI, avatarFVUPPAALMI, avatarFVProVerifMI, avatarFVStaticAnalysisMI,
      avatarCodeGenerationMI, avatarMCMI;

  // Other
  private JButton genrtlotos, genlotos, genuppaal, gendesign, genMapping, dse, dseZ3, noc;
  private JMenuItem genrtlotosMI, genlotosMI, genuppaalMI, gendesignMI, genMappingMI, dseMI, dseZ3MI, nocMI;
  private JButton checkcode, simulation, validation;
  private JMenuItem checkcodeMI, simulationMI, validationMI;

  private JButton oneClickrtlotos, onclicklotos, gensystemc, simusystemc, gentml, genC, genjava, nc, externalSearch,
      internalSearch;
  private JMenuItem oneClickrtlotosMI, onclicklotosMI, gensystemcMI, simusystemcMI, gentmlMI, genCMI, genjavaMI, ncMI;

  // Main button
  private JButton menuButton;
  private JPopupMenu menu;

  private boolean popupShown;

  private JTextField search;

  private MainGUI mgui;

  public JToolBarMainTurtle(MainGUI _mgui) {
    super();
    mgui = _mgui;
    buildToolBar(mgui);
  }

  // Menus
  private void buildToolBar(MainGUI mgui) {
    JButton button;
    MenuItem m;

    menu = new JPopupMenu();

    menu.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        popupShown = true;
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            popupShown = false;
          }
        });
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });

    menuButton = add(mgui.actions[TGUIAction.ACT_ACTION_MENU]);
    menuButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        final boolean shown = popupShown;
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            popupShown = shown;
          }
        });
      }
    });

    addSeparator();

    int j;
    for (j = 0; j < COMMON_ACTIONS.length; j++) {
      if (COMMON_ACTIONS[j] == -1) {
        addSeparator();
        menu.addSeparator();
      } else {
        button = add(mgui.actions[COMMON_ACTIONS[j]]);
        button.addMouseListener(mgui.mouseHandler);
        addMenu(mgui.actions[COMMON_ACTIONS[j]]);
      }
    }

    addSeparator();
    menu.addSeparator();

    if (MainGUI.experimentalOn) {
      gendesign = add(mgui.actions[TGUIAction.ACT_GEN_DESIGN]);
      gendesign.addMouseListener(mgui.mouseHandler);
      gendesignMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_DESIGN]);
    }

    addSeparator();
    menu.addSeparator();

    genMapping = add(mgui.actions[TGUIAction.ACT_GEN_MAPPING]);
    genMapping.addMouseListener(mgui.mouseHandler);
    genMappingMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_MAPPING]);

    dse = add(mgui.actions[TGUIAction.ACT_DSE]);
    dse.addMouseListener(mgui.mouseHandler);
    dseMI = addMenu(mgui.actions[TGUIAction.ACT_DSE]);

    if (MainGUI.experimentalOn) {
      dseZ3 = add(mgui.actions[TGUIAction.ACT_DSE_Z3]);
      dseZ3.addMouseListener(mgui.mouseHandler);
      dseZ3MI = addMenu(mgui.actions[TGUIAction.ACT_DSE_Z3]);
    }

    addSeparator();
    menu.addSeparator();

    avatarSimu = add(mgui.actions[TGUIAction.ACT_AVATAR_SIM]);
    avatarSimu.addMouseListener(mgui.mouseHandler);
    avatarSimuMI = addMenu(mgui.actions[TGUIAction.ACT_AVATAR_SIM]);

    addSeparator();
    menu.addSeparator();

    avatarMC = add(mgui.actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER]);
    avatarMC.addMouseListener(mgui.mouseHandler);
    avatarMCMI = addMenu(mgui.actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER]);

    avatarFVUPPAAL = add(mgui.actions[TGUIAction.ACT_AVATAR_FV_UPPAAL]);
    avatarFVUPPAAL.addMouseListener(mgui.mouseHandler);
    avatarFVUPPAALMI = addMenu(mgui.actions[TGUIAction.ACT_AVATAR_FV_UPPAAL]);

    if (MainGUI.uppaalOn) {
      genuppaal = add(mgui.actions[TGUIAction.ACT_GEN_UPPAAL]);
      genuppaal.addMouseListener(mgui.mouseHandler);
      genuppaalMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_UPPAAL]);
    }

    if (MainGUI.proverifOn) {
      avatarFVProVerif = add(mgui.actions[TGUIAction.ACT_AVATAR_FV_PROVERIF]);
      avatarFVProVerif.addMouseListener(mgui.mouseHandler);
      avatarFVProVerifMI = addMenu(mgui.actions[TGUIAction.ACT_AVATAR_FV_PROVERIF]);
    }

    avatarFVStaticAnalysis = add(mgui.actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS]);
    avatarFVStaticAnalysis.addMouseListener(mgui.mouseHandler);
    avatarFVStaticAnalysisMI = addMenu(mgui.actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS]);

    addSeparator();
    menu.addSeparator();

    avatarCodeGeneration = add(mgui.actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION]);
    avatarCodeGeneration.addMouseListener(mgui.mouseHandler);
    avatarCodeGenerationMI = addMenu(mgui.actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION]);

    addSeparator();
    menu.addSeparator();

    genrtlotos = add(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS]);
    genrtlotos.addMouseListener(mgui.mouseHandler);
    genrtlotosMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS]);

    if (MainGUI.systemcOn) {
      genlotos = add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
      genlotos.addMouseListener(mgui.mouseHandler);
      genlotosMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
      /*
       * addSeparator(); button = add(mgui.actions[TGUIAction.ACT_GEN_AUTS]);
       * button.addMouseListener(mgui.mouseHandler); button =
       * add(mgui.actions[TGUIAction.ACT_GEN_AUT]);
       * button.addMouseListener(mgui.mouseHandler);
       */
    } else if (MainGUI.lotosOn) {
      genlotos = add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
      genlotos.addMouseListener(mgui.mouseHandler);
      genlotosMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
    }

    addSeparator();
    menu.addSeparator();

    checkcode = add(mgui.actions[TGUIAction.ACT_CHECKCODE]);
    checkcode.addMouseListener(mgui.mouseHandler);
    checkcodeMI = addMenu(mgui.actions[TGUIAction.ACT_CHECKCODE]);

    simulation = add(mgui.actions[TGUIAction.ACT_SIMULATION]);
    simulation.addMouseListener(mgui.mouseHandler);
    simulationMI = addMenu(mgui.actions[TGUIAction.ACT_SIMULATION]);

    validation = add(mgui.actions[TGUIAction.ACT_VALIDATION]);
    validation.addMouseListener(mgui.mouseHandler);
    validationMI = addMenu(mgui.actions[TGUIAction.ACT_VALIDATION]);

    addSeparator();
    menu.addSeparator();

    oneClickrtlotos = add(mgui.actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG]);
    oneClickrtlotos.addMouseListener(mgui.mouseHandler);
    oneClickrtlotosMI = addMenu(mgui.actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG]);

    if (MainGUI.lotosOn) {
      onclicklotos = add(mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG]);
      onclicklotos.addMouseListener(mgui.mouseHandler);
      onclicklotosMI = addMenu(mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG]);
    }

    addSeparator();
    menu.addSeparator();

    if (MainGUI.systemcOn) {
      gensystemc = add(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC]);
      gensystemc.addMouseListener(mgui.mouseHandler);
      gensystemcMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC]);

      simusystemc = add(mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC]);
      simusystemc.addMouseListener(mgui.mouseHandler);
      simusystemcMI = addMenu(mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC]);
      addSeparator();
      menu.addSeparator();

      gentml = add(mgui.actions[TGUIAction.ACT_GEN_TMLTXT]);
      gentml.addMouseListener(mgui.mouseHandler);
      gentmlMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_TMLTXT]);
      addSeparator();
      menu.addSeparator();

      genC = add(mgui.actions[TGUIAction.ACT_GEN_CCODE]);
      genC.addMouseListener(mgui.mouseHandler);
      genCMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_CCODE]);
      addSeparator();
      menu.addSeparator();
    }

    addSeparator();
    menu.addSeparator();

    genjava = add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
    genjava.addMouseListener(mgui.mouseHandler);
    genjavaMI = addMenu(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
    // button = add(mgui.actions[TGUIAction.ACT_SIMU_JAVA]);
    // button.addMouseListener(mgui.mouseHandler);

    addSeparator();

    if (MainGUI.experimentalOn) {
      noc = add(mgui.actions[TGUIAction.ACT_REMOVENOC]);
      noc.addMouseListener(mgui.mouseHandler);
      nocMI = addMenu(mgui.actions[TGUIAction.ACT_REMOVENOC]);
    }

    if (MainGUI.ncOn) {
      nc = add(mgui.actions[TGUIAction.ACT_NC]);
      nc.addMouseListener(mgui.mouseHandler);
      ncMI = addMenu(mgui.actions[TGUIAction.ACT_NC]);
    }

    addSeparator();

    if ((SpecConfigTTool.ExternalCommand1.length() > 0) && (ConfigurationTTool.ExternalCommand1Host.length() > 0)) {
      button = add(mgui.actions[TGUIAction.EXTERNAL_ACTION_1]);
      button.addMouseListener(mgui.mouseHandler);
      button.setToolTipText(ConfigurationTTool.ExternalCommand1);
      JMenuItem mi = addMenu(mgui.actions[TGUIAction.EXTERNAL_ACTION_1]);
      mi.setToolTipText(ConfigurationTTool.ExternalCommand1);
      addSeparator();
    }

    if ((ConfigurationTTool.ExternalCommand2.length() > 0) && (ConfigurationTTool.ExternalCommand2Host.length() > 0)) {
      button = add(mgui.actions[TGUIAction.EXTERNAL_ACTION_2]);
      button.addMouseListener(mgui.mouseHandler);
      button.setToolTipText(ConfigurationTTool.ExternalCommand2);
      JMenuItem mi = addMenu(mgui.actions[TGUIAction.EXTERNAL_ACTION_2]);
      mi.setToolTipText(ConfigurationTTool.ExternalCommand2);
      addSeparator();
      menu.addSeparator();
    }

    addSeparator();

    showAvatarActions(false);
    search = new JTextField("", 10);
    search.setEnabled(false);
    add(search);
    search.addActionListener(this);

    search.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent documentEvent) {
        if (search.getText().length() > 0) {
          internalSearch.setEnabled(true);
        } else
          internalSearch.setEnabled(false);
      }

      @Override
      public void removeUpdate(DocumentEvent documentEvent) {
        if (search.getText().length() > 0) {
          internalSearch.setEnabled(true);
        } else
          internalSearch.setEnabled(false);
      }

      @Override
      public void changedUpdate(DocumentEvent documentEvent) {
      }
    });

    // @author : Huy TRUONG
    // add external search button into menu bar
    internalSearch = add(mgui.actions[TGUIAction.ACT_INTERNAL_SEARCH]);
    internalSearch.addMouseListener(mgui.mouseHandler);
    externalSearch = add(mgui.actions[TGUIAction.ACT_EXTERNAL_SEARCH]);
    externalSearch.addMouseListener(mgui.mouseHandler);
  }

  public void showAvatarActions(boolean b) {

    // TraceManager.addDev("Show avatar options with b = " + b);
    genMapping.setVisible(!b);
    genMappingMI.setVisible(!b);
    dse.setVisible(!b);
    dseMI.setVisible(!b);

    if (dseZ3 != null) {
      dseZ3.setVisible(!b);
    }
    if (dseZ3MI != null) {
      dseZ3MI.setVisible(!b);
    }

    if (noc != null) {
      noc.setVisible(!b);
      nocMI.setVisible(!b);
    }

    avatarSimu.setVisible(b);
    avatarFVUPPAAL.setVisible(b);
    avatarSimuMI.setVisible(b);
    avatarFVUPPAALMI.setVisible(b);

    if (avatarFVProVerif != null) {
      avatarFVProVerif.setVisible(b);
    }
    if (avatarFVProVerifMI != null) {
      avatarFVProVerifMI.setVisible(b);
    }

    avatarFVStaticAnalysis.setVisible(b);
    avatarCodeGeneration.setVisible(b);
    avatarFVStaticAnalysisMI.setVisible(b);
    avatarCodeGenerationMI.setVisible(b);

    if (genrtlotosMI != null) {
      genrtlotosMI.setVisible(!b);
    }

    if (genlotos != null) {
      genlotos.setVisible(!b);
    }
    if (genlotosMI != null) {
      genlotosMI.setVisible(!b);
    }

    if (genuppaal != null) {
      genuppaal.setVisible(!b);
    }

    if (genuppaalMI != null) {
      genuppaalMI.setVisible(!b);
    }

    if (checkcode != null) {
      checkcode.setVisible(!b);
    }

    if (checkcodeMI != null) {
      checkcodeMI.setVisible(!b);
    }

    if (simulation != null) {
      simulation.setVisible(!b);
    }

    if (simulationMI != null) {
      simulationMI.setVisible(!b);
    }

    if (validation != null) {
      validation.setVisible(!b);
    }

    if (validationMI != null) {
      validationMI.setVisible(!b);
    }

    if (oneClickrtlotos != null) {
      oneClickrtlotos.setVisible(!b);
    }

    if (oneClickrtlotosMI != null) {
      oneClickrtlotosMI.setVisible(!b);
    }

    if (onclicklotos != null) {
      onclicklotos.setVisible(!b);
    }

    if (onclicklotosMI != null) {
      onclicklotosMI.setVisible(!b);
    }

    if (gensystemc != null) {
      gensystemc.setVisible(!b);
    }

    if (gensystemcMI != null) {
      gensystemcMI.setVisible(!b);
    }

    if (simusystemc != null) {
      simusystemc.setVisible(!b);
    }

    if (simusystemcMI != null) {
      simusystemcMI.setVisible(!b);
    }

    if (gentml != null) {
      gentml.setVisible(!b);
    }

    if (gentmlMI != null) {
      gentmlMI.setVisible(!b);
    }

    if (genC != null) {
      genC.setVisible(!b);
    }

    if (genCMI != null) {
      genCMI.setVisible(!b);
    }

    if (genjavaMI != null) {
      genjavaMI.setVisible(!b);
    }

    if (genjava != null) {
      genjava.setVisible(!b);
    }

    if (nc != null) {
      nc.setVisible(!b);
    }

    if (ncMI != null) {
      ncMI.setVisible(!b);
    }

    if (gendesign != null) {
      gendesign.setVisible(!b);
    }

    if (gendesignMI != null) {
      gendesignMI.setVisible(!b);
    }

  }

  public void showDiplodocusActions(boolean b) {

    // TraceManager.addDev("Show diplodocus options with b = " + b);

    genMapping.setVisible(b);
    dse.setVisible(b);

    genMappingMI.setVisible(b);
    dseMI.setVisible(b);

    if (dseZ3 != null) {
      dseZ3.setVisible(b);
    }

    if (dseZ3MI != null) {
      dseZ3MI.setVisible(b);
    }

    if (noc != null) {
      noc.setVisible(b);
    }

    if (nocMI != null) {
      nocMI.setVisible(b);
    }

    avatarSimu.setVisible(b);
    avatarSimuMI.setVisible(b);

    avatarFVUPPAAL.setVisible(!b);
    avatarFVStaticAnalysis.setVisible(!b);
    avatarFVUPPAALMI.setVisible(!b);
    avatarFVStaticAnalysisMI.setVisible(!b);

    if (avatarFVProVerif != null) {
      avatarFVProVerif.setVisible(b);
    }

    if (avatarFVProVerifMI != null) {
      avatarFVProVerifMI.setVisible(b);
    }

    avatarCodeGeneration.setVisible(!b);
    avatarCodeGenerationMI.setVisible(!b);

    if (genrtlotos != null) {
      genrtlotos.setVisible(!b);
      genrtlotosMI.setVisible(!b);
    }

    if (genlotos != null) {
      genlotos.setVisible(!b);
      genlotosMI.setVisible(!b);
    }

    if (genuppaal != null) {
      genuppaal.setVisible(b);
      genuppaalMI.setVisible(b);
    }

    if (checkcode != null) {
      checkcode.setVisible(!b);
      checkcodeMI.setVisible(!b);
    }

    if (simulation != null) {
      simulation.setVisible(!b);
      simulationMI.setVisible(!b);
    }

    if (validation != null) {
      validation.setVisible(!b);
      validationMI.setVisible(!b);
    }

    if (oneClickrtlotos != null) {
      oneClickrtlotos.setVisible(!b);
      oneClickrtlotosMI.setVisible(!b);
    }

    if (onclicklotos != null) {
      onclicklotos.setVisible(!b);
      onclicklotosMI.setVisible(!b);
    }

    if (gensystemc != null) {
      gensystemc.setVisible(b);
      gensystemcMI.setVisible(b);
    }

    if (simusystemc != null) {
      simusystemc.setVisible(b);
      simusystemcMI.setVisible(b);
    }

    if (gentml != null) {
      gentml.setVisible(b);
      gentmlMI.setVisible(b);
    }

    if (genC != null) {
      genC.setVisible(b);
      genCMI.setVisible(b);
    }

    if (genjava != null) {
      genjava.setVisible(!b);
      genjavaMI.setVisible(!b);
    }

    if (nc != null) {
      nc.setVisible(!b);
      ncMI.setVisible(!b);
    }

    if (gendesign != null) {
      gendesign.setVisible(!b);
      gendesignMI.setVisible(!b);
    }

  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == search) {
      String text = search.getText();
      TraceManager.addDev("Searching elements with" + text);
      if (text.length() > 0) {
        search.setEnabled(false);
        mgui.search(text);
        search.setEnabled(true);
      }
    }
  }

  public void activateSearch(boolean enabled) {
    search.setEnabled(enabled);
  }

  public String getSearchText() {
    return search.getText();
  }

  public void setSearchText(final String text) {
    search.setText(text);
  }

  public void popupMainCommand() {
    if (popupShown) {
      menu.setVisible(false);
      popupShown = false;
      return;
    }

    // Get the location of the point 'on the screen'
    Point p = menuButton.getLocationOnScreen();
    menu.show(this, 0, 0);
    menu.setLocation(p.x, p.y + menuButton.getHeight());
  }

  private JMenuItem addMenu(TGUIAction action) {
    JMenuItem mi = new JMenuItem(action);
    addMenuItem(mi, action);
    return mi;
  }

  private void addMenuItem(JMenuItem mi, TGUIAction action) {
    mi.setName(action.getShortDescription());
    menu.add(mi);
  }

} // Class
