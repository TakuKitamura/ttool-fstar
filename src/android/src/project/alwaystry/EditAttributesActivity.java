package project.alwaystry;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import copyfromJAVAsource.AvatarMethod;
import copyfromJAVAsource.AvatarSignal;
import copyfromJAVAsource.TAttribute;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

public class EditAttributesActivity extends TabActivity {
	
	//AvatarBDPanelAndroid panel = (AvatarBDPanelAndroid)findViewById(R.id.avatarBDPanelAndroid1);
	private Vector  attributesPar, methodsPar, signalsPar; 

	private boolean checkKeyword, checkJavaKeyword;
	private int indexAttributeSelected = -1;
	private int indexMethodSelected = -1;
	private int indexSignalSelected =-1;
	private boolean hasMethod = false;
		
	protected void onCreate(Bundle savedInstanceState) {
		 
		super.onCreate(savedInstanceState);		 
		setContentView(R.layout.editattributesalert);
		 
		attributesPar = new Vector();
		methodsPar = new Vector();
		signalsPar = new Vector();
		
		TabHost mTabHost = getTabHost();
		
		Bundle attribundle = this.getIntent().getExtras();
		if(attribundle != null && attribundle.containsKey("attributes")){
			String[] attributes =this.getIntent().getStringArrayExtra("attributes");
			
			for(int i=0; i<attributes.length; i++) {
	            attributesPar.addElement(TAttribute.getTAttributeFromString(attributes[i]));
	        }	
			
			mTabHost.addTab(mTabHost.newTabSpec("Attributs").setIndicator("Attributs").setContent(R.id.attributesTab));		 
			PanelWithTitledBorder p2 = (PanelWithTitledBorder)findViewById(R.id.panelWithTitledBorder2);	 
			p2.setName("Managing Attributes"); 
			
			Spinner spinner1 = (Spinner)findViewById(R.id.attributeaccspinner);
		    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(		 
					 this, R.array.attributeacc_array, android.R.layout.simple_spinner_item);
			adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner1.setAdapter(adapter1);
	        
			Spinner spinner2 = (Spinner)findViewById(R.id.attributetypespinner);
			ArrayAdapter<CharSequence> adapter2;
			
			if(attribundle.containsKey("methods") && attribundle.containsKey("signals")){
				hasMethod = true;
				String[] methods = this.getIntent().getStringArrayExtra("methods");
				for(int i=0;i<methods.length;i++){
					methodsPar.addElement(AvatarMethod.isAValidMethod(methods[i]));
				}
				String[] signals = this.getIntent().getStringArrayExtra("signals");
				for(int i=0;i<signals.length;i++){
					
					signalsPar.addElement(AvatarSignal.isAValidSignal(signals[i]));
				}
				
				mTabHost.addTab(mTabHost.newTabSpec("Methods").setIndicator("Methods").setContent(R.id.methodsTab));	 
				mTabHost.addTab(mTabHost.newTabSpec("Signals").setIndicator("Signals").setContent(R.id.signalsTab));
				PanelWithTitledBorder p3 = (PanelWithTitledBorder)findViewById(R.id.panelWithTitledBorder3);		
				p3.setName("Adding Methods");		 		
				PanelWithTitledBorder p4 = (PanelWithTitledBorder)findViewById(R.id.panelWithTitledBorder4);		
				p4.setName("Managing Methods");	 		
				PanelWithTitledBorder p5 = (PanelWithTitledBorder)findViewById(R.id.panelWithTitledBorder5);		
				p5.setName("Adding Signals");		
				PanelWithTitledBorder p6 = (PanelWithTitledBorder)findViewById(R.id.panelWithTitledBorder6);		
				p6.setName("Managing Signals");
				Spinner spinner3 = (Spinner)findViewById(R.id.signalspinner);
				ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
		                this, R.array.signaltype_array, android.R.layout.simple_spinner_item);
				adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner3.setAdapter(adapter3);
				
				String[] datatypes = this.getIntent().getStringArrayExtra("datatypes");
				List<CharSequence> attributetypes = new ArrayList<CharSequence>();
				adapter2 = new ArrayAdapter<CharSequence>(this,
						android.R.layout.simple_spinner_item, attributetypes);
				adapter2.add("Boolean");
				adapter2.add("Integer");
				adapter2.add("Timer");
		//		adapter2 = ArrayAdapter.createFromResource(
		//                this, R.array.attributetype_array, android.R.layout.simple_spinner_item);
							
				for(int i=0;i<datatypes.length;i++){
					adapter2.add(datatypes[i]);
				}
				checkKeyword = true;
				checkJavaKeyword = true;
					
				updateAttributesList();
				updateMethodsList();
				updateSignalsList();
			}else{
				adapter2 = ArrayAdapter.createFromResource(
		                this, R.array.attributetype_array_1, android.R.layout.simple_spinner_item);
				//FrameLayout tabcontent = (FrameLayout)findViewById(android.R.id.tabcontent);
				RelativeLayout methodtab = (RelativeLayout)findViewById(R.id.methodsTab);
				((FrameLayout)methodtab.getParent()).removeView(methodtab);
				RelativeLayout msignaltab = (RelativeLayout)findViewById(R.id.signalsTab);
				((FrameLayout)msignaltab.getParent()).removeView(msignaltab);
				
				checkKeyword = true;
				checkJavaKeyword = true;
				updateAttributesList();
			}
			
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner2.setAdapter(adapter2);
		}
		
		mTabHost.setCurrentTab(0);	
		
	 }
	 
	 public void clickOnEditButtons(View v){
		 Log.i("edition", "buttonclicked");
	    	switch(v.getId()){
	    	case R.id.closeButton:	
	    		closeActivity();
	    		break;
	    	case R.id.cancelButton:
	    		cancelActivity();
	    		break;
	    	case R.id.addButton:
	    		addAttribute();
	    		Log.i("edit attributes", "add clicked");
	    		break;
	    	case R.id.removeButton:
	    		removeAttribute();
	    		break;
	    	case R.id.upButton:
	    		upAttribute();
	    		break;
	    	case R.id.downButton:
	    		downAttribute();
	    		break;
	    	case R.id.upMethodButton:
	    		upMethod();
	    		break;
	    	case R.id.downMethodButton:
	    		downMethod();
	    		break;
	    	case R.id.addMethodButton:
	    		addMethod();
	    		break;
	    	case R.id.removeMethodButton:
	    		removeMethod();
	    		break;
	    	case R.id.upSignalButton:
	    		upSignal();
	    		break;
	    	case R.id.downSignalButton:
	    		downSignal();
	    		break;
	    	case R.id.addSignalButton:
	    		addSignal();
	    		break;
	    	case R.id.removeSignalButton:
	    		removeSignal();
	    		break;
	    	}
	    	
	 }
	 
	 public void addSignal(){
		 EditText signalText = (EditText)findViewById(R.id.editTextsignal);
		 Spinner spinnersignal = (Spinner)findViewById(R.id.signalspinner);
		 String s = signalText.getText().toString();
		 AvatarSignal as = AvatarSignal.isAValidSignal(spinnersignal.getSelectedItemPosition(),s);
		 Log.i("addsignal", "as "+as);
		 AvatarSignal astmp;
		 
		 if (as != null) {
			// Checks whether the same signal already belongs to the list
				int index = -1;
				for(int i=0; i<signalsPar.size(); i++) {
					astmp = (AvatarSignal)(signalsPar.get(i));
					// Same id?
					if (astmp.equals(as)) {
						index = i;
						break;
					}
				}
				
				if (index == -1) {
					signalsPar.add(as);
				} else {
					signalsPar.removeElementAt(index);
					signalsPar.add(index, as);
				}
				updateSignalsList();
				signalText.setText("");	 
		 }else{
			 Toast.makeText(this, "Error: Badly formatted signal declaration", Toast.LENGTH_SHORT).show();
			 return;
			 
		 }
	 }
	 
	 public void addMethod(){
		 EditText methodText = (EditText)findViewById(R.id.methodEditText);
		 String s = methodText.getText().toString();
		 AvatarMethod am = AvatarMethod.isAValidMethod(s);
		 Log.i("addmethod", " " +am);
		 AvatarMethod amtmp;
		 
		 if(am != null){
			 Log.i("addmethod", am.toString());
			// Checks whether the same method already belongs to the list
				int index = -1;
				for(int i=0; i<methodsPar.size(); i++) {
					amtmp = (AvatarMethod)(methodsPar.get(i));
					// Same id?
					if (amtmp.equals(am)) {
						index = i;
						break;
					}
				}
				if (index == -1) {
					methodsPar.add(am);
				} else {
					methodsPar.removeElementAt(index);
					methodsPar.add(index, am);
				}
				updateMethodsList();
				methodText.setText("");
		 }else{
			 Toast.makeText(this, "Error: Badly formatted method declaration", Toast.LENGTH_SHORT).show();
			 return;
		 }
		 
	 }
	 
	 public void closeActivity(){
		 
		 String[] attributes = new String[attributesPar.size()];
		 for(int i=0;i<attributesPar.size();i++){
			 attributes[i] = ((TAttribute)attributesPar.get(i)).toString();
		 }
		 
		 String[] methods = new String[methodsPar.size()];
		 for(int i=0;i<methodsPar.size();i++){
			 methods[i] = ((AvatarMethod)methodsPar.get(i)).toString();
			 Log.i("closeActivity", methods[i]);
		 }
		 
		 String[] signals = new String[signalsPar.size()];
		 for(int i=0;i<signalsPar.size();i++){
			 signals[i] = ((AvatarSignal)signalsPar.get(i)).toString();
		 }
		 
		 Bundle bundle = new Bundle();
		 bundle.putStringArray("attributes", attributes);
		 if(hasMethod){
			 bundle.putStringArray("methods", methods);
			 bundle.putStringArray("signals", signals);
		 }
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
	 
	 public void removeAttribute(){
		 int index = indexAttributeSelected;
	        if (index!= -1) {
	            TAttribute a = (TAttribute)(attributesPar.elementAt(index));
	            a.setAccess(-1);
	            attributesPar.removeElementAt(index);
	            updateAttributesList();
	        }
	 }
	 
	 public void removeMethod(){
		 int index = indexMethodSelected;
	        if (index!= -1) {
	            methodsPar.removeElementAt(index);
	            updateMethodsList();
	        }
	 }
	 
	 public void removeSignal(){
		 int index = indexSignalSelected;
	        if (index!= -1) {
	           
	            signalsPar.removeElementAt(index);
	            updateSignalsList();
	        }
	 }
	 
	 public void downAttribute() {
		 int index = indexAttributeSelected;
		 Log.i("upAttributes", "index: "+index);
		 if((index!= -1) && (index != attributesPar.size() - 1)){
			 Object o = attributesPar.elementAt(index);
			 attributesPar.removeElementAt(index);
			 attributesPar.insertElementAt(o, index+1);
			 updateAttributesList();
		 }
	 }
	 
	 public void downMethod() {
		 int index = indexMethodSelected;
		 Log.i("upAttributes", "index: "+index);
		 if((index!= -1) && (index != methodsPar.size() - 1)){
			 Object o = methodsPar.elementAt(index);
			 methodsPar.removeElementAt(index);
			 methodsPar.insertElementAt(o, index+1);
			 updateMethodsList();
		 }
	 }
	 
	 public void downSignal() {
		 int index = indexSignalSelected;
		 Log.i("upAttributes", "index: "+index);
		 if((index!= -1) && (index != signalsPar.size() - 1)){
			 Object o = signalsPar.elementAt(index);
			 signalsPar.removeElementAt(index);
			 signalsPar.insertElementAt(o, index+1);
			 updateSignalsList();
		 }
	 }
	 
	 public void upAttribute() {
		 int index = indexAttributeSelected;
		 Log.i("upAttributes", "index: "+index);
		 if(index>0){
			 Object o = attributesPar.elementAt(index);
			 attributesPar.removeElementAt(index);
			 attributesPar.insertElementAt(o, index-1);
			 updateAttributesList();
		 }
	 }
	 
	 public void upMethod(){
		 int index = indexMethodSelected;
		 if(index>0){
			 Object o = methodsPar.elementAt(index);
			 methodsPar.removeElementAt(index);
			 methodsPar.insertElementAt(o, index-1);
			 updateMethodsList();
		 }
	 }
	 
	 public void upSignal(){
		 int index = indexSignalSelected;
		 if(index>0){
			 Object o = signalsPar.elementAt(index);
			 signalsPar.removeElementAt(index);
			 signalsPar.insertElementAt(o, index-1);
			 updateSignalsList();
		 }
	 }
	 
	 public void updateMethodsList(){
		 ListView methodsList = (ListView)findViewById(R.id.methodListView);
		 String[] methods = new String[methodsPar.size()];
		 for(int i=0;i<methodsPar.size();i++){
			 methods[i] = ((AvatarMethod)methodsPar.get(i)).toString();
		 }
		 methodsList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.list_item, methods));
		 
		 methodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					for(int i=0;i<arg0.getCount();i++){
						arg0.getChildAt(i).setBackgroundColor(Color.WHITE);
					}
					arg1.setBackgroundColor(Color.BLUE);
					indexMethodSelected = arg2;
					
				}
			});
		 
		 methodsList.invalidate();
		 
	 }
	 
	 public void updateSignalsList(){
		 ListView signalsList = (ListView)findViewById(R.id.signalListView);
		 String[] signals = new String[signalsPar.size()];
		 for(int i=0;i<signalsPar.size();i++){
			 signals[i] = ((AvatarSignal)signalsPar.get(i)).toString();
		 }
		 signalsList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.list_item, signals));
		 
		 
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
	 
	 public void updateAttributesList(){
		 
		 ListView attributesList = (ListView)findViewById(R.id.listView1);
		 String[] attributes = new String[attributesPar.size()];
		 for(int i=0;i<attributesPar.size();i++){
			 attributes[i] = ((TAttribute)attributesPar.get(i)).toString();
		 }
		 attributesList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.list_item, attributes));
		 
		 
		 
		
		 
		// attributesList.setSelector(this.getResources().getDrawable(R.color.list_s));
		 attributesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				for(int i=0;i<arg0.getCount();i++){
					arg0.getChildAt(i).setBackgroundColor(Color.WHITE);
				}
				arg1.setBackgroundColor(Color.BLUE);
				indexAttributeSelected = arg2;
				
			}
		});
		 
		 attributesList.invalidate();
		
		
	 }
	 
	 public void addAttribute(){
		 Log.i("addAttribute", "clicked");
		 Spinner spinneracc = (Spinner)findViewById(R.id.attributeaccspinner);
		 Spinner spinnertype = (Spinner)findViewById(R.id.attributetypespinner);
		 Object o1 = spinneracc.getSelectedItem();
		 Object o2 = spinnertype.getSelectedItem();
		 EditText identifierText = (EditText)findViewById(R.id.identifierEditText);
		 EditText initialValue = (EditText)findViewById(R.id.iniValueEditText);
		 String s = identifierText.getText().toString();
		 String value = initialValue.getText().toString();
		 
		 Log.i("addAttribute", o1.toString());
		 Log.i("addAttribute", o2.toString());
		 Log.i("addAttribute", s);
		 Log.i("addAttribute", value);
		 
		 TAttribute a;
		 
		 if (s.length()>0){
			 if((TAttribute.isAValidId(s, checkKeyword, checkJavaKeyword))){
				 int i = TAttribute.getAccess(o1.toString());
				 int j = TAttribute.getType(o2.toString());
				 Log.i("addAttribute", "i: "+i+" j:"+j);
				 if ((j == TAttribute.ARRAY_NAT) && (value.length() < 1)) {
						value = "2";
				 }
				 if ((i != -1) && (j!= -1)){
					 Log.i("addAttribute", "In i: "+i+" j:"+j);
					 if((value.length() < 1) || (initialValue.isEnabled() == false)){
						 value = "";
					 }else{
						 if (!TAttribute.isAValidInitialValue(j, value)) {
							 Log.i("addAttribute", "not valid i: "+i+" j:"+j);
							 Toast.makeText(this, "Error: The initial value is not valid", Toast.LENGTH_SHORT).show();							 
							 return;
						 }
					 }
					 
					 if (j == TAttribute.OTHER) {
						 a = new TAttribute(i, s, value, o2.toString());
						// a.isAvatar = true;
						 Log.i("addAttribute", a.toString());
					 }else {
						 a = new TAttribute(i, s, value, j);
						// a.isAvatar = true;
						 Log.i("addAttribute", a.toString());
				 	 }
					 
					//checks whether the same attribute already belongs to the list
	                    int index = attributesPar.size();
	                    Log.i("addatrribute", "index: "+index);
	                    if (attributesPar.contains(a)) {
	                        index = attributesPar.indexOf(a);
	                        a = (TAttribute)(attributesPar.elementAt(index));
	                        a.setAccess(i);
	                        if (j == TAttribute.OTHER) {
	                            a.setTypeOther(o2.toString());
	                        }
	                        a.setType(j);                        
	                        a.setInitialValue(value);
	                        //attributes.removeElementAt(index);
	                    }else{
	                    	attributesPar.add(index, a);
	                    	//add in list view!!
	                    	identifierText.setText("");
	                    	Log.i("addAttribute", a.toString());
	                    }
					 
				 }
				 
			 }
		 }
		 
		 updateAttributesList();
		 
	 }
	  
	 
	 
}
