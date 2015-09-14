package nz.co.guruservices.mobilecourier.deliver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import nz.co.guruservices.mobilecourier.common.model.DeliverConsignment;
import nz.co.guruservices.mobilecourier.common.model.DeliveryRequest;
import nz.co.guruservices.mobilecourier.consignment.ConsignmentNoteActivity;
import nz.co.guruservices.mobilecourier.signature.SignatureCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class DeliveryFragment extends DeliverFragment implements SignatureCallback, ConsignmentNoteCallback {

    private View rootView;

    private ListView deliveryConsignmentListView;

    private DeliveryConsignmentListAdapter adapter;

    private ImageView signatureView;

    private String signatureBase64String;

    private TextView totalSelectedConsignmentsView;

    private TextView printNameView;

    private ViewGroup button;

    private ScrollView scrollView;

    private static final String REST_URL = "consignment/delivered";

    private RadioGroup deliveryTypeRadioGroup;

    @Override
    protected View initializeFragmentView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	rootView = inflater.inflate(R.layout.deliver_seg_delivery, container, false);

	deliveryTypeRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_delivery_type);
	deliveryTypeRadioGroup.check(R.id.radio_delivery_type_store);

	final TextView consigneeNameView = (TextView) rootView.findViewById(R.id.deliver_consignee_name);
	scrollView = (ScrollView) rootView.findViewById(R.id.deliver_scroll_view);
	consigneeNameView.setText(parent.getConsignee().getName());

	totalSelectedConsignmentsView = (TextView) rootView.findViewById(R.id.deliver_consignments_total);

	deliveryConsignmentListView = (ListView) rootView.findViewById(R.id.deliver_consignment_list);
	adapter = new DeliveryConsignmentListAdapter(parent, parent.getConsignee().getConsignments());
	deliveryConsignmentListView.setAdapter(adapter);

	deliveryConsignmentListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
		final Consignment consignment = (Consignment) adapter.getItem(position);

		final Intent intent = new Intent(parent, ConsignmentNoteActivity.class);
		final ConsignmentDetail consignmentDetail = new ConsignmentDetail(parent.getConsignee(), consignment);
		captureDeliveryNote(consignmentDetail, position);
	    }

	});

	printNameView = (TextView) rootView.findViewById(R.id.input_consignment_delivery_print_name);

	updateSelectedConsignmentsNumber();

	initSignatureView();

	button = (ViewGroup) rootView.findViewById(R.id.bnt_deliver_delivery);
	button.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		// check if any selected consignments
		if (adapter.getSelectedConsignmentCount() == 0) {
		    parent.showAlertDialog("Please select consignment(s)");
		    return;
		}
		// if there is rejected consignment, check if note is provided.
		final List<String> rejectedConsignmentWithoutNotesNoList = adapter.getRejectedConsignmentNoWithoutNote();
		if (!rejectedConsignmentWithoutNotesNoList.isEmpty()) {
		    final StringBuilder builder = new StringBuilder("Please provide notes for the rejected consignment(s):");
		    for (final String consignmentNo : rejectedConsignmentWithoutNotesNoList) {
			builder.append("\n").append(consignmentNo);
		    }
		    parent.showAlertDialog(builder.toString());
		    return;
		}

		if (TextUtils.isEmpty(signatureBase64String)) {
		    parent.showAlertDialog("Signature required");
		    scrollView.post(new Runnable() {
			@Override
			public void run() {
			    scrollView.scrollTo(signatureView.getLeft(), signatureView.getBottom());
			}
		    });
		    return;
		}
		performDelivery();
	    }
	});

	// to solve 'ListView inside ScrollView' problem
	AndroidUtil.setListViewSize(deliveryConsignmentListView);

	return rootView;
    }

    private void performDelivery() {
	final DeliveryRequest request = new DeliveryRequest();
	request.setAuthToken(authToken);

	final Bitmap signatureBitmap = AndroidUtil.drawViewAsBitmap(signatureView);
	request.setSignatureData(signatureBase64String);
	request.setDeliveryType(getSelectedDeliveryType());

	for (final Consignment consignment : adapter.getConsignments()) {
	    if (consignment.isSelected()) {
		final DeliverConsignment deliverConsignment = new DeliverConsignment();
		deliverConsignment.setConsignmentId(consignment.getId());
		deliverConsignment.setNote(consignment.getNote());
		deliverConsignment.setAccepted(consignment.isAccepted());
		request.addDeliverConsignment(deliverConsignment);
	    }
	}

	if (!TextUtils.isEmpty(printNameView.getText().toString())) {
	    request.setPrintName(printNameView.getText().toString());
	}

	final Location location = parent.getLocation();
	if (location != null) {
	    request.setLat(new BigDecimal(location.getLatitude()));
	    request.setLon(new BigDecimal(location.getLongitude()));
	}

	performDeliver(request, REST_URL, "Deliver");
    }

    private void updateSelectedConsignmentsNumber() {
	totalSelectedConsignmentsView.setText(adapter.getSelectedConsignmentCount() + "/" + parent.getConsignee().getConsignments().size());
    }

    private void initSignatureView() {
	signatureView = (ImageView) rootView.findViewById(R.id.img_signature);
	signatureView.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		captureSignature();
	    }
	});

    }

    @Override
    public void onSign(final Object signatureData) {
	if (signatureData instanceof String) {
	    signatureBase64String = (String) signatureData;
	    AndroidUtil.loadBitmapDataToView(signatureView, AndroidUtil.decodeBase64String(signatureBase64String));
	}
    }

    public void captureSignature() {
	parent.captureSignature(this);
    }

    public void captureDeliveryNote(final ConsignmentDetail consignmentDetail, final int position) {
	parent.captureDeliveryNote(this, consignmentDetail, position);
    }

    @Override
    public void setConsignmentNote(final String note, final int position) {
	final Consignment consignment = (Consignment) adapter.getItem(position);
	consignment.setNote(note);

    }

    @Override
    protected View getRootView() {
	return rootView;
    }

    private class DeliveryConsignmentListAdapter extends BaseAdapter {

	private final Context context;

	private List<Consignment> consignments;

	public DeliveryConsignmentListAdapter(final Context context, final List<Consignment> consignments) {
	    super();
	    this.context = context;
	    this.consignments = consignments;
	}

	@Override
	public int getCount() {
	    return consignments.size();
	}

	@Override
	public Object getItem(final int position) {
	    return consignments.get(position);
	}

	@Override
	public long getItemId(final int position) {
	    return consignments.get(position).getConsigneeId();
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {

	    final Consignment consignment = (Consignment) getItem(position);

	    if (convertView == null) {
		final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.deliver_consignment_item, null);
	    }

	    final TextView consignmentNoView = (TextView) convertView.findViewById(R.id.text_delivery_consignment_no);
	    consignmentNoView.setText("#" + consignment.getConsignmentNo());

	    final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_consignment_delivery);
	    checkBox.setChecked(consignment.isSelected());

	    final Switch acceptSwitch = (Switch) convertView.findViewById(R.id.switch_consignment_accept_reject);
	    acceptSwitch.setChecked(consignment.isAccepted());
	    acceptSwitch.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
		    consignment.setAccepted(acceptSwitch.isChecked());
		}
	    });

	    checkBox.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
		    consignment.setSelected(checkBox.isChecked());
		    if (checkBox.isChecked()) {
			acceptSwitch.setEnabled(true);
		    } else {
			acceptSwitch.setEnabled(false);
		    }
		    // reset switch to true
		    acceptSwitch.setChecked(true);

		    consignment.setAccepted(acceptSwitch.isChecked());
		    updateSelectedConsignmentsNumber();
		}
	    });

	    return convertView;
	}

	public List<Consignment> getConsignments() {
	    return this.consignments;
	}

	public List<String> getRejectedConsignmentNoWithoutNote() {
	    final List<String> consignmentNoList = new ArrayList<String>();
	    for (final Consignment consignment : getConsignments()) {
		if (consignment.isSelected() && !consignment.isAccepted() && TextUtils.isEmpty(consignment.getNote())) {
		    consignmentNoList.add(consignment.getConsignmentNo());
		}
	    }
	    return consignmentNoList;
	}

	public int getSelectedConsignmentCount() {
	    int count = 0;
	    for (final Consignment consignment : adapter.getConsignments()) {
		if (consignment.isSelected()) {
		    count++;
		}
	    }
	    return count;
	}
    }

    @Override
    protected RadioGroup getDeliveryTypeRadioGroup() {
	return deliveryTypeRadioGroup;
    }

}
