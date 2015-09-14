package nz.co.guruservices.mobilecourier.signature;

import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {
    private static final float STROKE_WIDTH = 10f;

    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

    private Paint paint = new Paint();

    private Path path = new Path();

    private float lastTouchX;

    private float lastTouchY;

    private final RectF dirtyRect = new RectF();

    private List<OnStartSignListener> onStartSignListeners = new ArrayList<OnStartSignListener>();

    // private static final int MAX_SIGNATURE_BASE64_LENGTH = 102400;//100K
    private static final int MAX_SIGNATURE_BASE64_LENGTH = 51200;// 50K

    private static final int MAX_BITMAP_QUALITY = 100;

    /**
     * quality reduce step when convert bitmap to bytes
     */
    private static final int BITMAP_QUALITY_BIG_STEP = 10;

    private static final int BITMAP_QUALITY_MEDIUM_STEP = 5;

    private static final int BITMAP_QUALITY_SMAL_STEP = 1;

    public SignatureView(final Context context, final AttributeSet attrs) {
	super(context, attrs);
	paint.setAntiAlias(true);
	paint.setColor(Color.BLACK);
	paint.setStyle(Paint.Style.STROKE);
	paint.setStrokeJoin(Paint.Join.ROUND);
	paint.setStrokeWidth(STROKE_WIDTH);
    }

    public String getSignatureBase64String() {
	final Bitmap bitmap = getSignatureBitmap();
	return doGetSignatureBase64String(bitmap, MAX_BITMAP_QUALITY);

    }

    /**
     * recursively check the size of base64 string, reduce the image size until the size is less than the MAX_SIGNATURE_BASE64_LENGTH<br/>
     * if quality < 5, that mean the size too big to be reduced, just return the result, server will throw an exception, then user to
     * re-draw the signature, this is unlikely happens
     *
     * @param bitmap
     * @param quality
     * @return
     */
    private String doGetSignatureBase64String(final Bitmap bitmap, final int quality) {
	final byte[] bytes = AndroidUtil.convertBitmapToBytes(bitmap, quality);
	final String encodedBased64String = AndroidUtil.encodeBase64(bytes);
	if (encodedBased64String.length() > MAX_SIGNATURE_BASE64_LENGTH) {
	    if (quality > 50) {
		return doGetSignatureBase64String(bitmap, quality - BITMAP_QUALITY_BIG_STEP);
	    } else if (quality > 5) {
		return doGetSignatureBase64String(bitmap, quality - BITMAP_QUALITY_MEDIUM_STEP);
	    } else if (quality > 2) {
		return doGetSignatureBase64String(bitmap, quality - BITMAP_QUALITY_SMAL_STEP);
	    } else {
		// the image is really toooooo big and let server throw exception to ask use to re-draw the image, very unlikely happens
		return encodedBased64String;
	    }
	} else {
	    return encodedBased64String;
	}
    }

    private Bitmap getSignatureBitmap() {
	return AndroidUtil.drawViewAsBitmap(this);
    }

    public void addOnStartSignListener(final OnStartSignListener listener) {
	this.onStartSignListeners.add(listener);
    }

    public void clear() {
	path.reset();
	invalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
	canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
	final float eventX = event.getX();
	final float eventY = event.getY();

	for (final OnStartSignListener listener : onStartSignListeners) {
	    // getSignButton.setEnabled(true);
	    listener.startSign();
	}

	switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
		path.moveTo(eventX, eventY);
		lastTouchX = eventX;
		lastTouchY = eventY;
		return true;

	    case MotionEvent.ACTION_MOVE:

	    case MotionEvent.ACTION_UP:

		resetDirtyRect(eventX, eventY);
		final int historySize = event.getHistorySize();
		for (int i = 0; i < historySize; i++) {
		    final float historicalX = event.getHistoricalX(i);
		    final float historicalY = event.getHistoricalY(i);
		    expandDirtyRect(historicalX, historicalY);
		    path.lineTo(historicalX, historicalY);
		}
		path.lineTo(eventX, eventY);
		break;

	    default:
		debug("Ignored touch event: " + event.toString());
		return false;
	}

	invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH), (int) (dirtyRect.top - HALF_STROKE_WIDTH), (int) (dirtyRect.right + HALF_STROKE_WIDTH),
	        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

	lastTouchX = eventX;
	lastTouchY = eventY;

	return true;
    }

    private void debug(final String string) {
    }

    private void expandDirtyRect(final float historicalX, final float historicalY) {
	if (historicalX < dirtyRect.left) {
	    dirtyRect.left = historicalX;
	} else if (historicalX > dirtyRect.right) {
	    dirtyRect.right = historicalX;
	}

	if (historicalY < dirtyRect.top) {
	    dirtyRect.top = historicalY;
	} else if (historicalY > dirtyRect.bottom) {
	    dirtyRect.bottom = historicalY;
	}
    }

    private void resetDirtyRect(final float eventX, final float eventY) {
	dirtyRect.left = Math.min(lastTouchX, eventX);
	dirtyRect.right = Math.max(lastTouchX, eventX);
	dirtyRect.top = Math.min(lastTouchY, eventY);
	dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}
