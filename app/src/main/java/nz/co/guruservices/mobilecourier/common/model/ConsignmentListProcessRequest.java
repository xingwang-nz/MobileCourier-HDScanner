package nz.co.guruservices.mobilecourier.common.model;

import java.util.ArrayList;
import java.util.List;

public class ConsignmentListProcessRequest {
    private String authToken;

    private List<Long> consignmentIds = new ArrayList<Long>();

    public List<Long> getConsignmentIds() {
	return consignmentIds;
    }

    public void setConsignmentIds(final List<Long> consignmentIds) {
	this.consignmentIds = consignmentIds;
    }

    public void addConsignment(final long consignmentId) {
	consignmentIds.add(consignmentId);
    }

    public String getAuthToken() {
	return authToken;
    }

    public void setAuthToken(final String authToken) {
	this.authToken = authToken;
    }

}
