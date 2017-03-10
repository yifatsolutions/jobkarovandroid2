package karov.shemi.oz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static android.R.id.edit;

public class JobDetailsActivity extends MenuActionActivity{
	private String company,name,require,description,phone,mail,companyid,address,imagelink,area,fullTitle,companyDetails,link;
	private TextView tvname,tvcompany,tvdescription,tvrequire,tvarea,tvemail,tvarrivalTitle1,tvarrivalTitle2,tvarrivalTitle3;
	private int offset;
	private double x,y,myx,myy;
	private Button mapab,buttoncallnew,buttonsendcvnew;
    public static final int MAILCODE=1;
    public static final int VOICECODE=2;
    private ImageView pic;
    private boolean favorite,emailexist,cvfilerequired;
    private LocationListener locationListener1,locationListener2;
	private LocationManager locationManager;
	private boolean walking;
	private boolean bus;
	//private JSONArray ja;
	private CheckBox cbFavorites;
	private         String[] menuitems;

	
	@Override
	protected void onPause(){
		if ( Build.VERSION.SDK_INT < 23 ||
				(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

			locationManager.removeUpdates(locationListener1);
			locationManager.removeUpdates(locationListener2);
		}
		super.onPause();
	}
	public void onResume(){
        super.onResume();
		if ( Build.VERSION.SDK_INT < 23 ||
				(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, locationListener1);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 10, locationListener2);
		}
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        
        case R.id.item_voice:
      	  return voiceControll(true);  
    /*    case R.id.item_share:
        	  return share();
          case R.id.item_fav:
        	  if (favorite) item.setIcon(android.R.drawable.star_big_off);
        	  else item.setIcon(android.R.drawable.star_big_on);
        	  return addFavorite();*/
          default:
        	  return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
      /*  MenuItem item = menu.findItem(R.id.item_fav);  
        if (favorite) item.setIcon(android.R.drawable.star_big_on);
    	else item.setIcon(android.R.drawable.star_big_off);*/
	        return true;//super.onCreateOptionsMenu(menu);
    }
	public void onCreate(Bundle savedInstanceState) {
		  super.onCreateSuper(savedInstanceState);
	      setContentView(R.layout.resultslayout);
	      super.onCreate(savedInstanceState);
	      Bundle bundle = getIntent().getExtras();

	      locationListener1 = new locLis();
	      locationListener2 = new locLis();
	      locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if ( Build.VERSION.SDK_INT < 23 ||(
				ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {


			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener2);
		}
		calcXY();
	      company=bundle.getString(Constants.COMPANY,"");
	      name=bundle.getString(Constants.NAME,"");
	      imagelink=bundle.getString(Constants.PHOTO,"");
	      address=bundle.getString(Constants.ADDRESS,"");
	      if(address.length()==0 || address.equalsIgnoreCase("0")) {
	    	  address="";
	    	  area="";
	      }
	      x=bundle.getDouble(Constants.X,32.0);
	      y=bundle.getDouble(Constants.Y,32.0);
	      index = bundle.getInt(Constants.ID,0);
	      tvname = (TextView) findViewById(R.id.tvname);
	      tvcompany = (TextView) findViewById(R.id.tvcompany);
	      tvdescription = (TextView) findViewById(R.id.tvdetails);
	      tvrequire = (TextView) findViewById(R.id.tvrequires);
	      tvarea = (TextView) findViewById(R.id.tvarea);
	      tvemail = (TextView) findViewById(R.id.tvemail);
	      //tvMoreDetails = (TextView) findViewById(R.id.tvarrivaltime);
	      tvarrivalTitle1 = (TextView) findViewById(R.id.textViewArrival1);
	      tvarrivalTitle2 = (TextView) findViewById(R.id.textViewArrival2);
	      tvarrivalTitle3 = (TextView) findViewById(R.id.textViewArrival3);
	      pic= (ImageView)findViewById(R.id.openerImage);
	      cbFavorites=(CheckBox)findViewById(R.id.starresult);
	      cbFavorites.setOnClickListener(new OnClickListener() {
	    	  @Override
	    	  public void onClick(View v) {
	    		  addFavorite();
	    		  cbFavorites.setChecked(favorite);
	    	  }
	      });
		  //pointer = (settings.getInt(Constants.VAR,0))%Constants.VISITED_SIZE;
		  /*boolean heb =settings.getBoolean(Constants.HEBREW, true);
		  if (heb) {
			  tvdescription.setGravity(Gravity.LEFT);
			  tvrequire.setGravity(Gravity.LEFT);
			  tvemail.setGravity(Gravity.LEFT);	
		  }*/
		  tvname.setText(name);
		  tvcompany.setText(company);
		  String[] str1={Constants.baseUrl+version+Constants.urlCommand1+Integer.toString(index),Constants.USERCODE,usercode,Constants.USERID,userid};
		  GenericDownload downloadFile = new GenericDownload(1,true);
		  downloadFile.execute(str1);
		  NotesDbAdapter mDbHelper=new NotesDbAdapter(this);
		  mDbHelper.open();
		  favorite=mDbHelper.Exists(index,Constants.SEARCH+1);
		  cbFavorites.setChecked(favorite);
		  if (!mDbHelper.Exists(index,Constants.SEARCH+2)) {
			  mDbHelper.createNote(index,name,company,x,y,Constants.SEARCH+2,address,imagelink);
			  /*if(!mDbHelper.ExistsType(pointer+2)) mDbHelper.createNote(index,name,company,x,y,2+pointer,address,imagelink);
			  else mDbHelper.updateNote(index,name,company,x,y,2+pointer,address,imagelink);
			  SharedPreferences.Editor editor = settings.edit();
			  editor.putInt(Constants.VAR,pointer+1);
			  editor.commit();*/
			}  
	        mDbHelper.close();

	        buttonsendcvnew = (Button) findViewById(R.id.buttonsendcvnew);
	        buttonsendcvnew.setOnClickListener(new View.OnClickListener() {
		    	  public void onClick(View arg0) {
		    		  sendCV();
		    	  } 
		      });
	        buttoncallnew = (Button) findViewById(R.id.buttoncallnew);
        	buttoncallnew.setOnClickListener(new View.OnClickListener() {
		    	  public void onClick(View arg0) {
		    		  call(0);
		    	  } 
		      });
	        Button buttonmorefromcompany = (Button) findViewById(R.id.buttonmorefromcompany);
	        buttonmorefromcompany.setOnClickListener(new View.OnClickListener() {
		    	  public void onClick(View arg0) {
					  mTracker.send(new HitBuilders.EventBuilder()
							  .setCategory("ui_action" )
							  .setAction("button more from company")
							  .build());
		    		  Intent inten = new Intent(JobDetailsActivity.this, ResultsListActivity.class);
		    		  int[] selections={0,0,0,0,0};
		    		  inten.putExtra(Constants.VAR,selections);
		    		  inten.putExtra(Constants.ROLE, "0");
		    		  inten.putExtra(Constants.SIZE, "0");
		    		  inten.putExtra(Constants.TYPE,Constants.SEARCH);
		    		  inten.putExtra(Constants.X,myx);
		    		  inten.putExtra(Constants.Y,myy);
		    		  inten.putExtra(Constants.COMPANY,companyid);
		    		  startActivity(inten);
		    	  } 
		      });
	        Button buttonsharenew = (Button) findViewById(R.id.buttonsharenew);
	        buttonsharenew.setOnClickListener(new View.OnClickListener() {
		    	  public void onClick(View arg0) {
		    		  share();
		    	  } 
		      });
	      /*Button saveb = (Button) findViewById(R.id.buttonsave);
	      saveb.setOnClickListener(new View.OnClickListener() {
	    	  public void onClick(View arg0) {
	    		  addFavorite();
	    	  } 
	      });*/
	      Button contactCompany = (Button) findViewById(R.id.buttoncontact);
	      contactCompany.setOnClickListener(new View.OnClickListener() {
	    	  public void onClick(View arg0) {
	    		  showMyDialog(1);
	    	  } 
	      });
	      Button sendb = (Button) findViewById(R.id.buttoninfo);
	      sendb.setOnClickListener(new View.OnClickListener() {
	    	  public void onClick(View arg0) {
	    		  showMyDialog(2);
	    	  } 
	      });	          	
	    
	      mapab = (Button) findViewById(R.id.buttonmap);
	      if (address.equalsIgnoreCase("0")|| address.equalsIgnoreCase("")){
	    	  mapab.setVisibility(View.INVISIBLE);
	      }
	      mapab.setOnClickListener(new View.OnClickListener() {
	    	  public void onClick(View arg0) {
	    		  showMyDialog(0);
	    	  }
	      });
	}
	
	private void searchInfo(){
		if(companyDetails!=null && companyDetails.length()>3){
			AlertDialog.Builder alert = new AlertDialog.Builder(this); 
			alert.setTitle(R.string.commandinfo);
			alert.setMessage(companyDetails);
			alert.setNegativeButton(R.string.backtojob, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			alert.setPositiveButton(R.string.google, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();

					Intent inten = new Intent(JobDetailsActivity.this,WebViewActivity.class);
					inten.putExtra(Constants.LINK, "http://www.google.com/search?q="+company);
					inten.putExtra(Constants.TITLE,getString(R.string.commandinfo));
					startActivity(inten);
					//openWeb("http://www.google.com/search?q="+company,R.string.commandinfo,1000,true);
				}
			});
			if(link!=null && link.length()>3){
				alert.setNeutralButton(R.string.tocompanysite, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						Intent inten = new Intent(JobDetailsActivity.this,WebViewActivity.class);  
						 inten.putExtra(Constants.LINK, link);
						inten.putExtra(Constants.TITLE,getString(R.string.tocompanysite));
						startActivity(inten);
//						openWeb(link,R.string.commandinfo,1000,true);
					}
				});
			}
			alert.show();
		}
		else{
			openWeb("http://www.google.com/search?q="+company,R.string.commandinfo,1000,true);
		}
		/*Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		  intent.putExtra(SearchManager.QUERY, company); // query contains search string
		  startActivity(intent);*/
	}
	private void sendMail(){
		mail=settings.getString(Constants.MYEMAIL, UserEmailFetcher.getEmail(this));
		
		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
      	builder.setTitle(R.string.sendto);
      	builder.setMessage(mail);
      	builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                   dialog.cancel();
              }
              
          });
      	builder.setNeutralButton(R.string.anothermail, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              	dialog.cancel();
              	askForEmail();
              }});
      	builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
              	dialog.cancel();
              	sendmailnow();
              }});
              builder.show();
	}
	private void call(int mode){
		String[] strcount={Constants.baseUrl+version+Constants.urlCommandCount+"?"+Constants.TYPE+"=1&"+Constants.PLACE+"=2&"+Constants.ITEM+"="+Integer.toString(index),Constants.USERCODE,usercode,Constants.USERID,userid};
		GenericDownload sendcount = new GenericDownload(-3,true);
		sendcount.execute(strcount);
		
		String tmp="normal";
		if(mode==1)tmp="video";
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory("ui_action" )
				.setAction(tmp+" call")
				.build());
		String url = "tel:"+phone;
    	try{
			  Intent inten;
			  if(mode==0) inten= new Intent(Intent.ACTION_CALL, Uri.parse(url));
			  else {
				  inten=new Intent("com.android.phone.videocall");
				  inten.putExtra("videocall", true);
				  inten.setData(Uri.parse(url));
			  }
			  startActivity(inten);
		  }
		  catch (ActivityNotFoundException e)        {    
		         Log.e("shemi", "Call failed", e);    
		    }
	}
	private void sendCV(){
//		EasyTracker easyTracker = EasyTracker.getInstance(this);
//		easyTracker.send(MapBuilder.createEvent("ui_action","button_press","sendCV",null).build());
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory("ui_action" )
				.setAction("sendCV")
				.build());
		//String cvexist=settings.getString(Constants.CVEXIST, "");
		String useremail=settings.getString(Constants.EMAIL, "");
		if(userid.length()>0 && usercode.length()>0 && emailValidator(useremail)){
//			if(cvexist.length>0){
				String[] str1={Constants.baseUrl+version+Constants.urlCommand4+index,Constants.USERCODE,usercode,Constants.USERID,userid};
				GenericDownload sendcv = new GenericDownload(3,true);
				sendcv.execute(str1);
	/*		}
			else{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle( R.string.cvdoesntexist);
				builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent3=new Intent(JobDetailsActivity.this, RegistrationActivity.class);
						intent3.putExtra(Constants.ID, index);
						intent3.putExtra(Constants.REQUIRE_CV, cvfilerequired);
						startActivity(intent3);
					}
				}).show();
			}*/
		}
		else{
			Intent intent3=new Intent(this, SigninActivity.class);
			intent3.putExtra(Constants.ID, index);
			intent3.putExtra(Constants.REQUIRE_CV, cvfilerequired);
			startActivity(intent3);
		}
	}
	
	private void navigate(){
		Locale.setDefault(new Locale("iw","IL"));
        Intent inten = new Intent(android.content.Intent.ACTION_VIEW, 
        		//Uri.parse("geo:?q="+address+"&navigate=yes"));//Double.toString(x)+","+Double.toString(y)));//geo:?q=
        		Uri.parse("geo:0,0?q="+Double.toString(x)+","+Double.toString(y)+" ("+address+","+company+")"));
        Intent chooser = Intent.createChooser(inten, getResources().getString(R.string.navigateby));
	  startActivity(chooser);
	}
	private void streetView(){
		String uri="google.streetview:cbll="+ Double.toString(x)+","+Double.toString(y)+"&cbp=1,99.56,,1,-5.27&mz=21";
		Intent streetView = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(uri));
		startActivity(streetView);
	}
	private boolean addFavorite(){
		usercode=settings.getString(Constants.USERCODE, "");
		userid=settings.getString(Constants.USERID, "");
		
//		EasyTracker easyTracker = EasyTracker.getInstance(this);
		NotesDbAdapter mDbHelper=new NotesDbAdapter(JobDetailsActivity.this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(Constants.FAV, index);
		mDbHelper.open();
		if (favorite) {
			mDbHelper.deleteNote(index, Constants.SEARCH+1);
			mTracker.send(new HitBuilders.EventBuilder()
					.setCategory("ui_action" )
					.setAction("remove favorite")
					.build());
			//easyTracker.send(MapBuilder.createEvent("ui_action","button_press","remove favorite",(long)index).build());
			String[] strcount={Constants.baseUrl+getVersion()+Constants.FAV,Constants.ID,Integer.toString(index),Constants.USERCODE,usercode,Constants.USERID,userid,Constants.FAV,"0"};
			GenericDownload sendcount = new GenericDownload(-3,false);
			sendcount.execute(strcount);
			editor.putString(Constants.FAVMODE, Constants.NO);

		}else {
			String[] strcount={Constants.baseUrl+getVersion()+Constants.FAV,Constants.ID,Integer.toString(index),Constants.USERCODE,usercode,Constants.USERID,userid,Constants.FAV,"2"};
			  GenericDownload sendcount = new GenericDownload(-3,false);
			  sendcount.execute(strcount);
			mDbHelper.createNote(index,name,company,x,y,Constants.SEARCH+1,address,imagelink);
			confirmAutoDismiss(JobDetailsActivity.this,R.string.saved,2);
			//easyTracker.send(MapBuilder.createEvent("ui_action","button_press","add favorite",(long)index).build());
			mTracker.send(new HitBuilders.EventBuilder()
					.setCategory("ui_action" )
					.setAction("add favorite")
					.build());
			editor.putString(Constants.FAVMODE, Constants.YES);
		}
		mDbHelper.close();
		editor.commit();
		favorite=!favorite;
		return true;
	}
	private boolean share(){
		String[] strcount={Constants.baseUrl+version+Constants.urlCommandCount+"?"+Constants.TYPE+"=1&"+Constants.PLACE+"=7&"+"&"+Constants.ITEM+"="+Integer.toString(index),Constants.USERCODE,usercode,Constants.USERID,userid};
		  GenericDownload sendcount = new GenericDownload(-3,true);
		  sendcount.execute(strcount);
		
		  Intent intent = new Intent(Intent.ACTION_SEND);
		    intent.setType("text/plain");
		    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.jobShared));
		    intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shareprejobfix)+index);
		    startActivity(Intent.createChooser(intent, getResources().getString(R.string.chooseShare)));
		    return true;
	}
	public void confirmDontShow(Context context,int title, int msg){
		fullTitle=context.getResources().getString(title); 
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		WebView wv = new WebView(this);
		wv.loadUrl("file:///android_asset/4.html");
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl("file:///android_asset/2.html");
				return true;
			}
		});
		builder.setView(wv);
		//builder.setMessage(msg);
		builder.setNegativeButton(R.string.continuestr, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				voiceControll(false);
			}
		});
		builder.setPositiveButton(R.string.dontshowagain, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(fullTitle, true);
				editor.commit();
				dialog.cancel();
				voiceControll(false);
			}
		});
		builder.show();	    
	}
	private boolean voiceControll(boolean showMessage){
		boolean dontShowAgain =settings.getBoolean(fullTitle, false);
		if(!dontShowAgain && showMessage){
			confirmDontShow(this, R.string.titleshowresult, R.string.explainshowresult);
			return true;
	    }
	    else{
//	    	EasyTracker easyTracker = EasyTracker.getInstance(this);
//			easyTracker.send(MapBuilder.createEvent("ui_action","button_press","voice search job",null).build());
			mTracker.send(new HitBuilders.EventBuilder()
					.setCategory("ui_action" )
					.setAction("voice search job")
					.build());
	    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "he-IL");
	    	try {
	    		startActivityForResult(intent, VOICECODE);
	    	} catch (ActivityNotFoundException a) {
	    		confirm(this, R.string.cantunderstand);
	    		return false;
	    	}
	    	return true;
	    }
	}
	 private void openMap(){
		 if(checkIfMapsIsOk(this)){
		 Intent intent2 = new Intent(JobDetailsActivity.this, MapActivity.class);
		 String[] xxx=new String[1];
		 String[] yyy=new String[1];
		 String[] names=new String[1];
		 String[] companies=new String[1];
		 String[] cities=new String[1];
		 String[] pictures=new String[1];
		 String[] indexes=new String[1];
		 String[] addresses=new String[1];
		 indexes[0]=Integer.toString(index);

		 xxx[0]=Double.toString(x);
		 yyy[0]=Double.toString(y);
		 names[0]=name;
		 companies[0]=company;
		 addresses[0]=address;
		 cities[0]=area;
		 pictures[0]=imagelink;
		 intent2.putExtra(Constants.X, xxx);
		 intent2.putExtra(Constants.Y, yyy);
		 intent2.putExtra(Constants.NAME, names);
		 intent2.putExtra(Constants.COMPANY, companies);
		 intent2.putExtra(Constants.CITIES, cities);
		 intent2.putExtra(Constants.PHOTO, pictures);
		 intent2.putExtra(Constants.ID, indexes);
		 intent2.putExtra(Constants.ADDRESS, addresses);
		 startActivity(intent2);
		 }
	 }
	private void showMyDialog(int type){
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(R.string.selectAction);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(JobDetailsActivity.this,android.R.layout.select_dialog_item);
        if(type==0) {
        	menuitems=getResources().getStringArray(R.array.mapaSubMenu);
            builderSingle.setAdapter(arrayAdapter,new DialogInterface.OnClickListener() {
            	@Override
            	public void onClick(DialogInterface dialog, int which) {
//            		EasyTracker easyTracker = EasyTracker.getInstance(JobDetailsActivity.this);
//		    		  easyTracker.send(MapBuilder.createEvent("ui_action","button_press","button navigation"+menuitems[which],(long)index).build());
					mTracker.send(new HitBuilders.EventBuilder()
							.setCategory("ui_action" )
							.setAction("button navigation")
							.build());
            		//String strName = arrayAdapter.getItem(which);
            		switch (which) {
            		case 0:
            			openMap();
            			break;
            		case 1:
            			navigate();
            			break;
            		case 2:
            			streetView();
            			break;
            		default:
            			break;
            		}
            	}
            });
        }
        else if(type==1){
        	if (phone==null || phone.equalsIgnoreCase("0")||phone.equalsIgnoreCase("")){
        		menuitems=getResources().getStringArray(R.array.contactSubMenuShort);	
        	} 
        	else{
        		menuitems=getResources().getStringArray(R.array.contactSubMenu);
        	}
        	offset=0;
        	if(!emailexist) {
     			menuitems=Arrays.copyOfRange(menuitems, 1, menuitems.length);
     			offset=1;
     		}
        	builderSingle.setAdapter(arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        	EasyTracker easyTracker = EasyTracker.getInstance(JobDetailsActivity.this);
//      		    		  easyTracker.send(MapBuilder.createEvent("ui_action","button_press","button contact company"+menuitems[which],(long)index).build());
							mTracker.send(new HitBuilders.EventBuilder()
									.setCategory("ui_action" )
									.setAction("button contact company")
									.build());
                        	switch (which+offset) {
                    		case 0:
                    			sendCV();
                    		break;
                    		case 1:
                    			sendMail();
                        		break;
                    		case 2:
                    			call(0);
                        		break;
                    		case 3:
                    			call(1);
                        		break;
                    		case 4:
                    			sendSMS();
                    			break;
                        	}
                        }
   		});
        }
        else {
        	if(favorite) menuitems=getResources().getStringArray(R.array.infomenuitems2);
        	else menuitems=getResources().getStringArray(R.array.infomenuitems);
            builderSingle.setAdapter(arrayAdapter,new DialogInterface.OnClickListener() {
            	@Override
            	public void onClick(DialogInterface dialog, int which) {
//            		EasyTracker easyTracker = EasyTracker.getInstance(JobDetailsActivity.this);
//		    		  easyTracker.send(MapBuilder.createEvent("ui_action","button_press","button more"+menuitems[which],(long)index).build());
					mTracker.send(new HitBuilders.EventBuilder()
							.setCategory("ui_action" )
							.setAction("button more"+menuitems[which])
							.build());
            		//String strName = arrayAdapter.getItem(which);
            		switch (which) {
            		case 1:
            			searchInfo();
            			break;
            		case 2:
            			share();
            			break;
            		case 0:
            			cbFavorites.setChecked(!favorite);
            			addFavorite();
            			break;
            		case 3:
            			openPictures(0);
            			break;
            		case 4:
            			openPictures(1);
            			break;
            		default:
            			break;
            		}
            	}
            });
        }
        for (int i = 0; i < menuitems.length; i++) {
        	String item = menuitems[i];
        	arrayAdapter.add(item);
        }
        builderSingle.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
        	}
        });
        builderSingle.show();
	}
	
	public void askForEmail() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.insertemail);
		//alert.setMessage("Message");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		alert.setView(input);
		alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mail= input.getText().toString().trim();
				if(emailValidator(mail)) {
					Editor edit = settings.edit();
					edit.putString(Constants.MYEMAIL, mail);
					edit.commit();
					sendmailnow();
				}
				else{
					confirm(JobDetailsActivity.this,R.string.wrongmail);
				}
			}
		});

		alert.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  dialog.cancel();
		  }
		});
		AlertDialog dialog = alert.create();
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}
	private void sendSMS(){
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory("ui_action" )
				.setAction("sendSMS")
				.build());

		  String[] strcount={Constants.baseUrl+version+Constants.urlCommandCount+"?"+Constants.TYPE+"=1&"+Constants.PLACE+"=3&"+"&"+Constants.ITEM+"="+Integer.toString(index),Constants.USERCODE,usercode,Constants.USERID,userid};
		  GenericDownload sendcount = new GenericDownload(-3,true);
		  sendcount.execute(strcount);
		
		Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + phone ) ); 
		intent.putExtra( "sms_body", getResources().getString(R.string.askdetailsaboutjob2)+name+", "+area+"\n "+link+getResources().getString(R.string.askdetailsaboutjob) ); 
		startActivity( intent ); 
	}
	public void sendmailnow(){
		mTracker.send(new HitBuilders.EventBuilder()
				.setCategory("ui_action" )
				.setAction("sendToMail")
				.build());
		String[] str1={Constants.baseUrl+version+Constants.urlCommand2+mail+"&"+Constants.ID+"="+index,Constants.USERCODE,usercode,Constants.USERID,userid};
    	 GenericDownload sendmail = new GenericDownload(2,true);
    	 sendmail.execute(str1);
	}
	
	public void openPictures(int mode){
		 Intent intent2;
		 if(mode>0) intent2= new Intent(JobDetailsActivity.this,ImagePagerView.class);
		 else intent2= new Intent(JobDetailsActivity.this,GridViewActivity.class);
		 String[] imageHeaders=new String[20];
		 for (int i = 0; i < imageHeaders.length; i++) {
			 imageHeaders[i]=Constants.IMAGE_URL+companyid+"_"+i+Constants.IMAGE_SURRFIX;
		 }
		 intent2.putExtra(Constants.PHOTO, imageHeaders);
		 intent2.putExtra(Constants.SIZE, 20);
		 startActivity(intent2);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==RESULT_OK && null != data){
			
			switch (requestCode) {
			case VOICECODE:
				 ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				 String command= text.get(0);
				 if(phone.length()>2 && command.contains(getResources().getString(R.string.commandvideocall))){
					 call(1);
				 }
				 else if(phone.length()>2 && (command.startsWith(getResources().getString(R.string.commandcall1))|| command.startsWith(getResources().getString(R.string.commandcall2)))){
					 call(0);
				 }
				 else if(command.startsWith(getResources().getString(R.string.commandsendtomail1))){
					 //mail=UserEmailFetcher.getEmail(DetailsActivity.this);
					 sendMail();
				 }
				 else if(command.startsWith(getResources().getString(R.string.commandsendcv))){
					 sendCV();
				 }
				 else if(address.length()>0 && command.startsWith(getResources().getString(R.string.commandstreet))){
					 streetView();
				 }
				 else if(address.length()>0 &&  
						 (command.startsWith(getResources().getString(R.string.commandnavigate))
								 || command.startsWith(getResources().getString(R.string.commandnavigate2)) 
								 || command.startsWith(getResources().getString(R.string.commandnavigate3))
								 || command.startsWith(getResources().getString(R.string.commandnavigate4))
								 || command.contains(getResources().getString(R.string.commandnavigate5))
								 || command.startsWith(getResources().getString(R.string.commandnavigate6)))){
					 navigate();
				 }
				 else if(phone.length()>2 && (command.startsWith(getResources().getString(R.string.commandsms1))|| command.startsWith(getResources().getString(R.string.commandsms2)))){
					 sendSMS();
				 }
				 else if(address.length()>0 && command.startsWith(getResources().getString(R.string.commandmapa))){
					 openMap();
				 }
				 
				 else if(command.startsWith(getResources().getString(R.string.commandinfo))){
					 searchInfo();
				 }
				 else{
					 confirm(this,R.string.error,getString(R.string.wordwasntfind)+" "+command);
				 }
				break;
			case MAILCODE:
				Bundle extras = data.getExtras();
				mail=extras.getString(Constants.VAR);
				sendmailnow();
				
				break;
			
			}
			}
	}
	protected void taskResponse(JSONObject json,int responseMode) {
		if(responseMode==1){
			JSONArray allJobs=json.optJSONArray("list");
			JSONObject ja = allJobs.optJSONObject(0);
			index= ja.optInt(Constants.ID,0);
			name= ja.optString(Constants.NAME,"");
			company=ja.optString(Constants.COMPANYNAME, "");
			companyDetails=ja.optString(Constants.COMPANY_DESCRIPTION, "");
			link=ja.optString(Constants.COMPANY_WEBSITE,"");
			description= ja.optString(Constants.DESCRIPTION, "");
			require= ja.optString(Constants.REQUIRE, "");
			phone= ja.optString(Constants.PHONE, "");
			if(phone!=null && phone.length()>0 && !phone.substring(0, 1).equalsIgnoreCase("1") && !phone.substring(0, 1).equalsIgnoreCase("0") && !phone.substring(0, 1).equalsIgnoreCase("*")) phone="0"+phone;
			if (phone.equalsIgnoreCase("0")||phone.equalsIgnoreCase("")) buttoncallnew.setVisibility(View.GONE);
			area=ja.optString(Constants.AREA_NAME, "");
			address=ja.optString(Constants.ADDRESS,"");
			companyid= ja.optString(Constants.COMPANYID,"");
			if(ja.optString(Constants.LOGO, "").length()>0) imagelink=Constants.IMAGE_URL+(ja.optInt(Constants.LOGO, 0)/1000)+"/"+ja.optInt(Constants.LOGO, 0)+Constants.IMAGE_SURRFIX;//+ja.getString(17)+"/"+ja.getString(18);
			else imagelink="";
			if(ja.optInt(Constants.MANPOWER,0)==1) address="";
			emailexist= ja.optString(Constants.EMAIL, "").equalsIgnoreCase("1");
			if(!emailexist) buttonsendcvnew.setVisibility(View.GONE);
			cvfilerequired= ja.optString(Constants.REQUIRE_CV,"").equalsIgnoreCase("1");
			if (address.equalsIgnoreCase("0")|| address.equalsIgnoreCase("")){
				mapab.setVisibility(View.INVISIBLE);
			}
			else mapab.setVisibility(View.VISIBLE);
			x=ja.optDouble(Constants.X, 0.0);
			y=ja.optDouble(Constants.Y, 0.0);
			String[] url1 = {"https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin=" + myx + "," + myy
					+ "&destination=" + x + "," + y+"&key="+Constants.API_KEY_DIRECTIONS
					+ "&sensor=false&units=metric&language=he&mode=driving"};
			String[] url2 = {"https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin=" + myx + "," + myy
					+ "&destination=" + x + "," + y+"&key="+Constants.API_KEY_DIRECTIONS
					+ "&sensor=false&units=metric&language=he&mode=walking"};
			String[] url3 = {"https://maps.googleapis.com/maps/api/directions/json?"
					+ "origin=" + myx + "," + myy
					+ "&destination=" + x + "," + y+"&key="+Constants.API_KEY_DIRECTIONS
					+ "&sensor=false&units=metric&language=he&mode=transit&departure_time="+(System.currentTimeMillis()/1000)};
			walking=false;
			bus=false;
			DownloadTask downloadTask = new DownloadTask();
			DownloadTask downloadTask2 = new DownloadTask();
			DownloadTask downloadTask3 = new DownloadTask();
			downloadTask.execute(url1);
			downloadTask2.execute(url2);
			downloadTask3.execute(url3);          	
			//if(address.length()>0) {
				//if(area.length()>0 /*&& (address.length()+area.length())<25*/) tvarea.setText(area+" , "+address);
				//else
			tvarea.setText(address);
			//}
			tvname.setText(name);
			tvcompany.setText(company);
			description=description.replace(";", "\n");
			tvdescription.setText(description);
			if (require.length()>1) {
				require=require.replace(";", "\n");
				tvrequire.setText(require);
			}	
			else{
				TextView tvrequiretitle = (TextView) findViewById(R.id.tvrequirestitle);
				tvrequiretitle.setVisibility(View.GONE);
				tvrequire.setVisibility(View.GONE);
			}
			try{
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(imagelink, pic);
			}catch(Exception e){} 
		
		}
		else if(responseMode==2) confirm(JobDetailsActivity.this,R.string.sentsuccesfull,mail);
		else if(responseMode==3){
			int res= json.optInt(Constants.STATUS, -1);	
    		String err= json.optString(Constants.ERROR, getString(R.string.serverproblem));
    		String msg= json.optString(Constants.RESULT, err);
    		if(res==1){	
    			confirm(JobDetailsActivity.this,R.string.cvsent);		        		
    		}
    		else if(res==2){
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle( R.string.cvdoesntexist);
				builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent3=new Intent(JobDetailsActivity.this, RegistrationActivity.class);
						intent3.putExtra(Constants.ID, index);
						intent3.putExtra(Constants.REQUIRE_CV, cvfilerequired);
						startActivity(intent3);
					}
				}).show();
    		}
		}
		else super.taskResponse(json, responseMode);
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
private boolean calcXY(){
	  myx=0;
	  myy=0;
	  Location net_loc=null, gps_loc=null;
	try{
	  gps_loc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	  net_loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	} catch (SecurityException e) {}
	  //if there are both values use the latest one
	  if(gps_loc!=null && net_loc!=null){
		  if(gps_loc.getTime()>net_loc.getTime()){   	
			  myx=gps_loc.getLatitude();
			  myy=gps_loc.getLongitude();
		  }
		  else{
			  myx=net_loc.getLatitude();
			  myy=net_loc.getLongitude();
		  }
	  }
	  else{
		  if(gps_loc!=null){
			  myx=gps_loc.getLatitude();
			  myy=gps_loc.getLongitude();
		  }
		  if(net_loc!=null){
			  myx=net_loc.getLatitude();
			  myy=net_loc.getLongitude();
		  }
	  }
	  return ((gps_loc!=null) || (net_loc!=null));
}
private class DownloadTask extends AsyncTask<String, Void, String>{
    @Override
    protected String doInBackground(String... url) {
   	String data = null;
        try{
            data =Downloader.downloadPostObject(url);
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }
        return data;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        DirectionsJSONParser parser = new DirectionsJSONParser();
        JSONObject data = null;
        try{
            data =new JSONObject(result);
            String routes = parser.parse(data);
            routes=routes.replace(getString(R.string.onehour), getString(R.string.hour))
					.replace(getString(R.string.oneminute), getString(R.string.minute))
					.replace(getString(R.string.twohour), getString(R.string.twohours))
					.replace(getString(R.string.hours), getString(R.string.hoursafter));
            if(result.contains("BUS") && !bus) {
            	bus=true;
            	tvarrivalTitle2.setText(routes);
            }
            else if(result.contains("DRIVING")) {
            	tvarrivalTitle3.setText(routes);
            }
            else if(result.contains("WALKING") &&!walking) {
            	tvarrivalTitle1.setText(routes);
            	walking=true;
            }
             
            
        }catch(Exception e){
            Log.d("Background Task",e.toString());
        }
        
        
    }
}

}