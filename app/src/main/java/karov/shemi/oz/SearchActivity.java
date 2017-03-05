package karov.shemi.oz;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class SearchActivity extends MenuActionActivity implements MyDialog.NoticeDialogListener{
	protected double myx,myy;
	private String str,addressString;
	private Context context;
	protected int[] selections={0,0,0,-1,15000};
	protected int[] radiuses;
	private LocationListener locationListener1,locationListener2;
	private LocationManager locationManager;
	protected Spinner spinner5,spinner1,spinner4;
	private TextView tvradius,textspin1,textspin2,textspin3,textspin4,tv;
	private ArrayList<String> roles,dataCity,specialities,areas,types;
	private ArrayList<ArrayList<String>> allRoles,allCities;
	private CustomAdapterSpinner spinnerArrayAdapter1,spinnerArrayAdapter4,spinnerArrayAdapter5;
	protected boolean passEnoughTime,ready,updateAddressByGPS;
	private boolean presssed5=false;	
	protected String[] multiSelected={"0","0"};
	protected boolean addnumbers;
	//private ProgressDialog mProgressDialog;
	protected MultiSelectionSpinner spinner2,spinner3;
	protected int[] indexTypes,indexRoles,indexSpecialities,indexAreas;
	private ArrayList<ArrayList<Integer>> allIndexRoles;
	protected  HashMap<String, Integer> cityMap=new HashMap<String, Integer>();
	protected Button change1,change2,change3,change4,change5,changeAddress;
	protected SearchView searchView;
//	private int secondtime;
		
	@Override
	protected void onPause(){
		super.onPause();
		locationManager.removeUpdates(locationListener1);
		locationManager.removeUpdates(locationListener2);
	}
	public void onResume(){
		//if(context!=null) com.facebook.AppEventsLogger.activateApp(context, Constants.FACEBOOKID);
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
 			   ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==PackageManager.PERMISSION_GRANTED) {
 		   if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
 			   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000,10,locationListener2);  
 		   if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
 			   locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000,10,locationListener1);
 	   }

	}
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
	 protected void readSaved(){}
	protected void config(){
		//actionBar.hide();
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) findViewById(R.id.searchView);
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		//searchView.setIconified(true);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

		    @Override
		    public boolean onQueryTextSubmit(String query) {
		        calcAction();
		        return false;
		    }

		    @Override
		    public boolean onQueryTextChange(String newText) {
		    	spinner4.setSelection(1);
		    	return false;
		    }
		});
		searchView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				searchView.setIconified(false);
				searchView.setBackgroundResource(R.drawable.rectangle_border);
				
				
			}
		});
		searchView.setOnSearchClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchView.setBackgroundResource(R.drawable.rectangle_border);
				
				
			}
		});
		searchView.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public boolean onClose() {
				searchView.setBackgroundResource(R.drawable.freesearch2);
				return false;
			}
		});
			
		addnumbers=false;
				       // mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		ready=false;

		     
				 
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreateSuper(savedInstanceState);
        setContentView(R.layout.mainnav);
        super.onCreate(savedInstanceState);
        updateAddressByGPS=true;
//		 String name=settings.getString(Constants.NAME, "");	
		/*try{
			secondtime =settings.getInt(Constants.SECONDTIME, 0);
		}
		catch(Exception e){
			boolean tmp=settings.getBoolean(Constants.SECONDTIME, false);
			if(tmp)secondtime=1;
			else secondtime=0;
		}
		 secondtime++;		 
		 SharedPreferences.Editor editor = settings.edit();
		 editor.putInt(Constants.SECONDTIME, secondtime);		 
		 editor.commit();	
		 if(secondtime==2 && name.length()==0){
			 Intent inten =new Intent(this,FirstLoginActivity.class);
			דיב startActivity(inten);	
		 }*/
		addressString="";
		changeAddress = (Button) findViewById(R.id.addressButton);
		changeAddress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {	
				openMapSelect();
				searchView.clearFocus(); 
			}
		});   
		
		config();	
			 
        textspin1= (TextView)findViewById(R.id.textspin1);
        textspin2= (TextView)findViewById(R.id.textspin2);
        textspin3= (TextView)findViewById(R.id.textspin3);
        textspin4= (TextView)findViewById(R.id.textspin4);
           tvradius= (TextView)findViewById(R.id.textspin5);
        Typeface tf = Typeface.createFromAsset(getAssets(),"Alef-Bold.ttf");
        tv =(TextView)findViewById(R.id.answer);  
        tv.setVisibility(View.GONE);
        LinearLayout firstLL=(LinearLayout)findViewById(R.id.firstLL);
        firstLL.setVisibility(View.GONE);
        
        radiuses=getResources().getIntArray(R.array.radiuses2);
        
        String[] dataCityString=getResources().getStringArray(R.array.city0);
        String allString=getResources().getString(R.string.all);
        allRoles=new ArrayList<ArrayList<String>>();
        allIndexRoles=new ArrayList<ArrayList<Integer>>();

        allCities=new ArrayList<ArrayList<String>>();

        ArrayList<String> inner= new ArrayList<String>();
        ArrayList<Integer> innerIndex= new ArrayList<Integer>();
        ArrayList<String> innercity0= new ArrayList<String>();
        //ArrayList<String> innercity1= new ArrayList<String>();
        dataCity =new ArrayList<String>();
        //inner.add(allString);
        //innerIndex.add(0);
        for (int i = 0; i < dataCityString.length; i++) {
			innercity0.add(dataCityString[i]);
			dataCity.add(dataCityString[i]);
			cityMap.put(dataCityString[i],radiuses[i]);
		}
        cityMap.put(allString, 0);
        allRoles.add(inner);
        allIndexRoles.add(innerIndex);
        allCities.add(innercity0);
        roles =new ArrayList<String>();
        
        specialities=new ArrayList<String>();
        types=new ArrayList<String>();
        areas=new ArrayList<String>();
        specialities.add(allString);
        indexSpecialities=new int[1];
        indexSpecialities[0]=0;
        //types.add(allString);

        //roles.add(allString);
        String[] areasbasic=getResources().getStringArray(R.array.areasbasic);
        indexAreas=new int[areasbasic.length];
        for (int i = 0; i < areasbasic.length; i++) {
			areas.add(areasbasic[i]);
			indexAreas[i]=i-areasbasic.length;
		}
        
        spinner2 = (MultiSelectionSpinner) findViewById(R.id.multSpinner2);  

/*        spinner2 = (Spinner) findViewById(R.id.spin2);
        spinnerArrayAdapter2 = new CustomAdapterSpinner(this, roles);
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner2.setAdapter(spinnerArrayAdapter2);*/
           //spinner2.setOnItemSelectedListener(new NormalOnItemSelectedListener(1)); 
           indexRoles=new int[1];
           indexRoles[0]=0;
           
           spinner2.setItems(roles,new ArrayList<String>(),indexRoles,true,true);  
            
        
        spinner1 = (Spinner) findViewById(R.id.spin1);
        spinnerArrayAdapter1 = new CustomAdapterSpinner(this, specialities);
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        spinner1.setAdapter(spinnerArrayAdapter1);
       
       // ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
         //       this, R.array.specialities, R.layout.spinner_layout);      
        //adapter.setDropDownViewResource(R.layout.spinner_item);
        //spinner1.setAdapter(adapter);
        change1 = (Button) findViewById(R.id.change1);
        change1.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 
        		spinner1.performClick();	 
        		spinner1.setVisibility(View.VISIBLE);
       	 }
        });
        textspin1.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 
        		spinner1.performClick();	 
        		spinner1.setVisibility(View.VISIBLE);
       	 }
        });
        change2 = (Button) findViewById(R.id.change2);
        change2.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus();

        		spinner2.setVisibility(View.VISIBLE);
        		spinner2.performClick();	
        		change2.setText(R.string.change2);
       	 }
        });
        textspin2.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus();

        		spinner2.setVisibility(View.VISIBLE);
        		spinner2.performClick();	
        		change2.setText(R.string.change2);
       	 }
        });
        change3 = (Button) findViewById(R.id.change3);
        change3.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 
        		spinner3.setVisibility(View.VISIBLE);
        		spinner3.performClick();
        		change3.setText(R.string.change3);
       	 }
        });
        textspin3.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 
        		spinner3.setVisibility(View.VISIBLE);
        		spinner3.performClick();
        		change3.setText(R.string.change3);
       	 }
        });
        change4 = (Button) findViewById(R.id.change4);
        change4.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 
        		spinner4.performClick();
        		spinner4.setVisibility(View.VISIBLE);
       	 }
        });
        textspin4.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 
        		spinner4.performClick();
        		spinner4.setVisibility(View.VISIBLE);
       	 }
        });
        change5 = (Button) findViewById(R.id.change5);
        change5.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 

        		spinner5.performClick();
        		spinner5.setVisibility(View.VISIBLE);
       	 }
        });
        tvradius.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		searchView.clearFocus(); 

        		spinner5.performClick();
        		spinner5.setVisibility(View.VISIBLE);
       	 }
        });
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        		if(ready){ 
        			spinner1.setVisibility(View.VISIBLE);
        			change1.setText(R.string.change1);
        		};
        		if(indexSpecialities[arg2]!=selections[0]){
        			setSpinner2(arg2);
            		selections[0]=indexSpecialities[arg2];  
            		selections[1]=0;
            		spinner2.setSelection(0);
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        
        spinner3 = (MultiSelectionSpinner) findViewById(R.id.multSpinner3);
        indexTypes=new int[1];
        indexTypes[0]=0;
        spinner3.setItems(types,new ArrayList<String>(),indexTypes,true,true);
        
        /*Spinner spinner3 = (Spinner) findViewById(R.id.spin3);
        
        spinnerArrayAdapter3 = new CustomAdapterSpinner(this, types);
        spinnerArrayAdapter3.setDropDownViewResource(R.layout.spinner_item);
        spinner3.setAdapter(spinnerArrayAdapter3);*/
       
       /* ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                this, R.array.sizes, R.layout.spinner_layout);
        adapter3.setDropDownViewResource(R.layout.spinner_item);
        spinner3.setAdapter(adapter3);*/
       // spinner3.setOnItemSelectedListener(new NormalOnItemSelectedListener(2));
     spinner3.setSelection(0);
        spinner4 = (Spinner) findViewById(R.id.spin4);
        //ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
          //      this, R.array.areas, R.layout.spinner_layout);
        spinnerArrayAdapter4 = new CustomAdapterSpinner(this, areas);

        spinnerArrayAdapter4.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        spinner4.setAdapter(spinnerArrayAdapter4);
        spinner4.setOnItemSelectedListener(new AreaOnItemSelectedListener());
        spinner5 = (Spinner) findViewById(R.id.spin5);
        spinnerArrayAdapter5 = new CustomAdapterSpinner(this, dataCity);
        spinnerArrayAdapter5.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        spinner5.setAdapter(spinnerArrayAdapter5);
        spinner5.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!presssed5 &&selections[3]==0){
					presssed5=true;
					Intent inten = new Intent(SearchActivity.this, AutoCompleteActivity.class);
					inten.putStringArrayListExtra(Constants.CITIES, allCities.get(1));
					startActivityForResult(inten, 1);
					return true;
				}
				else return false;
			}
		});
           spinner5.setOnItemSelectedListener(new NormalOnItemSelectedListener(4));
          /* spinner5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent inten = new Intent(KarovelayActivity.this, AutoCompleteActivity.class);
				inten.putStringArrayListExtra(Constants.CITY, allCities.get(2));
				startActivity(inten);
				
			}
		});*/
        
        // Define a listener that responds to location updates
        locationListener1 = new locLis2();
        locationListener2 = new locLis2();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        	showNoticeDialog();  
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
    	        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==PackageManager.PERMISSION_GRANTED &&
    	        ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) ==PackageManager.PERMISSION_GRANTED) {
    	   if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener2);          
    	   if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener1);
    	   calcXY();
       }
       else{
    	   ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
       }
       
        
     
     Button calc = (Button) findViewById(R.id.calculate);
     calc.setTypeface(tf);
     calc.setOnClickListener(new View.OnClickListener() {
    	 public void onClick(View arg0) {	 
    		 EasyTracker easyTracker = EasyTracker.getInstance(SearchActivity.this);
    		 easyTracker.send(MapBuilder
    		      .createEvent("ui_action",     // Event category (required)
    		                   "button_press",  // Event action (required)
    		                   "mainSearch",   // Event label
    		                   null)            // Event value
    		      .build()
    		  );	
    		 calcAction();
    	 }
     });
     String[] str1={Constants.baseUrl+version+Constants.urlCommandTitles,Constants.USERCODE,usercode,Constants.USERID,userid};
     GenericDownload downloadFile = new GenericDownload(1,false);
      downloadFile.execute(str1);
    }
    private boolean calcXY(){
    	myx=32.078211;
	    myy=34.830093;
	   
		  Location net_loc=null, gps_loc=null;
		  gps_loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		  net_loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		  if(gps_loc!=null && net_loc!=null){
			  if(gps_loc.getTime()>net_loc.getTime()){   	
				  myx=gps_loc.getLatitude();
				  myy=gps_loc.getLongitude();
			  }
			  else{
				  myx=net_loc.getLatitude();
				  myy=net_loc.getLongitude();
			  }
		  }
		  else{
			  if(gps_loc!=null){
				  myx=gps_loc.getLatitude();
				  myy=gps_loc.getLongitude();
			  }
			  if(net_loc!=null){
				  myx=net_loc.getLatitude();
				  myy=net_loc.getLongitude();
			  }
		  }
		  return ((gps_loc!=null) || (net_loc!=null));
	  }
	
    protected void calcAction(){
    	String msg=searchView.getQuery().toString();
		 //searchView.setQuery("", false);
		 /*if(msg!=null && msg.length()>0 && selections[3]==-1 && selections[4]==15000 && selections[0]==0){
			 selections[3]=0;
			 selections[4]=0;
		 }*/
//    	Intent inten = new Intent(KarovelayActivity.this, ShowAll.class);
    	Intent inten = new Intent(SearchActivity.this, ResultsListActivity.class);

		 inten.putExtra(Constants.VAR,selections);
		 inten.putExtra(Constants.ROLE, spinner2.getSelectedIndAsString());
		 inten.putExtra(Constants.SIZE, spinner3.getSelectedIndAsString());
		 inten.putExtra(Constants.TYPE,Constants.SEARCH);
		 inten.putExtra(Constants.X,myx);
		 inten.putExtra(Constants.Y,myy);
		 inten.putExtra(Constants.MSG, msg);
		 boolean change1bool=false;
		 if(selections[3]!=-1) change1bool=true;// || selections[4]!=15000;
		 boolean change2bool=!(spinner3.getSelectedIndAsString().equalsIgnoreCase("0"));
		 boolean change3bool=false;
		 if(selections[0]!=0) change3bool=true;
		 if((change1bool&&change2bool) || (change1bool&&change3bool) || (change3bool&&change2bool) || (msg!=null && msg.length()>0)){
			 //Set<String> set = settings.getStringSet(Constants.LAST_SEARCHES, new HashSet<String>());
			 //ArrayList<String> array = new ArrayList<String>(set);

			 String str1=selections[0]+";"+spinner2.getSelectedIndAsString()+";"+spinner3.getSelectedIndAsString()+";"+selections[3]+";"+selections[4]+";"+addressString+";"+msg;
			 //array.add(0,str1);
			 //set.clear();
			 //set.addAll(array);			 
			 int ind=0;
			 boolean alreadyexist=false;
			 String savedjob=settings.getString(Constants.LASTSEARCHES + ind, null);
			 while(savedjob!=null && !alreadyexist){
				 if(savedjob.equalsIgnoreCase(str1)) alreadyexist=true;
				 ind++;
				 savedjob=settings.getString(Constants.LASTSEARCHES + ind, null);
			 }
			 if(!alreadyexist){
				 ind =ind%Constants.MAX_LAST_SEARCHES;
				 SharedPreferences.Editor editor = settings.edit();
				 editor.putString(Constants.LASTSEARCHES+ind, str1);
				 editor.commit();
			 }
		 }
		 //Toast.makeText(KarovelayActivity.this,"x="+myx+" y="+myy, Toast.LENGTH_LONG).show();
		 startActivity(inten);
    }
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  //if( Session.getActiveSession()!=null) Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  if (requestCode == 1) {
		  presssed5=false;
	     if(resultCode == RESULT_OK){      
	         String result=data.getStringExtra(Constants.CITIES);      
	         spinner5.setSelection(dataCity.lastIndexOf(result));
	         selections[4]=cityMap.get(result);
	     }
	     if (resultCode == RESULT_CANCELED) {    
	     }
	  }
	  else if (requestCode == 2) {
		     if(resultCode == RESULT_OK){  
		    	 updateAddressByGPS=false;
		    	 myx= data.getDoubleExtra(Constants.X,32.078211);
		    	 myy= data.getDoubleExtra(Constants.Y,32.078211);
				  //Toast.makeText(KarovelayActivity.this,"x="+myx+" y="+myy, Toast.LENGTH_LONG).show();

		    	 addressString=data.getStringExtra(Constants.ADDRESS);	
		    	 if(addressString.length()>0) changeAddress.setText(addressString+" "+getString(R.string.presstochange));
		    	 else {
		    		 addressString="";
		    		 changeAddress.setText(getResources().getString(R.string.unknownlocation)+" "+myx+" "+myy);
		 			Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
		 			try {
		 				List<Address> addresses = geocoder.getFromLocation(myx,myy, 1);
		 				addressString = addresses.get(0).getAddressLine(0);
		 				String city = addresses.get(0).getAddressLine(1);
		 				if(addressString.length()>0) changeAddress.setText(addressString+" "+city+" "+getString(R.string.presstochange));
		 			} catch (Exception e) {
		 				e.printStackTrace();
		 			}
		    	 }

		     }
		     if (resultCode == RESULT_CANCELED) {    
		     }
		     if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+myx+" "+myy);
 			
		  }
	}
  private void openMapSelect(){
	  Intent intent2 = new Intent(this, MapSelectActivity.class);
	  
	  intent2.putExtra(Constants.MYX, myx);
	  intent2.putExtra(Constants.MYY, myy);
	  startActivityForResult(intent2, 2);
  }
    
private class AreaOnItemSelectedListener implements OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
    	
  	  if(ready){ 
  		  spinner4.setVisibility(View.VISIBLE);
  		  change4.setText(R.string.change4);
  	  }
      if(selections[3]!=indexAreas[pos]) setSpinner5(pos);
      
      selections[3]=indexAreas[pos];
     
    }

    public void onNothingSelected(AdapterView parent) {
      // Do nothing.
    }
}

private class NormalOnItemSelectedListener implements OnItemSelectedListener {
	private int _id;
	public NormalOnItemSelectedListener(int index){
		_id=index;
	}
	public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
		if(_id==4){  
			if(ready){ 
				spinner5.setVisibility(View.VISIBLE);
				change5.setText(R.string.change);
			}
			if (selections[3]>-1){//!=-1){
				//String[] allcities =getResources().getStringArray(R.array.city1);
				//int index=0;
				//for(;index<allCities.get(2).size() && !allCities.get(2).get(index).equals(dataCity.get(pos));index++);
				String CityName=dataCity.get(pos);
				int index=cityMap.get(CityName);
				selections[4]=index;
			}
			else { 
				selections[4]=radiuses[pos];
			}
		}
		else      selections[_id]=pos;
	}

    public void onNothingSelected(AdapterView parent) {
      parent.setSelection(0);
    }



}


private class CustomAdapterSpinner extends ArrayAdapter<String>{
    public CustomAdapterSpinner(Context context,ArrayList<String> data) {
        //super(context, android.R.layout.simple_spinner_item, objects);
        super(context, R.layout.spinner_layout, data);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	View v = super.getView(position, convertView, parent);
    	return v;
    }
    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
    	View v =super.getDropDownView(position, convertView, parent);
    	return v;
    }
}
private void setSpinner5(int spinner4pos){
	//if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+" set selected ind" +spinner4pos);
	dataCity.clear();   
    for (int i=0;i<allCities.get(spinner4pos).size();i++) {
    	dataCity.add(allCities.get(spinner4pos).get(i));
    }
    spinnerArrayAdapter5.notifyDataSetChanged();
    spinner5.setSelection(0);
    if (spinner4pos==0) {
  	  tvradius.setText(R.string.selectradius);
  	  selections[4]=radiuses[0];        
    }
    else {
  	  tvradius.setText(R.string.selectcity);
  	  selections[4]=0;
    }
   
}
private void setSpinner2(int spinner1pos){
	roles.clear();
	//settings.getString(Constants.VAR+Integer.toString(arg2),"[]");
    ArrayList<String> tmp=  allRoles.get(spinner1pos);
    for (String item : tmp) {
		roles.add(item);
	}
    ArrayList<Integer> tmp2=  allIndexRoles.get(spinner1pos);
    indexRoles=new int[tmp2.size()];
    for(int i=0;i<tmp2.size();i++){
    	indexRoles[i]=tmp2.get(i);
	}
    
    spinner2.setItems(roles,new ArrayList<String>(),indexRoles,true,true);
    //spinnerArrayAdapter2.notifyDataSetChanged();        
    
}
private void cantconnect(int title){
	AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
  	builder.setTitle(title);
//  	builder.setMessage(mail);
  	builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
               dialog.cancel();
          }     
      });
  	builder.setPositiveButton(R.string.tryagain, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          	dialog.cancel();
          	String[] str1={Constants.baseUrl+version+Constants.urlCommandTitles,Constants.USERCODE,usercode,Constants.USERID,userid};
            GenericDownload downloadFile = new GenericDownload(1,false);
             downloadFile.execute(str1);
          }});
          builder.show();
}
protected void taskResponse(JSONObject json,int responseMode) {
	if(responseMode==1){
		try{
	        String allString=getResources().getString(R.string.all);
			SharedPreferences.Editor editor = settings.edit();	
			
			JSONArray jsonArr= json.getJSONArray(Constants.TYPE);
			indexTypes=new int[jsonArr.length()];
			types.clear();
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject innerJObject = jsonArr.getJSONObject(i);
				types.add(innerJObject.getString(Constants.NAME));
	            indexTypes[i]=innerJObject.getInt(Constants.ID);
				
			}	
			spinner3.setItems(types,new ArrayList<String>(),indexTypes,true,true);
			editor.putString(Constants.TYPE, jsonArr.toString());

			jsonArr= json.getJSONArray(Constants.ROLE);
			indexSpecialities=new int[jsonArr.length()+1];
			indexSpecialities[0]=0;
			//specialities.clear();
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject innerJObject = jsonArr.getJSONObject(i);
				specialities.add(innerJObject.getString(Constants.NAME));
				indexSpecialities[i+1]=innerJObject.getInt(Constants.ID);
				JSONArray roleJson=innerJObject.getJSONArray(Constants.ROWS);
				ArrayList<String> inner =new ArrayList<String>();
				ArrayList<Integer> innerIndex =new ArrayList<Integer>();
				for (int j = 0; j < roleJson.length(); j++) {
					JSONArray ind= roleJson.getJSONArray(j);
					if(addnumbers) inner.add(ind.getString(1)+" ("+ind.getString(2)+")");
					else inner.add(ind.getString(1));
					innerIndex.add(ind.getInt(0));	
				}
				editor.putString(Constants.ROLE+Integer.toString(i), roleJson.toString());
				allRoles.add(inner);
				allIndexRoles.add(innerIndex);

			}	
			
			spinnerArrayAdapter1.notifyDataSetChanged();

			editor.putString(Constants.SPECIALITY, jsonArr.toString());
			
			jsonArr= json.getJSONArray(Constants.AREA);
			indexAreas=new int[jsonArr.length()+1];
			indexAreas[0]=-1;
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject innerJObject = jsonArr.getJSONObject(i);
				areas.add(innerJObject.getString(Constants.NAME));
				indexAreas[i+1]=innerJObject.getInt(Constants.ID);
				JSONArray roleJson=innerJObject.getJSONArray(Constants.ROWS);
				ArrayList<String> inner =new ArrayList<String>();
				ArrayList<Integer> innerIndex =new ArrayList<Integer>();
				inner.add(allString);
				innerIndex.add(new Integer(0));
				for (int j = 0; j < roleJson.length(); j++) {
					JSONArray ind= roleJson.getJSONArray(j);
					//if(addnumbers) inner.add(ind.getString(1)+" ("+ind.getString(2)+")"); else 
						inner.add(ind.getString(1));
					innerIndex.add(ind.getInt(0));	
					Integer value = cityMap.get(ind.getString(1));
					if (value == null) cityMap.put(ind.getString(1),ind.getInt(0));
				}
				editor.putString(Constants.CITIES+Integer.toString(i),  roleJson.toString());
				allCities.add(inner);
				//allIndexRoles.add(innerIndex);

			}	
			
			spinnerArrayAdapter4.notifyDataSetChanged();
			editor.putString(Constants.AREA,  jsonArr.toString());
			editor.commit();
			
			readSaved();
			int spinner1pos=indexOfArray(indexSpecialities,  selections[0]);
			spinner1.setSelection(spinner1pos);
			
			setSpinner2(spinner1pos);
			
			int spinner4pos=indexOfArray(indexAreas,  selections[3]);
			spinner4.setSelection(spinner4pos);
			int index5=0;
			String tmpIndex5="";
			if (selections[3]>-1){
				 tmpIndex5=(String) getKeyFromValue(cityMap, Integer.valueOf(selections[4]));
			 }
			 else {
				 index5=indexOfArray(radiuses,selections[4]);
				 
			 }
			setSpinner5(spinner4pos);
			if (selections[3]>-1){
				 index5=dataCity.lastIndexOf(tmpIndex5);
				 //if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+" set selected5 out"+selections[4]+ " "+getKeyFromValue(cityMap, Integer.valueOf(selections[4])));
			 }
			spinner5.setSelection(index5);
			 //if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+" set selected3xxt"+multiSelected[1]+" "+multiSelected[0]);

			spinner3.setSelection(multiSelected[1]);//index of
			spinner2.setSelection(multiSelected[0]);
			
		}catch(JSONException e)        {
			cantconnect(R.string.netproblem);           	 
		}
		ready=true;
	}
}
@Override
public void onDialogPositiveClick(DialogFragment dialog) {
	str=getResources().getString(R.string.enablegpscommand);
	  Toast.makeText(SearchActivity.this,str, Toast.LENGTH_LONG).show();
	  Intent mintent =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	  startActivity(mintent);
    
}
@Override
public void onDialogNegativeClick(DialogFragment dialog) {
	
}
public void showNoticeDialog() {
	 AlertDialog.Builder builder;
	 builder = new AlertDialog.Builder(this);
	 builder.setTitle(R.string.gpsquestion);
	 builder.setMessage(R.string.operategps);
	
	 builder.setNegativeButton(R.string.no,null);
	 builder.setPositiveButton(R.string.yes,
             new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                	 str=getResources().getString(R.string.enablegpscommand);
               	  Toast.makeText(SearchActivity.this,str, Toast.LENGTH_LONG).show();
               	  Intent mintent =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               	  startActivity(mintent);

                 }
             });
	 builder.show();
	 
    //DialogFragment dialog = new MyDialog().newInstance(R.string.gpsquestion,R.string.operategps);
    //dialog.show(getSupportFragmentManager(), "Gps");
}

public static Object getKeyFromValue(Map hm, Object value) {
    for (Object o : hm.keySet()) {
      if (hm.get(o).equals(value)) {
        return o;
      }
    }
    return null;
  }
/*@TargetApi(Build.VERSION_CODES.HONEYCOMB)
private void setupActionBar() {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
}*/


private class locLis2 implements LocationListener {
	public void onLocationChanged(Location location) {
		if (location != null && updateAddressByGPS && location.getLatitude()!=0 && location.getLongitude()!=0) {
    		if (BuildConfig.DEBUG) Log.i(Constants.TAG,"finding locationx1");
    		
    		new AsyncTask<Location, Location, List<Address>>() {
    		    @Override
    		    protected void onPostExecute(List<Address> addresses) {
    		        super.onPostExecute(addresses);
    		        if(addresses!=null && addresses.size()>0){
    		        	try{
    		        	addressString = addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getAddressLine(1);
    		        	if (BuildConfig.DEBUG) Log.i(Constants.TAG,"finding locationx "+addressString);
    		        	if(addressString.length()>0) changeAddress.setText(addressString+" "+getString(R.string.presstochange));
    		        	else changeAddress.setText(getResources().getString(R.string.unknownlocation));
    		        	}
    		        	catch (Exception e) {
        	 				e.printStackTrace();
        	 				changeAddress.setText(getResources().getString(R.string.unknownlocation));
        	 				addressString="";
        	    		 }
    		        }		  
    		    }
				@Override
				protected List<Address> doInBackground(Location... params) {
					List<Address> addresses=null;
					try{
						Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
    	    			addresses = geocoder.getFromLocation(params[0].getLatitude(), params[0].getLongitude(), 1);
    	    		}
    	    		 catch (Exception e) {
    	 				e.printStackTrace();
    	 				try{
    	 				changeAddress.setText(getResources().getString(R.string.unknownlocation)+" "+ params[0].getLatitude()+" "+params[0].getLongitude());
    	 				} catch (Exception ee) {}
    	 				addressString="";
    	    		 }
					return addresses;
				}
    		}.execute(location);
			myx=location.getLatitude();
			myy=location.getLongitude();
		}
	}

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }
}

}
