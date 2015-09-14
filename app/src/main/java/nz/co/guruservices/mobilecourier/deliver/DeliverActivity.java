package nz.co.guruservices.mobilecourier.deliver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.app.ApplicationContext;
import nz.co.guruservices.mobilecourier.common.app.StringKeyValue;
import nz.co.guruservices.mobilecourier.common.http.AsynHttpResponseHandler;
import nz.co.guruservices.mobilecourier.common.http.GsonAsyncHttpClient;
import nz.co.guruservices.mobilecourier.common.mapper.BeanMapper;
import nz.co.guruservices.mobilecourier.common.mapper.ListConsignmentResponseConsigneeMapper;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.DriverIdentifier;
import nz.co.guruservices.mobilecourier.common.model.GeoPosition;
import nz.co.guruservices.mobilecourier.common.model.ListConsignmentResponse;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;
import nz.co.guruservices.mobilecourier.custom.ConsigneeItemView;
import nz.co.guruservices.mobilecourier.custom.draggable.DragSortListView;
import nz.co.guruservices.mobilecourier.custom.draggable.DragSortListView.DragScrollProfile;
import nz.co.guruservices.mobilecourier.custom.draggable.DragSortListView.RemoveListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeliverActivity extends Activity {

    private ActionBar actionBar;

    // drag-n-drop list view
    private DragSortListView listView;

    private TextView noDeliverMsgView;

    private DeliverConsigneesAdapter adapter;

    private ApplicationContext applicationContext;

    public static final String INTENT_PARAM_GEO_POSITIONS = "geoPositions";

    public static final String INTENT_PARAM_CONSIGNEE = "deliverConsignee";

    private static final int START_PROCESS_DELIVERY_ACTIVITY = 1;

    private AlertDialog errorDialog;

    private final BeanMapper<ListConsignmentResponse, List<Consignee>> mapper = new ListConsignmentResponseConsigneeMapper();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	applicationContext = (ApplicationContext) getApplication();

	// enable to show the spinner on action bar
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setProgressBarIndeterminateVisibility(false);

	setContentView(R.layout.deliver);

	initActionBar();

	noDeliverMsgView = (TextView) findViewById(R.id.text_no_deliver_available);

	errorDialog = AndroidUtil.buildAlertDialog(this, "Error", PopuDialogType.ERROR);

	listView = (DragSortListView) findViewById(R.id.consignee_list);
	listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

	listView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

		final Intent deliveryIntent = new Intent(DeliverActivity.this, DeliverOperationActivity.class);
		deliveryIntent.putExtra(INTENT_PARAM_CONSIGNEE, (Consignee) adapter.getItem(position));
		startActivityForResult(deliveryIntent, START_PROCESS_DELIVERY_ACTIVITY);
	    }
	});

	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	    @Override
	    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		// If your long-click method returns true then you are telling Android that you handled the event and nobody else should
		// know about the long-press. If your method returns false, Android will still call other handlers such as your onItemClick
		// handler.
		final Consignee consignee = (Consignee) adapter.getItem(position);
		if (!TextUtils.isEmpty(consignee.getPhone())) {
		    final String uri = "tel:" + consignee.getPhone();
		    final Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
		    startActivity(callIntent);
		}
		return true;
	    }
	});

	listView.setDropListener(adapter);
	listView.setRemoveListener(adapter);
	listView.setDragScrollProfile(adapter);

	addPhoneListener();

	loadConsignees();

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	// on delivery page, delivery operation has been performed
	if (requestCode == START_PROCESS_DELIVERY_ACTIVITY && resultCode == RESULT_OK) {
	    loadConsignees();
	}
    }

    private void loadConsignees() {
	try {
	    final DriverIdentifier driverIdentifier = new DriverIdentifier(AndroidUtil.getStringValueFromSharedPreferences(this, AndroidUtil.AUTH_TOKEN));

	    final GsonAsyncHttpClient<ListConsignmentResponse> client = new GsonAsyncHttpClient<ListConsignmentResponse>(this,
		    applicationContext.constructRestUrl("consignment/list/todeliver"));

	    client.post(driverIdentifier,
		    new AsynHttpResponseHandler<ListConsignmentResponse>(this, "Load deliver", ListConsignmentResponse.class, errorDialog) {

		@Override
		public void onStart() {
		    // use spinner in action bar
		    DeliverActivity.this.setProgressBarIndeterminateVisibility(true);
		    // use progress dialog
		    // progressDialog = AndroidUtil.showProgressSpinner(parent);
		}

		@Override
		public void onFinish() {
		    // use spinner in action bar
		    DeliverActivity.this.setProgressBarIndeterminateVisibility(false);
		    // use progress dialog
		    // progressDialog.dismiss();
		}

		@Override
		protected void processSuccess(final ListConsignmentResponse response) {
		    updateConsignees(mapper.map(response));
		}
	    });
	} catch (final Exception e) {
	    e.printStackTrace();
	    errorDialog.setMessage(String.format("%s failed: %s", "Load deliver data", e.getMessage()));
	    errorDialog.show();
	}
    }

    private void updateConsignees(final List<Consignee> consignees) {
	if (consignees.size() == 0) {
	    noDeliverMsgView.setVisibility(View.VISIBLE);
	} else {
	    noDeliverMsgView.setVisibility(View.GONE);
	}

	sortConsignees(consignees);
	adapter = new DeliverConsigneesAdapter(this, consignees);
	listView.setAdapter(adapter);
    }

    private void initActionBar() {
	actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);
	// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
	getMenuInflater().inflate(R.menu.devliver_route, menu);
	return true;
    }

    private final static Double ZERO_D = new Double(0.0);

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	final int itemId = item.getItemId();
	switch (itemId) {
	    case android.R.id.home: {
		final Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		return true;
	    }
	    case R.id.action_delivery_route:
		if (adapter.getConsignees().size() > 0) {
		    final ArrayList<GeoPosition> geoPositions = new ArrayList<GeoPosition>();
		    for (int i = 0; i < adapter.getConsignees().size(); i++) {
			final Consignee consignee = adapter.getConsignees().get(i);
			if (ZERO_D.equals(new Double(consignee.getLat())) && ZERO_D.equals(new Double(consignee.getLon()))) {
			    continue;
			}
			final GeoPosition geoPosition = new GeoPosition();
			geoPosition.setTitle(consignee.getName());
			geoPosition.setLatitude(consignee.getLat());
			geoPosition.setLongitude(consignee.getLon());
			geoPositions.add(geoPosition);
		    }

		    final Intent routeIntent = new Intent(this, RouteActivity.class);
		    routeIntent.putExtra(INTENT_PARAM_GEO_POSITIONS, geoPositions);
		    startActivity(routeIntent);
		}

	    default:
		return super.onOptionsItemSelected(item);
	}

    }

    private void sortConsignees(final List<Consignee> consignees) {
	// get order string from SharedPreferences
	final String consigneeIdString = AndroidUtil.getStringValueFromSharedPreferences(this, AndroidUtil.DELIVER_CONSIGNEES_ID_ORDER);

	if (!TextUtils.isEmpty(consigneeIdString)) {
	    final String[] consigneeIds = TextUtils.split(consigneeIdString, ",");

	    final int maxOrderIndex = Math.max(consigneeIds.length, consignees.size());

	    // consignee id maps to order index
	    final Map<Long, Integer> consigneeIdIndexMap = new HashMap<Long, Integer>();
	    for (int i = 0; i < consigneeIds.length; i++) {
		consigneeIdIndexMap.put(Long.valueOf(consigneeIds[i]), i);
	    }

	    for (final Consignee consignee : consignees) {
		final Integer orderIndex = consigneeIdIndexMap.get(consignee.getId());
		// for those consignees was not ordered before, append to the
		// list (with the biggest orderIndex)
		consignee.setOrderIndex(orderIndex != null ? orderIndex : maxOrderIndex);
	    }

	    Collections.sort(consignees, new Comparator<Consignee>() {
		@Override
		public int compare(final Consignee consignee1, final Consignee consignee2) {
		    return Integer.valueOf(consignee1.getOrderIndex()).compareTo(Integer.valueOf(consignee2.getOrderIndex()));
		}
	    });

	}

	saveConsigneeOrderToSharedPreferences(consignees);
    }

    // synchronize order string to sharedPreference
    private void saveConsigneeOrderToSharedPreferences(final List<Consignee> consignees) {
	final StringBuilder orderBuilder = new StringBuilder();
	boolean isFirst = true;
	for (final Consignee consignee : consignees) {
	    orderBuilder.append(isFirst ? "" : ",").append(consignee.getId());
	    if (isFirst) {
		isFirst = false;
	    }
	}
	AndroidUtil.saveValueInSharedPreferences(this, new StringKeyValue(AndroidUtil.DELIVER_CONSIGNEES_ID_ORDER, orderBuilder.toString()));
    }

    private class DeliverConsigneesAdapter extends BaseAdapter implements DragSortListView.DropListener, RemoveListener, DragScrollProfile {

	private Context context;

	private List<Consignee> consignees;

	public DeliverConsigneesAdapter(final Context context, final List<Consignee> consignees) {
	    super();
	    this.context = context;
	    this.consignees = consignees;
	}

	@Override
	public int getCount() {
	    return consignees.size();
	}

	@Override
	public Object getItem(final int position) {
	    return consignees.get(position);
	}

	@Override
	public long getItemId(final int position) {
	    return consignees.get(position).getId();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
	    final Consignee consignee = (Consignee) getItem(position);

	    if (convertView == null) {
		final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.consignee_list_item, null);
	    }

	    final ConsigneeItemView consigneeItemView = (ConsigneeItemView) convertView.findViewById(R.id.consignee_item);
	    consigneeItemView.setValue(consignee);

	    if (!TextUtils.isEmpty(consignee.getPhone())) {
		consigneeItemView.setShowPhoneIcon(true);
	    } else {
		consigneeItemView.setShowPhoneIcon(false);
	    }

	    return convertView;
	}

	@Override
	public void drop(final int from, final int to) {
	    final Consignee item = (Consignee) getItem(from);
	    consignees.remove(item);
	    consignees.add(to, item);
	    notifyDataSetChanged();
	    saveConsigneeOrderToSharedPreferences(consignees);
	}

	@Override
	public void remove(final int which) {
	    consignees.remove(which);

	}

	@Override
	public float getSpeed(final float w, final long t) {
	    if (w > 0.8f) {
		// Traverse all views in a millisecond
		return adapter.getCount() / 0.001f;
	    } else {
		return 10.0f * w;
	    }
	}

	public List<Consignee> getConsignees() {
	    return consignees;
	}

    }

    private void addPhoneListener() {
	final MyPhoneListener phoneListener = new MyPhoneListener();

	final TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

	// receive notifications of telephony state changes
	telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyPhoneListener extends PhoneStateListener {

	private boolean onCall = false;

	@Override
	public void onCallStateChanged(final int state, final String incomingNumber) {

	    switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
		    // phone ringing...
		    break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
		    // one call exists that is dialing, active, or on hold, because user answers the incoming call
		    onCall = true;
		    break;
		case TelephonyManager.CALL_STATE_IDLE:
		    // in initialization of the class and at the end of phone call
		    // detect flag from CALL_STATE_OFFHOOK
		    if (onCall == true) {
			// restart our application
			// final Intent restart =
			// getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
			// restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(restart);
			onCall = false;
		    }
		    break;
		default:
		    break;
	    }
	}
    }

}
