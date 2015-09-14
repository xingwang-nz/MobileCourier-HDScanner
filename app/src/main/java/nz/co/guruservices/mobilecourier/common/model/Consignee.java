package nz.co.guruservices.mobilecourier.common.model;

import java.util.ArrayList;
import java.util.List;

public class Consignee extends ItemData {

    private String name;

    private String region;

    private String address;

    private String phone;

    private double lat;

    private double lon;

    /**
     * used to indicate the position in the list for sorting on DeliverActivity
     */
    private transient int orderIndex;

    private List<Consignment> consignments = new ArrayList<Consignment>();

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public String getRegion() {
	return region;
    }

    public void setRegion(final String region) {
	this.region = region;
    }

    public List<Consignment> getConsignments() {
	return consignments;
    }

    public void setConsignments(final List<Consignment> consignments) {
	this.consignments = consignments;
    }

    public void addConsighment(final Consignment consignment) {
	consignments.add(consignment);
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(final String address) {
	this.address = address;
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(final String phone) {
	this.phone = phone;
    }

    public int getOrderIndex() {
	return orderIndex;
    }

    public void setOrderIndex(final int orderIndex) {
	this.orderIndex = orderIndex;
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
	return super.equals(obj);

    }

    public double getLat() {
	return lat;
    }

    public void setLat(final double lat) {
	this.lat = lat;
    }

    public double getLon() {
	return lon;
    }

    public void setLon(final double lon) {
	this.lon = lon;
    }

}
