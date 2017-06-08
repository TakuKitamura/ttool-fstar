
/**
 * Class TransferInterface
 * To be used with the TTool Java code generator
 * For more information on TTool, see http://www.eurecom.fr/~apvrille/TURTLE
 * Creation: 28/07/2005
 * @version 1.1 28/07/2005
 * @author Ludovic APVRILLE
 * @see
 */

package jttool;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TransferInterface extends Remote {

    public void asynchronousSend(String s) throws RemoteException;

}



