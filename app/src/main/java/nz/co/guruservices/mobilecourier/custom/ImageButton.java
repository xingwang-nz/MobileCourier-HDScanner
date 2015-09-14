package nz.co.guruservices.mobilecourier.custom;

import nz.co.guruservices.mobilecourier.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *
 * Custom Image button with Image on the top and text on the button
 *
 */
public class ImageButton extends LinearLayout {

    public ImageButton(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	init(context, attrs);

    }

    private void init(final Context context, final AttributeSet attrs) {
	inflate(getContext(), R.layout.custom_image_button, this);

	final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageButton, 0, 0);
	try {

	    final Drawable icon = typedArray.getDrawable(R.styleable.ImageButton_image_button_icon);
	    final ImageView imageView = (ImageView) findViewById(R.id.icon_custom_button);
	    if (icon != null) {
		imageView.setImageDrawable(icon);
	    }

	    final String text = typedArray.getString(R.styleable.ImageButton_image_button_text);
	    final TextView textView = (TextView) findViewById(R.id.text_custom_button);
	    textView.setText(text == null ? "" : text);

	    // When inflating the layout in the ImageButton constructor, the
	    // view hierarchy does not start at the LinearLayout defined in
	    // the XML, it has an extra root node (from the HomeButton class
	    // itself I guess), the LinearLayout defined in the XML is the
	    // second child of this root node. Setting the onClick listener on
	    // the ImageButton is fine but this is not what is needed in
	    // this case, as the onClick events get consumed by the first child
	    // node...

	    // Possible solution is: call setClickable(false) on the first
	    // child.

	    getChildAt(0).setClickable(false);
	} finally {
	    typedArray.recycle();
	}
    }
}
