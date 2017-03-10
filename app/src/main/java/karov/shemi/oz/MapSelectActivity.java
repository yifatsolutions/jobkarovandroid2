package karov.shemi.oz;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapSelectActivity extends MenuActionActivity implements MyDialog.NoticeDialogListener, OnMapReadyCallback {
	private LatLng latlon;//  
	private LocationListener locationListener1,locationListener2;
	private LocationManager locationManager;
	private GoogleMap map;
	private double xxx,yyy,gpsx,gpsy;
	private Marker mark;
	protected AutoCompleteTextView addressField;
	private SharedPreferences  settings;
	private Set<String> set;
	private PlacesAutoCompleteAdapter adapter2;
	private boolean firsttime;
	public static DecimalFormat fourDForm = new DecimalFormat("#0.0000");
	@Override
	protected void onPause(){
		super.onPause();
		if ( Build.VERSION.SDK_INT < 23 ||(
				ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

			locationManager.removeUpdates(locationListener1);
			locationManager.removeUpdates(locationListener2);
		}
	}
	public void onResume(){
        super.onResume();
		if ( Build.VERSION.SDK_INT < 23 ||(
				ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, locationListener1);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, locationListener2);
		}
    }

	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_map_select);
	    firsttime=true;
	     FragmentManager fragmentManager = getFragmentManager();
	    MapFragment mapFragment = (MapFragment) fragmentManager
	            .findFragmentById(R.id.map2);
	    mapFragment.getMapAsync(this);
	    xxx=getIntent().getDoubleExtra(Constants.MYX,32.078211);
	    yyy=getIntent().getDoubleExtra(Constants.MYY,34.830093);
	    if(xxx==0 && yyy==0){
	    	xxx=32.078211;
	    	yyy=34.830093;
	    }
	    gpsx=getIntent().getDoubleExtra(Constants.MYX,32.078211);
	    gpsy=getIntent().getDoubleExtra(Constants.MYY,34.830093);
	   
	    latlon= new LatLng(Double.valueOf(xxx),Double.valueOf(yyy));
	    Button done = (Button) findViewById(R.id.buttonDone1);
	      done.setOnClickListener(new View.OnClickListener() {
	    	  public void onClick(View arg0) {
	    		  done();
	    		 
	    	  } 
	      });

	      settings = getSharedPreferences(Constants.PREFS_NAME, 0);
	      set = settings.getStringSet(Constants.LAST_ADDRESS, new HashSet<String>());
	      String[] array = set.toArray(new String[set.size()+1]);
	      for (int i = array.length-1; i >0 ; i--) {
			array[i]=array[i-1];
	      }
	      array[0]=getResources().getString(R.string.currentlocation);
	      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item, array);
	      adapter2 =new PlacesAutoCompleteAdapter(this, R.layout.list_item);
	      addressField = (AutoCompleteTextView) findViewById(R.id.addressField);   
	      addressField.setAdapter(adapter);
	      addressField.setOnItemClickListener(new OnItemClickListener() {
	    	  @Override
	    	  public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {		
	    		  String str = (String) adapterView.getItemAtPosition(position);
	    		  if(str==null) str=addressField.getText().toString();
	    		  if(str.equalsIgnoreCase(getResources().getString(R.string.currentlocation))){
	    			  boolean LocFound=calcXY();
	    			  if (!LocFound){
	    				  Toast.makeText(MapSelectActivity.this,R.string.gpsproblem, Toast.LENGTH_LONG).show();
	    			  }
	    			  xxx=gpsx;
	    			  yyy=gpsy;
	    			  latlon= new LatLng(Double.valueOf(xxx),Double.valueOf(yyy));
	    			  mark.setPosition(latlon);    
	    			  map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, 15), 2000, null);
	    		  }
	    		  else{
	    			 
	    			  findAddress(str);  
	    		  }
	    	  }
	      });
	      addressField.addTextChangedListener(new TextWatcher() {
	    	  @Override
	    	  public void onTextChanged(CharSequence s, int start, int before, int count) {}
	    	  @Override
	    	  public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
	    	  @Override
	    	  public void afterTextChanged(Editable s) {
	    		  if(!addressField.getAdapter().equals(adapter2)){
	    			  addressField.setAdapter(adapter2);
	    		  }
	    	  }
	      });
	      addressField.setOnEditorActionListener(
		    		    new EditText.OnEditorActionListener() {
		    		    	@Override
		    		    	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		    		    	    if (actionId == EditorInfo.IME_ACTION_SEARCH ||actionId == EditorInfo.IME_ACTION_DONE ||event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		    		    	    	String str =v.getText().toString();
		    		    	    	findAddress(str); 
		    		    	    	return true; 
		    		    	    }
		    		    	    return false; // pass on to other listeners. 
		    		    	}
		    		    	});
		     addressField.setOnTouchListener(new View.OnTouchListener(){
		    	   @Override
		    	   public boolean onTouch(View v, MotionEvent event){
		    		   addressField.showDropDown();
		    	      return false;
		    	   }});
		     locationListener1 = new locLis();
		        locationListener2 = new locLis();
		        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		        	showNoticeDialog();  
		        }
		  if ( Build.VERSION.SDK_INT < 23 ||(
				  ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						  ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

			  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
			  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
		  }
	  }
	  private void done(){
		  Intent returnIntent = new Intent();
			 returnIntent.putExtra(Constants.X,xxx);
			 returnIntent.putExtra(Constants.Y,yyy);
			 String addresStr=addressField.getText().toString();
			 if(addresStr.equalsIgnoreCase(getResources().getString(R.string.currentlocation))) returnIntent.putExtra(Constants.ADDRESS,"");  
			 else returnIntent.putExtra(Constants.ADDRESS,addressField.getText().toString());
			 setResult(RESULT_OK,returnIntent);     
			 finish();  
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
		                	String str=getResources().getString(R.string.enablegpscommand);
		               	  Toast.makeText(MapSelectActivity.this,str, Toast.LENGTH_LONG).show();
		               	  Intent mintent =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		               	  startActivity(mintent);

		                 }
		             });
			 builder.show();
			 
		    //DialogFragment dialog = new MyDialog().newInstance(R.string.gpsquestion,R.string.operategps);
		    //dialog.show(getSupportFragmentManager(), "Gps");
		}
	  private boolean calcXY(){
		  gpsx=32.078211;
		    gpsy=34.830093;
		   Location net_loc=null, gps_loc=null;
		  if ( Build.VERSION.SDK_INT < 23 ||(
				  ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						  ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

			  gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			  net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		  }
		  if(gps_loc!=null && net_loc!=null){
			  if(gps_loc.getTime()>net_loc.getTime()){   	
				  gpsx=gps_loc.getLatitude();
				  gpsy=gps_loc.getLongitude();
			  }
			  else{
				  gpsx=net_loc.getLatitude();
				  gpsy=net_loc.getLongitude();
			  }
		  }
		  else{
			  if(gps_loc!=null){
				  gpsx=gps_loc.getLatitude();
				  gpsy=gps_loc.getLongitude();
			  }
			  if(net_loc!=null){
				  gpsx=net_loc.getLatitude();
				  gpsy=net_loc.getLongitude();
			  }
		  }
		  return ((gps_loc!=null) || (net_loc!=null));
	  }
	  protected void findAddress(String address){
		  if(!set.contains(address)) {
			  set.add(address);
			  SharedPreferences.Editor editor = settings.edit();					
			  editor.putStringSet(Constants.LAST_ADDRESS, set);
			  editor.commit();
		  }
		  ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(R.string.loading));
		  if(mProgressDialog!=null) mProgressDialog.show();
		  Geocoder gd = new Geocoder(this);
		  
		  try{
			  
			 List<Address> addresses=gd.getFromLocationName(address, 1);
		  xxx= addresses.get(0).getLatitude();
		  yyy=addresses.get(0).getLongitude();
		  latlon= new LatLng(Double.valueOf(xxx),Double.valueOf(yyy));
		  mark.setPosition(latlon);
		  
		  map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, 15), 2000, null);
		  addressField.clearFocus();
		  InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(addressField.getWindowToken(), 0);
		  mProgressDialog.dismiss();
		  }
		  catch (Exception e){
			  if(mProgressDialog!=null) mProgressDialog.dismiss();
			  Toast.makeText(this,R.string.noresults, Toast.LENGTH_LONG).show();
		  }
	  }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.map_select_actions, menu);
	    return true;// super.onCreateOptionsMenu(menu);
	  }
	  @Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case R.id.item_done:
		    	done();
		    	return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		    }
		}

	  private void showMyDialog(Marker marker){
		 
			AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapSelectActivity.this);
	        builderSingle.setTitle(R.string.mapselect);
	        builderSingle.setMessage(R.string.mapselectinstructions);
	        builderSingle.setNegativeButton(R.string.cancel,
	                new DialogInterface.OnClickListener() {

	                    @Override
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.dismiss();
	                    }
	                });

	   
	        builderSingle.show();
		}
	 
		
	  private ArrayList<String> autocomplete(String input) {
		    ArrayList<String> resultList = null;

		    HttpURLConnection conn = null;
		    StringBuilder jsonResults = new StringBuilder();
		    try {
		        StringBuilder sb = new StringBuilder(Constants.PLACES_API_BASE );
		        sb.append("?sensor=false&rankby=distance&key=" + Constants.API_KEY_DIRECTIONS);
		        sb.append("&components=country:il");
		        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

		        URL url = new URL(sb.toString());
		        conn = (HttpURLConnection) url.openConnection();
		        InputStreamReader in = new InputStreamReader(conn.getInputStream());

		        // Load the results into a StringBuilder
		        int read;
		        char[] buff = new char[1024];
		        while ((read = in.read(buff)) != -1) {
		            jsonResults.append(buff, 0, read);
		        }
		    } catch (MalformedURLException e) {
		        Log.e(Constants.TAG, "Error processing Places API URL", e);
		        return resultList;
		    } catch (IOException e) {
		        Log.e(Constants.TAG, "Error connecting to Places API", e);
		        return resultList;
		    } finally {
		        if (conn != null) {
		            conn.disconnect();
		        }
		    }

		    try {
		        // Create a JSON object hierarchy from the results
		        JSONObject jsonObj = new JSONObject(jsonResults.toString());
		        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

		        // Extract the Place descriptions from the results
		        resultList = new ArrayList<String>(predsJsonArray.length());
		        for (int i = 0; i < predsJsonArray.length(); i++) {
		            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
		        }
		    } catch (JSONException e) {
		        Log.e(Constants.TAG, "Cannot process JSON results", e);
		    }

		    return resultList;
		}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		map = googleMap;
		mark = map.addMarker(new MarkerOptions().position(latlon).title("my loc"));
		//.icon(BitmapDescriptorFactory//  .fromResource(R.drawable.pin2)));
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng newloc) {
				//map.clear();
				latlon=newloc;
				xxx=newloc.latitude;
				yyy=newloc.longitude;
				mark.setPosition(newloc);
				//Marker mark = map.addMarker(new MarkerOptions().position(newloc).title(getString(R.string.mapselect)));
				//map.moveCamera(CameraUpdateFactory.newLatLngZoom(newloc, 15));
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(newloc, 15));

			}
		});

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				showMyDialog(arg0);
				return true;
			}
		});
		map.setTrafficEnabled(true);

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlon, 15), 2000, null);
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				if(firsttime) addressField.showDropDown();
				firsttime=false;
			}
		});
	}


	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		    private ArrayList<String> resultList;

		    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		        super(context, textViewResourceId);
		    }

		    @Override
		    public int getCount() {
		    	if(resultList!=null) return resultList.size();
		    	else return 0;
		    }

		    @Override
		    public String getItem(int index) {
		    	if(resultList!=null) return resultList.get(index);
		    	else return null;
		    }

		    @Override
		    public Filter getFilter() {
		        Filter filter = new Filter() {
		            @Override
		            protected FilterResults performFiltering(CharSequence constraint) {
		                FilterResults filterResults = new FilterResults();
		                if (constraint != null) {
		                    // Retrieve the autocomplete results.
		                    resultList = autocomplete(constraint.toString());

		                    // Assign the data to the FilterResults
		                    filterResults.values = resultList;
		                    filterResults.count = resultList.size();
		                }
		                return filterResults;
		            }

		            @Override
		            protected void publishResults(CharSequence constraint, FilterResults results) {
		                if (results != null && results.count > 0) {
		                    notifyDataSetChanged();
		                }
		                else {
		                    notifyDataSetInvalidated();
		                }
		            }};
		        return filter;
		    }
		}


	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		String str=getResources().getString(R.string.enablegpscommand);
		  Toast.makeText(this,str, Toast.LENGTH_LONG).show();
		  Intent mintent =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		  startActivity(mintent);
		
	}
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}
}
