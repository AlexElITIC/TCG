package com.example.alex.tcgapp.auth;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.alex.tcgapp.R;
import com.example.alex.tcgapp.model.CardItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class EditCardActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private DatabaseReference mDatabaseRef;
    private ImageView imageView;
    private EditText cardName;
    private EditText cardDescription;
    private EditText cardStore;
    private EditText cardPrice;
    private EditText cardRarity;
    private EditText cardStock;
    private Uri imgUri;
    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "cards";
    private StorageReference mStorageRef;
    CardItem card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        Intent intent = getIntent();
       card = (CardItem) intent.getSerializableExtra(getResources().getString(R.string.cardItem));

        cardName = (EditText) findViewById(R.id.cardName);
        cardName.setText(card.getName());

        cardDescription=(EditText) findViewById(R.id.cardDescription);
        cardDescription.setText(card.getDescription());

        cardStore=(EditText) findViewById(R.id.cardStore);
        cardStore.setText(card.getStore());

        cardPrice=(EditText) findViewById(R.id.cardPrice);
        cardPrice.setText(card.getPrice());

        cardRarity=(EditText) findViewById(R.id.cardRarity);
        cardRarity.setText(card.getRarity());

        cardStock=(EditText) findViewById(R.id.cardStock);
        cardStock.setText(card.getStock());

        imageView = (ImageView) findViewById(R.id.cardView);
        Glide.with(EditCardActivity.this).load(card.getUrl()).into(imageView);

    }

    public void btnBrowse_Click(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selectPic)), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void update(View view) {
        if (imgUri != null){
            final StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));
        ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.updated), Toast.LENGTH_SHORT).show();
                card.setName(cardName.getText().toString());
                card.setDescription(cardDescription.getText().toString());
                card.setStore(cardStore.getText().toString());
                card.setPrice(cardPrice.getText().toString());
                card.setRarity(cardRarity.getText().toString());
                card.setStock(cardStock.getText().toString());
                card.setUrl(downloadUrl.toString());
                mDatabaseRef.child(card.getUid()).setValue(card);
                finish();
            }

        });
    }
    else{
            card.setName(cardName.getText().toString());
            card.setDescription(cardDescription.getText().toString());
            card.setStore(cardStore.getText().toString());
            card.setPrice(cardPrice.getText().toString());
            card.setRarity(cardRarity.getText().toString());
            card.setStock(cardStock.getText().toString());
            mDatabaseRef.child(card.getUid()).setValue(card);
            finish();
        }

    }

    public void delete(View view) {
        mDatabaseRef.child(card.getUid()).removeValue();
        finish();
    }
}
