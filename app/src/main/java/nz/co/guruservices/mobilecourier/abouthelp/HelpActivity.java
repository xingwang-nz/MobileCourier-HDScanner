package nz.co.guruservices.mobilecourier.abouthelp;

import nz.co.guruservices.mobilecourier.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

public class HelpActivity extends Activity {
    private ActionBar actionBar;

    private WebView webView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.help);

	initActionBar();

	webView = (WebView) findViewById(R.id.help_web_view);
	webView.getSettings().setJavaScriptEnabled(true);
	webView.loadUrl("file:///android_asset/help.html");

    }

    private void initActionBar() {
	actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	final int itemId = item.getItemId();
	switch (itemId) {

	    case android.R.id.home: {
		final Intent intent = new Intent();
		// intent.setData(Uri.parse(String.format("Order has been sent successfully.")));
		setResult(RESULT_OK, intent);
		finish();
		return true;
	    }
	    default:
		return true;
	}

    }
}