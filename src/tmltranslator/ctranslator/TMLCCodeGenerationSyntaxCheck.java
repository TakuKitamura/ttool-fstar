/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
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
 *
 * /**
 * Class TMLCCodeGenerationSyntaxCheck
 * Used for checking for errors before generating the application C code
 * Creation: 20/03/2015
 * @version 1.0 20/03/2015
 * @author Andrea ENRICI
 * @see
 */


package tmltranslator.ctranslator;;

import tmltranslator.*;
import java.util.*;

public class TMLCCodeGenerationSyntaxCheck {
    
		public TMLActivityElement element;
		private ArrayList<TMLCCodeGenerationError> errors;
		private ArrayList<TMLTask> mappedTasks;
		private TMLMapping tmap;
		private TMLModeling tmlm;
		private TMLArchitecture tmla;

    public final static int ERROR_STRUCTURE = 0;
    public final static int WARNING_STRUCTURE = 1;   
		public final static int ERROR_BEHAVIOR = 2;
    public final static int WARNING_BEHAVIOR = 3;
    public int type; // ERROR, WARNING
    public String message;
		public TMLTask task;
    
    public TMLCCodeGenerationSyntaxCheck( TMLMapping _tmap, TMLModeling _tmlm, TMLArchitecture _tmla ) {
			//mappedTasks = _mappedTasks;
			errors = new ArrayList<TMLCCodeGenerationError>();
			tmap = _tmap;
			tmlm = _tmlm;
			tmla = _tmla;
    }

	  public void addError( String message, int type )	{
			TMLCCodeGenerationError error = new TMLCCodeGenerationError( type );
			error.message = message;
			errors.add( error );
		}

  	public ArrayList<TMLCCodeGenerationError> getErrors() {
			return errors;
		}

		public boolean hasErrors()	{
			if( errors.size() > 0 )	{
				return true;
			}
			return false;
		}

		public void check()	{
			checkForPrexAndPostexChannels();
		}

		private void checkForPrexAndPostexChannels()	{

			boolean foundPrex = false, foundPostex = false;
			TMLPort originPort, destinationPort;

			//Fill the the prex and postex lists
			for( TMLChannel ch: tmlm.getChannels() )	{
				if( ch.isBasicChannel() )	{
					originPort = ch.getOriginPort();
					destinationPort = ch.getDestinationPort();
					if( originPort.isPrex() )	{
						//also check that the task has no input channel(s)
						if( ch.getOriginTask().getReadTMLChannels().size() > 0 )	{
							addError( "The selected channel cannot be marked as prex. Task " + ch.getOriginTask().getName() + " has input channels", TMLCCodeGenerationError.ERROR_STRUCTURE );
						}
						foundPrex = true;
					}
					if( destinationPort.isPostex() )	{
						foundPostex = true;
					}
				}
				if( ch.isAForkChannel() )	{
					originPort = ch.getOriginPorts().get(0);
					if( originPort.isPrex() )	{
						if( ch.getOriginTask().getReadTMLChannels().size() > 0 )	{
							addError( "The selected channel cannot be marked as prex. Task " + ch.getOriginTask().getName() + " has input channels", TMLCCodeGenerationError.ERROR_STRUCTURE );
						}
						foundPrex = true;
					}
				}
				if( ch.isAJoinChannel() )	{
					destinationPort = ch.getDestinationPorts().get(0);
					if( destinationPort.isPostex() )	{
						foundPostex = true;
					}
				}
			}
			if( !foundPrex )	{
				addError( "No suitable channel in the application diagram has been marked as prex", TMLCCodeGenerationError.ERROR_STRUCTURE );
			}
			if( !foundPostex )	{
				addError( "No suitable channel in the application diagram has been marked as postex", TMLCCodeGenerationError.ERROR_STRUCTURE );
			}
		}
}	//End of class
