package com.petyodrawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NewNoteActivity extends Activity implements OnItemClickListener{
	private String mainBarTitle;
	private MyDrawer myDrawer;
	
	/*
	 * class variables to be used for the pupposes of location
	 * */
	public double lat, lon;
	public Location currentLocation;
	
	/*
	 * class variables used to display the location
	 * */
	private TextView textLatitude;
	private TextView textLongitude;
	
	/*
	 * class variable used for data access
	 * */
	private DatabaseHelper dbh;
	
	/*
	 * String values to be passes to next activity
	 * the next activity will update the note record if is
	 * invoked
	 * */
	 String note_titleStr = ""; 
	 String noteStr = ""; 
	 String longitudeStr = ""; 
	 String latitudeStr = ""; 
	 String dateStr = ""; 
	 long id;
     

	/*
	 * method used to get the location of the phone
	 * */
	public void showLocation(){
	      LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	 
	      LocationListener locL = new MyLocationListener();
	      //this criteria most likely will give us a passive lock
	      Criteria criteria = new Criteria();
	      criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	      criteria.setPowerRequirement(Criteria.POWER_LOW);
	      criteria.setAltitudeRequired(false);
	      criteria.setBearingRequired(false);
	      criteria.setSpeedRequired(false);
	      criteria.setCostAllowed(true);
	 
	      String bestProvider = lm.getBestProvider(criteria, true);
	 
	      lm.requestLocationUpdates(bestProvider,0,0,locL);
	 }
	
	//inner class -  by the use of privite inner class we get extra layer of security 
	//since only this class can access the methods of the inner class
	private class MyLocationListener implements LocationListener {
		 @Override
		 public void onLocationChanged(Location loc) {
		 currentLocation = loc;
		 lat = currentLocation.getLatitude();
		 lon = currentLocation.getLongitude();
		 
		 /*
		  * setting the values of the text views after they have been created in onCreate()
		  * */
		 textLatitude.setText(String.valueOf(lat)); 
		 textLongitude.setText(String.valueOf(lon)); 
		 
		             
		 }
		 
		 @Override
		 public void onProviderDisabled(String provider) {}
		 
		 @Override
		 public void onProviderEnabled(String provider) {}
		 
		 @Override
		 public void onStatusChanged(String provider, int status, Bundle extras) {}
		  }
	
	/*
	 * used to save states of the object 
	 * */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mainBarTitle", mainBarTitle);
	}

	//here is where the life cycle of the activity starts
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_note);	
		
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
			
			/*
			 * gets the java representation of the text views 
			 * */
			textLatitude = (TextView) findViewById(R.id.latitude);
			textLongitude = (TextView) findViewById(R.id.longitude);
			
			/*
			 * shows the location
			 * */
			showLocation();
			
			/*
			 * create a new instance of the DatabaseHelper class and sets
			 * its class variables OpenHelper mDbHelper and SQLiteDatabase mDb
			 * 
			 **/
			 dbh = new DatabaseHelper(this);
		     dbh.open();    
			
	}
	
	
	/*
	 * method used to insert data into the database
	 * */
	public long insertRecord(View v){
		
		
		
		  String state = "";
		//get XML into java
	      EditText note_title = (EditText) findViewById(R.id.title);
	      EditText note = (EditText) findViewById(R.id.note);
	      TextView longitude = (TextView) findViewById(R.id.longitude);
	      TextView latitude = (TextView) findViewById(R.id.latitude);
	      long id = 0;
	     
	      /*
	       * initiates the member variables from the values present in the form
	       * */
	      longitudeStr = longitude.getText().toString(); 
	      latitudeStr = latitude.getText().toString(); 
	      note_titleStr = note_title.getText().toString(); 
	 	  noteStr =  note.getText().toString();
	 	   
	 	  //get current date
	 	  dateStr = (String) android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
	 
	 	  //adds a record associated with the note to the database
	 	 id = dbh.createNote(longitudeStr,
	    		  				   latitudeStr,
	    		                   note_titleStr,
	    		                   noteStr,
	    		                   dateStr,
	                               "none");
 		  
		     
	     
	     return id;
	 
	    }
	
	
	/*
	 * method to handle click on add note button
	 * */
	public void addNote(View v){
		String noteInsertedState = "";
		/////////////////////////////////////////////////////////////////////////////////////////
		/*
		 * save the note to the internal database
		 * */

		id = insertRecord(v);
		
		if (id != 0) {
			noteInsertedState = "Note Saved";
	     } else {
	    	 noteInsertedState = "Note NOT Saved";
	     }
		/*
		* adding dialog box to prompt the user whether they would like to take a photo
		* */
		new AlertDialog.Builder(this)
		.setTitle(noteInsertedState)
		.setMessage("Would you like to add a photo?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) { 
			// open Take A Photo activity
			Intent myIntent = new Intent(NewNoteActivity.this, PhotoActivity.class);
			/*
			 * passes the values to the next activity - PhotoActivity
			 * */
			String idSTRING = id + "";
			
			myIntent.putExtra("longitude", longitudeStr);
			myIntent.putExtra("latitude", latitudeStr);
			myIntent.putExtra("note_title", note_titleStr);
			myIntent.putExtra("note", noteStr);
			myIntent.putExtra("date", dateStr);
			myIntent.putExtra("id", idSTRING);
			NewNoteActivity.this.startActivity(myIntent);
		}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) { 
			// close dialog
			dialog.cancel();
			//relocate to the check notes with the current note shown
			/*
			 * passes the values to the next activity
			 * */
			Intent intent = new Intent(NewNoteActivity.this, ManageNotesActivity.class);
			String[] selectedListItem = {longitudeStr, latitudeStr, note_titleStr, noteStr, dateStr, "none", id+""};
	        intent.putExtra("listRow", selectedListItem);
	        NewNoteActivity.this.startActivity(intent);
		
		}
		})
		.setIcon(android.R.drawable.ic_input_add)
		.show();
		/////////////////////////////////////////////////////////////////////////////////////////
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
	public void onItemClick(final AdapterView<?> parent, final View view, final int position,
			final long id) {
		
/////////////////////////////////////////////////////////////////////////////////////////
		/*
		 * adding dialog box in case they want to leave activity
		 * */
		new AlertDialog.Builder(this)
	    .setTitle("Go to another activity")
	    .setMessage("Your note is not going to be saved")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // relocate to another activity
	        	myDrawer.onItemClick(parent, view, position, id);
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	            dialog.cancel();
	            //close drawer
	            myDrawer.drawerLayout.closeDrawers();

	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
/////////////////////////////////////////////////////////////////////////////////////////
		
		
	}

/////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////|END OF CODE RELATED TO DRAWER|///////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////	
	

}



