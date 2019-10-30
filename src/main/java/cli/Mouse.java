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
import common.SpecConfigTTool;
import launcher.RTLLauncher;
import myutil.PluginManager;
import myutil.TraceManager;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;
import ui.MainGUI;
import ui.util.IconManager;
import ui.window.JDialogSystemCGeneration;
import ui.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.BitSet;
import java.util.*;
import java.util.List;

/**
 * Class Mouse
 * Creation: 30/10/2018
 * Version 1.0 30/10/2018
 *
 * @author Ludovic APVRILLE
 */
public class Mouse extends Command {
    // Action commands
    private final static String LCLICK = "leftclick";
    private final static String RCLICK = "rightclick";
    private final static String MOVEA_LCLICK = "moveleftclick";
    private final static String MOVEA_RCLICK = "moverightclick";
    private final static String MOVE_ABS = "movea";
    private final static String MOVE_REL = "mover";

    private Robot robot;

    public Mouse() {
    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "mouse";
    }

    public String getShortCommand() {
        return "m";
    }

    public String getUsage() {
        return "mouse <subcommand> <options>";
    }

    public String getDescription() {
        return "Can be used to perform mouse actions in TTool";
    }




    public void fillSubCommands() {
        // Left Click
        Command lclick = new Command() {
            public String getCommand() {
                return LCLICK;
            }

            public String getShortCommand() {
                return "lc";
            }

            public String getDescription() {
                return "Left click at current location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                click(InputEvent.BUTTON1_DOWN_MASK);

                return null;
            }
        };

        // Right Click
        Command rclick = new Command() {
            public String getCommand() {
                return RCLICK;
            }

            public String getShortCommand() {
                return "rc";
            }

            public String getDescription() {
                return "Right click at current location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                click(InputEvent.BUTTON3_DOWN_MASK);

                return null;
            }
        };

        // Absolute move
        Command movea = new Command() {
            public String getCommand() {
                return MOVE_ABS;
            }

            public String getShortCommand() {
                return "ma";
            }

            public String getDescription() {
                return "moving mouse to new an absolute location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                if ((ret = moveAbsolute(command)) != null) return ret;

                return null;
            }
        };

        // Relative move
        Command mover = new Command() {
            public String getCommand() {
                return MOVE_REL;
            }

            public String getShortCommand() {
                return "mr";
            }

            public String getDescription() {
                return "moving mouse to a relative location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                if ((ret = moveRelative(command)) != null) return ret;


                return null;
            }
        };

        // Absolute move and left click
        Command movealc = new Command() {
            public String getCommand() {
                return MOVEA_LCLICK;
            }

            public String getShortCommand() {
                return "malc";
            }

            public String getDescription() {
                return "Moving mouse to new an absolute location and make a left click";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                if ((ret = moveAbsolute(command)) != null) return ret;

                click(InputEvent.BUTTON1_DOWN_MASK);

                return null;
            }
        };

        // Absolute move and right click
        Command movearc = new Command() {
            public String getCommand() {
                return MOVEA_RCLICK;
            }

            public String getShortCommand() {
                return "marc";
            }

            public String getDescription() {
                return "Moving mouse to new an absolute location and make a right click";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                if ((ret = moveAbsolute(command)) != null) return ret;

                click(InputEvent.BUTTON3_DOWN_MASK);

                return null;
            }
        };

        addAndSortSubcommand(lclick);
        addAndSortSubcommand(rclick);
        addAndSortSubcommand(movea);
        addAndSortSubcommand(mover);
        addAndSortSubcommand(movealc);
        addAndSortSubcommand(movearc);

    }




    // Helper commands

    public String checkRobot() {
        if (robot == null) {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                return Interpreter.ROBOT_EXCEPTION + ": " + e.getMessage();
            }
        }
        return null;
    }

    public void click(int button) {
        robot.mousePress(button);
        robot.mouseRelease(button);
    }

    public String moveAbsolute(String cd) {
        int indexSpace = cd.indexOf(" ");
        if (indexSpace == -1) {
            return Interpreter.BAD;
        }

        try {
            int x = Integer.decode(cd.substring(0, indexSpace));
            int y = Integer.decode(cd.substring(indexSpace + 1, cd.length()));
            robot.mouseMove(x, y);
        } catch (Exception e) {
            return Interpreter.BAD;
        }



        return null;
    }

    public String moveRelative(String cd)  {

        int indexSpace = cd.indexOf(" ");
        if (indexSpace == -1) {
            return Interpreter.BAD;
        }

        try {
        int x = Integer.decode(cd.substring(0, indexSpace));
        int y = Integer.decode(cd.substring(indexSpace+1, cd.length()));

        PointerInfo pi = MouseInfo.getPointerInfo();

        robot.mouseMove((int)(pi.getLocation().getX()) + x, (int)(pi.getLocation().getY()) + y);

        robot.mouseMove(x, y);
        } catch (Exception e) {
            return Interpreter.BAD;
        }

        return null;
    }

}
