package com.petyodrawer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	private MediaPlayer mp1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mp1 =  MediaPlayer.create(getBaseContext(), R.raw.splash_sound); 
		mp1.start ();
		
		//Remove title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//set content view AFTER ABOVE sequence (to avoid crash)
		setContentView(R.layout.activity_splash);
		
		Thread thread = new Thread(){
			public void run(){
				try {
					
					sleep(3000);//waits for 3 seconds and starts  MainActivity
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		};
		//starts a new Thread
		thread.start();
	}

}
