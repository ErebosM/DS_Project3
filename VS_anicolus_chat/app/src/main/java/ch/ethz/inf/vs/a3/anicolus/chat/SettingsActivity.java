package ch.ethz.inf.vs.a3.anicolus.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Text field for setting the server address.
        final EditText editServerAddress = (EditText) findViewById(R.id.serverAddress);
        if (MainActivity.serverAddress != null) // If available, show previous settings.
            editServerAddress.setText(MainActivity.serverAddress.toString().substring(1));
        editServerAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = true;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        String address = editServerAddress.getText().toString();
                        if (isValidIPv4(address)) {
                            Toast.makeText(getBaseContext(), "Entered a valid IPv4 address.", Toast.LENGTH_SHORT).show();
                            MainActivity.serverAddress = InetAddress.getByName(address);
                            Log.d("Server address", MainActivity.serverAddress.toString());
                            handled = false; // such that keyboard disappears
                        } else
                            Toast.makeText(getBaseContext(), "Please enter a valid IPv4 address.", Toast.LENGTH_SHORT).show();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
                return handled;
            }
        });

        // Text field for setting the server port.
        final EditText editServerPort = (EditText) findViewById(R.id.serverPort);
        if (MainActivity.serverPort >= 0) // If available, show previous settings.
            editServerPort.setText(String.valueOf(MainActivity.serverPort));
        editServerPort.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = true;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String port = editServerPort.getText().toString();
                        if (!port.equals("")) {
                            Toast.makeText(getBaseContext(), "Entered a valid port.", Toast.LENGTH_SHORT).show();
                            MainActivity.serverPort = Integer.parseInt(port);
                            Log.d("Server port", String.valueOf(MainActivity.serverPort));
                            handled = false; // such that keyboard disappears
                        } else
                            Toast.makeText(getBaseContext(), "Please enter a valid Port.", Toast.LENGTH_SHORT).show();
                }
                return handled;
            }
        });
    }

    boolean isValidIPv4(String str) {
        /** regex stolen from http://stackoverflow.com/questions/15689945/how-do-i-check-if-a-text-contains-an-ip-address
         *  (answer from Bujanca Mihai & Bryan Hobbs) */
        if (str.matches("((0|1[0-9]{0,2}|2[0-9]?|2[0-4][0-9]|25[0-5]|[3-9][0-9]?)\\.){3}(0|1[0-9]{0,2}|2[0-9]?|2[0-4][0-9]|25[0-5]|[3-9][0-9]?)"))
            return true;
        else
            return false;
    }

}
