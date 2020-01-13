/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici
 *
 * ludovic.apvrille AT telecom-paristech.fr
 * andrea.enrici AT telecom-paristech.fr
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

package ui.tmldd;

import ui.SwallowTGComponent;
import ui.TDiagramPanel;
import ui.TGComponent;
import ui.TGComponentManager;
import myutil.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class TMLArchiCommunicationNode
 * Node. To be used in TML architecture diagrams.
 * Creation: 23/11/2007
 * @version 1.1 30/05/2014
 * @author Ludovic APVRILLE, Andrea ENRICI
 */
public abstract class TMLArchiCommunicationNode extends TMLArchiNode implements SwallowTGComponent {

    public TMLArchiCommunicationNode(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father, TDiagramPanel _tdp)  {
        super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
    }

    @Override
    public boolean acceptSwallowedTGComponent(TGComponent tgc) {
        return ( (tgc instanceof TMLArchiCommunicationArtifact) || (tgc instanceof TMLArchiEventArtifact) );
    }

    @Override
    public boolean addSwallowedTGComponent( TGComponent tgc, int x, int y )     {
        if( tgc instanceof TMLArchiCommunicationArtifact )      {
            // Make it an internal component
            // It's one of my son
            //Set its coordinates
            tgc.setFather(this);
            tgc.setDrawingZone(true);
            tgc.resizeWithFather();
 
            //add it
            addInternalComponent( tgc, 0 );
            return true;
        }
        else    {
        	TraceManager.addDev("Testing swallow " + tgc);
            
        	if( tgc instanceof TMLArchiEventArtifact )  {
        		TraceManager.addDev("Testing swallow archievent ok " + tgc);
                tgc.setFather( this );
                tgc.setDrawingZone( true );
                tgc.resizeWithFather();
                addInternalComponent( tgc, 0 );
                return true;
            }
            return false;
        }
    }

    @Override
    public void removeSwallowedTGComponent(TGComponent tgc) {
        removeInternalComponent(tgc);
    }

    public List<TMLArchiCommunicationArtifact> getChannelArtifactList() {
        List<TMLArchiCommunicationArtifact> v = new ArrayList<TMLArchiCommunicationArtifact>();

        for( int i = 0; i < nbInternalTGComponent; i++ ) {
            if( tgcomponent[i] instanceof TMLArchiCommunicationArtifact )       {
                v.add( (TMLArchiCommunicationArtifact)( tgcomponent[i]) );
            }
        }
        
        return v;
    }

    public List<TMLArchiKey> getKeyList(){
        List<TMLArchiKey> v = new ArrayList<TMLArchiKey>();
        
        for( int i = 0; i < nbInternalTGComponent; i++ ) {
            if( tgcomponent[i] instanceof TMLArchiKey )       {
                v.add( (TMLArchiKey)( tgcomponent[i]) );
            }
        }
        
        return v;
    }

    public List<TMLArchiEventArtifact> getEventArtifactList() {
        List<TMLArchiEventArtifact> v = new ArrayList<TMLArchiEventArtifact>();

        for( int i = 0; i < nbInternalTGComponent; i++ )        {
            if( tgcomponent[i] instanceof TMLArchiEventArtifact )       {
                v.add( (TMLArchiEventArtifact)(tgcomponent[i]) );
            }
        }
        
        return v;
    }

    @Override
    public void hasBeenResized() {
        for( int i = 0; i < nbInternalTGComponent; i++ )        {
            if( tgcomponent[i] instanceof TMLArchiCommunicationArtifact ) {
                tgcomponent[i].resizeWithFather();
            }
            else        {
                if( tgcomponent[i] instanceof TMLArchiEventArtifact )   {
                    tgcomponent[i].resizeWithFather();
                }
            }
        }
    }

    @Override
    public int getDefaultConnector() {
        return TGComponentManager.CONNECTOR_NODE_TMLARCHI;
    }

    @Override
    public int getComponentType() {
        return OTHER;
    }
}
