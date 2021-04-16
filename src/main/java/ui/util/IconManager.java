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


package ui.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


/**
 * Class IconManager
 * Creation: 15/12/2003
 *
 * @author Ludovic APVRILLE
 * @version 1.1 15/12/2003
 */
public class IconManager {

    public static ImageIcon imgic8, imgic9;
    //Added by Solange
    public static ImageIcon imgic0, imgic1, imgic2;
    public static Image img8, img9;
    public static Image img5072, img5100, img5102, img5200;
    //Added by Solange
    public static Image img0, img1;

    public static ImageIcon imgic13, imgic14, imgic15, imgic16, imgic16_1, imgic17, imgic18, imgic19;
    public static ImageIcon imgic20, imgic21, imgic22, imgic23, imgic24, imgic25, imgic26, imgic27, imgic28, imgic29;
    public static ImageIcon imgic20_proj, imgic21_proj, imgic22_proj, imgic23_proj, imgic22_net, imgic23_net;

    public static ImageIcon imgic30, imgic31, imgic32, imgic33, imgic34, imgic35, imgic36, imgic37, imgic38, imgic39;

    public static ImageIcon imgic40, imgic41, imgic42, imgic43, imgic44, imgic45, imgic46, imgic47, imgic48, imgic49;
    public static ImageIcon imgic50, imgic51, imgic52, imgic53, imgic54, imgic55, imgic56, imgic57, imgic58, imgic59;
    public static ImageIcon imgic52r, imgic53r; // reverse icons
    public static ImageIcon imgic60, imgic61, imgic62, imgic63, imgic64, imgic65, imgic66, imgic68;
    public static ImageIcon imgic70, imgic71, imgic72, imgic73, imgic75, imgic76, imgic77, imgic78, imgic78Big, imgic79, imgic780;
    public static ImageIcon imgic80, imgic82, imgic84, imgic86, imgic88, imgic89, imgic89_z3;
    public static ImageIcon imgic90, imgic92, imgic94, imgic96, imgic98, imgic99;

    public static ImageIcon imgic142;


    // Class diagram
    public static ImageIcon imgic100, imgic101, imgic102, imgic104, imgic106, imgic108;
    public static ImageIcon imgic110, imgic112, imgic114, imgic116, imgic118;
    public static ImageIcon imgic120, imgic122, imgic124, imgic126, imgic128, imgic130;
    public static ImageIcon imgic132, imgic134, imgic136, imgic138, imgic140;
    public static ImageIcon imgic144, imgic146;

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
    public static ImageIcon imgic340, imgic341, imgic342, imgic344;

    //IOD
    public static ImageIcon imgic400, imgic402;

    public static ImageIcon imgic500, imgic502, imgic504, imgic506, imgic508, imgic510;
    public static ImageIcon imgic512, imgic514, imgic516, imgic518, imgic520, imgic522, imgic524;

    public static ImageIcon imgic600, imgic602, imgic604, imgic606, imgic608, imgic610, imgic612, imgic614;

    public static ImageIcon imgic700, imgic701, imgic702;

    public static ImageIcon imgic800, imgic802, imgic804, imgic806, imgic808, imgic810, imgic812;
    public static ImageIcon imgic900, imgic902, imgic904, imgic905, imgic906,  imgic907, imgic908, imgic910, imgic912, imgic914, imgic916, imgic918;
    public static ImageIcon imgic920, imgic922, imgic924, imgic926, imgic928, imgic930;
    public static ImageIcon imgic940, imgic941;
    // Requirement diagrams
    public static ImageIcon imgic1000, imgic1002, imgic1004, imgic1006, imgic1008, imgic1010, imgic1012, imgic1014, imgic1016, imgic1018;
    public static ImageIcon imgic1050, imgic1052, imgic1054, imgic1056, imgic1058, imgic1060;

    // Attack Tree Diagrams
    public static ImageIcon imgic1070, imgic1072, imgic1074, imgic1076, imgic1078;
    public static ImageIcon imgic1080, imgic1082, imgic1084, imgic1086;

    // Fault Tree Diagrams
    public static ImageIcon imgic1400, imgic1402, imgic1404, imgic1406, imgic1408;
    public static ImageIcon imgic1410, imgic1412, imgic1414, imgic1416;
    public static Image img1400, img1402, img1404, img1406, img1408;
    public static Image img1410, img1412, img1414;

    // TMLDD
    public static ImageIcon imgic1100, imgic1102, imgic1104, imgic1106,  imgic1107, imgic1108;
    public static ImageIcon imgic1110, imgic1112, imgic1114, imgic1116, imgic1118;
    public static ImageIcon imgic1120;

    // TML component task diagram
    public static ImageIcon imgic1200, imgic1202, imgic1204, imgic1206, imgic1208;
    public static ImageIcon imgic1210, imgic1212;
    public static ImageIcon imgic1300, imgic1302, imgic1304, imgic1306, imgic1308;
    public static ImageIcon imgic1310, imgic1312, imgic1314, imgic1316, imgic1318;
    public static ImageIcon imgic1320, imgic1322, imgic1324, imgic1326, imgic1328;
    public static ImageIcon imgic1330, imgic1332, imgic1334, imgic1336;


    // SMD diagram
    public static ImageIcon imgic2000, imgic2002, imgic2004, imgic2006, imgic2008, imgic2010, imgic2012, imgic2014,
	imgic2016, imgic2018, imgic2015, imgic2017;

    // CSD diagram
    public static ImageIcon imgic2100, imgic2104, imgic2106, imgic2108, imgic2110;


    // NC
    public static ImageIcon imgic3000;

    // AVATAR
    public static ImageIcon imgic5000, imgic5001, imgic5002, imgic5004, imgic5006, imgic5008;
    public static ImageIcon imgic5010, imgic5012, imgic5014, imgic5016, imgic5018;
    public static ImageIcon imgic5020, imgic5022, imgic5024, imgic5026, imgic5028;
    public static ImageIcon imgic5030, imgic5032, imgic5034, imgic5036, imgic5038;
    public static ImageIcon imgic5040, imgic5042, imgic5044, imgic5046, imgic5048;
    public static ImageIcon imgic5050, imgic5052, imgic5054, imgic5056, imgic5058;
    public static ImageIcon imgic5060, imgic5062, imgic5064, imgic5066, imgic5068;
    public static ImageIcon imgic5070, imgic5072, imgic5074, imgic5076, imgic5078;

    public static ImageIcon imgic5100, imgic5102, imgic5104, imgic5106, imgic5108;
    public static ImageIcon imgic5110, imgic5112, imgic5113, imgic5114, imgic5116;
    public static ImageIcon imgic5200;

    //pragmas
    public static ImageIcon imgic6000, imgic6001, imgic6002, imgic6003, imgic6004, imgic6005;
    public static ImageIcon imgic6006, imgic6007;

    //Security
    public static ImageIcon imgic7000, imgic7001;
    //Safety
    public static ImageIcon imgic7002, imgic7003, imgic7004, imgic7005, imgic7006;
    //Show traces
    public static ImageIcon imgic7007, imgic8030;
    //Attacker Scenarios
    public static ImageIcon imgic7008;
    //UPPAAL Help popup
    public static ImageIcon imgic7009;
    // Delegate ports image removed, by Solange
    //public static ImageIcon imgic2102;

    // Image of the help button for the ports (Solange)
    public static ImageIcon imgic2111; //New icon created by Solange
    
    // SystemC-AMS
    public static ImageIcon imgic8000, imgic8001, imgic8002, imgic8003, imgic8004, imgic8005, imgic8006, imgic8007, imgic8008;

    // ELN
    public static ImageIcon imgic8010, imgic8011, imgic8012, imgic8013, imgic8014, imgic8015, imgic8016, imgic8017, imgic8018, imgic8019, 
    						imgic8020, imgic8021, imgic8022, imgic8023, imgic8024, imgic8025, imgic8026, imgic8027, imgic8028, imgic8029;
    
    //private static String icon7 = "turtle_large.gif";
    private static String ttoolStringIcon = "starting_logo.gif";
    public static ImageIcon ttoolImageIcon;
    public static Image ttoolImage;

    private static String icon2 = "menu.gif";

    private static String icon8 = "turtle_16.gif";
    private static String icon9 = "diplodocus2_16.gif";

    //Added by Solange
    private static String icon0 = "hideifce.gif";
    //Added by Solange, the component icon
    private static String icon1 = "procomp.gif";
    //root for the image of the help icon, created by Solange
    private static String icon2111 = "sole.gif";

    //private static String icon10 = "toolbarButtonGraphics/general/Add24.gif";
    //private static String icon11 = "toolbarButtonGraphics/general/Save16.gif";
    private static String icon12 = "toolbarButtonGraphics/general/Import24.gif";
    private static String icon13 = "toolbarButtonGraphics/general/Information16.gif";

    private static String icon14 = "classdiagram.gif";
    private static String icon15 = "activitydiagram.gif";

    private static String icon16 = "toolbarButtonGraphics/general/Refresh16.gif";
    private static String icon16_1 = "toolbarButtonGraphics/general/Refresh24.gif";

    private static String icon17 = "iod.gif";
    private static String icon18 = "sd.gif";
    private static String icon19 = "ucd.gif";

    private static String icon20 = "New16.gif";
    private static String icon21 = "New24.gif";
    private static String icon20_proj = "NewP16.gif";
    private static String icon21_proj = "NewP24.gif";
    private static String icon22 = "Open16.gif";
    private static String icon23 = "Open24.gif";
    private static String icon22_proj = "OpenP16.gif";
    private static String icon23_proj = "OpenP24.gif";
    private static String icon22_net = "OpenN16.gif";
    private static String icon23_net = "OpenN24.gif";
    private static String icon24 = "toolbarButtonGraphics/general/Save16.gif";
    private static String icon25 = "toolbarButtonGraphics/general/Save24.gif";
    private static String icon26 = "toolbarButtonGraphics/general/Stop16.gif";
    private static String icon27 = "toolbarButtonGraphics/general/Stop24.gif";
    private static String icon28 = "toolbarButtonGraphics/general/History16.gif";
    private static String icon29 = "toolbarButtonGraphics/general/History24.gif";

    private static String icon30 = "toolbarButtonGraphics/general/About16.gif";
    private static String icon31 = "toolbarButtonGraphics/general/About24.gif";

    private static String icon32 = "toolbarButtonGraphics/general/Help16.gif";
    private static String icon33 = "toolbarButtonGraphics/general/Help24.gif";
    private static String icon34 = "genrtlotos.gif";
    private static String icon35 = "genrtlotos.gif";
    private static String icon36 = "checkmodel.gif";
    private static String icon37 = "checkmodel.gif";
    private static String icon38 = "genjava.gif";
    private static String icon39 = "genjava.gif";

    private static String icon40 = "toolbarButtonGraphics/general/Undo16.gif";
    private static String icon41 = "toolbarButtonGraphics/general/Undo24.gif";
    private static String icon42 = "toolbarButtonGraphics/general/Redo16.gif";
    private static String icon43 = "toolbarButtonGraphics/general/Redo24.gif";
    private static String icon44 = "toolbarButtonGraphics/media/Rewind16.gif";
    private static String icon45 = "toolbarButtonGraphics/media/Rewind24.gif";
    private static String icon46 = "toolbarButtonGraphics/media/StepBack16.gif";
    private static String icon47 = "toolbarButtonGraphics/media/StepBack24.gif";
    private static String icon48 = "toolbarButtonGraphics/media/StepForward16.gif";
    private static String icon49 = "toolbarButtonGraphics/media/StepForward24.gif";
    private static String icon50 = "toolbarButtonGraphics/media/FastForward16.gif";
    private static String icon51 = "toolbarButtonGraphics/media/FastForward24.gif";
    private static String icon52 = "toolbarButtonGraphics/media/Play16.gif";
    private static String icon53 = "toolbarButtonGraphics/media/Play24.gif";
    private static String icon52r = "toolbarButtonGraphics/media/Play16Reverse.gif";
    private static String icon53r = "toolbarButtonGraphics/media/Play24Reverse.gif";
    private static String icon54 = "toolbarButtonGraphics/media/Stop16.gif";
    private static String icon55 = "toolbarButtonGraphics/media/Stop24.gif";
    private static String icon56 = "toolbarButtonGraphics/general/Find16.gif";
    private static String icon57 = "toolbarButtonGraphics/general/Find24.gif";
    private static String icon58 = "gendesign.gif";
    private static String icon59 = "gendesign.gif";

    private static String icon60 = "dd.gif";

    private static String icon61 = "gensystc.gif";
    private static String icon62 = "tmldesign.gif";
    private static String icon63 = "tmlactivity.gif";
    private static String icon64 = "genaut.gif";
    private static String icon65 = "concomment.gif";
    private static String icon66 = "gentmltxt.gif";
    private static String icon68 = "gentmlc.gif";

    private static String icon70 = "toolbarButtonGraphics/general/SendMail16.gif";
    private static String icon71 = "toolbarButtonGraphics/general/SendMail24.gif";
    private static String icon72 = "toolbarButtonGraphics/general/Help16.gif";
    private static String icon73 = "toolbarButtonGraphics/general/Help24.gif";
    private static String icon75 = "toolbarButtonGraphics/general/Refresh24.gif";
    private static String icon76 = "toolbarButtonGraphics/general/Preferences16.gif";
    private static String icon77 = "toolbarButtonGraphics/general/Preferences24.gif";
    private static String icon78 = "toolbarButtonGraphics/navigation/Up16.gif";
    private static String icon78Big = "toolbarButtonGraphics/navigation/Up24.gif";
    private static String icon79 = "toolbarButtonGraphics/navigation/Down16.gif";
    private static String icon780 = "toolbarButtonGraphics/navigation/navig.gif";

    private static String icon80 = "avatarbd.gif";
    private static String icon82 = "avatarpd.gif";
    private static String icon84 = "avatarrd.gif";
    private static String icon86 = "avatarfvuppaal.png";
    private static String icon88 = "avatarfvproverif.png";
    private static String icon89 = "dse.png";
    private static String icon89_z3 = "dse_z3.png";
    private static String icon90 = "genlotos.gif";
    private static String icon92 = "genuppaal.gif";
    private static String icon94 = "avatarcodegeneration.gif";
    private static String icon96 = "avatarfvinvariant.png";
    private static String icon98 = "diplomethodo.gif";
    private static String icon99 = "avatarmethodo.gif";

    private static String icon100 = "toolbarButtonGraphics/general/Properties16.gif";
    private static String icon101 = "toolbarButtonGraphics/general/Properties24.gif";
    private static String icon102 = "cdassociation.gif";
    //private static String icon103 = "cdassociation.gif";
    private static String icon104 = "cdtclass.gif";
    //private static String icon105 = "cdtclass.gif";
    private static String icon106 = "cdassonav.gif";
    //private static String icon107 = "cdassonav.gif";
    private static String icon108 = "cdattribute.gif";
    //private static String icon109 = "cdattribute.gif";
    private static String icon110 = "cdsynchro.gif";
    //private static String icon111 = "cdsynchro.gif";
    private static String icon112 = "cdparallel.gif";
    //private static String icon113 = "cdparallel.gif";
    private static String icon114 = "cdsequence.gif";
    //private static String icon115 = "cdsequence.gif";
    private static String icon116 = "cdpreemption.gif";
    //private static String icon117 = "cdpreemption.gif";
    private static String icon118 = "cdnatural.gif";
    //private static String icon119 = "cdnatural.gif";
    private static String icon120 = "cdgates.gif";
    //private static String icon121 = "cdgates.gif";
    private static String icon122 = "cdoperation.gif";
    //private static String icon123 = "cdoperation.gif";
    private static String icon124 = "cdactivity.gif";
    //private static String icon125 = "cdactivity.gif";
    private static String icon126 = "cdinvocation.gif";
    //private static String icon127 = "cdinvocation.gif";
    private static String icon128 = "cdtobject.gif";
    //private static String icon129 = "cdtobject.gif";
    private static String icon130 = "cdtdata.gif";
    private static String icon132 = "cdtoggleatt.gif";
    private static String icon134 = "cdtogglegat.gif";
    private static String icon136 = "cdtogglesyn.gif";
    private static String icon138 = "adtogglejava.gif";
    private static String icon140 = "avatarfv.png";
    private static String icon142 = "toolbarButtonGraphics/navigation/Home16.gif";
    private static String icon144 = "ZoomInFont24.gif";
    private static String icon146 = "ZoomOutFont24.gif";

    private static String icon200 = "toolbarButtonGraphics/general/Properties16.gif";
    private static String icon201 = "toolbarButtonGraphics/general/Properties24.gif";
    private static String icon202 = "adconnector.gif";
    //private static String icon203 = "adconnector.gif";
    private static String icon204 = "actionstate.gif";
    //private static String icon205 = "actionstate.gif";
    private static String icon206 = "adparallel.gif";
    //private static String icon207 = "adparallel.gif";
    private static String icon208 = "adchoice.gif";
    //private static String icon209 = "adchoice.gif";
    private static String icon210 = "adstop.gif";
    //private static String icon211 = "adstop.gif";
    private static String icon212 = "adjunction.gif";
    //private static String icon213 = "adjunction.gif";
    private static String icon214 = "adddelay.gif";
    //private static String icon215 = "adddelay.gif";
    private static String icon216 = "adnddelay.gif";
    //private static String icon217 = "adnddelay.gif";
    private static String icon218 = "adtloffer.gif";
    //private static String icon219 = "adtloffer.gif";
    private static String icon220 = "adtlofferwl.gif";
    //private static String icon221 = "adtlofferwl.gif";
    private static String icon222 = "adstart.gif";
    //private static String icon223 = "adstart.gif";
    private static String icon224 = "adtimeinterval.gif";
    private static String icon226 = "adsequence.gif";
    private static String icon228 = "adpreemption.gif";
    private static String icon230 = "arrayget.gif";
    private static String icon232 = "arrayset.gif";

    private static String icon302 = "text1.gif";
    //private static String icon303 = "text1.gif";
    private static String icon304 = "point.gif";
    //private static String icon305 = "point.gif";
    private static String icon306 = "dta.gif";
    //private static String icon307 = "dta.gif";
    private static String icon308 = "rg.gif";

    private static String icon310 = "formalvalidation.gif";
    //private static String icon311 = "formalvalidation.gif";
    private static String icon312 = "simulation.gif";
    //private static String icon313 = "simulation.gif";

    private static String icon314 = "toolbarButtonGraphics/general/ZoomOut16.gif";
    private static String icon315 = "toolbarButtonGraphics/general/ZoomOut24.gif";
    private static String icon316 = "toolbarButtonGraphics/general/ZoomIn16.gif";
    private static String icon317 = "toolbarButtonGraphics/general/ZoomIn24.gif";

    private static String icon318 = "viewsimulation.gif";
    private static String icon319 = "viewsimulation.gif";

    private static String icon320 = "toolbarButtonGraphics/general/Edit16.gif";
    private static String icon321 = "toolbarButtonGraphics/general/Edit24.gif";
    private static String icon322 = "toolbarButtonGraphics/general/TipOfTheDay16.gif";
    private static String icon323 = "toolbarButtonGraphics/general/TipOfTheDay24.gif";

    private static String icon324 = "starting_logo.gif";

    private static String icon330 = "toolbarButtonGraphics/general/Cut16.gif";
    private static String icon331 = "toolbarButtonGraphics/general/Cut24.gif";
    private static String icon332 = "toolbarButtonGraphics/general/Copy16.gif";
    private static String icon333 = "toolbarButtonGraphics/general/Copy24.gif";
    private static String icon334 = "toolbarButtonGraphics/general/Paste16.gif";
    private static String icon335 = "toolbarButtonGraphics/general/Paste24.gif";
    private static String icon336 = "toolbarButtonGraphics/general/Delete16.gif";
    private static String icon337 = "toolbarButtonGraphics/general/Delete24.gif";
    private static String icon338 = "toolbarButtonGraphics/general/Import16.gif";
    private static String icon339 = "toolbarButtonGraphics/general/Import24.gif";
    private static String icon340 = "toolbarButtonGraphics/general/Export16.gif";
    private static String icon341 = "toolbarButtonGraphics/general/Export24.gif";

    private static String icon342 = "oneformalvalidation.gif";

    private static String icon344 = "viewrg.gif";


    // IOD
    private static String icon400 = "referencetosd.gif";
    private static String icon402 = "iodpreemption.gif";

    // SD
    private static String icon500 = "sdinstance.gif";
    private static String icon502 = "sdsynchronous.gif";
    private static String icon504 = "sdasynchronous.gif";
    private static String icon506 = "sdabsolutetc.gif";
    private static String icon508 = "sdrelativetc1.gif";
    private static String icon510 = "sdrelativetc2.gif";
    private static String icon512 = "actionstate.gif";
    private static String icon514 = "sdtimersetting.gif";
    private static String icon516 = "sdtimerexpire.gif";
    private static String icon518 = "sdtimercancel.gif";
    private static String icon520 = "sdcoregion.gif";
    private static String icon522 = "sdaligninstances.gif";
    private static String icon524 = "sdguard.gif";

    // UCD
    private static String icon600 = "ucdactor.gif";
    private static String icon602 = "ucdusecase.gif";
    private static String icon604 = "ucdconnact.gif";
    private static String icon606 = "ucdinclude.gif";
    private static String icon608 = "ucdextend.gif";
    private static String icon610 = "ucdspecia.gif";
    private static String icon612 = "ucdborder.gif";
    private static String icon614 = "ucdactorbox.gif";

    // Deployment diagrams
    private static String icon700 = "ddnode.gif";
    private static String icon701 = "ddnode.gif";
    private static String icon702 = "ddartifact.gif";


    // TML
    private static String icon800 = "tmlcdevt.gif";
    private static String icon802 = "tmlcdcha.gif";
    private static String icon804 = "tmlcdreq.gif";
    private static String icon806 = "tmltask.gif";
    private static String icon808 = "tmltogglecha.gif";
    private static String icon810 = "tmltoggleevt.gif";
    private static String icon812 = "tmltogglereq.gif";

    private static String icon900 = "tmladwrch.gif";
    private static String icon902 = "tmladsendreq.gif";
    private static String icon904 = "tmladsendevt.gif";
    private static String icon905 = "tmladwritecams.gif";
    private static String icon906 = "tmladrdch.gif";
    private static String icon907 = "tmladreadcams.gif";
    private static String icon908 = "tmladwaitevt.gif";
    private static String icon910 = "tmladexeci.gif";
    private static String icon912 = "tmladforloop.gif";
    private static String icon914 = "tmladexeciint.gif";
    private static String icon916 = "tmladselectevt.gif";
    private static String icon918 = "tmladnotifiedevt.gif";
    private static String icon920 = "tmladexecc.gif";
    private static String icon922 = "tmladexeccint.gif";
    private static String icon924 = "tmladrandom.gif";
    private static String icon926 = "tmladforeverloop.gif";
    private static String icon928 = "tmladunorderedsequence.gif";
    private static String icon930 = "tmladreadrequestarg.gif";
    private static String icon940 = "tmladencrypt.gif";
    private static String icon941 = "tmladdecrypt.gif";

    // Requirement diagrams
    private static String icon1000 = "reqdiag.gif";
    private static String icon1002 = "reqreq.gif";
    private static String icon1004 = "reqobs.gif";
    private static String icon1006 = "reqver.gif";
    private static String icon1008 = "reqder.gif";
    private static String icon1010 = "reqcopy.gif";
    private static String icon1012 = "reqcomp.gif";
    private static String icon1014 = "ebrddobs.gif";
    private static String icon1016 = "reqref.gif";
    private static String icon1018 = "reqsatisfy.gif";


    // EBRDD
    private static String icon1050 = "ebrdderc.gif";
    private static String icon1052 = "ercconnector.gif";
    private static String icon1054 = "ebrddeso.gif";
    private static String icon1056 = "ebrdderb.gif";
    private static String icon1058 = "ebrdd.gif";
    private static String icon1060 = "ebrddvar.gif";

    // Attack Tree Diagrams
    private static String icon1070 = "atdblock.gif";
    private static String icon1072 = "atdattack.gif";
    private static String icon1074 = "atdiag.gif";
    private static String icon1076 = "atdcomp.gif";
    private static String icon1078 = "atdcons.gif";
    private static String icon1080 = "atdlink.gif";
    private static String icon1082 = "atdcountermeasure.gif";
    private static String icon1084 = "atdcountermeasureconnector.gif";
    private static String icon1086 = "atdattackerpopulation.gif";

    // Fault trees
    private static String icon1400 = "AndFT.png";
    private static String icon1402 = "SequenceFT.png";
    private static String icon1404 = "AfterFT.png";
    private static String icon1406 = "BeforeFT.png";
    private static String icon1408 = "NotFT.png";
    private static String icon1410 = "OrFT.png";
    private static String icon1412 = "XorFT.png";
    private static String icon1414 = "VoteFT.png";
    private static String icon1416 = "ftdfault.gif";

    // DIPLODOCUS architecture
    private static String icon1100 = "tmlcpunode.gif";
    private static String icon1102 = "tmlbusnode.gif";
    private static String icon1104 = "tmlbridgenode.gif";
    private static String icon1106 = "tmlhwanode.gif";
    private static String icon1107 = "tmlcamsnode.gif";
    private static String icon1108 = "tmlmemorynode.gif";
    private static String icon1110 = "tmldmanode.gif";
    private static String icon1118 = "tmlkey.gif";
    private static String icon1120 = "tmlfpganode.gif";


    // For ADD
    private static String icon1112 = "tmlicunode.gif";
    private static String icon1114 = "tmlcopromwmrnode.gif";
    private static String icon1116 = "tmltimernode.gif";

    private static String icon1200 = "tmlcompcomp.gif";
    private static String icon1202 = "tmlprimcomp.gif";
    private static String icon1204 = "tmlcompport.gif";
    private static String icon1206 = "tmlprimport.gif";
    private static String icon1208 = "tmlcompdiag.gif";
    private static String icon1210 = "tmlcfork.gif";
    private static String icon1212 = "tmlcjoin.gif";

    private static String icon1300 = "stepforwardxtimeunits24.gif";
    private static String icon1302 = "stepforwardtobreakpoint24.gif";
    private static String icon1304 = "stepforwardtotime24.gif";
    private static String icon1306 = "stepforwardxtransactions24.gif";
    private static String icon1308 = "stepforwardxcommands24.gif";
    private static String icon1310 = "savevcd24.gif";
    private static String icon1312 = "savehtml24.gif";
    private static String icon1314 = "savetxt24.gif";
    private static String icon1316 = "steptobus.gif";
    private static String icon1318 = "steptotask.gif";
    private static String icon1320 = "steptocpu.gif";
    private static String icon1322 = "steptomem.gif";
    private static String icon1324 = "steptochan.gif";
    private static String icon1326 = "runexplo.gif";
    private static String icon1328 = "savesvg24.gif";
    private static String icon1330 = "stepforwardx24.gif";
    private static String icon1332 = "savexml24.gif";
    private static String icon1334 = "savecsv24.gif";
    private static String icon1336 = "steptrace24.gif";

    // SMD diagrams
    private static String icon2000 = "prosmdsendmsg.gif";
    private static String icon2002 = "prosmdgetmsg.gif";
    private static String icon2004 = "adchoice.gif";
    private static String icon2006 = "adjonction.gif"; //junction
    private static String icon2008 = "prosmdsubmachine1.gif"; //
    private static String icon2010 = "actionstate.gif";
    private static String icon2012 = "prosmdstate.gif";
    private static String icon2014 = "sendsignal.gif";
    private static String icon2016 = "receivesignal.gif";
    private static String icon2015 = "sendsignalams.gif";
    private static String icon2017 = "receivesignalams.gif";
    private static String icon2018 = "functioncall.gif";

    //CSD diagrams
    private static String icon2100 = "procsdcomponent.gif";
    //Delegate ports image removed, by Solange
    //private static String icon2102 = "adport.gif";
    private static String icon2104 = "cdtclass.gif";
    private static String icon2106 = "proCSDInPort.gif";
    private static String icon2108 = "proCSDOutPort.gif";
    private static String icon2110 = "ProCSDConector.gif";

    private static String icon3000 = "nc.gif";


    // AVATAR
    public static final int iconSize = 15;

    private static String icon5000 = "avatarblock.gif";
    private static String icon5001 = "avatarinterface.gif";
    private static String icon5002 = "avatarbdcomp.gif";
    private static String icon5004 = "avatarbdlink.gif";
    private static String icon5006 = "avatarrdreq.gif";
    private static String icon5008 = "avatarrdprop.gif";
    private static String icon5010 = "avatarpdattribute.gif";
    private static String icon5012 = "avatarpdsetting.gif";
    private static String icon5014 = "avatarpdbooleq.gif";
    private static String icon5016 = "avatarpdattributeconnector.gif";
    private static String icon5018 = "avatarpdsignalconnector.gif";
    private static String icon5020 = "avatarpdpropertyconnector.gif";
    private static String icon5022 = "avatarpdsignal.gif";
    private static String icon5024 = "avatarpdpropertyrelation.gif";
    private static String icon5026 = "avatarpdproperty.gif";
    private static String icon5028 = "avatarpdlogicalconstraint.gif";
    private static String icon5030 = "avatarpdtemporalconstraint.gif";
    private static String icon5032 = "avatarpdalias.gif";
    private static String icon5034 = "avatardatatype.gif";
    private static String icon5036 = "avatarstate.gif";
    private static String icon5038 = "avatarsettimer.gif";
    private static String icon5040 = "avatarresettimer.gif";
    private static String icon5042 = "avatarexpiretimer.gif";
    private static String icon5044 = "avatarcdassoc.gif";
    private static String icon5046 = "adstopflow.gif";
    private static String icon5048 = "avataractivity.gif";
    private static String icon5050 = "avatarsendsignal.gif";
    private static String icon5052 = "avatarpartition.gif";
    private static String icon5054 = "alignpartition.gif";
    private static String icon5056 = "avatarreceivesignal.gif";
    private static String icon5058 = "avatarmaddiag.gif";
    private static String icon5060 = "avatarmadaspt.gif";
    private static String icon5062 = "avatarmadeltref.gif";
    private static String icon5064 = "avatarmadversioning.gif";
    private static String icon5066 = "avatarmadimpact.gif";
    private static String icon5068 = "avatarrdref.gif";
    private static String icon5070 = "avatarlibfunction.gif";
    private static String icon5072 = "scissors.png";
    private static String icon5074 = "avatarrdreqref.gif";
    private static String icon5076 = "avataramsinterface.gif";
    private static String icon5078 = "avatarbdaggre.gif";

    private static String icon5100 = "avatarhead16.gif";
    private static String icon5102 = "avatarhead32.gif";


    private static String icon5104 = "savepng24.gif";

    private static String icon5106 = "attribute.gif";
    private static String icon5108 = "method.gif";
    private static String icon5110 = "invariant.gif";
    private static String icon5112 = "invariantbig.gif";
    private static String icon5113 = "toolbarButtonGraphics/general/Search16_E.gif";
    private static String icon5114 = "toolbarButtonGraphics/general/Search16_I.gif";
    private static String icon5116 = "daemon16.png";

    // Avatar DD
    private static String icon5200 = "search-ideogram-25px.png";

    //Dfferent Pragma
    private static String icon6000 = "toolbarButtonGraphics/general/pragma16.gif";
    private static String icon6001 = "toolbarButtonGraphics/general/pragma24.gif";
    private static String icon6002 = "toolbarButtonGraphics/general/safepragma16.gif";
    private static String icon6003 = "toolbarButtonGraphics/general/safepragma24.gif";
    private static String icon6004 = "toolbarButtonGraphics/general/performancepragma16.gif";
    private static String icon6005 = "toolbarButtonGraphics/general/performancepragma24.gif";
    private static String icon6006 = "toolbarButtonGraphics/general/tmlpragma16.gif";
    private static String icon6007 = "toolbarButtonGraphics/general/tmlpragma24.gif";

    //Security stuff
    private static String icon7000 = "tmlcrypto.gif";
    private static String icon7001 = "tmlfirewallnode.gif";
    private static String icon7002 = "A1.gif";
    private static String icon7003 = "A2.gif";
    private static String icon7004 = "E1.gif";
    private static String icon7005 = "E2.gif";
    private static String icon7006 = "imply.gif";
    //Show traces
    private static String icon7007 = "toolbarButtonGraphics/general/showTrace.gif";

    //Attacker Scenarios
    private static String icon7008 = "attacker.gif";
    private static String icon7009 = "uppaal.gif";

    // SystemC-AMS
    private static String icon8000 = "camstdfport.gif";
    private static String icon8001 = "camsdeport.gif";
    private static String icon8002 = "camsconverterport.gif";
    private static String icon8003 = "camsclusterblock.gif";
    private static String icon8004 = "camstdfblock.gif";
    private static String icon8005 = "camsdeblock.gif";
    private static String icon8006 = "camsgpio2vciblock.gif";
    private static String icon8007 = "clocksignal.gif";
    private static String icon8008 = "blockwithclocksignal.gif";
    // ELN
    private static String icon8010 = "elnresistor.gif";
    private static String icon8011 = "elncapacitor.gif";
    private static String icon8012 = "elninductor.gif";
    private static String icon8013 = "elnvcvs.gif";
    private static String icon8014 = "elnvccs.gif";
    private static String icon8015 = "elnidealtransformer.gif";
    private static String icon8016 = "elntransmissionline.gif";
    private static String icon8017 = "elnvsource.gif";
    private static String icon8018 = "elnisource.gif";
    private static String icon8019 = "elnground.gif";
    private static String icon8020 = "elntdfvsink.gif";
    private static String icon8021 = "elntdfisink.gif";
    private static String icon8022 = "elnterminal.gif";
    private static String icon8023 = "elntdfvsource.gif";
    private static String icon8024 = "elntdfisource.gif";
    private static String icon8025 = "elnmodule.gif";
    private static String icon8026 = "elndeisink.gif";
    private static String icon8027 = "elndeisource.gif";
    private static String icon8028 = "elndevsink.gif";
    private static String icon8029 = "elndevsource.gif";
    private static String icon8030 = "timeline.png";
    
    public IconManager() {


    }

    public static ImageIcon getIcon(String s) {
        URL url = IconManager.class.getResource(s);

        if (url != null) {
            return new ImageIcon(url);
        }

        return null;
    }

    public static void checkIcons() {
        ImageIcon imc = new ImageIcon(IconManager.icon12);
        Image im = imc.getImage();
        if (im == null) {
        
        } else {
        
        }
    }

    public static void loadImg() {
    
    	ttoolImageIcon = getIcon(ttoolStringIcon);
        ttoolImage = ttoolImageIcon.getImage();
    
        imgic8 = getIcon(icon8);
        imgic9 = getIcon(icon9);
        if (imgic8 != null) {
            img8 = imgic8.getImage();
        }
        
        // Issue #31: Need to check imgic9 not img9!
        if ( imgic9 == null) {
//        if (img9 != null) {
            img9 = imgic9.getImage();
        }

        //Added by Solange
        imgic0 = getIcon(icon0);
        //Added by Solange, the component icon
        imgic1 = getIcon(icon1);

        //Added by Solange
        if ( imgic0 != null ) {
        	img0 = imgic0.getImage();
        }

        if ( imgic1 != null ) {
        	img1 = imgic1.getImage();
        }

        imgic2 = getIcon(icon2);

        imgic13 = getIcon(icon13);
        imgic14 = getIcon(icon14);
        imgic15 = getIcon(icon15);
        imgic16 = getIcon(icon16);
        imgic16_1 = getIcon(icon16_1);
        imgic17 = getIcon(icon17);
        imgic18 = getIcon(icon18);
        imgic19 = getIcon(icon19);

        imgic20 = getIcon(icon20);
        imgic21 = getIcon(icon21);
        imgic20_proj = getIcon(icon20_proj);
        imgic21_proj = getIcon(icon21_proj);
        imgic22 = getIcon(icon22);
        imgic23 = getIcon(icon23);
        imgic22_proj = getIcon(icon22_proj);
        imgic23_proj = getIcon(icon23_proj);
        imgic22_net = getIcon(icon22_net);
        imgic23_net = getIcon(icon23_net);
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
        imgic52r = getIcon(icon52r);
        imgic53r = getIcon(icon53r);
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
        imgic68 = getIcon(icon68);

        imgic70 = getIcon(icon70);
        imgic71 = getIcon(icon71);
        imgic72 = getIcon(icon72);
        imgic73 = getIcon(icon73);
        imgic75 = getIcon(icon75);
        imgic76 = getIcon(icon76);
        imgic77 = getIcon(icon77);
        imgic78 = getIcon(icon78);
        imgic78Big = getIcon(icon78Big);
        imgic79 = getIcon(icon79);
        imgic780 = getIcon(icon780);

        imgic80 = getIcon(icon80);
        imgic82 = getIcon(icon82);
        imgic84 = getIcon(icon84);
        imgic86 = getIcon(icon86);
        imgic88 = getIcon(icon88);
        imgic89 = getIcon(icon89);
        imgic89_z3 = getIcon(icon89_z3);
        imgic90 = getIcon(icon90);
        imgic92 = getIcon(icon92);
        imgic94 = getIcon(icon94);
        imgic96 = getIcon(icon96);
        imgic98 = getIcon(icon98);
        imgic99 = getIcon(icon99);

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
        imgic140 = getIcon(icon140);
        imgic142 = getIcon(icon142);
        imgic144 = getIcon(icon144);
        imgic146 = getIcon(icon146);

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
        imgic344 = getIcon(icon344);

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
        imgic614 = getIcon(icon614);

        imgic700 = getIcon(icon700);
	imgic701 = getIcon(icon701);
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
	imgic905 = getIcon(icon905);
        imgic906 = getIcon(icon906);
	imgic907 = getIcon(icon907);
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
        imgic930 = getIcon(icon930);
        imgic940 = getIcon(icon940);
        imgic941 = getIcon(icon941);

        imgic1000 = getIcon(icon1000);
        imgic1002 = getIcon(icon1002);
        imgic1004 = getIcon(icon1004);
        imgic1006 = getIcon(icon1006);
        imgic1008 = getIcon(icon1008);
        imgic1010 = getIcon(icon1010);
        imgic1012 = getIcon(icon1012);
        imgic1014 = getIcon(icon1014);
        imgic1016 = getIcon(icon1016);
        imgic1018 = getIcon(icon1018);

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
        imgic1082 = getIcon(icon1082);
        imgic1084 = getIcon(icon1084);
        imgic1086 = getIcon(icon1086);

        imgic1400 = getIcon(icon1400);
        if (imgic1400 != null) {
            img1400 = imgic1400.getImage();
        }
        imgic1402 = getIcon(icon1402);
        if (imgic1402 != null) {
            img1402 = imgic1402.getImage();
        }
        imgic1404 = getIcon(icon1404);
        if (imgic1404 != null) {
            img1404 = imgic1404.getImage();
        }
        imgic1408 = getIcon(icon1408);
        if (imgic1408 != null) {
            img1408 = imgic1408.getImage();
        }
        imgic1406 = getIcon(icon1406);
        if (imgic1406 != null) {
            img1406 = imgic1406.getImage();
        }
        imgic1410 = getIcon(icon1410);
        if (imgic1410 != null) {
            img1410 = imgic1410.getImage();
        }
        imgic1412 = getIcon(icon1412);
        if (imgic1412 != null) {
            img1412 = imgic1412.getImage();
        }
        imgic1414 = getIcon(icon1414);
        if (imgic1414 != null) {
            img1414 = imgic1414.getImage();
        }
        imgic1416 = getIcon(icon1416);

        imgic1100 = getIcon(icon1100);
        imgic1102 = getIcon(icon1102);
        imgic1104 = getIcon(icon1104);
        imgic1106 = getIcon(icon1106);
        imgic1108 = getIcon(icon1108);
        imgic1110 = getIcon(icon1110);
        imgic1112 = getIcon(icon1112);
        imgic1114 = getIcon(icon1114);
        imgic1116 = getIcon(icon1116);
        imgic1118 = getIcon(icon1118);
        imgic1120 = getIcon(icon1120);

        imgic1200 = getIcon(icon1200);
        imgic1202 = getIcon(icon1202);
        imgic1204 = getIcon(icon1204);
        imgic1206 = getIcon(icon1206);
        imgic1208 = getIcon(icon1208);
        imgic1210 = getIcon(icon1210);
        imgic1212 = getIcon(icon1212);

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
        imgic1328 = getIcon(icon1328);
        imgic1330 = getIcon(icon1330);
        imgic1332 = getIcon(icon1332);
        imgic1334 = getIcon(icon1334);
        imgic1336 = getIcon(icon1336);

        imgic2000 = getIcon(icon2000);
        imgic2002 = getIcon(icon2002);
        imgic2004 = getIcon(icon2004);
        imgic2006 = getIcon(icon2006);
        imgic2008 = getIcon(icon2008);
        imgic2010 = getIcon(icon2010);
        imgic2012 = getIcon(icon2012);
        imgic2014 = getIcon(icon2014);
        imgic2016 = getIcon(icon2016);
        imgic2018 = getIcon(icon2018);
	    imgic2015 = getIcon(icon2015);
        imgic2017 = getIcon(icon2017);
	
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
	    imgic5001 = getIcon(icon5001);
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
        imgic5034 = getIcon(icon5034);
        imgic5036 = getIcon(icon5036);
        imgic5038 = getIcon(icon5038);
        imgic5040 = getIcon(icon5040);
        imgic5042 = getIcon(icon5042);
        imgic5044 = getIcon(icon5044);
        imgic5046 = getIcon(icon5046);
        imgic5048 = getIcon(icon5048);
        imgic5050 = getIcon(icon5050);
        imgic5052 = getIcon(icon5052);
        imgic5054 = getIcon(icon5054);
        imgic5056 = getIcon(icon5056);
        imgic5058 = getIcon(icon5058);
        imgic5060 = getIcon(icon5060);
        imgic5062 = getIcon(icon5062);
        imgic5064 = getIcon(icon5064);
        imgic5066 = getIcon(icon5066);
        imgic5068 = getIcon(icon5068);
        imgic5070 = getIcon(icon5070);
        imgic5072 = getIcon(icon5072);
        if (imgic5072 != null) {
            img5072 = imgic5072.getImage();
        }
        imgic5074 = getIcon(icon5074);
        imgic5076 = getIcon(icon5076);
        imgic5078 = getIcon(icon5078);

        imgic5100 = getIcon(icon5100);
        imgic5102 = getIcon(icon5102);
        imgic5104 = getIcon(icon5104);
        imgic5106 = getIcon(icon5106);
        imgic5108 = getIcon(icon5108);
        if (imgic5100 != null) {
            img5100 = imgic5100.getImage();
        }
        if (imgic5102 != null) {
            img5102 = imgic5102.getImage();
        }

        imgic5110 = getIcon(icon5110);
        imgic5112 = getIcon(icon5112);
        imgic5113 = getIcon(icon5113);
        imgic5114 = getIcon(icon5114);
        imgic5116 = getIcon(icon5116);

        imgic5200 = getIcon(icon5200);
        if (imgic5200 != null) {
            img5200 = imgic5200.getImage();
        }

        imgic6000 = getIcon(icon6000);
        imgic6001 = getIcon(icon6001);
        imgic6002 = getIcon(icon6002);
        imgic6003 = getIcon(icon6003);
        imgic6004 = getIcon(icon6004);
        imgic6005 = getIcon(icon6005);
        imgic6006 = getIcon(icon6006);
        imgic6007 = getIcon(icon6007);


        imgic7000 = getIcon(icon7000);
        imgic7001 = getIcon(icon7001);
        imgic7002 = getIcon(icon7002);
        imgic7003 = getIcon(icon7003);
        imgic7004 = getIcon(icon7004);
        imgic7005 = getIcon(icon7005);
        imgic7006 = getIcon(icon7006);
        imgic7007 = getIcon(icon7007);
        imgic7008 = getIcon(icon7008);
        imgic7009 = getIcon(icon7009);
        
        imgic8000 = getIcon(icon8000);
        imgic8001 = getIcon(icon8001);
        imgic8002 = getIcon(icon8002);
        imgic8003 = getIcon(icon8003);
        imgic8004 = getIcon(icon8004);
        imgic8005 = getIcon(icon8005);
        imgic8006 = getIcon(icon8006);
	imgic8007 = getIcon(icon8007);

        imgic8010 = getIcon(icon8010);
        imgic8011 = getIcon(icon8011);
        imgic8012 = getIcon(icon8012);
        imgic8013 = getIcon(icon8013);
        imgic8014 = getIcon(icon8014);
        imgic8015 = getIcon(icon8015);
        imgic8016 = getIcon(icon8016);
        imgic8017 = getIcon(icon8017);
        imgic8018 = getIcon(icon8018);
        imgic8019 = getIcon(icon8019);
        imgic8020 = getIcon(icon8020);
        imgic8021 = getIcon(icon8021);
        imgic8022 = getIcon(icon8022);
        imgic8023 = getIcon(icon8023);
        imgic8024 = getIcon(icon8024);
        imgic8025 = getIcon(icon8025);
        imgic8026 = getIcon(icon8026);
        imgic8027 = getIcon(icon8027);
        imgic8028 = getIcon(icon8028);
        imgic8029 = getIcon(icon8029);
        imgic8030 = getIcon(icon8030);
    }

} // Class
