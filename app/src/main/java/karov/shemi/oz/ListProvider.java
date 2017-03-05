package karov.shemi.oz;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * 
 */
public class ListProvider implements RemoteViewsFactory {
	private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
	private Context context = null;
	private int appWidgetId;

	public ListProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		populateListItems();
	}

	@SuppressWarnings("unchecked")
	private void populateListItems() {
		  listItemList = (ArrayList<ListItem>)
		                 RemoteFetchService.listItemList
		                .clone();
			if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+listItemList.size());

		}
	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 *Similar to getView of Adapter where instead of View
	 *we return RemoteViews 
	 * 
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				context.getPackageName(), R.layout.list_row);
		ListItem listItem = listItemList.get(position);
		remoteView.setTextViewText(R.id.heading, listItem.name);
		remoteView.setTextViewText(R.id.content, listItem.company);
		remoteView.setTextViewText(R.id.distance, listItem.city+listItem.distance);
		Bundle extras = new Bundle();
		Intent fillInIntent = new Intent();
        //extras.putInt(Constants.EXTRA_ITEM, position);
        extras.putDouble(Constants.X, listItem.x);
        extras.putDouble(Constants.Y, listItem.y);
        extras.putString(Constants.NAME, listItem.name);
        extras.putString(Constants.COMPANY, listItem.company);
        extras.putInt(Constants.ID, listItem.id);
        extras.putString(Constants.ADDRESS, listItem.city);
        extras.putString(Constants.PHOTO, listItem.photo);
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        remoteView.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
		return remoteView;
	}


	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		listItemList = (ArrayList<ListItem>)
                RemoteFetchService.listItemList
               .clone();
		if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+"set changed"+listItemList.size());
	}

	@Override
	public void onDestroy() {
	}



}
