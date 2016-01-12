package com.srbodroid.aicpmemo;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private DrawingView drawView;
    private float smallBrush, mediumBrush, largeBrush;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private int selectedColorR = 38, defaultColorG = 50, defaultColorB = 56;
    private int defaultColorR = 38, selectedColorG = 50, selectedColorB = 56, selectedColorRGB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
//            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });

            drawView = (DrawingView)findViewById(R.id.drawing);

            smallBrush = getResources().getInteger(R.integer.small_size);
            mediumBrush = getResources().getInteger(R.integer.medium_size);
            largeBrush = getResources().getInteger(R.integer.large_size);

            drawView.setBrushSize(smallBrush);

            FloatingActionButton brush_size = (FloatingActionButton) findViewById(R.id.draw_btn);
            brush_size.setOnClickListener(this);

            FloatingActionButton erase_btn = (FloatingActionButton) findViewById(R.id.erase_btn);
            erase_btn.setOnClickListener(this);

            FloatingActionButton color_fill = (FloatingActionButton) findViewById(R.id.color_fill);
            color_fill.setOnClickListener(this);

            FloatingActionButton color_pick = (FloatingActionButton) findViewById(R.id.color_pick);
            color_pick.setOnClickListener(this);

            FloatingActionButton save_btn = (FloatingActionButton) findViewById(R.id.save_btn);
            save_btn.setOnClickListener(this);
        }

    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // We will need to request the permission
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the storage
                    Toast whyWeNeedPermission = Toast.makeText(getApplicationContext(),
                            "We need permission to access that image.", Toast.LENGTH_SHORT);
                    whyWeNeedPermission.show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


            } else {
                // The permission is granted, we can perform the action
                drawView = (DrawingView)findViewById(R.id.drawing);
                drawView.placeImage(imageUri);
            }
        }
    }

    @Override
    public void onClick(View view){
    //respond to clicks
        if(view.getId() == R.id.color_fill){
            defaultColorR = selectedColorR;
            defaultColorG = selectedColorG;
            defaultColorB = selectedColorB;
            final ColorPicker cp = new ColorPicker(MainActivity.this, defaultColorR, defaultColorG, defaultColorB);
            cp.show();
            /* On Click listener for the dialog, when the user select the color */
            Button okColor = (Button)cp.findViewById(R.id.okColorButton);
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                    selectedColorR = cp.getRed();
                    selectedColorG = cp.getGreen();
                    selectedColorB = cp.getBlue();

                /* Or the android RGB Color (see the android Color class reference) */
                    selectedColorRGB = cp.getColor();
                    drawView.startNew(selectedColorRGB);

                    cp.dismiss();
                }
            });
        } else if(view.getId() == R.id.color_pick){
            defaultColorR = selectedColorR;
            defaultColorG = selectedColorG;
            defaultColorB = selectedColorB;
            final ColorPicker cp = new ColorPicker(MainActivity.this, defaultColorR, defaultColorG, defaultColorB);
            cp.show();
            /* On Click listener for the dialog, when the user select the color */
            Button okColor = (Button)cp.findViewById(R.id.okColorButton);
            okColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                    selectedColorR = cp.getRed();
                    selectedColorG = cp.getGreen();
                    selectedColorB = cp.getBlue();

                /* Or the android RGB Color (see the android Color class reference) */
                    selectedColorRGB = cp.getColor();
                    String finalColor = "#FF" + String.valueOf(selectedColorR) + String.valueOf(selectedColorG) + String.valueOf(selectedColorB);

                    drawView.setColor(finalColor);

                    cp.dismiss();
                }
            });
        } else if(view.getId()==R.id.draw_btn){
            //draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(smallBrush);
                    drawView.setLastBrushSize(smallBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(mediumBrush);
                    drawView.setLastBrushSize(mediumBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setBrushSize(largeBrush);
                    drawView.setLastBrushSize(largeBrush);
                    drawView.setErase(false);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if(view.getId()==R.id.erase_btn){
            //switch to erase - choose size
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Eraser size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawView.setErase(true);
                    drawView.setBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if(view.getId()==R.id.save_btn){
            //save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }
}
