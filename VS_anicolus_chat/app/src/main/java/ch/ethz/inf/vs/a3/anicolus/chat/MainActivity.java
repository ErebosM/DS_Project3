package ch.ethz.inf.vs.a3.anicolus.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.UUID;

import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.clock.VectorClock;
import ch.ethz.inf.vs.a3.solution.message.Message;

public class MainActivity extends AppCompatActivity {

    public static InetAddress serverAddress = null;
    public static int serverPort = 4446;
    public static String username = "Alessandro";
    public static boolean registered;
    public static DatagramSocket socket;
    public static UUID uuid;

    private EditText uName = null;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            serverAddress = InetAddress.getByName("192.168.56.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.connectionStatus);

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
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registered = false;
                        for (int i = 0; i < 6 && !registered; i++) { // if establishment fails on first attempt, try 5 more times
                            try {
                                // Show status of connection establishment on MainActivity.
                                update (status, "Trying to establish connection..." + i);
                                socket = new DatagramSocket();
                                socket.setSoTimeout(5000);

                                // prepare register message and send it as DatagramPacket
                                uuid = UUID.randomUUID();
                                Message register = new Message(username, uuid, MessageTypes.REGISTER, null, "");
                                String msg = register.getJSONObject().toString();
                                byte[] b = msg.getBytes();
                                DatagramPacket toSend = new DatagramPacket(b, b.length, serverAddress, serverPort);
                                socket.send(toSend);

                                // prepare to receive message from the server
                                byte[] c = new byte[1500];
                                DatagramPacket toRecv = new DatagramPacket(c, c.length, socket.getLocalAddress(), socket.getLocalPort());
                                socket.receive(toRecv);

                                // check that the server responded with an ack
                                JSONObject o = new JSONObject(new String(toRecv.getData()));
                                if (o != null && o.get("header") != null) {
                                    String type = (String) ((JSONObject) o.get("header")).get("type");
                                    if (type != null && type.equals("ack"))
                                        registered = true;
                                }
                                if (registered) {
                                    Log.d("status", "registered");
                                    update(status, "Connection established.");
                                    //Toast.makeText(getBaseContext(), "Successfully connected to the server.", Toast.LENGTH_SHORT).show();
                                    Intent chatActivity = new Intent(MainActivity.this, ChatActivity.class);
                                    startActivity(chatActivity);
                                } else
                                    Log.d("status", "not registered");

                            } catch (SocketTimeoutException e) {
                                Log.d("status", "timeout exception");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!registered) // Connection establishment failed 6 times.
                            update(status, "Connection establishment failed, please try again.");
                    }
                })).start();
            }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (uName != null && !username.equals(""))
            uName.setText(username);
        if (status != null)
            status.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isValidUsername(String str) {
        // TODO: Implement better version if necessary.
        return !str.equals("");
    }

    private void update(final TextView text, final String str) {
        text.getHandler().post(new Runnable() {
            @Override
            public void run() {
                text.setText(str);
            }
        });
    }
}
