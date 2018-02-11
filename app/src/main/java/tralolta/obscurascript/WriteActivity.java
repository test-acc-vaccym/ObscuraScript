package tralolta.obscurascript;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import tralolta.obscurascript.imagemanip.writers.ImageWriterFactory;
import tralolta.obscurascript.imagemanip.writers.ImageWriterStrategy;
import tralolta.obscurascript.imagemanip.exceptions.UnsupportedFormatException;
import tralolta.obscurascript.util.DataUtil;
import tralolta.obscurascript.util.RealPathUtil;

public class WriteActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{


    private Activity thisActivity;

    private String imagePath = null;
    private Uri imageUri;

    View.OnClickListener selectImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= 23){
                if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    doRead();
                } else {
                    ActivityCompat.requestPermissions(WriteActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_READ);
                }
            }else{
                doRead();
            }
        }
    };

    private void doRead(){
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(null);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            thisActivity.startActivityForResult(intent, 0);
        }
        else
        {
            Toast.makeText(thisActivity, "Can't perform operation", Toast.LENGTH_SHORT).show();
            return;
        }
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    private void processImage(byte[] messageBytes){
        ImageWriterFactory imageWriterFactory = new ImageWriterFactory();
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "obscuraScript_" + timeStamp + ".jpg";
            File myFolder = new File(Environment.getExternalStorageDirectory(), "ObscuraScript");
            if(!myFolder.exists()){
                myFolder.mkdir();
            }
            ImageWriterStrategy writerStrategy = imageWriterFactory.getInstance(
                    DataUtil.getBytesFromInputStream(this.getContentResolver().openInputStream(imageUri)),
                    myFolder);
            if(writerStrategy == null){
                throw new IOException();
            }
            writerStrategy.writeToImage(messageBytes);
            Toast.makeText(thisActivity, "Image saved to " + myFolder.getPath(), Toast.LENGTH_SHORT).show();
        } catch (UnsupportedFormatException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Image format not supported!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Error writing to file!", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int PERMISSION_REQUEST_STORAGE = 1337;
    private static final int PERMISSION_REQUEST_READ = 9001;

    View.OnClickListener writeDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= 23){
                if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    doWrite();
                } else {
                    ActivityCompat.requestPermissions(WriteActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_STORAGE);
                }
            }else{
                doWrite();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doWrite();
            } else {
                Toast.makeText(thisActivity, "Permission to write to storage not granted!", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PERMISSION_REQUEST_READ){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doRead();
            } else {
                Toast.makeText(thisActivity, "Permission to read storage not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doWrite(){
        TextView passwordTextView = findViewById(R.id.passwordTextField);
        String passwordText = String.valueOf(passwordTextView.getText());
        byte[] encryptedMessage = null;
        if(imageUri == null){
            Toast.makeText(thisActivity, "Please select an image!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordText.length() == 0 || passwordText.equals("")){
            Toast.makeText(thisActivity, "Please enter a password!", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView messageTextView = findViewById(R.id.messageTextField);
        String message = String.valueOf(messageTextView.getText());
        if(message.length() == 0 ||message.equals("")){
            Toast.makeText(thisActivity, "Please enter a message!", Toast.LENGTH_SHORT).show();
            return;
        }
        passwordTextView.setText("");
        messageTextView.setText("");
        try {
            byte[] salt = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            KeySpec spec = new PBEKeySpec(passwordText.toCharArray(), salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "DES");
            Cipher aesCipher = Cipher.getInstance("DES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedMessage = aesCipher.doFinal(message.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        }
        processImage(encryptedMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        thisActivity = this;
        setButtonListeners();
        final EditText messageTextField = findViewById(R.id.messageTextField);
        messageTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    messageTextField.clearFocus();
                }
                return false;
            }
        });
    }

    private void setButtonListeners(){
        ImageButton selectFileButton = findViewById(R.id.selectFileButton);
        ImageButton writeDataButton = findViewById(R.id.writeDataButton);
        selectFileButton.setOnClickListener(selectImageListener);
        writeDataButton.setOnClickListener(writeDataListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resCode, Intent data) {
        if(resCode == Activity.RESULT_OK && data != null){
            String realPath;
            imageUri = data.getData();
            if (Build.VERSION.SDK_INT < 19) {
                imageUri = data.getData();
            } else {
                imageUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    thisActivity.getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                }
                catch (SecurityException e){
                    e.printStackTrace();
                }
            }
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());

                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());

                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());


            imagePath = realPath;
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }
}
