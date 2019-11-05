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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.BitSet;
import java.util.*;
import java.util.List;

/**
 * Class Mouse
 * Creation: 30/10/2019
 * Version 1.0 30/10/2019
 *
 * @author Ludovic APVRILLE
 */
public class Robot extends Command {
    // Action commands
    private final static String DOUBLE_CLICK = "leftclick";
    private final static String LCLICK = "leftclick";
    private final static String RCLICK = "rightclick";
    private final static String MOVEA_LCLICK = "moveleftclick";
    private final static String MOVEA_RCLICK = "moverightclick";
    private final static String MOVE_ABS = "movea";
    private final static String MOVE_REL = "mover";
    private final static String DRAG_ABS = "draga";
    private final static String DRAG_REL = "dragr";
    private final static String ENTER_KEY = "key";
    private final static String ENTER_KEYS = "keys";
    private final static String ENTER_TEXT = "text";

    private java.awt.Robot robot;

    public Robot() {
    }

    public List<Command> getListOfSubCommands() {
        return subcommands;
    }

    public String getCommand() {
        return "robot";
    }

    public String getShortCommand() {
        return "r";
    }

    public String getUsage() {
        return "robot <subcommand> <options>";
    }

    public String getDescription() {
        return "Can be used to perform actions with a mouse/key robot in TTool";
    }




    public void fillSubCommands() {
        // Double click
        Command dclick = new Command() {
            public String getCommand() {
                return DOUBLE_CLICK;
            }

            public String getShortCommand() {
                return "dc";
            }

            public String getDescription() {
                return "Double click at current location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                click(InputEvent.BUTTON1_DOWN_MASK);
                click(InputEvent.BUTTON1_DOWN_MASK);

                return null;
            }
        };


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

        // drag tp abs coordinates
        Command draga = new Command() {
            public String getCommand() {
                return DRAG_ABS;
            }

            public String getShortCommand() {
                return "da";
            }

            public String getDescription() {
                return "Drag mouse from current to a new absolute location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                if ((ret = moveAbsolute(command)) != null) return ret;
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                return null;
            }
        };

        // drag tp abs coordinates
        Command dragr = new Command() {
            public String getCommand() {
                return DRAG_REL;
            }

            public String getShortCommand() {
                return "dr";
            }

            public String getDescription() {
                return "Drag mouse from current to a new relative location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                if ((ret = moveRelative(command)) != null) return ret;
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

                return null;
            }
        };

        // entering x key event
        Command keys = new Command() {
            public String getCommand() {
                return ENTER_KEYS;
            }

            public String getShortCommand() {
                return "ks";
            }

            public String getDescription() {
                return "Enter x times key at current location: <x> then DEL|TAB|SPACE|ENTER|BACKSPACE|UP|DOWN|LEFT|RIGHT";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                // Find how many times the key must be entered
                String[] split = command.trim().split(" ");
                if (split.length < 2) {
                    return Interpreter.BAD;
                }

                int nbOfTimes;

                try {
                    nbOfTimes = Integer.decode(split[0]);
                } catch (Exception e) {
                    return Interpreter.BAD;
                }

                if (nbOfTimes < 0) {
                    return Interpreter.BAD;
                }

                for (int i=0; i<nbOfTimes; i++) {
                    if ((ret = key(split[1])) != null) return ret;
                }

                return null;
            }
        };

        // entering key event
        Command key = new Command() {
            public String getCommand() {
                return ENTER_KEY;
            }

            public String getShortCommand() {
                return "k";
            }

            public String getDescription() {
                return "Enter key at current location: DEL|TAB|SPACE|ENTER|BACKSPACE|UP|DOWN|LEFT|RIGHT";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                if ((ret = key(command)) != null) return ret;

                return null;
            }
        };

        // Entering text
        Command text = new Command() {
            public String getCommand() {
                return ENTER_TEXT;
            }

            public String getShortCommand() {
                return "t";
            }

            public String getDescription() {
                return "Enter text at current location";
            }

            public String executeCommand(String command, Interpreter interpreter) {
                if (!interpreter.isTToolStarted()) {
                    return Interpreter.TTOOL_NOT_STARTED;
                }

                String ret; if ((ret = checkRobot()) != null) return ret;

                if ((ret = text(command)) != null) return ret;

                return null;
            }
        };

        addAndSortSubcommand(dclick);
        addAndSortSubcommand(lclick);
        addAndSortSubcommand(rclick);
        addAndSortSubcommand(movea);
        addAndSortSubcommand(mover);
        addAndSortSubcommand(movealc);
        addAndSortSubcommand(movearc);
        addAndSortSubcommand(draga);
        addAndSortSubcommand(dragr);
        addAndSortSubcommand(keys);
        addAndSortSubcommand(key);
        addAndSortSubcommand(text);

    }

    // Needed basic commands
    public String checkRobot() {
        if (robot == null) {
            try {
                robot = new java.awt.Robot();
                robot.setAutoDelay(50);
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
            
        } catch (Exception e) {
            return Interpreter.BAD;
        }

        return null;
    }

    public String key(String k) {

        int code = stringToKeyEventCode(k);

        if (code < 0) {
            return Interpreter.BAD;
        }

        robot.keyPress(code);
        robot.keyRelease(code);


        return null;
    }

    public String text(String t) {
        StringSelection stringSelection = new StringSelection(t);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        return null;
    }


    public int stringToKeyEventCode(String key) {
        key = key.trim();
        key = key.toUpperCase();

        TraceManager.addDev("Selected key: " + key);


        if (key.equals("DEL")) {
            return KeyEvent.VK_DELETE;
        } else if (key.equals("TAB")) {
            return KeyEvent.VK_TAB;
        } else if (key.equals("SPACE")) {
            return KeyEvent.VK_SPACE;
        } else if (key.equals("ENTER")) {
            return KeyEvent.VK_ENTER;
        } else if (key.equals("BACKSPACE")) {
            return KeyEvent.VK_BACK_SPACE;
        } else if (key.equals("UP")) {
            return KeyEvent.VK_UP;
        } else if (key.equals("DOWN")) {
            return KeyEvent.VK_DOWN;
        } else if (key.equals("LEFT")) {
            return KeyEvent.VK_DOWN;
        } else if (key.equals("RIGHT")) {
            return KeyEvent.VK_DOWN;
        }

        return -1;
    }


}
