package com.example.androidcalendar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private EventManager eventManager;
    private LanguageManager languageManager;
    private Button languageButton;
    private TextView monthYearText;
    private FloatingActionButton addEventFab;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化语言管理器
        languageManager = new LanguageManager(this);
        languageManager.initializeLanguage();
        
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupEventManager();
        setupCalendar();
        setupEventsRecyclerView();
        setupLanguageButton();
        setupAddEventButton();
        
        updateUI();
    }
    
    private void initializeViews() {
        calendarView = findViewById(R.id.calendar_view);
        eventsRecyclerView = findViewById(R.id.rv_events);
        languageButton = findViewById(R.id.btn_language);
        monthYearText = findViewById(R.id.tv_month_year);
        addEventFab = findViewById(R.id.fab_add_event);
    }
    
    private void setupEventManager() {
        eventManager = EventManager.getInstance();
        
        // 添加一些示例事件
        if (eventManager.getAllEvents().isEmpty()) {
            addSampleEvents();
        }
    }
    
    private void setupCalendar() {
        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                updateEventsForDate(selectedDate.getTime());
            });
        }
        
        // 设置当前日期
        updateEventsForDate(new Date());
        updateMonthYearDisplay();
    }
    
    private void setupEventsRecyclerView() {
        List<String> todayEvents = eventManager.getFormattedEventsForDate(new Date());
        eventsAdapter = new EventsAdapter(todayEvents);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventsAdapter);
    }
    
    private void setupLanguageButton() {
        updateLanguageButtonText();
        
        languageButton.setOnClickListener(v -> showLanguageSelectionDialog());
    }
    
    private void setupAddEventButton() {
        addEventFab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
            startActivityForResult(intent, 1);
        });
    }
    
    private void showLanguageSelectionDialog() {
        String[] languageNames = languageManager.getSupportedLanguageNames();
        String[] languageCodes = languageManager.getSupportedLanguages();
        
        int currentIndex = 0;
        String currentLang = languageManager.getCurrentLanguage();
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                currentIndex = i;
                break;
            }
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_language)
               .setSingleChoiceItems(languageNames, currentIndex, (dialog, which) -> {
                   String selectedLanguage = languageCodes[which];
                   languageManager.setLanguage(selectedLanguage);
                   
                   // 重启Activity以应用新语言
                   recreate();
                   dialog.dismiss();
               })
               .setNegativeButton(R.string.cancel, null)
               .show();
    }
    
    private void updateLanguageButtonText() {
        String currentLang = languageManager.getCurrentLanguage();
        String displayName = languageManager.getLanguageDisplayName(currentLang);
        languageButton.setText(displayName);
    }
    
    private void updateMonthYearDisplay() {
        if (monthYearText != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            String monthYear = monthYearFormat.format(calendar.getTime());
            monthYearText.setText(monthYear);
        }
    }
    
    private void updateEventsForDate(Date date) {
        List<String> eventsForDate = eventManager.getFormattedEventsForDate(date);
        if (eventsAdapter != null) {
            eventsAdapter.updateEvents(eventsForDate);
        }
    }
    
    private void updateUI() {
        updateLanguageButtonText();
        updateMonthYearDisplay();
        updateEventsForDate(new Date());
    }
    
    private void addSampleEvents() {
        Calendar today = Calendar.getInstance();
        
        // 今天的事件
        eventManager.addEvent(new CalendarEvent(
            "Team Meeting",
            "Weekly team sync meeting",
            today.getTime(),
            addHours(today.getTime(), 1)
        ));
        
        eventManager.addEvent(new CalendarEvent(
            "Lunch Break",
            "Lunch with colleagues",
            addHours(today.getTime(), 4),
            addHours(today.getTime(), 5)
        ));
        
        eventManager.addEvent(new CalendarEvent(
            "Project Review",
            "Review project progress",
            addHours(today.getTime(), 7),
            addHours(today.getTime(), 8)
        ));
    }
    
    private Date addHours(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 刷新事件列表
            updateEventsForDate(new Date());
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}

