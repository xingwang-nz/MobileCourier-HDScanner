package nz.co.guruservices.mobilecourier.deliver;

import static nz.co.guruservices.mobilecourier.deliver.DeliverActivity.INTENT_PARAM_CONSIGNEE;
import static nz.co.guruservices.mobilecourier.signature.CaptureSignatureActivity.SIGNATURE_DATA;
import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;
import nz.co.guruservices.mobilecourier.consignment.ConsignmentNoteActivity;
import nz.co.guruservices.mobilecourier.signature.CaptureSignatureActivity;
import nz.co.guruservices.mobilecourier.signature.SignatureCallback;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class DeliverOperationActivity extends FragmentActivity {

    private ViewPager viewPager;

    private ActionBar actionBar;

    // Tab titles
    private String[] tabNames;

    private DeliverOperationPagerAdapter adapter;

    private Consignee consignee;

    private SignatureCallback signatureCallback;

    private ConsignmentNoteCallback consignmentNoteCallback;

    private static final int SIGNATURE_ACTIVITY = 1;

    private static final int CONSIGNMENT_NOTE_ACTIVITY = 2;

    private AlertDialog alertDialog;

    private AlertDialog errorDialog;

    private GoogleApiClient googleApiClient;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private Location currentLocation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// enable to show the spinner on action bar
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setProgressBarIndeterminateVisibility(false);
	setContentView(R.layout.deliver_operation);

	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

	alertDialog = AndroidUtil.buildAlertDialog(this, "Alert", PopuDialogType.ALERT);

	errorDialog = AndroidUtil.buildAlertDialog(this, "Error", PopuDialogType.ERROR);

	consignee = (Consignee) getIntent().getExtras().getSerializable(INTENT_PARAM_CONSIGNEE);

	initActionBar();
	initTabView();

	final LocationTracker locationTracker = new LocationTracker();
	googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(locationTracker).addOnConnectionFailedListener(locationTracker)
	        .addApi(LocationServices.API).build();

	initConsignments();

    }

    @Override
    protected void onResume() {
	super.onResume();
	googleApiClient.connect();
    }

    @Override
    protected void onPause() {
	super.onPause();
	if (googleApiClient.isConnected()) {
	    googleApiClient.disconnect();
	}
    }

    // default to select all
    private void initConsignments() {
	for (final Consignment consignment : consignee.getConsignments()) {
	    consignment.setSelected(true);
	    consignment.setAccepted(true);
	}

    }

    @SuppressWarnings("deprecation")
    private void initActionBar() {
	actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);
	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @SuppressWarnings("deprecation")
    private void initTabView() {
	tabNames = getResources().getStringArray(R.array.label_deliver_tabs);
	viewPager = (ViewPager) findViewById(R.id.deliver_pager);
	adapter = new DeliverOperationPagerAdapter(getSupportFragmentManager());
	viewPager.setAdapter(adapter);

	/**
	 * on swiping the viewpager make respective tab selected
	 * */
	viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

	    @Override
	    public void onPageSelected(final int position) {
		// on changing the page, make respected tab selected
		actionBar.setSelectedNavigationItem(position);
	    }

	    @Override
	    public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
	    }

	    @Override
	    public void onPageScrollStateChanged(final int arg0) {
	    }
	});

	final TabListener tabListener = new TabListener() {
	    @Override
	    public void onTabSelected(final Tab tab, final FragmentTransaction ft) {
		// on tab selected show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
		updateActionBarTitle(tab.getPosition());
	    }

	    @Override
	    public void onTabUnselected(final Tab tab, final FragmentTransaction ft) {
	    }

	    @Override
	    public void onTabReselected(final Tab tab, final FragmentTransaction ft) {
	    }
	};

	for (final String tabName : tabNames) {
	    actionBar.addTab(actionBar.newTab().setText(tabName).setTabListener(tabListener));
	}

    }

    private void updateActionBarTitle(final int tabPosition) {
	switch (tabPosition) {
	    case 0:
		actionBar.setTitle(constrcutActionTitleFromSelectedTab(R.string.label_deliver_tab));
		break;
	    case 1:
		actionBar.setTitle(constrcutActionTitleFromSelectedTab(R.string.label_deliver_leave_card));
		break;
	    default:
		break;
	}
    }

    private String constrcutActionTitleFromSelectedTab(final int tabStringResourceId) {
	return getStringByResourceId(R.string.label_deliver) + "/" + getStringByResourceId(tabStringResourceId);
    }

    private String getStringByResourceId(final int resourceId) {
	return getResources().getString(resourceId);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	final int itemId = item.getItemId();
	switch (itemId) {
	    case android.R.id.home: {
		cancelAndReturn();
		return true;
	    }

	    default:
		return super.onOptionsItemSelected(item);
	}

    }

    public Consignee getConsignee() {
	return consignee;
    }

    public void cancelAndReturn() {
	final Intent intent = new Intent();
	setResult(RESULT_CANCELED, intent);
	finish();
    }

    public void processedAndReturn() {
	final Intent intent = new Intent();
	setResult(RESULT_OK, intent);
	finish();
    }

    public void captureSignature(final SignatureCallback signatureCallback) {
	this.signatureCallback = signatureCallback;
	final Intent intent = new Intent(this, CaptureSignatureActivity.class);
	startActivityForResult(intent, SIGNATURE_ACTIVITY);
    }

    public void captureDeliveryNote(final ConsignmentNoteCallback consignmentNoteCallback, final ConsignmentDetail consignmentDetail, final int position) {
	this.consignmentNoteCallback = consignmentNoteCallback;
	final Intent intent = new Intent(this, ConsignmentNoteActivity.class);
	intent.putExtra(ConsignmentNoteActivity.INTENT_PARAM_CONSIGNMENT_DETAIL, consignmentDetail);
	intent.putExtra(ConsignmentNoteActivity.INTENT_PARAM_CONSIGNMENT_POSITION, position);

	startActivityForResult(intent, CONSIGNMENT_NOTE_ACTIVITY);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
	switch (requestCode) {
	    case SIGNATURE_ACTIVITY:
		if (resultCode == RESULT_OK) {
		    final Bundle bundle = intent.getExtras();
		    final String status = bundle.getString(CaptureSignatureActivity.SIGNATURE_STATUS);
		    if (status.equalsIgnoreCase(CaptureSignatureActivity.SIGNATURE_DONE)) {
			signatureCallback.onSign(bundle.get(SIGNATURE_DATA));
		    }
		}
		break;
	    case CONSIGNMENT_NOTE_ACTIVITY:
		if (resultCode == RESULT_OK) {
		    final Bundle bundle = intent.getExtras();
		    final String note = bundle.getString(ConsignmentNoteActivity.INTENT_PARAM_CONSIGNMENT_NOTE);
		    final int index = bundle.getInt(ConsignmentNoteActivity.INTENT_PARAM_CONSIGNMENT_POSITION);
		    consignmentNoteCallback.setConsignmentNote(note, index);
		}
		break;
	}

    }

    public void showAlertDialog(final String message) {
	alertDialog.setMessage(message);
	alertDialog.show();
    }

    public void showError(final String message) {
	errorDialog.setMessage(message);
	errorDialog.show();
    }

    public AlertDialog getErrorDialog() {
	return errorDialog;
    }

    private class LocationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	private final LocationRequest locationRequest;

	public LocationTracker() {
	    locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000).setFastestInterval(1 * 1000);
	}

	@Override
	public void onConnectionFailed(final ConnectionResult connectionResult) {
	    if (connectionResult.hasResolution()) {
		try {
		    // Start an Activity that tries to resolve the error
		    connectionResult.startResolutionForResult(DeliverOperationActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
		} catch (final IntentSender.SendIntentException e) {
		    e.printStackTrace();
		}
	    } else {
		Log.i("location", "Location services connection failed with code " + connectionResult.getErrorCode());
	    }
	}

	@Override
	public void onConnected(final Bundle bundle) {
	    final Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
	    if (location == null) {
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	    } else {
		handleNewLocation(location);
	    }
	}

	@Override
	public void onConnectionSuspended(final int i) {
	}

	@Override
	public void onLocationChanged(final Location location) {
	    handleNewLocation(location);
	}

    }

    private void handleNewLocation(final Location location) {
	this.currentLocation = location;
	Log.d("location", location.toString());
    }

    public Location getLocation() {
	return currentLocation;
    }

}
