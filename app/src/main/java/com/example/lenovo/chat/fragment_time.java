package com.example.lenovo.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Lenovo on 20.11.2017.
 */

public class fragment_time extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_layout, container, false);
        TextView time =(TextView)v.findViewById(R.id.fragment_time_id);
        return v;
    }
}
