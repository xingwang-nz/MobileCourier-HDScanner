package nz.co.guruservices.mobilecourier.common.model;

public class DriverIdentifier {

    private final String authToken;

    public DriverIdentifier(final String authToken) {
	super();
	this.authToken = authToken;
    }

    public String getAuthToken() {
	return authToken;
    }

}
