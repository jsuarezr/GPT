package com.kymatic.gpstest01;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.cordova.DroidGap;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends DroidGap {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        showNotification("GPSTracker","Click para abrir");
        
     /*   if(savedInstanceState == null){
        	super.loadUrl("file:///android_asset/www/index.html");
        }else{
        	super.loadUrl("file:///android_asset/www/geoloc.html");
        	
        }
        */
        
        
       if (isServiceRunning("com.kymatic.gpstest01.GPSLoggerService")){
    	  super.loadUrl("file:///android_asset/www/geoloc.html");
    	   
    }else{
    	super.loadUrl("file:///android_asset/www/index.html");
    	
    }
       // finish(); //Destruimos esta activity para prevenit que el usuario retorne aqui presionando el boton Atras.  
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.main, menu);
    	
        return true;
    }
    

    public void showNotification( String contentTitle, String contentText ) {
    	   int icon = R.drawable.ic_stat_notification;
    	   long when = System.currentTimeMillis();
    	 
    	   Notification notification = new Notification(icon, contentTitle, when);
    	 
    	   notification.flags |= Notification.FLAG_NO_CLEAR; // .FLAG_AUTO_CANCEL;
    	   Intent notificationIntent = new Intent(this, MainActivity.class);
    	 
    	   PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	   notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
    	 
    	   NotificationManager nm = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
    	   nm.notify(1, notification);
    	}


    private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i
                    .next();

            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;
            }
           
            
            
            
        }
        return serviceRunning;
    }
    
 
    
	public void onDestroy(){
		 
		android.os.Process.killProcess(android.os.Process.myPid());
        Editor editor = getSharedPreferences("clear_cache", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        trimCache(this);
         
        super.onDestroy();
    }
	
	
	public static void trimCache(Context context) {
	    try {
	        File dir = context.getCacheDir();
	        if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);

	        }
	    } catch (Exception e) {
	        // TODO: handle exception
	    }
	}


	public static boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // <uses-permission
	    // android:name="android.permission.CLEAR_APP_CACHE"></uses-permission>
	    // The directory is now empty so delete it

	    return dir.delete();
	}
	
	
}

