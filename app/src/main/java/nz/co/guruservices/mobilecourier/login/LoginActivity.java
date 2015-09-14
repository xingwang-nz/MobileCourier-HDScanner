package nz.co.guruservices.mobilecourier.login;

import static nz.co.guruservices.mobilecourier.common.app.AndroidUtil.AUTH_TOKEN;
import static nz.co.guruservices.mobilecourier.common.app.AndroidUtil.DRIVER_NAME;
import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.app.ApplicationContext;
import nz.co.guruservices.mobilecourier.common.app.StringKeyValue;
import nz.co.guruservices.mobilecourier.common.http.AsynHttpResponseHandler;
import nz.co.guruservices.mobilecourier.common.http.GsonAsyncHttpClient;
import nz.co.guruservices.mobilecourier.common.model.LoginRequest;
import nz.co.guruservices.mobilecourier.common.model.LoginResponse;
import nz.co.guruservices.mobilecourier.common.model.PopuDialogType;
import nz.co.guruservices.mobilecourier.home.HomeActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

    private static final String LOGIN_URL_PATH = "login";

    private static final String ACTION_NAME = "Login";

    public static final String LOGIN_MESSAGE_NAME = "loginMessage";

    private AlertDialog alertDialog;

    private AlertDialog errorDialog;

    private TextView loginMessageTextView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	/*
	 * Check if we successfully registered in before. If we did, redirect to home page
	 */
	final String authToken = AndroidUtil.getStringValueFromSharedPreferences(this, AndroidUtil.AUTH_TOKEN);
	if (authToken != null) {
	    startMainActivity();
	    return;
	}

	setContentView(R.layout.login);

	alertDialog = AndroidUtil.buildAlertDialog(this, "Alert", PopuDialogType.ALERT);
	errorDialog = AndroidUtil.buildAlertDialog(this, "Error", PopuDialogType.ERROR);

	final Button b = (Button) findViewById(R.id.bnt_register);
	b.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(final View v) {
		final EditText driverIdText = (EditText) findViewById(R.id.text_driver_id);
		final String driverId = driverIdText.getText().toString();

		final EditText passwordText = (EditText) findViewById(R.id.text_password);
		final String password = passwordText.getText().toString();

		if (driverId.equals("")) {
		    driverIdText.requestFocus();
		    alertDialog.setMessage("Please provide " + getResources().getString(R.string.label_driver_id));
		    alertDialog.show();
		    return;
		}

		if (password.equals("")) {
		    passwordText.requestFocus();
		    alertDialog.setMessage("Please provide " + getResources().getString(R.string.label_password));
		    return;
		}

		final LoginRequest request = new LoginRequest();
		request.setDriverId(driverId);
		// 0VGQ7YBY
		request.setPassword(password);
		request.setDeviceId(getDeviceId());
		request.setMake(Build.MANUFACTURER);
		request.setModel(Build.MODEL);
		request.setOs("Android");
		request.setSdkVersion(Build.VERSION.RELEASE);

		performLogin(request);
	    }

	});

	loginMessageTextView = (TextView) findViewById(R.id.text_login_message);
	showLoginMessage();
    }

    private void showLoginMessage() {
	final Bundle extras = getIntent().getExtras();
	if (extras != null) {
	    final String loginMessage = extras.getString(LOGIN_MESSAGE_NAME);
	    if (TextUtils.isEmpty(loginMessage)) {
		loginMessageTextView.setText("");
		loginMessageTextView.setVisibility(View.GONE);
	    } else {
		loginMessageTextView.setText(loginMessage);
		loginMessageTextView.setVisibility(View.VISIBLE);
	    }
	}
    }

    private void performLogin(final LoginRequest request) {
	try {
	    final ApplicationContext applicationContext = (ApplicationContext) getApplication();

	    final GsonAsyncHttpClient<LoginResponse> client = new GsonAsyncHttpClient<LoginResponse>(this, applicationContext.constructRestUrl(LOGIN_URL_PATH));

	    client.post(request, new AsynHttpResponseHandler<LoginResponse>(this, ACTION_NAME, LoginResponse.class, errorDialog) {

		@Override
		protected void processSuccess(final LoginResponse response) {

		    AndroidUtil.saveValueInSharedPreferences(LoginActivity.this, new StringKeyValue(DRIVER_NAME, response.getResult().getDriverName()),
			    new StringKeyValue(AUTH_TOKEN, response.getResult().getAuthToken()));

		    startMainActivity();
		}
	    });

	} catch (final Exception exception) {
	    Log.e("LoginActivity", exception.getMessage(), exception);
	    errorDialog.setMessage(String.format("Login failed: %s", exception.getMessage()));
	    errorDialog.show();
	}

    }

    private String getDeviceId() {
	String identifier = null;

	final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	if (tm != null) {
	    identifier = tm.getDeviceId();
	}
	if (identifier == null || identifier.length() == 0) {
	    identifier = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}

	return identifier;

    }

    private void startMainActivity() {
	final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
	startActivity(intent);
	finish();
    }
}