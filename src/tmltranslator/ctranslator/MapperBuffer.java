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

public class MapperBuffer extends BaseBuffer	{

	public static final String[] symmetricalValues = { "OFF" , "ON" };
	public static final int numSamplesIndex = 1;
	public static final int baseAddressIndex = 2;
	public static final int bitsPerSymbolIndex = 3;
	public static final int bitOffsetFirstSymbolIndex = 4;
	public static final int symmetricalIndex = 5;
	
	protected static String numSamplesValue = USER_TO_DO;
	protected static final String numSamplesType = "uint8_t";
	
	protected static String baseAddressValue = USER_TO_DO;
	protected static final String baseAddressType = "uint32_t*";
	
	protected static String bitsPerSymbolValue = USER_TO_DO;
	protected static final String bitsPerSymbolType = "uint8_t";
	
	protected static String bitOffsetFirstSymbolValue = USER_TO_DO;
	protected static final String bitOffsetFirstSymbolType = "uint16_t*";

	protected static String symmetricalValue = USER_TO_DO;
	protected static final String symmetricalValueType = "bool";

	public static final String DECLARATION = "extern struct MAPPER_BUFFER_TYPE {" + CR + TAB +
																						numSamplesType + SP + "num_symbols" + SC + CR + TAB +
																						baseAddressType + SP + "base_address" + SC + CR + TAB +
																						bitsPerSymbolType + SP + "num_bits_symbol" + SC + CR + TAB +
																						bitOffsetFirstSymbolType + SP + "bit_offset_first_symbol" + SC + CR + TAB +
																						symmetricalValueType + SP + "symmetrical_value" + SC + CR + "};";
	
	private String Context = "embb_mapper_context";
	
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
		s.append( TAB + name + ".num_symbols = " + numSamplesValue + SC + CR );
		s.append( TAB + name + ".base_address = " + baseAddressValue + SC + CR );
		s.append( TAB + name + ".num_bits_per_symbol = " + bitsPerSymbolValue + SC + CR );
		s.append( TAB + name + ".bit_offset_first_symbol = " + bitOffsetFirstSymbolValue + SC + CR );
		s.append( TAB + name + ".symmetrical_value = " + symmetricalValue + SC + CR );
		return s.toString();
	}

	public String toString()	{

		StringBuffer s = new StringBuffer( super.toString() );
		s.append( TAB2 + "num_symbols = " + numSamplesValue + SC + CR );
		s.append( TAB2 + "base_address = " + baseAddressValue + SC + CR );
		s.append( TAB2 + "num_bits_per_symbol = " + bitsPerSymbolValue + SC + CR );
		s.append( TAB2 + "bit_offset_first_symbol = " + bitsPerSymbolValue + SC + CR );
		s.append( TAB2 + "symmetrical_value = " + symmetricalValue + SC + CR );
		return s.toString();
	}
	
	private void retrieveBufferParameters()	{

		if( bufferParameters.get( numSamplesIndex ).length() > 0 )	{
			numSamplesValue = bufferParameters.get( numSamplesIndex );
		}
		if( bufferParameters.get( baseAddressIndex ).length() > 0 )	{
			baseAddressValue = bufferParameters.get( baseAddressIndex );
		}
		if( bufferParameters.get( bitsPerSymbolIndex ).length() > 0 )	{
			bitsPerSymbolValue = bufferParameters.get( bitsPerSymbolIndex );
		}
		if( bufferParameters.get( bitOffsetFirstSymbolIndex ).length() > 0 )	{
			bitOffsetFirstSymbolValue = bufferParameters.get( bitOffsetFirstSymbolIndex );
		}
		if( bufferParameters.get( symmetricalIndex ).length() > 0 )	{
		symmetricalValue = String.valueOf( ( new Vector<String>( Arrays.asList( symmetricalValues ))).indexOf( bufferParameters.get( symmetricalIndex )));
		}
	}

	public String getContext()	{
		return Context;
	}
}	//End of class
