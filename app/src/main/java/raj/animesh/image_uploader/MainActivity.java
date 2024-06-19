package raj.animesh.image_uploader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import raj.animesh.image_uploader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    //recycler view
    RecyclerView recyclerView;

    //adapter
    MyAdapter myAdapter;
    ArrayList<DataClass> arrayList;

    // firabase
    FirebaseDatabase dbMain = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = dbMain.getReference("Images");

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);

        arrayList = new ArrayList<>();

        myAdapter = new MyAdapter(arrayList,this);
        binding.recyclerView.setAdapter(myAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    DataClass dataClass = snapshot1.getValue(DataClass.class);
                    arrayList.add(dataClass);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UploadActivity.class));
                finish();
            }
        });

        /*********************************

          someone add download wallpaper download feature to this app...

         ***********************************/


    }
}