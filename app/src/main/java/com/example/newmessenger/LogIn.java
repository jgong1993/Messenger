package com.example.newmessenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LogIn extends Activity {

	EditText userview;
	EditText passview;
	TextView logview;

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		Parse.initialize(this, "XiL7HDDRWmaBsc9WxysuM3th7jK87FwKFpZUXWYq", "Lh0pfZ23qVuvZUxE2OqvhXyDlKjxREBc5nAz4xjU");
        logIn();
		register();
	}
	
	
	
	public void logIn(){
		userview = (EditText) findViewById(R.id.enteredUser);
		passview = (EditText) findViewById(R.id.editText2);
		logview = (TextView) findViewById(R.id.loginstat);
		Button logInButton = (Button) findViewById(R.id.LoginButtonID);
		logInButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				ParseUser.logInInBackground(userview.getText().toString(), passview.getText().toString(), new LogInCallback(){
					public void done(ParseUser user, ParseException e){
						if(user != null){
							Log.d("Login ", "Success");
							logBut();
						}
						else{
							logview.setText("Something Be Wrong");
	                        Log.d("Error",e.getLocalizedMessage());

						}
					}
				});
			}
		});
	}

	public void register(){
		Button registerButton = (Button) findViewById(R.id.LoginReg);
		registerButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				regBut();
			}
		});
	}
	// GO TO Menu
	public void logBut(){
		finish();
		startActivity(new Intent(this,Menu_screen.class));
	}
	public void regBut(){
        finish();
        startActivity(new Intent(this, DisplayMessageActivity.class));
	}
	

	
	
}

