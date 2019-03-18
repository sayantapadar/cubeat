package in.cubeat.cubeat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SecretActivity extends AppCompatActivity {
    String url;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_layout);
    }

    public void devClick(View view) {
        url = "https://www.facebook.com/sayan.tapadar.7";
        i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void desClick(View view) {
        url = "https://www.facebook.com/anirban.dgupta.7";
        i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void fouClick(View view) {
        url = "https://www.facebook.com/shounak.mukherjee120796";
        i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
