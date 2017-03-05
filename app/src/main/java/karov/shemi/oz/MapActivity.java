package karov.shemi.oz;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends MenuActionActivity implements OnMapReadyCallback {
	  private LatLng latlon;//
	  private GoogleMap map;
	  private String[] xxx,yyy,names,companies,indexes,addresses,pictures,cities;
	  private int[] inSameLoc;
	  private ArrayList<Integer> indexArray;
	  private  LatLngBounds.Builder builder;
	  private String resultCompany,resultCity;
	  private void filterItems(){
		  Intent intent2 = new Intent(this, FilterActivity.class);
		  ArrayList<String> selectedCities=new ArrayList<String>(companies.length);
		  ArrayList<String> selectedCompanies=new ArrayList<String>(companies.length);
		  ArrayList<String> AllCities=new ArrayList<String>(companies.length);
		  ArrayList<String> AllCompanies=new ArrayList<String>(companies.length);
		  for (int i = 0; i < companies.length; i++) {
			  if(!selectedCompanies.contains(companies[i]) && !companies[i].isEmpty()) selectedCompanies.add(companies[i]);
			  if(!selectedCities.contains(cities[i]) && !cities[i].isEmpty()) selectedCities.add(cities[i]);	 
			  AllCities.add(cities[i]);
			  AllCompanies.add(companies[i]);
		  }
		  Collections.sort(selectedCompanies, String.CASE_INSENSITIVE_ORDER);
		  Collections.sort(selectedCities, String.CASE_INSENSITIVE_ORDER);
		  intent2.putExtra(Constants.COMPANY, selectedCompanies);
		  intent2.putExtra(Constants.CITIES, selectedCities);
		  intent2.putExtra(Constants.VAR, AllCities);
		  intent2.putExtra(Constants.RANGE, AllCompanies);
		  intent2.putExtra(Constants.COMPANYNAME, resultCompany);
		  intent2.putExtra(Constants.CITYNAME, resultCity);
		  startActivityForResult(intent2,1); 
	  }
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == 1) {	
			  if(resultCode == RESULT_OK){
				  resultCompany=data.getStringExtra(Constants.COMPANY);
				  resultCity=data.getStringExtra(Constants.CITIES);
				  map.clear();
				  String all=getString(R.string.all);
				  Arrays.fill(inSameLoc, 1);
				  for (int i = 0; i < xxx.length; i++) {	
					  if((resultCompany.contains(companies[i]) || resultCompany.equalsIgnoreCase(all)) && (resultCity.contains(cities[i]) || resultCity.equalsIgnoreCase(all))){
						  if(inSameLoc[i] ==1){
							  for (int j = i+1; j < xxx.length; j++) {
								  if(Math.abs(Double.valueOf(xxx[j])-Double.valueOf(xxx[i]))<0.0005 && Math.abs(Double.valueOf(yyy[j])-Double.valueOf(yyy[i]))<0.0005){
									  inSameLoc[i]++;
									  inSameLoc[j]=0;
								  }
							  }
						  }   
					  }
					  else inSameLoc[i]=-1;
				  }
				  latlon= new LatLng(32.078211,34.830093);
				  for (int i = 0; i < xxx.length; i++) {
					  if(addresses[i].length()>1 && inSameLoc[i]>0){
						  latlon= new LatLng(Double.valueOf(xxx[i]),Double.valueOf(yyy[i]));
						  if(inSameLoc[i] ==1){ 
							  Marker mark=map.addMarker(new MarkerOptions().position(latlon).title(indexes[i]).snippet(companies[i]+"\n"+names[i]+"\n"+addresses[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
							  builder.include(mark.getPosition());
						  }
						  else{
							  Marker mark=map.addMarker(new MarkerOptions().position(latlon).title(indexes[i]).snippet(companies[i]+"\n"+names[i]+"\n"+addresses[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmany)));
							  builder.include(mark.getPosition());
						  }
					  }
				  } 
			  }
			  else if (resultCode == RESULT_CANCELED) {   
			  }
		  }
	  }
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.allresults_activity_menu2, menu);
		  return true;//super.onCreateOptionsMenu(menu);
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
		  default:
			  return super.onOptionsItemSelected(item);
		  }
		  return true;
	  }
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.activity_map);
		  FragmentManager fragmentManager=getFragmentManager();
		  MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapx);
		  mapFragment.getMapAsync(this);
		  xxx=getIntent().getStringArrayExtra(Constants.X);
		  yyy=getIntent().getStringArrayExtra(Constants.Y);
		  names=getIntent().getStringArrayExtra(Constants.NAME);
		  companies=getIntent().getStringArrayExtra(Constants.COMPANY);
		  cities=getIntent().getStringArrayExtra(Constants.CITIES);
		  indexes=getIntent().getStringArrayExtra(Constants.ID);
		  addresses=getIntent().getStringArrayExtra(Constants.ADDRESS);
		  pictures=getIntent().getStringArrayExtra(Constants.PHOTO);
		  inSameLoc=new int[xxx.length];
		  Arrays.fill(inSameLoc, 1);
		  for (int i = 0; i < xxx.length; i++) {
				   if(inSameLoc[i] ==1){
					   for (int j = i+1; j < xxx.length; j++) {
						   if(Math.abs(Double.valueOf(xxx[j])-Double.valueOf(xxx[i]))<0.0005 && Math.abs(Double.valueOf(yyy[j])-Double.valueOf(yyy[i]))<0.0005){
							   inSameLoc[i]++;
							   inSameLoc[j]=0;
						   }
					   }
			   }

		  }

		  Button listButton = (Button) findViewById(R.id.listbutton);
		  listButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					finish();
				} 
			});
		TextView	tv1 =(TextView) findViewById(R.id.numberofjobsmap);
		String str2= Integer.toString(xxx.length);
		tv1.setText(str2);		
	  }
	 
	
	  private void showMyDialog(Marker marker){
		  ArrayList<String> stringArray =new ArrayList<String>();
		  ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		  indexArray =new ArrayList<Integer>();
		  AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);//, R.style.CustomActionBarThemeAlertView);
	      //builderSingle.setIcon(R.drawable.icon);
		  //String[] splited= marker.getSnippet().split("\n");  
		  ImageAdapterSimple arrayAdapter = new ImageAdapterSimple(this, data);//,R.layout.linemap, new String[] {Constants.COMPANY, Constants.NAME }, new int[] {R.id.text1, R.id.text2 });
		  //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapActivity.this,android.R.layout.select_dialog_item);
		  builderSingle.setAdapter(arrayAdapter,null);
		  for (int i = 0; i < xxx.length; i++) {
			  if(addresses[i].length()>1){
				  double lat=marker.getPosition().latitude;
				  double lon=marker.getPosition().longitude;
				  if(Math.abs(lat-Double.valueOf(xxx[i]))<0.0005 && Math.abs(lon-Double.valueOf(yyy[i]))<0.0005 && inSameLoc[i]>-1){
					  Map<String, String> datum = new HashMap<String, String>(2);
					  datum.put(Constants.COMPANY, companies[i]);
					  datum.put(Constants.NAME,names[i]);
					  if (pictures!=null && pictures.length>i)  datum.put(Constants.PHOTO,pictures[i]);
					  datum.put(Constants.ADDRESS,addresses[i]);
					  data.add(datum);
					  stringArray.add(companies[i]+"\n"+names[i]);
					  //arrayAdapter.add(companies[i]+"\n"+names[i]);
					  indexArray.add(Integer.valueOf(i));
				  }	   
			  }
		  } 
		  String markerSnippet=marker.getSnippet();
		  String[] snippetElements=markerSnippet.split("\n");
		  String addressMarker=getResources().getString(R.string.jobsinthislocation);
		  if(snippetElements.length>2)addressMarker=getResources().getString(R.string.jobsin)+snippetElements[2];
		  builderSingle.setTitle(data.size()+ " "+addressMarker);
		  builderSingle.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {}
		  });
		  AlertDialog dialog=builderSingle.create();
		  dialog.getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				int absolutePos=indexArray.get(position);
				  Intent inten = new Intent(MapActivity.this,JobDetailsActivity.class);
				  inten.putExtra(Constants.COMPANY, companies[absolutePos]);
				  inten.putExtra(Constants.NAME,names[absolutePos]);
				  inten.putExtra(Constants.ADDRESS,addresses[absolutePos]);
				  inten.putExtra(Constants.PHOTO,pictures[absolutePos]);
				  inten.putExtra(Constants.ID,Integer.valueOf(indexes[absolutePos]));
				  inten.putExtra(Constants.X,xxx[absolutePos]);
				  inten.putExtra(Constants.Y,yyy[absolutePos]);
				  inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				  startActivity(inten);
				
			}
		});
		  
		  
		  dialog.show();
		  //builderSingle.show();       
	  }

	@Override
	public void onMapReady(GoogleMap map2) {
		map=map2;
		map.setTrafficEnabled(true);
		builder = new LatLngBounds.Builder();
		map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		latlon= new LatLng(32.078211,34.830093);
		for (int i = 0; i < xxx.length; i++) {
			if(addresses[i].length()>1 && inSameLoc[i]>0){
				latlon= new LatLng(Double.valueOf(xxx[i]),Double.valueOf(yyy[i]));
				if(inSameLoc[i] ==1){
					Marker mark=map.addMarker(new MarkerOptions().position(latlon).title(indexes[i]).snippet(companies[i]+"\n"+names[i]+"\n"+addresses[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
					builder.include(mark.getPosition());
				}
				else{
					Marker mark=map.addMarker(new MarkerOptions().position(latlon).title(indexes[i]).snippet(companies[i]+"\n"+names[i]+"\n"+addresses[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmany)));
					builder.include(mark.getPosition());
				}
			}
			Button filter = (Button) findViewById(R.id.filterbutton2);
			filter.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					filterItems();
				}
			});
		}
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				showMyDialog(arg0);
				return true;
			}
		});
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon, 15));
		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				Intent inten = new Intent(MapActivity.this,JobDetailsActivity.class);
				String[] splited= marker.getSnippet().split("\n");
				inten.putExtra(Constants.COMPANY, splited[0]);
				inten.putExtra(Constants.NAME,splited[1]);
				inten.putExtra(Constants.ADDRESS,splited[2]);
				inten.putExtra(Constants.ID,Integer.valueOf(marker.getTitle()));
				inten.putExtra(Constants.X,marker.getPosition().latitude);
				inten.putExtra(Constants.Y,marker.getPosition().longitude);
				startActivity(inten);
			}
		});
		if(addresses.length>1) map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				LatLngBounds bounds = builder.build();
				int padding = 60; // offset from edges of the map in pixels
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				map.moveCamera(cu);
				//Toast.makeText(getApplicationContext(), "zoom="+map.getCameraPosition().zoom, Toast.LENGTH_LONG).show();
				if(map.getCameraPosition().zoom>15) {
					CameraUpdate cu2 = CameraUpdateFactory.zoomTo(15);
					map.animateCamera(cu2);
				}
				// Remove listener to prevent position reset on camera move.
				map.setOnCameraChangeListener(null);
			}
		});

	}


	private class PopupAdapter implements InfoWindowAdapter {
		  LayoutInflater inflater=null;

		  PopupAdapter(LayoutInflater inflater) {
		    this.inflater=inflater;
		  }

		  @Override
		  public View getInfoWindow(Marker marker) {
		    return(null);
		  }
	  @Override 
	  public View getInfoContents(Marker marker) { 
		  ArrayList<String> stringArray =new ArrayList<String>();
		  for (int i = 0; i < xxx.length; i++) {
			  if(addresses[i].length()>1){
					   stringArray.add(companies[i]+"\n"+names[i]+"\n"+addresses[i]);
			  }
		  }
		  ListView modeList = new ListView(MapActivity.this);
		  ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(MapActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
		  modeList.setAdapter(modeAdapter);
		  modeList.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));  
		  TextView info = new TextView(MapActivity.this); 
		  info.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)); 
		  info.setText(marker.getSnippet()); 
		  info.setTextColor(getResources().getColor(android.R.color.black));
		  return modeList; 
	  }
	  }
} 