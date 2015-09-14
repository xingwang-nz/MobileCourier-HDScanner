package nz.co.guruservices.mobilecourier.custom;

import java.util.Collections;
import java.util.List;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.Product;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ProductListView extends LinearLayout implements CustomView<List<Product>> {

    private ViewGroup viewGroup;

    private ListView listView;

    private ProductListAdapter adapter;

    public ProductListView(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	viewGroup = (ViewGroup) inflate(getContext(), R.layout.product_list_view, this);
	listView = (ListView) viewGroup.findViewById(R.id.view_product_list);
	adapter = new ProductListAdapter(getContext(), null);
	listView.setAdapter(adapter);

    }

    @Override
    public void setValue(final List<Product> products) {
	adapter.setProducts(products);
	adapter.notifyDataSetChanged();
    }

    private class ProductListAdapter extends BaseAdapter {

	private final Context context;

	private List<Product> products;

	public ProductListAdapter(final Context context, final List<Product> products) {
	    super();
	    this.context = context;
	    this.products = products == null ? Collections.<Product> emptyList() : products;
	}

	@Override
	public int getCount() {
	    return products.size();
	}

	@Override
	public Object getItem(final int position) {
	    return products.get(position);
	}

	@Override
	public long getItemId(final int position) {
	    return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
	    final Product product = (Product) getItem(position);

	    if (convertView == null) {
		final LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.product_list_item, null);
	    }

	    final ProductItemView productItemView = (ProductItemView) convertView.findViewById(R.id.product_item);
	    productItemView.setValue(product);

	    return convertView;
	}

	public void setProducts(final List<Product> products) {
	    this.products = products;
	}

    }

}
