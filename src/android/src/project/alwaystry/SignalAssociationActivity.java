package project.alwaystry;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import copyfromJAVAsource.AvatarSignal;
import copyfromJAVAsource.TAttribute;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SignalAssociationActivity extends Activity{
	
	private Vector localSignalAssociations;
	private Vector available1, available2;
	private int indexSignalSelected =-1;
	private static String block1Name;
	private static String block2Name;
	private Vector block1signals,block2signals;
	private int signal1Selected=-1, signal2Selected=-1;
	private EditText FIFOsizeEditText;
	private TextView FIFOsizeTextView;
	private CheckBox blockingBox;
	private Boolean isAsynchronous = false;
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.signalassociation);
		
		FIFOsizeEditText = (EditText)findViewById(R.id.sizeFIFOEditText);
		FIFOsizeEditText.setText("1024");
		FIFOsizeTextView = (TextView)findViewById(R.id.sizeFIFOTextView);
		blockingBox = (CheckBox)findViewById(R.id.blockingBox);
		
		RadioGroup sychroRadioGroup = (RadioGroup)findViewById(R.id.sychroRadioGroup);
		sychroRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId == R.id.sychroRadioButton){
					FIFOsizeEditText.setEnabled(false);
					FIFOsizeTextView.setEnabled(false);
					blockingBox.setEnabled(false);
					isAsynchronous = false;
				}else if(checkedId == R.id.asynchroRadioButton){
					FIFOsizeEditText.setEnabled(true);
					FIFOsizeTextView.setEnabled(true);
					blockingBox.setEnabled(true);
					isAsynchronous = true;
				}
				
			}
			
		});
		
		localSignalAssociations = new Vector();
		available1 = new Vector();
		available2 = new Vector();
		block1signals = new Vector();
		block2signals = new Vector();
		
		PanelWithTitledBorder p1 = (PanelWithTitledBorder)findViewById(R.id.addSignalsBox);	
		p1.setName("Adding sinals");
		
		PanelWithTitledBorder p2 = (PanelWithTitledBorder)findViewById(R.id.manageSignalsBox);	
		p2.setName("Managing sinals");
		
		PanelWithTitledBorder p3 = (PanelWithTitledBorder)findViewById(R.id.connectorTypeBox);	
		p1.setName("Connector type");
		
		Bundle associaBundle = this.getIntent().getExtras();
		if(associaBundle != null && associaBundle.containsKey("associationSignals")&& associaBundle.containsKey("availableSignals1") && associaBundle.containsKey("availableSignals2")){
			
			block1Name = this.getIntent().getStringExtra("block1");
			block2Name = this.getIntent().getStringExtra("block2");
			
			String[] signals1 = this.getIntent().getStringArrayExtra("block1signals");
			String[] signals2 = this.getIntent().getStringArrayExtra("block2signals");
			
			for(int i=0;i<signals1.length;i++){
				block1signals.addElement(AvatarSignal.isAValidSignal(signals1[i]));
			}
			
			for(int i=0;i<signals2.length;i++){
				block2signals.addElement(AvatarSignal.isAValidSignal(signals2[i]));
			}
			
			String[] asArray = this.getIntent().getStringArrayExtra("associationSignals");
			
			for(int i=0;i<asArray.length;i++){
				localSignalAssociations.addElement(asArray[i]);
			}
			
			String[] avsArray1 = this.getIntent().getStringArrayExtra("availableSignals1");
			
			for(int i=0;i<avsArray1.length;i++){
				available1.addElement(AvatarSignal.isAValidSignal(avsArray1[i]));
			}
			
			String[] avsArray2 = this.getIntent().getStringArrayExtra("availableSignals2");
			
			for(int i=0;i<avsArray2.length;i++){
				available2.addElement(AvatarSignal.isAValidSignal(avsArray2[i]));
			}
			
			makeSpinners();
			
//			Spinner spinner1 = (Spinner)findViewById(R.id.signalsSpinner1);
//			List<CharSequence> signals1 = new ArrayList<CharSequence>();
//		    ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this,
//					android.R.layout.simple_spinner_item, signals1);
//		    for(int i=0;i<avsArray1.length;i++){
//				adapter1.add(avsArray1[i]);
//			}
//		    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			spinner1.setAdapter(adapter1);
//			
//			Spinner spinner2 = (Spinner)findViewById(R.id.signalsSpinner2);
//			List<CharSequence> signals2 = new ArrayList<CharSequence>();
//		    ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this,
//					android.R.layout.simple_spinner_item, signals2);
//		    for(int i=0;i<avsArray2.length;i++){
//				adapter2.add(avsArray2[i]);
//			}
//		    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//			spinner2.setAdapter(adapter2);
			
			updateSignalsList();
			
		}
		
		
	}
	
	public void updateSignalsList(){
		
		ListView signalsList = (ListView)findViewById(R.id.signalsListView);		
		String[] signalassocias = new String[localSignalAssociations.size()];
		 
		for(int i=0;i<localSignalAssociations.size();i++){			 
			signalassocias[i] = (String)localSignalAssociations.elementAt(i);
		 
		}
		signalsList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.list_item, signalassocias));
		signalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					for(int i=0;i<arg0.getCount();i++){
						arg0.getChildAt(i).setBackgroundColor(Color.WHITE);
					}
					arg1.setBackgroundColor(Color.BLUE);
					indexSignalSelected = arg2;
					
				}
		});
		
		signalsList.invalidate();
		
	}

	public void clickOnEditButtons(View v){
		 Log.i("edition", "buttonclicked");
	    	switch(v.getId()){
	    	case R.id.signalsCloseButton:	
	    		closeActivity();
	    		break;
	    	case R.id.signalsCancelButton:
	    		cancelActivity();
	    		break;
	    	case R.id.addSignalsButton:
	    		addSignals();
	    		Log.i("edit attributes", "add clicked");
	    		break;
	    	case R.id.removeSignalsButton:
	    		removeSignals();
	    		break;
	    	case R.id.upSignalsButton:
	    		upSignals();
	    		break;
	    	case R.id.downSignalsButton:
	    		downSignals();
	    		break;
	    	
	    	}
	    	
	 }
	
	public void downSignals(){
		int i = indexSignalSelected;
        if ((i!= -1) && (i != localSignalAssociations.size() - 1)) {
            Object o = localSignalAssociations.elementAt(i);
            localSignalAssociations.removeElementAt(i);
            localSignalAssociations.insertElementAt(o, i+1);
            updateSignalsList();
        }
		
	}
	
	public void upSignals(){
		int i = indexSignalSelected;
        if (i > 0) {
            Object o = localSignalAssociations.elementAt(i);
            localSignalAssociations.removeElementAt(i);
            localSignalAssociations.insertElementAt(o, i-1);
            updateSignalsList();
        }
		
	}
	
//	private void updateAddButton() {
//		
//		int i1 = signal1Selected;
//        int i2 = signal2Selected;
//        
//        
//        if ((i1 > -1) && (i2 > -1)) {
//			AvatarSignal as1 = (AvatarSignal)(available1.elementAt(i1));
//            AvatarSignal as2 = (AvatarSignal)(available2.elementAt(i2));
//			Button b = (Button)findViewById(R.id.addSignalsButton);
//			b.setEnabled(as1.isCompatibleWith(as2));
//		}
//	}
	
	public void makeSpinners(){
		String [] avsArray1 = new String[available1.size()];
		for(int i=0; i<available1.size();i++){
			avsArray1[i] = ((AvatarSignal)available1.elementAt(i)).toString();
		}
		
		String [] avsArray2 = new String[available2.size()];
		for(int i=0; i<available2.size();i++){
			avsArray2[i] = ((AvatarSignal)available2.elementAt(i)).toString();
		}
		
		Spinner spinner1 = (Spinner)findViewById(R.id.signalsSpinner1);
		List<CharSequence> signals1 = new ArrayList<CharSequence>();
	    ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, signals1);
	    for(int i=0;i<avsArray1.length;i++){
			adapter1.add(avsArray1[i]);
		}
	    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
				
		Spinner spinner2 = (Spinner)findViewById(R.id.signalsSpinner2);
		List<CharSequence> signals2 = new ArrayList<CharSequence>();
	    ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item, signals2);
	    for(int i=0;i<avsArray2.length;i++){
			adapter2.add(avsArray2[i]);
		}
	    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		
		spinner1.invalidate();
		spinner2.invalidate();

	}
	
	public void addSignals(){
				
		Spinner spinner1 = (Spinner)findViewById(R.id.signalsSpinner1);
		Spinner spinner2 = (Spinner)findViewById(R.id.signalsSpinner2);
		int i1 = spinner1.getSelectedItemPosition();
		int i2 = spinner2.getSelectedItemPosition();
		
		if((i1 > -1) && (i2 > -1)){
			AvatarSignal as1 = (AvatarSignal)(available1.elementAt(i1));
			AvatarSignal as2 = (AvatarSignal)(available2.elementAt(i2));
			
			if(as1.isCompatibleWith(as2)){
				String s = makeSignalAssociation(as1,as2);
				localSignalAssociations.add(s);
				available1.removeElementAt(i1);
				available2.removeElementAt(i2);
				
				makeSpinners();
				updateSignalsList();
				
			}else{
				Toast.makeText(this, "Error: Two signals are not compatible.", Toast.LENGTH_SHORT).show();
			    return;
			}
			
						
		}						
	}
	
	
	
	public String getFirstSignalOfSignalAssociation(String _assoc) {
		int index0 = _assoc.indexOf(".");
		
		
		if (index0 == -1) {
			return null;
		}
		
		int index1 = _assoc.indexOf("->");
		int index2 = _assoc.indexOf("<-");
		
		index1 = Math.max(index1, index2);
		if (index1 == -1) {
			return null;
		}
		
		return _assoc.substring(index0+1, index1).trim();
	}
	
	public String getSecondSignalOfSignalAssociation(String _assoc) {
		int index0 = _assoc.indexOf("->");
		int index1 = _assoc.indexOf("<-");
		
		if ((index0 == -1) && (index1 == -1)) {
			return null;
		}
		
		index0 = Math.max(index0, index1);
		_assoc = _assoc.substring(index0+2, _assoc.length());
		
		index0 = _assoc.indexOf(".");
		
		if (index0 == -1) {
			return null;
		}
		
		return _assoc.substring(index0+1, _assoc.length()).trim();
	}
	
	public void removeSignals(){
		int index = indexSignalSelected;
		if(index!=-1){
			String s = (String)(localSignalAssociations.get(index));
			localSignalAssociations.removeElementAt(index);
			updateSignalsList();
			String sig1 = getFirstSignalOfSignalAssociation(s);
			String sig2 = getSecondSignalOfSignalAssociation(s);
			AvatarSignal as1 = getSignalNameBySignalDef(sig1,block1signals);
			AvatarSignal as2 = getSignalNameBySignalDef(sig2,block2signals);
			
			if ((as1 != null) && (as2 != null)) {
				available1.add(as1);
				available2.add(as2);
				makeSpinners();
			}
			
		}
		
	}
	
	public AvatarSignal getSignalNameBySignalDef(String _id,Vector signals){

		int index0 = _id.indexOf('(');
		if (index0 > -1) {
			_id = _id.substring(0, index0);
		}
		_id = _id.trim();
		//TraceManager.addDev("Searching for signal with id=" + _id);
		AvatarSignal as;
		for(int i=0; i<block1signals.size(); i++) {
			as = (AvatarSignal)(signals.get(i));
			if (as.getId().compareTo(_id) == 0) {
				//TraceManager.addDev("found");
				return as;
			}
		}
		//TraceManager.addDev("Not found");
		return null;
	
		
	}
	
	public void closeActivity(){
				
		String[] signalAssociations = new String[localSignalAssociations.size()];
		for(int i=0; i<localSignalAssociations.size();i++){
			signalAssociations[i] = (String)localSignalAssociations.elementAt(i);
		}
		Bundle bundle = new Bundle();
		bundle.putStringArray("signalAssociationsArray", signalAssociations);
		bundle.putBoolean("asynchro", isAsynchronous);
		try{
			int size = Integer.decode(FIFOsizeEditText.getText().toString()).intValue();
			size = Math.max(1, size);
			bundle.putInt("FIFOsize",size );
		}catch(Exception e){
			Toast.makeText(this, "Error: Unvalid FIFO size: "+FIFOsizeEditText.getText().toString(), Toast.LENGTH_SHORT).show();
			cancelActivity();
		}
		
		bundle.putBoolean("isBlocking", blockingBox.isChecked());
		
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK,intent);
		finish();
	}
	
	public void cancelActivity(){
		Intent intent = new Intent();
		setResult(RESULT_CANCELED,intent);
		finish();
		
	}
	
	
	public static String makeSignalAssociation(AvatarSignal _as1,  AvatarSignal _as2) {
		String s = block1Name + "." + _as1.toBasicString();
		if (_as1.getInOut() == AvatarSignal.OUT) {
			s += " -> ";
		} else {
			s += " <- ";
		}
		s +=block2Name + "." + _as2.toBasicString();
		return s;
	}
	
}
