package nz.co.guruservices.mobilecourier.abouthelp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import nz.co.guruservices.mobilecourier.R;

public class AboutActivity extends Activity {
    private ActionBar actionBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        initActionBar();

        final TextView versionText = (TextView) findViewById(R.id.text_about_version);
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText.setText("Version " + pInfo.versionName);
        } catch (final NameNotFoundException e) {
            e.printStackTrace();
        }

        final TextView contentText = (TextView) findViewById(R.id.text_about_content);
        contentText.setText("Guru Services help your business through mobile apps and cloud computing");

        final TextView contactText = (TextView) findViewById(R.id.text_about_contact);
        contactText.setText("Please contact Jason 0211842036");

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
