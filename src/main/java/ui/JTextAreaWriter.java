package ui;

import javax.swing.*;
import java.io.IOException;
import java.io.Writer;

public class JTextAreaWriter extends Writer {

	private final JTextArea textArea;
	
	public JTextAreaWriter( final JTextArea textArea ) {
		assert( textArea != null );
		
		this.textArea = textArea;
	}
	
	@Override
	public void write( 	char[] cbuf, 
						int off, 
						int len )
	throws IOException {
		textArea.append( new String( cbuf ).substring( off, off + len ) ); 
	}

	@Override
	public void flush()
	throws IOException {
	}

	@Override
	public void close()
	throws IOException {
	}
}
