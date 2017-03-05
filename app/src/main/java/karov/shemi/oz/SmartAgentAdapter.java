package karov.shemi.oz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class SmartAgentAdapter  extends BaseAdapter {
	private ArrayList<HashMap<String, String>> items=new ArrayList<HashMap<String, String>>();
private SmartAgent context;
private int pos1;
private String ind;




public SmartAgentAdapter(SmartAgent context , ArrayList<HashMap<String, String>> items) {
    super();
    this.context = context;
    this.items = items;
 }

@Override
public int getCount() {
    return items.size();
}

@Override
public Object getItem(int position) {
    return items.get(position);
}

@Override
public long getItemId(int position) {
    return position;
}
private void deleteSaved(int position,String ind){
	SharedPreferences         settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
	String usercode= settings.getString(Constants.USERCODE, "");
	String userid= settings.getString(Constants.USERID, "");
	
	PackageInfo pInfo=null;
	String version="/uknkown/";
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			 version = "/"+pInfo.versionName+"/"; 
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    String[] str1={Constants.baseUrl+version+Constants.urlCommandDeleteAgent,Constants.USERID,userid,Constants.USERCODE,usercode,Constants.ID,ind};
	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+" deleted "+Arrays.toString(str1));
	DeleteAgents deletetask = new DeleteAgents(position);
	deletetask.execute(str1);
	
}
@Override
public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    ViewHolder holder = null;
    HashMap<String, String> job=items.get(position);

     if (convertView == null) {

            convertView = mInflater.inflate(R.layout.lineagent, parent , false);
            holder = new ViewHolder();
            holder.tv1 = (TextView) convertView.findViewById(R.id.text1);
            holder.tv2= (TextView) convertView.findViewById(R.id.text2);
            holder.tv3 = (TextView) convertView.findViewById(R.id.text3);
            holder.tv4 = (TextView) convertView.findViewById(R.id.text4);

              holder.btnBookNow = (ImageButton) convertView.findViewById(R.id.imageAgentButton);

            holder.btnBookNow.setFocusable(false);
            holder.btnBookNow.setFocusableInTouchMode(false);

        /*    holder.btnBookNow.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(context , BookingFormActivity.class);
                    intent.putExtra("car_name", car.getCarName());
                    intent.putExtra("min_age", car.getMinimumAge());
                    context.startActivity(intent);
                }
            });
*/
            convertView.setTag(holder);
            
    }
     else {

         holder = (ViewHolder) convertView.getTag();
        }
     holder.tv1.setTag(Integer.valueOf(position));
     holder.tv1.setText(job.get(Constants.JOBNAME));
     holder.tv2.setText(job.get(Constants.ROLENAME));
     holder.tv3.setText(job.get(Constants.CITYNAME));
     if(job.get(Constants.NAME).length()>0) holder.tv4.setText(job.get(Constants.NAME));
     else holder.tv4.setText(context.getString(R.string.agentnumber)+" "+position); 
     //int index=Integer.valueOf(job.get(Constants.ID)); 
     convertView.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ViewHolder obg= (ViewHolder)v.getTag();
			int pos1=(Integer)obg.tv1.getTag();
			HashMap<String, String> o = (HashMap<String, String>) items.get(pos1);
			//String ind =o.get(Constants.ID);
			//Toast.makeText(SmartAgent.this, "ID '" +ind  + "' was clicked.", Toast.LENGTH_SHORT).show();	
	    	Intent inten = new Intent(context, ShowAll.class);
	    	int[] selections={0,0,0,-2,15000};
	    	selections[0]=Integer.valueOf(o.get(Constants.SPECIALITY));
	    	selections[3]=Integer.valueOf(o.get(Constants.AREA));
	    	selections[4]=Integer.valueOf(o.get(Constants.CITIES));
	    	inten.putExtra(Constants.VAR,selections);
	    	inten.putExtra(Constants.ROLE, o.get(Constants.ROLE));
	    	inten.putExtra(Constants.SIZE, o.get(Constants.SIZE));
	    	inten.putExtra(Constants.X,o.get(Constants.X));
	    	inten.putExtra(Constants.Y,o.get(Constants.Y));
	    	inten.putExtra(Constants.TYPE,Constants.SEARCH);
	    	context.startActivity(inten);
			
		}
	});
     convertView.setOnLongClickListener(new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			ViewHolder obg= (ViewHolder)v.getTag();
			 pos1=(Integer)obg.tv1.getTag();
			HashMap<String, String> o = (HashMap<String, String>) items.get(pos1);
			 ind =o.get(Constants.ID);
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
          	builder.setTitle(R.string.deleteagent);
          	builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                  }
                  
              });
          	builder.setPositiveButton(R.string.deleteyes, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {                		
            			deleteSaved(pos1, ind);
                  }});
                  builder.show();
				
                  
			return true;
		}
	});
     holder.btnBookNow.setTag(Integer.valueOf(position));
     /*holder.btnBookNow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton v, boolean arg1) {}});*/
     holder.btnBookNow.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int pos= (Integer)v.getTag();
			HashMap<String, String> o = (HashMap<String, String>) items.get(pos);
			//String ind =o.get(Constants.ID);
			//Toast.makeText(SmartAgent.this, "ID '" +ind  + "' was clicked.", Toast.LENGTH_SHORT).show();				
			Intent inten = new Intent(context, EditSmartAgent.class);
    		inten.putExtra(Constants.ID,o.get(Constants.ID));
    		inten.putExtra(Constants.SPECIALITY,o.get(Constants.SPECIALITY));
    		inten.putExtra(Constants.AREA,o.get(Constants.AREA));
    		inten.putExtra(Constants.CITIES,o.get(Constants.CITIES));
    		inten.putExtra(Constants.ROLE, o.get(Constants.ROLE));
    		inten.putExtra(Constants.SIZE, o.get(Constants.SIZE));
    		inten.putExtra(Constants.X,o.get(Constants.X));
    		inten.putExtra(Constants.Y,o.get(Constants.Y));
    		inten.putExtra(Constants.ADDRESS,o.get(Constants.ADDRESS));
    		inten.putExtra(Constants.NAME, o.get(Constants.NAME));
    		context.startActivity(inten);
		}
	});

  return convertView;
 }

 static class ViewHolder {

            TextView tv1;
            TextView tv2;
            TextView tv3;
            TextView tv4;

            ImageButton btnBookNow;
        }



 public class DeleteAgents extends AsyncTask<String, String, JSONObject> {
		private final ProgressDialog mProgressDialog = new ProgressDialog(context);
		private int idforchange;
		public DeleteAgents(int position) {
			idforchange=position;
		}
	    @Override
	    protected void onPreExecute() {
	    	super.onPreExecute();	        
	        mProgressDialog.setMessage(context.getResources().getString(R.string.loading));
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
	    		Toast.makeText(context,R.string.netproblem, Toast.LENGTH_SHORT).show();
	    	}
	    	 else {
	        		int res= json.optInt(Constants.STATUS, -1);
	        		if(res!=1){
			    		Toast.makeText(context,R.string.serverproblem, Toast.LENGTH_SHORT).show();
	        		}
	        		else{
	        			items.remove(idforchange);
	        			notifyDataSetChanged();
	        			context.updateCounter();
	        			
	        		}
	    	}
	    }
}	
}