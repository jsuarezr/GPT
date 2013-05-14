package com.kymatic.gpstest01;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
    

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;


public class GPSLoggerService extends BackgroundService  {

	 

	private static final int gpsMinTime = 240000;
	private static final int gpsMinDistance = 100;
    
	private static final int GEOCODER_MAX_RESULTS = 3;

 

	private final static String TAG = GPSLoggerService.class.getSimpleName();
	private String URL_server= "http://www.kymatic.es/mobile/develop/json_server.php";
	private int ANDROID_CONNECTION_TIMEOUT = 100;

 


	private LocationManager locationManager;
	private LocationListener locationListener;
	private int id_carrera = 2;
	private int id_user = 55;
	JSONObject returnedJObject= new JSONObject();

 

	public GPSLoggerService() {
		Log.d("XXGPSLoggerServiceXX","GPSLoggerService.GPSLoggerService().");
	}

 

	@Override
	public void onCreate() {
		Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onCreate().");

		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		try{
			Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onStart().");
			startLoggingService();
			 
			
			//locationManager.requestLocationUpdates(
			//		LocationManager.GPS_PROVIDER, gpsMinTime, gpsMinDistance, locationListener);
			
			// getLocation();
	        }catch(Exception e){
	        	Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onStart().ERROR: "+e);	
			}
		
 		super.onStart(intent, startId);
	}

	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onStartCommand().");
		try{
			startLoggingService();
	  		Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onStartCommand().locationManager");
			
			//locationManager.requestLocationUpdates(
			//		LocationManager.GPS_PROVIDER, gpsMinTime, gpsMinDistance, locationListener);
			
			// getLocation();
	        }catch(Exception e){
	        	Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onStartCommand().ERROR: "+e);	
			}
			Log.d("XXGPSLoggerServiceXX","GPSLoggerService.onStartCommand().FIN locationManager");		
		return Service.START_STICKY;
	}
	
	public void onDestroy(){
		stopLoggingService();
		 
        super.onDestroy();
    }
	

	private void stopLoggingService(){
		Log.d("ZZZZ","stopLoggingService");
		locationManager.removeUpdates(locationListener);
		stopSelf();
	}

	private void startLoggingService(){	
		Log.d("ZZZZ","startLoggingService");
		 
		
		
		//Nos registramos para recibir actualizaciones de la posición
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				//muestraPosicion(location);
				saveCoordinates(location ,"TEST1");
			}
			public void onProviderDisabled(String provider){
				// lblEstado.setText("Provider OFF");
			}
			public void onProviderEnabled(String provider){
				// lblEstado.setText("Provider ON");
			}
			public void onStatusChanged(String provider, int status, Bundle extras){
				Log.i("LocationListener", "Provider Status: " + status);
				// lblEstado.setText("Provider Status: " + status);
			}
		};
		
		getLocation();
		
		
		/* }catch(Exception e){
			Log.i("LocationListener", "ERROR: " + e.toString());
		}*/
		Log.d("ZZZZ","startLoggingService FIN ");
	}

	
	private Location getLocation() {         
		Log.d("ZZZZ","getLocation");
        Location gpslocation = null;
        Location networkLocation = null;
        
	        if(locationManager==null){
	        	locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
	        }
	    try {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,gpsMinTime, gpsMinDistance, locationListener);
                gpslocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,gpsMinTime, gpsMinDistance, locationListener);
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); 
            }
        } catch (IllegalArgumentException e) {
            //Log.e(ErrorCode.ILLEGALARGUMENTERROR, e.toString());
            Log.e("XXXerrorXXXX", e.toString());
        }
        if(gpslocation==null && networkLocation==null)
            return null;

        if(gpslocation!=null && networkLocation!=null){
            if(gpslocation.getTime() < networkLocation.getTime()){
                gpslocation = null;
                return networkLocation;
            }else{
                networkLocation = null;
                return gpslocation;
            }
        }
        if (gpslocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpslocation;
        }
        Log.d("ZZZZ","getLocation FIN ");
        return null;
    }

	
	private void startMonitoringTimer(){
		// monitoringTimer = new Timer();
		Log.d("ZZZZ","startMonitoringTimer");
		
		/*monitoringTimer.scheduleAtFixedRate(
				new TimerTask()
				{
					@Override
					public void run()
					{
						Log.d("ZZZZ","--> Disparamos el Task!!!! ");

						if (longitude != 0.0 && latitude != 0.0)
						{
							monitoringTimer.cancel();
							monitoringTimer = null;

							// manager.removeUpdates(GPSLoggerService.this);
							Log.d("ZZZZ","startMonitoringTimerLat:"+latitude);
							Log.d("ZZZZ","startMonitoringTimerLon:"+longitude);
							// saveCoordinates(latitude, longitude, altitude, getLocationName(latitude,longitude));
							stopLoggingService();
						}
						Log.d("ZZZZ","--> FIN del Task!!!! ");


					}



				}, 
				GPSLoggerService.TIMER_DELAY,
				GPSLoggerService.TIMER_DELAY);
				*/
		
		
	}

	private String getLocationName(double latitude, double longiture){
		String name = "";
		Geocoder geocoder = new Geocoder(this);
		Log.d("ZZZZ","getLocationName");
		try {
			List<Address> address = geocoder.getFromLocation(latitude, longiture, GPSLoggerService.GEOCODER_MAX_RESULTS);

			name = address.get(0).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}		

		return name;
	}

	// private void saveCoordinates(double latitude, double longitude, double altitude, String name){
	private void saveCoordinates(Location loc, String name){

		Log.d("ZZZZ","saveCoordinates");
		// tenemosStatus = true;
		JSONObject result = new JSONObject();

		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
			String now = df.format(new Date(System.currentTimeMillis())); 
			Log.e(TAG, "onCreate");


			// location = getMyLocation();

			// new DoBackgroundTask().execute();
			Log.d(TAG, "SERVICIO INICIADO...");

			String msg = "Hora " + now +"-latitud:"+String.valueOf(loc.getLatitude());
			//String androidID = System.getString(this.getContentResolver(),System.ANDROID_ID);
			
			result.put("user",id_user);
			result.put("carrera",id_carrera);
			result.put("hora",now);
			result.put("telefono",getMyPhoneNumber());

			result.put("latitud",String.valueOf(loc.getLatitude()));
			result.put("longitud",String.valueOf(loc.getLongitude()));
			result.put("altura",String.valueOf(loc.getAltitude()));
			result.put("velocidad", String.valueOf(loc.getSpeed()));


			Log.d(TAG, msg);
			String uuid = UUID.randomUUID().toString();
			// result.put("telefono",uuid);

			try {
				this.putDataToServer(URL_server+"?id="+uuid, result);
			} catch (Throwable ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				Log.d(TAG, ex.toString());
			}

		} catch (JSONException e) {
			Log.d(TAG, e.toString());
		}

	}

	@Override
	protected JSONObject doWork() {
		// TODO Auto-generated method stub
		Log.d("ZZZ","doWord()");
		Log.d("XXXX","doWord().onStartCommand()- Ponemos el timer en:");
		// if (tenemosStatus){
		//startLoggingService();
		// startMonitoringTimer();
		// tenemosStatus = false;
		/*}else{
			 Log.d("ZZZ","doWord() Ahora no toca");
		 }*/

		return null;
	}

	@Override
	protected JSONObject getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setConfig(JSONObject arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onTimerDisabled() {
		// TODO Auto-generated method stub
		stopLoggingService();
	}

	@Override
	protected void onTimerEnabled() {
		// TODO Auto-generated method stub

	}

	/*
	 * ENVIA PETICION JSON AL SERVER
	 */
	public  String putDataToServer(String url,JSONObject returnedJObject) throws Throwable
	{

		HttpPost request = new HttpPost(url);
		JSONStringer json = new JSONStringer();
		StringBuilder sb=new StringBuilder();


		if (returnedJObject!=null) 
		{
			Iterator<String> itKeys = returnedJObject.keys();
			if(itKeys.hasNext())
				json.object();
			while (itKeys.hasNext()) 
			{
				String k=itKeys.next();
				json.key(k).value(returnedJObject.get(k));
				Log.e("keys "+k,"value "+returnedJObject.get(k).toString());
			}             
		}
		json.endObject();


		StringEntity entity = new StringEntity(json.toString());
		entity.setContentType("application/json;charset=UTF-8");
		entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
		request.setHeader("Accept", "application/json");
		request.setEntity(entity); 

		HttpResponse response =null;
		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpConnectionParams.setSoTimeout(httpClient.getParams(), ANDROID_CONNECTION_TIMEOUT*1000); 
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),ANDROID_CONNECTION_TIMEOUT*1000); 
		try{

			response = httpClient.execute(request); 
			Log.e("SocketException", "ENVIADO...");
		}
		catch(SocketException se)
		{
			Log.e("SocketException", se+"");
			throw se;
		}




		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while((line = reader.readLine()) != null){
			sb.append(line);

		}
		Log.e("FINAL ", sb.toString());
		return sb.toString();
	}

	// Conocer el telefono:

	private String getMyPhoneNumber(){
		/*TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager)
				getSystemService(Context.TELEPHONY_SERVICE); 
		return mTelephonyMgr.getLine1Number();*/
		
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String identifier ="";
		if (tm != null)
		      identifier = tm.getDeviceId();
		
		return identifier;
		
		
	}

	private String getMy10DigitPhoneNumber(){
		String s = getMyPhoneNumber();
		return s.substring(2);
	}

 
	
}