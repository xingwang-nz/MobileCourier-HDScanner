package nz.co.guruservices.mobilecourier.common.http;

import java.util.HashMap;
import java.util.Map;

/**
 * The application response codes, these codes are NOT the standard http codes
 *
 *
 */
public enum ApplicationResponseCode {
    UN_KNOWN(0, "Unknown"),
    SUCCESS(2000, "Success"),

    DIFFERENT_DRIVER(1003, "Different driver"),
    SOME_FAILED(2001, "Some of records failed"),

    // INVALID_DRIVER_ID(3001, "Invalid driver ID"),
    DEVICE_ALREADY_REGISTERED(3002, "Device already registered"),

    AUTHENTICATION_ERROR(4000, "Authentication_error"),
    LOGIN_FAILED(4001, "Login Failed"),

    GENERIC_INTERNAL_ERROR(5000, "Internal error"),
    INVALID_MISSING_PARAMETER(5001, "Invalid or missing parameter"),

    JOB_NOT_FOUND(6000, "Job not found"),
    JOB_STATUS_INVALID(6001, "Job status invalid"),
    JOB_HAS_WRONG_QUANTITY(6002, "Job has wrong quantity"),
    SIGNATURE_TOO_LARGE(6100, "Signature too large"),
    INVALID_DELIVERY_TYPE(6201, "Invalid delivery type"),
    INVALID_DELIVERY_STATUS(6202, "Invalid delivery status"),
    PLEASE_TRY_AGAIN(7000, "Please try again");

    private final int code;

    private final String description;

    private static final Map<Integer, ApplicationResponseCode> map = new HashMap<Integer, ApplicationResponseCode>();

    static {
	for (final ApplicationResponseCode responseCode : ApplicationResponseCode.values()) {
	    map.put(responseCode.getCode(), responseCode);
	}
    }

    private ApplicationResponseCode(final int code, final String description) {
	this.code = code;
	this.description = description;
    }

    public int getCode() {
	return code;
    }

    public String getDescription() {
	return description;
    }

    public static ApplicationResponseCode valueOfCode(final int code) {
	final ApplicationResponseCode applicationResponseCode = map.get(code);
	return applicationResponseCode != null ? applicationResponseCode : UN_KNOWN;
    }
}
