package com.example.qrgenerator;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrgenerator.databinding.FragmentGeneratorBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Generator_Fragment extends Fragment {

    FragmentGeneratorBinding binding;
    private Bitmap qrBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using view binding
        binding = FragmentGeneratorBinding.inflate(inflater, container, false);

//        binding = NewGeneratorLayoutBinding.inflate(inflater, container, false);

        // Generate QR Code on button click
        binding.generateButton.setOnClickListener(view -> {
            String link = binding.linkInput.getText().toString().trim();
            if (!link.isEmpty()) {
                generateQRCode(link);
            } else {
                Toast.makeText(requireContext(), "Please enter a link", Toast.LENGTH_SHORT).show();
            }
        });

        // Reset fields
        binding.resetButton.setOnClickListener(view -> resetFields());

        // Save the QR Code on button click
        binding.DownloadQR.setOnClickListener(view -> {
            String imageName = binding.QRName.getText().toString().trim();
            if (qrBitmap != null && !imageName.isEmpty()) {
                saveQRCode(imageName);
            } else {
                Toast.makeText(requireContext(), "Generate a QR code and provide an image name", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void generateQRCode(String link) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            qrBitmap = barcodeEncoder.encodeBitmap(link, BarcodeFormat.QR_CODE, 250, 250);
            binding.qrCodeImage.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void resetFields() {
        binding.linkInput.setText("");
        binding.QRName.setText("");
        binding.qrCodeImage.setImageResource(0);
        qrBitmap = null;
        Toast.makeText(requireContext(), "Fields Reset", Toast.LENGTH_SHORT).show();
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

                Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = requireContext().getContentResolver().openOutputStream(uri);
                imagePath = uri.toString(); // Save path for scanning

                values.put(MediaStore.Images.Media.IS_PENDING, false);
                requireContext().getContentResolver().update(uri, values, null, null);

            } else {
                // For Android 9 and below
                File dir = new File(Environment.getExternalStorageDirectory(), "QRCode");
                if (!dir.exists()) dir.mkdirs();
                File image = new File(dir, imageName + ".png");
                fos = new FileOutputStream(image);
                imagePath = image.getAbsolutePath(); // Save path for scanning
            }

            if (qrBitmap != null) {
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(requireContext(), "QR Code saved as " + imageName + ".png", Toast.LENGTH_LONG).show();
                scanFile(imagePath); // Trigger media scan
            } else {
                Toast.makeText(requireContext(), "Error: No QR code to save", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error saving the QR code", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to trigger media scanner to update gallery
    private void scanFile(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        requireContext().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
