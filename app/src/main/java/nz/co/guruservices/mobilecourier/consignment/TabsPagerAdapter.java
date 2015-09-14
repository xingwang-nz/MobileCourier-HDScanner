package nz.co.guruservices.mobilecourier.consignment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    /**
     * Android create more than one tabs in order to cache the tag page. the
     * first time when the first segment is visible, it doesn't fire
     * OnPageChange event, the boolean variable used to indicate if the first
     * segment (MyConsignmentFragment) first shown so that after onCreateView
     * method(where segment is initialized) is invoked, call server load
     * consignments
     */
    private boolean firstFragmentFirstLoad = true;

    public TabsPagerAdapter(final FragmentManager fm) {
	super(fm);
    }

    @Override
    public Fragment getItem(final int index) {

	if (index == 0) {
	    final MyConsignmentFragment myConsignmentFragment = new MyConsignmentFragment(firstFragmentFirstLoad);
	    firstFragmentFirstLoad = false;
	    return myConsignmentFragment;
	} else if (index == 1) {
	    return new OpenConsignmentFragment();
	} else if (index == 2) {
	    return new LoadedConsignmentFragment();
	} else {
	    return null;
	}

    }

    @Override
    public int getCount() {
	// get item count - equal to number of tabs
	return 3;
    }

}
