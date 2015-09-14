package nz.co.guruservices.mobilecourier.consignment;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;
import nz.co.guruservices.mobilecourier.settings.SettingsActivity;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.barcode.ScannerHandler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

public class ConsignmentActivity extends FragmentActivity {

    private ViewPager viewPager;

    private TabsPagerAdapter consignmentAdapter;

    private ActionBar actionBar;

    // Tab titles
    private String[] tabNames;

    private CheckBox selectAllCheckbox;

    private ConsignmentFragment currentConsignmentFragment;

    private AlertDialog alertDialog;

    private AlertDialog errorDialog;

    private boolean scannerEnabled;

    private ScannerHandler scannerHandler;

    private BroadcastReceiver barcodeBroadcastReceiver;

    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";

    private static final String BARCODE_PARAM = "BARCODE";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// enable to show the spinner on action bar
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setProgressBarIndeterminateVisibility(false);

	setContentView(R.layout.consignment);

	initActionBar();

	alertDialog = AndroidUtil.buildAlertDialog(this, "Alert", PopuDialogType.ALERT);
	errorDialog = AndroidUtil.buildAlertDialog(this, "Error", PopuDialogType.ERROR);

	// check if display splash screen is set
	final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	scannerEnabled = sharedPref.getBoolean(SettingsActivity.KEY_PREF_ENABLED_HD_SCANNER, false);

	barcodeBroadcastReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(final Context context, final Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();
		if (BARCODE_ACTION.equals(action)) {
		    final String barcode = intent.getStringExtra(BARCODE_PARAM);
		    if (currentConsignmentFragment != null) {
			currentConsignmentFragment.onBarcodeScanner(barcode);
		    }
		}
	    }
	};

	initTabView();

    }

    @Override
    protected void onStart() {
	super.onStart();
    }

    @SuppressWarnings("deprecation")
    private void initActionBar() {
	actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);
	actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	// action bar TextView
	// final int titleId = getResources().getIdentifier("action_bar_title",
	// "id", "android");
	// final TextView actionBarTextView = (TextView) findViewById(titleId);
    }

    @SuppressWarnings("deprecation")
    private void initTabView() {
	tabNames = getResources().getStringArray(R.array.label_consignment_tabs);

	viewPager = (ViewPager) findViewById(R.id.consignment_pager);

	consignmentAdapter = new TabsPagerAdapter(getSupportFragmentManager());

	viewPager.setAdapter(consignmentAdapter);

	/**
	 * on swiping the viewpager make respective tab selected
	 * */
	viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

	    @Override
	    public void onPageSelected(final int position) {
		// on changing the page, make respected tab selected
		actionBar.setSelectedNavigationItem(position);

		selectAllCheckbox.setChecked(false);

		// load the current segment
		currentConsignmentFragment = (ConsignmentFragment) getSupportFragmentManager().findFragmentByTag(
		        "android:switcher:" + R.id.consignment_pager + ":" + viewPager.getCurrentItem());

		loadConsignments();
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

	// Adding Tabs
	for (final String tabName : tabNames) {
	    actionBar.addTab(actionBar.newTab().setText(tabName).setTabListener(tabListener));
	}

    }

    /**
     * this method relys on the currentConsignmentFragment is set
     */
    public void loadConsignments() {
	try {
	    currentConsignmentFragment.loadConsignments();
	} catch (final Exception e) {
	    e.printStackTrace();
	    showError(String.format("%s failed: %s", currentConsignmentFragment.getActionName(), e.getMessage()));
	}
    }

    private void updateActionBarTitle(final int tabPosition) {
	switch (tabPosition) {
	    case 0:
		actionBar.setTitle(constrcutActionTitleFromSelectedTab(R.string.label_my_consignment_tab));
		break;
	    case 1:
		actionBar.setTitle(constrcutActionTitleFromSelectedTab(R.string.label_open_consignment_tab));
		break;
	    case 2:
		actionBar.setTitle(constrcutActionTitleFromSelectedTab(R.string.label_loaded_consignment_tab));
		break;
	    default:
		break;
	}
    }

    private String getStringByResourceId(final int resourceId) {
	return getResources().getString(resourceId);
    }

    private String constrcutActionTitleFromSelectedTab(final int tabStringResourceId) {
	return getStringByResourceId(R.string.label_consignments) + "/" + getStringByResourceId(tabStringResourceId);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.select_all, menu);
	final MenuItem menuItem = menu.getItem(0);
	selectAllCheckbox = (CheckBox) menuItem.getActionView();

	selectAllCheckbox.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View view) {

		if (selectAllCheckbox.isChecked()) {
		    currentConsignmentFragment.selectAllConsignment();
		} else {
		    currentConsignmentFragment.deSelectAllConsignments();
		}
	    }
	});

	return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	switch (item.getItemId()) {
	    case android.R.id.home:
		finish();
		return true;
	    default:
		return super.onOptionsItemSelected(item);
	}
    }

    public void setCurrentConsignmentFragment(final ConsignmentFragment currentConsignmentFragment) {
	this.currentConsignmentFragment = currentConsignmentFragment;
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

    @Override
    protected void onResume() {
	// TODO Auto-generated method stub
	final IntentFilter intentFilter = new IntentFilter(BARCODE_ACTION);
	registerReceiver(barcodeBroadcastReceiver, intentFilter);
	super.onResume();
    }

}
