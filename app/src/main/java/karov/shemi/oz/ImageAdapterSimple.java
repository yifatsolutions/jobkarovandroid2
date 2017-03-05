package karov.shemi.oz;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Map;

public class ImageAdapterSimple extends ArrayAdapter<String>{
	private final Context context;
	private final ArrayList<Map<String, String>> items;

	private ImageLoader imageLoader;

	public ImageAdapterSimple(Context context,ArrayList<Map<String, String>> web) {
		super(context, R.layout.linemap);
		this.context = context;
		this.items = web;
	    imageLoader = ImageLoader.getInstance();

	}
	@Override
	public int getCount() {
	    return items.size();
	}

	@Override
	public String getItem(int position) {
	    return items.get(position).get(Constants.COMPANY);
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View rowView= inflater.inflate(R.layout.linemap, null, true);
		TextView txtTitle1 = (TextView) rowView.findViewById(R.id.text1);
		TextView txtTitle2 = (TextView) rowView.findViewById(R.id.text2);
		TextView txtTitle3 = (TextView) rowView.findViewById(R.id.text3);

		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewMap);
		txtTitle1.setText(items.get(position).get(Constants.COMPANY));
		txtTitle2.setText(items.get(position).get(Constants.NAME));
		txtTitle3.setText(items.get(position).get(Constants.ADDRESS));

		imageLoader.displayImage(items.get(position).get(Constants.PHOTO), imageView);

		return rowView;
	
	}
	
}
