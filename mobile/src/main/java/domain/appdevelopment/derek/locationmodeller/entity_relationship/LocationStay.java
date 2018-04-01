package domain.appdevelopment.derek.locationmodeller.entity_relationship;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static domain.appdevelopment.derek.locationmodeller.entity_relationship.LocConstants.*;

/**
 * This class represents the relationship of a Place to a finite duration of time in which the place is occasioned by the user. This LocationStay
 * Object should only serve as a product from the database in that it ought only be retrieved and then applied.
 */
public class LocationStay extends Entity
{
    /**
     * A calendar used for the conversion of timestamp to dates.
     */
    private static final Calendar CALENDAR = Calendar.getInstance();
    /**
     * A formatter for display of date.
     */
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat();

    static
    {
        FORMATTER.setTimeZone(TimeZone.getTimeZone("EST"));
    }

    /**
     * Construct a LocationStay object retrieved from the database.
     * @param startTime The starting time of this stay.
     * @param endTime The ending time of this stay.
     * @param duration the duration of this stay.
     * @param loc_id The id of the location visited by the user.
     * @param stay_id The auto-generated id of this stay from the database.
     */
    public LocationStay(long startTime, long endTime, long duration, long loc_id, long stay_id)
    {
        setFieldValue(KEY_START_TIME,startTime);
        setFieldValue(KEY_END_TIME,endTime);
        setFieldValue(KEY_DURATION,duration);
        setFieldValue(KEY_PLID,loc_id);
        setFieldValue(KEY_STAY_ID,stay_id);
    }

    public String toString()
    {
        CALENDAR.setTime(new Date((long)getValueByField(KEY_START_TIME)));
        String startTime = FORMATTER.format(CALENDAR.getTime());
        CALENDAR.setTime(new Date((long)getValueByField(KEY_END_TIME)));
        String endTime = FORMATTER.format(CALENDAR.getTime());
        long duration = ((long)getValueByField(KEY_END_TIME)-(long)getValueByField(KEY_START_TIME));
        long hour = duration/3600000;
        duration = duration%3600000;
        long min = duration/60000;
        duration = duration%60000;
        long sec = duration/1000;
        return String.format("%s Stay\nfrom %s\nto %s\nof %s\nat\n",
                getValueByField(KEY_STAY_ID),
                startTime,
                endTime,
                String.format("%s hours %s minutes %s seconds",hour,min,sec));
    }

}
