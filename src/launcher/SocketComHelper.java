package launcher;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import myutil.AESEncryptor;
import myutil.TraceManager;

public class SocketComHelper {

	private static final Comparator<Enum<?>> codeComparator = new Comparator<Enum<?>>() {

		@Override
		public int compare(	final Enum<?> code1,
							final Enum<?> code2 ) {
			// Sort by decreasing ordinal number
			return Integer.valueOf( code2.ordinal() ).compareTo( code1.ordinal() );
		}
	};
	
	private static final List<RequestCode> sortedRequestCodes;
	
	static {
		sortedRequestCodes = new ArrayList<RequestCode>( Arrays.asList( RequestCode.values() ) );
		Collections.sort( sortedRequestCodes, codeComparator );
	}
	
	private static final List<ResponseCode> sortedResponseCodes;
	
	static {
		sortedResponseCodes = new ArrayList<ResponseCode>( Arrays.asList( ResponseCode.values() ) );
		Collections.sort( sortedResponseCodes, codeComparator );
	}

	private SocketComHelper() {
	}
    
    public static RequestCode requestCode( final String message ) {
    	if ( message != null ) {
	    	for ( final RequestCode code : sortedRequestCodes ) {
	    		if ( message.startsWith( toString( code ) ) ) {
	    			return code;
	    		}
	    	}
    	}
    	
    	return null;
    }
    
    public static String message( 	final RequestCode code, 
    								final String completeMessage ) {
    	return completeMessage.replaceFirst( toString( code ), "" );
    }
    
    public static void send( 	final PrintStream out,
    							final RequestCode code,
    							String message,
    							final String encryptionKey ) {
        message = toString( code ) + message;
	
		if ( encryptionKey != null) {
		    // cipher the information
			message = AESEncryptor.encrypt( encryptionKey, RshServer.iv, message);
		    TraceManager.addDev( "Ciphered message to server=" + message );
		}

		out.println( message);
        out.flush();
    }
    
    public static ResponseCode responseCode( final String message ) {
    	if ( message != null ) {
	    	for ( final ResponseCode code : sortedResponseCodes ) {
	    		if ( message.startsWith( toString( code ) ) ) {
	    			return code;
	    		}
	    	}
    	}
    	
    	return null;
    }
    
    public static String message( 	final ResponseCode code, 
    								final String completeMessage ) {
    	return completeMessage.replaceFirst( toString( code ), "" );
    }
    
    public static void send( 	final PrintStream out,
    							final ResponseCode code,
    							final String message ) {
    	final StringBuilder sentMess = new StringBuilder();
    	
    	if ( code != null ) {
    		sentMess.append( toString( code ) );
    	}
    	
    	if ( message != null ) {
    		sentMess.append( message );
    	}

        out.println( sentMess.toString() );
        out.flush();
    }
    
    private static String toString( final Enum<?> code ) {
    	return String.valueOf( code.ordinal() ) + ":";
    }
}
