package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView longii, lattii, cityy, addr, countryy;
    private final static int REQUEST_CODE = 100;
    Button btn;

    EditText edt;
    Button sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        longii = findViewById(R.id.longi);
        lattii = findViewById(R.id.latti);
        cityy = findViewById(R.id.city);
        addr = findViewById(R.id.address);
        countryy = findViewById(R.id.country);
        btn = findViewById(R.id.button_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getlastlocation();

            }
        });

        edt=findViewById(R.id.editTextTextPersonName);
        sms=findViewById(R.id.button_sms);
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED)
                    {
                        Sendsms();
                    }
                    else
                    {
                        requestPermissions(new String[]{ Manifest.permission.SEND_SMS} , 1);
                    }
                }


            }
        });
    }
    private void Sendsms()
    {

        String p_no=edt.getText().toString();
        String ending="this is a system generated , generated in case of emergency, KINDLY HELP ME!";
        String mess="Help me I am TRAPPED in this location:"  +"\n" + "longitude: " + longii + "\n" + "latitude: " + lattii + "\n" + "City: " + cityy + "\n" + "Address line: " + addr + "\n" + ending;
        SmsManager smsManager=SmsManager.getDefault();
        smsManager.sendTextMessage(p_no , null , mess.toString()  ,null , null );
        Toast.makeText(this , " Your location has been send" , Toast.LENGTH_SHORT).show();

    }

    private void getlastlocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            lattii.setText("lattitude is: " + addresses.get(0).getLatitude());
                            longii.setText("longitude is: " + addresses.get(0).getLongitude());
                            addr.setText("address is: " + addresses.get(0).getAddressLine(0));
                            cityy.setText(("city is: " + addresses.get(0).getLocality()));
                            countryy.setText("country is:" + addresses.get(0).getCountryName());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        } else {
            getpermission();
        }

    }

    private void getpermission() {

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getlastlocation();
            }

        } else {

            Toast.makeText(MainActivity.this, "Give the required permission access!", Toast.LENGTH_SHORT).show();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}