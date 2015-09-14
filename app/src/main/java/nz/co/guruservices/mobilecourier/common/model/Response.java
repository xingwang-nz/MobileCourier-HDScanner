package nz.co.guruservices.mobilecourier.common.model;

/**
 * A top level JSON object with code and raw result JSON string
 */
public class Response {
    private int code;

    private String message;

    public int getCode() {
	return code;
    }

    public void setCode(final int code) {
	this.code = code;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(final String message) {
	this.message = message;
    }

}
