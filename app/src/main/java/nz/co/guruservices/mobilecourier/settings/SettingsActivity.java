package nz.co.guruservices.mobilecourier.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

    public static final String KEY_PREF_DISPLAY_SPLASH_SCREEN = "prefDisplaySplashScreen";

    public static final String KEY_PREF_ENABLED_HD_SCANNER = "prefEnableHdScanner";

    public static final String KEY_PREF_LANGUAGE = "prefLanguage";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// Display the fragment as the main content.
	getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	getActionBar().setHomeButtonEnabled(true);

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
