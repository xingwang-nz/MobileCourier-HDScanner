package nz.co.guruservices.mobilecourier.common.model;


public abstract class ProcessConsignmentRequest {
    private String authToken;

    private DeliveryType deliveryType;

    public String getAuthToken() {
	return authToken;
    }

    public void setAuthToken(final String authToken) {
	this.authToken = authToken;
    }

    public DeliveryType getDeliveryType() {
	return deliveryType;
    }

    public void setDeliveryType(final DeliveryType deliveryType) {
	this.deliveryType = deliveryType;
    }

}
