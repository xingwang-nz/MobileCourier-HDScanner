package nz.co.guruservices.mobilecourier.common.model;

public class LoginResult {
    private String driverName;

    private String driverId;

    private String authToken;

    public String getDriverName() {
	return driverName;
    }

    public void setDriverName(final String driverName) {
	this.driverName = driverName;
    }

    public String getDriverId() {
	return driverId;
    }

    public void setDriverId(final String driverId) {
	this.driverId = driverId;
    }

    public String getAuthToken() {
	return authToken;
    }

    public void setAuthToken(final String authToken) {
	this.authToken = authToken;
    }
}
