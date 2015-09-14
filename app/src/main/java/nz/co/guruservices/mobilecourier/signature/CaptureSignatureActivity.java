package nz.co.guruservices.mobilecourier.signature;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class CaptureSignatureActivity extends Activity {

    private SignatureView signatureView;

    private Button clearButton;

    private Button getSignButton;

    private Button cancelButton;

    public static final String SIGNATURE_DATA = "signatureData";

    public static final String SIGNATURE_STATUS = "signatureStatus";

    public static final String SIGNATURE_DONE = "signatureDone";

    public static final String SIGNATURE_CANCELLED = "signatureCancelled";

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.signature);

	signatureView = (SignatureView) findViewById(R.id.signature_view);

	clearButton = (Button) findViewById(R.id.clear);
	getSignButton = (Button) findViewById(R.id.getsign);
	getSignButton.setEnabled(false);
	cancelButton = (Button) findViewById(R.id.cancel);

	signatureView.addOnStartSignListener(new OnStartSignListener() {
	    @Override
	    public void startSign() {
		getSignButton.setEnabled(true);
	    }
	});

	clearButton.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(final View v) {
		Log.v("CaptureSignatureActivity", "Panel Cleared");
		signatureView.clear();
		getSignButton.setEnabled(false);
	    }
	});

	getSignButton.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(final View v) {
		new GetSignatureTask().execute();
	    }
	});

	cancelButton.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(final View v) {
		final Bundle b = new Bundle();
		b.putString(SIGNATURE_STATUS, SIGNATURE_CANCELLED);
		final Intent intent = new Intent();
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	    }
	});
    }

    private class GetSignatureTask extends AsyncTask<Void, Void, String> {

	@Override
	protected String doInBackground(final Void... params) {
	    return signatureView.getSignatureBase64String();
	}

	@Override
	protected void onPreExecute() {
	    progressDialog = AndroidUtil.showProgressSpinner(CaptureSignatureActivity.this);
	}

	@Override
	protected void onPostExecute(final String result) {
	    progressDialog.dismiss();
	    final Bundle b = new Bundle();
	    b.putString(SIGNATURE_STATUS, SIGNATURE_DONE);
	    b.putString(SIGNATURE_DATA, result);
	    final Intent intent = new Intent();
	    intent.putExtras(b);
	    setResult(RESULT_OK, intent);
	    finish();
	}

    }

    @Override
    protected void onDestroy() {
	Log.w("CaptureSignatureActivity", "onDestory");
	super.onDestroy();
    }

}