package edu.stanford.android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.stanford.android.WebAuth.DialogListener;

public class WADialog extends Dialog {
	private static final String TAG = "WADialog";
	public static final int USERINFO_ERROR = 100;
	
	static final String WA_URL = "https://www.stanford.edu/~aslai/cgi-bin/webauth";
	static final String JS_INTERFACE = "HTMLOUT";
	
    static final FrameLayout.LayoutParams FILL = 
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
                         ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;
    
    private DialogListener mListener;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;
	
	public WADialog(Context context, DialogListener listener) {
		super(context);
		mListener = listener;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        setCancelable(false);
        
        mSpinner = new ProgressDialog(getContext());
        mSpinner.setMessage("Loading...");
        
        mContent = (LinearLayout)findViewById(R.id.login);
        setUpTitle();
        setUpWebView();
    }
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
	}
	
	private void setUpTitle() {
        mTitle = new TextView(getContext());
        mTitle.setText("Stanford WebLogin");
        mTitle.setTextColor(Color.WHITE);
        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        //mTitle.setBackgroundColor(FB_BLUE);
        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
        mContent.addView(mTitle);
    }
    
    private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new WADialog.WAWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new HTMLDumpInterface(), JS_INTERFACE);
        mWebView.loadUrl(WA_URL);
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }
    
    private class WAWebViewClient extends WebViewClient {
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		Log.d(TAG, "shouldOverrideUrlLoading(): url = " + url);
    		
    		// Start external URLs in the browser
    		if (url.equals("http://adminguide.stanford.edu/") || 
    				url.equals("https://weblogin.stanford.edu/settings") ||
    				url.equals("https://weblogin.stanford.edu/help.html")) {
    			getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    			return true;
    		}
    		return false;
    	}
    	
    	@Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
    		mListener.onError(new DialogError(description, errorCode, failingUrl));
    	}
    	
    	@Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }
    	
    	@Override
        public void onPageFinished(WebView view, String url) {
    		Log.d(TAG, "onPageFinished(): url = " + url);
    		super.onPageFinished(view, url);
    		if (url.equals(WA_URL)) {
    			mWebView.loadUrl("javascript:window." + JS_INTERFACE + ".dumpHTML(document.getElementsByTagName('body')[0].innerHTML);");
    			WADialog.this.dismiss();
    		}
    		mSpinner.dismiss();
    	}
    }
    
    private class HTMLDumpInterface {
    	public void dumpHTML(String html) {
    		Log.d(TAG, "html dump = " + html);
    		String[] tokens = html.split(",");
    		if (tokens.length == 3) {
	    		Bundle values = new Bundle();
	    		values.putString(WebAuth.SUID_KEY, tokens[0]);
	    		values.putString(WebAuth.FIRSTNAME_KEY, tokens[1]);
	    		values.putString(WebAuth.LASTNAME_KEY, tokens[2]);
	    		mListener.onComplete(values);
    		}
    		else
    			mListener.onError(new DialogError("Could not get user info", USERINFO_ERROR, ""));
    	}
    }
}
