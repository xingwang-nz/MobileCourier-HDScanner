package nz.co.guruservices.mobilecourier.custom;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Product;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductItemView extends LinearLayout implements CustomView<Product> {

    public ProductItemView(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	inflate(getContext(), R.layout.product_item, this);
    }

    @Override
    public void setValue(final Product product) {

	final TextView nameField = (TextView) findViewById(R.id.text_product_desc);
	nameField.setText(product.getItemDesc());

	final TextView codeField = (TextView) findViewById(R.id.text_product_code);
	codeField.setText(product.getItemCode());

	final TextView quantityField = (TextView) findViewById(R.id.text_product_quantity);
	quantityField.setText(String.valueOf(product.getQuantity()));

	final TextView serialNoField = (TextView) findViewById(R.id.text_product_serial_no);
	serialNoField.setText(product.getSerialNumber() == null ? "" : product.getSerialNumber());

	final TextView packageSizeField = (TextView) findViewById(R.id.text_product_pack_size);
	packageSizeField.setText(product.getPackSize());

    }

}
