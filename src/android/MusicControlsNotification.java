package com.homerours.musiccontrols;

import org.apache.cordova.CordovaInterface;

//import java.util.Random;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;

import android.util.Log;
import android.R;
import android.content.Context;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.net.Uri;

public class MusicControlsNotification {
    private Activity cordovaActivity;
    private NotificationManager notificationManager;
    private final int notificationID = 4821;

    public MusicControlsNotification(Activity cordovaActivity){
        Log.v("MusicControls","Create notification");
        this.cordovaActivity = cordovaActivity;
        Context context = cordovaActivity.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Random r = new Random();
        //this.notificationID= r.nextInt(100000);
    }

    private Notification.Builder createBuilder(String artist, String song, String imageNativeURL, boolean isPlaying){
        Context context = cordovaActivity.getApplicationContext();
        Notification.Builder builder = new Notification.Builder(context).setContentTitle(song).setContentText(artist);
        builder.setWhen(0);

        // Use the Mediastyle display for lollipop
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Notification.MediaStyle style = new Notification.MediaStyle();
            builder.setStyle(style);
        }

        if (isPlaying){
            builder.setSmallIcon(R.drawable.ic_media_play);
        } else {
            builder.setSmallIcon(R.drawable.ic_media_pause);
        }

        if (!imageNativeURL.isEmpty()){
            try {
                Uri uri = Uri.parse(imageNativeURL);
                File file = new File(uri.getPath());
                FileInputStream fileStream = new FileInputStream(file);
                BufferedInputStream buf = new BufferedInputStream(fileStream);
                Bitmap image = BitmapFactory.decodeStream(buf);
                buf.close();
                builder.setLargeIcon(image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Back to application
        Intent resultIntent = new Intent(context, cordovaActivity.getClass());
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        builder.setContentIntent(resultPendingIntent);


        builder.setPriority(Notification.PRIORITY_HIGH);

        // PREVIOUS
        Intent previousIntent = new Intent("music-controls-previous");
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 0, previousIntent, 0);
        builder.addAction(android.R.drawable.ic_media_rew, "", previousPendingIntent);

        if (isPlaying){
            // PAUSE
            Intent pauseIntent = new Intent("music-controls-pause");
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, pauseIntent, 0);
            builder.addAction(android.R.drawable.ic_media_pause, "", pausePendingIntent);
        } else {
            // PLAY
            Intent playIntent = new Intent("music-controls-play");
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, 0);
            builder.addAction(android.R.drawable.ic_media_play, "", playPendingIntent);
        }

        // NEXT
        Intent nextIntent = new Intent("music-controls-next");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, 0);
        builder.addAction(android.R.drawable.ic_media_ff, "", nextPendingIntent);


        return builder;
    }

    public void updateNotification(String artist, String song, String imageNativeURL, boolean isPlaying){
        Log.v("MusicControls","Update notification");
        Notification.Builder builder = this.createBuilder(artist,song,imageNativeURL, isPlaying);
        Notification noti = builder.build();
        // Flags to make this notification permanent
        //noti.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        this.notificationManager.notify(this.notificationID, noti);
    }

    public void cancel(){
        this.notificationManager.cancel(this.notificationID);
    }
}

