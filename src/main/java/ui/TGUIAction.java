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
 * "http://www.cecil.info".
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
import ui.util.IconManager;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import myutil.*;


/**
 * Class TGUIAction
 *
 * Creation: 21/12/2003
 * @version 1.1 11/07/2006
 * @author Ludovic APVRILLE, Emil Salageanu
 * @see TGComponent
 */
public class TGUIAction extends AbstractAction {

    private static final long serialVersionUID = -4942930048930203001L;

    // Actions
    public static final int ACT_NEW = 0;
    public static final int ACT_NEW_PROJECT = 471;
    public static final int ACT_NEW_DESIGN = 100;
    public static final int ACT_NEW_ANALYSIS = 101;
    public static final int ACT_OPEN = 1;
    public static final int ACT_OPEN_PROJECT = 472;
    public static final int ACT_OPEN_FROM_NETWORK = 455;
    public static final int ACT_OPEN_LAST = 154;
    public static final int ACT_MERGE = 228;
    public static final int ACT_SAVE = 2;

    public static final int ACT_SAVE_AS_PROJECT = 31;
    public static final int ACT_SAVE_AS_MODEL = 475;
    public static final int ACT_SAVE_TIF = 213;
    public static final int ACT_OPEN_TIF = 214;
    public static final int ACT_OPEN_SD = 268;
    public static final int ACT_QUIT = 3;

    public static final int ACT_SAVE_LOTOS = 64;
    public static final int ACT_SAVE_DTA = 65;
    public static final int ACT_SAVE_RG = 66;
    public static final int ACT_SAVE_TLSA = 74;
    public static final int ACT_SAVE_AUT = 67;
    public static final int ACT_SAVE_AUTPROJ = 68;
    public static final int ACT_SAVE_AUTMODIFIED = 232;

    public static final int ACT_TURTLE_WEBSITE = 117;
    public static final int ACT_TURTLE_DOCUMENTATION = 118;
    public static final int ACT_DIPLODOCUS_DOCUMENTATION = 242;
    public static final int ACT_SYSMLSEC_DOCUMENTATION = 426;
    public static final int ACT_ABOUT = 4;
    public static final int ACT_ENHANCE = 160;
    public static final int ACT_TTOOL_CONFIGURATION = 323;


    public static final int TCD_EDIT = 5;
    public static final int TCD_NEW_TCLASS = 6;
    public static final int TCD_NEW_TOBJECT = 50;
    public static final int TCD_NEW_TDATA = 111;
    public static final int TCD_ASSOCIATION = 7;
    public static final int AD_EDIT = 8;
    public static final int AD_CONNECTOR = 9;
    public static final int AD_ACTION_STATE = 10;
    public static final int AD_PARALLEL = 11;
    public static final int AD_SEQUENCE = 51;
    public static final int AD_PREEMPTION = 52;
    public static final int AD_CHOICE = 12;
    public static final int AD_STOP = 13;
    public static final int AD_JUNCTION = 14;
    public static final int AD_DETERMINISTIC_DELAY = 15;
    public static final int AD_NON_DETERMINISTIC_DELAY = 16;
    public static final int AD_DELAY_NON_DETERMINISTIC_DELAY = 49;
    public static final int AD_TIME_LIMITED_OFFER = 17;
    public static final int AD_TIME_LIMITED_OFFER_WITH_LATENCY = 18;
    public static final int AD_TIME_CAPTURE = 269;
    public static final int TCD_PARALLEL_OPERATOR = 19;
    public static final int TCD_CONNECTOR_ATTRIBUTE = 20;
    public static final int TCD_SEQUENCE_OPERATOR = 21;
    public static final int TCD_PREEMPTION_OPERATOR = 22;
    public static final int TCD_SYNCHRO_OPERATOR = 23;
    public static final int TCD_INVOCATION_OPERATOR = 48;
    public static final int TCD_ASSOCIATION_NAVIGATION = 24;
    public static final int AD_START = 43;
    public static final int AD_ARRAY_GET = 261;
    public static final int AD_ARRAY_SET = 262;

    public static final int IOD_EDIT = 77;
    public static final int IOD_CHOICE = 78;
    public static final int IOD_START = 79;
    public static final int IOD_STOP = 80;
    public static final int IOD_PARALLEL = 81;
    public static final int IOD_PREEMPTION = 98;
    public static final int IOD_SEQUENCE = 159;
    public static final int IOD_JUNCTION = 82;
    public static final int IOD_CONNECTOR = 83;
    public static final int IOD_REF_SD = 84;
    public static final int IOD_REF_IOD = 158;

    public static final int SD_EDIT = 85;
    public static final int SD_INSTANCE = 86;
    public static final int SD_CONNECTOR_MESSAGE_ASYNC = 87;
    public static final int SD_CONNECTOR_MESSAGE_SYNC = 88;
    public static final int SD_ABSOLUTE_TIME_CONSTRAINT = 89;
    public static final int SD_TIME_INTERVAL = 188;
    public static final int SD_RELATIVE_TIME_CONSTRAINT = 90;
    public static final int SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR = 91;
    public static final int SD_ACTION_STATE = 92;
    public static final int SD_GUARD = 212;
    public static final int SD_TIMER_SETTING = 93;
    public static final int SD_TIMER_EXPIRATION = 94;
    public static final int SD_TIMER_CANCELLATION = 95;
    public static final int SD_COREGION = 96;
    public static final int SD_ALIGN_INSTANCES = 97;

    public static final int SDZV_EDIT = 440;
    public static final int SDZV_INSTANCE = 441;
    public static final int SDZV_CONNECTOR_MESSAGE_ASYNC = 442;
    public static final int SDZV_CONNECTOR_MESSAGE_SYNC = 443;
    public static final int SDZV_ABSOLUTE_TIME_CONSTRAINT = 444;
    public static final int SDZV_TIME_INTERVAL = 445;
    public static final int SDZV_RELATIVE_TIME_CONSTRAINT = 446;
    public static final int SDZV_RELATIVE_TIME_CONSTRAINT_CONNECTOR = 447;
    public static final int SDZV_ACTION_STATE = 448;
    public static final int SDZV_GUARD = 449;
    public static final int SDZV_TIMER_SETTING = 450;
    public static final int SDZV_TIMER_EXPIRATION = 451;
    public static final int SDZV_TIMER_CANCELLATION = 452;
    public static final int SDZV_COREGION = 453;
    public static final int SDZV_ALIGN_INSTANCES = 454;


    public static final int UCD_EDIT = 104;
    public static final int UCD_ACTOR = 105;
    public static final int UCD_ACTORBOX = 333;
    public static final int UCD_USECASE = 106;
    public static final int UCD_CONNECTOR_ACTOR_UC = 107;
    public static final int UCD_CONNECTOR_INCLUDE = 108;
    public static final int UCD_CONNECTOR_EXTEND = 109;
    public static final int UCD_CONNECTOR_SPECIA = 110;
    public static final int UCD_BORDER = 161;

    public static final int TMLTD_EDIT = 127;
    public static final int TMLTD_TASK = 128;
    public static final int TMLTD_CHANNEL = 129;
    public static final int TMLTD_REQ = 130;
    public static final int TMLTD_EVENT = 131;
    public static final int TMLTD_CONNECTOR = 132;
    public static final int TMLTD_ASSOC = 133;

    public static final int TMLCTD_EDIT = 233;
    public static final int TMLCTD_CCOMPONENT = 234;
    public static final int TMLCTD_CREMOTECOMPONENT = 246;
    public static final int TMLCTD_CPORT = 237;
    public static final int TMLCTD_PORT_CONNECTOR = 238;
    public static final int TMLCTD_PCOMPONENT = 239;
    public static final int TMLCTD_COPORT = 240;
    public static final int TMLCTD_RCOMPONENT = 330;
    public static final int TMLCTD_JOIN = 393;
    public static final int TMLCTD_FORK = 394;


    public static final int TMLAD_EDIT = 134;
    public static final int TMLAD_CONNECTOR = 135;
    public static final int TMLAD_ACTION_STATE = 136;
    public static final int TMLAD_CHOICE = 137;
    public static final int TMLAD_STOP = 138;
    public static final int TMLAD_START = 139;
    public static final int TMLAD_JUNCTION = 140;
    public static final int TMLAD_EXECI = 141;
    public static final int TMLAD_EXECI_INTERVAL = 152;
    public static final int TMLAD_EXECC = 243;
    public static final int TMLAD_EXECC_INTERVAL = 244;
    public static final int TMLAD_DELAY = 248;
    public static final int TMLAD_INTERVAL_DELAY = 249;
    public static final int TMLAD_WRITE_CHANNEL = 142;
    public static final int TMLAD_SEND_REQUEST = 143;
    public static final int TMLAD_SEND_EVENT = 144;
    public static final int TMLAD_WAIT_EVENT = 145;
    public static final int TMLAD_NOTIFIED_EVENT = 203;
    public static final int TMLAD_READ_CHANNEL = 146;
    public static final int TMLAD_FOR_LOOP = 147;
    public static final int TMLAD_FOR_STATIC_LOOP = 226;
    public static final int TMLAD_FOR_EVER_LOOP = 255;
    public static final int TMLAD_SEQUENCE = 156;
    public static final int TMLAD_UNORDERED_SEQUENCE = 322;
    public static final int TMLAD_SELECT_EVT = 206;
    public static final int TMLAD_RANDOM = 245;
    public static final int TMLAD_READ_REQUEST_ARG = 335;

    public static final int TMLAD_ENCRYPT = 430;
    public static final int TMLAD_DECRYPT = 431;

    public static final int TMLARCHI_EDIT = 216;
    public static final int TMLARCHI_LINK = 217;
    public static final int TMLARCHI_CPUNODE = 218;
    public static final int TMLARCHI_BUSNODE = 221;
    public static final int TMLARCHI_CPNODE = 392;
    public static final int TMLARCHI_ARTIFACT = 219;
    public static final int TMLARCHI_COMMUNICATION_ARTIFACT = 222;
    public static final int TMLARCHI_PORT_ARTIFACT = 412;
    public static final int TMLARCHI_EVENT_ARTIFACT = 395;
    public static final int TMLARCHI_HWANODE = 223;
    public static final int TMLARCHI_CAMSNODE = 461;
    public static final int TMLARCHI_MEMORYNODE = 224;
    public static final int TMLARCHI_DMANODE = 364;
    public static final int TMLARCHI_BRIDGENODE = 225;
    public static final int TMLARCHI_KEY = 435;
    public static final int TMLARCHI_FIREWALL = 436;
    public static final int TMLARCHI_FPGANODE = 474;

    public static final int TMLCP_EDIT = 378;
    public static final int TMLCP_CONNECTOR = 379;
    public static final int TMLCP_REF_SD = 380;
    public static final int TMLCP_REF_CP = 381;
    public static final int TMLCP_FORK = 382;
    public static final int TMLCP_JOIN = 383;
    public static final int TMLCP_START = 384;
    public static final int TMLCP_STOP = 385;
    public static final int TMLCP_CHOICE = 386;
    public static final int TMLCP_JUNCTION = 387;
    public static final int TMLCP_FOR_LOOP = 416;

    public static final int TMLSD_STORAGE_INSTANCE = 388;
    public static final int TMLSD_CONTROLLER_INSTANCE = 396;
    public static final int TMLSD_TRANSFER_INSTANCE = 397;
    public static final int TMLSD_ACTION_STATE = 389;
    public static final int TMLSD_MESSAGE_ASYNC = 390;
    public static final int TMLSD_EDIT = 391;

    public static final int CAMS_EDIT = 458;
    public static final int CAMS_BLOCK_TDF = 459;
    public static final int CAMS_CONNECTOR = 460;
    public static final int CAMS_BLOCK_DE = 476;
    public static final int CAMS_PORT_TDF = 477;
    public static final int CAMS_PORT_DE = 478;
    public static final int CAMS_PORT_CONVERTER = 479;
    public static final int CAMS_CLUSTER = 480;

    public static final int EBRDD_EDIT = 271;
    public static final int EBRDD_CONNECTOR = 272;
    public static final int EBRDD_START = 273;
    public static final int EBRDD_STOP = 274;
    public static final int EBRDD_CHOICE = 275;
    public static final int EBRDD_ERC = 276;
    public static final int EBRDD_ACTION = 277;
    public static final int EBRDD_FOR_LOOP = 278;
    public static final int EBRDD_SEQUENCE = 279;
    public static final int EBRDD_ESO = 280;
    public static final int EBRDD_ERB = 281;
    public static final int EBRDD_CONNECTOR_ERC = 282;
    public static final int EBRDD_VARIABLE_DECLARATION = 283;

    // TURTLE-OS
    public static final int TOS_TCLASS = 189;
    public static final int TOS_ASSOCIATION = 190;
    public static final int TOS_ASSOCIATION_NAVIGATION = 191;
    public static final int TOS_CONNECTOR_ATTRIBUTE = 192;
    public static final int TOS_CALL_OPERATOR = 193;
    public static final int TOS_EVT_OPERATOR = 205;

    public static final int TOSAD_ACTION_STATE = 194;
    public static final int TOSAD_CONNECTOR = 195;
    public static final int TOSAD_CHOICE = 196;
    public static final int TOSAD_START_STATE = 197;
    public static final int TOSAD_STOP_STATE = 198;
    public static final int TOSAD_JUNCTION = 199;
    public static final int TOSAD_TIME_INTERVAL = 200;
    public static final int TOSAD_INT_TIME_INTERVAL = 201;

    //ProActive State Machine Diagram
    public static final int PROSMD_EDIT = 168;
    public static final int PROSMD_STOP = 169;
    public static final int PROSMD_START = 170;
    public static final int PROSMD_CONNECTOR = 172;
    public static final int PROSMD_SENDMSG = 173;
    public static final int PROSMD_GETMSG = 175;
    public static final int PROSMD_CHOICE = 176;
    public static final int PROSMD_JUNCTION = 183;
    public static final int PROSMD_SUBMACHINE = 184;
    public static final int PROSMD_ACTION = 185;
    public static final int PROSMD_PARALLEL = 186;
    public static final int PROSMD_STATE = 187;

    public static final int PROCSD_EDIT = 171;
    public static final int PROCSD_COMPONENT = 174;
    //Delegate port action removed, by Solange
    //public static final int PROCSD_DELEGATE_PORT = 177;
    public static final int PROCSD_CONNECTOR = 178;
    public static final int PROCSD_CONNECTOR_DELEGATE = 182;
    public static final int PROCSD_INTERFCE=179;
    public static final int PROCSD_IN_PORT = 180;
    public static final int PROCSD_OUT_PORT = 181;
    public static final int PROCSD_CONNECTOR_PORT_INTERFACE = 209;

    public static final int TDD_EDIT = 119;
    public static final int TDD_LINK = 120;
    public static final int TDD_NODE = 121;
    public static final int TDD_ARTIFACT = 122;

    // NC
    public static final int NCDD_EDIT = 250;
    public static final int NCDD_LINK = 251;
    public static final int NCDD_EQNODE = 252;
    public static final int NCDD_SWITCHNODE = 254;
    public static final int NCDD_TRAFFIC_ARTIFACT = 253;
    public static final int NCDD_ROUTE_ARTIFACT = 256;
    public static final int ACT_NC = 257;

    // SysML Requirements

    public static final int TREQ_REQUIREMENT = 163;
    public static final int TREQ_OBSERVER = 166;
    public static final int TREQ_EBRDD = 270;
    public static final int TREQ_DERIVE = 164;
    public static final int TREQ_VERIFY = 165;
    public static final int TREQ_COPY = 258;
    public static final int TREQ_COMPOSITION = 259;

    public static final int ATD_BLOCK = 284;
    public static final int ATD_ATTACK = 285;
    public static final int ATD_ATTACK_CONNECTOR = 286;
    public static final int ATD_COMPOSITION_CONNECTOR = 288;
    public static final int ATD_CONSTRAINT = 287;
    public static final int ATD_COUNTERMEASURE = 456;
    public static final int ATD_COUNTERMEASURE_CONNECTOR = 457;

    public static final int FTD_BLOCK = 465;
    public static final int FTD_FAULT = 466;
    public static final int FTD_FAULT_CONNECTOR = 467;
    public static final int FTD_COMPOSITION_CONNECTOR = 468;
    public static final int FTD_CONSTRAINT = 469;
    public static final int FTD_COUNTERMEASURE = 470;
    public static final int FTD_COUNTERMEASURE_CONNECTOR = 473;

    // AVATAR Block Diagram
    public static final int ABD_BLOCK = 289;
    public static final int ABD_CRYPTOBLOCK = 332;
    public static final int ABD_DATATYPE = 324;
    public static final int ABD_COMPOSITION_CONNECTOR = 290;
    public static final int ABD_PORT_CONNECTOR = 295;
    public static final int ABD_LIBRARYFUNCTION = 428;
    public static final int ABD_CRYPTOLIBRARYFUNCTION = 429;

    // AVATAR State Machine
    public static final int ASMD_EDIT = 291;
    public static final int ASMD_CONNECTOR = 292;
    public static final int ASMD_START = 293;
    public static final int ASMD_STOP = 294;
    public static final int ASMD_SEND_SIGNAL = 296;
    public static final int ASMD_RECEIVE_SIGNAL = 297;
    public static final int ASMD_LIBRARY_FUNCTION_CALL = 432;
    public static final int ASMD_PARALLEL = 298;
    public static final int ASMD_STATE = 299;
    public static final int ASMD_CHOICE = 325;
    public static final int ASMD_RANDOM = 326;
    public static final int ASMD_SET_TIMER = 327;
    public static final int ASMD_RESET_TIMER = 328;
    public static final int ASMD_EXPIRE_TIMER = 329;

    // AVATAR Requirement
    public static final int ARD_EDIT = 300;
    public static final int ARD_REQUIREMENT = 301;
    public static final int ARD_PROPERTY = 302;
    public static final int ARD_ELEMENT_REFERENCE = 423;
    public static final int ARD_VERIFY_CONNECTOR = 303;
    public static final int ARD_DERIVE_CONNECTOR = 304;
    public static final int ARD_REFINE_CONNECTOR = 343;
    public static final int ARD_SATISFY_CONNECTOR = 422;
    public static final int ARD_COPY_CONNECTOR = 305;
    public static final int ARD_COMPOSITION_CONNECTOR = 306;

    // AVATAR Assumptions
    public static final int AMAD_EDIT = 370;
    public static final int AMAD_ASSUMPTION = 371;
    public static final int AMAD_DIAGRAM_REFERENCE = 372;
    public static final int AMAD_ELEMENT_REFERENCE = 373;
    public static final int AMAD_COMPOSITION_CONNECTOR = 374;
    public static final int AMAD_BELONGSTOCOMPOSITION_CONNECTOR = 417;
    public static final int AMAD_IMPACT_CONNECTOR = 375;
    public static final int AMAD_MEET_CONNECTOR = 377;
    public static final int AMAD_VERSIONING_CONNECTOR = 376;

    //AVATAR Deployment diagram
    public static final int ADD_EDIT = 398;
    public static final int ADD_LINK = 399;
    public static final int ADD_CPUNODE = 400;
    public static final int ADD_BUSNODE = 420;
    public static final int ADD_BLOCKARTIFACT = 402;
    public static final int ADD_TTYNODE = 403;
    public static final int ADD_RAMNODE = 404;
    public static final int ADD_ROMNODE = 405;
    public static final int ADD_DMANODE = 406;
    public static final int ADD_BRIDGENODE = 407;
    public static final int ADD_ICUNODE = 408;
    public static final int ADD_COPROMWMRNODE = 409;
    public static final int ADD_TIMERNODE = 410;
    public static final int ADD_CHANNELARTIFACT = 411;
    public static final int ADD_VGMNNODE = 401;
    public static final int ADD_CROSSBARNODE = 421;

    // -------------------------------------------------------------
    public static final int DEPLOY_AVATAR_DIAGRAM = 418;
    public static final int EXTRAC_DEPLOY_PARAM_TO_FILE = 419;
    // --------------------------------------------------------------------




    // AVATAR Parametric Diagrams
    public static final int APD_EDIT = 307;
    public static final int APD_BLOCK = 308;
    public static final int APD_LOGICAL_CONSTRAINT = 309;
    public static final int APD_TEMPORAL_CONSTRAINT = 310;
    public static final int APD_ATTRIBUTE = 311;
    public static final int APD_SIGNAL = 312;
    public static final int APD_ALIAS = 313;
    public static final int APD_BOOLEQ = 314;
    public static final int APD_ATTRIBUTE_SETTING = 315;
    public static final int APD_PROPERTY = 316;
    public static final int APD_PROPERTY_RELATION = 317;
    public static final int APD_ATTRIBUTE_CONNECTOR = 318;
    public static final int APD_SIGNAL_CONNECTOR = 319;
    public static final int APD_PROPERTY_CONNECTOR = 320;
    public static final int APD_COMPOSITION_CONNECTOR = 321;

    // AVATAR context Diagrams
    public static final int ACD_EDIT = 344;
    public static final int ACD_BLOCK = 345;
    public static final int ACD_COMPOSITION_CONNECTOR = 346;
    public static final int ACD_ACTOR_STICKMAN = 347;
    public static final int ACD_ACTOR_BOX = 348;
    public static final int ACD_ASSOCIATION_CONNECTOR = 349;

    // AVATAR Activity Diagrams
    public static final int AAD_EDIT = 350;
    public static final int AAD_ASSOCIATION_CONNECTOR = 351;
    public static final int AAD_START_STATE = 352;
    public static final int AAD_STOP_STATE = 353;
    public static final int AAD_CHOICE = 354;
    public static final int AAD_JUNCTION = 355;
    public static final int AAD_PARALLEL = 356;
    public static final int AAD_ACTIVITY = 357;
    public static final int AAD_ACTION = 358;
    public static final int AAD_STOP_FLOW = 359;
    public static final int AAD_ACCEPT_EVENT_ACTION = 360;
    public static final int AAD_SEND_SIGNAL_ACTION = 361;
    public static final int AAD_PARTITION = 362;
    public static final int AAD_ALIGN_PARTITION = 363;


    public static final int ACT_MODEL_CHECKING = 25;
    public static final int ACT_GEN_RTLOTOS = 27;
    public static final int ACT_GEN_LOTOS = 155;
    public static final int ACT_GEN_UPPAAL = 204;
    public static final int ACT_AVATAR_MODEL_CHECKER = 433;
    public static final int ACT_GEN_JAVA = 112;
    public static final int ACT_SIMU_JAVA = 167;
    public static final int ACT_GEN_SYSTEMC = 148;
    public static final int ACT_SIMU_SYSTEMC = 263;
    public static final int ACT_GEN_TMLTXT = 215;
    public static final int ACT_GEN_CCODE = 413;
    public static final int ACT_GEN_AUT = 157;
    public static final int ACT_GEN_AUTS = 202;
    public static final int ACT_GEN_DESIGN = 103;
    public static final int ACT_CHECKCODE = 28;
    public static final int ACT_SIMULATION = 29;
    public static final int ACT_VALIDATION = 30;
    public static final int ACT_PROJECTION = 54;
    public static final int ACT_GRAPH_MODIFICATION = 230;
    public static final int ACT_BISIMULATION = 69;
    public static final int ACT_BISIMULATION_CADP = 229;
    public static final int ACT_DEADLOCK_SEEKER_AUT = 75;
    public static final int ACT_DEADLOCK_SEEKER_SAVED_AUT = 76;


    public static final int ACT_ONECLICK_LOTOS_RG = 210;
    public static final int ACT_ONECLICK_RTLOTOS_RG = 211;

    public static final int ACT_VIEW_RTLOTOS = 32;
    public static final int ACT_VIEW_SUGGESTED_DESIGN = 99;

    public static final int ACT_BACKWARD = 33;
    public static final int ACT_FORWARD = 34;

    public static final int ACT_FIRST_DIAG = 35;
    public static final int ACT_BACK_DIAG = 36;
    public static final int ACT_NEXT_DIAG = 37;
    public static final int ACT_LAST_DIAG = 38;

    public static final int ACT_VIEW_JAVA = 113;
    public static final int ACT_VIEW_DTADOT = 39;
    public static final int ACT_VIEW_RGDOT = 40;
    public static final int ACT_VIEW_TLSADOT = 73;
    public static final int ACT_VIEW_RGAUTDOT = 53;
    public static final int ACT_VIEW_STAT_AUT = 70;
    public static final int ACT_VIEW_STAT_AUTDIPLODOCUS = 342;
    public static final int ACT_VIEW_RGAUTPROJDOT = 55;
    public static final int ACT_VIEW_MODIFIEDAUTDOT = 231;
    public static final int ACT_VIEW_STAT_AUTPROJ = 71;
    public static final int ACT_VIEW_SIM = 41;
    public static final int ACT_VIEW_SIM_CHRONO = 102;
    public static final int ACT_VIEW_SAVED_LOT = 62;
    public static final int ACT_VIEW_SAVED_DOT = 63;
    public static final int ACT_VIEW_STAT_SAVED_AUT = 72;
    public static final int ACT_VIEW_BIRDEYES = 115;
    public static final int ACT_VIEW_BIRDEYES_EMB = 116;
    public static final int ACT_VIEW_WAVE = 153;
    public static final int ACT_VIEW_RG_DIPLODOCUS = 341;

    public static final int ACT_VIEW_PM_AUT = 265;
    public static final int ACT_VIEW_PM_AUTPROJ = 266;
    public static final int ACT_VIEW_PM_SAVED_AUT = 267;

    public static final int UML_NOTE = 42;
    public static final int PRAGMA = 424;
    public static final int SAFETY_PRAGMA = 425;
    public static final int PERFORMANCE_PRAGMA = 438;
    public static final int AVATAR_FIREWALL = 437;

    public static final int ACT_CUT = 44;
    public static final int ACT_COPY = 45;
    public static final int ACT_PASTE = 46;
    public static final int ACT_DELETE = 47;
    public static final int ACT_SUPPR = 462;

    public static final int ACT_ZOOM_MORE = 235;
    public static final int ACT_ZOOM_LESS = 236;
    public static final int ACT_SHOW_ZOOM = 241;

    public static final int ACT_IMPORT_LIB = 56;
    public static final int ACT_EXPORT_LIB = 57;

    public static final int ACT_SCREEN_CAPTURE = 58;
    public static final int ACT_TTOOL_WINDOW_CAPTURE = 59;
    public static final int ACT_DIAGRAM_CAPTURE = 60;
    public static final int ACT_SVG_DIAGRAM_CAPTURE = 366;
    public static final int ACT_ALL_DIAGRAM_CAPTURE = 114;
    public static final int ACT_ALL_DIAGRAM_CAPTURE_SVG = 427;
    public static final int ACT_SELECTED_CAPTURE = 61;

    public static final int ACT_GEN_DOC = 247;
    public static final int ACT_GEN_DOC_REQ = 260;

    public static final int ACT_TOGGLE_ATTRIBUTES = 123;
    public static final int ACT_TOGGLE_GATES = 124;
    public static final int ACT_TOGGLE_SYNCHRO = 125;

    public static final int ACT_TOGGLE_CHANNELS = 149;
    public static final int ACT_TOGGLE_EVENTS = 150;
    public static final int ACT_TOGGLE_REQUESTS = 151;

    public static final int ACT_TOGGLE_JAVA = 126;
    public static final int ACT_TOGGLE_ATTR = 220;

    public static final int ACT_TOGGLE_DIPLO_ID = 264;
    public static final int ACT_TOGGLE_AVATAR_ID = 334;
    public static final int ACT_TOGGLE_TEPE_ID = 336;

    public static final int ACT_TOGGLE_INTERNAL_COMMENT = 227;

    public static final int CONNECTOR_COMMENT = 162;

    public static final int EXTERNAL_ACTION_1 = 207;
    public static final int EXTERNAL_ACTION_2 = 208;

    public static final int ACT_AVATAR_SIM = 339;
    public static final int ACT_AVATAR_FV_UPPAAL = 337;
    public static final int ACT_AVATAR_FV_PROVERIF = 338;
    public static final int ACT_AVATAR_FV_STATICANALYSIS = 365;
    public static final int ACT_AVATAR_EXECUTABLE_GENERATION = 340;

    public static final int ACT_DSE = 434;

    // Ontologies
    public static final int ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM = 367;
    public static final int ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS = 369;
    public static final int ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS = 368;

    //Action for the help button created by Solange
    public static final int PRUEBA_1 = 205;

    //Action for the external Search
    //@author Huy TRUONG.
    public static final int ACT_EXTERNAL_SEARCH = 414;
    public static final int ACT_INTERNAL_SEARCH = 415;
    //--

    public static final int MOVE_ENABLED = 463;
    public static final int FIRST_DIAGRAM = 464;
    
    public static final int NB_ACTION = 481;

    private static final TAction [] actions = new TAction[NB_ACTION];

    private EventListenerList listeners;

    public static final String JLF_IMAGE_DIR = "";

    public static final String LARGE_ICON = "LargeIcon";

    public TGUIAction(TAction _t) {
        putValue(Action.ACTION_COMMAND_KEY, _t.ACTION_COMMAND_KEY);
        putValue(Action.NAME, _t.NAME);
        putValue(Action.SMALL_ICON, _t.SMALL_ICON);
        putValue(LARGE_ICON, _t.LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, _t.SHORT_DESCRIPTION);
        putValue(Action.LONG_DESCRIPTION, _t.LONG_DESCRIPTION);
    }

    public TGUIAction(int id, String name) {
        if (actions[0] == null) {
            init();
        }
        if (actions[id] == null) {
            return ;
        }

        putValue(Action.ACTION_COMMAND_KEY, name);

        putValue(Action.NAME, name);
        putValue(Action.SMALL_ICON, actions[id].SMALL_ICON);
        putValue(LARGE_ICON, actions[id].LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, name);
        putValue(Action.LONG_DESCRIPTION, name);
        //putValue(Action.MNEMONIC_KEY, new Integer(actions[id].MNEMONIC_KEY));
        if (actions[id].MNEMONIC_KEY != 0) {
            if (actions[id].hasControl) {
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].KEY, java.awt.event.InputEvent.CTRL_MASK));
            } else {
                if ((actions[id].MNEMONIC_KEY >= 37 && actions[id].MNEMONIC_KEY <= 40) || actions[id].MNEMONIC_KEY == KeyEvent.VK_DELETE) //handling for arrow and delete keys
                    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].MNEMONIC_KEY, 0));
                else
                    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].KEY));
            }
        }

        if ((id == EXTERNAL_ACTION_1) && (ConfigurationTTool.ExternalCommand1 != null) && (ConfigurationTTool.ExternalCommand1.length()) > 0) {
            setLongDescription(EXTERNAL_ACTION_1, ConfigurationTTool.ExternalCommand1);
        }
        if ((id == EXTERNAL_ACTION_2) && (ConfigurationTTool.ExternalCommand2 != null) && (ConfigurationTTool.ExternalCommand2.length()) > 0) {
            setLongDescription(EXTERNAL_ACTION_2, ConfigurationTTool.ExternalCommand2);
        }
    }

    public TGUIAction(int id) {
        if (actions[0] == null) {
            init();
        }
        if (actions[id] == null) {
            return ;
        }


        putValue(Action.NAME, actions[id].NAME);
        putValue(Action.SMALL_ICON, actions[id].SMALL_ICON);
        putValue(LARGE_ICON, actions[id].LARGE_ICON);
        putValue(Action.SHORT_DESCRIPTION, actions[id].SHORT_DESCRIPTION);
        putValue(Action.LONG_DESCRIPTION, actions[id].LONG_DESCRIPTION);
        //putValue(Action.MNEMONIC_KEY, new Integer(actions[id].MNEMONIC_KEY));
        if (actions[id].MNEMONIC_KEY != 0) {
            if (actions[id].hasControl) {
                putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].KEY, java.awt.event.InputEvent.CTRL_MASK));
            } else {
                if ((actions[id].MNEMONIC_KEY >= 37 && actions[id].MNEMONIC_KEY <= 40) || actions[id].MNEMONIC_KEY == KeyEvent.VK_DELETE) //handling for arrow and delete keys
                    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].MNEMONIC_KEY, 0));
                else
                    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(actions[id].KEY));
            }
        }
        putValue(Action.ACTION_COMMAND_KEY, actions[id].ACTION_COMMAND_KEY);

        if ((id == EXTERNAL_ACTION_1) && (ConfigurationTTool.ExternalCommand1 != null) && (ConfigurationTTool.ExternalCommand1.length()) > 0) {
            setLongDescription(EXTERNAL_ACTION_1, ConfigurationTTool.ExternalCommand1);
        }
        if ((id == EXTERNAL_ACTION_2) && (ConfigurationTTool.ExternalCommand2 != null) && (ConfigurationTTool.ExternalCommand2.length()) > 0) {
            setLongDescription(EXTERNAL_ACTION_2, ConfigurationTTool.ExternalCommand2);
        }
    }

    public void setName(int index, String name) {
        actions[index].NAME = name;
        putValue(Action.NAME, actions[index].NAME);
    }

    public void setLongDescription(int index, String description) {
        actions[index].LONG_DESCRIPTION = description;
        putValue(Action.LONG_DESCRIPTION, actions[index].LONG_DESCRIPTION);
    }

    public void init() {
        actions[ACT_NEW] = new TAction("new-command", "New Model", IconManager.imgic20, IconManager.imgic21, "New Model" , "New model", 'N');
        actions[ACT_NEW_PROJECT] = new TAction("new-project-command", "New project", IconManager.imgic20_proj, IconManager.imgic21_proj, "New project", "New project", 0);
        actions[ACT_NEW_DESIGN] = new TAction("new-command-design", "New design", IconManager.imgic14, IconManager.imgic14, "New design", "New TURTLE design", 0);
        actions[ACT_NEW_ANALYSIS] = new TAction("new-command-analysis", "New analysis", IconManager.imgic17, IconManager.imgic17, "New analysis", "New TURTLE analysis", 0);
        actions[ACT_OPEN] = new TAction("open-command","Open Model", IconManager.imgic22, IconManager.imgic23, "Open Model", "Open an existing TTool model",'O', true);
        actions[ACT_OPEN_PROJECT] = new TAction("open-project-command","Open project", IconManager.imgic22_proj, IconManager.imgic23_proj, "Open project", "Open an existing TTool project",'O', true);
        actions[ACT_OPEN_FROM_NETWORK] = new TAction("open-command-from-network","Open project from TTool repository", IconManager.imgic22_net, IconManager.imgic23_net, "Open project from TTool repository", "Open an existing TTool model from the TTool repository (TTool website)",'0', true);
        actions[ACT_OPEN_LAST] = new TAction("openlast-command","Open file: " + ConfigurationTTool.LastOpenFile, IconManager.imgic22, IconManager.imgic23, "Open: " + ConfigurationTTool.LastOpenFile, "Open the lastly saved TTool model", 0);
        actions[ACT_MERGE] = new TAction("merge-command","Merge", IconManager.imgic22, IconManager.imgic23, "Merge", "Merge the current TTool modeling with another one saved in a file ", 0);
        actions[ACT_SAVE] = new TAction("save-command", "Save",IconManager.imgic24, IconManager.imgic25, "Save", "Save an opened or a new TTool modeling", 'S', true);
        actions[ACT_SAVE_TIF] = new TAction("save-tif-command", "Save TIF specification",IconManager.imgic24, IconManager.imgic25, "Save TIF Specification", "Save in TIF a TURTLE modeling", 0);
        actions[ACT_OPEN_TIF] = new TAction("open-tif-command", "Open TIF specification",IconManager.imgic24, IconManager.imgic25, "Open TIF Specification", "Open a TURTLE modeling given in TIF", 0);
        actions[ACT_OPEN_SD] = new TAction("open-sd-command", "Open MSC specification",IconManager.imgic24, IconManager.imgic25, "Open MSC Specification", "Open a MSC specification given in xml format", 0);
        actions[ACT_SAVE_AS_PROJECT] = new TAction("saveasproject-command", "Save as Project",IconManager.imgic24, IconManager.imgic25, "Save as Project", "Save an opened or a new TTool modeling under a new project", 0);
        actions[ACT_SAVE_AS_MODEL] = new TAction("saveasmodel-command", "Save as Model",IconManager.imgic24, IconManager.imgic25, "Save as Model", "Save an opened or a new TTool modeling under a new model", 0);
        actions[ACT_QUIT] = new TAction("quit-command", "Quit", IconManager.imgic26, IconManager.imgic27, "Quit",  "Quit TTool", 'Q');

        actions[ACT_SAVE_LOTOS] = new TAction("save-last-lotos", "Save last RT-LOTOS specification",IconManager.imgic24, IconManager.imgic25, "Save last RT-LOTOS specification", "Save the lastest automatically generated RT-LOTOS specification", 0);
        actions[ACT_SAVE_DTA] = new TAction("save-last-dta", "Save last DTA",IconManager.imgic24, IconManager.imgic25, "Save last DTA", "Save the lastest built DTA", 0);
        actions[ACT_SAVE_RG] = new TAction("save-last-rg", "Save last RG",IconManager.imgic24, IconManager.imgic25, "Save last RG", "Save the lastest built RG", 0);
        actions[ACT_SAVE_TLSA] = new TAction("save-last-tlsa", "Save last TLSA",IconManager.imgic24, IconManager.imgic25, "Save last TLSA", "Save the lastest generated TLSA", 0);
        actions[ACT_SAVE_AUT] = new TAction("save-last-rg-aut", "Save last RG (AUT format)",IconManager.imgic24, IconManager.imgic25, "Save last RG (AUT format)", "Save the lastest built RG (AUT Format)", 0);
        actions[ACT_SAVE_AUTPROJ] = new TAction("save-last-rg-autproj", "Save last minimized RG (AUT format)",IconManager.imgic24, IconManager.imgic25, "Save last minimized RG (AUT format)", "Save the lastest minimized RG (AUT Format)", 0);
        actions[ACT_SAVE_AUTMODIFIED] = new TAction("save-last-rg-modified", "Save last modified RG (AUT format)",IconManager.imgic24, IconManager.imgic25, "Save last modified RG (AUT format)", "Save the lastest modified RG (AUT Format)", 0);

        actions[ACT_IMPORT_LIB] = new TAction("import-lib-command", "Import library", IconManager.imgic338, IconManager.imgic339, "Import library",  "Import a TTool library", 'E');
        actions[ACT_EXPORT_LIB] = new TAction("export-lib-command", "Export library", IconManager.imgic340, IconManager.imgic341, "Export library",  "Export a TTool library", 'I');

        actions[ACT_CUT] = new TAction("cut-command", "Cut",IconManager.imgic330, IconManager.imgic331, "Cut", "Cut selected components", 'X', true);
        actions[ACT_COPY] = new TAction("copy-command", "Copy", IconManager.imgic332, IconManager.imgic333, "Copy", "Copy selected components", 'C', true);
        actions[ACT_PASTE] = new TAction("paste-command", "Paste",IconManager.imgic334, IconManager.imgic335, "Paste", "Paste - if possible - previously cut / copied components at the minimal position of the currently opened diagram", 'V', true);
        actions[ACT_SUPPR] = new TAction("delete-command", "Delete", IconManager.imgic336, IconManager.imgic337, "Delete", "Delete selected components", KeyEvent.VK_DELETE);
        actions[ACT_DELETE] = new TAction("delete-command", "Delete", IconManager.imgic336, IconManager.imgic337, "Delete", "Delete selected components", KeyEvent.VK_BACK_SPACE);

        actions[ACT_ZOOM_MORE] = new TAction("zoommore-command", "Zoom +", IconManager.imgic316, IconManager.imgic317, "Zoom +", "Zoom +", '0');
        actions[ACT_ZOOM_LESS] = new TAction("zoomless-command", "Zoom -", IconManager.imgic314, IconManager.imgic315, "Zoom -", "Zoom -", '0');
        actions[ACT_SHOW_ZOOM] = new TAction("showzoom-command", "100%", null, null, "Zoom value", "Zoom value", '0');

        actions[ACT_BACKWARD] = new TAction("backward-command", "Undo",IconManager.imgic40, IconManager.imgic41, "Undo", "One operation before", 'Z', true);
        actions[ACT_FORWARD] = new TAction("forward-command", "Redo", IconManager.imgic42, IconManager.imgic43, "Redo", "One operation ahead", 'Y', true);

        actions[ACT_FIRST_DIAG] = new TAction("firstdiag-command", "First diagram",IconManager.imgic44, IconManager.imgic45, "First diagram", "Open the first diagram", 'A');
        actions[ACT_BACK_DIAG] = new TAction("backdiag-command", "Previous diagram", IconManager.imgic46, IconManager.imgic47, "Previous diagram", "Open the previous diagram", 'P', true);
        actions[ACT_NEXT_DIAG] = new TAction("nextdiag-command", "Next diagram",IconManager.imgic48, IconManager.imgic49, "Next diagram", "Open the next diagram", 'F', true);
        actions[ACT_LAST_DIAG] = new TAction("lastdiag-command", "Last diagram", IconManager.imgic50, IconManager.imgic51, "Last diagram", "Open the last diagram", 'E');

        actions[ACT_TOGGLE_ATTRIBUTES] = new TAction("toggle-att-command", "Show / hide Attributes",IconManager.imgic132, IconManager.imgic132, "Show / hide Attributes", "Show / hide Attributes", '0');
        actions[ACT_TOGGLE_GATES] = new TAction("toggle-gate-command", "Show / hide Gates",IconManager.imgic134, IconManager.imgic134, "Show / hide Gates", "Show / hide Gates", '0');
        actions[ACT_TOGGLE_SYNCHRO] = new TAction("toggle-synchro-command", "Show / hide Synchronization gates",IconManager.imgic136, IconManager.imgic136, "Show / hide Synchronization gates", "Show / hide Synchronization gates", '0');

        actions[ACT_TOGGLE_JAVA] = new TAction("toggle-java-command", "Show / hide Java code", IconManager.imgic138, IconManager.imgic138, "Show / hide Java code", "Show / hide Java code", '0');
        actions[ACT_TOGGLE_ATTR] = new TAction("toggle-attr-command", "Show / hide attributes (OFF -> partial -> Full)", IconManager.imgic138, IconManager.imgic138, "Show / hide element attributes (OFF -> Partial -> Full)", "Show / hide attributes (OFF -> Partial -> Full)", '0');

        actions[ACT_TOGGLE_DIPLO_ID] = new TAction("toggle-tml-id", "Show / hide DIPLODOCUS IDs", IconManager.imgic138, IconManager.imgic138, "Show / hide DIPLODOCUS IDs", "Show / hide DIPLODOCUS IDs", '0');
        actions[ACT_TOGGLE_AVATAR_ID] = new TAction("toggle-avatar-id", "Show / hide AVATAR IDs", IconManager.imgic138, IconManager.imgic138, "Show / hide AVATAR IDs", "Show / hide AVATAR IDs", '0');
        actions[ACT_TOGGLE_TEPE_ID] = new TAction("toggle-tepe-id", "Show / hide TEPE IDs", IconManager.imgic138, IconManager.imgic138, "Show / hide TEPE IDs", "Show / hide TEPE IDs", '0');

        actions[ACT_TOGGLE_INTERNAL_COMMENT] = new TAction("toggle-internal-comment-command", "Show / hide (OFF -> partial -> Full)", IconManager.imgic138, IconManager.imgic138, "Show / hide internal comments (OFF -> partial -> Full)", "Show / hide internal comments (OFF -> partial -> Full)", '0');

        actions[ACT_MODEL_CHECKING] = new TAction("checking-command", "Syntax analysis", IconManager.imgic36, IconManager.imgic37, "Syntax analysis",  "Checks that all diagrams follows the TTool's syntax", '1');
        actions[ACT_GEN_RTLOTOS] = new TAction("gen_rtlotos-command", "Generate RT-LOTOS", IconManager.imgic34, IconManager.imgic35, "Generate RT-LOTOS specification",  "Generates a RT-LOTOS specification from TTool diagrams", KeyEvent.VK_F6);
        actions[ACT_GEN_LOTOS] = new TAction("gen_lotos-command", "Generate LOTOS", IconManager.imgic90, IconManager.imgic90, "Generate LOTOS specification",  "Generates a LOTOS specification from TTool diagrams", '0');
        actions[ACT_ONECLICK_LOTOS_RG] = new TAction("gen_rglotos-command", "One-click LOTOS-based verification", IconManager.imgic342, IconManager.imgic342, "One-click LOTOS-based verification",  "Generates a LOTOS-based RG  from TTool diagrams", '0');
        actions[ACT_ONECLICK_RTLOTOS_RG] = new TAction("gen_rgrtlotos-command", "Generate RT-LOTOS-based RG", IconManager.imgic342, IconManager.imgic342, "Generate RT-LOTOS-based RG ",  "Generates an RT-LOTOS-based RG  from TTool diagrams", '0');

        // Issue #35: Renamed action name to be closer to actual functionality
        actions[ACT_GEN_UPPAAL] = new TAction("gen_uppaal-command", "Safety Verification (UPPAAL)", IconManager.imgic86, IconManager.imgic86, "Safety Verification (UPPAAL)",  "Formal safety verification of TTool diagrams with UPPAAL", '0');

        actions[ACT_AVATAR_MODEL_CHECKER] = new TAction("avatar-model-checker", "Safety verification", IconManager.imgic140, IconManager.imgic140, "Safety verification (Internal tool)",  "Safety verification with the internal model checker", '0');
        actions[ACT_GEN_JAVA] = new TAction("gen_java-command", "Generate JAVA", IconManager.imgic38, IconManager.imgic39, "Generate JAVA",  "Generates Java code from TURTLE diagrams", 0);
        actions[ACT_SIMU_JAVA] = new TAction("gen_simujava-command", "Java-based simulation", IconManager.imgic38, IconManager.imgic39, "JAVA-based simualtion",  "Simulate diagrams using Java language", 0);
        //@author: Huy TRUONG
        //action for button External Search and Internal Sarch
        actions[ACT_EXTERNAL_SEARCH] = new TAction("external_search-command", "External Search", IconManager.imgic5113, IconManager.imgic5113, "External Search",  "External Search Interface", 0);
        actions[ACT_INTERNAL_SEARCH] = new TAction("internal_search-command", "Internal Search", IconManager.imgic5114, IconManager.imgic5114, "Internal Search",  "Internal Search Interface", 0);
        //--
        actions[ACT_GEN_SYSTEMC] = new TAction("gen_systemc-command", "Generate simulation code", IconManager.imgic61, IconManager.imgic61, "Generate code for simulation",  "Generates code for simulation", 0);
        actions[ACT_SIMU_SYSTEMC] = new TAction("simu_systemc", "Interactive Simulation", IconManager.imgic61, IconManager.imgic61, "Interactive simulation",  "Interactive simulation ", 0);
        actions[ACT_GEN_TMLTXT] = new TAction("gen_tmltxt-command", "Generate TML / TMAP in text format", IconManager.imgic66, IconManager.imgic66, "Generate TML / TMAP in text format",  "Generates TML / TMAP code in text format from TML Design diagrams", 0);
        actions[ACT_GEN_CCODE] = new TAction("gen_CCode-command", "Generate C code", IconManager.imgic68, IconManager.imgic68, "Generate C code",  "Generate C code from DIPLODOCUS deployment diagrams", 0);
        actions[ACT_GEN_AUT] = new TAction("gen_aut-command", "Generate automata", IconManager.imgic64, IconManager.imgic64, "Generate automata",  "Generates automata from TML Design diagrams", 0);
        actions[ACT_GEN_AUTS] = new TAction("gen_auts-command", "Generate automata via LOTOS", IconManager.imgic64, IconManager.imgic64, "Generate automata via LOTOS",  "Generates automata from TML Design diagrams, using LOTOS", 0);
        actions[ACT_GEN_DESIGN] = new TAction("gen_design-command", "Generate Design", IconManager.imgic58, IconManager.imgic59, "Generate Design from analysis",  "Generates a TURTLE design from a TURTLE analysis", 0);
        actions[ACT_CHECKCODE] = new TAction("gen_checkcode-command", "Check syntax of formal code", IconManager.imgic312, IconManager.imgic312, "Check syntax of formal code",  "Gives as input to the corresponding tool the lastly generated formal specification", 0);
        actions[ACT_SIMULATION] = new TAction("gen_sim-command", "Run intensive simulation", IconManager.imgic312, IconManager.imgic312, "Run simulation",  "Generate a simulation trace for the lastly generated formal specification", '0');
        actions[ACT_VALIDATION] = new TAction("gen_val-command", "Formal Verification", IconManager.imgic310, IconManager.imgic310, "Formal verification",  "Generate an automata (DTA, RG) from the lastly generated formal specification", '0');
        actions[ACT_PROJECTION] = new TAction("proj_val-command", "Make minimization", IconManager.imgic310, IconManager.imgic310, "Make minimization",  "Minimize a RG using Aldebaran", KeyEvent.VK_F9);
        actions[ACT_GRAPH_MODIFICATION] = new TAction("graph_modification-command", "Modify minimized graph", IconManager.imgic310, IconManager.imgic310, "Modify minimized graph",  "Modify minimized graph according to a selected function", 0);
        actions[ACT_BISIMULATION] = new TAction("bisimulation-command", "Make bisimulation (Aldebaran)", IconManager.imgic310, IconManager.imgic310, "Make bisimulation (Aldebaran)",  "Perform bisimulations using Aldebaran", KeyEvent.VK_F10);
        actions[ACT_BISIMULATION_CADP] = new TAction("bisimulation-cadp-command", "Make bisimulation (BISIMULATOR)", IconManager.imgic310, IconManager.imgic310, "Make bisimulation (BISIMULATOR)",  "Perform bisimulations using BISIMULATOR", KeyEvent.VK_F10);
        actions[ACT_DEADLOCK_SEEKER_AUT] = new TAction("deadlockseeker-command", "Search for Deadlocks on last AUT graph", IconManager.imgic310, IconManager.imgic310, "Search for deadlocks (last AUT graph)",  "Display all states with no exit transitions (potential deadlocks on lastly generated AUT graph)", 0);
        actions[ACT_DEADLOCK_SEEKER_SAVED_AUT] = new TAction("deadlockseekersavedaut-command", "Search for Deadlocks on saved AUT graph", IconManager.imgic310, IconManager.imgic310, "Search for deadlocks (saved AUT graph)",  "Display all states with no exit transitions (potential deadlocks on previously saved AUT graph)", 0);
        actions[ACT_VIEW_STAT_AUT] = new TAction("viewstataut-command", "Analysis (last AUT graph)", IconManager.imgic28, IconManager.imgic29, "Analysis (last AUT graph)",  "Analysis on the last generated reachability graph generated in AUT (Aldebaran) format", 0);
        actions[ACT_VIEW_STAT_AUTDIPLODOCUS] = new TAction("viewstatautdiplodocus-command", "Analysis (last DIPLODOCUS graph)", IconManager.imgic28, IconManager.imgic29, "Analysis (last DIPLODOCUS graph)",  "Analysis on the last DIPLODOCUS reachability graph generated by the simulator", 0);
        actions[ACT_VIEW_STAT_AUTPROJ] = new TAction("viewstatautproj-command", "Analysis (last minimized AUT graph)", IconManager.imgic28, IconManager.imgic29, "Analysis (last minimized AUT graph)",  "Analysis on the last minimized reachability graph  in AUT (Aldebaran) format", 0);
        actions[ACT_VIEW_STAT_SAVED_AUT] = new TAction("viewstatsavedautproj-command", "Analysis (last saved RG)", IconManager.imgic28, IconManager.imgic29, "Analysis (last saved RG)",  "Analysis on a save dgraph", 0);
        actions[ACT_VIEW_PM_AUT] = new TAction("viewpmaut-command", "Power Management Analysis (last AUT graph)", IconManager.imgic28, IconManager.imgic29, "Power Management Analysis (last AUT graph)",  "Power Management Analysis on the last generated reachability graph generated in AUT (Aldebaran) format", 0);
        actions[ACT_VIEW_PM_AUTPROJ] = new TAction("viewpmautproj-command", "Power Management Analysis (last minimized AUT graph)", IconManager.imgic28, IconManager.imgic29, "Power Management Analysis (last minimized AUT graph)",  "Power Management Analysis on the last minimized reachability graph  in AUT (Aldebaran) format", 0);
        actions[ACT_VIEW_PM_SAVED_AUT] = new TAction("viewpmsavedautproj-command", "Power Management Analysis (saved AUT graph)", IconManager.imgic28, IconManager.imgic29, "Power Management Analysis (saved AUT graph)",  "Power Management Analysis on a graph saved in AUT (Aldebaran) format", 0);
        actions[ACT_DSE] = new TAction("auto-dse", "Automated Design Space Exploration", IconManager.imgic89, IconManager.imgic89, "Automated Design Space Exploration", "Find the optimal mapping and security additions automatically",0);
        // AVATAR
        actions[ACT_AVATAR_SIM] = new TAction("avatar-simu", "Interactive simulation", IconManager.imgic18, IconManager.imgic18, "Interactive simulation",  "Interactive simulation of the AVATAR design under edition", '2');
        actions[ACT_AVATAR_FV_UPPAAL] = new TAction("avatar-formal-verification-uppaal", "Safety formal verification with UPPAAL (Safety)", IconManager.imgic86, IconManager.imgic86, "Formal verification with UPPAAL (Safety)",  "Formal verification with UPPAAL (Safety) of the AVATAR design under edition", '3');
        actions[ACT_AVATAR_FV_PROVERIF] = new TAction("avatar-formal-verification-proverif", "Security verification (ProVerif)", IconManager.imgic88, IconManager.imgic88, "Security verification (ProVerif)",  "Security formal verification (with ProVerif)", '4');
        actions[ACT_AVATAR_FV_STATICANALYSIS] = new TAction("avatar-formal-verification-staticanalysis", "Safety analysis (invariants)", IconManager.imgic96, IconManager.imgic96, "Safaty analysis (invariants)",  "Safety analysis using the invariant tecnique", '5');
        actions[ACT_AVATAR_EXECUTABLE_GENERATION] = new TAction("avatar-executable-generation", "Code generation" , IconManager.imgic94, IconManager.imgic94, "Code generation",  "Generation of C-POSIX executable code from AVATAR design under edition", '6');


        actions[ACT_VIEW_JAVA] = new TAction("view-java", "Display Java code", IconManager.imgic38, IconManager.imgic39, "Display Java code",  "Display the java code of the pointed component", 0);
        actions[ACT_VIEW_RTLOTOS] = new TAction("view-rtlotos", "Show last formal specification", IconManager.imgic302, IconManager.imgic302, "Show formal specification",  "Display the lastest generated formal specification", 0);
        actions[ACT_VIEW_SUGGESTED_DESIGN] = new TAction("view-suggested-design", "Show last suggested design", IconManager.imgic302, IconManager.imgic302, "Show suggested design",  "Display the lastest generated suggested design", 0);
        actions[ACT_VIEW_SIM] = new TAction("view-sim", "Show last simulation trace (timing order)", IconManager.imgic318, IconManager.imgic319, "Show last simulation trace (timing order)",  "Display the lastest generated simulation trace in function of action time", 0);
        actions[ACT_VIEW_SIM_CHRONO] = new TAction("view-sim-chrono", "Show last simulation trace (chronological order)", IconManager.imgic318, IconManager.imgic319, "Show last simulation trace (chronological order)",  "Display the lastest generated simulation trace in function of action chronological order", 0);
        actions[ACT_VIEW_DTADOT] = new TAction("view-dtadot", "Show last DTA", IconManager.imgic306, IconManager.imgic306, "Show DTA",  "Display the lastest generated DTA", 0);
        actions[ACT_VIEW_RGDOT] = new TAction("view-rgdot", "Show last RG", IconManager.imgic308, IconManager.imgic308, "Show RG",  "Display the lastest generated RG", 0);
        actions[ACT_VIEW_TLSADOT] = new TAction("view-tlsadot", "Show last TLSA", IconManager.imgic308, IconManager.imgic308, "Show TLSA",  "Display the lastest generated TLSA", 0);
        actions[ACT_VIEW_RGAUTDOT] = new TAction("view-rgdot-aut", "Show last RG (AUT format)", IconManager.imgic308, IconManager.imgic308, "Show RG (aldebaran)",  "Display the lastest generated RG (Aldebaran format)", 0);
        actions[ACT_VIEW_RGAUTPROJDOT] = new TAction("view-rgdot-aut-proj", "View minimized RG", IconManager.imgic308, IconManager.imgic308, "View minimized RG",  "Display the lastest minimized RG", 0);
        actions[ACT_VIEW_MODIFIEDAUTDOT] = new TAction("view-modified-aut-dot", "View modified RG", IconManager.imgic308, IconManager.imgic308, "View modified RG",  "Display the lastest modified RG", 0);
        actions[ACT_VIEW_SAVED_LOT] = new TAction("view-saved-lot", "View RT-LOTOS specification", IconManager.imgic308, IconManager.imgic308, "View RT-LOTOS specification",  "Display a RT-LOTOS specification previously saved", 0);
        actions[ACT_VIEW_SAVED_DOT] = new TAction("view-saved-dot", "View saved graph", IconManager.imgic308, IconManager.imgic308, "View saved graph",  "Display a previously saved graph (DTA, RG, AUT)", 0);
        actions[ACT_VIEW_BIRDEYES] = new TAction("view-birdeyes", "Show external bird eyes view", IconManager.imgic56, IconManager.imgic57, "Show external bird eyes view",  "Bird eyes view of the diagram under edition", 0);
        actions[ACT_VIEW_BIRDEYES_EMB] = new TAction("view-birdeyes-emb", "Hide / Show embedded bird eyes view", IconManager.imgic56, IconManager.imgic57, "Hide / Show embedded bird eyes view",  "Embedded bird eyes view of the diagram under edition", 0);
        actions[ACT_VIEW_WAVE] = new TAction("view-wave", "View wave", IconManager.imgic56, IconManager.imgic57, "View wave",  "Use gtkwave to display various signals of the lastly performed simulation", 0);
        actions[ACT_VIEW_RG_DIPLODOCUS] = new TAction("view-rg-diplodocus", "Show last DIPLODOCUS RG", IconManager.imgic308, IconManager.imgic308, "Show last DIPLODOCUS RG",  "Display the lastest RG generated by the DIPLODOCUS simulator", 0);


        actions[ACT_SCREEN_CAPTURE] = new TAction("screen-capture", "Screen capture", IconManager.imgic338, IconManager.imgic339, "Screen capture",  "Capture the screen", 0);
        actions[ACT_TTOOL_WINDOW_CAPTURE] = new TAction("tool-window-capture", "TTool Window capture", IconManager.imgic338, IconManager.imgic339, "Main window capture",  "Capture the main window", 0);
        actions[ACT_DIAGRAM_CAPTURE] = new TAction("diagram-capture", "Current diagram capture (PNG)", IconManager.imgic338, IconManager.imgic339, "Diagram capture",  "Capture the currenlty opened diagram", 0);
        actions[ACT_SVG_DIAGRAM_CAPTURE] = new TAction("svg-diagram-capture", "Current diagram capture (SVG)", IconManager.imgic338, IconManager.imgic339, "Diagram capture (SVG)",  "Capture the currenlty opened diagram in svg format", 0);
        actions[ACT_ALL_DIAGRAM_CAPTURE] = new TAction("all-diagram-capture", "All diagrams capture (PNG)", IconManager.imgic338, IconManager.imgic339, "All diagram capture (PNG)",  "Capture in png images the currenlty opened set of diagram (analysis, design, etc.)", 0);
        actions[ACT_ALL_DIAGRAM_CAPTURE_SVG] = new TAction("all-diagram-capture-svg", "All diagrams capture (SVG)", IconManager.imgic338, IconManager.imgic339, "All diagram capture (SVG)",  "Capture in svg images the currenlty opened set of diagram (analysis, design, etc.)", 0);
        actions[ACT_SELECTED_CAPTURE] = new TAction("selected-capture", "Capture of selected components", IconManager.imgic338, IconManager.imgic339, "Capture of selected components",  "Capture the selected components of the currently opened diagram", 0);
        actions[ACT_GEN_DOC] = new TAction("gen-doc", "Documentation generation", IconManager.imgic28, IconManager.imgic28, "Documentation generation",  "Documentation may be automatically generated, in html format, from edited diagrams", 0);
        actions[ACT_GEN_DOC_REQ] = new TAction("gen-doc-req", "SysML requirements documentation", IconManager.imgic28, IconManager.imgic28, "SysML requirements documentation",  "SysML requirements documentation is displayed in an array, and may be further automatically generated in html format", 0);

        actions[ACT_ABOUT] = new TAction("about-command", "About", IconManager.imgic30, IconManager.imgic31, "About", "TTool information", 0);
        actions[ACT_TURTLE_WEBSITE] = new TAction("turtle-website-command", "TTool's website", IconManager.imgic30, IconManager.imgic31, "TTool's website", "Various information (e.g., documentation) on TTool", 0);
        actions[ACT_TURTLE_DOCUMENTATION] = new TAction("turtle-docu-command", "AVATAR online help", IconManager.imgic30, IconManager.imgic31, "AVATAR online help", "AVATAR online help", 0);
        actions[ACT_DIPLODOCUS_DOCUMENTATION] = new TAction("diplo-docu-command", "DIPLODOCUS online help", IconManager.imgic30, IconManager.imgic31, "DIPLODOCUS online help", "DIPLODOCUS online help", 0);

        // Issue # 34: SysMLSec help was using the command of DIPLODOCUS resulting in SysMLSec help being displayed instead
        actions[ACT_SYSMLSEC_DOCUMENTATION] = new TAction("sysmlsec-docu-command", "SysML-Sec online help", IconManager.imgic30, IconManager.imgic31, "SysML-Sec online help", "SysML-Sec online help", 0);
        //      actions[ACT_SYSMLSEC_DOCUMENTATION] = new TAction("diplo-docu-command", "SysML-Sec online help", IconManager.imgic30, IconManager.imgic31, "SysML-Sec online help", "SysML-Sec online help", 0);
        actions[ACT_TTOOL_CONFIGURATION] = new TAction("configuration-command", "Configuration", IconManager.imgic76, IconManager.imgic77, "Configuration", "Configuration loaded at startup", 0);

        actions[ACT_ENHANCE] = new TAction("enhance-command", "Enhance", IconManager.imgic28, IconManager.imgic29, "Enhance", "Automatically enhance diagram", 0);

        actions[CONNECTOR_COMMENT] = new TAction("add-comment-connector", "Add Comment connector", IconManager.imgic65, IconManager.imgic65, "Comment connector", "Add a Connector between a UML note and a component in the currently opened diagram", 0);

        //for the new action created by Solange
        actions[PRUEBA_1] = new TAction("Help for color of the ports", "Help for color of the ports", IconManager.imgic2111, IconManager.imgic2111, "Help for color of the ports", "Help for color of the ports",0);

        actions[TCD_EDIT] = new TAction("edit-class-diagram", "Edit class diagram", IconManager.imgic100, IconManager.imgic101, "Edit Class Diagram", "Make it possible to edit the currently opened class diagram", 0);
        actions[TCD_NEW_TCLASS] = new TAction("add-tclass", "Add Tclass", IconManager.imgic104, IconManager.imgic104, "Tclass", "Add a TClass to the currently opened class diagram", 0);
        actions[TCD_NEW_TOBJECT] = new TAction("add-tobject", "Add Tobject", IconManager.imgic128, IconManager.imgic128, "Tobject", "Add a TObject (= instance of a TClass) to the currently opened class diagram", 0);
        actions[TCD_NEW_TDATA] = new TAction("add-tdata", "Add Tdata", IconManager.imgic130, IconManager.imgic130, "Tdata", "Add a Tdata (= TURTLE data) to the currently opened class diagram", 0);
        actions[TCD_ASSOCIATION] = new TAction("add-association", "Add association", IconManager.imgic102, IconManager.imgic102, "Association", "Add an association between two tclasses of the currently opened class diagram", 0);
        actions[AD_EDIT] = new TAction("edit-activity-diagram", "Edit activity diagram", IconManager.imgic200, IconManager.imgic201, "Edit Activity Diagram", "Make it possible to edit the currently opened activity diagram", 0);
        actions[AD_CONNECTOR] = new TAction("add-ad-connector", "Add AD Connector", IconManager.imgic202, IconManager.imgic202, "ADConnector", "Add a Connector between two components of the currently opnened activity diagram", 0);
        actions[AD_ACTION_STATE] = new TAction("add-action-state", "Add Action State", IconManager.imgic204, IconManager.imgic204, "Action state", "Add an action state (i.e. synchronization on gate, variable modification, process spawning) to the currently opened activity diagram", 0);
        actions[AD_PARALLEL] = new TAction("add-parallel", "Add Parallel", IconManager.imgic206, IconManager.imgic206, "Parallel", "Add a parallel or synchro operator to the currently opened activity diagram", 0);
        actions[AD_SEQUENCE] = new TAction("add-sequence", "Add Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence operator to the currently opened activity diagram", 0);
        actions[AD_PREEMPTION] = new TAction("add-preemption", "Add Preemption", IconManager.imgic228, IconManager.imgic228, "Preemption", "Add a preemption operator to the currently opened activity diagram", 0);
        actions[AD_CHOICE] = new TAction("add-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened activity diagram", 0);
        actions[AD_START] = new TAction("add-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened activity diagram", 0);
        actions[AD_STOP] = new TAction("add-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened activity diagram", 0);
        actions[AD_JUNCTION] = new TAction("add-juunction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened activity diagram", 0);
        actions[AD_DETERMINISTIC_DELAY] = new TAction("add-deterministic-delay", "Add Deterministic Delay", IconManager.imgic214, IconManager.imgic214, "Deterministic Delay", "Add a deterministic delay to the currently opened activity diagram", 0);
        actions[AD_NON_DETERMINISTIC_DELAY] = new TAction("add-non-deterministic-delay", "Add Non-Deterministic Delay", IconManager.imgic216, IconManager.imgic216, "Non-Deterministic Delay", "Add a non-deterministic delay to the currently opened activity diagram", 0);
        actions[AD_DELAY_NON_DETERMINISTIC_DELAY] = new TAction("add-time-interval", "Add Time Interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened activity diagram", 0);
        actions[AD_TIME_LIMITED_OFFER] = new TAction("add-time-limited-offer", "Add Time-Limited offer", IconManager.imgic218, IconManager.imgic218, "Time-Limited Offer", "Add a time-limited offer to the currently opened activity diagram", 0);
        actions[AD_TIME_LIMITED_OFFER_WITH_LATENCY] = new TAction("add-time-limited-offer-with-latency", "Add Time-Limited offer with a non-deterministic delay", IconManager.imgic220, IconManager.imgic220, "Time-Limited Offer with non-deterministic delay", "Adds a time-limited offer, beginning with a non-deterministic delay, to the currently opened activity diagram", 0);
        actions[AD_TIME_CAPTURE] = new TAction("add-time-capture", "Time capture", IconManager.imgic204, IconManager.imgic204, "Action time capture", "Add a time capture operator to the currently opened activity diagram", 0);
        actions[AD_ARRAY_GET] = new TAction("add-array-get", "Add array get element", IconManager.imgic230, IconManager.imgic230, "Array get element", "Add an array get element action to the currently opened activity diagram", 0);
        actions[AD_ARRAY_SET] = new TAction("add-array-set", "Add array set element", IconManager.imgic232, IconManager.imgic232, "Array set element", "Add an array set element action to the currently opened activity diagram", 0);

        actions[ADD_EDIT] = new TAction("edit-add-diagram", "Edit AVATAR deployment diagram", IconManager.imgic100, IconManager.imgic101, "Edit AVATAR deployment diagram", "Make it possible to edit the currently opened AVATAR deployment diagram", 0);
        actions[ADD_LINK] = new TAction("add-add-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened AVATAR deployment diagram", 0);
        actions[ADD_CPUNODE] = new TAction("add-add-cpunode", "Add a CPU", IconManager.imgic1100, IconManager.imgic1100, "CPU", "Add a cpu node to the currently opened AVATAR deployment diagram", 0);

        actions[ADD_BUSNODE] = new TAction("add-add-busnode", "Add a Bus", IconManager.imgic1102, IconManager.imgic1102, "Bus", "Add a bus node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_VGMNNODE] = new TAction("add-add-vgmnnode", "Add a VGMN", IconManager.imgic1102, IconManager.imgic1102, "VGMN", "Add a VGMN node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_CROSSBARNODE] = new TAction("add-add-crossbarnode", "Add a CROSSBAR", IconManager.imgic1102, IconManager.imgic1102, "Crossbar", "Add a Crossbar node to the currently opened AVATAR deployment diagram", 0);

        actions[ADD_TTYNODE] = new TAction("add-add-tty", "Add a TTY node", IconManager.imgic1108, IconManager.imgic1108, "TTY", "Add a tty node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_BLOCKARTIFACT] = new TAction("add-add-blockartifact", "Map an AVATAR block", IconManager.imgic702, IconManager.imgic702, "Map an AVATAR block", "Map an AVATAR block onto a node in the currently opened AVATAR deployment diagram", 0);
        // julien ----------------------------------------------------------------------------------
        actions[DEPLOY_AVATAR_DIAGRAM] = new TAction("DEPLOY", "DEPLOY",IconManager.imgic94, IconManager.imgic94, "Generate DEPLOY Soclib", "DEPLOY diagram without check syntax", 0);

        actions[EXTRAC_DEPLOY_PARAM_TO_FILE] = new TAction("FILE", "FILE",IconManager.imgic340,IconManager.imgic340, "Extrac attributes to File", "Extrac attributes to specified File", 0);
        // -----------------------------------------------------------------------------------------

        actions[ADD_CHANNELARTIFACT] = new TAction("add-add-channelartifact", "Map an AVATAR Channel", IconManager.imgic702, IconManager.imgic702, "Map an AVATAR channel", "Map an AVATAR channel onto a memory in the currently opened AVATAR deployment diagram", 0);
        actions[ADD_BRIDGENODE] = new TAction("add-add-bridgenode", "Add a Bridge", IconManager.imgic1104, IconManager.imgic1104, "Bridge node", "Add a bridge node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_RAMNODE] = new TAction("add-add-ramnode", "Add a RAM", IconManager.imgic1108, IconManager.imgic1108, "RAM", "Add a RAM node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_ROMNODE] = new TAction("add-add-romnode", "Add a ROM", IconManager.imgic1108, IconManager.imgic1108, "ROM", "Add a memory node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_DMANODE] = new TAction("add-add-dmanode", "Add a DMA", IconManager.imgic1110, IconManager.imgic1110, "DMA", "Add a DMA node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_ICUNODE] = new TAction("add-add-icunode", "Add an ICU", IconManager.imgic1112, IconManager.imgic1112, "ICU", "Add a ICU node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_COPROMWMRNODE] = new TAction("add-add-copromwmrnode", "Add a MWMR Copro", IconManager.imgic1114, IconManager.imgic1114, "MWMR Copro", "Add a DMA node to the currently opened AVATAR deployment diagram", 0);
        actions[ADD_TIMERNODE] = new TAction("add-add-timernode", "Add a Timer", IconManager.imgic1116, IconManager.imgic1116, "Timer", "Add a timer node to the currently opened AVATAR deployment diagram", 0);


        actions[TCD_PARALLEL_OPERATOR] = new TAction("add-parallel-operator", "Add Parallel composition operator", IconManager.imgic112, IconManager.imgic112, "Parallel composition operator", "Add a Parallel composition operator to the currently opened class diagram", 0);
        actions[TCD_CONNECTOR_ATTRIBUTE] = new TAction("add-attribute-connector", "Connects an association to a composition operator", IconManager.imgic108, IconManager.imgic108, "Attributes an association with a composition operator", "Adds a connector between an association and a composition operator to the currently opened class diagram", 0);
        actions[TCD_SEQUENCE_OPERATOR] = new TAction("add-sequence-operator", "Add Sequence composition operator", IconManager.imgic114, IconManager.imgic114, "Sequence composition operator", "Add a Sequence composition operator to the currently opened class diagram", 0);
        actions[TCD_PREEMPTION_OPERATOR] = new TAction("add-preemption-operator", "Add Preemption composition operator", IconManager.imgic116, IconManager.imgic116, "Preemption composition operator", "Add a Preemption composition operator to the currently opened class diagram", 0);
        actions[TCD_SYNCHRO_OPERATOR] = new TAction("add-synchro-operator", "Add Synchro composition operator", IconManager.imgic110, IconManager.imgic110, "Synchro composition operator", "Adds a Synchro composition operator to the currently opened class diagram", 0);
        actions[TCD_INVOCATION_OPERATOR] = new TAction("add-invocation-operator", "Add Invocation composition operator", IconManager.imgic126, IconManager.imgic126, "Invocation composition operator", "Adds an invocation composition operator to the currently opened class diagram", 0);
        actions[TCD_ASSOCIATION_NAVIGATION] = new TAction("add-association-navigation", "Add association with navigation", IconManager.imgic106, IconManager.imgic108, "Association with navigation", "Adds an association with a navigation indication between two tclasses of the currently opened class diagram", 0);
        actions[UML_NOTE] = new TAction("edit-note", "Add a note to a diagram", IconManager.imgic320, IconManager.imgic321, "Add a comment", "Add a comment to the currently opened TTool diagram", 0);
        actions[PRAGMA] = new TAction("pragma", "Security pragmas", IconManager.imgic6000, IconManager.imgic6001, "Security pragmas", "Add security pragmas to the currently opened TTool diagram", 0);
        actions[SAFETY_PRAGMA] = new TAction("safety_pragma", "Add a safety (UPPAAL) property to a diagram", IconManager.imgic6002, IconManager.imgic6003, "Safety property (UPPAAL)", "Add a safety (UPPAAL) property to the currently opened TTool diagram", 0);
        actions[PERFORMANCE_PRAGMA] = new TAction("performance_pragma", "Add a performance property to a diagram", IconManager.imgic6004, IconManager.imgic6005, "Performance property", "Add a performance property to the currently opened TTool diagram", 0);
        actions[AVATAR_FIREWALL] = new TAction("avatar_firewall", "Add an avatar firewall to a diagram", IconManager.imgic7001, IconManager.imgic7001, "Add an avatar firewall", "Add an avatar firewall to the currently opened TTool diagram", 0);

        actions[IOD_EDIT] = new TAction("edit-io-diagram", "Edit interaction overview diagram", IconManager.imgic100, IconManager.imgic101, "Edit interaction overview diagram", "Make it possible to edit the currently opened interaction overview diagram", 0);
        actions[IOD_CONNECTOR] = new TAction("add-iod-connector", "Add IOD Connector", IconManager.imgic202, IconManager.imgic202, "Connector", "Add a Connector between two components of the currently opened interaction overview diagram", 0);
        actions[IOD_REF_SD] = new TAction("add-ref-sd", "Add reference to a SD", IconManager.imgic400, IconManager.imgic400, "Reference to a SD", "Add a reference to a sequence diagram", 0);
        actions[IOD_REF_IOD] = new TAction("add-ref-iod", "Add reference to an IOD", IconManager.imgic400, IconManager.imgic400, "Reference to an IOD", "Add a reference to an interaction overview diagram", 0);
        actions[IOD_PARALLEL] = new TAction("add-iod-parallel", "Add Parallel", IconManager.imgic206, IconManager.imgic206, "Parallel", "Add a parallel or synchro operator to the currently opened interaction overview diagram", 0);
        //actions[AD_SEQUENCE] = new TAction("add-sequence", "Add Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence operator to the currently opened activity diagram", 0);
        actions[IOD_PREEMPTION] = new TAction("add-iod-preemption", "Add Preemption", IconManager.imgic402, IconManager.imgic402, "Preemption", "Add a preemption operator to the currently opened interaction overview diagram", 0);
        actions[IOD_SEQUENCE] = new TAction("add-iod-sequence", "Add Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence operator to the currently opened interaction overview diagram", 0);
        actions[IOD_CHOICE] = new TAction("add-iod-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened interaction overview diagram", 0);
        actions[IOD_START] = new TAction("add-iod-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened interaction overview diagram", 0);
        actions[IOD_STOP] = new TAction("add-iod-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened interaction overview diagram", 0);
        actions[IOD_JUNCTION] = new TAction("add-iod-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened interaction overview diagram", 0);

        actions[SD_EDIT] = new TAction("edit-sd-diagram", "Edit sequence diagram", IconManager.imgic100, IconManager.imgic101, "Edit sequence diagram", "Make it possible to edit the currently opened sequence diagram", 0);
        actions[SD_INSTANCE] = new TAction("add-sd-instance", "Add an instance", IconManager.imgic500, IconManager.imgic500, "Instance", "Add a new instance  to the currently opened sequence diagram", 0);
        actions[SD_CONNECTOR_MESSAGE_ASYNC] = new TAction("add-sd-connector-message-async", "Add asynchronous message", IconManager.imgic504, IconManager.imgic504, "Asynchronous message", "Add an asynchronous message between two instances of the currently opened sequence diagram", 0);
        actions[SD_CONNECTOR_MESSAGE_SYNC] = new TAction("add-sd-connector-message-sync", "Add synchronous message", IconManager.imgic502, IconManager.imgic502, "Synchronous message", "Add an synchronous message between two instances of the currently opened sequence diagram", 0);
        actions[SD_ABSOLUTE_TIME_CONSTRAINT] = new TAction("add-sd-atc", "Absolute time constraint", IconManager.imgic506, IconManager.imgic506, "Absolute time constraint", "Add an absolute time constraint to the currently opened sequence diagram", 0);
        actions[SD_RELATIVE_TIME_CONSTRAINT] = new TAction("add-sd-rtc", "Relative time constraint", IconManager.imgic508, IconManager.imgic508, "Relative time constraint", "Add a relative time constraint to the currently opened sequence diagram", 0);
        actions[SD_RELATIVE_TIME_CONSTRAINT_CONNECTOR] = new TAction("add-sd-rtc-c", "Connect two relative time constraint lines", IconManager.imgic510, IconManager.imgic510, "Relative time constraint connector", "Add a connection between two relative time constraint lines of the currently opened sequence diagram", 0);
        actions[SD_ACTION_STATE] = new TAction("add-sd-action-state", "Action state", IconManager.imgic512, IconManager.imgic512, "Action state", "Add an action state to the currently opened sequence diagram", 0);
        actions[SD_GUARD] = new TAction("add-sd-guard", "Guard", IconManager.imgic524, IconManager.imgic524, "Guard", "Add a guard (non-deterministic, else, end) to the currently opened sequence diagram", 0);
        actions[SD_TIMER_SETTING] = new TAction("add-sd-timer-set-state", "Timer setting", IconManager.imgic514, IconManager.imgic514, "Timer setting", "Add a timer setting to the currently opened sequence diagram", 0);
        actions[SD_TIME_INTERVAL] = new TAction("add-sd-time-interval", "Time interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened sequence diagram", 0);
        actions[SD_TIMER_EXPIRATION] = new TAction("add-sd-timer-expiration-state", "Timer expiration", IconManager.imgic516, IconManager.imgic516, "Timer expiration", "Add a timer expiration to the currently opened sequence diagram", 0);
        actions[SD_TIMER_CANCELLATION] = new TAction("add-sd-timer-cancellation-state", "Timer cancellation", IconManager.imgic518, IconManager.imgic518, "Timer cancellation", "Add a timer cancellation to the currently opened sequence diagram", 0);
        actions[SD_COREGION] = new TAction("add-sd-coregion", "Coregion", IconManager.imgic520, IconManager.imgic520, "Coregion", "Add a coregion to the currently opened sequence diagram", 0);
        actions[SD_ALIGN_INSTANCES] = new TAction("add-sd-align_instances", "Align instances", IconManager.imgic522, IconManager.imgic522, "Align instances", "align instances of the currently opened sequence diagram", 0);

        actions[SDZV_EDIT] = new TAction("edit-sdzv-diagram", "Edit sequence diagram", IconManager

.imgic100, IconManager.imgic101, "Edit sequence diagram", "Make it possible to edit the currently opened sequence diagram", 0);
        actions[SDZV_INSTANCE] = new TAction("add-sdzv-instance", "Add an instance", IconManager.imgic500, IconManager.imgic500, "Instance", "Add a new instance  to the currently opened sequence diagram", 0);
        actions[SDZV_CONNECTOR_MESSAGE_ASYNC] = new TAction("add-sdzv-connector-message-async", "Add asynchronous message", IconManager.imgic504, IconManager.imgic504, "Asynchronous message", "Add an asynchronous message between two instances of the currently opened sequence diagram", 0);
        actions[SDZV_CONNECTOR_MESSAGE_SYNC] = new TAction("add-sdzv-connector-message-sync", "Add synchronous message", IconManager.imgic502, IconManager.imgic502, "Synchronous message", "Add an synchronous message between two instances of the currently opened sequence diagram", 0);
        actions[SDZV_ABSOLUTE_TIME_CONSTRAINT] = new TAction("add-sdzv-atc", "Absolute time constraint", IconManager.imgic506, IconManager.imgic506, "Absolute time constraint", "Add an absolute time constraint to the currently opened sequence diagram", 0);
        actions[SDZV_RELATIVE_TIME_CONSTRAINT] = new TAction("add-sdzv-rtc", "Relative time constraint", IconManager.imgic508, IconManager.imgic508, "Relative time constraint", "Add a relative time constraint to the currently opened sequence diagram", 0);
        actions[SDZV_RELATIVE_TIME_CONSTRAINT_CONNECTOR] = new TAction("add-sdzv-rtc-c", "Connect two relative time constraint lines", IconManager.imgic510, IconManager.imgic510, "Relative time constraint connector", "Add a connection between two relative time constraint lines of the currently opened sequence diagram", 0);
        actions[SDZV_ACTION_STATE] = new TAction("add-sdzv-action-state", "Action state", IconManager.imgic512, IconManager.imgic512, "Action state", "Add an action state to the currently opened sequence diagram", 0);
        actions[SDZV_GUARD] = new TAction("add-sdzv-guard", "Guard", IconManager.imgic524, IconManager.imgic524, "Guard", "Add a guard (non-deterministic, else, end) to the currently opened sequence diagram", 0);
        actions[SDZV_TIMER_SETTING] = new TAction("add-sdzv-timer-set-state", "Timer setting", IconManager.imgic514, IconManager.imgic514, "Timer setting", "Add a timer setting to the currently opened sequence diagram", 0);
        actions[SDZV_TIME_INTERVAL] = new TAction("add-sdzv-time-interval", "Time interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened sequence diagram", 0);
        actions[SDZV_TIMER_EXPIRATION] = new TAction("add-sdzv-timer-expiration-state", "Timer expiration", IconManager.imgic516, IconManager.imgic516, "Timer expiration", "Add a timer expiration to the currently opened sequence diagram", 0);
        actions[SDZV_TIMER_CANCELLATION] = new TAction("add-sdzv-timer-cancellation-state", "Timer cancellation", IconManager.imgic518, IconManager.imgic518, "Timer cancellation", "Add a timer cancellation to the currently opened sequence diagram", 0);
        actions[SDZV_COREGION] = new TAction("add-sdzv-coregion", "Coregion", IconManager.imgic520, IconManager.imgic520, "Coregion", "Add a coregion to the currently opened sequence diagram", 0);
        actions[SDZV_ALIGN_INSTANCES] = new TAction("add-sdzv-align_instances", "Align instances", IconManager.imgic522, IconManager.imgic522, "Align instances", "align instances of the currently opened sequence diagram", 0);

        actions[UCD_EDIT] = new TAction("edit-ucd-diagram", "Edit use case diagram", IconManager.imgic100, IconManager.imgic101, "Edit use case diagram", "Make it possible to edit the currently opened use case diagram", 0);
        actions[UCD_ACTOR] = new TAction("add-actor", "Add an actor", IconManager.imgic600, IconManager.imgic600, "Actor", "Add an actor to the currently opened use case diagram", 0);
        actions[UCD_ACTORBOX] = new TAction("add-actor-box", "Add an actor (box format)", IconManager.imgic614, IconManager.imgic614, "Actor (box format)", "Add an actor in box format to the currently opened use case diagram", 0);
        actions[UCD_USECASE] = new TAction("add-usecase", "Add a use case", IconManager.imgic602, IconManager.imgic602, "Add a use case", "Add a use case to the currently opened use case diagram", 0);
        actions[UCD_BORDER] = new TAction("add-border", "Add a border", IconManager.imgic612, IconManager.imgic612, "Add a border", "Add a border to the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_ACTOR_UC] = new TAction("add-connector-actor-uc", "actor <-> use case", IconManager.imgic604, IconManager.imgic604, "Actor <-> use case", "Add a connector between an actor and a use case of the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_INCLUDE] = new TAction("add-connector-include", "<<include>> relationship", IconManager.imgic606, IconManager.imgic606, "<<include>> relationship", "Add a <<include>> relationship to the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_EXTEND] = new TAction("add-connector-extend", "<<extend>> relationship", IconManager.imgic608, IconManager.imgic608, "<<extend>> relationship", "Add a <<extend>> relationship to the currently opened use case diagram", 0);
        actions[UCD_CONNECTOR_SPECIA] = new TAction("add-connector-specia", "Specialization", IconManager.imgic610, IconManager.imgic610, "specialization", "Add a specialization relationship to the currently opened use case diagram", 0);

        actions[TDD_EDIT] = new TAction("edit-tdd-diagram", "Edit deployment diagram", IconManager.imgic100, IconManager.imgic101, "Edit deployment diagram", "Make it possible to edit the currently opened deployment diagram", 0);
        actions[TDD_LINK] = new TAction("add-tdd-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened deployment diagram", 0);
        actions[TDD_NODE] = new TAction("add-tdd-node", "Add a node", IconManager.imgic700, IconManager.imgic700, "Node", "Add a node to the currently opened deployment diagram", 0);
        actions[TDD_ARTIFACT] = new TAction("add-tdd-artifact", "Add an artifact", IconManager.imgic702, IconManager.imgic702, "Artifact", "Add an artifact to the currently opened deployment diagram", 0);

        actions[NCDD_EDIT] = new TAction("edit-ncdd-diagram", "Edit network calculus diagram", IconManager.imgic100, IconManager.imgic101, "Edit network calculs diagram", "Make it possible to edit the currently opened network calculus diagram", 0);
        actions[NCDD_LINK] = new TAction("add-ncdd-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened network calculus diagram", 0);
        actions[NCDD_EQNODE] = new TAction("add-ncdd-eqnode", "Add an equipment node", IconManager.imgic700, IconManager.imgic700, "Add an equipment node", "Add an equipment node to the currently opened network calculus diagram", 0);
        actions[NCDD_SWITCHNODE] = new TAction("add-ncdd-switchnode", "Add a switch node", IconManager.imgic700, IconManager.imgic700, "Add a switch node", "Add a switch node to the currently opened network calculus diagram", 0);
        actions[NCDD_TRAFFIC_ARTIFACT] = new TAction("add-ncdd-traffic", "Add a traffic", IconManager.imgic702, IconManager.imgic702, "Traffic", "Add a traffic to the currently opened network calculus diagram", 0);
        actions[NCDD_ROUTE_ARTIFACT] = new TAction("add-ncdd-route", "Add a route", IconManager.imgic702, IconManager.imgic702, "Route", "Add a route to the currently opened network calculus diagram", 0);
        actions[ACT_NC] = new TAction("gen_nc-command", "Network calculus", IconManager.imgic3000, IconManager.imgic3000, "Network calculus",  "Perform network calculus from  UML diagrams", '0');



        actions[TMLTD_EDIT] = new TAction("edit-tmlcd-diagram", "Edit TML Task diagram", IconManager.imgic100, IconManager.imgic101, "Edit TML task diagram", "Make it possible to edit the currently opened TML task diagram", 0);
        actions[TMLTD_TASK] = new TAction("add-tmlcd-task", "Add a TML Task", IconManager.imgic806, IconManager.imgic806, "Add a TML task", "Add a TML task to the currently opened TML task diagram", 0);
        actions[TMLTD_CHANNEL] = new TAction("add-tmlcd-channel", "Add a channel operator", IconManager.imgic802, IconManager.imgic802, "Add a channel operator", "Add a channel operator to the currently opened TML task diagram", 0);
        actions[TMLTD_REQ] = new TAction("add-tmlcd-req", "Add a request operator", IconManager.imgic804, IconManager.imgic804, "Add a request operator", "Add a request operator to the currently opened TML task diagram", 0);
        actions[TMLTD_EVENT] = new TAction("add-tmlcd-event", "Add an event operator", IconManager.imgic800, IconManager.imgic800, "Add an event operator", "Add an event operator to the currently opened TML task diagram", 0);
        actions[TMLTD_ASSOC] = new TAction("add-tmlcd-assoc", "Add an association between two TML tasks", IconManager.imgic202, IconManager.imgic202, "Add an association between two TML tasks", "Add an association between two TML tasks to the currently opened TML task diagram", 0);
        actions[TMLTD_CONNECTOR] = new TAction("add-tmlcd-connector", "Connect an operator to an association", IconManager.imgic108, IconManager.imgic108, "Connect an operator to an association", "Connect an operator to an asociation onto the currently opened TML task diagram", 0);

        actions[TMLCTD_EDIT] = new TAction("edit-tmlccd-diagram", "Edit TML Component Task diagram", IconManager.imgic100, IconManager.imgic101, "Edit TML Component Task diagram", "Make it possible to edit the currently opened TML Component Task diagram", 0);
        actions[TMLCTD_CCOMPONENT] = new TAction("add-tmlccd-ccomponent", "Add a composite component", IconManager.imgic1200, IconManager.imgic1200, "Add a composite component", "Add a composite component to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_CREMOTECOMPONENT] = new TAction("add-tmlcremotecd-ccomponent", "Add a reference to a composite component", IconManager.imgic1200, IconManager.imgic1200, "Add a reference to a composite component", "Add a reference to a composite component to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_PCOMPONENT] = new TAction("add-tmlccd-pcomponent", "Add a primitive component", IconManager.imgic1202, IconManager.imgic1202, "Add a primitive component", "Add a primitive component to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_CPORT] = new TAction("add-tmlccd-cport", "Add a composite port", IconManager.imgic1204, IconManager.imgic1204, "Add a composite port", "Add a composite port to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_COPORT] = new TAction("add-tmlccd-coport", "Add a primitive port", IconManager.imgic1206, IconManager.imgic1206, "Add a primitive port", "Add a primitive port to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_PORT_CONNECTOR] = new TAction("add-tmlccd-port-connector", "Add a connector between two ports", IconManager.imgic202, IconManager.imgic202, "Add a connector between two ports", "Add a connector between two ports of the currently opened TML component Task diagram", 0);
        actions[TMLCTD_RCOMPONENT] = new TAction("add-tmlccd-rcomponent", "Add a record component", IconManager.imgic1202, IconManager.imgic1202, "Add a record component", "Add a record component to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_FORK] = new TAction("add-tmlccd-fork", "Add a channel fork", IconManager.imgic1210, IconManager.imgic1210, "Add a channel fork", "Add a fork channel port to the currently opened TML component Task diagram", 0);
        actions[TMLCTD_JOIN] = new TAction("add-tmlccd-join", "Add a channel join", IconManager.imgic1212, IconManager.imgic1212, "Add a channel join", "Add a join channel port to the currently opened TML component Task diagram", 0);

        actions[TMLAD_EDIT] = new TAction("edit-tmlad-diagram", "Edit TML Task activity diagram", IconManager.imgic100, IconManager.imgic101, "Edit TML task activity diagram", "Make it possible to edit the currently opened TML task activity diagram", 0);
        actions[TMLAD_CONNECTOR] = new TAction("add-tmlad-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened TML task activity diagram", 0);
        actions[TMLAD_ACTION_STATE] = new TAction("add-tmlad-action-state", "Add Action State", IconManager.imgic204, IconManager.imgic204, "Action state", "Add an action state (i.e. variable modification, operation call) to the currently opened TML activity diagram", 0);
        actions[TMLAD_CHOICE] = new TAction("add-tmlad-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened TML Task activity diagram", 0);
        actions[TMLAD_START] = new TAction("add-tmladd-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened TML activity diagram", 0);
        actions[TMLAD_STOP] = new TAction("add-tmlad-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened TML task activity diagram", 0);
        actions[TMLAD_JUNCTION] = new TAction("add-tmlad-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECI] = new TAction("add-tmlad-execi", "EXECI", IconManager.imgic910, IconManager.imgic910, "EXECI", "Add an EXECI delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECI_INTERVAL] = new TAction("add-tmlad-execi-int", "EXECI (time interval)", IconManager.imgic914, IconManager.imgic914, "EXECI (time interval)", "Add an EXECI time interval to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECC] = new TAction("add-tmlad-execc", "EXECC", IconManager.imgic920, IconManager.imgic920, "EXECC", "Add an EXECC delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_EXECC_INTERVAL] = new TAction("add-tmlad-execc-int", "EXECC (time interval)", IconManager.imgic922, IconManager.imgic922, "EXECC (time interval)", "Add an EXECC time interval to the currently opened TML task activity diagram", 0);
        actions[TMLAD_DELAY] = new TAction("add-tmlad-delay", "DELAY[]", IconManager.imgic214, IconManager.imgic214, "DELAY[]", "Add a physical delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_INTERVAL_DELAY] = new TAction("add-tmlad-interval-delay", "DELAY[,]", IconManager.imgic224, IconManager.imgic224, "DELAY[,]", "Add a physical interval delay to the currently opened TML task activity diagram", 0);
        actions[TMLAD_WRITE_CHANNEL] = new TAction("add-tmladd-write-channel", "Write in channel", IconManager.imgic900, IconManager.imgic900, "Write in channel", "Add a write to channel operator opened TML activity diagram", 0);
        actions[TMLAD_SEND_REQUEST] = new TAction("add-tmladd-send-request", "Send request", IconManager.imgic902, IconManager.imgic902, "Send request", "Add a send request operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_SEND_EVENT] = new TAction("add-tmladd-send-event", "Send event", IconManager.imgic904, IconManager.imgic904, "Send event", "Add a send event operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_WAIT_EVENT] = new TAction("add-tmladd-wait-event", "Wait event", IconManager.imgic908, IconManager.imgic908, "Wait event", "Add a wait event operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_NOTIFIED_EVENT] = new TAction("add-tmladd-notified-event", "Notified event", IconManager.imgic918, IconManager.imgic918, "Notified event", "Add a notified event operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_READ_CHANNEL] = new TAction("add-tmladd-read-channel", "Read in channel", IconManager.imgic906, IconManager.imgic906, "Read in channel", "Add a read to channel operator opened TML activity diagram", 0);
        actions[TMLAD_FOR_LOOP] = new TAction("add-tmlad-for-loop", "Loop (for)", IconManager.imgic912, IconManager.imgic912, "Loop (for)", "Add a for loop to the currently opened TML activity diagram", 0);
        actions[TMLAD_FOR_STATIC_LOOP] = new TAction("add-tmlad-for-static-loop", "Static loop (for)", IconManager.imgic912, IconManager.imgic912, "Static loop (for)", "Add a static for loop to the currently opened TML activity diagram", 0);
        actions[TMLAD_FOR_EVER_LOOP] = new TAction("add-tmlad-for-ever-loop", "Loop for ever", IconManager.imgic926, IconManager.imgic926, "Loop for ever", "Add a loop-for-ever operator to the currently opened TML activity diagram", 0);
        actions[TMLAD_SEQUENCE] = new TAction("add-tmlad-sequence", "Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence to the currently opened TML activity diagram", 0);
        actions[TMLAD_UNORDERED_SEQUENCE] = new TAction("add-tmlad-unordered-sequence", "Random sequence", IconManager.imgic928, IconManager.imgic928, "Random sequence", "Add a random sequence to the currently opened TML activity diagram", 0);
        actions[TMLAD_SELECT_EVT] = new TAction("add-tmlad-select-evt", "Add select event", IconManager.imgic916, IconManager.imgic916, "Select event", "Add a selection between events to the currently opened TML Task activity diagram", 0);
        actions[TMLAD_RANDOM] = new TAction("add-tmlad-random", "Add random", IconManager.imgic924, IconManager.imgic924, "Select random", "Add a random operator to the currently opened TML Task activity diagram", 0);
        actions[TMLAD_READ_REQUEST_ARG] = new TAction("add-tmladd-read-request-arg", "Reading request arguments", IconManager.imgic930, IconManager.imgic930, "Reading request arguments", "Add a reading request argument operator to the currently opened TML activity diagram", 0);



        actions[TMLAD_ENCRYPT] = new TAction("add-tmlad-encrypt", "Add Encryption", IconManager.imgic940, IconManager.imgic940, "Encryption", "Add an encryption operator to the currently opened TML Task activity diagram", 0);
        actions[TMLAD_DECRYPT] = new TAction("add-tmlad-decrypt", "Add Decryption", IconManager.imgic941, IconManager.imgic941, "Decryption", "Add a decryption operator to the currently opened TML Task activity diagram", 0);


        actions[EBRDD_EDIT] = new TAction("edit-ebrdd-diagram", "Edit EBRDD", IconManager.imgic100, IconManager.imgic101, "Edit EBRDD", "Make it possible to edit the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_CONNECTOR] = new TAction("add-ebrdd-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_START] = new TAction("add-ebrdd-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_STOP] = new TAction("add-ebrdd-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened  Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_CHOICE] = new TAction("add-ebrdd-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a deterministic choice to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_ERC] = new TAction("add-ebrdd-erc", "Add ERC", IconManager.imgic1050, IconManager.imgic1050, "Event Reception Container", "Add an Event Reception Container to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_ACTION] = new TAction("add-ebrdd-action", "Add Action", IconManager.imgic204, IconManager.imgic204, "Action", "Add an Action to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_FOR_LOOP] = new TAction("add-ebrdd-for-loop", "Loop (for)", IconManager.imgic912, IconManager.imgic912, "Loop (for)", "Add a for loop to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_SEQUENCE] = new TAction("add-ebrdd-sequence", "Sequence", IconManager.imgic226, IconManager.imgic226, "Sequence", "Add a sequence to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_CONNECTOR_ERC] = new TAction("add-ebrdd-erc-connector", "Connect two ESO / ERB operators together", IconManager.imgic1052, IconManager.imgic1052, "Connect two ESO / ERB operators together", "Connect two ESO / ERB operators of the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_ESO] = new TAction("add-ebrdd-eso", "Add ESO", IconManager.imgic1054, IconManager.imgic1054, "Event Sequencing Operator", "Add an Event Sequencing Operator to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_ERB] = new TAction("add-ebrdd-erb", "Add ERB", IconManager.imgic1056, IconManager.imgic1056, "Event Reaction Block", "Add an Event Reaction Block to the currently opened Event-Based Requirement Description Diagram", 0);
        actions[EBRDD_VARIABLE_DECLARATION] = new TAction("add-ebrdd-var-dec", "Add Variable Declaration", IconManager.imgic1060, IconManager.imgic1060, "Add Variable Declaration", "Add a variable declaration block to the currently opened Event-Based Requirement Description Diagram", 0);

        actions[TMLARCHI_EDIT] = new TAction("edit-tmlarchi-diagram", "Edit DIPLODOCUS architecture diagram", IconManager.imgic100, IconManager.imgic101, "Edit DIPLODOCUS architecture diagram", "Make it possible to edit the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_LINK] = new TAction("add-tmlarchi-link", "Add a link", IconManager.imgic202, IconManager.imgic202, "Link", "Add a link between two nodes of the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_CPUNODE] = new TAction("add-tmlarchi-cpunode", "Add a CPU node", IconManager.imgic1100, IconManager.imgic1100, "CPU node", "Add a cpu node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_FPGANODE] = new TAction("add-tmlarchi-fpganode", "Add a FPGA node", IconManager.imgic1120, IconManager.imgic1120, "FPGA node", "Add a fpga node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_BUSNODE] = new TAction("add-tmlarchi-busnode", "Add a Bus node", IconManager.imgic1102, IconManager.imgic1102, "Bus node", "Add a bus node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_CPNODE] = new TAction("add-tmlarchi-cpnode", "Add a Communication pattern node", IconManager.imgic1102, IconManager.imgic1102, "CP node", "Add a communication pattern node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_ARTIFACT] = new TAction("add-tmlarchi-artifact", "Map a task", IconManager.imgic702, IconManager.imgic702, "Map a task", "Map a task onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_COMMUNICATION_ARTIFACT] = new TAction("add-tmlarchi-communication-artifact", "Map a channel", IconManager.imgic702, IconManager.imgic702, "Map a channel", "Map a channel onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_PORT_ARTIFACT] = new TAction("add-tmlarchi-port-artifact", "Map a port", IconManager.imgic702, IconManager.imgic702, "Map a port", "Map a port onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_EVENT_ARTIFACT] = new TAction("add-tmlarchi-event-artifact", "Map an event / request", IconManager.imgic702, IconManager.imgic702, "Map an event/request", "Map an event or a request onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_BRIDGENODE] = new TAction("add-tmlarchi-bridgenode", "Add a Bridge node", IconManager.imgic1104, IconManager.imgic1104, "Bridge node", "Add a bridge node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_HWANODE] = new TAction("add-tmlarchi-hwanode", "Add a hardware accelerator node", IconManager.imgic1106, IconManager.imgic1106, "Hardware accelerator node", "Add a hardware accelerator node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_CAMSNODE] = new TAction("add-tmlarchi-camsnode", "Add a System C-AMS node", IconManager.imgic1106, IconManager.imgic1106, "System C-AMS node", "Add a System C-AMS node to the currently open DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_MEMORYNODE] = new TAction("add-tmlarchi-memorynode", "Add a memory node", IconManager.imgic1108, IconManager.imgic1108, "Memory node", "Add a memory node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_DMANODE] = new TAction("add-tmlarchi-dmanode", "Add a DMA node", IconManager.imgic1110, IconManager.imgic1110, "DMA node", "Add a DMA node to the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_KEY] = new TAction("add-tmlarchi-key", "Map a key", IconManager.imgic1118, IconManager.imgic1118, "Map a key", "Map a key onto a node in the currently opened DIPLODOCUS architecture diagram", 0);
        actions[TMLARCHI_FIREWALL] = new TAction("add-tmlarchi-firewall", "Add a Firewall", IconManager.imgic7001, IconManager.imgic7001, "Add a Firewall", "Add a Firewall in the currently opened DIPLODOCUS architecture diagram",0);

        actions[TMLCP_EDIT] = new TAction("edit-tmlcp-diagram", "Edit communication pattern diagram", IconManager.imgic100, IconManager.imgic101, "Edit communication pattern diagram", "Make it possible to edit the currently opened communication pattern diagram", 0);
        actions[TMLCP_CONNECTOR] = new TAction("add-tmlcp-connector", "Add CP Connector", IconManager.imgic202, IconManager.imgic202, "Connector", "Add a Connector between two components of the currently opened communication pattern diagram", 0);
        actions[TMLCP_REF_SD] = new TAction("add-ref-sd-tmlcp", "Add reference to a SD", IconManager.imgic400, IconManager.imgic400, "Reference to a SD", "Add a reference to a sequence diagram in the the currently opened communication pattern diagram", 0);
        actions[TMLCP_REF_CP] = new TAction("add-ref-cp-tmlcp", "Add reference to a CP", IconManager.imgic400, IconManager.imgic400, "Reference to an AD", "Add a reference to a communication pattern diagram in the currently opened communication pattern diagram", 0);
        actions[TMLCP_FORK] = new TAction("add-tmlcp-fork", "Add fork", IconManager.imgic206, IconManager.imgic206, "Fork", "Add a fork to the currently opened communication pattern diagram", 0);
        actions[TMLCP_JOIN] = new TAction("add-tmlcp-join", "Add join", IconManager.imgic206, IconManager.imgic206, "Join", "Add a join to the currently opened communication pattern diagram", 0);
        actions[TMLCP_CHOICE] = new TAction("add-tmlcp-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened communication pattern diagram", 0);
        actions[TMLCP_START] = new TAction("add-tmlcp-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened communication pattern diagram", 0);
        actions[TMLCP_STOP] = new TAction("add-tmlcp-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened communication pattern diagram", 0);
        actions[TMLCP_JUNCTION] = new TAction("add-tmlcp-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened communication pattern diagram", 0);
        actions[TMLCP_FOR_LOOP] = new TAction("add-tmlcp-forloop", "Add loop", IconManager.imgic912, IconManager.imgic912, "Loop", "Add a for loop to the currently opened communication pattern diagram", 0);

        actions[TMLSD_EDIT] = new TAction("edit-tmlsd-diagram", "Edit CP-SD diagram", IconManager.imgic100, IconManager.imgic101, "Edit the Sequence Diagram", "Make it possible to edit the currently opened communication pattern sequence diagram", 0);
        actions[TMLSD_MESSAGE_ASYNC] = new TAction("add-tmlsd-messageasync", "Add async msg", IconManager.imgic202, IconManager.imgic202, "Asynchronous message", "Add an asynchronous message between two components of the currently opened communication pattern sequence diagram", 0);
        actions[TMLSD_STORAGE_INSTANCE] = new TAction("add-storage-instance-tmlsd", "Storage instance", IconManager.imgic500, IconManager.imgic500, "Storage instance", "Add a storage instance to the currently opened communication pattern sequence diagram", 0);
        actions[TMLSD_TRANSFER_INSTANCE] = new TAction("add-transfer-instance-tmlsd", "Transfer instance", IconManager.imgic500, IconManager.imgic500, "Transfer instance", "Add a transfer instance to the currently opened communication pattern sequence diagram", 0);
        actions[TMLSD_CONTROLLER_INSTANCE] = new TAction("add-controller-instance-tmlsd", "Controller instance", IconManager.imgic500, IconManager.imgic500, "Controller instance", "Add a controller instance to the currently opened communication pattern sequence diagram", 0);
        actions[TMLSD_ACTION_STATE] = new TAction("add-action-tmlsd", "Action state", IconManager.imgic512, IconManager.imgic512, "Action state", "Add an action state to the currently opened communication pattern sequence diagram", 0);

        //System C-AMS
        actions[CAMS_EDIT] = new TAction("add-action-C-AMS", "Action state", IconManager.imgic100, IconManager.imgic101, "Action state", "Add an action state to the currently opened SystemC-AMS diagram", 0);
        actions[CAMS_BLOCK_TDF] = new TAction("C-AMS-block-TDF", "Add a TDF block", IconManager.imgic5000, IconManager.imgic5000, "TDF block", "Add a TDF block to the currently opened SystemC-AMS Diagram", 0);
        actions[CAMS_BLOCK_DE] = new TAction("C-AMS-block-DE", "Add a DE block", IconManager.imgic5000, IconManager.imgic5000, "DE block", "Add a DE block to the currently opened SystemC-AMS Diagram", 0);
        actions[CAMS_CONNECTOR] = new TAction("C-AMS-connector", "Add a connection", IconManager.imgic202, IconManager.imgic202, "Connector", "Connects two block of the currently opened SystemC-AMS Diagram", 0);
        actions[CAMS_PORT_TDF] = new TAction("C-AMS-port-TDF", "Add a TDF port", IconManager.imgic8000, IconManager.imgic8000, "TDF port", "Add a TDF port to the currently opened SystemC-AMS Diagram", 0);
        actions[CAMS_PORT_DE] = new TAction("C-AMS-port-DE", "Add a DE port", IconManager.imgic8001, IconManager.imgic8001, "DE port", "Add a DE port to the currently opened SystemC-AMS Diagram", 0);
        actions[CAMS_PORT_CONVERTER] = new TAction("C-AMS-port-converter", "Add a converter port", IconManager.imgic8003, IconManager.imgic8003, "Converter port", "Add a converter port to the currently opened SystemC-AMS Diagram", 0);
        actions[CAMS_CLUSTER] = new TAction("C-AMS-cluster", "Add a cluster", IconManager.imgic5000, IconManager.imgic5000, "Cluster", "Add a cluster to the currently opened SystemC-AMS Diagram", 0);


        //ProActive State Machine Diagram
        actions[PROSMD_EDIT] = new TAction("edit-prosmd-diagram", "Edit ProActive state machine diagram", IconManager.imgic100, IconManager.imgic101, "Edit ProActive state machine diagram", "Make it possible to edit the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_START] = new TAction("add-prosmd-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_STOP] = new TAction("add-prosmd-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_CONNECTOR] = new TAction("add-prosmd-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_SENDMSG] = new TAction("add-prosmd-sendmsg", "Send msg", IconManager.imgic2000, IconManager.imgic2000, "Send msg", "Add a send message to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_GETMSG] = new TAction("add-prosmd-getmsg", "Get msg", IconManager.imgic2002, IconManager.imgic2002, "Get msg", "Add a get message to the currently opened ProActive state machine diagram", 0);
        actions[PROSMD_CHOICE] = new TAction("add-prosmd-choice", "Choice", IconManager.imgic2004,IconManager.imgic2004,"Choice operator","Add a choice operator to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_JUNCTION] = new TAction("add-prosmd-junction", "Junction", IconManager.imgic2006,IconManager.imgic2006,"Junction operator","Add a junction operator to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_SUBMACHINE] = new TAction("add-prosmd-submachine", "Submachine", IconManager.imgic2008,IconManager.imgic2008,"Submachine","Add a submachine to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_ACTION] = new TAction("add-prosmd-action", "Action", IconManager.imgic2010,IconManager.imgic2010,"Action","Add an action to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_PARALLEL] = new TAction("add-prosmd-nondeterministic-choice", "Nondeterministic choice operator", IconManager.imgic206,IconManager.imgic206,"Parallel operator","Add a parallel operator to the currently opened ProActive state machine diagram",0);
        actions[PROSMD_STATE] = new TAction("add-prosmd-state", "State ", IconManager.imgic2012,IconManager.imgic2012,"State ","Add state to the currently opened ProActive state machine diagram",0);

        //ProcActive Composite Structure Diagram
        actions[PROCSD_EDIT] = new TAction("edit-procsd-diagram", "Edit ProActive composite stucture diagram", IconManager.imgic100, IconManager.imgic101, "Edit ProActive composite structure diagram", "Make it possible to edit the currently opened ProActive composite structure diagram", 0);
        actions[PROCSD_COMPONENT] = new TAction("add-procsd-component", "Add component", IconManager.imgic2100, IconManager.imgic2100, "Add component", "Add a component to the currently opened ProActive composite structure diagram", 0);
        //Delegate port action removed, by Solange
        //actions[PROCSD_DELEGATE_PORT] = new TAction("add-procsd-delegate-port", "Add a delegate port", IconManager.imgic2102, IconManager.imgic2102, "Add a delegate port", "Add a delegate port to a component", 0);
        actions[PROCSD_CONNECTOR] = new TAction("add-procsd-connector", "Connect two ports together", IconManager.imgic2110, IconManager.imgic2110, "Connect two ports together", "An interface coresponds to this conector", 0);
        actions[PROCSD_CONNECTOR_DELEGATE] = new TAction("add-procsd-connector-delegate", "Delegates the message", IconManager.imgic202, IconManager.imgic202, "Delegates the message ", "It transmits the message between the border and an inside component of a composite.", 0);
        actions[PROCSD_INTERFCE] = new TAction("add-procsd-interface", "Add an interface", IconManager.imgic2104, IconManager.imgic2104,"Add an interface", "an interface describes the messages wich pass through a connection", 0);
        actions[PROCSD_IN_PORT] = new TAction("add-procsd-in-port", "Add in port", IconManager.imgic2106, IconManager.imgic2106, "Add in port", "Add an in port to a component. An in port coresponds to a offered/server interface", 0);
        actions[PROCSD_OUT_PORT] = new TAction("add-procsd-out-port", "Add out port", IconManager.imgic2108, IconManager.imgic2108, "Add out port", "Add an out port to a component. An out port coresponds to an requiered/client  interface", 0);
        actions[PROCSD_CONNECTOR_PORT_INTERFACE] = new TAction("add-port-to-interface-connector", "Connect a port to an interface definition", IconManager.imgic108, IconManager.imgic108, "Connects a port to an interface definition", "Port to interface connector", 0);

        actions[ACT_TOGGLE_CHANNELS] = new TAction("toggle-ch-command", "Show / hide Channels",IconManager.imgic808, IconManager.imgic808, "Show / hide channels", "Show / hide channels", '0');
        actions[ACT_TOGGLE_EVENTS] = new TAction("toggle-evt-command", "Show / hide Events",IconManager.imgic810, IconManager.imgic810, "Show / hide events", "Show / hide events", '0');
        actions[ACT_TOGGLE_REQUESTS] = new TAction("toggle-req-command", "Show / hide Synchronization Requests",IconManager.imgic812, IconManager.imgic812, "Show / hide requests", "Show / hide requests", '0');

        // Requirement diagrams
        actions[TREQ_REQUIREMENT] = new TAction("add-treq-requirement", "Add a requirement", IconManager.imgic1002, IconManager.imgic1002, "Requirement", "Add a requirement to the currently opened requirement diagram", 0);
        actions[TREQ_OBSERVER] = new TAction("add-treq-observer", "Add an observer", IconManager.imgic1004, IconManager.imgic1004, "Observer", "Add an observer to the currently opened requirement diagram", 0);
        actions[TREQ_EBRDD] = new TAction("add-treq-ebrdd", "Add a reference to an EBRDD", IconManager.imgic1014, IconManager.imgic1014, "EBRDD", "Add an EBRDD-based observer to the currently opened requirement diagram", 0);
        actions[TREQ_DERIVE] = new TAction("add-treq-derive", "Add a <<deriveReqt>> link", IconManager.imgic1008, IconManager.imgic1008, "DeriveReqt", "Add a <<deriveReqt>> link between two requirements of the currently opened requirement diagram", 0);
        actions[TREQ_VERIFY] = new TAction("add-treq-verify", "Add a <<verify>> link", IconManager.imgic1006, IconManager.imgic1006, "Verify", "Add a <<verify>> link between an observer and a requirement of the currently opened requirement diagram", 0);
        actions[TREQ_COPY] = new TAction("add-treq-copy", "Add a <<copy>> link", IconManager.imgic1010, IconManager.imgic1010, "Copy", "Add a <<copy>> link between two requirements of the currently opened requirement diagram", 0);
        actions[TREQ_COMPOSITION] = new TAction("add-treq-composition", "Add a <<derive>> link", IconManager.imgic1012, IconManager.imgic1012, "Composition", "Add a <<copy>> link between two requirements of the currently opened requirement diagram", 0);


        // Attack Tree Diagrams
        actions[ATD_BLOCK] = new TAction("atd-block", "Add a block", IconManager.imgic1070, IconManager.imgic1070, "Block", "Add a SysML Block to the currently opened attack tree diagram", 0);
        actions[ATD_ATTACK] = new TAction("atd-attack", "Add an attack", IconManager.imgic1072, IconManager.imgic1072, "Attack", "Add an attack (i.e. a SysML value type) to the currently opened attack tree diagram", 0);
        actions[ATD_ATTACK_CONNECTOR] = new TAction("atd-attack-connector", "Add a connector between attacks /constraints", IconManager.imgic1080, IconManager.imgic1080, "Connector", "Add a connector between attacks / constraints of the currently opened attack tree diagram", 0);
        actions[ATD_COMPOSITION_CONNECTOR] = new TAction("atd-composition-connector", "Add a composition connector between blocks", IconManager.imgic1076, IconManager.imgic1076, "Composition connector", "Add a composition between blocks of the currently opened attack tree diagram", 0);
        actions[ATD_CONSTRAINT] = new TAction("atd-constraint", "Add a constraint", IconManager.imgic1078, IconManager.imgic1078, "Constraint", "Add a constraint to the currently opened attack tree diagram", 0);
        actions[ATD_COUNTERMEASURE] = new TAction("atd-countermeasure", "Add a countermeasure", IconManager.imgic1082, IconManager.imgic1082, "Countermeasure", "Add a countermeasure to the currently opened attack tree diagram", 0);
        actions[ATD_COUNTERMEASURE_CONNECTOR] = new TAction("atd-countermeasure-connector", "Add a connector between attacks / countermeasures", IconManager.imgic1084, IconManager.imgic1084, "Connect countermeasure", "Add a connector between attacks / countermeasures of the currently opened attack tree diagram", 0);

	// Fault Tree Diagrams
        actions[FTD_BLOCK] = new TAction("ftd-block", "Add a block", IconManager.imgic1070, IconManager.imgic1070, "Block", "Add a SysML Block to the currently opened fault tree diagram", 0);
        actions[FTD_FAULT] = new TAction("ftd-fault", "Add a fault", IconManager.imgic1416, IconManager.imgic1416, "Fault", "Add a fault (i.e. a SysML value type) to the currently opened fault tree diagram", 0);
        actions[FTD_FAULT_CONNECTOR] = new TAction("ftd-fault-connector", "Add a connector between faults /constraints", IconManager.imgic1080, IconManager.imgic1080, "Connector", "Add a connector between faults / constraints of the currently opened fault tree diagram", 0);
        actions[FTD_COMPOSITION_CONNECTOR] = new TAction("ftd-composition-connector", "Add a composition connector between blocks", IconManager.imgic1076, IconManager.imgic1076, "Composition connector", "Add a composition between blocks of the currently opened fault tree diagram", 0);
        actions[FTD_CONSTRAINT] = new TAction("ftd-constraint", "Add a constraint", IconManager.imgic1078, IconManager.imgic1078, "Constraint", "Add a constraint to the currently opened fault tree diagram", 0);
        actions[FTD_COUNTERMEASURE] = new TAction("ftd-countermeasure", "Add a countermeasure", IconManager.imgic1082, IconManager.imgic1082, "Countermeasure", "Add a countermeasure to the currently opened fault tree diagram", 0);
        actions[FTD_COUNTERMEASURE_CONNECTOR] = new TAction("ftd-countermeasure-connector", "Add a connector between faults / countermeasures", IconManager.imgic1084, IconManager.imgic1084, "Connect countermeasures", "Add a connector between faults / countermeasures of the currently opened fault tree diagram", 0);

        // TURTLE-OS
        actions[TOS_TCLASS] = new TAction("add-tclass-os", "Add Tclass", IconManager.imgic104, IconManager.imgic104, "Tclass", "Add a TClass to the currently opened class diagram", 0);
        actions[TOS_ASSOCIATION] = new TAction("add-association-tos", "Add association", IconManager.imgic102, IconManager.imgic102, "Association", "Add an association between two tclasses of the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_ASSOCIATION_NAVIGATION] = new TAction("add-association-nav-tos", "Add association with navigation", IconManager.imgic106, IconManager.imgic106, "Association with navigation", "Add an association with navigation between two tclasses of the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_CONNECTOR_ATTRIBUTE] = new TAction("add-attribute-connector-tos", "Connects an association to a composition operator", IconManager.imgic108, IconManager.imgic108, "Attributes an association with a composition operator", "Adds a connector between an association and a composition operator to the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_CALL_OPERATOR] = new TAction("add-call-operator", "Add Call composition operator", IconManager.imgic126, IconManager.imgic126, "Call composition operator", "Adds a call composition operator to the currently opened TURTLE-OS class diagram", 0);
        actions[TOS_EVT_OPERATOR] = new TAction("add-evt-operator", "Add Evt composition operator", IconManager.imgic126, IconManager.imgic126, "Evt composition operator", "Adds an evt composition operator to the currently opened TURTLE-OS class diagram", 0);
        actions[TOSAD_ACTION_STATE] = new TAction("add-action-state-osad", "Add Action State", IconManager.imgic204, IconManager.imgic204, "Action state", "Add an action state (i.e. synchronization on gate, variable modification, call on protected object) to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_CONNECTOR] = new TAction("add-tosad-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_CHOICE] = new TAction("add-tosad-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_START_STATE] = new TAction("add-tosad-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened TURTLKE-OS activity diagram", 0);
        actions[TOSAD_STOP_STATE] = new TAction("add-tosad-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_JUNCTION] = new TAction("add-tosad-junction", "Add Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_TIME_INTERVAL] = new TAction("add-tosad-time-interval", "Add Time Interval", IconManager.imgic224, IconManager.imgic224, "Time interval", "Add a time interval to the currently opened TURTLE-OS activity diagram", 0);
        actions[TOSAD_INT_TIME_INTERVAL] = new TAction("add-tosad-int-time-interval", "Add Interruptible Time Interval", IconManager.imgic224, IconManager.imgic224, "Interruptible time interval", "Add an interruptible time interval to the currently opened TURTLE-OS activity diagram", 0);

        actions[EXTERNAL_ACTION_1] = new TAction("user-command-1", "User command 1", IconManager.imgic338, IconManager.imgic338, "User command 1", "Execute the user command #1", '0');
        actions[EXTERNAL_ACTION_2] = new TAction("user-command-2", "User command 2", IconManager.imgic338, IconManager.imgic338, "User command 2", "Execute the user command #2", '0');

        // AVATAR Block Diagrams
        actions[ABD_BLOCK] = new TAction("abd-block", "Add a block", IconManager.imgic5000, IconManager.imgic5000, "Block", "Add a SysML Block to the currently opened AVATAR Block Diagram", 0);
        actions[ABD_CRYPTOBLOCK] = new TAction("abd-cryptoblock", "Add a crypto block", IconManager.imgic5000, IconManager.imgic5000, "Crypto block", "Add a SysML Crypto Block to the currently opened AVATAR Block Diagram", 0);
        actions[ABD_DATATYPE] = new TAction("abd-datatype", "Add a data type", IconManager.imgic5034, IconManager.imgic5034, "Data type", "Add a SysML Block representing a Data Type to the currently opened AVATAR Block Diagram", 0);
        actions[ABD_COMPOSITION_CONNECTOR] = new TAction("abd-composition-connector", "Add a composition connector between blocks", IconManager.imgic5002, IconManager.imgic5002, "Composition connector", "Add a composition between blocks of the currently opened AVATAR Block Diagram", 0);
        actions[ABD_PORT_CONNECTOR] = new TAction("abd-port-connector", "Add a composition connector between blocks", IconManager.imgic5004, IconManager.imgic5004, "Port connector", "Add a port link between blocks of the currently opened AVATAR Block Diagram", 0);
        actions[ABD_LIBRARYFUNCTION] = new TAction("abd-libraryfunction", "Add a library function", IconManager.imgic5070, IconManager.imgic5000, "Library Function", "Add a Library Function to the currently opened AVATAR Block Diagram", 0);
        actions[ABD_CRYPTOLIBRARYFUNCTION] = new TAction("abd-cryptolibraryfunction", "Add a crypto library function", IconManager.imgic5070, IconManager.imgic5000, "Crypto library function", "Add a Crypto Library Function to the currently opened AVATAR Block Diagram", 0);

        // AVATAR State Machine Diagrams
        actions[ASMD_EDIT] = new TAction("edit-asmd-diagram", "Edit AVATAR state machine diagram", IconManager.imgic100, IconManager.imgic101, "Edit AVATAR state machine diagram", "Make it possible to edit the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_CONNECTOR] = new TAction("add-asmd-connector", "Connect two operators together", IconManager.imgic202, IconManager.imgic202, "Connect two operators together", "Connect two operators of the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_START] = new TAction("add-asmd-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_STOP] = new TAction("add-asmd-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_SEND_SIGNAL] = new TAction("add-asmd-sendsignal", "Send signal", IconManager.imgic2014, IconManager.imgic2014, "Send signal", "Add a send signal operator to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_RECEIVE_SIGNAL] = new TAction("add-asmd-receivesignal", "Receive signal", IconManager.imgic2016, IconManager.imgic2016, "Receive signal", "Add a receive signal operator to the currently opened AVATAR state machine diagram", 0);
        // TODO: change icon
        actions[ASMD_LIBRARY_FUNCTION_CALL] = new TAction("add-asmd-libraryfunctioncall", "Library function call", IconManager.imgic2018, IconManager.imgic2018, "Library function call", "Add a library function call to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_PARALLEL] = new TAction("add-asmd-parallel", "Parallel", IconManager.imgic206, IconManager.imgic206, "Parallel", "Add a parallel operator to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_STATE] = new TAction("add-asmd-state", "State", IconManager.imgic5036, IconManager.imgic5036, "State", "Add a new state to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_CHOICE] = new TAction("add-asmd-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice - non-deterministic or guarded - to the currently opened AVATAR state machine diagram", 0);
        actions[ASMD_RANDOM] = new TAction("add-asmd-random", "Add random", IconManager.imgic924, IconManager.imgic924, "Select random", "Add a random operator to the currently opened AVATAR State Machine diagram", 0);
        actions[ASMD_SET_TIMER] = new TAction("add-asmd-setrimer", "Set timer", IconManager.imgic5038, IconManager.imgic5038, "Set timer", "Add a set timer operator to the currently opened AVATAR State Machine diagram", 0);
        actions[ASMD_RESET_TIMER] = new TAction("add-asmd-resettimer", "Reset timer", IconManager.imgic5040, IconManager.imgic5040, "Reset timer", "Add a reset timer operator to the currently opened AVATAR State Machine diagram", 0);
        actions[ASMD_EXPIRE_TIMER] = new TAction("add-asmd-expiretimer", "Timer expiration", IconManager.imgic5042, IconManager.imgic5042, "Wait for timer expiration", "Add a timer expiration operator to the currently opened AVATAR State Machine diagram", 0);


        // AVATAR Modeling Assumptions Diagram
        actions[AMAD_EDIT] = new TAction("edit-amad-diagram", "Edit AVATAR Modeling Assumptions Diagram", IconManager.imgic100, IconManager.imgic101, "Edit AVATAR Modeling Assumptions Diagram", "Make it possible to edit the currently opened AVATAR Modeling Assumption diagram", 0);
        actions[AMAD_ASSUMPTION] = new TAction("add-amad-assumption", "Add an assumption", IconManager.imgic5060, IconManager.imgic5060, "Assumption", "Add an assumption to the currently opened avatar Modeling Assumptions Diagram", 0);
        actions[AMAD_DIAGRAM_REFERENCE] = new TAction("add-amad-diagram-reference", "Add a diagram reference", IconManager.imgic5058, IconManager.imgic5058, "Diagram reference", "Add a diagram reference to the currently opened avatar Modeling Assumptions Diagram", 0);
        actions[AMAD_ELEMENT_REFERENCE] = new TAction("add-amad-element-reference", "Add a reference to an modeling element", IconManager.imgic5062, IconManager.imgic5062, "Element reference", "Add a reference to an model element in the currently opened avatar Modeling Assumptions Diagram", 0);
        actions[AMAD_COMPOSITION_CONNECTOR] = new TAction("add-amad-composition", "Add a composite link", IconManager.imgic1012, IconManager.imgic1012, "Composite", "Add a composite link between two assumptions of the currently opened avatar Modeling Assumptions diagram", 0);
        actions[AMAD_VERSIONING_CONNECTOR] = new TAction("add-amad-versioning", "Add a versioning link", IconManager.imgic5064, IconManager.imgic5064, "Versioning", "Add a versioning link between two assumptions of the currently opened avatar Modeling Assumptions diagram", 0);
        actions[AMAD_IMPACT_CONNECTOR] = new TAction("add-amad-impact", "Add an impact link", IconManager.imgic5066, IconManager.imgic5066, "Impact", "Add an impact link between two references to model elements, in the currently opened avatar Modeling Assumptions diagram", 0);
        actions[AMAD_MEET_CONNECTOR] = new TAction("add-amad-meet", "Add an meet link", IconManager.imgic5066, IconManager.imgic5066, "Meet", "Add an meet link between a model element and an assumption in the currently opened Avatar Modeling Assumptions diagram", 0);
        actions[AMAD_BELONGSTOCOMPOSITION_CONNECTOR] = new TAction("amad-belongstocomposition-connector", "Add a composition connector between references", IconManager.imgic5002, IconManager.imgic5002, "Composition connector", "Add a composition between blocks of the currently opened Avatar Modeling Assumptions diagram", 0);

        // AVATAR Requirement Diagrams
        actions[ARD_EDIT] = new TAction("edit-ard-diagram", "Edit Requirement Diagram", IconManager.imgic100, IconManager.imgic101, "Edit Requirement Diagram", "Make it possible to edit the currently opened Requirement Diagram", 0);
        actions[ARD_REQUIREMENT] = new TAction("add-ard-requirement", "Add a requirement", IconManager.imgic5006, IconManager.imgic5006, "Requirement", "Add a requirement to the currently opened requirement diagram", 0);
        actions[ARD_PROPERTY] = new TAction("add-ard-property", "Add a property", IconManager.imgic5008, IconManager.imgic5008, "Property", "Add a reference to a property of the currently opened requirement diagram", 0);
        actions[ARD_ELEMENT_REFERENCE] = new TAction("add-ard-element-reference", "Add a reference to an element", IconManager.imgic5068, IconManager.imgic5068, "Element Reference", "Add a reference to an element of the currently opened requirement diagram", 0);
        actions[ARD_DERIVE_CONNECTOR] = new TAction("add-ard-derive", "Add a <<deriveReqt>> link", IconManager.imgic1008, IconManager.imgic1008, "DeriveReqt", "Add a <<deriveReqt>> link between two requirements of the currently opened requirement diagram", 0);
        actions[ARD_SATISFY_CONNECTOR] = new TAction("add-ard-satisfy", "Add a <<satisfy>> link", IconManager.imgic1018, IconManager.imgic1018, "Satisfy", "Add a <<satisfy>> link between two requirements of the currently opened requirement diagram", 0);

        actions[ARD_REFINE_CONNECTOR] = new TAction("add-ard-refine", "Add a <<refine>> link", IconManager.imgic1016, IconManager.imgic1016, "refine", "Add a <<refine>> link between two requirements of the currently opened requirement diagram", 0);
        actions[ARD_VERIFY_CONNECTOR] = new TAction("add-ard-verify", "Add a <<verify>> link", IconManager.imgic1006, IconManager.imgic1006, "Verify", "Add a <<verify>> link between an observer and a requirement of the currently opened requirement diagram", 0);
        actions[ARD_COPY_CONNECTOR] = new TAction("add-ard-copy", "Add a <<copy>> link", IconManager.imgic1010, IconManager.imgic1010, "Copy", "Add a <<copy>> link between two requirements of the currently opened requirement diagram", 0);
        actions[ARD_COMPOSITION_CONNECTOR] = new TAction("add-ard-composition", "Add a  composition link", IconManager.imgic1012, IconManager.imgic1012, "Composition", "Add a composition link between two requirements of the currently opened requirement diagram", 0);

        // AVATAR Parametric Diagrams
        actions[APD_EDIT] = new TAction("edit-apd-diagram", "Edit AVATAR Parametric Diagrams", IconManager.imgic100, IconManager.imgic101, "Edit AVATAR Parametric Diagram", "Make it possible to edit the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_BLOCK] = new TAction("apd-block", "Add a block", IconManager.imgic5000, IconManager.imgic5000, "Block", "Add a SysML Block to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_LOGICAL_CONSTRAINT] = new TAction("apd-logical-constraint", "Add a Logical Constraint", IconManager.imgic5028, IconManager.imgic5028, "Logical Constraint", "Add a Logical Constraint to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_TEMPORAL_CONSTRAINT] = new TAction("apd-temporal-constraint", "Add a Temporal Constraint", IconManager.imgic5030, IconManager.imgic5030, "Temporal Constraint", "Add a Temporal Constraint to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_ATTRIBUTE] = new TAction("apd-attribute", "Add a attribute", IconManager.imgic5010, IconManager.imgic5010, "Attribute", "Add an attribute to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_SIGNAL] = new TAction("apd-signal", "Add a signal", IconManager.imgic5022, IconManager.imgic5022, "Signal", "Add an signal to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_ALIAS] = new TAction("apd-alias", "Add a alias", IconManager.imgic5032, IconManager.imgic5032, "Alias", "Add an alias to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_BOOLEQ] = new TAction("apd-booleq", "Add a boolean equation", IconManager.imgic5014, IconManager.imgic5014, "boolean equation", "Add a boolean equation to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_ATTRIBUTE_SETTING] = new TAction("apd-attribute-setting", "Setting attributes", IconManager.imgic5012, IconManager.imgic5012, "Setting attributes", "Add an attribute affectation to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_PROPERTY] = new TAction("apd-property", "Add a property", IconManager.imgic5026, IconManager.imgic5026, "Property", "Add a property to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_PROPERTY_RELATION] = new TAction("apd-property-relation", "Add a property relation", IconManager.imgic5024, IconManager.imgic5024, "Property relation", "Add a property relation to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_ATTRIBUTE_CONNECTOR] = new TAction("apd-attribute-connector", "Add a connector between attribute elements", IconManager.imgic5016, IconManager.imgic5016, "Connector between attributes", "Add a connector between attributes to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_SIGNAL_CONNECTOR] = new TAction("apd-signal-connector", "Add a connector between signal elements", IconManager.imgic5018, IconManager.imgic5018, "Connector between signals", "Add a connector between signals to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_PROPERTY_CONNECTOR] = new TAction("apd-property-connector", "Add a connector between property elements", IconManager.imgic5020, IconManager.imgic5020, "Connector between properties", "Add a connector between properties to the currently opened AVATAR Parametric Diagram", 0);
        actions[APD_COMPOSITION_CONNECTOR] = new TAction("apd-composition-connector", "Add a composition between blocks", IconManager.imgic5002, IconManager.imgic5002, "Composition between blocks", "Add a composition between blocks to the currently opened AVATAR Parametric Diagram", 0);

        // AVATAR Context Diagrams
        actions[ACD_EDIT] = new TAction("edit-acd-diagram", "Edit AVATAR Context Diagrams", IconManager.imgic100, IconManager.imgic101, "Edit AVATAR Context Diagram", "Make it possible to edit the currently opened AVATAR Context Diagram", 0);
        actions[ACD_BLOCK] = new TAction("acd-block", "Add a block", IconManager.imgic5000, IconManager.imgic5000, "Block", "Add a SysML Block to the currently opened AVATAR Context Diagram", 0);
        actions[ACD_COMPOSITION_CONNECTOR] = new TAction("acd-composition-connector", "Add a composition between blocks", IconManager.imgic5002, IconManager.imgic5002, "Composition between blocks", "Add a composition between blocks to the currently opened AVATAR Context Diagram", 0);
        actions[ACD_ASSOCIATION_CONNECTOR] = new TAction("acd-association-connector", "Add an association", IconManager.imgic5044, IconManager.imgic5044, "Association", "Add an association between blocks to the currently opened AVATAR Context Diagram", 0);
        actions[ACD_ACTOR_STICKMAN] = new TAction("acd-add-actor", "Add an actor (stickman format)", IconManager.imgic600, IconManager.imgic600, "Actor", "Add a stickman actor to the currently opened AVATAR Context diagram", 0);
        actions[ACD_ACTOR_BOX] = new TAction("acd-add-actor-box", "Add an actor (box format)", IconManager.imgic614, IconManager.imgic614, "Actor (box format)", "Add a box actor  to the currently opened AVATAR Context diagram", 0);

        // AVATAR Activity Diagrams
        actions[AAD_EDIT] = new TAction("edit-aad-diagram", "Edit AVATAR Activity Diagrams", IconManager.imgic100, IconManager.imgic101, "Edit AVATAR Activity Diagram", "Make it possible to edit the currently opened AVATAR Activity Diagram", 0);
        actions[AAD_ASSOCIATION_CONNECTOR] = new TAction("aad-association-connector", "Add an association", IconManager.imgic202, IconManager.imgic202, "Association", "Add an association between blocks to the currently opened AVATAR Activity Diagram", 0);
        actions[AAD_START_STATE] = new TAction("add-add-start", "Add Start state", IconManager.imgic222, IconManager.imgic222, "Start", "Add a start state to the currently opened activity diagram", 0);
        actions[AAD_STOP_STATE] = new TAction("add-aad-stop", "Add Stop", IconManager.imgic210, IconManager.imgic210, "Stop", "Add a termination state to the currently opened activity diagram", 0);
        actions[AAD_CHOICE] = new TAction("add-aad-choice", "Add Choice", IconManager.imgic208, IconManager.imgic208, "Choice", "Add a choice to the currently opened Avatar activity diagram", 0);
        actions[AAD_JUNCTION] = new TAction("add-aad-junction", "Junction", IconManager.imgic212, IconManager.imgic212, "Junction", "Add a junction with three inputs to the currently opened avatar activity diagram", 0);
        actions[AAD_PARALLEL] = new TAction("add-adparallel", "Parallel", IconManager.imgic206, IconManager.imgic206, "Parallel", "Add a parallel  operator to the currently opened avatar activity diagram", 0);
        actions[AAD_ACTIVITY] = new TAction("add-aad-activity", "Activity", IconManager.imgic5048, IconManager.imgic5048, "Activity", "Add a new activity to the currently opened AVATAR avatar activity diagram", 0);
        actions[AAD_ACTION] = new TAction("add-aad-action", "Action", IconManager.imgic204, IconManager.imgic204, "Action", "Add a new action to the currently opened AVATAR avatar activity diagram", 0);
        actions[AAD_STOP_FLOW] = new TAction("add-aad-stop-flow", "Stop flow", IconManager.imgic5046, IconManager.imgic5046, "Stop flow", "Add a stop flow state to the currently opened avatar activity diagram", 0);
        actions[AAD_SEND_SIGNAL_ACTION] = new TAction("add-add-send-signal-action", "Send signal", IconManager.imgic5050, IconManager.imgic5050, "Send signal", "Add a send signal operator to the currently opened avatar activity diagram", 0);
        actions[AAD_ACCEPT_EVENT_ACTION] = new TAction("add-add-accept-event-action", "Accept event", IconManager.imgic5056, IconManager.imgic5056, "Accept event", "Add an accept event operator to the currently opened avatar activity diagram", 0);
        actions[AAD_PARTITION] = new TAction("add-add-partition", "Partition", IconManager.imgic5052, IconManager.imgic5052, "Partition", "Add a partition to the currently opened avatar activity diagram", 0);
        actions[AAD_ALIGN_PARTITION] = new TAction("add-aad-align_partitions", "Align partitions", IconManager.imgic5054, IconManager.imgic5054, "Align partitions", "Align partitions of the currently opened avatar activity diagram", 0);

        // Ontologies
        actions[ACT_GENERATE_ONTOLOGIES_CURRENT_DIAGRAM] = new TAction("generate-ontology-current-diagram", "Generate ontology (current diagram)", IconManager.imgic338, IconManager.imgic339, "Generate ontology (current diagram)",  "Generate the ontology for the diagram under edition", 0);
        actions[ACT_GENERATE_ONTOLOGIES_CURRENT_SET_OF_DIAGRAMS] = new TAction("generate-ontology-current-set-of-diagrams", "Generate ontology (current set of diagrams)", IconManager.imgic338, IconManager.imgic339, "Generate ontology (current set of diagrams)",  "Generate the ontology for the current set of diagrams under edition", 0);
        actions[ACT_GENERATE_ONTOLOGIES_ALL_DIAGRAMS] = new TAction("generate-ontology-all-diagrams", "Generate ontology (all diagrams)", IconManager.imgic338, IconManager.imgic339, "Generate ontology (all diagrams)",  "Generate the ontology for the diagrams under edition", 0);

        actions[MOVE_ENABLED] = new TAction("Move", "Move enabled", IconManager.imgic780, IconManager.imgic780, "Move enabled (shift + arrow)", "Move", 0);
	actions[FIRST_DIAGRAM] = new TAction("FirstDiagram", "First Diagram", IconManager.imgic142, IconManager.imgic142, "Switch the the first diagram", "Switch to the first diagram", 0);
    }


    public String getActionCommand()  {
        return (String)getValue(Action.ACTION_COMMAND_KEY);
    }

    public String getShortDescription()  {
        return (String)getValue(Action.SHORT_DESCRIPTION);
    }

    public String getLongDescription()  {
        return (String)getValue(Action.LONG_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent evt)  {
        //
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();

            // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
                                            (String)getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length-2; i += 2) {
                ((ActionListener)listenerList[i+1]).actionPerformed(e);
            }
        }
    }

    public void addActionListener(ActionListener l)  {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        //TraceManager.addDev("Action listener added");
        listeners.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l)  {
        if (listeners == null) {
            return;
        }
        listeners.remove(ActionListener.class, l);
    }
}
