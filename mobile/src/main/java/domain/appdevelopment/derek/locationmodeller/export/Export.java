package domain.appdevelopment.derek.locationmodeller.export;

import static domain.appdevelopment.derek.locationmodeller.entity_relationship.LocConstants.*;
import domain.appdevelopment.derek.locationmodeller.db.DBHandler;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Export
{
    /**
     * A calendar for converting timestamps and dates.
     */
    private static final Calendar CALENDAR = Calendar.getInstance();
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat();
    /**
     * The single output file to be written.
     */
    private static final File output = new File(String.format("%s%s%s",android.os.Environment.getExternalStorageDirectory().getAbsolutePath(),File.separator,"LocationStayData.csv"));

    private static boolean fileCreated;

    static
    {
        //Initialize the time zone.
        FORMATTER.setTimeZone(TimeZone.getTimeZone("EST"));
    }


    /**
     * Export the location stay information in the standard format of the representation of a LocationStay object
     * in its various fields in the form of a .csv file into the path of default system storage in android.
     * @param db The database to read from.
     * @exception IOException
     */
    public static void exportCSV(DBHandler db) throws IOException
    {
        String defaultDirectory = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        FileWriter wri = new FileWriter(new File(String.format("%s%s%s",defaultDirectory,File.separator,"LocationStayData.csv")),true);
        //Title the columns.
        String liner = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                "Start Time","End Time","Duration",KEY_STREET_ADDRESS,KEY_PLACE_TYPE,KEY_STREET_NUM,KEY_ROUTE,KEY_NEIGHBORHOOD,KEY_LOCALITY,
                KEY_ADMINISTRATIVE2,KEY_ADMINISTRATIVE1,KEY_COUNTRY,KEY_ZIP);
        if(!fileCreated)
        {
            wri.write(liner);
            fileCreated = true;
        }
        SQLiteDatabase read = db.getReadableDatabase();
        Cursor cursor = read.rawQuery(String.format("select * from %s",TABLE_LOC_STAY),null);
        while(cursor.moveToNext())
        {
            CALENDAR.setTime(new Date(cursor.getLong(cursor.getColumnIndex(KEY_START_TIME))));
            String startTime = FORMATTER.format(CALENDAR.getTime());
            CALENDAR.setTime(new Date(cursor.getLong(cursor.getColumnIndex(KEY_END_TIME))));
            String endTime = FORMATTER.format(CALENDAR.getTime());
            long duration = (cursor.getLong(cursor.getColumnIndex(KEY_END_TIME))-cursor.getLong(cursor.getColumnIndex(KEY_START_TIME)));
            long hour = duration/3600000;
            duration = duration%3600000;
            long min = duration/60000;
            duration = duration%60000;
            long sec = duration/1000;
            Cursor cur = read.rawQuery(String.format("select * from %s where %s=%s",TABLE_LOC,KEY_PLID,cursor.getLong(cursor.getColumnIndex(KEY_PLID))),null);
            if(!cur.moveToNext())
                throw new RuntimeException("Storage Error: Place Not Found");
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%s",
                    startTime,endTime,String.format("%s hours %s minutes %s seconds",hour,min,sec),
                    cur.getString(cur.getColumnIndex(KEY_STREET_ADDRESS)),cur.getString(cur.getColumnIndex(KEY_PLACE_TYPE)),
                    cur.getString(cur.getColumnIndex(KEY_STREET_NUM)),cur.getString(cur.getColumnIndex(KEY_ROUTE)),
                    cur.getString(cur.getColumnIndex(KEY_NEIGHBORHOOD)),cur.getString(cur.getColumnIndex(KEY_LOCALITY)),
                    cur.getString(cur.getColumnIndex(KEY_ADMINISTRATIVE2)),cur.getString(cur.getColumnIndex(KEY_ADMINISTRATIVE1)),
                    cur.getString(cur.getColumnIndex(KEY_COUNTRY)),cur.getString(cur.getColumnIndex(KEY_ZIP)),"\n");
            wri.write(line);
        }
        wri.write("\n\n");
        wri.close();
    }
}
