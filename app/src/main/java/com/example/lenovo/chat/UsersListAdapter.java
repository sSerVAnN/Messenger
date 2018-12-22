package com.example.lenovo.chat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class UsersListAdapter extends ArrayAdapter<User> {
    public UsersListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        User user = getItem(position);

        convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.user_item, parent, false);

        TextView userNameTextView = (TextView) convertView.findViewById(R.id.user_name);
        ImageView userAvatarView = (ImageView) convertView.findViewById(R.id.avatar);

        userNameTextView.setText(user.getName());
        if (user.getPhotoURI()!=null){
            Picasso.with(this.getContext()).load(user.getPhotoURI()).into(userAvatarView);
        }
        return convertView;
    }


}
