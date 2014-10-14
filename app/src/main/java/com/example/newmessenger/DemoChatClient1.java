package com.example.newmessenger;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.fusesource.mqtt.client.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DemoChatClient1 extends Activity {

    private ActiveMQ connection;
    private final String TAG = "TEST WHAT WHAT WHAT";
    private EditText msgText;
    private EditText outputText;
    private Button sendButton;
    private String chatLogTitle;
    private String user1, user2;
    List<String> chatMessages = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_chat_client1);
        Bundle b = getIntent().getExtras();
        Parse.initialize(this, "XiL7HDDRWmaBsc9WxysuM3th7jK87FwKFpZUXWYq", "Lh0pfZ23qVuvZUxE2OqvhXyDlKjxREBc5nAz4xjU");
        //topicToEnter = b.getString("topic");
        user1 = b.getString("user1");
        user2 = b.getString("user2");
        //chatLogTitle = toCheese(user1,user2);
        chatLogTitle = "TEAM/25/" + user2;
        TextView userTxtview = (TextView)findViewById(R.id.userTextView);
        userTxtview.setText(user1);
        connect();

    }

    private void setUpTopics()
    {
        ParseQuery<ParseObject> g2query = ParseQuery.getQuery("ChatLog");
        g2query.whereEqualTo("ConnectionName", chatLogTitle);
        List<ParseObject> list;
        try{
            list = g2query.find();
            if (list.size() != 0) {
                for(int i =0; i <list.size();i++) {
                    chatMessages.add(list.get(i).get("Person").toString() + ": " + list.get(i).get("Message"));

                }
            } else {
                Log.d("Fail", "aaa");
            }
        }catch(Exception e){
            Log.e("Error inside setUpTopics", e.toString());
        };
    }


    public String toCheese(String some1, String some2)
    {
        String cheese= "";
        int num = some1.compareTo(some2);
        if(num < 0)
            cheese = "TEAM/25/" + some1 +""+some2;
        if(num > 0)
            cheese = "TEAM/25/" + some2+""+some1;

        return cheese;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void connect() {

        outputText = (EditText) findViewById(R.id.textOutput);
        outputText.setBackground(null);
        connection = new ActiveMQ(this);
        ParseQuery<ParseObject> g2query = ParseQuery.getQuery("ChatLog");
        g2query.whereEqualTo("ConnectionName", chatLogTitle);
        List<ParseObject> list;
        try{
            list = g2query.find();
            if (list.size() != 0) {
                for(int i =0; i <list.size();i++) {
                    //chatMessages.add(list.get(i).get("Person").toString() + ": " + list.get(i).get("Message"));
                    outputText.append(list.get(i).get("Person").toString() + ": " + list.get(i).get("Message"));
                    outputText.append("\n\n");
                }
            } else {
                Log.d("Fail", "aaa");
            }
        }catch(Exception e){
            Log.e("Error inside setUpTopics", e.toString());
        };
        // Pushing message to the textbox
        connection.setListener(new MessageListener() {
            @Override
            public void onMessage(Serializable msg) {
                msgText = (EditText) findViewById(R.id.textInput);

                outputText.append(msg.toString());
                outputText.append("\n\n");
                final ScrollView sv = (ScrollView)findViewById(R.id.scrollView1);
                sv.post(new Runnable() {
                    public void run() {
                        sv.fullScroll(sv.FOCUS_DOWN);
                    }
                });

            }
        });
        connection.connect(new Callback<Void>() {
            public void onSuccess(Void value) {
                connection.subscribe(Constants.CLIENT_TOPICNAME, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] arg0) {
                        Log.d("Successful subscription", "Subscribed to Topic " + Constants.CLIENT_TOPICNAME);
                    }
                    @Override
                    public void onFailure(Throwable arg0) {
                        toast("Failed subscription to Topic " + Constants.CLIENT_TOPICNAME + " - " + arg0.getLocalizedMessage());
                    }
                });
            }

            public void onFailure(Throwable e) {
                toast("Problem connecting to host");
                Log.e(TAG, "Exception connecting to ActiveMQ - " + e);
            }
        });
    }


    public void clear() {
        msgText.setText("");
    }

    public void send(View view) {

        if (connection != null) {
            msgText = (EditText) findViewById(R.id.textInput);
            String msgText2 = user1 + ":    " + msgText.getText().toString();
            connection.send(Constants.SERVER_TOPICNAME, msgText2, new Callback<Void>() {
                @Override
                public void onSuccess(Void arg0) {
                    ParseObject chatLog = new ParseObject("ChatLog");
                    chatLog.put("ConnectionName", chatLogTitle);
                    chatLog.put("Person", user1);
                    chatLog.put("Message", msgText.getText().toString());
                    chatLog.saveInBackground();
                    clear();
                }

                @Override
                public void onFailure(Throwable arg0) {
                    toast("Error sending the message: " + arg0.getLocalizedMessage());
                }
            });
        } else {
            toast("No connection has been made, please create the connection");
        }
    }



}