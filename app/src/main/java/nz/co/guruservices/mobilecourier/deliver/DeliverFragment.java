package nz.co.guruservices.mobilecourier.deliver;

import nz.co.guruservices.mobilecourier.common.app.AndroidUtil;
import nz.co.guruservices.mobilecourier.common.app.ApplicationContext;
import nz.co.guruservices.mobilecourier.common.http.AsynHttpResponseHandler;
import nz.co.guruservices.mobilecourier.common.http.GsonAsyncHttpClient;
import nz.co.guruservices.mobilecourier.common.model.DeliveryType;
import nz.co.guruservices.mobilecourier.common.model.ProcessConsignmentRequest;
import nz.co.guruservices.mobilecourier.common.model.Response;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public abstract class DeliverFragment extends Fragment {

    protected DeliverOperationActivity parent;

    protected ApplicationContext applicationContext;

    protected String authToken;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
	parent = (DeliverOperationActivity) getActivity();
	applicationContext = (ApplicationContext) parent.getApplication();
	authToken = AndroidUtil.getStringValueFromSharedPreferences(parent, AndroidUtil.AUTH_TOKEN);

	return initializeFragmentView(inflater, container, savedInstanceState);
    }

    protected abstract View initializeFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract View getRootView();

    protected abstract RadioGroup getDeliveryTypeRadioGroup();

    protected DeliveryType getSelectedDeliveryType() {
	final View rootView = getRootView();
	final RadioGroup radioGroup = getDeliveryTypeRadioGroup();
	final RadioButton selectedRadio = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());

	return DeliveryType.valueOfName((String) selectedRadio.getText());

    }

    public DeliverOperationActivity getParent() {
	return parent;
    }

    protected <T extends ProcessConsignmentRequest> void performDeliver(final T t, final String restUrl, final String action) {
	try {
	    final GsonAsyncHttpClient<Response> client = new GsonAsyncHttpClient<Response>(parent, applicationContext.constructRestUrl(restUrl));
	    client.post(t, new AsynHttpResponseHandler<Response>(parent, action, Response.class, parent.getErrorDialog()) {

		@Override
		public void onStart() {
		    parent.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onFinish() {
		    parent.setProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected void processSuccess(final Response response) {
		    parent.processedAndReturn();
		}

	    });
	} catch (final Exception e) {
	    e.printStackTrace();
	    parent.showError("Error while process " + action + e.getMessage());
	}
    }
}
