package nz.co.guruservices.mobilecourier.common.model;

import java.util.HashMap;
import java.util.Map;

public enum DeliveryType {
    STORE("Store"),
    HOME("Home"),
    RURAL("Rural");

    private static final Map<String, DeliveryType> NAME_TYPE = new HashMap<String, DeliveryType>();

    static {
	for (final DeliveryType type : values()) {
	    NAME_TYPE.put(type.getName(), type);
	}
    }

    private final String name;

    private DeliveryType(final String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public static DeliveryType valueOfName(final String name) {
	final DeliveryType type = NAME_TYPE.get(name);

	return type != null ? type : HOME;
    }

}
