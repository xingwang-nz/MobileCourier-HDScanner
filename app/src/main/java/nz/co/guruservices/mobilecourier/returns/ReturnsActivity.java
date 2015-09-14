package nz.co.guruservices.mobilecourier.returns;

import static nz.co.guruservices.mobilecourier.signature.CaptureSignatureActivity.SIGNATURE_DATA;

import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.app.ApplicationContext;
import nz.co.guruservices.mobilecourier.common.http.AsynHttpResponseHandler;
import nz.co.guruservices.mobilecourier.common.http.GsonAsyncHttpClient;
import nz.co.guruservices.mobilecourier.common.mapper.BeanMapper;
import nz.co.guruservices.mobilecourier.common.mapper.ListConsignmentResponseConsigneeMapper;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import nz.co.guruservices.mobilecourier.common.model.DriverIdentifier;
import nz.co.guruservices.mobilecourier.common.model.ItemData;
import nz.co.guruservices.mobilecourier.common.model.ListConsignmentResponse;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;
import nz.co.guruservices.mobilecourier.common.model.Response;
import nz.co.guruservices.mobilecourier.common.model.ReturnRequest;
import nz.co.guruservices.mobilecourier.consignment.ConsignmentDetailActivity;
import nz.co.guruservices.mobilecourier.signature.CaptureSignatureActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnsActivity extends Activity {

    private ActionBar actionBar;

    private ApplicationContext applicationContext;

    private String authToken;

    private ListView listView;

    private ReturnConsignmentListAdapter adapter;

    private TextView noMessageText;

    private ViewGroup button;

    private final BeanMapper<ListConsignmentResponse, List<Consignee>> mapper = new ListConsignmentResponseConsigneeMapper();

    private AlertDialog alertDialog;

    private AlertDialog errorDialog;

    private ScrollView contentScrollView;

    private String signatureBase64String;

    private ImageView signatureView;

    private static final int SIGNATURE_ACTIVITY = 1;

    private CheckBox selectAllCheckbox;

    private EditText printNameField;

    private EditText noteField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// enable to show the spinner on action bar
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setProgressBarIndeterminateVisibility(false);
	applicationContext = (ApplicationContext) getApplication();
	authToken = AndroidUtil.getStringValueFromSharedPreferences(this, AndroidUtil.AUTH_TOKEN);

	setContentView(R.layout.returns);
	initActionBar();
	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

	contentScrollView = (ScrollView) findViewById(R.id.content_scroll_view);
	printNameField = (EditText) findViewById(R.id.input_consignment_delivery_print_name);
	noteField = (EditText) findViewById(R.id.input_consignment_return_note);

	listView = (ListView) findViewById(R.id.return_consignment_list);
	noMessageText = (TextView) findViewById(R.id.text_no_return_consignment);

	adapter = new ReturnConsignmentListAdapter(this, new ArrayList<Consignee>());
	listView.setAdapter(adapter);

	// on child click listener
	listView.setOnItemClickListener(new OnItemClickListener() {
	    @Override
	    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

		final ItemData itemData = adapter.getItemById((int) id);
		if (itemData instanceof ReturnConsignmentCategory) {
		    // click on header ignore;
		    return;
		}

		final Consignment consignment = (Consignment) itemData;
		final Consignee consignee = adapter.getConsigneeForConsignment(consignment);
		final ConsignmentDetail consignmentDetail = new ConsignmentDetail(consignee, consignment);

		final Intent consignmentDetailsIntent = new Intent(ReturnsActivity.this, ConsignmentDetailActivity.class);
		consignmentDetailsIntent.putExtra(ConsignmentDetailActivity.INTENT_PARAM_CONSIGNMENT_DETAIL, consignmentDetail);
		startActivity(consignmentDetailsIntent);
	    }
	});

	initSignatureView();

	button = (ViewGroup) findViewById(R.id.bnt_consignment_return);
	button.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		if (!adapter.hasConsignments()) {
		    return;
		}
		if (!adapter.hasSelectedConsignment()) {
		    alertDialog.setMessage("Please select consignments");
		    alertDialog.show();
		    return;
		}
		if (TextUtils.isEmpty(signatureBase64String)) {
		    alertDialog.setMessage("Signature required");
		    alertDialog.show();
		    contentScrollView.post(new Runnable() {
			@Override
			public void run() {
			    contentScrollView.scrollTo(signatureView.getLeft(), signatureView.getBottom());
			}
		    });

		    return;

		}
		performReturnConsignments();
	    }

	});

	alertDialog = AndroidUtil.buildAlertDialog(this, "Alert", PopuDialogType.ALERT);
	errorDialog = AndroidUtil.buildAlertDialog(this, "Error", PopuDialogType.ERROR);

	loadConsignments();
    }

    private void initSignatureView() {
	signatureView = (ImageView) findViewById(R.id.img_signature);
	signatureView.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		final Intent intent = new Intent(ReturnsActivity.this, CaptureSignatureActivity.class);
		startActivityForResult(intent, SIGNATURE_ACTIVITY);
	    }
	});

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	switch (requestCode) {
	    case SIGNATURE_ACTIVITY:
		if (resultCode == RESULT_OK) {

		    final Bundle bundle = data.getExtras();
		    final String status = bundle.getString(CaptureSignatureActivity.SIGNATURE_STATUS);
		    if (status.equalsIgnoreCase(CaptureSignatureActivity.SIGNATURE_DONE)) {
			signatureBase64String = (String) bundle.get(SIGNATURE_DATA);
			AndroidUtil.loadBitmapDataToView(signatureView, AndroidUtil.decodeBase64String(signatureBase64String));
		    }
		}
		break;
	}

    }

    private void performReturnConsignments() {
	try {

	    final ReturnRequest request = new ReturnRequest();
	    request.setAuthToken(authToken);
	    final List<Consignment> selectedConsignments = adapter.getSelectedConsignments();
	    for (final Consignment consignment : selectedConsignments) {
		request.addConsignmentId(consignment.getId());
	    }
	    final String note = noteField.getText().toString();
	    if (!TextUtils.isEmpty(note)) {
		request.setDeliveryNote(note);
	    }

	    final String printName = printNameField.getText().toString();
	    if (!TextUtils.isEmpty(printName)) {
		request.setPrintName(printName);
	    }

	    Log.d("signatureBase64StringLength: ", String.valueOf(signatureBase64String.length()));
	    request.setSignatureData(signatureBase64String);

	    final GsonAsyncHttpClient<Response> client = new GsonAsyncHttpClient<Response>(this, applicationContext.constructRestUrl("consignment/revoke"));
	    client.post(request, new AsynHttpResponseHandler<Response>(this, "Return consignments", Response.class, errorDialog) {

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
		    Toast.makeText(ReturnsActivity.this, String.format("%s consignment(s) have been returned successfully", selectedConsignments.size()),
			    Toast.LENGTH_LONG).show();
		    final Intent intent = new Intent();
		    setResult(RESULT_OK, intent);
		    finish();

		}

	    });

	} catch (final Exception e) {
	    e.printStackTrace();
	    errorDialog.setMessage(String.format("%s failed: %s", "Return consignments", e.getMessage()));
	    errorDialog.show();
	}

    }

    private void loadConsignments() {
	try {
	    doLoadConsignments();
	} catch (final Exception e) {
	    e.printStackTrace();
	    errorDialog.setMessage(String.format("%s failed: %s", "Load return consignments", e.getMessage()));
	    errorDialog.show();
	    // Toast.makeText(ReturnsActivity.this, String.format("%s failed: %s", "Load return consignments", e.getMessage()),
	    // Toast.LENGTH_LONG).show();
	}
    }

    private void doLoadConsignments() throws Exception {

	final DriverIdentifier driverIdentifier = new DriverIdentifier(authToken);

	final ReturnRequest request = new ReturnRequest();
	request.setAuthToken(authToken);

	final GsonAsyncHttpClient<ListConsignmentResponse> client = new GsonAsyncHttpClient<ListConsignmentResponse>(this,
		applicationContext.constructRestUrl("consignment/list/torevoke"));

	client.post(driverIdentifier, new AsynHttpResponseHandler<ListConsignmentResponse>(this, "Load return consignments", ListConsignmentResponse.class,
		errorDialog) {

	    @Override
	    public void onStart() {
		setProgressBarIndeterminateVisibility(true);
	    }

	    @Override
	    public void onFinish() {
		setProgressBarIndeterminateVisibility(false);
	    }

	    @Override
	    protected void processSuccess(final ListConsignmentResponse response) {

		setConsignments(mapper.map(response));
		// setConsignments(new ArrayList<Consignee>());
	    }

	});

    }

    private void setConsignments(final List<Consignee> consignees) {

	adapter.setData(consignees);
	// to solve 'ListView inside ScrollView' problem
	AndroidUtil.setListViewSize(listView);

	if (!adapter.hasConsignments()) {
	    noMessageText.setText("No return consignments");
	    contentScrollView.setVisibility(View.GONE);
	    noMessageText.setVisibility(View.VISIBLE);

	} else {
	    noMessageText.setText("");
	    noMessageText.setVisibility(View.GONE);
	    contentScrollView.setVisibility(View.VISIBLE);
	}
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
		    adapter.selectAllConsignment();
		} else {
		    adapter.deSelectAllConsignments();
		}
	    }
	});

	return true;
    }

}
