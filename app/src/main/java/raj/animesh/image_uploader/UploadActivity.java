package raj.animesh.image_uploader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import raj.animesh.image_uploader.databinding.ActivityUploadBinding;


public class UploadActivity extends AppCompatActivity {

    ActivityUploadBinding b;
    private Uri imageUri;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference = db.getReference("Images");

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.progressBar.setVisibility(View.INVISIBLE);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            b.imageUpload.setImageURI(imageUri);
                        }else if (imageUri == null){
                            Toast.makeText(UploadActivity.this, "Please select a image!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        b.imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photopickeer = new Intent();
                photopickeer.setAction(Intent.ACTION_GET_CONTENT);
                photopickeer.setType("image/*");
                activityResultLauncher.launch(photopickeer);
            }
        });

        b.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(imageUri);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UploadActivity.this,MainActivity.class));
        finish();
    }

    private void uploadImage(Uri imageuri){
        String title = b.titleUplaod.getText().toString().trim();

        b.progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(title) && imageUri != null){

            // saving image in the firebase storage

            final StorageReference filePath = storageReference.child("Images").child("my_images" + Timestamp.now().getSeconds());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // uploading details to  firebase realtime database
                            String imageUrl = uri.toString();

                            DataClass dataClass = new DataClass();
                            dataClass.setCaption(title);
                            dataClass.setImageUrl(imageUrl);
                            
                            String key = databaseReference.push().getKey();

                            databaseReference.child(key).setValue(dataClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    b.progressBar.setVisibility(View.INVISIBLE);

                                    startActivity(new Intent(UploadActivity.this,MainActivity.class));
                                    b.titleUplaod.setText("");
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, "Failed to upload. Try again!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                    b.progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            b.progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Enter Title please!!!", Toast.LENGTH_SHORT).show();
        }
    }
}