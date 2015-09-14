package nz.co.guruservices.mobilecourier.consignment;

import nz.co.guruservices.mobilecourier.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyConsignmentFragment extends ConsignmentFragment {

    private static final String ACTION_NAME = "My consignments";

    private boolean firstLoaded;

    private View rootView;

    private ViewGroup loadButton;

    private ViewGroup declineButton;

    private TextView noMessageText;

    private ExpandableListView expandableListView;

    // private ViewGroup scanButton;
    // private ScannerHandler scannerHandler;

    public MyConsignmentFragment(final boolean firstLoaded) {
	this.firstLoaded = firstLoaded;
    }

    @Override
    protected View initializeFragmentView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	rootView = inflater.inflate(R.layout.consignment_seg_my, container, false);
	noMessageText = (TextView) rootView.findViewById(R.id.text_no_my_consignment);

	loadButton = (ViewGroup) rootView.findViewById(R.id.bnt_load_consignments);
	loadButton.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		if (!validate()) {
		    return;
		}
		processConsignments("consignment/loadJobs");
	    }
	});

	declineButton = (ViewGroup) rootView.findViewById(R.id.bnt_decline_consignments);
	declineButton.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		if (!validate()) {
		    return;
		}
		processConsignments("consignment/declineJobs");
	    }
	});

	// scanButton = (ViewGroup) rootView.findViewById(R.id.bnt_scan_consignments);
	// scanButton.setOnClickListener(new View.OnClickListener() {
	// @Override
	// public void onClick(final View v) {
	// Scanner.Read();
	// }
	// });

	expandableListView = (ExpandableListView) rootView.findViewById(R.id.my_consignment_list);
	moveExpandIndicatorToRight(expandableListView);

	// initBarCodeScanner();

	return rootView;
    }

    // private void initBarCodeScanner() {
    // scannerHandler = new ScannerHandler();
    // scannerHandler.setBarcodeScannerListener(new BarcodeScannerListener() {
    // @Override
    // public void onScan(final String barcode) {
    // // Toast.makeText(parent, barcode, Toast.LENGTH_LONG).show();
    // consignmentListAdapter.selectConsignmentByBarcode(barcode);
    //
    // }
    // });
    //
    // Scanner.m_handler = scannerHandler;
    // // initialize the scanner
    // Scanner.InitSCA();
    //
    // }

    /**
     *
     * Android create more than one tabs in order to cache the tag pages. the first time when the first segment is visible, it doesn't fire
     * OnPageChange event, the boolean variable used to indicate if the first segment (MyConsignmentFragment) first shown, so that after
     * onCreateView method(where segment is initialized) is invoked, call server load consignments
     *
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned
     */
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
	if (firstLoaded) {
	    parent.setCurrentConsignmentFragment(this);
	    parent.loadConsignments();
	}
    }

    @Override
    protected String getRetrieveConsignmentsRestUrlPath() {
	return "consignment/list/assigned";
    }

    @Override
    protected String getActionName() {
	return ACTION_NAME;
    }

    @Override
    protected void beforeLoadConsignments() {
    }

    @Override
    protected void afterLoadConsignments() {
	if (hasConsignment()) {
	    noMessageText.setText("");
	    noMessageText.setVisibility(View.GONE);
	} else {
	    noMessageText.setText("No Consignments");
	    noMessageText.setVisibility(View.VISIBLE);
	}
    }

    @Override
    protected ExpandableListView getConsignmentListView() {
	return expandableListView;
    }

    @Override
    protected void onBarcodeScanner(final String barcode) {
	Toast.makeText(parent, barcode, Toast.LENGTH_LONG).show();
	consignmentListAdapter.selectConsignmentByBarcode(barcode);

    }

}
