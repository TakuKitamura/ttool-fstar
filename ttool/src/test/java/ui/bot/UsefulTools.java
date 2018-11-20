/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file gather all the useful functions and tools in order to debug
 * and  to create some tests
 */

package ui.bot;

import myutil.TraceManager;

/*
 * Class UsefulTools
 * Creation: 20/11/2018
 * @version 1.0 20/11/2018
 * @author Arthur VUAGNIAUX
*/

public class UsefulTools {

	public UsefulTools() {}
	
	public void debugThread(int time, String function) {
		TraceManager.addDev(function + "Thread and Debug time");
    	try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}