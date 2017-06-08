/**
 * Class LibTemporalOperator
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 10/03/2005
 * @version 1.1 10/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.util.*;

public class LibTemporalOperator {
    public static Random random = new Random();

    public static void waitFor(long millis, long nanos) {
	long diff;

	// Calculate the real number of nanos
	if (nanos > 999999) {
	    diff = nanos - 999999;
	    nanos = 999999;
	    millis = millis + (diff / 1000000);
	}

	//System.out.println("Waiting for exactly millis=" + millis + " nanos=" + nanos);

	try {
	    Thread.currentThread().sleep(millis, (int)(nanos));
	} catch (IllegalArgumentException iae) {
	    //System.out.println("Illegal argument millis=" + millis + " nanos=" + nanos);
	} catch (InterruptedException ie) {
	    //System.out.println("Interrupted");
	}
    }

    public static void waitForAtMost(long millis, long nanos) {
	long diff;

	// Calculate the real number of nanos
	if (nanos > 999999) {
	    diff = nanos - 999999;
	    nanos = 999999;
	    millis = millis + (diff / 1000000);
	}

	//System.out.println("Waiting for at most millis=" + millis + " nanos=" + nanos);
	
	if (millis != 0)
	    millis = Math.abs(random.nextLong() % millis);

	if (nanos != 0)
	    nanos = Math.abs(random.nextLong() % nanos);

	waitFor(millis, nanos);
    }

    // In milliseconds only
    public static long getRandomValueBetween(long v1, long v2) {
	long ret = (long)(Math.random()*(v2 - v1))+v1;
	System.out.println("Waiting for:" + ret);
	return ret;
    }

}
