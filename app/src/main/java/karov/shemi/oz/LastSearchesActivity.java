package karov.shemi.oz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class LastSearchesActivity extends MenuActionActivity{
	private ListView lv1;
	private TextView tv1;
	private SharedPreferences settings;
	private BaseAdapter adapter;
	private ArrayList<HashMap<String, String>> mylist; 

	

	
	 public void updateCounter(){
		 String str2= Integer.toString(mylist.size());
		 tv1.setText(str2);//+" "+str1+" ");
	 }
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_searches);
        mylist=new ArrayList<HashMap<String, String>>();
        lv1=(ListView)findViewById(R.id.listsmartagent3);
		registerForContextMenu(lv1);
		tv1 =(TextView) findViewById(R.id.smartagentTv3);
		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        boolean heb =settings.getBoolean(Constants.HEBREW, true);
        if (heb) {
        	tv1.setGravity(Gravity.LEFT);
        }
        fillList();		
	}
	private void fillList(){
		try{
			String all=getString(R.string.all);
			String stringSpecialities= settings.getString(Constants.SPECIALITY, "");
			JSONArray ja2 =new JSONArray(stringSpecialities);
			String[] specialities =new String[ja2.length()];
			int[] indexSpecialities =new int[specialities.length];				
			for (int i = 0; i < ja2.length(); i++) {
				JSONObject ind= ja2.getJSONObject(i);
				specialities[i]=ind.getString(Constants.NAME);
				indexSpecialities[i]=ind.getInt(Constants.ID);			
			}

			String stringAreas= settings.getString(Constants.AREA, "");
			JSONArray ja3 =new JSONArray(stringAreas);
			String[] areas =new String[ja3.length()+1];
			int[] indexAreas =new int[areas.length];
			areas[0]=getString(R.string.aroundaddress);
			indexAreas[0]=-1;
			for (int i = 0; i < ja3.length(); i++) {
				JSONObject ind= ja3.getJSONObject(i);
				areas[i+1]=ind.getString(Constants.NAME);
				indexAreas[i+1]=ind.getInt(Constants.ID);			
			}
  		
			String stringCitys= settings.getString(Constants.CITIES+"1", "");
			JSONArray ja5 =new JSONArray(stringCitys);
			String[] cities =new String[ja5.length()];
			int[] indexCities =new int[cities.length];
			for (int i = 0; i < ja5.length(); i++) {
				JSONArray ind= ja5.getJSONArray(i);
				cities[i]=ind.getString(1);
				indexCities[i]=ind.getInt(0);			
			}
			//Set<String> set = settings.getStringSet(Constants.LAST_SEARCHES, new HashSet<String>());
			ArrayList<String> array = new ArrayList<String>();
			int indarray=0;
			String element=settings.getString(Constants.LASTSEARCHES + indarray, null);
			while(element!=null){
				array.add(0,element);
				indarray++;
				element=settings.getString(Constants.LASTSEARCHES + indarray, null);
			}
			
			//Object[] array =  reverse(set.toArray(new String[set.size()]));
			for (int i = 0; i < array.size(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				String[] elements=((String)array.get(i)).split(";");	    	  
				int job =Integer.valueOf(elements[0]);
	  			int area =Integer.valueOf(elements[3]);
	  			int city =Integer.valueOf(elements[4]);
	  			//int radius =ja.optInt(Constants.RADIUS,0);
	  			String allRoles =elements[1];
	  			String size =elements[2];
	  			//Log.e(Constants.TAG,Constants.TAG+"size ="+size);
	  			String[] rolesStrings=allRoles.split(",");
	  			if(job>0){
	  				int jobpos=indexOfArray(indexSpecialities,  job);
	  				map.put(Constants.JOBNAME,specialities[jobpos]);
	  				String stringRoles= settings.getString(Constants.ROLE+Integer.toString(jobpos), "");	
	  				JSONArray ja4 =new JSONArray(stringRoles);
	  				String[] roles =new String[ja4.length()];
	  				int[] indexRoles =new int[roles.length];				
	  				for (int j = 0; j < ja4.length(); j++) {
	  					JSONArray ind= ja4.getJSONArray(j);
	  					roles[j]=ind.getString(1);
	  					indexRoles[j]=ind.getInt(0);			
	  				}
	  				//Log.e(Constants.TAG, "jobpos= "+jobpos+" string roles= "+stringRoles+ "role string = "+rolesStrings[0]+ "indexroles ="+Arrays.toString(indexRoles));
	  				String combined="";
	  				for (int j = 0; j < rolesStrings.length; j++) {		
	  					int rolepos=indexOfArray(indexRoles,  Integer.valueOf(rolesStrings[j]));
	  					if(rolepos>=roles.length) combined=all;
	  					else{
	  						if (combined.length()>0) combined=combined+","+roles[rolepos];
	  						else combined=roles[rolepos];
	  					}
	  				}
	  				map.put(Constants.ROLENAME,combined);
	  			}
	  			else{
	  				map.put(Constants.JOBNAME,all);
	  				map.put(Constants.ROLENAME,all);
	  			}
	  			if(area>-1){
	  				map.put(Constants.CITIES,Integer.toString(city));
	  				int areapos=indexOfArray(indexAreas,  area);
	  				if(city>0) {
	  					int citypos=indexOfArray(indexCities,  city);
	  					map.put(Constants.CITYNAME,cities[citypos]);
	  				}
	  				else map.put(Constants.CITYNAME,areas[areapos]);
	  			}
	  			else{
	  				map.put(Constants.CITIES,Integer.toString(city));
	  				if(elements.length>5) map.put(Constants.CITYNAME,elements[5]);
	  			}
	  			map.put(Constants.SPECIALITY,Integer.toString(job));
	  			map.put(Constants.AREA,Integer.toString(area));
	  			map.put(Constants.ROLE,allRoles);
	  			map.put(Constants.SIZE, size);
	  			map.put(Constants.X,"0.0");
	  			map.put(Constants.Y,"0.0");
	  			if(elements.length>5) map.put(Constants.ADDRESS,elements[5]);
	  			map.put(Constants.MSG,"");	    		  
	  			if(elements.length>6){
	  				map.put(Constants.ROLENAME,map.get(Constants.JOBNAME));
	  				map.put(Constants.JOBNAME,elements[6]);
	//  				map.put(Constants.CITYNAME,"");
	  				map.put(Constants.MSG,elements[6]);
	  			}
	  			mylist.add(map);	 
			}
		}catch(Exception ii) {
			Exception x=ii;
		}
		adapter = new SimpleAdapter(this, mylist,R.layout.list_row,new String[] {Constants.JOBNAME,Constants.ROLENAME,Constants.CITYNAME }, new int[] {R.id.heading,R.id.content,R.id.distance });
		lv1.setAdapter(adapter);
		//adapter = new SimpleAdapter(this, mylist , R.layout.lineagent,new String[] {Constants.NAME,Constants.NAME,Constants.COMPANY}, to3);
		String str2= Integer.toString(mylist.size());
		tv1.setText(str2);//+" "+str1+" ");
		lv1.setTextFilterEnabled(true);
		lv1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> o = (HashMap<String, String>) lv1.getItemAtPosition(position);
				//String ind =o.get(Constants.ID);
				//Toast.makeText(SmartAgent.this, "ID '" +ind  + "' was clicked.", Toast.LENGTH_SHORT).show();				
				Intent inten = new Intent(LastSearchesActivity.this, ResultsListActivity.class);
		    	int[] selections={0,0,0,-2,15000};
		    	selections[0]=Integer.valueOf(o.get(Constants.SPECIALITY));
		    	selections[3]=Integer.valueOf(o.get(Constants.AREA));
		    	selections[4]=Integer.valueOf(o.get(Constants.CITIES));
		    	inten.putExtra(Constants.VAR,selections);
		    	inten.putExtra(Constants.ROLE, o.get(Constants.ROLE));
		    	inten.putExtra(Constants.SIZE, o.get(Constants.SIZE));
//		    	inten.putExtra(Constants.X,o.get(Constants.X));
//		    	inten.putExtra(Constants.Y,o.get(Constants.Y));
		    	inten.putExtra(Constants.TYPE,Constants.SEARCH);
		    	inten.putExtra(Constants.MSG,o.get(Constants.MSG));
		    	startActivity(inten);
			}
		});
	}
	public static Object[] reverse(Object[] arr) {
		List<Object> list = Arrays.asList(arr);
		Collections.reverse(list);
		return list.toArray();
	}
	private void deleteSaved(int position){	
		mylist.remove(position);
		adapter.notifyDataSetChanged();
		String str2= Integer.toString(mylist.size());
		tv1.setText(str2);
	}
}
	
	

