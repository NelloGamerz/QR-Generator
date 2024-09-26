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
    private Generator_Fragment generatorFragment;
    private Scanner_Fragment scannerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Generator_Fragment()).commit();


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            // Initialize fragments only once
            if (generatorFragment == null) {
                generatorFragment = new Generator_Fragment();
            }
            if (scannerFragment == null) {
                scannerFragment = new Scanner_Fragment();
            }

            // Use a switch statement for better readability
            switch (item.getItemId()) {
                case R.id.generator:
                    // Check if already displayed
                    if (!(getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof Generator_Fragment)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, generatorFragment)
                                .commitAllowingStateLoss(); // Prevent state loss
                    }
                    return true;

                case R.id.scanner:
                    // Check if already displayed
                    if (!(getSupportFragmentManager().findFragmentById(R.id.frameLayout) instanceof Scanner_Fragment)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frameLayout, scannerFragment)
                                .commitAllowingStateLoss(); // Prevent state loss
                    }
                    return true;

                default:
                    return false; // Handle any other unexpected cases
            }
        });
    }
}
