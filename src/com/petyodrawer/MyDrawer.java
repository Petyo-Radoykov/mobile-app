package com.petyodrawer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyDrawer {
	//class variables needed by the drawer
	public DrawerLayout drawerLayout;
	public ListView listView;
	public ActionBarDrawerToggle drawerListener;
	public MyAdapter myAdapter;
	public String mainBarTitle;

	public Activity activity;

	//constructor of the class
	public MyDrawer(Activity activity) {
		//set member variables
		this.activity = activity;
		//gets data form XML into java 
		listView = (ListView) activity.findViewById(R.id.drawerList);

		//instantiate the custom adapter
		myAdapter = new MyAdapter(activity);
		listView.setAdapter(myAdapter);
		
		//All of the activities which will have an instance of this class as
		//a class member variable will implements OnItemClickListener so can be casted to it
		listView.setOnItemClickListener((OnItemClickListener) activity);

		//XML into java
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawerLayout);

		// R.string.drawer_open and R.string.drawer_close are used for
		//accessibility ppurposes e.g. screen readers
		drawerListener = new ActionBarDrawerToggle(activity, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				// Toast.makeText(MainActivity.this, " Drawer Opened ",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				// Toast.makeText(MainActivity.this, " Drawer Closed ",
				// Toast.LENGTH_SHORT).show();
			}

		};

		drawerLayout.setDrawerListener(drawerListener);
		// top left button
		activity.getActionBar().setHomeButtonEnabled(true);
		activity.getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	int lastClicked = -1;
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
				
		//some button was clicked and we will 
		//set its color to the general color
		if (lastClicked != -1) {
            parent.getChildAt(lastClicked).setBackgroundColor(
                    Color.parseColor("#333333"));
        }

		//changes color on currently  clicked button
		parent.getChildAt(position).setBackgroundColor(
                Color.parseColor("#992418"));

        
        lastClicked = position;
		
		
		//here we make sure the drawers will close automatically after item click
		////////////////////////////////////////////////////////////////////////
		DrawerLayout drawerLayout;
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawerLayout);
		drawerLayout.closeDrawers();
		////////////////////////////////////////////////////////////////////////
        
		String title;
		View row = view;
		title = ((TextView)row.findViewById(R.id.textView1)).getText().toString();
		
		////////////////////////////////////////////////////////////////////////
		mainBarTitle = title;
		////////////////////////////////////////////////////////////////////////
		
		selectItem(position);
		setTitle(title);
		
		//depending on the position of the item clicked different activity will be started
		if(position == 0){
			Intent myIntent = new Intent(activity, MainActivity.class);
			activity.startActivity(myIntent);	
		}  else if(position == 1){
			Intent myIntent = new Intent(activity, CheckNotesActivity.class);
			activity.startActivity(myIntent);
		} else if(position == 2){
			Intent myIntent = new Intent(activity, NewNoteActivity.class);
			activity.startActivity(myIntent);
		} else if(position == 3){
			Intent myIntent = new Intent(activity, ManageNotesActivity.class);
			activity.startActivity(myIntent);
		}else if(position == 4){
			 ///////////////////////////////////////////////////////////////////////////////////////////////////
 	 		/*
 			* adding dialog box to prompt the user whether they would like to leave the app
 			* */
 			new AlertDialog.Builder(activity)
 			.setTitle("Exit the app")
 			.setMessage("Would you like to exit the app?")
 			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
 			public void onClick(DialogInterface dialog, int which) { 
 				activity.moveTaskToBack(true);
 				System.exit(0);
 			}
 			})
 			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
 			public void onClick(DialogInterface dialog, int which) { 
 				// close dialog
 				dialog.cancel();
 			}
 			})
 			.setIcon(android.R.drawable.ic_dialog_alert)
 			.show();
 			//////////////////////////////////////////////////////////////////////////////////////////////////////////
 	 	
		}
		
	}

	public void selectItem(int position) {
		listView.setItemChecked(position, true);

		
	}
	
	public void setTitle(String title){  
		activity.getActionBar().setTitle(title);
	}
	 
}
