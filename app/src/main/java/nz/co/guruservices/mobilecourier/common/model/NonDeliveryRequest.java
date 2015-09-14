package nz.co.guruservices.mobilecourier.common.model;

import java.util.ArrayList;
import java.util.List;

public class NonDeliveryRequest extends ProcessConsignmentRequest {
    private List<Long> consignmentIds = new ArrayList<Long>();

    private String deliveryNote;

    public List<Long> getConsignmentIds() {
	return consignmentIds;
    }

    public void setConsignmentIds(final List<Long> consignmentIds) {
	this.consignmentIds = consignmentIds;
    }

    public String getDeliveryNote() {
	return deliveryNote;
    }

    public void setDeliveryNote(final String deliveryNote) {
	this.deliveryNote = deliveryNote;
    }

    public void addConsignmentId(final Long consignmentId) {
	this.consignmentIds.add(consignmentId);
    }
}
