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


package cli;

import common.ConfigurationTTool;
import launcher.RTLLauncher;
import myutil.Conversion;
import myutil.IntExpressionEvaluator;
import myutil.PluginManager;
import myutil.TraceManager;
import ui.MainGUI;
import ui.util.IconManager;
import avatartranslator.*;

import java.io.File;
import java.util.BitSet;
import java.util.*;


/**
 * Class Set
 * Creation: 19/03/2019
 * Version 2.0 19/03/2019
 *
 * @author Ludovic APVRILLE
 */
public class TestSpecific extends Command  {


    public TestSpecific() {

    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "specifictest";
    }

    public String getShortCommand() {
        return "st";
    }

    public String getUsage() { return "specifictest <arg>"; }

    public String getDescription() { return "Starting s specific test. Reserved for Development purpose"; }

    public String getExample() {
        return "specifictest x*y";
    }


    public  String executeCommand(String command, Interpreter interpreter) {
        try {
            AvatarSpecification as = new AvatarSpecification("Spec", null);
            AvatarBlock block = new AvatarBlock("myblock", as, null);
            as.addBlock(block);
            AvatarAttribute x = new AvatarAttribute("x", AvatarType.INTEGER, block, null);
            block.addAttribute(x);
            AvatarAttribute y = new AvatarAttribute("y", AvatarType.INTEGER, block, null);
            block.addAttribute(y);
            AvatarAttribute z = new AvatarAttribute("z", AvatarType.INTEGER, block, null);
            block.addAttribute(z);

            x.setInitialValue("10");
            y.setInitialValue("5");
            z.setInitialValue("2");

            AvatarTransition at = new AvatarTransition(block, "at", null);
            at.addAction(command);

            IntExpressionEvaluator iee = new IntExpressionEvaluator();

            System.out.println("Value of x=" + x.getInitialValue() + " y=" + y.getInitialValue() + " z=" +z.getInitialValue());
            String expr = at.getAction(0).toString();
            int index = expr.indexOf('=');
            if (index == -1) {
                System.out.println("Invalid expr");
                return "Test failed no '=' in assignation";
            }

            expr = expr.substring(index+1, expr.length()).trim();
            System.out.println("Evaluating: " + expr);

            for(AvatarAttribute aa: block.getAttributes()) {
                expr = Conversion.putVariableValueInString(AvatarSpecification.ops, expr, aa.getName(), aa.getInitialValue());
            }

            System.out.println("Evaluating: " + expr);

            double result = iee.getResultOf(expr);
            System.out.println("Result = " + result);

            return null;
        } catch (Exception e) {
            TraceManager.addDev("Exception: " + e.getMessage());
            return "Test failed";

        }

    }

    public void fillSubCommands() {

    }
}
