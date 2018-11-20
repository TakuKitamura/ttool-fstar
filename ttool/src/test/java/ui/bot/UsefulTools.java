/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 * 
 * This file gather all the useful functions and tools in order to debug
 * and  to create some tests
 */

package ui.bot;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
	
	public void stringToKeyEvent(String s) {
		ArrayList<Integer> t = new ArrayList<Integer>();
		
		for (char c : s.toCharArray()) {
			t.add(charToKeyEvent(c));
		}
		for (int a : t)
		{
			System.out.println(a);
		}
		
	}
	
	public int numToKeyEvent(char c) {
		int code = 0;
		switch(c) {
			case '1' : code = KeyEvent.VK_1;
				break;
			case '2' : code = KeyEvent.VK_2;
				break;
			case '3' : code = KeyEvent.VK_3;
				break;
			case '4' : code = KeyEvent.VK_4;
				break;
			case '5' : code = KeyEvent.VK_5;
				break;
			case '6' : code = KeyEvent.VK_6;
				break;
			case '7' : code = KeyEvent.VK_7;
				break;
			case '8' : code = KeyEvent.VK_8;
				break;
			case '9' : code = KeyEvent.VK_9;
				break;
			default:
		}
		return code;
	}
	
	public int charToKeyEvent(char c) {
		int code = 0;
		switch(c) {
			case 'a' : code = KeyEvent.VK_A;
				break;
			case 'b' : code = KeyEvent.VK_B;
				break;
			case 'c' : code = KeyEvent.VK_C;
				break;
			case 'd' : code = KeyEvent.VK_D;
				break;
			case 'e' : code = KeyEvent.VK_E;
				break;
			case 'f' : code = KeyEvent.VK_F;
				break;
			case 'g' : code = KeyEvent.VK_G;
				break;
			case 'h' : code = KeyEvent.VK_H;
				break;
			case 'i' : code = KeyEvent.VK_I;
				break;
			case 'j' : code = KeyEvent.VK_J;
				break;
			case 'k' : code = KeyEvent.VK_K;
				break;
			case 'l' : code = KeyEvent.VK_L;
				break;
			case 'm' : code = KeyEvent.VK_M;
				break;
			case 'n' : code = KeyEvent.VK_N;
				break;
			case 'o' : code = KeyEvent.VK_O;
				break;
			case 'p' : code = KeyEvent.VK_P;
				break;
			case 'q' : code = KeyEvent.VK_Q;
				break;
			case 'r' : code = KeyEvent.VK_R;
				break;
			case 's' : code = KeyEvent.VK_S;
				break;
			case 't' : code = KeyEvent.VK_T;
				break;
			case 'u' : code = KeyEvent.VK_U;
				break;
			case 'v' : code = KeyEvent.VK_V;
				break;
			case 'w' : code = KeyEvent.VK_W;
				break;
			case 'x' : code = KeyEvent.VK_X;
				break;
			case 'y' : code = KeyEvent.VK_Y;
				break;
			case 'z' : code = KeyEvent.VK_Z;
				break;			
			default:
		}
		return code;
	}
}