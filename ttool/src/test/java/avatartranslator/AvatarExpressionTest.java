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
 * Class AvatarExpressionTest
 * Creation: 29/04/2020
 * @version 1.0 29/04/2020
 * @author Alessandro TEMPIA CALVINO
 * @see
 */


package avatartranslator;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import avatartranslator.*;
import avatartranslator.modelchecker.SpecificationBlock;

public class AvatarExpressionTest {
    
    private AvatarGuard res;
    private AvatarBlock block;
    private SpecificationBlock specBlock;
    
    public AvatarExpressionTest() {
        
    }
    
    @Before
    public void test () {
        AvatarSpecification as = new AvatarSpecification("avatarspecification", null);

        block = new AvatarBlock("myblock", as, null);
        as.addBlock(block);
        AvatarAttribute x = new AvatarAttribute("x", AvatarType.INTEGER, block, null);
        block.addAttribute(x);
        AvatarAttribute y = new AvatarAttribute("y", AvatarType.INTEGER, block, null);
        block.addAttribute(y);
        AvatarAttribute z = new AvatarAttribute("z", AvatarType.INTEGER, block, null);
        block.addAttribute(z);
        AvatarAttribute w = new AvatarAttribute("w", AvatarType.BOOLEAN, block, null);
        block.addAttribute(w);
        
        x.setInitialValue("10");
        y.setInitialValue("5");
        z.setInitialValue("2");
        
        specBlock = new SpecificationBlock();
        specBlock.init(block, false);
    }
    
    @Test
    public void testImmediate() {
        AvatarExpressionSolver e1 = new AvatarExpressionSolver("10 + 15 >= 20");
        e1.buildExpression();
        AvatarExpressionSolver e2 = new AvatarExpressionSolver("-10 / 2 - 15 * 2 + 1 == -30 -4");
        e2.buildExpression();
        AvatarExpressionSolver e3 = new AvatarExpressionSolver("not(-10 / 2 - 15 * 2 + 1 == -(60 - 26))");
        e3.buildExpression();
        AvatarExpressionSolver e4 = new AvatarExpressionSolver("1 && 0 >= 1 || 0");
        e4.buildExpression();
        AvatarExpressionSolver e5 = new AvatarExpressionSolver("true and not(false) == not(false or false)");
        e5.buildExpression();
        AvatarExpressionSolver e6 = new AvatarExpressionSolver("10 -Cabin.match");
        assertTrue(e1.getResult() == 1);
        assertTrue(e2.getResult() == 1);
        assertTrue(e3.getResult() == 0);
        assertTrue(e4.getResult() == 0);
        assertTrue(e5.getResult() == 1);
        assertTrue(e6.buildExpression() == false);
    }
    
    @Test
    public void testBlock() {
        AvatarExpressionSolver e1 = new AvatarExpressionSolver("x + y");
        e1.buildExpression(block);
        AvatarExpressionSolver e2 = new AvatarExpressionSolver("-x / y - 15 * z + 1 == -31");
        e2.buildExpression(block);
        AvatarExpressionSolver e3 = new AvatarExpressionSolver("not(-x / z - (x + y) * 2 + 1 == -(60 - 26))");
        e3.buildExpression(block);
        assertTrue(e1.getResult(specBlock) == 15);
        assertTrue(e2.getResult(specBlock) == 1);
        assertTrue(e3.getResult(specBlock) == 0);
    }

}
