package com.petyodrawer;

	import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

	import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PhotoActivity extends Activity implements Callback {
	
	//Instance variables to handle the camera, its parameters, the surface and its holder
	Camera camera;
	Camera.Parameters camParam;
	SurfaceView surface;
	SurfaceHolder holder;
	
	/*
	 * class variable used for data access
	 * */
	private DatabaseHelper dbh;
	/*
	 * Strings passes from prev activity
	 * */
	String longitude; 
	String latitude; 
	String note_title; 
	String note; 
	String date; 
	String id; 
	
	/*
	 * this string will contain the address of the last photo
	 * it will be set in the method where the photo is taken
	 * */
	String photo_address = "";
	long maxID = 0;
	
	//standard onCreate for an app
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        
        /*
         * gathers the information sent from the prev activity
         * will be used to update the current note record
         * */
        Intent intent = getIntent();
        longitude = intent.getStringExtra("longitude"); 
        latitude = intent.getStringExtra("latitude"); 
        note_title = intent.getStringExtra("note_title"); 
        note = intent.getStringExtra("note"); 
        date = intent.getStringExtra("date");
        id = intent.getStringExtra("id");
        id = id.toString();
        
        
        //point our surface to the surface view in our XML interface
        surface = (SurfaceView)findViewById(R.id.surfaceView);
        
        //create our surface holder on this surface view and allow the data to 
        //add a callback to allow us to have a preview of the camera
        holder = surface.getHolder();
        holder.addCallback(this);
        
        /*
		 * create a new instance of the DatabaseHelper class and sets
		 * its class variables OpenHelper mDbHelper and SQLiteDatabase mDb
		 * 
		 **/
		 dbh = new DatabaseHelper(this);
	     dbh.open();  
  
    }

    //standard action bar method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.photo, menu);
        return true;
    }

    //Actions to perform if the surface view changes, such as change of orientation etc... 
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	//What to do on the creation of our surface
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			//open the camera interface
			camera = Camera.open();
			
			//check if the camera is in portrait or landscape mode 
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){ 
				//if portrait then rotate the picture 90 degrees
				camera.setDisplayOrientation(90);
			} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){ 
				//if in landscape leve at 0 degrees
				camera.setDisplayOrientation(0);
			
			}
			//apply the camera to the surface holder
			camera.setPreviewDisplay(holder);
			//start the camera preview
			camera.startPreview();
		} catch (IOException e) {
			//log any exceptions
			Log.d("CAMERA", e.getMessage());
		}
	}

	//What to do when the surface is destroyed (ie when the app is closed)
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//stop the preview
		camera.stopPreview();
		
		//release the camera for other apps to use.
		camera.release();
	}
	
	private void sendData(){
		//relocate to the check notes with the current note shown
				/*
				 * passes the values to the next activity
				 * */
				Intent intent = new Intent(PhotoActivity.this, ManageNotesActivity.class);
				String[] selectedListItem = {longitude, latitude, note_title, note, date, photo_address, id};
		        intent.putExtra("listRow", selectedListItem);
		        PhotoActivity.this.startActivity(intent);
	}
	
	//Method to handle the capture button click
	public void capture (View v){
		//tell the camera to capture an image.  The three methods at the end of the class then handle the image data
		camera.takePicture(shutterCallback, rawCallback, jpegCallback);

		updateAddress();
	}
	 /*
     *adds the address of the photo take in the note record
     * 
     * */
    public boolean updateAddress(){
    	/*
		 * calculate the last id added
		 * it will be used to update the last record 
		 * */
		maxID = dbh.getMaxID();
		//variable to check if the update has been performend
		boolean success = false;
		
		//success = dbh.updateUsers(long rowId, String longitude, String latitude, String note_title, String note, String photo_address)
		success = dbh.updateNotes( maxID,
							       longitude, 
							       latitude, 
							       note_title, 
							       note,
							       date,
							       photo_address);
		
		return success;
		  	
    }
	
	
	
	//Method to handle the opening of the gallery
	public void gallery (View v){
		//create our intent to do something and find the best app to open
		Intent intentBrowseFiles = new Intent(Intent.ACTION_GET_CONTENT);
		//limit the files to search to images
		intentBrowseFiles.setType("image/*");
		//Tell the system this is a new task
		intentBrowseFiles.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//Start the new Activity
		startActivity(intentBrowseFiles);
	}
	
	//Default method to quickly capture the data currently being picked up by the image sensor
	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {}
	};

	//Default method to  handle the RAW image data
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {}
	};
	
	//Custom method to handle the image being taken in from the capture method
	PictureCallback jpegCallback = new PictureCallback() {
		//When a picture is taken
		public void onPictureTaken(byte[] data, Camera camera) {
			//Create a file stream
			FileOutputStream outStream = null;
			try {
				//locate our app directory for saving images
				File location = new File(Environment.getExternalStorageDirectory() + "/cameraTutorial");
				
				//see if the directory exists, if it doesnt then create it
				if(!location.exists()){
				        location.mkdir();
				}
				//Sets the photo_address which will be used to populate the database record for this note
				photo_address = String.format(location + "/%d.jpg", System.currentTimeMillis());
				
				
				//Sending data to the manage notes activity
				//done here because the background thread finishes after the main one 
				sendData();
				/*
				 * I have moved this method here because this thread finished after the main and I need the 
				 * update to take plase after I know the value of photo_address
				 * */
				
				if( updateAddress() ){
					
					Toast.makeText(PhotoActivity.this,"RECORD UPDATED" , Toast.LENGTH_LONG).show();
				} 
				
				
				//open the connection to our new file giving it a name based on the miliseconds of the current time
				outStream = new FileOutputStream(photo_address);
				//write the file
				outStream.write(data);
				//close the stream
				outStream.close();
				
				//send a system broadcast requesting a media update to the gallery
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + location))); 
				
				//relink the holder
				camera.setPreviewDisplay(holder);
				//restart the preview
				camera.startPreview();
				
				
				
			} catch (FileNotFoundException e) {
				//log any exceptions
				Log.d("CAMERA", e.getMessage());
			} catch (IOException e) {
				//log any exceptions
				Log.d("CAMERA", e.getMessage());
			}
		}
	};
	

    
}
	
	
	
	
	