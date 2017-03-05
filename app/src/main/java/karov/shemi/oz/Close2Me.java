package karov.shemi.oz;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class Close2Me  extends Application {
	    @Override
	    public void onCreate() {
	        super.onCreate();
	        File cacheDir = StorageUtils.getCacheDirectory(this);
	        DisplayImageOptions options = new DisplayImageOptions.Builder()
	        .showStubImage(R.drawable.iconbig)
	        .showImageForEmptyUri(R.drawable.iconbig) // resource or drawable
	        .showImageOnFail(R.drawable.iconbig) // resource or drawable
	        .resetViewBeforeLoading(false)  // default
	        .cacheInMemory(true) // default
	        .cacheOnDisc(true) // default
	        .displayer(new RoundedBitmapDisplayer(20)) // default
	        .build();
	        // Create global configuration and initialize ImageLoader with this configuration
	        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
	        .discCache(new LimitedAgeDiscCache(cacheDir, 864000))
	        .defaultDisplayImageOptions(options)
	        
	         .build();
	        ImageLoader.getInstance().init(config);
	    }
	
}
