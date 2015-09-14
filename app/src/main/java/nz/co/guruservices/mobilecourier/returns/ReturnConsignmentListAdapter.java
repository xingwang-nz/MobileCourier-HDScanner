package nz.co.guruservices.mobilecourier.returns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentStatus;
import nz.co.guruservices.mobilecourier.common.model.ItemData;
import nz.co.guruservices.mobilecourier.custom.ConsignmentItemView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ReturnConsignmentListAdapter extends BaseAdapter {
    private final ReturnConsignmentCategory attemptedConsignmentCategory = new ReturnConsignmentCategory(ConsignmentStatus.ATTEMPTED, "Attempted Consignments");

    private final ReturnConsignmentCategory rejectedConsignmentCategory = new ReturnConsignmentCategory(ConsignmentStatus.RETURNED, "Rejected Consignments");

    private final ReturnConsignmentCategory loadedConsignmentCategory = new ReturnConsignmentCategory(ConsignmentStatus.LOADED, "Loaded Consignments");

    private final Context context;

    private final List<ReturnConsignmentCategory> categories = new ArrayList<ReturnConsignmentCategory>();

    private final Map<Long, Consignee> consigneeMap = new HashMap<Long, Consignee>();

    /**
     * install all data inlcude group category
     */
    private List<ItemData> dataList = new ArrayList<ItemData>();

    public List<ReturnConsignmentCategory> getCategories() {
	return categories;
    }

    public ReturnConsignmentListAdapter(final Context context, final List<Consignee> consignees) {
	this.context = context;
	setData(consignees);
    }

    public void setData(final List<Consignee> consignees) {
	clearCategories();
	for (final Consignee consignee : consignees) {
	    for (final Consignment consignment : consignee.getConsignments()) {
		consigneeMap.put(consignment.getId(), consignee);

		if (ConsignmentStatus.ATTEMPTED.equals(consignment.getStatus())) {
		    attemptedConsignmentCategory.addConsignment(consignment);
		    consignment.setSelected(true);
		} else if (ConsignmentStatus.RETURNED.equals(consignment.getStatus())) {
		    rejectedConsignmentCategory.addConsignment(consignment);
		    consignment.setSelected(true);
		} else if (ConsignmentStatus.LOADED.equals(consignment.getStatus())) {
		    loadedConsignmentCategory.addConsignment(consignment);
		}
	    }
	}

	if (!attemptedConsignmentCategory.isEmppty()) {
	    categories.add(attemptedConsignmentCategory);
	}
	if (!rejectedConsignmentCategory.isEmppty()) {
	    categories.add(rejectedConsignmentCategory);
	}
	if (!loadedConsignmentCategory.isEmppty()) {
	    categories.add(loadedConsignmentCategory);
	}

	for (final ReturnConsignmentCategory category : categories) {
	    dataList.add(category);
	    for (final Consignment consignment : category.getConsignments()) {
		dataList.add(consignment);
	    }
	}

	notifyDataSetChanged();

    }

    private void clearCategories() {
	categories.clear();
	consigneeMap.clear();
	attemptedConsignmentCategory.clear();
	rejectedConsignmentCategory.clear();
	loadedConsignmentCategory.clear();

	dataList.clear();
    }

    public boolean hasConsignments() {
	for (final ReturnConsignmentCategory category : categories) {
	    if (!category.isEmppty()) {
		return true;
	    }
	}
	return false;
    }

    public Consignee getConsigneeForConsignment(final Consignment consignment) {
	return consigneeMap.get(consignment.getId());
    }

    @Override
    public boolean hasStableIds() {
	return true;
    }

    public boolean hasSelectedConsignment() {
	if (!categories.isEmpty()) {
	    for (final ReturnConsignmentCategory category : getCategories()) {
		for (final Consignment consignment : category.getConsignments()) {
		    if (consignment.isSelected()) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public List<Consignment> getSelectedConsignments() {
	final List<Consignment> consignments = new ArrayList<Consignment>();
	for (final ReturnConsignmentCategory category : getCategories()) {
	    for (final Consignment consignment : category.getConsignments()) {
		if (consignment.isSelected()) {
		    consignments.add(consignment);
		}
	    }
	}

	return consignments;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
	final ItemData itemData = (ItemData) getItem(position);
	if (itemData instanceof ReturnConsignmentCategory) {
	    final ReturnConsignmentCategory category = (ReturnConsignmentCategory) itemData;
	    if (convertView == null) {
		final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.return_consignment_category, null);
	    }
	    final TextView headerView = (TextView) convertView.findViewById(R.id.text_return_consignment_category_name);
	    headerView.setText(category.getCategoryName());
	} else {
	    if (convertView == null) {
		final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.return_consignment_item, null);
	    }
	    final Consignment consignment = (Consignment) itemData;

	    final TextView cosigneeNameText = (TextView) convertView.findViewById(R.id.text_return_consignee);
	    cosigneeNameText.setText(consignment.getConsigneeName());

	    // consignment
	    final ConsignmentItemView consignmentItemView = (ConsignmentItemView) convertView.findViewById(R.id.consignment_item);
	    consignmentItemView.setValue(consignment);

	    final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_return_consignment_list_item);
	    checkBox.setChecked(consignment.isSelected());
	    checkBox.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(final View view) {
		    consignment.setSelected(checkBox.isChecked());
		}
	    });
	}

	return convertView;
    }

    @Override
    public int getCount() {
	return dataList.size();
    }

    @Override
    public Object getItem(final int position) {
	return dataList.get(position);
    }

    public ItemData getItemById(final int id) {
	for (final ItemData itemData : dataList) {
	    if (id == itemData.getId()) {
		return itemData;
	    }
	}
	return null;
    }

    @Override
    public long getItemId(final int position) {
	return ((ItemData) getItem(position)).getId();
    }

    public void selectAllConsignment() {
	doSelectAll(true);
	notifyDataSetChanged();
    }

    public void deSelectAllConsignments() {
	doSelectAll(false);
	notifyDataSetChanged();
    }

    private void doSelectAll(final boolean selected) {
	if (!categories.isEmpty()) {
	    for (final ReturnConsignmentCategory category : getCategories()) {
		for (final Consignment consignment : category.getConsignments()) {
		    consignment.setSelected(selected);
		}
	    }
	}
    }
}
