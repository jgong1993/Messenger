package com.example.newmessenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;


public class DisplayMessageActivity extends Activity {
	TextView numOfUsers;
	TextView regview;
	EditText userview;
	EditText passview;
	EditText emailview;
	Button eCheck;
	Button bBut;
	Button reg;
	ParseObject one;
	private static final int EXIT_ID = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "XiL7HDDRWmaBsc9WxysuM3th7jK87FwKFpZUXWYq", "Lh0pfZ23qVuvZUxE2OqvhXyDlKjxREBc5nAz4xjU");
		setContentView(R.layout.activity_display_message);

		createID();
		deleteID();
		bBut = (Button) findViewById(R.id.backbut);
		bBut.setVisibility(View.INVISIBLE);
		
		
		

	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        switch(id) {
        case EXIT_ID:
            dialog = new AlertDialog.Builder(this).setMessage(
                                "Do you want to go back to the login page?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    goLog();
                }
            })
            .setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }
 
    // manages key presses not handled in other Views from this Activity
    @SuppressWarnings("deprecation")
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
        	showDialog(EXIT_ID);
        }
        return true;
    }

	public void goLog(){
		finish();
		startActivity(new Intent(this,LogIn.class));
		
	}
	// Create the ID
	public void createID(){
		userview = (EditText)  findViewById(R.id.userName);
        passview = (EditText)  findViewById(R.id.passwordThing);	
        emailview = (EditText) findViewById(R.id.emailThing);
        
        regview = (TextView)   findViewById(R.id.reg_status);
		reg = (Button) findViewById(R.id.submit);
		reg.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				String uview = userview.getText().toString();
		        String pview = passview.getText().toString();
		        String eview = emailview.getText().toString();
				if(uview.matches(""))
				{
					Log.d("Print", uview);
					regview.setText("Username missing");
				    return;
				}
		        if(pview.matches(""))
		        {
		        	regview.setText("Password missing");
		        	return;
		        }
			    if(eview.matches(""))
			    {
			    	regview.setText("Email missing");
			    	return;
			    }
                if(uview.length() < 4)
                {
                    regview.setText("You must have at least 4 characters for your username.");
                    return;
                }
                if(uview.length() >7)
                {
                    regview.setText("USERNAME TOO LONG. WHY DO YOU NEED SUCH A LONG PASSWORD?!");
                    return;
                }

		
				ParseUser user = new ParseUser();
				user.setUsername(userview.getText().toString().toLowerCase());
				user.setPassword(passview.getText().toString().toLowerCase());
				user.setEmail(emailview.getText().toString().toLowerCase());		
				user.signUpInBackground(new SignUpCallback(){
					public void done(ParseException e){
						if(e == null)
						  {
							 Log.d("Signup", "Correct");
							 regview.setText("SUCCESS! Click the button below to log in!");
							 bBut.setVisibility(View.VISIBLE);
							 reg.setVisibility(View.INVISIBLE);



							 bBut.setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									goLog();
								}
							});
						  }
						else
						  {
							switch(e.getCode()){
							case ParseException.USERNAME_TAKEN:
					            regview.setText("Username Taken");
					            break;
							case ParseException.USERNAME_MISSING:
					            regview.setText("Username Missing");
					            break;
							case ParseException.PASSWORD_MISSING:
						        regview.setText("Password Missing");
						        break;
							case ParseException.EMAIL_TAKEN:
								regview.setText("Email Taken");
								break;
							case ParseException.EMAIL_MISSING:
							    regview.setText("Email Missing");
							    break;
							case ParseException.EMAIL_NOT_FOUND:
						        regview.setText("Email Not Found");
						        break;
							case ParseException.CONNECTION_FAILED:
								regview.setText("No Connection");
								break;
							case ParseException.INVALID_EMAIL_ADDRESS:
								regview.setText("Invalid Email");
								break;
							default:
								//Log.d("Type of error", e.toString());
								regview.setText("What.. Your error is: " + e.toString());
								return;
							}
							return;
					      }
					}
				});

			}
			
		}
		
				);
	}

	
	public void deleteID(){
		Button dell = (Button)findViewById(R.id.delBut);
		dell.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
			final ParseObject copy = getID();
			if(copy==null || copy.getObjectId() == null)
				return;
			ParseQuery<ParseObject> query = ParseQuery.getQuery("User_ID");
			query.whereEqualTo("Username", copy);
			query.findInBackground(new FindCallback<ParseObject>() {
			    public void done(List<ParseObject> scoreList, ParseException e) {
			        if (e == null) {
			        	ParseObject.createWithoutData("User_ID", copy.getObjectId()).deleteEventually();
						Log.d("User_ID", "ID: " + copy.getObjectId());			        } 
			        else {
			            Log.d("score", "Error: " + e.getMessage());
			        }
			    }
			});
			
			
			}
		});
    }
	
	public ParseObject getID(){
		return one;
	}
	public void setID(ParseObject two){
		one = two;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_message, menu);
		return true;
	}

}
