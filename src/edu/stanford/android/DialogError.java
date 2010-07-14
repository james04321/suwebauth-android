package edu.stanford.android;

public class DialogError extends Throwable {
    
    private static final long serialVersionUID = 1L;

    /** 
     * The ErrorCode received by the WebView: see
     * http://developer.android.com/reference/android/webkit/WebViewClient.html
     */
    private int mErrorCode;
    
    /** The URL that the dialog was trying to load */
    private String mFailingUrl;

    public DialogError(String message, int errorCode, String failingUrl) {
        super(message);
        mErrorCode = errorCode;
        mFailingUrl = failingUrl;
    }
    
    public int getErrorCode() {
        return mErrorCode;
    }
    
    public String getFailingUrl() {
        return mFailingUrl;
    }
    
    public String toString() {
    	return "(" + mErrorCode + ") " + getMessage();
    }
    
}