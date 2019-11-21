package com.example.tbscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static int OPEN_IMAGE_REQUEST_CODE = 101;

    ImageView tbImage;
    Button analyseImage;
    TextView resultTxtView;

    ProgressDialog analyzeProgressDialog;

    Classifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        classifier = new Classifier(this);

        resultTxtView = findViewById(R.id.result);
        tbImage = findViewById(R.id.tb_image);
        tbImage.setOnClickListener((v)->getImage());

        analyseImage = findViewById(R.id.btn_analyze);
        analyseImage.setOnClickListener((v -> {
            showProgressDialog();
            analyzeImage();
        }));
    }

    private void analyzeImage() {
        Random random = new Random();
        Bitmap bitmap = ((BitmapDrawable) tbImage.getDrawable()).getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int minDim = (width>height)?height:width;

        TBResult tbResult;

        // check if image is the same as placeholder image
        if (bitmap.getWidth() == 480 && bitmap.getHeight() == 301){
            Toast.makeText(this,"Please select an image"+random.nextInt(7),Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            return;
        }

        for (int i = 0; i < minDim/2; i++) {
            int rand = random.nextInt(minDim);
            int pixel = bitmap.getPixel(rand,rand);

            if (!(Color.red(pixel)==Color.green(pixel)  && Color.blue(pixel)==Color.red(pixel) && Color.blue(pixel)==Color.green(pixel))){
                Toast.makeText(this,"Please select a valid x-ray image",Toast.LENGTH_SHORT).show();
                hideProgressDialog();
                return;
            }
        }

        tbResult = classifier.getResult(bitmap);
        displayResult(tbResult);
        hideProgressDialog();
    }

    private void displayResult(TBResult tbResult){
        String result;
        if (tbResult == TBResult.TB){
            result = "Has TB";
        }else{
            result = "The Lungs image has no trace of TB";
        }

        resultTxtView.setText(result);
    }

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,OPEN_IMAGE_REQUEST_CODE);
    }

    private void showProgressDialog(){
        analyzeProgressDialog = new ProgressDialog(this);
        analyzeProgressDialog.setMessage("Checking image");
        analyzeProgressDialog.setCancelable(false);
        analyzeProgressDialog.show();
    }

    private void hideProgressDialog(){
        analyzeProgressDialog.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            if (requestCode==OPEN_IMAGE_REQUEST_CODE){
                if(data!=null){
//                    tbImage.setImageURI(data.getData());
                    Glide.with(MainActivity.this).load(data.getData()).override(400,400).into(tbImage);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

                getSharedPreferences(LoginActivity.TB_PREF,MODE_PRIVATE).edit()
                        .putBoolean(LoginActivity.LOGIN_STATUS,false).apply();
        }
        return super.onOptionsItemSelected(item);
    }
}
