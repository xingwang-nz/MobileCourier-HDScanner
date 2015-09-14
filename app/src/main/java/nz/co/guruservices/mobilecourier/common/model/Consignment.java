package nz.co.guruservices.mobilecourier.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Consignment extends ItemData {

    private ConsignmentStatus status;

    private String jobRef;

    private String manifestNo;

    private String consignmentNo;

    private double volume;

    private double weight;

    private int quantity;

    private int labels;

    private long consigneeId;

    private String carrierRef;

    private Date docketDate;

    private Date created;

    private Date lastModified;

    private List<Product> items = new ArrayList<Product>();

    private transient boolean selected;

    private transient boolean accepted;

    private String consigneeName;

    private String note = "";

    public ConsignmentStatus getStatus() {
	return status;
    }

    public void setStatus(final ConsignmentStatus status) {
	this.status = status;
    }

    public String getJobRef() {
	return jobRef;
    }

    public void setJobRef(final String jobRef) {
	this.jobRef = jobRef;
    }

    public String getConsignmentNo() {
	return consignmentNo;
    }

    public void setConsignmentNo(final String consignmentNo) {
	this.consignmentNo = consignmentNo;
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

    public int getQuantity() {
	return quantity;
    }

    public void setQuantity(final int quantity) {
	this.quantity = quantity;
    }

    public long getConsigneeId() {
	return consigneeId;
    }

    public void setConsigneeId(final long consigneeId) {
	this.consigneeId = consigneeId;
    }

    public String getCarrierRef() {
	return carrierRef;
    }

    public void setCarrierRef(final String carrierRef) {
	this.carrierRef = carrierRef;
    }

    public Date getDocketDate() {
	return docketDate;
    }

    public void setDocketDate(final Date docketDate) {
	this.docketDate = docketDate;
    }

    public Date getCreated() {
	return created;
    }

    public void setCreated(final Date created) {
	this.created = created;
    }

    public Date getLastModified() {
	return lastModified;
    }

    public void setLastModified(final Date lastModified) {
	this.lastModified = lastModified;
    }

    public String getManifestNo() {
	return manifestNo;
    }

    public void setManifestNo(final String manifestNo) {
	this.manifestNo = manifestNo;
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
	return super.equals(obj);
    }

    public boolean isSelected() {
	return selected;
    }

    public void setSelected(final boolean selected) {
	this.selected = selected;
    }

    public List<Product> getItems() {
	return items != null ? items : Collections.<Product> emptyList();
    }

    public void setProducts(final List<Product> items) {
	this.items = items;
    }

    public void addProduct(final Product item) {
	this.items.add(item);
    }

    public boolean isAccepted() {
	return accepted;
    }

    public void setAccepted(final boolean accepted) {
	this.accepted = accepted;
    }

    public String getConsigneeName() {
	return consigneeName;
    }

    public void setConsigneeName(final String consigneeName) {
	this.consigneeName = consigneeName;
    }

    public String getNote() {
	return note;
    }

    public void setNote(final String note) {
	this.note = note == null ? "" : note.trim();
    }

    public void setItems(final List<Product> items) {
	this.items = items;
    }

    public int getLabels() {
	return labels;
    }

    public void setLabels(final int labels) {
	this.labels = labels;
    }

}
