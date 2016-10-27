package ch.ethz.inf.vs.a3.anicolus.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.PriorityQueue;
import java.util.UUID;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.clock.VectorClock;
import ch.ethz.inf.vs.a3.solution.message.Message;

import static android.R.id.message;
import static ch.ethz.inf.vs.a3.anicolus.chat.MainActivity.registered;
import static ch.ethz.inf.vs.a3.anicolus.chat.MainActivity.socket;

public class ChatActivity extends AppCompatActivity {

    PriorityQueue<Message> messagequeue = new PriorityQueue<Message>(20, new MessageComparator());
    EditText chatbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView server = (TextView) findViewById(R.id.connectedServer);
        server.setText("Connected to " + MainActivity.serverAddress.toString().substring(1) +
                ":" + MainActivity.serverPort +
                " as " + MainActivity.username + ".");


        chatbox = (EditText) findViewById(R.id.chatlog);
        Button getlog = (Button) findViewById(R.id.getlog);
        getlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatbox.setText("Chat Log:");
                retrieveLog();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "called");
        // prepare deregister message and send it as DatagramPacket
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message deregister = new Message(MainActivity.username, MainActivity.uuid, MessageTypes.DEREGISTER, null, "");
                String msg = deregister.getJSONObject().toString();
                byte[] b = msg.getBytes();
                DatagramPacket toSend = new DatagramPacket(b, b.length, MainActivity.serverAddress, MainActivity.serverPort);
                try {
                    socket.send(toSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected void retrieveLog() {

        Thread t = new Thread() {
            @Override
            public void run() {
                //Send request to retrieve chat log from server
                Message register = new Message(MainActivity.username, MainActivity.uuid, MessageTypes.RETRIEVE_CHAT_LOG, null, "");
                String msg = register.getJSONObject().toString();
                byte[] b = msg.getBytes();
                DatagramPacket toSend = new DatagramPacket(b, b.length, MainActivity.serverAddress, MainActivity.serverPort);
                try {
                    MainActivity.socket.send(toSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // prepare to receive message from the server
                //TODO wait for server to finish sending
                for (int i = 0; i < 7; i++) {
                    byte[] c = new byte[1500];
                    DatagramPacket toRecv = new DatagramPacket(c, c.length, MainActivity.socket.getLocalAddress(), MainActivity.socket.getLocalPort());
                    try {
                        MainActivity.socket.receive(toRecv);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {

                        JSONObject o = new JSONObject(new String(toRecv.getData()));

                        Message me = convertJSONToMessage(o);

                        messagequeue.add(me);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        };
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        int size = messagequeue.size();
        for (int i = 0; i < size; i++) {
            try {
                chatbox.setText(chatbox.getText() + "\n" + messagequeue.poll().getJSONObject().get("body").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    protected Message convertJSONToMessage(JSONObject json) {


        try {
            String username = (String) (((JSONObject) json.get("header")).get("username"));
            String type = (String) (((JSONObject) json.get("header")).get("type"));
            String timestamp = (String) (((JSONObject) json.get("header")).get("timestamp"));
            String body = (String) (((JSONObject) json.get("body")).get("content"));
            // String body = json.get("body").toString();
            VectorClock c = new VectorClock();
            c.setClockFromString(timestamp);

            //TODO pass correct UUID
            return new Message(username, MainActivity.uuid, type, c, body);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
