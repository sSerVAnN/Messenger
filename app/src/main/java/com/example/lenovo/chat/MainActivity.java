package com.example.lenovo.chat;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Matrix;
import android.renderscript.Matrix2f;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView listOfMessages;
    private EditText inputMessage;
    private Button buttonSend;
    private message_adapter messagesAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference messageReference;
    private ChildEventListener childEventListener;


    private String whatChat;
    private String withWhom;
    private String name;





    @Override
    protected void onDestroy() {
        if (childEventListener != null){
            messageReference.removeEventListener(childEventListener);
            childEventListener = null;
            super.onDestroy();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listOfMessages = (ListView) findViewById(R.id.message_list);
        inputMessage = (EditText) findViewById(R.id.message_input);
        buttonSend = (Button) findViewById(R.id.message_send_button);


        Intent myIntent = getIntent();
        withWhom = myIntent.getStringExtra("withWhom");
        whatChat = myIntent.getStringExtra("whatChat");
        name = myIntent.getStringExtra("name");

        setTitle(name);

        firebaseDatabase = FirebaseDatabase.getInstance();
        messageReference= firebaseDatabase.getReference().child("Chats").child(whatChat);

        final MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.click);
        /*buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                EditText input = (EditText)findViewById(R.id.message_input);
                message msg = new message(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                messageReference.push().setValue(msg);
                input.setText("");
            }
        });*/



        buttonSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mp.start();
                    EditText input = (EditText)findViewById(R.id.message_input);
                    message msg = new message(input.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    messageReference.push().setValue(msg);
                    input.setText("");
                }

                return true;
            }
        });





        showChat();

    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            finish();
                        }
                    });
        }
        return true;
    }*/

    private void showChat() {

        if (childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final message msg = dataSnapshot.getValue(message.class);
                    messagesAdapter.add(msg);
                    buildNotification(msg);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        messageReference.addChildEventListener(childEventListener);

        final List<message> messages = new ArrayList<>();

        messagesAdapter = new message_adapter(this, R.layout.message, messages);
        listOfMessages.setAdapter(messagesAdapter);

    }

    public void buildNotification(final message msg) {

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName()
                .equalsIgnoreCase(getApplicationContext().getPackageName())) {
            isActivityFound = true;
        }

        if (isActivityFound) {

        } else {

            DatabaseReference ref = firebaseDatabase.getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("started_chats");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String tmp = (String)dataSnapshot.child(msg.getAuthorUID()).child("id").getValue();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("withWhom", msg.getUser());
                    intent.putExtra("whatChat", tmp);
                    PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "my_id");
                    builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                    builder.setContentTitle("New message from: " + msg.getUser());
                    builder.setContentText(msg.getText());
                    builder.setContentIntent(contentIntent);
                    NotificationManager notificationManager = (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }


}
