package com.example.qrgenerator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QRDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_detail);

        // Get the QR code result passed from the scanning activity
        Intent intent = getIntent();
        String qrResult = intent.getStringExtra("QR_RESULT");

        // Find the TextView and display the result
        TextView qrResultTextView = findViewById(R.id.qr_result_text);
        qrResultTextView.setText(qrResult);

        // Check if the result contains a URL and make it clickable
        if (!TextUtils.isEmpty(qrResult)) {
            if (qrResult.startsWith("http://") || qrResult.startsWith("https://")) {
                qrResultTextView.setAutoLinkMask(Linkify.WEB_URLS);  // Automatically linkify URLs
                qrResultTextView.setOnClickListener(v -> {
                    // Open the link in the browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrResult));
                    startActivity(browserIntent);
                });
            }
        }

//        Button button = findViewById(R.id.btn_back);
//        button.setOnClickListener(v -> {
//            Intent back = new Intent(QRDetailActivity.this, Scanner_Fragment.class);
//            startActivity(back);
//        });

        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish this activity to go back to the previous screen (QR Scanner Fragment)
                finish();
            }
        });
    }
}