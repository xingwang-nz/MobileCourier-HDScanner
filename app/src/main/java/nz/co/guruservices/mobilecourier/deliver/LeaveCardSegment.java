package nz.co.guruservices.mobilecourier.deliver;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.LeaveCardRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class LeaveCardSegment extends DeliverFragment {
    private View rootView;

    private ViewGroup button;

    private EditText noteField;

    private static final String REST_URL = "consignment/attempted";

    private RadioGroup deliveryTypeRadioGroup;

    @Override
    protected View initializeFragmentView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	rootView = inflater.inflate(R.layout.deliver_seg_leave_card, container, false);

	deliveryTypeRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_delivery_type);
	deliveryTypeRadioGroup.check(R.id.radio_delivery_type_home);

	noteField = (EditText) rootView.findViewById(R.id.input_leave_card_note);

	button = (ViewGroup) rootView.findViewById(R.id.bnt_deliver_leave_card);
	button.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		final String notes = noteField.getText().toString();
		if (TextUtils.isEmpty(notes)) {
		    parent.showAlertDialog("Please enter note");
		    return;
		}

		performLeaveCard();
	    }

	});

	final TextView consigneeNameView = (TextView) rootView.findViewById(R.id.deliver_consignee_name);
	consigneeNameView.setText(parent.getConsignee().getName());

	final TextView totalConsignmentsView = (TextView) rootView.findViewById(R.id.deliver_consignments_total);

	totalConsignmentsView.setText(String.valueOf(parent.getConsignee().getConsignments().size()));

	return rootView;
    }

    private void performLeaveCard() {
	final LeaveCardRequest request = new LeaveCardRequest();
	request.setAuthToken(authToken);
	request.setDeliveryNote(noteField.getText().toString());
	request.setDeliveryType(getSelectedDeliveryType());

	for (final Consignment consignment : parent.getConsignee().getConsignments()) {
	    request.addConsignmentId(consignment.getId());
	}

	performDeliver(request, REST_URL, "Leave card");

    }

    @Override
    protected View getRootView() {
	return rootView;
    }

    @Override
    protected RadioGroup getDeliveryTypeRadioGroup() {
	return deliveryTypeRadioGroup;
    }
}
