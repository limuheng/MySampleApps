/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.example.android.customviews;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.android.customviews.views.CustomBitmapDrawable;
import com.example.android.customviews.views.DrawerView;

public class MainActivity extends Activity {

    public static final int PICK_IMAGE = 1;

    DrawerView mDrawerView;
    Button mClearButton;
    Button mGenShapeButton;
    Button mGenImageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        Resources res = getResources();
//        final PieChart pie = (PieChart) this.findViewById(R.id.Pie);
//        pie.addItem("Agamemnon", 2, res.getColor(R.color.seafoam));
//        pie.addItem("Bocephus", 3.5f, res.getColor(R.color.chartreuse));
//        pie.addItem("Calliope", 2.5f, res.getColor(R.color.emerald));
//        pie.addItem("Daedalus", 3, res.getColor(R.color.bluegrass));
//        pie.addItem("Euripides", 1, res.getColor(R.color.turquoise));
//        pie.addItem("Ganymede", 3, res.getColor(R.color.slate));
//
//        ((Button) findViewById(R.id.Reset)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                pie.setCurrentItem(0);
//            }
//        });

        mDrawerView = (DrawerView) findViewById(R.id.shapedrawer);

        mClearButton = ((Button) findViewById(R.id.clear_button));
        mClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerView != null) {
                    mDrawerView.clearAll();
                }
            }
        });

        mGenShapeButton = ((Button) findViewById(R.id.gen_shape_button));
        mGenShapeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerView != null) {
                    mDrawerView.addCustomDrawable(ShapeGenerator.genOvalShape(getApplicationContext()));
                }
            }
        });
        mGenImageButton = ((Button) findViewById(R.id.gen_image_button));
        mGenImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (mDrawerView != null) {
                try {
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    InputStream imageStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap= BitmapFactory.decodeStream(imageStream);
                    CustomBitmapDrawable drawable = new CustomBitmapDrawable();
                    drawable.setBitmap(bitmap);
                    mDrawerView.addCustomDrawable(drawable);

//                    File file = new File("/sdcard/Download/test.png");
//                    if (!file.exists()) {
//                        file.createNewFile();
//                    }
//                    FileOutputStream fos = new FileOutputStream(file);
//                    drawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    fos.flush();
//                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

