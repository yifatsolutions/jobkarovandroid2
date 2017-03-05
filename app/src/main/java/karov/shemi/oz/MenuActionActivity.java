package karov.shemi.oz;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.data;


public class MenuActionActivity extends Activity {
		protected ActionBar actionBar;
		protected TextView tvTitle;
		protected SharedPreferences settings;
		protected ActionBarDrawerToggle mDrawerToggle;
		protected String[] multiSelected={"0","0"};
		protected DrawerLayout mDrawerLayout;
		protected ExpandableListView mDrawerList;
		protected int index;
		protected String WelcomeMsg,version,usercode,userid;
		protected void taskResponse(JSONObject json,int responseMode) {
    		optAndSave(json);
			if(responseMode==-1){
		    	int res= json.optInt(Constants.STATUS, -1);
	        	if(res==1){
	        		confirm(this,R.string.cvsent);
	        	}
	        	else if(res==2){
	        		Editor edit= settings.edit();
	        		edit.putString(Constants.PASSWORD, "");
	        		edit.apply();
	        		Intent intent3=new Intent(this, RegistrationActivity.class);
	        		intent3.putExtra(Constants.ID, index);
	        		startActivity(intent3);	  
	        	}
	        	else {
	        		Editor edit= settings.edit();
	        		edit.putString(Constants.PASSWORD, "");
	        		edit.apply();
	        	}
	        }
			else if(responseMode==-2){
				String first_name=json.optString(Constants.FIRST_NAME, "");
        		String last_name=json.optString(Constants.LAST_NAME, "");
        		String WelcomeMsg=first_name+" "+last_name;
//        		optAndSave(json);
        		String facebook=settings.getString(Constants.FACEBOOKNAME, "");	
        		confirm(MenuActionActivity.this,R.string.welcome);
        		showWelcome(WelcomeMsg,facebook);
			}
	    }
		private void defineSideList(){
	        String[] mPlanetTitles0 = getResources().getStringArray(R.array.sidemenuitems0);
	        String[] mPlanetTitles1 = getResources().getStringArray(R.array.sidemenuitems1);
	        String[] mPlanetTitles2 = getResources().getStringArray(R.array.sidemenuitems2);
	        String[] mPlanetTitles3 = getResources().getStringArray(R.array.sidemenuitems3);
	        String[] mPlanetTitles4 = getResources().getStringArray(R.array.sidemenuitems4);
	        String[] mPlanetGroups = getResources().getStringArray(R.array.sidemenugroups);
  		    String useremail=settings.getString(Constants.EMAIL, "");
//  		    usercode=settings.getString(Constants.USERCODE, "");
//  		    userid=settings.getString(Constants.USERID, "");
  		    if(userid.length()>0 && usercode.length()>0 && emailValidator(useremail)){
  		    	mPlanetGroups = getResources().getStringArray(R.array.sidemenugroupsloged);
  		    	mPlanetTitles1 = getResources().getStringArray(R.array.sidemenuitems1loged);
  		    }
  		    List<String> listDataHeader= new ArrayList<String>(Arrays.asList(mPlanetGroups));
	        HashMap<String, List<String>> listDataChild= new HashMap<String, List<String>>();
	        listDataChild.put(mPlanetGroups[0], (Arrays.asList(mPlanetTitles0)));
	        listDataChild.put(mPlanetGroups[1], (Arrays.asList(mPlanetTitles1)));
	        listDataChild.put(mPlanetGroups[2], (Arrays.asList(mPlanetTitles2)));
	        listDataChild.put(mPlanetGroups[3], (Arrays.asList(mPlanetTitles3)));
	        listDataChild.put(mPlanetGroups[4], (Arrays.asList(mPlanetTitles4)));
	        if(mDrawerList!=null) {
	        	mDrawerList.setAdapter(new karov.shemi.oz.ExpandableListAdapter(this, listDataHeader, listDataChild));
	        	for (int i = 0; i < mPlanetGroups.length; i++) {
					if(mPlanetGroups[i].length()<2)mDrawerList.expandGroup(i);
				}
	        	
	        }
		}
		public void onResume(){
			super.onResume();
			defineSideList();
		}
		public void onCreateSuper(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			usercode=settings.getString(Constants.USERCODE, "");
			userid=settings.getString(Constants.USERID, "");
			
			version=getVersion();
			actionBar = getActionBar();
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    		if(mDrawerLayout!=null){
    			mDrawerList = (ExpandableListView) findViewById(R.id.left_drawer);
//    			actionBar.setDisplayHomeAsUpEnabled(true);
//    			actionBar.setHomeButtonEnabled(true);
    			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
//    			actionBar.setIcon(android.R.color.transparent);
    			mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_menu,R.string.drawer_open,R.string.drawer_close) {
  		            public void onDrawerClosed(View view) {
  		            }
  		            public void onDrawerOpened(View drawerView) {
  		            }
  		        }; 
  		        mDrawerLayout.setDrawerListener(mDrawerToggle);
  		        //defineSideList();
  		        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
  		        	@Override
  		        	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
  		        		mDrawerList.setItemChecked(childPosition, true);
  		        		mDrawerLayout.closeDrawer(mDrawerList);
  		        		selectItem(groupPosition,childPosition);
  		        		 EasyTracker easyTracker = EasyTracker.getInstance(MenuActionActivity.this);
  		    		   easyTracker.send(MapBuilder.createEvent("ui_action","button_press","results screen left drawer"+v.toString(),(long)(groupPosition*100+childPosition)).build());
  		   			
  		                return true;
  		            }
  		        });
    		}
    		else{
    			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP| ActionBar.DISPLAY_SHOW_TITLE| ActionBar.DISPLAY_USE_LOGO);
    		}
    		actionBar.setCustomView(R.layout.abs_layout);
    		((TextView)actionBar.getCustomView().findViewById(R.id.mytext)).setText(getTitle());
    		setTitle("");
//    		actionBar.setDisplayHomeAsUpEnabled(true);
//    		actionBar.setHomeButtonEnabled(true);  
		}    
		@Override
		public void onStart() {
			super.onStart();
	    	settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			EasyTracker easyTracker = EasyTracker.getInstance(this);
//			easyTracker.activityStop(this);
			easyTracker.set(Fields.SCREEN_NAME, this.getClass().getSimpleName());
			easyTracker.send(MapBuilder.createAppView().build());
		}
		@Override
		public void onStop() {
			super.onStop();
			EasyTracker.getInstance(this).activityStop(this);  // Add this method.
		}
		/*@Override
		public boolean onPrepareOptionsMenu(Menu menu) {
		    MenuItem item= menu.findItem(R.id.action_settings);
		    item.setVisible(false);
		    super.onPrepareOptionsMenu(menu);
		    return true;
		}*/
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.allresults_activity_menu, menu);
	        return true;//super.onCreateOptionsMenu(menu);
	    }
	    protected int indexOfArray(int[] array1, int element){
	    	int i = 0;
	    	for (; i < array1.length; i++) {
	    		if(array1[i]==element) break;
	    	}
	    	return i;
	    }

	    @Override
		 protected void onPostCreate(Bundle savedInstanceState) {
		     super.onPostCreate(savedInstanceState);
		     // Sync the toggle state after onRestoreInstanceState has occurred.
		    if(mDrawerToggle!=null) mDrawerToggle.syncState();
		 }

		 @Override
		 public void onConfigurationChanged(Configuration newConfig) {
		     super.onConfigurationChanged(newConfig);
		     if(mDrawerToggle!=null) mDrawerToggle.onConfigurationChanged(newConfig);
		 }
		
	    @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (mDrawerToggle!=null && mDrawerToggle.isDrawerIndicatorEnabled() &&
		            mDrawerToggle.onOptionsItemSelected(item)) {
		        return true;
		    }
	    	final AlertDialog.Builder builder;
	        switch (item.getItemId()) {
	        	
	        case android.R.id.home:
		    	this.finish();//NavUtils.navigateUpFromSameTask(this);
		        return true;
		    
	            
	            case R.id.NEW_ID:
	            	Intent inten =new Intent(this,SearchActivity.class);
	    	        //inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
	    	        startActivity(inten);
	    	    	break;
	         
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	        return true;
	    }
	    public void confirmAutoDismiss(Context context,int title, int msg,int seconds){
	    	final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(msg);  
	    	final AlertDialog alert = dialog.create();
	    	alert.show();
	    	final Handler handler  = new Handler();
	    	final Runnable runnable = new Runnable() {
	    	    @Override
	    	    public void run() {
	    	        if (alert.isShowing()) {
	    	            alert.dismiss();
	    	        }
	    	    }
	    	};

	    	alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
	    	    @Override
	    	    public void onDismiss(DialogInterface dialog) {
	    	        handler.removeCallbacks(runnable);
	    	    }
	    	});

	    	handler.postDelayed(runnable, 1000*seconds);
	    }
	    public void confirmAutoDismiss(Context context,int title, int seconds){
	    	final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle(title);  
	    	final AlertDialog alert = dialog.create();
	    	alert.show();
	    	final Handler handler  = new Handler();
	    	final Runnable runnable = new Runnable() {
	    	    @Override
	    	    public void run() {
	    	        if (alert.isShowing()) {
	    	            alert.dismiss();
	    	        }
	    	    }
	    	};

	    	alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
	    	    @Override
	    	    public void onDismiss(DialogInterface dialog) {
	    	        handler.removeCallbacks(runnable);
	    	    }
	    	});

	    	handler.postDelayed(runnable, 1000*seconds);
	    }
	public void confirm2buttons(Context context,int title, int msg,int positivebutton){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	             dialog.cancel();
	        }
	    });
		builder.setPositiveButton(positivebutton, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	             dialog.cancel();
	        }
	    });
		builder.show();
		
	}
	public void confirm(Context context,int title, int msg){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	             dialog.cancel();
	        }
	    });
		builder.show();
		
	}
	public void confirm(Context context,int title, String msg){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	             dialog.cancel();
	        }
	    });
		builder.show();
		
	}
	public void confirm(Context context,int title){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		//builder.setMessage(msg);
		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	             dialog.cancel();
	        }
	    });
		try{
			builder.show();
		}finally{}
		
	}
	public void confirmFinish(Context context,int title){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	Intent returnIntent = new Intent();
	        	setResult(Activity.RESULT_OK,returnIntent);
	             finish();
	        }
	    });
		builder.show();
		
	}
	public void confirmFinish(Context context,int title, String msg){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	Intent returnIntent = new Intent();
	        	setResult(Activity.RESULT_OK,returnIntent);
	             finish();
	        }
	    });
		builder.show();
	}
	protected void optAndSave(JSONObject json){
		SharedPreferences.Editor editor = settings.edit();
			Iterator<String> temp = json.keys();
			while (temp.hasNext()) {
				String key = temp.next();
				String value = json.optString(key,"");
				if(value.length()>0) editor.putString(key.toLowerCase(), value);
			}
		/*String first_name=json.optString(Constants.FIRST_NAME, "");
 		String last_name=json.optString(Constants.LAST_NAME, "");
 		WelcomeMsg=first_name+" "+last_name;
		if (WelcomeMsg.length()>0) editor.putString(Constants.NAME, WelcomeMsg);

 		String usercode= json.optString(Constants.USERCODE, "");
 		String userid= json.optString(Constants.USERID, "");
 		String email=json.optString(Constants.EMAIL, "");
 		String phone=json.optString(Constants.PHONE, "");
 		String lifestory=json.optString(Constants.LIFE_STORY, "");
 		//int cvexist=json.optInt(Constants.CVEXIST, -1);
		String facebookName=json.optString(Constants.FACEBOOKNAME, "");	
		String description=json.optString(Constants.MY_DESCRIPTION, "");	
		String takanon=json.optString(Constants.TAKANON, "");	

 		if (usercode.length()>0 && userid.length()>0){
 			editor.putString(Constants.USERCODE, usercode);
 			editor.putString(Constants.USERID, userid);
 			if(takanon.length()>0)editor.putString(Constants.TAKANON, takanon);

 			if(first_name.length()>0) editor.putString(Constants.FIRST_NAME, first_name);
 			if(last_name.length()>0) editor.putString(Constants.LAST_NAME, last_name);
 	 		editor.putString(Constants.EMAIL, email);
 	 		editor.putString(Constants.PHONE, phone);
 	 		if(facebookName.length()>0) editor.putString(Constants.FACEBOOKNAME, facebookName);
 	 		if(description.length()>0) editor.putString(Constants.MY_DESCRIPTION, description);
 	 		if(lifestory.length()>0) editor.putString(Constants.LIFE_STORY, lifestory);

 	 		//if(cvexist>-1) editor.putInt(Constants.CVEXIST, cvexist);

 		}*/
		editor.apply();
	}
	public boolean phoneValidator(String phone) {
		Pattern pattern,pattern2;
	    Matcher matcher,matcher2;
	    final String PHONE_PATTERN = "^0[2-4,8-9]{1}[0-9]{7}$";
	    final String PHONE_PATTERN2 = "^0[5,7]{1}[0-9]{8}$";
	    pattern = Pattern.compile(PHONE_PATTERN);
	    pattern2 = Pattern.compile(PHONE_PATTERN2);
	    matcher = pattern.matcher(phone);
	    matcher2 = pattern2.matcher(phone);
	    return (matcher.matches() ||matcher2.matches());
	}
	
	public boolean emailValidator(String email) {
	    Pattern pattern;
	    Matcher matcher;
	    if(email.endsWith("@jobkarov.com")) return false;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(email);
	    return matcher.matches();
	}
	
	
	
	protected void selectItem(int group,int position) {   
	    switch(group*100+position) {
	    
	    case 0:
//	    	Intent inten =new Intent(this,Main2Activity.class);
	    	Intent inten =new Intent(this,SearchActivity.class);
	        //inten.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);   
	        startActivity(inten);
	    	break;
	    
	    case 1:
	    	inten =new Intent(this,LastSearchesActivity.class);
	        startActivity(inten);
	    	break;
	    	
	    case 100:
	    	inten = new Intent(this,SigninActivity.class);// LoginMainActivity.class);
	    	inten.putExtra(Constants.SECONDTIME, true);
	    	startActivity(inten);
	    	//SignIn();
	    	break;
	    case 101:
	    	 inten=new Intent(this, RegistrationActivity.class);
			  inten.putExtra(Constants.UPDATE, 1);
			  startActivity(inten);
		break;
	    case 102:
	    	inten =new Intent(this,SmartAgent.class);
	    	//inten =new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.urlAgent));
			startActivity(inten);
			  break;
		
	    case 200:

			 inten = new Intent(this, ShowAll.class);
			 
			 inten.putExtra(Constants.FAV,2);
			 startActivity(inten);
	    	 break;
	    case 201:

				 inten = new Intent(this, ShowAll.class);		 
				 inten.putExtra(Constants.FAV,1);
				 startActivity(inten);
	       	 break;
	    case 202:
	    	sendfriend();
	    	break;
	    case 203:
	    	inten =new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.urlCompany));
	    	startActivity(inten);
		break;
	    
	    case 300:
	    	inten =new Intent(this,SearchHelpActivity.class);
	    	startActivity(inten);
	    	break;
	    case 301:
			 inten = new Intent(this,ContactUsActivity.class);  
			 startActivity(inten);
				 break;
	 case 302:
	 case 303:
	 case 304:
		 inten = new Intent(this,WebViewActivity.class);
		 inten.putExtra(Constants.DESC, position);
		 //inten.putExtra(Constants.LINK, "https://www.jobkarov.com/Home/Page/47");
		 startActivity(inten);
		 break;
	 case 305:
		 inten = new Intent(this,WebViewActivity.class);  
		 inten.putExtra(Constants.DESC, position);
		 inten.putExtra(Constants.LINK, "https://www.jobkarov.com/Home/Page/47");
		 startActivity(inten);
		 break;

	   /* case 103:
	    	inten = new Intent(this, ExplanationsActivity.class);
				 startActivity(inten);
	        break;  
	    case 104:
	  	  confirm(this, R.string.voiceexplain, R.string.explainshowresult);
	  	  break;
	    case 105:
	    	inten = new Intent(this,GeneralTextActivity.class);  
			 inten.putExtra(Constants.DESC, 1);
			 startActivity(inten);
	        break;*/   
	   
	    
	   
	   
	    case 400:
	    	//inten =new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.urlRegister));
	    	//startActivity(inten);
	    	inten=new Intent(this, RegistrationActivity.class);
			  inten.putExtra(Constants.ID, 0);
			  startActivity(inten);
		break;
	    case 401:
	    	facebooklike();
		break;
	    case 402:
	    	toGooglePlus();
		break;
	    case 404:
	    	rate();
		break;
	    case 403:
	    	toTheSite();
		break;
	    case 405:
	    	confirm(this, R.string.vision, R.string.visionexplain);
	    break;   
	}

	}
	private void toGooglePlus(){
		Intent inten =new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com"));
		startActivity(inten);
	}
	private void toTheSite(){
		Intent inten =new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.JobKarov.com"));
		startActivity(inten);
	}
	private void facebooklike(){
		//Intent intent=getFB();
		Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/JobKarov"));
		startActivity(intent);
	}

	private Intent getFB(){
		 try { 
			   getPackageManager().getPackageInfo("com.facebook.katana", 0); 
			   return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/362475767146819")); 
			 } catch (Exception e) { 
			   return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Appclose2me")); 
			 }
	}
	private void sendfriend(){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.apptoshare));
	    shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.googleplay));    
	    startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.chooseShare)));
	}
	private void rate(){
		final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
		final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

		if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
		{
		    startActivity(rateAppIntent);
		}
		else
		{
		    /* handle your error case */
		}
	}
	 public String getVersion() {
		 PackageInfo pInfo=null;
		 version="/uknkown/";
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				 version = "/"+pInfo.versionName+"/"; 
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return version;
	 }
	 protected void openWeb(String website,int title,int seconds,boolean showClosebButton){
		 final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.loading), true);
		 final AlertDialog.Builder dialog = new AlertDialog.Builder(this); 
		 dialog.setTitle(title);
		 WebView mWebview = new WebView(this);
		 mWebview.setWebChromeClient(new WebChromeClient());
		    mWebview.clearCache(true);
		    mWebview.clearHistory();
		    mWebview.getSettings().setJavaScriptEnabled(true);
		    mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		 mWebview.setWebViewClient(new WebViewClient() {
			 public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				 confirm(MenuActionActivity.this,R.string.error);
			 }
			 @Override
			 public void onPageStarted(WebView view, String url, Bitmap favicon){
				 pd.show();
			 }
			 @Override
			 public boolean shouldOverrideUrlLoading(WebView view, String url) {
				 view.loadUrl(url);
				 return true;
			 }
			 @Override
			 public void onPageFinished(WebView view, String url) {
				 pd.dismiss();
				 //String webUrl = mWebview.getUrl();
			 }
		 });
		 mWebview.loadUrl(website);
		 dialog.setView(mWebview);
		 if(showClosebButton) dialog.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			 @Override
			 public void onClick(DialogInterface dialog, int id) {
				 dialog.dismiss();
			 }	
		 });
		 final AlertDialog alert = dialog.create();
		 alert.show();
		 final Handler handler  = new Handler();
		 final Runnable runnable = new Runnable() {
			 @Override
			 public void run() {
				 if (alert.isShowing()) {
					 alert.dismiss();
				 }	
			 }
		 };
		 alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
			 @Override
			 public void onDismiss(DialogInterface dialog) {
				 handler.removeCallbacks(runnable);
			 }
		 });
		 handler.postDelayed(runnable, 1000*seconds);
	 }
	public void showWelcome(String msg,String facbookname){}	
	public class GenericDownload extends AsyncTask<String, String, JSONObject> {
		
	    private final ProgressDialog mProgressDialog = new ProgressDialog(MenuActionActivity.this);
	    protected int responseMode;
	    private boolean showProgressBar;
		protected   GenericDownload(int response,boolean showProgressBar){
			super();
			this.responseMode=response;
			this.showProgressBar=showProgressBar;
		}
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(showProgressBar){
	        	mProgressDialog.setMessage(getResources().getString(R.string.loading));
	        	mProgressDialog.show();
	        }
	    }
	    @Override
	    protected JSONObject doInBackground(String... url) {
	    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(url));
	    	String resultString= Downloader.downloadPostObject(url);
	    	JSONObject data = null;
	         try{
	             data =new JSONObject(resultString);
	         }
	         catch(Exception e){
	        	 if (BuildConfig.DEBUG) Log.i(Constants.TAG, "failed "+url[0]);
	         }
	         return data;
	    }
	   
	    protected void onPostExecute(JSONObject json) {
	    	if(mProgressDialog!=null && showProgressBar) mProgressDialog.dismiss();
	    	if (json==null) {
	    		confirm(MenuActionActivity.this, R.string.netproblem);
	    	}
	    	else {
	    		int res= json.optInt(Constants.STATUS, -1);	
	    		String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
	    		String msg= json.optString(Constants.RESULT, err);
	    		if(res==-1){	
	    			confirm(MenuActionActivity.this,R.string.serverproblem,msg);		        		
	    		}			    
	    		else if(res!=1 && res!=2 && res!=3){
	    			confirm(MenuActionActivity.this,R.string.error,msg);
	    		}		        		
	    		else {
	    			JSONObject conf= json.optJSONObject(Constants.CONF);
	    			if(conf!=null){
	    				JSONArray jsonArr=conf.optJSONArray(Constants.COLOR);
	    		        SharedPreferences.Editor editor = settings.edit();
	    				editor.putString(Constants.COLOR,  jsonArr.toString());
	    				JSONArray jsonMsgs=conf.optJSONArray(Constants.MESSAGES);
	    				if(jsonMsgs!=null && jsonMsgs.length()>0) {
	    					JSONObject jsonMsg=jsonMsgs.optJSONObject(0);
	    					if(jsonMsg!=null && (settings.getString(Constants.MESSAGES+jsonMsg.optString(Constants.ID, "1"), "").length()==0 )){//|| jsonMsg.optString(Constants.UNIQUE, "1").equalsIgnoreCase("0"))) {
	    						openWeb(Constants.URLMSG+jsonMsg.optString(Constants.ID, "1"), R.string.message,jsonMsg.optInt(Constants.TIMEOUT, 1000),jsonMsg.optBoolean(Constants.TYPE, true));
	    						editor.putString(Constants.MESSAGES+jsonMsg.optString(Constants.ID, "1"), jsonMsg.optString(Constants.ID, "1"));
	    					}
	    				}
	    				editor.apply();

	    			}
	    			taskResponse(json,this.responseMode);
	    		}
	    		}
	    }
	}
	
	}
	
	
