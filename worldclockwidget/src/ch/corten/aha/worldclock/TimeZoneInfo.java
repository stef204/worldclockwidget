package ch.corten.aha.worldclock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class TimeZoneInfo {
    public static final String[] AREAS = {
        "Africa",
        "America",
        "Antarctica",
        "Arctic",
        "Asia",
        "Atlantic",
        "Australia",
        "Europe",
        "Indian",
        "Pacific"
    };
    
    public static Map<String, String[]> CITY_ALIAS = new HashMap<String, String[]>();
    static {
        CITY_ALIAS.put("Zurich", new String[] { "Bern" });
        CITY_ALIAS.put("Toronto", new String[] { "Ottawa" });
    }
    
    private String city;
    private String area;
    private String id;
    
    private TimeZoneInfo(String city, String area, String id) {
        this.city = city;
        this.area = area;
        this.id = id;
    }

    public static TimeZoneInfo[] getAllTimeZones() {
        final String[] iDs = TimeZone.getAvailableIDs();
        final ArrayList<TimeZoneInfo> timeZones = new ArrayList<TimeZoneInfo>();
        
        for (String id : iDs) {
            final TimeZoneInfo tz = getTimeZone(id);
            if (tz != null) {
                timeZones.add(tz);
                final String[] alias = CITY_ALIAS.get(tz.getCity());
                if (alias != null) {
                    for (String cityAlias : alias) {
                        final TimeZoneInfo tzAlias = new TimeZoneInfo(cityAlias, tz.getArea(), tz.getId());
                        timeZones.add(tzAlias);
                    }
                }
            }
        }
        
        return timeZones.toArray(new TimeZoneInfo[0]);
    }
    
    public static TimeZoneInfo getCity(String city) {
        TimeZoneInfo[] timeZones = getAllTimeZones();
        for (TimeZoneInfo timeZone : timeZones) {
            if (timeZone.getCity().equalsIgnoreCase(city)) {
                return timeZone;
            }
        }
        return null;
    }
    
    public static TimeZoneInfo getTimeZone(String id) {
        if (!isValidArea(id)) return null;
        
        String[] parts = id.split("/");
        if (parts.length < 2) {
            return null;
        }
        String city = parts[parts.length - 1].replace('_', ' ');
        StringBuilder area = new StringBuilder();
        for (int i = parts.length - 2; i >= 0; i--) {
            if (area.length() > 0) {
                area.append(", ");
            }
            area.append(parts[i].replace('_', ' '));
        }
        return new TimeZoneInfo(city, area.toString(), id);
    }

    private static boolean isValidArea(String id) {
        for (String area : AREAS) {
            if (id.startsWith(area)) {
                return true;
            }
        }
        return false;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getId() {
        return id;
    }
    
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(id);
    }
    
    public int getTimeDifference() {
        return getTimeDifference(getTimeZone());
    }
    
    public static int getTimeDifference(TimeZone tz) {
        int milliseconds = tz.getOffset(System.currentTimeMillis());
        return milliseconds / 60000;
    }
    
    public static String getTimeDifferenceString(TimeZone tz) {
        int minutesDiff = getTimeDifference(tz);
        StringBuilder sb = new StringBuilder();
        sb.append("GMT");
        if (minutesDiff != 0) {
            if (minutesDiff < 0) {
                sb.append("-");
            } else {
                sb.append("+");
            }
            minutesDiff = Math.abs(minutesDiff);
            sb.append(minutesDiff / 60);
            sb.append(":");
            
            int minutes = minutesDiff % 60;
            if (minutes < 10) {
                sb.append("0");
            }
            sb.append(minutes);
        }
        return sb.toString();
    }
    
    public static String getDescription(TimeZone tz) {
        if (tz.useDaylightTime() && tz.inDaylightTime(new Date())) {
            return tz.getDisplayName(true, TimeZone.LONG);
        }
        return tz.getDisplayName();
    }
    
    @Override
    public String toString() {
        return "TimeZoneInfo [city=" + city + ", area=" + area + ", id=" + id
                + "]";
    }
}
