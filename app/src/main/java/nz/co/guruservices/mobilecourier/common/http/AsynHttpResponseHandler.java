package nz.co.guruservices.mobilecourier.common.http;

import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.gson.GsonUtil;
import nz.co.guruservices.mobilecourier.common.model.Response;
import nz.co.guruservices.mobilecourier.login.LoginActivity;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

/**
 * A super class process http response string and unmarshal it to <code>Response</code>
 *
 */
public abstract class AsynHttpResponseHandler<T extends Response> extends TextHttpResponseHandler {

    private final Activity activity;

    private final String actionName;

    private final Class<T> responseClass;

    private AlertDialog errorDialog;

    public AsynHttpResponseHandler(final Activity activity, final String actionName, final Class<T> responseClass, final AlertDialog errorDialog) {
	this(activity, actionName, responseClass);
	this.errorDialog = errorDialog;
    }

    public AsynHttpResponseHandler(final Activity activity, final String actionName, final Class<T> responseClass) {
	super();

	this.activity = activity;
	this.actionName = actionName;
	this.responseClass = responseClass;
    }

    /**
     * http response code is not 200
     */
    @Override
    public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
	Log.e(actionName, throwable.getMessage(), throwable);
	showError(String.format("%s failed: %s, %s", actionName, statusCode, throwable.getMessage()));

    }

    /**
     * http response code is 200, unmarshal the JSON string and process the response
     */
    @Override
    public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
	final T response = GsonUtil.getGson().fromJson(responseString, responseClass);

	if (ApplicationResponseCode.SUCCESS.getCode() == response.getCode()) {
	    processSuccess(response);
	} else {
	    processFailure(response);
	}
    }

    protected abstract void processSuccess(T response);

    /**
     * by default, just display a toaster to show the error code and description
     *
     * @param response
     */
    protected void processFailure(final T response) {
	// if authentication failed, go to register page and forget all activities history
	if (ApplicationResponseCode.AUTHENTICATION_ERROR.getCode() == response.getCode()) {
	    AndroidUtil.clearSharedPreferences(activity);
	    final Intent loginIntent = new Intent(activity, LoginActivity.class);
	    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    if (!TextUtils.isEmpty(response.getMessage())) {
		loginIntent.putExtra(LoginActivity.LOGIN_MESSAGE_NAME, response.getMessage());
	    }
	    activity.startActivity(loginIntent);
	    activity.finish();
	} else {
	    if (!TextUtils.isEmpty(response.getMessage())) {
		showError(String.format("%s failed: %s", actionName, response.getMessage()));
	    } else {
		showError(String.format("%s failed: %s", actionName, ApplicationResponseCode.valueOfCode(response.getCode()).getDescription()));
	    }

	}

    }

    private void showError(final String error) {
	if (errorDialog != null) {
	    errorDialog.setMessage(error);
	    errorDialog.show();
	} else {
	    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
	}
    }
}
