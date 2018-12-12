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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    static final int TIME_DIALOG_ID = 1111;
    private int hourOpen, hourClose;
    private int minuteOpen, minuteClose;
    private boolean timeEnabled;

    private Client client;

    private Button setOpen, setClose, refresh;
    private TextView openTimeView, closeTimeView;
    private Switch enabledSwitch;

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

        refreshTime();
        setupButtons();
        getTimeFromServer();
        updateTime(hourOpen, minuteOpen, openTimeView);
        updateTime(hourClose, minuteClose, closeTimeView);
    }

    private void setupButtons(){
        setOpen = (Button) findViewById(R.id.setOpenBtn);
        setOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(SettingsActivity.this, timePickerListenerOpen, hourOpen, minuteOpen, false).show();
                updateTime(hourOpen, minuteOpen, openTimeView);
                sendTimes();
            }
        });

        setClose = (Button) findViewById(R.id.setCloseBtn);
        setClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(SettingsActivity.this, timePickerListenerClose, hourClose, minuteClose, false).show();
                updateTime(hourClose, minuteClose, closeTimeView);
                sendTimes();
            }
        });

        enabledSwitch = (Switch) findViewById(R.id.enabledSwitch);
        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                timeEnabled = isChecked;
                if(isChecked){
                    toastMessage("Auto Open Close Enabled");
                } else {
                    toastMessage("Auto Open Close Disabled");
                }
                sendTimes();

            }
        });

        refresh = (Button) findViewById(R.id.refreshSettingsBtn);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeFromServer();
            }
        });
    }

    private void sendTimes(){
        String opentime = Integer.toString(hourOpen) + Integer.toString(minuteOpen);
        String closetime = Integer.toString(hourClose) + Integer.toString(minuteClose);
        Client client = new Client("POST /time", timeEnabled, opentime, closetime);
        new Thread(client).start();
        toastMessage("Time saved successfully");
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
            startActivity(mainActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getTimeFromServer(){
        String[] currentTime = client.getCurrentTime();
        timeEnabled = Boolean.parseBoolean(currentTime[0]);
        enabledSwitch.setChecked(timeEnabled);
        hourOpen = Integer.parseInt(currentTime[1].substring(0,2));
        minuteOpen = Integer.parseInt(currentTime[1].substring(2,4));
        hourClose = Integer.parseInt(currentTime[2].substring(0,2));
        minuteClose = Integer.parseInt(currentTime[2].substring(2,4));

        updateTime(hourOpen, minuteOpen, openTimeView);
        updateTime(hourClose, minuteClose, closeTimeView);
    }

    private void refreshTime(){
        client = new Client("GET /Time");
        Thread clientThread = new Thread(client);
        clientThread.start();
        try{
            clientThread.join();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
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

    /**
     * Simple method to write toast messages passed a string
     * @param message message to display
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
