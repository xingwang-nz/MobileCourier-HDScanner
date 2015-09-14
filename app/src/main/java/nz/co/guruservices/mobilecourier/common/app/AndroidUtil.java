package nz.co.guruservices.mobilecourier.common.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;

import static android.content.Context.MODE_PRIVATE;

public class AndroidUtil {

    public static final String APP_PREFS = "appPrefs";

    // public static final String DRIVER_ID = "driverId";

    public static final String DRIVER_NAME = "driverName";

    public static final String AUTH_TOKEN = "authToken";

    /**
     * a key for the list of deliver consignees' IDs, separated by ","
     */
    public static final String DELIVER_CONSIGNEES_ID_ORDER = "deliverConsigneesOrder";

    public static void saveValueInSharedPreferences(final Context context, final StringKeyValue... keyValues) {
        final SharedPreferences settings = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        for (final StringKeyValue keyValue : keyValues) {
            editor.putString(keyValue.getKey(), keyValue.getValue());
        }
        editor.commit();
    }

    public static void deleteValueFromSharedPreferences(final Context context, final String... keys) {
        final SharedPreferences settings = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        for (final String key : keys) {
            editor.remove(key);
        }
        editor.commit();
    }

    public static void clearSharedPreferences(final Context context) {
        final SharedPreferences settings = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public static String getStringValueFromSharedPreferences(final Context context, final String key) {
        final SharedPreferences settings = context.getSharedPreferences(APP_PREFS, MODE_PRIVATE);
        return settings.getString(key, null);
    }

    public static ProgressDialog showProgressSpinner(final Context context) {
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, null, true, false);
        progressDialog.setContentView(R.layout.custom_progress_spinner);

        return progressDialog;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static String formatDate(final Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }

    public static AlertDialog buildAlertDialog(final Context context, final String title, final PopuDialogType dialogType) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        switch (dialogType) {
            case ALERT:
                alertDialog.setIcon(R.drawable.ic_warning);
                break;
            case ERROR:
                alertDialog.setIcon(R.drawable.ic_close);
            default:
                break;
        }
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                alertDialog.dismiss();
            }
        });

        if (PopuDialogType.ERROR.equals(dialogType)) {
            alertDialog.setCancelable(false);
        } else {
            alertDialog.setCancelable(true);
        }

        return alertDialog;
    }

    // to solve 'ListView inside ScrollView' problem
    public static void setListViewSize(final ListView listView) {
        final ListAdapter myListAdapter = listView.getAdapter();
        if (myListAdapter == null) {
            // do nothing return null
            return;
        }
        // set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            final View listItem = myListAdapter.getView(size, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // setting listview item in adapter
        final ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getDividerHeight() * (myListAdapter.getCount() - 1);
        listView.setLayoutParams(params);
    }

    public static String encodeBase64(final byte[] data) {
        return data != null ? Base64.encodeToString(data, Base64.DEFAULT) : "";
    }

    public static byte[] decodeBase64String(final String base64String) {
        return !TextUtils.isEmpty(base64String) ? Base64.decode(base64String.getBytes(), Base64.DEFAULT) : null;
    }

    public static Bitmap drawViewAsBitmap(final View view) {
        final String logTag = "drawViewAsBitmap";
        Log.v(logTag, "Width: " + view.getWidth());
        Log.v(logTag, "Height: " + view.getHeight());
        final Bitmap signatureBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(signatureBitmap);
        try {
            view.draw(canvas);
            return signatureBitmap;
        } catch (final Exception e) {
            Log.v(logTag, e.toString());
            return null;
        }
    }

    public static byte[] convertBitmapToBytes(final Bitmap bitmap, final int quality) {
        if (bitmap != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            final byte[] imageBytes = baos.toByteArray();
            return imageBytes;
        } else {
            return null;
        }
    }

    /**
     * @param bitmapBytes
     * @param inSampleSize - reduce image size by 1/inSampleSize
     * @return
     */
    public static Bitmap convertBytesToBitmap(final byte[] bitmapBytes, final int inSampleSize) {
        final BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inSampleSize = 4;

        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, bounds);

    }

    public static void loadBitmapDataToView(final ImageView view, final byte[] bitmapBytes) {
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        view.setImageBitmap(bitmap);
    }

}
