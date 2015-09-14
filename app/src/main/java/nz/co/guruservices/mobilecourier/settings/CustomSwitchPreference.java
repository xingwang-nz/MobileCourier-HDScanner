package nz.co.guruservices.mobilecourier.settings;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class CustomSwitchPreference extends SwitchPreference {

    public CustomSwitchPreference(final Context context) {
        super(context);
    }

    public CustomSwitchPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomSwitchPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(final View view) {
        super.onBindView(view);

        final TextView title = (TextView) view.findViewById(android.R.id.title);
        // final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        // ViewGroup.LayoutParams.WRAP_CONTENT);
        // title.setLayoutParams(layoutParams);
        title.setSingleLine(false);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }

}
