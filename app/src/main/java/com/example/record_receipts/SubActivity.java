package com.example.record_receipts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubActivity extends AppCompatActivity implements SelectListener{
    RecyclerView recyclerView;
    List<MyModel> myModelList;
    CustomAdapter customAdapter;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_main);
        linearLayout = findViewById(R.id.activity_sub_linear_layout);
        displayItems();

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    private void displayItems() {

        String path = Environment.getExternalStorageDirectory().toString();
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();

        recyclerView = findViewById(R.id.recycler_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        myModelList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            myModelList.add(new MyModel(files[i].getName()));
        }
        myModelList.add(new MyModel("John"));
        myModelList.add(new MyModel("Sam"));
        customAdapter = new CustomAdapter(this, myModelList, this);
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    public void onItemClicked(MyModel myModel) {
        // here is what happens when clicked on item use this to click on images
        // and redirect to ocr
        // the photo needs to be passed to the activity.
        //right now, im just passing the name of the item
        String item_name = myModel.getName();
        Intent intent = new Intent(this, webview_activity.class);
        intent.putExtra("item_name", item_name);
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

            // this is where to delete the item... for now teting but actually delete the object
            // from the directory
            myModelList.remove(viewHolder.getAdapterPosition());
            customAdapter.notifyDataSetChanged();
        }
    };
}