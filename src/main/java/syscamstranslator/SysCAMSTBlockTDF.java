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

package syscamstranslator;

import java.util.LinkedList;

import javax.swing.DefaultListModel;

/**
 * Class SysCAMSTBlockTDF
 * Parameters of a SystemC-AMS block TDF
 * Creation: 19/05/2018
 * @version 1.0 19/05/2018
 * @author Irina Kit Yan LEE
 * @version 1.1 06/08/2018
 * @author Rodrigo CORTES PORTO
*/

public class SysCAMSTBlockTDF extends SysCAMSTComponent {
	private String name;
	private double period;
	private String time;
	private String processCode;
	private DefaultListModel<String> listStruct;
	private String nameTemplate;
	private String typeTemplate;
	private DefaultListModel<String> listTypedef;
	
	private SysCAMSTCluster cluster;
	
	private LinkedList<SysCAMSTPortTDF> portTDF;
	private LinkedList<SysCAMSTPortConverter> portConverter;
    private SysCAMSTPortConverter localPortConverter;
    private int n;
    private boolean isTimestepPropagated;
	
	public SysCAMSTBlockTDF(String _name, double _period, String _time, String _processCode, DefaultListModel<String> _listStruct, String _nameTemplate, String _typeTemplate, DefaultListModel<String> _listTypedef, SysCAMSTCluster _cluster) {
		name = _name;
		period = _period;
		time = _time;
		processCode = _processCode;
		listStruct = _listStruct;
		nameTemplate = _nameTemplate;
		typeTemplate = _typeTemplate;
		listTypedef = _listTypedef;
		cluster = _cluster;
		portTDF = new LinkedList<SysCAMSTPortTDF>();
		portConverter = new LinkedList<SysCAMSTPortConverter>();
        n = 0;
        isTimestepPropagated = false;
	}

	public String getName() {
		return name;
	}
	
	public double getPeriod() {
		return period;
	}
    
    public void setPeriod(double _period) {
		period = _period;
	}
	
	public String getTime() {
		return time;
	}

	public String getProcessCode() {
		return processCode;
	}

	public DefaultListModel<String> getListStruct() {
		return listStruct;
	}

	public String getNameTemplate() {
		return nameTemplate;
	}

	public String getTypeTemplate() {
		return typeTemplate;
	}

	public DefaultListModel<String> getListTypedef() {
		return listTypedef;
	}

	public SysCAMSTCluster getCluster() {
		return cluster;
	}

	public LinkedList<SysCAMSTPortTDF> getPortTDF(){
		return portTDF;
	}

	public void addPortTDF(SysCAMSTPortTDF tdf){
		portTDF.add(tdf);
	}

	public LinkedList<SysCAMSTPortConverter> getPortConverter(){
		return portConverter;
	}

	public void addPortConverter(SysCAMSTPortConverter converter){
		portConverter.add(converter);
	}
    
    public boolean getIsTimestepPropagated() {
        return isTimestepPropagated;
    }
    
    public void setIsTimestepPropagated() {
        isTimestepPropagated = true;
    }

    public void syncTDFBlockDEBlock(double[] time_prev) throws SysCAMSValidateException {
        double tp;
        try{
            for(int i = 0; i < portConverter.size(); i++) {
                localPortConverter = portConverter.get(i);
                if(localPortConverter.getOrigin() == 0) { //Input
                    check_causality_in(time_prev);
                } else if (localPortConverter.getOrigin() == 1) { //Output
                    check_causality_out(time_prev);
                }
            }
            //Increase number of times block has been executed
            n++;
        } catch (SysCAMSValidateException se){
             throw new SysCAMSValidateException(se.getMessage());
        }
    }
    
    private void check_causality_in(double[] time_prev_max) throws SysCAMSValidateException {
        double time_now_min_tdf, time_now_max_tdf, time_tmp_tdf, time_tmp_de,
                time_now_min_de, time_now_max_de;
        double tm = 0.0;
        double tp = 0.0;
        int r = 1;
        int d = 0;
        int k = 1;
        if(period > 0)
            tm = period;
        if(localPortConverter.getPeriod() > 0)
            tp = localPortConverter.getPeriod();
        if(localPortConverter.getRate() > 0)
            r = localPortConverter.getRate();
        if(localPortConverter.getDelay() > 0)
            d = localPortConverter.getDelay();

        time_now_min_tdf = (n*tm)+((k-1)*tp);
        time_now_max_tdf = (n*tm)+((k-1)*tp);
        time_now_min_de = (n*tm)+((k-1)*tp)-(d*tp);
        time_now_max_de = (n*tm)+((k-1)*tp)-(d*tp);
        
        for (k = 1; k <= r; k++) {
            time_tmp_tdf = (n*tm)+((k-1)*tp);
            time_tmp_de = (n*tm)+((k-1)*tp)-(d*tp);
            System.out.println("tmstmp_in_tdf: " + time_tmp_tdf);
            System.out.println("tmstmp_in_de: " + time_tmp_de);
            time_now_min_tdf = Math.min(time_tmp_tdf, time_now_min_tdf);
            time_now_max_tdf = Math.max(time_tmp_tdf, time_now_max_tdf);
            time_now_min_de = Math.min(time_tmp_de, time_now_min_de);
            time_now_max_de = Math.max(time_tmp_de, time_now_max_de);
            System.out.println("time_now_min_de: " + time_now_min_de);
            System.out.println("time_now_max_de: " + time_now_max_de);
            System.out.println("time_now_min_tdf: " + time_now_min_tdf);
            System.out.println("time_now_max_tdf: " + time_now_max_tdf);
        }
        
        System.out.println("time_prev_max_out: " + time_prev_max[1]);
        if(time_now_min_tdf < time_prev_max[1]) {
            localPortConverter.setDelay((int)Math.ceil((time_prev_max[1]-time_now_min_de)/tp) + d);
            localPortConverter.setRecompute(true);
            throw new SysCAMSValidateException("Timestamp of previous write port executed module is: " + time_prev_max[1]
                 + " and current timestamp is: " + time_now_min_tdf + ".\n"
                 + "Suggested delay in port \"" + localPortConverter.getName() + "\": " + (Math.ceil((time_prev_max[1]-time_now_min_tdf)/tp) + d));
        }
        time_prev_max[0] = Double.valueOf(Math.max(time_prev_max[0],time_now_max_de));
        System.out.println("New time_prev_max_in: " + time_prev_max[0]);
    }
    
    private void check_causality_out(double[] time_prev_max) throws SysCAMSValidateException {
        double time_now_min_tdf, time_now_max_tdf, time_tmp_tdf, time_tmp_de,
                time_now_min_de, time_now_max_de;
        double tm = 0.0;
        double tp = 0.0;
        int r = 1;
        int d = 0;
        int k = 1;
        if(period > 0)
            tm = period;
        if(localPortConverter.getPeriod() > 0)
            tp = localPortConverter.getPeriod();
        if(localPortConverter.getRate() > 0)
            r = localPortConverter.getRate();
        if(localPortConverter.getDelay() > 0)
            d = localPortConverter.getDelay();

        time_now_min_tdf = (n*tm)+((k-1)*tp);
        time_now_max_tdf = (n*tm)+((k-1)*tp);
        time_now_min_de = (n*tm)+((k-1)*tp)+(d*tp);
        time_now_max_de = (n*tm)+((k-1)*tp)+(d*tp);
        
        for (k = 1; k <= r; k++) {
            time_tmp_tdf = (n*tm)+((k-1)*tp);
            time_tmp_de = (n*tm)+((k-1)*tp)+(d*tp);;
            System.out.println("tmstmp_out_tdf: " + time_tmp_tdf);
            System.out.println("tmstmp_out_de: " + time_tmp_de);
            time_now_min_tdf = Math.min(time_tmp_tdf, time_now_min_tdf);
            time_now_max_tdf = Math.max(time_tmp_tdf, time_now_max_tdf);
            time_now_min_de = Math.min(time_tmp_de, time_now_min_de);
            time_now_max_de = Math.max(time_tmp_de, time_now_max_de);
            System.out.println("time_now_min_de: " + time_now_min_de);
            System.out.println("time_now_max_de: " + time_now_max_de);
            System.out.println("time_now_min_tdf: " + time_now_min_tdf);
            System.out.println("time_now_max_tdf: " + time_now_max_tdf);
        }
        
        System.out.println("time_prev_max_in: " + time_prev_max[0]);
        if(time_now_min_de < time_prev_max[0]) {
            localPortConverter.setDelay((int)Math.ceil((time_prev_max[0]-time_now_min_de)/tp) + d);
            localPortConverter.setRecompute(true);
            throw new SysCAMSValidateException("Timestamp of previous read port executed module is: " + time_prev_max[0]
                 + " and current timestamp is: " + time_now_min_de + ".\n"
                 + "Suggested delay in port " + localPortConverter.getName() + ": " + (Math.ceil((time_prev_max[0]-time_now_min_de)/tp) + d));
        }
        time_prev_max[1] = Double.valueOf(Math.max(time_prev_max[1],time_now_max_tdf));
        System.out.println("New time_prev_max_out: " + time_prev_max[1]);
    }
    
    public void setN(int _n) {
        n = _n;
    }

}
