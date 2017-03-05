package karov.shemi.oz;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SideMenuAdapter extends ArrayAdapter<String> {
	private String[] items;
	private Context context;
	private int layoutRes;
	private int[] pics={
			android.R.drawable.ic_menu_info_details
	        ,android.R.drawable.ic_menu_search,android.R.drawable.ic_menu_save,
	        android.R.drawable.ic_menu_view,android.R.drawable.ic_menu_send
	        ,android.R.drawable.ic_menu_help,android.R.drawable.ic_menu_agenda
	        ,android.R.drawable.ic_menu_share
	        ,android.R.drawable.ic_menu_directions
	        ,android.R.drawable.ic_dialog_info
	        ,android.R.drawable.ic_menu_edit
	        ,android.R.drawable.ic_input_get
	        ,android.R.drawable.ic_menu_mapmode
	        ,android.R.drawable.ic_menu_upload
	};

public SideMenuAdapter(Context context , int textViewResourceId, String[] items) {
    super(context, textViewResourceId, items);
    this.layoutRes=textViewResourceId;
    this.context = context;
    this.items = items;   
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    ViewHolder holder = null;

     if (convertView == null) {

            convertView = mInflater.inflate(layoutRes, parent , false);
            holder = new ViewHolder();
            holder.tv1 = (TextView) convertView.findViewById(R.id.text1);
             holder.imgJob = (ImageView) convertView.findViewById(R.id.imageView2);
                      convertView.setTag(holder);
            
    }
     else {

         holder = (ViewHolder) convertView.getTag();
        }
     String title= items[position];
     holder.imgJob.setImageResource(pics[position]);
     holder.tv1.setText(title);
  return convertView;
 }

 static class ViewHolder {

            TextView tv1;
           
            ImageView imgJob;
 }
 }




