package com.example.alex.tcgapp.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alex.tcgapp.R;
import com.example.alex.tcgapp.model.CardItem;

import java.util.List;

public class CardListAdapter extends BaseAdapter {
    private Activity context;
    private List<CardItem> cardList;


    public CardListAdapter(@NonNull Activity context, @NonNull List<CardItem> objects) {

        this.context = context;
        cardList = objects;

    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        protected TextView name;
        protected ImageView img;
        protected TextView cardRarity;
        protected TextView cardPrice;
        protected TextView stock;
        protected TextView store;
        protected TextView user;
        public ViewHolder(View view) {
             name =  view.findViewById(R.id.cardName);
             img =  view.findViewById(R.id.cardView);
             cardRarity =  view.findViewById(R.id.cardRarity);
             cardPrice =  view.findViewById(R.id.cardPrice);
             stock =  view.findViewById(R.id.cardStock);
             store =  view.findViewById(R.id.store);
             user =  view.findViewById(R.id.user);

        }
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CardItem card=(CardItem)getItem(position);
        viewHolder.name.setText(card.getName());
        viewHolder.cardPrice.setText(card.getPrice());
        viewHolder.cardRarity.setText(card.getRarity());
        viewHolder.user.setText(card.getUser());
        viewHolder.stock.setText(card.getStock());
        viewHolder.store.setText(card.getStore());
        Glide.with(context).load(card.getUrl()).into(viewHolder.img);
        return convertView;
    }


}