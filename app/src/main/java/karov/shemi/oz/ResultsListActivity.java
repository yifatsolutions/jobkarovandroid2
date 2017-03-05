package karov.shemi.oz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultsListActivity extends AppCompatActivity {
//	public static FragmentManager fragmentManager;
	private NotesDbAdapter mDbHelper;
	protected int[] selections={0,0,0,-1,50000};
	private ArrayList<HashMap<String, String>> mylist1,mylist2; 
	private Toolbar toolbar;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private GoogleCloudMessaging gcm;
	private Context context;
	private String xStr,yStr, version,companyParam,id,sel1,sel2,regid,link;
	private LocationManager locationManager;	
	private LocationListener locationListener1,locationListener2;
	private int type;
	private double x,y;
	private Bundle extras=null;
	private Location loc;
	protected SharedPreferences settings;
	private SecondFragment bydate,bydistance;
	private MapFragment mapfragment;
private DrawerLayout mDrawerLayout;
private ExpandableListView mDrawerList;
private ActionBarDrawerToggle mDrawerToggle;

@Override
protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();

}
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
	            } else {
	                Log.i(Constants.TAG, "This device is not supported.");
	                finish();
	            }
	            return false;
	        }
	        return true;
	    }

	   
	    private void storeRegistrationId(Context context, String regId) {
	        int appVersion = getAppVersion(context);
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putString(Constants.PROPERTY_REG_ID, regId);
	        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
	        editor.commit();
	    }

	 
	    private String getRegistrationId(Context context) {
	        String registrationId = settings.getString(Constants.PROPERTY_REG_ID, "");
	        if (registrationId.isEmpty()) {
	            Log.i(Constants.TAG, "Registration not found.");
	            return "";
	        }
	       
	        int registeredVersion = settings.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(Constants.TAG, "App version changed.");
	            return "";
	        }
	        return registrationId;
	    }

	  
	    private void registerInBackground() {
	        new AsyncTask<Void, Void, JSONObject>() {
	            @Override
	            protected JSONObject doInBackground(Void... params) {
	                JSONObject data = null;
	                try {
	                    if (gcm == null) {
	                        gcm = GoogleCloudMessaging.getInstance(context);
	                    }
	                    regid = gcm.register(Constants.SENDER_ID);
	                    storeRegistrationId(context, regid);
	                    final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	                    final String tmDevice = "" + tm.getDeviceId();
	                    String[] str1={Constants.baseUrl+version+Constants.urlCommandGCM,Constants.USERID,settings.getString(Constants.USERID, ""),Constants.USERCODE,settings.getString(Constants.USERCODE, ""),Constants.TOKEN,regid,Constants.MODELTYPE,"1",Constants.MODELNAME,tmDevice};
	                    
	        	            
	                    data =new JSONObject(Downloader.downloadPostObject(str1));
	        	        
					      
	                } catch (Exception ex) {
	                	Exception exx=ex;
	                }
	                return data;
	            }

	            @Override
	            protected void onPostExecute(JSONObject json) {
	            	if(json==null ) {
	            		Log.i(Constants.TAG,getResources().getString(R.string.netproblem));
	    	        }
	    	        else {
	    	        		int res= json.optInt(Constants.STATUS, -1);
	    	        		String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	    	        		String msg= json.optString(Constants.RESULT, err);
	    	        		if (BuildConfig.DEBUG) Log.i(Constants.TAG,msg);
	    	        		if(res>0){	    	        			
	    	        			String first_name=json.optString(Constants.FIRST_NAME, "");
	    	        	 		String last_name=json.optString(Constants.LAST_NAME, "");
	    	        	 		String usercode= json.optString(Constants.USERCODE, "");
	    	        	 		String userid= json.optString(Constants.USERID, "");
	    	        	 		String email=json.optString(Constants.EMAIL, "");
	    	        	 		//int cvexist=json.optInt(Constants.CVEXIST, -1);
	    	        			String facebookName=json.optString(Constants.FACEBOOKNAME, "");	
	    	        			String description=json.optString(Constants.MY_DESCRIPTION, "");	

	    	        	 		if (usercode.length()>0 && userid.length()>0){
	    	        	 	 		SharedPreferences.Editor editor = settings.edit();
	    	        	 			editor.putString(Constants.USERCODE, usercode);
	    	        	 			editor.putString(Constants.USERID, userid);
	    	        	 			if(first_name.length()>0) editor.putString(Constants.FIRST_NAME, first_name);
	    	        	 			if(last_name.length()>0) editor.putString(Constants.LAST_NAME, last_name);
	    	        	 	 		editor.putString(Constants.EMAIL, email);
	    	        	 	 		if(facebookName.length()>0) editor.putString(Constants.FACEBOOKNAME, facebookName);
	    	        	 	 		if(description.length()>0) editor.putString(Constants.MY_DESCRIPTION, description);

	    	        	 	 		//if(cvexist>-1) editor.putInt(Constants.CVEXIST, cvexist);
	    	        	 	 		editor.commit();
	    	        	 		}	    	        		}
	    	        }
	            }
	        }.execute(null, null, null);
	    } 
	    public String getVersion() {
			 PackageInfo pInfo=null;
			 version="/uknkown/";
				try {
					pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
					 version = "/"+pInfo.versionName+"/"; 
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 return version;
		 }
	    private void defineSideList(){
	        String[] mPlanetTitles0 = getResources().getStringArray(R.array.sidemenuitems0);
	        String[] mPlanetTitles1 = getResources().getStringArray(R.array.sidemenuitems1);
	        String[] mPlanetTitles2 = getResources().getStringArray(R.array.sidemenuitems2);
	        String[] mPlanetTitles3 = getResources().getStringArray(R.array.sidemenuitems3);
	        String[] mPlanetTitles4 = getResources().getStringArray(R.array.sidemenuitems4);
	        String[] mPlanetGroups = getResources().getStringArray(R.array.sidemenugroups);
  		    String useremail=settings.getString(Constants.EMAIL, "");
  		    String usercode=settings.getString(Constants.USERCODE, "");
  		    String userid=settings.getString(Constants.USERID, "");
  		    if(userid.length()>0 && usercode.length()>0 && emailValidator(useremail)){
  		    	mPlanetGroups = getResources().getStringArray(R.array.sidemenugroupsloged);
  		    	mPlanetTitles1 = getResources().getStringArray(R.array.sidemenuitems1loged);
  		    }
  		    List<String> listDataHeader= new ArrayList<String>(Arrays.asList(mPlanetGroups));
	        HashMap<String, List<String>> listDataChild= new HashMap<String, List<String>>();
	        listDataChild.put(mPlanetGroups[0], (Arrays.asList(mPlanetTitles0)));
	        listDataChild.put(mPlanetGroups[1], (Arrays.asList(mPlanetTitles1)));
	        listDataChild.put(mPlanetGroups[2], (Arrays.asList(mPlanetTitles2)));
	        listDataChild.put(mPlanetGroups[3], (Arrays.asList(mPlanetTitles3)));
	        listDataChild.put(mPlanetGroups[4], (Arrays.asList(mPlanetTitles4)));
	        if(mDrawerList!=null) {
	        	mDrawerList.setAdapter(new karov.shemi.oz.ExpandableListAdapter(this, listDataHeader, listDataChild));
	        	for (int i = 0; i < mPlanetGroups.length; i++) {
					if(mPlanetGroups[i].length()<2)mDrawerList.expandGroup(i);
				}
	        	
	        }
		}
	    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
	        switch (requestCode) {
	            case 1: {
	                // If request is cancelled, the result arrays are empty.
	                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
	                	  if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);   
	                      if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
	                      calcXY(); 
	                } else {
	                    // permission denied
	                }
	            return;
	            }
	               
	        }
	    }	
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main2);
       settings = getSharedPreferences(Constants.PREFS_NAME, 0);
       String usercode=settings.getString(Constants.USERCODE, "");
       String userid=settings.getString(Constants.USERID, "");
		    
       mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
       toolbar = (Toolbar) findViewById(R.id.toolbar);
       mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
       mDrawerLayout.setDrawerListener(mDrawerToggle);
       //defineSideList();
       mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
    	   @Override
    	   public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
    		   mDrawerList.setItemChecked(childPosition, true);
    		   mDrawerLayout.closeDrawer(mDrawerList);
    		   selectItem(groupPosition,childPosition);
    		   EasyTracker easyTracker = EasyTracker.getInstance(ResultsListActivity.this);
    		   easyTracker.send(MapBuilder.createEvent("ui_action","button_press","results screen left drawer"+v.toString(),(long)(groupPosition*100+childPosition)).build());
   			
    		   return true;
    	   }
       });
//       fragmentManager = getSupportFragmentManager();

       version=getVersion();
       toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayShowTitleEnabled(false);

       //toolbar.setBackgroundColor(getResources().getColor(R.color.orangebasic));
       //toolbar.setLogo(R.drawable.ic_launcher);
      //toolbar.setNavigationIcon(R.drawable.ic_drawer);
//       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       viewPager = (ViewPager) findViewById(R.id.viewpager);
       ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
       String[] tabstrings= getResources().getStringArray(R.array.tabstrings);
       bydate= new SecondFragment();
       bydistance= new SecondFragment();
       mapfragment=new MapFragment();
       adapter.addFragment(mapfragment, tabstrings[0]);
       adapter.addFragment(bydate, tabstrings[1]);
       adapter.addFragment(bydistance, tabstrings[2]);
       viewPager.setAdapter(adapter);
       tabLayout = (TabLayout) findViewById(R.id.tabs);
       tabLayout.setupWithViewPager(viewPager);
       tabLayout.getTabAt(0).setIcon(R.drawable.tab_selector);
       tabLayout.getTabAt(1).setIcon(R.drawable.tab_selector_today);
       tabLayout.getTabAt(2).setIcon(R.drawable.tab_selector_directions);
       TextView newTab0 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
       newTab0.setText(tabstrings[0]); //tab label txt
       newTab0.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.tab_selector, 0);
       tabLayout.getTabAt(0).setCustomView(newTab0);
       TextView newTab1 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
       newTab1.setText(tabstrings[1]); //tab label txt
       newTab1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.tab_selector_today, 0);
       tabLayout.getTabAt(1).setCustomView(newTab1);
       TextView newTab2 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
       newTab2.setText(tabstrings[2]); //tab label txt
       newTab2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.tab_selector_directions, 0);
       tabLayout.getTabAt(2).setCustomView(newTab2);
       
       int tabindex=settings.getInt(Constants.TAB, -3);
       if(tabindex==-3){
       	if((selections[3]>-1 && selections[4]>0) || selections[4]>10000) tabindex=2;
       	else tabindex=1;
       }
       tabLayout.getTabAt(tabindex).select();
       tabLayout.setOnTabSelectedListener(
    		   new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
    		     @Override
    		     public void onTabSelected(TabLayout.Tab tab) {
    		         super.onTabSelected(tab);
    		         int numTab = tab.getPosition();
    		 		EasyTracker easyTracker = EasyTracker.getInstance(ResultsListActivity.this);
    				easyTracker.send(MapBuilder.createEvent("ui_action","button_press","tab pressed "+tab.getText(),(long)numTab).build());
    		         
    		         SharedPreferences.Editor editor = settings.edit();
    		         editor.putInt(Constants.TAB,numTab);
    		         editor.commit();
    		     }
    		   });
       context = getApplicationContext();
       // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
       if (checkPlayServices()) {
       	gcm = GoogleCloudMessaging.getInstance(this);
       	regid = getRegistrationId(context);
       	if (regid.isEmpty()) {
       		registerInBackground();
       	}
       } else {
       	Log.i(Constants.TAG, "No valid Google Play Services APK found.");
       }
       locationListener1 = new locLis();
       locationListener2 = new locLis();
       locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
       if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
    	        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==PackageManager.PERMISSION_GRANTED &&
    	        ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) ==PackageManager.PERMISSION_GRANTED) {
    	   if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);          
    	   if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
    	   calcXY();
       }
       else{
    	   ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
       }
       extras = getIntent().getExtras();
       if(extras!=null && extras.getDouble(Constants.X)!=0 && extras.getDouble(Constants.Y)!=0){
       	x= extras.getDouble(Constants.X);
       	y= extras.getDouble(Constants.Y);
       }
        xStr =Double.toString(x);
        yStr =Double.toString(y);
	    loc = new Location("");
	    loc.setLatitude(x);
	    loc.setLongitude(y);
	    mylist1=new ArrayList<HashMap<String, String>>();	
	    mylist2=new ArrayList<HashMap<String, String>>();	
	    if(extras!=null){
	    	type =extras.getInt(Constants.TYPE,Constants.SEARCH);
	    }
	    else type=Constants.SEARCH;
	    if (Intent.ACTION_SEARCH.equals(getIntent().getAction())){// || (extras!=null && extras.getString(Constants.MSG, "").toString().length()>1)) {
	    	String query =extras.getString(Constants.MSG, "");
	    	if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) query= getIntent().getStringExtra(SearchManager.QUERY);	
	    	type=Constants.SEARCH;  
	    	/*String forSave=selections[0]+";"+extras.getString(Constants.ROLE,"0")+";"+extras.getString(Constants.SIZE,"0")+";"+selections[3]+";"+selections[4]+";"+""+";"+query;
	    	int ind=0;
	    	boolean alreadyexist=false;
	    	String savedjob=settings.getString(Constants.LASTSEARCHES + ind, null);
	    	while(savedjob!=null &&!alreadyexist){
	    		if(savedjob.equalsIgnoreCase(forSave)) alreadyexist=true;
	    		ind++;
	    		savedjob=settings.getString(Constants.LASTSEARCHES + ind, null);
	    	}
	    	if(!alreadyexist){
	    		ind=ind%Constants.MAX_LAST_SEARCHES;
	    		SharedPreferences.Editor editor = settings.edit();
	    		editor.putString(Constants.LASTSEARCHES+ind, forSave);
	    		editor.commit();	
	    	}*/
	    	String[] str1={Constants.baseUrl+version+Constants.urlCommand3,Constants.QUERY,query,Constants.X,xStr,Constants.Y,yStr,Constants.ORDER,"0",Constants.USERCODE,usercode,Constants.USERID,userid};
	    	String[] str2={Constants.baseUrl+version+Constants.urlCommand3,Constants.QUERY,query,Constants.X,xStr,Constants.Y,yStr,Constants.ORDER,"4",Constants.USERCODE,usercode,Constants.USERID,userid};
	    	GenericDownload downloadFile = new GenericDownload(1,false);
	    	GenericDownload downloadFile2 = new GenericDownload(2,false);
	    	downloadFile.execute(str1);
	    	downloadFile2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,str2);

	    }
	    else if(Intent.ACTION_VIEW.equals(getIntent().getAction())){
	    	Uri data = getIntent().getData();
	    	String url="";
	    	if(data!=null){
	    		url = data.getPath();
	    	}
	    	String[] str1={Constants.baseUrl+version+Constants.urlCommandByURL,"url",data.toString(),Constants.X,xStr,Constants.Y,yStr,Constants.ORDER,"0",Constants.USERCODE,usercode,Constants.USERID,userid};
	    	String[] str2={Constants.baseUrl+version+Constants.urlCommandByURL,"url",data.toString(),Constants.X,xStr,Constants.Y,yStr,Constants.ORDER,"4",Constants.USERCODE,usercode,Constants.USERID,userid};
	    	GenericDownload downloadFile = new GenericDownload(1,false);
	    	GenericDownload downloadFile2 = new GenericDownload(2,false);
	    	downloadFile.execute(str1);
	    	downloadFile2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,str2);
	    }
	    else if (type==Constants.SEARCH){
	    	String query ="";
	    	link="";
	    	if(extras!=null && extras.getIntArray(Constants.VAR)!=null) selections = extras.getIntArray(Constants.VAR);
	    	if(extras!=null){
		    	query =extras.getString(Constants.MSG, "");
		    	link =extras.getString(Constants.LINK, "");
	    		companyParam=extras.getString(Constants.COMPANY,"0");
	    		if(!companyParam.equalsIgnoreCase("0")) {
	    			setTitle(R.string.morefromcompany);
	    			tabLayout.getTabAt(2).select();
	    		}
	    		sel1= extras.getString(Constants.ROLE,"0");// Integer.toString(selections[1]);
	    		sel2= extras.getString(Constants.SIZE,"0");//Integer.toString(selections[2]);
	    		id=Integer.toString(extras.getInt(Constants.ID, 0));
	    	}
	    	else{
	    		sel1= "0";
	    		sel2= "0";
	    		companyParam="0";
	    		id="0";
	    	}
	    	if(selections[3]<0) selections[3]=-1;
	    	String sel0= Integer.toString(selections[0]);
	    	String sel3= Integer.toString(selections[3]);
	    	String sel4=Integer.toString(selections[4]);
	    	if(id.equalsIgnoreCase("0")) id="";
	    	String[] str1={Constants.baseUrl+version+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"cities",sel4,Constants.X,xStr,Constants.Y,yStr,"type","0",Constants.ORDER,"0",Constants.ID,id,Constants.COMPANY,companyParam,Constants.USERCODE,usercode,Constants.USERID,userid,Constants.QUERY,query,"url",link};
	    	String[] str2={Constants.baseUrl+version+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"cities",sel4,Constants.X,xStr,Constants.Y,yStr,"type","0",Constants.ORDER,"4",Constants.ID,id,Constants.COMPANY,companyParam,Constants.USERCODE,usercode,Constants.USERID,userid,Constants.QUERY,query,"url",link};
	    	if(selections[3]<=-1) {
        			str1[9]="radius";
        			str2[9]="radius";
	    	}					
	    	GenericDownload downloadFile = new GenericDownload(1,true);
	    	GenericDownload downloadFile2 = new GenericDownload(2,true);
	    	downloadFile.execute(str1);
	    	downloadFile2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,str2);
	    }
	    
        	
        }
       
        
       
       
   

  
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       	inflater.inflate(R.menu.allresults_activity_menu3, menu);
       return super.onCreateOptionsMenu(menu);
   }
   
   public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle!=null && mDrawerToggle.isDrawerIndicatorEnabled() &&
	            mDrawerToggle.onOptionsItemSelected(item)) {
	        return true;
	    }
   	 switch (item.getItemId()) {
		 case R.id.item_search:
			 EasyTracker easyTracker = EasyTracker.getInstance(this);
			 easyTracker.send(MapBuilder.createEvent("ui_action","button_press","main search job",null).build());
				
			 Intent inten =new Intent(this,SearchActivity.class);
			 inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
			 startActivity(inten);
		    	break;
		
		 default:
			 return super.onOptionsItemSelected(item);
		 }
		 return true;
	}
  
   @Override
	protected void onPause(){
		
		locationManager.removeUpdates(locationListener1);
		locationManager.removeUpdates(locationListener2);
		super.onPause();
	}
   public void onResume(){
	   super.onResume();
	   defineSideList();
	   //if(context!=null) com.facebook.AppEventsLogger.activateApp(context, Constants.FACEBOOKID);
	   if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
			   ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==PackageManager.PERMISSION_GRANTED) {
		   if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
			   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000,10,locationListener1);  
		   if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
			   locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000,10,locationListener2);
	   }
       checkPlayServices();
	   int lastFavChanged = settings.getInt(Constants.FAV, -1);
	   String lastFavChangedMode = settings.getString(Constants.FAVMODE, "");

	   if(mylist1!=null && mylist2!=null && lastFavChanged>0){
		   int ind=0;
		   for (;(Integer.valueOf((String)mylist1.get(ind).get(Constants.ID)))!=lastFavChanged;ind++);
		   HashMap<String, String> o = (HashMap) mylist1.get(ind);
		   o.put(Constants.FAV, lastFavChangedMode);
		   ind=0;
		   for (;(Integer.valueOf((String)mylist2.get(ind).get(Constants.ID)))!=lastFavChanged;ind++);
		   o = (HashMap) mylist2.get(ind);
		   o.put(Constants.FAV, lastFavChangedMode);

		   SharedPreferences.Editor editor = settings.edit();
		   editor.putInt(Constants.FAV, -1);
		   editor.commit();
	   }

   }
	
	private boolean calcXY(){
		  x=0;
		  y=0;
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
	 
	
	
	@Override
	public void onStart() {
		super.onStart();
    	settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		EasyTracker easyTracker = EasyTracker.getInstance(this);
//		easyTracker.activityStop(this);
		easyTracker.set(Fields.SCREEN_NAME, this.getClass().getSimpleName());
		easyTracker.send(MapBuilder.createAppView().build());
	}
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}

	protected void taskResponse(JSONObject json,int responseMode) {
		JSONArray allJobs=json.optJSONArray("list");
		if(allJobs==null  || allJobs.length()==0) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(ResultsListActivity.this);
			builder.setTitle(R.string.noresults);
			builder.setNegativeButton(R.string.tryagain, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		             finish();
		        }
		    });
			try{
			builder.show();
			}finally{}
		}
		else{		  					
			try{
				if(mDbHelper==null) {
					mDbHelper=new NotesDbAdapter(ResultsListActivity.this);
					
				}   
				mDbHelper.open();
				String meter = getResources().getString(R.string.meter);
				String km = getResources().getString(R.string.km);
    	  		
				for(int i=0;i<allJobs.length();i++){            	  
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject ja = allJobs.optJSONObject(i);
					Iterator<String> iter = ja.keys();
					while (iter.hasNext()) {
					    String key = iter.next();
					    try {
					        String value = ja.getString(key);
					        map.put(key,value);
					    } catch (JSONException e) {
					    }
					}
					if (mDbHelper.Exists(Integer.valueOf(ja.optString(Constants.ID)),Constants.SEARCH+1)) {
						map.put(Constants.FAV, "1");
					}
					else map.put(Constants.FAV, "0");
					float[] distance =new float[3];
					double jobx=Double.parseDouble(map.get(Constants.X));
					double joby=Double.parseDouble(ja.optString(Constants.Y));
					Location.distanceBetween(x, y, jobx,joby , distance); 	
					if (distance[0] <1000) {
						map.put(Constants.DISTANCE, String.format("%.0f", distance[0]));
						map.put(Constants.METER, meter);
					}
					else if (distance[0] <1000000) {
						map.put(Constants.DISTANCE, String.format("%.1f", distance[0]/1000));
						map.put(Constants.METER, km);
					}
					else{
						map.put(Constants.DISTANCE, "0");
						map.put(Constants.METER, "0");
					}
					String path=Constants.IMAGE_URL+(ja.getInt(Constants.LOGO)/1000)+"/"+ja.getInt(Constants.LOGO)+Constants.IMAGE_SURRFIX;
					map.put(Constants.PHOTO,path);
					if(responseMode==2) {
						mylist1.add(map);
					}
					else if(responseMode==1) {
						mylist2.add(map);
					}
				}
			}
			catch(JSONException e)        {	     }
			mDbHelper.close();
		}
			if(responseMode==2) {
				bydate.setList(mylist1,ResultsListActivity.this);
			}
			else if(responseMode==1) {
				mapfragment.setList(mylist2,ResultsListActivity.this);
				bydistance.setList(mylist2,ResultsListActivity.this);
			}
			
		       

		  
		
	}

	protected void selectItem(int group,int position) {   
	    switch(group*100+position) {
	    
	    case 0:
	    	Intent inten =new Intent(this,SearchActivity.class);
	        //inten.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);   
	        startActivity(inten);
	    	break;
	    
	    case 1:
	    	inten =new Intent(this,LastSearchesActivity.class);
	        startActivity(inten);
	    	break;
	    	
	    case 100:
	    	inten = new Intent(this,SigninActivity.class);// LoginMainActivity.class);
	    	inten.putExtra(Constants.SECONDTIME, true);
	    	startActivity(inten);
	    	//SignIn();
	    	break;
	    case 101:
	    	 inten=new Intent(this, RegistrationActivity.class);
			  inten.putExtra(Constants.UPDATE, 1);
			  startActivity(inten);
		break;
	    case 102:
	    	inten =new Intent(this,SmartAgent.class);
			startActivity(inten);
			  break;
		
	    case 200:

			 inten = new Intent(this, ShowAll.class);
			 
			 inten.putExtra(Constants.FAV,2);
			 startActivity(inten);
	    	 break;
	    case 201:

				 inten = new Intent(this, ShowAll.class);		 
				 inten.putExtra(Constants.FAV,1);
				 startActivity(inten);
	       	 break;
	    case 202:
	    	sendfriend();
	    	break;
	    case 203:
	    	inten =new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.urlCompany));
	    	startActivity(inten);
		break;
	    
	    case 300:
	    	inten =new Intent(this,SearchHelpActivity.class);
	    	startActivity(inten);
	    	break;
	    case 301:
			 inten = new Intent(this,ContactUsActivity.class);  
			 startActivity(inten);
				 break;
	 case 302:
	 case 303:
	 case 304:
		 inten = new Intent(this,WebViewActivity.class);
		 inten.putExtra(Constants.DESC, position);
		 startActivity(inten);
	        break;
			case 305:

			inten = new Intent(this,WebViewActivity.class);
			inten.putExtra(Constants.DESC, position);
			inten.putExtra(Constants.LINK, "https://www.jobkarov.com/Home/Page/47");
			startActivity(inten);
			break;
	   /* case 103:
	    	inten = new Intent(this, ExplanationsActivity.class);
				 startActivity(inten);
	        break;  
	    case 104:
	  	  confirm(this, R.string.voiceexplain, R.string.explainshowresult);
	  	  break;
	    case 105:
	    	inten = new Intent(this,GeneralTextActivity.class);  
			 inten.putExtra(Constants.DESC, 1);
			 startActivity(inten);
	        break;*/   
	   
	    
	   
	   
	    case 400:
	    	//inten =new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.urlRegister));
	    	//startActivity(inten);
	    	inten=new Intent(this, RegistrationActivity.class);
			  inten.putExtra(Constants.ID, 0);
			  startActivity(inten);
		break;
	    case 401:
	    	facebooklike();
		break;
	    case 402:
	    	toGooglePlus();
		break;
	    case 404:
	    	rate();
		break;
	    case 403:
	    	toTheSite();
		break;
	    case 405:
	    	AlertDialog.Builder builder = new AlertDialog.Builder(ResultsListActivity.this);
			builder.setTitle(R.string.vision);
			builder.setMessage(R.string.visionexplain);
			builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		             dialog.cancel();
		        }
		    });
			builder.show();

	    break;   
	}

	}
	private void toGooglePlus(){
		Intent inten =new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com"));
		startActivity(inten);
	}
	private void toTheSite(){
		Intent inten =new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.JobKarov.com"));
		startActivity(inten);
	}
	private void facebooklike(){
		//Intent intent=getFB();
		Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/JobKarov"));
		startActivity(intent);
	}

	private Intent getFB(){
		 try { 
			   getPackageManager().getPackageInfo("com.facebook.katana", 0); 
			   return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/362475767146819")); 
			 } catch (Exception e) { 
			   return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Appclose2me")); 
			 }
	}
	private void sendfriend(){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.apptoshare));
	    shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.googleplay));    
	    startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.chooseShare)));
	}
	private void rate(){
		final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
		final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

		if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
		{
		    startActivity(rateAppIntent);
		}
		else
		{
		    /* handle your error case */
		}
	}
	public boolean emailValidator(String email) {
	    Pattern pattern;
	    Matcher matcher;
	    if(email.endsWith("@jobkarov.com")) return false;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(email);
	    return matcher.matches();
	}
	
	
   class ViewPagerAdapter extends FragmentPagerAdapter {
       private final List<Fragment> mFragmentList = new ArrayList<>();
       private final List<String> mFragmentTitleList = new ArrayList<>();

       public ViewPagerAdapter(FragmentManager manager) {
           super(manager);
       }

       @Override
       public Fragment getItem(int position) {
           return mFragmentList.get(position);
       }

       @Override
       public int getCount() {
           return mFragmentList.size();
       }

       public void addFragment(Fragment fragment, String title) {
           mFragmentList.add(fragment);
           mFragmentTitleList.add(title);
       }

       public void addFragment(SecondFragment fragment, String title) {
           mFragmentList.add(fragment);
           mFragmentTitleList.add(title);
       }
       
       @Override
       public CharSequence getPageTitle(int position) {
           return mFragmentTitleList.get(position);
       }
   }
   public class GenericDownload extends AsyncTask<String, String, JSONObject> {
		
	    //private final ProgressDialog mProgressDialog = new ProgressDialog(Main2Activity.this);
	    public int responseMode;
	    private boolean showProgressBar;
		public  GenericDownload(int response,boolean showProgressBar){
			super();
			this.responseMode=response;
			this.showProgressBar=showProgressBar;
		}
		private void errorMessage(int title,String msg){
			if(responseMode==2) {
				bydate.setList(mylist1,ResultsListActivity.this);
			}
			else if(responseMode==1) {
				mapfragment.setList(mylist2,ResultsListActivity.this);
				bydistance.setList(mylist2,ResultsListActivity.this);
			}
			final AlertDialog.Builder builder = new AlertDialog.Builder(ResultsListActivity.this);
			builder.setTitle(title);
			builder.setMessage(msg);
			builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		             dialog.cancel();
		        }
		    });
			try{ 
				builder.show();
			}finally{}
			
		}
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(showProgressBar){
	        	//mProgressDialog.setMessage(getResources().getString(R.string.loading));
	        	//mProgressDialog.show();
	        }
	    }
	    @Override
	    protected JSONObject doInBackground(String... url) {
	    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(url));
	    	String resultString= Downloader.downloadPostObject(url);
	    	JSONObject data = null;
	         try{
	             data =new JSONObject(resultString);
	         }
	         catch(Exception e){
	        	 if (BuildConfig.DEBUG) Log.i(Constants.TAG, "failed "+url[0]);
	         }
	         return data;
	    }
	   
	    protected void onPostExecute(JSONObject json) {
	    	//if(mProgressDialog!=null && showProgressBar) mProgressDialog.dismiss();
	    	if (json==null) {
	    		errorMessage( R.string.netproblem,"");
	    	}
	    	else {
	    		int res= json.optInt(Constants.STATUS, -1);	
	    		String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	    		String msg= json.optString(Constants.RESULT, err);
	    		if(res==-1){	
	    			errorMessage(R.string.serverproblem,msg);		        		
	    		}			    
	    		else if(res!=1 && res!=2){
	    			errorMessage(R.string.error,msg);
	    		}		        		
	    		else {
	    			JSONObject conf= json.optJSONObject(Constants.CONF);
	    			if(conf!=null){
	    				JSONArray jsonArr=conf.optJSONArray(Constants.COLOR);
	    		        SharedPreferences.Editor editor = settings.edit();
	    				editor.putString(Constants.COLOR,  jsonArr.toString());
	    				JSONArray jsonMsgs=conf.optJSONArray(Constants.MESSAGES);
	    				if(responseMode==1 && jsonMsgs!=null && jsonMsgs.length()>0) {
	    					JSONObject jsonMsg=jsonMsgs.optJSONObject(0);
	    					String existMsg=settings.getString(Constants.MESSAGES+jsonMsg.optString(Constants.ID, "1"), "");
	    					int upgrade =Integer.valueOf(jsonMsg.optString(Constants.UPGRADE, "0"));
	    					if(upgrade>0){
	    						AlertDialog.Builder builder = new AlertDialog.Builder(ResultsListActivity.this);
	    						builder.setTitle(R.string.newversionexist);
	    						builder.setCancelable(false);
	    						builder.setPositiveButton(R.string.upgrade, new DialogInterface.OnClickListener() {
	    					        public void onClick(DialogInterface dialog, int id) {
	    					        	try {
	    	                                Intent intent = new Intent(Intent.ACTION_VIEW);
	    	                                intent.setData(Uri.parse("market://details?id=karov.shemi.oz"));
	    	                                startActivity(intent);
	    	                                
	    	                            } catch (android.content.ActivityNotFoundException anfe) {
	    	                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=karov.shemi.oz")));
	    	                            }
	    					        	finish();
	    					        }
	    					    });
	    						if(upgrade==1) builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	    					        public void onClick(DialogInterface dialog, int id) {
//	    					        	dialog.dismiss();	    					      
	    					        	}
	    					    });
	    						builder.show();
	    					}
	    					else if(ResultsListActivity.this.isTaskRoot() && jsonMsg!=null && (existMsg.length()==0 || jsonMsg.optString(Constants.UNIQUE, "1").equalsIgnoreCase("0"))) {
//	    						openWeb(Constants.URLMSG+jsonMsg.optString(Constants.ID, "0"), R.string.message,jsonMsg.optInt(Constants.TIMEOUT, 1000),jsonMsg.optBoolean(Constants.TYPE, true));
	    						editor.putString(Constants.MESSAGES+jsonMsg.optString(Constants.ID, "1"), jsonMsg.optString(Constants.ID, "1"));
	    						Intent inten = new Intent(ResultsListActivity.this,WebViewActivity.class);  
	    						 inten.putExtra(Constants.LINK, Constants.URLMSG+jsonMsg.optString(Constants.ID, "0"));
	    						 inten.putExtra(Constants.TIMEOUT,jsonMsg.optInt(Constants.TIMEOUT, 100000000));
	    						 startActivity(inten);
	    						
	    					}
	    				}
	    				editor.commit();

	    			}
	    			taskResponse(json,this.responseMode);
	    		}
	    		}
	    }
	}
   protected void openWeb(String website,int title,int seconds,boolean showClosebButton){
		 final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.loading), true);
		 final AlertDialog.Builder dialog = new AlertDialog.Builder(this); 
		 dialog.setTitle(title);
		 WebView mWebview = new WebView(this);
		 mWebview.setWebViewClient(new WebViewClient() {
			 public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//				 confirm(MenuActionActivity.this,R.string.error);
			 }
			 @Override
			 public void onPageStarted(WebView view, String url, Bitmap favicon){
				 pd.show();
			 }
			 @Override
			 public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 view.loadUrl(url);
				 return true;
			 }
			 @Override
			 public void onPageFinished(WebView view, String url) {
				 pd.dismiss();
				 //String webUrl = mWebview.getUrl();
			 }
		 });
		 mWebview.loadUrl(website);
		 dialog.setView(mWebview);
		 if(showClosebButton) dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			 @Override
			 public void onClick(DialogInterface dialog, int id) {
				 dialog.dismiss();
			 }	
		 });
		 final AlertDialog alert = dialog.create();
		 alert.show();
		 final Handler handler  = new Handler();
		 final Runnable runnable = new Runnable() {
			 @Override
			 public void run() {
				 if (alert.isShowing()) {
					 alert.dismiss();
				 }	
			 }
		 };
		 alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
			 @Override
			 public void onDismiss(DialogInterface dialog) {
				 handler.removeCallbacks(runnable);
			 }
		 });
		 handler.postDelayed(runnable, 1000*seconds);
	 }
	
}