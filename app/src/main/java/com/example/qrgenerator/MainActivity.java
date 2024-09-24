package com.example.qrgenerator;

import static android.media.MediaScannerConnection.scanFile;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.qrgenerator.databinding.ActivityMainBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Generator_Fragment()).commit();

        binding.bottomNavigationView.setOnItemSelectedListener(view ->{
            if(view.getItemId() == R.id.generator){
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Generator_Fragment()).commit();
                return true;
            }else if(view.getItemId() == R.id.scanner){
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Scanner_Fragment()).commit();
                return true;
            }
            return true;
        });
    }
}
