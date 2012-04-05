package project.alwaystry;

import android.app.Dialog;
import android.content.Context;
import android.widget.TabHost;
import android.widget.TextView;

public class EditAttributesDialog extends Dialog{

	String name;
	public EditAttributesDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}
	public void setName(String n){
		name = n;
	}
	
	public void organizeContent(){
		setTitle("setting attributes of "+name);
		setContentView(R.layout.editattributesalert);
		
		TabHost tabHost = (TabHost)findViewById(R.id.tabhost1);
		tabHost.setup();
		
		TextView textView = (TextView)findViewById(R.id.textView1);
		textView.setText("Tabs!!!");
		
		TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
		spec1.setIndicator("Attributes");
		spec1.setContent(R.id.textView1);
		tabHost.addTab(spec1);
		
		TabHost.TabSpec spec2 = tabHost.newTabSpec("tab1");
		spec2.setIndicator("Methods");
		spec2.setContent(R.id.textView1);
		tabHost.addTab(spec2);
		
		TabHost.TabSpec spec3 = tabHost.newTabSpec("tab1");
		spec3.setIndicator("Signals");
		spec3.setContent(R.id.textView1);
		tabHost.addTab(spec3);
	}
	
	

}
