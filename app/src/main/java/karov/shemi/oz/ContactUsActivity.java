package karov.shemi.oz;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.analytics.tracking.android.EasyTracker;

import org.json.JSONObject;

public class ContactUsActivity extends MenuActionActivity {
	private EditText name,mail,phone,msg;
	private int title;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	    	this.finish();//NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.contactuslayout);
	     Spinner spinner1 = (Spinner) findViewById(R.id.spincontact1);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.contacttitle, android.R.layout.simple_spinner_item);      
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            	title=arg2;
                     
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        msg = (EditText) findViewById(R.id.editTextinput);
        name = (EditText) findViewById(R.id.editText2);
        phone = (EditText) findViewById(R.id.editText3);
        mail = (EditText) findViewById(R.id.editText1);
        
		Button confirmButton = (Button) findViewById(R.id.insertbutton);
		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	if(msg.getText().toString().length()<2 || name.getText().toString().length()<2){
		    		confirm(ContactUsActivity.this, R.string.noinput2);
		    		//Toast.makeText(ContactUsActivity.this,R.string.noinput2, Toast.LENGTH_SHORT).show();
		    	}
		    	else if(!phoneValidator(phone.getText().toString()) ){
		    		confirm(ContactUsActivity.this,R.string.wrongphone);
		    		phone.requestFocus();
		    	}
		    	else if(!emailValidator(mail.getText().toString())){
		    		confirm(ContactUsActivity.this,R.string.wrongmail);
		    		mail.requestFocus();
		    	}
		    	
		    	else {
		    		String[] str1={Constants.baseUrl+version+Constants.urlCommandContactUs,Constants.TYPE,Integer.toString(title),Constants.MSG,msg.getText().toString(),Constants.NAME,name.getText().toString(),Constants.PHONE,phone.getText().toString(),Constants.MAIL,mail.getText().toString(),Constants.USERCODE,usercode,Constants.USERID,userid};
		    		GenericDownload sendmail = new GenericDownload(1,true);
		    		sendmail.execute(str1);
		    	}
				   
		    	
		    }
		});

	}
	protected void taskResponse(JSONObject json,int responseMode) {
		if(responseMode==1) confirmFinish(this,R.string.succesfullcontact);
		else super.taskResponse(json, responseMode);
	}
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}

}
