package nz.co.guruservices.mobilecourier.custom.draggable;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;

public class DragSortItemViewCheckable extends DragSortItemView implements Checkable {
    public DragSortItemViewCheckable(final Context context) {
	super(context);
    }

    @Override
    public boolean isChecked() {
	final View child = getChildAt(0);
	if (child instanceof Checkable) {
	    return ((Checkable) child).isChecked();
	} else {
	    return false;
	}
    }

    @Override
    public void setChecked(final boolean checked) {
	final View child = getChildAt(0);
	if (child instanceof Checkable) {
	    ((Checkable) child).setChecked(checked);
	}
    }

    @Override
    public void toggle() {
	final View child = getChildAt(0);
	if (child instanceof Checkable) {
	    ((Checkable) child).toggle();
	}
    }
}
