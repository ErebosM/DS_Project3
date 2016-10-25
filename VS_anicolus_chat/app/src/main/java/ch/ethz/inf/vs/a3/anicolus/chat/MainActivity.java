package ch.ethz.inf.vs.a3.anicolus.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    public static InetAddress serverAddress = null;
    public static int serverPort = -1;
    public static String username = "";

    private EditText uName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Open new activity in order to
        Button settings = (Button) findViewById(R.id.settingsButton);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });

        // Text field for entering username.
        uName = (EditText) findViewById(R.id.username);
        uName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = true;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    username = uName.getText().toString();
                    if (isValidUsername(username)) { // TODO: Error handling - what usernames are valid? Implement isValidUsername.
                        Toast.makeText(getBaseContext(), "Entered a valid username.", Toast.LENGTH_SHORT).show();
                        Log.d("username", username);
                        handled = false; // such that keyboard disappears
                    } else
                        Toast.makeText(getBaseContext(), "Please enter a valid username.", Toast.LENGTH_SHORT).show();
                }
                return handled;
            }
        });

        // Try to establish the desired connection and open a new activity
        Button join = (Button) findViewById(R.id.joinButton);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverAddress == null && serverPort < 0)
                    Toast.makeText(getBaseContext(), "Please enter server address and port.", Toast.LENGTH_SHORT).show();
                else if (serverAddress == null)
                    Toast.makeText(getBaseContext(), "Please enter a server address.", Toast.LENGTH_SHORT).show();
                else if (serverPort < 0)
                    Toast.makeText(getBaseContext(), "Please enter a server port.", Toast.LENGTH_SHORT).show();
                else if (username.equals(""))
                    Toast.makeText(getBaseContext(), "Please enter a username.", Toast.LENGTH_SHORT).show();
                else {
                    Intent chatActivity = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(chatActivity);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uName != null && !username.equals(""))
            uName.setText(username);
    }

    private boolean isValidUsername(String str) {
        // TODO: Implement better version if necessary.
        return !str.equals("");
    }
}
