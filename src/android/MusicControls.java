package com.homerours.musiccontrols;

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
import android.os.Bundle;
import android.view.View;
import android.R;
import android.content.BroadcastReceiver;


/**
 *  * This class echoes a string called from JavaScript.
 *   */
public class MusicControls extends CordovaPlugin {
    private static final String TAG = "MusicControls";
    private MusicControlsBroadcastReceiver mMessageReceiver = new MusicControlsBroadcastReceiver();
    private MusicControlsNotification notification;
    private boolean firstLaunch = true;

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final Context context=this.cordova.getActivity().getApplicationContext();
        final Activity activity=this.cordova.getActivity();

        if (this.firstLaunch){
            this.firstLaunch = false;
            context.registerReceiver((BroadcastReceiver)this.mMessageReceiver, new IntentFilter("music-controls-previous"));
            context.registerReceiver((BroadcastReceiver)this.mMessageReceiver, new IntentFilter("music-controls-pause"));
            context.registerReceiver((BroadcastReceiver)this.mMessageReceiver, new IntentFilter("music-controls-play"));
            context.registerReceiver((BroadcastReceiver)this.mMessageReceiver, new IntentFilter("music-controls-next"));
            this.notification = new MusicControlsNotification(activity);
        }

        if (action.equals("show")) {
            final JSONObject params = args.getJSONObject(0);
            final String artist = params.getString("artist");
            final String song = params.getString("song");
            final boolean isPlaying= params.getBoolean("isPlaying");
            final String imageNativeURL = params.getString("image");
            this.cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    notification.updateNotification(artist,song,imageNativeURL,isPlaying);
                    callbackContext.success("success");
                }
            });
        }
        if (action.equals("watch")) {
            this.cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    mMessageReceiver.setCallback(callbackContext);
                }
            });
        }
        if (action.equals("stop")){
            this.notification.cancel();
            this.mMessageReceiver.stopListening();
            callbackContext.success("success");
        }
        return true;
    }

    //public void onStart() {
            //Log.v(this.TAG,"start");
    //}

    //// Called when the activity is no longer visible to the user.
    //public void onStop() {
    //}

    //// The final call you receive before your activity is destroyed.
    //public void onDestroy() {
    //}


}
