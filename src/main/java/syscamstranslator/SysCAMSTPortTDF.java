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

import elntranslator.ELNTCluster;
import elntranslator.ELNTModule;

/**
 * Class SysCAMSTPortTDF Parameters of a SystemC-AMS port TDF Creation:
 * 07/05/2018
 * 
 * @version 1.0 07/05/2018
 * @author Irina Kit Yan LEE
 */

public class SysCAMSTPortTDF extends SysCAMSTComponent {

    private String name;
    private double period;
    private String time;
    private int rate;
    private int delay;
    private int arity;
    private int origin;
    private String TDFType;
    private boolean recompute;

    private SysCAMSTBlockTDF blockTDF;
    private ELNTCluster cluster;
    private ELNTModule module;

    public SysCAMSTPortTDF(String _name, double _period, String _time, int _rate, int _delay, int _origin,
            int _arity, String _TDFType, SysCAMSTBlockTDF _blockTDF) {
        name = _name;
        period = _period;
        time = _time;
        rate = _rate;
        delay = _delay;
        arity= _arity;
        origin = _origin;
        TDFType = _TDFType;
        recompute = false;
        blockTDF = _blockTDF;
    }

    public SysCAMSTPortTDF(String _name, int _period, String _time, int _rate, int _delay, int _arity, int _origin, String _TDFType,
            ELNTCluster _cluster) {
        name = _name;
        period = _period;
        time = _time;
        rate = _rate;
        delay = _delay;
        arity = _arity;
        origin = _origin;
        TDFType = _TDFType;
        cluster = _cluster;
    }

    public SysCAMSTPortTDF(String _name, int _period, String _time, int _rate, int _delay, int _arity, int _origin, String _TDFType,
            ELNTModule _module) {
        name = _name;
        period = _period;
        time = _time;
        rate = _rate;
        delay = _delay;
        arity = _arity;
        origin = _origin;
        TDFType = _TDFType;
        module = _module;
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

    public int getRate() {
        return rate;
    }

    public void setRate(int _rate) {
        rate = _rate;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int _delay) {
        delay = _delay;
    }
    
    public int getArity() {
        return arity;
    }
    
    public void setArity(int _arity) {
        arity = _arity;
    }
    


    public boolean getRecompute() {
        return recompute;
    }

    public void setRecompute(boolean _recompute) {
        recompute = _recompute;
    }

    public int getOrigin() {
        return origin;
    }

    public String getTDFType() {
        return TDFType;
    }

    public SysCAMSTBlockTDF getBlockTDF() {
        return blockTDF;
    }

    public ELNTCluster getCluster() {
        return cluster;
    }

    public ELNTModule getModule() {
        return module;
    }
}
