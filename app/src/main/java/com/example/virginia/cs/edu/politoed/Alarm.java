package com.example.virginia.cs.edu.politoed;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by Neal on 11/10/2014.
 */
public class Alarm {
    private int id;
    private String name;
    private int dateYear;
    private int dateMonth;
    private int dateDay;
    private int timeHour; // the hour component in 24-hour format (0-23)
    private int timeMinute; // the minute component (0-60)
    private String category;
    private String priority;
    private String notes;
    private Integer twitter;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        if (dateDay != alarm.dateDay) return false;
        if (dateMonth != alarm.dateMonth) return false;
        if (dateYear != alarm.dateYear) return false;
        if (id != alarm.id) return false;
        if (timeHour != alarm.timeHour) return false;
        if (timeMinute != alarm.timeMinute) return false;
        if (!category.equals(alarm.category)) return false;
        if (!name.equals(alarm.name)) return false;
        if (!notes.equals(alarm.notes)) return false;
        if (!priority.equals(alarm.priority)) return false;
        if (!twitter.equals(alarm.twitter)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + dateYear;
        result = 31 * result + dateMonth;
        result = 31 * result + dateDay;
        result = 31 * result + timeHour;
        result = 31 * result + timeMinute;
        result = 31 * result + category.hashCode();
        result = 31 * result + priority.hashCode();
        result = 31 * result + notes.hashCode();
        result = 31 * result + twitter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateYear=" + dateYear +
                ", dateMonth=" + dateMonth +
                ", dateDay=" + dateDay +
                ", timeHour=" + timeHour +
                ", timeMinute=" + timeMinute +
                ", category='" + category + '\'' +
                ", priority='" + priority + '\'' +
                ", notes='" + notes + '\'' +
                ", twitter=" + twitter +
                '}';
    }

    public Alarm(int id_, String name_, int dateYear_, int dateMonth_, int dateDay_,
                 int timeHour_, int timeMinute_, String category_, String priority_,
                  String notes_, int twitter_) {
        id = id_;
        name = name_;
        dateYear = dateYear_;
        dateMonth = dateMonth_;
        dateDay = dateDay_;
        timeHour = timeHour_;
        timeMinute = timeMinute_;
        category = category_;
        priority = priority_;
        notes = notes_;
        twitter = twitter_;
    }

    public String getFormattedTime() {
        String amOrPm = (timeHour < 12 ? "AM" : "PM");
        int newHour12 = timeHour;

        if (timeHour ==0) {
            newHour12 = 12;
        }

        if (timeHour >12) {
            newHour12 = newHour12 -12;
        }
        return String.format("%d:%02d %s", newHour12, timeMinute, amOrPm);
    }

    public String getFormattedDate() {
        Calendar cal = new GregorianCalendar(dateYear, dateMonth, dateDay);
        SimpleDateFormat fmt = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        return fmt.format(cal.getTime());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDateYear() {
        return dateYear;
    }

    public void setDateYear(int dateYear) {
        this.dateYear = dateYear;
    }

    public int getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(int dateMonth) {
        this.dateMonth = dateMonth;
    }

    public int getDateDay() {
        return dateDay;
    }

    public void setDateDay(int dateDay) {
        this.dateDay = dateDay;
    }

    public int getTimeHour() {
        return timeHour;
    }

    public void setTimeHour(int timeHour) {
        this.timeHour = timeHour;
    }

    public int getTimeMinute() {
        return timeMinute;
    }

    public void setTimeMinute(int timeMinute) {
        this.timeMinute = timeMinute;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTwitter(Integer twitter) {
        this.twitter = twitter;
    }

    public Integer getTwitter() {
        return twitter;
    }
}