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




package tmltranslator.toproverif;

import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import proverifspec.*;
import tmltranslator.*;
import common.ConfigurationTTool;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Class TML2ProVerif
 * Creation: 03/09/2010
 * @version 1.1 03/09/2010
 * @author Ludovic APVRILLE, Letitia LI
 */
public class TML2ProVerif {

 //   private final static String ATTR_DELIM = "__";

    private final static String UNKNOWN = "UNKNOWN";

    private final static String TRUE = "TRUE";
    private final static String FALSE = "FALSE";

    private final static String PK_PK = "pk";
    private final static String PK_ENCRYPT = "aencrypt";
    private final static String PK_DECRYPT = "adecrypt";
    private final static String PK_SIGN = "sign";
    private final static String PK_VERIFYSIGN = "verifySign";

    private final static String CERT_CERT = "cert";
    private final static String CERT_VERIFYCERT = "verifyCert";
    private final static String CERT_GETPK = "getpk";

    private final static String SK_ENCRYPT = "sencrypt";
    private final static String SK_DECRYPT = "sdecrypt";

    private final static String DH_DH = "DH";

    private final static String MAC_MAC = "MAC";
    private final static String MAC_VERIFYMAC = "verifyMAC";

    private final static String HASH_HASH = "hash";

    private final static String CH_MAINCH = "ch";
    private final static String CH_ENCRYPT = "privChEnc";
    private final static String CH_DECRYPT = "privChDec";

    private final static String CHCTRL_CH = "chControl";
    private final static String CHCTRL_ENCRYPT = "chControlEnc";
    private final static String CHCTRL_DECRYPT = "chControlDec";
  //  private final static Integer channelPublic = 0;
    private final static Integer channelPrivate = 1;
    private final static Integer channelUnreachable = 2;
    private ProVerifSpec spec;
    private TMLMapping<?> tmlmap;
    private TMLModeling<?> tmlmodel;
   // private boolean stateReachability;

   // private Vector warnings;
    public Map<String, Integer> channelMap = new HashMap<String,Integer>();

    public TML2ProVerif(TMLMapping<?> _tmlmap) {
        this.tmlmap = _tmlmap;
        this.spec = null;
        this.tmlmodel= tmlmap.getTMLModeling();
    }


    public void checkChannels(){
    	List<TMLChannel> channels = tmlmodel.getChannels();
    	List<TMLTask> destinations = new ArrayList<TMLTask>();
    	TMLTask a; 
    	for (TMLChannel channel: channels){	
    		destinations.clear();
    		if (channel.isBasicChannel()){
    			a = channel.getOriginTask();
    			destinations.add(channel.getDestinationTask());
    		}
    		else {
    			a=channel.getOriginTasks().get(0);
    			destinations.addAll(channel.getDestinationTasks());
    		}  
    		HwCPU node1 = (HwCPU) tmlmap.getHwNodeOf(a);
    		for (TMLTask t: destinations){
    			//	        List<HwBus> buses = new ArrayList<HwBus>();
    			HwNode node2 = tmlmap.getHwNodeOf(t);
    			if (node1==node2){
    				TraceManager.addDev("Channel "+channel.getName() + " between Task "+ a.getTaskName() + " and Task " + t.getTaskName() + " is confidential");
    				channelMap.put(channel.getName(), channelPrivate);
    			}
    			if (node1!=node2){
    				//Navigate architecture for node
    				List<HwLink> links = tmlmap.getTMLArchitecture().getHwLinks();
    				//  HwNode last = node1;
    				List<HwNode> found = new ArrayList<HwNode>();	
    				List<HwNode> done = new ArrayList<HwNode>();
    				List<HwNode> path = new ArrayList<HwNode>();
    				Map<HwNode, List<HwNode>> pathMap = new HashMap<HwNode, List<HwNode>>();
    				for (HwLink link: links){
    					if (link.hwnode == node1){
    						found.add(link.bus);
    						List<HwNode> tmp = new ArrayList<HwNode>();
    						tmp.add(link.bus);
    						pathMap.put(link.bus, tmp);
    					}
    				}
    				outerloop:
    					while (found.size()>0){
    						HwNode curr = found.remove(0);
    						for (HwLink link: links){
    							if (curr == link.bus){
    								if (link.hwnode == node2){
    									path = pathMap.get(curr);
    									break outerloop;
    								}
    								if (!done.contains(link.hwnode) && !found.contains(link.hwnode) && link.hwnode instanceof HwBridge){
    									found.add(link.hwnode);
    									List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
    									tmp.add(link.hwnode);
    									pathMap.put(link.hwnode, tmp);
    								}
    							}
    							else if (curr == link.hwnode){
    								if (!done.contains(link.bus) && !found.contains(link.bus)){
    									found.add(link.bus);
    									List<HwNode> tmp = new ArrayList<HwNode>(pathMap.get(curr));
    									tmp.add(link.bus);
    									pathMap.put(link.bus, tmp);
    								}
    							}
    						}
    						done.add(curr);
    					}
    				if (path.size() ==0){
    					TraceManager.addDev("Path does not exist for channel " + channel.getName() + " between Task " + a.getTaskName() + " and Task " + t.getTaskName());
    					channelMap.put(channel.getName(), channelUnreachable);
    				}
    				else {
    					int priv=1;
    					HwBus bus;
    					//Check if all buses and bridges are private
    					for (HwNode n: path){
    						if (n instanceof HwBus){
    							bus = (HwBus) n;
    							TraceManager.addDev("BUS PRIVACY "+bus.privacy);
    							if (bus.privacy ==0){
    								priv=0;
    								break;
    							}
    						}
    					}
    					channelMap.put(channel.getName(), priv);
    					TraceManager.addDev("Channel "+channel.getName() + " between Task "+ a.getTaskName() + " and Task " + t.getTaskName() + " is " + (priv==1 ? "confidential" : "not confidential"));
    				}
    			}
    		}
    	}  
    }

    public boolean saveInFile(String path) throws FileException {
    	//Our hash is saved in config
    	//TraceManager.addDev(this.spec.toString());
    	String hashCode= Integer.toString(this.spec.getStringSpec().hashCode());
    	File file = new File(path);
    	BufferedReader br;
    	if (file.exists()){
    		String hash = ConfigurationTTool.ProVerifHash;
    		if (!hash.equals("")){
    			try {
    				br = new BufferedReader(new FileReader(path));
    				String s = br.readLine();
    				String tmp;
    				while ((tmp = br.readLine()) !=null){
    					s = s+"\n" + tmp;
    				}
    				String fileHash = Integer.toString(s.hashCode());
    				if (!hash.equals(fileHash)){
    					if(JOptionPane.showConfirmDialog(null, "File " + path + " already exists. Do you want to overwrite?", "Overwrite File?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
    	    				br.close();

    	    				return false;
    					}
    				}
    				br.close();
    			} catch (Exception e) {
    				//
    			}
    		}
    	}
    	FileUtils.saveFile(path, this.spec.getStringSpec());
    	ConfigurationTTool.ProVerifHash = hashCode;
    	try {
    		ConfigurationTTool.saveConfiguration();
    	} catch (Exception e){
    		//
    	}
    	return true;
    }

//    public Vector getWarnings() {
//        return this.warnings;
//    }

    public TMLMapping<?> getMapping () {
        return this.tmlmap;
    }

    public ProVerifSpec generateProVerif(boolean _debug, boolean _optimize, boolean _stateReachability, boolean _typed) {
	TraceManager.addDev("generating spec...");

       // this.stateReachability = _stateReachability;
       // this.warnings = new Vector();
        if (_typed)
            this.spec = new ProVerifSpec (new ProVerifPitypeSyntaxer ());
        else
            this.spec = new ProVerifSpec (new ProVerifPiSyntaxer ());
        
        checkChannels();

        List<TMLAttribute> allKnowledge = this.makeStartingProcess();

        this.makeHeader();

        this.makeTasks(allKnowledge);

        return this.spec;
    }

  /*  public ProVerifOutputAnalyzer getOutputAnalyzer () {
        return new ProVerifOutputAnalyzer (this);
    }
*/
 
//
//    private static String makeAttrName (String... _params) {
//        String result = "";
//        boolean first = true;
//        for (String p: _params) {
//            if (first)
//                first = false;
//            else
//                result += ATTR_DELIM;
//            result += p;
//        }
//        return result;
//    }

    public void makeHeader() {
        TraceManager.addDev("\n\n=+=+=+ Making Headers +=+=+=");
        this.spec.addDeclaration (new ProVerifComment    ("Boolean return types"));
        this.spec.addDeclaration (new ProVerifConst      (TRUE, "bitstring"));
        this.spec.addDeclaration (new ProVerifConst      (FALSE, "bitstring"));
        this.spec.addDeclaration (new ProVerifComment    ("Functions data"));
        this.spec.addDeclaration (new ProVerifConst      (UNKNOWN, "bitstring"));

        this.spec.addDeclaration (new ProVerifComment    ("Public key cryptography"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_PK, new String[] {"bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_ENCRYPT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("y", "bitstring")}, PK_DECRYPT + " (" + PK_ENCRYPT + " (x, " + PK_PK + " (y)), y) = x"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_SIGN, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifFunc       (PK_VERIFYSIGN, new String[] {"bitstring", "bitstring", "bitstring"}, "bitstring",
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("sk", "bitstring")}, PK_VERIFYSIGN + " (m, " + PK_SIGN + " (m, sk), " + PK_PK + " (sk)) = " + TRUE,
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("m2", "bitstring"), new ProVerifVar ("ppk", "bitstring")}, PK_VERIFYSIGN + " (m, m2, ppk) = " + FALSE))));


        this.spec.addDeclaration (new ProVerifComment    ("Certificates"));
        this.spec.addDeclaration (new ProVerifFunc       (CERT_CERT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifFunc       (CERT_VERIFYCERT, new String[] {"bitstring", "bitstring"}, "bitstring",
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("epk", "bitstring"), new ProVerifVar ("sk", "bitstring")}, CERT_VERIFYCERT + " (" + CERT_CERT + " (epk, " + PK_SIGN + " (epk, sk)), " + PK_PK + " (sk)) = " + TRUE,
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("ppk", "bitstring")}, CERT_VERIFYCERT + " (m, ppk) = " + FALSE))));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("epk", "bitstring"), new ProVerifVar ("sk", "bitstring")}, CERT_GETPK + " (" + CERT_CERT + " (epk, " + PK_SIGN + " (epk,sk))) = epk"));

        this.spec.addDeclaration (new ProVerifComment    ("Symmetric key cryptography"));
        this.spec.addDeclaration (new ProVerifFunc       (SK_ENCRYPT, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("k", "bitstring")}, SK_DECRYPT + " (" + SK_ENCRYPT + " (x, k), k) = x"));

        this.spec.addDeclaration (new ProVerifComment    ("Diffie-Hellman"));
        this.spec.addDeclaration (new ProVerifFunc       (DH_DH, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifEquation   (new ProVerifVar[] {new ProVerifVar ("x", "bitstring"), new ProVerifVar ("y", "bitstring")}, DH_DH + " (" + PK_PK + " (x), y) = " + DH_DH + " (" + PK_PK + " (y), x)"));

        this.spec.addDeclaration (new ProVerifComment    ("MAC"));
        this.spec.addDeclaration (new ProVerifFunc       (MAC_MAC, new String[] {"bitstring", "bitstring"}, "bitstring"));
        this.spec.addDeclaration (new ProVerifFunc       (MAC_VERIFYMAC, new String[] {"bitstring", "bitstring", "bitstring"}, "bitstring",
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("k", "bitstring")}, MAC_VERIFYMAC + " (m, k, " + MAC_MAC + " (m, k)) = " + TRUE,
                                  new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("m", "bitstring"), new ProVerifVar ("m2", "bitstring"), new ProVerifVar ("k", "bitstring")}, MAC_VERIFYMAC + " (m, k, m2) = " + FALSE))));

        this.spec.addDeclaration (new ProVerifComment    ("HASH"));
        this.spec.addDeclaration (new ProVerifFunc       (HASH_HASH, new String[] {"bitstring"}, "bitstring"));

        this.spec.addDeclaration (new ProVerifComment    ("Channel"));
        this.spec.addDeclaration (new ProVerifVar        (CH_MAINCH, "channel"));
        this.spec.addDeclaration (new ProVerifFunc       (CH_ENCRYPT, new String[] {"bitstring"}, "bitstring", true));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring")}, CH_DECRYPT + " (" + CH_ENCRYPT + " (x)) = x", true));

	/* Queries */
        this.spec.addDeclaration (new ProVerifComment    ("Queries Secret"));
        TraceManager.addDev("Queries Secret"); 
        for (String[] pragma: tmlmap.getPragmas ()){
            if (pragma[0].equals("#Confidentiality")){
                    this.spec.addDeclaration (new ProVerifQueryAtt   (pragma[1].replaceAll("\\.", "__"), true));
                    TraceManager.addDev("|    attacker (" + pragma[1].replaceAll("\\.", "__") + ")"); 
                
	    }
	}
        // Queries for states
        this.spec.addDeclaration (new ProVerifComment    ("Queries Event"));
        TraceManager.addDev ("Queries Event"); 
        for (TMLTask task: tmlmodel.getTasks()){
	    int stateNum=0;
	    TMLActivity act= task.getActivityDiagram();
	    TraceManager.addDev("ACT "+act);
	    TMLActivityElement ae = act.getFirst();
	    TraceManager.addDev("FIRST "+ae);

	    while (ae!=null){
		TraceManager.addDev("EVENT "+ae.getName());
                this.spec.addDeclaration (new ProVerifQueryEv    (new ProVerifVar[] {}, "enteringState__" + task.getName() + "__" + stateNum));
                this.spec.addDeclaration (new ProVerifEvDecl     ("enteringState__" + task.getName() + "__" + stateNum, new String[] {}));
                TraceManager.addDev("|    event (enteringState__" + task.getName() + "__" + stateNum + ")"); 
                stateNum++;
		ae = ae.getNextElement(0);
	    }
        }
	//  for (TMLChannel chan: tmlmodel.getChannels(task){
	  //  this.spec.addDeclaration();
	 // }
          //
        //}
        this.spec.addDeclaration (new ProVerifComment    ("Control Channel"));
        this.spec.addDeclaration (new ProVerifVar        (CHCTRL_CH, "channel"));
        this.spec.addDeclaration (new ProVerifFunc       (CHCTRL_ENCRYPT, new String[] {"bitstring"}, "bitstring", true));
        this.spec.addDeclaration (new ProVerifReduc      (new ProVerifVar[] {new ProVerifVar ("x", "bitstring")}, CHCTRL_DECRYPT + " (" + CHCTRL_ENCRYPT + " (x)) = x", true));

        /* Declare all the call__*** variables */
        List<TMLTask> tasks = this.tmlmap.getMappedTasks();

        for (TMLTask task:tasks){
        	this.spec.addDeclaration (new ProVerifVar        ("call__" + task.getName() + "__0", "bitstring", true));
  
        }
       // String action = "(";


        this.spec.addDeclaration (new ProVerifComment    ("Data"));
        TraceManager.addDev("Constants");
    }

    private List<TMLAttribute> makeStartingProcess() {
        TraceManager.addDev("\n\n=+=+=+ Making Starting Process +=+=+=");
        List<TMLAttribute> systemKnowledge = new LinkedList<TMLAttribute>();
        // Create starting process
        ProVerifProcess p = new ProVerifProcess("starting__", new ProVerifVar[] {});
        ProVerifProcInstr lastInstr = p;

        // Get all the tasks
        List<TMLTask> tasks = tmlmap.getMappedTasks();


 	// Call all the processes corresponding to crossroads in the state machine
        ProVerifProcRawGlobing globing = new ProVerifProcRawGlobing ("! (", ")");
        lastInstr.setNextInstr (globing);
        lastInstr = globing.getIntra ();

        lastInstr = lastInstr.setNextInstr (new ProVerifProcNew ("sessionID", "bitstring"));

        ProVerifProcParallel paral = new ProVerifProcParallel ();
        lastInstr = lastInstr.setNextInstr (paral);

        for(TMLTask task: tasks) {
           paral.addInstr (new ProVerifProcCall (task.getName () + "__0", new ProVerifVar[] {new ProVerifVar ("sessionID", "bitstring")}));
        }


        globing = new ProVerifProcRawGlobing ("(", ")");
        paral.addInstr (globing);
        lastInstr = globing.getIntra ();


 
        LinkedList<ProVerifVar> processArgs = new LinkedList<ProVerifVar>();
        processArgs.add (new ProVerifVar ("sessionID", "bitstring"));

        // Call every start process
        TraceManager.addDev("Finding processes");
        paral = new ProVerifProcParallel ();
        for(TMLTask task: tasks)
            paral.addInstr (new ProVerifProcCall (task.getName() + "__start", processArgs.toArray (new ProVerifVar[processArgs.size ()])));
        lastInstr = lastInstr.setNextInstr (paral);

        // Set main process
        spec.setMainProcess(p);

        return systemKnowledge;
    }

    /**
     * Generate ProVerif code for each process for each TMLTask
     */
    private void makeTasks( List<TMLAttribute> allKnowledge ) {
        TraceManager.addDev("\n\n=+=+=+ Making Tasks +=+=+=");

        List<TMLTask> tasks = tmlmap.getMappedTasks();
        
        for(TMLTask task: tasks)
            makeTask(task, allKnowledge);
    }


    /**
     * Compute a list of ProVerifVar corresponding to the attributes of the block
     */


    /**
     * Generate ProVerif code for one TMLTask
     */
    private void makeTask(TMLTask task, List<TMLAttribute> allKnowledge) {
        TraceManager.addDev("\nTMLTask: " + task.getName ());

        List<ProVerifVar> processArgs = new LinkedList<ProVerifVar>();
        processArgs.add (new ProVerifVar ("sessionID", "bitstring"));

        ProVerifProcInstr lastInstr = new ProVerifProcess(task.getName() + "__start", processArgs.toArray (new ProVerifVar[processArgs.size ()]));
        spec.addDeclaration (lastInstr);
        List<String> variables = new ArrayList<String>();
		Set<String> channelOutNames = new HashSet<String>();
	
		for (TMLAttribute arg: task.getAttributes ()) {
			variables.add(arg.getName());
        }

		for (TMLWriteChannel ch:task.getWriteChannels()){
		    String chName = ch.getChannel(0).getName();
		    channelOutNames.add("data__"+chName);
		}

        // Create a ProVerif Variable corresponding to each attribute block
        for (String arg: variables) {
            ProVerifProcInstr tmpInstr;
            tmpInstr = new ProVerifProcNew (arg, "bitstring");
            TraceManager.addDev("|    TMLAttribute: " + arg);
            lastInstr = lastInstr.setNextInstr (tmpInstr);
        }
	
        for (String chName: channelOutNames){
		    ProVerifProcInstr tmpInstr;
		    tmpInstr = new ProVerifProcNew (chName, "bitstring");
            TraceManager.addDev("|    TMLAttribute: " + chName);
            lastInstr = lastInstr.setNextInstr (tmpInstr);
		}

        // Call the first "real" process
        String tmp = "out (" + CHCTRL_CH + ", " + CHCTRL_ENCRYPT + " ((sessionID, call__" + task.getName () + "__0";
		
        for (String arg:variables){
		    tmp+=", " + arg;
		}

		for (String chName: channelOutNames){
		    tmp+=", " + chName;
		}
        
		lastInstr = lastInstr.setNextInstr(new ProVerifProcRaw (tmp + ")))"));

        // Generate a new process for every simplified element of the block's state machine
        ProVerifProcInstr p = new ProVerifProcess(task.getName()+"__0", new ProVerifVar[] {new ProVerifVar ("sessionID", "bitstring")});
        List<ProVerifVar> attributes = new LinkedList<ProVerifVar> ();


        attributes.add (new ProVerifVar ("sessionID", "bitstring", false, true));
        attributes.add (new ProVerifVar ("call__" + task.getName () + "__0", "bitstring", false, true));
	
        for (String arg:variables) {
            attributes.add (new ProVerifVar (arg, "bitstring"));
        }

        for (String chName: channelOutNames){
		    attributes.add(new ProVerifVar(chName, "bitstring"));
		}

        spec.addDeclaration(p);
		p = p.setNextInstr (new ProVerifProcIn (CHCTRL_CH, new ProVerifVar[] {new ProVerifVar ("chControlData", "bitstring")}));
		p = p.setNextInstr (new ProVerifProcLet (attributes.toArray (new ProVerifVar[attributes.size()]), CHCTRL_DECRYPT + " (chControlData)"));
		TMLActivity act= task.getActivityDiagram();
		TMLActivityElement ae = act.getFirst();
		int stateNum=0;
		
		while (ae!=null){
		    //declare entering
		    p = p.setNextInstr (new ProVerifProcRaw ("event enteringState__" + task.getName() + "__" + stateNum + "()", true));
		    stateNum++;
		    if (ae instanceof TMLChoice){
			//TMLChoice aechoice = (TMLChoice) ae;
	//			for (int i=0; i< ae.getNbNext(); i++){
	//			    
	//			}
		    }
		    else {
		        if (ae instanceof TMLActivityElementChannel){
		            TMLActivityElementChannel aec = (TMLActivityElementChannel) ae;
		            int channelStatus = channelMap.get(aec.getChannel(0).getName());
		            
		            if (aec instanceof TMLWriteChannel){
				        tmp = "out (" + CH_MAINCH + ", ";
				        //Look up privacy
				        if (channelStatus!=channelUnreachable){
				            if (channelStatus==channelPrivate)
		                                tmp += CH_ENCRYPT + " (";
		            	    	    tmp += "data__"+aec.getChannel(0).getName()+")";
				            if (channelStatus==channelPrivate)
		                                tmp += ")";
				            p = p.setNextInstr(new ProVerifProcRaw (tmp, true));
				        }
		            }
		            else {
				        if (channelStatus==channelPrivate) {
					    	List<ProVerifVar> vars = new LinkedList<ProVerifVar> ();
	                	    TraceManager.addDev("|    |    in (chPriv, ...)");
	                	    vars.add (new ProVerifVar ("data__"+ aec.getChannel(0).getName(), "bitstring"));
	                	    p = p.setNextInstr (new ProVerifProcIn (CH_MAINCH, new ProVerifVar[] {new ProVerifVar ("privChData", "bitstring")}));
	                	    p = p.setNextInstr (new ProVerifProcLet (vars.toArray (new ProVerifVar[vars.size()]), CH_DECRYPT + " (privChData)"));
		            	}
				        else {
			                List<ProVerifVar> vars = new LinkedList<ProVerifVar> ();
				            vars.add (new ProVerifVar ("data__"+ aec.getChannel(0).getName(), "bitstring"));
				            p=p.setNextInstr(new ProVerifProcIn (CH_MAINCH, vars.toArray (new ProVerifVar[vars.size()])));
				        }
		            }
		        }

		        ae = ae.getNextElement(0);
		    }
		}	
    }

    public ProVerifOutputAnalyzer getOutputAnalyzer () {
        return new ProVerifOutputAnalyzer (null);
    }
}
