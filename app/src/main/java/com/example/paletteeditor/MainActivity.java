package com.example.paletteeditor;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private ImageView imageView;
    private Bitmap bitmap;

    private Button galleryBtn;
    private Button saveBtn;
    private Button displayPalette;

    private Switch rgbSwitch;

    private int r, g, b;
    private String colorStringList = "";

    public static final String SHARED_PREFS_COLOR = "sharedPrefsColor";
    public static final String COLORS_STRING_LIST = "colorList";

    private String testDisplay = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.extractImage);
        textView = (TextView) findViewById(R.id.rgbText);
        galleryBtn = (Button) findViewById(R.id.gallery);
        saveBtn = (Button) findViewById(R.id.saveToPalette);
        displayPalette = (Button) findViewById(R.id.displayPalette);
        rgbSwitch = (Switch) findViewById(R.id.rbgToHex);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_COLOR, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();

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

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hexColorTmp = rgbToHex(r,g,b);
                colorStringList=colorStringList+hexColorTmp+"\n";
                saveData();
            }
        });

        displayPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PaletteActivity.class);
                startActivity(intent);
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
        rHex = fixHexLength(rHex);
        gHex = fixHexLength(gHex);
        bHex = fixHexLength(bHex);
        String finalHex = "#"+rHex+gHex+bHex;
        return finalHex;
    }

    protected String fixHexLength(String hexCode) {
        String newHexCode;
        if (hexCode.length()==1) {
            newHexCode = "0"+hexCode;
        } else {
            newHexCode = hexCode;
        }
        return newHexCode;
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

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_COLOR, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(COLORS_STRING_LIST, colorStringList);

        editor.apply();

        Toast.makeText(this, "Couleur sauvegardée", Toast.LENGTH_SHORT).show();
    }
}
