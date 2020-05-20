package com.example.lab8kotlin;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import ru.mirea.lab8.R;

public class SecondActivity extends AppCompatActivity {
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private PlaceItem place1;
    private PlaceItem place2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        place1 = intent.getParcelableExtra("place1");
        place2 = intent.getParcelableExtra("place2");
        createMapView();

        String[] addresses = new String[2];
        addresses[0] = place1.getAddress();
        addresses[1] = place2.getAddress();
        new GetCoordinates().execute(addresses);
    }

    private void createMapView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        try {
            if (null == map) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;
                    }
                });
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    private class GetCoordinates extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            try {
                String[] address = strings;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format(
                        "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
                        address[0], address[1], HttpDataHandler.KEY_API);
                response = http.getHTTPData(url);
                return response;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            JSONObject jsonObject = null;
            String way = "";
            try {
                jsonObject = new JSONObject(s);
                way = jsonObject.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<LatLng> mPoints = PolyUtil.decode(way);
            PolylineOptions line = new PolylineOptions();

            line.width(4f).color(Color.BLACK);
            LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
            for (int i = 0; i < mPoints.size(); i++) {
                if (i == 0) {
                    MarkerOptions startMarkerOptions = new MarkerOptions()
                            .position(mPoints.get(i));
                    map.addMarker(startMarkerOptions).setTitle("Начало пути");
                } else if (i == mPoints.size() - 1) {
                    MarkerOptions endMarkerOptions = new MarkerOptions()
                            .position(mPoints.get(i));
                    map.addMarker(endMarkerOptions).setTitle("Конец пути");
                }
                line.add(mPoints.get(i));
                latLngBuilder.include(mPoints.get(i));
            }
            map.addPolyline(line);
            try {
                int size = getResources().getDisplayMetrics().widthPixels;
                LatLngBounds latLngBounds = latLngBuilder.build();
                CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
                map.moveCamera(track);
            } catch (Exception e) {
                Toast.makeText(SecondActivity.this,
                        "Not found",
                        Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }

    }
}


