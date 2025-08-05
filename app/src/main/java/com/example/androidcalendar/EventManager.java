package com.example.androidcalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EventManager {
    private static EventManager instance;
    private List<CalendarEvent> events;

    private EventManager() {
        events = new ArrayList<>();
        initializeSampleEvents();
    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    private void initializeSampleEvents() {
        Calendar calendar = Calendar.getInstance();
        
        // Today's events
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);
        Date event1Start = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date event1End = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date event2Start = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date event2End = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        Date event3Start = calendar.getTime();
        calendar.add(Calendar.HOUR, 2);
        Date event3End = calendar.getTime();

        events.add(new CalendarEvent(
                UUID.randomUUID().toString(),
                "Team Meeting",
                "Weekly team sync meeting",
                event1Start,
                event1End,
                "#FF4A90E2"
        ));

        events.add(new CalendarEvent(
                UUID.randomUUID().toString(),
                "Lunch Break",
                "Lunch with colleagues",
                event2Start,
                event2End,
                "#FF7B68EE"
        ));

        events.add(new CalendarEvent(
                UUID.randomUUID().toString(),
                "Project Review",
                "Review project progress and next steps",
                event3Start,
                event3End,
                "#FFFF6B6B"
        ));
    }

    public List<CalendarEvent> getAllEvents() {
        return new ArrayList<>(events);
    }

    public List<CalendarEvent> getEventsForDate(Date date) {
        List<CalendarEvent> dayEvents = new ArrayList<>();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        
        Calendar eventCalendar = Calendar.getInstance();
        
        for (CalendarEvent event : events) {
            eventCalendar.setTime(event.getStartTime());
            
            if (targetCalendar.get(Calendar.YEAR) == eventCalendar.get(Calendar.YEAR) &&
                targetCalendar.get(Calendar.DAY_OF_YEAR) == eventCalendar.get(Calendar.DAY_OF_YEAR)) {
                dayEvents.add(event);
            }
        }
        
        return dayEvents;
    }

    public List<String> getFormattedEventsForDate(Date date) {
        List<CalendarEvent> dayEvents = getEventsForDate(date);
        List<String> formattedEvents = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        for (CalendarEvent event : dayEvents) {
            String formattedEvent = event.getTitle() + " at " + timeFormat.format(event.getStartTime());
            formattedEvents.add(formattedEvent);
        }
        
        return formattedEvents;
    }

    public void addEvent(CalendarEvent event) {
        if (event.getId() == null || event.getId().isEmpty()) {
            event.setId(UUID.randomUUID().toString());
        }
        events.add(event);
    }

    public void removeEvent(String eventId) {
        events.removeIf(event -> event.getId().equals(eventId));
    }

    public void updateEvent(CalendarEvent updatedEvent) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(updatedEvent.getId())) {
                events.set(i, updatedEvent);
                break;
            }
        }
    }

    public CalendarEvent getEventById(String eventId) {
        for (CalendarEvent event : events) {
            if (event.getId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }
}

