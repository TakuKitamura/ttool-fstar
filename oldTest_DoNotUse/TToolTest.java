/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class AvatarPragma
 * Creation: 20/05/2010
 * @version 1.1 01/07/2014
 * @author Ludovic APVRILLE, Florian LUGOU
 * @see
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import myutil.TraceManager;

public abstract class TToolTest {	
    private MessageDigest md;
    private boolean verbose;
    private boolean ok;
    private String name;

    public TToolTest (String name, boolean verbose) {
        if (!this.verbose) {
            TraceManager.userPolicy = TraceManager.TO_DEVNULL;
            TraceManager.devPolicy = TraceManager.TO_DEVNULL;
            TraceManager.errPolicy = TraceManager.TO_DEVNULL;
        }

        try {
            this.ok = true;
            this.md = MessageDigest.getInstance ("SHA");
            this.verbose = verbose;
            this.name = name;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
            System.exit (-1);
        }
    }

    public TToolTest (String name) {
        this (name, false);
    }

    protected void error (String str) {
        if (this.ok)
            System.err.println ();
        System.err.println (str);
        this.ok = false;
    }

    protected void updateDigest (String str) {
        if (this.verbose)
            System.out.println (str);
        this.md.update (str.getBytes ());
    }

    public byte[] getDigest () {
        return this.md.digest ();
    }

    public boolean testDigest (byte[] expected) {
        byte[] dig = this.getDigest ();
        if (dig.length != expected.length)
            return false;

        for (int i=0; i<dig.length; i++)
            if (expected[i] != dig[i])
                return false;

        return true;
    }

    public void printDigest () {
        boolean first = true;
        byte[] dig = this.getDigest ();

        System.out.print ("new byte[] {");
        for (byte b: dig) {
            if (first)
                first = false;
            else
                System.out.print (", ");
            System.out.print (b);
        }
        System.out.println ("}");
    }

    public void resetDigest () {
        this.md.reset ();
    }

    public void runTest () {
        System.out.print("==========> Testing " + this.name);
        System.out.flush ();
        if (this.verbose)
            System.out.println ();

        this.test ();

        this.end ();
    }

    private void end () {
        if (this.ok) {
            if (this.verbose)
                System.out.print("==========> Testing " + this.name);
            System.out.println(": ok");
        }
        else
            System.exit (-1);
    }

    protected abstract void test ();
}
