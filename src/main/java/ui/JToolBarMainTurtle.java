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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class JToolBarMainTurtle
 * Main toolbar of the ttool main window
 * Creation: 09/12/2003
 * @author Ludovic APVRILLE
 */
public  class JToolBarMainTurtle extends JToolBar implements ActionListener     {
    // Avatar
    JButton  avatarSimu, avatarFVUPPAAL, avatarFVProVerif, avatarFVStaticAnalysis, avatarCodeGeneration, avatarMC;

    // Other
    JButton genrtlotos, genlotos, genuppaal, gendesign, dse;
    JButton checkcode, simulation, validation;
    JButton oneClickrtlotos, onclicklotos, gensystemc, simusystemc, gentml, genC, genjava, nc,externalSearch, internalSearch;

    private JTextField search;

    MainGUI mgui;

    public JToolBarMainTurtle(MainGUI _mgui) {
        super();
        mgui = _mgui;
        buildToolBar(mgui);
    }

    // Menus
    private void buildToolBar(MainGUI mgui) {
        JButton button;

        button = add(mgui.actions[TGUIAction.ACT_NEW]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_NEW_PROJECT]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_OPEN]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_OPEN_PROJECT]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_SAVE]);
        button.addMouseListener(mgui.mouseHandler);
        //button = add(mgui.actions[TGUIAction.ACT_SAVE_AS]);
        //button.addMouseListener(mgui.mouseHandler);

        addSeparator();

        button = add(mgui.actions[TGUIAction.ACT_IMPORT_LIB]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_EXPORT_LIB]);
        button.addMouseListener(mgui.mouseHandler);

        addSeparator();

        button = add(mgui.actions[TGUIAction.ACT_CUT]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_COPY]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_PASTE]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_DELETE]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.MOVE_ENABLED]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.FIRST_DIAGRAM]);
        button.addMouseListener(mgui.mouseHandler);

        addSeparator();

        button = add(mgui.actions[TGUIAction.ACT_BACKWARD]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_FORWARD]);
        button.addMouseListener(mgui.mouseHandler);

        addSeparator();

        button = add(mgui.actions[TGUIAction.ACT_ZOOM_LESS]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_SHOW_ZOOM]);
        button.addMouseListener(mgui.mouseHandler);
        button = add(mgui.actions[TGUIAction.ACT_ZOOM_MORE]);
        button.addMouseListener(mgui.mouseHandler);
        //button.setMinimumSize(button.getSize());

        addSeparator();

        /*button = add(mgui.actions[TGUIAction.ACT_FIRST_DIAG]);
          button.addMouseListener(mgui.mouseHandler);
          button = add(mgui.actions[TGUIAction.ACT_BACK_DIAG]);
          button.addMouseListener(mgui.mouseHandler);
          button = add(mgui.actions[TGUIAction.ACT_NEXT_DIAG]);
          button.addMouseListener(mgui.mouseHandler);
          button = add(mgui.actions[TGUIAction.ACT_LAST_DIAG]);
          button.addMouseListener(mgui.mouseHandler);*/

        addSeparator();
        addSeparator();

        button = add(mgui.actions[TGUIAction.ACT_MODEL_CHECKING]);
        button.addMouseListener(mgui.mouseHandler);
        addSeparator();

        if (MainGUI.experimentalOn) {
            gendesign = add(mgui.actions[TGUIAction.ACT_GEN_DESIGN]);
            gendesign.addMouseListener(mgui.mouseHandler);
        }

        addSeparator();

        avatarSimu = add(mgui.actions[TGUIAction.ACT_AVATAR_SIM]);
        avatarSimu.addMouseListener(mgui.mouseHandler);


        dse = add(mgui.actions[TGUIAction.ACT_DSE]);
        dse.addMouseListener(mgui.mouseHandler);

        addSeparator();

        //if (MainGUI.experimentalOn) {
        avatarMC = add(mgui.actions[TGUIAction.ACT_AVATAR_MODEL_CHECKER]);
        avatarMC.addMouseListener(mgui.mouseHandler);
        //}
        avatarFVUPPAAL = add(mgui.actions[TGUIAction.ACT_AVATAR_FV_UPPAAL]);
        avatarFVUPPAAL.addMouseListener(mgui.mouseHandler);
        if (MainGUI.uppaalOn) {
            genuppaal = add(mgui.actions[TGUIAction.ACT_GEN_UPPAAL]);
            genuppaal.addMouseListener(mgui.mouseHandler);
        }
        if (MainGUI.proverifOn) {
            avatarFVProVerif = add(mgui.actions[TGUIAction.ACT_AVATAR_FV_PROVERIF]);
            avatarFVProVerif.addMouseListener(mgui.mouseHandler);
        }

        avatarFVStaticAnalysis = add(mgui.actions[TGUIAction.ACT_AVATAR_FV_STATICANALYSIS]);
        avatarFVStaticAnalysis.addMouseListener(mgui.mouseHandler);

        addSeparator();

        avatarCodeGeneration = add(mgui.actions[TGUIAction.ACT_AVATAR_EXECUTABLE_GENERATION]);
        avatarCodeGeneration.addMouseListener(mgui.mouseHandler);

        addSeparator();

        genrtlotos = add(mgui.actions[TGUIAction.ACT_GEN_RTLOTOS]);
        genrtlotos.addMouseListener(mgui.mouseHandler);
        if (MainGUI.systemcOn) {
            genlotos = add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
            genlotos.addMouseListener(mgui.mouseHandler);
            /*addSeparator();
              button = add(mgui.actions[TGUIAction.ACT_GEN_AUTS]);
              button.addMouseListener(mgui.mouseHandler);
              button = add(mgui.actions[TGUIAction.ACT_GEN_AUT]);
              button.addMouseListener(mgui.mouseHandler);*/
        } else if (MainGUI.lotosOn) {
            genlotos = add(mgui.actions[TGUIAction.ACT_GEN_LOTOS]);
            genlotos.addMouseListener(mgui.mouseHandler);
        }

        addSeparator();

        checkcode = add(mgui.actions[TGUIAction.ACT_CHECKCODE]);
        checkcode.addMouseListener(mgui.mouseHandler);
        simulation = add(mgui.actions[TGUIAction.ACT_SIMULATION]);
        simulation.addMouseListener(mgui.mouseHandler);
        validation = add(mgui.actions[TGUIAction.ACT_VALIDATION]);
        validation.addMouseListener(mgui.mouseHandler);

        addSeparator();

        oneClickrtlotos = add(mgui.actions[TGUIAction.ACT_ONECLICK_RTLOTOS_RG]);
        oneClickrtlotos.addMouseListener(mgui.mouseHandler);
        if (MainGUI.lotosOn) {
            onclicklotos = add(mgui.actions[TGUIAction.ACT_ONECLICK_LOTOS_RG]);
            onclicklotos.addMouseListener(mgui.mouseHandler);
        }

        addSeparator();

        if (MainGUI.systemcOn) {
            gensystemc = add(mgui.actions[TGUIAction.ACT_GEN_SYSTEMC]);
            gensystemc.addMouseListener(mgui.mouseHandler);

            simusystemc = add(mgui.actions[TGUIAction.ACT_SIMU_SYSTEMC]);
            simusystemc.addMouseListener(mgui.mouseHandler);
            addSeparator();

            gentml = add(mgui.actions[TGUIAction.ACT_GEN_TMLTXT]);
            gentml.addMouseListener(mgui.mouseHandler);
            addSeparator();

            genC = add(mgui.actions[TGUIAction.ACT_GEN_CCODE]);
            genC.addMouseListener(mgui.mouseHandler);
            addSeparator();
        }

        addSeparator();

        genjava = add(mgui.actions[TGUIAction.ACT_GEN_JAVA]);
        genjava.addMouseListener(mgui.mouseHandler);
        //button = add(mgui.actions[TGUIAction.ACT_SIMU_JAVA]);
        //button.addMouseListener(mgui.mouseHandler);

        addSeparator();

        if (MainGUI.ncOn) {
            nc = add(mgui.actions[TGUIAction.ACT_NC]);
            nc.addMouseListener(mgui.mouseHandler);
        }

        addSeparator();


        if ((SpecConfigTTool.ExternalCommand1.length() > 0) && (ConfigurationTTool.ExternalCommand1Host.length() > 0)) {
            button = add(mgui.actions[TGUIAction.EXTERNAL_ACTION_1]);
            button.addMouseListener(mgui.mouseHandler);
            button.setToolTipText(ConfigurationTTool.ExternalCommand1);
            addSeparator();
        }

        if ((ConfigurationTTool.ExternalCommand2.length() > 0) && (ConfigurationTTool.ExternalCommand2Host.length() > 0)) {
            button = add(mgui.actions[TGUIAction.EXTERNAL_ACTION_2]);
            button.addMouseListener(mgui.mouseHandler);
            button.setToolTipText(ConfigurationTTool.ExternalCommand2);
            addSeparator();
        }

        addSeparator();

        showAvatarActions(false);
        search = new JTextField("", 10);
        search.setEnabled(false);
        add( search );
        search.addActionListener(this);

        search.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    if ( search.getText().length()>0){
                        internalSearch.setEnabled(true);
                    }else
                        internalSearch.setEnabled(false);
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    if ( search.getText().length()>0){
                        internalSearch.setEnabled(true);
                    }else
                        internalSearch.setEnabled(false);
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                }
            });

        //@author : Huy TRUONG
        //add external search button into menu bar
        internalSearch = add(mgui.actions[TGUIAction.ACT_INTERNAL_SEARCH]);
        internalSearch.addMouseListener(mgui.mouseHandler);
        externalSearch = add(mgui.actions[TGUIAction.ACT_EXTERNAL_SEARCH]);
        externalSearch.addMouseListener(mgui.mouseHandler);
    }

    public void showAvatarActions(boolean b) {

        //TraceManager.addDev("Show avatar options with b = " + b);
        dse.setVisible(!b);

        avatarSimu.setVisible(b);
        avatarFVUPPAAL.setVisible(b);
        if (avatarFVProVerif != null) {
            avatarFVProVerif.setVisible(b);
        }
        //avatarFVStaticAnalysis.setVisible(b);
        avatarFVStaticAnalysis.setVisible(b);
        avatarCodeGeneration.setVisible(b);

        if (genrtlotos != null) {
            genrtlotos.setVisible(!b);
        }

        if (genlotos != null) {
            genlotos.setVisible(!b);
        }

        if (genuppaal != null) {
            genuppaal.setVisible(!b);
        }

        if (checkcode != null) {
            checkcode.setVisible(!b);
        }

        if (simulation != null) {
            simulation.setVisible(!b);
        }

        if (validation != null) {
            validation.setVisible(!b);
        }

        if (oneClickrtlotos != null) {
            oneClickrtlotos.setVisible(!b);
        }

        if (onclicklotos != null) {
            onclicklotos.setVisible(!b);
        }

        if (gensystemc != null) {
            gensystemc.setVisible(!b);
        }

        if (simusystemc != null) {
            simusystemc.setVisible(!b);
        }

        if (gentml != null) {
            gentml.setVisible(!b);
        }

        if (genC != null) {
            genC.setVisible(!b);
        }

        if (genjava != null) {
            genjava.setVisible(!b);
        }

        if (nc != null) {
            nc.setVisible(!b);
        }

        if (gendesign != null) {
            gendesign.setVisible(!b);
        }

    }

    public void showDiplodocusActions(boolean b) {

        //TraceManager.addDev("Show diplodocus options with b = " + b);

        dse.setVisible(b);
        avatarSimu.setVisible(!b);
        avatarFVUPPAAL.setVisible(!b);
        avatarFVStaticAnalysis.setVisible(!b);
        if (avatarFVProVerif != null) {
            avatarFVProVerif.setVisible(b);
        }
        avatarCodeGeneration.setVisible(!b);

        if (genrtlotos != null) {
            genrtlotos.setVisible(!b);
        }

        if (genlotos != null) {
            genlotos.setVisible(!b);
        }

        if (genuppaal != null) {
            genuppaal.setVisible(b);
        }

        if (checkcode != null) {
            checkcode.setVisible(!b);
        }

        if (simulation != null) {
            simulation.setVisible(!b);
        }

        if (validation != null) {
            validation.setVisible(!b);
        }

        if (oneClickrtlotos != null) {
            oneClickrtlotos.setVisible(!b);
        }

        if (onclicklotos != null) {
            onclicklotos.setVisible(!b);
        }

        if (gensystemc != null) {
            gensystemc.setVisible(b);
        }

        if (simusystemc != null) {
            simusystemc.setVisible(b);
        }

        if (gentml != null) {
            gentml.setVisible(b);
        }

        if (genC != null) {
            genC.setVisible(b);
        }

        if (genjava != null) {
            genjava.setVisible(!b);
        }

        if (nc != null) {
            nc.setVisible(!b);
        }

        if (gendesign != null) {
            gendesign.setVisible(!b);
        }

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == search ) {
            String text = search.getText();
            TraceManager.addDev("Searching elements with" + text);
            if (text.length()>0) {
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
    
    public void setSearchText( final String text ) {
    	search.setText( text );
    }
//
//	public JTextField getSearch() {
//		return search;
//	}
//
//	public void setSearch(JTextField search) {
//		this.search = search;
//	}
} // Class
