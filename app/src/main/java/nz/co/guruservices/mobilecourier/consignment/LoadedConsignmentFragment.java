package nz.co.guruservices.mobilecourier.consignment;

import nz.co.guruservices.mobilecourier.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class LoadedConsignmentFragment extends ConsignmentFragment {

    private View rootView;

    private TextView noMessageText;

    private ExpandableListView expandableListView;

    @Override
    public View initializeFragmentView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

	rootView = inflater.inflate(R.layout.consignment_seg_loaded, container, false);
	noMessageText = (TextView) rootView.findViewById(R.id.text_no_loaded_consignment);

	final ViewGroup unloadButton = (ViewGroup) rootView.findViewById(R.id.bnt_unload_consignments);
	unloadButton.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		if (!validate()) {
		    return;
		}
		processConsignments("consignment/unloadJobs");
	    }
	});

	expandableListView = (ExpandableListView) rootView.findViewById(R.id.loaded_consignment_list);
	moveExpandIndicatorToRight(expandableListView);
	return rootView;
    }

    @Override
    protected String getRetrieveConsignmentsRestUrlPath() {
	return "consignment/list/loaded";
    }

    @Override
    protected String getActionName() {
	return "Loaded consignments";
    }

    @Override
    protected void beforeLoadConsignments() {
	// TODO Auto-generated method stub

    }

    @Override
    protected void afterLoadConsignments() {
	if (hasConsignment()) {
	    noMessageText.setText("");
	    noMessageText.setVisibility(View.GONE);
	} else {
	    noMessageText.setText("No Loaded Consignments");
	    noMessageText.setVisibility(View.VISIBLE);
	}

    }

    @Override
    protected ExpandableListView getConsignmentListView() {
	return expandableListView;
    }

    @Override
    protected void onBarcodeScanner(final String barcode) {
	// TODO Auto-generated method stub

    }

}
