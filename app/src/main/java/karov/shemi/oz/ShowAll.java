package karov.shemi.oz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class ShowAll extends MenuActionActivity{
	private ListView lv1;
	private TextView tv1,tv2;
	//private Intent inten;
	private double x,y;
	private String xStr,yStr, resultCompany,resultCity,companyParam,id,sel1,sel2,regid;
	private NotesDbAdapter mDbHelper;
	private Cursor mNotesCursor;
	private int type,firstVisibleRow,lastVisibleRow,positionGlobal,sortby;
	private Location loc;
	private ArrayList<HashMap<String, String>> mylist; 
	//private int[] to2 = new int[]{R.id.text1,R.id.text2,R.id.text3};
	private LocationListener locationListener1,locationListener2;
	private LocationManager locationManager;	
	private BaseAdapter adapter;
	private Bundle extras=null;
	protected int[] selections={0,0,0,-1,50000};
	private GoogleCloudMessaging gcm;
	private Context context;
    private Handler mHandler = new Handler();


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
	        // Check if app was updated; if so, it must clear the registration ID
	        // since the existing regID is not guaranteed to work with the new
	        // app version.
	        int registeredVersion = settings.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	        int currentVersion = getAppVersion(context);
	        if (registeredVersion != currentVersion) {
	            Log.i(Constants.TAG, "App version changed.");
	            return "";
	        }
	        return registrationId;
	    }

	    /**
	     * Registers the application with GCM servers asynchronously.
	     * <p>
	     * Stores the registration ID and the app versionCode in the application's
	     * shared preferences.
	     */
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
	    	        			optAndSave(json);
	    	        		}
	    	        }
	            }
	        }.execute(null, null, null);
	    } 

	protected void onRestart(){
		super.onRestart();
		if(adapter!=null && !adapter.isEmpty()) {
			adapter.notifyDataSetChanged();
			String str2= Integer.toString(adapter.getCount());
			tv1.setText(str2);
			tv2.setVisibility(View.VISIBLE);
		}
	}
	protected void onDestroy(){
		if(mNotesCursor!=null) mNotesCursor.close();
		super.onDestroy();
	}
	@Override
	protected void onPause(){
		
		locationManager.removeUpdates(locationListener1);
		locationManager.removeUpdates(locationListener2);
		super.onPause();
	}
	public void onResume(){
        super.onResume();
		//if(context!=null) com.facebook.AppEventsLogger.activateApp(context, Constants.FACEBOOKID);
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000,10,locationListener1);  
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000,10,locationListener2);
        checkPlayServices();
    }
	private void sortMenu(){
		 AlertDialog.Builder builder;
		 builder = new AlertDialog.Builder(this);
		 builder.setTitle(R.string.orderstring);
		 builder.setItems(R.array.sortby, new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int item) {
				 dialog.dismiss();
				 sort(item);
			 }
		 });
		 builder.setNegativeButton(R.string.cancel,
	                new DialogInterface.OnClickListener() {
	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });
		 builder.show();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle!=null && mDrawerToggle.isDrawerIndicatorEnabled() &&
	            mDrawerToggle.onOptionsItemSelected(item)) {
	        return true;
	    }
		 switch (item.getItemId()) {
		 case R.id.item_search:
			 Intent inten =new Intent(this,SearchActivity.class);
			 inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
			 startActivity(inten);
		    	break;
		/* case R.id.item_map:
			 openMap();
			 break;*/	
		 default:
			 return super.onOptionsItemSelected(item);
		 }
		 return true;
	}
	        
	 private void openMap(){
		 if(checkIfMapsIsOk(this)){
			 firstVisibleRow=0;
			 lastVisibleRow=mylist.size()-1;
			 if(lastVisibleRow>299)lastVisibleRow=299;
			 Intent intent2 = new Intent(ShowAll.this, MapActivity.class);
			 String[] xxx=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] yyy=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] names=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] companies=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] indexes=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] addresses=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] pictures=new String[lastVisibleRow-firstVisibleRow+1];
			 String[] citiesMap=new String[lastVisibleRow-firstVisibleRow+1];
			 int count=0;
			 if (type==Constants.SEARCH){
				 for (int i = firstVisibleRow; i <= lastVisibleRow; i++) {
					 HashMap<String, String> o = (HashMap<String, String>) lv1.getItemAtPosition(i);
					 if(((String)o.get(Constants.CITYNAME)).length()>0){
						 count++;
					 }
					 String x =o.get(Constants.X);	
					 String y =o.get(Constants.Y);
					 xxx[i-firstVisibleRow]=x;
					 yyy[i-firstVisibleRow]=y;
					 names[i-firstVisibleRow]=o.get(Constants.NAME);
					 companies[i-firstVisibleRow]=o.get(Constants.COMPANY);
					 indexes[i-firstVisibleRow]=o.get(Constants.ID);
					 addresses[i-firstVisibleRow]=o.get(Constants.ADDRESS);
					 pictures[i-firstVisibleRow]=o.get(Constants.PHOTO);
					 citiesMap[i-firstVisibleRow]=o.get(Constants.CITYNAME);
					 //}
				 }
			 }
			 else{
				 for (int i = firstVisibleRow; i <= lastVisibleRow; i++) {
					 Cursor c = mNotesCursor;
					 c.moveToPosition(i);
					 if(c.getString(6).length()>0){
						 count++;
					 }
					 xxx[i-firstVisibleRow]=Double.toString(c.getDouble(3));
					 yyy[i-firstVisibleRow]=Double.toString(c.getDouble(4));
					 names[i-firstVisibleRow]=c.getString(2);
					 companies[i-firstVisibleRow]=c.getString(5);
					 indexes[i-firstVisibleRow]=Integer.toString(c.getInt(0));
					 addresses[i-firstVisibleRow]=c.getString(6);		
				 }
			 }
			 intent2.putExtra(Constants.X, xxx);
			 intent2.putExtra(Constants.Y, yyy);
			 intent2.putExtra(Constants.NAME, names);
			 intent2.putExtra(Constants.COMPANY, companies);
			 intent2.putExtra(Constants.ID, indexes);
			 intent2.putExtra(Constants.ADDRESS, addresses);
			 intent2.putExtra(Constants.PHOTO, pictures);
			 intent2.putExtra(Constants.CITIES, citiesMap);
			 if(count>0)	startActivity(intent2);
			 else confirm(this,R.string.hiddenaddress, R.string.hiddenaddressmsg);
		 }
	 }
	 private void filterItems(){
		// ((ImageAdapter)adapter).getFilter(Constants.COMPANY).filter("");
		 //((ImageAdapter)adapter).getFilter(Constants.CITY).filter("");
		 Intent intent2 = new Intent(this, FilterActivity.class);
		 ArrayList<String> cities=new ArrayList<String>(mylist.size());
		 ArrayList<String> companies=new ArrayList<String>(mylist.size());
		 ArrayList<String> AllCities=new ArrayList<String>(mylist.size());
		 ArrayList<String> AllCompanies=new ArrayList<String>(mylist.size());
		
		 for (int i = 0; i < mylist.size(); i++) {
			 HashMap<String, String> o = (HashMap<String, String>)mylist.get(i);
			 if(!companies.contains(o.get(Constants.COMPANY)) && !o.get(Constants.COMPANY).isEmpty()) companies.add(o.get(Constants.COMPANY));
			 if(!cities.contains(o.get(Constants.CITYNAME)) && !o.get(Constants.CITYNAME).isEmpty()) cities.add(o.get(Constants.CITYNAME));	 
			 AllCities.add(o.get(Constants.CITYNAME));
			 AllCompanies.add(o.get(Constants.COMPANY));
		 }
		 Collections.sort(companies, String.CASE_INSENSITIVE_ORDER);
		 Collections.sort(cities, String.CASE_INSENSITIVE_ORDER);
		 intent2.putExtra(Constants.COMPANY, companies);
		 intent2.putExtra(Constants.CITIES, cities);
		 intent2.putExtra(Constants.VAR, AllCities);
		 intent2.putExtra(Constants.RANGE, AllCompanies);
		 intent2.putExtra(Constants.COMPANYNAME, resultCompany);
		 intent2.putExtra(Constants.CITYNAME, resultCity);///
		 startActivityForResult(intent2,1);
		 
	 }
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    if (requestCode == 1) {
		    	String str2= Integer.toString(mylist.size());
				 adapter = new ImageAdapter(this, mylist);	
				 tv1.setText(str2);//+" "+str1+" ");
				 tv2.setVisibility(View.VISIBLE);
				 lv1.setAdapter(adapter);	
				 
		        if(resultCode == RESULT_OK){
		            resultCompany=data.getStringExtra(Constants.COMPANY);
		            resultCity=data.getStringExtra(Constants.CITIES);
		            String[] fileds ={Constants.COMPANY,Constants.CITIES};
		            String[] constraints={data.getStringExtra(Constants.COMPANY),data.getStringExtra(Constants.CITIES)};
		            if(resultCompany.length()>0 || resultCity.length()>0) ((ImageAdapter)adapter).filter(fileds,constraints);
		            //if(resultCity.length()>0) ((ImageAdapter)adapter).getFilter(Constants.CITY).filter(resultCity);
		            str2= Integer.toString(adapter.getCount());
		    		tv1.setText(str2);
		    		tv2.setVisibility(View.VISIBLE);
		           
		        }
		        else if (resultCode == RESULT_CANCELED) {
		         
		        }
		    }
		}
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        //if(extras==null || extras.getInt(Constants.TYPE,Constants.SEARCH)!=Constants.SEARCH) inflater.inflate(R.menu.allresults_activity_menu2, menu);
	        //else 
	        	inflater.inflate(R.menu.allresults_activity_menu2, menu);
	        return true;//super.onCreateOptionsMenu(menu);
	    }

	public static boolean checkIfMapsIsOk(Activity context) {
	    int checkGooglePlayServices =    GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	    if (checkGooglePlayServices != ConnectionResult.SUCCESS) { 
	        GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices, context, 1122).show();
	        return false;
	    } else {
	        return true;
	    }
	}
	public void onCreate(Bundle savedInstanceState) {
        super.onCreateSuper(savedInstanceState);
        extras = getIntent().getExtras();
        setContentView(R.layout.searchlayout2);         		       
        super.onCreate(savedInstanceState);
        /*actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                int index=tab.getPosition();
                sort(index);
                SharedPreferences.Editor editor = settings.edit();
    			editor.putInt(Constants.TAB,index);
    			editor.commit();
            }
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };*/
        //actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.TransparentGrey)));
        //actionBar.setSplitBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orangebasic)));
        /*String[] tabnames =getResources().getStringArray(R.array.showalltabs);
        for (int i = 0; i < tabnames.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(tabnames[i])
                            .setTabListener(tabListener));
        }*/
        
        positionGlobal=-1;
        locationListener1 = new locLis();
        locationListener2 = new locLis();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);   
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
        lv1=(ListView)findViewById(R.id.list);
		//registerForContextMenu(lv1);
		tv1 =(TextView) findViewById(R.id.instructionTv);
		tv2 =(TextView) findViewById(R.id.jobstitle);
        boolean heb =settings.getBoolean(Constants.HEBREW, true);
        if (heb) {
        	tv1.setGravity(Gravity.LEFT);
        }
        calcXY();
        if(extras!=null && extras.getDouble(Constants.X)!=0 && extras.getDouble(Constants.Y)!=0){
        	x= extras.getDouble(Constants.X);
        	y= extras.getDouble(Constants.Y);
        }
	    xStr =Double.toString(x);
	    yStr =Double.toString(y);
	    //Toast.makeText(this,"x="+x+" y="+y, Toast.LENGTH_LONG).show();
	    loc = new Location("");
	    loc.setLatitude(x);
	    loc.setLongitude(y);
        mylist = (ArrayList<HashMap<String, String>>) getLastNonConfigurationInstance();
        if (mylist==null){
        	mylist=new ArrayList<HashMap<String, String>>();	
        	if(extras!=null){
        		type =extras.getInt(Constants.TYPE,Constants.SEARCH);
        	}
        	else type=Constants.SEARCH;
        	if (Intent.ACTION_SEARCH.equals(getIntent().getAction()) || (extras!=null && extras.getString(Constants.MSG, "").toString().length()>1)) {
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
        			SharedPreferences.Editor editor = settings.edit();
        			editor.putString(Constants.LASTSEARCHES+ind, forSave);
        			editor.commit();	
        		}*/
        		String[] str1={Constants.baseUrl+version+Constants.urlCommand3,"query",query,"x",xStr,"y",yStr,"for","1"};
        		GenericDownload downloadFile = new GenericDownload(0,true);
        		downloadFile.execute(str1);
        	}
        	else if(Intent.ACTION_VIEW.equals(getIntent().getAction())){
    			Uri data = getIntent().getData();
    			String url="";
    			if(data!=null){
    				url = data.getPath();
    			}
    			String[] str1={Constants.baseUrl+version+Constants.urlCommandByURL,"url",url,"x",xStr,"y",yStr,"for","1"};
    			GenericDownload downloadFile = new GenericDownload(0,true);
        		downloadFile.execute(str1);
    		}
        	else if (type==Constants.SEARCH){
        		int fav=0;
        		if(extras!=null && extras.getIntArray(Constants.VAR)!=null) selections = extras.getIntArray(Constants.VAR);
        		if(extras!=null){
        			fav=extras.getInt(Constants.FAV, 0);
        			if(fav==1) setTitle(R.string.visited);
        			else if(fav==2) setTitle(R.string.favour);
        			companyParam=extras.getString(Constants.COMPANY,"0");
        			if(!companyParam.equalsIgnoreCase("0")) setTitle(R.string.morefromcompany);
        			sel1= extras.getString(Constants.ROLE,"0");// Integer.toString(selections[1]);
        			sel2= extras.getString(Constants.SIZE,"0");//Integer.toString(selections[2]);
        			id=Integer.toString(extras.getInt(Constants.ID, 0));
        		}
        		else{
        			/*Set<String> set = settings.getStringSet(Constants.LAST_SEARCHES, new HashSet<String>());
			    		if(set.size()>0){
			    			String[] array = set.toArray(new String[set.size()]);
			  	    	  	String[] elements=array[array.length-1].split(";");
			  	  			selections[0] =Integer.valueOf(elements[0]);
			  	  			sel1 =elements[1];
			  	  			sel2 =elements[2];
			  	  			selections[3] =Integer.valueOf(elements[3]);
			  	  			selections[4] =Integer.valueOf(elements[4]);
			  	  			//elements[5] is address
			  	  			xStr=elements[6];
			  	  			yStr=elements[7];
			    		}
			    		else{*/
        			sel1= "0";
        			sel2= "0";
        			//}
        			companyParam="0";
        			id="0";
        		}
        		if(selections[3]<0) selections[3]=-1;
        		String sel0= Integer.toString(selections[0]);
        		String sel3= Integer.toString(selections[3]);
        		String sel4=Integer.toString(selections[4]);
        		if(id.equalsIgnoreCase("0")) id="";
        		//Toast.makeText(this,id,Toast.LENGTH_LONG);
        		//if (selections[3]==-1) sel4=getResources().getStringArray(R.array.radiuses2)[selections[4]];
        		String[] str1={Constants.baseUrl+version+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"cities",sel4,"x",xStr,"y",yStr,"type","0","for","1",Constants.ID,id,Constants.COMPANY,companyParam,Constants.FAV,Integer.toString(fav),Constants.USERID,userid,Constants.USERCODE,usercode};
        		if(selections[3]<=-1) {
        			str1[9]="radius";
        			//String[] str4={Constants.url1+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"radius",sel4,"x",xStr,"y",yStr,"type","0","for","1",Constants.ID,id,Constants.COMPANY,Integer.toString(companyParam)};
        			//str1=str4;
        		}					
        		GenericDownload downloadFile = new GenericDownload(0,true);
        		downloadFile.execute(str1);
        	}
        	else{
        		mDbHelper = new NotesDbAdapter(this);
        		mDbHelper.open();	
        		fillData();
        		lv1.setOnItemClickListener(new OnItemClickListener() {
        			public void onItemClick(AdapterView<?> parent, View view,int positionLocal, long id) {
        				if(positionGlobal==-1){
        					Cursor c = mNotesCursor;
							c.moveToPosition(positionLocal);
							Intent inten = new Intent(ShowAll.this, JobDetailsActivity.class);
							inten.putExtra(Constants.COMPANY, c.getString(2));
							inten.putExtra(Constants.NAME,c.getString(5));
							inten.putExtra(Constants.ADDRESS,c.getString(6));
							inten.putExtra(Constants.PHOTO,c.getString(7));
							inten.putExtra(Constants.ID,c.getInt(0));
							inten.putExtra(Constants.X,c.getDouble(3));
							inten.putExtra(Constants.Y,c.getDouble(4));
							inten.putExtra(Constants.MYX,x);
							inten.putExtra(Constants.MYY,y);
							startActivity(inten);
        				}
        				//tmpindex=c.getInt(0);//ColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID);
        				//tmptype=c.getInt(1);
        				//inten.putExtra(Constants.TYPE, tmpindex);
        				//String str1=Constants.url1+Constants.urlCommand1+Integer.toString(tmpindex);
        				//DownloadFile downloadFile = new DownloadFile();
        				//downloadFile.execute(str1);     
        			}			   
        		});
        		lv1.setOnItemLongClickListener(new OnItemLongClickListener() {
        			@Override
        			public boolean onItemLongClick(AdapterView<?> arg0,View arg1, int positionLocal, long arg3) {
        				positionGlobal=positionLocal;
        				AlertDialog.Builder builder;
        				builder = new AlertDialog.Builder(ShowAll.this);
        				builder.setTitle(R.string.delete);							
        				builder.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
        					@Override
        					public void onClick(DialogInterface dialog, int which) {
        						positionGlobal=-1;
        					} 
        				});
        				builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
        					@Override
        					public void onClick(DialogInterface dialog, int which) {
        						Cursor c = mNotesCursor;
        						c.moveToPosition(positionGlobal);
        						positionGlobal=-1;
        						deleteSaved(c.getInt(0),c.getInt(1));
        					}
        				});
        				builder.show();							
        				return false;
        			}
        		});
        	}
        }
        else {
        	fillList();
        }
        
        /*int tabindex=settings.getInt(Constants.TAB, -3);
        if(tabindex==-3){
        	if((selections[3]>-1 && selections[4]>0) || selections[4]>10000) tabindex=0;
        	else tabindex=1;
        }
        actionBar.setSelectedNavigationItem(tabindex);
        */
        lv1.setOnScrollListener(new OnScrollListener() {	
        	@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
        	@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        		firstVisibleRow = lv1.getFirstVisiblePosition();
        		lastVisibleRow = lv1.getLastVisiblePosition();	
        	}
        });
        Button sort = (Button) findViewById(R.id.sortbutton);
        if (type==Constants.SEARCH) {
        	sort.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View arg0) {
        			sortMenu();
        		} 
        	});
        }
        else sort.setVisibility(View.GONE);
        Button filter = (Button) findViewById(R.id.filterbutton);
        if (type==Constants.SEARCH) {
        	filter.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View arg0) {
        			filterItems();
        		} 
        	});
        }
        else filter.setVisibility(View.GONE);
        Button map = (Button) findViewById(R.id.mapbutton);
        if (type==Constants.SEARCH) {
        	map.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View arg0) {
        			openMap();
        		} 
        	});
        }
        else map.setVisibility(View.GONE);
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

	}
	public void sort(int by){
		 //final ProgressDialog mProgressDialog22 = ProgressDialog.show(this, getResources().getString(R.string.sort),getResources().getString(R.string.loading), true);
		sortby=by;
		new Thread() {

			public void run() {
			if (type==Constants.SEARCH) {
				Comparator<HashMap<String, String>> comparator=null;
				switch (sortby){
					
					case 0:
						comparator = new Comparator<HashMap<String, String>>() {  
							 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
							@Override
							public int compare(HashMap<String, String> object1, HashMap<String, String> object2){       
								try {
									return dateFormat.parse(object1.get(Constants.UPDATEDATE)).compareTo(dateFormat.parse(object2.get(Constants.UPDATEDATE)));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									return 0;
								}
							}
						};
						break;
					case 1:
				comparator = new Comparator<HashMap<String, String>>() {                                    
					@Override
					public int compare(HashMap<String, String> object1, HashMap<String, String> object2){       
						if (object1.get(Constants.METER)!=object2.get(Constants.METER)) return object1.get(Constants.METER).compareToIgnoreCase(object2.get(Constants.METER));
						else return Float.valueOf(object1.get(Constants.DISTANCE)).compareTo(Float.valueOf(object2.get(Constants.DISTANCE)));
					}
				};  
				
				
			break;
					case 2:
						comparator = new Comparator<HashMap<String, String>>() {                                    
							@Override
							public int compare(HashMap<String, String> object1, HashMap<String, String> object2){       
								return object1.get(Constants.COMPANY).compareToIgnoreCase(object2.get(Constants.COMPANY));
							}
						};
						break;
					case 3:
						comparator = new Comparator<HashMap<String, String>>() {                                    
							@Override
							public int compare(HashMap<String, String> object1, HashMap<String, String> object2){
								return object1.get(Constants.ROLENAME).compareToIgnoreCase(object2.get(Constants.ROLENAME));
							}
						};      					
				break;
						case 4:
						comparator = new Comparator<HashMap<String, String>>() {                                    
							@Override
							public int compare(HashMap<String, String> object1, HashMap<String, String> object2){  
//								return Float.valueOf(object1.get(Constants.CITY)).compareTo(Float.valueOf(object2.get(Constants.CITY)));
								return object1.get(Constants.CITYNAME).compareToIgnoreCase(object2.get(Constants.CITYNAME));
							}
						};
						break;
				}
			if(mylist!=null) Collections.sort(mylist, comparator);
			}
			else {
				mNotesCursor=mDbHelper.fetchAllNotes(type,sortby,x,y);
				//startManagingCursor(mNotesCursor);
				((CustomCursorAdapter) adapter).changeCursor(mNotesCursor);
				//adapter.notifyDataSetChanged();
			}
			mHandler.post(new Runnable() {
                public void run() {
        			if(adapter!=null) adapter.notifyDataSetChanged();
        			//mProgressDialog22.dismiss();
                }
            });
			
			}}.start();
			//lv1.setAdapter(adapter);		
		}
	
		private void fillList(){
			adapter = new ImageAdapter(this, mylist);
			String str2= Integer.toString(mylist.size());
			tv1.setText(str2);//+" "+str1+" ");
			tv2.setVisibility(View.VISIBLE);
			lv1.setAdapter(adapter);					
			lv1.setTextFilterEnabled(true);
			
			
		}
		private void fillData() {
		mNotesCursor=mDbHelper.fetchAllNotes(type,0,x,y);	    
	    String str1;
		if (type>Constants.SEARCH+1) {
			str1=getResources().getString(R.string.numviewed);
			setTitle(R.string.visited);
		}
		else {
			str1=getResources().getString(R.string.numsave);
			setTitle(R.string.favorites);
		}
		tv2.setText(str1);
	    String str2= Integer.toString(mNotesCursor.getCount());
	    tv1.setText(str2);//+" "+str1);
	    tv2.setVisibility(View.VISIBLE);
	    //String str = getResources().getString(R.string.noentries);
	    if (mNotesCursor.getCount()==0){
	    	Toast.makeText(this,R.string.noentries, Toast.LENGTH_LONG).show();
	    	finish();
	    }
	    //startManagingCursor(mNotesCursor);
	    //String[] from = new String[]{NotesDbAdapter.KEY_TITLE,NotesDbAdapter.KEY_NAME,NotesDbAdapter.KEY_ADRESS};
	    adapter =  new CustomCursorAdapter(this, mNotesCursor, 0); 
	    		//SimpleCursorAdapter(this, R.layout.twolineslayout, mNotesCursor, from, to2);
	    lv1.setAdapter(adapter);
	}
		private void deleteSaved(int id,int thistype){
			mDbHelper.deleteNote(id,thistype);
			mNotesCursor=mDbHelper.fetchAllNotes(type,0,x,y);
			//startManagingCursor(mNotesCursor);
			((CustomCursorAdapter) adapter).changeCursor(mNotesCursor);
		
			adapter.notifyDataSetChanged();

		    String str1;
			if (type>Constants.SEARCH+1) str1=getResources().getString(R.string.numviewed);
			else str1=getResources().getString(R.string.numsave);
			tv2.setText(str1);
		    String str2= Integer.toString(mNotesCursor.getCount());
		    tv1.setText(str2);//+" "+str1);
		    tv2.setVisibility(View.VISIBLE);
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
   	 
		protected void taskResponse(JSONObject json,int responseMode) {
			JSONArray allJobs=json.optJSONArray("list");
			if(allJobs==null  || allJobs.length()==0) {
				if(extras!=null) confirmFinish(ShowAll.this, R.string.noresults);
	    		else confirm(ShowAll.this, R.string.noresults);		  					
			}
			else{		  					
				try{
					if(mDbHelper==null) {
						mDbHelper=new NotesDbAdapter(ShowAll.this);
						mDbHelper.open();
					}        		  					
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
						mylist.add(map);
					}
				}catch(JSONException e)        {	     }
				fillList();
				mDbHelper.close();
			}
		
		}

}
