package karov.shemi.oz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EditSmartAgent extends SearchActivity {
	 
	private String id;
	private String name;
   
    @Override
    protected void readSaved(){
    	config();
    }
    
    @Override
    protected void config(){
    	//mDrawerListRight.setVisibility(View.GONE);
    	mDrawerList.setVisibility(View.GONE);
    	addnumbers=false;
    	searchView = (SearchView) findViewById(R.id.searchView);
    	searchView.setVisibility(View.GONE);
    	Bundle extras = getIntent().getExtras();
    	if(extras!=null){
    		id= extras.getString(Constants.ID,"0");
    		selections[0]= Integer.valueOf(extras.getString(Constants.SPECIALITY,"0"));
    		selections[3]= Integer.valueOf(extras.getString(Constants.AREA,"-2"));
    		selections[4]= Integer.valueOf(extras.getString(Constants.CITIES,"15000"));
    		myx= Double.valueOf(extras.getString(Constants.X,"0.0"));
    		myy= Double.valueOf(extras.getString(Constants.Y,"0.0"));
    		if(myx==0 && myy==0) updateAddressByGPS=true;
    		else updateAddressByGPS=false;
    		String addressVal=extras.getString(Constants.ADDRESS, "");
    		changeAddress.setText(addressVal);
    		multiSelected[0]=extras.getString(Constants.ROLE,"0");
    		name=extras.getString(Constants.NAME,"");
    		multiSelected[1]=extras.getString(Constants.SIZE,"0");
    	}
    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, "add agent "+Arrays.toString(selections)+" "+Arrays.toString(multiSelected));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mainnav);
        Button calc = (Button) findViewById(R.id.calculate);
        calc.setText(R.string.save);
        if(selections[0]!=0) spinner1.setVisibility(View.VISIBLE);
        if(!multiSelected[0].equalsIgnoreCase("0") && !multiSelected[0].equalsIgnoreCase("")) spinner2.setVisibility(View.VISIBLE);
        if(!multiSelected[1].equalsIgnoreCase("0") && !multiSelected[1].equalsIgnoreCase("")) spinner3.setVisibility(View.VISIBLE);
    }
    @Override
    protected void calcAction(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.insertagentname);
    	final EditText input = new EditText(this);
    	if(name!=null && name.length()>0) input.setText(name);
    	builder.setView(input);
    	
    	builder.setNegativeButton(R.string.done, new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	String usercode= settings.getString(Constants.USERCODE, "");
    			String userid= settings.getString(Constants.USERID, "");
    			
    			 if(selections[3]<-1){
    				  
    				  Geocoder gd = new Geocoder(getApplicationContext(), Locale.getDefault());
    				  
    				  try{
    					  
    					  
    					 List<Address> addresses=gd.getFromLocation(myx, myy, 1);
    					 String addressVal=addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getLocality();
    					 changeAddress.setText(addressVal);
    				  }
    				  catch (Exception e){
    					
    				  }
    				  selections[3]=-1;
    			 }
    	    	String xStr =Double.toString(myx);
    	 	    String yStr =Double.toString(myy);
    	    	String sel0= Integer.toString(selections[0]);
    	    	String sel1= spinner2.getSelectedIndAsString();
    	    	String sel2= spinner3.getSelectedIndAsString();
    	    	String sel3= Integer.toString(selections[3]);
    	    	String sel4=Integer.toString(selections[4]);
    	    	String[] str1={Constants.baseUrl+version+Constants.urlCommandSmartAgent,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"cities",sel4,"x",xStr,"y",yStr,Constants.TYPE,"1","for","1",Constants.USERID,userid,Constants.USERCODE,usercode,Constants.ID,id,Constants.ADDRESS,changeAddress.getText().toString().replaceAll(getResources().getString(R.string.presstochange),""),Constants.NAME,input.getText().toString(),Constants.TIME,"3"};
    	    	if(selections[3]<=-1){
        	    	String[] str4={Constants.baseUrl+version+Constants.urlCommandSmartAgent,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"radius",sel4,"x",xStr,"y",yStr,Constants.TYPE,"1","for","1",Constants.USERID,userid,Constants.USERCODE,usercode,Constants.ID,id,Constants.ADDRESS,changeAddress.getText().toString().replaceAll(getResources().getString(R.string.presstochange),""),Constants.NAME,input.getText().toString(),Constants.TIME,"3"};
        	    	str1=str4;
    	    	}
    	    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(str1));
    	    	AddAgent downloadFile = new AddAgent();
    	    	downloadFile.execute(str1);
    	    }
    	});
    	
    	AlertDialog dialog = builder.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();    	
		
    }
    private class AddAgent extends AsyncTask<String, String, JSONObject> {
	    private final ProgressDialog mProgressDialog = new ProgressDialog(EditSmartAgent.this);
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        mProgressDialog.setMessage(getResources().getString(R.string.sendingnow));
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
	        super.onPostExecute(json);
	        mProgressDialog.dismiss();
	        if(json==null ) {
	        	Toast.makeText(EditSmartAgent.this,getResources().getString(R.string.netproblem), Toast.LENGTH_SHORT).show();
	        }
	        else {
	        		int res= json.optInt(Constants.STATUS, -1);
	        		String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	        		String msg= json.optString(Constants.RESULT, err);
	        		if(res==-1){
	        			//confirm(EditSmartAgent.this,R.string.serverproblem,msg);
	        			Toast.makeText(EditSmartAgent.this,getResources().getString(R.string.serverproblem)+" "+msg, Toast.LENGTH_SHORT).show();
	        		}
	        		else if(res!=1){
	        			Toast.makeText(EditSmartAgent.this,getResources().getString(R.string.error)+" "+msg, Toast.LENGTH_SHORT).show();
	        			//confirm(EditSmartAgent.this,R.string.error,msg);
	        		}
	        		else{
	        			int index= json.optInt(Constants.ID, 0);
						int spinner1pos=indexOfArray(indexSpecialities,  selections[0]);
						String item1= spinner1.getItemAtPosition(spinner1pos).toString();
						int spinner4pos=indexOfArray(indexAreas,  selections[3]);
						String item4= spinner4.getItemAtPosition(spinner4pos).toString();
						String item5;
						if (selections[3]>-1){
							 item5=(String) getKeyFromValue(cityMap, Integer.valueOf(selections[4]));
						 }
						 else {
							 int spinner5pos=indexOfArray(radiuses,  selections[4]);
							 item5= spinner1.getItemAtPosition(spinner5pos).toString();
							 
						 }
						Intent returnIntent = new Intent();
						returnIntent.putExtra(Constants.ID,Integer.toString(index));
						returnIntent.putExtra(Constants.JOBNAME,spinner1.getSelectedItem().toString());
						returnIntent.putExtra(Constants.CITYNAME,spinner4.getSelectedItem().toString());
		        		returnIntent.putExtra(Constants.ROLENAME, spinner2.getSelectedItemsAsString());

		        		returnIntent.putExtra(Constants.SPECIALITY,Integer.toString(selections[0]));
		        		returnIntent.putExtra(Constants.AREA,Integer.toString(selections[3]));
		        		returnIntent.putExtra(Constants.CITIES,Integer.toString(selections[4]));
		        		returnIntent.putExtra(Constants.ROLE, spinner2.getSelectedIndAsString());
		        		returnIntent.putExtra(Constants.SIZE, spinner3.getSelectedIndAsString());
		        		returnIntent.putExtra(Constants.X,Double.toString(myx));
		        		returnIntent.putExtra(Constants.Y,Double.toString(myy));
		        		returnIntent.putExtra(Constants.ADDRESS,changeAddress.getText().toString());
		        		setResult(RESULT_OK,returnIntent); 
	        			finish();
	        			
	        		}
	        }
	
	    }
	} 
 
}