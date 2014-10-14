package com.example.newmessenger;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.fusesource.mqtt.client.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu_screen extends ListActivity {

    List<String> name2 = new ArrayList<String>(); // array list for display
	EditText newUserAdd;  // Enter Username box
    String newUserAddString; // Enter username box's STRING
    arrayAdapter adapter;  // array adapter for update
    private static final int EXIT_ID = 0;  // ID for back button
    ActiveMQ connection;  // connection
    Button connectButton;  // Connect Button
    Button buttonB;        // Add Friend Button
    private Map<String,String> topicsCreated = new HashMap<String,String>(); // Hashmap that will be loaded with topics
    ParseObject post;      // ParseObject containing friends

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_menu);

		Parse.initialize(this, "XiL7HDDRWmaBsc9WxysuM3th7jK87FwKFpZUXWYq", "Lh0pfZ23qVuvZUxE2OqvhXyDlKjxREBc5nAz4xjU");
        topicsCreated.put("Public Main Chat Room", Constants.CLIENT_TOPICNAME);
        name2.add("Public Main Chat Room");
        setUpTopics();
        ParseUser user2 = ParseUser.getCurrentUser();
		adapter = new arrayAdapter(this,name2, topicsCreated,user2.getUsername());
		this.setListAdapter(adapter);
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
	}

    private void setUpTopics()
    {
        ParseQuery<ParseObject> g2query = ParseQuery.getQuery("Friends");
        ParseUser currentUser2 = ParseUser.getCurrentUser();
        g2query.whereEqualTo("userName",currentUser2.getUsername());
        List<ParseObject> list;
        try{
            list = g2query.find();
            if (list.size() != 0) {
                for(int i =0; i <list.size();i++) {
                    // Creating hashmap of created topics for connection
                    topicsCreated.put(list.get(i).getString("frienduserName"),
                            toCheese(list.get(i).getString("frienduserName")));
                    // Array list of names for display
                    name2.add(list.get(i).getString("frienduserName"));
                }
            } else {
                Log.d("Fail", "aaa");
            }
            //toast("# Of Friends: " + topicsCreated.size());
        }catch(Exception e){
            Log.e("Error inside setUpTopic", e.toString());
        };
    }

    /* This is for a dialog popup to ask if the user wants to exit the program */
    @SuppressWarnings("deprecation")
	@Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        switch(id) {
        case EXIT_ID:
            dialog = new AlertDialog.Builder(this).setMessage(
                                "Do you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Menu_screen.this.finish();
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

    private void toast(String message)
	{
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
    <T> Callback<T> onui(final Callback<T> original) {
		return new Callback<T>() {
			public void onSuccess(final T value) {
				runOnUiThread(new Runnable(){
					public void run() {
						original.onSuccess(value);
					}
				});
			}
			public void onFailure(final Throwable error) {
				runOnUiThread(new Runnable(){
					public void run() {
						original.onFailure(error);
					}
				});
			}
		};
	}

    /*
	public void connect(View view)
	{
		connectButton = (Button) findViewById(R.id.connectButton);
    	connection = new ActiveMQ(this);
		connection.connect(new Callback<Void>(){
			public void onSuccess(Void value) {
				toast("Connected");
				connectButton.setEnabled(false);
				
				connection.subscribe(Constants.CLIENT_TOPICNAME, new Callback<byte[]>() {
					
					@Override
					public void onSuccess(byte[] arg0) {
						toast("Subscribed to Queue "+ Constants.CLIENT_TOPICNAME);
					}
					
					@Override
					public void onFailure(Throwable arg0) {
						toast("Failed subscription to Queue "+ Constants.CLIENT_TOPICNAME + " - " + arg0.getLocalizedMessage());
					}
				});
				
			}
			public void onFailure(Throwable e) {
				toast("Problem connecting to host");
				//Log.e("Error", "Exception connecting to ActiveMQ - " + e);
			}
		});
	}
*/

    public void clear() {
        newUserAdd.setText("");
    }

    public String toCheese(String something)
    {
        String cheese = "TEAM/25/" + something;
        System.out.println(cheese);
        return cheese;
    }



    // NEED TO UPDATE THE TOPIC AFTER FRIEND ADD
	public void addFriend(View view){
        Log.d("AFTER adapter", "#: " + topicsCreated.size());
        buttonB = (Button) findViewById(R.id.button11);
        newUserAdd = (EditText)findViewById(R.id.enteredUser);
        newUserAddString = newUserAdd.getText().toString();

        // Accessing database where usernames are stored
        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username", newUserAdd.getText().toString().toLowerCase());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() != 0) {
                    // Checking if the user is already added on friendlist
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    for(int i = 0; i < name2.size(); i++)
                        if(newUserAddString.equalsIgnoreCase(name2.get(i))) {
                            toast("User is already added");
                            clear();
                            return;
                        }

                    name2.add(newUserAddString);
                    post = new ParseObject("Friends");

                    // Adding information if the friend isn't added
                    ParseQuery<ParseObject> gquery = ParseQuery.getQuery("Friends");
                    gquery.whereEqualTo("friendID", new String(toCheese(newUserAddString)));
                    gquery.whereEqualTo("username", currentUser.getUsername());
                    gquery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                Log.d("Good", "You good");
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                post.put("userName", currentUser.getUsername());
                                post.put("frienduserName",newUserAddString);
                                post.put("friendID",new String(toCheese(newUserAddString)));
                                post.saveInBackground();

                            } else {
                                Log.d("Fail", "Same friend");
                            }
                        }
                    });
                    clear();
                    adapter.setNotifyOnChange(true);
                    adapter.notifyDataSetChanged();
                } else {
                    toast("User does not exist");
                    clear();
                }
            }
        });
	}
}
