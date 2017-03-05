package karov.shemi.oz;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class CustomCursorAdapter extends CursorAdapter {
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;

public CustomCursorAdapter(Context context, Cursor c, int flags) {
	super(context, c, flags);
	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	imageLoader = ImageLoader.getInstance();
}
@Override
public void bindView(View view, Context context, Cursor cursor) {
	TextView tv1 = (TextView) view.findViewById(R.id.text1);
	tv1.setText(cursor.getString(2));
	TextView tv2 = (TextView) view.findViewById(R.id.text2);
	tv2.setText(cursor.getString(5));
	TextView tv3 = (TextView) view.findViewById(R.id.text7);
	tv3.setText(cursor.getString(6));
	ImageView imgJob = (ImageView) view.findViewById(R.id.imageView2);
	 String path=Constants.IMAGE_URL+(cursor.getInt(0)/1000)+"/"+cursor.getInt(0)+Constants.IMAGE_SURRFIX;
	imageLoader.displayImage(cursor.getString(7), imgJob);
}

@Override
public View newView(Context context, Cursor cursor, ViewGroup parent) {
	return mInflater.inflate(R.layout.linefav2, parent, false);
}

}