package tmltranslator.tomappingsystemc2;

import java.util.List;

import req.ebrdd.EBRDD;
import tepe.TEPE;
import tmltranslator.TMLMapping;
import tmltranslator.TMLModeling;

public class DiploSimulatorFactory {
	
	public static final DiploSimulatorFactory INSTANCE = new DiploSimulatorFactory();

	private DiploSimulatorFactory() {
	}
	
	public IDiploSimulatorCodeGenerator createTranslator(	final TMLModeling tmlModeling ) {
		return new DiploSimulatorCodeGenerator( tmlModeling );
	}
	
	public IDiploSimulatorCodeGenerator createTranslator(	final TMLMapping tmlMapping ) {
		return new DiploSimulatorCodeGenerator( tmlMapping );
	}
	
	public IDiploSimulatorCodeGenerator createTranslator(	final TMLModeling tmlModeling, 
															final List<EBRDD> ebrdds, 
															final List<TEPE> tepes ) {
		return new DiploSimulatorCodeGenerator( tmlModeling, ebrdds, tepes );
	}
	
	public IDiploSimulatorCodeGenerator createTranslator(	final TMLMapping tmlMapping, 
															final List<EBRDD> ebrdds, 
															final List<TEPE> tepes ) {
		return new DiploSimulatorCodeGenerator( tmlMapping, ebrdds, tepes );
	}
}
