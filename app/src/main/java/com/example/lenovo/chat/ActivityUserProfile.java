package com.example.lenovo.chat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;


public class ActivityUserProfile extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Button updateProfileButton;
    String newImageURL="";
    RelativeLayout relativeLayout;

    private ImageView avatar;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    private float scaleFactor = 1f;
    private boolean onTouch = false;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        //avatar = (ImageView)findViewById(R.id.avatar);
        Intent myIntent = getIntent();
        int startPosition = myIntent.getIntExtra("index", 0);
        boolean isYourProfile = myIntent.getBooleanExtra("isYourProfile", false);

        if (!isYourProfile) {
            setContentView(R.layout.view_pager);
            setTitle("Users profiles");
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            viewPagerAdapter = new ViewPagerAdapter(ActivityUserList.users, ActivityUserProfile.this);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(startPosition);
        }
        else{
            setContentView(R.layout.user_profile);
            setTitle("Your profile");
            /*DBHelper helper = new DBHelper(this);
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete(helper.TABLE_NAME, null, null);
            db.delete(helper.TABLE_INFO, null, null);
            ContentValues values = new ContentValues();
            for (int i = 0; i < ActivityUserList.users.size(); i++)
            {
                values.clear();
                values.put(helper.KEY_NAME, ActivityUserList.users.get(i).getName());
                db.insert(helper.TABLE_NAME, null, values);
            }
            for (int i = 0; i < ActivityUserList.users.size(); i++)
            {
                values.clear();
                values.put(helper.KEY_MAIL, ActivityUserList.users.get(i).getEmail());
                values.put(helper.KEY_AVATAR, ActivityUserList.users.get(i).getPhotoURI());
                db.insert(helper.TABLE_INFO, null, values);
            }*/




            /*Cursor cursor = db.query(helper.TABLE_INFO, null,null,null,null,null,null);
            if (cursor.moveToFirst()){
                int index = cursor.getColumnIndex(helper.KEY_MAIL);
                int ind = cursor.getColumnIndex(helper.COLUMN_ID_2nd);

                do{
                    Log.d("MyLog", "name " + cursor.getString(index) + "index:" +cursor.getInt(ind));
                }while(cursor.moveToNext());
            }
            cursor.close();*/



            SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();
            _QB.setTables(DBHelper.TABLE_NAME +
                    " INNER JOIN " + DBHelper.TABLE_INFO + " ON " +
                    DBHelper.COLUMN_ID + " = " + DBHelper.COLUMN_ID_2nd);
            SQLiteDatabase _DB = ActivityUserList.helper.getReadableDatabase();
            Cursor cursor2 = _QB.query(_DB, null, null, null, null, null, null);
            if(cursor2.moveToFirst()) {
                int name = cursor2.getColumnIndex(DBHelper.KEY_NAME);
                int mail = cursor2.getColumnIndex(DBHelper.KEY_MAIL);
                int id = cursor2.getColumnIndex(DBHelper.COLUMN_ID);
                do {
                    Log.i("innerjoin", "name" + cursor2.getString(name) + "mail" + cursor2.getString(mail) + "id" + cursor2.getInt(id));
                }while (cursor2.moveToNext());
            }






            updateProfileButton = (Button)findViewById(R.id.update_profile);
            relativeLayout = (RelativeLayout)findViewById(R.id.main_profile_layout);
            updateProfileButton.setVisibility(View.VISIBLE);
            updateProfileButton.setEnabled(true);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            List<User> listOfUsers = ActivityUserList.users;
            avatar = (ImageView)findViewById(R.id.avatar);
            scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener(avatar));
            final EditText nameTextView = (EditText)findViewById(R.id.edit_text_name);
            EditText emailTextVIew = (EditText)findViewById(R.id.edit_text_email);
            final EditText phoneNumberTextView = (EditText)findViewById(R.id.edit_text_phone);
            TextView avatar_text = (TextView)findViewById(R.id.avatar_text);

            nameTextView.setText(user.getDisplayName());
            emailTextVIew.setText(user.getEmail());
            phoneNumberTextView.setText(user.getPhoneNumber());

            if (user.getPhotoUrl()!=null){
                Picasso.with(this).load(user.getPhotoUrl()).into(avatar);
            }
            emailTextVIew.setKeyListener(null);












            final MediaPlayer mp = MediaPlayer.create(this, R.raw.click);
            updateProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mp.start();
                    String newName = nameTextView.getText().toString();
                    String newPhone = phoneNumberTextView.getText().toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                    HashMap<String, Object> newData = new HashMap<>();
                    newData.put("name", newName);
                    newData.put("phoneNumber", newPhone);
                    ref.updateChildren(newData);

                    UserProfileChangeRequest userUpdateProfile = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName).build();
                    user.updateProfile(userUpdateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d("UPDATE", "User profile updated.");
                        }
                    });

                }
            });



            final RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            final int height = avatar.getLayoutParams().height;
            final int tmp = height;
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (avatar.getLayoutParams().height == height) {
                        avatar.setLayoutParams(new RelativeLayout.LayoutParams(par));
                        onTouch = true;
                    }
                    else {
                        avatar.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 788));
                        onTouch = false;
                    }
                }
            });




            avatar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!onTouch) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUserProfile.this);
                        builder.setTitle("Update image");
                        final EditText input = new EditText(ActivityUserProfile.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            boolean onError = false;

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                newImageURL = input.getText().toString();
                                Picasso.with(ActivityUserProfile.this).load(newImageURL).into(avatar, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        updatePhoto(user);
                                    }

                                    @Override
                                    public void onError() {
                                        avatar.setImageResource(R.drawable.default_avatar);
                                        Toast.makeText(ActivityUserProfile.this,
                                                "This url not exist.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });

                            }
                        });


                        builder.show();


                    }

                    return true;
                }
            });

        }

    }


    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        Log.d("AAAAAAAA", "AAAAAAAAAAAAAAAA");
        return false;
    }*/


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        Log.d("AAAAAAAA", "AAAAAAAAAAAAAAAA");
        return false;
    }

    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {

        private ImageView iv;
        float factor;

        private ScaleListener(ImageView iv) {
            super();
            this.iv = iv;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //onTouch = true;
            factor = 1.0f;
            return true;
            //return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //onTouch = false;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor() - 1;
            factor += scaleFactor;
            if(factor > 1.5f)
                factor = 1.5f;
            iv.setScaleX(factor);
            iv.setScaleY(factor);
            return true;
        }
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        Log.d("AAAAAAAA", "AAAAAAAAAAAAAAAA");
        return true;
    }*/

    private void updatePhoto(FirebaseUser user)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("photoURI", newImageURL);
        ref.updateChildren(newData);
        UserProfileChangeRequest userUpdateProfile = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(newImageURL)).build();
        user.updateProfile(userUpdateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d("UPDATE", "User profile updated.");

            }
        });
    }

}
