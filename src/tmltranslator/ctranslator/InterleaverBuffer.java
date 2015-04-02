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

public class InterleaverBuffer extends Buffer	{

	public static final String[] symmetricalValues = { "OFF" , "ON" };
	//data in
	public static final int packedBinaryInIntlIndex = 1;
	public static final int widthIntlIndex = 2;
	public static final int bitInOffsetIntlIndex = 3;
	public static final int inputOffsetIntlIndex = 4;
	//data out
	public static final int packedBinaryOutIntlIndex = 5;
	public static final int bitOutOffsetIntlIndex = 6;
	public static final int outputOffsetIntlIndex = 7;
	//permutation table
	public static final int offsetPermIntlIndex = 8;
	public static final int lengthPermIntlIndex = 9;
	
	public String packedBinaryInIntlValue = USER_TO_DO;
	public static String packedBinaryInIntlType = "bool";
	
	public String widthIntlValue = USER_TO_DO;
	public static String widthIntlType = "uint8_t";
	
	public String bitInOffsetIntlValue = USER_TO_DO;
	public static String bitInOffsetIntlType = "uint8_t";
	
	public String inputOffsetIntlValue = USER_TO_DO;
	public static String inputOffsetIntlType = "uint16_t";
	
	//data out
	public String packedBinaryOutIntlValue = USER_TO_DO;
	public static String packedBinaryOutIntlType = "bool";
	
	public String bitOutOffsetIntlValue = USER_TO_DO;
	public static String bitOutOffsetIntlType = "uint8_t";
	
	public String outputOffsetIntlValue = USER_TO_DO;
	public static String outputOffsetIntlType = "uint16_t";
	
	//permutation table
	public String offsetPermIntlValue = USER_TO_DO;
	public static String offsetPermIntlType = "uint16_t";
	
	public String lengthPermIntlValue = USER_TO_DO;
	public static String lengthPermIntlType = "uint16_t";

	public static final String DECLARATION = "extern struct INTERLEAVER_BUFFER_TYPE {" + CR + TAB +
																						packedBinaryInIntlType + SP + "packed_binary_input_mode" + SC + CR + TAB +
																						widthIntlType + SP + "sample_width" + SC + CR + TAB +
																						bitInOffsetIntlType + SP + "bit_input_offset" + SC + CR + TAB +
																						inputOffsetIntlType + SP + "input_offset" + SC + CR + TAB +
																						//data out
																						packedBinaryOutIntlType + SP + "packed_binary_output_mode" + SC + CR + TAB +
																						bitOutOffsetIntlType + SP + "bit_output_offset" + SC + CR + TAB +
																						outputOffsetIntlType + SP + "output_offset" + SC + CR + TAB +
																						//permutation table
																						offsetPermIntlType + SP + "permutation_offset" + SC + CR + TAB + 
																						lengthPermIntlType + SP + "permutation_length" + SC + CR + "};";
	
	private String Context = "INTL_CONTEXT";
	
	public InterleaverBuffer( String _name, TMLTask _task )	{
		type = "INTERLEAVER_BUFFER_TYPE";
		name = _name;
		task = _task;
	}

	@Override public String getInitCode()	{
		StringBuffer s = new StringBuffer();
		if( bufferParameters != null )	{
			retrieveBufferParameters();
		}
		s.append( TAB + name + ".packed_binary_input_mode = " + packedBinaryInIntlValue + SC + CR );
		s.append( TAB + name + ".sample_width = " + SP + widthIntlValue + SC + CR );
		s.append( TAB + name + ".bit_input_offset = " + SP + bitInOffsetIntlValue + SC + CR );
		s.append( TAB + name + ".input_offset = " + SP + inputOffsetIntlValue + SC + CR );
		//data out
		s.append( TAB + name + ".packed_binary_output_mode = " + SP + packedBinaryOutIntlValue + SC + CR );
		s.append( TAB + name + ".bit_output_offset = " + SP + bitOutOffsetIntlValue + SC + CR );
		s.append( TAB + name + ".output_offset = " + SP + outputOffsetIntlValue + SC + CR );
		//permutation table
		s.append( TAB + name + ".permutation_offset = " + SP + offsetPermIntlValue + SC + CR ); 
		s.append( TAB + name + ".permutation_length = " + SP + lengthPermIntlValue + SC + CR );
		return s.toString();
	}

	public String toString()	{

		StringBuffer s = new StringBuffer( super.toString() );
		s.append( TAB2 + ".packed_binary_input_mode = " + packedBinaryInIntlValue + SC + CR );
		s.append( TAB2 + ".sample_width = " + SP + widthIntlValue + SC + CR );
		s.append( TAB2 + ".bit_input_offset = " + SP + bitInOffsetIntlValue + SC + CR );
		s.append( TAB2 + ".input_offset = " + SP + inputOffsetIntlValue + SC + CR );
		//data out
		s.append( TAB2 + ".packed_binary_output_mode = " + SP + packedBinaryOutIntlValue + SC + CR );
		s.append( TAB2 + ".bit_output_offset = " + SP + bitOutOffsetIntlValue + SC + CR );
		s.append( TAB2 + ".output_offset = " + SP + outputOffsetIntlValue + SC + CR );
		//permutation table
		s.append( TAB2 + ".permutation_offset = " + SP + offsetPermIntlValue + SC + CR ); 
		s.append( TAB2 + ".permutation_length = " + SP + lengthPermIntlValue + SC + CR );
		return s.toString();
	}
	
	private void retrieveBufferParameters()	{

		if( bufferParameters.get( packedBinaryInIntlIndex ).length() > 0 )	{
			packedBinaryInIntlValue = String.valueOf((new Vector<String>( Arrays.asList( Buffer.onOffVector  ))).indexOf( bufferParameters.get( packedBinaryInIntlIndex )));
		}
		if( bufferParameters.get( widthIntlIndex ).length() > 0 )	{
			widthIntlValue = bufferParameters.get( widthIntlIndex );
		}
		if( bufferParameters.get( bitInOffsetIntlIndex ).length() > 0 )	{
			bitInOffsetIntlValue = bufferParameters.get( bitInOffsetIntlIndex );
		}
		if( bufferParameters.get( inputOffsetIntlIndex ).length() > 0 )	{
			inputOffsetIntlValue = bufferParameters.get( inputOffsetIntlIndex );
		}
		if( bufferParameters.get( packedBinaryOutIntlIndex ).length() > 0 )	{
			packedBinaryOutIntlValue = String.valueOf((new Vector<String>( Arrays.asList( Buffer.onOffVector  ))).indexOf( bufferParameters.get( packedBinaryOutIntlIndex )));
		}
		if( bufferParameters.get( bitOutOffsetIntlIndex ).length() > 0 )	{
			bitOutOffsetIntlValue = bufferParameters.get( bitOutOffsetIntlIndex );
		}
		if( bufferParameters.get( outputOffsetIntlIndex ).length() > 0 )	{
			outputOffsetIntlValue = bufferParameters.get( outputOffsetIntlIndex );
		}
		if( bufferParameters.get( offsetPermIntlIndex ).length() > 0 )	{
			offsetPermIntlValue = bufferParameters.get( offsetPermIntlIndex );
		}
		if( bufferParameters.get( lengthPermIntlIndex ).length() > 0 )	{
			lengthPermIntlValue = bufferParameters.get( lengthPermIntlIndex );
		}
	}

	public String getContext()	{
		return Context;
	}
}	//End of class
