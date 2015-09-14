package nz.co.guruservices.mobilecourier.home;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.abouthelp.AboutActivity;
import nz.co.guruservices.mobilecourier.abouthelp.HelpActivity;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.app.ApplicationContext;
import nz.co.guruservices.mobilecourier.common.http.AsynHttpResponseHandler;
import nz.co.guruservices.mobilecourier.common.http.GsonAsyncHttpClient;
import nz.co.guruservices.mobilecourier.common.model.DriverIdentifier;
import nz.co.guruservices.mobilecourier.common.model.JobSummary;
import nz.co.guruservices.mobilecourier.common.model.JobSummaryResponse;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;
import nz.co.guruservices.mobilecourier.common.model.Response;
import nz.co.guruservices.mobilecourier.consignment.ConsignmentActivity;
import nz.co.guruservices.mobilecourier.deliver.DeliverActivity;
import nz.co.guruservices.mobilecourier.login.LoginActivity;
import nz.co.guruservices.mobilecourier.returns.ReturnsActivity;
import nz.co.guruservices.mobilecourier.settings.SettingsActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class HomeActivity extends Activity {

    private ActionBar actionBar;

    private AlertDialog errorDialog;

    private String authToken;

    private ApplicationContext applicationContext;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// enable to show the spinner on action bar
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setProgressBarIndeterminateVisibility(false);

	applicationContext = (ApplicationContext) getApplication();
	authToken = AndroidUtil.getStringValueFromSharedPreferences(this, AndroidUtil.AUTH_TOKEN);

	setContentView(R.layout.home);

	actionBar = getActionBar();
	// hide home icon
	// actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
	// actionBar.setTitle(getResources().getString(R.string.label_home));

	final ViewGroup buttonConsignment = (ViewGroup) findViewById(R.id.bnt_consignments);
	buttonConsignment.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		final Intent intent = new Intent(HomeActivity.this, ConsignmentActivity.class);
		startActivity(intent);

	    }
	});

	final ViewGroup buttonDeliver = (ViewGroup) findViewById(R.id.bnt_deliver);
	buttonDeliver.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		final Intent intent = new Intent(HomeActivity.this, DeliverActivity.class);
		startActivity(intent);

	    }
	});

	final ViewGroup buttonReturns = (ViewGroup) findViewById(R.id.bnt_returns);
	buttonReturns.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		final Intent intent = new Intent(HomeActivity.this, ReturnsActivity.class);
		startActivity(intent);
	    }
	});

	errorDialog = AndroidUtil.buildAlertDialog(this, "Error", PopuDialogType.ERROR);

	makeActionOverflowMenuShown();

    }

    // devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
    private void makeActionOverflowMenuShown() {
	try {
	    final ViewConfiguration config = ViewConfiguration.get(this);
	    final Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	    if (menuKeyField != null) {
		menuKeyField.setAccessible(true);
		menuKeyField.setBoolean(config, false);
	    }
	} catch (final Exception e) {
	    Log.d("HomeActivity", e.getLocalizedMessage());
	}
    }

    private void loadJobSummary() {
	try {

	    final DriverIdentifier driverIdentifier = new DriverIdentifier(authToken);

	    final GsonAsyncHttpClient<JobSummaryResponse> client = new GsonAsyncHttpClient<JobSummaryResponse>(this,
		    applicationContext.constructRestUrl("consignment/summary"));
	    client.post(driverIdentifier, new AsynHttpResponseHandler<JobSummaryResponse>(this, "Return consignments", JobSummaryResponse.class, errorDialog) {

		@Override
		public void onStart() {
		    setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onFinish() {
		    setProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected void processSuccess(final JobSummaryResponse response) {
		    final JobSummary jobSummary = response.getResult();
		    if (jobSummary == null) {
			return;
		    }

		    final TextView driverView = (TextView) findViewById(R.id.summary_driver);
		    driverView.setText(jobSummary.getDriverName());

		    final TextView organisationView = (TextView) findViewById(R.id.summary_organisation);
		    organisationView.setText(jobSummary.getOrganisation());

		    final TextView areaView = (TextView) findViewById(R.id.summary_area);
		    areaView.setText(jobSummary.getArea());

		    final TextView myConsignmentView = (TextView) findViewById(R.id.summary_my_consignment);
		    myConsignmentView.setText(String.valueOf(jobSummary.getMyConsignmentSubTotal()));

		    final TextView openConsignmentView = (TextView) findViewById(R.id.summary_open_consignment);
		    openConsignmentView.setText(String.valueOf(jobSummary.getOpenConsignmentSubTotal()));

		    final TextView loadedConsignmentView = (TextView) findViewById(R.id.summary_loaded_consignment);
		    loadedConsignmentView.setText(String.valueOf(jobSummary.getLoadedConsignmentSubTotal()));
		}

	    });

	} catch (final Exception e) {
	    e.printStackTrace();
	    errorDialog.setMessage(String.format("%s failed: %s", "Load consignment summary", e.getMessage()));
	    errorDialog.show();
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
	loadJobSummary();
    };

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

	final int itemId = item.getItemId();

	switch (itemId) {
	    case android.R.id.home:
		return true;
	    case R.id.action_about:
		final Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
		startActivity(aboutIntent);
		return true;
	    case R.id.action_help:
		final Intent helpIntent = new Intent(HomeActivity.this, HelpActivity.class);
		startActivity(helpIntent);
		return true;
	    case R.id.action_settings:
		final Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
		startActivity(intent);
		return true;
	    case R.id.action_logout:
		AndroidUtil.clearSharedPreferences(this);
		final Intent loginIntent = new Intent(this, LoginActivity.class);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(loginIntent);
		finish();
		return true;
	}

	return super.onOptionsItemSelected(item);

    }

    private void performLogout() {
	try {

	    final DriverIdentifier driverIdentifier = new DriverIdentifier(authToken);

	    final GsonAsyncHttpClient<Response> client = new GsonAsyncHttpClient<Response>(this, applicationContext.constructRestUrl("logout"));
	    client.post(driverIdentifier, new AsynHttpResponseHandler<Response>(this, "Logout", Response.class, errorDialog) {

		@Override
		public void onStart() {
		    setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onFinish() {
		    setProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected void processSuccess(final Response response) {
		    AndroidUtil.clearSharedPreferences(HomeActivity.this);
		    final Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
		    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		    startActivity(loginIntent);
		    finish();
		}

	    });

	} catch (final Exception e) {
	    errorDialog.setMessage(String.format("%s failed: %s", "Logout", e.getMessage()));
	    errorDialog.show();
	}

    }

    /**
     * on Android 3.0+, the icons in the menu are not shown by design. This is a design decision by Google.<br/>
     * The following is workaround telling the overflow menu to display the icons directly.
     */
    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
	if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
	    if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
		try {
		    final Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
		    m.setAccessible(true);
		    m.invoke(menu, true);
		} catch (final NoSuchMethodException e) {
		    Log.e("onMenuOpened", "onMenuOpened", e);
		} catch (final Exception e) {
		    throw new RuntimeException(e);
		}
	    }
	}
	return super.onMenuOpened(featureId, menu);
    }
}
