package domain.appdevelopment.derek.locationmodeller;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import domain.appdevelopment.derek.locationmodeller.db.DBHandler;

public class ClusterService extends Service
{
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

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public int onStartCommand(Intent inte, int flags, int startId)
    {
        Toast.makeText(this,"Location Collection and Clustering Service Commenced.",Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    /**
     * Initialize the collection and clustering service.
     */
    public void onCreate()
    {
        super.onCreate();
        db = MainActivity.db;
        client = LocationServices.getFusedLocationProviderClient(this);
        //Initializing call back behavior for database storage.
        callback = new LocationCallback() {
            public void onLocationResult(LocationResult result) {
                if (result == null)
                    Toast.makeText(ClusterService.this, "Null Location Result", Toast.LENGTH_SHORT).show();
                else {
                    Location currentLoc = result.getLocations().get(0);
                    db.updateStay(currentLoc.getTime(), currentLoc.getLatitude(), currentLoc.getLongitude(),ClusterService.this);
                }
            }
        };
        req = LocationRequest.create();
        req.setInterval(10000);
        req.setFastestInterval(10000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client.requestLocationUpdates(req, callback, null);
    }



}
