package karov.shemi.oz;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.Arrays;

public class ConfigActivity extends SearchActivity {
	 
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
 
  
    @Override
    protected void readSaved(){
    	config();
    }
    @Override
    protected void config(){
    	selections[0]= Integer.valueOf(settings.getString(Constants.SPECIALITY,"0"));
    	selections[3]= Integer.valueOf(settings.getString(Constants.AREA+"w","0"));
    	selections[4]= Integer.valueOf(settings.getString(Constants.CITIES+"w","0"));
		myx=Double.valueOf(settings.getString(Constants.X, "0.0"));
		myy=Double.valueOf(settings.getString(Constants.Y, "0.0"));
		if(myx==0 && myy==0) updateAddressByGPS=true;
		else updateAddressByGPS=false;
		changeAddress.setText(settings.getString(Constants.ADDRESS, ""));
		multiSelected[0]=settings.getString(Constants.ROLE,"0");
		multiSelected[1]=settings.getString(Constants.SIZE,"0");
		 if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+Arrays.toString(selections)+" "+Arrays.toString(multiSelected));

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mainnav);
        Button calc = (Button) findViewById(R.id.calculate);
        calc.setText(R.string.save);
        actionBar.hide();
        assignAppWidgetId();
    }
 
    /**
     * Widget configuration activity,always receives appwidget Id appWidget Id =
     * unique id that identifies your widget analogy : same as setting view id
     * via @+id/viewname on layout but appwidget id is assigned by the system
     * itself
     */
    private void assignAppWidgetId() {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    protected void calcAction(){
		SharedPreferences.Editor editor = settings.edit();					
		editor.putString(Constants.SPECIALITY,Integer.toString(selections[0]));
		editor.putString(Constants.AREA+"w",Integer.toString(selections[3]));
		editor.putString(Constants.CITIES+"w",Integer.toString(selections[4]));
		editor.putString(Constants.ROLE, spinner2.getSelectedIndAsString());
		editor.putString(Constants.SIZE, spinner3.getSelectedIndAsString());
		editor.putString(Constants.X,Double.toString(myx));
		editor.putString(Constants.Y,Double.toString(myy));
		editor.putString(Constants.ADDRESS,changeAddress.getText().toString());
		
		editor.commit();
		startWidget();
    }

    /**
     * This method right now displays the widget and starts a Service to fetch
     * remote data from Server
     */
    private void startWidget() {
 
        // this intent is essential to show the widget
        // if this intent is not included,you can't show
        // widget on homescreen
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(Activity.RESULT_OK, intent);
 
        // start your service
        // to fetch data from web
        Intent serviceIntent = new Intent(this, RemoteFetchService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startService(serviceIntent);
 
        // finish this activity
        this.finish();
 
    }
 
}