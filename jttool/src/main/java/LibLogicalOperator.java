/**
 * Class LibLogicalOperator
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 10/03/2005
 * @version 1.1 10/03/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.util.*;

public class LibLogicalOperator {
    public static Random random = new Random();

    public static int makeChoice(boolean b[]) {
	int i, cpt = 0, r;

	//System.out.println("length of b[]:" + b.length);

	for(i=0; i<b.length; i++) {
	    //System.out.println("Checking b[" + i + "]");
	    if (b[i]) { 
		//System.out.println("cpt ++");
		cpt++;
	    }
	}

	if (cpt == 0) {
	    return -1;
	}

	//System.out.println("cpt = " + cpt);

	r = random.nextInt(cpt);
	cpt = cpt - r;
	//System.out.println("r=" + r);

	i=0;

	while(i<b.length) {
	    if ((cpt == 1) && (b[i])) {
		return i;
	    }
	    if (b[i]) {
		cpt --;
	    }
	    i++;
	}

	return -1;
    }

    public static int nbOfTrue(boolean b[]) {
	int i, cpt = 0;

	for(i=0; i<b.length; i++) {
	    //System.out.println("Checking b[" + i + "]");
	    if (b[i]) { 
		cpt++;
	    }
	}

	return cpt;
    }

}
