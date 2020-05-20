package com.example.lab8kotlin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.mirea.lab8.R;

public class MainActivity extends AppCompatActivity {
    private DelayAutoCompleteTextView delayAutoCompleteTextView1;
    private DelayAutoCompleteTextView delayAutoCompleteTextView2;
    private Button buttonSearch;
    private SupportMapFragment mapFragment1;
    private SupportMapFragment mapFragment2;
    private GoogleMap map1;
    private GoogleMap map2;
    private PlaceItem place1;
    private PlaceItem place2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        delayAutoCompleteTextView1 = (DelayAutoCompleteTextView) findViewById(R.id.edit_where_to_follow);
        delayAutoCompleteTextView2 = (DelayAutoCompleteTextView) findViewById(R.id.edit_where_to_go);
        buttonSearch = (Button) findViewById(R.id.btn_search);
        createMapView();

        delayAutoCompleteTextView1.setThreshold(3);
        delayAutoCompleteTextView1.onFilterComplete(3);
        delayAutoCompleteTextView1.setAdapter(new PlaceAutoCompleteAdapter(MainActivity.this));
        delayAutoCompleteTextView1.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar1));
        delayAutoCompleteTextView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                place1 = (PlaceItem) adapterView.getItemAtPosition(position);
                delayAutoCompleteTextView1.setText(place1.getAddress());
                if (map1 == null) {
                    finish();
                    return;
                }
                LatLng ll = new LatLng(Double.valueOf(place1.getLat()),
                        Double.valueOf(place1.getLng()));
                init(map1, ll, "старта");
            }
        });
        delayAutoCompleteTextView2.setThreshold(3);
        delayAutoCompleteTextView2.setAdapter(new PlaceAutoCompleteAdapter(MainActivity.this));
        delayAutoCompleteTextView2.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar2));
        delayAutoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                place2 = (PlaceItem) adapterView.getItemAtPosition(position);
                delayAutoCompleteTextView2.setText(place2.getAddress());
                if (map2 == null) {
                    finish();
                    return;
                }
                LatLng ll = new LatLng(Double.valueOf(place2.getLat()),
                        Double.valueOf(place2.getLng()));
                init(map2, ll, "финиша");
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (place1 != null && place2 != null) {
                    Intent i = new Intent(MainActivity.this, SecondActivity.class);
                    i.putExtra("place1", place1);
                    i.putExtra("place2", place2);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this,
                        "Выберите местоположения, между которыми следует построить маршрут",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createMapView(){
        mapFragment1 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment2 = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        try {
            if(null == map1 || map2 == null){
                mapFragment1.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map1 = googleMap;
                    }
                });

                mapFragment2.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map2 = googleMap;
                    }
                });
            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }


    private void init(GoogleMap map, LatLng ll, String text) {
        map.clear();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ll)
                .zoom(5)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(ll)
                .title("Точка " + text));
    }
}
