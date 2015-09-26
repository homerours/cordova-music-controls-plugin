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
            String message = intent.getAction();
            this.cb.success(message);
            this.cb = null;
        }
    }
}
