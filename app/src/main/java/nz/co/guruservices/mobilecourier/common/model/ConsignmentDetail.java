package nz.co.guruservices.mobilecourier.common.model;

import java.io.Serializable;

public class ConsignmentDetail implements Serializable {

    private Consignee consignee;

    private Consignment consignment;

    public ConsignmentDetail() {

    }

    public ConsignmentDetail(final Consignee consignee, final Consignment consignment) {
	this.consignee = consignee;
	this.consignment = consignment;
    }

    public Consignee getConsignee() {
	return consignee;
    }

    public void setConsignee(final Consignee consignee) {
	this.consignee = consignee;
    }

    public Consignment getConsignment() {
	return consignment;
    }

    public void setConsignment(final Consignment consignment) {
	this.consignment = consignment;
    }

}
