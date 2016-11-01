package ch.ethz.inf.vs.a3.anicolus.chat;

import android.os.Handler;
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
import java.net.SocketException;
import java.util.PriorityQueue;
import java.util.UUID;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.message.MessageTypes;
import ch.ethz.inf.vs.a3.solution.clock.VectorClock;
import ch.ethz.inf.vs.a3.solution.message.Message;

import static android.R.id.checkbox;
import static android.R.id.message;
import static ch.ethz.inf.vs.a3.anicolus.chat.MainActivity.registered;
import static ch.ethz.inf.vs.a3.anicolus.chat.MainActivity.socket;

public class ChatActivity extends AppCompatActivity {

    PriorityQueue<Message> messagequeue = new PriorityQueue<Message>(20, new MessageComparator());
    EditText chatbox;
    Button getlog;
  


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView server = (TextView) findViewById(R.id.connectedServer);
        server.setText("Connected to " + MainActivity.serverAddress.toString().substring(1) +
                ":" + MainActivity.serverPort +
                " as " + MainActivity.username + ".");


        chatbox = (EditText) findViewById(R.id.chatlog);
        getlog = (Button) findViewById(R.id.getlog);
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
                Handler h = getlog.getHandler();
                if (h == null)
                    return;
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        getlog.setEnabled(false);
                    }
                });
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


                // prepare to receive message from the server, read packets and put them into the queue
                try {
                    MainActivity.socket.setSoTimeout(1000);
                    while (true) {
                        byte[] c = new byte[1500];
                        DatagramPacket toRecv = new DatagramPacket(c, c.length, MainActivity.socket.getLocalAddress(), MainActivity.socket.getLocalPort());
                        MainActivity.socket.receive(toRecv);
                        JSONObject o = new JSONObject(new String(toRecv.getData()));
                        Message me = convertJSONToMessage(o);
                        messagequeue.add(me);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                int size = messagequeue.size();
                for (int i = 0; i < size; i++) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                chatbox.setText(chatbox.getText() + "\n" + messagequeue.poll().getJSONObject().get("body").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        getlog.setEnabled(true);
                    }
                });
            }


        };
        t.start();


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
