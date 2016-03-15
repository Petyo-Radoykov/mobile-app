package com.petyodrawer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{
	private String mainBarTitle;
	private MyDrawer myDrawer;

	/*
	 * used to save states of the object 
	 * */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mainBarTitle", mainBarTitle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		////////////////////////////////////////////////////
		/*
		 * savedInstanceState -> checks if there were any states saved from 
		 * this.onSaveInstanceState(Bundle outState)
		 * */
			
			//if savedInstanceState never loaded with any values
			if(savedInstanceState == null){
				
			} else {
				//gets the value stored in counter or 0
				mainBarTitle = savedInstanceState.getString("mainBarTitle");
				getActionBar().setTitle(mainBarTitle);
			}
		////////////////////////////////////////////////////
			
			myDrawer = new MyDrawer(this);
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////////////|CODE RELATED TO DRAWER|///////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * makes the drawer pop up when the home button is pressed
	 * */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * if the item was selected this
		 * will cause the drawer to toggle
		 * */
		if(myDrawer.drawerListener.onOptionsItemSelected(item)){
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		/*
		 * when configurations change e.g. screen rotation
		 * notify the drawer listener and it will take care 
		 * adapting
		 * */
		myDrawer.drawerListener.onConfigurationChanged(newConfig);
				    
	}
	
	/*
	 * makes the icon indicating that there is a drawer show and slide with the drawer
	 * */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		myDrawer.drawerListener.syncState();
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		myDrawer.onItemClick(parent, view, position, id);
	}

/////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////|END OF CODE RELATED TO DRAWER|///////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////	

}


