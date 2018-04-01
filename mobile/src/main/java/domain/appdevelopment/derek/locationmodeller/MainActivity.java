package domain.appdevelopment.derek.locationmodeller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
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

import java.io.FileNotFoundException;
import java.util.List;

import static domain.appdevelopment.derek.locationmodeller.entity_relationship.LocConstants.*;
import domain.appdevelopment.derek.locationmodeller.db.DBHandler;
import domain.appdevelopment.derek.locationmodeller.entity_relationship.LocationStay;
import domain.appdevelopment.derek.locationmodeller.entity_relationship.Place;
import domain.appdevelopment.derek.locationmodeller.export.Export;

public class MainActivity extends AppCompatActivity
{
    private static final int GPS_PERMISSION = 0;
    /**
     * The database that stores the location information.
     */
    protected static DBHandler db;
    /**
     * The button to export the data collected by now.
     */
    private Button export;
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
        export = findViewById(R.id.export);
        stay_dis = findViewById(R.id.stay_display);
        export.setOnClickListener(new Button.OnClickListener()
        {
            @SuppressLint("StaticFieldLeak")
            public void onClick(View view)
            {
                display = new Intent(MainActivity.this,DisplayTable.class);
                Toast.makeText(MainActivity.this,"Exporting LocationStay Statistics...",Toast.LENGTH_LONG).show();
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try
                        {
                            Export.exportCSV(db);
                        }catch(FileNotFoundException ex)
                        {
                            Log.e("ExportFailure","Literally Impossible");
                        }
                        return null;
                    }

                    protected void onPostExecute(Void voi)
                    {
                        Toast.makeText(MainActivity.this,"LocationStay Export Successful!",Toast.LENGTH_LONG).show();
                    }
                }.execute();
            }
        });
        stay_dis.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View view)
            {
                display = new Intent(MainActivity.this,DisplayTable.class);
                List<LocationStay> stays = db.getCurrentStays();
                StringBuilder builder = new StringBuilder();
                for(LocationStay holder: stays)
                    builder.append(holder.toString()).append(((Place)db.getPlaceById((long)holder.getValueByField(KEY_PLID))).getValueByField(KEY_STREET_ADDRESS)).append("\n");
                display.putExtra("table",builder.toString());
                startActivity(display);
            }
        });
        //Ask for gps permission.
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION);
        else
        {
            //Start the location collection and clustering service.
            Intent inte = new Intent(this,ClusterService.class);
            startService(inte);
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int id, String[] perms, int[] results) {
        switch (id) {
            case GPS_PERMISSION:
            {
                //Initialize the location collection and clustering service.
                Intent intent = new Intent(this,ClusterService.class);
                startService(intent);
            }
        }
    }

    /**
     * Terminate the service.
     */
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(this,ClusterService.class));
    }

}