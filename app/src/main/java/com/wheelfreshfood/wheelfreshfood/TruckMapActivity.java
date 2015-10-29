package com.wheelfreshfood.wheelfreshfood;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TruckMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
//    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_map);

//        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static JSONObject jsonArrayToObject(JSONArray json, int i) {
        try {
            JSONObject obj = json.getJSONObject(i);
            return obj;
        } catch (JSONException e) {
            Log.e("TRUCK REQUEST", "Invalid JSON string: " + json, e);
            return null;
        }
    }

    public static JSONObject getObjectProp(JSONObject json, String name) {
        try {
            JSONObject obj = json.getJSONObject(name);
            return obj;
        } catch (JSONException e) {
            Log.e("Object from object", "Invalid JSON string: " + json, e);
            return null;
        }
    }

    public static JSONArray getObjecPropArray(JSONObject json, String name) {
        try {
            JSONArray arr = json.getJSONArray(name);
            return arr;
        } catch (JSONException e) {
            Log.e("Object from array", "Invalid JSON string: " + json, e);
            return null;
        }
    }

    public static double getMyInt(JSONArray json, int i) {
        try {
            double number = json.getDouble(i);
            return number;
        } catch (JSONException e) {
            Log.e("Int from array", "Invalid JSON string: " + json, e);
            return 0;
        }
    }

    public static String getMyString(JSONObject json, String name) {
        try {
            String value = json.getString(name);
            return value;
        } catch (JSONException e) {
            Log.e("String from object", "Invalid JSON string: " + json, e);
            return null;
        }
    }

    public void addMarkers(JSONArray response) {
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.food_truck_icon);

        for (int i = 0; i < response.length(); i++) {
            JSONObject truckObj = jsonArrayToObject(response, i);
            JSONObject truckGeo = getObjectProp(truckObj, "geo");
            JSONArray coords = getObjecPropArray(truckGeo, "coordinates");
            double lat = getMyInt(coords, 0);
            double lng = getMyInt(coords, 1);
            LatLng truckPin = new LatLng(lng, lat);
            String truckName = getMyString(truckObj, "name");
            System.out.println(truckObj);
            String truckCopy  = getMyString(truckObj, "windowCopy");
            String truckUrl = getMyString(truckObj, "menuUrl");
            mMap.addMarker(
                    new MarkerOptions()
                            .position(truckPin)
                            .title(truckName)
                            .snippet(truckCopy)
                            .icon(icon));
        }
    }

    public void getTrucks() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.wheelfreshfood.com/api/trucks";

        JsonArrayRequest truckRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        addMarkers(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("That didn't work!");
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(truckRequest);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng norfolk = new LatLng(36.8458816,-76.2884479);

        getTrucks();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(norfolk, 13));
    }
}
