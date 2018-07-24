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

package proverifspec;

import avatartranslator.toproverif.AVATAR2ProVerif;
import myutil.TraceManager;
import ui.AvatarDesignPanel;
import ui.TAttribute;
import ui.avatarbd.AvatarBDBlock;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class ProVerifResultTrace
 * Creation: 22/05/2017
 * @version 1.0 22/05/2017
 * @author Florian LUGOU
 */
public class ProVerifResultTrace {
    private static Pattern tracePattern;
    private static Pattern blockNamePattern;
    private static Pattern attrPattern;

    private List<ProVerifResultTraceStep> trace;
    private StringBuilder buffer;
    private List<String> proverifProcess;
    private Map<String, Integer> attackerNamesMap;


    static
    {
        ProVerifResultTrace.attrPattern = Pattern.compile("\\b((\\w+?)" + AVATAR2ProVerif.ATTR_DELIM + ")?(\\w+?)(" + AVATAR2ProVerif.ATTR_DELIM + "[0-9_]+?)?(_\\d+)?\\b(\\[[^\\]]*\\])?");
        ProVerifResultTrace.tracePattern = Pattern.compile("^\\d+\\. (.*)$");
        ProVerifResultTrace.blockNamePattern = Pattern.compile("let \\(=sessionID,=call" + AVATAR2ProVerif.ATTR_DELIM + "(.+?)" + AVATAR2ProVerif.ATTR_DELIM + ".*");
    }

    private class OutStep implements ProVerifResultTraceStep {
        private String from;
        private String to;
        private String message;
        private String channel;

        public OutStep(String from, String to, String message, String channel) {
            this.from = from;
            this.to = to;
            this.message = message;
            this.channel = channel;
        }

        public boolean messageEquals(String message)
        {
            return this.message.equals(message);
        }

        public void setTo(String to)
        {
            this.to = to;
        }

        public boolean isToAttacker()
        {
            return this.to.equals("Attacker");
        }

        @Override
        public String describeAsString(AvatarDesignPanel adp)
        {
            return "MSG " + this.from + " -- " + this.channel + " --> " + this.to + " : " + ProVerifResultTrace.this.replaceAllAttributeNames(adp, this.message).replaceAll(",", ", ");
        }

        @Override
        public void describeAsSDTransaction(AvatarDesignPanel adp, BufferedWriter writer, int step) throws IOException
        {
            writer.write("#" + step + " time=0.000000000 block=" + this.from + " blockdestination=" + this.to + " type=synchro channel=" + this.channel + " params=\"" + ProVerifResultTrace.this.replaceAllAttributeNames(adp, this.message).replaceAll(",", ", ") + "\"");
            writer.newLine();
            writer.flush();
        }
        @Override
        public void describeAsTMLSDTransaction(BufferedWriter writer, int step) throws IOException
        {
            writer.write("#" + step + " time=0.000000000 block=" + this.from + " blockdestination=" + this.to + " type=synchro channel=" + this.channel + " params=\"" + this.message.replaceAll(",", ", ") + "\"");
	//		TraceManager.addDev("#" + step + " time=0.000000000 block=" + this.from + " blockdestination=" + this.to + " type=synchro channel=" + this.channel + " params=\"" + this.message.replaceAll(",", ", ") + "\"");
            writer.newLine();
            writer.flush();
        }
    }

    private class EventStep implements ProVerifResultTraceStep {
        private String block;
        private String name;

        public EventStep (String block, String name)
        {
            this.block = block;
            this.name = name;
        }

        @Override
        public String describeAsString(AvatarDesignPanel adp)
        {
            return "EV  " + this.block + "." + this.name;
        }

        @Override
        public void describeAsSDTransaction(AvatarDesignPanel adp, BufferedWriter writer, int step) throws IOException
        {
            writer.write("#" + step + " time=0.000000000 block=" + this.block + " type=state_entering state="+ this.name);
            writer.newLine();
            writer.flush();
        }
        @Override
        public void describeAsTMLSDTransaction(BufferedWriter writer, int step) throws IOException
        {
            writer.write("#" + step + " time=0.000000000 block=" + this.block + " type=state_entering state="+ this.name);
            writer.newLine();
            writer.flush();
        }
    }

    private class NewStep implements ProVerifResultTraceStep {
        private String name;

        public NewStep(String name)
        {
            this.name = name;
        }

        @Override
        public String describeAsString(AvatarDesignPanel adp)
        {
            return "NEW " + ProVerifResultTrace.this.replaceAttributeName(adp, this.name);
        }

        @Override
        public void describeAsSDTransaction(AvatarDesignPanel adp, BufferedWriter writer, int step) throws IOException
        {
            writer.write("#" + step + " time=0.000000000 block=Attacker type=function_call func=new parameters=" + ProVerifResultTrace.this.replaceAttributeName(adp, this.name));
            writer.newLine();
            writer.flush();
        }
        @Override
        public void describeAsTMLSDTransaction(BufferedWriter writer, int step) throws IOException
        {
            writer.write("#" + step + " time=0.000000000 block=Attacker type=function_call func=new parameters=" + this.name);
            writer.newLine();
            writer.flush();
        }
    }

    public ProVerifResultTrace(LinkedList<String> proverifProcess)
    {
        this.proverifProcess = proverifProcess;
        this.trace = new LinkedList<ProVerifResultTraceStep> ();
        this.attackerNamesMap = new HashMap<String, Integer> ();
        this.buffer = null;
    }

    public List<ProVerifResultTraceStep> getTrace()
    {
        return this.trace;
    }

    public void addTraceStep(String str)
    {
        Matcher m = tracePattern.matcher(str);
        if (m.matches())
        {
            this.finalizeStep();
            this.buffer = new StringBuilder();
            str = m.group(1);
        }

        this.buffer.append(str);
    }

    private String replaceAttributeName(AvatarDesignPanel adp, String str)
    {
        Matcher m = ProVerifResultTrace.attrPattern.matcher(str);
        if (m.matches())
        {
            String part1 = m.group(2);
            String part2 = m.group(3);
            Integer s;

            if (part1 == null) {
                part1 = "Attacker";
                s = this.attackerNamesMap.get(part1 + AVATAR2ProVerif.ATTR_DELIM + str);
                if (s == null)
                    return str;
            }
            else
            {
                s = this.attackerNamesMap.get(str);
                if (s != null)
                {
                    String [] spl = str.split(AVATAR2ProVerif.ATTR_DELIM, 3);
                    part1 = spl[0];
                    part2 = spl[1];
                }
            }

            String blockPart = "Attacker";
            String attrPart = part2;
            if (!part1.equals("Attacker"))
            {
                // TODO: why is it just name and not FQN ?
                AvatarBDBlock block = adp.getAvatarBDPanel().getBlockFromOwnerName(part1.replaceAll("__", "."));
                if (block == null)
                {
                    TraceManager.addDev("[ERROR] Unknown block : " + part1);
                    return null;
                }

                if (s == null)
                    blockPart = block.getOwnerName();

                String attrName[] = part2.split("__", 2);
                if (attrName.length >= 2)
                {
                    TAttribute attr = block.getAttributeByName(attrName[0]);
                    if (attr == null)
                    {
                        // TODO: can happen when library function ?
                        TraceManager.addDev("[ERROR] Unknown attribute : " + part2 + " for block " + block.getOwnerName());
                        attrPart = part2.replaceAll("__", ".");
                    }
                    else
                    {
                        // TODO: is it possible that subtype is itself composed ?
                        LinkedList<TAttribute> types = adp.getAvatarBDPanel().getAttributesOfDataType(attr.getTypeOther());
                        if (types.size() > 1)
                            attrPart = attrName[0] + "." + attrName[1];
                        else
                            attrPart = attrName[0];
                    }
                }
            }

            if (s != null && s.intValue() > 0)
                attrPart += "_" + s;

            return blockPart + "." + attrPart;
        }

        return str;
    }

    private String removeBrackets(String str) {
        Stack<Character> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();

        for (char c: str.toCharArray()) {
            if (c == '(' && !stack.empty()) {
                stack.push(c);
            } else if (c == '[') {
                stack.push(c);
            } else if (c == ')' && !stack.empty()) {
                if (stack.peek() == '(')
                    stack.pop();
                else
                    throw new IllegalArgumentException("Malformed expression: " + str);
            } else if (c == ']') {
                if (stack.empty())
                    throw new IllegalArgumentException("Malformed expression: " + str);
                else if (stack.peek() == '[')
                    stack.pop();
                else
                    throw new IllegalArgumentException("Malformed expression: " + str);
            }

            if (stack.empty() && c != ']')
                builder.append(c);
        }

        return builder.toString();
    }

    private String replaceAllAttributeNames(AvatarDesignPanel adp, String str)
    {
        str = this.removeBrackets(str);
        Matcher m = ProVerifResultTrace.attrPattern.matcher(str);
        String result = "";

        int lastEnd = 0;
        while (m.find(lastEnd))
        {
            result += str.substring(lastEnd, m.start());
            result += this.replaceAttributeName(adp, m.group(0));
            lastEnd = m.end();
        }
        result += str.substring(lastEnd);

        // Replace pk(...)
        Pattern p = Pattern.compile("pk\\(([a-zA-Z0-9_.]+)\\)");
        m = p.matcher(result);
        lastEnd = 0;
        str = "";
        while (m.find(lastEnd))
        {
            String replaceBy = m.group(0);
            for (String pragma: adp.getModelPragmas())
            {
                String parts[] = pragma.split("\\s+");
                if (!parts[0].equals("#PrivatePublicKeys") || parts.length < 4)
                    continue;

                if (m.group(1).equals(parts[1] + "." + parts[2]))
                {
                    replaceBy = parts[1] + "." + parts[3];
                    break;
                }
            }

            str += result.substring(lastEnd, m.start());
            str += replaceBy;
            lastEnd = m.end();
        }
        str += result.substring(lastEnd);


        return str;
    }

    private String getBlockNameFromLine(int line)
    {
        for (int i=line; i>=0; i--)
        {
            Matcher m = blockNamePattern.matcher(this.proverifProcess.get(i));
            if (m.matches())
            {
                return m.group(1).replaceAll("__", ".");
            }
        }

        // This can happen when out(pk(...)) in no process due to pragma PrivatePublicKey
        return null;
    }

    private String consumePrecondition(String str)
    {
        try {
            Pattern p = Pattern.compile("The message (.+?) that the attacker may have by (.+?) may be received at input \\{(.+?)\\}\\.(.*)");
            Matcher m = p.matcher(str);
            if (m.matches())
            {
                String msgName = m.group(1);

                if (msgName.startsWith("chControlEnc")
                        || msgName.startsWith("strong" + AVATAR2ProVerif.ATTR_DELIM)
                        || msgName.startsWith("choice" + AVATAR2ProVerif.ATTR_DELIM))
                    return m.group(4);

                String channelName = "";
                Matcher m2 = Pattern.compile(AVATAR2ProVerif.CH_ENCRYPT + ".+?__(.+?)\\((.*)\\)").matcher(msgName);
                if (m2.matches())
                {
                    channelName = m2.group(1);
                    msgName = m2.group(2);
                }

                boolean foundAStep = false;
                for (ProVerifResultTraceStep step: this.trace)
                {
                    if (step instanceof OutStep)
                    {
                        OutStep out = (OutStep) step;
                        if (out.messageEquals(msgName) && out.isToAttacker())
                        {
                            foundAStep = true;
                            out.setTo(this.getBlockNameFromLine(Integer.parseInt(m.group(3))));
                            break;
                        }

                        // TODO: ProVerif does not output twice the out in case of weak authenticity violation...
                        // It can only be seen in the trace reconstruction phase.
                    }
                }

                if (!foundAStep)
                {
                    this.trace.add(new OutStep("Attacker", this.getBlockNameFromLine(Integer.parseInt(m.group(3))), msgName, channelName));
                }

                return m.group(4);
            }

            p = Pattern.compile("We have (.+?)\\.(.*)");
            m = p.matcher(str);
            if (m.matches())
            {
                return m.group(2);
            }

            p = Pattern.compile("The event (.+?)( \\(with environment .+?)? may be executed at \\{(.+?)\\}\\.(.*)");
            m = p.matcher(str);
            if (m.matches())
            {
                return m.group(4);
            }

        } catch (NumberFormatException e) {
            TraceManager.addDev("[ERROR] Parsing int");
        }

        return null;
    }

    private void finalizeStep()
    {
        if (this.buffer == null)
            return;

        String str = this.buffer.toString();
        String newStr = str;
        while (newStr != null)
        {
            str = newStr;
            newStr = this.consumePrecondition(newStr);
        }

        if (str.startsWith("By "))
        {

            return;
        }

        try {
            Pattern p = Pattern.compile("(So t|T)he message (.*) may be sent (.*) at output \\{(\\d+)\\}.*");
            Matcher m = p.matcher(str);

            if (m.matches())
            {
                String msgName = m.group(2);
                if (msgName.startsWith("strong" + AVATAR2ProVerif.ATTR_DELIM)
                        || msgName.startsWith("choice" + AVATAR2ProVerif.ATTR_DELIM)
                        || msgName.startsWith("chControlEnc"))
                    return;

                String blockName = this.getBlockNameFromLine(Integer.parseInt(m.group(4)));

                String channelName = "";
                m = Pattern.compile(AVATAR2ProVerif.CH_ENCRYPT + ".+?__(.+?)\\((.*)\\)").matcher(msgName);
                if (m.matches())
                {
                    channelName = m.group(1);
                    msgName = m.group(2);
                }

                if (blockName != null)
                    this.trace.add(new OutStep(blockName, "Attacker", msgName, channelName));

                return;
            }

            p = Pattern.compile("So event (.*) may be executed at \\{(\\d+)\\}( in session .+)?\\..*");
            m = p.matcher(str);
            if (m.matches())
            {
                String line = m.group(2);
                p = Pattern.compile("enteringState" + AVATAR2ProVerif.ATTR_DELIM + "[a-zA-Z0-9_]+" + AVATAR2ProVerif.ATTR_DELIM + "([a-zA-Z0-9_]+)");
                m = p.matcher(m.group(1));

                if (m.matches())
                {
                    this.trace.add(new EventStep(this.getBlockNameFromLine(Integer.parseInt(line)), m.group(1)));

                    return;
                }

                return;
            }

            p = Pattern.compile("The attacker has some term (.*)\\.attacker\\(\\1\\)\\.");
            m = p.matcher(str);
            p = Pattern.compile("We assume as hypothesis thatattacker\\((\\w*)\\)\\.");
            Matcher m2 = p.matcher(str);
            if (m.matches() || m2.matches())
            {
                String attrName;
                if (m.matches())
                    attrName = m.group(1);
                else
                    attrName = m2.group(1);
                String attrName2 = attrName;
                if (attrName.startsWith("strong" + AVATAR2ProVerif.ATTR_DELIM)
                        || attrName.startsWith("choice" + AVATAR2ProVerif.ATTR_DELIM))
                    return;

                m = ProVerifResultTrace.attrPattern.matcher(attrName);
                if (m.matches())
                {
                    String newName = m.group(3);
                    if (!attrName.contains(AVATAR2ProVerif.ATTR_DELIM))
                        attrName = "Attacker" + AVATAR2ProVerif.ATTR_DELIM + attrName;
                    int n = 0;
                    for (String k: this.attackerNamesMap.keySet())
                    {
                        if (newName.equals(k.split(AVATAR2ProVerif.ATTR_DELIM, 3)[1]))
                        {

                            if (this.attackerNamesMap.get(k) == 0)
                            {
                                this.attackerNamesMap.remove(k);
                                this.attackerNamesMap.put(k, new Integer("1"));
                                n = 2;
                                break;
                            }
                            else
                            {
                                int nn = this.attackerNamesMap.get(k).intValue();
                                if (nn >= n)
                                    n = nn+1;
                            }
                        }
                    }

                    this.attackerNamesMap.put(attrName, new Integer(n));
                        
                    this.trace.add(new NewStep(attrName2));
                }

                return;
            }

        } catch (NumberFormatException e) {
            TraceManager.addDev("[ERROR] Parsing int");
        }

        TraceManager.addDev("[DEBUG] unmatch: " + str);
    }

    public void finalize()
    {
        this.finalizeStep();
    }
}
