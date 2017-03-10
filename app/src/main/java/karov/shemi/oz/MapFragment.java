package karov.shemi.oz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

	private MapView mMapView;
	private GoogleMap googleMap;
	private LatLng latlon;//
	private int[] inSameLoc;
	private ArrayList<Integer> indexArray;
	private  LatLngBounds.Builder builder;
	private ArrayList<HashMap<String, String>> mylist; 

	 public void setList(ArrayList<HashMap<String, String>> models,Context context){
		 mylist = models;
		 if(mylist!=null && mylist.size()>0 && googleMap!=null) {
			 setupMap();
		 }
	 }

	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		 LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		 View v = inflater.inflate(R.layout.fragment_location_info, container,false);
		 mMapView = (MapView) v.findViewById(R.id.mapView);
		 mMapView.onCreate(savedInstanceState);
		 mMapView.onResume();// needed to get the map to display immediately
		 try {
			 MapsInitializer.initialize(getActivity().getApplicationContext());
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 mMapView.getMapAsync(this);
		 if(mylist!=null && mylist.size()>0 && googleMap!=null){
			 setupMap();
		 }
	   
		 return v;
	 }

	 @Override
	 public void onResume() {
		 super.onResume();
		 mMapView.onResume();
	 }

	 @Override
	 public void onPause() {
		 super.onPause();
		 mMapView.onPause();
	 }

	 @Override
	 public void onDestroy() {
		 super.onDestroy();
		 if(mMapView!=null) mMapView.onDestroy();
	 }

	 @Override
	 public void onLowMemory() {
		 super.onLowMemory();
		 mMapView.onLowMemory();
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
	 private void setupMap(){
		 inSameLoc=new int[mylist.size()];
		 Arrays.fill(inSameLoc, 1);
		 for (int i = 0; i < mylist.size(); i++) {
			 HashMap<String, String> element=mylist.get(i);
			 double externalx=Double.parseDouble(element.get(Constants.X));
			 double externaly=Double.parseDouble(element.get(Constants.Y));
			 if(inSameLoc[i] ==1){
				 for (int j = i+1; j < mylist.size(); j++) {
					 HashMap<String, String> element2=mylist.get(j);
					 double internalx=Double.parseDouble(element2.get(Constants.X));
					 double internaly=Double.parseDouble(element2.get(Constants.Y));
					 if(Math.abs(Double.valueOf(internalx)-Double.valueOf(externalx))<0.0005 && Math.abs(Double.valueOf(internaly)-Double.valueOf(externaly))<0.0005){
						 inSameLoc[i]++;
						 inSameLoc[j]=0;
					 }
				 }
			 }
		 }
		 googleMap.setTrafficEnabled(true);
		 builder = new LatLngBounds.Builder();
		 //googleMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		 latlon= new LatLng(32.078211,34.830093);
		 for (int i = 0; i < mylist.size(); i++) {
			 HashMap<String, String> element=mylist.get(i);	
			 if(element.get(Constants.ADDRESS).length()>1 && inSameLoc[i]>0){
				 double externalx=Double.parseDouble(element.get(Constants.X));
				 double externaly=Double.parseDouble(element.get(Constants.Y));
				 String id=element.get(Constants.ID);
				 String company=element.get(Constants.COMPANY);
				 String name=element.get(Constants.NAME);
				 String address=element.get(Constants.ADDRESS);
				 latlon= new LatLng(externalx,externaly);
				 if(inSameLoc[i] ==1){ 
					 Marker mark=googleMap.addMarker(new MarkerOptions().position(latlon).title(id).snippet(company+"\n"+name+"\n"+address).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
					 builder.include(mark.getPosition());
				 }
				 else{
					 Marker mark=googleMap.addMarker(new MarkerOptions().position(latlon).title(id).snippet(company+"\n"+name+"\n"+address).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinmany)));
					 builder.include(mark.getPosition());
				 }
			 }
		 }
		 googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			 @Override
			 public boolean onMarkerClick(Marker arg0) {
				 showMyDialog(arg0);
				 return true;
			 }
		 });
		 googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlon, 15));
		 googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {	
			 @Override
			 public void onInfoWindowClick(Marker marker) {
				 Intent inten = new Intent(getActivity(),JobDetailsActivity.class);
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
	    	
		 //if(addresses.length>1) 
		 googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			 @Override
			 public void onCameraChange(CameraPosition arg0) {
				 if(mylist.size()>0){				 
					 LatLngBounds bounds = builder.build();
					 int padding = 60; // offset from edges of the map in pixels
					 CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);	
					 googleMap.moveCamera(cu);
					 if(googleMap.getCameraPosition().zoom>15) {
						 CameraUpdate cu2 = CameraUpdateFactory.zoomTo(15);
						 googleMap.animateCamera(cu2);		
					 }
					 // Remove listener to prevent position reset on camera move.
					 googleMap.setOnCameraChangeListener(null);
				 }
			 }
		 });
	 }
	 private void showMyDialog(Marker marker){
		 ArrayList<String> stringArray =new ArrayList<String>();
		 ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		 indexArray =new ArrayList<Integer>();
		 AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());//, R.style.CustomActionBarThemeAlertView);
		 //builderSingle.setIcon(R.drawable.icon);
		 //String[] splited= marker.getSnippet().split("\n");  
		 ImageAdapterSimple arrayAdapter = new ImageAdapterSimple(getActivity(), data);//,R.layout.linemap, new String[] {Constants.COMPANY, Constants.NAME }, new int[] {R.id.text1, R.id.text2 });
		 //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapActivity.this,android.R.layout.select_dialog_item);
		 builderSingle.setAdapter(arrayAdapter,null);
		 for (int i = 0; i < mylist.size(); i++) {
			 HashMap<String, String> element=mylist.get(i);
			 double externalx=Double.parseDouble(element.get(Constants.X));
			 double externaly=Double.parseDouble(element.get(Constants.Y));
			 String id=element.get(Constants.ID);
			 String company=element.get(Constants.COMPANY);
			 String name=element.get(Constants.NAME);
			 String address=element.get(Constants.ADDRESS);
			 String photo=element.get(Constants.PHOTO);
			 if(address.length()>1){
				 double lat=marker.getPosition().latitude;
				 double lon=marker.getPosition().longitude;
				 if(Math.abs(lat-externalx)<0.0005 && Math.abs(lon-externaly)<0.0005 && inSameLoc[i]>-1){
					 Map<String, String> datum = new HashMap<String, String>(2);
					 datum.put(Constants.COMPANY, company);
					 datum.put(Constants.NAME,name);
					 datum.put(Constants.PHOTO,photo);
					 datum.put(Constants.ADDRESS,address);
					 data.add(datum);
					 stringArray.add(company+"\n"+name);
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
				 Intent inten = new Intent(getActivity(),JobDetailsActivity.class);
				 HashMap<String, String> element=mylist.get(absolutePos);
				 double externalx=Double.parseDouble(element.get(Constants.X));
				 double externaly=Double.parseDouble(element.get(Constants.Y));
				 String index=element.get(Constants.ID);
				 String company=element.get(Constants.COMPANY);
				 String name=element.get(Constants.NAME);
				 String address=element.get(Constants.ADDRESS);
				 String photo=element.get(Constants.PHOTO);
				 inten.putExtra(Constants.COMPANY, company);
				 inten.putExtra(Constants.NAME,name);
				 inten.putExtra(Constants.ADDRESS,address);
				 inten.putExtra(Constants.PHOTO,photo);
				 inten.putExtra(Constants.ID,Integer.valueOf(index));
				 inten.putExtra(Constants.X,externalx);
				 inten.putExtra(Constants.Y,externaly);
				 inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 startActivity(inten);
			 }
		 });
		 dialog.show();
		 //builderSingle.show();       
	 }

	@Override
	public void onMapReady(GoogleMap googleMap2) {
		googleMap=googleMap2;
		if(mylist!=null && mylist.size()>0 && googleMap!=null) {
			setupMap();
		}
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
	  for (int i = 0; i < mylist.size(); i++) {
		  HashMap<String, String> element=mylist.get(i);
		  double externalx=Double.parseDouble(element.get(Constants.X));
		  double externaly=Double.parseDouble(element.get(Constants.Y));
		  String id=element.get(Constants.ID);
		  String company=element.get(Constants.COMPANY);
		  String name=element.get(Constants.NAME);
		  String address=element.get(Constants.ADDRESS);
		  String photo=element.get(Constants.PHOTO);
		  if(address.length()>1){
				   stringArray.add(company+"\n"+name+"\n"+address);
		  }
	  }
	  ListView modeList = new ListView(getActivity());
	  ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
	  modeList.setAdapter(modeAdapter);
	  modeList.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));  
	  TextView info = new TextView(getActivity()); 
	  info.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)); 
	  info.setText(marker.getSnippet()); 
	  info.setTextColor(getResources().getColor(android.R.color.black));
	  return modeList; 
  }
  }
}