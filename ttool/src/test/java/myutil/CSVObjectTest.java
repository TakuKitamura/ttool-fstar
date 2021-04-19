/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille

   ludovic.apvrille AT enst.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.
*/
package myutil;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class CSVObjectTest {
    private static String GOOD_CSV = "student, teacher, class\nfoo, 86e65064-2361-4a60-8871-efe389f6e549, 1\nfii, tii, os and uml ";
    private static String BAD_CSV_1 = "student, teacher, 1\nfoo, too\nfii, tii, os";
    private static String BAD_CSV_2 = "student, teacher, class\nfoo, 86e65064-2361-4a60-8871-efe389f6e549, uml, os\nfii, tii, os";

    @Test
    public void parsingTest() {

        CSVObject csv = new CSVObject();

        assertFalse(csv.parse(null));
        assertFalse(csv.parse(""));
        assertFalse(csv.parse(" "));

        assertFalse(csv.parse(BAD_CSV_1));
        assertFalse(csv.parse(BAD_CSV_2));

        assertTrue(csv.parse(GOOD_CSV));

        assertEquals(csv.getNbOfLines(), 3);
        assertEquals(csv.getNbOfEltsPerLine(), 3);

        assertEquals(csv.get(0, 0), "student");
        assertEquals(csv.get(0, 1), "teacher");
        assertEquals(csv.get(0, 2), "class");
        assertEquals(csv.get(1, 0), "foo");
        assertEquals(csv.get(1, 1), "86e65064-2361-4a60-8871-efe389f6e549");
        assertEquals(csv.get(1, 2), "1");
        assertEquals(csv.get(2, 0), "fii");
        assertEquals(csv.get(2, 1), "tii");
        assertEquals(csv.get(2, 2), "os and uml");


        // Testing int
        boolean hasException = false;
        try {
            int test = csv.getInt(0, 0);
        } catch (NumberFormatException nfe) {
            hasException = true;
        }
        assertTrue(hasException);

        hasException = false;
        try {
            int test = csv.getInt(1, 2);
        } catch (NumberFormatException nfe) {
            hasException = true;
        }
        assertFalse(hasException);

        // Testing UUID
        boolean isIllegal = false;
        try {
            UUID myUUID = csv.getUUID(0, 0);
        } catch (IllegalArgumentException iae) {
            isIllegal = true;
        }
        assertTrue(isIllegal);

        isIllegal = false;
        try {
            UUID myUUID = csv.getUUID(1, 1);
        } catch (IllegalArgumentException iae) {
            isIllegal = true;
        }
        assertFalse(isIllegal);



    }


}
