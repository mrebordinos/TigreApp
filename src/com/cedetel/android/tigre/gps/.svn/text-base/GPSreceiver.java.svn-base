package com.cedetel.android.tigre.gps;

import android.content.Context;
import android.content.Intent;
import android.content.IntentReceiver;
import android.util.Log;

// GPS RECEIVER

public class GPSreceiver extends IntentReceiver{
	
	private static final String LOG_TAG = "LOCATIONUPDATERECEIVER";

	  public void onReceiveIntent(Context context, Intent intent) {
	        Log.d( LOG_TAG, intent.getAction() );
	        GPSservice gpsSim = GPSservice.getActivityReference();
	        if( intent.getAction() == null )
	              Log.e( LOG_TAG,"Action==null!" );
	        else
	              gpsSim.handleUpdateEvent(intent.getAction(), intent.getExtras());
	  }

}