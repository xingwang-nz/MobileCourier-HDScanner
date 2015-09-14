package nz.co.guruservices.mobilecourier.common.mapper;

import java.util.Collections;
import java.util.List;

import nz.co.guruservices.mobilecourier.common.model.Consignee;
import nz.co.guruservices.mobilecourier.common.model.Consignment;
import nz.co.guruservices.mobilecourier.common.model.ListConsignmentResponse;
import android.util.Log;

public class ListConsignmentResponseConsigneeMapper implements BeanMapper<ListConsignmentResponse, List<Consignee>> {

    @Override
    public List<Consignee> map(final ListConsignmentResponse response) {

	if (response.getResult() == null) {
	    Log.w("ListConsignmentResponseConsigneeMapper", "The result of load consignment response is null");
	    return Collections.<Consignee> emptyList();
	}

	// set the consignments for each consignee
	final List<Consignee> consignees = response.getResult().getConsignees() != null ? response.getResult().getConsignees() : Collections
	        .<Consignee> emptyList();

	final List<Consignment> consignments = response.getResult().getConsignments() != null ? response.getResult().getConsignments() : Collections
	        .<Consignment> emptyList();

	for (final Consignee consignee : consignees) {
	    for (final Consignment consignment : consignments) {
		if (consignee.getId() == consignment.getConsigneeId()) {
		    consignment.setConsigneeName(consignee.getName());
		    consignee.addConsighment(consignment);
		    // fake some produc
		    // final Product product = new Product();
		    // product.setCode("TH-50AS640Z");
		    // product.setName("TV FHD SMART LED 100HZ");
		    // product.setQuantity(1);
		    // product.setSerialNo("S12345678");
		    // for (int i = 0; i < 20; i++) {
		    // consignment.addProduct(product);
		    // }
		}
	    }
	}

	return consignees;
    }

}
