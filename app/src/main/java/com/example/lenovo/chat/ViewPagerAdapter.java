package com.example.lenovo.chat;


import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private List<User> listOfUsers;
    private LayoutInflater inflater;
    private Activity activity;
    private boolean test = true;

    public ViewPagerAdapter(List<User> listOfUsers, Activity activity) {
        this.listOfUsers = listOfUsers;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return  listOfUsers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.user_profile, container, false);
        ImageView avatar = (ImageView)view.findViewById(R.id.avatar);
        EditText nameTextView = (EditText)view.findViewById(R.id.edit_text_name);
        EditText emailTextVIew = (EditText)view.findViewById(R.id.edit_text_email);
        EditText phoneNumberTextView = (EditText)view.findViewById(R.id.edit_text_phone);

        nameTextView.setText(listOfUsers.get(position).getName());
        emailTextVIew.setText(listOfUsers.get(position).getEmail());
        phoneNumberTextView.setText(listOfUsers.get(position).getPhoneNumber());

        if (listOfUsers.get(position).getPhotoURI()!=null){
            Picasso.with(view.getContext()).load(listOfUsers.get(position).getPhotoURI()).into(avatar);
        }

        nameTextView.setKeyListener(null);
        emailTextVIew.setKeyListener(null);
        phoneNumberTextView.setKeyListener(null);
        container.addView(view);
        return view;
    }
}
