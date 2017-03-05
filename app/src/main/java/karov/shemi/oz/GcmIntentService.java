package karov.shemi.oz;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            	//sendNotification("Send error: " + extras.toString(),null);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString(),null);
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                 sendNotification(extras);
                Log.i(Constants.TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extras) {
    	NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent inten = new Intent(this, ResultsListActivity.class);
    	int[] selections={0,0,0,0,0};
        inten.putExtra(Constants.VAR,selections);
		 inten.putExtra(Constants.ROLE, "0");
		 inten.putExtra(Constants.SIZE, "0");
		 inten.putExtra(Constants.TYPE,Constants.SEARCH);
		 inten.putExtra(Constants.X,0.0);
		 inten.putExtra(Constants.Y,0.0);
        inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        //Srecific data for app
        int id=0;
        String link="";
        String company="";
        String address="";
        String msg="";
        String title=getString(R.string.app_name);
        if(extras!=null){
        	/*for (String key : extras.keySet()) {
        	    String value = extras.get(key).toString();
        	    inten.putExtra(key,value);      	    
        	}*/
        	link=extras.getString(Constants.LINK,"");
        	if(link.length()>0){
            	if(link.contains("www.jobkarov.co")){
            		inten.putExtra(Constants.LINK,link);
            	}
            	else inten = new Intent(Intent.ACTION_VIEW, Uri.parse(link));            
            }
            
        	company=extras.getString(Constants.COMPANYNAME,"");
        	inten.putExtra(Constants.COMPANY, company);
        	msg=extras.getString(Constants.MSG,"");
        	title=extras.getString(Constants.TITLE,title);
        	id=Integer.valueOf(extras.getString(Constants.ID,"0"));
        	inten.putExtra(Constants.NAME,msg);
        	address=extras.getString(Constants.ADDRESS,"");
        	inten.putExtra(Constants.ADDRESS,address);
        	inten.putExtra(Constants.ID,id);
        	inten.putExtra(Constants.X,Double.valueOf(extras.getString(Constants.X,"0.0")));
        	inten.putExtra(Constants.Y,Double.valueOf(extras.getString(Constants.Y,"0.0")));
        }
        //till here Srecific data for app
        PendingIntent contentIntent = PendingIntent.getActivity(this, id,
               inten, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle(title)
        .setContentText(msg)
        .setDefaults(Notification.DEFAULT_ALL)
        .setAutoCancel(true)
        .setContentIntent(contentIntent)
        .setStyle(new NotificationCompat.InboxStyle()
         .addLine(company)
         //.addLine(address)
         .setBigContentTitle(msg)
         .setSummaryText(address));
        Notification noti = mBuilder.build();
        mNotificationManager.notify(id,noti);
    }
}
