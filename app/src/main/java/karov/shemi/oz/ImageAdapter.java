package karov.shemi.oz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.appevents.AppEventsConstants;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageAdapter extends BaseAdapter implements Filterable {
	private String colorStr;
	private JSONArray colors;
	private Context context;
	private String field1;
	private ImageLoader imageLoader;
	private ArrayList<HashMap<String, String>> items;
	private NotesDbAdapter mDbHelper;
	private String usercode;
	private String userid;

	/* renamed from: karov.shemi.oz.ImageAdapter.1 */
	class C04831 implements OnClickListener {
		C04831() {
		}

		public void onClick(View v) {
			HashMap<String, String> o = (HashMap) ImageAdapter.this.items.get(((Integer) ((ViewHolder) v.getTag()).tv1.getTag()).intValue());
			String ind = (String) o.get(Constants.ID);
			Intent inten = new Intent(ImageAdapter.this.context, JobDetailsActivity.class);
			inten.putExtra(Constants.COMPANY, (String) o.get(Constants.COMPANY));
			inten.putExtra(NotesDbAdapter.KEY_NAME, (String) o.get(NotesDbAdapter.KEY_NAME));
			inten.putExtra(Constants.ADDRESS, (String) o.get(Constants.ADDRESS));
			inten.putExtra(Constants.PHOTO, (String) o.get(Constants.PHOTO));
			inten.putExtra(Constants.ID, Integer.valueOf(ind));
			inten.putExtra(NotesDbAdapter.KEY_X, Double.valueOf((String) o.get(NotesDbAdapter.KEY_X)));
			inten.putExtra(NotesDbAdapter.KEY_Y, Double.valueOf((String) o.get(NotesDbAdapter.KEY_Y)));
			ImageAdapter.this.context.startActivity(inten);
		}
	}

	/* renamed from: karov.shemi.oz.ImageAdapter.2 */
	class C04842 implements OnClickListener {
		C04842() {
		}

		public void onClick(View v) {
			HashMap<String, String> o = (HashMap) ImageAdapter.this.items.get(((Integer) v.getTag()).intValue());
			int index = Integer.valueOf((String) o.get(Constants.ID)).intValue();
			ImageAdapter.this.mDbHelper.open();
			EasyTracker easyTracker = EasyTracker.getInstance(ImageAdapter.this.context);
			if (ImageAdapter.this.mDbHelper.Exists(index, 1)) {
				ImageAdapter.this.mDbHelper.deleteNote((long) index, 1);
				o.put(Constants.FAV, AppEventsConstants.EVENT_PARAM_VALUE_NO);
				easyTracker.send(MapBuilder.createEvent("ui_action", "button_press", "delete favorite", Long.valueOf((long) index)).build());
				new GenericDownload().execute(new String[]{new StringBuilder(Constants.baseUrl).append(ImageAdapter.this.getVersion()).append(Constants.FAV).toString(), Constants.ID, Integer.toString(index), Constants.USERCODE, ImageAdapter.this.usercode, Constants.USERID, ImageAdapter.this.userid, Constants.FAV, AppEventsConstants.EVENT_PARAM_VALUE_NO});
			} else {
				ImageAdapter.this.mDbHelper.createNote(index, (String) o.get(NotesDbAdapter.KEY_NAME), (String) o.get(Constants.COMPANY), Double.valueOf((String) o.get(NotesDbAdapter.KEY_X)).doubleValue(), Double.valueOf((String) o.get(NotesDbAdapter.KEY_Y)).doubleValue(), 1, (String) o.get(Constants.ADDRESS), (String) o.get(Constants.PHOTO));
				o.put(Constants.FAV, AppEventsConstants.EVENT_PARAM_VALUE_YES);
				easyTracker.send(MapBuilder.createEvent("ui_action", "button_press", "add favorite", Long.valueOf((long) index)).build());
				new GenericDownload().execute(new String[]{new StringBuilder(Constants.baseUrl).append(ImageAdapter.this.getVersion()).append(Constants.FAV).toString(), Constants.ID, Integer.toString(index), Constants.USERCODE, ImageAdapter.this.usercode, Constants.USERID, ImageAdapter.this.userid, Constants.FAV, "2"});
			}
			ImageAdapter.this.mDbHelper.close();
		}
	}

	/* renamed from: karov.shemi.oz.ImageAdapter.3 */
	class C04853 extends Filter {
		C04853() {
		}

		protected void publishResults(CharSequence constraint, FilterResults results) {
			ImageAdapter.this.items = (ArrayList) results.values;
			ImageAdapter.this.notifyDataSetChanged();
		}

		protected FilterResults performFiltering(CharSequence constraint) {
			String query = constraint.toString().toLowerCase();
			String[] querys = query.split("_");
			FilterResults results = new FilterResults();
			ArrayList<HashMap<String, String>> FilteredArrayNames = new ArrayList();
			for (int i = 0; i < ImageAdapter.this.items.size(); i++) {
				HashMap<String, String> o = (HashMap) ImageAdapter.this.items.get(i);
				String item = ((String) o.get(ImageAdapter.this.field1)).trim().toLowerCase();
				boolean found = true;
				for (int j = 0; j < querys.length; j++) {
					if (item.length() == 0 || !query.contains(item)) {
						found = false;
					}
				}
				if (found) {
					FilteredArrayNames.add(o);
				}
			}
			results.count = FilteredArrayNames.size();
			results.values = FilteredArrayNames;
			ImageAdapter.this.items = FilteredArrayNames;
			return results;
		}
	}

	public class GenericDownload extends AsyncTask<String, String, JSONObject> {
		protected JSONObject doInBackground(String... url) {
			try {
				return new JSONObject(Downloader.downloadPostObject(url));
			} catch (Exception e) {
				return null;
			}
		}
	}

	static class ViewHolder {
		CheckBox btnBookNow;
		ImageView imgJob;
		TextView tv1;
		TextView tv2;
		TextView tv3;

		ViewHolder() {
		}
	}

	public ImageAdapter(Context context, ArrayList<HashMap<String, String>> items) {
		this.items = null;
		this.context = context;
		if (this.items == null) {
			this.items = items;
		}
		this.imageLoader = ImageLoader.getInstance();
		SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		this.usercode = settings.getString(Constants.USERCODE, BuildConfig.VERSION_NAME);
		this.userid = settings.getString(Constants.USERID, BuildConfig.VERSION_NAME);
		this.mDbHelper = new NotesDbAdapter(context);
		this.colorStr = settings.getString(Constants.COLOR, BuildConfig.VERSION_NAME);
		try {
			if (this.colorStr.length() > 0) {
				this.colors = new JSONArray(this.colorStr);
			} else {
				this.colors = null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getVersion() {
		String version = "/uknkown/";
		try {
			version = "/" + this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionName + "/";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	public int getCount() {
		return this.items.size();
	}

	public Object getItem(int position) {
		return this.items.get(position);
	}

	public long getItemId(int position) {
		return (long) position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		boolean favexist = false;
		LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
		HashMap<String, String> job = (HashMap) this.items.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.linefav, parent, false);
			holder = new ViewHolder();
			holder.tv1 = (TextView) convertView.findViewById(R.id.text1);
			holder.tv2 = (TextView) convertView.findViewById(R.id.text2);
			holder.tv3 = (TextView) convertView.findViewById(R.id.textlocation);
			holder.imgJob = (ImageView) convertView.findViewById(R.id.imageView2);
			holder.btnBookNow = (CheckBox) convertView.findViewById(R.id.star);
			holder.btnBookNow.setFocusable(false);
			holder.btnBookNow.setFocusableInTouchMode(false);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (this.colors == null || job.get(Constants.COLOR) == null || ((String) job.get(Constants.COLOR)).isEmpty() || Integer.valueOf((String) job.get(Constants.COLOR)).intValue() <= 0) {
			convertView.setBackgroundColor(this.context.getResources().getColor(android.R.color.transparent));
		} else {
			try {
				convertView.setBackgroundColor(Color.parseColor(this.colors.getString(Integer.valueOf((String) job.get(Constants.COLOR)).intValue())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		holder.tv1.setTag(Integer.valueOf(position));
		holder.tv1.setText((CharSequence) job.get(NotesDbAdapter.KEY_NAME));
		holder.tv2.setText((CharSequence) job.get(Constants.COMPANY));
		holder.tv3.setText(new StringBuilder(String.valueOf((String) job.get(Constants.CITYNAME))).append(this.context.getResources().getString(R.string.distanceof)).append((String) job.get(Constants.DISTANCE)).append((String) job.get(Constants.METER)).append(this.context.getResources().getString(R.string.kmfromyou)).toString());
		this.imageLoader.displayImage((String) job.get(Constants.PHOTO), holder.imgJob);
		this.mDbHelper.open();
		int intValue = Integer.valueOf((String) job.get(Constants.ID)).intValue();
		if (Integer.valueOf((String) job.get(Constants.FAV)).intValue() > 0) {
			favexist = true;
		}
		holder.btnBookNow.setChecked(favexist);
		this.mDbHelper.close();
		convertView.setOnClickListener(new C04831());
		holder.btnBookNow.setTag(Integer.valueOf(position));
		holder.btnBookNow.setOnClickListener(new C04842());
		return convertView;
	}

	protected void filter(String[] field, String[] constraint) {
		ArrayList<HashMap<String, String>> FilteredArrayNames = new ArrayList();
		String allString = this.context.getResources().getString(R.string.all);
		for (int i = 0; i < this.items.size(); i++) {
			HashMap<String, String> o = (HashMap) this.items.get(i);
			boolean found = true;
			int j = 0;
			while (j < constraint.length) {
				if (!(constraint[j] == null || constraint[j].length() <= 0 || constraint[j].equalsIgnoreCase(allString))) {
					String item = ((String) o.get(field[j])).trim().toLowerCase();
					if (item.length() == 0 || !constraint[j].toLowerCase().contains(item)) {
						found = false;
					}
				}
				j++;
			}
			if (found) {
				FilteredArrayNames.add(o);
			}
		}
		this.items = FilteredArrayNames;
	}

	public Filter getFilter() {
		return getFilter(Constants.COMPANY);
	}

	public Filter getFilter(String field) {
		this.field1 = field;
		return new C04853();
	}
}
