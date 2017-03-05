package karov.shemi.oz;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.GridView;

import java.util.ArrayList;

public class GridViewActivity extends MenuActionActivity {

    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private String[] imageHeaders;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.blue))));

        gridView = (GridView) findViewById(R.id.grid_view);
 
         // Initilizing Grid View
        InitilizeGridLayout();
 
        imageHeaders=getIntent().getStringArrayExtra(Constants.PHOTO);
        
        // Gridview adapter
        adapter = new GridViewImageAdapter(GridViewActivity.this, imageHeaders,
                columnWidth);
 
        // setting grid view adapter
        gridView.setAdapter(adapter);
    }
 
    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Constants.GRID_PADDING, r.getDisplayMetrics());
 
        columnWidth = (int) ((getScreenWidth() - ((Constants.NUM_OF_COLUMNS + 1) * padding)) / Constants.NUM_OF_COLUMNS);
 
        gridView.setNumColumns(Constants.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) 
                this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
       // try {
        //    display.getSize(point);// getSize(point);
        //} catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        //}
        columnWidth = point.x;
        return columnWidth;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

