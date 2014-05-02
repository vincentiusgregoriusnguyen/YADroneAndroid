package de.yadrone.android;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import com.example.missilefire.MainActivity.SocketManager;
import com.momentofgeekiness.missilelauncher.io.MLCommand;
import com.vincent.imageprocessing.ExampleImageStitching;
import com.vincent.imageprocessing.ObjectPositionDetect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
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
import android.widget.Toast;
import at.abraxas.amarino.Amarino;
import de.yadrone.base.IARDrone;

public class ControlActivity extends Activity {
	
	private static final String DEVICE_ADDRESS = "00:12:12:04:32:51";
	private int port = 20000;	
	private InetAddress addr;
	private static final int check = 111;
	private static ArrayList<String> hmm;
	private static IARDrone drone2 = null;
	private static boolean isFlying = false;
	private static TextView text;
	private static TextToSpeech tts;
	private static ArrayList<String> k = new ArrayList<String>();
	
    public void onCreate(Bundle savedInstanceState) {
    	try {
			addr = InetAddress.getByName("192.168.1.2"); // change to static ip of Raspi
		} catch (UnknownHostException e) {e.printStackTrace();}
    	
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Amarino.connect(this, DEVICE_ADDRESS);
        setContentView(R.layout.activity_control);
        setTitle("Terrestrial Hawk");
        text =  (TextView)findViewById(R.id.textView5);
        Preview preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        
        tts = new TextToSpeech(ControlActivity.this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR){tts.setLanguage(Locale.ENGLISH);}				
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
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					drone.getCommandManager().forward(20);
				}else if (event.getAction() == MotionEvent.ACTION_UP){ 
					drone.hover();
				}
				return true;
			}
		});
    	
    	ImageButton backward = (ImageButton)findViewById(R.id.cmd_backward);
    	backward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN){ 
					drone.getCommandManager().backward(20);
				}else if (event.getAction() == MotionEvent.ACTION_UP){
					drone.hover();
				}
				return true;
			}
		});

    	
    	ImageButton left = (ImageButton)findViewById(R.id.cmd_left);
    	left.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN){ 
					drone.getCommandManager().goLeft(20);
				}else if (event.getAction() == MotionEvent.ACTION_UP){
					drone.hover();
				}
				return true;
			}
		});

    	
    	ImageButton right = (ImageButton)findViewById(R.id.cmd_right);
    	right.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN){ 
					drone.getCommandManager().goRight(20);
				}else if (event.getAction() == MotionEvent.ACTION_UP){
	                drone.hover();
				}
				return true;
			}
		});
    	
    	ImageButton up = (ImageButton)findViewById(R.id.cmd_up);
    	up.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					drone.getCommandManager().up(40);
				}else if (event.getAction() == MotionEvent.ACTION_UP){
	                drone.hover();
				}
				return true;
			}
		});
    	
    	ImageButton down = (ImageButton)findViewById(R.id.cmd_down);
    	down.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event){
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
    	landing.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if (!isFlying){
					text.setText("Land");
					String temp = "Drone take off Initialised";
					tts.speak(temp, TextToSpeech.QUEUE_ADD, null);
					drone.takeOff();
				}else{
					text.setText("Air");
					drone.landing();
				}
				isFlying = !isFlying;
			}
		});
    	
    	ImageButton emergency = (ImageButton)findViewById(R.id.cmd_emergency);
    	emergency.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				drone.reset();
			}
		});
	}
    
    ShutterCallback shutterCallback = new ShutterCallback() {public void onShutter() {}};
    PictureCallback rawCallback = new PictureCallback() {
    	public void onPictureTaken(byte[] data, Camera camera) {}};

    PictureCallback jpegCallback = new PictureCallback() {
          @SuppressLint("SdCardPath")
		public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                	  String y = String.format("/sdcard/%d.jpg", System.currentTimeMillis());
                	  k.add(y);
                      outStream = new FileOutputStream(y);
                      outStream.write(data);
                      outStream.close();      
                      ExampleImageStitching.StitchImages(k);
                      k = ObjectPositionDetect.getDirections();
                      sendAllDirection(k);
                } catch (FileNotFoundException e) { 
                	e.printStackTrace();
                } catch (IOException e) { 
                	e.printStackTrace();
                }
          }
    };
    
    public void sendFirstDirectionOnly(ArrayList<String> x){
    	String forward = "Forward", right = "Right", left = "Left";
    	if(x.get(0).equals(forward)){
    		forward();
    	}
    	if(x.get(0).equals(left)){
			left();
    	}
    	if(x.get(0).equals(right)){
			right();
		}
    }
    
    public void sendAllDirection(ArrayList<String> x){
    	String forward = "Forward", right = "Right", left = "Left";
    	for(int i = 0; i < x.size(); i++ ){
    		if(x.get(i).equals(forward)){
    			forward();
    		}
    		if(x.get(i).equals(left)){
    			left();
    		}
    		if(x.get(i).equals(right)){
    			right();
    		}
    	}
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == check && resultCode == RESULT_OK){
			hmm = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if(hmm.contains("forward")){
				drone2.getCommandManager().forward(30);
			}
			if(hmm.contains("backward")){
				drone2.getCommandManager().backward(30);}
			if(hmm.contains("left")){drone2.getCommandManager().goLeft(30);
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
			if(hmm.contains("fire")){
				handleCommand(MLCommand.FIRE);
			}
			if(hmm.contains("attack")){
				handleCommand(MLCommand.FIRE);
			}
		} 
	}

	public boolean onCreateOptionsMenu(Menu menu) {
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
	
	private void handleCommand(MLCommand command) {
		try {
			SocketManager sm = new SocketManager(addr, port);
			sm.execute(command.getCommand());
		} catch (SocketException e) {
			Toast.makeText(getApplicationContext(),"Port "+port+" in use on the Android device!", Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	private class SocketManager extends AsyncTask<String, Boolean, Boolean> {

		private InetAddress addr;
		private DatagramSocket s;
		private int port;

		public SocketManager(InetAddress addr, int port) throws SocketException {
			this.s = new DatagramSocket();
			this.port = port;
			this.addr = addr;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			byte[] commands = params[0].getBytes();			
			try {
				for(byte command : commands) {
					DatagramPacket packet= new DatagramPacket(new byte[]{command}, 1,addr,port);					
					s.send(packet);
				}
			} catch (Exception e) {
				return Boolean.valueOf(false);
			} finally {
				close();
			}
			return Boolean.valueOf(true);
		}
		
		@Override
		protected void onPostExecute(Boolean bool) {
			if(!bool.booleanValue())
				Toast.makeText(getApplicationContext(),"Error occured when sending package!", Toast.LENGTH_SHORT).show();
		}
		
		public void close() {
			s.close();
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
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
