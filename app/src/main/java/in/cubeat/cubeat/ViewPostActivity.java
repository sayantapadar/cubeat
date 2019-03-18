package in.cubeat.cubeat;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.FitWindowsLinearLayout;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.List;

public class ViewPostActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog bar;
    TextView title;
    WebView content;
    TextView author;
    TextView date;
    ImageView image;
    FitWindowsLinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_post);
        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        bar = new ProgressDialog(this);
        bar.setMessage("Loading");
        bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        bar.setIndeterminate(true);
        bar.setProgress(0);
        bar.setCanceledOnTouchOutside(false);
        bar.show();

        title= (TextView) findViewById(R.id.view_title);
        content= (WebView) findViewById(R.id.view_content);
        author= (TextView) findViewById(R.id.view_author);
        date= (TextView) findViewById(R.id.view_date);
        image=(ImageView) findViewById(R.id.view_image);
        container=(FitWindowsLinearLayout)findViewById(R.id.container);

        if(getIntent().getAction() == Intent.ACTION_VIEW){
            String url=getIntent().getDataString();
            String urlParts[]=url.split("/");
            String ID=urlParts[urlParts.length-1];
            BackendHelper.setPostByUrl(container, this,ID,bar, title, content, author, date, image);
        }
        else {
            String ID = getIntent().getStringExtra(BackendHelper.POST_ID_KEY_INTENT);
            if (BackendHelper.post == null || !BackendHelper.post.ID.equals(ID)) {
                BackendHelper.setPost(container, this, ID, bar, title, content, author, date, image);
            } else {
                Log.d("ViewPost", "There m8");
                container.setVisibility(View.VISIBLE);
                bar.dismiss();
                title.setText(BackendHelper.post.title);
                content.loadDataWithBaseURL(BackendHelper.post.postURL, BackendHelper.post.content, "text/html", "UTF-8", null);
                WebSettings webSettings=content.getSettings();
                //webSettings.setJavaScriptEnabled(true);
                webSettings.setDisplayZoomControls(true);
                author.setText(BackendHelper.post.author);
                date.setText(BackendHelper.post.date);
                if (!BackendHelper.post.imageURL.equals("") && BackendHelper.post.imageURL != null) {
                    Glide.with(this)
                            .load(BackendHelper.post.imageURL).asBitmap()
                            .fitCenter()
                            .into(image);
                } else
                    Glide.with(this)
                            .load(R.drawable.thumbnail_default)
                            .fitCenter()
                            .into(image);
            }
        }
        findViewById(R.id.share).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("Dest","Destroyed");
        Glide.clear(image);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(!BackendHelper.mainRunning) {
            startActivity(new Intent(ViewPostActivity.this,MainActivity.class));
            finish();
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {   //if this is to be changed, changed onClickListener in BackendHelper.setPostInUI too.
        switch (v.getId()){
            case R.id.share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");

                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, BackendHelper.post.title);
                share.putExtra(Intent.EXTRA_TEXT, BackendHelper.post.postURL);

                startActivity(Intent.createChooser(share, "Share post"));
        }
    }
}
