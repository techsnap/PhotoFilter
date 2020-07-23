package com.mypc.photofilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileDescriptor;
import java.io.IOException;

import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private int WRITE_PERMISSION_CODE = 1;

    private ImageView imageView;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
    }

    public void choosePhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void savePhoto(View v) {
        requestStoragePermissions();

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap filteredImage = drawable.getBitmap();

            MediaStore.Images.Media.insertImage(getContentResolver(), filteredImage, "Filtered", "Wow");    
        } else {
            Toast.makeText(this, "Load image first.", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestStoragePermissions() {
        // If permission hasn't been granted then request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        }
    }

    public void applyFilter(Transformation<Bitmap> filter) {
        Glide
                .with(this)
                .load(image)
                .apply(RequestOptions.bitmapTransform(filter))
                .into(imageView);
    }

    public void applySepia(View v) {
        applyFilter(new SepiaFilterTransformation());
    }

    public void applyToon(View v) {
        applyFilter(new ToonFilterTransformation());
    }

    public void applySketch(View v) {
        applyFilter(new SketchFilterTransformation());
    }

    public void applyPixelate(View v) {
        applyFilter(new PixelationFilterTransformation());
    }

    public void applyCrop(View v) {
        applyFilter(new CropTransformation(100, 300, CropTransformation.CropType.CENTER));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                // Create image
                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();

                // Set image
                imageView.setImageBitmap(image);
            } catch (IOException e) {
                Log.e("photo filter", "Image not found", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
//    if(requestCode == WRITE_PERMISSION_CODE) {
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//        }
//    }
}
