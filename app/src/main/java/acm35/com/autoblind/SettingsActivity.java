package acm35.com.autoblind;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    static final int TIME_DIALOG_ID = 1111;
    private int hourOpen, hourClose;
    private int minuteOpen, minuteClose;

    private Button setOpen, setClose;
    private TextView openTimeView, closeTimeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        final Calendar calendar = Calendar.getInstance();
        hourOpen = calendar.get(Calendar.HOUR_OF_DAY);
        minuteOpen = calendar.get(Calendar.MINUTE);
        hourClose = calendar.get(Calendar.HOUR_OF_DAY);
        minuteClose = calendar.get(Calendar.MINUTE);

        openTimeView = (TextView) findViewById(R.id.openTimeView);
        closeTimeView = (TextView) findViewById(R.id.closeTimeView);

        setOpen = (Button) findViewById(R.id.setOpenBtn);
        setOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(SettingsActivity.this, timePickerListenerOpen, hourOpen, minuteOpen, false).show();
                updateTime(hourOpen, minuteOpen, openTimeView);
            }
        });

        setClose = (Button) findViewById(R.id.setCloseBtn);
        setClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog closeTimeDialog = new TimePickerDialog(SettingsActivity.this, timePickerListenerClose, hourOpen, minuteOpen, false);
                closeTimeDialog.show();
                updateTime(hourClose, minuteClose, closeTimeView);
            }
        });

    }

    /**
     * Inflates the menu_main bar so the add button is there
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Creates the add button listener for the main action bar
     * loads the dialog so that a new idea can be added
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent mainActivityIntent = new Intent(SettingsActivity.this, MainActivity.class);
//            mindMapIntent.putExtra("id", itemID);
//            mindMapIntent.putExtra("name", selectedItem);
            startActivity(mainActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private TimePickerDialog.OnTimeSetListener timePickerListenerOpen = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hourOpen = hourOfDay;
            minuteOpen = minutes;
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListenerClose = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hourClose = hourOfDay;
            minuteClose = minutes;
        }
    };

    private static String utilTime(int value) {
        if (value < 10) {
            return "0" + String.valueOf(value);
        } else {
            return String.valueOf(value);
        }
    }


    private void updateTime(int hours, int mins, TextView view) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String minutes = "";
        if (mins < 10) {
            minutes = "0" + mins;
        } else {
            minutes = String.valueOf(mins);
        }
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
        view.setText(aTime);
    }
}
