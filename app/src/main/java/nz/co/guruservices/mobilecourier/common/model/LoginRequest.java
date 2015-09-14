package nz.co.guruservices.mobilecourier.common.model;

public class LoginRequest {

    private String driverId;

    private String password;

    private String deviceId;

    private String make;

    private String model;

    private String os;

    private String sdkVersion;

    public String getDriverId() {
	return driverId;
    }

    public void setDriverId(final String driverId) {
	this.driverId = driverId;
    }

    public String getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(final String deviceId) {
	this.deviceId = deviceId;
    }

    public String getMake() {
	return make;
    }

    public void setMake(final String make) {
	this.make = make;
    }

    public String getModel() {
	return model;
    }

    public void setModel(final String model) {
	this.model = model;
    }

    public String getOs() {
	return os;
    }

    public void setOs(final String os) {
	this.os = os;
    }

    public String getSdkVersion() {
	return sdkVersion;
    }

    public void setSdkVersion(final String sdkVersion) {
	this.sdkVersion = sdkVersion;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(final String password) {
	this.password = password;
    }
}
