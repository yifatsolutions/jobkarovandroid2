package karov.shemi.oz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SmartAgent extends MenuActionActivity{
	private ListView lv1;
	private TextView tv1;
	private SharedPreferences settings;
	private BaseAdapter adapter;
	private ArrayList<HashMap<String, String>> mylist; 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		/* case R.id.add_smart_agent:
			 addAgent();
			 break;
		 */
		 default:
			 return super.onOptionsItemSelected(item);
		 }
		// return true;
	}
	 private void addAgent(){
		 Intent inten = new Intent(SmartAgent.this, EditSmartAgent.class);
 		startActivityForResult(inten, 0);
	 }
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main_activity_actions, menu);
	        return true;// super.onCreateOptionsMenu(menu);
	    }
	

	 protected void onRestart(){
		 super.onRestart();
		 mylist.clear();
		 String usercode= settings.getString(Constants.USERCODE, "");
		 String userid= settings.getString(Constants.USERID, "");
		 String myemail= settings.getString(Constants.EMAIL, "");

		 if(usercode.length()>0 && userid.length()>0 && emailValidator(myemail)){
	
	        String[] str1={Constants.baseUrl+version+Constants.urlCommandAllSmartAgent,Constants.USERID,userid,Constants.USERCODE,usercode};
	    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(str1));
	    	DownloadAgents downloadFile = new DownloadAgents();
	    	downloadFile.execute(str1);
		 }
		/* else{
			 Toast.makeText(SmartAgent.this, R.string.pleaseregister, Toast.LENGTH_SHORT).show();				
			 Intent inten = new Intent(this,FirstLoginActivity.class);
			 inten.putExtra(Constants.SECONDTIME, true);
			 startActivity(inten);
			 //finish();
		    	
		 }*/
	 }
	 public void updateCounter(){
		 String str1=getResources().getString(R.string.numagents);
		 String str2= Integer.toString(mylist.size());
		 tv1.setText(str2);//+" "+str1+" ");
	 }
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_agent);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));
        mylist=new ArrayList<HashMap<String, String>>();
        lv1=(ListView)findViewById(R.id.listsmartagent);
		registerForContextMenu(lv1);
		tv1 =(TextView) findViewById(R.id.smartagentTv);
		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        boolean heb =settings.getBoolean(Constants.HEBREW, true);
        if (heb) {
        	tv1.setGravity(Gravity.LEFT);
        }
        fillList();
       Button add = (Button) findViewById(R.id.addagentbutton);
        add.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
        		addAgent();
        	} 
        });
        String usercode= settings.getString(Constants.USERCODE, "");
		String userid= settings.getString(Constants.USERID, "");
		 String myemail= settings.getString(Constants.EMAIL, "");

		if(usercode.length()>0 && userid.length()>0 && emailValidator(myemail)){
			String[] str1={Constants.baseUrl+version+Constants.urlCommandAllSmartAgent,Constants.USERID,userid,Constants.USERCODE,usercode};
			if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(str1));
			DownloadAgents downloadFile = new DownloadAgents();
			downloadFile.execute(str1);
		}
		else{
			Toast.makeText(SmartAgent.this, R.string.pleaseregister, Toast.LENGTH_SHORT).show();
			Intent inten = new Intent(this,SigninActivity.class);
			 inten.putExtra(Constants.SECONDTIME, true);
			 startActivity(inten);
			//finish();
		}
			
	}
	private void fillList(){
		//adapter = new SimpleAdapter(this, mylist , R.layout.lineagent,new String[] {Constants.NAME,Constants.NAME,Constants.COMPANY}, to3);
		adapter = new SmartAgentAdapter(this, mylist);
		
		String str2= Integer.toString(mylist.size());
		tv1.setText(str2);//+" "+str1+" ");
		lv1.setAdapter(adapter);					
		lv1.setTextFilterEnabled(true);
		lv1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> o = (HashMap<String, String>) lv1.getItemAtPosition(position);
				//String ind =o.get(Constants.ID);
				//Toast.makeText(SmartAgent.this, "ID '" +ind  + "' was clicked.", Toast.LENGTH_SHORT).show();				
				Intent inten = new Intent(SmartAgent.this, EditSmartAgent.class);
        		inten.putExtra(Constants.ID,o.get(Constants.ID));
        		inten.putExtra(Constants.SPECIALITY,o.get(Constants.SPECIALITY));
        		inten.putExtra(Constants.AREA,o.get(Constants.AREA));
        		inten.putExtra(Constants.CITIES,o.get(Constants.CITIES));
        		inten.putExtra(Constants.ROLE, o.get(Constants.ROLE));
        		inten.putExtra(Constants.SIZE, o.get(Constants.SIZE));
        		inten.putExtra(Constants.NAME, o.get(Constants.NAME));
        		inten.putExtra(Constants.X,o.get(Constants.X));
        		inten.putExtra(Constants.Y,o.get(Constants.Y));
        		inten.putExtra(Constants.ADDRESS,o.get(Constants.ADDRESS));
        		startActivityForResult(inten, position+1);
			}
		});	
	}
	private void deleteSaved(int position){
		
		String usercode= settings.getString(Constants.USERCODE, "");
		String userid= settings.getString(Constants.USERID, "");
		HashMap<String, String> o = (HashMap<String, String>) lv1.getItemAtPosition(position);
		String ind =o.get(Constants.ID);

        String[] str1={Constants.baseUrl+version+Constants.urlCommandDeleteAgent,Constants.USERID,userid,Constants.USERCODE,usercode,Constants.ID,ind};
    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+" deleted "+Arrays.toString(str1));
    	DeleteAgents downloadFile = new DeleteAgents(position);
    	downloadFile.execute(str1);
		
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){ 
			if (requestCode == 0) { 	         
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(Constants.ID, data.getStringExtra(Constants.ID));
	  			map.put(Constants.JOBNAME,data.getStringExtra(Constants.JOBNAME));
	  			map.put(Constants.CITYNAME,data.getStringExtra(Constants.CITYNAME));
	  			map.put(Constants.SPECIALITY,data.getStringExtra(Constants.SPECIALITY));
	  			map.put(Constants.AREA,data.getStringExtra(Constants.AREA));
	  			map.put(Constants.CITIES,data.getStringExtra(Constants.CITIES));
	  			map.put(Constants.ROLE,data.getStringExtra(Constants.ROLE));
	  			map.put(Constants.SIZE, data.getStringExtra(Constants.SIZE));
	  			map.put(Constants.X,data.getStringExtra(Constants.X));
	  			map.put(Constants.Y,data.getStringExtra(Constants.Y));
	  			map.put(Constants.ADDRESS,data.getStringExtra(Constants.ADDRESS));			            	        
	  			mylist.add(map);
	  			adapter.notifyDataSetChanged();
	  	     }
	  	     else{
	  	    	HashMap<String, String> map =mylist.get(requestCode-1);
	  	    	map.put(Constants.JOBNAME,data.getStringExtra(Constants.JOBNAME));
	  			map.put(Constants.CITYNAME,data.getStringExtra(Constants.CITYNAME));
	  			map.put(Constants.SPECIALITY,data.getStringExtra(Constants.SPECIALITY));
	  			map.put(Constants.AREA,data.getStringExtra(Constants.AREA));
	  			map.put(Constants.CITIES,data.getStringExtra(Constants.CITIES));
	  			map.put(Constants.ROLE,data.getStringExtra(Constants.ROLE));
	  			map.put(Constants.SIZE, data.getStringExtra(Constants.SIZE));
	  			map.put(Constants.X,data.getStringExtra(Constants.X));
	  			map.put(Constants.Y,data.getStringExtra(Constants.Y));
	  			map.put(Constants.ADDRESS,data.getStringExtra(Constants.ADDRESS));			            	        
	  			adapter.notifyDataSetChanged();
	  	     }
	  	 }
	  	 else if (resultCode == RESULT_CANCELED) {}  
		String str2= Integer.toString(mylist.size());
		tv1.setText(str2);
	  	}
	  public class DeleteAgents extends AsyncTask<String, String, JSONObject> {
			private final ProgressDialog mProgressDialog = new ProgressDialog(SmartAgent.this);
			private int idforchange;
			public DeleteAgents(int position) {
				idforchange=position;
			}
		    @Override
		    protected void onPreExecute() {
		    	super.onPreExecute();	        
		        mProgressDialog.setMessage(getResources().getString(R.string.loading));
		        mProgressDialog.show();
		    }
		  
		    @Override
		    protected JSONObject doInBackground(String... url) {
		    	JSONObject data = null;
		        try{
		            data =new JSONObject(Downloader.downloadPostObject(url));
		        }catch(Exception e){
		        }
		        
		        return data;
		    }
		    @Override
		    protected void onPostExecute(JSONObject json) {
		    	mProgressDialog.dismiss();	
		    	if (json==null) {
		    		Toast.makeText(SmartAgent.this,R.string.netproblem, Toast.LENGTH_SHORT).show();
		    	}
		    	 else {
		        		int res= json.optInt(Constants.STATUS, -1);
		        		if(res!=1){
				    		Toast.makeText(SmartAgent.this,R.string.serverproblem, Toast.LENGTH_SHORT).show();
		        		}
		        		else{
		        			mylist.remove(idforchange);
		        			adapter.notifyDataSetChanged();
		        			String str2= Integer.toString(mylist.size());
		        			tv1.setText(str2);
		        		}
		    	}
		    }
	  }		    
	public class DownloadAgents extends AsyncTask<String, String, String> {
		private final ProgressDialog mProgressDialog = new ProgressDialog(SmartAgent.this);
		
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();	        
	        mProgressDialog.setMessage(getResources().getString(R.string.loading));
	        mProgressDialog.show();
	    }
	  
	    @Override
	    protected String doInBackground(String... url) {
	    	JSONArray json =null;
	        String result=Downloader.downloadPostObject(url);
	       
	    	 return result;
	    }
	    protected void onPostExecute(String jsonOrig) {
	    	mProgressDialog.dismiss();	
	    	JSONArray json=null;
	    	try {
	    		json=new JSONArray(jsonOrig);	    			 
	    		if(json.length()==0){
	    		Toast.makeText(SmartAgent.this,getResources().getString(R.string.noresults), Toast.LENGTH_SHORT).show();
	    	}
	    	else {
	    		String all=getString(R.string.all);
	    		try{						
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
        	  		
        	  		String stringCitys= settings.getString(Constants.CITIES+"0", "");
        	  		JSONArray ja5 =new JSONArray(stringCitys);
        	  		String[] cities =new String[ja5.length()];
					int[] indexCities =new int[cities.length];
        	  		for (int i = 0; i < ja5.length(); i++) {
    						JSONArray ind= ja5.getJSONArray(i);
    						cities[i]=ind.getString(1);
    						indexCities[i]=ind.getInt(0);			
        	  		}
	    			//Log.e(Constants.TAG, "cities= "+Arrays.toString(indexCities));

        	  		for(int i=0;i<json.length();i++){            	  
        	  			HashMap<String, String> map = new HashMap<String, String>();
        	  			JSONObject ja = json.getJSONObject(i);
    	    			Log.e(Constants.TAG,Constants.TAG+"json ="+ja.toString()); 
        	  			map.put(Constants.ID,  ja.optString(Constants.ID));
        	  			int job =ja.optInt(Constants.SPECIALITY,0);
        	  			int area =ja.optInt(Constants.AREA,-1);
        	  			int city =ja.optInt(Constants.CITIES,0);
        	  			int radius =ja.optInt(Constants.RADIUS,0);

        	  			String allRoles =ja.optString(Constants.ROLE,"0").replace("null", "0");
        	  			String size =ja.optString(Constants.SIZE,"0").replace("null", "0");
        	  			Log.e(Constants.TAG,Constants.TAG+"size ="+size);
        	  			String[] rolesStrings=allRoles.replace(" ", "").split(",");
        	  			//int role =ja.optInt(Constants.ROLE,0);			
        	  			if(job>0){
            	  			int jobpos=indexOfArray(indexSpecialities,  job);
            	  			map.put(Constants.JOBNAME,specialities[jobpos]);
            	  			String stringRoles= settings.getString(Constants.ROLE+Integer.toString(jobpos+2), "");

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
        	    			for (int j = 0; j < rolesStrings.length && rolesStrings[j].length()>0; j++) {
								
							
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
            	  			map.put(Constants.CITIES,Integer.toString(radius));
        	  				map.put(Constants.CITYNAME,ja.optString(Constants.ADDRESS,all));
        	  			}
        	  			
        	  			map.put(Constants.SPECIALITY,Integer.toString(job));
        	  			map.put(Constants.AREA,Integer.toString(area));
        	  			map.put(Constants.ROLE,allRoles);
        	  			map.put(Constants.SIZE, size);
        	  			map.put(Constants.X,Double.toString(ja.optDouble(Constants.X,0.0)));
        	  			map.put(Constants.Y,Double.toString(ja.optDouble(Constants.Y,0.0)));
        	  			map.put(Constants.ADDRESS,ja.optString(Constants.ADDRESS));
        	  			map.put(Constants.NAME,ja.optString(Constants.NAME));			            	        
        	  			mylist.add(map);	 
        	  		}    	 
	    		}catch(JSONException e)        {
	    			Toast.makeText(SmartAgent.this,getResources().getString(R.string.netproblem), Toast.LENGTH_SHORT).show();          	 
	    			Log.e(Constants.TAG, "Error parsing data "+e.toString());
	    		}
	    		fillList();
	    	}
	    	} catch (JSONException e) {
	    		JSONObject jsonObject=null;
	    		try {
	    			jsonObject=new JSONObject(jsonOrig);
	    			confirm(SmartAgent.this, R.string.error, jsonObject.getString("result"));
	    		} catch (JSONException e1) {
	    			Toast.makeText(SmartAgent.this,R.string.netproblem, Toast.LENGTH_SHORT).show();
	    			e1.printStackTrace();
	    		}
	    	}
	    }	    	
	}
}
	
	
