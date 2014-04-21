package de.yadrone.android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import de.yadrone.base.IARDrone;

public class ControlActivity extends Activity {
	
	private static final String DEVICE_ADDRESS = "00:12:12:04:32:51";
	private static final int check = 111;
	private static ArrayList<String> hmm;
	private static IARDrone drone2 = null;
	private static boolean isFlying = false;
	private static TextView text;
	private static TextToSpeech tts;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Amarino.connect(this, DEVICE_ADDRESS);
        setContentView(R.layout.activity_control);
        text =  (TextView)findViewById(R.id.textView5);
        Preview preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        
        tts = new TextToSpeech(ControlActivity.this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR){
					tts.setLanguage(Locale.ENGLISH);
				}				
			}
		});
        
        initButtons();
    }
    
    private void initButtons(){
    	YADroneApplication app = (YADroneApplication)getApplication();
   	 	final IARDrone drone = app.getARDrone();
   	 	drone2 = drone;
    	ImageButton groundforward = (ImageButton)findViewById(R.id.forward);
    	groundforward.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				forward();
				return true;
			}
    	});
    	
    	ImageButton voice = (ImageButton)findViewById(R.id.voice);
    	voice.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				startActivityForResult(i,check);
				return false;
			}
    	});
    	
    	ImageButton groundleft = (ImageButton)findViewById(R.id.left);
    	groundleft.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				left();
				return true;
			}
    	});
    	
    	ImageButton groundright = (ImageButton)findViewById(R.id.right);
    	groundright.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				right();
				return true;
			}
    	});
    	
    	ImageButton groundBack = (ImageButton)findViewById(R.id.down);
    	groundBack.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				backward();
				return true;
			}
    	});
    	
    	ImageButton forward = (ImageButton)findViewById(R.id.cmd_forward);
    	forward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN) {drone.getCommandManager().forward(20);}
				else if (event.getAction() == MotionEvent.ACTION_UP){ drone.hover();}
				return true;
			}
		});
    	
    	ImageButton backward = (ImageButton)findViewById(R.id.cmd_backward);
    	backward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN){ drone.getCommandManager().backward(20);}
				else if (event.getAction() == MotionEvent.ACTION_UP){drone.hover();}
				return true;
			}
		});

    	
    	ImageButton left = (ImageButton)findViewById(R.id.cmd_left);
    	left.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
					drone.getCommandManager().goLeft(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
	                drone.hover();

				return true;
			}
		});

    	
    	ImageButton right = (ImageButton)findViewById(R.id.cmd_right);
    	right.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
					drone.getCommandManager().goRight(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
	                drone.hover();

				return true;
			}
		});
    	
    	ImageButton up = (ImageButton)findViewById(R.id.cmd_up);
    	up.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
					drone.getCommandManager().up(40);
				else if (event.getAction() == MotionEvent.ACTION_UP)
	                drone.hover();

				return true;
			}
		});
    	
    	ImageButton down = (ImageButton)findViewById(R.id.cmd_down);
    	down.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
					drone.getCommandManager().down(40);
				else if (event.getAction() == MotionEvent.ACTION_UP)
	                drone.hover();
				return true;
			}
		});

    	
    /*	Button spinLeft = (Button)findViewById(R.id.cmd_spin_left);
    	spinLeft.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
					drone.getCommandManager().spinLeft(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
	                drone.hover();

				return true;
			}
		});

    	
    	Button spinRight = (Button)findViewById(R.id.cmd_spin_right);
    	spinRight.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event)
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN) 
					drone.getCommandManager().spinRight(20);
				else if (event.getAction() == MotionEvent.ACTION_UP)
	                drone.hover();

				return true;
			}
		}); */
    	 
    	final ImageButton landing = (ImageButton)findViewById(R.id.cmd_landing);
    	landing.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				if (!isFlying)
				{
					text.setText("Land");
					String temp = "Drone take off Initialised";
					tts.speak(temp, TextToSpeech.QUEUE_ADD, null);
					drone.takeOff();
				}
				else
				{
					text.setText("Air");
					drone.landing();
				}
				isFlying = !isFlying;
			}
		});
    	
    	ImageButton emergency = (ImageButton)findViewById(R.id.cmd_emergency);
    	emergency.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				drone.reset();
			}
		});
	}
    
    ShutterCallback shutterCallback = new ShutterCallback() {public void onShutter() {}};
    PictureCallback rawCallback = new PictureCallback() {public void onPictureTaken(byte[] data, Camera camera) {}};

    PictureCallback jpegCallback = new PictureCallback() {
          @SuppressLint("SdCardPath")
		public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                      outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
                      outStream.write(data);
                      outStream.close();      
                } catch (FileNotFoundException e) { 
                	e.printStackTrace();
                } catch (IOException e) { 
                	e.printStackTrace();
                }
          }
    };
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == check && resultCode == RESULT_OK){
			hmm = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if(hmm.contains("forward")){
				drone2.getCommandManager().forward(30);
			}
			if(hmm.contains("backward")){
				drone2.getCommandManager().backward(30);
			}
			if(hmm.contains("left")){
				drone2.getCommandManager().goLeft(30);
			}
			if(hmm.contains("right")){
				drone2.getCommandManager().goRight(30);
			}
			if(hmm.contains("land")){
				isFlying = false;
				drone2.getCommandManager().landing();
			}
			if(hmm.contains("up")){
				drone2.getCommandManager().up(40);
			}
			if(hmm.contains("down")){
				drone2.getCommandManager().down(40);
			}
			if(hmm.contains("air")){
				isFlying = true;
				drone2.getCommandManager().takeOff();
				String temp = "Drone take off Initialised";
				tts.speak(temp, TextToSpeech.QUEUE_ADD, null);
			}
			if(hmm.contains("emergency")){
				drone2.reset();
				String temp = "Emergency Emergency";
				tts.speak(temp, TextToSpeech.QUEUE_ADD, null);
			}
		} 
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_control, menu);	    
		return true;
	}
	
	private void forward(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'A', 0);
	}

	private void backward(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'B', 0);
	}

	private void right(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'C', 0);
	}

	private void left(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'D', 0);
	}

	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i;
	    switch (item.getItemId()){
	    	case R.id.menuitem_main:
	    		i = new Intent(this, MainActivity.class);
	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(i);
		        return true;

		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
}
