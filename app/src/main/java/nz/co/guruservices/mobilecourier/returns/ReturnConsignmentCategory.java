package nz.co.guruservices.mobilecourier.returns;

import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentStatus;
import nz.co.guruservices.mobilecourier.common.model.ItemData;

public class ReturnConsignmentCategory extends ItemData {
    private final ConsignmentStatus consignmentStatus;

    private String categoryName;

    private List<Consignment> consignments = new ArrayList<Consignment>();

    public ReturnConsignmentCategory(final ConsignmentStatus consignmentStatus, final String categoryName) {
	super();
	this.consignmentStatus = consignmentStatus;
	this.categoryName = categoryName;

	switch (consignmentStatus) {
	    case ATTEMPTED:
		setId(-1);
		break;
	    case RETURNED:
		setId(-2);
		break;
	    case LOADED:
		setId(-3);
		break;
	    default:
		setId(-3);
	}
    }

    public ConsignmentStatus getConsignmentStatus() {
	return consignmentStatus;
    }

    public String getCategoryName() {
	return categoryName;
    }

    public List<Consignment> getConsignments() {
	return consignments;
    }

    public void setConsignments(final List<Consignment> consignments) {
	this.consignments = consignments;
    }

    public void addConsignment(final Consignment consignment) {
	this.consignments.add(consignment);
    }

    public void clear() {
	consignments.clear();
    }

    public boolean isEmppty() {
	return consignments.isEmpty();
    }
}
