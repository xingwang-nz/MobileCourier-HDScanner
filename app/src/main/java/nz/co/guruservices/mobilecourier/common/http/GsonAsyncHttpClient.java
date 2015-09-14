package nz.co.guruservices.mobilecourier.common.http;

import nz.co.guruservices.mobilecourier.common.gson.GsonEntity;
import nz.co.guruservices.mobilecourier.common.model.Response;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;

/**
 *
 * RESTful http client to use Gson to marshal the request and post to server
 *
 */
public class GsonAsyncHttpClient<T extends Response> extends AsyncHttpClient {

    private final Context context;

    private final String serviceUrl;

    public GsonAsyncHttpClient(final Context context, final String serviceUrl) {
	super();
	this.context = context;
	this.serviceUrl = serviceUrl;
    }

    public void post(final Object request, final AsynHttpResponseHandler<T> handler) throws Exception {
	if (!isConnectingToInternet()) {
	    throw new Exception("No internet connection.");
	}
	super.post(context, serviceUrl, new GsonEntity(request), GsonEntity.APPLICATION_JSON, handler);
    }

    public boolean isConnectingToInternet() {
	final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (connectivityManager != null) {
	    // final NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
	    // if (info != null) {
	    // for (int i = 0; i < info.length; i++) {
	    // if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	    // return true;
	    // }
	    // }
	    // }

	    final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

	}
	return false;
    }

}
