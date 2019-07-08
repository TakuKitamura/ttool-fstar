/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

import ui.ad.*;
import ui.atd.*;
import ui.avatarad.*;
import ui.avatarbd.*;
import ui.avatarcd.*;
import ui.avatardd.*;
import ui.avatarmad.*;
import ui.avatarmethodology.*;
import ui.avatarpd.*;
import ui.avatarrd.*;
import ui.avatarsmd.*;
import ui.cd.*;
import ui.dd.TDDArtifact;
import ui.dd.TDDNode;
import ui.dd.TGConnectorLinkNode;
import ui.diplodocusmethodology.*;
import ui.ebrdd.*;
import ui.eln.*;
import ui.eln.sca_eln.*;
import ui.eln.sca_eln_sca_de.*;
import ui.eln.sca_eln_sca_tdf.*;
import ui.ftd.*;
import ui.iod.*;
import ui.ncdd.*;
import ui.osad.*;
import ui.oscd.*;
import ui.procsd.*;
import ui.prosmd.*;
import ui.req.*;
import ui.syscams.*;
import ui.sysmlsecmethodology.*;
import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmlcompd.*;
import ui.tmlcp.*;
import ui.tmldd.*;
import ui.tmlsd.*;
import ui.ucd.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Vector;

// DIPLODOCUS

/**
 * Class TGComponentManager
 * Definition and creation of all possible graphical components of TTool
 * Creation: 21/12/2003
 *
 * @author Ludovic APVRILLE, Andrea ENRICI
 * @version 1.3 27/01/2016
 */
public class TGComponentManager {

    public static final int NONE = -1;
    public static final int TAD_DETERMINISTIC_DELAY = 11;
    public static final int TAD_PARALLEL = 12;
    public static final int TAD_SEQUENCE = 21;
    public static final int TAD_PREEMPTION = 22;
    public static final int TAD_STOP_STATE = 13;
    public static final int TAD_START_STATE = 14;
    public static final int TAD_ACTION_STATE = 15;
    public static final int TAD_TIME_LIMITED_OFFER = 16;
    public static final int TAD_JUNCTION = 17;
    public static final int TAD_NON_DETERMINISTIC_DELAY = 18;
    public static final int TAD_DELAY_NON_DETERMINISTIC_DELAY = 20;
    public static final int TAD_CHOICE = 19;
    public static final int TAD_TIME_LIMITED_OFFER_WITH_LATENCY = 10;
    public static final int TAD_TIME_CAPTURE = 27;
    public static final int TAD_ARRAY_GET = 25;
    public static final int TAD_ARRAY_SET = 26;


    public static final int CONNECTOR_AD_DIAGRAM = 101;
    public static final int CONNECTOR_ASSOCIATION = 102;
    public static final int CONNECTOR_ATTRIBUTE = 103;
    public static final int CONNECTOR_ASSOCIATION_NAVIGATION = 104;
    public static final int CONNECTOR_INTERACTION = 105;
    public static final int CONNECTOR_MESSAGE_ASYNC_SD = 106;
    public static final int CONNECTOR_MESSAGE_SYNC_SD = 107;
    public static final int CONNECTOR_RELATIVE_TIME_SD = 109;
    public static final int CONNECTOR_ACTOR_UCD = 110;
    public static final int CONNECTOR_INCLUDE_UCD = 111;
    public static final int CONNECTOR_SPECIA_UCD = 112;
    public static final int CONNECTOR_EXTEND_UCD = 113;
    public static final int CONNECTOR_NODE_DD = 114;

    public static final int CONNECTOR_TMLAD = 115;
    public static final int CONNECTOR_TML_ASSOCIATION_NAV = 116;
    public static final int CONNECTOR_TML_COMPOSITION_OPERATOR = 117;

    public static final int CONNECTOR_COMMENT = 118;

    public static final int CONNECTOR_DERIVE_REQ = 119;
    public static final int CONNECTOR_COPY_REQ = 128;
    public static final int CONNECTOR_COMPOSITION_REQ = 129;
    public static final int CONNECTOR_VERIFY_REQ = 120;

    public static final int CONNECTOR_PROSMD = 121;
    public static final int CONNECTOR_PROCSD = 122;
    public static final int CONNECTOR_DELEGATE_PROCSD = 123;
    public static final int CONNECTOR_PROCSD_PORT_INTERFACE = 124;

    public static final int CONNECTOR_NODE_TMLARCHI = 125;

    public static final int CONNECTOR_PORT_TMLC = 126;

    public static final int CONNECTOR_NODE_NC = 127;

    public static final int CONNECTOR_EBRDD = 130;
    public static final int CONNECTOR_EBRDD_ERC = 131;

    public static final int ATD_COMPOSITION_CONNECTOR = 132;
    public static final int ATD_ATTACK_CONNECTOR = 133;
    public static final int ATD_COUNTERMEASURE_CONNECTOR = 137;

    public static final int FTD_COMPOSITION_CONNECTOR = 6000;
    public static final int FTD_FAULT_CONNECTOR = 6001;
    public static final int FTD_COUNTERMEASURE_CONNECTOR = 6002;

    public static final int CONNECTOR_MESSAGE_ASYNC_SDZV = 134;
    public static final int CONNECTOR_MESSAGE_SYNC_SDZV = 135;
    public static final int CONNECTOR_RELATIVE_TIME_SDZV = 136;

    public static final int TCD_TCLASS = 201;
    public static final int TCD_PARALLEL_OPERATOR = 202;
    public static final int TCD_SEQUENCE_OPERATOR = 203;
    public static final int TCD_PREEMPTION_OPERATOR = 204;
    public static final int TCD_SYNCHRO_OPERATOR = 205;
    public static final int TCD_INVOCATION_OPERATOR = 206;
    public static final int TCD_TOBJECT = 207;
    public static final int TCD_WATCHDOG_OPERATOR = 208;
    public static final int TCD_TDATA = 209;

    public static final int UML_NOTE = 301;
    public static final int PRAGMA = 302;
    public static final int SAFETY_PRAGMA = 303;
    public static final int INFO_PANEL = 304;
    public static final int AVATAR_FIREWALL = 306;
    public static final int PERFORMANCE_PRAGMA = 307;

    public static final int IOD_CHOICE = 501;
    public static final int IOD_START_STATE = 502;
    public static final int IOD_STOP_STATE = 503;
    public static final int IOD_JUNCTION = 504;
    public static final int IOD_PARALLEL = 505;
    public static final int IOD_REF_SD = 506;
    public static final int IOD_PREEMPTION = 507;
    public static final int IOD_SEQUENCE = 509;
    public static final int IOD_REF_IOD = 508;

    public static final int SD_INSTANCE = 606;
    public static final int SD_ABSOLUTE_TIME_CONSTRAINT = 607;
    public static final int SD_RELATIVE_TIME_CONSTRAINT = 601;
    public static final int SD_ACTION_STATE = 602;
    public static final int SD_GUARD = 609;
    public static final int SD_TIMER_SETTING = 603;
    public static final int SD_TIMER_CANCELLATION = 604;
    public static final int SD_TIMER_EXPIRATION = 605;
    public static final int SD_TIME_INTERVAL = 608;
    public static final int SD_COREGION = 600;


    public static final int SDZV_INSTANCE = 620;
    public static final int SDZV_ABSOLUTE_TIME_CONSTRAINT = 621;
    public static final int SDZV_RELATIVE_TIME_CONSTRAINT = 622;
    public static final int SDZV_ACTION_STATE = 623;
    public static final int SDZV_GUARD = 624;
    public static final int SDZV_TIMER_SETTING = 625;
    public static final int SDZV_TIMER_CANCELLATION = 626;
    public static final int SDZV_TIMER_EXPIRATION = 627;
    public static final int SDZV_TIME_INTERVAL = 628;
    public static final int SDZV_COREGION = 629;
    public static final int SDZV_PORT_MESSAGE = 630;

    public static final int UCD_ACTOR = 700;
    public static final int UCD_ACTORBOX = 703;
    public static final int UCD_USECASE = 701;
    public static final int UCD_BORDER = 702;

    public static final int TDD_NODE = 800;
    public static final int TDD_ARTIFACT = 801;

    public static final int DIPLODODUSMETHODOLOGY_REF_APPLICATION = 6000;
    public static final int DIPLODODUSMETHODOLOGY_REF_ARCHITECTURE = 6001;
    public static final int DIPLODODUSMETHODOLOGY_REF_MAPPING = 6002;
    public static final int DIPLODODUSMETHODOLOGY_REF_REQUIREMENT = 6003;
    public static final int DIPLODOCUSMETHODOLOGY_CONNECTOR = 6004;
    public static final int DIPLODODUSMETHODOLOGY_DIAGRAM_NAME = 6005;
    public static final int DIPLODODUSMETHODOLOGY_REF_CP = 6006;


    public static final int TMLAD_START_STATE = 1000;
    public static final int TMLAD_STOP_STATE = 1001;
    public static final int TMLTD_TASK = 1002;
    public static final int TMLTD_CHANNEL_OPERATOR = 1003;
    public static final int TMLTD_REQUEST_OPERATOR = 1004;
    public static final int TMLTD_EVENT_OPERATOR = 1005;
    public static final int TMLAD_WRITE_CHANNEL = 1006;
    public static final int TMLAD_SEND_REQUEST = 1007;
    public static final int TMLAD_SEND_EVENT = 1008;
    public static final int TMLAD_READ_CHANNEL = 1009;
    public static final int TMLAD_WAIT_EVENT = 1010;
    public static final int TMLAD_NOTIFIED_EVENT = 1017;
    public static final int TMLAD_ACTION_STATE = 1011;
    public static final int TMLAD_CHOICE = 1012;
    public static final int TMLAD_EXECI = 1013;
    public static final int TMLAD_FOR_LOOP = 1014;
    public static final int TMLAD_EXECI_INTERVAL = 1015;
    public static final int TMLAD_SEQUENCE = 1016;
    public static final int TMLAD_UNORDERED_SEQUENCE = 1032;
    public static final int TMLAD_SELECT_EVT = 1018;
    public static final int TMLAD_FOR_STATIC_LOOP = 1020;
    public static final int TMLAD_EXECC = 1021;
    public static final int TMLAD_EXECC_INTERVAL = 1022;
    public static final int TMLAD_RANDOM = 1024;
    public static final int TMLAD_DELAY = 1026;
    public static final int TMLAD_INTERVAL_DELAY = 1028;
    public static final int TMLAD_FOR_EVER_LOOP = 1030;
    public static final int TMLAD_READ_REQUEST_ARG = 1034;
    public static final int TMLAD_ENCRYPT = 1035;
    public static final int TMLAD_DECRYPT = 1036;

    public static final int TMLARCHI_CPUNODE = 1100;
    public static final int TMLARCHI_ARTIFACT = 1101;
    public static final int TMLARCHI_BUSNODE = 1102;
    public static final int TMLARCHI_COMMUNICATION_ARTIFACT = 1103;
    public static final int TMLARCHI_HWANODE = 1104;
    public static final int TMLARCHI_CAMSNODE = 1115;
    public static final int TMLARCHI_MEMORYNODE = 1105;
    public static final int TMLARCHI_BRIDGENODE = 1106;
    public static final int TMLARCHI_DMANODE = 1107;
    public static final int TMLARCHI_CPNODE = 1108;
    public static final int TMLARCHI_EVENT_ARTIFACT = 1109;
    public static final int TMLARCHI_PORT_ARTIFACT = 1110;
    public static final int TMLARCHI_KEY = 1111;
    public static final int TMLARCHI_VGMNNODE = 1112;
    public static final int TMLARCHI_CROSSBARNODE = 1113;
    public static final int TMLARCHI_FIREWALL = 1114;
    public static final int TMLARCHI_FPGANODE = 1116;
    public static final int TMLARCHI_ROUTERNODE = 1117;

    public static final int TMLCTD_CCOMPONENT = 1200;
    public static final int TMLCTD_CPORT = 1201;
    public static final int TMLCTD_PCOMPONENT = 1202;
    public static final int TMLCTD_COPORT = 1203;
    public static final int TMLCTD_CREMOTECOMPONENT = 1204;
    public static final int TMLCTD_RCOMPONENT = 1205;
    public static final int TMLCTD_CREMOTEPORTCOMPONENT = 1206;
    public static final int TMLCTD_FORK = 1207;
    public static final int TMLCTD_JOIN = 1208;

    public static final int EBRDD_START_STATE = 1300;
    public static final int EBRDD_STOP_STATE = 1301;
    public static final int EBRDD_CHOICE = 1302;
    public static final int EBRDD_ERC = 1303;
    public static final int EBRDD_ACTION = 1304;
    public static final int EBRDD_FOR_LOOP = 1305;
    public static final int EBRDD_SEQUENCE = 1306;
    public static final int EBRDD_ESO = 1307;
    public static final int EBRDD_ERB = 1308;
    public static final int EBRDD_VARIABLE_DECLARATION = 1309;

    public static final int TREQ_REQUIREMENT = 900;
    public static final int TREQ_OBSERVER = 901;
    public static final int TREQ_EBRDD = 902;

    public static final int ATD_BLOCK = 1400;
    public static final int ATD_ATTACK = 1401;
    public static final int ATD_CONSTRAINT = 1402;
    public static final int ATD_COUNTERMEASURE = 1403;

    public static final int FTD_BLOCK = 6100;
    public static final int FTD_FAULT = 6101;
    public static final int FTD_CONSTRAINT = 6102;
    public static final int FTD_COUNTERMEASURE = 6103;

    // TML Communication patterns and TMLSD
    public static final int TMLCP_CHOICE = 1500;
    public static final int CONNECTOR_TMLCP = 1501;
    public static final int TMLCP_FORK = 1502;
    public static final int TMLCP_JOIN = 1503;
    public static final int TMLCP_REF_CP = 1504;
    public static final int TMLCP_REF_SD = 1505;
    public static final int TMLCP_START_STATE = 1506;
    public static final int TMLCP_STOP_STATE = 1507;

    // Issue #69
   // public static final int TMLCP_JUNCTION = 1508;
    public static final int TMLCP_FOR_LOOP = 1510;

    public static final int TMLSD_STORAGE_INSTANCE = 1520;
    public static final int TMLSD_TRANSFER_INSTANCE = 1521;
    public static final int TMLSD_CONTROLLER_INSTANCE = 1522;
    public static final int CONNECTOR_MESSAGE_ASYNC_TMLSD = 1523;
    public static final int TMLSD_ACTION_STATE = 1524;

    // SystemC-AMS
    public static final int CAMS_CONNECTOR = 1601;
    public static final int CAMS_BLOCK_TDF = 1602;
    public static final int CAMS_BLOCK_DE = 1603;
    public static final int CAMS_PORT_TDF = 1604;
    public static final int CAMS_PORT_DE = 1605;
    public static final int CAMS_PORT_CLOCK = 5722;
    public static final int CAMS_PORT_CONVERTER = 1606;
    public static final int CAMS_CLUSTER = 1607;
    public static final int CAMS_BLOCK_GPIO2VCI = 1608;
    public static final int CAMS_CLOCK = 5721;
    // ELN
    public static final int ELN_CONNECTOR = 1610;
    public static final int ELN_RESISTOR = 1611;
    public static final int ELN_CAPACITOR = 1612;
    public static final int ELN_INDUCTOR = 1613;
    public static final int ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE = 1614;
    public static final int ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE = 1615;
    public static final int ELN_IDEAL_TRANSFORMER = 1616;
    public static final int ELN_TRANSMISSION_LINE = 1617;
    public static final int ELN_INDEPENDENT_VOLTAGE_SOURCE = 1618;
    public static final int ELN_INDEPENDENT_CURRENT_SOURCE = 1619;
    public static final int ELN_NODE_REF = 1620;
    public static final int ELN_TDF_VOLTAGE_SINK = 1621;
    public static final int ELN_TDF_CURRENT_SINK = 1622;
    public static final int ELN_MODULE = 1623;
    public static final int ELN_MODULE_TERMINAL = 1624;
    public static final int ELN_TDF_VOLTAGE_SOURCE = 1625;
    public static final int ELN_TDF_CURRENT_SOURCE = 1626;
    public static final int ELN_CLUSTER = 1627;
    public static final int ELN_MODULE_PORT_DE = 1628;
    public static final int ELN_MODULE_PORT_TDF = 1629;
    public static final int ELN_CLUSTER_TERMINAL = 1630;
    public static final int ELN_CLUSTER_PORT_DE = 1631;
    public static final int ELN_CLUSTER_PORT_TDF = 1632;
    public static final int ELN_DE_CURRENT_SINK = 1633;
    public static final int ELN_DE_CURRENT_SOURCE = 1634;
    public static final int ELN_DE_VOLTAGE_SINK = 1635;
    public static final int ELN_DE_VOLTAGE_SOURCE = 1636;
    
    // SMD diagram
    public static final int PROSMD_START_STATE = 2000;
    public static final int PROSMD_STOP_STATE = 2001;
    public static final int PROSMD_SENDMSG = 2002;
    public static final int PROSMD_GETMSG = 2004;
    public static final int PROSMD_CHOICE = 2006;
    public static final int PROSMD_JUNCTION = 2008;
    public static final int PROSMD_SUBMACHINE = 2010;
    public static final int PROSMD_ACTION = 2012;
    public static final int PROSMD_PARALLEL = 2014;
    public static final int PROSMD_STATE = 2016;
    // CSD diagram
    public static final int PROCSD_COMPONENT = 2100;
    //No more delegate ports, by Solange
    //public static final int PROCSD_DELEGATE_PORT = 2102;
    public static final int PROCSD_INTERFACE = 2104;
    public static final int PROCSD_IN_PORT = 2106;
    public static final int PROCSD_OUT_PORT = 2108;

    // TURTLE-OS
    public static final int TOSCD_TCLASS = 3000;
    public static final int TOS_CONNECTOR_ATTRIBUTE = 3001;
    public static final int TOS_CONNECTOR_ASSOCIATION_NAVIGATION = 3002;
    public static final int TOS_CONNECTOR_ASSOCIATION = 3003;
    public static final int TOSCD_CALL_OPERATOR = 3004;
    public static final int TOSCD_EVT_OPERATOR = 3013;

    public static final int TOSAD_ACTION_STATE = 3005;
    public static final int TOSAD_CONNECTOR = 3006;
    public static final int TOSAD_CHOICE = 3007;
    public static final int TOSAD_START_STATE = 3008;
    public static final int TOSAD_STOP_STATE = 3009;
    public static final int TOSAD_JUNCTION = 3010;
    public static final int TOSAD_TIME_INTERVAL = 3011;
    public static final int TOSAD_INT_TIME_INTERVAL = 3012;

    // NC
    public static final int NCDD_EQNODE = 4000;
    public static final int NCDD_SWITCHNODE = 4001;
    public static final int NCDD_TRAFFIC_ARTIFACT = 4002;
    public static final int NCDD_ROUTE_ARTIFACT = 4003;

    // AVATAR BD -> starts at 5000
    public static final int AVATARBD_BLOCK = 5000;
    public static final int AVATARBD_COMPOSITION_CONNECTOR = 5001;
    public static final int AVATARBD_PORT_CONNECTOR = 5002;
    public static final int AVATARBD_DATATYPE = 5003;
    public static final int AVATARBD_CRYPTOBLOCK = 5004;
    public static final int AVATARBD_LIBRARYFUNCTION = 5005;
    public static final int AVATARBD_CRYPTOLIBRARYFUNCTION = 5006;

    // AVATAR SMD -> starts at 5100
    public static final int AVATARSMD_START_STATE = 5100;
    public static final int AVATARSMD_STOP_STATE = 5101;
    public static final int AVATARSMD_CONNECTOR = 5102;
    public static final int AVATARSMD_SEND_SIGNAL = 5103;
    public static final int AVATARSMD_RECEIVE_SIGNAL = 5104;
    
    // Issue #69
    //public static final int AVATARSMD_PARALLEL = 5105;
    
    public static final int AVATARSMD_STATE = 5106;
    public static final int AVATARSMD_CHOICE = 5107;
    public static final int AVATARSMD_RANDOM = 5108;
    public static final int AVATARSMD_SET_TIMER = 5109;
    public static final int AVATARSMD_RESET_TIMER = 5110;
    public static final int AVATARSMD_EXPIRE_TIMER = 5111;
    public static final int AVATARSMD_LIBRARY_FUNCTION_CALL = 5112;

    // AVATAR RD -> starts at 5200
    public static final int AVATARRD_REQUIREMENT = 5200;
    public static final int AVATARRD_REQUIREMENT_REFERENCE = 5208;
    public static final int AVATARRD_PROPERTY = 5201;
    public static final int AVATARRD_ELEMENT_REFERENCE = 5207;
    public static final int AVATARRD_DERIVE_CONNECTOR = 5202;
    public static final int AVATARRD_SATISFY_CONNECTOR = 5208;
    public static final int AVATARRD_REFINE_CONNECTOR = 5206;
    public static final int AVATARRD_VERIFY_CONNECTOR = 5203;
    public static final int AVATARRD_COPY_CONNECTOR = 5204;
    public static final int AVATARRD_COMPOSITION_CONNECTOR = 5205;


    // AVATAR AMD -> starts at 5250
    public static final int AVATARMAD_ASSUMPTION = 5250;
    public static final int AVATARMAD_DIAGRAM_REFERENCE = 5251;
    public static final int AVATARMAD_ELEMENT_REFERENCE = 5252;
    public static final int AVATARMAD_COMPOSITION_CONNECTOR = 5253;
    public static final int AVATARMAD_VERSIONING_CONNECTOR = 5254;
    public static final int AVATARMAD_IMPACT_CONNECTOR = 5255;
    public static final int AVATARMAD_MEET_CONNECTOR = 5256;
    public static final int AVATARMAD_BELONGSTOCOMPOSITION_CONNECTOR = 5257;

    // AVATAR PD -> starts at 5300
    public static final int APD_BLOCK = 5300;
    public static final int APD_LOGICAL_CONSTRAINT = 5301;
    public static final int APD_TEMPORAL_CONSTRAINT = 5302;
    public static final int APD_ATTRIBUTE = 5303;
    public static final int APD_SIGNAL = 5304;
    public static final int APD_ALIAS = 5305;
    public static final int APD_BOOLEQ = 5306;
    public static final int APD_ATTRIBUTE_SETTING = 5307;
    public static final int APD_PROPERTY = 5308;
    public static final int APD_PROPERTY_RELATION = 5309;
    public static final int APD_ATTRIBUTE_CONNECTOR = 5310;
    public static final int APD_SIGNAL_CONNECTOR = 5311;
    public static final int APD_PROPERTY_CONNECTOR = 5312;
    public static final int APD_COMPOSITION_CONNECTOR = 5313;

    // Avatar Deployment Diagrams at 5350
    public static final int ADD_CONNECTOR = 5350;
    public static final int ADD_CPUNODE = 5351;
    public static final int ADD_ARTIFACT = 5352;
    public static final int ADD_BUSNODE = 5353;
    public static final int ADD_TTYNODE = 5354;
    public static final int ADD_RAMNODE = 5355;
    public static final int ADD_ROMNODE = 5356;
    public static final int ADD_BRIDGENODE = 5357;
    public static final int ADD_DMANODE = 5358;
    public static final int ADD_ICUNODE = 5359;
    public static final int ADD_COPROMWMRNODE = 5360;
    public static final int ADD_TIMERNODE = 5361;
    public static final int ADD_CHANNELARTIFACT = 5362;
    public static final int ADD_VGMNNODE = 5363;
    public static final int ADD_CROSSBARNODE = 5364;
    public static final int ADD_CLUSTERNODE = 5365;

    // AVATAR CD -> starts at 5400
    public static final int ACD_BLOCK = 5400;
    public static final int ACD_ACTOR_STICKMAN = 5401;
    public static final int ACD_ACTOR_BOX = 5402;
    public static final int ACD_COMPOSITION_CONNECTOR = 5403;
    public static final int ACD_ASSOCIATION_CONNECTOR = 5404;

    // AVATAR AD -> starts at 5500
    public static final int AAD_ASSOCIATION_CONNECTOR = 5500;
    public static final int AAD_START_STATE = 5501;
    public static final int AAD_STOP_STATE = 5502;
    public static final int AAD_CHOICE = 5503;
    public static final int AAD_JUNCTION = 5504;
    public static final int AAD_PARALLEL = 5505;
    public static final int AAD_ACTION = 5506;
    public static final int AAD_ACTIVITY = 5507;
    public static final int AAD_STOP_FLOW = 5508;
    public static final int AAD_SEND_SIGNAL_ACTION = 5509;
    public static final int AAD_ACCEPT_EVENT_ACTION = 5510;
    public static final int AAD_PARTITION = 5511;


    // Avatar Methodology Diagrams at 5600
    public static final int AVATARMETHODOLOGY_REF_ASSUMPTIONS = 5601;
    public static final int AVATARMETHODOLOGY_REF_REQUIREMENT = 5602;
    public static final int AVATARMETHODOLOGY_REF_ANALYSIS = 5603;
    public static final int AVATARMETHODOLOGY_REF_DESIGN = 5604;
    public static final int AVATARMETHODOLOGY_REF_PROPERTIES = 5605;
    public static final int AVATARMETHODOLOGY_REF_PROTOTYPE = 5606;
    public static final int AVATARMETHODOLOGY_DIAGRAM_NAME = 5607;
    public static final int AVATARMETHODOLOGY_CONNECTOR = 5608;

    // SysML-Sec Methodology Diagrams at 5700
    public static final int SYSMLSEC_METHODOLOGY_REF_ASSUMPTIONS = 5701;
    public static final int SYSMLSEC_METHODOLOGY_REF_REQUIREMENT = 5702;
    public static final int SYSMLSEC_METHODOLOGY_REF_ANALYSIS = 5703;
    public static final int SYSMLSEC_METHODOLOGY_REF_DESIGN = 5704;
    public static final int SYSMLSEC_METHODOLOGY_REF_PROPERTIES = 5705;
    public static final int SYSMLSEC_METHODOLOGY_REF_PROTOTYPE = 5706;
    public static final int SYSMLSEC_METHODOLOGY_DIAGRAM_NAME = 5707;
    public static final int SYSMLSEC_METHODOLOGY_REF_FUNCTIONAL_VIEW = 5713;
    public static final int SYSMLSEC_METHODOLOGY_REF_ARCHITECTURE_VIEW = 5709;
    public static final int SYSMLSEC_METHODOLOGY_REF_MAPPING_VIEW = 5710;
    public static final int SYSMLSEC_METHODOLOGY_REF_CP_VIEW = 5711;
    public static final int SYSMLSEC_METHODOLOGY_REF_ATTACK = 5712;
    public static final int SYSMLSEC_METHODOLOGY_REF_FAULT = 5719;
    public static final int SYSMLSEC_METHODOLOGY_CONNECTOR = 5718;

    // PLUGIN
    public static final int COMPONENT_PLUGIN = 10000;

    public static final int EDIT = -1;
    public static final int COMPONENT = 0;
    public static final int CONNECTOR = 1;


    public static LinkedList<ADDConnector> addconnectors = new LinkedList<ADDConnector>();


    public static LinkedList<ADDConnector> getAllADDConnectors() {
        return addconnectors;
    }

    public final static TGComponent addComponent(int x, int y, int id, TDiagramPanel tdp) {
        TGComponent tgc = null;
        switch (id) {
            // PLUGIN
            case COMPONENT_PLUGIN:
                tgc = new TGComponentPlugin(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            // AVATAR
            case AVATARBD_BLOCK:
                tgc = new AvatarBDBlock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARBD_CRYPTOBLOCK:
                tgc = new AvatarBDBlock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                ((AvatarBDBlock) tgc).addCryptoElements();
                break;
            case AVATARBD_DATATYPE:
                tgc = new AvatarBDDataType(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARBD_LIBRARYFUNCTION:
                tgc = new AvatarBDLibraryFunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARBD_CRYPTOLIBRARYFUNCTION:
                tgc = new AvatarBDLibraryFunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                ((AvatarBDLibraryFunction) tgc).addCryptoElements();
                break;
            case AVATARSMD_START_STATE:
                tgc = new AvatarSMDStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_STOP_STATE:
                tgc = new AvatarSMDStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_SEND_SIGNAL:
                tgc = new AvatarSMDSendSignal(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_LIBRARY_FUNCTION_CALL:
                tgc = new AvatarSMDLibraryFunctionCall(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_RECEIVE_SIGNAL:
                tgc = new AvatarSMDReceiveSignal(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
//              case AVATARSMD_PARALLEL: Issue #69
//              tgc = new AvatarSMDParallel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
//              break;
            case AVATARSMD_STATE:
                tgc = new AvatarSMDState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_CHOICE:
                tgc = new AvatarSMDChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_RANDOM:
                tgc = new AvatarSMDRandom(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_SET_TIMER:
                tgc = new AvatarSMDSetTimer(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_RESET_TIMER:
                tgc = new AvatarSMDResetTimer(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARSMD_EXPIRE_TIMER:
                tgc = new AvatarSMDExpireTimer(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            case AVATARRD_REQUIREMENT:
                tgc = new AvatarRDRequirement(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARRD_REQUIREMENT_REFERENCE:
                tgc = new AvatarRDRequirementReference(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARRD_PROPERTY:
                tgc = new AvatarRDProperty(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARRD_ELEMENT_REFERENCE:
                tgc = new AvatarRDElementReference(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            case AVATARMAD_ASSUMPTION:
                tgc = new AvatarMADAssumption(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMAD_DIAGRAM_REFERENCE:
                tgc = new AvatarMADDiagramReference(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMAD_ELEMENT_REFERENCE:
                tgc = new AvatarMADElementReference(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            // AVATAR PD
            case APD_BLOCK:
                tgc = new AvatarPDBlock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_LOGICAL_CONSTRAINT:
                tgc = new AvatarPDLogicalConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_TEMPORAL_CONSTRAINT:
                tgc = new AvatarPDTemporalConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_ATTRIBUTE:
                tgc = new AvatarPDAttribute(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_SIGNAL:
                tgc = new AvatarPDSignal(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_ALIAS:
                tgc = new AvatarPDAlias(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_BOOLEQ:
                tgc = new AvatarPDBoolEq(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_ATTRIBUTE_SETTING:
                tgc = new AvatarPDAttributeSetting(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_PROPERTY:
                tgc = new AvatarPDProperty(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case APD_PROPERTY_RELATION:
                tgc = new AvatarPDPropertyRelation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            // AVATAR CD
            case ACD_BLOCK:
                tgc = new AvatarCDBlock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ACD_ACTOR_STICKMAN:
                tgc = new AvatarCDActorStickman(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ACD_ACTOR_BOX:
                tgc = new AvatarCDActorBox(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            // AVATAR AD
            case AAD_START_STATE:
                tgc = new AvatarADStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_STOP_STATE:
                tgc = new AvatarADStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_CHOICE:
                tgc = new AvatarADChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_JUNCTION:
                tgc = new AvatarADJunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_PARALLEL:
                tgc = new AvatarADParallel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_ACTION:
                tgc = new AvatarADAction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_ACTIVITY:
                tgc = new AvatarADActivity(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_STOP_FLOW:
                tgc = new AvatarADStopFlow(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_SEND_SIGNAL_ACTION:
                tgc = new AvatarADSendSignalAction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_ACCEPT_EVENT_ACTION:
                tgc = new AvatarADAcceptEventAction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AAD_PARTITION:
                tgc = new AvatarADPartition(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            //AVATAR DD
            case ADD_CPUNODE:
                tgc = new ADDCPUNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_BUSNODE:
                tgc = new ADDBusNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_VGMNNODE:
                tgc = new ADDVgmnNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_CROSSBARNODE:
                tgc = new ADDCrossbarNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_TTYNODE:
                tgc = new ADDTTYNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_BRIDGENODE:
                tgc = new ADDBridgeNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_RAMNODE:
                tgc = new ADDRAMNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_ROMNODE:
                tgc = new ADDROMNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_DMANODE:
                tgc = new ADDDMANode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_ICUNODE:
                tgc = new ADDICUNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_COPROMWMRNODE:
                tgc = new ADDCoproMWMRNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_TIMERNODE:
                tgc = new ADDTimerNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_ARTIFACT:
                tgc = new ADDBlockArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_CHANNELARTIFACT:
                tgc = new ADDChannelArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ADD_CLUSTERNODE:
                tgc = new ADDClusterNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            //AVATAR Methodology
            case AVATARMETHODOLOGY_REF_ASSUMPTIONS:
                tgc = new AvatarMethodologyReferenceToAssumptions(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMETHODOLOGY_REF_REQUIREMENT:
                tgc = new AvatarMethodologyReferenceToRequirement(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMETHODOLOGY_REF_ANALYSIS:
                tgc = new AvatarMethodologyReferenceToAnalysis(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMETHODOLOGY_REF_DESIGN:
                tgc = new AvatarMethodologyReferenceToDesign(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMETHODOLOGY_REF_PROPERTIES:
                tgc = new AvatarMethodologyReferenceToProperties(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMETHODOLOGY_REF_PROTOTYPE:
                tgc = new AvatarMethodologyReferenceToPrototype(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATARMETHODOLOGY_DIAGRAM_NAME:
                tgc = new AvatarMethodologyDiagramName(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            //SysML-Sec Methodology
            case SYSMLSEC_METHODOLOGY_REF_ASSUMPTIONS:
                tgc = new SysmlsecMethodologyReferenceToAssumptions(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_REQUIREMENT:
                tgc = new SysmlsecMethodologyReferenceToRequirement(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_ANALYSIS:
                tgc = new SysmlsecMethodologyReferenceToAnalysis(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_DESIGN:
                tgc = new SysmlsecMethodologyReferenceToDesign(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_PROPERTIES:
                tgc = new SysmlsecMethodologyReferenceToProperties(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_PROTOTYPE:
                tgc = new SysmlsecMethodologyReferenceToPrototype(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_FUNCTIONAL_VIEW:
                tgc = new SysmlsecMethodologyReferenceToApplication(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_ARCHITECTURE_VIEW:
                tgc = new SysmlsecMethodologyReferenceToArchitecture(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_MAPPING_VIEW:
                tgc = new SysmlsecMethodologyReferenceToMapping(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_CP_VIEW:
                tgc = new SysmlsecMethodologyReferenceToCP(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_ATTACK:
                tgc = new SysmlsecMethodologyReferenceToAttack(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_REF_FAULT:
                tgc = new SysmlsecMethodologyReferenceToFault(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SYSMLSEC_METHODOLOGY_DIAGRAM_NAME:
                tgc = new SysmlsecMethodologyDiagramName(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;


            // Others
            case TAD_DETERMINISTIC_DELAY:
                tgc = new TADDeterministicDelay(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_PARALLEL:
                tgc = new TADParallel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_SEQUENCE:
                tgc = new TADSequence(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_PREEMPTION:
                tgc = new TADPreemption(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_STOP_STATE:
                tgc = new TADStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_START_STATE:
                tgc = new TADStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_ACTION_STATE:
                tgc = new TADActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_ARRAY_GET:
                tgc = new TADArrayGetState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_ARRAY_SET:
                tgc = new TADArraySetState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_TIME_LIMITED_OFFER:
                tgc = new TADTimeLimitedOffer(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_JUNCTION:
                tgc = new TADJunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_NON_DETERMINISTIC_DELAY:
                tgc = new TADNonDeterministicDelay(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_DELAY_NON_DETERMINISTIC_DELAY:
                tgc = new TADTimeInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_TIME_LIMITED_OFFER_WITH_LATENCY:
                tgc = new TADTimeLimitedOfferWithLatency(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_TIME_CAPTURE:
                tgc = new TADTimeCapture(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TAD_CHOICE:
                tgc = new TADChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_TCLASS:
                tgc = new TCDTClass(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_TOBJECT:
                tgc = new TCDTObject(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_TDATA:
                tgc = new TCDTData(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_PARALLEL_OPERATOR:
                tgc = new TCDParallelOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_SEQUENCE_OPERATOR:
                tgc = new TCDSequenceOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_PREEMPTION_OPERATOR:
                tgc = new TCDPreemptionOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_SYNCHRO_OPERATOR:
                tgc = new TCDSynchroOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_INVOCATION_OPERATOR:
                tgc = new TCDInvocationOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TCD_WATCHDOG_OPERATOR:
                tgc = new TCDWatchdogOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case UML_NOTE:
                tgc = new TGCNote(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case INFO_PANEL:
                tgc = new TGCPanelInfo(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PRAGMA:
                tgc = new AvatarBDPragma(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SAFETY_PRAGMA:
                tgc = new AvatarBDSafetyPragma(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PERFORMANCE_PRAGMA:
                tgc = new AvatarBDPerformancePragma(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case AVATAR_FIREWALL:
                tgc = new AvatarBDFirewall(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_CHOICE:
                tgc = new IODChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_START_STATE:
                tgc = new IODStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_STOP_STATE:
                tgc = new IODStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_JUNCTION:
                tgc = new IODJunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_PARALLEL:
                tgc = new IODParallel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_REF_SD:
                tgc = new IODRefSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_REF_IOD:
                tgc = new IODRefIOD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_PREEMPTION:
                tgc = new IODPreemption(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case IOD_SEQUENCE:
                tgc = new IODSequence(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_INSTANCE:
                tgc = new ui.sd.SDInstance(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_ABSOLUTE_TIME_CONSTRAINT:
                tgc = new ui.sd.SDAbsoluteTimeConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_RELATIVE_TIME_CONSTRAINT:
                tgc = new ui.sd.SDRelativeTimeConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_ACTION_STATE:
                tgc = new ui.sd.SDActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_GUARD:
                tgc = new ui.sd.SDGuard(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIME_INTERVAL:
                tgc = new ui.sd.SDTimeInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIMER_SETTING:
                tgc = new ui.sd.SDTimerSetting(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIMER_EXPIRATION:
                tgc = new ui.sd.SDTimerExpiration(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIMER_CANCELLATION:
                tgc = new ui.sd.SDTimerCancellation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_COREGION:
                tgc = new ui.sd.SDCoregion(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            case SDZV_INSTANCE:
                tgc = new ui.sd2.SDInstance(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_PORT_MESSAGE:
                tgc = new ui.sd2.SDPortForMessage(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_ABSOLUTE_TIME_CONSTRAINT:
                tgc = new ui.sd2.SDAbsoluteTimeConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_RELATIVE_TIME_CONSTRAINT:
                tgc = new ui.sd2.SDRelativeTimeConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_ACTION_STATE:
                tgc = new ui.sd2.SDActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_GUARD:
                tgc = new ui.sd2.SDGuard(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_TIME_INTERVAL:
                tgc = new ui.sd2.SDTimeInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_TIMER_SETTING:
                tgc = new ui.sd2.SDTimerSetting(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_TIMER_EXPIRATION:
                tgc = new ui.sd2.SDTimerExpiration(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_TIMER_CANCELLATION:
                tgc = new ui.sd2.SDTimerCancellation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SDZV_COREGION:
                tgc = new ui.sd2.SDCoregion(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            case UCD_ACTOR:
                tgc = new UCDActor(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case UCD_ACTORBOX:
                tgc = new UCDActorBox(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case UCD_USECASE:
                tgc = new UCDUseCase(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case UCD_BORDER:
                tgc = new UCDBorder(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TDD_NODE:
                tgc = new TDDNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TDD_ARTIFACT:
                tgc = new TDDArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_START_STATE:
                tgc = new EBRDDStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_STOP_STATE:
                tgc = new EBRDDStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_CHOICE:
                tgc = new EBRDDChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_ERC:
                tgc = new EBRDDERC(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_ACTION:
                tgc = new EBRDDActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_FOR_LOOP:
                tgc = new EBRDDForLoop(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_VARIABLE_DECLARATION:
                tgc = new EBRDDAttributeBox(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_SEQUENCE:
                tgc = new EBRDDSequence(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_ESO:
                tgc = new EBRDDESO(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case EBRDD_ERB:
                tgc = new EBRDDERB(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ATD_BLOCK:
                tgc = new ATDBlock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ATD_ATTACK:
                tgc = new ATDAttack(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ATD_CONSTRAINT:
                tgc = new ATDConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case ATD_COUNTERMEASURE:
                tgc = new ATDCountermeasure(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case FTD_BLOCK:
                tgc = new FTDBlock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case FTD_FAULT:
                tgc = new FTDFault(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case FTD_CONSTRAINT:
                tgc = new FTDConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case FTD_COUNTERMEASURE:
                tgc = new FTDCountermeasure(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case DIPLODODUSMETHODOLOGY_REF_APPLICATION:
                tgc = new DiplodocusMethodologyDiagramReferenceToApplication(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case DIPLODODUSMETHODOLOGY_REF_CP:
                tgc = new DiplodocusMethodologyDiagramReferenceToCP(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case DIPLODODUSMETHODOLOGY_REF_ARCHITECTURE:
                tgc = new DiplodocusMethodologyDiagramReferenceToArchitecture(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case DIPLODODUSMETHODOLOGY_REF_MAPPING:
                tgc = new DiplodocusMethodologyDiagramReferenceToMapping(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case DIPLODODUSMETHODOLOGY_REF_REQUIREMENT:
                tgc = new DiplodocusMethodologyDiagramReferenceToRequirement(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case DIPLODODUSMETHODOLOGY_DIAGRAM_NAME:
                tgc = new DiplodocusMethodologyDiagramName(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            case TMLAD_START_STATE:
                tgc = new TMLADStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_STOP_STATE:
                tgc = new TMLADStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_WRITE_CHANNEL:
                tgc = new TMLADWriteChannel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_READ_CHANNEL:
                tgc = new TMLADReadChannel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_SEND_REQUEST:
                tgc = new TMLADSendRequest(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_SEND_EVENT:
                tgc = new TMLADSendEvent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_WAIT_EVENT:
                tgc = new TMLADWaitEvent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_NOTIFIED_EVENT:
                tgc = new TMLADNotifiedEvent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLTD_TASK:
                tgc = new TMLTaskOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLTD_CHANNEL_OPERATOR:
                tgc = new TMLChannelOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLTD_EVENT_OPERATOR:
                tgc = new TMLEventOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLTD_REQUEST_OPERATOR:
                tgc = new TMLRequestOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_ACTION_STATE:
                tgc = new TMLADActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_CHOICE:
                tgc = new TMLADChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_EXECI:
                tgc = new TMLADExecI(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_EXECI_INTERVAL:
                tgc = new TMLADExecIInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_EXECC:
                tgc = new TMLADExecC(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_EXECC_INTERVAL:
                tgc = new TMLADExecCInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_DELAY:
                tgc = new TMLADDelay(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_INTERVAL_DELAY:
                tgc = new TMLADDelayInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_FOR_LOOP:
                tgc = new TMLADForLoop(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_FOR_STATIC_LOOP:
                tgc = new TMLADForStaticLoop(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_FOR_EVER_LOOP:
                tgc = new TMLADForEverLoop(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_READ_REQUEST_ARG:
                tgc = new TMLADReadRequestArg(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_SEQUENCE:
                tgc = new TMLADSequence(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_UNORDERED_SEQUENCE:
                tgc = new TMLADUnorderedSequence(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_SELECT_EVT:
                tgc = new TMLADSelectEvt(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_RANDOM:
                tgc = new TMLADRandom(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_ENCRYPT:
                tgc = new TMLADEncrypt(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_DECRYPT:
                tgc = new TMLADDecrypt(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_CCOMPONENT:
                tgc = new TMLCCompositeComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_CREMOTECOMPONENT:
                tgc = new TMLCRemoteCompositeComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_CREMOTEPORTCOMPONENT:
                tgc = new TMLCRemotePortCompositeComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_CPORT:
                tgc = new TMLCCompositePort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_JOIN:
                tgc = new TMLCJoin(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_FORK:
                tgc = new TMLCFork(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_PCOMPONENT:
                tgc = new TMLCPrimitiveComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_RCOMPONENT:
                tgc = new TMLCRecordComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCTD_COPORT:
                tgc = new TMLCChannelOutPort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_CPUNODE:
                tgc = new TMLArchiCPUNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_FPGANODE:
                tgc = new TMLArchiFPGANode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_BUSNODE:
                tgc = new TMLArchiBUSNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_VGMNNODE:
                tgc = new TMLArchiVGMNNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_CROSSBARNODE:
                tgc = new TMLArchiCrossbarNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_CPNODE:
                tgc = new TMLArchiCPNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_BRIDGENODE:
                tgc = new TMLArchiBridgeNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_ROUTERNODE:
                tgc = new TMLArchiRouterNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_FIREWALL:
                tgc = new TMLArchiFirewallNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_HWANODE:
                tgc = new TMLArchiHWANode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
	case TMLARCHI_CAMSNODE:
                tgc = new TMLArchiCAMSNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_MEMORYNODE:
                tgc = new TMLArchiMemoryNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_DMANODE:
                tgc = new TMLArchiDMANode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_ARTIFACT:
                tgc = new TMLArchiArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_COMMUNICATION_ARTIFACT:
                tgc = new TMLArchiCommunicationArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_PORT_ARTIFACT:
                tgc = new TMLArchiPortArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_EVENT_ARTIFACT:
                tgc = new TMLArchiEventArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_KEY:
                tgc = new TMLArchiKey(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            // SystemC-AMS
            case CAMS_BLOCK_TDF:
                tgc = new SysCAMSBlockTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case CAMS_BLOCK_DE:
            	tgc = new SysCAMSBlockDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
		 case CAMS_CLOCK:
            	tgc = new SysCAMSClock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case CAMS_PORT_TDF:
            	tgc = new SysCAMSPortTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case CAMS_PORT_DE:
            	tgc = new SysCAMSPortDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
	case CAMS_PORT_CLOCK:
            	tgc = new SysCAMSPortClock(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case CAMS_PORT_CONVERTER:
            	tgc = new SysCAMSPortConverter(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case CAMS_CLUSTER:
            	tgc = new SysCAMSCompositeComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case CAMS_BLOCK_GPIO2VCI:
            	tgc = new SysCAMSBlockGPIO2VCI(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            // ELN
            case ELN_RESISTOR:
            	tgc = new ELNComponentResistor(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_CAPACITOR:
            	tgc = new ELNComponentCapacitor(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_INDUCTOR:
            	tgc = new ELNComponentInductor(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE:
            	tgc = new ELNComponentVoltageControlledVoltageSource(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE:
            	tgc = new ELNComponentVoltageControlledCurrentSource(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_IDEAL_TRANSFORMER: 
            	tgc = new ELNComponentIdealTransformer(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_TRANSMISSION_LINE:
            	tgc = new ELNComponentTransmissionLine(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_INDEPENDENT_VOLTAGE_SOURCE:
            	tgc = new ELNComponentIndependentVoltageSource(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_INDEPENDENT_CURRENT_SOURCE: 
            	tgc = new ELNComponentIndependentCurrentSource(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;	
            case ELN_NODE_REF: 
            	tgc = new ELNNodeRef(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_TDF_VOLTAGE_SINK: 
            	tgc = new ELNComponentVoltageSinkTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_TDF_CURRENT_SINK: 
            	tgc = new ELNComponentCurrentSinkTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_MODULE: 
            	tgc = new ELNModule(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_MODULE_TERMINAL: 
            	tgc = new ELNModuleTerminal(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_TDF_VOLTAGE_SOURCE: 
            	tgc = new ELNComponentVoltageSourceTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_TDF_CURRENT_SOURCE: 
            	tgc = new ELNComponentCurrentSourceTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_CLUSTER: 
            	tgc = new ELNCluster(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_MODULE_PORT_DE: 
            	tgc = new ELNModulePortDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_MODULE_PORT_TDF: 
            	tgc = new ELNModulePortTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_CLUSTER_TERMINAL: 
            	tgc = new ELNClusterTerminal(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_CLUSTER_PORT_DE: 
            	tgc = new ELNClusterPortDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_CLUSTER_PORT_TDF: 
            	tgc = new ELNClusterPortTDF(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_DE_CURRENT_SINK: 
            	tgc = new ELNComponentCurrentSinkDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_DE_CURRENT_SOURCE: 
            	tgc = new ELNComponentCurrentSourceDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_DE_VOLTAGE_SINK: 
            	tgc = new ELNComponentVoltageSinkDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            case ELN_DE_VOLTAGE_SOURCE: 
            	tgc = new ELNComponentVoltageSourceDE(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
            	break;
            // Communication patterns + SD
            case TMLCP_CHOICE:
                tgc = new TMLCPChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCP_FORK:
                tgc = new TMLCPFork(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCP_JOIN:
                tgc = new TMLCPJoin(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCP_REF_CP:
                tgc = new TMLCPRefAD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCP_REF_SD:
                tgc = new TMLCPRefSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCP_START_STATE:
                tgc = new TMLCPStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLCP_STOP_STATE:
                tgc = new TMLCPStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
//            case TMLCP_JUNCTION: // Issue #69
//                tgc = new TMLCPJunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
//                break;
            case TMLCP_FOR_LOOP:
                tgc = new TMLCPForLoop(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLSD_ACTION_STATE:
                tgc = new TMLSDActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLSD_STORAGE_INSTANCE:
                tgc = new TMLSDStorageInstance(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLSD_CONTROLLER_INSTANCE:
                tgc = new TMLSDControllerInstance(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLSD_TRANSFER_INSTANCE:
                tgc = new TMLSDTransferInstance(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;

            // Requirements
            case TREQ_REQUIREMENT:
                tgc = new Requirement(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TREQ_OBSERVER:
                tgc = new RequirementObserver(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TREQ_EBRDD:
                tgc = new EBRDDObserver(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case NCDD_EQNODE:
                tgc = new NCEqNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case NCDD_SWITCHNODE:
                tgc = new NCSwitchNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case NCDD_TRAFFIC_ARTIFACT:
                tgc = new NCTrafficArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case NCDD_ROUTE_ARTIFACT:
                tgc = new NCRouteArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_START_STATE:
                tgc = new ProSMDStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_STOP_STATE:
                tgc = new ProSMDStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_SENDMSG:
                tgc = new ProSMDSendMsg(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_GETMSG:
                tgc = new ProSMDGetMsg(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_CHOICE:
                tgc = new ProSMDChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_JUNCTION:
                tgc = new ProSMDJunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_SUBMACHINE:
                tgc = new ProSMDSubmachine(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_ACTION:
                tgc = new ProSMDAction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_PARALLEL:
                tgc = new ProSMDParallel(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROSMD_STATE:
                tgc = new ProSMDState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROCSD_COMPONENT:
                tgc = new ProCSDComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            /* No more delegate ports, by Solange
               case PROCSD_DELEGATE_PORT:
               tgc = new ProCSDDelegatePort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
               break; */
            case PROCSD_IN_PORT:
                tgc = new ProCSDInPort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROCSD_OUT_PORT:
                tgc = new ProCSDOutPort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case PROCSD_INTERFACE:
                tgc = new ProCSDInterface(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSCD_TCLASS:
                tgc = new TOSClass(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSCD_CALL_OPERATOR:
                tgc = new TOSCallOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSCD_EVT_OPERATOR:
                tgc = new TOSEvtOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_ACTION_STATE:
                tgc = new TOSADActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_CHOICE:
                tgc = new TOSADChoice(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_START_STATE:
                tgc = new TOSADStartState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_STOP_STATE:
                tgc = new TOSADStopState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_JUNCTION:
                tgc = new TOSADJunction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_TIME_INTERVAL:
                tgc = new TOSADTimeInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TOSAD_INT_TIME_INTERVAL:
                tgc = new TOSADIntTimeInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            default:
                break;
        }
        return tgc;
    }


    public final static int getType(TGComponent tgc) {
        // PLUGIN
        if (tgc instanceof TGComponentPlugin) {
            return COMPONENT_PLUGIN;

            // AVATAR BD
        } else if (tgc instanceof AvatarBDBlock) {
            return AVATARBD_BLOCK;
        } else if (tgc instanceof AvatarBDDataType) {
            return AVATARBD_DATATYPE;
        } else if (tgc instanceof AvatarBDCompositionConnector) {
            return AVATARBD_COMPOSITION_CONNECTOR;
        } else if (tgc instanceof AvatarBDPortConnector) {
            return AVATARBD_PORT_CONNECTOR;
        } else if (tgc instanceof AvatarBDLibraryFunction) {
            return AVATARBD_LIBRARYFUNCTION;


            // AVATAR SMD
        } else if (tgc instanceof AvatarSMDStartState) {
            return AVATARSMD_START_STATE;
        } else if (tgc instanceof AvatarSMDStopState) {
            return AVATARSMD_STOP_STATE;
        } else if (tgc instanceof AvatarSMDConnector) {
            return AVATARSMD_CONNECTOR;
        } else if (tgc instanceof AvatarSMDSendSignal) {
            return AVATARSMD_SEND_SIGNAL;
        } else if (tgc instanceof AvatarSMDReceiveSignal) {
            return AVATARSMD_RECEIVE_SIGNAL;
        } else if (tgc instanceof AvatarSMDLibraryFunctionCall) {
            return AVATARSMD_LIBRARY_FUNCTION_CALL;
//        } else if (tgc instanceof AvatarSMDParallel) { // Issue #69
//            return AVATARSMD_PARALLEL;
        } else if (tgc instanceof AvatarSMDState) {
            return AVATARSMD_STATE;
        } else if (tgc instanceof AvatarSMDChoice) {
            return AVATARSMD_CHOICE;
        } else if (tgc instanceof AvatarSMDRandom) {
            return AVATARSMD_RANDOM;
        } else if (tgc instanceof AvatarSMDSetTimer) {
            return AVATARSMD_SET_TIMER;
        } else if (tgc instanceof AvatarSMDResetTimer) {
            return AVATARSMD_RESET_TIMER;
        } else if (tgc instanceof AvatarSMDExpireTimer) {
            return AVATARSMD_EXPIRE_TIMER;


            //AVATAR DD
        } else if (tgc instanceof ADDConnector) {
            return ADD_CONNECTOR;
        } else if (tgc instanceof ADDCPUNode) {
            return ADD_CPUNODE;
        } else if (tgc instanceof ADDBlockArtifact) {
            return ADD_ARTIFACT;
        } else if (tgc instanceof ADDChannelArtifact) {
            return ADD_CHANNELARTIFACT;
        } else if (tgc instanceof ADDBusNode) {
            return ADD_BUSNODE;
        } else if (tgc instanceof ADDVgmnNode) {
            return ADD_VGMNNODE;
        } else if (tgc instanceof ADDCrossbarNode) {
            return ADD_CROSSBARNODE;
        } else if (tgc instanceof ADDTTYNode) {
            return ADD_TTYNODE;
        } else if (tgc instanceof ADDRAMNode) {
            return ADD_RAMNODE;
        } else if (tgc instanceof ADDROMNode) {
            return ADD_ROMNODE;
        } else if (tgc instanceof ADDBridgeNode) {
            return ADD_BRIDGENODE;
        } else if (tgc instanceof ADDDMANode) {
            return ADD_DMANODE;
        } else if (tgc instanceof ADDICUNode) {
            return ADD_ICUNODE;
        } else if (tgc instanceof ADDTimerNode) {
            return ADD_TIMERNODE;
        } else if (tgc instanceof ADDCoproMWMRNode) {
            return ADD_COPROMWMRNODE;


            // AVATAR Methodology
        } else if (tgc instanceof AvatarMethodologyReferenceToAssumptions) {
            return AVATARMETHODOLOGY_REF_ASSUMPTIONS;
        } else if (tgc instanceof AvatarMethodologyReferenceToRequirement) {
            return AVATARMETHODOLOGY_REF_REQUIREMENT;
        } else if (tgc instanceof AvatarMethodologyReferenceToAnalysis) {
            return AVATARMETHODOLOGY_REF_ANALYSIS;
        } else if (tgc instanceof AvatarMethodologyReferenceToDesign) {
            return AVATARMETHODOLOGY_REF_DESIGN;
        } else if (tgc instanceof AvatarMethodologyReferenceToProperties) {
            return AVATARMETHODOLOGY_REF_PROPERTIES;
        } else if (tgc instanceof AvatarMethodologyReferenceToPrototype) {
            return AVATARMETHODOLOGY_REF_PROTOTYPE;
        } else if (tgc instanceof AvatarMethodologyDiagramName) {
            return AVATARMETHODOLOGY_DIAGRAM_NAME;
        } else if (tgc instanceof AvatarMethodologyConnector) {
            return AVATARMETHODOLOGY_CONNECTOR;

            // SysML-Sec Methodology
        } else if (tgc instanceof SysmlsecMethodologyReferenceToAssumptions) {
            return SYSMLSEC_METHODOLOGY_REF_ASSUMPTIONS;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToRequirement) {
            return SYSMLSEC_METHODOLOGY_REF_REQUIREMENT;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToAnalysis) {
            return SYSMLSEC_METHODOLOGY_REF_ANALYSIS;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToDesign) {
            return SYSMLSEC_METHODOLOGY_REF_DESIGN;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToProperties) {
            return SYSMLSEC_METHODOLOGY_REF_PROPERTIES;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToPrototype) {
            return SYSMLSEC_METHODOLOGY_REF_PROTOTYPE;
        } else if (tgc instanceof SysmlsecMethodologyDiagramName) {
            return SYSMLSEC_METHODOLOGY_DIAGRAM_NAME;
        } else if (tgc instanceof SysmlsecMethodologyConnector) {
            return SYSMLSEC_METHODOLOGY_CONNECTOR;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToApplication) {
            return SYSMLSEC_METHODOLOGY_REF_FUNCTIONAL_VIEW;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToArchitecture) {
            return SYSMLSEC_METHODOLOGY_REF_ARCHITECTURE_VIEW;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToCP) {
            return SYSMLSEC_METHODOLOGY_REF_CP_VIEW;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToMapping) {
            return SYSMLSEC_METHODOLOGY_REF_MAPPING_VIEW;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToAttack) {
            return SYSMLSEC_METHODOLOGY_REF_ATTACK;
        } else if (tgc instanceof SysmlsecMethodologyReferenceToFault) {
            return SYSMLSEC_METHODOLOGY_REF_FAULT;


            // AVATAR MAD
        } else if (tgc instanceof AvatarMADAssumption) {
            return AVATARMAD_ASSUMPTION;
        } else if (tgc instanceof AvatarMADDiagramReference) {
            return AVATARMAD_DIAGRAM_REFERENCE;
        } else if (tgc instanceof AvatarMADElementReference) {
            return AVATARMAD_ELEMENT_REFERENCE;
        } else if (tgc instanceof AvatarMADCompositionConnector) {
            return AVATARMAD_COMPOSITION_CONNECTOR;
        } else if (tgc instanceof AvatarMADVersioningConnector) {
            return AVATARMAD_VERSIONING_CONNECTOR;
        } else if (tgc instanceof AvatarMADImpactConnector) {
            return AVATARMAD_IMPACT_CONNECTOR;
        } else if (tgc instanceof AvatarMADMeetConnector) {
            return AVATARMAD_MEET_CONNECTOR;
        } else if (tgc instanceof AvatarMADBelongsToCompositionConnector) {
            return AVATARMAD_BELONGSTOCOMPOSITION_CONNECTOR;

            // AVATAR RD
        } else if (tgc instanceof AvatarRDRequirement) {
            return AVATARRD_REQUIREMENT;
        } else if (tgc instanceof AvatarRDRequirementReference) {
            return AVATARRD_REQUIREMENT_REFERENCE;
        } else if (tgc instanceof AvatarRDProperty) {
            return AVATARRD_PROPERTY;
        } else if (tgc instanceof AvatarRDElementReference) {
            return AVATARRD_ELEMENT_REFERENCE;
        } else if (tgc instanceof AvatarRDDeriveConnector) {
            return AVATARRD_DERIVE_CONNECTOR;
        } else if (tgc instanceof AvatarRDSatisfyConnector) {
            return AVATARRD_SATISFY_CONNECTOR;
        } else if (tgc instanceof AvatarRDRefineConnector) {
            return AVATARRD_REFINE_CONNECTOR;
        } else if (tgc instanceof AvatarRDVerifyConnector) {
            return AVATARRD_VERIFY_CONNECTOR;
        } else if (tgc instanceof AvatarRDCopyConnector) {
            return AVATARRD_COPY_CONNECTOR;
        } else if (tgc instanceof AvatarRDCompositionConnector) {
            return AVATARRD_COMPOSITION_CONNECTOR;

            // AVATAR PD
        } else if (tgc instanceof AvatarPDBlock) {
            return APD_BLOCK;
        } else if (tgc instanceof AvatarPDLogicalConstraint) {
            return APD_LOGICAL_CONSTRAINT;
        } else if (tgc instanceof AvatarPDTemporalConstraint) {
            return APD_TEMPORAL_CONSTRAINT;
        } else if (tgc instanceof AvatarPDAttribute) {
            return APD_ATTRIBUTE;
        } else if (tgc instanceof AvatarPDSignal) {
            return APD_SIGNAL;
        } else if (tgc instanceof AvatarPDAlias) {
            return APD_ALIAS;
        } else if (tgc instanceof AvatarPDBoolEq) {
            return APD_BOOLEQ;
        } else if (tgc instanceof AvatarPDAttributeSetting) {
            return APD_ATTRIBUTE_SETTING;
        } else if (tgc instanceof AvatarPDProperty) {
            return APD_PROPERTY;
        } else if (tgc instanceof AvatarPDPropertyRelation) {
            return APD_PROPERTY_RELATION;
        } else if (tgc instanceof AvatarPDAttributeConnector) {
            return APD_ATTRIBUTE_CONNECTOR;
        } else if (tgc instanceof AvatarPDSignalConnector) {
            return APD_SIGNAL_CONNECTOR;
        } else if (tgc instanceof AvatarPDPropertyConnector) {
            return APD_PROPERTY_CONNECTOR;
        } else if (tgc instanceof AvatarPDCompositionConnector) {
            return APD_COMPOSITION_CONNECTOR;

            // AVATAR CD
        } else if (tgc instanceof AvatarCDBlock) {
            return ACD_BLOCK;
        } else if (tgc instanceof AvatarCDActorStickman) {
            return ACD_ACTOR_STICKMAN;
        } else if (tgc instanceof AvatarCDActorBox) {
            return ACD_ACTOR_BOX;
        } else if (tgc instanceof AvatarCDCompositionConnector) {
            return ACD_COMPOSITION_CONNECTOR;
        } else if (tgc instanceof AvatarCDAssociationConnector) {
            return ACD_ASSOCIATION_CONNECTOR;

            // AVATAR CD
        } else if (tgc instanceof AvatarADStartState) {
            return AAD_START_STATE;
        } else if (tgc instanceof AvatarADStopState) {
            return AAD_STOP_STATE;
        } else if (tgc instanceof AvatarADChoice) {
            return AAD_CHOICE;
        } else if (tgc instanceof AvatarADJunction) {
            return AAD_JUNCTION;
        } else if (tgc instanceof AvatarADParallel) {
            return AAD_PARALLEL;
        } else if (tgc instanceof AvatarADAction) {
            return AAD_ACTION;
        } else if (tgc instanceof AvatarADActivity) {
            return AAD_ACTIVITY;
        } else if (tgc instanceof AvatarADStopFlow) {
            return AAD_STOP_FLOW;
        } else if (tgc instanceof AvatarADSendSignalAction) {
            return AAD_SEND_SIGNAL_ACTION;
        } else if (tgc instanceof AvatarADAcceptEventAction) {
            return AAD_ACCEPT_EVENT_ACTION;
        } else if (tgc instanceof AvatarADPartition) {
            return AAD_PARTITION;
        } else if (tgc instanceof AvatarADAssociationConnector) {
            return AAD_ASSOCIATION_CONNECTOR;

            // SystemC-AMS
        } else if (tgc instanceof SysCAMSPortConnector) {
            return CAMS_CONNECTOR;
        } else if (tgc instanceof SysCAMSBlockTDF) {
        	return CAMS_BLOCK_TDF;
        } else if (tgc instanceof SysCAMSBlockDE) {
        	return CAMS_BLOCK_DE;
	} else if (tgc instanceof SysCAMSClock) {
        	return CAMS_CLOCK;
        } else if (tgc instanceof SysCAMSPortTDF) {
        	return CAMS_PORT_TDF;
        } else if (tgc instanceof SysCAMSPortDE) {
        	return CAMS_PORT_DE;
	} else if (tgc instanceof SysCAMSPortClock) {
        	return CAMS_PORT_CLOCK;
        } else if (tgc instanceof SysCAMSPortConverter) {
        	return CAMS_PORT_CONVERTER;
        } else if (tgc instanceof SysCAMSCompositeComponent) {
        	return CAMS_CLUSTER;
        } else if (tgc instanceof SysCAMSBlockGPIO2VCI) {
        	return CAMS_BLOCK_GPIO2VCI;
        	// ELN
        } else if (tgc instanceof ELNConnector) {
        	return ELN_CONNECTOR;	
        } else if (tgc instanceof ELNComponentResistor) {
        	return ELN_RESISTOR;
        } else if (tgc instanceof ELNComponentCapacitor) {
        	return ELN_CAPACITOR;
        } else if (tgc instanceof ELNComponentInductor) {
        	return ELN_INDUCTOR;
        } else if (tgc instanceof ELNComponentInductor) {
        	return ELN_VOLTAGE_CONTROLLED_VOLTAGE_SOURCE;
        } else if (tgc instanceof ELNComponentInductor) {
        	return ELN_VOLTAGE_CONTROLLED_CURRENT_SOURCE;
        } else if (tgc instanceof ELNComponentIdealTransformer) {
        	return ELN_IDEAL_TRANSFORMER;
        } else if (tgc instanceof ELNComponentTransmissionLine) {
        	return ELN_TRANSMISSION_LINE;
        } else if (tgc instanceof ELNComponentIndependentVoltageSource) {
        	return ELN_INDEPENDENT_VOLTAGE_SOURCE;
        } else if (tgc instanceof ELNComponentIndependentCurrentSource) {
        	return ELN_INDEPENDENT_CURRENT_SOURCE;
        } else if (tgc instanceof ELNNodeRef) {
        	return ELN_NODE_REF;
        } else if (tgc instanceof ELNComponentVoltageSinkTDF) {
        	return ELN_TDF_VOLTAGE_SINK;
        } else if (tgc instanceof ELNComponentCurrentSinkTDF) {
        	return ELN_TDF_CURRENT_SINK;
        } else if (tgc instanceof ELNModule) {
        	return ELN_MODULE;
        } else if (tgc instanceof ELNModuleTerminal) {
        	return ELN_MODULE_TERMINAL;
        } else if (tgc instanceof ELNComponentVoltageSourceTDF) {
        	return ELN_TDF_VOLTAGE_SOURCE;
        } else if (tgc instanceof ELNComponentCurrentSourceTDF) {
        	return ELN_TDF_CURRENT_SOURCE;
        } else if (tgc instanceof ELNCluster) {
        	return ELN_CLUSTER;
        } else if (tgc instanceof ELNModulePortDE) {
        	return ELN_MODULE_PORT_DE;
        } else if (tgc instanceof ELNModulePortTDF) {
        	return ELN_MODULE_PORT_TDF;
        } else if (tgc instanceof ELNClusterTerminal) {
        	return ELN_CLUSTER_TERMINAL;
        } else if (tgc instanceof ELNClusterPortDE) {
        	return ELN_CLUSTER_PORT_DE;
        } else if (tgc instanceof ELNClusterPortTDF) {
        	return ELN_CLUSTER_PORT_TDF;
        } else if (tgc instanceof ELNComponentCurrentSinkDE) {
        	return ELN_DE_CURRENT_SINK;
        } else if (tgc instanceof ELNComponentCurrentSourceDE) {
        	return ELN_DE_CURRENT_SOURCE;
        } else if (tgc instanceof ELNComponentVoltageSinkDE) {
        	return ELN_DE_VOLTAGE_SINK;
        } else if (tgc instanceof ELNComponentVoltageSourceDE) {
        	return ELN_DE_VOLTAGE_SOURCE;
        	
        	// Others
        } else if (tgc instanceof TADDeterministicDelay) {
            return TAD_DETERMINISTIC_DELAY;
        } else if (tgc instanceof TADParallel) {
            return TAD_PARALLEL;
        } else if (tgc instanceof TADStopState) {
            return TAD_STOP_STATE;
        } else if (tgc instanceof TADStartState) {
            return TAD_START_STATE;
        } else if (tgc instanceof TADActionState) {
            return TAD_ACTION_STATE;
        } else if (tgc instanceof TADArrayGetState) {
            return TAD_ARRAY_GET;
        } else if (tgc instanceof TADArraySetState) {
            return TAD_ARRAY_SET;
        } else if (tgc instanceof TADTimeLimitedOffer) {
            return TAD_TIME_LIMITED_OFFER;
        } else if (tgc instanceof TADJunction) {
            return TAD_JUNCTION;
        } else if (tgc instanceof TADNonDeterministicDelay) {
            return TAD_NON_DETERMINISTIC_DELAY;
        } else if (tgc instanceof TADTimeInterval) {
            return TAD_DELAY_NON_DETERMINISTIC_DELAY;
        } else if (tgc instanceof TADTimeLimitedOfferWithLatency) {
            return TAD_TIME_LIMITED_OFFER_WITH_LATENCY;
        } else if (tgc instanceof TADTimeCapture) {
            return TAD_TIME_CAPTURE;
        } else if (tgc instanceof TADChoice) {
            return TAD_CHOICE;
        } else if (tgc instanceof TCDTClass) {
            return TCD_TCLASS;
        } else if (tgc instanceof TCDTObject) {
            return TCD_TOBJECT;
        } else if (tgc instanceof TCDTData) {
            return TCD_TDATA;
        } else if (tgc instanceof TCDParallelOperator) {
            return TCD_PARALLEL_OPERATOR;
        } else if (tgc instanceof TCDSequenceOperator) {
            return TCD_SEQUENCE_OPERATOR;
        } else if (tgc instanceof TCDPreemptionOperator) {
            return TCD_PREEMPTION_OPERATOR;
        } else if (tgc instanceof TCDSynchroOperator) {
            return TCD_SYNCHRO_OPERATOR;
        } else if (tgc instanceof TCDInvocationOperator) {
            return TCD_INVOCATION_OPERATOR;
        } else if (tgc instanceof TCDWatchdogOperator) {
            return TCD_WATCHDOG_OPERATOR;
        } else if (tgc instanceof TGConnectorFullArrow) {
            return CONNECTOR_AD_DIAGRAM;
        } else if (tgc instanceof TGConnectorAssociation) {
            return CONNECTOR_ASSOCIATION;
        } else if (tgc instanceof TGConnectorAssociationWithNavigation) {
            return CONNECTOR_ASSOCIATION_NAVIGATION;
        } else if (tgc instanceof TGConnectorAttribute) {
            return CONNECTOR_ATTRIBUTE;
        } else if (tgc instanceof TGConnectorInteraction) {
            return CONNECTOR_INTERACTION;
        } else if (tgc instanceof TGCNote) {
            return UML_NOTE;
        } else if (tgc instanceof TGCPanelInfo) {
            return INFO_PANEL;
        } else if (tgc instanceof IODChoice) {
            return IOD_CHOICE;
        } else if (tgc instanceof IODStartState) {
            return IOD_START_STATE;
        } else if (tgc instanceof IODStopState) {
            return IOD_STOP_STATE;
        } else if (tgc instanceof IODJunction) {
            return IOD_JUNCTION;
        } else if (tgc instanceof IODParallel) {
            return IOD_PARALLEL;
        } else if (tgc instanceof IODRefSD) {
            return IOD_REF_SD;
        } else if (tgc instanceof IODRefIOD) {
            return IOD_REF_IOD;
        } else if (tgc instanceof IODPreemption) {
            return IOD_PREEMPTION;
        } else if (tgc instanceof IODSequence) {
            return IOD_SEQUENCE;
        } else if (tgc instanceof ui.sd.TGConnectorMessageAsyncSD) {
            return CONNECTOR_MESSAGE_ASYNC_SD;
        } else if (tgc instanceof ui.sd.TGConnectorMessageSyncSD) {
            return CONNECTOR_MESSAGE_SYNC_SD;
        } else if (tgc instanceof ui.sd.TGConnectorRelativeTimeSD) {
            return CONNECTOR_RELATIVE_TIME_SD;
        } else if (tgc instanceof ui.sd.SDInstance) {
            return SD_INSTANCE;
        } else if (tgc instanceof ui.sd.SDAbsoluteTimeConstraint) {
            return SD_ABSOLUTE_TIME_CONSTRAINT;
        } else if (tgc instanceof ui.sd.SDRelativeTimeConstraint) {
            return SD_RELATIVE_TIME_CONSTRAINT;
        } else if (tgc instanceof ui.sd.SDActionState) {
            return SD_ACTION_STATE;
        } else if (tgc instanceof ui.sd.SDGuard) {
            return SD_GUARD;
        } else if (tgc instanceof ui.sd.SDTimeInterval) {
            return SD_TIME_INTERVAL;
        } else if (tgc instanceof ui.sd.SDTimerSetting) {
            return SD_TIMER_SETTING;
        } else if (tgc instanceof ui.sd.SDTimerExpiration) {
            return SD_TIMER_EXPIRATION;
        } else if (tgc instanceof ui.sd.SDTimerCancellation) {
            return SD_TIMER_CANCELLATION;
        } else if (tgc instanceof ui.sd.SDCoregion) {
            return SD_COREGION;

        } else if (tgc instanceof ui.sd2.TGConnectorMessageAsyncSD) {
            return CONNECTOR_MESSAGE_ASYNC_SDZV;
        } else if (tgc instanceof ui.sd2.TGConnectorMessageSyncSD) {
            return CONNECTOR_MESSAGE_SYNC_SDZV;
        } else if (tgc instanceof ui.sd2.TGConnectorRelativeTimeSD) {
            return CONNECTOR_RELATIVE_TIME_SDZV;
        } else if (tgc instanceof ui.sd2.SDInstance) {
            return SDZV_INSTANCE;
        } else if (tgc instanceof ui.sd2.SDPortForMessage) {
            return SDZV_PORT_MESSAGE;
        } else if (tgc instanceof ui.sd2.SDAbsoluteTimeConstraint) {
            return SDZV_ABSOLUTE_TIME_CONSTRAINT;
        } else if (tgc instanceof ui.sd2.SDRelativeTimeConstraint) {
            return SDZV_RELATIVE_TIME_CONSTRAINT;
        } else if (tgc instanceof ui.sd2.SDActionState) {
            return SDZV_ACTION_STATE;
        } else if (tgc instanceof ui.sd2.SDGuard) {
            return SDZV_GUARD;
        } else if (tgc instanceof ui.sd2.SDTimeInterval) {
            return SDZV_TIME_INTERVAL;
        } else if (tgc instanceof ui.sd2.SDTimerSetting) {
            return SDZV_TIMER_SETTING;
        } else if (tgc instanceof ui.sd2.SDTimerExpiration) {
            return SDZV_TIMER_EXPIRATION;
        } else if (tgc instanceof ui.sd2.SDTimerCancellation) {
            return SDZV_TIMER_CANCELLATION;
        } else if (tgc instanceof ui.sd2.SDCoregion) {
            return SDZV_COREGION;

        } else if (tgc instanceof UCDActor) {
            return UCD_ACTOR;
        } else if (tgc instanceof UCDActorBox) {
            return UCD_ACTORBOX;
        } else if (tgc instanceof UCDUseCase) {
            return UCD_USECASE;
        } else if (tgc instanceof UCDBorder) {
            return UCD_BORDER;
        } else if (tgc instanceof TGConnectorUseCase) {
            return CONNECTOR_ACTOR_UCD;
        } else if (tgc instanceof TGConnectorInclude) {
            return CONNECTOR_INCLUDE_UCD;
        } else if (tgc instanceof TGConnectorExtend) {
            return CONNECTOR_EXTEND_UCD;
        } else if (tgc instanceof TGConnectorSpecialization) {
            return CONNECTOR_SPECIA_UCD;
        } else if (tgc instanceof TGConnectorLinkNode) {
            return CONNECTOR_NODE_DD;
        } else if (tgc instanceof TDDNode) {
            return TDD_NODE;
        } else if (tgc instanceof TDDArtifact) {
            return TDD_ARTIFACT;
        } else if (tgc instanceof NCEqNode) {
            return NCDD_EQNODE;
        } else if (tgc instanceof NCTrafficArtifact) {
            return NCDD_TRAFFIC_ARTIFACT;
        } else if (tgc instanceof NCRouteArtifact) {
            return NCDD_ROUTE_ARTIFACT;
        } else if (tgc instanceof EBRDDStartState) {
            return EBRDD_START_STATE;
        } else if (tgc instanceof EBRDDStopState) {
            return EBRDD_STOP_STATE;
        } else if (tgc instanceof EBRDDChoice) {
            return EBRDD_CHOICE;
        } else if (tgc instanceof EBRDDERC) {
            return EBRDD_ERC;
        } else if (tgc instanceof EBRDDActionState) {
            return EBRDD_ACTION;
        } else if (tgc instanceof EBRDDForLoop) {
            return EBRDD_FOR_LOOP;
        } else if (tgc instanceof EBRDDAttributeBox) {
            return EBRDD_VARIABLE_DECLARATION;
        } else if (tgc instanceof EBRDDSequence) {
            return EBRDD_SEQUENCE;
        } else if (tgc instanceof EBRDDESO) {
            return EBRDD_ESO;
        } else if (tgc instanceof EBRDDERB) {
            return EBRDD_ERB;

        } else if (tgc instanceof ATDBlock) {
            return ATD_BLOCK;
        } else if (tgc instanceof ATDAttack) {
            return ATD_ATTACK;
        } else if (tgc instanceof ATDCountermeasure) {
            return ATD_COUNTERMEASURE;
        } else if (tgc instanceof ATDConstraint) {
            return ATD_CONSTRAINT;
        } else if (tgc instanceof ATDCompositionConnector) {
            return ATD_COMPOSITION_CONNECTOR;
        } else if (tgc instanceof ATDAttackConnector) {
            return ATD_ATTACK_CONNECTOR;
        } else if (tgc instanceof ATDCountermeasureConnector) {
            return ATD_COUNTERMEASURE_CONNECTOR;

        } else if (tgc instanceof FTDBlock) {
            return FTD_BLOCK;
        } else if (tgc instanceof FTDFault) {
            return FTD_FAULT;
        } else if (tgc instanceof FTDCountermeasure) {
            return FTD_COUNTERMEASURE;
        } else if (tgc instanceof FTDConstraint) {
            return FTD_CONSTRAINT;
        } else if (tgc instanceof FTDCompositionConnector) {
            return FTD_COMPOSITION_CONNECTOR;
        } else if (tgc instanceof FTDFaultConnector) {
            return FTD_FAULT_CONNECTOR;
        } else if (tgc instanceof FTDCountermeasureConnector) {
            return FTD_COUNTERMEASURE_CONNECTOR;

        } else if (tgc instanceof DiplodocusMethodologyDiagramReferenceToApplication) {
            return DIPLODODUSMETHODOLOGY_REF_APPLICATION;
        } else if (tgc instanceof DiplodocusMethodologyDiagramReferenceToCP) {
            return DIPLODODUSMETHODOLOGY_REF_CP;
        } else if (tgc instanceof DiplodocusMethodologyDiagramReferenceToArchitecture) {
            return DIPLODODUSMETHODOLOGY_REF_ARCHITECTURE;
        } else if (tgc instanceof DiplodocusMethodologyDiagramReferenceToMapping) {
            return DIPLODODUSMETHODOLOGY_REF_MAPPING;
        } else if (tgc instanceof DiplodocusMethodologyDiagramReferenceToRequirement) {
            return DIPLODODUSMETHODOLOGY_REF_REQUIREMENT;
        } else if (tgc instanceof DiplodocusMethodologyDiagramName) {
            return DIPLODODUSMETHODOLOGY_DIAGRAM_NAME;
        } else if (tgc instanceof DiplodocusMethodologyConnector) {
            return DIPLODOCUSMETHODOLOGY_CONNECTOR;
        } else if (tgc instanceof TMLADStartState) {
            return TMLAD_START_STATE;
        } else if (tgc instanceof TMLADStopState) {
            return TMLAD_STOP_STATE;
        } else if (tgc instanceof TMLADWriteChannel) {
            return TMLAD_WRITE_CHANNEL;
        } else if (tgc instanceof TMLADReadChannel) {
            return TMLAD_READ_CHANNEL;
        } else if (tgc instanceof TMLADSendRequest) {
            return TMLAD_SEND_REQUEST;
        } else if (tgc instanceof TMLADSendEvent) {
            return TMLAD_SEND_EVENT;
        } else if (tgc instanceof TMLADWaitEvent) {
            return TMLAD_WAIT_EVENT;
        } else if (tgc instanceof TMLADNotifiedEvent) {
            return TMLAD_NOTIFIED_EVENT;
        } else if (tgc instanceof TMLTaskOperator) {
            return TMLTD_TASK;
        } else if (tgc instanceof TMLADActionState) {
            return TMLAD_ACTION_STATE;
        } else if (tgc instanceof TMLADChoice) {
            return TMLAD_CHOICE;
        } else if (tgc instanceof TMLADExecI) {
            return TMLAD_EXECI;
        } else if (tgc instanceof TMLADExecIInterval) {
            return TMLAD_EXECI_INTERVAL;
        } else if (tgc instanceof TMLADExecC) {
            return TMLAD_EXECC;
        } else if (tgc instanceof TMLADExecCInterval) {
            return TMLAD_EXECC_INTERVAL;
        } else if (tgc instanceof TMLADDelay) {
            return TMLAD_DELAY;
        } else if (tgc instanceof TMLADDelayInterval) {
            return TMLAD_INTERVAL_DELAY;
        } else if (tgc instanceof TMLADForLoop) {
            return TMLAD_FOR_LOOP;
        } else if (tgc instanceof TMLADForStaticLoop) {
            return TMLAD_FOR_STATIC_LOOP;
        } else if (tgc instanceof TMLADForEverLoop) {
            return TMLAD_FOR_EVER_LOOP;
        } else if (tgc instanceof TMLADReadRequestArg) {
            return TMLAD_READ_REQUEST_ARG;
        } else if (tgc instanceof TMLADSequence) {
            return TMLAD_SEQUENCE;
        } else if (tgc instanceof TMLADUnorderedSequence) {
            return TMLAD_UNORDERED_SEQUENCE;
        } else if (tgc instanceof TMLADSelectEvt) {
            return TMLAD_SELECT_EVT;
        } else if (tgc instanceof TMLADEncrypt) {
            return TMLAD_ENCRYPT;
        } else if (tgc instanceof TMLADDecrypt) {
            return TMLAD_DECRYPT;
        } else if (tgc instanceof TMLADRandom) {
            return TMLAD_RANDOM;

        } else if (tgc instanceof TMLCCompositeComponent) {
            return TMLCTD_CCOMPONENT;
        } else if (tgc instanceof TMLCPrimitiveComponent) {
            return TMLCTD_PCOMPONENT;
        } else if (tgc instanceof TMLCRecordComponent) {
            return TMLCTD_RCOMPONENT;
        } else if (tgc instanceof TMLCRemoteCompositeComponent) {
            return TMLCTD_CREMOTECOMPONENT;
        } else if (tgc instanceof TMLCRemotePortCompositeComponent) {
            return TMLCTD_CREMOTEPORTCOMPONENT;
        } else if (tgc instanceof TMLCCompositePort) {
            return TMLCTD_CPORT;
        } else if (tgc instanceof TMLCJoin) {
            return TMLCTD_JOIN;
        } else if (tgc instanceof TMLCFork) {
            return TMLCTD_FORK;
        } else if (tgc instanceof TMLCPrimitivePort) {
            return TMLCTD_COPORT;

        } else if (tgc instanceof TMLCPortConnector) {
            return CONNECTOR_PORT_TMLC;

        } else if (tgc instanceof TGConnectorTMLAD) {
            return CONNECTOR_TMLAD;
        } else if (tgc instanceof TGConnectorTMLAssociationNav) {
            return CONNECTOR_TML_ASSOCIATION_NAV;
        } else if (tgc instanceof TGConnectorTMLCompositionOperator) {
            return CONNECTOR_TML_COMPOSITION_OPERATOR;
        } else if (tgc instanceof TMLArchiConnectorNode) {
            return CONNECTOR_NODE_TMLARCHI;
        } else if (tgc instanceof TMLArchiCPUNode) {
            return TMLARCHI_CPUNODE;
        } else if (tgc instanceof TMLArchiFPGANode) {
            return TMLARCHI_FPGANODE;
        } else if (tgc instanceof TMLArchiBUSNode) {
            return TMLARCHI_BUSNODE;
        } else if (tgc instanceof TMLArchiVGMNNode) {
            return TMLARCHI_VGMNNODE;
        } else if (tgc instanceof TMLArchiCrossbarNode) {
            return TMLARCHI_CROSSBARNODE;
        } else if (tgc instanceof TMLArchiCPNode) {
            return TMLARCHI_CPNODE;
        } else if (tgc instanceof TMLArchiBridgeNode) {
            return TMLARCHI_BRIDGENODE;
        } else if (tgc instanceof TMLArchiRouterNode) {
            return TMLARCHI_ROUTERNODE;
        } else if (tgc instanceof TMLArchiFirewallNode) {
            return TMLARCHI_FIREWALL;
        } else if (tgc instanceof AvatarBDFirewall) {
            return AVATAR_FIREWALL;
        } else if (tgc instanceof TMLArchiHWANode) {
            return TMLARCHI_HWANODE;
	} else if (tgc instanceof TMLArchiCAMSNode) {
            return TMLARCHI_CAMSNODE;
        } else if (tgc instanceof TMLArchiMemoryNode) {
            return TMLARCHI_MEMORYNODE;
        } else if (tgc instanceof TMLArchiDMANode) {
            return TMLARCHI_DMANODE;
        } else if (tgc instanceof TMLArchiArtifact) {
            return TMLARCHI_ARTIFACT;
        } else if (tgc instanceof TMLArchiCommunicationArtifact) {
            return TMLARCHI_COMMUNICATION_ARTIFACT;
        } else if (tgc instanceof TMLArchiPortArtifact) {
            return TMLARCHI_PORT_ARTIFACT;
        } else if (tgc instanceof TMLArchiEventArtifact) {
            return TMLARCHI_EVENT_ARTIFACT;
        } else if (tgc instanceof TMLArchiKey) {
            return TMLARCHI_KEY;

            // Communication patterns
        } else if (tgc instanceof TMLCPChoice) {
            return TMLCP_CHOICE;
        } else if (tgc instanceof TMLCPFork) {
            return TMLCP_FORK;
        } else if (tgc instanceof TMLCPJoin) {
            return TMLCP_JOIN;
        } else if (tgc instanceof TMLCPRefAD) {
            return TMLCP_REF_CP;
        } else if (tgc instanceof TMLCPRefSD) {
            return TMLCP_REF_SD;
        } else if (tgc instanceof TMLCPStartState) {
            return TMLCP_START_STATE;
        } else if (tgc instanceof TMLCPStopState) {
            return TMLCP_STOP_STATE;
//          } else if (tgc instanceof TMLCPJunction) { Issue #69
//          return TMLCP_JUNCTION;
        } else if (tgc instanceof TMLCPForLoop) {
            return TMLCP_FOR_LOOP;
        } else if (tgc instanceof TGConnectorTMLCP) {
            return CONNECTOR_TMLCP;
        } else if (tgc instanceof TMLSDActionState) {
            return TMLSD_ACTION_STATE;
        } else if (tgc instanceof TMLSDStorageInstance) {
            return TMLSD_STORAGE_INSTANCE;
        } else if (tgc instanceof TMLSDTransferInstance) {
            return TMLSD_TRANSFER_INSTANCE;
        } else if (tgc instanceof TMLSDControllerInstance) {
            return TMLSD_CONTROLLER_INSTANCE;
        } else if (tgc instanceof TGConnectorMessageAsyncTMLSD) {
            return CONNECTOR_MESSAGE_ASYNC_TMLSD;


        } else if (tgc instanceof TGConnectorComment) {
            return CONNECTOR_COMMENT;
        } else if (tgc instanceof Requirement) {
            return TREQ_REQUIREMENT;
        } else if (tgc instanceof RequirementObserver) {
            return TREQ_OBSERVER;
        } else if (tgc instanceof EBRDDObserver) {
            return TREQ_EBRDD;
        } else if (tgc instanceof TGConnectorDerive) {
            return CONNECTOR_DERIVE_REQ;
        } else if (tgc instanceof TGConnectorCopy) {
            return CONNECTOR_COPY_REQ;
        } else if (tgc instanceof TGConnectorComposition) {
            return CONNECTOR_COMPOSITION_REQ;
        } else if (tgc instanceof TGConnectorVerify) {
            return CONNECTOR_VERIFY_REQ;
        } else if (tgc instanceof ProSMDStartState) {
            return PROSMD_START_STATE;
        } else if (tgc instanceof ProSMDStopState) {
            return PROSMD_STOP_STATE;
        } else if (tgc instanceof TGConnectorProSMD) {
            return CONNECTOR_PROSMD;
        } else if (tgc instanceof TGConnectorProCSD) {
            return CONNECTOR_PROCSD;
        } else if (tgc instanceof TGConnectorDelegateProCSD) {
            return CONNECTOR_DELEGATE_PROCSD;
        } else if (tgc instanceof ProSMDSendMsg) {
            return PROSMD_SENDMSG;
        } else if (tgc instanceof ProSMDGetMsg) {
            return PROSMD_GETMSG;
        } else if (tgc instanceof ProSMDChoice) {
            return PROSMD_CHOICE;
        } else if (tgc instanceof ProSMDJunction) {
            return PROSMD_JUNCTION;
        } else if (tgc instanceof ProSMDSubmachine) {
            return PROSMD_SUBMACHINE;
        } else if (tgc instanceof ProSMDAction) {
            return PROSMD_ACTION;
        } else if (tgc instanceof ProSMDParallel) {
            return PROSMD_PARALLEL;
        } else if (tgc instanceof ProSMDState) {
            return PROSMD_STATE;
        } else if (tgc instanceof TGConnectorPortInterface) {
            return CONNECTOR_PROCSD_PORT_INTERFACE;
        } else if (tgc instanceof ProCSDComponent) {
            return PROCSD_COMPONENT;

            /* No more delegate ports, by Solange
               } else if (tgc instanceof ProCSDDelegatePort) {
               return PROCSD_DELEGATE_PORT;
            */

            //NC
        } else if (tgc instanceof NCEqNode) {
            return NCDD_EQNODE;
        } else if (tgc instanceof NCSwitchNode) {
            return NCDD_SWITCHNODE;
        } else if (tgc instanceof NCTrafficArtifact) {
            return NCDD_TRAFFIC_ARTIFACT;
        } else if (tgc instanceof NCConnectorNode) {
            return CONNECTOR_NODE_NC;

            // TURTLE-OS
        } else if (tgc instanceof TOSClass) {
            return TOSCD_TCLASS;
        } else if (tgc instanceof TOSConnectorAssociation) {
            return TOS_CONNECTOR_ASSOCIATION;
        } else if (tgc instanceof TOSConnectorAssociationWithNavigation) {
            return TOS_CONNECTOR_ASSOCIATION_NAVIGATION;
        } else if (tgc instanceof TOSConnectorCompositionOperator) {
            return TOS_CONNECTOR_ATTRIBUTE;
        } else if (tgc instanceof TOSCallOperator) {
            return TOSCD_CALL_OPERATOR;
        } else if (tgc instanceof TOSEvtOperator) {
            return TOSCD_EVT_OPERATOR;
        } else if (tgc instanceof TOSADActionState) {
            return TOSAD_ACTION_STATE;
        } else if (tgc instanceof TOSADConnector) {
            return TOSAD_CONNECTOR;
        } else if (tgc instanceof TOSADChoice) {
            return TOSAD_CHOICE;
        } else if (tgc instanceof TOSADStartState) {
            return TOSAD_START_STATE;
        } else if (tgc instanceof TOSADStopState) {
            return TOSAD_STOP_STATE;
        } else if (tgc instanceof TOSADJunction) {
            return TOSAD_JUNCTION;
        } else if (tgc instanceof TOSADTimeInterval) {
            return TOSAD_TIME_INTERVAL;
        } else if (tgc instanceof TOSADIntTimeInterval) {
            return TOSAD_INT_TIME_INTERVAL;
        }
        return -1;
    }


    public final static TGConnector addConnector(int x, int y, int id, TDiagramPanel tdp, TGConnectingPoint p1, TGConnectingPoint p2, Vector<Point> listPoint) {
        TGConnector tgc = null;

        switch (id) {
            // AVATAR
            // AVATAR BD
            case AVATARBD_COMPOSITION_CONNECTOR:
                tgc = new AvatarBDCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARBD_PORT_CONNECTOR:
                tgc = new AvatarBDPortConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // AVATAR SMD
            case AVATARSMD_CONNECTOR:
                tgc = new AvatarSMDConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;


            // AVATAR Methodology
            case AVATARMETHODOLOGY_CONNECTOR:
                tgc = new AvatarMethodologyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // SysML-Sec Methodology
            case SYSMLSEC_METHODOLOGY_CONNECTOR:
                tgc = new SysmlsecMethodologyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // AVATAR MAD
            case AVATARMAD_COMPOSITION_CONNECTOR:
                tgc = new AvatarMADCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARMAD_VERSIONING_CONNECTOR:
                tgc = new AvatarMADVersioningConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARMAD_IMPACT_CONNECTOR:
                tgc = new AvatarMADImpactConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARMAD_MEET_CONNECTOR:
                tgc = new AvatarMADMeetConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARMAD_BELONGSTOCOMPOSITION_CONNECTOR:
                tgc = new AvatarMADBelongsToCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // AVATAR RD
            case AVATARRD_DERIVE_CONNECTOR:
                tgc = new AvatarRDDeriveConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARRD_SATISFY_CONNECTOR:
                tgc = new AvatarRDSatisfyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARRD_REFINE_CONNECTOR:
                tgc = new AvatarRDRefineConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARRD_VERIFY_CONNECTOR:
                tgc = new AvatarRDVerifyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARRD_COPY_CONNECTOR:
                tgc = new AvatarRDCopyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case AVATARRD_COMPOSITION_CONNECTOR:
                tgc = new AvatarRDCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // AVATAR CD
            case ACD_COMPOSITION_CONNECTOR:
                tgc = new AvatarCDCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case ACD_ASSOCIATION_CONNECTOR:
                tgc = new AvatarCDAssociationConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // AVATAR AD
            case AAD_ASSOCIATION_CONNECTOR:
                tgc = new AvatarADAssociationConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // AVATAR DD
            case ADD_CONNECTOR:
                tgc = new ADDConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //DG 21.02.

                addconnectors.add((ADDConnector) tgc);//DG 21.02.
                break;

            // AVATAR PD
            case APD_ATTRIBUTE_CONNECTOR:
                tgc = new AvatarPDAttributeConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case APD_SIGNAL_CONNECTOR:
                tgc = new AvatarPDSignalConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case APD_PROPERTY_CONNECTOR:
                tgc = new AvatarPDPropertyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case APD_COMPOSITION_CONNECTOR:
                tgc = new AvatarPDCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            // Others
            case CONNECTOR_AD_DIAGRAM:
                tgc = new TGConnectorFullArrow(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_ASSOCIATION:
                tgc = new TGConnectorAssociation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_ASSOCIATION_NAVIGATION:
                tgc = new TGConnectorAssociationWithNavigation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_ATTRIBUTE:
                tgc = new TGConnectorAttribute(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_INTERACTION:
                tgc = new TGConnectorInteraction(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;
            case CONNECTOR_MESSAGE_ASYNC_SD:
                tgc = new ui.sd.TGConnectorMessageAsyncSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;
            case CONNECTOR_MESSAGE_SYNC_SD:
                tgc = new ui.sd.TGConnectorMessageSyncSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;
            case CONNECTOR_RELATIVE_TIME_SD:
                tgc = new ui.sd.TGConnectorRelativeTimeSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;
            case CONNECTOR_MESSAGE_ASYNC_SDZV:
                tgc = new ui.sd2.TGConnectorMessageAsyncSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;
            case CONNECTOR_MESSAGE_SYNC_SDZV:
                tgc = new ui.sd2.TGConnectorMessageSyncSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;
            case CONNECTOR_RELATIVE_TIME_SDZV:
                tgc = new ui.sd2.TGConnectorRelativeTimeSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //TraceManager.addDev("Connector interaction");
                break;

            case CONNECTOR_ACTOR_UCD:
                tgc = new TGConnectorUseCase(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_INCLUDE_UCD:
                tgc = new TGConnectorInclude(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_EXTEND_UCD:
                tgc = new TGConnectorExtend(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_SPECIA_UCD:
                tgc = new TGConnectorSpecialization(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_NODE_DD:
                tgc = new TGConnectorLinkNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_NODE_NC:
                tgc = new NCConnectorNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_COMMENT:
                tgc = new TGConnectorComment(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case TOS_CONNECTOR_ASSOCIATION:
                tgc = new TOSConnectorAssociation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case TOS_CONNECTOR_ASSOCIATION_NAVIGATION:
                tgc = new TOSConnectorAssociationWithNavigation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case TOS_CONNECTOR_ATTRIBUTE:
                tgc = new TOSConnectorCompositionOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case TOSAD_CONNECTOR:
                tgc = new TOSADConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_EBRDD:
                tgc = new TGConnectorEBRDD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_EBRDD_ERC:
                tgc = new TGConnectorEBRDDERC(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case ATD_COMPOSITION_CONNECTOR:
                tgc = new ATDCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case ATD_ATTACK_CONNECTOR:
                tgc = new ATDAttackConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case ATD_COUNTERMEASURE_CONNECTOR:
                tgc = new ATDCountermeasureConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            case FTD_COMPOSITION_CONNECTOR:
                tgc = new FTDCompositionConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case FTD_FAULT_CONNECTOR:
                tgc = new FTDFaultConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case FTD_COUNTERMEASURE_CONNECTOR:
                tgc = new FTDCountermeasureConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;


            case DIPLODOCUSMETHODOLOGY_CONNECTOR:
                tgc = new DiplodocusMethodologyConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            case CONNECTOR_TMLAD:
                tgc = new TGConnectorTMLAD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_PORT_TMLC:
                tgc = new TMLCPortConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CAMS_CONNECTOR:
            	tgc = new SysCAMSPortConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
            	break;
            case ELN_CONNECTOR:
            	tgc = new ELNConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
            	break;
            case CONNECTOR_NODE_TMLARCHI:
                tgc = new TMLArchiConnectorNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_TML_ASSOCIATION_NAV:
                tgc = new TGConnectorTMLAssociationNav(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_TML_COMPOSITION_OPERATOR:
                tgc = new TGConnectorTMLCompositionOperator(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;

            case CONNECTOR_TMLCP:
                tgc = new TGConnectorTMLCP(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_MESSAGE_ASYNC_TMLSD:
                tgc = new TGConnectorMessageAsyncTMLSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;


            case CONNECTOR_DERIVE_REQ:
                tgc = new TGConnectorDerive(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_COPY_REQ:
                tgc = new TGConnectorCopy(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_COMPOSITION_REQ:
                tgc = new TGConnectorComposition(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_VERIFY_REQ:
                tgc = new TGConnectorVerify(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_PROSMD:
                tgc = new TGConnectorProSMD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_PROCSD:
                tgc = new TGConnectorProCSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_DELEGATE_PROCSD:
                tgc = new TGConnectorDelegateProCSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
            case CONNECTOR_PROCSD_PORT_INTERFACE:
                tgc = new TGConnectorPortInterface(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);

        }

        return tgc;

    }

/*    public final static CAMSBlockConnector addCAMSConnector(int x, int y, int id, TDiagramPanel tdp, CAMSConnectingPoint p1, CAMSConnectingPoint p2, Vector<Point> listPoint) {
        CAMSBlockConnector cbc = null;
        cbc = new CAMSBlockConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
        return cbc;
    }*/

} // Class
