/* Generator of the top cell for simulation with SoCLib virtual component 
   library */

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import java.io.*;
import java.io.Writer.*;
import java.util.*;

import java.util.List;

public class TopCellGenerator
{
	private static final String MAPPING_TXT = "mapping.txt"; //$NON-NLS-1$	
	//--------------- accessing Avatardd -----------------
	public static AvatarddSpecification avatardd;
	// ---------------------------------------------------

	public String VCIparameters;
	public String config;
	public String mainFile;
	public String src;
	public String top;
        public String deployinfo;
        public String deployinfo_map; 
        public String procinfo; 
        public String nbproc;
	public final String DOTH = ".h";
	public final String DOTCPP = ".cpp";
	public final String SYSTEM_INCLUDE = "#include \"systemc.h\"";
	public final String CR = "\n";
	public final String CR2 = "\n\n";
	public final String SCCR = ";\n";
	public final String EFCR = "}\n";
	public final String EFCR2 = "}\n\n";
	public final String EF = "}";
	public final String COTE = "";
	public final String NAME_RST = "signal_resetn";
	public final String TYPEDEF = "typedef";

        private final static String GENERATED_PATH = "generated_topcell" + File.separator;      
	
	public TopCellGenerator(AvatarddSpecification dd){
		avatardd = dd;
	}

	public String generateTopCell() {
	    String icn;

	    /* first test validity of the hardware platform*/
            if(TopCellGenerator.avatardd.getNbCPU()==0){
		    System.out.println("***Warning: require at least one CPU***");
		}
	    if(TopCellGenerator.avatardd.getNbRAM()==0){
		    System.out.println("***Warning: require at least one RAM***");
		}
	    if(TopCellGenerator.avatardd.getNbTTY()==0){
		    System.out.println("***Warning: require at least one TTY***");
		}
	    /* if there is one VGMN, this is the central interconnect */
	    if(TopCellGenerator.avatardd.getNbVgmn()>1){
		 System.out.println("***Warning: No more than one central VGMN***");
	    }
            if(TopCellGenerator.avatardd.getNbVgmn()==1){
                System.out.println("***VGMN based***");
		icn="vgmn";
	    }
	    else{
		System.out.println("***VGSB based ***");
		icn="vgsb";
	    }
	    /* More complicate dto detect : central crossbar or local crossbars : ToDo */
	   
	    if(TopCellGenerator.avatardd.getNbCrossbar()>0){
		 System.out.println("***Clustered Interconnect***");
	    }
	    makeVCIparameters();
	    makeConfig();
	    String top = Header.getHeader() + 
		VCIparameters +
		config +				
		Code.getCode() +
		MappingTable.getMappingTable() +
		Loader.getLoader() +
		Declaration.getDeclarations() +	
		Signal.getSignal() +
		NetList.getNetlist(icn) +
		Simulation.getSimulation();
	    return (top);
	}	

	public List<String> readInMapping() {
		List<String> mappingLines = new ArrayList<String>();		
		try {
		    BufferedReader in = new BufferedReader(new FileReader(MAPPING_TXT));
		    String line = null;
			while ((line = in.readLine()) != null) {
			    System.out.println(" Line read : " + line);
			    mappingLines.add(line);// read one line of the file;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return mappingLines;
	}

    public void saveFile(String path) {
		try {
          System.err.println(path + GENERATED_PATH + "top.cc");
			FileWriter fw = new FileWriter(path + GENERATED_PATH + "/top.cc");
			top = generateTopCell();
			fw.write(top);
			fw.close();
		} catch (IOException ex) {
		}
		saveFileDeploy(path);
		saveFileProcinfo(path);
		saveFileNBproc(path);
	}

    public void saveFileDeploy(String path) {

		try {
          System.err.println(path + GENERATED_PATH + "deployinfo.h");
			FileWriter fw = new FileWriter(path + GENERATED_PATH + "/deployinfo.h");			
			deployinfo = Deployinfo.getDeployInfo();
			fw.write(deployinfo);
			fw.close();

 System.err.println(path + GENERATED_PATH + "deployinfo_map.h");
			FileWriter fw_map = new FileWriter(path + GENERATED_PATH + "/deployinfo_map.h");
			deployinfo_map = Deployinfo.getDeployInfoMap();
			fw_map.write(deployinfo_map);
			fw_map.close();

		} catch (IOException ex) {
		}
	}

    public void saveFileProcinfo(String path) {

		try {
          System.err.println(path + GENERATED_PATH + "procinfo.mk");
			FileWriter fw = new FileWriter(path + GENERATED_PATH + "/procinfo.mk");
			procinfo = Deployinfo.getProcInfo();
			fw.write(procinfo);
			fw.close();
		} catch (IOException ex) {
		}
	}

    public void saveFileNBproc(String path) {

		try {
          System.err.println(path + GENERATED_PATH + "nbproc");
			FileWriter fw = new FileWriter(path + GENERATED_PATH + "/nbproc");
			nbproc = Deployinfo.getNbProc();
			fw.write(nbproc);
			fw.close();
		} catch (IOException ex) {
		}
	}

	public void makeVCIparameters() {
		VCIparameters = CR2 + "typedef caba::VciParams<4,9,32,1,1,1,8,1,1,1> vci_param;";
		VCIparameters = VCIparameters + "// Define our VCI parameters" + CR2 + "struct CpuEntry;" + CR2;
	}

	public void makeConfig() {
		config = CR2 + "#if defined(CONFIG_GDB_SERVER)" + CR;
		config = config + "#  if defined(CONFIG_SOCLIB_MEMCHECK)" + CR;
		config = config + "#    warning Using GDB and memchecker" + CR;
		config = config + "#    define ISS_NEST(T) common::GdbServer<common::IssMemchecker<T> >" + CR;
		config = config + "#  else" + CR;
		config = config + "#    warning Using GDB" + CR;
		config = config + "#    define ISS_NEST(T) common::GdbServer<T>" + CR;
		config = config + "#  endif" + CR;
		config = config + "#elif defined(CONFIG_SOCLIB_MEMCHECK)" + CR;
		config = config + "#  warning Using Memchecker" + CR;
		config = config + "#  define ISS_NEST(T) common::GdbServer<common::IssMemchecker<T> " + CR;
		config = config + "#else" + CR;
		config = config + "#  warning Using raw processor" + CR;
		config = config + "#  define ISS_NEST(T) T" + CR;
		config = config + "#endif" + CR;
	}
}
