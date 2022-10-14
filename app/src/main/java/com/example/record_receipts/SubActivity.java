package com.example.record_receipts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubActivity extends AppCompatActivity implements SelectListener{
    RecyclerView recyclerView;
    List<MyModel> myModelList;
    CustomAdapter customAdapter;
    LinearLayout linearLayout;
    Button returnBtn;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_main);
        linearLayout = findViewById(R.id.activity_sub_linear_layout);
        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(view -> {
            returnToMain("dining");
        });
        displayItems();

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    private void displayItems() {

        // Get list of files in specified path
        String category = getIntent().getStringExtra("category");
        path = getApplicationContext().getFilesDir()+"/"+category;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        // Display and fill recyclerview
        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        myModelList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            myModelList.add(new MyModel(files[i].getName()));
        }
        customAdapter = new CustomAdapter(this, myModelList, this);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    public void onItemClicked(MyModel myModel) {
        // Passes in the path to jpeg
        String item_name = myModel.getName();
        Intent intent = new Intent(this, webview_activity.class);
        intent.putExtra("path", path+"/"+item_name);
        startActivity(intent);
        //Toast.makeText(this, myModel.getName(), Toast.LENGTH_SHORT).show();
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Snackbar snackbar = Snackbar.make(linearLayout, "Item Deleted!", Snackbar.LENGTH_LONG);
            snackbar.show();

            // this is where to delete the item... for now tetsing but actually delete the object
            // from the directory
            myModelList.remove(viewHolder.getAdapterPosition());
            customAdapter.notifyDataSetChanged();
        }
    };

    // Return to MainActivity, showing list of directories
    private void returnToMain(String subDir)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}