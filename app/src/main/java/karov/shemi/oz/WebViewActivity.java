package karov.shemi.oz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class WebViewActivity extends MenuActionActivity {
	private boolean pressed=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.loading), true);
		setContentView(R.layout.activity_web_view);
		WebView mWebview = (WebView) findViewById(R.id.webview);
		Bundle bundle = getIntent().getExtras();
	    int mode=bundle.getInt(Constants.DESC,-1);
	    String link=bundle.getString(Constants.LINK, "");
		String customTitle=bundle.getString(Constants.TITLE, "");

		int timeout=bundle.getInt(Constants.TIMEOUT, 0);
	    if(timeout>0){
	    	new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                if(!pressed)finish();
	            }
	        }, timeout*1000);
	    }
		if(customTitle.length()>0) setTitle(customTitle);
	    else if(mode<0) setTitle(getString(R.string.message));
	    else{
	    	String[] titles=getResources().getStringArray(R.array.sidemenuitems3);
	    	setTitle(titles[mode]);
	    }
	    mWebview.setWebChromeClient(new WebChromeClient());
	    mWebview.clearCache(true);
	    mWebview.clearHistory();
	    mWebview.getSettings().setJavaScriptEnabled(true);
	    mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	    mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewActivity.this, description, Toast.LENGTH_SHORT).show();
            }

          @Override
          public void onPageStarted(WebView view, String url, Bitmap favicon){
        	  if(!pressed)pd.show();
          }
            @Override
            public void onPageFinished(WebView view, String url) {
            	if (!WebViewActivity.this.isFinishing() && pd != null && pd.isShowing()) { 
            		pd.dismiss();
            	}
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("mailto:")){
                    Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(i);   
                }
                else{
                    view.loadUrl(url);  
                }
                return true;
            }


	    });
	    mWebview.setOnTouchListener(new View.OnTouchListener() {
	    	@Override
	        public boolean onTouch(View view, MotionEvent motionEvent) {
	            if (motionEvent.getAction()==MotionEvent.ACTION_UP) {	                
	                	pressed=true;
	            }
	            return false;
	        }
	    });
		((TextView)actionBar.getCustomView().findViewById(R.id.mytext)).setText(getTitle());
		if(link.length()>0) mWebview.loadUrl(link);
		else mWebview.loadUrl("file:///android_asset/"+mode+".html");
	}
}
