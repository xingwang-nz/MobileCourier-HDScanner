package nz.co.guruservices.mobilecourier.custom;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * A reusable custom view to encapsulate the details of consignee fields
 *
 */
public class ConsigneeItemView extends LinearLayout implements CustomView<Consignee> {

    private TextView consigneeName;

    private TextView consigneeAddress;

    private TextView consigneePhone;

    private LinearLayout consigneePhonePanel;

    private ImageView imageView;

    public ConsigneeItemView(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	inflate(getContext(), R.layout.consignee_item, this);
	consigneeName = (TextView) findViewById(R.id.text_consignee_item_name);
	consigneeAddress = (TextView) findViewById(R.id.text_consignee_item_address);
	consigneePhone = (TextView) findViewById(R.id.text_consignee_item_phone);
	consigneePhonePanel = (LinearLayout) findViewById(R.id.panel_consignee_phone);
	imageView = (ImageView) findViewById(R.id.ic_consignee_phone);
    }

    @Override
    public void setValue(final Consignee consignee) {
	consigneeName.setText(consignee.getName());
	consigneeAddress.setText(consignee.getAddress());
	consigneePhone.setText(consignee.getPhone());
    }

    public ViewGroup getPhonePanel() {
	return consigneePhonePanel;
    }

    public void setShowPhoneIcon(final boolean show) {
	imageView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
