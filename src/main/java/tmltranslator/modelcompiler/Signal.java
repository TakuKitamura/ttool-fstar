/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 * 
 * ludovic.apvrille AT enst.fr
 * andrea.enrici AT enstr.fr
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

package tmltranslator.modelcompiler;

import tmltranslator.TMLChannel;
import tmltranslator.TMLEvent;

/**
 * Class Signal Creation: 11/02/2014
 * 
 * @version 1.0 11/02/2014
 * @author Andrea ENRICI
 */
public class Signal implements CCodeGenConstants {

    public static final String DECLARATION = "struct SIG_TYPE	{\n\tbool f;\n\tvoid *pBuff;\n};\n\ntypedef struct SIG_TYPE SIG_TYPE;\nextern SIG_TYPE sig[];\n\n";
    public static final String USERTODO = "/* USER TODO: signal */";

    // public String CR = "\n";
    // public String SC = ";";

    // private boolean status = false;
    // private Buffer buffPointer = null;
    private String name;
    private TMLChannel channel;
    private TMLEvent event;

    public Signal(TMLChannel _ch) {
        channel = _ch;
        if (_ch.isBasicChannel()) {
            name = _ch.getOriginPort().getName(); // return the name of the source port of the channel
        } else if (_ch.isAForkChannel()) {
            name = _ch.getOriginPorts().get(0).getName(); // return the name of the source port of the channel
        } else if (_ch.isAJoinChannel()) {
            name = "SIGNAL__" + _ch.getName().split("__")[1] + "__" + _ch.getName().split("__")[2] + "__"
                    + _ch.getName().split("__")[3];
        }
    }

    /*
     * public Signal( TMLChannel _ch, TMLEvent _evt ) { channel = _ch; event = _evt;
     * if( _ch.isBasicChannel() ) { name = _ch.getOriginPort().getName(); //return
     * the name of the source port of the channel //"SIGNAL__" +
     * _ch.getName().split("__")[1] + "__" + _ch.getName().split("__")[3]; } else
     * if( _ch.isAForkChannel() ) { name = _ch.getOriginPorts().get(0).getName();
     * //return the name of the source port of the channel //"SIGNAL__" +
     * _ch.getName().split("__")[1] + "__" + _ch.getName().split("__")[2] + "__" +
     * _ch.getName().split("__")[3]; } else if( _ch.isAJoinChannel() ) { name =
     * "SIGNAL__" + _ch.getName().split("__")[1] + "__" +
     * _ch.getName().split("__")[2] + "__" + _ch.getName().split("__")[3]; } }
     */

    @Override
    public String toString() {
        String s = "";
        s += "SIGNAL " + name + CR + channel.toString();

        return s;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        name = _name;
    }

    public TMLChannel getTMLChannel() {
        return channel;
    }

    public TMLEvent getTMLEvent() {
        return event;
    }

    public boolean isBasicSignal() {
        return channel.isBasicChannel();
    }

    public boolean isAForkSignal() {
        return channel.isAForkChannel();
    }

    public boolean isAJoinSignal() {
        return channel.isAJoinChannel();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Signal)) {
            return false;
        } else {
            Signal sig = (Signal) o;
            return sig.getName().equals(this.getName());
        }
    }
} // End of class
