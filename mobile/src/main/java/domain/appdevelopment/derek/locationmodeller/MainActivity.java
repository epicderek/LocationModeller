package domain.appdevelopment.derek.locationmodeller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import domain.appdevelopment.derek.locationmodeller.db.DBHandler;
import domain.appdevelopment.derek.locationmodeller.entity_relationship.LocationStay;
import domain.appdevelopment.derek.locationmodeller.entity_relationship.Place;

public class MainActivity extends AppCompatActivity
{
    private static final int GPS_PERMISSION = 0;
    /**
     * The google location service provider.
     */
    private FusedLocationProviderClient client;
    /**
     * The call back for the location service client.
     */
    private LocationCallback callback;
    /**
     * The locaiton request of this activity.
     */
    private LocationRequest req;
    /**
     * The database that stores the location information.
     */
    private DBHandler db;
    /**
     * The text view for display
     */
    private Button loc_dis;
    /**
     * The button to control the display of collected LocationStays.
     */
    private Button stay_dis;
    /**
     * Intent for displaying collected information.
     */
    private Intent display;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHandler(this);
        //Fetch the buttons for display.
        loc_dis = findViewById(R.id.place_display);
        stay_dis = findViewById(R.id.stay_display);
        loc_dis.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View view)
            {
                display = new Intent(MainActivity.this,DisplayTable.class);
                List<Place> places = db.getAllPlaces();
                StringBuilder builder = new StringBuilder();
                for(Place pla: places)
                    builder.append(pla.toString()).append("\n");
                display.putExtra("table",builder.toString());
                startActivity(display);
            }
        });
        stay_dis.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View view)
            {
                display = new Intent(MainActivity.this,DisplayTable.class);
                List<LocationStay> stays = db.getCurrentStays();
                String dis = stays.toString().replaceAll(",","\n");
                display.putExtra("table",dis);
                startActivity(display);
            }
        });
        //Ask for gps permission.
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION);
        else {
            client = LocationServices.getFusedLocationProviderClient(this);
            //Initializing call back behavior for database storage.
            callback = new LocationCallback() {
                public void onLocationResult(LocationResult result) {
                    if (result == null)
                        Toast.makeText(MainActivity.this, "Null Location Result", Toast.LENGTH_SHORT).show();
                    else {
                        Location currentLoc = result.getLocations().get(0);
                        db.updateStay(currentLoc.getTime(), currentLoc.getLatitude(), currentLoc.getLongitude(),MainActivity.this);
                    }
                }
            };
            req = LocationRequest.create();
            req.setInterval(10000);
            req.setFastestInterval(1000);
            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            client.requestLocationUpdates(req, callback, null);
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int id, String[] perms, int[] results) {
        switch (id) {
            case GPS_PERMISSION: {
                client = LocationServices.getFusedLocationProviderClient(this);
                //Initializing call back behavior for database storage.
                callback = new LocationCallback() {
                    public void onLocationResult(LocationResult result) {
                        if (result == null)
                            Toast.makeText(MainActivity.this, "Null Location Result", Toast.LENGTH_SHORT).show();
                        else {
                            Location currentLoc = result.getLocations().get(0);
                            db.updateStay(currentLoc.getTime(), currentLoc.getLatitude(), currentLoc.getLongitude(),MainActivity.this);
                        }
                    }
                };
                req = LocationRequest.create();
                req.setInterval(10000);
                req.setFastestInterval(1000);
                req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                client.requestLocationUpdates(req, callback, null);
            }
        }

    }

   protected void onDestroy()
   {
       super.onDestroy();
       client.removeLocationUpdates(callback);
   }

}