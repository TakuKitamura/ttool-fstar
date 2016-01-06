/* authors: v1.0 Raja GATGOUT 2014
            v2.0 Daniela GENIUS, Julien HENON 2015 */

package ddtranslatorSoclib.toTopCell;
import java.util.*;
import ddtranslatorSoclib.*;

public class Simulation {
	
    private  static String simulation;
	
    private final static String CR = "\n";
	private final static String CR2 = "\n\n";
    
    public Simulation(){
    }

    public static String getSimulation(){
		 simulation  = CR2+ CR2+ 
		     "/***************************************************************************" +	CR +
		     "----------------------------simulation-------------------------" + CR +
		     "***************************************************************************/"+CR2 ;
		 simulation =simulation+"int sc_main (int argc, char *argv[])" + CR + "{" + CR;
		 simulation = simulation +"       try {" + CR +"         return _main(argc, argv);" + CR + "    }" + CR2;
		 simulation =simulation +"       catch (std::exception &e) {" + CR + "            std::cout << e.what() << std::endl;" + CR + "            throw;"+ CR+"    }"; 
		simulation =simulation+" catch (...) {" + CR;
		simulation =simulation+"std::cout << \"Unknown exception occured\" << std::endl;" + CR;
		simulation =simulation+"throw;" + CR;
		simulation =simulation+"}" + CR;
		simulation =  simulation+ CR +"       return 1;"+ CR + "}"  ;		 
		return simulation;
    }
}
