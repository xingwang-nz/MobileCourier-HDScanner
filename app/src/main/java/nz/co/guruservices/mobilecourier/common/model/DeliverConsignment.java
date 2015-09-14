package nz.co.guruservices.mobilecourier.common.model;

public class DeliverConsignment {
    private Long consignmentId;

    private String note;

    private boolean accepted;

    public Long getConsignmentId() {
	return consignmentId;
    }

    public void setConsignmentId(final Long consignmentId) {
	this.consignmentId = consignmentId;
    }

    public String getNote() {
	return note;
    }

    public void setNote(final String note) {
	this.note = note;
    }

    public boolean isAccepted() {
	return accepted;
    }

    public void setAccepted(final boolean accepted) {
	this.accepted = accepted;
    }

}
