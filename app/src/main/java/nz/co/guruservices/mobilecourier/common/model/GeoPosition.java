package nz.co.guruservices.mobilecourier.common.model;

import java.io.Serializable;

public class GeoPosition implements Serializable {

    private String title = "";

    private double latitude;

    private double longitude;

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(final double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(final double longitude) {
	this.longitude = longitude;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(final String title) {
	this.title = title;
    }

}
