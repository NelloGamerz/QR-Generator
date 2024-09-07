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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText linkInput;
    private EditText QRName;
    private Button generateButton;
    private Button resetButton;
    private Button downloadButton;
    private ImageView qrCodeImage;
    private Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        linkInput = findViewById(R.id.linkInput);
        generateButton = findViewById(R.id.generateButton);
        qrCodeImage = findViewById(R.id.qrCodeImage);
        resetButton = findViewById(R.id.resetButton);
        downloadButton = findViewById(R.id.DownloadQR);
        QRName = findViewById(R.id.QR_Name);

        generateButton.setOnClickListener(view -> {
            String link = linkInput.getText().toString().trim();
            if (!link.isEmpty()) {
                generateQRCode(link);  // Generate QR code
            } else {
                Toast.makeText(MainActivity.this, "Please enter a link", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for the reset button
        resetButton.setOnClickListener(view -> resetFields());

        downloadButton.setOnClickListener(view -> {
            String imageName = QRName.getText().toString().trim();
            if (qrBitmap != null && !imageName.isEmpty()) {  // Check if QR code is generated and name is provided
                saveQRCode(imageName);  // Save the QR code
            } else {
                Toast.makeText(MainActivity.this, "Generate a QR code and provide an image name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateQRCode(String link) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrBitmap = barcodeEncoder.encodeBitmap(link, BarcodeFormat.QR_CODE, 250, 250);  // Store generated bitmap in qrBitmap
            qrCodeImage.setImageBitmap(qrBitmap);  // Display QR code
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void resetFields() {
        linkInput.setText(""); // Clear the input field
        QRName.setText(""); // Clear the QR name field
        qrCodeImage.setImageResource(0); // Reset the ImageView (remove QR code)
        qrBitmap = null;
        Toast.makeText(MainActivity.this, "Fields Reset", Toast.LENGTH_SHORT).show();
    }

    private void saveQRCode(String imageName) {
        OutputStream fos;
        try {
            String imagePath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 and above (Scoped Storage)
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Code");
                values.put(MediaStore.Images.Media.IS_PENDING, true);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName + ".png");

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = getContentResolver().openOutputStream(uri);
                imagePath = uri.toString(); // Save path for scanning

                values.put(MediaStore.Images.Media.IS_PENDING, false);
                getContentResolver().update(uri, values, null, null);

            } else {
                // For Android 9 and below
                File dir = new File(Environment.getExternalStorageDirectory(), "QRCode");
                if (!dir.exists()) dir.mkdirs();
                File image = new File(dir, imageName + ".png");
                fos = new FileOutputStream(image);
                imagePath = image.getAbsolutePath(); // Save path for scanning
            }

            if (qrBitmap != null) {  // Check if qrBitmap exists before saving
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(this, "QR Code saved as " + imageName + ".png", Toast.LENGTH_LONG).show();
                scanFile(imagePath); // Trigger media scan
            } else {
                Toast.makeText(this, "Error: No QR code to save", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving the QR code", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to trigger media scanner to update gallery
    private void scanFile(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}