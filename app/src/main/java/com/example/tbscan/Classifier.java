package com.example.tbscan;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

class Classifier {

    private static final int BATCH_SIZE = 1;
    private static final int IMAGE_WIDTH = 224;
    private static final int IMAGE_HEIGHT = 224;
    private static final int CHANNELS = 3;

    private Interpreter tfliteInterpreter;
    private float[][] output= new float[1][1];

    Classifier(Activity activity){
        tfliteInterpreter = new Interpreter(loadModelFile(activity));
    }

    TBResult getResult(Bitmap bitmap){
        tfliteInterpreter.run(convertImageToByteBufferAndResize(bitmap),output);
        return (output[0][0]>0.5)?TBResult.TB:TBResult.NORMAL;
    }

    private MappedByteBuffer loadModelFile(Activity activity) {
        String model = "tb_model.tflite";
        AssetFileDescriptor fileDescriptor = null;
        MappedByteBuffer mappedByteBuffer = null;
        try {
            fileDescriptor = activity.getAssets().openFd(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert fileDescriptor != null;
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        try {
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mappedByteBuffer;
    }

    private ByteBuffer convertImageToByteBufferAndResize(Bitmap image){

        Bitmap scaledBitmap = scaleBitmap(image);

        int[] intValues = new int[IMAGE_WIDTH * IMAGE_HEIGHT];

        ByteBuffer imgData = ByteBuffer.allocateDirect(
                4 * BATCH_SIZE * IMAGE_WIDTH * IMAGE_HEIGHT * CHANNELS);
        imgData.order(ByteOrder.nativeOrder());
        imgData.rewind();
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < 224; ++i) {
            for (int j = 0; j < 224; ++j) {
                int val = intValues[pixel++];
                imgData.putFloat(((val >> 16) & 0xFF)/255f);
                imgData.putFloat(((val >> 8) & 0xFF)/255f);
                imgData.putFloat(((val) & 0xFF)/255f);
            }
        }
        return imgData;
    }

    private Bitmap scaleBitmap(Bitmap image){
        return Bitmap.createScaledBitmap(image,IMAGE_WIDTH,IMAGE_HEIGHT, true);
    }
}
