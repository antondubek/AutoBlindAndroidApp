package acm35.com.autoblind;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity class which shows and creates the functionality of the main page
 */
public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private Button openBtn, closeBtn, refresh;

    private Client client;

    /**
     * Overriden oncreate method which sets click listeners for the buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Add the view and the top bar
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Initialise the buttons
        openBtn = (Button) findViewById(R.id.openBtn);
        closeBtn = (Button) findViewById(R.id.closeBtn);
        refresh = (Button) findViewById(R.id.refreshBtn);

        // Get the current position of the blind
        getPosition();
        seekBar();

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPi("POST /open");
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPi("POST /close");
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSeekBar();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent settingsActivityIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates a client and sends a passed command to the RPi socket server
     * @param command command to be sent to the socket server
     */
    public void sendToPi(String command){
        client = new Client(command);
        new Thread(client).start();
    }

    /**
     * Create a client and asks for the current position of the blind
     * @return true
     */
    private boolean getPosition(){
        client = new Client("GET /Position");
        Thread clientThread = new Thread(client);
        clientThread.start();
        try{
            clientThread.join();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    /**
     * Creates a seekbar which has values 0-4 which user can interact with
     */
    private void seekBar(){
        seekBar = (SeekBar) findViewById(R.id.progressBar);

        seekBar.setProgress(client.getCurrentPosition());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (progressValue) {
                    case 0:
                        sendToPi("POST /close");
                        break;
                    case 1:
                        sendToPi("POST /quarter");
                        break;
                    case 2:
                        sendToPi("POST /half");
                        break;
                    case 3:
                        sendToPi("POST /3quarter");
                        break;
                    case 4:
                        sendToPi("POST /open");
                        break;
                }
            }
        });
    }

    /**
     * Simple method to update the seekbar
     */
    private void updateSeekBar(){
        getPosition();
        seekBar.setProgress(client.getCurrentPosition());
    }
}

