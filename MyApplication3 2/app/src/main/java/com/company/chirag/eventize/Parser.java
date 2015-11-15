package com.company.chirag.eventize;

/**
 * Created by Chirag on 11/14/2015.
 */
import android.content.Intent;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.StringTokenizer;

public class Parser {

    private MyQueue<String> buffer;
    private StringTokenizer st;

    private Calendar from, to;
    private String Location;
    private String Event;
    private Intent result;



    static String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    static String[] months_long = { "January", "Febuary", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December" };
    public String getLocation()
    {
        return Location;
    }
    public String getEvent()
    {
        return Event;
    }
    public Calendar getFrom()
    {
        return from;
    }
    public Calendar getTo()
    {
        return to;
    }

    public Intent filterTest(String ans) {
        String previousToken = "-1";// for month matching
        StringTokenizer main = new StringTokenizer(ans, "\n");
        while (main.hasMoreTokens()){
            String ms = main.nextToken();
            ms = ms.replaceAll("[^a-zA-Z0-9]+", " ");
            st = new StringTokenizer(ms);
            buffer = new MyQueue<String>();

            while (st.hasMoreTokens()) {
                String current = "";
                current = getNext();

                if (isMonth(current) != -1) {
                    String s1 = getNext();
                    if (s1 != null)

                        if (isDate(current, previousToken, s1) == -1) {
                            System.out.println("found a month");

                            buffer.add(s1);
                        } else {
                            if (isDate(current, previousToken, s1) == 1) {
                                initDate(previousToken, isMonth(current));//previoustoken is the date
                                break;
                            } else {
                                initDate(s1, isMonth(current));
                                break;
                            }
                        }
                } else if (current.equalsIgnoreCase("Where") || current.equalsIgnoreCase("Location") || current.equalsIgnoreCase("Locati") || current.equalsIgnoreCase("cation")) {
                    String s1 = "";
                    while(st.hasMoreTokens())
                        s1 += st.nextToken() + ' ';
                    initLocation(s1);
                    break;
                } else if (current.equalsIgnoreCase("Event")) {
                    String s1 = "";
                    while(st.hasMoreTokens())
                        s1 += st.nextToken() + ' ';
                    initEvent(s1);
                    break;
                }
                previousToken = current;

            }

        }


        if (to == null) to = from;

        Intent intent = new Intent(Intent.ACTION_INSERT);

        if (from != null)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, from.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, to.getTimeInMillis());
        if (Event != null)
            intent.putExtra(CalendarContract.Events.TITLE, Event);
        if (Location != null)
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, Location);
        intent.setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        return intent;
    }




    public void initLocation(String loc){
        Location = loc;

    }

    public void initEvent(String ev){
        Event = ev;

    }

    public void initDate(String day, int month){
        Calendar beginTime = Calendar.getInstance();
        int thisYear = beginTime.get(Calendar.YEAR);
        beginTime.set(thisYear, month, Integer.parseInt(day), 7, 30);
        if (from == null) from = beginTime;
        else{
            if (beginTime.compareTo(from) < 0){
                to = from;
                from = beginTime;
            } else
                to = beginTime;
        }

    }

    public String getNext() {
        if (buffer.isEmpty()){
            if (st.hasMoreTokens())
                return st.nextToken();
            else return null;
        }else
            return buffer.get();
    }

    public int isMonth(String name) {
        for (int i = 0; i < months.length; i++) {
            if (name.equalsIgnoreCase(months[i]) || name.equalsIgnoreCase(months_long[i])) {
                return i ;
            }
        }
        return -1;
    }

    public int isDate(String current, String val1, String val2) {
        System.out.println(current + " " + val1 + " " + val2);
        int yd;
        int yd2;

        int day = -1;


        try {
            yd = (Integer) Integer.parseInt(val1);
        } catch (Exception e) {
            yd = -1;
        }

        try {
            yd2 = (Integer) Integer.parseInt(val2);
        } catch (Exception e) {
            yd2 = -1;
        }

        if (yd <= 31 && yd >= 0){
            return 1;
        }

        if (yd2 <= 31 && yd2 >= 0){
            return 2;
        }

        return -1;


    }
}
