
/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
  Daniela Genius, Lip6, UMR 7606 

  ludovic.apvrille AT enst.fr
  daniela.genius@lip6.fr

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
*/

/* Generator of the top cell for simulation with SoCLib virtual component 
   library */

/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;

import avatartranslator.AvatarRelation;
import avatartranslator.AvatarSpecification;
import ddtranslatorSoclib.*;
import ddtranslatorSoclib.toSoclib.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import myutil.TraceManager;

public class TopCellGenerator
{
    private static final String MAPPING_TXT = "mapping.txt";	//$NON-NLS-1$  
    //--------------- accessing Avatardd -----------------
    public static AvatarddSpecification avatardd;
    // ---------------------------------------------------

    public static AvatarSpecification avspec;

    public String VCIparameters;
    public String config;
    public String mainFile;
    public String src;
    public String top;
    public String deployinfo;
    public String deployinfo_map;
    public String deployinfo_ram;
    public String platform_desc;
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

    private final static String GENERATED_PATH =
	"generated_topcell" + File.separator;
    private boolean tracing;
    public TopCellGenerator (AvatarddSpecification dd, boolean _tracing,
			     AvatarSpecification _avspec)
    {
	avatardd = dd;
	tracing = _tracing;
	avspec = _avspec;
    }

    public String generateTopCell ()
    {
	String icn;

	/* first test validity of the hardware platform */
	if (TopCellGenerator.avatardd.getNbCPU () == 0)
	  {
	      TraceManager.addDev ("***Warning: require at least one CPU***");
	  }
	if (TopCellGenerator.avatardd.getNbRAM () == 0)
	  {
	      TraceManager.addDev ("***Warning: require at least one RAM***");
	  }
	if (TopCellGenerator.avatardd.getNbTTY () == 0)
	  {
	      TraceManager.addDev ("***Warning: require at least one TTY***");
	  }
	/* if there is one VGMN, this is the central interconnect */
	if (TopCellGenerator.avatardd.getNbVgmn () > 1)
	  {
	      TraceManager.
		  addDev ("***Warning: No more than one central VGMN***");
	  }
	if (TopCellGenerator.avatardd.getNbVgmn () == 1)
	  {
	      TraceManager.addDev ("***VGMN based***");
	      icn = "vgmn";
	  }
	else
	  {
	      TraceManager.addDev ("***VGSB based ***");
	      icn = "vgsb";
	  }

	// If there is a spy, add spy component to vci interface;
	// both adjacent componants are spied.
	// Currently for CPU and RAM only.
	// RAM monitoring is required for determining the buffer size and
	// various infos on MWMR channels 
	// RAM and CPU  monitoring are for  required for determining latency
	// of memory accesses other than channel    

      for (AvatarConnector connector:avatardd.getConnectors ())
	  {

	      AvatarConnectingPoint my_p1 = connector.get_p1 ();
	      AvatarConnectingPoint my_p2 = connector.get_p2 ();

	      //If a spy glass symbol is found, and component itself not yet marked 

	      AvatarComponent comp1 = my_p1.getComponent ();
	      AvatarComponent comp2 = my_p2.getComponent ();

	      if (connector.getMonitored () == 1)
		{
		    //comp2 devrait toujours etre un interconnect
		    if (comp1 instanceof AvatarRAM)
		      {
			  AvatarRAM comp1ram = (AvatarRAM) comp1;
			  TraceManager.addDev ("RAM  monitored " +
					       comp1ram.getMonitored ());

		      }

		    if (comp1 instanceof AvatarCPU)
		      {
			  AvatarCPU comp1cpu = (AvatarCPU) comp1;
			  TraceManager.addDev ("CPU monitored " +
					       comp1cpu.getMonitored ());

		      }

		    /*  if (comp2 instanceof AvatarRAM){ 
		       AvatarRAM comp2ram = (AvatarRAM)comp1;
		       TraceManager.addDev("RAM2 topcell monitored "+comp2ram.getMonitored());
		       comp2ram.setMonitored(comp2ram.getMonitored());
		       }

		       if (comp2 instanceof AvatarCPU){ 
		       AvatarCPU comp2cpu = (AvatarCPU)comp2;
		       TraceManager.addDev("CPU2 topcell monitored "+comp2cpu.getMonitored());
		       comp2cpu.setMonitored(comp2cpu.getMonitored());
		       } */
		}
	  }

	/* Central interconnect or local crossbars */

	if (TopCellGenerator.avatardd.getNbCrossbar () > 0)
	  {
	      TraceManager.addDev ("***Clustered Interconnect***");
	  }
	makeVCIparameters ();
	makeConfig ();
	String top = Header.getHeader () +
	    VCIparameters +
	    config +
	    Code.getCode () +
	    MappingTable.getMappingTable () +
	    Loader.getLoader (avspec) +
	    Declaration.getDeclarations (avspec) +
	    Signal.getSignal () +
	    NetList.getNetlist (icn, tracing) + Simulation.getSimulation ();
	return (top);
    }

    public List < String > readInMapping ()
    {
	List < String > mappingLines = new ArrayList < String > ();
	try
	{
	    BufferedReader in =
		new BufferedReader (new FileReader (MAPPING_TXT));
	    String line = null;
	    while ((line = in.readLine ()) != null)
	      {
		  TraceManager.addDev (" Line read : " + line);
		  mappingLines.add (line);
	      }
	    in.close ();
	}
	catch (IOException e)
	{
	    e.printStackTrace ();
	}

	return mappingLines;
    }

    public void saveFile (String path)
    {
	try
	{

	    FileWriter fw =
		new FileWriter (path + GENERATED_PATH + "/top.cc");
	    top = generateTopCell ();
	    fw.write (top);
	    fw.close ();
	} catch (IOException ex)
	{
	}
	saveFileDeploy (path);
	saveFilePlatform (path);
	saveFileProcinfo (path);
	saveFileNBproc (path);
    }

    public void saveFileDeploy (String path)
    {

	try
	{

	    FileWriter fw =
		new FileWriter (path + GENERATED_PATH + "/deployinfo.h");
	    deployinfo = Deployinfo.getDeployInfo ();
	    fw.write (deployinfo);
	    fw.close ();


	    FileWriter fw_map =
		new FileWriter (path + GENERATED_PATH + "/deployinfo_map.h");
	    deployinfo_map = Deployinfo.getDeployInfoMap (avspec);
	    fw_map.write (deployinfo_map);
	    fw_map.close ();

	    FileWriter fw_ram =
		new FileWriter (path + GENERATED_PATH + "/deployinfo_ram.h");
	    deployinfo_ram = Deployinfo.getDeployInfoRam (avspec);
	    fw_ram.write (deployinfo_ram);
	    fw_ram.close ();
	} catch (Exception ex)
	{
	    ex.printStackTrace ();
	}
    }

    public void saveFileProcinfo (String path)
    {

	try
	{

	    FileWriter fw =
		new FileWriter (path + GENERATED_PATH + "/procinfo.mk");
	    procinfo = Deployinfo.getProcInfo ();
	    fw.write (procinfo);
	    fw.close ();
	} catch (IOException ex)
	{
	}
    }

    public void saveFileNBproc (String path)
    {

	try
	{

	    FileWriter fw =
		new FileWriter (path + GENERATED_PATH + "/nbproc");
	    nbproc = Deployinfo.getNbProc ();
	    fw.write (nbproc);
	    fw.close ();
	} catch (IOException ex)
	{
	}
    }

    public void saveFilePlatform (String path)
    {

	try
	{

	    FileWriter fw =
		new FileWriter (path + GENERATED_PATH + "/platform_desc");
	    platform_desc = Platforminfo.getPlatformInfo ();
	    fw.write (platform_desc);
	    fw.close ();
	} catch (IOException ex)
	{
	}
    }

    public void makeVCIparameters ()
    {
	VCIparameters =
	    CR2 + "typedef caba::VciParams<4,9,32,1,1,1,8,1,1,1> vci_param;";
	VCIparameters =
	    VCIparameters + "// Define our VCI parameters" + CR2 +
	    "struct CpuEntry;" + CR2;
    }

    public void makeConfig ()
    {
	config = CR2 + "#if defined(CONFIG_GDB_SERVER)" + CR;
	config = config + "#  if defined(CONFIG_SOCLIB_MEMCHECK)" + CR;
	config = config + "#    warning Using GDB and memchecker" + CR;
	config =
	    config +
	    "#    define ISS_NEST(T) common::GdbServer<common::IssMemchecker<T> >"
	    + CR;
	config = config + "#  else" + CR;
	config = config + "#    warning Using GDB" + CR;
	config = config + "#    define ISS_NEST(T) common::GdbServer<T>" + CR;
	config = config + "#  endif" + CR;
	config = config + "#elif defined(CONFIG_SOCLIB_MEMCHECK)" + CR;
	config = config + "#  warning Using Memchecker" + CR;
	config =
	    config +
	    "#  define ISS_NEST(T) common::GdbServer<common::IssMemchecker<T> "
	    + CR;
	config = config + "#else" + CR;
	config = config + "#  warning Using raw processor" + CR;
	config = config + "#  define ISS_NEST(T) T" + CR;
	config = config + "#endif" + CR;
    }
}
