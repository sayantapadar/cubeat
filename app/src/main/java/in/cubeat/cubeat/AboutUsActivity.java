package in.cubeat.cubeat;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity implements View.OnClickListener {
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        TextView content= (TextView) findViewById(R.id.about_text);
        FloatingActionButton contactFAB= (FloatingActionButton) findViewById(R.id.about_us_fab);
        CardView facebookFollow=(CardView)findViewById(R.id.about_facebook);
        CardView twitterFollow=(CardView)findViewById(R.id.about_twitter);

        contactFAB.setOnClickListener(this);
        facebookFollow.setOnClickListener(this);
        twitterFollow.setOnClickListener(this);
        findViewById(R.id.about_logo).setOnClickListener(this);

        Spanned text=Html.fromHtml(getResources().getString(R.string.about_text_content));
        content.setText(text);
        content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        String url;
        Intent i;
        switch (v.getId()){
            case R.id.about_logo:
                counter++;
                if(counter>=5){
                    startActivity(new Intent(AboutUsActivity.this,SecretActivity.class));
                }
                break;
            case R.id.about_facebook:
                url = "https://www.facebook.com/cubeat.in";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.about_twitter:
                url = "https://twitter.com/cubeat_in";
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.about_us_fab:
                Snackbar.make(findViewById(R.id.about_coordinator),"Encountered a bug?",Snackbar.LENGTH_SHORT)
                        .setAction("Report", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@cubeat.in"});
                                email.putExtra(Intent.EXTRA_SUBJECT, "Encountered a bug in android application");

//need this to prompts email client only
                                email.setType("message/rfc822");

                                startActivity(Intent.createChooser(email, "Choose an email client :"));
                            }
                        }).show();
                break;
        }
    }
}
