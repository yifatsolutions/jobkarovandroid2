package karov.shemi.oz;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentImageView extends Fragment {

	private String itemData;
	private Bitmap myBitmap;
	public ProgressDialog pd;
	private ImageView ivImage;
	private ImageLoader imageLoader;


	public static FragmentImageView newInstance() {
		FragmentImageView f = new FragmentImageView();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.imageview, container, false);
		ivImage = (ImageView) root.findViewById(R.id.ivImageView);
		imageLoader = ImageLoader.getInstance();	     
		 imageLoader.displayImage(itemData, ivImage);
		
		return root;
	}
	public void setImageList(String path) {
		this.itemData = path;
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (myBitmap != null) {
			myBitmap.recycle();
			myBitmap = null;
		}
	}
}