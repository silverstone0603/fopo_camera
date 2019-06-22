package com.teamfopo.fopo.nodes;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerMyItems implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public MarkerMyItems(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }
    public MarkerMyItems(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle=title;
        mSnippet = snippet;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

}
