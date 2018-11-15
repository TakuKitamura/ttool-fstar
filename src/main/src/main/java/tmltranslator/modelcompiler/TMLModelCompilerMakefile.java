/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT nokia.com
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

package tmltranslator.modelcompiler;

/**
    * This class is used to generate the Makefile of a control code generation project
   * Class TMLModelCompilerMakefile
   * Creation: 09/02/2014
   * @version 1.0 09/02/2014
   * @author Andrea ENRICI
 */
public class TMLModelCompilerMakefile implements CCodeGenConstants {

//	private String CR = "\n";
//	private String CR2 = "\n\n";
	private StringBuffer code = new StringBuffer();
	
	public TMLModelCompilerMakefile( String ApplicationName )	{
		
		code.append(
		"#In order to compile wpd, please define EMBB_INSTALL, either as an environment" + CR +
		"# variable or as a Makefile variable by uncommenting and editing the following" + CR +
		"# line:" + CR +
		" EMBB_INSTALL	= /home/libembb2" + CR +
		"# where <someplace> is the full path of the directory in which you installed" + CR +
		"# libembb. $(EMBB_INSTALL)/include shall contain embb/fep.h and" + CR +
		"# $(EMBB_INSTALL)/lib shall contain libembb.so and libembbemu.so" + CR +
		"CXX		= g++" + CR +
		"LD		= g++" + CR +
		"CXXFLAGS	= -c -g -Wall -Wno-unused-variable" + CR +
		"ifndef EMBB_INSTALL" + CR +
		"all:" + CR +
		"\t@echo \"**************************************************************************************\"; \\" + CR +
		"\techo \"* Please define the EMBB_INSTALL environment variable and assign it the absolute path\"; \\" + CR +
		"\techo \"* of the libembb install directory. $(EMBB_INSTALL)/include shall contain embb/fep.h\"; \\" + CR +
		"\techo \"* and $(EMBB_INSTALL)/lib shall contain libembb.so and libembbemu.so\"; \\" + CR +
		"\techo \"**************************************************************************************\"; \\" + CR +
		"\texit 1" + CR + 
		"else" + CR2 + 
		"EMBBINCLUDEDIR	= $(EMBB_INSTALL)/include" + CR +
		"EMBBLIBDIR	= $(EMBB_INSTALL)/lib" + CR +
		"OBJS	= $(patsubst %.c,%.o,$(wildcard *.c))" + CR +
		"HRDS	= $(wildcard *.h)" + CR +
		"EXECS	= waveform.x" + CR2 +
		"print-%:" + CR +
		"\t@echo '$(OBJS)'" + CR2 +
		"all: $(EXECS)" + CR2 +
		"%.o: %.c" + CR2 +
		"\t$(CXX) $(CXXFLAGS) $(INCLUDES) -c $<" + CR +
		"$(OBJS): INCLUDES += -I$(EMBBINCLUDEDIR)" + CR2 +
		"$(EXECS): LDFLAGS += -L$(EMBBLIBDIR) -Wl,-rpath,$(EMBBLIBDIR)" + CR +
		"$(EXECS): LIBS += -lembb -lembbemu" + CR +
		"$(EXECS): $(OBJS)" + CR +
		"\t$(LD) $(LDFLAGS) -o $@ $^ $(LIBS)" + CR2 +
		"clean:" + CR +
		"\trm -rf $(OBJS)" + CR2 +
		"ultraclean:" + CR +
		"\trm -rf $(OBJS) $(EXECS)" + CR +
		"endif" );
	}
	
	public String getCode()	{
		return code.toString();
	}
} //End of class
