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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView textView;

    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Button openBtn = (Button) findViewById(R.id.openBtn);
        Button closeBtn = (Button) findViewById(R.id.closeBtn);

        client = new Client("retrieve");
        Thread clientThread = new Thread(client);
        clientThread.start();
        try{
            clientThread.join();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        seekBar();

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPi("open");
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPi("close");
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
//            mindMapIntent.putExtra("id", itemID);
//            mindMapIntent.putExtra("name", selectedItem);
            startActivity(settingsActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendToPi(String command){
        client = new Client(command);
        new Thread(client).start();
    }

    private void seekBar(){
        seekBar = (SeekBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.Text1);

        seekBar.setProgress(client.getCurrentPosition());

        textView.setText("Covered: " + seekBar.getProgress() + " / " + seekBar.getMax());



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressValue;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressValue = progress;
                textView.setText("Covered: " + progress + " / " + seekBar.getMax());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Covered: " + progressValue + " / " + seekBar.getMax());
                switch (progressValue) {
                    case 0:
                        sendToPi("close");
                        break;
                    case 1:
                        sendToPi("quarter");
                        break;
                    case 2:
                        sendToPi("half");
                        break;
                    case 3:
                        sendToPi("3quarter");
                        break;
                    case 4:
                        sendToPi("open");
                        break;
                }
            }
        });
    }
}

