package project.alwaystry;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class BDTabWidget extends TabActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);

       // Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, AlwaystryActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("avatarbd").setIndicator("Avatar BD")
                      .setContent(intent);
        tabHost.addTab(spec);
        intent = new Intent().setClass(this, AnotherTagActivity.class);
        spec = tabHost.newTabSpec("anotherTag").setIndicator("Another Tag")
                .setContent(intent);
        tabHost.addTab(spec);

    }
    
    
    
}
