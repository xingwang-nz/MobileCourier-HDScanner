package nz.co.guruservices.mobilecourier.common.model;

public class JobSummaryResponse extends Response {
    private JobSummary result;

    public JobSummary getResult() {
	return result;
    }

    public void setResult(final JobSummary result) {
	this.result = result;
    }
}
