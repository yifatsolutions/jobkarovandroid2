package karov.shemi.oz;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	
	

/**
* this method is called every 30 mins as specified on widgetinfo.xml
* this method is also called on every phone reboot
**/
	
@Override
public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
 
	 for (int i = 0; i < appWidgetIds.length; ++i) {
		 Intent serviceIntent = new Intent(context, RemoteFetchService.class);
         serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                 appWidgetIds[i]);
         context.startService(serviceIntent);
     /*RemoteViews remoteViews = updateWidgetListView(context,
                                           appWidgetIds[i]);
     appWidgetManager.updateAppWidget(appWidgetIds[i], 
                                           remoteViews);
     String sel0= Integer.toString(selections[0]);
		String sel1= Integer.toString(selections[1]);
		String sel2= Integer.toString(selections[2]);
		String sel3= Integer.toString(selections[3]);
		String sel4=Integer.toString(selections[4]);
		String xStr =Double.toString(x);
	    String yStr =Double.toString(y);
		String[] str1={Constants.url1+Constants.urlCommand3,"speciality",sel0,"role",sel1,"size",sel2,"area",sel3,"radius",sel4,"x",xStr,"y",yStr,"type","0","for","1"};

     new DownloadBitmap(remoteViews, appWidgetIds[i], appWidgetManager).execute(str1);
*/
    }
 super.onUpdate(context, appWidgetManager, appWidgetIds);
}
private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
	 
    // which layout to show on widget
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
            R.layout.widget_layout2);

    Intent serviceIntent = new Intent(context, RemoteFetchService.class);
	 serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId);
    Intent intent = new Intent(context, ConfigActivity.class);

   PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
   PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent, 0);

   remoteViews.setOnClickPendingIntent(R.id.refreshbutton, pendingIntent);
   remoteViews.setOnClickPendingIntent(R.id.configurationbutton, pendingIntent2);

   
   Intent toastIntent = new Intent(context, WidgetProvider.class);
   toastIntent.setAction(Constants.WIDGET_ITEM);
   toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
   intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
   PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
       PendingIntent.FLAG_UPDATE_CURRENT);
   remoteViews.setPendingIntentTemplate(R.id.listViewWidget, toastPendingIntent);    
    // RemoteViews Service needed to provide adapter for ListView
    Intent svcIntent = new Intent(context, WidgetService.class);
    // passing app widget id to that RemoteViews Service
    svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
    // setting a unique Uri to the intent
    // don't know its purpose to me right now
    svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
    // setting adapter to listview of the widget
    remoteViews.setRemoteAdapter(appWidgetId, R.id.listViewWidget,
            svcIntent);
    // setting an empty view in case of no data
    remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
    return remoteViews;
}
@Override
public void onReceive(Context context, Intent intent) {
     if (intent.getAction().equals(Constants.WIDGET_ITEM)) {
         int id= intent.getIntExtra(Constants.ID,0);
         String name= intent.getStringExtra(Constants.NAME);
         String company= intent.getStringExtra(Constants.COMPANY);
         double x= intent.getDoubleExtra(Constants.X,0.0);
         double y= intent.getDoubleExtra(Constants.Y,0.0);
         String address= intent.getStringExtra(Constants.ADDRESS);
         Intent inten = new Intent();
         inten.setClassName("karov.shemi.oz", "karov.shemi.oz.DetailsActivity");
         inten.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         inten.putExtra(Constants.COMPANY, company);
         inten.putExtra(Constants.NAME,name);
         inten.putExtra(Constants.ADDRESS,address);
         inten.putExtra(Constants.ID,id);
         inten.putExtra(Constants.X,x);
         inten.putExtra(Constants.Y,y);
         inten.putExtra(Constants.MYX,x);
         inten.putExtra(Constants.MYY,y);
         context.startActivity(inten);       
     }
    super.onReceive(context, intent);
    if (intent.getAction().equals(Constants.DATA_FETCHED)) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        
        RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
		int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
	    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
	    
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);
    }
}
   
}