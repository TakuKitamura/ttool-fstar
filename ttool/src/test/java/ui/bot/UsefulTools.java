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
			if (c < 123 && c > 96) {
				t.add(charToKeyEvent(c));
			}
			else if (c < 91 && c > 64) {
				t.add(KeyEvent.VK_CAPS_LOCK);
				t.add(upCharToKeyEvent(c));
				t.add(KeyEvent.VK_CAPS_LOCK);
			}
			else if (c < 58 && c > 47) {
				t.add(numToKeyEvent(c));
			}
			else
				t.add(specialCharToKeyEvent(c));
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
				TraceManager.addDev("UsefulTools : numToKeyEvent : char not foud");
		}
		return code;
	}
	
	public int specialCharToKeyEvent(char c) {
		int code = 0;
		switch(c) {
			case '@' : code = KeyEvent.VK_AT;
				break;
			case '\\' : code = KeyEvent.VK_BACK_SLASH;
				break;
			case '/' : code = KeyEvent.VK_SLASH;
				break;
			case '^' : code = KeyEvent.VK_CIRCUMFLEX;
				break;
			case '[' : code = KeyEvent.VK_OPEN_BRACKET;
				break;
			case ']' : code = KeyEvent.VK_CLOSE_BRACKET;
				break;
			case '(' : code = KeyEvent.VK_LEFT_PARENTHESIS;
				break;
			case ')' : code = KeyEvent.VK_RIGHT_PARENTHESIS;
				break;
			case '{' : code = KeyEvent.VK_BRACELEFT;
				break;
			case '}' : code = KeyEvent.VK_BRACERIGHT;
				break;
			case ':' : code = KeyEvent.VK_COLON;
				break;
			case ';' : code = KeyEvent.VK_SEMICOLON;
				break;
			case '.' : code = KeyEvent.VK_PERIOD;
				break;
			case ',' : code = KeyEvent.VK_COMMA;
				break;
			case '<' : code = KeyEvent.VK_LESS;
				break;
			case '>' : code = KeyEvent.VK_GREATER;
				break;
			case '!' : code = KeyEvent.VK_EXCLAMATION_MARK;
				break;
			case ' ' : code = KeyEvent.VK_SPACE;
				break;
			case '-' : code = KeyEvent.VK_MINUS;
				break;
			case '+' : code = KeyEvent.VK_PLUS;
				break;
			case '*' : code = KeyEvent.VK_ASTERISK;
				break;
			case '=' : code = KeyEvent.VK_EQUALS;
				break;
			case '$' : code = KeyEvent.VK_DOLLAR;
				break;
			case '#' : code = KeyEvent.VK_NUMBER_SIGN;
				break;
			case '_' : code = KeyEvent.VK_UNDERSCORE;
				break;
			default:
				TraceManager.addDev("UsefulTools : specialCharToKeyEvent : char not foud");
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
				TraceManager.addDev("UsefulTools : charToKeyEvent : char not foud");
		}
		return code;
	}
	
	public int upCharToKeyEvent(char c) {
		int code = 0;
		switch(c) {
			case 'A' : code = KeyEvent.VK_A;
				break;
			case 'B' : code = KeyEvent.VK_B;
				break;
			case 'C' : code = KeyEvent.VK_C;
				break;
			case 'D' : code = KeyEvent.VK_D;
				break;
			case 'E' : code = KeyEvent.VK_E;
				break;
			case 'F' : code = KeyEvent.VK_F;
				break;
			case 'G' : code = KeyEvent.VK_G;
				break;
			case 'H' : code = KeyEvent.VK_H;
				break;
			case 'I' : code = KeyEvent.VK_I;
				break;
			case 'J' : code = KeyEvent.VK_J;
				break;
			case 'K' : code = KeyEvent.VK_K;
				break;
			case 'L' : code = KeyEvent.VK_L;
				break;
			case 'M' : code = KeyEvent.VK_M;
				break;
			case 'N' : code = KeyEvent.VK_N;
				break;
			case 'O' : code = KeyEvent.VK_O;
				break;
			case 'P' : code = KeyEvent.VK_P;
				break;
			case 'Q' : code = KeyEvent.VK_Q;
				break;
			case 'R' : code = KeyEvent.VK_R;
				break;
			case 'S' : code = KeyEvent.VK_S;
				break;
			case 'T' : code = KeyEvent.VK_T;
				break;
			case 'U' : code = KeyEvent.VK_U;
				break;
			case 'V' : code = KeyEvent.VK_V;
				break;
			case 'W' : code = KeyEvent.VK_W;
				break;
			case 'X' : code = KeyEvent.VK_X;
				break;
			case 'Y' : code = KeyEvent.VK_Y;
				break;
			case 'Z' : code = KeyEvent.VK_Z;
				break;			
			default:
				TraceManager.addDev("UsefulTools : upCharToKeyEvent : char not foud");
		}
		return code;
	}
	
}