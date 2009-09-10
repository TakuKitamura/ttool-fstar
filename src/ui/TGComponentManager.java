/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TGComponentManager
 * Definition and creation of all possible TURTLE graphical component
 * Creation: 21/12/2003
 * @version 1.1 28/10/2005
 * @author Ludovic APVRILLE
 * @see
 */

package ui;

//import java.awt.*;
import java.util.*;

import ui.ad.*;
import ui.cd.*;
import ui.dd.*;
import ui.iod.*;
import ui.sd.*;
import ui.ucd.*;

import ui.req.*;
import ui.ebrdd.*;

import ui.tmlad.*;
import ui.tmlcd.*;
import ui.tmldd.*;
import ui.tmlcompd.*;

import ui.procsd.*;
import ui.prosmd.*;

import ui.oscd.*;
import ui.osad.*;

import ui.ncdd.*;

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
    
    public static final int UCD_ACTOR = 700;
    public static final int UCD_USECASE = 701;
    public static final int UCD_BORDER = 702;
    
    public static final int TDD_NODE = 800;
    public static final int TDD_ARTIFACT = 801;
    
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
    public static final int TMLAD_SELECT_EVT = 1018;
	public static final int TMLAD_FOR_STATIC_LOOP = 1020;
	public static final int TMLAD_EXECC = 1021;
	public static final int TMLAD_EXECC_INTERVAL = 1022;
	public static final int TMLAD_RANDOM = 1024;
	public static final int TMLAD_DELAY = 1026;
	public static final int TMLAD_INTERVAL_DELAY = 1028;
	public static final int TMLAD_FOR_EVER_LOOP = 1030;
	
	public static final int TMLARCHI_CPUNODE = 1100;
	public static final int TMLARCHI_ARTIFACT = 1101;
	public static final int TMLARCHI_BUSNODE = 1102;
	public static final int TMLARCHI_COMMUNICATION_ARTIFACT = 1103;
	public static final int TMLARCHI_HWANODE = 1104;
	public static final int TMLARCHI_MEMORYNODE = 1105;
	public static final int TMLARCHI_BRIDGENODE = 1106;
	
	public static final int TMLCTD_CCOMPONENT = 1200;
	public static final int TMLCTD_CPORT = 1201;
	public static final int TMLCTD_PCOMPONENT = 1202;
	public static final int TMLCTD_COPORT = 1203;
	public static final int TMLCTD_CREMOTECOMPONENT = 1204;
	
	public static final int EBRDD_START_STATE = 1300;
    public static final int EBRDD_STOP_STATE = 1301;
	public static final int EBRDD_CHOICE = 1302;
    public static final int EBRDD_ERC = 1303;
	public static final int EBRDD_ACTION = 1304;
	public static final int EBRDD_FOR_LOOP = 1305;
	public static final int EBRDD_SEQUENCE = 1306;
	public static final int EBRDD_ESO = 1307;
	public static final int EBRDD_ERB = 1308;
    
    public static final int TREQ_REQUIREMENT = 900;
    public static final int TREQ_OBSERVER = 901;
	public static final int TREQ_EBRDD = 902;
    
    // SMD diagram
    public static final int PROSMD_START_STATE = 2000;
    public static final int PROSMD_STOP_STATE = 2001;
    public static final int PROSMD_SENDMSG = 2002;
    public static final int PROSMD_GETMSG = 2004;
    public static final int PROSMD_CHOICE=2006;
    public static final int PROSMD_JUNCTION=2008;
    public static final int PROSMD_SUBMACHINE=2010;
    public static final int PROSMD_ACTION=2012;
    public static final int PROSMD_PARALLEL=2014;
    public static final int PROSMD_STATE=2016;
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

    
    public static final int EDIT = -1;
    public static final int COMPONENT = 0;
    public static final int CONNECTOR = 1;
    
    
    public final static TGComponent addComponent(int x, int y, int id, TDiagramPanel tdp) {
        TGComponent tgc = null;
        switch (id) {
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
                tgc = new SDInstance(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_ABSOLUTE_TIME_CONSTRAINT:
                tgc = new SDAbsoluteTimeConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_RELATIVE_TIME_CONSTRAINT:
                tgc = new SDRelativeTimeConstraint(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_ACTION_STATE:
                tgc = new SDActionState(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case SD_GUARD:
                tgc = new SDGuard(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIME_INTERVAL:
                tgc = new SDTimeInterval(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIMER_SETTING:
                tgc = new SDTimerSetting(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIMER_EXPIRATION:
                tgc = new SDTimerExpiration(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_TIMER_CANCELLATION:
                tgc = new SDTimerCancellation(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case SD_COREGION:
                tgc = new SDCoregion(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case UCD_ACTOR:
                tgc = new UCDActor(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
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
                tgc = new EBRDDERC(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case EBRDD_FOR_LOOP:
                tgc = new EBRDDForLoop(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
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
            case TMLAD_SEQUENCE:
                tgc = new TMLADSequence(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLAD_SELECT_EVT:
                tgc = new TMLADSelectEvt(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLAD_RANDOM:
                tgc = new TMLADRandom(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLCTD_CCOMPONENT:
                tgc = new TMLCCompositeComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLCTD_CREMOTECOMPONENT:
                tgc = new TMLCRemoteCompositeComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLCTD_CPORT:
                tgc = new TMLCCompositePort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLCTD_PCOMPONENT:
                tgc = new TMLCPrimitiveComponent(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLCTD_COPORT:
                tgc = new TMLCChannelOutPort(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
			case TMLARCHI_CPUNODE:
                tgc = new TMLArchiCPUNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break; 
			case TMLARCHI_BUSNODE:
                tgc = new TMLArchiBUSNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break; 
			case TMLARCHI_BRIDGENODE:
                tgc = new TMLArchiBridgeNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break; 
			case TMLARCHI_HWANODE:
                tgc = new TMLArchiHWANode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break; 
			case TMLARCHI_MEMORYNODE:
                tgc = new TMLArchiMemoryNode(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break; 
			case TMLARCHI_ARTIFACT:
                tgc = new TMLArchiArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
            case TMLARCHI_COMMUNICATION_ARTIFACT:
                tgc = new TMLArchiCommunicationArtifact(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp);
                break;
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
        if (tgc instanceof 	TADDeterministicDelay) {
            return 	TAD_DETERMINISTIC_DELAY;
        } else if (tgc instanceof TADParallel) {
            return TAD_PARALLEL;
        } else if (tgc instanceof TADStopState) {
            return 	TAD_STOP_STATE;
        } else if (tgc instanceof TADStartState) {
            return 	TAD_START_STATE;
        } else if (tgc instanceof TADActionState) {
            return 	TAD_ACTION_STATE;
        } else if (tgc instanceof TADArrayGetState) {
            return 	TAD_ARRAY_GET;
        } else if (tgc instanceof TADArraySetState) {
            return 	TAD_ARRAY_SET;
        } else if (tgc instanceof TADTimeLimitedOffer) {
            return 	TAD_TIME_LIMITED_OFFER;
        } else if (tgc instanceof TADJunction) {
            return 	TAD_JUNCTION;
        } else if (tgc instanceof TADNonDeterministicDelay) {
            return 	TAD_NON_DETERMINISTIC_DELAY;
        } else if (tgc instanceof TADTimeInterval) {
            return 	TAD_DELAY_NON_DETERMINISTIC_DELAY;
        } else if (tgc instanceof TADTimeLimitedOfferWithLatency) {
            return 	TAD_TIME_LIMITED_OFFER_WITH_LATENCY;
        } else if (tgc instanceof TADTimeCapture) {
            return 	TAD_TIME_CAPTURE;
        } else if (tgc instanceof TADChoice) {
            return 	TAD_CHOICE;
        } else if (tgc instanceof TCDTClass) {
            return 	TCD_TCLASS;
        } else if (tgc instanceof TCDTObject) {
            return 	TCD_TOBJECT;
        } else if (tgc instanceof TCDTData) {
            return 	TCD_TDATA;
        } else if (tgc instanceof TCDParallelOperator) {
            return 	TCD_PARALLEL_OPERATOR;
        } else if (tgc instanceof TCDSequenceOperator) {
            return 	TCD_SEQUENCE_OPERATOR;
        } else if (tgc instanceof TCDPreemptionOperator) {
            return 	TCD_PREEMPTION_OPERATOR;
        } else if (tgc instanceof TCDSynchroOperator) {
            return 	TCD_SYNCHRO_OPERATOR;
        }  else if (tgc instanceof TCDInvocationOperator) {
            return 	TCD_INVOCATION_OPERATOR;
        } else if (tgc instanceof TCDWatchdogOperator) {
            return 	TCD_WATCHDOG_OPERATOR;
        } else if (tgc instanceof TGConnectorFullArrow) {
            return 	CONNECTOR_AD_DIAGRAM;
        } else if (tgc instanceof TGConnectorAssociation) {
            return 	CONNECTOR_ASSOCIATION;
        } else if (tgc instanceof TGConnectorAssociationWithNavigation) {
            return 	CONNECTOR_ASSOCIATION_NAVIGATION;
        } else if (tgc instanceof TGConnectorAttribute) {
            return 	CONNECTOR_ATTRIBUTE;
        } else if (tgc instanceof TGConnectorInteraction) {
            return 	CONNECTOR_INTERACTION;
        } else if (tgc instanceof TGCNote) {
            return 	UML_NOTE;
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
        } else if (tgc instanceof TGConnectorMessageAsyncSD) {
            return CONNECTOR_MESSAGE_ASYNC_SD;
        } else if (tgc instanceof TGConnectorMessageSyncSD) {
            return CONNECTOR_MESSAGE_SYNC_SD;
        } else if (tgc instanceof TGConnectorRelativeTimeSD) {
            return CONNECTOR_RELATIVE_TIME_SD;
        } else if (tgc instanceof SDInstance) {
            return SD_INSTANCE;
        } else if (tgc instanceof SDAbsoluteTimeConstraint) {
            return SD_ABSOLUTE_TIME_CONSTRAINT;
        } else if (tgc instanceof SDRelativeTimeConstraint) {
            return SD_RELATIVE_TIME_CONSTRAINT;
        } else if (tgc instanceof SDActionState) {
            return SD_ACTION_STATE;
        } else if (tgc instanceof SDGuard) {
            return SD_GUARD;
        } else if (tgc instanceof SDTimeInterval) {
            return SD_TIME_INTERVAL;
        } else if (tgc instanceof SDTimerSetting) {
            return SD_TIMER_SETTING;
        } else if (tgc instanceof SDTimerExpiration) {
            return SD_TIMER_EXPIRATION;
        } else if (tgc instanceof SDTimerCancellation) {
            return SD_TIMER_CANCELLATION;
        } else if (tgc instanceof SDCoregion) {
            return SD_COREGION;
        } else if (tgc instanceof UCDActor) {
            return UCD_ACTOR;
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
        } else if (tgc instanceof EBRDDSequence) {
            return EBRDD_SEQUENCE;
        } else if (tgc instanceof EBRDDESO) {
            return EBRDD_ESO;
        } else if (tgc instanceof EBRDDERB) {
            return EBRDD_ERB;
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
        } else if (tgc instanceof TMLADSequence) {
            return TMLAD_SEQUENCE;
        } else if (tgc instanceof TMLADSelectEvt) {
            return TMLAD_SELECT_EVT;
        } else if (tgc instanceof TMLADRandom) {
            return TMLAD_RANDOM;
        } else if (tgc instanceof TMLCCompositeComponent) {
            return TMLCTD_CCOMPONENT;
		} else if (tgc instanceof TMLCRemoteCompositeComponent) {
            return TMLCTD_CREMOTECOMPONENT;
		} else if (tgc instanceof TMLCCompositeComponent) {
            return TMLCTD_CPORT;
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
        } else if (tgc instanceof TMLArchiBUSNode) {
            return TMLARCHI_BUSNODE;
        } else if (tgc instanceof TMLArchiBridgeNode) {
            return TMLARCHI_BRIDGENODE;
        } else if (tgc instanceof TMLArchiHWANode) {
            return TMLARCHI_HWANODE;
        } else if (tgc instanceof TMLArchiMemoryNode) {
            return TMLARCHI_MEMORYNODE;
        } else if (tgc instanceof TMLArchiArtifact) {
            return TMLARCHI_ARTIFACT;
        } else if (tgc instanceof TMLArchiCommunicationArtifact) {
            return TMLARCHI_COMMUNICATION_ARTIFACT;
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
        }
        else if (tgc instanceof TGConnectorPortInterface) {
            return CONNECTOR_PROCSD_PORT_INTERFACE;
        }
        
        else if (tgc instanceof ProCSDComponent) {
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


    public final static TGConnector addConnector(int x, int y, int id, TDiagramPanel tdp, TGConnectingPoint p1, TGConnectingPoint p2, Vector listPoint) {
        TGConnector tgc = null;
        switch(id) {
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
                //System.out.println("Connector interaction");
                break;
            case CONNECTOR_MESSAGE_ASYNC_SD:
                tgc = new TGConnectorMessageAsyncSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //System.out.println("Connector interaction");
                break;
            case CONNECTOR_MESSAGE_SYNC_SD:
                tgc = new TGConnectorMessageSyncSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //System.out.println("Connector interaction");
                break;
            case CONNECTOR_RELATIVE_TIME_SD:
                tgc = new TGConnectorRelativeTimeSD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                //System.out.println("Connector interaction");
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
            case CONNECTOR_TMLAD:
                tgc = new TGConnectorTMLAD(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
                break;
			case CONNECTOR_PORT_TMLC:
                tgc = new TMLCPortConnector(x, y, tdp.getMinX(), tdp.getMaxX(), tdp.getMinY(), tdp.getMaxY(), false, null, tdp, p1, p2, listPoint);
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
    
} // Class
