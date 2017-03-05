package karov.shemi.oz;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
 
 
 
public class OneFragment extends ListFragment {
	private ArrayList<HashMap<String, String>> mylist; 
	private BaseAdapter adapter;
   
    public void setList(ArrayList<HashMap<String, String>> models,Context context){
        mylist = models;
        adapter = new ImageAdapter(context, mylist);
        setListAdapter(adapter);
		
		
  }
  
}