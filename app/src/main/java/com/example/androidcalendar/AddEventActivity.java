package com.example.androidcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private Button saveButton;
    private Button cancelButton;

    private Calendar startCalendar;
    private Calendar endCalendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initializeViews();
        setupDateTimeFormatters();
        setupClickListeners();
        initializeDefaultDateTime();
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        startDateButton = findViewById(R.id.startDateButton);
        startTimeButton = findViewById(R.id.startTimeButton);
        endDateButton = findViewById(R.id.endDateButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.HOUR, 1); // Default 1 hour duration
    }

    private void setupDateTimeFormatters() {
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    private void initializeDefaultDateTime() {
        updateDateTimeButtons();
    }

    private void updateDateTimeButtons() {
        startDateButton.setText(dateFormat.format(startCalendar.getTime()));
        startTimeButton.setText(timeFormat.format(startCalendar.getTime()));
        endDateButton.setText(dateFormat.format(endCalendar.getTime()));
        endTimeButton.setText(timeFormat.format(endCalendar.getTime()));
    }

    private void setupClickListeners() {
        startDateButton.setOnClickListener(v -> showDatePicker(startCalendar, true));
        startTimeButton.setOnClickListener(v -> showTimePicker(startCalendar, true));
        endDateButton.setOnClickListener(v -> showDatePicker(endCalendar, false));
        endTimeButton.setOnClickListener(v -> showTimePicker(endCalendar, false));

        saveButton.setOnClickListener(v -> saveEvent());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void showDatePicker(Calendar calendar, boolean isStartDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    if (isStartDate && startCalendar.after(endCalendar)) {
                        endCalendar.setTime(startCalendar.getTime());
                        endCalendar.add(Calendar.HOUR, 1);
                    }
                    
                    updateDateTimeButtons();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker(Calendar calendar, boolean isStartTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    
                    if (isStartTime && startCalendar.after(endCalendar)) {
                        endCalendar.setTime(startCalendar.getTime());
                        endCalendar.add(Calendar.HOUR, 1);
                    }
                    
                    updateDateTimeButtons();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void saveEvent() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter event title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startCalendar.after(endCalendar)) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
            return;
        }

        CalendarEvent newEvent = new CalendarEvent(
                null, // ID will be generated by EventManager
                title,
                description,
                startCalendar.getTime(),
                endCalendar.getTime(),
                "#FF4A90E2" // Default color
        );

        EventManager.getInstance().addEvent(newEvent);
        Toast.makeText(this, getString(R.string.save) + "d successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

