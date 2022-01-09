package com.example.labsocialnetworkv2.domain;

import com.example.labsocialnetworkv2.constants.DateFormatter;

import java.time.LocalDate;

public class Event extends Entity<Integer> {
    private final String eventName;
    private final LocalDate eventDate;

    public Event(Integer id, String eventName, LocalDate eventDate) {
        super(id);
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    public Event(String eventName, LocalDate eventDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String toString() {
        return this.eventName + " " + this.eventDate.format(DateFormatter.STANDARD_DATE_FORMAT);
    }

}
