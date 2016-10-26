package ch.ethz.inf.vs.a3.anicolus.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;

import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.message.Message;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView server = (TextView) findViewById(R.id.connectedServer);
        server.setText("Connected to " + MainActivity.serverAddress.toString().substring(1) +
                ":" + MainActivity.serverPort +
                " as " + MainActivity.username + ".");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "called");
            // prepare deregister message and send it as DatagramPacket
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message deregister = new Message(MainActivity.username, MainActivity.uuid, MessageTypes.DEREGISTER);
                    String msg = deregister.getJSONObject().toString();
                    byte[] b = msg.getBytes();
                    DatagramPacket toSend = new DatagramPacket(b, b.length, MainActivity.serverAddress, MainActivity.serverPort);
                    try {
                        MainActivity.socket.send(toSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }
}
