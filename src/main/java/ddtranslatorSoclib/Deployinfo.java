 
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
    
/* This class generates the deployment info for the ldscript*/ 
    
/* authors: v1.0 Daniela GENIUS 2015 modified for clustered architecture 08/2016 */ 
    
/* 

/* here is an example of an ldscript :
#define CHANNEL_0_NAME channel0
#define CHANNEL_1_NAME channel1

#define DEPLOY_RAM_0_NAME channel_ram0
#define DEPLOY_RAM_0_ADDR 0x6f000000
#define DEPLOY_RAM_0_SIZE 0x01000000

#define DEPLOY_RAM_1_NAME channel_ram1
#define DEPLOY_RAM_1_ADDR 0x5f000000
#define DEPLOY_RAM_1_SIZE 0x01000000

#define MAP_A\
        .channel0 : { \
               __channel_0_start = ABSOLUTE(.);\
              *(section_channel0)\
                } > DEPLOY_RAM_0_NAME\            
        .channel1 : { \
               __channel_1_start = ABSOLUTE(.);\
              *(section_channel1) \
                } > DEPLOY_RAM_1_NAME 
 */ 
    package ddtranslatorSoclib.toTopCell;
import ddtranslatorSoclib.*;
import ddtranslatorSoclib.toSoclib.*;
import avatartranslator.*;
import myutil.Conversion;
import myutil.FileException;
import myutil.FileUtils;
import myutil.TraceManager;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
public class Deployinfo
{
    private final static String CR = "\n";
    private final static String CR2 = "\n\n";
    public static AvatarSpecification avspec;
    public static AvatarddSpecification avddspec;
    private Vector < ? >warnings;
    private MainFileSoclib mainFile;
    private Vector < TaskFileSoclib > taskFiles;
    private String makefile_src;
    private String makefile_SocLib;
    
	/* for the moment, this is specific to PowerPC */ 
	public Deployinfo (AvatarddSpecification _avddspec,
			    AvatarSpecification _avspec)
    {
	avspec = _avspec;
	avddspec = _avddspec;
	taskFiles = new Vector < TaskFileSoclib > ();
    } public static String getDeployInfo ()
    {
	int nb_clusters =
	    TopCellGenerator.avatardd.getAllCrossbar ().size ();
	String deployinfo = CR;
	
	    /*  dimension segments according to the number of clusters */ 
	    
	    /*determine the "step" between segments dedicated to a cluster */ 
	int CLUSTER_SIZE;
	
	    /*if the user does not specify the size, take default value */ 
	    if (nb_clusters < 16)
	  {
	      CLUSTER_SIZE = 268435456;
	  }
	
	else
	  {
	      CLUSTER_SIZE = 134217728;
	  }			// to be refined -> dynamically adapt
	int size;
	
	    /* there can be many RAMS, but then must be smaller dimensioned */ 
	int i = 0;
      for (AvatarRAM ram:TopCellGenerator.avatardd.getAllRAM ())
	  {
	      
		  /* data memory always starts at 0x10000000 */ 
	      int address_start = 268435456;
	      String string_adress_start =
		  Integer.toHexString (i * 268435456);
	      
		  /* segment size is either given by the user or a default value is calculated */ 
		  if (ram.getDataSize () == 0)
		{
		    if ((nb_clusters < 16)
			  || (TopCellGenerator.avatardd.getAllRAM ().size () <
			      16))
		      {
			  size = 268435456;
		      }
		    
		    else
		      {
			  
			      //smaller segments
			      size = 134217728;
		      }
		}
	      
	      else
		{
		    size = ram.getDataSize ();
		}
	      ram.setDataSize (size);
	      size = ram.getDataSize ();
	      TraceManager.addDev ("***hardware RAM size" + size);
	      String string_size_half = (Integer.toHexString (size / 2));	//segments  are half uram, half cram
	      deployinfo +=
		  "#define CACHED_RAM" + ram.getIndex () + "_NAME cram" +
		  ram.getIndex () + CR;
	      deployinfo =
		  deployinfo + "#define CACHED_RAM" + ram.getIndex () +
		  "_ADDR 0x" + Integer.toHexString (address_start +
						    i * CLUSTER_SIZE) + CR;
	      deployinfo =
		  deployinfo + "#define CACHED_RAM" + ram.getIndex () +
		  "_SIZE 0x" + string_size_half + CR;
	      deployinfo +=
		  "#define DEPLOY_RAM" + ram.getIndex () + "_NAME uram" +
		  ram.getIndex () + CR;
	      int cacheability_bit = 2097152;	//0x00200000 
	      deployinfo =
		  deployinfo + "#define DEPLOY_RAM" + ram.getIndex () +
		  "_ADDR 0x" + Integer.toHexString (address_start +
						    i * CLUSTER_SIZE +
						    size / 2 +
						    cacheability_bit) + CR;
	      
		  // 31.08. simplifie
		  deployinfo =
		  deployinfo + "#define DEPLOY_RAM" + ram.getIndex () +
		  "_SIZE 0x" + (string_size_half) + CR;
	      i++;
	  } 
	    //Calculate Adresses of MWMR segments, one for each hardware accellerator
	    i = 0;
      for (AvatarCoproMWMR copro:TopCellGenerator.avatardd.
	       getAllCoproMWMR ())
	  {
	      deployinfo =
		  deployinfo + "#define MWMR_RAM" + i + "_NAME mwmr_ram" + i +
		  CR;
	      deployinfo =
		  deployinfo + "#define MWMR_RAM" + i + "_ADDR 0xA02" +
		  Integer.toHexString (i * 4096) + CR;
	      deployinfo =
		  deployinfo + "#define MWMR_RAM" + i + "_SIZE 0x1000" + CR;
	      i++;
	  }
	return deployinfo;
    }
    public static String getDeployInfoMap (AvatarSpecification _avspec)
    {
	avspec = _avspec;
	int i = 0;
	String deployinfo_map = CR;
	int j;
	deployinfo_map += "#define MAP_A\\" + CR;
	try
	{
	  for (AvatarRAM ram:TopCellGenerator.avatardd.
		  getAllRAM ())
	      {
		  if (!(ram.getChannels ().isEmpty ()))
		    {
		      for (AvatarRelation ar:avspec.
			      getRelations
			      ())
			  {
			      for (j = 0; j < ar.nbOfSignals (); j++)
				{
				    deployinfo_map =
					deployinfo_map + "\n .channel" + i +
					" : {";
				    deployinfo_map =
					deployinfo_map + "*(section_channel" +
					i + ")";
				    deployinfo_map = deployinfo_map + "} > uram" + ram.getIndex () + CR;	//ram n° was incorrect (see above) 
				    i++;
				}
			  }
			i = 0;
		      for (AvatarRelation ar:avspec.
			      getRelations
			      ())
			  {
			      for (j = 0; j < ar.nbOfSignals (); j++)
				{
				    deployinfo_map =
					deployinfo_map + "\n .lock" + i +
					" : { ";
				    deployinfo_map =
					deployinfo_map + "*(section_lock" +
					i + ")";
				    deployinfo_map =
					deployinfo_map + "} > uram" +
					ram.getIndex () + CR;
				    i++;
				}
			  }
		    }
	      }
	}
	catch (Exception e)
	{
	    e.printStackTrace ();
	}
	return deployinfo_map;
    }
    public static String getDeployInfoRam (AvatarSpecification _avspec)
    {
	avspec = _avspec;
	int i = 0;
	int j;
	String deployinfo_ram = CR;
	try
	{
	  for (AvatarRelation ar:avspec.getRelations ())
	      {
		  for (j = 0; j < ar.nbOfSignals (); j++)
		    {
			deployinfo_ram +=
			    "#if defined(DEPLOY_RAM" + i + "_NAME)" + CR;
			deployinfo_ram +=
			    "\tDEPLOY_RAM" + i +
			    "_NAME (RWAL) : ORIGIN = DEPLOY_RAM" + i +
			    "_ADDR, LENGTH = DEPLOY_RAM" + i + "_SIZE" + CR;
			deployinfo_ram += "#endif" + CR;
			deployinfo_ram +=
			    "#if defined(CACHED_RAM" + i + "_NAME)" + CR;
			deployinfo_ram +=
			    "\tCACHED_RAM" + i +
			    "_NAME (RWAL) : ORIGIN = CACHED_RAM" + i +
			    "_ADDR, LENGTH = CACHED_RAM" + i + "_SIZE" + CR;
			deployinfo_ram += "#endif" + CR;
			i++;
		    }
	      }
	}
	catch (Exception e)
	{
	    e.printStackTrace ();
	}
	return deployinfo_ram;
    }
    public static String getProcInfo ()
    {
	int i = 0;
	String procinfo = "SOCLIB_CPU_COUNT = ";
      for (AvatarCPU cpu:TopCellGenerator.avatardd.getAllCPU ())
	  {
	      i++;
	  }
	procinfo += i + CR;
	return procinfo;
    }
    public static String getNbProc ()
    {
	int i = 0;
	String nbproc = "CONFIG_CPU_MAXCOUNT ";
      for (AvatarCPU cpu:TopCellGenerator.avatardd.getAllCPU ())
	  {
	      i++;
	  }
	nbproc += i + CR;
	return nbproc;
    }
}


