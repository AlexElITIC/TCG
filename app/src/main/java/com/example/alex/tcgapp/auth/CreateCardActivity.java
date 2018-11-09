package com.example.alex.tcgapp.auth;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alex.tcgapp.R;
import com.example.alex.tcgapp.model.CardItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreateCardActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ImageView imageView;
    private EditText cardName;
    private EditText cardDescription;
    private EditText cardStore;
    private EditText cardPrice;
    private EditText cardRarity;
    private EditText cardStock;
    private Uri imgUri;
    Button loadButton;
    private final String channelID = "notification";
    private final int notificationID=001;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "cards";
    public static final int REQUEST_CODE = 1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        imageView = (ImageView) findViewById(R.id.cardView);
        cardName = (EditText) findViewById(R.id.cardName);
        cardDescription = (EditText) findViewById(R.id.cardDescription);
        cardPrice = (EditText) findViewById(R.id.cardPrice);
        cardRarity = (EditText) findViewById(R.id.cardRarity);
        cardStock = (EditText) findViewById(R.id.cardStock);
        cardStore = (EditText) findViewById(R.id.cardStore);
        loadButton= (Button) findViewById(R.id.loadImage);
        if(ValidatePermissions()){
            loadButton.setEnabled(true);
        }else{
            loadButton.setEnabled(false);
        }
    }

    private boolean ValidatePermissions() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            loadRecomendation();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                loadButton.setEnabled(true);
            }else{
                manualPermissions();
            }
        }

    }

    private void manualPermissions() {
        final CharSequence[] opciones={getResources().getString(R.string.yes),getResources().getString(R.string.no)};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(CreateCardActivity.this);
        alertOpciones.setTitle(getResources().getString(R.string.manual));
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals(getResources().getString(R.string.yes))){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.permisosAceptados),Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void loadRecomendation() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(CreateCardActivity.this);
        dialog.setTitle(getResources().getString(R.string.permisosDesactivados));
        dialog.setMessage(getResources().getString(R.string.mustAcept));

        dialog.setPositiveButton(getResources().getString(R.string.acept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},100);
                }
            }
        });
        dialog.show();
    }

    public void btnBrowse_Click(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.seleccionar_imagen)), REQUEST_CODE);
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

    public void btnUpload_Click(View v) {
        if (imgUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(getResources().getString(R.string.cartaRegistrada));
            dialog.show();

            final StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));


            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.cartaRegistrada), Toast.LENGTH_SHORT).show();
                    CardItem newCard = new CardItem(cardName.getText().toString(),downloadUrl.toString(),user.getEmail().toString(),cardDescription.getText().toString(),cardRarity.getText().toString(),cardStore.getText().toString(),cardStock.getText().toString(),cardPrice.getText().toString(),UUID.randomUUID().toString());


                    mDatabaseRef.child(newCard.getUid()).setValue(newCard);
                    cardStore.setText("");
                    cardName.setText("");
                    cardRarity.setText("");
                    cardStock.setText("");
                    cardPrice.setText("");
                    cardDescription.setText("");
                    imageView.setImageBitmap(null);
                    notification(newCard.getName());
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show upload progress

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage(getResources().getString(R.string.cartaRegistrada)+" " + (int) progress + "%");
                        }
                    });

        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.seleccionar_imagen), Toast.LENGTH_SHORT).show();
        }
    }

    void notification(String cardName){
        createChannel();
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, channelID);
        notificationBuilder.setSmallIcon(R.drawable.logo);
        notificationBuilder.setContentTitle(getResources().getString(R.string.newCard));
        notificationBuilder.setContentText(cardName);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationID, notificationBuilder.build());

    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = getResources().getString(R.string.newCard);
            String description = getResources().getString(R.string.aviso);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(channelID,name,importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
