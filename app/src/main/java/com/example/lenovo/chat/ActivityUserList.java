package com.example.lenovo.chat;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;


public class ActivityUserList extends AppCompatActivity {


    private int SIGN_IN_REQUEST_CODE;

    private ListView listOfUsers;
    private UsersListAdapter usersListAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersChatsStartedReference;
    private DatabaseReference userListReference;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;
    boolean isChatExistBoolean;

    private String chatID;
    private User startChatWith;

    static List<User> users;
    static DBHelper helper;
    private SQLiteDatabase db;


    @Override
    protected void onDestroy() {
        if (childEventListener != null){
            userListReference.removeEventListener(childEventListener);
            if (valueEventListener!=null && usersChatsStartedReference!=null) {
                usersChatsStartedReference.removeEventListener(valueEventListener);
                valueEventListener = null;
            }
            childEventListener = null;
            super.onDestroy();

        }
    }




    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_users_list);
        setTitle("Users list");
        isChatExistBoolean = false;

        listOfUsers = (ListView)findViewById(R.id.listOfUsers);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userListReference = firebaseDatabase.getReference().child("Users");
        registerForContextMenu(listOfUsers);




        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            //showChat();
            showUserList();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                checkUserInDatabase();
                //showChat();
                showUserList();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    @Override
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
                            Toast.makeText(ActivityUserList.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();
                            finish();
                        }
                    });
        }
        if (item.getItemId() == R.id.my_profile){
            Intent intent = new Intent(ActivityUserList.this, ActivityUserProfile.class);
            intent.putExtra("index", 0);
            intent.putExtra("isYourProfile", true);
            startActivity(intent);

        }

        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listOfUsers) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(users.get(info.position).getName());
            getMenuInflater().inflate(R.menu.user_item_menu, menu);
            }
        }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.menu_profile:
                showProfile(info.position, false);
                return true;
            case R.id.menu_call:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showProfile(int index, boolean isYourProfile){
        /*Intent intent = new Intent(ActivityUserList.this, ActivityUserProfile.class);
        intent.putExtra("index", index);
        intent.putExtra("isYourProfile", isYourProfile);
        startActivity(intent);*/

        Intent intent = new Intent(ActivityUserList.this, ScreenSlidePagerActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);


    }


    private void add_in_db(User user)
    {
        ContentValues values = new ContentValues();
        values.put(helper.KEY_NAME, user.getName());
        db.insert(helper.TABLE_NAME, null, values);
        values.clear();
        values.put(helper.KEY_MAIL, user.getEmail());
        values.put(helper.KEY_AVATAR, user.getPhotoURI());
        db.insert(helper.TABLE_INFO, null, values);
    }


    private void showUserList() {

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        db.delete(helper.TABLE_NAME, null, null);
        db.delete(helper.TABLE_INFO, null, null);


        if (childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        usersListAdapter.add(user);
                        add_in_db(user);
                    }
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

        userListReference.addChildEventListener(childEventListener);

        users = new ArrayList<>();

        usersListAdapter = new UsersListAdapter(this, R.layout.user_item, users);
        listOfUsers.setAdapter(usersListAdapter);



        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                isChatExistBoolean = false;
                if (dataSnapshot.child(startChatWith.getUid()).exists()) {
                    isChatExistBoolean = true;
                    chatID = (String)dataSnapshot.child(startChatWith.getUid()).child("id").getValue();
                }
                if (!isChatExistBoolean)
                    pushUsersChatsWith(startChatWith);

                Intent intent = new Intent(ActivityUserList.this, MainActivity.class);
                intent.putExtra("withWhom", startChatWith.getUid());
                intent.putExtra("whatChat", chatID);
                intent.putExtra("name", startChatWith.getName());
                startActivity(intent);
                usersListAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        listOfUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                usersChatsStartedReference = firebaseDatabase.getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("started_chats");
                usersChatsStartedReference.addListenerForSingleValueEvent(valueEventListener);
                User tmp = users.get(index);
                users.add(0, users.get(index));
                users.remove(index+1);
                startChatWith = tmp;
            }
        });



    }


    /*private void isChatExist(final User user){
        isChatExistBoolean = 0;
        setTitle(Integer.toString(isChatExistBoolean));
        DatabaseReference usersChatsStartedReference = firebaseDatabase.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("started_chats");
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            int i = 0;
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Map<String, String> tmpMap;
                tmpMap = (HashMap<String, String>) snapshot.getValue();
                if (tmpMap.get("withWhom").equals(user.getEmail())) {
                    isChatExistBoolean = 1;
                    setTitle(Integer.toString(i));
                }
                i++;
            }
            if (isChatExistBoolean == 0)
                pushUsersChatsWith(user, chatID);

        }

        usersChatsStartedReference.removeEventListener();


    }*/

    private void pushUsersChatsWith(final User user){
            chatID = UUID.randomUUID().toString();
            DatabaseReference tmp = firebaseDatabase.getReference().child("Users");
            HashMap<String, String> newChat = new HashMap<>();
            newChat.put("id", chatID);
            newChat.put("withWhom", user.getEmail());
            tmp.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("started_chats").child(user.getUid()).setValue(newChat);
            newChat.clear();
            newChat.put("id", chatID);
            newChat.put("withWhom", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            tmp.child(user.getUid()).child("started_chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newChat);
    }




    private void checkUserInDatabase(){

        final DatabaseReference newDatabaseRef;
        newDatabaseRef = FirebaseDatabase.getInstance().getReference();

        newDatabaseRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    User newUser = new User(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                        newUser.setPhotoURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                    }
                    newDatabaseRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newUser);
                    ContentValues values = new ContentValues();
                    values.put(helper.KEY_NAME, newUser.getName());
                    db.insert(helper.TABLE_NAME, null, values);
                    values.clear();
                    values.put(helper.KEY_MAIL, newUser.getEmail());
                    values.put(helper.KEY_AVATAR, newUser.getPhotoURI());
                    db.insert(helper.TABLE_INFO, null, values);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
