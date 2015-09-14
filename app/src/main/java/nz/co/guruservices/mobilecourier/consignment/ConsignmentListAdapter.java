package nz.co.guruservices.mobilecourier.consignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.Product;
import nz.co.guruservices.mobilecourier.custom.ConsignmentItemView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ConsignmentListAdapter extends BaseExpandableListAdapter {

    private final Context context;

    private List<Consignee> consignees;

    public ConsignmentListAdapter(final Context context, final List<Consignee> consignees) {
	super();
	this.context = context;
	this.consignees = consignees;

    }

    @Override
    public int getGroupCount() {
	return consignees.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
	return consignees.get(groupPosition).getConsignments().size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
	return consignees.get(groupPosition);
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
	return consignees.get(groupPosition).getConsignments().get(childPosition);
    }

    @Override
    public long getGroupId(final int groupPosition) {
	return consignees.get(groupPosition).getId();
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
	return consignees.get(groupPosition).getConsignments().get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
	return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
	if (convertView == null) {
	    final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.consignment_list_consignee, null);
	}

	final Consignee consignee = (Consignee) getGroup(groupPosition);
	final TextView consigneeText = (TextView) convertView.findViewById(R.id.text_consignment_list_consignee);
	consigneeText.setText(consignee.getName());

	return convertView;
    }

    public static final String DATE_FIELD_LABEL = "Date:";

    public static final String MANIFEST_NO_FIELD_LABEL = "Man#:";

    public static final String CONSIGNMENT_NO_FIELD_LABEL = "Con#:";

    public static final String LABELS_FIELD_LABEL = "Labels:";

    public static final String VOLUME_FIELD_LABEL = "Volume:";

    public static final String WEIGHT_FIELD_LABEL = "Weight:";

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
	final Consignment consignment = (Consignment) getChild(groupPosition, childPosition);

	if (convertView == null) {
	    final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    convertView = infalInflater.inflate(R.layout.consignment_list_item, null);
	}

	// consignment
	final ConsignmentItemView consignmentItemView = (ConsignmentItemView) convertView.findViewById(R.id.consignment_item);
	consignmentItemView.setValue(consignment);

	final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_consignment_list_item);

	checkBox.setChecked(consignment.isSelected());

	checkBox.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View view) {
		consignment.setSelected(checkBox.isChecked());
	    }
	});

	return convertView;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
	return true;
    }

    public void clear() {
	consignees = Collections.<Consignee> emptyList();
	notifyDataSetChanged();
    }

    public void selectAll() {
	doSelectAll(true);
	notifyDataSetChanged();
    }

    public void deSelectAll() {
	doSelectAll(false);
	notifyDataSetChanged();
    }

    private void doSelectAll(final boolean selected) {
	for (final Consignee consignee : consignees) {
	    for (final Consignment consignment : consignee.getConsignments()) {
		consignment.setSelected(selected);
	    }
	}
    }

    public boolean hasConsignment() {
	if (consignees == null) {
	    return false;
	}

	for (final Consignee consignee : consignees) {
	    if (consignee.getConsignments() != null && consignee.getConsignments().size() > 0) {
		return true;
	    }
	}

	return false;
    }

    public boolean hasSelectedConsignment() {
	if (consignees == null) {
	    return false;
	}
	for (final Consignee consignee : consignees) {
	    for (final Consignment consignment : consignee.getConsignments()) {
		if (consignment.isSelected()) {
		    return true;
		}
	    }
	}

	return false;

    }

    public List<Consignment> getSelectedConsignments() {
	final List<Consignment> consignments = new ArrayList<Consignment>();
	for (final Consignee consignee : consignees) {
	    for (final Consignment consignment : consignee.getConsignments()) {
		if (consignment.isSelected()) {
		    consignments.add(consignment);
		}
	    }
	}
	return consignments;

    }

    public void selectConsignmentByBarcode(final String barcode) {
	for (final Consignee consignee : consignees) {
	    for (final Consignment consignment : consignee.getConsignments()) {
		for (final Product product : consignment.getItems()) {
		    if (product.getUlabel().equals(barcode)) {
			consignment.setSelected(true);
			notifyDataSetChanged();
		    }
		}
	    }
	}
    }

}