package de.yadrone.android;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import de.yadrone.base.IARDrone;

public class MainActivity extends Activity {
	String TAG;

	public void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Terrestrial Hawk");
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final TextView text = (TextView)findViewById(R.id.text_init);
		text.append("\nConnected to " + wifi.getConnectionInfo().getSSID());
		initialize();
    }
    
	private void initialize()
    {
     final TextView text = (TextView)findViewById(R.id.text_init);
     YADroneApplication app = (YADroneApplication)getApplication();
     IARDrone drone = app.getARDrone();
	    
		try
		{
			text.append("\n\nInitialize the drone ...\n");
			drone.start();
			text.append("initialised");
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			if (drone != null)
				drone.stop();
		}
	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			new AlertDialog.Builder(this).setMessage("Upon exiting, drone will be disconnected !").setTitle("Exit YADrone ?").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					YADroneApplication app = (YADroneApplication)getApplication();
			    	IARDrone drone = app.getARDrone();
			    	drone.stop();
			    	
					finish();
				}
			}).setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{}
			}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
    public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_main, menu);	    
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i;
	    switch (item.getItemId()) 
	    {
	    	case R.id.menuitem_control:
		    	i = new Intent(this, ControlActivity.class);
		    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	startActivity(i);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item); 
	    }
	}
}
