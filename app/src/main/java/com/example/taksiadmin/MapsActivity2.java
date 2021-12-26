package com.example.taksiadmin;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.taksiadmin.databinding.ActivityMaps2Binding;
import com.google.android.gms.maps.model.Polyline;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, RoutingListener {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    protected LocationManager locationManager;
    public Double latitude,longitude;
    public Long time;
    protected LocationListener locationListener;
    private Button call_user;
    private boolean sending,received_user = false;
    private boolean Clicked_to_marker = false;
    private TextView durum_txtview;
    String Clicked_Taxi = "";
    String userLat="";
    String userLon="";
    String StatusforUser = "0";
    String Time_user_req = "";
    UserData u = new UserData();
    LatLng user_latlng;
    LatLng taksi_latlng;
    String def_valid = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        call_user = findViewById(R.id.call_user_btn);
        call_user.setOnClickListener(this);
        durum_txtview=findViewById(R.id.durum_txtview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(false);
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Clicked_to_marker = true;
                //TODO: YEŞİL YANIP SÖNEN BİŞİ YAP Kİ ANLAYALIM TAKSİYİ İSTİYOZ >> TAKSİ ÇAĞIR BUTONUNU
                //TODO: TAKSİ SİMGESİNİ Bİ TIK BÜYÜTEBİLİRİZ HER TIKLANANA
                //TODO: TAKSİCİDE GÖRÜNEN MARKER SİLİNECEK
                String markerTitle = marker.getTitle();
                Clicked_Taxi = markerTitle;
                popClicled();

                return false;
            }
        });
    }

    private void popClicled() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Yolculuk Başlıyor")
                .setMessage("Belirtilen Bölgeden Yolcuyu Almak İçin Yola Çık")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StatusforUser="2";
                        sendUserRequestResult();
                        getDAfuckingWay();
                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StatusforUser="3";
                        sendUserRequestResult();
                    }
                })
                .show();
    }



    private void getDAfuckingWay() {
        /*Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(taksi_latlng, user_latlng)
                .key("AIzaSyCjp5ib3oBhIYViXfycdpoLNQwAMJ0XDXU")  //also define your api key here.
                .build();
        routing.execute();
         */
    }

    private void sendUserRequestResult() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_Taksi_Request_Result, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    //todo : false olarak kontrol et önce
                    String message = jsonObject.getString("message");
                    userLat= jsonObject.getJSONArray("message").getJSONObject(0).getString("lat");
                    userLon= jsonObject.getJSONArray("message").getJSONObject(0).getString("lon");
                    user_latlng = new LatLng(Double.valueOf(userLat), Double.valueOf(userLon));
                    updateMapWithUserMarker();
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
                params.put("taksi_id","1");
                params.put("time",Time_user_req);
                params.put("status",StatusforUser);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude  = location.getLatitude();
        longitude = location.getLongitude();
        time = (location.getTime());
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f) );
        taksi_latlng = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Taksi")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_yellow))
                .anchor((float) 0.5, (float) 0.5));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

    }
    @Override
    public void onClick(View v) {
        if(v == call_user){;
            Log.i("click","clicked");
            //TODO LOGIN HANDSHAKE WILL BE HERE
            changeDAimagesToGreen();
            if (!sending){
                changeDAimagesToGreen();
                sending= true;
                startTimer();
                startSendingLocation();
            }else{
                changeDAimagesToRed();
                sending = false;
                stopTimer();
                //stopSendingLocation();
            }

        }
    }

    private void changeDAimagesToRed() {
        durum_txtview.setText("Çevrim Dışısın ");
        durum_txtview.setTextColor(getResources().getColor(R.color.RedAlert));
        call_user.setText("ARA");
        call_user.setBackgroundResource(R.drawable.button_ara);

    }

    private void changeDAimagesToGreen() {
        durum_txtview.setText("Çevrim içisin ");
        durum_txtview.setTextColor(getResources().getColor(R.color.GreenGo));
        call_user.setText("DUR");
        call_user.setBackgroundResource(R.drawable.button_dur);
    }

    private void stopTimer() {
        Log.d("latlo22n2",userLat + " " + userLon);
    }

    private void startTimer() {
        new CountDownTimer(10000, 1000){
            public void onTick(long millisUntilFinished){
                if(!received_user){
                    doesSomebodyWantMe();
                }

            }
            public  void onFinish(){

                if(sending){
                    startSendingLocation();
                    startTimer();
                }else{
                    //STOP
                }
            }
        }.start();
    }

    private void doesSomebodyWantMe() {
//URL_DoesSomebodyWantMe
        Log.d("latlon2",userLat + " " + userLon);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_DoesSomebodyWantMe, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    //todo : false olarak kontrol et önce
                    String message = jsonObject.getString("message");
                    Log.d("message_info3",message);
                    if ((message.equals("false"))){
                        def_valid = "0";
                        userLat= jsonObject.getJSONArray("message").getJSONObject(0).getString("lat");
                        userLon= jsonObject.getJSONArray("message").getJSONObject(0).getString("lon");
                        Time_user_req = jsonObject.getJSONArray("message").getJSONObject(0).getString("time");
                        updateMapWithUserMarker();
                        Log.d("message_info",message);
                        received_user = true;
                    }else{
                        def_valid = "1";
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
                params.put("taksi_id","1");
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateMapWithUserMarker() {
        //mMap.clear();
        LatLng first = new LatLng(Double.valueOf(userLat), Double.valueOf(userLon));
        Marker m = mMap.addMarker(new MarkerOptions().position(first).title("Take Me!"));
        Log.d("taksi_idea",userLat + " " + userLon);
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
                params.put("valid",def_valid);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

    }

    @Override
    public void onRoutingCancelled() {

    }
}