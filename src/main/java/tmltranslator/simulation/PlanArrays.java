package tmltranslator.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

public class PlanArrays {
  private List<String> onPathBehavior = new ArrayList<String>();
  private List<String> offPathBehavior = new ArrayList<String>();
  private List<String> offPathBehaviorCausingDelay = new ArrayList<String>();
  private List<String> mandatoryOptional = new ArrayList<String>();
  private static final String ZERO = "0";
  private static final String DEVICE_NAME = "Device Name";
  private String[] columnNames;
  private Object[][] dataDetailedByTask;

  public void fillArrays(DependencyGraphTranslator dgraph, int row, boolean firstTable, Boolean taint) {
    int maxTime = -1;
    int minTime = Integer.MAX_VALUE;
    int tmpEnd, tmpStart, length;
    Vector<String> deviceNames1 = new Vector<String>();
    if (firstTable) {
      for (SimulationTransaction st : dgraph.getRowDetailsTaks(row)) {
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        tmpEnd = Integer.parseInt(st.endTime);
        if (tmpEnd > maxTime) {
          maxTime = tmpEnd;
        }
        tmpStart = Integer.parseInt(st.startTime);
        if (tmpStart < minTime) {
          minTime = tmpStart;
        }
        String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
        if (!deviceNames1.contains(deviceNameandcore)) {
          deviceNames1.add(deviceNameandcore);
        }
      }
      for (SimulationTransaction st : dgraph.getMandatoryOptionalSimTTaks(row)) {
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        tmpEnd = Integer.parseInt(st.endTime);
        if (tmpEnd > maxTime) {
          maxTime = tmpEnd;
        }
        tmpStart = Integer.parseInt(st.startTime);
        if (tmpStart < minTime) {
          minTime = tmpStart;
        }
        String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
        if (!deviceNames1.contains(deviceNameandcore)) {
          deviceNames1.add(deviceNameandcore);
        }
      }
      for (SimulationTransaction st : dgraph.getRowDetailsByHW(row)) {
        tmpEnd = Integer.parseInt(st.endTime);
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        if (tmpEnd > maxTime) {
          maxTime = tmpEnd;
        }
        tmpStart = Integer.parseInt(st.startTime);
        if (tmpStart < minTime) {
          minTime = tmpStart;
        }
        String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
        if (!deviceNames1.contains(deviceNameandcore)) {
          deviceNames1.add(deviceNameandcore);
        }
      }
      int timeInterval = 0;
      if (maxTime > -1 && minTime < Integer.MAX_VALUE) {
        timeInterval = (maxTime - minTime);
      }
      columnNames = new String[timeInterval + 1];
      columnNames[0] = DEVICE_NAME;
      for (int i = 0; i < timeInterval; i++) {
        columnNames[i + 1] = Integer.toString(minTime + i);
      }
      dataDetailedByTask = new Object[deviceNames1.size()][timeInterval + 1];
      for (SimulationTransaction st : dgraph.getRowDetailsTaks(row)) {
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        for (String dName : deviceNames1) {
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (deviceNameandcore.equals(dName)) {
            length = Integer.parseInt(st.length);
            for (int i = 0; i < length; i++) {
              int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
              dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
              ;
              onPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
              if (!dgraph.getOnPath().contains(st)) {
                dgraph.getOnPath().add(st);
              }
            }
          }
        }
      }
      for (SimulationTransaction st : dgraph.getMandatoryOptionalSimTTaks(row)) {
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        for (String dName : deviceNames1) {
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (deviceNameandcore.equals(dName)) {
            length = Integer.parseInt(st.length);
            for (int i = 0; i < length; i++) {
              int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
              dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
              ;
              mandatoryOptional.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
              // if (!dgraph.getOnPath().contains(st)) {
              // dgraph.getOnPath().add(st);
              // }
            }
          }
        }
      }
      HashMap<String, ArrayList<ArrayList<Integer>>> delayTime = dgraph.getRowDelayDetailsByHW(row);
      for (SimulationTransaction st : dgraph.getRowDetailsByHW(row)) {
        int startTime = Integer.valueOf(st.startTime);
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        for (String dName : deviceNames1) {
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (deviceNameandcore.equals(dName)) {
            length = Integer.parseInt(st.length);
            for (int i = 0; i < length; i++) {
              int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
              dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
              ;
              boolean causeDelay = false;
              if (delayTime.containsKey(deviceNameandcore)) {
                for (Entry<String, ArrayList<ArrayList<Integer>>> entry : delayTime.entrySet()) {
                  if (entry.getKey().equals(deviceNameandcore)) {
                    ArrayList<ArrayList<Integer>> timeList = entry.getValue();
                    for (int j = 0; j < timeList.size(); j++) {
                      if (startTime >= timeList.get(j).get(0) && startTime <= timeList.get(j).get(1)) {
                        causeDelay = true;
                      }
                    }
                  }
                }
              }
              if (causeDelay) {
                offPathBehaviorCausingDelay.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
                if (!dgraph.getOffPathDelay().contains(st)) {
                  dgraph.getOffPathDelay().add(st);
                }
              } else {
                offPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
                if (!dgraph.getOffPath().contains(st)) {
                  dgraph.getOffPath().add(st);
                }
              }
            }
          }
        }
      }
      for (String dName : deviceNames1) {
        dataDetailedByTask[deviceNames1.indexOf(dName)][0] = dName;
        ;
      }
    } else {
      Vector<SimulationTransaction> minMaxTasksByRow;
      List<SimulationTransaction> minMaxHWByRowDetails;
      HashMap<String, ArrayList<ArrayList<Integer>>> delayTime;
      // min/max table row selected
      if (taint) {
        delayTime = dgraph.getTimeDelayedPerRowMinMaxTainted(row);
        minMaxTasksByRow = dgraph.getMinMaxTasksByRowTainted(row);
        minMaxHWByRowDetails = dgraph.getTaskMinMaxHWByRowDetailsTainted(row);
        for (SimulationTransaction st : minMaxTasksByRow) {
          tmpEnd = Integer.parseInt(st.endTime);
          if (st.coreNumber == null) {
            st.coreNumber = ZERO;
          }
          if (tmpEnd > maxTime) {
            maxTime = tmpEnd;
          }
          tmpStart = Integer.parseInt(st.startTime);
          if (tmpStart < minTime) {
            minTime = tmpStart;
          }
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (!deviceNames1.contains(deviceNameandcore)) {
            deviceNames1.add(deviceNameandcore);
          }
        }
        for (SimulationTransaction st : minMaxHWByRowDetails) {
          tmpEnd = Integer.parseInt(st.endTime);
          if (tmpEnd > maxTime) {
            maxTime = tmpEnd;
          }
          tmpStart = Integer.parseInt(st.startTime);
          if (tmpStart < minTime) {
            minTime = tmpStart;
          }
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (!deviceNames1.contains(deviceNameandcore)) {
            deviceNames1.add(deviceNameandcore);
          }
        }
      } else {
        delayTime = dgraph.getRowDelayDetailsByHWMinMax(row);
        minMaxTasksByRow = dgraph.getMinMaxTasksByRow(row);
        minMaxHWByRowDetails = dgraph.getTaskMinMaxHWByRowDetails(row);
        for (SimulationTransaction st : minMaxTasksByRow) {
          if (st.coreNumber == null) {
            st.coreNumber = ZERO;
          }
          tmpEnd = Integer.parseInt(st.endTime);
          if (tmpEnd > maxTime) {
            maxTime = tmpEnd;
          }
          tmpStart = Integer.parseInt(st.startTime);
          if (tmpStart < minTime) {
            minTime = tmpStart;
          }
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (!deviceNames1.contains(deviceNameandcore)) {
            deviceNames1.add(deviceNameandcore);
          }
        }
        for (SimulationTransaction st : minMaxHWByRowDetails) {
          if (st.coreNumber == null) {
            st.coreNumber = ZERO;
          }
          tmpEnd = Integer.parseInt(st.endTime);
          if (tmpEnd > maxTime) {
            maxTime = tmpEnd;
          }
          tmpStart = Integer.parseInt(st.startTime);
          if (tmpStart < minTime) {
            minTime = tmpStart;
          }
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (!deviceNames1.contains(deviceNameandcore)) {
            deviceNames1.add(deviceNameandcore);
          }
        }
      }
      int timeInterval = 0;
      if (maxTime > -1 && minTime < Integer.MAX_VALUE) {
        timeInterval = (maxTime - minTime);
      }
      columnNames = new String[timeInterval + 1];
      columnNames[0] = DEVICE_NAME;
      for (int i = 0; i < timeInterval; i++) {
        columnNames[i + 1] = Integer.toString(minTime + i);
      }
      dataDetailedByTask = new Object[deviceNames1.size()][timeInterval + 1];
      for (SimulationTransaction st : minMaxTasksByRow) {
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        for (String dName : deviceNames1) {
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (deviceNameandcore.equals(dName)) {
            length = Integer.parseInt(st.length);
            for (int i = 0; i < length; i++) {
              int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
              dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
              ;
              onPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
              if (!dgraph.getOnPath().contains(st)) {
                dgraph.getOnPath().add(st);
              }
            }
          }
        }
      }
      for (SimulationTransaction st : minMaxHWByRowDetails) {
        int startTime = Integer.valueOf(st.startTime);
        if (st.coreNumber == null) {
          st.coreNumber = ZERO;
        }
        for (String dName : deviceNames1) {
          String deviceNameandcore = st.deviceName + "_" + st.coreNumber;
          if (deviceNameandcore.equals(dName)) {
            length = Integer.parseInt(st.length);
            for (int i = 0; i < length; i++) {
              int columnnmber = Integer.parseInt(st.endTime) - minTime - i;
              dataDetailedByTask[deviceNames1.indexOf(dName)][columnnmber] = dgraph.getNameIDTaskList().get(st.id);
              ;
              boolean causeDelay = false;
              if (delayTime.containsKey(deviceNameandcore)) {
                for (Entry<String, ArrayList<ArrayList<Integer>>> entry : delayTime.entrySet()) {
                  if (entry.getKey().equals(deviceNameandcore)) {
                    ArrayList<ArrayList<Integer>> timeList = entry.getValue();
                    for (int j = 0; j < timeList.size(); j++) {
                      if (startTime >= timeList.get(j).get(0) && startTime <= timeList.get(j).get(1)) {
                        causeDelay = true;
                      }
                    }
                  }
                }
              }
              if (causeDelay) {
                offPathBehaviorCausingDelay.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
                if (!dgraph.getOffPathDelay().contains(st)) {
                  dgraph.getOffPathDelay().add(st);
                }
              } else {
                offPathBehavior.add(dgraph.getNameIDTaskList().get(st.id) + columnNames[columnnmber]);
                if (!dgraph.getOffPath().contains(st)) {
                  dgraph.getOffPath().add(st);
                }
              }
            }
          }
        }
      }
      for (String dName : deviceNames1) {
        dataDetailedByTask[deviceNames1.indexOf(dName)][0] = dName;
        ;
      }
    }
    return;
  }

  public Object[][] getDataDetailedByTask() {
    return dataDetailedByTask;
  }

  public List<String> getOnPathBehavior() {
    return onPathBehavior;
  }

  public List<String> getOffPathBehavior() {
    return offPathBehavior;
  }

  public List<String> getOffPathBehaviorCausingDelay() {
    return offPathBehaviorCausingDelay;
  }

  public String[] getColumnNames() {
    return columnNames;
  }

  public List<String> getMandatoryOptional() {
    return mandatoryOptional;
  }

}
