package avatartranslator.modelchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import avatartranslator.AvatarBlock;
import avatartranslator.AvatarSpecification;
import avatartranslator.AvatarStateMachineElement;
import avatartranslator.AvatarTransition;

public class SpecificationActionLoop {
    private List<ArrayList<AvatarTransition>> internalLoops;
    private SafetyProperty[] reachabilities;
    private SafetyProperty[] properties;
    private AvatarStateMachineElement[] states;
    private int[] cover; // 0: non reachable; 1: reachable; 2: reachable with exit
    private boolean result;
    private boolean error;
    private int pointer;

    public SpecificationActionLoop(List<ArrayList<AvatarTransition>> paths) {
        // this.path = path;
        this.error = false;
        pointer = 0;
        internalLoops = paths;
    }

    public void init(AvatarSpecification spec) {
        Map<AvatarStateMachineElement, Set<AvatarTransition>> map = new HashMap<>();

        removeForLoops();

        if (internalLoops.size() == 0) {
            error = true;
            return;
        }

        for (List<AvatarTransition> list : internalLoops) {
            AvatarStateMachineElement state = list.get(list.size() - 1).getNext(0); // loop state
            for (AvatarTransition at : list) {
                if (map.containsKey(state)) {
                    map.get(state).add(at);
                } else {
                    Set<AvatarTransition> transitions = new HashSet<>();
                    transitions.add(at);
                    map.put(state, transitions);
                }
                state = at.getNext(0);
            }
        }

        reachabilities = new SafetyProperty[map.keySet().size()];
        properties = new SafetyProperty[map.keySet().size()];
        states = new AvatarStateMachineElement[map.keySet().size()];
        cover = new int[internalLoops.size()];

        AvatarBlock block = (AvatarBlock) internalLoops.get(0).get(0).getBlock();

        int i = 0;
        for (AvatarStateMachineElement el : map.keySet()) {
            Set<AvatarTransition> set = map.get(el);

            states[i] = el;

            SafetyProperty reachability = new SafetyProperty("E<> " + el.getName());
            reachability.analyzeProperty(block, spec);
            error |= reachability.hasError();
            reachabilities[i] = reachability;

            boolean guarded = false;
            StringBuilder subCondition = new StringBuilder("!(");
            for (AvatarTransition at : set) {
                if (at.isGuarded()) {
                    String guard = at.getGuard().toString().replaceAll("\\[", "").trim().replaceAll("\\]", "");
                    if (guarded) {
                        subCondition.append(" || " + guard);
                    } else {
                        subCondition.append(guard);
                    }
                    guarded = true;
                }
            }
            subCondition.append(")");
            if (guarded) {
                SafetyProperty property = new SafetyProperty("E<> " + el.getName() + " && " + subCondition.toString());
                property.analyzeProperty(block, spec);
                error |= property.hasError();
                properties[i] = property;
            }
            i++;
        }
    }

    public void initLeadsTo(AvatarSpecification spec) {
        Set<AvatarStateMachineElement> stateSet = new HashSet<>();

        removeForLoops();

        if (internalLoops.size() == 0) {
            error = true;
            return;
        }

        for (List<AvatarTransition> list : internalLoops) {
            for (AvatarTransition at : list) {
                stateSet.add(at.getNext(0));
            }
        }

        properties = new SafetyProperty[1];

        AvatarBlock block = (AvatarBlock) internalLoops.get(0).get(0).getBlock();

        int i = 0;
        StringBuilder formula = new StringBuilder();
        for (AvatarStateMachineElement el : stateSet) {
            if (i != 0) {
                formula.append("||");
            }
            formula.append(el.getName());
            i++;
        }

        SafetyProperty property = new SafetyProperty(formula.toString() + "-->!(" + formula.toString() + ")");
        property.analyzeProperty(block, spec);
        error |= property.hasError();
        properties[0] = property;
    }

    public boolean hasError() {
        return error;
    }

    public boolean hasProperty() {
        if (pointer < properties.length && reachabilities[pointer].result) {
            return properties[pointer] != null;
        }
        return false;
    }

    public SafetyProperty getReachability() {
        if (pointer < reachabilities.length) {
            return reachabilities[pointer];
        }
        return null;
    }

    public SafetyProperty getProperty() {
        if (pointer < properties.length && reachabilities[pointer].result) {
            return properties[pointer];
        }
        return null;
    }

    public SafetyProperty getPropertyLeadsTo() {
        return properties[0];
    }

    public boolean increasePointer() {
        if (pointer < states.length - 1) {
            pointer++;
            return true;
        }
        return false;
    }

    public boolean setCover() {
        int i = 0;

        if (reachabilities[pointer].result) {
            AvatarStateMachineElement state = states[pointer];
            for (List<AvatarTransition> list : internalLoops) {
                if (cover[i] != 2) {
                    for (AvatarTransition at : list) {
                        if (at.getNext(0) == state) {
                            if (properties[pointer] != null && properties[pointer].result) {
                                cover[i] = 2;
                            } else {
                                cover[i] = 1;
                            }
                            break;
                        }
                    }
                }
                i++;
            }
        }

        for (i = 0; i < internalLoops.size(); i++) {
            if (cover[i] != 2) {
                return false;
            }
        }
        return true;
    }

    public void setResult() {
        result = false;
        for (int i = 0; i < internalLoops.size(); i++) {
            if (cover[i] == 1) {
                result = true;
                break;
            }
        }
    }

    public void setResultLeadsTo() {
        result = !properties[0].result;
    }

    public boolean getResult() {
        return result;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        if (internalLoops == null) {
            return "";
        } else if (cover == null) {
            s.append("In block " + internalLoops.get(0).get(0).getBlock().getName() + " : ");
            boolean first = true;
            for (List<AvatarTransition> list : internalLoops) {
                if (!first) {
                    s.append(", or ");
                }
                first = false;
                s.append(list.get(list.size() - 1).getNext(0).getName());
                for (AvatarTransition at : list) {
                    s.append(" --> " + at.getNext(0).getName());
                }
            }
            s.append(" lead to a infinite internal loop\n");
            return s.toString();
        }

        int i = 0;
        for (List<AvatarTransition> list : internalLoops) {
            if (cover[i] == 1) {
                s.append("In block " + list.get(list.size() - 1).getBlock().getName() + " : "
                        + list.get(list.size() - 1).getNext(0).getName());
                for (AvatarTransition at : list) {
                    s.append(" --> " + at.getNext(0).getName());
                }
            }
            i++;
            s.append("\n");
        }

        return s.toString();
    }

    private void removeForLoops() {
        Iterator<ArrayList<AvatarTransition>> iter = internalLoops.iterator();
        while (iter.hasNext()) {
            List<AvatarTransition> list = iter.next();
            if (list.size() == 1 && list.get(0).getName().startsWith("Transition2ForRandom__")) {
                // remove random for cycle
                iter.remove();
            }
        }
    }

    // public static void findIntersectionSets(List<ArrayList<AvatarTransition>>
    // internalLoops) {
    // if (internalLoops == null || internalLoops.size() <= 1) {
    // return;
    // }
    //
    // internalLoops.sort((x1, x2) -> {return x2.size() - x1.size();});
    //
    // Set<AvatarStateMachineElement> states = new HashSet<>();
    // Set<AvatarStateMachineElement> intersection = new HashSet<>();
    //
    // for (AvatarTransition at : internalLoops.get(0)) {
    // states.add(at.getNext(0));
    // }
    //
    // int i = 0;
    // for (List<AvatarTransition> list : internalLoops) {
    // if (i == 0) {
    // i++;
    // continue;
    // }
    // for (AvatarTransition at : list) {
    // if (states.contains(at.getNext(0))) {
    // intersection.add(at.getNext(0));
    // }
    // }
    // if (intersection.size() != 0) {
    // states = intersection;
    // intersection = new HashSet<>();
    // }
    // }
    // }
}
