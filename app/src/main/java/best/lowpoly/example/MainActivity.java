package best.lowpoly.example;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import java.io.IOException;


import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {

    private ImageView polyImg;
    private TextView addTxt, saveTxt, gradientTxt;
    private Bitmap resBitmap;
    private LowPolyViewModel lowPolyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        polyImg = findViewById(R.id.polyImg);
        addTxt = findViewById(R.id.addTxt);
        saveTxt = findViewById(R.id.saveTxt);
        gradientTxt = findViewById(R.id.gradientTxt);

        lowPolyViewModel = new ViewModelProvider(getViewModelStore(),
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                    .get(LowPolyViewModel.class);

        lowPolyViewModel.getLiveData().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                ProgressDialog.getInstance(MainActivity.this).close();
                polyImg.setImageBitmap(bitmap);
                resBitmap = bitmap;
            }
        });

        lowPolyViewModel.getStatusLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.photo);
        lowPolyViewModel.setImage(bitmap);
        ProgressDialog.getInstance(this).show();

        saveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resBitmap != null){
                    try {
                        saveImage(resBitmap, getResources().getString(R.string.app_name));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 100);
            }
        });

        gradientTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker picker = new ColorPicker(MainActivity.this);
                picker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, final int color1) {
                        ColorPicker picker = new ColorPicker(MainActivity.this);
                        picker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                            @Override
                            public void setOnFastChooseColorListener(int position, int color2) {
                                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                        new int[]{color1, color2});

                                DisplayMetrics metrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                                Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                gd.setBounds(0, 0, metrics.widthPixels, metrics.heightPixels);
                                gd.draw(canvas);

                                ProgressDialog.getInstance(MainActivity.this).show();
                                lowPolyViewModel.setImage(bitmap);
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
                        picker.setColumns(5)
                                .show();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                picker.setColumns(5)
                        .show();
            }
        });

    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException {
        lowPolyViewModel.saveImage(bitmap, name);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            lowPolyViewModel.openImage(imageUri);
            ProgressDialog.getInstance(this).show();

        }else {
            Toast.makeText(MainActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
