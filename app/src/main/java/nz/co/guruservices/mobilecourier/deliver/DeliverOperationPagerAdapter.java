package nz.co.guruservices.mobilecourier.deliver;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DeliverOperationPagerAdapter extends FragmentPagerAdapter {

    public DeliverOperationPagerAdapter(final FragmentManager fm) {
	super(fm);
    }

    @Override
    public Fragment getItem(final int position) {
	if (position == 0) {
	    return new DeliveryFragment();

	} else if (position == 1) {

	    return new LeaveCardSegment();
	}
	return null;
    }

    @Override
    public int getCount() {
	return 2;
    }

}
