package com.homerours.musiccontroller;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.app.PendingIntent;
import android.os.Bundle;
import android.R;
import android.content.BroadcastReceiver;
import android.media.AudioManager;

public class MusicController extends CordovaPlugin {
	private MusicControllerBroadcastReceiver mMessageReceiver;
	private MusicControllerNotification notification;

	private AudioManager mAudioManager;
	private PendingIntent mediaButtonPendingIntent;


	private void registerBroadcaster(MusicControllerBroadcastReceiver mMessageReceiver){
		final Context context = this.cordova.getActivity().getApplicationContext();
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("music-controller-previous"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("music-controller-pause"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("music-controller-play"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("music-controller-next"));
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter("music-controller-media-button"));

		// Listen for headset plug/unplug
		context.registerReceiver((BroadcastReceiver)mMessageReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}

	// Register pendingIntent for broacast
	public void registerMediaButtonEvent(){
		this.mAudioManager.registerMediaButtonEventReceiver(this.mediaButtonPendingIntent);
	}
	public void unregisterMediaButtonEvent(){
		this.mAudioManager.unregisterMediaButtonEventReceiver(this.mediaButtonPendingIntent);
	}

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		final Activity activity = this.cordova.getActivity();
		this.notification = new MusicControllerNotification(activity);
		this.mMessageReceiver = new MusicControllerBroadcastReceiver(this);
		registerBroadcaster(mMessageReceiver);

		// Register media (headset) button event receiver
		final Context context=activity.getApplicationContext();
		this.mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		Intent headsetIntent = new Intent("music-controller-media-button");
		this.mediaButtonPendingIntent = PendingIntent.getBroadcast(context, 0, headsetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.registerMediaButtonEvent();
	}

	@Override
	public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		final Context context=this.cordova.getActivity().getApplicationContext();
		final Activity activity=this.cordova.getActivity();

		if (action.equals("create")) {
			final JSONObject params = args.getJSONObject(0);
			final String track = params.getString("track");
			final String artist = params.getString("artist");
			final String cover = params.getString("cover");
			final boolean isPlaying= params.getBoolean("isPlaying");
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					notification.updateNotification(artist, track, cover, isPlaying);
					callbackContext.success("success");
				}
			});
		}
		else if (action.equals("destroy")){
			this.notification.destroy();
			this.mMessageReceiver.stopListening();
			callbackContext.success("success");
		}
		else if (action.equals("watch")) {
			this.registerMediaButtonEvent();
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					mMessageReceiver.setCallback(callbackContext);
				}
			});
		}
		return true;
	}

	@Override
	public void onDestroy() {
		this.notification.destroy();
		this.mMessageReceiver.stopListening();
		this.unregisterMediaButtonEvent();
		super.onDestroy();
	}

	@Override
	public void onReset() {
		onDestroy();
		super.onReset();
	}
}
