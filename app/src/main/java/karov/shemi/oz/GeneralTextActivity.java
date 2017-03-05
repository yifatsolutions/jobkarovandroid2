package karov.shemi.oz;
import android.os.Bundle;
import android.widget.TextView;
public class GeneralTextActivity extends MenuActionActivity {
	private int mode;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explanations);
		Bundle bundle = getIntent().getExtras();
	    mode=bundle.getInt(Constants.DESC);
		 TextView tv1=(TextView)findViewById(R.id.textexplanatios);
		String takanon=getResources().getString(R.string.about);
		String title=getResources().getString(R.string.aboutmenu);
		if(mode==1) {
			takanon=getResources().getString(R.string.takanondetails);
			title=getResources().getString(R.string.takanon);
		}
		//String [] texts=getResources().getStringArray(R.array.generaltexts);
		//tv1.setText(texts[mode]);
		setTitle(title);
		((TextView)actionBar.getCustomView().findViewById(R.id.mytext)).setText(getTitle());
		tv1.setText(takanon);
	}
} 