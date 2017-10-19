package cn.ralken.android.calendar.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.ralken.android.calendar.CalendarView;
import cn.ralken.android.calendar.MonthFlipperView;

/**
 *
 * @author liaoralken
 */
public class MainActivity extends AppCompatActivity {

    protected CalendarView mCalendarView;
    //private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mCalendarView = (CalendarView) findViewById(R.id.calendar_view);

        final Calendar selectCalendar = Calendar.getInstance();
        mCalendarView.setSelectedDate(selectCalendar, this.mCalendarEvents);
    }

    private MonthFlipperView.OnCalendarListener mCalendarEvents = new MonthFlipperView.OnCalendarListener() {
        @Override
        public void onDateSelected(Date date, boolean hasData) {

        }

        @Override
        public List<Date> onMonthChanged(List<Date> newMonthDates) {
            // provide some Date(s) which show dot at the cell bottom as selection.
            final List<Date> selectedItems = new ArrayList<>();

            final Calendar selectCalendar = Calendar.getInstance();

            selectCalendar.add(Calendar.DAY_OF_MONTH, 1);
            selectedItems.add(new Date(selectCalendar.getTimeInMillis()));

            selectCalendar.add(Calendar.DAY_OF_MONTH, 3);
            selectedItems.add(new Date(selectCalendar.getTimeInMillis()));

            selectCalendar.add(Calendar.DAY_OF_MONTH, 6);
            selectedItems.add(new Date(selectCalendar.getTimeInMillis()));

            selectCalendar.add(Calendar.DAY_OF_MONTH, 9);
            selectedItems.add(new Date(selectCalendar.getTimeInMillis()));
            return selectedItems;
        }
    };

}
