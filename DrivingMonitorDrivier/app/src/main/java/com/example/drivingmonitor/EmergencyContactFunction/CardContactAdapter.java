package com.example.drivingmonitor.EmergencyContactFunction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.drivingmonitor.R;

import java.util.List;

public class CardContactAdapter extends BaseAdapter {
    private List<CardContactClass> list;

    public CardContactAdapter(List<CardContactClass> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CardContactHolder cardContactHolder;

        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_card, viewGroup, false);
            cardContactHolder = new CardContactHolder();
            cardContactHolder.name = view.findViewById(R.id.contactName);
            cardContactHolder.number = view.findViewById(R.id.contactNumber);
            view.setTag(cardContactHolder);
        }else {
            cardContactHolder = (CardContactHolder) view.getTag();
        }

        cardContactHolder.name.setText(list.get(i).getName());
        cardContactHolder.number.setText(list.get(i).getNumber());

        return view;
    }

    static class CardContactHolder {
        TextView name;
        TextView number;
    }
}
