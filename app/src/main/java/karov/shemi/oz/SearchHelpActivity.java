package karov.shemi.oz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SearchHelpActivity extends MenuActionActivity {

	// Values for email and password at the time of the login attempt.
	private String mEmail;

	// UI references.
	private EditText mEmailView;
	private TextView mLoginStatusMessageView;
	private Button send;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_help);
		mEmailView = (EditText) findViewById(R.id.email);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		mLoginStatusMessageView.setVisibility(View.INVISIBLE);
		send= (Button)findViewById(R.id.sign_in_button);
		send.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	public void attemptLogin() {
		mEmail = mEmailView.getText().toString().replaceAll("\\W", "").toLowerCase();
		String[] specialities;
  		String specilitiesSaved= settings.getString(Constants.SPECIALITY, "");
  		if(specilitiesSaved.length()>0){
  			JSONArray ja2;
			try {
				ja2 = new JSONArray(specilitiesSaved);
				specialities=new String[ja2.length()];
				for (int i = 0; i < ja2.length(); i++) {
					specialities[i]=ja2.getJSONObject(i).getString(Constants.NAME);// String(i);
				}
			} catch (JSONException e) {
				specialities=getResources().getStringArray(R.array.specialities);
			}
  			
  		}
  		else{
  			specialities=getResources().getStringArray(R.array.specialities);
  		}
		
		
		
		String[] roles=null;
		ArrayList<String> found_specialities = new ArrayList<String>();
		ArrayList<String> found_roles = new ArrayList<String>();
		for (int j = 0; j < specialities.length; j++) {
			String item1 = specialities[j].replaceAll("\\W", "");
			if(item1.toLowerCase().contains(mEmail) || mEmail.contains(item1.toLowerCase())) {
				found_specialities.add(specialities[j]);
				found_roles.add("");
				
			}
			String roleSaved= settings.getString(Constants.ROLE+Integer.toString(j), "");
	  		if(roleSaved.length()>0){
	  			JSONArray ja2;
				try {
					ja2 = new JSONArray(roleSaved);
					roles=new String[ja2.length()];
					for (int i = 0; i < ja2.length(); i++) {
						roles[i]=ja2.getJSONArray(i).getString(1);
					}
				} catch (JSONException e) {
					roles=getResources().getStringArray(R.array.role0+j);
				}
	  			
	  		}
	  		else{
	  			roles=getResources().getStringArray(R.array.role0+j);
	  		}
			
			
			
			for (int k = 0; k < roles.length; k++) {
				String item = roles[k].replaceAll("\\W", "");
				if(item.toLowerCase().contains(mEmail) || mEmail.contains(item.toLowerCase())) {
					found_specialities.add(specialities[j]);
					found_roles.add(roles[k]);
					
				}
			}
			
		}
	
		if(found_specialities.size()>0){
			String connection2=getResources().getString(R.string.roleequal);
			String connection1=getResources().getString(R.string.specialityequal);
			String result=mEmail+" "+getResources().getString(R.string.foundat);
			//+specialities[speciality]+getResources().getString(R.string.roleequal)+roles[role];
			for (int j = 0; j < found_specialities.size(); j++) {
				result= result+connection1+" "+found_specialities.get(j);
				if(found_roles.get(j).length()>0) result=result+connection2+" "+found_roles.get(j);
				result=result+"\n\n";
				
			}
			mLoginStatusMessageView.setText(result);
			
		}
		else{
			mLoginStatusMessageView.setText(R.string.noresults);
		}
		mLoginStatusMessageView.setVisibility(View.VISIBLE);
	}
}
