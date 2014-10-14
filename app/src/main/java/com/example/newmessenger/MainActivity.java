package com.example.newmessenger;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toast;


public class MainActivity extends Activity {
    int i = 0;
    TextView view;
    
    @SuppressLint("InlinedApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
     View decorView = getWindow().getDecorView();
     // Hide the status bar.
     int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
     decorView.setSystemUiVisibility(uiOptions);
     // Remember that you should never show the action bar if the
     // status bar is hidden, so hide that too if necessary.
     ActionBar actionBar = getActionBar();
     actionBar.hide();
     
     RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.main);
     rlayout.setOnClickListener(new OnClickListener(){
    	 public void onClick(View v)
    	 {
    		sendMessage();
    	 }
     });
    }

    public void sendMessage(){
    	finish();
        startActivity(new Intent(this,LogIn.class));
    }

}
