package tralolta.obscurascript;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import tralolta.obscurascript.imagemanip.exceptions.UnsupportedFormatException;
import tralolta.obscurascript.imagemanip.readers.ImageReaderFactory;
import tralolta.obscurascript.imagemanip.readers.ImageReaderStrategy;
import tralolta.obscurascript.util.DataUtil;
import tralolta.obscurascript.util.RealPathUtil;

public class ReadActivity extends Activity  implements ActivityCompat.OnRequestPermissionsResultCallback{
    private Activity thisActivity;

    private String imagePath = null;
    private Uri imageUri;


    private static final int PERMISSION_REQUEST_READ = 9001;
    View.OnClickListener selectImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= 23){
                if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    doRead();
                } else {
                    ActivityCompat.requestPermissions(ReadActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_READ);
                }
            }else{
                doRead();
            }
        }
    };

    private void doRead(){
        ImageView imageView = findViewById(R.id.imageReadView);
        imageView.setImageDrawable(null);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if(Build.VERSION.SDK_INT >= 19){
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
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

    private void processImage(){
        ImageReaderFactory imageReaderFactory = new ImageReaderFactory();
        try {
            ImageReaderStrategy readerStrategy = imageReaderFactory.getInstance(
                    DataUtil.getBytesFromInputStream(this.getContentResolver().openInputStream(imageUri)));
            if(readerStrategy == null){
                throw new IOException();
            }
            TextView outputTextField = findViewById(R.id.outputTextField);
            outputTextField.setText("");
            byte[] readBytes = readerStrategy.readFromImage();
            EditText passwordTextField = findViewById(R.id.passwordReadTextField);
            String password = String.valueOf(passwordTextField.getText());
            passwordTextField.setText("");
            byte[] salt = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
            Cipher aesCipher = Cipher.getInstance("DES");
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "DES");
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = aesCipher.doFinal(readBytes);
            outputTextField.setText(new String(decryptedBytes, "UTF-8"));
            Toast.makeText(thisActivity, "Reading from image complete!", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedFormatException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Image format not supported!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Error reading from file!", Toast.LENGTH_SHORT).show();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Decryption error!", Toast.LENGTH_SHORT).show();
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Decryption error!", Toast.LENGTH_SHORT).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Decryption error!", Toast.LENGTH_SHORT).show();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Decryption error!", Toast.LENGTH_SHORT).show();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Decryption error!", Toast.LENGTH_SHORT).show();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Toast.makeText(thisActivity, "Decryption error!", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(thisActivity, "Could not read image content!", Toast.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener readDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView passwordTextView = findViewById(R.id.passwordReadTextField);
            String passwordText = String.valueOf(passwordTextView.getText());
            if(imageUri == null){
                Toast.makeText(thisActivity, "Please select an image!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(passwordText.length() == 0 || passwordText.equals("")){
                Toast.makeText(thisActivity, "Please enter a password!", Toast.LENGTH_SHORT).show();
                return;
            }
            processImage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        thisActivity = this;
        setButtonListeners();
        EditText outputTextField = findViewById(R.id.outputTextField);
        outputTextField.setInputType(InputType.TYPE_NULL);
        final EditText passwordReadField = findViewById(R.id.passwordReadTextField);
        passwordReadField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_DONE){
                    passwordReadField.clearFocus();
                }
                return false;
            }
        });
    }

    private void setButtonListeners(){
        ImageButton selectFileButton = findViewById(R.id.selectfileReadButton);
        ImageButton writeDataButton = findViewById(R.id.readDataButton);
        selectFileButton.setOnClickListener(selectImageListener);
        writeDataButton.setOnClickListener(readDataListener);
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
            ImageView imageView = findViewById(R.id.imageReadView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
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
}
