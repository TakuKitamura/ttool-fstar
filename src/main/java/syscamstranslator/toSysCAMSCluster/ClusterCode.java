/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * Daniela Genius, Lip6, UMR 7606 
 * 
 * ludovic.apvrille AT enst.fr
 * daniela.genius@lip6.fr
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

package syscamstranslator.toSysCAMSCluster;

import java.util.LinkedList;

import syscamstranslator.*;

/**
 * Class ClusterCode Principal code of a cluster component that wraps all AMS
 * components. Creation: 30/07/2018
 * 
 * @version 1.0 30/07/2018
 * @author Rodrigo CORTES PORTO
 */

public class ClusterCode {
  static private String corpsCluster;
  private final static String CR = "\n";
  private final static String CR2 = "\n\n";

  ClusterCode() {
  }

  public static String getClusterCode(SysCAMSTCluster cluster, LinkedList<SysCAMSTConnector> connectors) {
    int nb_con = 0;
    int nb_block = 0;

    LinkedList<String> names = new LinkedList<String>();

    if (cluster != null) {
      LinkedList<SysCAMSTBlockTDF> tdf = cluster.getBlockTDF();
      LinkedList<SysCAMSTBlockDE> de = cluster.getBlockDE();
      LinkedList<SysCAMSTClock> clock = cluster.getClock();
      corpsCluster = "";
      for (SysCAMSTClock t : clock) {

        String unitString = "SC_SEC";
        String unitStartTimeString = "SC_SEC";

        if (t.getUnit().equals("s"))
          unitString = "SC_SEC";
        if (t.getUnitStartTime().equals("s"))
          unitStartTimeString = "SC_SEC";
        if (t.getUnit().equals("ms"))
          unitString = "SC_MS";
        if (t.getUnitStartTime().equals("ms"))
          unitStartTimeString = "SC_MS";
        if (t.getUnit().equals("\u03BCs"))
          unitString = "SC_US";
        if (t.getUnitStartTime().equals("\u03BCs"))
          unitStartTimeString = "SC_US";
        if (t.getUnit().equals("ns"))
          unitString = "SC_NS";
        if (t.getUnitStartTime().equals("ns"))
          unitStartTimeString = "SC_NS";
        corpsCluster += "\t  sc_clock " + t.getName() + " (\"" + t.getName() + "\"," + t.getFrequency() + ","
            + unitString + "," + t.getDutyCycle() + "," + t.getStartTime() + "," + unitStartTimeString + ","
            + t.getPosFirst() + ");" + CR;
      }

      corpsCluster += "template <typename vci_param>" + CR + "class " + cluster.getClusterName()
          + " : public sc_core::sc_module { " + CR;

      // corpsCluster = corpsCluster + "using namespace sc_core;"+CR+
      // "using namespace sca_util;"+CR;

      corpsCluster = corpsCluster + CR + "\t// Instantiate cluster's modules." + CR;

      for (SysCAMSTBlockTDF t : tdf) {
        if (!t.getListTypedef().isEmpty()) {
          for (int i = 0; i < t.getListTypedef().getSize(); i++) {
            String select = t.getListTypedef().get(i);
            String[] split = select.split(" : ");
            corpsCluster = corpsCluster + "\ttypedef " + split[1] + "<" + t.getValueTemplate() + "> " + split[0] + ";"
                + CR;
          }
        }
      }

      for (SysCAMSTBlockDE t : de) {
        if (!t.getListTypedef().isEmpty()) {
          for (int i = 0; i < t.getListTypedef().getSize(); i++) {
            String select = t.getListTypedef().get(i);
            String[] split = select.split(" : ");
            corpsCluster = corpsCluster + "\ttypedef " + split[1] + "<" + t.getValueTemplate() + "> " + split[0] + ";"
                + CR;
          }
        }
      }

      corpsCluster = corpsCluster + CR + "\t// Declare signals to interconnect." + CR;

      for (int i = 0; i < connectors.size(); i++) {
        nb_con = i;
        if (!((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
            && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockGPIO2VCI() != null)
            || (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockGPIO2VCI() != null))) {
          if ((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortTDF
              && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortTDF)
              || (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortTDF
                  && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortTDF)) {
            int arity = 1;
            // Have to make sure that port is TDF
            if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortTDF) {
              SysCAMSTPortTDF pt = (SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent();
              arity = pt.getArity();
            }
            if (arity > 1) {

              if (connectors.get(i).getName().equals("")) {
                corpsCluster = corpsCluster + "\tsca_tdf::sca_signal<"
                    + ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getTDFType() + "> " + "sig_"
                    + nb_con + "[" + arity + "];" + CR;
                names.add("sig_" + nb_con);
              } else {
                corpsCluster = corpsCluster + "\tsca_tdf::sca_signal<"
                    + ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getTDFType() + "> "
                    + connectors.get(i).getName() + "[" + arity + "];" + CR;
                names.add(connectors.get(i).getName());
              }

            }

            else {
              if (connectors.get(i).getName().equals("")) {
                corpsCluster = corpsCluster + "\tsca_tdf::sca_signal<"
                    + ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getTDFType() + "> " + "sig_"
                    + nb_con + ";" + CR;
                names.add("sig_" + nb_con);
              } else {
                corpsCluster = corpsCluster + "\tsca_tdf::sca_signal<"
                    + ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getTDFType() + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());
              }
            }
          } else if ((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortConverter
              && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE)) {
            if (connectors.get(i).getName().equals("")) {
              corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                  + ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getConvType() + "> " + "sig_"
                  + nb_con + ";" + CR;
              names.add("sig_" + nb_con);
            } else {
              if (((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getNbits() == 0) {
                corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                    + ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getConvType() + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());
              } else {

                corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                    + ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getConvType() + "<"
                    + ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getNbits() + "> " + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());

              }
            }
          } else if ((connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortConverter
              && connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE)) {
            if (connectors.get(i).getName().equals("")) {
              corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                  + ((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getConvType() + "> " + "sig_"
                  + nb_con + ";" + CR;
              names.add("sig_" + nb_con);
            } else {
              if (((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getNbits() == 0) {
                corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                    + ((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getConvType() + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());
              } else {

                corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                    + ((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getConvType() + "<"
                    + ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getNbits() + "> " + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());

              }
            }

          } else if ((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
              && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE)
              || (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                  && connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE)) {
            if (connectors.get(i).getName().equals("")) {
              corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                  + ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getDEType() + "> " + "sig_" + nb_con
                  + ";" + CR;
              names.add("sig_" + nb_con);
            } else {
              if (((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getNbits() == 0) {

                corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                    + ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getDEType() + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());
              } else {

                corpsCluster = corpsCluster + "\tsc_core::sc_signal<"
                    + ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getDEType() + "<"
                    + ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getNbits() + "> " + "> "
                    + connectors.get(i).getName() + ";" + CR;
                names.add(connectors.get(i).getName());
              }

            }
          }

        } else {
          names.add("gpio_sig" + nb_con);
        }
      }

      corpsCluster = corpsCluster + CR + "\t// Instantiate cluster's modules." + CR;
      for (SysCAMSTBlockTDF t : tdf) {
        corpsCluster = corpsCluster + "\t" + t.getName();
        if (!t.getListTypedef().isEmpty()) {
          corpsCluster += "<" + t.getValueTemplate() + ">";
        }
        corpsCluster += " " + t.getName() + "_" + nb_block + ";" + CR;
        nb_block++;
      }

      for (SysCAMSTBlockDE t : de) {
        corpsCluster = corpsCluster + "\t" + t.getName();
        if (!t.getListTypedef().isEmpty()) {
          corpsCluster += "<" + t.getValueTemplate() + ">";
        }
        corpsCluster += " " + t.getName() + "_" + nb_block + ";" + CR;
        nb_block++;
      }
      for (SysCAMSTClock t : clock) {
        corpsCluster = corpsCluster + "\t  sc_clock " + t.getName() + ";" + CR;
      }
      corpsCluster = corpsCluster + "public:" + CR;
      corpsCluster = corpsCluster + "\tsc_in< typename vci_param::data_t > in_ams;" + CR;
      corpsCluster = corpsCluster + "\tsc_out< typename vci_param::data_t > out_ams;" + CR2;

      nb_block = 0;
      corpsCluster = corpsCluster + "\tSC_CTOR(" + cluster.getClusterName() + ") :" + CR;

      for (SysCAMSTBlockTDF t : tdf) {
        corpsCluster = corpsCluster + "\t" + t.getName() + "_" + nb_block + "(\"" + t.getName() + "_" + nb_block
            + "\")," + CR;
        nb_block++;
      }
      for (SysCAMSTBlockDE t : de) {
        corpsCluster = corpsCluster + "\t" + t.getName() + "_" + nb_block + "(\"" + t.getName() + "_" + nb_block
            + "\")," + CR;
        nb_block++;
      }
      corpsCluster = corpsCluster + "\tin_ams(\"in_ams\")," + CR;
      corpsCluster = corpsCluster + "\tout_ams(\"out_ams\") {" + CR;

      nb_block = 0;
      for (SysCAMSTBlockTDF t : tdf) {
        // corpsCluster = corpsCluster + "\t" + t.getName() + " " + t.getName() + "_" +
        // nb_block + "(\"" + t.getName() + "_" + nb_block + "\");" + CR;

        LinkedList<SysCAMSTPortTDF> portTDF = t.getPortTDF();
        LinkedList<SysCAMSTPortConverter> portConv = t.getPortConverter();

        for (SysCAMSTPortTDF p : portTDF) {
          for (int i = 0; i < connectors.size(); i++) {
            if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortTDF
                && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortTDF) {
              if (((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                  && ((SysCAMSTPortTDF) connectors.get(i).get_p1().getComponent()).getBlockTDF().getName()
                      .equals(t.getName())) {
                corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                    + names.get(i) + ");" + CR;
              } else if (((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                  && ((SysCAMSTPortTDF) connectors.get(i).get_p2().getComponent()).getBlockTDF().getName()
                      .equals(t.getName())) {
                corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                    + names.get(i) + ");" + CR;
              }
            }
          }
        }

        for (SysCAMSTPortConverter p : portConv) {
          for (int i = 0; i < connectors.size(); i++) {
            nb_con = i;

            if (!((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
                && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockGPIO2VCI() != null)
                || (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                    && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockGPIO2VCI() != null))) {
              if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortConverter
                  && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE) {
                if (((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getBlockTDF().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                } else if (((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockDE().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                }
              } else if (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortConverter
                  && connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE) {
                if (((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getBlockTDF().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                } else if (((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockDE().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                }
              }
            } else {
              if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
                  && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockGPIO2VCI() != null) {
                if (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortConverter) {
                  if (((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                      && ((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getBlockTDF().getName()
                          .equals(t.getName())) {
                    corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName()
                        + "(in_ams);" + CR;
                  }
                }
              } else if (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                  && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockGPIO2VCI() != null) {
                if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortConverter) {
                  if (((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                      && ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getBlockTDF().getName()
                          .equals(t.getName())) {
                    corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName()
                        + "(out_ams);" + CR;
                  }
                }
              }
            }
          }
        }
        corpsCluster = corpsCluster + CR;
        nb_block++;
      }

      for (SysCAMSTBlockDE t : de) {
        // corpsCluster = corpsCluster + "\t" + t.getName() + " " + t.getName() + "_" +
        // nb_block + "(\"" + t.getName() + "_" + nb_block + "\");" + CR;
        if (t.getClockName() != "")
          corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + t.getClockName() + "("
              + t.getClockName() + ");" + CR;

        LinkedList<SysCAMSTPortDE> portDE = t.getPortDE();

        for (SysCAMSTPortDE p : portDE) {
          for (int i = 0; i < connectors.size(); i++) {
            if (!((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
                && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockGPIO2VCI() != null)
                || (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                    && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockGPIO2VCI() != null))) {
              if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
                  && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE) {
                if (((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockDE().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                } else if (((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockDE().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                }
              } else if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortConverter
                  && connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE) {
                if (((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortConverter) connectors.get(i).get_p1().getComponent()).getBlockTDF().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                } else if (((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockDE().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                }
              } else if (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortConverter
                  && connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE) {
                if (((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortConverter) connectors.get(i).get_p2().getComponent()).getBlockTDF().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                } else if (((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                    && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockDE().getName()
                        .equals(t.getName())) {
                  corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName() + "("
                      + names.get(i) + ");" + CR;
                }
              }
            } else {
              if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
                  && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockGPIO2VCI() != null) {
                if (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE) {
                  if (((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getName().equals(p.getName())
                      && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockDE().getName()
                          .equals(t.getName())) {
                    corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName()
                        + "(in_ams);" + CR;
                  }
                }
              } else if (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                  && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockGPIO2VCI() != null) {
                if (connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE) {
                  if (((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getName().equals(p.getName())
                      && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockDE().getName()
                          .equals(t.getName())) {
                    corpsCluster = corpsCluster + "\t\t" + t.getName() + "_" + nb_block + "." + p.getName()
                        + "(out_ams);" + CR;
                  }
                }
              }
            }
          }
        }
        corpsCluster = corpsCluster + CR;
        nb_block++;
      }

      corpsCluster = corpsCluster + "\t}" + CR2;

      corpsCluster = corpsCluster + "\t// Configure signal tracing." + CR;
      corpsCluster += "\tvoid trace_" + cluster.getClusterName() + "(sca_util::sca_trace_file* tf) {" + CR;
      for (int i = 0; i < connectors.size(); i++) {
        if (!((connectors.get(i).get_p1().getComponent() instanceof SysCAMSTPortDE
            && ((SysCAMSTPortDE) connectors.get(i).get_p1().getComponent()).getBlockGPIO2VCI() != null)
            || (connectors.get(i).get_p2().getComponent() instanceof SysCAMSTPortDE
                && ((SysCAMSTPortDE) connectors.get(i).get_p2().getComponent()).getBlockGPIO2VCI() != null))) {
          corpsCluster += "\t\tsca_trace(tf, " + names.get(i) + ", \"" + names.get(i) + "\");" + CR;
        }
      }
      corpsCluster += "\t}" + CR;

      corpsCluster = corpsCluster + "};" + CR2;
      corpsCluster = corpsCluster + "#endif " + CR;// DG
      // corpsCluster = corpsCluster + "#endif // " +
      // cluster.getClusterName().toUpperCase() + "_TDF_H"+ CR;//DG
    } else {
      corpsCluster = "";
    }
    return corpsCluster;
  }
}
