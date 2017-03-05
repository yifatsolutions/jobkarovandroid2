package karov.shemi.oz;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ImagePagerView extends MenuActionActivity implements
OnClickListener, OnPageChangeListener {

private Button btnImagePrevious, btnImageNext;
private int position, totalImage;
private ViewPager viewPage;
private FragmentPagerAdapter adapter;

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
    	this.finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
}
@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.imageview_page);

    actionBar.setDisplayHomeAsUpEnabled(true);
    //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));

	viewPage = (ViewPager) findViewById(R.id.viewPager);
	btnImagePrevious = (Button) findViewById(R.id.btnImagePrevious);
	btnImageNext = (Button) findViewById(R.id.btnImageNext);
	String[] imageHeaders=getIntent().getStringArrayExtra(Constants.PHOTO);
	position=getIntent().getIntExtra(Constants.ID, 0);
	
	totalImage = imageHeaders.length;
	setPage(position);
	
	//adapter = new FragmentPagerAdapter(getSupportFragmentManager(),imageHeaders);
	
	viewPage.setAdapter(adapter);
	viewPage.setCurrentItem(position);
	viewPage.setOnPageChangeListener(ImagePagerView.this);
	
	btnImagePrevious.setOnClickListener(this);
	btnImageNext.setOnClickListener(this);
	
	}
	
	@Override
	public void onClick(View v) {
	if (v == btnImagePrevious) {
		position--;
		viewPage.setCurrentItem(position);
	} else if (v == btnImageNext) {
		position++;
		viewPage.setCurrentItem(position);
	}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int position) {
	this.position = position;
	setPage(position);
	}
	
	private void setPage(int page) {
	if (page == 0 && totalImage > 0) {
		btnImageNext.setVisibility(View.VISIBLE);
		btnImagePrevious.setVisibility(View.INVISIBLE);
	} else if (page == totalImage - 1 && totalImage > 0) {
		btnImageNext.setVisibility(View.INVISIBLE);
		btnImagePrevious.setVisibility(View.VISIBLE);
	} else {
		btnImageNext.setVisibility(View.VISIBLE);
		btnImagePrevious.setVisibility(View.VISIBLE);
	}
	}
}