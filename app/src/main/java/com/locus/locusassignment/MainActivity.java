package com.locus.locusassignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.locus.locusassignment.model.Item;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DataLoaderTaskListener, OnItemClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    String imageFilePath;
    File photoFile = null;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private int position;
    private Item item;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new DataLoaderTask(this).execute();
    }

    @Override
    @NonNull
    public Context getContext() {
        return this;
    }

    @Override
    public void onDataLoaded(@Nullable List<Item> items) {
        this.items = items;
        Log.d("TAG", items != null ? items.toString() : "null");
        this.progressBar.setVisibility(View.GONE);
        if (items == null) {
            Toast.makeText(this, "Items are Null", Toast.LENGTH_SHORT).show();
            return;
        }
        recyclerViewAdapter = new RecyclerViewAdapter(items, this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onItemClick(int position, Item item) {
        this.position = position;
        this.item = item;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        }
    }


    @Override
    public void onCrossClick(int position, Item item) {
        item.getDataMap().addProperty("imagePath", "null");
        recyclerViewAdapter.notifyItemChanged(position);
    }


    private void dispatchTakePictureIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.locus.locusassignment", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String photoUri = null;
            if (extras != null) {
                photoUri = extras.getString(MediaStore.EXTRA_OUTPUT);
            }
            this.item.getDataMap().addProperty("imagePath", String.valueOf(photoFile));
            recyclerViewAdapter.notifyItemChanged(position);

        } else {
            Toast.makeText(this, "Could not take pitcure from camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageFilePath = image.getAbsolutePath();
        return image;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                printAllItems();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void printAllItems() {
        for (int i = 0; i < items.size(); i++) {
            Log.d("TAG", items.get(i).toString());
        }
    }

}


