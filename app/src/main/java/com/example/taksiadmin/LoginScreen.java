package com.example.taksiadmin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginScreen extends Activity implements View.OnClickListener {
    private Button login_button,bypass,signin_button,deneme,qr_btn;
    private EditText username_text,password_text;

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_ACCESS_COARSE_LOCATION_CODE = 200;
    private static final int MY_ACCESS_FINE_LOCATION_CODE = 300;
    private static final int MY_ACCESS_BACKGROUND_LOCATION_CODE = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_screen);
        checkPermissons();
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        //TUYGAN BYPASSS
        bypass = findViewById(R.id.bypass);
        bypass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this,MapsActivity2.class));
            }
        });

        username_text = findViewById(R.id.username_text);
        password_text = findViewById(R.id.password_text);
    }

    //TODO:RECORD AUDIO PERMISSON CHECK GELECEK!
    private void checkPermissons() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_ACCESS_COARSE_LOCATION_CODE);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_ACCESS_FINE_LOCATION_CODE);
        }
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    private void openUserMainScreen() {
        Intent intent_userscreen = new Intent(this,MapsActivity2.class);
        startActivity(intent_userscreen);
    }
    private void loginUser() {
        final String username = username_text.getText().toString().trim();
        final String password = password_text.getText().toString().trim();
        Log.i("loginuser",Constants.URL_LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message  = jsonObject.getString("error");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    if(message.equals("false")){
                        openUserMainScreen();
                    }else{
                        showAlert();
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
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
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.setNegativeButton("Hayır",null);
        alertDialog.setTitle("Kayıt Halen Sürmekte");
        alertDialog.setMessage("Kayıtı Durdurmak İster Misiniz?");
        alertDialog.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_ACCESS_COARSE_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "location permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "location permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_ACCESS_FINE_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "location - Fine permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "location - Fine permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_ACCESS_BACKGROUND_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "background location permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "background location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onClick(View v) {
        if(v == login_button){;
            Log.i("click","clicked");
            //TODO LOGIN HANDSHAKE WILL BE HERE
            loginUser();
        }else if(v == signin_button) {

        }
    }

}