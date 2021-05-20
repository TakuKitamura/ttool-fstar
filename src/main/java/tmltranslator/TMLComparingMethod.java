package tmltranslator;

import java.util.*;

public class TMLComparingMethod {
    TMLComparingMethod() {
    }

    public boolean isOncommondesListEquals(List<HwCommunicationNode> list1, List<HwCommunicationNode> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(HwCommunicationNode::getName));
        Collections.sort(list2, Comparator.comparing(HwCommunicationNode::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isMappedcommeltsListEquals(List<TMLElement> list1, List<TMLElement> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLElement::getID));
        Collections.sort(list2, Comparator.comparing(TMLElement::getID));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isTasksListEquals(List<TMLTask> list1, List<TMLTask> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLTask::getName));
        Collections.sort(list2, Comparator.comparing(TMLTask::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isOnExecutionNodeListEquals(List<HwExecutionNode> list1, List<HwExecutionNode> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(HwExecutionNode::getName));
        Collections.sort(list2, Comparator.comparing(HwExecutionNode::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isListOfStringArrayEquals(List<String[]> list1, List<String[]> list2) {

        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, (x1, x2) -> {
            if (x1.length > 0 && x2.length > 0) {
                return x2[0].compareTo(x1[0]);
            }
            if (x1.length > 0) {
                return 1;
            }
            if (x2.length > 0) {
                return -1;
            }
            return x2.length - x1.length;
        });

        Collections.sort(list2, (x1, x2) -> {
            if (x1.length > 0 && x2.length > 0) {
                return x2[0].compareTo(x1[0]);
            }
            if (x1.length > 0) {
                return 1;
            }
            if (x2.length > 0) {
                return -1;
            }
            return x2.length - x1.length;
        });

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = Arrays.equals(list1.get(i), list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isSecurityPatternMapEquals(Map<SecurityPattern, List<HwMemory>> map1,
            Map<SecurityPattern, List<HwMemory>> map2) {

        if (map1 == null && map2 == null) {
            return true;
        }
        // Only one of them is null
        else if (map1 == null || map1 == null) {
            return false;
        } else if (map1.size() != map2.size()) {
            return false;
        }

        boolean test;
        for (SecurityPattern sp : map1.keySet()) {
            test = isHwMemoryListEquals(map1.get(sp), map2.get(sp));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isHwMemoryListEquals(List<HwMemory> list1, List<HwMemory> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(HwMemory::getName));
        Collections.sort(list2, Comparator.comparing(HwMemory::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isTMLActivityEltListEquals(List<TMLActivityElement> list1, List<TMLActivityElement> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLActivityElement::getName));
        Collections.sort(list2, Comparator.comparing(TMLActivityElement::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isHwNodeListEquals(List<HwNode> list1, List<HwNode> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(HwNode::getName));
        Collections.sort(list2, Comparator.comparing(HwNode::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isHwlinkListEquals(List<HwLink> list1, List<HwLink> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(HwLink::getName));
        Collections.sort(list2, Comparator.comparing(HwLink::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isPortListEquals(List<TMLPort> list1, List<TMLPort> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        // Only one of them is null
        else if (list1 == null || list2 == null) {
            return false;
        } else if (list1.size() != list2.size()) {
            return false;
        }

        // copying to avoid rearranging original lists
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);

        Collections.sort(list1, Comparator.comparing(TMLPort::getName));
        Collections.sort(list2, Comparator.comparing(TMLPort::getName));

        boolean test;

        for (int i = 0; i < list1.size(); i++) {
            test = list1.get(i).equalSpec(list2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isTMLChannelSetEquals(Set<TMLChannel> channelSet1, Set<TMLChannel> channelSet2) {
        if (channelSet1 == null && channelSet2 == null)
            return true;
        if (channelSet1 == null || channelSet2 == null)
            return false;

        if (channelSet1.size() != channelSet2.size())
            return false;

        List<TMLChannel> channels1 = new ArrayList<>(channelSet1);
        List<TMLChannel> channels2 = new ArrayList<>(channelSet2);

        Collections.sort(channels1, Comparator.comparing(TMLChannel::getName));
        Collections.sort(channels2, Comparator.comparing(TMLChannel::getName));

        boolean test;

        for (int i = 0; i < channels1.size(); i++) {
            test = channels1.get(i).equalSpec(channels2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

    public boolean isTMLEventSetEquals(Set<TMLEvent> eventSet1, Set<TMLEvent> eventSet2) {
        if (eventSet1 == null && eventSet2 == null)
            return true;
        if (eventSet1 == null || eventSet2 == null)
            return false;

        if (eventSet1.size() != eventSet2.size())
            return false;

        List<TMLEvent> events1 = new ArrayList<>(eventSet1);
        List<TMLEvent> events2 = new ArrayList<>(eventSet2);

        Collections.sort(events1, Comparator.comparing(TMLEvent::getName));
        Collections.sort(events2, Comparator.comparing(TMLEvent::getName));

        boolean test;

        for (int i = 0; i < events1.size(); i++) {
            test = events1.get(i).equalSpec(events2.get(i));
            if (!test)
                return false;
        }

        return true;
    }

}
