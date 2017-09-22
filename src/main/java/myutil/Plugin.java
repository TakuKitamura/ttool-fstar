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

import javax.swing.*;

/**
   * Class Plugin
   * Creation: 24/05/2017
   * Version 1.0 24/05/2017
   * @author Ludovic APVRILLE
 */
public class Plugin {
    private String path;
    private String name;
    private File file;
    private HashMap<String, Class> listOfClasses;
    private Class classAvatarCodeGenerator;
    private Class classDiplodocusCodeGenerator;
    private Class classGraphicalComponent;
    

    public Plugin(String _path, String _name) {
	path = _path;
        name = _name;
        listOfClasses = new HashMap<String, Class>();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
	return path;
    }

    public boolean hasAvatarCodeGenerator() {
	String ret = executeRetStringMethod(removeJar(name), "hasAvatarCodeGenerator");
	if (ret != null) {
	    classAvatarCodeGenerator = getClass(ret);
	    return true;
	}

	return false;
    }
    
    public boolean hasDiplodocusCodeGenerator() {
	String ret = executeRetStringMethod(removeJar(name), "hasDiplodocusCodeGenerator");
	if (ret != null) {
	    classDiplodocusCodeGenerator = getClass(ret);
	    return true;
	}

	return false;
    }

    public String getDiplodocusCodeGeneratorIdentifier() {
	String desc = executeRetStringMethod(classDiplodocusCodeGenerator, "getIdentifier");
	return desc;
    }
    

    public boolean hasGraphicalComponent() {
	String ret = executeRetStringMethod(removeJar(name), "hasGraphicalComponent");
	if (ret != null) {
	    classGraphicalComponent = getClass(ret);
	    String diagOk = executeRetStringMethod(classGraphicalComponent, "getPanelClassName");
	    if (diagOk != null) {
		return true;
	    }
	}
	classGraphicalComponent = null;
	return false;	
    }

    public boolean hasGraphicalComponent(String _diagID) {
	//TraceManager.addDev("Test GC with diag=" + _diagID);
	String ret = executeRetStringMethod(removeJar(name), "hasGraphicalComponent");
	if (ret != null) {
	    classGraphicalComponent = getClass(ret);
	    String diagOk = executeRetStringMethod(classGraphicalComponent, "getPanelClassName");
	    if (diagOk != null) {
		if (diagOk.compareTo(_diagID) == 0) {
		    //TraceManager.addDev("Found graphical component in plugin:" + name);
		    return true;
		}
	    }
	}
	classGraphicalComponent = null;

	return false;
    }

    public Class getClassAvatarCodeGenerator() {
	return classAvatarCodeGenerator;
    }
    
    public Class getClassGraphicalComponent() {
	return classGraphicalComponent;
    }

    public Class getClassDiplodocusCodeGenerator() {
	return classDiplodocusCodeGenerator;
    }
    

    public Class getClass(String _className) {
	Class<?> c = listOfClasses.get(_className);
	if (c != null) {
	    return c;
	}

	try {
            if (c == null) {
                file = new File( path + java.io.File.separator + name);
                TraceManager.addDev("Loading plugin=" + path + java.io.File.separator + name);
                URL[] urls = new URL[] { file.toURI().toURL() };
                ClassLoader loader = new URLClassLoader(urls);
                //TraceManager.addDev("getClass() Loader created");
                c = loader.loadClass( PluginManager.PLUGIN_PKG + "." + _className);
                //TraceManager.addDev("getClass() class loaded");
                if (c == null) {
                    return null;
                }
                listOfClasses.put(_className, c);
		return c;
            }

        } catch (Exception e) {
            TraceManager.addDev( "getClass()\n" );
            e.printStackTrace( System.out );
    	    //TraceManager.addDev("Exception when using plugin " + name + " with className=" + _className);
	    return null;
        }

	return null;
    }

    public Method getMethod(String _className, String _methodName) {
        Class<?> c = listOfClasses.get(_className);

        try {
            if (c == null) {
                file = new File(path + java.io.File.separator + name);
                //TraceManager.addDev("Loading plugin=" + path + java.io.File.separator + name);
                URL[] urls = new URL[] { file.toURI().toURL() };
                ClassLoader loader = new URLClassLoader(urls);
                //TraceManager.addDev("Loader created");
                c = loader.loadClass( PluginManager.PLUGIN_PKG + "." + _className);
                //TraceManager.addDev( "Class loaded" );
                if (c == null) {
                    return null;
                }
                listOfClasses.put(_className, c);
            }

            return c.getMethod(_methodName);
        } catch (Exception e) {
            e.printStackTrace( System.out );
	        //TraceManager.addDev("Exception when using plugin " + name + " with className=" + _className + " and method " + _methodName);
	    return null;
        }

    }

    public String executeRetStringMethod(String _className, String _methodName) {
	// We have a valid plugin. We now need to get the Method
	Method m = getMethod(_className, _methodName);
	if (m == null) {
	    //TraceManager.addDev("Null method with class as a string class=" + _className + " _method=" + _methodName);
	    return null;
	}
	
	try {
	    return (String)(m.invoke(null));
	} catch (Exception e) {
	    TraceManager.addDev("Exception occured when executing method " + _methodName + " in class=" + _className);
	    return null;
	}
    }

    public String executeRetStringMethod(Class<?> c, String _methodName) {
	// We have a valid plugin. We now need to get the Method
		
	try {
	    //TraceManager.addDev("Getting " + _methodName + " in class " + c.getName());
	    Method m = c.getMethod(_methodName);
	    
	    if (m == null) {
		//TraceManager.addDev("Null method in executeRetStringMethod with Class parameter");
		return null;
	    }
	    return (String)(m.invoke(null));
	} catch (Exception e) {
	    TraceManager.addDev("Exception occured when executing method " + _methodName);
	    return null;
	}
    }

    public static int executeIntMethod(Object instance, String _methodName) throws Exception {
	Class[] cArg = new Class[0];
	Method method = instance.getClass().getMethod(_methodName, cArg);
	return (int)(method.invoke(instance));
    }

    public static boolean executeBoolMethod(Object instance, String _methodName) throws Exception {
	Class[] cArg = new Class[0];
	Method method = instance.getClass().getMethod(_methodName, cArg);
	return (boolean)(method.invoke(instance));
    }
    
    public static boolean executeBoolStringMethod(Object instance, String value, String _methodName) throws Exception {
	Class[] cArg = new Class[1];
	cArg[0] = String.class;
	//TraceManager.addDev("Looking for method=" + _methodName + " in instance " + instance);
	Method method = instance.getClass().getMethod(_methodName, cArg);
	return (boolean)(method.invoke(instance, value));
    }
    

    
    public ImageIcon executeRetImageIconMethod(Class<?> c, String _methodName) {
	// We have a valid plugin. We now need to get the Method
	try {
	Method m = c.getMethod(_methodName);
	if (m == null) {
	    return null;
	}
	
	return (ImageIcon)(m.invoke(null));
	} catch (Exception e) {
	    TraceManager.addDev("Exception occured when executing method " + _methodName);
	    return null;
	}
    }

    public String removeJar(String withjar) {
	int index = withjar.indexOf(".jar");
	if (index == -1) {
	    return withjar;
	}
	return withjar.substring(0, index);

    }

}
