package nz.co.guruservices.mobilecourier.common.model;


public class ReturnRequest extends NonDeliveryRequest {

    private String printName;

    private String signatureData;

    public String getPrintName() {
	return printName;
    }

    public void setPrintName(final String printName) {
	this.printName = printName;
    }

    public String getSignatureData() {
	return signatureData;
    }

    public void setSignatureData(final String signatureData) {
	this.signatureData = signatureData;
    }

}
