package nz.co.guruservices.mobilecourier.custom;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * this view contains consignment details with product details, consignee details and note
 *
 */
public class ConsignmentDetailView extends LinearLayout implements CustomView<ConsignmentDetail> {

    private ConsigneeItemView consigneeItemView;

    private ConsignmentItemView consignmentItemView;

    private TextView totalItems;

    private ProductListView productListview;

    public ConsignmentDetailView(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	inflate(getContext(), R.layout.consignment_detail, this);

	consigneeItemView = (ConsigneeItemView) findViewById(R.id.consignee_item);
	consignmentItemView = (ConsignmentItemView) findViewById(R.id.consignment_item);
	totalItems = (TextView) findViewById(R.id.number_of_items);
	productListview = (ProductListView) findViewById(R.id.product_list);

    }

    @Override
    public void setValue(final ConsignmentDetail consignmentDetail) {
	final Consignee consignee = consignmentDetail.getConsignee();
	consigneeItemView.setValue(consignee);
	final Consignment consignment = consignmentDetail.getConsignment();
	consignmentItemView.setValue(consignment);
	if (consignment.getItems().size() != 0) {
	    totalItems.setText(String.valueOf(consignment.getItems().size()));
	} else {
	    totalItems.setText("N/A");
	}
	productListview.setValue(consignment.getItems());
    }

}
