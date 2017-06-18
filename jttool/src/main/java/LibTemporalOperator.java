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




package jttool;

import java.util.*;


/**
 * Class LibTemporalOperator
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 10/03/2005
 * @version 1.1 10/03/2005
 * @author Ludovic APVRILLE
 */
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
