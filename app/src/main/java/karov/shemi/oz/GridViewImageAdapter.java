package karov.shemi.oz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GridViewImageAdapter extends BaseAdapter {
	private Activity _activity;
    private String[] _filePaths;
    private int imageWidth;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

 
    public GridViewImageAdapter(Activity activity, String[] filePaths,
            int imageWidth) {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.iconbig)
        .showImageForEmptyUri(R.drawable.iconbig) // resource or drawable
        .showImageOnFail(R.drawable.iconbig) // resource or drawable
        .resetViewBeforeLoading(false)  // default
        .cacheInMemory(false) // default
        .cacheOnDisc(true) // default
        .displayer(new SimpleBitmapDisplayer()) // default
        .build();
    }
 
    @Override
    public int getCount() {
        return this._filePaths.length;
    }
 
    @Override
    public Object getItem(int position) {
        return this._filePaths[position];
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }
 
        // get screen dimensions
        //Bitmap image = decodeFile(_filePaths.get(position), imageWidth,imageWidth);
 
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,imageWidth));
        //imageView.setImageBitmap(image);
                imageLoader.displayImage (_filePaths[position], imageView,options);

        // image view click listener
        imageView.setOnClickListener(new OnImageClickListener(position));
 
        return imageView;
    }
 
    class OnImageClickListener implements OnClickListener {
 
        int _postion;
 
        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }
 
        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity,ImagePagerView.class);// FullImageActivity.class);
            i.putExtra(Constants.ID, _postion);
            i.putExtra(Constants.PHOTO, _filePaths);//.get(_postion));
            _activity.startActivity(i);
        }
 
    }
 
    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {
 
            File f = new File(filePath);
 
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
 
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
 
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
 
}