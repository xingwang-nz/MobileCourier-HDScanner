package nz.co.guruservices.mobilecourier.deliver;

import static nz.co.guruservices.mobilecourier.deliver.DeliverActivity.INTENT_PARAM_GEO_POSITIONS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nz.co.guruservices.mobilecourier.R;
import nz.co.guruservices.mobilecourier.common.model.GeoPosition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteActivity extends Activity {

    private ActionBar actionBar;

    private GoogleMap map;

    private ArrayList<GeoPosition> geoPositions;

    // private ArrayList<LatLng> latLngList;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	// enable to show the spinner on action bar
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setProgressBarIndeterminateVisibility(false);

	setContentView(R.layout.deliver_route);
	initActionBar();

	try {
	    // load map
	    initilizeMap();

	    if (map == null) {
		return;
	    }

	    geoPositions = (ArrayList<GeoPosition>) getIntent().getExtras().getSerializable(INTENT_PARAM_GEO_POSITIONS);
	    if (geoPositions.size() == 0) {
		return;
	    }

	    // latLngList = new ArrayList<LatLng>();

	    // add markers
	    for (final GeoPosition geoPosition : geoPositions) {
		final LatLng latLng = new LatLng(geoPosition.getLatitude(), geoPosition.getLongitude());
		map.addMarker(new MarkerOptions().position(latLng).title(geoPosition.getTitle()));
		// latLngList.add(latLng);
	    }

	    if (geoPositions.size() > 1) {
		drawRoutes(geoPositions);
	    }

	    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(geoPositions.get(0).getLatitude(), geoPositions.get(0).getLongitude()), 10.0f));

	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    private void initilizeMap() {
	if (map == null) {
	    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

	    // check if map is created
	    if (map == null) {
		Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_LONG).show();
	    } else {
		map.setMyLocationEnabled(true);
		// Enable MyLocation Button in the Map

		map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
		    @Override
		    public void onMapLoaded() {
			// zoomToCoverAllMarkers();
		    }
		});

	    }
	}

    }

    private void initActionBar() {
	actionBar = getActionBar();
	actionBar.setHomeButtonEnabled(true);
    }

    private void zoomToCoverAllMarkers() {

	final LatLngBounds.Builder builder = new LatLngBounds.Builder();
	/*
	 * for (Marker marker : markers) { builder.include(marker.getPosition()); }
	 */
	// for (final LatLng marker : latLngList) {
	// builder.include(marker);
	// }

	for (final GeoPosition geoPosition : geoPositions) {
	    final LatLng latLng = new LatLng(geoPosition.getLatitude(), geoPosition.getLongitude());
	    builder.include(latLng);
	}

	final LatLngBounds bounds = builder.build();
	final int padding = 0; // offset from edges of the map in pixels
	final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
	map.moveCamera(cu);
	map.animateCamera(cu);
    }

    private void drawRoutes(final ArrayList<GeoPosition> positions) {
	// draw route
	final String url = getDirectionsUrl(positions);
	final DownloadTask downloadTask = new DownloadTask();
	// Start downloading json data from Google Directions API
	downloadTask.execute(url);
    }

    private String getDirectionsUrl(final ArrayList<GeoPosition> positions) {
	final StringBuilder urlBuilder = new StringBuilder();
	urlBuilder.append("https://maps.googleapis.com/maps/api/directions/json?");

	final GeoPosition originPosition = positions.get(0);
	urlBuilder.append("origin=").append(originPosition.getLatitude()).append(",").append(originPosition.getLongitude());

	final GeoPosition destination = positions.get(positions.size() - 1);
	urlBuilder.append("&destination=").append(destination.getLatitude()).append(",").append(destination.getLongitude());

	if (positions.size() > 2) {
	    urlBuilder.append("&waypoints=");
	    for (int i = 1; i < positions.size() - 1; i++) {
		urlBuilder.append(i == 1 ? "" : "|");
		urlBuilder.append(positions.get(i).getLatitude()).append(",").append(positions.get(i).getLongitude());
	    }
	}

	urlBuilder.append("&mode=driving");
	return urlBuilder.toString();
    }

    /** A method to download json data from url */
    private String downloadUrl(final String strUrl) throws IOException {
	String data = "";
	InputStream iStream = null;
	HttpURLConnection urlConnection = null;
	try {
	    final URL url = new URL(strUrl);

	    // Creating an http connection to communicate with url
	    urlConnection = (HttpURLConnection) url.openConnection();

	    // Connecting to url
	    urlConnection.connect();

	    // Reading data from url
	    iStream = urlConnection.getInputStream();

	    final BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

	    final StringBuffer sb = new StringBuffer();

	    String line = "";
	    while ((line = br.readLine()) != null) {
		sb.append(line);
	    }

	    data = sb.toString();

	    br.close();

	} catch (final Exception e) {
	    Log.d("Exception while downloading url", e.toString());
	} finally {
	    iStream.close();
	    urlConnection.disconnect();
	}
	return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

	private ProgressDialog progressDialog;

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    setProgressBarIndeterminateVisibility(true);

	    // progressDialog = new ProgressDialog(RouteActivity.this);
	    // progressDialog.setMessage("Fetching route, Please wait...");
	    // progressDialog.setIndeterminate(true);
	    // progressDialog.show();
	}

	// Downloading data in non-ui thread
	@Override
	protected String doInBackground(final String... url) {

	    // For storing data from web service

	    String data = "";

	    try {
		// Fetching the data from web service
		data = downloadUrl(url[0]);
	    } catch (final Exception e) {
		Log.d("Background Task", e.toString());
	    }
	    return data;
	}

	// Executes in UI thread, after the execution of
	// doInBackground()
	@Override
	protected void onPostExecute(final String result) {
	    super.onPostExecute(result);

	    final ParserTask parserTask = new ParserTask();

	    // Invokes the thread for parsing the JSON data
	    parserTask.execute(result);
	}
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<LatLng>>> {

	// Parsing the data in non-ui thread
	@Override
	protected List<List<LatLng>> doInBackground(final String... jsonData) {

	    List<List<LatLng>> routes = null;

	    try {
		final JSONObject jObject = new JSONObject(jsonData[0]);
		final DirectionsJSONParser parser = new DirectionsJSONParser();

		// Starts parsing data
		routes = parser.parse(jObject);
	    } catch (final Exception e) {
		e.printStackTrace();
	    }
	    return routes;
	}

	// Executes in UI thread, after the parsing process
	@Override
	protected void onPostExecute(final List<List<LatLng>> result) {
	    // progressDialog.hide();
	    setProgressBarIndeterminateVisibility(false);

	    // Traversing through all the routes
	    for (int i = 0; i < result.size(); i++) {
		final ArrayList<LatLng> points = new ArrayList<LatLng>();

		final PolylineOptions lineOptions = new PolylineOptions();

		// Fetching i-th route
		final List<LatLng> path = result.get(i);

		// Fetching all the points in i-th route
		for (int j = 0; j < path.size(); j++) {

		    // final HashMap<String, String> point = path.get(j);
		    //
		    // final double lat = Double.parseDouble(point.get("lat"));
		    // final double lng = Double.parseDouble(point.get("lng"));

		    points.add(path.get(j));
		}

		// Adding all the points in the route to LineOptions
		lineOptions.addAll(points);
		lineOptions.width(5);
		lineOptions.color(Color.BLUE);

		// Drawing polyline in the Google Map for the i-th route
		map.addPolyline(lineOptions);
	    }

	}
    }

    public class DirectionsJSONParser {

	/**
	 * Receives a JSONObject and returns a list of lists containing latitude and longitude
	 */
	public List<List<LatLng>> parse(final JSONObject jObject) {

	    final List<List<LatLng>> routes = new ArrayList<List<LatLng>>();

	    try {

		final JSONArray jRoutes = jObject.getJSONArray("routes");

		/** Traversing all routes */
		for (int i = 0; i < jRoutes.length(); i++) {

		    final JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
		    /** Traversing all legs */
		    for (int j = 0; j < jLegs.length(); j++) {

			final List<LatLng> path = new ArrayList<LatLng>();

			final JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
			/** Traversing all steps */
			for (int k = 0; k < jSteps.length(); k++) {
			    final String polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
			    /** Add all points in this step */
			    path.addAll(decodePoly(polyline));
			}
			routes.add(path);
		    }
		}
	    } catch (final JSONException e) {
		e.printStackTrace();
	    } catch (final Exception e) {
	    }
	    return routes;
	}

	/**
	 * Method to decode polyline points Courtesy : jeffreysambells.com/2010/05
	 * /27/decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private List<LatLng> decodePoly(final String encoded) {

	    final List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0;
	    final int len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
		int b, shift = 0, result = 0;
		do {
		    b = encoded.charAt(index++) - 63;
		    result |= (b & 0x1f) << shift;
		    shift += 5;
		} while (b >= 0x20);
		final int dlat = (result & 1) != 0 ? ~(result >> 1) : result >> 1;
			lat += dlat;

			shift = 0;
			result = 0;
			do {
			    b = encoded.charAt(index++) - 63;
			    result |= (b & 0x1f) << shift;
			    shift += 5;
			} while (b >= 0x20);
			final int dlng = (result & 1) != 0 ? ~(result >> 1) : result >> 1;
		lng += dlng;

		final LatLng p = new LatLng(lat / 1E5, lng / 1E5);
		poly.add(p);
	    }

	    return poly;
	}
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
	final int itemId = item.getItemId();
	switch (itemId) {
	    case android.R.id.home: {
		final Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
		return true;
	    }
	    default:
		return super.onOptionsItemSelected(item);
	}

    }

}
