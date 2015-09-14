package nz.co.guruservices.mobilecourier.common.gson;

import java.lang.reflect.Modifier;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

    private static Gson GSON;

    public static Gson getGson() {
	if (GSON == null) {
	    final GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(Date.class, new DateTypeAdapter());
	    GSON = gsonBuilder.excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
	}

	return GSON;
    }
}
