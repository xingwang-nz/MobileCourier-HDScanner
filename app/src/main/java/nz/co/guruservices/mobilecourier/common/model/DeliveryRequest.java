package nz.co.guruservices.mobilecourier.common.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DeliveryRequest extends ProcessConsignmentRequest {

    // @SerializedName("returnIds")
    private List<DeliverConsignment> deliverConsignments = new ArrayList<DeliverConsignment>();

    private String printName;

    private String signatureData;

    private BigDecimal lat;

    private BigDecimal lon;

    public List<DeliverConsignment> getDeliverConsignments() {
	return deliverConsignments;
    }

    public void setDeliverConsignments(final List<DeliverConsignment> deliverConsignments) {
	this.deliverConsignments = deliverConsignments;
    }

    public String getPrintName() {
	return printName;
    }

    public void setPrintName(final String printName) {
	this.printName = printName;
    }

    public String getSignatureData() {
	return signatureData;
    }

    public void setSignatureData(final String signatureData) {
	this.signatureData = signatureData;
    }

    public void addDeliverConsignment(final DeliverConsignment deliverConsignment) {
	this.deliverConsignments.add(deliverConsignment);
    }

    public BigDecimal getLat() {
	return lat;
    }

    public void setLat(final BigDecimal lat) {
	this.lat = lat;
    }

    public BigDecimal getLon() {
	return lon;
    }

    public void setLon(final BigDecimal lon) {
	this.lon = lon;
    }

}
