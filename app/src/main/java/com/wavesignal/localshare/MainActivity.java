package com.wavesignal.localshare;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View;

import com.wavesignal.localshare.model.UpdateListener;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button btnServer;
    private Button btnClient;
    private Button btnScan;
    private TextView tvStatus;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnServer = findViewById(R.id.btnServer);
        btnClient = findViewById(R.id.btnClient);
        btnScan = findViewById(R.id.btnScan);

        tvStatus = findViewById(R.id.tvStatus);
        tvMessage = findViewById(R.id.tvMessage);

        Handler handler = new Handler(Looper.getMainLooper());

        UpdateListener listener = new UpdateListener() {
            @Override
            public void onUpdate(String message) {
                handler.post(() -> tvMessage.setText(message));
            }

            @Override
            public void onMessage(String message) {

            }
        };

        btnServer.setOnClickListener(view -> {
            ChatUser chatUser = new ChatUser();
            chatUser.startServer(listener);
            tvStatus.setText("I am Server");
        });

        btnClient.setOnClickListener(view -> {
            ChatUser chatUser = new ChatUser();
            chatUser.startClient(listener);
            tvStatus.setText("I am Client");
        });

        btnScan.setOnClickListener(view -> {
            ChatUser chatUser = new ChatUser();
            chatUser.startScanning();
        });

        // Function to capture a screenshot of the entire screen
//        handler.post(() -> takeScreenshot(this));

        // To generate click on button programmatically
//        handler.post(() -> clickMe(btnServer));
    }

    private void takeScreenshot(MainActivity mainActivity) {
        View view = getWindow().getDecorView().getRootView();
        Bitmap bitmap = captureScreenshot(view);
//            Bitmap bitmap = captureScreenshot(btnServer);
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.programmer);

        ImageView imageView = findViewById(R.id.ivScreenshot);
        imageView.setImageBitmap(bitmap);
    }

    // Function to capture a screenshot of a specific view
    public Bitmap captureScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return screenshot;
    }

    // Capture a screenshot and save it as a bitmap to a file
    public void saveScreenshotAsBitmap(Bitmap screenshot, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clickMe(View view) {
        // Create a MotionEvent instance for your touch event
        int x = 5; // X coordinate of the touch event
        int y = 5; // Y coordinate of the touch event

        // Create a MotionEvent instance for a down event
        MotionEvent downEvent = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);

        // Dispatch the down event to the view
        view.dispatchTouchEvent(downEvent);

        // Create a MotionEvent instance for an up event
        MotionEvent upEvent = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, x, y, 0);

        // Dispatch the up event to the view
        view.dispatchTouchEvent(upEvent);
    }
}