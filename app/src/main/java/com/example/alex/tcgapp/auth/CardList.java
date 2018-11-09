package com.example.alex.tcgapp.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.alex.tcgapp.R;
import com.example.alex.tcgapp.adapters.CardListAdapter;
import com.example.alex.tcgapp.model.CardItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardList extends Fragment {
    private DatabaseReference mDatabaseRef;
    private List<CardItem> imgList;
    private ListView cardList;
    private CardListAdapter adapter;
    private ProgressDialog progressDialog;
    final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    private String currentUser=auth.getEmail();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_card_list, container,false);
        imgList = new ArrayList<>();
        cardList = (ListView) view.findViewById(R.id.cardListView);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                CardItem card = (CardItem) cardList.getItemAtPosition(position);
                if(currentUser.equals(card.getUser())){
                    Intent intent = new Intent(getActivity(), EditCardActivity.class);
                    intent.putExtra(getResources().getString(R.string.cardItem), card);
                    startActivity(intent);
                }
            }
        });
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(CreateCardActivity.FB_DATABASE_PATH);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
                imgList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    CardItem img = snapshot.getValue(CardItem.class);
                    imgList.add(img);
                }
                adapter = new CardListAdapter(getActivity(), imgList);
                cardList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });
        return view;
    }

}
