/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file is the main file, allowing to build the main frame correctly
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