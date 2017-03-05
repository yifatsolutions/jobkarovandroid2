package karov.shemi.oz;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context _context;
    private List<String> _listDataHeader; // header titles
    private HashMap<String, List<String>> _listDataChild;
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.linemenu, null);
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.text1);
        ImageView imgJob = (ImageView) convertView.findViewById(R.id.imageView2);
        String headerTitle = (String) getGroup(groupPosition);
    	if(headerTitle.length()<2) {
    		String name = "icon"+groupPosition+"_"+childPosition;
    		int id =_context.getResources().getIdentifier(name, "drawable", _context.getPackageName());
        	imgJob.setImageResource(id);
        	imgJob.setVisibility(View.VISIBLE);
        	convertView.setBackgroundResource(android.R.color.white);
        	txtListChild.setTextColor(_context.getResources().getColor(android.R.color.black));
        }
        else {
        	imgJob.setVisibility(View.GONE);
        	txtListChild.setTextColor(_context.getResources().getColor(android.R.color.white));
        	convertView.setBackgroundResource(R.color.blueicon);
        }
        txtListChild.setText(childText);
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
    	String headerTitle = (String) getGroup(groupPosition);
    	if(headerTitle.length()>1) {

    		LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		convertView = infalInflater.inflate(R.layout.linemenu, null);
    		TextView lblListHeader = (TextView) convertView.findViewById(R.id.text1);
    		ImageView imgJob = (ImageView) convertView.findViewById(R.id.imageView2);
    		ImageView imgJob2 = (ImageView) convertView.findViewById(R.id.imageView3);
    		String name = "iconheader"+groupPosition;
    		int id =_context.getResources().getIdentifier(name, "drawable", _context.getPackageName());
        	imgJob.setImageResource(id);
        	imgJob2.setImageResource(id);
        	/*if(isExpanded){
        		imgJob.setVisibility(View.GONE);
        		imgJob2.setVisibility(View.VISIBLE);
        	}
        	else{*/
        		imgJob.setVisibility(View.VISIBLE);
        		imgJob2.setVisibility(View.GONE);
        	//}  
        	lblListHeader.setTypeface(null, Typeface.BOLD);
        	lblListHeader.setText(headerTitle);
    	}
        else {
        	convertView = new FrameLayout(_context);
        	convertView.setVisibility(View.GONE);
        }
       
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}