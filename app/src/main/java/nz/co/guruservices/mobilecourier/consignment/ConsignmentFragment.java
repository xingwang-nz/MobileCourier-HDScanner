package nz.co.guruservices.mobilecourier.consignment;

import java.util.List;

import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.app.ApplicationContext;
import nz.co.guruservices.mobilecourier.common.http.AsynHttpResponseHandler;
import nz.co.guruservices.mobilecourier.common.http.GsonAsyncHttpClient;
import nz.co.guruservices.mobilecourier.common.mapper.BeanMapper;
import nz.co.guruservices.mobilecourier.common.mapper.ListConsignmentResponseConsigneeMapper;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentListProcessRequest;
import nz.co.guruservices.mobilecourier.common.model.DriverIdentifier;
import nz.co.guruservices.mobilecourier.common.model.ListConsignmentResponse;
import nz.co.guruservices.mobilecourier.common.model.Response;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * A super class of consignment to process common consignment loading
 */
public abstract class ConsignmentFragment extends Fragment {

    protected ConsignmentActivity parent;

    protected ApplicationContext applicationContext;

    protected ProgressDialog progressDialog;

    protected ConsignmentListAdapter consignmentListAdapter;

    protected String authToken;

    private final BeanMapper<ListConsignmentResponse, List<Consignee>> mapper = new ListConsignmentResponseConsigneeMapper();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	parent = (ConsignmentActivity) getActivity();
	applicationContext = (ApplicationContext) parent.getApplication();
	authToken = AndroidUtil.getStringValueFromSharedPreferences(parent, AndroidUtil.AUTH_TOKEN);

	return initializeFragmentView(inflater, container, savedInstanceState);
    }

    protected abstract View initializeFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * Gets REST sub resource path for concatenate full URL string
     *
     * @return
     */
    protected abstract String getRetrieveConsignmentsRestUrlPath();

    protected abstract void beforeLoadConsignments();

    protected abstract void afterLoadConsignments();

    protected abstract ExpandableListView getConsignmentListView();

    protected abstract void onBarcodeScanner(String barcode);

    /**
     * @return name of action
     */
    protected abstract String getActionName();

    public void loadConsignments() throws Exception {
	final DriverIdentifier driverIdentifier = new DriverIdentifier(authToken);

	final GsonAsyncHttpClient<ListConsignmentResponse> client = new GsonAsyncHttpClient<ListConsignmentResponse>(parent,
		applicationContext.constructRestUrl(getRetrieveConsignmentsRestUrlPath()));

	client.post(driverIdentifier,
		new AsynHttpResponseHandler<ListConsignmentResponse>(parent, getActionName(), ListConsignmentResponse.class, parent.getErrorDialog()) {

	    @Override
	    public void onStart() {
		// clear list
		clearConsignmentList();

		// use spinner in progress
		parent.setProgressBarIndeterminateVisibility(true);
		beforeLoadConsignments();

		// use progress dialog
		// progressDialog = AndroidUtil.showProgressSpinner(parent);
	    }

	    @Override
	    public void onFinish() {
		afterLoadConsignments();

		// use spinner in progress
		parent.setProgressBarIndeterminateVisibility(false);
		// use progress dialog
		// progressDialog.dismiss();
	    }

	    @Override
	    protected void processSuccess(final ListConsignmentResponse response) {
		// updateConsignments(Collections.<Consignee> emptyList());
		updateConsignments(mapper.map(response));
	    }
	});

    }

    protected void processConsignments(final String processConsignmentsRestUrl) {
	if (consignmentListAdapter == null || !consignmentListAdapter.hasSelectedConsignment()) {
	    return;
	}
	final ConsignmentListProcessRequest request = new ConsignmentListProcessRequest();
	request.setAuthToken(authToken);

	for (final Consignment consignment : consignmentListAdapter.getSelectedConsignments()) {
	    request.addConsignment(consignment.getId());
	}

	final GsonAsyncHttpClient<Response> client = new GsonAsyncHttpClient<Response>(parent, applicationContext.constructRestUrl(processConsignmentsRestUrl));

	try {
	    client.post(request, new AsynHttpResponseHandler<Response>(parent, "Process consignments", Response.class, parent.getErrorDialog()) {

		@Override
		public void onStart() {
		    parent.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onFinish() {
		    parent.setProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected void processSuccess(final Response response) {
		    try {
			loadConsignments();
		    } catch (final Exception e) {
			e.printStackTrace();
			parent.showError("Error while reload consignments " + e.getMessage());
		    }
		}

	    });
	} catch (final Exception e) {
	    e.printStackTrace();
	    parent.showError("Error while process consignments " + e.getMessage());
	}

    }

    private void clearConsignmentList() {
	if (consignmentListAdapter != null) {
	    consignmentListAdapter.clear();
	}
    }

    public void selectAllConsignment() {
	if (consignmentListAdapter != null) {
	    consignmentListAdapter.selectAll();
	}
    }

    public void deSelectAllConsignments() {
	if (consignmentListAdapter != null) {
	    consignmentListAdapter.deSelectAll();
	}
    }

    protected void updateConsignments(final List<Consignee> consignees) {
	consignmentListAdapter = new ConsignmentListAdapter(parent, consignees);
	final ExpandableListView consignmentListView = getConsignmentListView();
	consignmentListView.setAdapter(consignmentListAdapter);

	// expand each group
	for (int i = 0; i < consignmentListAdapter.getGroupCount(); i++) {
	    consignmentListView.expandGroup(i);
	}

	consignmentListView.setOnGroupClickListener(new OnGroupClickListener() {
	    @Override
	    public boolean onGroupClick(final ExpandableListView parent, final View convertView, final int groupPosition, final long id) {
		return false;
	    }
	});

	// on child click listener
	consignmentListView.setOnChildClickListener(new OnChildClickListener() {
	    @Override
	    public boolean onChildClick(final ExpandableListView expandableListView, final View view, final int groupPosition, final int childPosition,
		    final long id) {
		final Consignee consignee = (Consignee) consignmentListAdapter.getGroup(groupPosition);
		final Consignment consignment = (Consignment) consignmentListAdapter.getChild(groupPosition, childPosition);
		final ConsignmentDetail consignmentDetail = new ConsignmentDetail(consignee, consignment);

		final Intent consignmentDetailsIntent = new Intent(parent, ConsignmentDetailActivity.class);
		consignmentDetailsIntent.putExtra(ConsignmentDetailActivity.INTENT_PARAM_CONSIGNMENT_DETAIL, consignmentDetail);
		startActivity(consignmentDetailsIntent);
		return true;
	    }
	});
    }

    public boolean hasSelectedConsignment() {
	return consignmentListAdapter != null ? consignmentListAdapter.hasSelectedConsignment() : false;
    }

    protected boolean hasConsignment() {
	return consignmentListAdapter != null ? consignmentListAdapter.hasConsignment() : false;
    }

    protected boolean validate() {
	if (!hasConsignment()) {
	    return false;
	}

	if (!hasSelectedConsignment()) {
	    parent.showAlertDialog("Please select consignment(s)");
	    return false;
	}

	return true;
    }

    @TargetApi(18)
    /**
     * @TargetApi just hushes the compiler about the error by telling it that we're aware and have handled it.
     * @param expandableListView
     */
    protected void moveExpandIndicatorToRight(final ExpandableListView expandableListView) {

	final int width = getResources().getDisplayMetrics().widthPixels;
	if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
	    expandableListView.setIndicatorBounds(width - getPixelValue(40), width - getPixelValue(10));
	} else {
	    expandableListView.setIndicatorBoundsRelative(width - getPixelValue(40), width - getPixelValue(10));
	}
    }

    private int getPixelValue(final int dp) {
	final float scale = getResources().getDisplayMetrics().density;
	return (int) (dp * scale + 0.5f);
    }

}
