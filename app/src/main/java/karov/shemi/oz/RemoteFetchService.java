package karov.shemi.oz;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

public class RemoteFetchService extends IntentService {
	 
    public RemoteFetchService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

    public RemoteFetchService() {
		super("downloadService");
		// TODO Auto-generated constructor stub
	}

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
 
	double x,y;
	//private LocationListener locationListener1,locationListener2;
	private LocationManager locationManager;
	private String meter,km,distanceof,fromyou;
    public static ArrayList<ListItem> listItemList;
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    private boolean calcXY(){
		  x=0.0;
		  y=0.0;
		  Location net_loc=null, gps_loc=null;
		  gps_loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		  net_loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		  //if there are both values use the latest one
		  if(gps_loc!=null && net_loc!=null){
			  if(gps_loc.getTime()>net_loc.getTime()){   	
				  x=gps_loc.getLatitude();
				  y=gps_loc.getLongitude();
			  }
			  else{
				  x=net_loc.getLatitude();
				  y=net_loc.getLongitude();
			  }
		  }
		  else{
			  if(gps_loc!=null){
				  x=gps_loc.getLatitude();
				  y=gps_loc.getLongitude();
			  }
			  if(net_loc!=null){
				  x=net_loc.getLatitude();
				  y=net_loc.getLongitude();
			  }
		  }
		  return ((gps_loc!=null) || (net_loc!=null));
	  }
    /**
     * Retrieve appwidget id from intent it is needed to update widget later
     * initialize our AQuery class
     */
    @Override
    //public int onStartCommand(Intent intent, int flags, int startId) {	  
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        distanceof = getResources().getString(R.string.distanceof);
        fromyou = getResources().getString(R.string.kmfromyou);
        meter = getResources().getString(R.string.meter);
    	km = getResources().getString(R.string.km);
    	//locationListener1 = new locLis();
        //locationListener2 = new locLis();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);   
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2); 
    	 boolean LocFound=calcXY();
        fetchDataFromWeb();
        //return super.onHandleIntent(intent);//, flags, startId);
    }
 
    /**
     * method which fetches data(json) from web aquery takes params
     * remoteJsonUrl = from where data to be fetched String.class = return
     * format of data once fetched i.e. in which format the fetched data be
     * returned AjaxCallback = class to notify with data once it is fetched
     */
    private void fetchDataFromWeb() {
    	SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
    	 String sel0=settings.getString(Constants.SPECIALITY,"0");
    	 String sel3=settings.getString(Constants.AREA+"w","0");
    	 String sel4=settings.getString(Constants.CITIES+"w","0");
    	 String sel1=settings.getString(Constants.ROLE,"0");
    	 String sel2=settings.getString(Constants.SIZE,"0");
    	 //String xStr=settings.getString(Constants.X,"0");
    	 //String yStr=settings.getString(Constants.Y,"0");
    	 if(Integer.valueOf(sel3)<0) sel3="-1";
    	
    	 /* String sel0= Integer.toString(selections[0]);
  		String sel1= Integer.toString(selections[1]);
  		String sel2= Integer.toString(selections[2]);
  		String sel3= Integer.toString(selections[3]);
  		String sel4=Integer.toString(selections[4]);*/
  		String xStr =Double.toString(x);
  	    String yStr =Double.toString(y);
  	    PackageInfo pInfo=null;
		String version="/uknkown/";
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				 version = "/"+pInfo.versionName+"/"; 
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String[] str1={Constants.baseUrl+version+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"cities",sel4,"x",xStr,"y",yStr,"type","0","for","1"};
		if(Integer.valueOf(sel3)<=-1) {
			String[] str4={Constants.baseUrl+version+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"radius",sel4,"x",xStr,"y",yStr,"type","0","for","1"};
			str1=str4;
		}
		if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(str1));
		String result0= Downloader.downloadPostObject(str1);
		JSONArray result1;
		try {
			result1 = new JSONArray(result0);
	    	processResult(result1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	 
    }
 
    /**
     * Json parsing of result and populating ArrayList<ListItem> as per json
     * data retrieved from the string
     */
    private void processResult(JSONArray json) {
        listItemList = new ArrayList<ListItem>();
        if (json==null) {
      	  //Toast.makeText(WidgetProvider.this,R.string.netproblem, Toast.LENGTH_SHORT).show();
        }
        else if(json.length()==0){
       	 //Toast.makeText(ShowAll.this,getResources().getString(R.string.noresults), Toast.LENGTH_SHORT).show();
        }
        else {
      	  String result=null;
       try{
      	 result= json.getString(0); 
       }catch(JSONException e)        {
	    	 //Toast.makeText(ShowAll.this,getResources().getString(R.string.netproblem), Toast.LENGTH_SHORT).show();
       }
       if(result.equalsIgnoreCase("-1")){
	  			//Toast.makeText(ShowAll.this,getResources().getString(R.string.netproblem), Toast.LENGTH_SHORT).show();
	  		}
       else	if (result.startsWith("Could")){
    	   //Toast.makeText(ShowAll.this,getResources().getString(R.string.serverproblem), Toast.LENGTH_SHORT).show();
       }
       else{
    	   if(result.startsWith("no result")) {	}
    	   else{
    		   for(int i=0;i<json.length();i++){  
    			   ListItem listItem = new ListItem();
    			   JSONArray ja;
				try {
					ja = json.getJSONArray(i);
				
    			   listItem.id=  Integer.valueOf(ja.getString(0));
    			   listItem.name= ja.getString(2).trim();
    			   listItem.company=ja.getString(1);
    			   listItem.x=Double.valueOf(ja.getString(3));
    			   listItem.y=Double.valueOf(ja.getString(4));
    			   listItem.city=ja.getString(5);
    			   float[] distance =new float[3];
    			   Location.distanceBetween(x, y, Double.parseDouble(ja.getString(3)), Double.parseDouble(ja.getString(4)), distance);		            	             	
    			   if (distance[0] <1000) {
    				   listItem.distance=distanceof+String.format("%.0f", distance[0])+meter+fromyou;
    				   
    			   }
    			   else {
    				   listItem.distance= distanceof+String.format("%.1f", distance[0]/1000)+km+fromyou;
    				   
    			   }
    			   String path=Constants.IMAGE_URL+ja.getInt(8)+Constants.IMAGE_SURRFIX;//+ja.getString(8)+"/"+ja.getString(9);
    			   listItem.photo=path;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			   listItemList.add(listItem);
    		   }
            }
       }
        }
        populateWidget();
       }
        
    
    /**
     * Method which sends broadcast to WidgetProvider
     * so that widget is notified to do necessary action
     * and here action == WidgetProvider.DATA_FETCHED
     */
    private void populateWidget() {
		if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+"remote ed"+listItemList.size());

        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(Constants.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);
 
        this.stopSelf();
    }
}