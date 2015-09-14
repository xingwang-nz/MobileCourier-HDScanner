package nz.co.guruservices.mobilecourier.common.model;

public enum ConsignmentStatus {
    ASSIGNED("Assigned"),
    OPEN("Open"),
    REJECTED("DriverDecline"),
    LOADED("Loaded"),
    ATTEMPTED("Leave Card"),
    RETURNED("ClientRejected"),
    REVOKED("Return");

    private String description;

    private ConsignmentStatus(final String description) {
	this.description = description;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(final String description) {
	this.description = description;
    }

}
