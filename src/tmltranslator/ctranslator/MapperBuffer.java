/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille, Andrea Enrici

   ludovic.apvrille AT enst.fr
   andrea.enrici AT enstr.fr

   This software is a computer program whose purpose is to allow the
   edition of TURTLE analysis, design and deployment diagrams, to
   allow the generation of RT-LOTOS or Java code from this diagram,
   and at last to allow the analysis of formal validation traces
   obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
   from INRIA Rhone-Alpes.

   This software is governed by the CeCILL  license under French law and
   abiding by the rules of distribution of free software.  You can  use,
   modify and/ or redistribute the software under the terms of the CeCILL
   license as circulated by CEA, CNRS and INRIA at the following URL
   "http://www.cecill.info".

   As a counterpart to the access to the source code and  rights to copy,
   modify and redistribute granted by the license, users are provided only
   with a limited warranty  and the software's author,  the holder of the
   economic rights,  and the successive licensors  have only  limited
   liability.

   In this respect, the user's attention is drawn to the risks associated
   with loading,  using,  modifying and/or developing or reproducing the
   software by the user in light of its specific status of free software,
   that may mean  that it is complicated to manipulate,  and  that  also
   therefore means  that it is reserved for developers  and  experienced
   professionals having in-depth computer knowledge. Users are therefore
   encouraged to load and test the software's suitability as regards their
   requirements in conditions enabling the security of their systems and/or
   data to be ensured and,  more generally, to use and operate it in the
   same conditions as regards security.

   The fact that you are presently reading this means that you have had
   knowledge of the CeCILL license and that you accept its terms.

   /**
   * Class FEPBuffer
   * Creation: 11/02/2014
   * @version 1.0 11/02/2014
   * @author Andrea ENRICI
   * @see
   */

package tmltranslator.ctranslator;;

import java.util.*;
import java.nio.*;
import myutil.*;
import tmltranslator.*;

public class MapperBuffer extends Buffer	{

	public static final String[] symmetricalValues = { "OFF" , "ON" };

	//data in
	public static final int numSamplesDataInMappIndex = 1;
	protected String numSamplesDataInMappValue = USER_TO_DO;
	public static final String numSamplesDataInMappType = "uint16_t";

	public static final int baseAddressDataInMappIndex = 2;
	protected String baseAddressDataInMappValue = USER_TO_DO;
	public static final String baseAddressDataInMappType = "uint16_t*";

	public static final int bitsPerSymbolDataInMappIndex = 3;
	protected String bitsPerSymbolDataInMappValue = USER_TO_DO;
	public static final String bitsPerSymbolDataInMappType = "uint16_t";


	public static int symmetricalValueDataInMappIndex = 4;
	protected String symmetricalValueDataInMappValue = USER_TO_DO;
	protected static final String symmetricalValueDataInMappType = "bool";

	//data out
	public static final int baseAddressDataOutMappIndex = 5;
	protected String baseAddressDataOutMappValue = USER_TO_DO;
	public static final String baseAddressDataOutMappType = "uint16_t*";
	
	//Look up table
	public static final int baseAddressLUTMappIndex = 6;
	protected String baseAddressLUTMappValue = USER_TO_DO;
	public static final String baseAddressLUTMappType = "uint16_t*";
	
	public static final String DECLARATION = "struct MAPPER_BUFFER_TYPE {" + CR + TAB +
																						numSamplesDataInMappType + SP + "num_symbols" + SC + CR + TAB +
																						baseAddressDataInMappType + SP + "input_base_address" + SC + CR + TAB +
																						bitsPerSymbolDataInMappType + SP + "num_bits_per_symbol" + SC + CR + TAB +
																						symmetricalValueDataInMappType + SP + "symmetrical_value" + SC + CR + TAB +
																						baseAddressDataOutMappType + SP + "output_base_address" + SC + CR + TAB +
																						baseAddressLUTMappType + SP + "lut_base_address" + SC + CR + "}" + SC + CR2 +
																						"typedef MAPPER_BUFFER_TYPE MAPPER_BUFFER_TYPE" + SC;
	
	private String Context = "MAPPER_CONTEXT";
	
	public MapperBuffer( String _name, TMLTask _task )	{
		type = "MAPPER_BUFFER_TYPE";
		name = _name;
		task = _task;
	}

	@Override public String getInitCode()	{
		StringBuffer s = new StringBuffer();
		if( bufferParameters != null )	{
			retrieveBufferParameters();
		}
		s.append( TAB + name + ".num_symbols = " + "(" + numSamplesDataInMappType + ")" + numSamplesDataInMappValue + SC + CR );
		s.append( TAB + name + ".input_base_address = " + "(" + baseAddressDataInMappType + ")" + baseAddressDataInMappValue + SC + CR );
		s.append( TAB + name + ".num_bits_per_symbol = " + "(" + bitsPerSymbolDataInMappType + ")" + bitsPerSymbolDataInMappValue + SC + CR );
		s.append( TAB + name + ".symmetrical_value = " + "(" + symmetricalValueDataInMappType + ")" + symmetricalValueDataInMappValue + SC + CR );
		s.append( TAB + name + ".output_base_address = " + "(" + baseAddressDataOutMappType + ")" + baseAddressDataOutMappValue + SC + CR );
		s.append( TAB + name + ".lut_base_address = " + "(" + baseAddressLUTMappType + ")" + baseAddressLUTMappValue + SC + CR );
		return s.toString();
	}

	public String toString()	{

		StringBuffer s = new StringBuffer( super.toString() );
		s.append( TAB2 + "num_symbols = " + numSamplesDataInMappValue + SC + CR );
		s.append( TAB2 + "input_base_address = " + baseAddressDataInMappValue + SC + CR );
		s.append( TAB2 + "num_bits_per_symbol = " + bitsPerSymbolDataInMappValue + SC + CR );
		s.append( TAB2 + "symmetrical_value = " + symmetricalValueDataInMappValue + SC + CR );
		s.append( TAB2 + "output_base_address = " + baseAddressDataOutMappValue + SC + CR );
		s.append( TAB2 + "lut_base_address = " + baseAddressLUTMappValue + SC + CR );
		return s.toString();
	}
	
	private void retrieveBufferParameters()	{

		if( bufferParameters.get( numSamplesDataInMappIndex ).length() > 0 )	{
			numSamplesDataInMappValue = bufferParameters.get( numSamplesDataInMappIndex );
		}
		if( bufferParameters.get( baseAddressDataInMappIndex ).length() > 0 )	{
			baseAddressDataInMappValue = bufferParameters.get( baseAddressDataInMappIndex );
		}
		if( bufferParameters.get( bitsPerSymbolDataInMappIndex ).length() > 0 )	{
			bitsPerSymbolDataInMappValue = bufferParameters.get( bitsPerSymbolDataInMappIndex );
		}
		if( bufferParameters.get( symmetricalValueDataInMappIndex ).length() > 0 )	{
			symmetricalValueDataInMappValue = String.valueOf( ( new Vector<String>( Arrays.asList( symmetricalValues ))).indexOf( bufferParameters.get( symmetricalValueDataInMappIndex )));
		}
		if( bufferParameters.get( baseAddressDataOutMappIndex ).length() > 0 )	{
			baseAddressDataOutMappValue = bufferParameters.get( baseAddressDataOutMappIndex );
		}
		if( bufferParameters.get( baseAddressLUTMappIndex ).length() > 0 )	{
			baseAddressLUTMappValue = bufferParameters.get( baseAddressLUTMappIndex );
		}
	}

	public String getContext()	{
		return Context;
	}
}	//End of class
