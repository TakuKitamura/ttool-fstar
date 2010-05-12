/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

ludovic.apvrille AT enst.fr

This software is a computer program whose purpose is to allow the 
edition of TURTLE analysis, design and deployment diagrams, to 
allow the generation of RT-LOTOS or Java code from this diagram, 
and at last to allow the analysis of formal validation traces 
obtained from external tools, e.g. RTL from LAAS-CNRS and CADP 
from INRIA Rhone-Alpes.

This software is governed by the CeCILL  license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL license and that you accept its terms.

/**
 * Class IconManager
 * Creation: 15/12/2003
 * @version 1.1 15/12/2003
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

import java.net.*;
import java.awt.*;
import javax.swing.ImageIcon;

/**
 * Class
 *
 * @author Ludovic APVRILLE
 * @see
 */
public class IconManager {
	
	public static ImageIcon imgic8, imgic9;
    //Added by Solange
    public static ImageIcon imgic0, imgic1;
    public static Image img8, img9;
	public static Image img5100, img5102;
    //Added by Solange
    public static Image img0, img1;
    
    public static ImageIcon imgic13, imgic14, imgic15, imgic16, imgic17, imgic18, imgic19;
    public static ImageIcon imgic20, imgic21, imgic22, imgic23, imgic24, imgic25, imgic26, imgic27, imgic28, imgic29;
    
    public static ImageIcon imgic30, imgic31, imgic32, imgic33, imgic34, imgic35, imgic36, imgic37, imgic38, imgic39;
    
    public static ImageIcon imgic40, imgic41, imgic42, imgic43, imgic44, imgic45, imgic46, imgic47, imgic48, imgic49;
    public static ImageIcon imgic50, imgic51, imgic52, imgic53, imgic54, imgic55, imgic56, imgic57, imgic58, imgic59;
    public static ImageIcon imgic60, imgic61, imgic62, imgic63, imgic64, imgic65, imgic66;
    public static ImageIcon imgic70, imgic71, imgic72, imgic73, imgic75, imgic76, imgic77;
	
    // Class diagram
    public static ImageIcon imgic100, imgic101, imgic102, imgic104, imgic106, imgic108;
    public static ImageIcon imgic110, imgic112, imgic114, imgic116, imgic118;
    public static ImageIcon imgic120, imgic122, imgic124, imgic126, imgic128, imgic130;
    public static ImageIcon imgic132, imgic134, imgic136, imgic138;
    
    //Activity Diagram
    public static ImageIcon imgic200, imgic201, imgic202, imgic204, imgic206, imgic208;
    public static ImageIcon imgic210, imgic212, imgic214, imgic216, imgic218;
    public static ImageIcon imgic220, imgic222, imgic224, imgic226, imgic228;
	public static ImageIcon imgic230, imgic232;
    
    //Common to all diagrams
    public static ImageIcon imgic302, imgic304, imgic306, imgic308;
    public static ImageIcon imgic310, imgic312, imgic314, imgic315, imgic316, imgic317, imgic318, imgic319;
    public static ImageIcon imgic320, imgic321, imgic322, imgic323, imgic324;
    public static ImageIcon imgic330, imgic331, imgic332, imgic333, imgic334, imgic335, imgic336, imgic337, imgic338, imgic339;
    public static ImageIcon imgic340, imgic341, imgic342;
    
    //IOD
    public static ImageIcon imgic400, imgic402;
    
    public static ImageIcon imgic500, imgic502, imgic504, imgic506, imgic508, imgic510;
    public static ImageIcon imgic512, imgic514, imgic516, imgic518, imgic520, imgic522, imgic524;

    public static ImageIcon imgic600, imgic602, imgic604, imgic606, imgic608, imgic610, imgic612;
    
    public static ImageIcon imgic700, imgic702;
    
    public static ImageIcon imgic800, imgic802, imgic804, imgic806, imgic808, imgic810, imgic812;
    public static ImageIcon imgic900, imgic902, imgic904, imgic906, imgic908, imgic910, imgic912, imgic914, imgic916, imgic918;
	public static ImageIcon imgic920, imgic922, imgic924, imgic926, imgic928;    
    // Requirement diagrams
    public static ImageIcon imgic1000, imgic1002, imgic1004,imgic1006,imgic1008, imgic1010, imgic1012, imgic1014;
	public static ImageIcon imgic1050, imgic1052, imgic1054,imgic1056, imgic1058, imgic1060;
	
	// Attack Tree Diagrams
	public static ImageIcon imgic1070, imgic1072, imgic1074, imgic1076, imgic1078;
	public static ImageIcon imgic1080;
	
	// TMLDD
	public static ImageIcon imgic1100, imgic1102, imgic1104, imgic1106, imgic1108;
    
	// TML component task diagram
	public static ImageIcon imgic1200, imgic1202, imgic1204, imgic1206, imgic1208;
	public static ImageIcon imgic1300, imgic1302, imgic1304, imgic1306, imgic1308;
	public static ImageIcon imgic1310, imgic1312, imgic1314, imgic1316, imgic1318;
	public static ImageIcon imgic1320, imgic1322, imgic1324, imgic1326;
	
	
    // SMD diagram
    public static ImageIcon imgic2000, imgic2002, imgic2004,imgic2006,imgic2008,imgic2010, imgic2012;
    
    // CSD diagram
    public static ImageIcon imgic2100, imgic2104, imgic2106,imgic2108,imgic2110;
      
 
	// NC
	public static ImageIcon imgic3000;
	
	// AVATAR
	public static ImageIcon imgic5000, imgic5002, imgic5004, imgic5006, imgic5008;
	public static ImageIcon imgic5010, imgic5012, imgic5014, imgic5016, imgic5018;
	public static ImageIcon imgic5020, imgic5022, imgic5024, imgic5026, imgic5028;
	public static ImageIcon imgic5030, imgic5032;
	
	public static ImageIcon imgic5100, imgic5102, imgic5104;
	
       // Delegate ports image removed, by Solange
    //public static ImageIcon imgic2102;
    
       // Image of the help button for the ports (Solange)
    public static ImageIcon imgic2111; //New icon created by Solange
	
    
    //private static String icon7 = "images/turtle_large.gif";
    private static String icon8 = "images/turtle_16.gif";
	private static String icon9 = "images/diplodocus2_16.gif";
    //Added by Solange
    private static String icon0= "images/hideifce.gif";
    //Added by Solange, the component icon
    private static String icon1= "images/procomp.gif";
      //root for the image of the help icon, created by Solange
    private static String icon2111= "images/sole.gif";
    
    //private static String icon10 = "images/toolbarButtonGraphics/general/Add24.gif";
    //private static String icon11 = "images/toolbarButtonGraphics/general/Save16.gif";
    private static String icon12 = "images/toolbarButtonGraphics/general/Import24.gif";
    private static String icon13 = "images/toolbarButtonGraphics/general/Information16.gif";
    
    private static String icon14 = "images/classdiagram.gif";
    private static String icon15 = "images/activitydiagram.gif";
    
    private static String icon16 = "images/toolbarButtonGraphics/general/Refresh16.gif";
    
    private static String icon17 = "images/iod.gif";
    private static String icon18 = "images/sd.gif";
    private static String icon19 = "images/ucd.gif";
    
    private static String icon20 = "images/toolbarButtonGraphics/general/New16.gif";
    private static String icon21 = "images/toolbarButtonGraphics/general/New24.gif";
    private static String icon22 = "images/toolbarButtonGraphics/general/Open16.gif";
    private static String icon23 = "images/toolbarButtonGraphics/general/Open24.gif";
    private static String icon24 = "images/toolbarButtonGraphics/general/Save16.gif";
    private static String icon25 = "images/toolbarButtonGraphics/general/Save24.gif";
    private static String icon26 = "images/toolbarButtonGraphics/general/Stop16.gif";
    private static String icon27 = "images/toolbarButtonGraphics/general/Stop24.gif";
    private static String icon28 = "images/toolbarButtonGraphics/general/History16.gif";
    private static String icon29 = "images/toolbarButtonGraphics/general/History24.gif";
    
    private static String icon30 = "images/toolbarButtonGraphics/general/About16.gif";
    private static String icon31 = "images/toolbarButtonGraphics/general/About24.gif";
    
    private static String icon32 = "images/toolbarButtonGraphics/general/Help16.gif";
    private static String icon33 = "images/toolbarButtonGraphics/general/Help24.gif";
    private static String icon34 = "images/genrtlotos.gif";
    private static String icon35 = "images/genrtlotos.gif";
    private static String icon36 = "images/checkmodel.gif";
    private static String icon37 = "images/checkmodel.gif";
    private static String icon38 = "images/genjava.gif";
    private static String icon39 = "images/genjava.gif";
    
    private static String icon40 = "images/toolbarButtonGraphics/general/Undo16.gif";
    private static String icon41 = "images/toolbarButtonGraphics/general/Undo24.gif";
    private static String icon42 = "images/toolbarButtonGraphics/general/Redo16.gif";
    private static String icon43 = "images/toolbarButtonGraphics/general/Redo24.gif";
    private static String icon44 = "images/toolbarButtonGraphics/media/Rewind16.gif";
    private static String icon45 = "images/toolbarButtonGraphics/media/Rewind24.gif";
    private static String icon46 = "images/toolbarButtonGraphics/media/StepBack16.gif";
    private static String icon47 = "images/toolbarButtonGraphics/media/StepBack24.gif";
    private static String icon48 = "images/toolbarButtonGraphics/media/StepForward16.gif";
    private static String icon49 = "images/toolbarButtonGraphics/media/StepForward24.gif";
    private static String icon50 = "images/toolbarButtonGraphics/media/FastForward16.gif";
    private static String icon51 = "images/toolbarButtonGraphics/media/FastForward24.gif";
    private static String icon52 = "images/toolbarButtonGraphics/media/Play16.gif";
    private static String icon53 = "images/toolbarButtonGraphics/media/Play24.gif";
    private static String icon54 = "images/toolbarButtonGraphics/media/Stop16.gif";
    private static String icon55 = "images/toolbarButtonGraphics/media/Stop24.gif";
    private static String icon56 = "images/toolbarButtonGraphics/general/Find16.gif";
    private static String icon57 = "images/toolbarButtonGraphics/general/Find24.gif";
    private static String icon58 = "images/gendesign.gif";
    private static String icon59 = "images/gendesign.gif";
    
    private static String icon60 = "images/dd.gif";
    
    private static String icon61 = "images/gensystc.gif";
    private static String icon62 = "images/tmldesign.gif";
    private static String icon63 = "images/tmlactivity.gif";
    private static String icon64 = "images/genaut.gif";
    private static String icon65 = "images/concomment.gif";
	private static String icon66 = "images/gentmltxt.gif";
	
	private static String icon70 = "images/toolbarButtonGraphics/general/SendMail16.gif";
	private static String icon71 = "images/toolbarButtonGraphics/general/SendMail24.gif";
	private static String icon72 = "images/toolbarButtonGraphics/general/Help16.gif";
	private static String icon73 = "images/toolbarButtonGraphics/general/Help24.gif";
	private static String icon75 = "images/toolbarButtonGraphics/general/Refresh24.gif";
	private static String icon76 = "images/toolbarButtonGraphics/general/Preferences16.gif";
	private static String icon77 = "images/toolbarButtonGraphics/general/Preferences24.gif";
	
    
    private static String icon100 = "images/toolbarButtonGraphics/general/Properties16.gif";
    private static String icon101 = "images/toolbarButtonGraphics/general/Properties24.gif";
    private static String icon102 = "images/cdassociation.gif";
    //private static String icon103 = "images/cdassociation.gif";
    private static String icon104 = "images/cdtclass.gif";
    //private static String icon105 = "images/cdtclass.gif";
    private static String icon106 = "images/cdassonav.gif";
    //private static String icon107 = "images/cdassonav.gif";
    private static String icon108 = "images/cdattribute.gif";
    //private static String icon109 = "images/cdattribute.gif";
    private static String icon110 = "images/cdsynchro.gif";
    //private static String icon111 = "images/cdsynchro.gif";
    private static String icon112 = "images/cdparallel.gif";
    //private static String icon113 = "images/cdparallel.gif";
    private static String icon114 = "images/cdsequence.gif";
    //private static String icon115 = "images/cdsequence.gif";
    private static String icon116 = "images/cdpreemption.gif";
    //private static String icon117 = "images/cdpreemption.gif";
    private static String icon118 = "images/cdnatural.gif";
    //private static String icon119 = "images/cdnatural.gif";
    private static String icon120 = "images/cdgates.gif";
    //private static String icon121 = "images/cdgates.gif";
    private static String icon122 = "images/cdoperation.gif";
    //private static String icon123 = "images/cdoperation.gif";
    private static String icon124 = "images/cdactivity.gif";
    //private static String icon125 = "images/cdactivity.gif";
    private static String icon126 = "images/cdinvocation.gif";
    //private static String icon127 = "images/cdinvocation.gif";
    private static String icon128 = "images/cdtobject.gif";
    //private static String icon129 = "images/cdtobject.gif";
    private static String icon130 = "images/cdtdata.gif";
    private static String icon132 = "images/cdtoggleatt.gif";
    private static String icon134 = "images/cdtogglegat.gif";
    private static String icon136 = "images/cdtogglesyn.gif";
    private static String icon138 = "images/adtogglejava.gif";
    
    private static String icon200 = "images/toolbarButtonGraphics/general/Properties16.gif";
    private static String icon201 = "images/toolbarButtonGraphics/general/Properties24.gif";
    private static String icon202 = "images/adconnector.gif";
    //private static String icon203 = "images/adconnector.gif";
    private static String icon204 = "images/actionstate.gif";
    //private static String icon205 = "images/actionstate.gif";
    private static String icon206 = "images/adparallel.gif";
    //private static String icon207 = "images/adparallel.gif";
    private static String icon208 = "images/adchoice.gif";
    //private static String icon209 = "images/adchoice.gif";
    private static String icon210 = "images/adstop.gif";
    //private static String icon211 = "images/adstop.gif";
    private static String icon212 = "images/adjunction.gif";
    //private static String icon213 = "images/adjunction.gif";
    private static String icon214 = "images/adddelay.gif";
    //private static String icon215 = "images/adddelay.gif";
    private static String icon216 = "images/adnddelay.gif";
    //private static String icon217 = "images/adnddelay.gif";
    private static String icon218 = "images/adtloffer.gif";
    //private static String icon219 = "images/adtloffer.gif";
    private static String icon220 = "images/adtlofferwl.gif";
    //private static String icon221 = "images/adtlofferwl.gif";
    private static String icon222 = "images/adstart.gif";
    //private static String icon223 = "images/adstart.gif";
    private static String icon224 = "images/adtimeinterval.gif";
    private static String icon226 = "images/adsequence.gif";
    private static String icon228 = "images/adpreemption.gif";
	private static String icon230 = "images/arrayget.gif";
	private static String icon232 = "images/arrayset.gif";
    
    private static String icon302 = "images/text1.gif";
    //private static String icon303 = "images/text1.gif";
    private static String icon304 = "images/point.gif";
    //private static String icon305 = "images/point.gif";
    private static String icon306 = "images/dta.gif";
    //private static String icon307 = "images/dta.gif";
    private static String icon308 = "images/rg.gif";
    //private static String icon309 = "images/rg.gif";
    private static String icon310 = "images/formalvalidation.gif";
    //private static String icon311 = "images/formalvalidation.gif";
    private static String icon312 = "images/simulation.gif";
    //private static String icon313 = "images/simulation.gif";
    
    private static String icon314 = "images/toolbarButtonGraphics/general/ZoomOut16.gif";
    private static String icon315 = "images/toolbarButtonGraphics/general/ZoomOut24.gif";
    private static String icon316 = "images/toolbarButtonGraphics/general/ZoomIn16.gif";
    private static String icon317 = "images/toolbarButtonGraphics/general/ZoomIn24.gif";
    
    private static String icon318 = "images/viewsimulation.gif";
    private static String icon319 = "images/viewsimulation.gif";
    
    private static String icon320 = "images/toolbarButtonGraphics/general/Edit16.gif";
    private static String icon321 = "images/toolbarButtonGraphics/general/Edit24.gif";
    private static String icon322 = "images/toolbarButtonGraphics/general/TipOfTheDay16.gif";
    private static String icon323 = "images/toolbarButtonGraphics/general/TipOfTheDay24.gif";
    
    private static String icon324 = "images/starting_logo.gif";
    
    private static String icon330 = "images/toolbarButtonGraphics/general/Cut16.gif";
    private static String icon331 = "images/toolbarButtonGraphics/general/Cut24.gif";
    private static String icon332 = "images/toolbarButtonGraphics/general/Copy16.gif";
    private static String icon333 = "images/toolbarButtonGraphics/general/Copy24.gif";
    private static String icon334 = "images/toolbarButtonGraphics/general/Paste16.gif";
    private static String icon335 = "images/toolbarButtonGraphics/general/Paste24.gif";
    private static String icon336 = "images/toolbarButtonGraphics/general/Delete16.gif";
    private static String icon337 = "images/toolbarButtonGraphics/general/Delete24.gif";
    private static String icon338 = "images/toolbarButtonGraphics/general/Import16.gif";
    private static String icon339 = "images/toolbarButtonGraphics/general/Import24.gif";
    private static String icon340 = "images/toolbarButtonGraphics/general/Export16.gif";
    private static String icon341 = "images/toolbarButtonGraphics/general/Export24.gif";
	
	private static String icon342 = "images/oneformalvalidation.gif";;
    
    
    // IOD
    private static String icon400 = "images/referencetosd.gif";
    private static String icon402 = "images/iodpreemption.gif";
    
    // SD
    private static String icon500 = "images/sdinstance.gif";
    private static String icon502 = "images/sdsynchronous.gif";
    private static String icon504 = "images/sdasynchronous.gif";
    private static String icon506 = "images/sdabsolutetc.gif";
    private static String icon508 = "images/sdrelativetc1.gif";
    private static String icon510 = "images/sdrelativetc2.gif";
    private static String icon512 = "images/actionstate.gif";
    private static String icon514 = "images/sdtimersetting.gif";
    private static String icon516 = "images/sdtimerexpire.gif";
    private static String icon518 = "images/sdtimercancel.gif";
    private static String icon520 = "images/sdcoregion.gif";
    private static String icon522 = "images/sdaligninstances.gif";
	private static String icon524 = "images/sdguard.gif";
    
    // UCD
    private static String icon600 = "images/ucdactor.gif";
    private static String icon602 = "images/ucdusecase.gif";
    private static String icon604 = "images/ucdconnact.gif";
    private static String icon606 = "images/ucdinclude.gif";
    private static String icon608 = "images/ucdextend.gif";
    private static String icon610 = "images/ucdspecia.gif";
    private static String icon612 = "images/ucdborder.gif";
    
    // Deployment diagrams
    private static String icon700 = "images/ddnode.gif";
    private static String icon702 = "images/ddartifact.gif";
    

    
    // TML
    private static String icon800 = "images/tmlcdevt.gif";
    private static String icon802 = "images/tmlcdcha.gif";
    private static String icon804 = "images/tmlcdreq.gif";
    private static String icon806 = "images/tmltask.gif";
    private static String icon808 = "images/tmltogglecha.gif";
    private static String icon810 = "images/tmltoggleevt.gif";
    private static String icon812 = "images/tmltogglereq.gif";
    
    private static String icon900 = "images/tmladwrch.gif";
    private static String icon902 = "images/tmladsendreq.gif";
    private static String icon904 = "images/tmladsendevt.gif";
    private static String icon906 = "images/tmladrdch.gif";
    private static String icon908 = "images/tmladwaitevt.gif";
    private static String icon910 = "images/tmladexeci.gif";
    private static String icon912 = "images/tmladforloop.gif";
    private static String icon914 = "images/tmladexeciint.gif";
    private static String icon916 = "images/tmladselectevt.gif";
    private static String icon918 = "images/tmladnotifiedevt.gif";
	private static String icon920 = "images/tmladexecc.gif";
	private static String icon922 = "images/tmladexeccint.gif";
	private static String icon924 = "images/tmladrandom.gif";
	private static String icon926 = "images/tmladforeverloop.gif";
	private static String icon928 = "images/tmladunorderedsequence.gif";
	
	// Requirement diagrams
    private static String icon1000 = "images/reqdiag.gif";
    private static String icon1002 = "images/reqreq.gif";
    private static String icon1004 = "images/reqobs.gif";
    private static String icon1006 = "images/reqver.gif";
    private static String icon1008 = "images/reqder.gif";
	private static String icon1010 = "images/reqcopy.gif";
	private static String icon1012 = "images/reqcomp.gif";
	private static String icon1014 = "images/ebrddobs.gif";
	
	
	// EBRDD
	private static String icon1050 = "images/ebrdderc.gif";
	private static String icon1052 = "images/ercconnector.gif";
	private static String icon1054 = "images/ebrddeso.gif";
	private static String icon1056 = "images/ebrdderb.gif";
	private static String icon1058 = "images/ebrdd.gif";
	private static String icon1060 = "images/ebrddvar.gif";
	
	// Attack Tree Diagrams
	private static String icon1070 = "images/atdblock.gif";
	private static String icon1072 = "images/atdattack.gif";
    private static String icon1074 = "images/atdiag.gif";
	private static String icon1076 = "images/atdcomp.gif";
	private static String icon1078 = "images/atdcons.gif";
	private static String icon1080 = "images/atdlink.gif";
	
	// DIPLODOCUS architecture
	private static String icon1100 = "images/tmlcpunode.gif";
	private static String icon1102 = "images/tmlbusnode.gif";
	private static String icon1104 = "images/tmlbridgenode.gif";
	private static String icon1106 = "images/tmlhwanode.gif";
	private static String icon1108 = "images/tmlmemorynode.gif";
	
	private static String icon1200 = "images/tmlcompcomp.gif";
	private static String icon1202 = "images/tmlprimcomp.gif";
	private static String icon1204 = "images/tmlcompport.gif";
	private static String icon1206 = "images/tmlprimport.gif";
	private static String icon1208 = "images/tmlcompdiag.gif";
	
	private static String icon1300 = "images/stepforwardxtimeunits24.gif";
    private static String icon1302 = "images/stepforwardtobreakpoint24.gif";
    private static String icon1304 = "images/stepforwardtotime24.gif";
    private static String icon1306 = "images/stepforwardxtransactions24.gif";
    private static String icon1308 = "images/stepforwardxcommands24.gif";
	private static String icon1310 = "images/savevcd24.gif";
	private static String icon1312 = "images/savehtml24.gif";
	private static String icon1314 = "images/savetxt24.gif";
	private static String icon1316 = "images/steptobus.gif";
	private static String icon1318 = "images/steptotask.gif";
	private static String icon1320 = "images/steptocpu.gif";
	private static String icon1322 = "images/steptomem.gif";
	private static String icon1324 = "images/steptochan.gif";
	private static String icon1326 = "images/runexplo.gif";
   
    // SMD diagrams
    private static String icon2000 = "images/prosmdsendmsg.gif";
    private static String icon2002 = "images/prosmdgetmsg.gif";
    private static String icon2004 = "images/adchoice.gif";
    private static String icon2006 = "images/adjonction.gif"; //junction
    private static String icon2008 = "images/prosmdsubmachine1.gif"; //
    private static String icon2010= "images/actionstate.gif";
    private static String icon2012= "images/prosmdstate.gif";
    
    
    //CSD diagrams
    private static String icon2100 = "images/procsdcomponent.gif";
      //Delegate ports image removed, by Solange
    //private static String icon2102 = "images/adport.gif";
    private static String icon2104 = "images/cdtclass.gif";
    private static String icon2106 = "images/proCSDInPort.gif";
    private static String icon2108 = "images/proCSDOutPort.gif";
    private static String icon2110 = "images/ProCSDConector.gif";
	
	private static String icon3000= "images/nc.gif";
	
	
	// AVATAR
	private static String icon5000 = "images/avatarblock.gif";
	private static String icon5002 = "images/avatarbdcomp.gif";
	private static String icon5004 = "images/avatarbdlink.gif";
	private static String icon5006 = "images/avatarrdreq.gif";
    private static String icon5008 = "images/avatarrdprop.gif";
	private static String icon5010 = "images/avatarpdattribute.gif";
	private static String icon5012 = "images/avatarpdsetting.gif";
	private static String icon5014 = "images/avatarpdbooleq.gif";
	private static String icon5016 = "images/avatarpdattributeconnector.gif";
	private static String icon5018 = "images/avatarpdsignalconnector.gif";
	private static String icon5020 = "images/avatarpdpropertyconnector.gif";
	private static String icon5022 = "images/avatarpdsignal.gif";
	private static String icon5024 = "images/avatarpdpropertyrelation.gif";
	private static String icon5026 = "images/avatarpdproperty.gif";
	private static String icon5028 = "images/avatarpdlogicalconstraint.gif";
	private static String icon5030 = "images/avatarpdtemporalconstraint.gif";
	private static String icon5032 = "images/avatarpdalias.gif";
	
	private static String icon5100 = "images/avatarhead16.gif";
	private static String icon5102 = "images/avatarhead32.gif";
	
	
    public IconManager() {
        
    }
    
    public ImageIcon getIcon(String s){
        URL url = this.getClass().getResource(s);
        
        if (url != null)  {
            return new ImageIcon(url);
        } else {
            System.out.println("Could not load " + s);
        }
        
        return null;
    }
    
    public static void checkIcons() {
        ImageIcon imc = new ImageIcon(IconManager.icon12);
        Image im = imc.getImage();
        if (im == null) {
            System.out.println("Cannot load icons");
        } else {
            System.out.println("Icons loaded");
        }
    }
    
    public void loadImg() {
        imgic8 = getIcon(icon8);
		imgic9 = getIcon(icon9);
		if (imgic8 != null) {
			img8 = imgic8.getImage();
		}
		if (img9 != null) {
			img9 = imgic9.getImage();
		}
		
        //Added by Solange
        imgic0 = getIcon(icon0);
        //Added by Solange, the component icon
        imgic1 = getIcon(icon1);
        
        //Added by Solange
        img0 = imgic0.getImage();
        img1 = imgic1.getImage();
        
        imgic13 = getIcon(icon13);
        imgic14 = getIcon(icon14);
        imgic15 = getIcon(icon15);
        imgic16 = getIcon(icon16);
        imgic17 = getIcon(icon17);
        imgic18 = getIcon(icon18);
        imgic19 = getIcon(icon19);
        
        imgic20 = getIcon(icon20);
        imgic21 = getIcon(icon21);
        imgic22 = getIcon(icon22);
        imgic23 = getIcon(icon23);
        imgic24 = getIcon(icon24);
        imgic25 = getIcon(icon25);
        imgic26 = getIcon(icon26);
        imgic27 = getIcon(icon27);
        imgic28 = getIcon(icon28);
        imgic29 = getIcon(icon29);
        
        imgic30 = getIcon(icon30);
        imgic31 = getIcon(icon31);
        imgic32 = getIcon(icon32);
        imgic33 = getIcon(icon33);
        imgic34 = getIcon(icon34);
        imgic35 = getIcon(icon35);
        imgic36 = getIcon(icon36);
        imgic37 = getIcon(icon37);
        imgic38 = getIcon(icon38);
        imgic39 = getIcon(icon39);
        
        imgic40 = getIcon(icon40);
        imgic41 = getIcon(icon41);
        imgic42 = getIcon(icon42);
        imgic43 = getIcon(icon43);
        imgic44 = getIcon(icon44);
        imgic45 = getIcon(icon45);
        imgic46 = getIcon(icon46);
        imgic47 = getIcon(icon47);
        imgic48 = getIcon(icon48);
        imgic49 = getIcon(icon49);
        
        imgic50 = getIcon(icon50);
        imgic51 = getIcon(icon51);
        imgic52 = getIcon(icon52);
        imgic53 = getIcon(icon53);
        imgic54 = getIcon(icon54);
        imgic55 = getIcon(icon55);
        imgic56 = getIcon(icon56);
        imgic57 = getIcon(icon57);
        imgic58 = getIcon(icon58);
        imgic59 = getIcon(icon59);
        
        imgic60 = getIcon(icon60);
        imgic61 = getIcon(icon61);
        imgic62 = getIcon(icon62);
        imgic63 = getIcon(icon63);
        imgic64 = getIcon(icon64);
        imgic65 = getIcon(icon65);
		imgic66 = getIcon(icon66);
		
		imgic70 = getIcon(icon70);
        imgic71 = getIcon(icon71);
		imgic72 = getIcon(icon72);
        imgic73 = getIcon(icon73);
		imgic75 = getIcon(icon75);
		imgic76 = getIcon(icon76);
		imgic77 = getIcon(icon77);
        
        imgic100 = getIcon(icon100);
        imgic101 = getIcon(icon101);
        imgic102 = getIcon(icon102);
        imgic104 = getIcon(icon104);
        imgic106 = getIcon(icon106);
        imgic108 = getIcon(icon108);
        imgic110 = getIcon(icon110);
        imgic112 = getIcon(icon112);
        imgic114 = getIcon(icon114);
        imgic116 = getIcon(icon116);
        imgic118 = getIcon(icon118);
        imgic120 = getIcon(icon120);
        imgic122 = getIcon(icon122);
        imgic124 = getIcon(icon124);
        imgic126 = getIcon(icon126);
        imgic128 = getIcon(icon128);
        imgic130 = getIcon(icon130);
        imgic132 = getIcon(icon132);
        imgic134 = getIcon(icon134);
        imgic136 = getIcon(icon136);
        imgic138 = getIcon(icon138);
        
        imgic200 = getIcon(icon200);
        imgic201 = getIcon(icon201);
        imgic202 = getIcon(icon202);
        imgic204 = getIcon(icon204);
        imgic206 = getIcon(icon206);
        imgic208 = getIcon(icon208);
        imgic210 = getIcon(icon210);
        imgic212 = getIcon(icon212);
        imgic214 = getIcon(icon214);
        imgic216 = getIcon(icon216);
        imgic218 = getIcon(icon218);
        imgic220 = getIcon(icon220);
        imgic222 = getIcon(icon222);
        imgic224 = getIcon(icon224);
        imgic226 = getIcon(icon226);
        imgic228 = getIcon(icon228);
		imgic230 = getIcon(icon230);
		imgic232 = getIcon(icon232);
        
        imgic302 = getIcon(icon302);
        imgic304 = getIcon(icon304);
        imgic306 = getIcon(icon306);
        imgic308 = getIcon(icon308);
        imgic310 = getIcon(icon310);
        imgic312 = getIcon(icon312);
        
        imgic314 = getIcon(icon314);
        imgic315 = getIcon(icon315);
        imgic316 = getIcon(icon316);
        imgic317 = getIcon(icon317);
        
        imgic318 = getIcon(icon318);
        imgic319 = getIcon(icon319);
        
        imgic320 = getIcon(icon320);
        imgic321 = getIcon(icon321);
        imgic322 = getIcon(icon322);
        imgic323 = getIcon(icon323);
        
        imgic324 = getIcon(icon324);
        
        imgic330 = getIcon(icon330);
        imgic331 = getIcon(icon331);
        imgic332 = getIcon(icon332);
        imgic333 = getIcon(icon333);
        imgic334 = getIcon(icon334);
        imgic335 = getIcon(icon335);
        imgic336 = getIcon(icon336);
        imgic337 = getIcon(icon337);
        imgic338 = getIcon(icon338);
        imgic339 = getIcon(icon339);
        imgic340 = getIcon(icon340);
        imgic341 = getIcon(icon341);
		imgic342 = getIcon(icon342);
        
        imgic400 = getIcon(icon400);
        imgic402 = getIcon(icon402);
        
        imgic500 = getIcon(icon500);
        imgic502 = getIcon(icon502);
        imgic504 = getIcon(icon504);
        imgic506 = getIcon(icon506);
        imgic508 = getIcon(icon508);
        imgic510 = getIcon(icon510);
        imgic512 = getIcon(icon512);
        imgic514 = getIcon(icon514);
        imgic516 = getIcon(icon516);
        imgic518 = getIcon(icon518);
        imgic520 = getIcon(icon520);
        imgic522 = getIcon(icon522);
		imgic524 = getIcon(icon524);
        
        imgic600 = getIcon(icon600);
        imgic602 = getIcon(icon602);
        imgic604 = getIcon(icon604);
        imgic606 = getIcon(icon606);
        imgic608 = getIcon(icon608);
        imgic610 = getIcon(icon610);
        imgic612 = getIcon(icon612);
        
        imgic700 = getIcon(icon700);
        imgic702 = getIcon(icon702);
        
        imgic800 = getIcon(icon800);
        imgic802 = getIcon(icon802);
        imgic804 = getIcon(icon804);
        imgic806 = getIcon(icon806);
        imgic808 = getIcon(icon808); 
        imgic810 = getIcon(icon810);
        imgic812 = getIcon(icon812); 
        
        imgic900 = getIcon(icon900);
        imgic902 = getIcon(icon902);
        imgic904 = getIcon(icon904);
        imgic906 = getIcon(icon906);
        imgic908 = getIcon(icon908);
        imgic910 = getIcon(icon910);
        imgic912 = getIcon(icon912);
        imgic914 = getIcon(icon914);
        imgic916 = getIcon(icon916);
        imgic918 = getIcon(icon918);
		imgic920 = getIcon(icon920);
		imgic922 = getIcon(icon922); 
		imgic924 = getIcon(icon924);
		imgic926 = getIcon(icon926);
		imgic928 = getIcon(icon928);
        
        imgic1000 = getIcon(icon1000);
        imgic1002 = getIcon(icon1002);
        imgic1004 = getIcon(icon1004);
        imgic1006 = getIcon(icon1006);
        imgic1008 = getIcon(icon1008);
		imgic1010 = getIcon(icon1010);
		imgic1012 = getIcon(icon1012);
		imgic1014 = getIcon(icon1014);
		
		imgic1050 = getIcon(icon1050);
        imgic1052 = getIcon(icon1052);
        imgic1054 = getIcon(icon1054);
        imgic1056 = getIcon(icon1056);
		imgic1058 = getIcon(icon1058);
		imgic1060 = getIcon(icon1060);
		
		imgic1070 = getIcon(icon1070);
        imgic1072 = getIcon(icon1072);
		imgic1074 = getIcon(icon1074);
		imgic1076 = getIcon(icon1076);
		imgic1078 = getIcon(icon1078);
		imgic1080 = getIcon(icon1080);
		
		imgic1100 = getIcon(icon1100);
		imgic1102 = getIcon(icon1102);
		imgic1104 = getIcon(icon1104);
		imgic1106 = getIcon(icon1106);
		imgic1108 = getIcon(icon1108);
		
		imgic1200 = getIcon(icon1200);
		imgic1202 = getIcon(icon1202);
		imgic1204 = getIcon(icon1204);
		imgic1206 = getIcon(icon1206);
		imgic1208 = getIcon(icon1208);
		
		imgic1300 = getIcon(icon1300);
		imgic1302 = getIcon(icon1302);
		imgic1304 = getIcon(icon1304);
		imgic1306 = getIcon(icon1306);
		imgic1308 = getIcon(icon1308); 
		imgic1310 = getIcon(icon1310);
		imgic1312 = getIcon(icon1312);
		imgic1314 = getIcon(icon1314);
		imgic1316 = getIcon(icon1316);
		imgic1318 = getIcon(icon1318);
		imgic1320 = getIcon(icon1320);
		imgic1322 = getIcon(icon1322);
		imgic1324 = getIcon(icon1324);
		imgic1326 = getIcon(icon1326);
        
        imgic2000 = getIcon(icon2000);
        imgic2002 = getIcon(icon2002);
        imgic2004 = getIcon(icon2004);
        imgic2006 = getIcon(icon2006);
        imgic2008 = getIcon(icon2008);
        imgic2010 = getIcon(icon2010);
        imgic2012 = getIcon(icon2012);
        
        imgic2100 = getIcon(icon2100);
        //Delegate Ports removed, by Solange
        //imgic2102 = getIcon(icon2102);
        imgic2104 = getIcon(icon2104);
        imgic2106 = getIcon(icon2106);
        imgic2108 = getIcon(icon2108);
        imgic2110 = getIcon(icon2110);
        imgic2111 = getIcon(icon2111); //Icon created by Solange
		
		// NC
		imgic3000 = getIcon(icon3000);
		
		// AVATAR
		//imgic3000 = getIcon(icon3000);
		imgic5000 = getIcon(icon5000);
		imgic5002 = getIcon(icon5002);
		imgic5004 = getIcon(icon5004);
		imgic5006 = getIcon(icon5006);
		imgic5008 = getIcon(icon5008);
		imgic5010 = getIcon(icon5010);
		imgic5012 = getIcon(icon5012);
		imgic5014 = getIcon(icon5014);
		imgic5016 = getIcon(icon5016);
		imgic5018 = getIcon(icon5018);
		imgic5020 = getIcon(icon5020);
		imgic5022 = getIcon(icon5022);
		imgic5024 = getIcon(icon5024);
		imgic5026 = getIcon(icon5026);
		imgic5028 = getIcon(icon5028);
		imgic5030 = getIcon(icon5030);
		imgic5032 = getIcon(icon5032);
		
		imgic5100 = getIcon(icon5100);
		imgic5102 = getIcon(icon5102);
		if (imgic5100 != null) {
			img5100 = imgic5100.getImage();
		}
		if (imgic5102 != null) {
			img5102 = imgic5102.getImage();
		}
    }
    
} // Class
