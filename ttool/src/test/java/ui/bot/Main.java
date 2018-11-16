/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 */

package ui.bot;

import java.io.File;

import common.SpecConfigTTool;
import ui.MainGUI;
import ui.util.IconManager;

/*
 * Class Main
 * Creation: 09/10/2018
 * @version 1.0 09/10/2018
 * @author Arthur VUAGNIAUX
*/

public class Main extends MainGUI {

	public Main(boolean _openLast, boolean _turtleOn, boolean _systemcOn, boolean _lotosOn, boolean _proactiveOn,
			boolean _tpnOn, boolean _osOn, boolean _uppaalOn, boolean _ncOn, boolean _avatarOn, boolean _proverifOn,
			boolean _avatarOnly, boolean _experimental) {
		super(_openLast, _turtleOn, true, _lotosOn, _proactiveOn, _tpnOn, _osOn, _uppaalOn, _ncOn, _avatarOn, _proverifOn,
				_avatarOnly, _experimental);
		
		SpecConfigTTool.setDirConfig(new File("lauch_configuration/config.xml"));
		IconManager.loadImg();
		build(); 
	}
}

//public class Main implements ActionListener {
//    public boolean finish = false;
//    public static Main main = new Main();
//    public static MainGUI test;
//    public static boolean systemc = true;
//    public static boolean lotos = true;
//    public static boolean proactive = false;
//    public static boolean tpn = false;
//    public static boolean os = false;
//    public static boolean uppaal = true;
//    public static boolean nc = true; // Network calculus
//    public static boolean avatar = true; // avatar profile
//    public static boolean proverif = true;
//    public static boolean experimental = false;
//    public static boolean avataronly = false;
//    public static boolean turtle = false;
//    public static boolean openLast = false;
//
//    public Main() {
//
//
//        TraceManager.devPolicy = TraceManager.TO_DEVNULL;
//
//        System.out.println("\n*** TTool version: " + DefaultText.getFullVersion() + " ***\n");
//
//
//        // Read the image data and display the splash screen
//        // -------------------------------------------------
//
//
//        new Timer(2500, main).start();
//
//        JStartingWindow splashFrame = null;
//        //System.out.println("hello");
//        URL imageURL = Main.class.getResource("ui/util/starting_logo.gif");
//        Image img;
//        //System.out.println("helli");
//        if (imageURL != null) {
//
//            img = (new ImageIcon(imageURL)).getImage();
//            splashFrame = JStartingWindow.splash(img, "Loading TTool's elements");
//            //splashFrame.setLicenceMessage("An open-source toolkit from:");
//            if (splashFrame != null) {
//                splashFrame.setMiddleMessage("version " + DefaultText.getVersion());
//            }
//            //System.out.println("helly");
//        } else {
//            System.err.println("Starting image not found");
//            System.exit(0);
//        }
//
//
//        //new Timer(2500, main).start();
//
//
//        // Starting window
//        // setting default language
//        if (splashFrame != null) {
//            splashFrame.setMessage("Setting language");
//        }
//        Locale.setDefault(new Locale("en"));
//
//        boolean startLauncher = true;
//
//        // Setting certificates
//	/*String trustStore = System.getProperty("javax.net.ssl.trustStore");
//	if (trustStore == null) {
//	    System.setProperty("javax.net.ssl.trustStore", "cacerts.jks");
//	    } */
//
//        // Analyzing arguments
//        String config = "config.xml";
//        startLauncher = true;
//
//        // Icons
//        if (splashFrame != null) {
//            splashFrame.setMessage("Loading images");
//        }
//        IconManager.loadImg();
//
//        // Loading configuration
//        if (splashFrame != null) {
//            splashFrame.setMessage("Loading configuration file: " + config);
//        }
//
//
//        //ConfigurationTTool.makeDefaultConfiguration();
//        try {
//            ConfigurationTTool.loadConfiguration(config, systemc);
//            SpecConfigTTool.setBasicConfigFile(config);
//        } catch (Exception e) {
//            System.out.println("Couldn't load configuration from file: " + config);
//        }
//        TraceManager.addDev("\nConfiguration:\n--------------");
//        TraceManager.addDev(ConfigurationTTool.getConfiguration(systemc));
//        TraceManager.addDev("\nDebugging trace:\n----------------");
//
//        TraceManager.addDev("\nPreparing plugins\n");
//        if (splashFrame != null) {
//            splashFrame.setMessage("Preparing plugins");
//        }
//        PluginManager.pluginManager = new PluginManager();
//        PluginManager.pluginManager.preparePlugins(ConfigurationTTool.PLUGIN_PATH, ConfigurationTTool.PLUGIN, ConfigurationTTool.PLUGIN_PKG);
//
//        if (ConfigurationTTool.LauncherPort.length() > 0) {
//            try {
//                int port = Integer.decode(ConfigurationTTool.LauncherPort).intValue();
//                launcher.RshClient.PORT_NUMBER = port;
//                launcher.RshServer.PORT_NUMBER = port;
//                TraceManager.addDev("Port number set to: " + port);
//            } catch (Exception e) {
//                TraceManager.addError("Wrong port number:" + ConfigurationTTool.LauncherPort);
//            }
//        }
//
//        if (startLauncher) {
//            Thread t = new Thread(new RTLLauncher());
//            t.start();
//        }
//
//        // making main window
//        if (splashFrame != null) {
//            splashFrame.setMessage("Creating main window");
//        }
//        MainGUI mainGUI = new MainGUI(openLast, turtle, systemc, lotos, proactive, tpn, os, uppaal, nc, avatar, proverif, avataronly, experimental);
//        if (splashFrame != null) {
//            splashFrame.setMessage("Building graphical components");
//        }
//        mainGUI.build();
//
//        // loading configuration
//
//        // starting application
//// DB: Useless
////        if (mainGUI == null) {
////            ErrorGUI.exit(ErrorGUI.GUI);
////        }
//
//        if (splashFrame != null) {
//            splashFrame.setMessage("Starting TTool ...");
//        }
//        main.waitFinish();
//        //mainGUI.start();
//        test = mainGUI;
//
//        // Dispose the splash screen
//        // -------------------------
//        if (splashFrame != null) {
//            splashFrame.dispose();
//        }
//
//        // Checking for update
//        //TToolUpdater tu = new TToolUpdater(mainGUI.frame);
//        //tu.start();
//    }
//
//    
//    
//    public MainGUI getMainGui() {
//    	return test;
//    }
//    
//    public synchronized void waitFinish() {
//        while (finish == false) {
//            try {
//                wait();
//            } catch (InterruptedException ie) {
//            }
//        }
//    }
//
//    public synchronized void okFinish() {
//        finish = true;
//        notifyAll();
//    }
//
//    public void actionPerformed(ActionEvent evt) {
//        main.okFinish();
//    }
//
//    public static void testBoolExpr() {
//
//
//        evalBool("t or f");
//
//        evalBool("(t) or f");
//
//        evalBool("(0==0)");
//        
//        System.exit(-1);
//    }
//
//    public static void evalBool(String s) {
//        BoolExpressionEvaluator bee = new BoolExpressionEvaluator();
//        System.out.println("->Result of " + s + " =" + bee.getResultOf(s));
//        if (bee.hasError()) {
//            System.out.println("Error = " + bee.getFullError());
//        }
//        System.out.println("\n\n");
//    }
//	
//}
