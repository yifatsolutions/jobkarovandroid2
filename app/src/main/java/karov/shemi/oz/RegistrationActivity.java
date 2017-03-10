package karov.shemi.oz;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class RegistrationActivity extends MenuActionActivity {
	private EditText name,mail,phone,msg,lastname,password;
	private SharedPreferences settings;
	//private String filePath;
	private Uri outputFileUri;
	 private static final int PICKFILE_RESULT_CODE = 1;
	 private static final int PICKFILE_RESULT_CODENEW = 2;
	 private boolean fileUploaded=false;
	 private boolean cvFileRequired=false;
	 private boolean updateOnly=false;
	 private 	            Uri selectedImageUri;
	 private String filePath,lifestory;
	 private ScrollView scroller;
	 private Button pickFileButton;
	 private TextView registrationfilename;
	  private CheckBox termsofuse,acceptmail;
	private boolean mode;


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	this.finish();//NavUtils.navigateUpFromSameTask(this);
	        return true;

	    case R.id.ALREADY_REGISTER:
	    	Intent intent3=new Intent(this, SigninActivity.class);
	    	intent3.putExtra(Constants.ID, index);
	    	startActivity(intent3);
	    }
	    return super.onOptionsItemSelected(item);
	}
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.registration, menu);
	        return true;//super.onCreateOptionsMenu(menu);
	    }
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		setTitle("");
		//filePath="";
		Bundle bundle = getIntent().getExtras();
    	if(bundle!=null) {
    		index = bundle.getInt(Constants.ID,0);
    		cvFileRequired=bundle.getBoolean(Constants.REQUIRE_CV, false);
    		updateOnly=bundle.getInt(Constants.UPDATE, 0)>0;
    	}
    	else index=0;

		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        scroller = (ScrollView) findViewById(R.id.scrollregistration);
        msg = (EditText) findViewById(R.id.editTextinput);

        if(cvFileRequired){
        	msg.setVisibility(View.GONE);
        	TextView tv11 = (TextView) findViewById(R.id.dialogtitle);
        	tv11.setVisibility(View.GONE);
        }

		TextView acceptmailtext1 = (TextView) findViewById(R.id.acceptmailtext1);
		String acceptmailstring=settings.getString(Constants.TAKANONSTRING,getString(R.string.acceptmail));
		acceptmailtext1.setText(acceptmailstring);


		name = (EditText) findViewById(R.id.editText2);
        String firstnamestr=settings.getString(Constants.FIRST_NAME, "");
		name.setText(firstnamestr);
        lastname = (EditText) findViewById(R.id.editTextLastName2);
		String lastnamestr=settings.getString(Constants.LAST_NAME, "");
		lastname.setText(lastnamestr);
        phone = (EditText) findViewById(R.id.editText3);
        registrationfilename = (TextView) findViewById(R.id.registrationfilename);
        termsofuse = (CheckBox) findViewById(R.id.checkboxtermsofuse);
		acceptmail = (CheckBox) findViewById(R.id.checkboxacceptmail);


		String takanon=settings.getString(Constants.TAKANON, "");
		String acceptmailstatus=settings.getString(Constants.TAKANON2, "");

		if(takanon.equalsIgnoreCase("1")){
        	termsofuse.setChecked(true);
        }
		if(acceptmailstatus.equalsIgnoreCase("1")){
			acceptmail.setChecked(true);
		}
        TextView checkboxtermsofusetext2= (TextView)findViewById(R.id.checkboxtermsofusetext2);
        checkboxtermsofusetext2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent inten = new Intent(RegistrationActivity.this,WebViewActivity.class);
				 inten.putExtra(Constants.DESC, 5);
				inten.putExtra(Constants.LINK,"https://www.jobkarov.com/Home/Page/47");
				 startActivity(inten);

			}
		});
        mail = (EditText) findViewById(R.id.editText1);
        mail.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
              if (!hasFocus) {
				String mailString=mail.getText().toString();
				String[] str1={Constants.baseUrl+version+Constants.urlCommandCheckEmail,Constants.USERCODE,usercode,Constants.USERID,userid,Constants.EMAIL,mailString};
				GenericDownload checkemail = new GenericDownload(-3,false);
				checkemail.execute(str1);

			}
            }
		});
        password = (EditText) findViewById(R.id.editTextPassword);
		String useremail=settings.getString(Constants.EMAIL, "");
		if(emailValidator(useremail)) mail.setText(useremail);
		String userphone=settings.getString(Constants.PHONE, "");
		if(phoneValidator(userphone)) phone.setText(userphone);
		lifestory=settings.getString(Constants.LIFE_STORY, "");
		if(lifestory.length()>0) {
			String fileselected=getString(R.string.selectedfile);
    		registrationfilename.setText(fileselected+" "+lifestory);
		}
		String mydescription=settings.getString(Constants.MY_DESCRIPTION, " ");
		if(mydescription!=null && !mydescription.equalsIgnoreCase("null") && mydescription.length()>0) msg.setText(mydescription);


		if(updateOnly) {
			mail.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
			RelativeLayout llpassword=(RelativeLayout)findViewById(R.id.linearlayoutpassword);
			RelativeLayout llmail=(RelativeLayout)findViewById(R.id.linearlayoutmail);
			llpassword.setVisibility(View.GONE);
			llmail.setVisibility(View.GONE);
		}
        pickFileButton = (Button) findViewById(R.id.pickcvbutton);

        pickFileButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	if ( isMediaProviderSupported()) {
		    		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		    	    intent.addCategory(Intent.CATEGORY_OPENABLE);
		    	    intent.setType("*/*");
		    	    startActivityForResult(intent, PICKFILE_RESULT_CODENEW);
		    	}
		    	else{
		    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		        intent.setType("file/*");
		        intent.addCategory(Intent.CATEGORY_OPENABLE);

		        // special intent for Samsung file manager
		        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
		         // if you want any file type, you can skip next line
		        sIntent.putExtra("CONTENT_TYPE", "*/*");
		        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

		        Intent chooserIntent;
		        if (getPackageManager().resolveActivity(sIntent, 0) != null){
		            // it is device with samsung file manager
		            chooserIntent = Intent.createChooser(sIntent, "Open file");
					Parcelable[] parcelableArr = new Intent[RegistrationActivity.PICKFILE_RESULT_CODE];
					parcelableArr[0] = intent;
					chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", parcelableArr);
		        }
		        else {
		            chooserIntent = Intent.createChooser(intent, "Open file");
		        }

		        try {
		            startActivityForResult(chooserIntent, PICKFILE_RESULT_CODE);
		        } catch (android.content.ActivityNotFoundException ex) {
		            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
		        }
		    	}
		    	    // Filesystem.
//		    	    final Intent galleryIntent = new Intent();
//		    	    galleryIntent.setType("*/*");
//		    	    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		    	    // Chooser of filesystem options.
		    	    //final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		    	    // Add the camera options.
//		    	    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

//		    	    startActivityForResult(galleryIntent, PICKFILE_RESULT_CODE);
		    	}
		});

		Button confirmButton = (Button) findViewById(R.id.insertbutton);

		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	if( name.getText().toString().length()<2){
		    		scroller.fullScroll(ScrollView.FOCUS_UP);
		    		name.requestFocus();
		    		confirm(RegistrationActivity.this,R.string.noinput3);
		    	}
		    	else if(lastname.getText().toString().length()<2 ){
		    		scroller.fullScroll(ScrollView.FOCUS_UP);
		    		lastname.requestFocus();
		    		confirm(RegistrationActivity.this,R.string.noinput3);
		    	}
		    	else if(!emailValidator(mail.getText().toString())){
		    		scroller.fullScroll(ScrollView.FOCUS_UP);
		    		mail.requestFocus();
		    		confirm(RegistrationActivity.this,R.string.wrongmail);

		    	}
				else if(phone.getText().toString().length()<1) {
					scroller.fullScroll(ScrollView.FOCUS_DOWN);
					phone.requestFocus();
					confirm(RegistrationActivity.this,R.string.noinput3);
				}
		    	else if(password.getText().toString().length()<5 && !updateOnly){
		    		scroller.fullScroll(ScrollView.FOCUS_UP);
		    		password.requestFocus();
		    		confirm(RegistrationActivity.this,R.string.noinputpassword);
		    	}
		    	else if(!phoneValidator(phone.getText().toString()) ){
		    		scroller.fullScroll(ScrollView.FOCUS_DOWN);
		    		phone.requestFocus();
		    		confirm(RegistrationActivity.this,R.string.wrongphone);

		    	}


		    	else if(msg.getText().toString().length()<150 && !fileUploaded && lifestory.length()<1){
		    		if(!cvFileRequired){
			    		scroller.fullScroll(ScrollView.FOCUS_DOWN);
		    			msg.requestFocus();
		    		}
		    		confirm(RegistrationActivity.this,R.string.error,R.string.noinputcv);
		    	}
		    	/*else if(!termsofuse.isChecked()){
			    	scroller.fullScroll(ScrollView.FOCUS_DOWN);
		    		confirm(RegistrationActivity.this,R.string.error,R.string.pleaseapproveterms);
		    	}*/
		    	else {
		    		/*SharedPreferences.Editor editor = settings.edit();
					if(termsofuse.isChecked()){
						editor.putString(Constants.TAKANON, "1");
					}
		 			else{
						editor.putString(Constants.TAKANON, "0");
					}
		 			editor.commit();*/

		    		String usercode=settings.getString(Constants.USERCODE, "0");
		    		String userid=settings.getString(Constants.USERID, "0");
			        String registrationId = settings.getString(Constants.PROPERTY_REG_ID, "");
					String termsofuseval = termsofuse.isChecked()? "1" : "0";
					String acceptmailval = acceptmail.isChecked()? "1" : "0";
					String[] str1={Constants.baseUrl+version+Constants.urlCommandRegisterNative,Constants.SiteID,Integer.toString(index),Constants.MY_DESCRIPTION,msg.getText().toString(),Constants.FIRST_NAME,name.getText().toString(),Constants.LAST_NAME,lastname.getText().toString(),Constants.PHONE,phone.getText().toString(),Constants.EMAIL,mail.getText().toString(),Constants.USERCODE,usercode,Constants.USERID,userid,Constants.PASSWORD,password.getText().toString(),Constants.TOKEN,registrationId,Constants.TAKANON,termsofuseval,Constants.TAKANON2,acceptmailval};
		    		RegisterNativeTask RegisterTask = new RegisterNativeTask();
		    		RegisterTask.execute(str1);
		    	}
		    }
		});

	}
	@SuppressLint({"NewApi"})
	private boolean isMediaProviderSupported() {
		if (Build.VERSION.SDK_INT  < 19) {
			return false;
		}
		for (ResolveInfo info : getPackageManager().queryIntentContentProviders(new Intent("android.content.action.DOCUMENTS_PROVIDER"), 0)) {
			if (!(info == null || info.providerInfo == null)) {
				if (isMediaDocumentProvider(Uri.parse("content://" + info.providerInfo.authority))) {
					return true;
				}
			}
		}
		return false;
	}

	  private static boolean isMediaDocumentProvider(final Uri uri)
	    {
	        return "com.android.providers.media.documents".equals(uri.getAuthority());
	    }

	@SuppressLint({"NewApi"})
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != -1 || data == null) {
			confirm(this, R.string.cvnotloaded);
			this.fileUploaded = false;
			return;
		}
		String action = data.getScheme();
		Cursor cursor;
		if (action.startsWith("content")) {
			this.selectedImageUri = data.getData();
			String[] separated = getContentResolver().getType(this.selectedImageUri).split("/");
			String extension = separated[separated.length - 1];
			this.fileUploaded = true;
			cursor = getContentResolver().query(this.selectedImageUri, null, null, null, null, null);
			if (cursor != null) {
				try {
					if (cursor.moveToFirst()) {
						this.filePath = cursor.getString(cursor.getColumnIndex("_display_name")) + "." + extension;
					}
				} catch (Throwable th) {
					cursor.close();
					this.mode = true;
				}
			}
			cursor.close();
			this.mode = true;
			return;
		}
		boolean isCamera;
		this.mode = false;
		if (data == null) {
			isCamera = true;
		} else if (action == null) {
			isCamera = false;
		} else {
			isCamera = action.equals("android.media.action.IMAGE_CAPTURE");
		}
		if (isCamera) {
			this.selectedImageUri = this.outputFileUri;
			this.filePath = this.outputFileUri.getPath();
		} else {
			this.selectedImageUri = data == null ? null : data.getData();
			String scheme = this.selectedImageUri.getScheme();
			if (scheme.equals("file")) {
				this.filePath = this.selectedImageUri.getPath();
				this.registrationfilename.setText(getString(R.string.selectedfile) + " " + this.selectedImageUri.getLastPathSegment());
			} else {
				if (scheme.equals("content")) {
					String[] proj = new String[PICKFILE_RESULT_CODE];
					proj[0] = "_data";
					cursor = getContentResolver().query(this.selectedImageUri, proj, null, null, null);
					try {
						int column_index = cursor.getColumnIndexOrThrow("_data");
						cursor.moveToFirst();
						this.filePath = cursor.getString(column_index);
						if (cursor != null) {
							cursor.close();
						}
					} catch (Throwable th2) {
						if (cursor != null) {
							cursor.close();
						}
					}
				}
			}
		}
		this.fileUploaded = true;
	}


	protected void taskResponse(JSONObject json,int responseMode) {
		int res= json.optInt(Constants.STATUS, -1);
		if(responseMode==-3 && res==2){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.error);
			builder.setMessage(R.string.useralreadyexist);
			builder.setNegativeButton(R.string.signin, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	Intent returnIntent = new Intent();
	        	setResult(7,returnIntent);
	             finish();
	        }
			});
			builder.setPositiveButton(R.string.newuser, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        	dialog.dismiss();
		        }
				});
			builder.show();
		}

	}
	private class RegisterNativeTask extends AsyncTask<String, String, JSONObject> {
		private final ProgressDialog mProgressDialog2;

		private RegisterNativeTask() {
			this.mProgressDialog2 = new ProgressDialog(RegistrationActivity.this);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			this.mProgressDialog2.setMessage(RegistrationActivity.this.getResources().getString(R.string.sendingnow));
			this.mProgressDialog2.show();
		}

		protected JSONObject doInBackground(String... url) {
			try {
				if (RegistrationActivity.this.fileUploaded && RegistrationActivity.this.mode) {
					return new JSONObject(Downloader.downloadPostObject(url, RegistrationActivity.this.selectedImageUri, RegistrationActivity.this.getContentResolver(), RegistrationActivity.this.filePath));
				} else if (!RegistrationActivity.this.fileUploaded || RegistrationActivity.this.filePath == null || RegistrationActivity.this.filePath.length() <= 0) {
					return new JSONObject(Downloader.downloadPostObject(url));
				} else {
					return new JSONObject(Downloader.downloadPostObject(url, RegistrationActivity.this.filePath));
				}
			} catch (Exception e) {
				return null;
			}
		}

		protected void onPostExecute(JSONObject json) {
			if (this.mProgressDialog2 != null) {
				this.mProgressDialog2.dismiss();
			}
			if (json == null) {
				RegistrationActivity.this.confirm(RegistrationActivity.this, R.string.netproblem);
				return;
			}
			int res = json.optInt(Constants.STATUS, -1);
			String msg = json.optString(Constants.RESULT, json.optString(Constants.ERROR, RegistrationActivity.this.getString(R.string.serverproblem)));
			if (res == -1) {
				RegistrationActivity.this.confirm(RegistrationActivity.this, (int) R.string.serverproblem, msg);
			} else if (res == 2) {
				RegistrationActivity.this.confirmFinish(RegistrationActivity.this, R.string.error, msg);
			} else if (res != RegistrationActivity.PICKFILE_RESULT_CODE) {
				RegistrationActivity.this.confirm(RegistrationActivity.this, (int) R.string.error, msg);
			} else {
				RegistrationActivity.this.optAndSave(json);
				if (RegistrationActivity.this.index > 0) {
					RegistrationActivity.this.confirmFinish(RegistrationActivity.this, R.string.cvsent);
				} else if (RegistrationActivity.this.updateOnly) {
					RegistrationActivity.this.confirmFinish(RegistrationActivity.this, R.string.updatedscussefully);
				} else {
					RegistrationActivity.this.confirmFinish(RegistrationActivity.this, R.string.signsuccessfull);
				}
			}
		}
	}



}
