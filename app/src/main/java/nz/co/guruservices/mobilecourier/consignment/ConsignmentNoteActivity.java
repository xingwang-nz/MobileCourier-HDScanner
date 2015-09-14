package nz.co.guruservices.mobilecourier.consignment;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.ConsignmentDetail;
import nz.co.guruservices.mobilecourier.custom.ConsignmentDetailView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConsignmentNoteActivity extends Activity {

    public static final String INTENT_PARAM_CONSIGNMENT_DETAIL = "intentConsignmentDetail";

    public static final String INTENT_PARAM_CONSIGNMENT_POSITION = "intentConsignmentPosition";

    public static final String INTENT_PARAM_CONSIGNMENT_NOTE = "intentConsignmentNote";

    private ConsignmentDetailView consignmentDetailView;

    private EditText noteText;

    private Button button;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.consignment_note);

	getActionBar().setHomeButtonEnabled(true);

	final Bundle bundle = getIntent().getExtras();
	final ConsignmentDetail consignmentDetail = (ConsignmentDetail) bundle.getSerializable(INTENT_PARAM_CONSIGNMENT_DETAIL);
	consignmentDetailView = (ConsignmentDetailView) findViewById(R.id.delivery_consignment_detail);
	consignmentDetailView.setValue(consignmentDetail);

	final int position = bundle.getInt(INTENT_PARAM_CONSIGNMENT_POSITION);

	noteText = (EditText) findViewById(R.id.delivery_consignment_note);
	if (!TextUtils.isEmpty(consignmentDetail.getConsignment().getNote())) {
	    noteText.setText(consignmentDetail.getConsignment().getNote());
	}
	// noteText.requestFocus();

	button = (Button) findViewById(R.id.bnt_save_note);
	button.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(final View view) {
		final Bundle b = new Bundle();
		b.putInt(INTENT_PARAM_CONSIGNMENT_POSITION, position);
		final String text = noteText.getText().toString();
		if (text == null || TextUtils.isEmpty(text.trim())) {
		    b.putString(INTENT_PARAM_CONSIGNMENT_NOTE, "");
		} else {
		    b.putString(INTENT_PARAM_CONSIGNMENT_NOTE, text.trim());
		}
		final Intent intent = new Intent();
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	    }

	});

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	switch (item.getItemId()) {
	    case android.R.id.home:
		setResult(RESULT_CANCELED);
		finish();
		return true;
	    default:
		return super.onOptionsItemSelected(item);
	}
    }

}
