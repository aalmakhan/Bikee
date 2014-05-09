package com.example.greatbikee;

import java.io.IOException;
import java.util.HashMap;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

public class BikeeProviderService extends SAAgent {
	public static final String TAG = "BikeeProviderService";
	
	public static final int CHANNEL_ID = 111;
	public int ONLY_CONNECTION_ID;
	
	HashMap<Integer, BikeeProviderConnection> mConnectionsMap = null;
	
	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
		public BikeeProviderService getService() {
			return BikeeProviderService.this;
		}
	}
	
    public BikeeProviderService() {    	
        super(TAG, BikeeProviderConnection.class);        
    }	
    
    public void sendMessage(String message) {
		BikeeProviderConnection uHandler = 
				mConnectionsMap.get(Integer.parseInt(String.valueOf(this.ONLY_CONNECTION_ID))); 
		try {
			uHandler.send(CHANNEL_ID, message.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public class BikeeProviderConnection extends SASocket { 
		private int mConnectionId; 

		public BikeeProviderConnection() { 
			super(BikeeProviderConnection.class.getName()); 
		} 

		@Override 
		public void onError(int channelId, String errorString, int error) { 
			Log.e(TAG, "Connection is not alive ERROR: " + errorString + " " + error); 
		} 

		@Override 
		public void onReceive(int channelId, byte[] data) {
			Log.d(TAG, "onReceive");
			
			String incomeMsg = new String(data);
			
			Toast.makeText(getBaseContext(), incomeMsg, Toast.LENGTH_LONG).show();
			
			BikeeProviderConnection uHandler = 
					mConnectionsMap.get(Integer.parseInt(String.valueOf(mConnectionId))); 
			try { 
				uHandler.send(CHANNEL_ID, incomeMsg.getBytes()); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 

		@Override 
		protected void onServiceConnectionLost(int errorCode) { 
			Log.e(TAG, "onServiceConectionLost for peer = " + mConnectionId + "error code =" + errorCode); 
			if (mConnectionsMap != null) { 
				mConnectionsMap.remove(mConnectionId); 
			} 
		} 
	} 

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate of Provider Service");
		SA mAccessory = new SA();
		try {
			mAccessory.initialize(this);
		} catch (SsdkUnsupportedException e) {
			
		} catch (Exception e1) {
			Log.e(TAG, "Cannot initialize Accessory package.");
			e1.printStackTrace();
			stopSelf();
		}
	}
	
	@Override
	protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
		acceptServiceConnectionRequest(peerAgent);
	}

	@Override
	protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override 
	protected void onServiceConnectionResponse(SASocket thisConnection, int result) { 
		if (result == CONNECTION_SUCCESS) { 
			if (thisConnection != null) { 
				BikeeProviderConnection myConnection = (BikeeProviderConnection) thisConnection; 
				if (mConnectionsMap == null) { 
					mConnectionsMap = new HashMap<Integer, BikeeProviderConnection>(); 
				} 
				myConnection.mConnectionId = (int) (System.currentTimeMillis() & 255);
				this.ONLY_CONNECTION_ID = myConnection.mConnectionId;
				
				Log.d(TAG, "onServiceConnection connectionID = " + myConnection.mConnectionId); 
				mConnectionsMap.put(myConnection.mConnectionId, myConnection); 

				Toast.makeText(getBaseContext(), "Connection is established. Have fun!", Toast.LENGTH_LONG).show(); 
			} else 
				Log.e(TAG, "SASocket object is null"); 
		} else if (result == CONNECTION_ALREADY_EXIST) { 
			Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST"); 
		} else { 
			Log.e(TAG, "onServiceConnectionResponse result error =" + result); 
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
}
