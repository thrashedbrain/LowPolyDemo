package best.lowpoly.example;

import android.app.Activity;
import android.view.View;

import androidx.appcompat.app.AlertDialog;


public class ProgressDialog {
    private static ProgressDialog Instance;
    private Activity context;
    private AlertDialog alertDialog;

    static ProgressDialog getInstance(Activity context){
        if (Instance == null){
            Instance = new ProgressDialog(context);
        }
        return Instance;
    }

    private ProgressDialog(Activity context){
        this.context = context;
    }

    void show(){
        View view = context.getLayoutInflater().inflate(R.layout.progress_dialog, null);
        alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .show();
    }

    void close(){
        alertDialog.cancel();
    }
}
