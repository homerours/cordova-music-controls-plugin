package com.homerours.musiccontrols;

import org.apache.cordova.CordovaInterface;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

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
	private Notification.Builder notificationBuilder;
	private int notificationID;
	private MusicControlsInfos infos;
	private Notification.Action playpauseAction;

	public MusicControlsNotification(Activity cordovaActivity,int id) {
		this.notificationID = id;
		this.cordovaActivity = cordovaActivity;
		Context context = cordovaActivity;
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}


	private Bitmap getBitmapFromURL(String strURL) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void createBuilder() {
		Context context = cordovaActivity;
		Notification.Builder builder = new Notification.Builder(context);

		//Configure builder
		builder.setContentTitle(infos.track);
		if (!infos.artist.isEmpty()){
			builder.setContentText(infos.artist);
		}
		builder.setWhen(0);
		builder.setOngoing(true);
		if (!infos.ticker.isEmpty()){
			builder.setTicker(infos.ticker);
		}
		builder.setPriority(Notification.PRIORITY_MAX);

		//If 5.0 >= use MediaStyle
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
			builder.setStyle(new Notification.MediaStyle());
		}

		//Set SmallIcon
		if (infos.isPlaying){
			builder.setSmallIcon(R.drawable.ic_media_play);
		} else {
			builder.setSmallIcon(R.drawable.ic_media_pause);
		}

		//Set LargeIcon
		if (!infos.cover.isEmpty()){
			if(infos.cover.matches("^(https?|ftp)://.*$"))
				try{
					builder.setLargeIcon(getBitmapFromURL(infos.cover));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			else{
				try {
					Uri uri = Uri.parse(infos.cover);
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
		}

		//Open app if tapped
		Intent resultIntent = new Intent(context, cordovaActivity.getClass());
		resultIntent.setAction(Intent.ACTION_MAIN);
		resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
		builder.setContentIntent(resultPendingIntent);

		//Controls
		/* Previous  */
		Intent previousIntent = new Intent("music-controls-previous");
		PendingIntent previousPendingIntent = PendingIntent.getBroadcast(context, 1, previousIntent, 0);
		builder.addAction(android.R.drawable.ic_media_rew, "", previousPendingIntent);
		
		if (infos.isPlaying){
			/* Pause  */
			Intent pauseIntent = new Intent("music-controls-pause");
			PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, 0);
			//builder.addAction(android.R.drawable.ic_media_pause, "", pausePendingIntent);
			this.playpauseAction = new Notification.Action(android.R.drawable.ic_media_pause, "", pausePendingIntent);
		} else {
			/* Play  */
			Intent playIntent = new Intent("music-controls-play");
			PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 1, playIntent, 0);
			//builder.addAction(android.R.drawable.ic_media_play, "", playPendingIntent);
			this.playpauseAction = new Notification.Action(android.R.drawable.ic_media_play, "", playPendingIntent);
		}
		builder.addAction(playpauseAction);

		/* Next */
		Intent nextIntent = new Intent("music-controls-next");
		PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, 0);
		builder.addAction(android.R.drawable.ic_media_ff, "", nextPendingIntent);

		//Return the created builder
		//return builder;
		this.notificationBuilder = builder;
	}

	public void updateNotification(MusicControlsInfos infos) {
		this.infos = infos;
		this.createBuilder();
		Notification noti = this.notificationBuilder.build();
		this.notificationManager.notify(this.notificationID, noti);
	}

	public void updateIsPlaying(boolean isPlaying) {
		Context context = cordovaActivity;
		if (isPlaying){
			/* Pause  */
			this.notificationBuilder.setSmallIcon(R.drawable.ic_media_play);
			Intent pauseIntent = new Intent("music-controls-pause");
			PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, 0);
			this.playpauseAction.icon = android.R.drawable.ic_media_pause;
			this.playpauseAction.actionIntent = pausePendingIntent;
		} else {
			/* Play  */
			this.notificationBuilder.setSmallIcon(R.drawable.ic_media_pause);
			Intent playIntent = new Intent("music-controls-play");
			PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 1, playIntent, 0);
			this.playpauseAction.icon = android.R.drawable.ic_media_play;
			this.playpauseAction.actionIntent = playPendingIntent;
		}

		//this.infos.isPlaying = isPlaying;
		//this.createBuilder();
		Notification notif = this.notificationBuilder.build();
		this.notificationManager.notify(this.notificationID, notif);
	}

	public void destroy(){
		this.notificationManager.cancel(this.notificationID);
	}
}

