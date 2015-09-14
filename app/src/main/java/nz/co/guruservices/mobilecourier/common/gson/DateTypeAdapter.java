package nz.co.guruservices.mobilecourier.common.gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
	try {
	    return dateFormat.parse(jsonElement.getAsString());
	} catch (final ParseException e) {
	    throw new JsonParseException(jsonElement.getAsString(), e);
	}
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
	return date != null ? new JsonPrimitive(dateFormat.format(date)) : null;
    }

}
