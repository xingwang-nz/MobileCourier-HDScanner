package nz.co.guruservices.mobilecourier.common.model;

import java.io.Serializable;

public class ItemData implements Serializable {
    private long id;

    public long getId() {
	return id;
    }

    public void setId(final long id) {
	this.id = id;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (id ^ id >>> 32);
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ItemData other = (ItemData) obj;
	if (id != other.id) {
	    return false;
	}
	return true;
    }

}
