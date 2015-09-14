package nz.co.guruservices.mobilecourier.common.model;

public class JobSummary {
    private String driverName;

    private String organisation;

    private String area;

    private int myConsignmentSubTotal;

    private int openConsignmentSubTotal;

    private int loadedConsignmentSubTotal;

    public String getDriverName() {
	return driverName;
    }

    public void setDriverName(final String driverName) {
	this.driverName = driverName;
    }

    public String getOrganisation() {
	return organisation;
    }

    public void setOrganisation(final String organisation) {
	this.organisation = organisation;
    }

    public String getArea() {
	return area;
    }

    public void setArea(final String area) {
	this.area = area;
    }

    public int getMyConsignmentSubTotal() {
	return myConsignmentSubTotal;
    }

    public void setMyConsignmentSubTotal(final int myConsignmentSubTotal) {
	this.myConsignmentSubTotal = myConsignmentSubTotal;
    }

    public int getOpenConsignmentSubTotal() {
	return openConsignmentSubTotal;
    }

    public void setOpenConsignmentSubTotal(final int openConsignmentSubTotal) {
	this.openConsignmentSubTotal = openConsignmentSubTotal;
    }

    public int getLoadedConsignmentSubTotal() {
	return loadedConsignmentSubTotal;
    }

    public void setLoadedConsignmentSubTotal(final int loadedConsignmentSubTotal) {
	this.loadedConsignmentSubTotal = loadedConsignmentSubTotal;
    }
}
