package com.example.androidcalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GridLayout calendarGrid;
    private TextView monthYearText;
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton addEventButton;
    private Calendar currentCalendar;
    private List<String> events;
    private EventsAdapter eventsAdapter;
    private EventManager eventManager;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupCalendar();
        setupEvents();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh events when returning from AddEventActivity
        refreshEvents();
    }

    private void initializeViews() {
        calendarGrid = findViewById(R.id.calendarGrid);
        monthYearText = findViewById(R.id.monthYearText);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        addEventButton = findViewById(R.id.addEventButton);
        
        currentCalendar = Calendar.getInstance();
        selectedDate = (Calendar) currentCalendar.clone();
        eventManager = EventManager.getInstance();
    }

    private void setupCalendar() {
        updateMonthYearDisplay();
        generateCalendarDays();
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearText.setText(monthYearFormat.format(currentCalendar.getTime()));
    }

    private void generateCalendarDays() {
        calendarGrid.removeAllViews();
        
        // Add day headers
        String[] dayHeaders = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayHeaders) {
            TextView dayHeader = createDayHeaderView(day);
            calendarGrid.addView(dayHeader);
        }

        // Get first day of month and number of days
        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty cells for days before month starts
        for (int i = 0; i < firstDayOfWeek; i++) {
            TextView emptyDay = createDayView("");
            calendarGrid.addView(emptyDay);
        }

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayView = createDayView(String.valueOf(day));
            
            // Highlight today
            Calendar today = Calendar.getInstance();
            if (today.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == day) {
                dayView.setBackgroundColor(Color.parseColor("#80FF6B6B"));
                dayView.setTextColor(Color.WHITE);
            }
            
            // Highlight selected date
            if (selectedDate.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                selectedDate.get(Calendar.DAY_OF_MONTH) == day) {
                dayView.setBackgroundColor(Color.parseColor("#804A90E2"));
                dayView.setTextColor(Color.WHITE);
            }
            
            // Check if day has events
            Calendar dayCalendar = (Calendar) currentCalendar.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);
            List<CalendarEvent> dayEvents = eventManager.getEventsForDate(dayCalendar.getTime());
            if (!dayEvents.isEmpty()) {
                dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, android.R.drawable.ic_menu_info_details);
            }
            
            // Add click listener for date selection
            final int finalDay = day;
            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedDate.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
                    selectedDate.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
                    selectedDate.set(Calendar.DAY_OF_MONTH, finalDay);
                    generateCalendarDays(); // Refresh to show selection
                    refreshEvents(); // Update events for selected date
                }
            });
            
            calendarGrid.addView(dayView);
        }
    }

    private TextView createDayHeaderView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(12);
        textView.setTextColor(Color.parseColor("#FF666666"));
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(android.view.Gravity.CENTER);
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textView.setLayoutParams(params);
        
        return textView;
    }

    private TextView createDayView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(Color.parseColor("#FF212121"));
        textView.setPadding(8, 16, 8, 16);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setClickable(true);
        textView.setFocusable(true);
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textView.setLayoutParams(params);
        
        return textView;
    }

    private void setupEvents() {
        events = new ArrayList<>();
        eventsAdapter = new EventsAdapter(events);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventsAdapter);
        refreshEvents();
    }

    private void refreshEvents() {
        events.clear();
        List<String> formattedEvents = eventManager.getFormattedEventsForDate(selectedDate.getTime());
        events.addAll(formattedEvents);
        eventsAdapter.notifyDataSetChanged();
    }

    private void setupClickListeners() {
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.todayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCalendar = Calendar.getInstance();
                selectedDate = (Calendar) currentCalendar.clone();
                setupCalendar();
                refreshEvents();
            }
        });
    }
}

