package nz.co.guruservices.mobilecourier.custom;

import static nz.co.guruservices.mobilecourier.consignment.ConsignmentListAdapter.CONSIGNMENT_NO_FIELD_LABEL;
import static nz.co.guruservices.mobilecourier.consignment.ConsignmentListAdapter.DATE_FIELD_LABEL;
import static nz.co.guruservices.mobilecourier.consignment.ConsignmentListAdapter.MANIFEST_NO_FIELD_LABEL;
import static nz.co.guruservices.mobilecourier.consignment.ConsignmentListAdapter.LABELS_FIELD_LABEL;
import static nz.co.guruservices.mobilecourier.consignment.ConsignmentListAdapter.VOLUME_FIELD_LABEL;
import static nz.co.guruservices.mobilecourier.consignment.ConsignmentListAdapter.WEIGHT_FIELD_LABEL;
import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * A reusable custom view to encapsulate the details of consignment fields
 *
 */
public class ConsignmentItemView extends LinearLayout implements CustomView<Consignment> {

    public ConsignmentItemView(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	inflate(getContext(), R.layout.consignment_item, this);
    }

    @Override
    public void setValue(final Consignment consignment) {

	final TextView dateField = (TextView) findViewById(R.id.text_consignment_item_date);
	dateField.setText(DATE_FIELD_LABEL + AndroidUtil.formatDate(consignment.getDocketDate()));

	final TextView statusField = (TextView) findViewById(R.id.text_consignment_item_status);
	statusField.setText(consignment.getStatus() != null ? consignment.getStatus().getDescription() : "");

	final TextView manifestNoField = (TextView) findViewById(R.id.text_consignment_item_manifest_no);
	manifestNoField.setText(MANIFEST_NO_FIELD_LABEL + consignment.getManifestNo());

	final TextView consignmentNoField = (TextView) findViewById(R.id.text_consignment_item_consignment_no);
	consignmentNoField.setText(CONSIGNMENT_NO_FIELD_LABEL + consignment.getConsignmentNo());

	final TextView labelsField = (TextView) findViewById(R.id.text_consignment_item_labels);
	labelsField.setText(LABELS_FIELD_LABEL + consignment.getLabels());

	final TextView volumeField = (TextView) findViewById(R.id.text_consignment_item_volume);
	volumeField.setText(VOLUME_FIELD_LABEL + String.valueOf(consignment.getVolume()));

	final TextView weightField = (TextView) findViewById(R.id.text_consignment_item_weight);
	weightField.setText(WEIGHT_FIELD_LABEL + String.valueOf(consignment.getWeight()));

    }

}
