package com.firebase.hackathon.firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b1 = findViewById(R.id.button);
        mImageView = findViewById(R.id.image);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);

            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            String resultText = result.getText();
                            for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
                                String blockText = block.getText();
                                Float blockConfidence = block.getConfidence();
                                List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                Point[] blockCornerPoints = block.getCornerPoints();
                                Rect blockFrame = block.getBoundingBox();
                                for (FirebaseVisionText.Line line: block.getLines()) {
                                    String lineText = line.getText();
                                    Float lineConfidence = line.getConfidence();
                                    List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                    Point[] lineCornerPoints = line.getCornerPoints();
                                    Rect lineFrame = line.getBoundingBox();
                                    for (FirebaseVisionText.Element element: line.getElements()) {
                                        String elementText = element.getText();
                                        Float elementConfidence = element.getConfidence();
                                        List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                        Point[] elementCornerPoints = element.getCornerPoints();
                                        Rect elementFrame = element.getBoundingBox();
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    Log.e(TAG, "Text Recognition Failed");
                                }
                            });
        }
        }
    }
}
