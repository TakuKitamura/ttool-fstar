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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.distribution.*;

/**
 * Class MyMath
 * Creation: 08/02/2012
 * Version 1.0 08/02/2012
 * @author Ludovic APVRILLE
 */
public class MyMath {
  
   
	public static int gcd(int[] numbers) {
		if ((numbers ==null) || (numbers.length == 0)){
			return -1;
		}
		
		if (numbers.length ==1) {
			return Math.abs(numbers[0]);
		}
		
		int egcd = egcd(Math.abs(numbers[0]), Math.abs(numbers[1]));
		int index = 2;
		while(index<numbers.length) {
			egcd = egcd(egcd, Math.abs(numbers[index]));
			index ++;
		}
		
		return egcd;
		
	}
	
	// Euclidian algorithm for GCD
	// a and b must be positive
	public static int egcd(int a, int b) {
    
		if (a == 0)
        
			return b;

   
		while (b != 0) {
			if (a > b)
				a = a - b;
			else
				b = b - a;
		}
    
		return a;

	}

	// Triangular distribution
    // a,b: interval min and max
    // c: std deviation
	public static double triangularDistribution(double a, double b, double c) {
		double F = (c - a) / (b - a);
		double rand = Math.random();
		if (rand < F) {
			return a + Math.sqrt(rand * (b - a) * (c - a));
		} else {
			return b - Math.sqrt((1 - rand) * (b - a) * (b - c));
		}
	}

    // Gaussian distribution
    // a: min of interval
    // b : max of interval
    // c: standard deviation
    public static double gaussianDistribution(double a, double b, double c) {
        Random r = new Random();
        double n = r.nextGaussian();
        //System.out.println("1. n=" + n);
        n = n * c;
        //System.out.println("1.1 n=" + n);
        n = n + (b+a)/2;
        //System.out.println("1.2 n=" + n);
        if (n < a) {
            return gaussianDistribution(a, b, c);
        }
        //System.out.println("1.3 n=" + n);
        if (n>b) {
            return gaussianDistribution(a, b, c);
        }
        //n = Math.max(a, n);
        //System.out.println("2. n=" + n);
        return n;
    }


    public static double logNormalDistribution(double a, double b, double sigma, double mean) {
	    //TraceManager.addDev("LOG  NORMAL. SIGMA=" + sigma + " MEAN=" + mean);
	    LogNormalDistribution lnd = new LogNormalDistribution(sigma, mean);
	    double val = lnd.sample();
	    val += a;
	    if (val > b) {
	        return logNormalDistribution(a, b, sigma, mean);
        }
        return val;
    }

    public static double exponentialDistribution(double a, double b, double mean) {
        ExponentialDistribution ed = new ExponentialDistribution(mean);
        double val = ed.sample();
        val += a;
        if (val > b) {
            return exponentialDistribution(a, b, mean);
        }
        return val;
    }

    public static double weibullDistribution(double a, double b, double shape, double scale) {
        WeibullDistribution wd = new WeibullDistribution(shape, scale);
        double val = wd.sample();
        val += a;
        if (val > b) {
            return weibullDistribution(a, b, shape, scale);
        }
        return val;
    }

    /**
     * Extract the positive or negative int values from a String
     *"-" is considered only as an unary operator
     *
     *
     * @param s Input String
     */
    public static List<Integer> extractIntegerValues(String s) {
		ArrayList<Integer> retList = new ArrayList<>();

		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(s);
		while (m.find()) {
			try {
				retList.add(Integer.decode(m.group()));
			} catch (Exception e) {

			}
		}
		return retList;
	}

    /**
     * @param s Input String
     * @param maxV Maximum of the interval. Expected to be higher than minV
     * @return whether the provided String contains
     * positive or negative int values outside
     * of the input interval
     */
	public static boolean hasIntegerValueOverMax(String s, int maxV) {
        List<Integer> listOfInt = extractIntegerValues(s);
        for(Integer i: listOfInt) {
            int v = Math.abs(i.intValue());
            if (v > maxV) {
                return true;
            }
        }
        return false;
    }




  
}
