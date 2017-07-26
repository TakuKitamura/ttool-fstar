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




package systemcamstranslator.toComponent;

import systemcamstranslator.*;
import systemcamstranslator.GenericTLMComponent;
import systemcamstranslator.SystemCamsSpecification;
import ddtranslatorSoclib.toTopCell.TopCellGenerator;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 *  Daniela GENIUS
 */
public class ToComponent {

    private final static int USEC = 0;
    private final static int MSEC = 1;
    private final static int SEC = 2;


    private final static String UNUSED_ATTR = "__attribute__((unused))";    
    private final static String GENERATED_PATH = "generated_component" + File.separator; 
 //   private final static String UNKNOWN = "UNKNOWN";
    private final static String CR = "\n";
    private final static String CR2 = "\n\n";

    public static SystemCamsSpecification spec ;
 
    private Vector<?> warnings;

    private MainFileSoclib mainFile;
    private Vector<ComponentFiles> ComponentFiles;
    private String makefile_src;
    private String makefile_SocLib;

    private int timeUnit;
    private boolean debug;
    private boolean tracing;
    private boolean includeUserCode = true;
 
    public ToComponent(SystemCamsSpecification spec) {
        spec = _spec;     
        componentFiles = new Vector<ComponentFiles>();
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

        for(ComponentFiles componentFile: componentFiles) {
	    FileUtils.saveFile(path + GENERATED_PATH + componentFile.getName() + ".h", Conversion.indentString(componentFile.getFullHeaderCode/*Soclib*/(), 2));
	    FileUtils.saveFile(path + GENERATED_PATH + componentFile.getName() + ".cpp", Conversion.indentString(taskFile.getMainCode(), 2));
        }

        // Standard Makefile
        makeMakefileSrc(GENERATED_PATH);
        FileUtils.saveFile(path + "Makefile.src", makefile_src);

        // Makefile for SocLib
        makeMakefileSocLib();
        FileUtils.saveFile(path + "Makefile.soclib", makefile_SocLib);
    }

    public Vector<?> getWarnings() {
        return warnings;
    }

    public void generateSystemCAms(boolean _debug, boolean _tracing) {
        debug = _debug;
        tracing = _tracing;

        mainFile = new  MainFileSoclib("main");

        if (avspec.hasApplicationCode() && includeUserCode) {
            //mainFile.appendToBeforeMainCode("/* User code */\n");
            mainFile.appendToBeforeMainCode(avspec.getApplicationCode());
            //mainFile.appendToBeforeMainCode("\n/* End of User code */\n\n");
        }
        
        makeComponents();
       
    }

 
      
    public int FindCPUidFromTask(SystemCamsBlock block){
    	List<SystemCamsTask> tasks = avddspec.getAllMappedTask();
	
    	for (SystemCamsTask task : tasks){
    		if (task.getTaskName().equals(block.getName())){
    			return task.getCPUNo();
    		}
    	}

    	return -1;
    }

    public void makeComponents() {
        componentFiles.add(componentFile);
        }


    public void defineAllSignal(ComponentFile _componentFile) {

        for () {
	   
            _componentFile.addToMainCode( "" + CR);
        }
        _componentFile.addToMainCode(CR);
    }
    
    public void defineAllMethods(SystemCamsBlock _block, TaskFileSoclib _taskFile) {
        Vector<String> allNames = new Vector<String>();
        for (SystemCamsMethod am: _block.getMethods()) {
            makeMethod(_block, am, allNames, _taskFile);
        }

        // Make method of father
        makeFatherMethod(_block, _block, allNames, _taskFile);
    }

    private void makeFatherMethod(SystemCamsBlock _originBlock, SystemCamsBlock _currentBlock, Vector<String> _allNames, componentFile _componentFile) {
        if (_currentBlock.getFather() == null) {
            return;
        }

        for (SystemCamsMethod am: _currentBlock.getFather().getMethods()) {
            makeMethod(_originBlock, am, _allNames, _taskFile);
        }

        makeFatherMethod(_originBlock, _currentBlock.getFather(), _allNames, _taskFile);
    }

    private void makeMethod(SystemCamsBlock _block, SystemCamsMethod _am, Vector<String> _allNames, TaskFileSoclib _taskFile) {
  
    }


    private String makeChannelAction(SystemCamsBlock _block, SystemCamsStateMachineElement asme) {

    }

  
    public String makeCanalDec(SystemCamsBlock _block) {
       
    }
    //************************************************************************RG

    public void makeMainFunction(SystemCamsBlock _block, TaskFileSoclib _taskFile) {
    }
   
}
