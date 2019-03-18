package in.cubeat.cubeat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.FitWindowsLinearLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SAYAN on 31-05-2016.
 */

class BackendHelper {
    static ArrayList<PostPreview> allPosts;
    private static ArrayList<PostPreview> editorials = new ArrayList<>();
    private static ArrayList<PostPreview> talk = new ArrayList<>();
    private static ArrayList<PostPreview> news = new ArrayList<>();
    private static ArrayList<PostPreview> upcoming = new ArrayList<>();
    private static ArrayList<PostPreview> review = new ArrayList<>();
    public static final String rootURL="https://public-api.wordpress.com/rest/v1.1/sites/cubeat.wordpress.com/";

    static Post post;

    static final String POST_ID_KEY_INTENT = "postID";
    static boolean mainRunning = false;

    private static RequestQueue mRequestQueue;
    private static Cache cache;
    private static Network network;
    private static boolean initialized = false;

    private static void initializeNetwork(Context context) {
        if (!initialized) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);

            // Start the queue
            mRequestQueue.start();
            initialized = true;
        }
    }

    static void setPosts(final Context context, final ProgressDialog bar, final ViewPager viewPager, final TabLayout tabLayout, final MainActivity.ImagePagerAdapter imagePagerAdapter) {
        String URL = rootURL+"posts/?fields=found,author,date,ID,title,post_thumbnail,categories,excerpt&number=100";
        initializeNetwork(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                bar.dismiss();
                try {
                    ArrayList<PostPreview> data = new ArrayList<>();
                    Log.d("JSON", response.toString());
                    try {
                        for (int i = 0; i < Integer.parseInt(response.getString("found")); i++) {
                            PostPreview postPreview = getPreviewObject(response, i);
                            data.add(postPreview);
                            if (response.getJSONArray("posts").getJSONObject(i).getJSONObject("categories").has("News Stories"))
                                news.add(postPreview);
                            if (response.getJSONArray("posts").getJSONObject(i).getJSONObject("categories").has("Editorials"))
                                editorials.add(postPreview);
                            if (response.getJSONArray("posts").getJSONObject(i).getJSONObject("categories").has("CUbTalk"))
                                talk.add(postPreview);
                            if (response.getJSONArray("posts").getJSONObject(i).getJSONObject("categories").has("Film Reviews"))
                                review.add(postPreview);
                            if (response.getJSONArray("posts").getJSONObject(i).getJSONObject("categories").has("Upcoming Events"))
                                upcoming.add(postPreview);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    allPosts = data;
                    imagePagerAdapter.posts = data;
                    imagePagerAdapter.notifyDataSetChanged();
                    setupViewPager(context, viewPager, tabLayout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                bar.dismiss();
                error.printStackTrace();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add the request to the RequestQueue.
        mRequestQueue.add(jsonArrayRequest);

        // ...
    }

    public static void setSearchResults(final Context context, final ProgressDialog bar, final RecyclerView recyclerView, String keyword, String fromDate, String toDate, final ImageView searchFail) {
        String URL = rootURL+"posts/?fields=found,author,date,ID,title,post_thumbnail,categories,excerpt&number=100";
        if (keyword != null) {
            if (!keyword.equals(""))
                URL = URL + "&search=" + keyword.replace(' ', '+');
            Log.d("Serch keyword", keyword);
        }
        if (fromDate != null) {
            if (!fromDate.equals(""))
                URL = URL + "&after=" + fromDate;
            Log.d("From date", fromDate);
        }
        if (toDate != null) {
            if (!toDate.equals(""))
                URL = URL + "&before=" + toDate;
            Log.d("To date", toDate);
        }
        Log.d("SearchURL", URL);
        initializeNetwork(context);
        final
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                bar.dismiss();
                Log.d("Search", response.toString());
                ArrayList<PostPreview> data = new ArrayList<>();
                try {
                    for (int i = 0; i < Integer.parseInt(response.getString("found")); i++) {
                        PostPreview postPreview = getPreviewObject(response, i);
                        data.add(postPreview);
                    }
                    recyclerView.setAdapter(new PostRecyclerAdapter(context, data));
                    if(Integer.parseInt(response.getString("found"))==0){
                        searchFail.setVisibility(View.VISIBLE);
                        /*Glide.with(context)
                                .load(R.drawable.search_gif)
                                .asGif()
                                .into(searchFail);*/
                        Log.d("search","unsuccessful");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                bar.dismiss();
                error.printStackTrace();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add the request to the RequestQueue.
        mRequestQueue.add(jsonArrayRequest);
    }

    private static PostPreview getPreviewObject(JSONObject response, int i) {
        PostPreview postPreview = new PostPreview();
        try {
            postPreview.author = response.getJSONArray("posts").getJSONObject(i).getJSONObject("author").getString("name");
            postPreview.title = response.getJSONArray("posts").getJSONObject(i).getString("title");
            postPreview.date = getDate(response.getJSONArray("posts").getJSONObject(i).getString("date"));
            postPreview.ID = response.getJSONArray("posts").getJSONObject(i).getString("ID");
            postPreview.excerpt = response.getJSONArray("posts").getJSONObject(i).getString("excerpt");
            if (response.getJSONArray("posts").getJSONObject(i).has("post_thumbnail")) {
                if (response.getJSONArray("posts").getJSONObject(i).getJSONObject("post_thumbnail").has("URL")) {
                    postPreview.imageURL = response.getJSONArray("posts").getJSONObject(i).getJSONObject("post_thumbnail").getString("URL");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postPreview;
    }

    static void setupViewPager(Context context, ViewPager viewPager, TabLayout tabLayout) {
        tabLayout.removeAllTabs();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager());
        PostFragment editorialFragment = PostFragment.getInstance(editorials);
        PostFragment newsFragment = PostFragment.getInstance(news);
        PostFragment talkFragment = PostFragment.getInstance(talk);
        PostFragment reviewFragment = PostFragment.getInstance(review);
        PostFragment upcomingFragment = PostFragment.getInstance(upcoming);

        viewPagerAdapter.addFrag(editorialFragment, "Editorials");
        viewPagerAdapter.addFrag(newsFragment, "News stories");
        viewPagerAdapter.addFrag(talkFragment, "CUB Talk");
        viewPagerAdapter.addFrag(reviewFragment, "Film Reviews");
        viewPagerAdapter.addFrag(upcomingFragment, "Upcoming Events");

        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.VISIBLE);

        Log.d("Editorial", String.valueOf(editorials.size()));
        Log.d("News", String.valueOf(news.size()));
        Log.d("Talk", String.valueOf(talk.size()));
        Log.d("Film", String.valueOf(review.size()));
        Log.d("Film", String.valueOf(upcoming.size()));
    }

    /*public static void setupViewPagerUpcomingEvents(Context context, ViewPager viewPager, TabLayout tabLayout){
        tabLayout.removeAllTabs();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager());
        PostFragment upcomingFragment = PostFragment.getInstance(upcoming);
        viewPagerAdapter.addFrag(upcomingFragment, "Upcoming events");
        Log.d("Upcoming", String.valueOf(upcoming.size()));

        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
    }*/

    static void setPost(final FitWindowsLinearLayout container, final Context context, String ID, final ProgressDialog bar, final TextView title, final WebView content, final TextView author, final TextView date, final ImageView image) {
        final String ID_URL = rootURL+"posts/" + ID;
        setPostInUI(container, context, ID_URL, bar, title, content, author, date, image);
    }

    static void setPostByUrl(final FitWindowsLinearLayout container, final Context context, String url, final ProgressDialog bar, final TextView title, final WebView content, final TextView author, final TextView date, final ImageView image) {
        final String ID_URL = rootURL+"posts/slug:" + url;
        setPostInUI(container, context, ID_URL, bar, title, content, author, date, image);
    }

    private static void setPostInUI(final FitWindowsLinearLayout container, final Context context, String ID_URL, final ProgressDialog bar, final TextView title, final WebView content, final TextView author, final TextView date, final ImageView image) {
        initializeNetwork(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ID_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Post I clicked", response.toString());
                container.setVisibility(View.VISIBLE);
                post = new Post();
                //bar.dismiss();
                try {
                    post.ID = response.getString("ID");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    post.title = response.getString("title");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    post.author = response.getJSONObject("author").getString("name");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    post.date = getDate(response.getString("date"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    post.content = response.getString("content");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    post.imageURL = response.getJSONObject("post_thumbnail").getString("URL");
                } catch (Exception e) {
                    post.imageURL = "";
                    e.printStackTrace();
                }
                try {
                    post.postURL = response.getString("URL");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                title.setText(post.title);

                String htmlData = BackendHelper.post.content;
                if (Build.VERSION.SDK_INT >= 21) {
                    content.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
                content.loadDataWithBaseURL(post.postURL, htmlData, "text/html", "UTF-8", null);
                WebSettings webSettings=content.getSettings();
                //webSettings.setJavaScriptEnabled(true);
                webSettings.setDisplayZoomControls(true);
                content.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                            bar.dismiss();
                    }
                });
                Log.d("content", BackendHelper.post.content);
                author.setText(post.author);
                date.setText(post.date);
                try {
                    if (!post.imageURL.equals("") && post.imageURL != null) {
                        Glide.with(context)
                                .load(post.imageURL).asBitmap()
                                .fitCenter().dontAnimate()
                                .into(image);
                    }else {
                        Log.d("sdfsdfsf","fgsdfd");
                        Glide.with(context)
                                .load(R.drawable.thumbnail_default)
                                .fitCenter()
                                .into(image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bar.dismiss();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // Add the request to the RequestQueue.
        mRequestQueue.add(jsonObjectRequest);
    }

    private static String getDate(String date) {
        String ProcessedDate;
        String year = date.substring(0, 4);
        int mnt = Integer.parseInt(date.substring(5, 7));
        String month = "";
        String day = date.substring(8, 10);
        switch (mnt) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
        }
        ProcessedDate = day + " " + month + ", " + year;
        return ProcessedDate;
    }

}