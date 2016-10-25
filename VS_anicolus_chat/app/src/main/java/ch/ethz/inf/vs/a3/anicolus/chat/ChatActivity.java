package ch.ethz.inf.vs.a3.anicolus.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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
}
