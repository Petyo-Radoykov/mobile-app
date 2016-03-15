// this is the name of the package where all of the code is located
package com.petyodrawer;

//additional classes and methods included 
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

//this class is a subclass of abstract super class Activity and overrides some of its methods
//it implements OnItemClickListener in order to attach event handlers to the drawer menu
public class CheckNotesActivity extends Activity implements OnItemClickListener{
	//class member variables 
	// this holds the title
	private String mainBarTitle;
	//an instance of the drawer class used 
	private MyDrawer myDrawer;

	/*
	 * class variables used to access the database and store the data returned
	 * */
	private DatabaseHelper dbh;
	private ArrayList<String> listItems = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
    
    /*
     * we need access to this variable within different parts of the class
     * that is why it is a class member variable
     * however, we do not want it to be directly accessible from outside
     * of the class and that is why it has been declared as private
     * */
	private ListView lv;
	
	/*
	 * used to be populated dynamically
	 * the data stored will be extracted at on save state
	 * */
	private ArrayList<String[]> allSelectedListItems = new ArrayList<String[]>();
	private String[] selectedListItem;
	
	/*
	 * used to save states of the object 
	 * the class is re instantiated every time the screen is rotated
	 * different object are saved in to a Bundle object
	 * which is passes to the on create method which is at the beginning of
	 * the life cycle of the activity
	 * 
	 * */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("mainBarTitle", mainBarTitle);

		/*
		 * using this to save the text in the list view on rotation of screen
		 * */
		outState.putStringArrayList("savedList", listItems);
		
		
	}

	// this method is called every time an instance of the class is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// classes the method of its super class
		super.onCreate(savedInstanceState);
		//links the XML layout to the java code
		setContentView(R.layout.activity_check_notes);
		
		//links the list view
		lv = (ListView) findViewById(R.id.list);
		
		/*
		 * instantiate the member variables
		 * */
		dbh = new DatabaseHelper(this);
		dbh.open();

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
				
				/*
				 * gets the saved data before screen rotation
				 *  
				 * */
				listItems = savedInstanceState.getStringArrayList("savedList");
				 
			}
		////////////////////////////////////////////////////
			/*
			 * having the adapter instantiated after the check for saved date 
			 * which we do above is vital for restoration of data
			 * if we swap them the adapter is going to be instantiated with an
			 * empty list
			 * 			 
			 * * */
			adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
			
			lv.setAdapter(adapter);
			
			/*
			 * add action listener for the dinamically generated list view
			 * */
			lv.setOnItemClickListener(new OnItemClickListener() {
				//this method will be called when some of the list items is clicked
				// here we have the drawer which is a list view and the list with the 
				//notes saved
			    @Override
			    public void onItemClick(AdapterView<?> parent, View view, int position,
			            long id) {
			    	//intent is used to call another activity and to pass some values to it
			        Intent intent = new Intent(CheckNotesActivity.this, ManageNotesActivity.class);
			    	selectedListItem = allSelectedListItems.get(position);
			        intent.putExtra("listRow", selectedListItem);
			        startActivity(intent);
			    }
			});
			
			//still in the on create method we make sure the my drawer is instantiated
			myDrawer = new MyDrawer(this);
	}
	
	
/*
 * this method is an event handler for the search button which is part of the activity
 * 
 * */
public void displayAll(View v){
	    String temp = "";
	
	    
	    ArrayList<String[]> searchResult = new ArrayList<String[]>();
	          EditText searchTitle = (EditText) findViewById(R.id.searchCriteria);
	          //here we clear the values stored in the item list
	          //e.g. in case this is not the first time we call this method
	listItems.clear();
	//using a method declared in a database helper class we fetch data 
	          searchResult = dbh.fetchNoteByTitle(searchTitle.getText().toString());
	          //get all of the data passed from the DB
	          //will be used at on item clicked to pass the data 
	          //of the clicked object to the next activity
	          allSelectedListItems = searchResult;
	          
	          String title = "", date = "", id="";

	          //gets values from every string array which is in the array list
	for (int count = 0 ; count < searchResult.size() ; count++) {
				title = searchResult.get(count)[2];
				date = searchResult.get(count)[4]; 
				id = searchResult.get(count)[6]; 
				
				temp = title + " - " + date;
				//adds the gathered data to the list which will be used by the adapter
				//for displaying of the data
	listItems.add(temp);
	          }  
	//here we add refresh the screen and display the new data
	adapter.notifyDataSetChanged();    
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

	//this on click method is related to the drawer
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		myDrawer.onItemClick(parent, view, position, id);
	}

/////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////|END OF CODE RELATED TO DRAWER|///////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////	

}












