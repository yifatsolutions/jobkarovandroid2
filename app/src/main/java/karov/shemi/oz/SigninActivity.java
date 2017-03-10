package karov.shemi.oz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

import static karov.shemi.oz.R.string.signin;


public class SigninActivity extends MenuActionActivity {

	private String token,tmDevice;
	private boolean alreadySent=false;
	private TextView tvwelcome;
	private Button usingmail,logout,register;
	private LoginButton authButton;
	private boolean cvFileRequired=false;
	private CallbackManager callbackManager;
	protected void taskResponse(JSONObject json,int responseMode) {
		if(responseMode==3){
			int res= json.optInt(Constants.STATUS, -1);	
			if(res==3){
				String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	    		String msg= json.optString(Constants.RESULT, err);
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.error);
				builder.setMessage(msg);
				builder.setNegativeButton(signin, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        	 	dialog.dismiss();
		        	 	SignIn();
			        }
					});
				builder.show();
			}
			else{
				optAndSave(json);
				String facebook=settings.getString(Constants.FACEBOOKNAME, "");
				String firstname=settings.getString(Constants.FIRST_NAME, "");
				String lastname=settings.getString(Constants.LAST_NAME, "");

				if(index>0) confirmFinish(this, R.string.cvsent);
				else{
					confirm(SigninActivity.this,R.string.welcome,firstname+" "+lastname);
					showWelcome(firstname+" "+lastname,facebook);
				}
			}
		}
		else if(responseMode==4){
			final AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivity.this);
 			builder.setTitle(R.string.passwordsent);
 			builder.setMessage(R.string.passwordsent);
 			builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
 		        public void onClick(DialogInterface dialog, int id) {
	        			SignIn();
 		        }
 		    });
 			builder.show();
		}
		else if(responseMode==5){
			optAndSave(json);
			String first_name=json.optString(Constants.FIRST_NAME, "");
    		String last_name=json.optString(Constants.LAST_NAME, "");
    		String WelcomeMsg=first_name+" "+last_name;
    		String facebook=settings.getString(Constants.FACEBOOKNAME, "");
    		showWelcome(WelcomeMsg,facebook);
			int res= json.optInt(Constants.STATUS, -1);	
			if(res==2){
				/*String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	    		String msg= json.optString(Constants.RESULT, err);
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.error);
				builder.setMessage(msg);
				builder.setNegativeButton(signin, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        	 	dialog.dismiss();*/

				Intent intent3=new Intent(SigninActivity.this, RegistrationActivity.class);
				intent3.putExtra(Constants.ID, index);
				intent3.putExtra(Constants.REQUIRE_CV, cvFileRequired);
				startActivityForResult(intent3,1);
			        /*}
					});
				builder.show();*/
			}
			else{
				if(index>0) confirmFinish(this, R.string.cvsent);
				else  confirm(this,R.string.welcome,getString(R.string.welcome)+ " "+WelcomeMsg);

			}
		}
		else{
			super.taskResponse(json,responseMode);
		}
		}
	public void SignIn(){
		String oldpass=settings.getString(Constants.PASSWORD, "");
		String oldname=settings.getString(Constants.MYEMAIL, "");  
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_signin, (ViewGroup) findViewById(R.id.root));
		final EditText input1 = (EditText) layout.findViewById(R.id.username);
		final EditText input2 = (EditText) layout.findViewById(R.id.password);
		if(oldname.length()>0) input1.setText(oldname);
		if(oldpass.length()>0) input2.setText(oldpass);
	 	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	 	alert.setView(layout);
	 	alert.setTitle(R.string.insertpassword);
	 	alert.setPositiveButton(signin, new DialogInterface.OnClickListener() {
	 		public void onClick(DialogInterface dialog, int whichButton) {
	 			String user= input1.getText().toString();
	 			String password= input2.getText().toString();
	 			Editor edit= settings.edit();
	 			edit.putString(Constants.PASSWORD, password);
	 			edit.putString(Constants.MYEMAIL, user);
	 			edit.apply();
	 			if(user.length()>1 && password.length()>1){
	 		        String registrationId = settings.getString(Constants.PROPERTY_REG_ID, "");
	 		        String[] str1={Constants.baseUrl+version+Constants.urlCommandLogin,Constants.EMAIL,user,Constants.PASSWORD,password,Constants.TOKEN,registrationId,Constants.SiteID,Integer.toString(index),Constants.MODELTYPE,"1",Constants.MODELNAME,tmDevice};
	 				GenericDownload signin = new GenericDownload(3,true);
	 				signin.execute(str1);
		 			dialog.cancel();
	 			}
	 			else{
	 				confirm(SigninActivity.this,R.string.wrongpass);
	 			}
	 		}
	 	});
	 	alert.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	 		public void onClick(DialogInterface dialog, int whichButton) {
	 			dialog.cancel();  
	 		}
	 	});
	 	alert.setNeutralButton(R.string.forgotpassword, new DialogInterface.OnClickListener() {
	 		public void onClick(DialogInterface dialog, int whichButton) {
	 			dialog.cancel();
	 			forgotPassword(input1.getText().toString());
	 		}
	 	});
	 	AlertDialog dialog = alert.create();
	 	dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	 	dialog.show();
	}
	private void forgotPassword(String username){
		if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+"hi");
		String oldname=settings.getString(Constants.MYEMAIL, "");
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.forgotpassword);
		alert.setMessage(R.string.insertemail);
		final EditText input = new EditText(this);
		if(emailValidator(username)) input.setText(username);
		else input.setText(oldname);
		alert.setView(input);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String user= input.getText().toString();
				dialog.cancel();
				if(emailValidator(user)){
					String[] str1={Constants.baseUrl+version+Constants.urlCommandForgot,Constants.EMAIL,user};
					GenericDownload sendpassword = new GenericDownload(4,true);
					sendpassword.execute(str1);
				}
				else{
					confirm(SigninActivity.this,R.string.wrongmail);
				}
		    }
		  });
		  alert.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  dialog.cancel();
				  SignIn();
			  }
			});
		  AlertDialog dialog = alert.create();
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			dialog.show();		  
		
	
	}
	@Override
	public void showWelcome(String msg,String facbookname){
		String useremail=settings.getString(Constants.EMAIL, "");

		if(emailValidator(useremail)){
			tvwelcome.setText(getString(R.string.hello)+" "+msg);
			tvwelcome.setVisibility(View.VISIBLE);
    		if(facbookname.length()>0){
    			authButton.setVisibility(View.VISIBLE);
    			logout.setVisibility(View.GONE);
    			//sendRequestButton.setVisibility(View.VISIBLE);
    		}
    		else{
    			authButton.setVisibility(View.GONE);
    			logout.setVisibility(View.VISIBLE);
        		//sendRequestButton.setVisibility(View.GONE);
    		}
    		usingmail.setVisibility(View.GONE);
    		register.setVisibility(View.GONE);
		}
		else {
			tvwelcome.setVisibility(View.GONE);
			usingmail.setVisibility(View.VISIBLE);
			authButton.setVisibility(View.VISIBLE);
    		logout.setVisibility(View.GONE);
    		//sendRequestButton.setVisibility(View.GONE);
    		register.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_login);
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) ==PackageManager.PERMISSION_GRANTED){	
			tmDevice = "" + tm.getDeviceId();
		}	
		else{
	    	   ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		}
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null) {
			index = bundle.getInt(Constants.ID,0);
			cvFileRequired=bundle.getInt(Constants.REQUIRE_CV, 0)>0;
		}
		else index=0;
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));
		alreadySent=false;
//		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		tvwelcome= (TextView)findViewById(R.id.textusername);
		//Profile fbProfile = Profile.getCurrentProfile();
		AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
			@Override
			protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
													   AccessToken currentAccessToken) {
				if (currentAccessToken == null) {
					LogOutTask logouttask = new LogOutTask(false);
					logouttask.execute();
				}
			}
		};
		//if (fbProfile == null) {
		authButton = (LoginButton) findViewById(R.id.authButton);
		
		authButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_education_history"));
		callbackManager = CallbackManager.Factory.create();

		authButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
			public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
				/*usercode=settings.getString(Constants.USERCODE, "");
				userid=settings.getString(Constants.USERID, "");
					String registrationId = settings.getString(Constants.PROPERTY_REG_ID, "");
					String[] str1={Constants.baseUrl+version+Constants.urlCommandRegisterFacebook,"token",accessToken.getToken(),"facebookuser",accessToken.getUserId(),Constants.MODELTYPE,"1",Constants.MODELNAME,tmDevice,Constants.TOKEN,registrationId,Constants.SiteID,Integer.toString(index)};
					GenericDownload signin;
					if(index>0) signin= new GenericDownload(5,true);
					else signin= new GenericDownload(-2,true);
					signin.execute(str1);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(Constants.FACEBOOKNAME, accessToken.getToken());
					editor.commit();

				showWelcome(accessToken.toString(),accessToken.toString());*/


				GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
						SharedPreferences.Editor editor = settings.edit();
						if(user!=null) {
							Iterator<String> temp = user.keys();

							while (temp.hasNext()) {
								String key = temp.next();
								String value = user.optString(key, "");
								if (value.length() > 0) editor.putString(key.toLowerCase(), value);
							}
						}
                        String email=user.optString("email");
                    //    String useremail=settings.getString(Constants.EMAIL, "");
                        usercode=settings.getString(Constants.USERCODE, "");
                        userid=settings.getString(Constants.USERID, "");
                      //  if(userid.length()==0 || usercode.length()==0 ||!email.equalsIgnoreCase(useremail)){

                            String registrationId = settings.getString(Constants.PROPERTY_REG_ID, "");
                            String[] str1={Constants.baseUrl+version+Constants.urlCommandRegisterFacebook,Constants.USERCODE,usercode,Constants.USERID,userid, Constants.EMAIL,email,Constants.PASSWORD,Constants.PASSWORD,Constants.FIRST_NAME,user.optString("first_name"),Constants.LAST_NAME,user.optString("last_name"),"token",accessToken.getToken(),Constants.MODELTYPE,"1",Constants.MODELNAME,tmDevice,Constants.TOKEN,registrationId,Constants.SiteID,Integer.toString(index)};
                            GenericDownload signin;
                            //if(index>0)
							signin= new GenericDownload(5,true);
                            //else signin= new GenericDownload(-2,true);
                            signin.execute(str1);
                            editor.putString(Constants.FACEBOOKNAME, user.optString("name"));
                            editor.commit();
//                        }
                        showWelcome(user.optString("first_name",""), user.optString("last_name",""));


                    }
                });
				Bundle parameters = new Bundle();
				parameters.putString("fields", "id,name,email,gender,education,first_name,hometown,last_name,work,birthday");
				request.setParameters(parameters);
				request.executeAsync();
				}


			@Override
			public void onCancel() {
				// App code
			}

			@Override
			public void onError(FacebookException exception) {
				// App code
			}
		});
		logout = (Button) findViewById(R.id.logout); 
		logout.setOnClickListener(new View.OnClickListener() {      
	    	public void onClick(View arg0) {
	    		LogOutTask logouttask = new LogOutTask(true);
	    		logouttask.execute();
	    	}});
	    
	    usingmail = (Button) findViewById(R.id.usingmail); 
	    usingmail.setOnClickListener(new View.OnClickListener() {      
	    	public void onClick(View arg0) {
	    		SignIn();
	    	}});
	     register = (Button) findViewById(R.id.register);
	    register.setOnClickListener(new View.OnClickListener() {
	    	 public void onClick(View arg0) {
	    		 Intent intent3=new Intent(SigninActivity.this, RegistrationActivity.class);
	   		  intent3.putExtra(Constants.ID, index);
			  intent3.putExtra(Constants.REQUIRE_CV, cvFileRequired);
			  startActivityForResult(intent3,1);
	    	 }});

//	    String name=settings.getString(Constants.NAME, "");
//	    String facebook=settings.getString(Constants.FACEBOOKNAME, "");

//		showWelcome(name,facebook);
		
	    if(getIntent().getBooleanExtra(Constants.SECONDTIME, false)){}
	}

	

   @Override
	public void onResume() {
	    super.onResume();
	    String first_name=settings.getString(Constants.FIRST_NAME, "");
	   String last_name=settings.getString(Constants.LAST_NAME, "");

	   String facebook=settings.getString(Constants.FACEBOOKNAME, "");

		showWelcome(first_name+" "+last_name,facebook);

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 1 && index>0 && resultCode==Activity.RESULT_OK) {
	    	finish();
	    }
	    else if(requestCode == 1  && resultCode==7) {
	    	SignIn();
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu); 
		return true;//super.onCreateOptionsMenu(menu);
	}
	
	private class LogOutTask extends AsyncTask<String, String, JSONObject> {
	    private final ProgressDialog mProgressDialog = new ProgressDialog(SigninActivity.this);
		private boolean showProgressBar;
		protected   LogOutTask(boolean showProgressBar){
			super();
			this.showProgressBar=showProgressBar;
		}
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(showProgressBar) {
				mProgressDialog.setMessage(getResources().getString(R.string.sendingnow));
				mProgressDialog.show();
			}
	    }
	    @Override
	    protected JSONObject doInBackground(String... url) {
	    	JSONObject data = null;
	        try{
	        	 final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
                 final String tmDevice = "" + tm.getDeviceId();
         	    String token=settings.getString(Constants.PROPERTY_REG_ID,"");
	        	String[] str1={Constants.baseUrl+version+Constants.urlCommandModelLogout,Constants.USERID,settings.getString(Constants.USERID, ""),Constants.USERCODE,settings.getString(Constants.USERCODE, ""),Constants.MODELTYPE,"1",Constants.MODELNAME,tmDevice,Constants.TOKEN,token};
                data =new JSONObject(Downloader.downloadPostObject(str1));
    	        
	        }catch(Exception e){
		    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, e.toString());
	        	
	        }
	        return data;
	    }
	    @Override
        protected void onPostExecute(JSONObject json) {
	    	mProgressDialog.dismiss();
        	if(json==null ) {
        		confirm(SigninActivity.this,R.string.netproblem);
	        }
	        else {
	        		int res= json.optInt(Constants.STATUS, -1);
	        		String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	        		String msg= json.optString(Constants.RESULT, err);
	        		if (BuildConfig.DEBUG) Log.i(Constants.TAG,msg);
	        		if(res>0){
	        			String usercode= json.optString(Constants.USERCODE, "");
	        			String userid= json.optString(Constants.USERID, "");
	        			SharedPreferences.Editor editor = settings.edit();
	        			Toast.makeText(SigninActivity.this,getResources().getString(R.string.logoutsuccese), Toast.LENGTH_LONG).show();
	        			editor.putString(Constants.USERCODE, usercode);
	        			editor.putString(Constants.USERID, userid);
	    	    		editor.putString(Constants.PASSWORD, "");
						editor.putString(Constants.FACEBOOKNAME, "");
						editor.putString(Constants.EMAIL, "");
	        			editor.putString(Constants.NAME, "");
	        			editor.putString(Constants.FIRST_NAME, "");
	        			editor.putString(Constants.LAST_NAME, "");
	        			editor.putString(Constants.MY_DESCRIPTION, "");
	        			editor.putString(Constants.LIFE_STORY, "");
	        			editor.putString(Constants.PHONE, "");
						editor.putString(Constants.FAVMODE, "0");
						editor.putInt(Constants.FAV, -2);
						editor.commit();
	    	    		showWelcome("", "");
	        		}
	        		else confirm(SigninActivity.this, R.string.error,msg);
	        }
        }
}

}
