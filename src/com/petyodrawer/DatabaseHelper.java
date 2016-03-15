package com.petyodrawer;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper {
//here we declare constants which will be in use 
	private static final String DATABASE_NAME = "notesDatabase";
	//Added a new field date and now will drop the old table
    private static final int DATABASE_VERSION = 3;  
    private static final String TABLE_NAME = "note";
    
    
   //those are class member variables used to access the database
    //declared as privite since for internal use
    private OpenHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context dbContext;
    
    //constant sting which will be used for the creation of the table
    private static final String DATABASE_CREATE =
        				"CREATE TABLE " + TABLE_NAME + " (" +
        				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        				"longitude TEXT NOT NULL, " +
        				"latitude TEXT NOT NULL, " +
        				"note_title TEXT NOT NULL, " +
        				"note TEXT NOT NULL, " +
        				"date TEXT NOT NULL, " +
        				"photo_address TEXT NOT NULL);";
    

    
    //the constructor of the class
    //here we set some of the class variables
    public DatabaseHelper(Context ctx) {
        this.dbContext = ctx;
    }

    //This method is called from the controlling class when the open() call is used
    //it creates an instance of openHelper (detailed above) and sets up appropriate   
    //connections to the database.
    public DatabaseHelper open() throws SQLException {
        mDbHelper = new OpenHelper(dbContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    //used to close db connections
    public void close() {
        mDbHelper.close();
    }

    //this method is used to insert data into the table
    //it returns the last id auto incremented
    public long createNote(String longitude, String latitude, String note_title, String note, String date, String photo_address) {
       //we add the Strings we would like to add to a ContentValues instance 
    	ContentValues initialValues = new ContentValues();
        initialValues.put("longitude", longitude);
        initialValues.put("latitude", latitude);
        initialValues.put("note_title", note_title);
        initialValues.put("note", note);
        initialValues.put("date", date);
        initialValues.put("photo_address", photo_address);
        
        long id = mDb.insert(TABLE_NAME, null, initialValues);

        return id;
    }
    
    //method used for update of the information 
    public boolean updateNotes(long rowId, String longitude, String latitude, String note_title, String note, String date, String photo_address) {
    	ContentValues args = new ContentValues();

        args.put("longitude", longitude);
        args.put("latitude", latitude);
        args.put("note_title", note_title);
        args.put("note", note);
        args.put("date", date);
        args.put("photo_address", photo_address);

        return mDb.update(TABLE_NAME, args, "_id=" + rowId, null) > 0;
    }
    
    /*
     * since we use auto increment the last record added will have the
     * highest ID. To find such a record at run time it is useful to delare 
     * such a method 
     * */
/////////////////////////////////////////////////////////////////
    public long getMaxID() {
        int id = 0;
        final String MY_QUERY = "SELECT MAX(_id) AS _id FROM " + TABLE_NAME;
        Cursor mCursor = mDb.rawQuery(MY_QUERY, null);  

              if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id = mCursor.getInt(mCursor.getColumnIndex("_id"));
              }

    return id;
        }
/////////////////////////////////////////////////////////////////
    
    //This method uses a built in delete function to clear all the   
    //contents of a table in a SQLite database
    public void deleteAll() {
    	mDb.delete(TABLE_NAME, null, null);
    }
    
    /*
     * did not work for me had to change it
     * */
    public void deleteRecord(long rowID) {
    	//mDb.delete(TABLE_NAME, "_id=" + rowID, null);
    	int id = (int)rowID;
    	
    	mDb.delete(TABLE_NAME, "_id=?", new String[] {Integer.toString(id)});
    }
    
    //This method is used to return a record set from a table via   
    //a String ArrayList.  The contents of this ArrayList are then passed back
    //to the calling method.
  
    public ArrayList<String[]> fetchNoteByTitle(String note_title) throws SQLException {
    	//Declare our string ArrayList.  This will capture our data from the
    	//database.  An ArrayList is being used as a normal String Array
    	//requires to be initialised with a definitive size.  
    	//We at this point do not know how many records will be returned.
    	ArrayList<String[]> myArray = new ArrayList<String[]>();
     	
    	//This is a pointer so we can keep track of the number of records being
    	//saved into our ArrayList
    	int pointer = 0;   	 
    	
    	//Declare and populate a cursor for receiving data from the      
    	//database.  The data is retrieved via a query.  The query      
    	//function is based on SQL and takes the following form:  
    	//query(FROM,SELECT,WHERE,WHERE ARGS,GROUP,HAVING,ORDER)
    	//FROM:   in our case this is the TABLE_NAME variable declared earlier.
    	//SELECT: here we are specifying a new array in which we are going
    	//		  to save the ID, forename, surname and age.  This can be modified
    	//		  to include any fields in our table.
    	//WHERE:  This parameter allows us to specify our WHERE clause, in our case
    	//        surname LIKE and the parameter we have passed in
    	//WHERE ARGS:  	The second where allows us to specify specific values to filter 
    	//				on.  However, all our filtering is catered for by the LIKE in 
    	//				the previous section.
    	//GROUP, HAVING and ORDER should be self explanatory!
    	//
    	//The result of the query is then saved into our cursor
    	Cursor mCursor = mDb.query(TABLE_NAME, new String[] {"_id", "longitude", "latitude", "note_title",
                "note","date", "photo_address" }, "note_title LIKE '%" + note_title + "%'", null,
                    null, null, "date desc");
    	
    	
    	//here we get integer representing the number of column associated with 
    	// the name
    	int longitudeColumn = mCursor.getColumnIndex("longitude");
    	int latitudeColumn = mCursor.getColumnIndex("latitude"); 
    	int note_titleColumn = mCursor.getColumnIndex("note_title"); 
    	int noteColumn = mCursor.getColumnIndex("note"); 
    	int dateColumn = mCursor.getColumnIndex("date"); 
    	int photo_addressColumn = mCursor.getColumnIndex("photo_address"); 
    	int idColumn = mCursor.getColumnIndex("_id"); 
    	
    	//int surNameColumn = mCursor.getColumnIndex("surname"); 
        
        //Check to see if there is a cursor for us to use.
    	if (mCursor != null){
    	   //If possible move to the first record within the cursor
           if (mCursor.moveToFirst()){
    			// Use a do....while Loop to iterate through all Records
    			do {
    				//we add a string array containing all of the data in each row
    				// in this case 7 columns - 7 strings
    				myArray.add(new String[7]);
    				//Save the fore-name into the string array
    				myArray.get(pointer)[0] = mCursor.getString(longitudeColumn);
    				myArray.get(pointer)[1] = mCursor.getString(latitudeColumn);
    				myArray.get(pointer)[2] = mCursor.getString(note_titleColumn);
    				myArray.get(pointer)[3] = mCursor.getString(noteColumn);
    				myArray.get(pointer)[4] = mCursor.getString(dateColumn);
    				myArray.get(pointer)[5] = mCursor.getString(photo_addressColumn);
    				myArray.get(pointer)[6] = mCursor.getString(idColumn);
    				//increment our pointer variable.
    				pointer++;
    			} while (mCursor.moveToNext()); // If possible move to the next record
           } else {
        	   //if no records are returned then add a new array and say no results.
        	   myArray.add(new String[7]);
        	   myArray.get(pointer)[0] = "";
        	   myArray.get(pointer)[1] = "";
        	   myArray.get(pointer)[2] = "NO RESULTS";
        	   myArray.get(pointer)[3] = "";
        	   myArray.get(pointer)[4] = "";
        	   myArray.get(pointer)[5] = "";
        	   myArray.get(pointer)[6] = "";
           }
    	} 
        
        //Return the ArrayList which now holds all our required records.
        return myArray;

    }
    
     
    //here we query the data base with for all of the records
    public ArrayList<String[]> selectAll() {
   	
    	//Declare our ArrayList
    	ArrayList<String[]> results = new ArrayList<String[]>();

    	//Declare a counter for population purposes	   
    	int counter = 0;

    	//Declare and populate a cursor for receiving data from the      
    	//database.  The data is retrieved via a query.  The query      
    	//function takes the following form:  
    	//query(FROM,SELECT,WHERE,WHERE ARGS,GROUP,HAVING,ORDER)
    	Cursor cursor = this.mDb.query(TABLE_NAME, new String[]  {"_id", "longitude", "latitude", "note_title", "note", "date", "photo_address"}, null, null, null, null, "_id desc");

    	//Populate ArrayList     
    	if (cursor.moveToFirst()) {
    	do {
    		results.add(new String[7]);
    		results.get(counter)[0] = cursor.getString(0).toString();
    		results.get(counter)[1] = cursor.getString(1).toString();
    		results.get(counter)[2] = cursor.getString(2).toString();
    		results.get(counter)[3] = cursor.getString(3).toString();
    		results.get(counter)[4] = cursor.getString(4).toString();
    		results.get(counter)[5] = cursor.getString(5).toString();
    		results.get(counter)[6] = cursor.getString(6).toString();
    		counter++;
    		} while (cursor.moveToNext());
    	}
    	if (cursor != null && !cursor.isClosed()) {
    		cursor.close();
    	}

    	//Return ArrayList
    	return results;
    }
    

        
    //This is our Open helper class
    //This class is our actual database controller.  We are using it to   
    //create and upgrade our database.  
    private static class OpenHelper extends SQLiteOpenHelper {
    	//This is our constructor, which is simply calling the constructor     
    	//of SQLiteOpenHelper.  What this does is check for the existence     
    	//of the specified database.  If it does not exist or is out of     
    	//date it creates or updates it.
    	OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}

    	//This method is over ridden from its super class.  If the     
    	//database does not exist then once it has been created by the     
    	//constructor this create table statement is executed. 
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        
        //This method is over ridden from its super class.  If the     
        //database it out of date then it is updated in the constructor     
        //and logged here.  Once updated the tables are dropped and     
        //onCreate is recalled.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}