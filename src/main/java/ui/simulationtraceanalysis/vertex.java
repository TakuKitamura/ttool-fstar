package ui.simulationtraceanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class vertex {

    protected static final int TYPE_CHANNEL = 11;
    protected static final int TYPE_FOR_EVER_LOOP = 1;
    protected static final int TYPE_STATIC_FOR_LOOP = 2;
    protected static final int TYPE_FOR_LOOP = 3;
    protected static final int TYPE_START = 4;
    protected static final int TYPE_END = 5;
    protected static final int TYPE_CHOICE = 6;
    protected static final int TYPE_SEQ = 7;
    protected static final int TYPE_UNORDER_SEQ = 8;
    protected static final int TYPE_TRANSACTION = 9;
    protected static final int TYPE_CTRL = 10;

    private String name;
    private int id; // identifier

    private Boolean skipVertex = false; // to skip vertex in case value =0

    private List<String> label = new ArrayList<String>();// will be used for store data taint
    private int type; // To know the if the vertex is a for lopp, data channel
    private int taintFixedNumber; // the number of times the taint should be considered

    private int sampleNumber; // the number of samples to write or read
    private int virtualLengthAdded; // the number of samples to write or read

    private HashMap<String, Integer> taintConsideredNumber = new HashMap<String, Integer>();; // the number of times the taint should be considered

    private HashMap<String, Integer> maxTaintFixedNumber = new HashMap<String, Integer>();

    public vertex(String name, int id) {
        this.name = name;
        this.id = id;
        this.label = this.getLabel();
        this.sampleNumber = 0;
        this.virtualLengthAdded = 0;

    }

    public HashMap<String, Integer> getMaxTaintFixedNumber() {
        return maxTaintFixedNumber;
    }

    public void setMaxTaintFixedNumber(HashMap<String, Integer> maxTaintFixedNumber) {
        this.maxTaintFixedNumber = maxTaintFixedNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
        return (o instanceof vertex) && (toString().equals(o.toString()));
    }

    public List<String> getLabel() {
        return this.label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public void addLabel(String l) {
        this.label.add(l);

        this.getTaintConsideredNumber().put(l, 0);
    }

    public String getLastLabel() {

        for (int i = 0; i < this.getLabel().size(); i++) {
            if (this.getMaxTaintFixedNumber().get(this.label.get(i)) == 0) {

                return this.label.get(i);

            }

        }

        return this.label.get(this.label.size() - 1);

    }

    public int getTaintFixedNumber() {
        return taintFixedNumber;
    }

    public void setTaintFixedNumber(int taintFixedNumber) {
        this.taintFixedNumber = taintFixedNumber;
    }

    public HashMap<String, Integer> getTaintConsideredNumber() {
        return taintConsideredNumber;
    }

    public void setTaintConsideredNumber(HashMap<String, Integer> taintConsideredNumber) {
        this.taintConsideredNumber = taintConsideredNumber;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public int getVirtualLengthAdded() {
        return virtualLengthAdded;
    }

    public void setVirtualLengthAdded(int virtualLengthAdded) {
        this.virtualLengthAdded = virtualLengthAdded;
    }

    public Boolean getSkipVertex() {
        return skipVertex;
    }

    public void setSkipVertex(Boolean skipVertex) {
        this.skipVertex = skipVertex;
    }

    public int getId() {
        return id;
    }

}