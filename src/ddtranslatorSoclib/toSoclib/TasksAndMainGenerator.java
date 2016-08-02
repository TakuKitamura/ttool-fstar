
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
 * Class AVATAR2CSOCLIB
 * Creation: 01/07/2014
 * @version 1.0 01/07/2014 
 * @version 2.0 07/07/2015
 * @author Ludovic APVRILLE, Raja GATGOUT, Julien HENON, Daniela GENIUS
 * @see
 */

package ddtranslatorSoclib.toSoclib;

import java.awt.*;
import java.util.*;

import java.io.*;

import myutil.*;
import avatartranslator.*;
import ddtranslatorSoclib.*;

public class TasksAndMainGenerator {

    private final static int USEC = 0;
    private final static int MSEC = 1;
    private final static int SEC = 2;


    private final static String UNUSED_ATTR = "__attribute__((unused))";    
    //private final static String GENERATED_PATH = "generated_src_soclib" + File.separator; 
    private final static String GENERATED_PATH = "generated_src" + File.separator; 
    private final static String UNKNOWN = "UNKNOWN";
    private final static String CR = "\n";
    private final static String CR2 = "\n\n";

    public static AvatarSpecification avspec;
    public static AvatarddSpecification avddspec;
    private Vector warnings;

    private MainFileSoclib mainFile;
    private Vector<TaskFileSoclib> taskFiles;
    private String makefile_src;
    private String makefile_SocLib;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;
    private boolean includeUserCode = true;
 
    int channel_counter=0;

    public TasksAndMainGenerator(AvatarddSpecification _avddspec,AvatarSpecification _avspec) {
        avspec = _avspec;
        avddspec = _avddspec;
        taskFiles = new Vector<TaskFileSoclib>();
    }

    public void setTimeUnit(int _timeUnit) {
        timeUnit = _timeUnit;
    }

    public void includeUserCode(boolean _inc) {
        includeUserCode = _inc;
    }

    public static String getGeneratedPath() {
        return GENERATED_PATH;
    }

    public void saveInFiles(String path) throws FileException {

	System.err.println("ok");
        TraceManager.addDev("Generating files");

        if (mainFile != null) {
            TraceManager.addDev("Generating main files in " + path + mainFile.getName() + ".h");
            FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".h", Conversion.indentString(mainFile.getHeaderCode(), 2));
            FileUtils.saveFile(path + GENERATED_PATH + mainFile.getName() + ".c", Conversion.indentString(mainFile.getMainCode(), 2));
        }

        for(TaskFileSoclib taskFile: taskFiles) {
	    FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".h", Conversion.indentString(taskFile.getFullHeaderCode/*Soclib*/(), 2));
	    FileUtils.saveFile(path + GENERATED_PATH + taskFile.getName() + ".c", Conversion.indentString(taskFile.getMainCode(), 2));
        }

        // Standard Makefile
        makeMakefileSrc(GENERATED_PATH);
        FileUtils.saveFile(path + "Makefile.src", makefile_src);

        // Makefile for SocLib
        makeMakefileSocLib();
        FileUtils.saveFile(path + "Makefile.soclib", makefile_SocLib);
    }

    public Vector getWarnings() {
        return warnings;
    }

    public void generateSoclib(boolean _debug, boolean _tracing) {
        debug = _debug;
        tracing = _tracing;

        mainFile = new  MainFileSoclib("main");

        avspec.removeCompositeStates();
        avspec.removeTimers();

        if (avspec.hasApplicationCode() && includeUserCode) {
            //mainFile.appendToBeforeMainCode("/* User code */\n");
            mainFile.appendToBeforeMainCode(avspec.getApplicationCode());
            //mainFile.appendToBeforeMainCode("\n/* End of User code */\n\n");
        }
        
        makeTasks();

        makeMainHeader();

        makeMainMutex();

	makeSynchronousChannels();

        makeAsynchronousChannels();  

	makeThreadsInMain(debug);
    }

    public void makeMainMutex() {
        // Create a main mutex
        mainFile.appendToBeforeMainCode("/* Main mutex */" + CR);
	
	mainFile.appendToHCode("extern pthread_mutex_t __mainMutex;" + CR + CR);
	//  mainFile.appendToBeforeMainCode("pthread_mutex_t __mainMutex;" + CR + CR);
        mainFile.appendToBeforeMainCode("pthread_barrier_t barrier ;" + CR );
        mainFile.appendToBeforeMainCode("pthread_attr_t *attr_t;" + CR );
        mainFile.appendToBeforeMainCode("pthread_mutex_t __mainMutex;" + CR2 );
               
	int d=0;

	for(AvatarRelation ar: avspec.getRelations()) {
	    mainFile.appendToBeforeMainCode("#define CHANNEL"+d+" __attribute__((section(\"section_channel"+d+"\")))" + CR ); 

	mainFile.appendToBeforeMainCode("#define LOCK"+d+" __attribute__((section(\"section_lock"+d+"\")))" + CR );//one lock per channel
	    d++;
	}


        mainFile.appendToBeforeMainCode("#define base(arg) arg" + CR2 );
        mainFile.appendToBeforeMainCode("typedef struct mwmr_s mwmr_t;" + CR2);




        mainFile.appendToMainCode("void *ptr;" + CR);
        mainFile.appendToMainCode("pthread_barrier_init(&barrier,NULL, NB_PROC);" +CR);
        mainFile.appendToMainCode("pthread_attr_t *attr_t = malloc(sizeof(pthread_attr_t));" +CR);
        mainFile.appendToMainCode("pthread_attr_init(attr_t);" + CR );
        mainFile.appendToMainCode("pthread_mutex_init(&__mainMutex, NULL);" +CR2);       
    }
  
    public void makeSynchronousChannels() {
	int i=0;        
        // Create a synchronous channel per relation/signal
        mainFile.appendToHCode("/* Synchronous channels */" + CR);
        mainFile.appendToBeforeMainCode("/* Synchronous channels */" + CR);
        mainFile.appendToMainCode("/* Synchronous channels */" + CR);
        for(AvatarRelation ar: avspec.getRelations()) {
	    int j=0;
	if (!ar.isAsynchronous()) {
		    for(i=0; i<ar.nbOfSignals() ; i++) {
			mainFile.appendToHCode("extern syncchannel __" + getChannelName(ar, i) + ";" + CR);

			mainFile.appendToBeforeMainCode("syncchannel __" +getChannelName(ar, i) + ";" + CR);

			//DG 2.8. 
			// In AVATAR, all channels are of width 1 signal but can have depth if asynchronous
	mainFile.appendToMainCode(getChannelName(ar, i) + "_status.rptr = 0;" + CR);		
	mainFile.appendToMainCode(getChannelName(ar, i) + "_status.wptr = 0;" + CR);
	mainFile.appendToMainCode(getChannelName(ar, i) + "_status.usage = 0;" + CR);
	mainFile.appendToMainCode(getChannelName(ar, i) + "_status.lock = 0;" + CR2);

	mainFile.appendToMainCode(getChannelName(ar, i) + ".width = 1;" + CR);
	mainFile.appendToMainCode(getChannelName(ar, i) + ".depth = 1;" + CR);
	mainFile.appendToMainCode(getChannelName(ar, i) + ".gdepth = 1;" + CR);
	mainFile.appendToMainCode(getChannelName(ar, i) + ".buffer = "+getChannelName(ar, i)+"_data;" + CR);
	mainFile.appendToMainCode(getChannelName(ar, i) + ".status = &"+getChannelName(ar, i)+"_status;" + CR2);
	//end DG 2.8. bugfix

			mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
			mainFile.appendToMainCode("__" +getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);		
			mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".mwmr_fifo = &" + getChannelName(ar, i) + ";" + CR);//DG 28.06. enleve &

			/* DG 5.7.2016 force init because mutekh initializer does not work*/		
	mainFile.appendToMainCode(getChannelName(ar, i) + ".status =&"+ getChannelName(ar, i)+"_status;" + CR);

			mainFile.appendToMainCode(getChannelName(ar, i)+".status->lock=0;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i)+".status->rptr=0;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i)+".status->usage=0;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i) + ".status = &"+ getChannelName(ar, i)+"_status->wptr=0;" + CR);

	//mainFile.appendToBeforeMainCode("uint32_t const "+ getChannelName(ar, i)+"_lock= LOCK"+j+";" + CR); //DG 01.07. enleve *

			mainFile.appendToBeforeMainCode("uint32_t const "+ getChannelName(ar, i)+"_lock= LOCK"+i+";" + CR); //DG 2.8.

			//mainFile.appendToBeforeMainCode("struct mwmr_status_s "+ getChannelName(ar, i) +"_status CHANNEL"+j+" = MWMR_STATUS_INITIALIZER(1, 1);" + CR); 	
			mainFile.appendToBeforeMainCode("struct mwmr_status_s "+ getChannelName(ar, i) +"_status CHANNEL"+j+";" + CR); 		

			//synchronous channels are depth 1
			//	mainFile.appendToBeforeMainCode("uint8_t "+getChannelName(ar, i) +"_data[32] CHANNEL"+j+";" + CR);
			mainFile.appendToBeforeMainCode("uint8_t "+getChannelName(ar, i) +"_data[32] CHANNEL"+i+";" + CR);//DG 2.8.
			//mainFile.appendToBeforeMainCode("struct mwmr_s "+getChannelName(ar, i) +" CHANNEL"+i+" = MWMR_INITIALIZER(1, 1, "+getChannelName(ar, i)+"_data,&"+getChannelName(ar, i)+"_status,\""+getChannelName(ar, i)+"\",&"+getChannelName(ar, i)+"_lock);" + CR2);
			mainFile.appendToBeforeMainCode("struct mwmr_s "+getChannelName(ar, i) +" CHANNEL"+i+";" + CR2);		
			j++;	
	  }
	}
      }
    }

    public void makeAsynchronousChannels() {
	if (avspec.ASynchronousExist()){
	    // Create a synchronous channel per relation/signal
	    mainFile.appendToHCode("/* Asynchronous channels */" + CR);
	    mainFile.appendToBeforeMainCode("/* Asynchronous channels */" + CR);
	    mainFile.appendToMainCode("/* Asynchronous channels */" + CR);
            int j=0;
	    for(AvatarRelation ar: avspec.getRelations()) {
		if (ar.isAsynchronous()) {
		    for(int i=0; i<ar.nbOfSignals() ; i++) {
			mainFile.appendToHCode("extern asyncchannel __" + getChannelName(ar, i) + ";" + CR);

			mainFile.appendToBeforeMainCode("asyncchannel __" +getChannelName(ar, i) + ";" + CR);


			//DG 2.8. 
			// In AVATAR, all channels are of width 1 signal but can have depths if asynchronous
			mainFile.appendToMainCode(getChannelName(ar, i) + "_status.rptr = 0;" + CR);		
			mainFile.appendToMainCode(getChannelName(ar, i) + "_status.wptr = 0;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i) + "_status.usage = 0;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i) + "_status.lock = 0;" + CR2);


			mainFile.appendToMainCode(getChannelName(ar, i) + ".width = 1;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i) + ".depth = 1;" + CR);//provisional; to be changed for depth > 1
			mainFile.appendToMainCode(getChannelName(ar, i) + ".gdepth = "+getChannelName(ar, i)+".depth;" + CR);//gdepth = depth becaus width = 1
			mainFile.appendToMainCode(getChannelName(ar, i) + ".buffer = "+getChannelName(ar, i)+"_data;" + CR);
			mainFile.appendToMainCode(getChannelName(ar, i) + ".status = &"+getChannelName(ar, i)+"_status;" + CR);
	//end DG 2.8. bugfix

			mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".inname =\"" + ar.getInSignal(i).getName() + "\";" + CR);
			mainFile.appendToMainCode("__" +getChannelName(ar, i) + ".outname =\"" + ar.getOutSignal(i).getName() + "\";" + CR);
			if (ar.isBlocking()) {
			    mainFile.appendToMainCode("__" +getChannelName(ar, i) + ".isBlocking = 1;" + CR);
			} else {
			    mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".isBlocking = 0;" + CR);
			}
			mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".maxNbOfMessages = " + ar.getSizeOfFIFO() + ";" + CR);
			
			mainFile.appendToMainCode("__" + getChannelName(ar, i) + ".mwmr_fifo = &" + getChannelName(ar, i) + ";" + CR);//DG 28.06. enleve &
				
	/* DG 5.7.2016 force init because mutekh initializer does not work*/		
	mainFile.appendToMainCode(getChannelName(ar, i) + ".status =&"+ getChannelName(ar, i)+"_status;" + CR);

			mainFile.appendToMainCode(getChannelName(ar, i) +".status->lock=0;" + CR);
		    mainFile.appendToMainCode(getChannelName(ar, i)+".status->rptr=0;" + CR);
		mainFile.appendToMainCode(getChannelName(ar, i)+".status->usage=0;" + CR);
	    mainFile.appendToMainCode(getChannelName(ar, i)+".status->wptr=0;" + CR);



	    //	mainFile.appendToBeforeMainCode("uint32_t const "+ getChannelName(ar, i)+"_lock LOCK"+j+";" + CR); 
	    mainFile.appendToBeforeMainCode("uint32_t const "+ getChannelName(ar, i)+"_lock LOCK"+i+";" + CR); // DG 2.8.

	//parameter <depth>  should ultimately be generated; width is fixed as long as data is not explicitly modeled
			//mainFile.appendToBeforeMainCode("struct mwmr_status_s "+ getChannelName(ar, i) +"_status CHANNEL"+i+"=  MWMR_STATUS_INITIALIZER(1, 1);" + CR);
			mainFile.appendToBeforeMainCode("struct mwmr_status_s "+ getChannelName(ar, i) +"_status CHANNEL"+i+";" + CR);
						
			//mainFile.appendToBeforeMainCode("uint8_t "+getChannelName(ar, i) +"_data[32] CHANNEL"+j+";" + CR);
			mainFile.appendToBeforeMainCode("uint8_t "+getChannelName(ar, i) +"_data[32] CHANNEL"+i+";" + CR);//DG 2.8.

	//	mainFile.appendToBeforeMainCode("struct mwmr_s "+getChannelName(ar, i) + " CHANNEL"+i+"= MWMR_INITIALIZER(1, 1, "+getChannelName(ar, i)+"_data,&"+getChannelName(ar, i)+"_status,\""+getChannelName(ar, i)+"\",&"+getChannelName(ar, i)+"_lock);" + CR2);
	mainFile.appendToBeforeMainCode("struct mwmr_s "+getChannelName(ar, i) + " CHANNEL"+i+";" + CR2);
			j++;		
		    }
		}
	    }
	}
    }
      
    public int FindCPUidFromTask(AvatarBlock block){
	LinkedList<AvatarTask> tasks = avddspec.getAllMappedTask();
	for (AvatarTask task : tasks){
	    if (task.getTaskName().equals(block.getName())){
		return task.getCPUNo();
	    }
	}
	return 0;
    }

    public void makeTasks() {
        for(AvatarBlock block: avspec.getListOfBlocks()) {
	    makeTask(block,FindCPUidFromTask(block));
        }
    }

    public void makeTask(AvatarBlock block , int cpuId) {
	TaskFileSoclib taskFile = new TaskFileSoclib(block.getName(),cpuId);
        //taskFile.addToHeaderCode("#include \"main.h\"" + CR);	
        //taskFile.addToMainCode("#include \"" + block.getName() + ".h\"");
        if (includeUserCode) {
            String tmp = block.getGlobalCode();
            if (tmp != null) {
                taskFile.addToMainCode(CR + "// Header code defined in the model" + CR + tmp + CR + "// End of header code defined in the model" + CR + CR);
            }
        }
        defineAllSignal(block,taskFile);

        defineAllStates(block, taskFile);

        defineAllMethods(block, taskFile);

        makeMainFunction(block, taskFile);

        taskFiles.add(taskFile);
    }

    // ---------------------------------------------------------

    public void defineAllStates(AvatarBlock _block, TaskFileSoclib _taskFile) {
        int id = 1;

        _taskFile.addToMainCode("#define STATE__START__STATE 0" + CR);

        for (AvatarStateMachineElement asme: _block.getStateMachine().getListOfElements()) {
            if (asme instanceof AvatarState) {
                _taskFile.addToMainCode("#define STATE__" + asme.getName() + " " + id + CR);
                id ++;
            }
        }
        _taskFile.addToMainCode("#define STATE__STOP__STATE " + id + CR);
        _taskFile.addToMainCode(CR);
    }

    public void defineAllSignal(AvatarBlock _block, TaskFileSoclib _taskFile) {

        for (AvatarSignal as: _block.getSignals()) {
	   
            _taskFile.addToMainCode( "static uint32_t _"  + as.getName()  + ";" + CR);
        }
        _taskFile.addToMainCode(CR);
    }
    
    public void defineAllMethods(AvatarBlock _block, TaskFileSoclib _taskFile) {
        Vector<String> allNames = new Vector<String>();
        for (AvatarMethod am: _block.getMethods()) {
            makeMethod(_block, am, allNames, _taskFile);
        }

        // Make method of father
        makeFatherMethod(_block, _block, allNames, _taskFile);
    }

    private void makeFatherMethod(AvatarBlock _originBlock, AvatarBlock _currentBlock, Vector<String> _allNames, TaskFileSoclib _taskFile) {
        if (_currentBlock.getFather() == null) {
            return;
        }

        for (AvatarMethod am: _currentBlock.getFather().getMethods()) {
            makeMethod(_originBlock, am, _allNames, _taskFile);
        }

        makeFatherMethod(_originBlock, _currentBlock.getFather(), _allNames, _taskFile);
    }

    private void makeMethod(AvatarBlock _block, AvatarMethod _am, Vector<String> _allNames, TaskFileSoclib _taskFile) {
        String ret = "";
        LinkedList<AvatarAttribute> list;
        LinkedList<AvatarAttribute> listA;

        String nameMethod = _block.getName() + "__" +_am.getName();

        for(String s: _allNames) {
            if (s.compareTo(nameMethod) == 0) {
                return;
            }
        }

        list = _am.getListOfReturnAttributes();
        if (list.size() == 0) {
            ret += "void";
        } else {
            ret += getCTypeOf(list.get(0));
        }

        ret += " " + nameMethod + "(";
        list = _am.getListOfAttributes();
        int cpt = 0;
        for(AvatarAttribute aa: list) {
            if (cpt != 0) {
                ret += ", ";
            }
            ret += getCTypeOf(aa) + " " + aa.getName();
            cpt ++;
        }

        ret += ") {" + CR;

        if (tracing) {
            String tr = "";
            cpt = 0;
            if (list.size() > 0) {
                ret += "char my__attr[CHAR_ALLOC_SIZE];" + CR;
                ret += "sprintf(my__attr, \"";
                for(AvatarAttribute aa: list) {
                    if (cpt != 0) {
                        tr += ",";
                        ret += ",";
                    }
                    tr += aa.getName();
                    ret += "%d";
                    cpt ++;
                }
                ret += "\"," + tr + ");" + CR;
                ret += traceFunctionCall(_block.getName(), _am.getName(), "my__attr");
            }  else {
                ret += traceFunctionCall(_block.getName(), _am.getName(), null);
            }
        }

        if (debug) {
            ret += "debugMsg(\"-> ....() Executing method " + _am.getName() + "\");" + CR;

            list = _am.getListOfAttributes();
            cpt = 0;
            for(AvatarAttribute aa: list) {
                ret += "debugInt(\"Attribute " + aa.getName() + " = \"," + aa.getName() + ");" + CR;
            }
        }

        listA = list;
        list = _am.getListOfReturnAttributes();
        if (list.size() != 0) {
            // Returns the first attribute. If not possible, return 0;
            // Implementation is provided by the user?
            // In that case, no need to generate the code!
            if (_am.isImplementationProvided()) {
                ret += "return _userImplemented_" + nameMethod + "(";
                cpt = 0;
                for(AvatarAttribute aaa: listA) {
                    if (cpt != 0) {
                        ret += ", ";
                    }
                    ret += aaa.getName();
                    cpt ++;
                }
                ret+= ");" + CR;

            } else {

                if (listA.size() >0) {
                    ret += "return " + listA.get(0).getName() + ";" + CR;
                } else {
                    ret += "return 0;" + CR;
                }
            }
        } else {
            if (_am.isImplementationProvided()) {
                ret += "_userImplemented_" + nameMethod + "(";
                cpt = 0;
                for(AvatarAttribute aaa: listA) {
                    if (cpt != 0) {
                        ret += ", ";
                    }
                    ret += aaa.getName();
                    cpt ++;
                }
                ret+= ");" + CR;

            }
        }
        ret += "}" + CR + CR;
        _taskFile.addToMainCode(ret + CR);

    }

    public void makeMainHeader() {
        mainFile.appendToBeforeMainCode(CR);
        for(TaskFileSoclib taskFile: taskFiles) {
            mainFile.appendToBeforeMainCode("#include \"" + taskFile.getName() + ".h\"" + CR);
        }
        mainFile.appendToBeforeMainCode(CR);
    }


    private String makeChannelAction(AvatarBlock _block,AvatarStateMachineElement asme) {

        String ret2 = "";
        int i;

        if (asme instanceof AvatarActionOnSignal) {
            AvatarActionOnSignal aaos = (AvatarActionOnSignal)asme;

            AvatarSignal as = aaos.getSignal();

            AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
	   
	    //if(ar.isAsynchronous()){
            ret2+= CR + "struct mwmr_s *" + getChannelName(ar, as);
	    //}
        }


        return ret2;
    }

  
    public static String[] enleveDoublons(String[] table) {

        String[] copy = new String[table.length];
        System.arraycopy(table, 0, copy, 0, table.length);
        table = copy;
        for (int i = 0; i < table.length; i++) {
            for (int j = i + 1; j < table.length; j++) {
                //Si table[j] n'est jamais null, simplifier la ligne suivante
                if (table[j] != null && table[j].equals(table[i])) {
                    table[j] = table[table.length - 1];
                    table[table.length - 1] = null;
                    String[] newTable = new String[table.length - 1];
                    System.arraycopy(table, 0, newTable, 0, table.length - 1);
                    table = newTable;
                    j--;
                }
            }
        }
        return table;
    }

/************************************************************************
                public int AccountAllSignal(AvatarBlock _block) {
                int cpt=0;
                        for (AvatarSignal as: _block.getSignals()) {
                                        cpt ++; }
                return cpt;

                        }
 ************************************************************************/

    public String makeCanalDec(AvatarBlock _block) {
        AvatarStateMachine asm = _block.getStateMachine();
        String ret="";
        int m=0;
        int i =0;
      
        String []canal= new String[100];
        
        String block_Name = _block.getName() ;

        for(AvatarStateMachineElement asme: asm.getListOfElements()) {

            canal[i]= makeChannelAction(_block, asme);  //"[" + i + "];";
            i ++;
        }


        String [] canalNonDouble= enleveDoublons(canal);

        //************************************************************************RG

        for(int k = 0; k<canalNonDouble.length ; k++) {

            if ((canalNonDouble[k] != null) && (canalNonDouble[k] !="")){
                String vari = "= channels_" + block_Name + "[" + m + "];";
                ret += (canalNonDouble[k] + vari);
                m++;}
        }

        return ret;
    }
    //************************************************************************RG

    public void makeMainFunction(AvatarBlock _block, TaskFileSoclib _taskFile) {
        int i; 
	String s;
        AvatarStateMachine asm = _block.getStateMachine();
	
	s = "void *mainFunc__" + _block.getName() + "(struct mwmr_s *channels_"+ _block.getName() +"[])";	 

        String sh = "extern " + s + ";" + CR;
        s+= "{" + CR;

        s+= makeCanalDec(       _block) + CR;
        s += makeAttributesDeclaration(_block, _taskFile);

        s+= CR + "int __currentState = STATE__START__STATE;" + CR;

        int nbOfMaxParams = _block.getMaxNbOfParams();
       
	for(i=0; i<_block.getMaxNbOfMultipleBranches(); i++) {
	    s+= UNUSED_ATTR + " request __req" + i + ";" + CR;
	    s+= UNUSED_ATTR + "int *__params" + i + "[" + nbOfMaxParams + "];" + CR;
	}
	s+= UNUSED_ATTR + "setOfRequests __list;" + CR;
		
	s+= UNUSED_ATTR + "pthread_cond_t __myCond;" + CR;
	s+= UNUSED_ATTR + "request *__returnRequest;" + CR;
		
	//s+= CR + "char * __myname = (char *)arg;" + CR;
        s+= CR + "char * __myname = \"" + _block.getName() + "\";"+ CR;//DG 7.7.
	// s+= CR + "char * __myname;"+ CR;
	
	s+= CR + "pthread_cond_init(&__myCond, NULL);" + CR;
		
	s+= CR + "fillListOfRequests(&__list, __myname, &__myCond, &__mainMutex);" + CR; 
		
	s+= "//printf(\"my name = %s\\n\", __myname);" + CR;
	       
        s+= CR + "/* Main loop on states */" + CR;
        s+= "while(__currentState != STATE__STOP__STATE) {" + CR;

        s += "switch(__currentState) {" + CR;
       
        s += "case STATE__START__STATE: " + CR;
        s += traceStateEntering("__myname", "__StartState");
        s += makeBehaviourFromElement(_block, asm.getStartState(), true);
        s += "break;" + CR + CR;

        String tmp;
        // Making other states


        for(AvatarStateMachineElement asme: asm.getListOfElements()) {
            if (asme instanceof AvatarState) {
                s += "case STATE__" + asme.getName() + ": " + CR;
                //s += traceStateEntering("__myname", asme.getName());

                if (includeUserCode) {
                    tmp = ((AvatarState)asme).getEntryCode();
                    if (tmp != null) {
                        if (tmp.trim().length() > 0) {
                            s += "/* Entry code */\n" + tmp + "\n/* End of entry code */\n\n";
                        }
                    }
                }

                s += makeBehaviourFromElement(_block, asme, true);
                s += "break;" + CR + CR;
            }
        }

        s += "}" + CR;

        s += "}" + CR;

        s+= "//printf(\"Exiting = %s\\n\", __myname);" + CR;
        s+= "return NULL;" + CR;
        s += "}" + CR;
        _taskFile.addToMainCode(s + CR);
        _taskFile.addToHeaderCode(sh + CR);
    }

    public String makeBehaviourFromElement(AvatarBlock _block, AvatarStateMachineElement _asme, boolean firstCall) {
        AvatarStateMachineElement asme0;


        if (_asme == null) {
            return "";
        }

        String ret = "";
        int i;

        if (_asme instanceof AvatarStartState) {
            return makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarTransition) {
            AvatarTransition at = (AvatarTransition)_asme;

            if (at.isGuarded()) {
               	String g = modifyGuard(at.getGuard().toString());
		
                ret += "if (!" + g + ") {" + CR;
                if (debug) {
                    ret += "debug2Msg(__myname, \"Guard failed: " + g + "\");" + CR;
                }
                ret += "__currentState = STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;
            }

            if (at.hasDelay()) {
                ret+= "waitFor(" + reworkDelay(at.getMinDelay()) + ", " + reworkDelay(at.getMaxDelay()) + ");" + CR;
            }

            String act;
            ret += makeActionsOfTransaction(_block, at);
            /*for(i=0; i<at.getNbOfAction(); i++) {
            // Must know whether this is an action or a method call
            act = at.getAction(i);
            if (at.isAMethodCall(act)) {
            ret +=  modifyMethodName(_block, act) + ";" + CR;
            } else {
            ret +=  act + ";" + CR;
            }
            }*/


            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarState) {
	   
	    int cpuid = FindCPUidFromTask(_block);
	    //   ret += "printf(\"tracing cycles --- block: "+_block.getName()+" cpu: %d cycle count: %d \\n\","+ cpuid+", cpu_cycle_count());"+CR;
            if (!firstCall) {
		if (debug) {
		    ret += "debug2Msg(__myname, \"-> (=====) Entering state + " + _asme.getName() + "\");" + CR;
		}
                return ret + "__currentState = STATE__" + _asme.getName() + ";" + CR;
            } else {
                if (_asme.nbOfNexts() == 0) {
                    return ret + "__currentState = STATE__STOP__STATE;" + CR;
                }

                if (_asme.nbOfNexts() == 1) {
                    return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
                }

                // Complex case of states -> several nexts
                // Put in list all

                // 1) Only immediatly executable transitions
                for(i=0; i<_asme.nbOfNexts(); i++) {
                    if (_asme.getNext(i) instanceof AvatarTransition) {
                        AvatarTransition at = (AvatarTransition)(_asme.getNext(i));

                        if (at.hasActions()) {
                            ret += makeImmediateAction(at, i);
                        } else {
                            if (at.getNext(0) instanceof AvatarActionOnSignal) {

                                ret += makeSignalAction(at, i);
                            } else {
                                // nothing special to do : immediate choice
                                ret += makeImmediateAction(at, i);
                            }
                        }
                    }
                }

                // Make all requests
                // Test if at least one request in the list!
                ret += "if (nbOfRequests(&__list) == 0) {" + CR;
                ret += "debug2Msg(__myname, \"No possible request\");" + CR;
                ret += "__currentState = STATE__STOP__STATE;" + CR;
                ret += "break;" + CR;
                ret += "}" + CR;

                ret += "__returnRequest = executeListOfRequests(&__list);" + CR;
                ret += "clearListOfRequests(&__list);" + CR ;
                ret += traceRequest();

                // Resulting requests
                for(i=0; i<_asme.nbOfNexts(); i++) {
                    if (i != 0) {
                        ret += "else ";
                    }
                    AvatarTransition at = (AvatarTransition)(_asme.getNext(i));
                    if (at.hasActions()) {
                        ret += " if (__returnRequest == &__req" + i + ") {" + CR;
                        ret += makeActionsOfTransaction(_block, at);
                        /*for(int j=0; j<at.getNbOfAction(); j++) {
                          if (at.isAMethodCall(at.getAction(j))) {
                          ret +=  modifyMethodName(_block, at.getAction(j)) + ";" + CR;
                          } else {
                          ret +=  at.getAction(j) + ";" + CR;

                          }

                          }*/
                        ret += makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                    }  else {
                        if (at.getNext(0) instanceof AvatarActionOnSignal) {
                            ret += " if (__returnRequest == &__req" + i + ") {" + CR + makeBehaviourFromElement(_block, at.getNext(0).getNext(0), false) + CR + "}";
                        } else {
                            // nothing special to do : immediate choice
                            ret += " if (__returnRequest == &__req" + i + ") {" + CR + makeBehaviourFromElement(_block, at.getNext(0), false) + CR + "}";
                        }
                    }
                    ret += CR;

                }
                return ret;
            }
        }

        if (_asme instanceof AvatarStopState) {
            return ret + "__currentState = STATE__STOP__STATE;" + CR;
        }

        if (_asme instanceof AvatarRandom) {
            AvatarRandom ar = (AvatarRandom)_asme;
            ret += ar.getVariable() + " = computeRandom(" + ar.getMinValue() + ", " + ar.getMaxValue() + ");" + CR;
            return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
        }

        if (_asme instanceof AvatarActionOnSignal) {
            AvatarActionOnSignal aaos = (AvatarActionOnSignal)_asme;
            ret += makeSignalAction(aaos, 0, false, "", "");
            AvatarSignal as = aaos.getSignal();
            AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);
            ret += executeOneRequest("__req0");
            ret += traceRequest();
        }

        // Default
        return ret + makeBehaviourFromElement(_block, _asme.getNext(0), false);
    }

    private String makeSignalAction(AvatarTransition _at, int _index) {
        String ret = "";
        AvatarActionOnSignal aaos;

        if (!(_at.getNext(0) instanceof AvatarActionOnSignal)) {
            return "";
        }

        aaos = (AvatarActionOnSignal)(_at.getNext(0));

        if (_at.isGuarded()) {
            String g = modifyGuard(_at.getGuard().toString());
	    
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) {
            ret += makeSignalAction(aaos, _index, true, _at.getMinDelay(), _at.getMaxDelay());
        } else {
            ret += makeSignalAction(aaos, _index, false, "", "");
        }
        ret += "addRequestToList(&__list, &__req" + _index + ");" + CR;

        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;
    }

    private String makeSignalAction(AvatarActionOnSignal _aaos, int _index, boolean hasDelay, String minDelay, String maxDelay) {
        String ret = "";
        int i;

        AvatarSignal as = _aaos.getSignal();
        AvatarRelation ar = avspec.getAvatarRelationWithSignal(as);

        String delay;

        if (hasDelay) {
            delay = "1, " + reworkDelay(minDelay) + ", " + reworkDelay(maxDelay);
        } else {
            delay = "0, 0, 0";
        }

        if (ar != null) {

	   
	    // Sending
	    if (_aaos.isSending()) {
                // Putting params
                for(i=0; i<_aaos.getNbOfValues() ;i++) {
		    ret += "__params" + _index + "[" + i + "] = &" +  _aaos.getValue(i) + ";" + CR;
                }
                if (ar.isAsynchronous()) {
		    //DG 3.12. inserted debug messages
		    ret += "debug2Msg(__myname, \"-> (=====)before MakeNewRequest\");" + CR;
		    ret+="debugInt(\"channel address\", &__req"+_index+");" + CR;
		    ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", SEND_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
		    ret += "debug2Msg(__myname, \"-> (=====)after MakeNewRequest\");" + CR;
		    ret += "__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;                
                } else {
		    if (ar.isBroadcast()) {
			ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID()+ ", SEND_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
			ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;

		    } else {
			ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID()+ ", SEND_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
			ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;

		    }
                }
		// Receiving
	    }
	    else {
		for(i=0; i<_aaos.getNbOfValues() ;i++) {
		    ret += "__params" + _index + "[" + i + "] = &" +  _aaos.getValue(i) + ";" + CR;
		}
		if (ar.isAsynchronous()) {
		    //ret += "debug2Msg(__myname, \"-> (=====)before MakeNewRequest\");" + CR;
//ret+="debugInt(\"channel address\", &__req"+_index+");" + CR;
		    ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_ASYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
		    ret += "__req" + _index + ".asyncChannel = &__" + getChannelName(ar, as) + ";" + CR;
		} else {
                    if (ar.isBroadcast()) {
                        ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_BROADCAST_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
			ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    } else {
			ret += "makeNewRequest(&__req" + _index + ", " + _aaos.getID() + ", RECEIVE_SYNC_REQUEST, " + delay + ", " + _aaos.getNbOfValues() + ", __params" + _index + ");" + CR;
			ret += "__req" + _index + ".syncChannel = &__" + getChannelName(ar, as) + ";" + CR;
                    }
		}
	    }
	}
		
	return ret;
    }


    private String makeImmediateAction(AvatarTransition _at, int _index) {
        String ret = "";
        if (_at.isGuarded()) {
           String g = modifyGuard(_at.getGuard().toString());
	   
            ret += "if (" + g + ") {" + CR;
        }

        if (_at.hasDelay()) { // if (( remainingTime>0 ))          
            ret += "makeNewRequest(&__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 1, " + reworkDelay(_at.getMinDelay()) + ", " + reworkDelay(_at.getMaxDelay()) + ", 0, __params" + _index + ");" + CR;
        } else {            
            ret += "makeNewRequest(&__req" + _index + ", " + _at.getID() + ", IMMEDIATE, 0, 0, 0, 0, __params" + _index + ");" + CR;
        }
        ret += "addRequestToList(&__list, &__req" + _index + ");" + CR;
        if (_at.isGuarded()) {
            ret += "}" + CR;
        }

        return ret;

    }
  
    private String executeOneRequest(String var) {
 String  ret = "debug2Msg(__myname, \"-> (=====)before executeOneRequest\");" + CR;
	ret += "__returnRequest = executeOneRequest(&__list, &" + var + ");" + CR; 
	ret += "debug2Msg(__myname, \"-> (=====)after executeOneRequest\");" + CR;
	ret += "clearListOfRequests(&__list);" + CR;
	return ret;
    }


    public String makeAttributesDeclaration(AvatarBlock _block, TaskFileSoclib _taskFile) {
	String ret = "";
	for(AvatarAttribute aa: _block.getAttributes()) {
	    ret += getCTypeOf(aa) + " " + aa.getName() + " = " + aa.getInitialValue() + ";" + CR;
	}
	return ret;
    }

    public void makeThreadsInMain(boolean _debug) {
	mainFile.appendToMainCode(CR + "/* Threads of tasks */" + CR);
	for(TaskFileSoclib taskFile: taskFiles) {
	    mainFile.appendToMainCode("pthread_t thread__" + taskFile.getName() + ";" + CR);
	}

	makeArgumentsInMain(_debug);
        if (_debug) {
            mainFile.appendToMainCode("/* Activating debug messages */" + CR);
            mainFile.appendToMainCode("activeDebug();" + CR);
        }

        mainFile.appendToMainCode("/* Activating randomness */" + CR);
        mainFile.appendToMainCode("initRandom();" + CR);

	mainFile.appendToMainCode("/* Initializing the main mutex */" + CR);
	mainFile.appendToMainCode("if (pthread_mutex_init(&__mainMutex, NULL) < 0) { exit(-1);}" + CR + CR);

	if (avspec.hasApplicationCode()) {
	    mainFile.appendToMainCode("/* User initialization */" + CR);
	    mainFile.appendToMainCode("__user_init();" + CR);
	}

	mainFile.appendToMainCode(CR + CR + mainDebugMsg("Starting tasks"));
     
 /* one relation specified in the AVATAR block diagram can contain 
    several signals which translate into several soclib channels
    for all channels they are transmitted in one array*/

	for(TaskFileSoclib taskFile: taskFiles) { 
	    
	    int cptchannels_array = 0;
	    String channelString= "";
           	int j=0;
	    for(AvatarRelation ar: avspec.getRelations()) {		
		int i;
		
	  if(ar.nbOfSignals()!=0){
	      for(i=0; i<ar.nbOfSignals() ; i++) {		  		
	
		  //	  if(((ar.block1.getName()==taskFile.getName())||(ar.block2.getName()==taskFile.getName()))&&(ar.isAsynchronous())){ //DG 2.5.

		  //DG 5.7.2016 is it correct to declare one channels array per couple f communicationg tasks? Attention doubly named channels!

		  if(((ar.block1.getName()==taskFile.getName())||(ar.block2.getName()==taskFile.getName()))){ 
		      channelString+="channels_array_"+  taskFile.getName() +"["+j+"]=&"+getChannelName(ar, i)+";" + CR;
		      cptchannels_array ++;
		      j++;		
		  }		    		   		    
	      }	  
}	  
	    }
	    //skip this line if there are no channels associated
	 	  //DG 5.7.2016 is it correct to declare one channels array per couple f communicationg tasks? Attention doubly named channels!
	        if(!(cptchannels_array==0)){
		mainFile.appendToMainCode("struct mwmr_s *channels_array_"+  taskFile.getName() +"["+cptchannels_array+"];"+CR);
	        mainFile.appendToMainCode(channelString+CR);
		}
		
		else{	
		    mainFile.appendToMainCode("struct mwmr_s *channels_array_"+  taskFile.getName()+";"+CR);
		}
	   
	    mainFile.appendToMainCode("ptr =malloc(sizeof(pthread_t));" + CR);		   
	    mainFile.appendToMainCode("thread__"+taskFile.getName() + "= (pthread_t)ptr;" + CR); 
	    mainFile.appendToMainCode("attr_t = malloc(sizeof(pthread_attr_t));" + CR);
	    //mainFile.appendToMainCode("attr_t->cpucount = "+ taskFile.getCPUId()  +";  " +CR);
	    mainFile.appendToMainCode("pthread_attr_affinity(attr_t, "+ taskFile.getCPUId()  +");  " +CR);
	    mainFile.appendToMainCode(CR + CR + mainDebugMsg("Starting tasks"));
	    //if(cptchannels_array==0){
	    //	mainFile.appendToMainCode("pthread_create(&thread__" +taskFile.getName()+", NULL, mainFunc__" + taskFile.getName()+", NULL);" + CR2);
	    // }else{	    
	    mainFile.appendToMainCode("pthread_create(&thread__" +taskFile.getName()+", attr_t, mainFunc__" + taskFile.getName() +", (void *)channels_array_"+taskFile.getName()+");" + CR2);
	    //}		    
	}
    
	mainFile.appendToMainCode(CR + CR + mainDebugMsg("Joining tasks"));
	for(TaskFileSoclib taskFile: taskFiles) {
            mainFile.appendToMainCode("pthread_join(thread__" + taskFile.getName() + ", NULL);" + CR);
	}

	mainFile.appendToMainCode(CR + CR + mainDebugMsg("Application terminated"));
	mainFile.appendToMainCode("return 0;" + CR);
    }

    public void makeArgumentsInMain(boolean _debug) {
        mainFile.appendToMainCode("/* Activating tracing  */" + CR);

        if (tracing) {
            mainFile.appendToMainCode("if (argc>1){" + CR);
            mainFile.appendToMainCode("activeTracingInFile(argv[1]);" + CR + "} else {" + CR);
            mainFile.appendToMainCode("activeTracingInConsole();" + CR + "}" + CR);
        }
    }

    public void makeMakefileSrc(String _path) {
	makefile_src = "SRCS = ";
	makefile_src += _path + "main.c ";
        for(TaskFileSoclib taskFile: taskFiles) {
            makefile_src += _path + taskFile.getName() + ".c ";
        }
    }

    public void makeMakefileSocLib() {
        makefile_SocLib = "objs = ";
        makefile_SocLib += "main.o ";
        for(TaskFileSoclib taskFile: taskFiles) {
            makefile_SocLib += taskFile.getName() + ".o ";
        }
    }

    public String getCTypeOf(AvatarAttribute _aa) {
        String ret = "int";
        if (_aa.getType() == AvatarType.BOOLEAN) {
            ret = "bool";
        }
        return ret;
    }

    public String getChannelName(AvatarRelation _ar, int _index) {
        return _ar.block1.getName() + "_" + _ar.getSignal1(_index).getName() + "__" + _ar.block2.getName() + "_" + _ar.getSignal2(_index).getName();
    }
    
    public String getSigName(AvatarRelation _ar, int _index) {
        return _ar.getSignal2(_index).getName();
    }

    public String getChannelName(AvatarRelation _ar, AvatarSignal _as) {
        int index = _ar.getIndexOfSignal(_as);
        return getChannelName(_ar, index);
    }
    
    public String getSigName(AvatarRelation _ar, AvatarSignal _as) {
        int index = _ar.getIndexOfSignal(_as);
        return getSigName(_ar, index);
    }

    public String modifyGuard(String _g) {
        String g = Conversion.replaceAllString(_g, "[", "(").trim();
        g = Conversion.replaceAllString(g, "]", ")").trim();
        g = Conversion.replaceOp(g, "and", "&&");
        g = Conversion.replaceOp(g, "or", "||");
        g = Conversion.replaceOp(g, "not", "!");

        return g;
    }

    public String reworkDelay(String _delay) {

        switch(timeUnit) {
        case USEC:
            return _delay;
        case MSEC:
            return "(" + _delay + ")*1000";
        case SEC:
            return "(" + _delay + ")*1000000";
        }

        return _delay;
    }

    private String modifyMethodName(AvatarBlock _ab, String _call) {
        int index;
        String ret0 = "";

        index = _call.indexOf("=");

        if (index > -1) {
            ret0 = _call.substring(0, index+1);
            _call = _call.substring(index+2, _call.length());
        }

        return ret0 + _ab.getName() + "__" + _call.trim();
    }

    private String traceRequest() {
        if (!tracing) {
            return "";
        }
        return "traceRequest(__myname, __returnRequest);" + CR;
    }

    private String traceVariableModification(String blockName, String varName, String type) {
        if (!tracing) {
            return "";
        }

        return "traceVariableModification(\"" + blockName + "\", \"" + varName + "\", " + varName + "," + type + ");" + CR;
    }

    private String traceFunctionCall(String blockName, String functionName, String params) {
        if (!tracing) {
            return "";
        }

        if (params == null) {
            params = "\"-\"";
        }
        return "traceFunctionCall(\"" + blockName + "\", \"" + functionName + "\", " + params + ");" + CR;
    }

    private String traceStateEntering(String name, String stateName) {
        if (!tracing) {
            return "";
        }
        return "traceStateEntering(" + name + ", \"" + stateName + "\");" + CR;
    }

    private String mainDebugMsg(String s) {
        if (!debug) {
            return "";
        }
        return "debugMsg(\"" + s + "\");" + CR;
    }

    private String taskDebugMsg(String s) {
        if (!debug) {
            return "";
        }

        return "debug2Msg(__myname, \"" + s + "\");" + CR;
    }

    public String makeActionsOfTransaction(AvatarBlock _block, AvatarTransition _at) {
        String ret = "";
        String act;
        String var;
        String type;
        for(int i=0; i<_at.getNbOfAction(); i++) {
            // Must know whether this is an action or a method call
	    AvatarAction actObj = _at.getAction(i);
            act = actObj.getName();
            if (actObj.isAMethodCall()) {
                ret +=  modifyMethodName(_block, act) + ";" + CR;
            } else {
                ret +=  act + ";" + CR;
                AvatarAttribute aa;
                aa = (AvatarAttribute) ((AvatarActionAssignment) actObj).getLeftHand();
                var = aa.getName ();
                if (aa != null) {
                    if (aa.isInt()) {
                        type = "0";
                    } else {
                        type = "1";
                    }
                    //ret += "sprintf(__value, \"%d\", " + var + ");" + CR;
                    ret += traceVariableModification(_block.getName(), var, type);
                }

            }
        }

        return ret;
    }

}
