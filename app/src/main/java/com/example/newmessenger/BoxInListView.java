package com.example.newmessenger;



import android.os.Bundle;
import android.app.ListActivity;
import android.view.Menu;

public class BoxInListView extends ListActivity {

	//this is the friends list where it will inflate all the friends into the list veiw
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_box_in_list_view);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.box_in_list_view, menu);
		return true;
	}



}
