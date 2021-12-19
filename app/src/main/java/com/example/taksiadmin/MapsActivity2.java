package com.example.taksiadmin;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.taksiadmin.databinding.ActivityMaps2Binding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,LocationListener,View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    protected LocationManager locationManager;
    public Double latitude,longitude;
    public Long time;
    protected LocationListener locationListener;
    private Button call_user;
    private boolean sending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        call_user = findViewById(R.id.call_user_btn);
        call_user.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
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


        //mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f) );
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        time = (location.getTime());
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f) );
        /*
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Taksi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
         */
    }
    @Override
    public void onClick(View v) {
        if(v == call_user){;
            Log.i("click","clicked");
            //TODO LOGIN HANDSHAKE WILL BE HERE
            if (!sending){
                sending= true;
                startTimer();
                startSendingLocation();
            }else{
                sending = false;
                stopTimer();
                stopSendingLocation();
            }

        }
    }

    private void stopTimer() {
    }

    private void startTimer() {
        new CountDownTimer(10000, 1000){
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                startSendingLocation();
                if(sending){
                    startTimer();
                }else{
                    //STOP
                }
            }
        }.start();
    }

    private void stopSendingLocation() {
    }

    private void startSendingLocation() {
        Log.i("loginuser",Constants.URL_LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_TaksiRegisterData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message  = jsonObject.getString("error");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if(message.equals("false")){

                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error","psam error");
                Log.i("error2",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username","HakanÇolak");
                params.put("lat",latitude.toString());
                params.put("lon",longitude.toString());
                params.put("time",time.toString());
                params.put("valid","1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}