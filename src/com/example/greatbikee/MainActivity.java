package com.example.greatbikee;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import com.example.greatbikee.R;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {

	private static final String TAG = "BikeeProviderActivity";
	
	private Context mCtxt;
	
    private BikeeProviderService mFTService;

    private ServiceConnection mFTConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "service connection lost");
            mFTService = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Log.d(TAG, "service connected");
            mFTService = ((com.example.greatbikee.BikeeProviderService.LocalBinder) service).getService();            
        }
    };	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCtxt = getApplicationContext();
		
		mCtxt.bindService(new Intent(getApplicationContext(), BikeeProviderService.class), 
                this.mFTConnection, Context.BIND_AUTO_CREATE);
		
		
	}
	
	public void buttonClick (View v) {
		String message = "I am fine. Thanks, and you?";
		mFTService.sendMessage(message);
	}
	
		
}
