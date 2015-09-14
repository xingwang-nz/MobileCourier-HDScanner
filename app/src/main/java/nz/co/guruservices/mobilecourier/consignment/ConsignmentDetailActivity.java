package nz.co.guruservices.mobilecourier.consignment;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import nz.co.guruservices.mobilecourier.custom.ConsignmentDetailView;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class ConsignmentDetailActivity extends Activity {

    public static final String INTENT_PARAM_CONSIGNMENT_DETAIL = "intentConsignmentDetail";

    private ConsignmentDetailView consignmentDetailView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.consignment_detail_container);

	getActionBar().setHomeButtonEnabled(true);

	final ConsignmentDetail consignmentDetail = (ConsignmentDetail) getIntent().getExtras().getSerializable(INTENT_PARAM_CONSIGNMENT_DETAIL);

	consignmentDetailView = (ConsignmentDetailView) findViewById(R.id.consignment_detail);
	consignmentDetailView.setValue(consignmentDetail);

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

}
