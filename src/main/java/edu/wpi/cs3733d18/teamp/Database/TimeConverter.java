package edu.wpi.cs3733d18.teamp.Database;

import java.sql.Timestamp;

public class TimeConverter {

    // Attributes
    private Timestamp startTime;
    private Timestamp endTime;

    // Constructors
    TimeConverter(){} // Empty constructor
    TimeConverter(Timestamp startTime, Timestamp endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Using the start and end times, find the difference in minutes (for records)
     * @return The total amount of minutes between the times
     */
    long timeDiffMinutes() {
        System.out.println(startTime);
        System.out.println(endTime);
        long start = startTime.getTime() / (60 * 1000); // Milliseconds / ((Seconds in Minute) * (Ms in Second))
        long end = endTime.getTime() / (60 * 1000);

        return end - start;
    }

    // Getters
    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    // Setters
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
