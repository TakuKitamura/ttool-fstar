
/**
 * Class DataTransferInterface
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 28/07/2005
 * @version 1.1 28/07/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

public class DataTransferInterface {
    public TransferInterface ti;
    public String host;
    public int port;

    public DataTransferInterface(TransferInterface _ti, String _host, int _port) {
	ti = _ti;
	host = _host;
	port = _port;
    }
   

}



