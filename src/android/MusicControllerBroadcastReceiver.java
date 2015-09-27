package com.homerours.musiccontroller;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.view.KeyEvent;

public class MusicControllerBroadcastReceiver extends BroadcastReceiver {
	private CallbackContext cb;
	private MusicController musicController;


	public MusicControllerBroadcastReceiver(MusicController musicController){
		this.musicController=musicController;
	}

	public void setCallback(CallbackContext cb){
		this.cb = cb;
	}

	public void stopListening(){
		if (this.cb != null){
			this.cb.success("music-controller-stop-listening");
			this.cb = null;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (this.cb != null){
			String message = intent.getAction();

			if(message.equals(Intent.ACTION_HEADSET_PLUG)){
				// Headphone plug/unplug
				int state = intent.getIntExtra("state", -1);
				switch (state) {
					case 0:
						this.cb.success("music-controller-headset-unplugged");
						this.cb = null;
						this.musicController.unregisterMediaButtonEvent();
						break;
					case 1:
						this.cb.success("music-controller-headset-plugged");
						this.cb = null;
						this.musicController.registerMediaButtonEvent();
						break;
					default:
						break;
				}
			} else if (message.equals("music-controller-media-button")){
				// Media button
				KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					this.cb.success(message);
					this.cb = null;
				}
			} else {
				this.cb.success(message);
				this.cb = null;
			}

		}

	}
}
