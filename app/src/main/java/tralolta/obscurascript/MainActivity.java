package tralolta.obscurascript;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{


    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonListeners();
        thisActivity = this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_READ){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doRead();
            } else {
                Toast.makeText(thisActivity, "Permission to read storage not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int PERMISSION_REQUEST_READ = 9001;
    private void doRead(){Intent intent = new Intent(Intent.ACTION_VIEW);
        File folder = new File(Environment.getExternalStorageDirectory(), "ObscuraScript");
        if(!folder.exists()){
            Toast.makeText(thisActivity, "No images saved yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        File[] files = folder.listFiles();
        File lastFile = files[files.length - 1];
        Uri selectedUri = Uri.fromFile(lastFile);
        intent.setDataAndType(selectedUri, "image/*");
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(thisActivity, "Can't perform operation", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonListeners(){
        ImageButton writeButton = findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity.getBaseContext(), WriteActivity.class);
                startActivity(intent);
            }
        });
        ImageButton readButton = findViewById(R.id.readButton);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity.getBaseContext(), ReadActivity.class);
                startActivity(intent);
            }
        });
        ImageButton aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisActivity.getBaseContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
        ImageButton openImageFolderButton = findViewById(R.id.openImagesFolderButton);
        openImageFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23){
                    if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        doRead();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_READ);
                    }
                }else{
                    doRead();
                }
            }
        });
    }

}
