package nz.co.guruservices.mobilecourier.common.model;

import java.util.List;

/**
 *
 * Json model object for consignment/list call
 *
 */
public class ListConsignmentResult {

    private List<Consignment> consignments;

    private List<Consignee> consignees;

    public List<Consignment> getConsignments() {
	return consignments;
    }

    public void setConsignments(List<Consignment> consignments) {
	this.consignments = consignments;
    }

    public List<Consignee> getConsignees() {
	return consignees;
    }

    public void setConsignees(List<Consignee> consignees) {
	this.consignees = consignees;
    }

}
