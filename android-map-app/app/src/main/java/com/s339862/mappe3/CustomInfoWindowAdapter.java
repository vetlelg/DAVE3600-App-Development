package com.s339862.mappe3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

// Custom InfoWindowAdapter for the Google Maps API
// This class is used to customize the layout of the info window that appears when a marker is clicked
public class CustomInfoWindowAdapter implements InfoWindowAdapter {
    private final View mContents;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        // Layoutinflater is used to set the layout with the custom_info_contents.xml file
        mContents = inflater.inflate(R.layout.custom_info_contents, null);
    }

    // This is used to set the layout of the entire info window
    public View getInfoWindow(Marker marker) {
        return null;
    }

    // This is used to only set the layout of the contents of the info window
    public View getInfoContents(Marker marker) {
        render(marker, mContents);
        return mContents;
    }

    // Sets the text for the address (title), positive info (snippet) and description (tag)
    private void render(Marker marker, View view) {
        String title = marker.getTitle();
        String snippet = marker.getSnippet();
        String description = (String) marker.getTag();

        TextView titleUi = view.findViewById(R.id.title);
        TextView snippetUi = view.findViewById(R.id.snippet);
        TextView descriptionUi = view.findViewById(R.id.description);

        if (title != null) {
            titleUi.setText(title);
        } else {
            titleUi.setText("");
        }

        if (snippet != null) {
            snippetUi.setText(snippet);
        } else {
            snippetUi.setText("");
        }

        if (description != null) {
            descriptionUi.setText(description);
        } else {
            descriptionUi.setText("");
        }
    }
}
