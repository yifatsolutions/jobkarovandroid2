package karov.shemi.oz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
  
public class MultiSelectionSpinner extends Spinner implements    OnMultiChoiceClickListener {  
	ArrayList<String> _items = null;  
	ArrayList<String> _itemsSorted = null;  
	List<String>_selected;
	boolean[] mSelection = null;  
	int[] _indexes = null;  
	ArrayAdapter<String> simple_adapter;  
  	private boolean _addAll,_sortAll;
  	public MultiSelectionSpinner(Context context) {  
  		super(context);  
  		simple_adapter = new ArrayAdapter<String>(context,  R.layout.spinner_layout);  
  		//simple_adapter.setDropDownViewResource(R.layout.spinner_item);
  		super.setAdapter(simple_adapter);  
  	}  
  	public MultiSelectionSpinner(Context context, AttributeSet attrs) {  
  		super(context, attrs);  
  		simple_adapter = new ArrayAdapter<String>(context,  R.layout.spinner_layout);  
  		//simple_adapter.setDropDownViewResource(R.layout.spinner_item);
  		super.setAdapter(simple_adapter);  
  	}  
  	public void onClick(DialogInterface dialog, int which, boolean isChecked) {  
  		if (mSelection != null && which < mSelection.length) {  
  			final AlertDialog alert = (AlertDialog)dialog;
  			final ListView list = alert.getListView();
  			mSelection[which] = isChecked;  
  			if(isChecked && which>0){
  				mSelection[0]=false;
  				list.setItemChecked(0, false);
  			}
  			else if(which==0){
  				_selected.clear();
  				for(int i=1;i<mSelection.length;i++){
  					mSelection[i]=false;
  					list.setItemChecked(i, false);
  				}  
  				mSelection[0]=true;
  				list.setItemChecked(0, true);
  			}
  			if(this.getSelectedIndAsString().length()==0){
  				this.setSelection(0);
  				list.setItemChecked(0, true);
  			}
  			_selected=getSelectedStrings();
  			simple_adapter.clear();  
  			simple_adapter.add(getSelectedItemsAsString());
  		} else {  
  			throw new IllegalArgumentException(  "Argument 'which' is out of bounds.");  
  		}  
  	}   
  	@Override  
  	public boolean performClick() {
  		if(_sortAll){
  			_itemsSorted=new ArrayList<String>(_items.size());   
  			for(String item: _items) _itemsSorted.add(item);
  			Comparator<String> comparator = new Comparator<String>() {                                    
				
  				@Override
  				public int compare(String first, String second) {
  					boolean firstin=_selected.contains(first);
  					boolean secondin=_selected.contains(second);
  					if ((firstin &&secondin) || (!firstin &&!secondin)) {
  						return first.compareToIgnoreCase(second);
  					} else {
  						if (secondin) {
  							return 1;
  						} else {
  							return -1;
  						}
  					}
  				}
  			};      
  			Collections.sort(_itemsSorted, comparator);
  		}
  		else{
  			_itemsSorted=new ArrayList<String>(_selected.size());
  			for(String item: _selected) _itemsSorted.add(item);
  			for (String item:  _items) {
  				if (!_selected.contains(item)) {
  					_itemsSorted.add(item);
  				}
  			}
  		}
  		int tmpInt=0;
  		if(_addAll) {
  			 _itemsSorted.add(0, getResources().getString(R.string.all));
  			 tmpInt=1;
  		}
  		mSelection = new boolean[_items.size()+tmpInt]; 

  		Arrays.fill(mSelection, false);
  		if(_addAll && (_selected.size()==0 || _selected.get(0).equalsIgnoreCase( getResources().getString(R.string.all)))){
  			mSelection[0]=true;
  		}
  		else Arrays.fill(mSelection, tmpInt,_selected.size()+tmpInt,true);
  		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
  		String[] array = _itemsSorted.toArray(new String[_itemsSorted.size()]);
  		builder.setMultiChoiceItems (array, mSelection, this);  
  		builder.setNegativeButton(R.string.donestring, null);
  		/*builder.setPositiveButton(R.string.all, new DialogInterface.OnClickListener() {
  			@Override
  			public void onClick(DialogInterface dialog, int which) {
  				_selected.clear();
  				Arrays.fill(mSelection, false);
  				simple_adapter.clear();  
  				simple_adapter.add(getResources().getString(R.string.all));
  			}	
  		});*/
  		builder.show();  
  		return true;  
  	}  
  
  	@Override  
  	public void setAdapter(SpinnerAdapter adapter) {  
  		throw new RuntimeException(  "setAdapter is not supported by MultiSelectSpinner.");  
  	}  
  
  	public void setItems(ArrayList<String> items,List<String> selected,int[] indexes,boolean addAll,boolean sortAll) {
  		_selected=selected;
  		_sortAll=sortAll;
  		_addAll=addAll;
  		_items = items;  
  		_indexes=indexes;  		
  		  
  		if(_sortAll){
  			_itemsSorted=new ArrayList<String>(_items.size());   
  			for(String item: _items) _itemsSorted.add(item);
  			Comparator<String> comparator = new Comparator<String>() {                                    
				
  				@Override
  				public int compare(String first, String second) {
  					boolean firstin=_selected.contains(first);
  					boolean secondin=_selected.contains(second);
  					if ((firstin &&secondin) || (!firstin &&!secondin)) {
  						return first.compareToIgnoreCase(second);
  					} else {
  						if (secondin) {
  							return 1;
  						} else {
  							return -1;
  						}
  					}
  				}
  			};      
  			Collections.sort(_itemsSorted, comparator);
  		}
  		else{
  			_itemsSorted=new ArrayList<String>(_selected.size());
  			for(String item: _selected) _itemsSorted.add(item);
  			for (String item:  _items) {
  				if (!_selected.contains(item)) {
  					_itemsSorted.add(item);
  				}
  			}
  		}
  		int tmpInt=0;
  		if(_addAll) {
  			 _itemsSorted.add(0, getResources().getString(R.string.all));
  			 tmpInt=1;
  		}
  		mSelection = new boolean[_items.size()+tmpInt]; 
  		if(_addAll && _selected.size()==0) {
  			mSelection[0]=true;
  			_selected.add(getResources().getString(R.string.all));	
  		}
  		Arrays.fill(mSelection, false);
  		if(_addAll && (_selected.size()==0 || _selected.get(0).equalsIgnoreCase( getResources().getString(R.string.all)))){
  			mSelection[0]=true;
  		}
  		else Arrays.fill(mSelection, tmpInt,_selected.size()+tmpInt,true);
  		
  		simple_adapter.clear();
  		simple_adapter.add(getSelectedItemsAsStringFromList());
	  //if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+" set items" +Arrays.toString(_items));
 }  
 public void setItems(ArrayList<String> items,String[] selected,int[] indexes,boolean addAll,boolean sortAll) {
	 ArrayList<String> selectedTmp=new ArrayList<String>(selected.length);   
	 for(String item: selected) if(item.length()>0 && !item.equalsIgnoreCase(" ")) selectedTmp.add(item);
	 setItems(items,selectedTmp, indexes,addAll,sortAll);
 }

 public void setItems(String[] items,ArrayList<String> selected,int[] indexes,boolean addAll,boolean sortAll) {  
	 _items=new ArrayList<String>(items.length);   
	 for(String item: items) _items.add(item);
	 setItems(_items,selected, indexes,addAll,sortAll);
 }  
  
 public void setSelection(String[] selection) {
	 boolean foundSelected=false;
	 for (int i = 0; i < mSelection.length; i++) {  
		 mSelection[i] = false;  
	 }  
	 _selected.clear();
	 for (int i = 0; i < selection.length; i++) {
		 String item = selection[i];
		 if(item.trim()!=""){
			 try{
				 int itemInt=Integer.valueOf(item);
				 int j=0;
				 for (j = 0; j < _indexes.length && _indexes[j]!=itemInt; j++) {}
				 if(j<_indexes.length) _selected.add(_items.get(j));
			 }
			 catch(NumberFormatException nfe)  {  
				 _selected.add(item);
			 }
		 }
	 } 
	 for (String cell : _selected) {  
		 for (int j = 0; j < _itemsSorted.size(); ++j) {  
			 if (_itemsSorted.get(j).equalsIgnoreCase(cell)) {  
				 mSelection[j] = true;  
				 foundSelected=true;
			 }  
		 }  
	 }  
	 if(!foundSelected)mSelection[0] = true; 
	 simple_adapter.clear();  
	 simple_adapter.add(getSelectedItemsAsString());  
 }  
  
 public void setSelection(List<String> selection) {  
  for (int i = 0; i < mSelection.length; i++) {  
   mSelection[i] = false;  
  }  
  for (String sel : selection) {  
   for (int j = 0; j < _itemsSorted.size(); ++j) {  
    if (_itemsSorted.get(j).equals(sel)) {  
     mSelection[j] = true;  
    }  
   }  
  }  
  simple_adapter.clear();  
  simple_adapter.add(getSelectedItemsAsString());  
 }  
   public void setSelection(String selectedIndiciesString) {
	 String[] selectedIndicies=selectedIndiciesString.split(",");
	 setSelection(selectedIndicies);  
 }
 public List<String> getSelectedStrings() {  
  List<String> selection = new LinkedList<String>();  
  for (int i = 0; i < mSelection.length; ++i) {  
   if (mSelection[i]) {  
    selection.add(_itemsSorted.get(i));  
   }  
  }  
  return selection;  
 }  
  
 public String getSelectedIndAsString() {  
	 StringBuilder sb = new StringBuilder();  
	  boolean foundOne = false;  
	  
	  for (int i = 0; i < mSelection.length; ++i) {  
	   if (mSelection[i]) {  
	    if (foundOne) {  
	     sb.append(",");  
	    }  
	    foundOne = true;  
	    int index=_items.lastIndexOf(_itemsSorted.get(i));///!!!!!!!
	    if(index==-1) sb.append(Integer.toString(0));
	    else sb.append(Integer.toString(_indexes[index]));  
	   }  
	  } 
	  if(sb.length()==0 )return "0";
	  else return sb.toString();  
	 }  
 

  
 public String getSelectedItemsAsString() {  
   StringBuilder sb = new StringBuilder();  
   boolean foundOne = false;  
   for (int i = 0; i < mSelection.length; ++i) {
    if (mSelection[i]) {  
     if (foundOne) {  
       sb.append(", ");  
     }  
     foundOne = true;  
     sb.append(_itemsSorted.get(i).replaceAll("\\([0-9]*\\)$","" ));  
    }  
   }  
   return sb.toString();  
 }
 public String getSelectedItemsAsStringFromList() {  
	 StringBuilder sb = new StringBuilder();  
	 for (String str : _selected) {
		 if (sb.length()>0) {  
			 sb.append(", ");  
		 }  
		 sb.append(str.replaceAll("\\([0-9]*\\)$","" ));   
	 }  
	 return sb.toString();  
 }


}