package karov.shemi.oz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;

public class FilterActivity extends MenuActionActivity {
	  
	protected MultiSelectionSpinner spinner2,spinner3;
	private ArrayList<String> cities,companies,allCities,allCompanies;
	private Button change2,change3,find,cancel;
	private String resultCompany,resultCity,allString;
	private int[] citiesIndex;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		companies=getIntent().getStringArrayListExtra(Constants.COMPANY);
		cities=getIntent().getStringArrayListExtra(Constants.CITIES);
		allString=getResources().getString(R.string.all);
		//companies.add(0, allString);
		//cities.add(0, allString);
		allCompanies=getIntent().getStringArrayListExtra(Constants.RANGE);
		allCities=getIntent().getStringArrayListExtra(Constants.VAR);
		resultCompany=getIntent().getStringExtra(Constants.COMPANYNAME);
		resultCity=getIntent().getStringExtra(Constants.CITYNAME);
		if(resultCompany==null)resultCompany="";
		if(resultCity==null)resultCity="";
		change2 = (Button) findViewById(R.id.change2);
        change2.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		spinner2.setVisibility(View.VISIBLE);
        		spinner2.performClick();	
        		change2.setText(R.string.change);
       	 }
        });
        change3 = (Button) findViewById(R.id.change3);
        change3.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	
        		filterSpinner();
        		spinner3.setVisibility(View.VISIBLE);
        		spinner3.performClick();
        		change3.setText(R.string.change);
       	 }
        });
        int[] companiesIndex =new int[companies.size()];
        for (int i = 0; i < companiesIndex.length; i++) {
        	companiesIndex[i]=i;
		}
        citiesIndex =new int[cities.size()];
		for (int i = 0; i < citiesIndex.length; i++) {
			citiesIndex[i]=i;
		}
		spinner2 = (MultiSelectionSpinner) findViewById(R.id.multSpinner2);
        spinner2.setItems(companies,resultCompany.split(","), companiesIndex,true,true);
    	if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+"resultCompany="+resultCompany);

        spinner3 = (MultiSelectionSpinner) findViewById(R.id.multSpinner3);
        spinner3.setItems(cities,resultCity.split(","), citiesIndex,true,true);
        if(resultCompany!=null && resultCompany.length()>0){
        	//String[] companySplitted=resultCompany.split(", ");
        	//spinner2.setSelection(companySplitted);
        	spinner2.setVisibility(View.VISIBLE);
        }
        if(resultCity!=null && resultCity.length()>0){
    	   //String[] citySplitted=resultCity.split(", ");
    	   //spinner3.setSelection(citySplitted);
    	   spinner3.setVisibility(View.VISIBLE);
        }
        spinner3.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Toast.makeText(getApplicationContext(), "child clicked"+event.getAction(), Toast.LENGTH_SHORT).show();
				filterSpinner();
				return false;
			}
		});
       
       
        find = (Button) findViewById(R.id.calculate);
        find.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	       		
        		Intent inten = new Intent();
        		inten.putExtra(Constants.COMPANY, spinner2.getSelectedItemsAsString());
        		inten.putExtra(Constants.CITIES, spinner3.getSelectedItemsAsString());			 
        		setResult(RESULT_OK,inten);
        		finish();	   
        	}
        }); 
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {	       		
        		Intent inten = new Intent();
        		setResult(RESULT_CANCELED,inten);
        		finish();	   
        	}
        });
	}
	private void filterSpinner(){
		if(spinner2.getSelectedItemsAsString().length()>0 && !spinner2.getSelectedItemsAsString().equalsIgnoreCase(allString)){
			ArrayList<String> citiesNew=new ArrayList<String>();
			for (int i = 0; i < allCompanies.size(); i++) {
				String currentCompany = allCompanies.get(i).toLowerCase();
				String currentCity = allCities.get(i);
				if(spinner2.getSelectedItemsAsString().toLowerCase().contains(currentCompany) && !citiesNew.contains(currentCity) && !currentCity.isEmpty()){
					citiesNew.add(currentCity);
				}
			}
			if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+"cities="+citiesNew.size()+"allcompanies size="+allCompanies.size()+"selected comp="+spinner2.getSelectedItemsAsString()+"result company="+resultCompany);
			Collections.sort(citiesNew, String.CASE_INSENSITIVE_ORDER);
			citiesIndex =new int[citiesNew.size()];
			for (int i = 0; i < citiesNew.size(); i++) {
				citiesIndex[i]=i;
			}
			//citiesNew.add(0, allString);
			if (BuildConfig.DEBUG) Log.i(Constants.TAG, Constants.TAG+"cities="+citiesNew.size()+" "+citiesIndex.length);
			spinner3.setItems(citiesNew,resultCity.split(","),citiesIndex,true,true);
		}
		else {
			citiesIndex =new int[cities.size()];
			for (int i = 0; i < citiesIndex.length; i++) {
				citiesIndex[i]=i;
			}
			spinner3.setItems(cities,resultCity.split(","),citiesIndex,true,true);
		}
		/*if(resultCity!=null && resultCity.length()>0){
			String[] citySplitted=resultCity.split(", ");
			spinner3.setSelection(citySplitted);   
		} */ 
	}
}
