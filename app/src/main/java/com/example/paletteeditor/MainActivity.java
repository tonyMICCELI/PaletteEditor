package com.example.paletteeditor;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private ImageView imageView;
    private Bitmap bitmap;

    private Button galleryBtn;

    private Switch rgbSwitch;

    private int r, g, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.extractImage);
        textView = (TextView) findViewById(R.id.rgbText);
        galleryBtn = (Button) findViewById(R.id.gallery);
        rgbSwitch = (Switch) findViewById(R.id.rbgToHex);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);

        imageView.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                bitmap = imageView.getDrawingCache();
                int pixel = bitmap.getPixel((int) motionEvent.getX(), (int) motionEvent.getY());

                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);

                textView.setBackgroundColor(Color.rgb(r,g,b));
                displayColorCode();
            }
            return true;
        });

        rgbSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayColorCode();
                if (rgbSwitch.isChecked()){
                    rgbSwitch.setText("HEX");
                } else {
                    rgbSwitch.setText("RGB");
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launchActivity.launch(intent);
            }
        });
    }

    protected void displayColorCode() {
        String hexCode = rgbToHex(r,g,b);
        if (rgbSwitch.isChecked()) {
            textView.setText(hexCode);
        } else {
            textView.setText("R("+r+")\n"+"G("+g+")\n"+"B("+b+")");
        }
    }
    protected String rgbToHex(int r, int g, int b) {
        String rHex = Integer.toHexString(r);
        String gHex = Integer.toHexString(g);
        String bHex = Integer.toHexString(b);
        String finalHex = "#"+rHex+gHex+bHex;
        return finalHex;
    }

    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        Bitmap imageBitmap = null;
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(imageBitmap);
                        bitmap = imageView.getDrawingCache();
                    }
                }
            });
}
