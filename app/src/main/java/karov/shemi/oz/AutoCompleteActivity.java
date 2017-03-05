package karov.shemi.oz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

public class AutoCompleteActivity extends MenuActionActivity {
	private ArrayList<String> allCities;
	AutoCompleteTextView autocompletetextview;

	/* renamed from: karov.shemi.oz.AutoCompleteActivity.1 */
	class C04741 implements OnItemClickListener {
		C04741() {
		}

		public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra(Constants.CITIES, AutoCompleteActivity.this.autocompletetextview.getText().toString());
			AutoCompleteActivity.this.setResult(-1, returnIntent);
			AutoCompleteActivity.this.finish();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_complete);
		this.allCities = getIntent().getExtras().getStringArrayList(Constants.CITIES);
		this.autocompletetextview = (AutoCompleteTextView) findViewById(R.id.autocompletetextview);
		ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, this.allCities);
		this.autocompletetextview.setThreshold(1);
		this.autocompletetextview.setAdapter(adapter);
		this.autocompletetextview.setOnItemClickListener(new C04741());
	}
}
