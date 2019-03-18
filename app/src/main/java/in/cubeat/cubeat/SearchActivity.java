package in.cubeat.cubeat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SearchActivity extends AppCompatActivity {
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private EditText from,to;
    private SimpleDateFormat dateFormatter;

    String search="";
    String fromDate="";
    String toDate="";

    ImageView searchFail;
    RecyclerView postRecycler;
    EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        postRecycler=(RecyclerView)findViewById(R.id.post_recycler);
        postRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //postRecycler.setAdapter(new PostRecyclerAdapter(this, BackendHelper.allPosts));

        keyword=(EditText)findViewById(R.id.search_keyword);
        keyword.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        from=(EditText)findViewById(R.id.search_from);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            from.setShowSoftInputOnFocus(false);
        to=(EditText)findViewById(R.id.search_to);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            to.setShowSoftInputOnFocus(false);

        setDateTimeField();

        from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(to.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(from.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(keyword.getWindowToken(), 0);
                    fromDatePickerDialog.show();
                } else {
                    fromDatePickerDialog.dismiss();
                }
            }
        });
        to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(to.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(from.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(keyword.getWindowToken(), 0);
                    toDatePickerDialog.show();
                } else {
                    toDatePickerDialog.dismiss();
                }
            }
        });

        View.OnClickListener touchToDisplayMessage= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.search_coordinator),"Long click to remove date",Snackbar.LENGTH_SHORT).show();
            }
        };
        from.setOnClickListener(touchToDisplayMessage);
        to.setOnClickListener(touchToDisplayMessage);

        from.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("Long click","Long");
                from.setText("");
                return false;
            }
        });
        to.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("Long click","Long");
                to.setText("");
                return false;
            }
        });
        keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        searchFail=(ImageView)findViewById(R.id.search_fail);
       /* Glide.with(this)
                .load(R.drawable.search_gif)
                .asGif()
                .crossFade()
                .into(searchFail);*/
        findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(to.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(from.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(keyword.getWindowToken(), 0);
        searchFail.setVisibility(View.GONE);
        ProgressDialog bar = new ProgressDialog(SearchActivity.this);
        bar.setMessage("Loading");
        bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        bar.setIndeterminate(true);
        bar.setProgress(0);
        bar.setCancelable(false);
        bar.setCanceledOnTouchOutside(false);
        bar.show();
        search=keyword.getText().toString();
        BackendHelper.setSearchResults(SearchActivity.this,bar,postRecycler,search,fromDate,toDate,searchFail);
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                from.setText(dateFormatter.format(newDate.getTime()));

                Date date=newDate.getTime();
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                fromDate = df.format(date);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                to.setText(dateFormatter.format(newDate.getTime()));

                Date date=newDate.getTime();
                TimeZone tz = TimeZone.getTimeZone("UTC");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
                df.setTimeZone(tz);
                toDate = df.format(date);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }
}
