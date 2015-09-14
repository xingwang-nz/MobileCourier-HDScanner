package nz.co.guruservices.mobilecourier.common.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String itemCode;

    private String itemDesc;

    private String serialNumber;

    private String barcode;

    private String ulabel;

    private Integer quantity;

    private double volume;

    private double weight;

    private String packSize;

    private String packDivider;

    public String getItemCode() {
	return itemCode;
    }

    public void setItemCode(final String itemCode) {
	this.itemCode = itemCode;
    }

    public String getItemDesc() {
	return itemDesc;
    }

    public void setItemDesc(final String itemDesc) {
	this.itemDesc = itemDesc;
    }

    public String getSerialNumber() {
	return serialNumber;
    }

    public void setSerialNumber(final String serialNumber) {
	this.serialNumber = serialNumber;
    }

    public String getBarcode() {
	return barcode;
    }

    public void setBarcode(final String barcode) {
	this.barcode = barcode;
    }

    public String getUlabel() {
	return ulabel;
    }

    public void setUlabel(final String ulabel) {
	this.ulabel = ulabel;
    }

    public Integer getQuantity() {
	return quantity;
    }

    public void setQuantity(final Integer quantity) {
	this.quantity = quantity;
    }

    public double getVolume() {
	return volume;
    }

    public void setVolume(final double volume) {
	this.volume = volume;
    }

    public double getWeight() {
	return weight;
    }

    public void setWeight(final double weight) {
	this.weight = weight;
    }

    public String getPackSize() {
	return packSize;
    }

    public void setPackSize(final String packSize) {
	this.packSize = packSize;
    }

    public String getPackDivider() {
	return packDivider;
    }

    public void setPackDivider(final String packDivider) {
	this.packDivider = packDivider;
    }

}
