package com.example.lenovo.chat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class message_adapter extends ArrayAdapter<message>  {
    public message_adapter(Context context, int resource,List<message> objects) {
        super(context, resource, objects);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        message msg = getItem(position);
        int layoutResource = 0;

        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(msg.getUser())){
            layoutResource = R.layout.message_my;
        }else{
            layoutResource = R.layout.message;
        }

        convertView = ((Activity) getContext()).getLayoutInflater().inflate(layoutResource, parent, false);

        TextView message_text_view = (TextView) convertView.findViewById(R.id.message_text);
        TextView message_time_view = (TextView) convertView.findViewById(R.id.message_time);
        message_text_view.setText(msg.getText());
        message_time_view.setText(DateFormat.format("dd-MM-yy (HH:mm)", msg.getTime()));

        return convertView;

    }

}
