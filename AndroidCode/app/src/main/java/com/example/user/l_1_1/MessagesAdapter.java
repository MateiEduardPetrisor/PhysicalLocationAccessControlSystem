package com.example.user.l_1_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessagesAdapter extends ArrayAdapter<String> {
    public MessagesAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.message_details1, null);
        }

        String message = getItem(position);
        if (message != null) {

            TextView tvDetails = (TextView) view.findViewById(R.id.tvDetails);
            if (tvDetails != null) {
                tvDetails.setText(message);
            }
        }
        return view;
    }
}