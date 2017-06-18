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




package myutil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

/**
   * Class Plugin
   * Creation: 24/05/2017
   * Version 1.0 24/05/2017
   * @author Ludovic APVRILLE
 */
public class Plugin {
    private String name;
    private File file;
    private HashMap<String, Class> listOfClasses;

    public Plugin(String _name) {
        name = _name;
        listOfClasses = new HashMap<String, Class>();
    }

    public String getName() {
        return name;
    }

    public Method getMethod(String _className, String _methodName) {
        Class<?> c = listOfClasses.get(_className);

        try {
            if (c == null) {
                file = new File(name);
                TraceManager.addDev("Loading plugin=" + name);
                URL[] urls = new URL[] { file.toURI().toURL() };
                ClassLoader loader = new URLClassLoader(urls);
                TraceManager.addDev("Loader created");
                c = loader.loadClass(_className);
                if (c == null) {
                    return null;
                }
                listOfClasses.put(_className, c);
            }

            return c.getMethod(_methodName);
        } catch (Exception e) {
	    TraceManager.addDev("Exception when using plugin " + name + " with className=" + _className + " and method " + _methodName);
	    return null;
        }

    }


}
