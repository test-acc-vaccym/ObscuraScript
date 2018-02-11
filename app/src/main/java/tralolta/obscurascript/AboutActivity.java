package tralolta.obscurascript;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setText();
    }

    private void setText(){
        TextView textView = findViewById(R.id.textView);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ObscuraScript v1.0");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("This application is provided free of charge and without advertisements.");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("©");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Nemanja Đokić");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Digitabulum Software 2018.");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Visual assets designed by");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Gregor Cresnar from www.flaticon.com ");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("https://www.flaticon.com/authors/gregor-cresnar");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Application icon designed by");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Smashicons from www.flaticon.com");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("https://www.flaticon.com/authors/smashicons");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Supported image formats are");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("JPG PNG");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("Note that some messaging applications and viewers may use image compression methods.");
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("In case of that the content of the image will most likely be altered and embedded text will not be preserved.");
        textView.setText(stringBuilder.toString());
    }
}
