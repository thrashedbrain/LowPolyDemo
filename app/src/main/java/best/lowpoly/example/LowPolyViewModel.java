package best.lowpoly.example;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zebrostudio.rxlowpoly.Quality;
import com.zebrostudio.rxlowpoly.RxLowpoly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class LowPolyViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> bitmapMutableLiveData;
    private MutableLiveData<String> stringMutableLiveData;

    public LowPolyViewModel(@NonNull Application application) {
        super(application);
    }

    void openImage(Uri uri){
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            setImage(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    LiveData<Bitmap> getLiveData(){
        bitmapMutableLiveData = new MutableLiveData<>();
        return bitmapMutableLiveData;
    }

    LiveData<String> getStatusLiveData(){
        stringMutableLiveData = new MutableLiveData<>();
        return stringMutableLiveData;
    }

    void setImage(Bitmap bitmap){
        RxLowpoly.with(getApplication())
                .input(bitmap)
                .quality(Quality.VERY_HIGH)
                .generateAsync()
                .subscribe(new SingleObserver<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Bitmap bitmap) {

                        bitmapMutableLiveData.postValue(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    void saveImage(Bitmap bitmap, String name) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getApplication().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + name);
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + name;

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        if (fos != null) {
            fos.flush();
            fos.close();
        }

        if (saved){
            stringMutableLiveData.postValue("Success!");
        }
        else {
            stringMutableLiveData.postValue("Something went wrong");
        }
    }

}
