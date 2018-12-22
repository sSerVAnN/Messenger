package com.example.lenovo.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ScreenSlidePageFragment extends Fragment {


    private int index;
    List<User> listOfUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(
                R.layout.user_profile, container, false);
        listOfUsers = ActivityUserList.users;
        Bundle bundle = getArguments();
        index = bundle.getInt("index");
        final ImageView avatar = (ImageView)view.findViewById(R.id.avatar);
        EditText nameTextView = (EditText)view.findViewById(R.id.edit_text_name);
        EditText emailTextVIew = (EditText)view.findViewById(R.id.edit_text_email);
        EditText phoneNumberTextView = (EditText)view.findViewById(R.id.edit_text_phone);
        final RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final int height = avatar.getLayoutParams().height;
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (avatar.getLayoutParams().height == height)
                    avatar.setLayoutParams(new RelativeLayout.LayoutParams(par));
                else
                    avatar.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            }
        });


        SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();
        _QB.setTables(DBHelper.TABLE_NAME +
                " INNER JOIN " + DBHelper.TABLE_INFO + " ON " +
                DBHelper.COLUMN_ID + " = " + DBHelper.COLUMN_ID_2nd);
        SQLiteDatabase _DB = ActivityUserList.helper.getReadableDatabase();
        Cursor cursor2 = _QB.query(_DB, null, null, null, null, null, null);
            cursor2.move(index+1);
            int name = cursor2.getColumnIndex(DBHelper.KEY_NAME);
            int mail = cursor2.getColumnIndex(DBHelper.KEY_MAIL);
            nameTextView.setText(cursor2.getString(name));
            emailTextVIew.setText(cursor2.getString(mail));
            phoneNumberTextView.setText(listOfUsers.get(index).getPhoneNumber());
            //do {
                //Log.i("innerjoin", "name" + cursor2.getString(name) + "mail" + cursor2.getString(mail) + "id" + cursor2.getInt(id));
            //}while (cursor2.moveToNext());






       // nameTextView.setText(listOfUsers.get(index).getName());
        //emailTextVIew.setText(listOfUsers.get(index).getEmail());
        //phoneNumberTextView.setText(listOfUsers.get(index).getPhoneNumber());




        if (listOfUsers.get(index).getPhotoURI()!=null){
            Picasso.with(view.getContext()).load(listOfUsers.get(index).getPhotoURI()).into(avatar);
        }

        nameTextView.setKeyListener(null);
        emailTextVIew.setKeyListener(null);
        phoneNumberTextView.setKeyListener(null);

        return view;

    }


}
