package com.final_sem_project.tomhardy.sensorservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private String methodName = "";
    final private String nameSpace = "http://android.webservice.com/";
    //    final private String url = "http://192.168.1.2:8081/android/AndroidWebService?wsdl";
    final private static String url = "http://web-service-android-sensor-web-service.1d35.starter-us-east-1.openshiftapps.com/AndroidWebService?wsdl";
    //    final static String url = "http://10.0.2.2:8080/android/AndroidWebService?wsdl";
    private String soapAction = "";

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
//            Log.d("location", "is changed");
            new UpdateUsersLocationOnly().execute();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private SensorManager sensorManager = null;
    private Sensor proximitySensor = null, lightSensor = null;

    final private SensorEventListener sensorEventListenerProximity = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                proximity = event.values[0];
//                Log.d("sensor", "proximity " + proximity);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    final private SensorEventListener sensorEventListenerLight = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

                light = event.values[0];
//                Log.d("sensor", "light " + light);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private float proximity = 0, light = 0;

    private LocationManager locationManager = null;
    private GoogleApiClient googleApiClient;
    private final int REQUEST_LOCATION = 199;

    private String user_name = null;
    private String password = null;
    private String token = null;
    private double longitude = -1;
    private double latitude = -1;
    final private String statusOnline = "Online";
    final private String statusOffline = "Offline";

    private FirebaseAuth firebaseAuth;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Notification", " is received");
            new UsersDatabaseOperation("updateUsers", statusOnline, proximity, light, true).execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Log.d("ProfileActivity", "onCreate");

        locationManager = (LocationManager) ProfileActivity.this.getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(ProfileActivity.this)) {
            Toast.makeText(ProfileActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
//            finish();
        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(ProfileActivity.this)) {
            Toast.makeText(ProfileActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(ProfileActivity.this)) {
            Log.e("keshav", "Gps not enabled");
            Toast.makeText(ProfileActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth == null)
            Log.d("firebaseAuth", "null");
        else
            Log.d("firebaseAuth", firebaseAuth + "");

        ((TextView) findViewById(R.id.textView)).setText("Welcome " + firebaseAuth.getCurrentUser().getEmail());

        String type = getIntent().getStringExtra("type");
        token = getIntent().getStringExtra("token");
        Log.d("token ProfileActivity", token);
        if (type.equals("signup")) {
            Log.d("type", type);
            user_name = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");

            new UsersDatabaseOperation("signUp", statusOnline, -1, -1, false).execute();

        } else if (type.equals("signin") || type.equals("login")) {
            Log.d("type", type);
            user_name = firebaseAuth.getCurrentUser().getEmail();

            new UsersDatabaseOperation("updateUsers", statusOnline, 0, 0, false).execute();

        }

        Log.d("user_name ", user_name);
    }

    @Override
    protected void onPause() {
        new UsersDatabaseOperation("updateUsers", statusOffline, -1, -1, false).execute();
        super.onPause();
        Log.d("ProfileActivity", "onPause");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        locationManager.removeUpdates(locationListener);

        sensorManager.unregisterListener(sensorEventListenerProximity);
        sensorManager.unregisterListener(sensorEventListenerLight);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ProfileActivity", "onResume");

        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("Notification"));

        new UsersDatabaseOperation("updateUsers", statusOnline, -1, -1, false).execute();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        sensorManager.registerListener(sensorEventListenerProximity, proximitySensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorEventListenerLight, lightSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        new UsersDatabaseOperation("updateUsers", statusOffline, -1, -1, false).execute();
        super.onDestroy();

        Log.d("ProfileActivity", "onDestroy");
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(ProfileActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(ProfileActivity.this, REQUEST_LOCATION);

//                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    public void logOut(View view) {
        firebaseAuth.signOut();

        Log.d("logOut", "ProfileActivity " + user_name);
        new UsersDatabaseOperation("updateUsers", statusOffline, 0, 0, false).execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    class UsersDatabaseOperation extends AsyncTask<String, Void, String> {

        private String mode;
        private String status;
        private float proximity, light;
        private boolean notificationReceived;


        public UsersDatabaseOperation(String mode, String status, float proximity, float light, boolean notificationReceived) {
            this.mode = mode;
            this.status = status;

            this.proximity = proximity;
            this.light = light;
            this.notificationReceived = notificationReceived;
//            Log.d("proximity_light",proximity+" , "+light);
        }

        @Override
        protected String doInBackground(String... strings) {

            Object result = "";

            methodName = mode;
            soapAction = nameSpace + methodName;

            SoapObject request = new SoapObject(nameSpace, methodName);


            if (methodName.equals("signUp")) {
                request.addProperty("password", password);
            }

            request.addProperty("user_name", user_name);
            request.addProperty("token", token);
            request.addProperty("status", status);

            if (methodName.equals("updateUsers")) {
                request.addProperty("proximity", Float.toString(proximity));
                request.addProperty("light", Float.toString(light));
                Log.d("proximity_light", proximity + " , " + light);
            }

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);


            try {
                androidHttpTransport.call(soapAction, envelope);
                result = envelope.getResponse();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Status", " : " + s + " ,status " + status + " ,method " + methodName + ", " + proximity + " , " + light);
            if (notificationReceived) {
//                Log.d("Status", " : " + s + " ,user_name " + user_name + " ,method " + methodName + ", " + proximity + " , " + light);
                Log.d("notificationReceived ", " " + notificationReceived);
                ((TextView) findViewById(R.id.textView2)).setText("status : " + s + " , method: " + methodName + " , proximity: " + proximity + " , light: " + light);
                notificationReceived = false;
            }
        }
    }

    class UpdateUsersLocationOnly extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Object result = "";

            methodName = "updateUsersLocation";
            soapAction = nameSpace + methodName;

            SoapObject request = new SoapObject(nameSpace, methodName);

            request.addProperty("user_name", user_name);
            request.addProperty("longitude", Double.toString(longitude));
            request.addProperty("latitude", Double.toString(latitude));

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);

            try {
                androidHttpTransport.call(soapAction, envelope);
                result = envelope.getResponse();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return result.toString();
        }
    }
}