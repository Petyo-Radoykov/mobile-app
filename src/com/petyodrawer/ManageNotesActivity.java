 package com.petyodrawer;

 import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//this part of the class is identical to the activities documented 
 public class ManageNotesActivity extends Activity implements OnItemClickListener{
 	private String mainBarTitle;
 	private MyDrawer myDrawer;
 	
 	private String[] recievedListRow;
 	private TextView title;
 	
 	//database connection object
    DatabaseHelper dbh;
 	
 	
    //this method is used to transform the XML structure 
    //represented into manage_notes file into java menu objects
 	@Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 		getMenuInflater().inflate(R.menu.manage_notes, menu);
 		return true;
 	}
 	
 			
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
 		setContentView(R.layout.activity_manage_notes);
 		
 		//here we connect the XMML layout to the java code
 		title = (TextView) findViewById(R.id.title);
 		ImageView jpgView  = (ImageView) findViewById(R.id.imageView);
 		TextView note = (TextView) findViewById(R.id.note);
 		/*
 		 * data received from prev activity
 		 * */
 		
 		//connecting to the database only if needed (see comment above)
		 dbh = new DatabaseHelper(this);
		 dbh.open(); 
 		
 		Intent intent = getIntent();
 		if(intent != null){
 			//we get the data send from other activity
 			//it might be empty if on other activity has sent information
 			//N.B. All of the information coming from other activities of the database
 			//is going to be stored in uniform manner into the same string array
 			//which has been declared as a class member variable and had global scope
 			//within the class 
 			recievedListRow = intent.getStringArrayExtra("listRow");
 		}
	 		//title.setText(Arrays.toString(recievedListRow).replaceAll("\\[|\\]", ""));
	 		if(recievedListRow == null){
		 		/*
		 		 * if there was not any data sent from other activity
		 		 * this means we have started opting for manage notes
		 		 * so by default get the last note entered
		 		 * */
	 			
	 			 //taking the ID of the last inserted element
	 			// long maxID = dbh.getMaxID();
	 								
	 			ArrayList<String[]> temp = dbh.selectAll();	 			
	 			if(temp.size() != 0){
	 				/*
	 				 * get the first element since the output of
	 				 * sellectAll is sorted desc
	 				 * */
	 				String[] swap = temp.get(0);
	 				
	 				recievedListRow = new String[7];
	 				
	 				//reorganizing the data the confirm with the common rule
	 				for(int i = 0; i < recievedListRow.length - 1; i++){
	 					recievedListRow[i] = swap[i + 1];
	 				} 
	 				//last string dedicated to the id
	 				recievedListRow[6] = swap[0];
	 				
	 			} else {
	 				Toast.makeText(getApplicationContext(), "Empty Database", Toast.LENGTH_LONG).show();
	 			}
	 			
	 		}	
	 		
	 		//We checked above for recievedListRow == null but still after the database query 
	 		//the recievedListRow String array can still be empty
	 		//in case the database was empty - the user has never saved notes
	 		if(recievedListRow != null){
	 			if(!recievedListRow[2].equalsIgnoreCase("")){
		 			title.setText(recievedListRow[2]);
		 		}
		 		
		 		if(!recievedListRow[3].equalsIgnoreCase("")){
		 			note.setText(recievedListRow[3]);
		 		}
		 		/*
		 		 * checking to see if any data was passed and if there is a photo associated with the 
		 		 * note
		 		 * */
		 		if(!recievedListRow[5].equalsIgnoreCase("") && !recievedListRow[5].equalsIgnoreCase("none")){
			 		//get photos saved into the external memory
		 			File imageFile = new File(recievedListRow[5]);
			 		Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			 		jpgView.setImageBitmap(bitmap);
		 		}
	 		}
	 		
	 		
 		
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
 	
 	
 	
 	
 	/*
 	 * this method is used to delete the selected note
 	 * */
 	 public void deleteRow(MenuItem item){
 		 if(recievedListRow == null){
 			Toast.makeText(getApplicationContext(), "There is not note to be deleted", Toast.LENGTH_LONG).show();
 	 	 } else {
 			 //delete current record from DB
 	 		 ///////////////////////////////////////////////////////////////////////////////////////////////////
 	 		/*
 			* adding dialog box to prompt the user whether they would like to delete note
 			* */
 			new AlertDialog.Builder(this)
 			.setTitle("Delete Current Note")
 			.setMessage("Would you like to delete this note?")
 			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
 			public void onClick(DialogInterface dialog, int which) { 
 				// DELETE
 				
 				long id = Long.valueOf(recievedListRow[6].toString()).longValue();
 				//Toast.makeText(getApplicationContext(), "id BEFORE DELETE = " + id, Toast.LENGTH_LONG).show();
 				dbh.deleteRecord(id);
 				//relocate after deletion
 				Intent myIntent = new Intent(ManageNotesActivity.this, CheckNotesActivity.class);
 				ManageNotesActivity.this.startActivity(myIntent);
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



 

	
 