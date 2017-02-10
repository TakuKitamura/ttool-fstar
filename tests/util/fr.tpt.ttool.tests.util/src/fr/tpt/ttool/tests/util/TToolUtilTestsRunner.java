package fr.tpt.ttool.tests.util;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import fr.tpt.ttool.tests.util.remote.TestRshClient;

public class TToolUtilTestsRunner {
	
	public static void main(String[] args) {
	      Result result = JUnitCore.runClasses( TestRshClient.class );
			
	      for ( final Failure failure : result.getFailures() ) {
	         System.err.println( "Test failed : " + failure.toString() );
	      }
		
	      if ( result.wasSuccessful() ) {
	    	  System.out.println( "All tests passed." );
	      }
	      else {
	    	  System.err.println( "Some of the tests failed!" );
	      }
	      
	      System.exit( 0 );
	}
}
