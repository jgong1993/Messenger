package com.example.newmessenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class arrayAdapter extends ArrayAdapter<String>{

	private Context context;
	private List<String> name;
	private Map<String, String> topicsCreated;
    private String user;

	public arrayAdapter(Context context, List<String> name, Map<String,String> topicsCreated, String user)
	{
		super(context, R.layout.activity_box_in_list_view, name);
		this.context = context;
		this.name = name;
        this.topicsCreated = topicsCreated;
        this.user = user;
	}
	

	public View getView(int position, View convertView, ViewGroup parent){
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_box_in_list_view, parent, false);
		final int pos = position;
		TextView  nameView = (TextView) rowView.findViewById(R.id.textView1);
        nameView.setText(name.get(position));
		rowView.setTag(name.get(position));
		rowView.setTag(name.indexOf(position));
		rowView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
                Log.d("Position", "Position: " + topicsCreated.get(name.get(pos)));
				Intent intent = new Intent(context, DemoChatClient1.class);
                Bundle b = new Bundle();
                b.putString("topic",topicsCreated.get(name.get(pos))); // TEAM/25/[..]
                b.putString("user1", user);                            // Current user
                b.putString("user2", name.get(pos));                   // Username clicked
                intent.putExtras(b);
				context.startActivity(intent);
			}
		});
		
		return rowView; 
	}
}
