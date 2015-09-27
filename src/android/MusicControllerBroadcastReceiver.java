package com.filfatstudios.musiccontroller;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.BroadcastReceiver;

public class MusicControllerBroadcastReceiver extends BroadcastReceiver {
	private CallbackContext cb;

	public MusicControllerBroadcastReceiver(){

	}
	public void setCallback(CallbackContext cb){
		this.cb = cb;
	}
	public void stopListening(){
		if (this.cb != null){
			this.cb.success("stop-listening");
			this.cb = null;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (this.cb != null){
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
				int state = intent.getIntExtra("state", -1);
				switch (state) {
					case 0:
						this.cb.success("music-controller-headset-unplugged");
						this.cb = null;
						break;
					case 1:
						this.cb.success("music-controller-headset-plugged");
						this.cb = null;
						break;
					default:
						//Log.d(TAG, "I have no idea what the headset state is");
				}

			} else {
				String message = intent.getAction();
				this.cb.success(message);
				this.cb = null;
			}

		}
	}
}
