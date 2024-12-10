package com.s339862.mappe3;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;


public class MapsActivity extends AppCompatActivity implements
        OnMarkerClickListener,
        OnMapClickListener,
        OnMapReadyCallback {

    // This is used to store the Google maps object
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Set event listeneres for the map and markers
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        // The CustomInfoWindowAdapter class is used to display custom layouts within the info window
        // This is necessary to show all the required information about the markers
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater()));

        // Default location is Oslo and the zoom level is set to 10
        LatLng oslo = new LatLng(59.91, 10.76);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo, 10));

        // Makes a request to the server, gets the markers and adds them to the map
        fetchMarkersFromServer();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Centers the map on the marker and shows the info window
        CameraUpdate update = CameraUpdateFactory.newLatLng(marker.getPosition());
        mMap.moveCamera(update);
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Shows a dialog to create a new marker when the map is clicked
        showCreateMarkerDialog(latLng);
    }

    private void showCreateMarkerDialog(LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_marker);

        // The dialog uses the create_marker_dialog.xml layout
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.create_marker_dialog, null);
        builder.setView(view);

        EditText descriptionText = view.findViewById(R.id.description_text);
        EditText positiveInfoText = view.findViewById(R.id.positive_text);

        // The dialog has two buttons, one to cancel and one to create the marker
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.setPositiveButton(R.string.create, (dialog, which) -> {
            // Gets values from input fields
            String description = descriptionText.getText().toString();
            String positiveInfo = positiveInfoText.getText().toString();
            // creates marker on the map and sends it to the server
            createMarker(description, positiveInfo, latLng);
        });
        builder.show();
    }

    private void createMarker(String description, String positiveInfo, LatLng latLng) {
        // Adds a marker to the map
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet("Noe positivt om stedet: " + positiveInfo));
        marker.setTag("Beskrivelse: " + description);

        // Uses Geocode API to get the address from the location
        // Then sets the address as the title of the marker
        setAddressFromLocation(latLng, marker);

        // Tries to create a JSON object with the marker data and send it to the server
        try {
            JSONObject json = new JSONObject();
            json.put("description", description);
            json.put("positive_info", positiveInfo);
            json.put("lat", latLng.latitude);
            json.put("lng", latLng.longitude);
            sendMarkerToServer(json);
        } catch (Exception e) {
            Log.e("MapsActivity", "Error creating marker", e);
        }
    }

    private void fetchMarkersFromServer() {
        // Creates a new thread before making a request to the server
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Makes a GET request to the URL and gets the response
                URL url = new URL("https://dave3600.cs.oslomet.no/~s339862/jsonout.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();

                // If the response code is OK, the markers are added to the map
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Converts the raw byte data from the server to a json string
                    String response = inputStreamToString(connection.getInputStream());
                    // Converts the json string to a list of json objects
                    List<JSONObject> markers = stringToJsonObjectList(response);
                    // Adds the markers to the map
                    // runOnUiThread is used to update the UI on the main thread
                    runOnUiThread(() -> addMarkersToMap(markers));
                } else {
                    // Shows a toast message if response is not OK
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                // Shows a toast message if an exception occurs
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void addMarkersToMap(List<JSONObject> markers) {
        for (JSONObject markerData : markers) {
            // Tries to create a marker from the json object and adds it to the map
            try {
                LatLng latLng = new LatLng(markerData.getDouble("lat"), markerData.getDouble("lng"));
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet("Noe positivt om stedet: " + markerData.getString("positive_info")));
                marker.setTag("Beskrivelse: " + markerData.getString("description"));
                setAddressFromLocation(latLng, marker);
            } catch (Exception e) {
                // Shows a toast message and logs the error if an exception occurs
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MapsActivity", "Error adding markers to map", e);
            }
        }
    }

    private void sendMarkerToServer(JSONObject json) {
        // Creates a new thread before making a request to the server
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Sends the json object with a POST request to the server
                URL url = new URL("https://dave3600.cs.oslomet.no/~s339862/jsonin.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // This line converts the json object to bytes and writes it to the output stream
                // Then the data is sent to the server
                connection.getOutputStream().write(json.toString().getBytes());
                int responseCode = connection.getResponseCode();

                // Toast messages are shown on the main thread to inform the user about the result
                if (responseCode == HttpURLConnection.HTTP_OK)
                    runOnUiThread(() -> Toast.makeText(this, "Marker successfully sent to the server", Toast.LENGTH_SHORT).show());
                else
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e("MapsActivity", "Error sending marker to server", e);
            }
        });
    }

    // Converts the byte data from the server to a json string
    private String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            builder.append(line);
        reader.close();
        return builder.toString();
    }

    // Converts the json string to a list of json objects
    private List<JSONObject> stringToJsonObjectList(String jsonString) {
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++)
                list.add(jsonArray.getJSONObject(i));
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MapsActivity", "Error converting string to JSON object list", e);
        }
        return list;
    }

    // Uses Geocode API to get the address from the coordinates
    private void setAddressFromLocation(LatLng latLng, Marker marker) {
        // This is used to communicate with the Geocode API
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Uses a separate thread when sending a request to the Geocode API
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Gets a list of addresses with these coordinates, but only the first one is used
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                // Checks if the result is null or the list is empty
                if (addresses == null || addresses.isEmpty())
                    return;
                // Get the first address in the list as a string
                String address = addresses.get(0).getAddressLine(0);
                // Sets the address as the title of the marker on the main thread
                runOnUiThread(() -> marker.setTitle(address));
            } catch (Exception e) {
                // Shows a toast message and logs the error if an exception occurs
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e("MapsActivity", "Error getting address from location", e);
            }
        });
    }

}