package nz.co.guruservices.mobilecourier.common.gson;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

/**
 *
 * A class marshal JSON object to string and set CONTENT_TYPE as
 * application/json
 *
 */
public class GsonEntity extends StringEntity {

    public static final String APPLICATION_JSON = "application/json";

    public GsonEntity(Object jsonObject) throws UnsupportedEncodingException {
	super(GsonUtil.getGson().toJson(jsonObject));
	setContentType(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
    }
}
