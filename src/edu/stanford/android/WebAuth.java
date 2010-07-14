package edu.stanford.android;

import android.content.Context;
import android.os.Bundle;

public class WebAuth {
	public static String SUID_KEY = "suid";
	public static String FIRSTNAME_KEY = "first_name";
	public static String LASTNAME_KEY = "last_name";
	public static String EMAIL_SUFFIX = "@stanford.edu";
	
	public WebAuth() {}
	
	public void authorize(Context context, final DialogListener listener) {
		new WADialog(context, listener).show();
	}
	
	/**
     * Callback interface for dialog requests.
     *
     */
    public static interface DialogListener {

        /**
         * Called when a dialog completes.
         * 
         * Executed by the thread that initiated the dialog.
         * 
         * @param values
         *            Key-value string pairs extracted from the response.
         */
        public void onComplete(Bundle values);
        
        /**
         * Called when a dialog has an error.
         * 
         * Executed by the thread that initiated the dialog.
         * 
         */        
        public void onError(DialogError e); 
    }
}
