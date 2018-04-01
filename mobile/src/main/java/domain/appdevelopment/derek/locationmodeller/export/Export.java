package domain.appdevelopment.derek.locationmodeller.export;

import static domain.appdevelopment.derek.locationmodeller.entity_relationship.LocConstants.*;
import domain.appdevelopment.derek.locationmodeller.db.DBHandler;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileNotFoundException;
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

    static
    {
        //Initialize the time zone.
        FORMATTER.setTimeZone(TimeZone.getTimeZone("EST"));
    }


    /**
     * Export the location stay information in the standard format of the representation of a LocationStay object
     * in its various fields in the form of a .csv file into the path of default system storage in android.
     * @param db The database to read from.
     * @exception FileNotFoundException
     */
    public static void exportCSV(DBHandler db) throws FileNotFoundException
    {
        String defaultDirectory = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        PrintWriter wri = new PrintWriter(String.format("%s%s%s",defaultDirectory,File.separator,"LocationStayData.csv"));
        //Title the columns.
        String liner = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                "Start Time","End Time","Duration",KEY_STREET_ADDRESS,KEY_PLACE_TYPE,KEY_STREET_NUM,KEY_ROUTE,KEY_NEIGHBORHOOD,KEY_LOCALITY,
                KEY_ADMINISTRATIVE2,KEY_ADMINISTRATIVE1,KEY_COUNTRY,KEY_ZIP);
        wri.write(liner);
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
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%s",
                    startTime,endTime,String.format("%s hours %s minutes %s seconds",hour,min,sec),
                    cursor.getString(cursor.getColumnIndex(KEY_STREET_ADDRESS)),cursor.getString(cursor.getColumnIndex(KEY_PLACE_TYPE)),
                    cursor.getString(cursor.getColumnIndex(KEY_STREET_NUM)),cursor.getString(cursor.getColumnIndex(KEY_ROUTE)),
                    cursor.getString(cursor.getColumnIndex(KEY_NEIGHBORHOOD)),cursor.getString(cursor.getColumnIndex(KEY_LOCALITY)),
                    cursor.getString(cursor.getColumnIndex(KEY_ADMINISTRATIVE2)),cursor.getString(cursor.getColumnIndex(KEY_ADMINISTRATIVE1)),
                    cursor.getString(cursor.getColumnIndex(KEY_COUNTRY)),cursor.getString(cursor.getColumnIndex(KEY_ZIP)),"\n");
            wri.write(line);
        }
        wri.write("\n\n");
        wri.close();
    }
}
