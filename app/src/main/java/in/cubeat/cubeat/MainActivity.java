package in.cubeat.cubeat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Context context;
    ProgressDialog bar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ViewPager viewPager;
    TabLayout tabLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        final ViewPager imagePageViewer = (ViewPager) findViewById(R.id.image_page_viewer);
        final ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter();
        imagePageViewer.setAdapter(imagePagerAdapter);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        /*if (collapsingToolbarLayout != null) {
            //collapsingToolbarLayout.setTitle("");
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
            collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));
        }*/
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        assert appBarLayout != null;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d("Total scroll", String.valueOf(appBarLayout.getTotalScrollRange()));
                /*if (verticalOffset == -(appBarLayout.getTotalScrollRange()))
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
                else
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.transparent));*/
                Log.d("Offset", String.valueOf(verticalOffset));
            }
        });
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        toolbar.setTitle(R.string.app_name);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        if (BackendHelper.allPosts == null) {
            bar = new ProgressDialog(context);
            bar.setMessage("Loading");
            bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            bar.setIndeterminate(true);
            bar.setProgress(0);
            bar.setCancelable(false);
            bar.setCanceledOnTouchOutside(false);
            bar.show();
            Log.d("sdgsdfg", "sdfgsdfg");
            BackendHelper.setPosts(context, bar, viewPager, tabLayout, imagePagerAdapter);
        } else {
            imagePagerAdapter.posts = BackendHelper.allPosts;
            imagePagerAdapter.notifyDataSetChanged();
            BackendHelper.setupViewPager(context, viewPager, tabLayout);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        int[][] state = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed

        };

        int[] color = new int[] {
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary
        };
        ColorStateList csl = new ColorStateList(state, color);
        navigationView.setItemTextColor(csl);
        final Menu menuItems = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked())
                    item.setChecked(false);
                else
                    item.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {
                    /*case R.id.item0:
                        BackendHelper.setupViewPager(context,viewPager,tabLayout);
                        menuItems.findItem(R.id.item1).setVisible(true);
                        menuItems.findItem(R.id.item2).setVisible(true);
                        menuItems.findItem(R.id.item3).setVisible(true);
                        menuItems.findItem(R.id.item4).setVisible(true);

                        return true;*/
                    case R.id.item1:
                        navigationView.getMenu().getItem(0).setChecked(false);
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.item2:
                        navigationView.getMenu().getItem(1).setChecked(false);
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.item3:
                        navigationView.getMenu().getItem(2).setChecked(false);
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.item4:
                        navigationView.getMenu().getItem(3).setChecked(false);
                        viewPager.setCurrentItem(3);
                        return true;
                    case R.id.item5:
                        navigationView.getMenu().getItem(4).setChecked(false);
                        viewPager.setCurrentItem(4);
                        /*BackendHelper.setupViewPagerUpcomingEvents(context,viewPager,tabLayout);
                        menuItems.findItem(R.id.item1).setVisible(false);
                        menuItems.findItem(R.id.item2).setVisible(false);
                        menuItems.findItem(R.id.item3).setVisible(false);
                        menuItems.findItem(R.id.item4).setVisible(false);*/
                        return true;
                    case R.id.item7:
                        startActivity(new Intent(MainActivity.this, SearchActivity.class));
                        return true;
                    case R.id.item6:
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        return true;
                    default:
                        return true;
                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.search){
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BackendHelper.mainRunning=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BackendHelper.mainRunning=false;
    }
    /*private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        PostFragment home = new PostFragment();
        PostFragment editorial = new PostFragment();
        PostFragment news = new PostFragment();
        PostFragment cubtalk = new PostFragment();
        viewPagerAdapter.addFrag(home, "Home");
        viewPagerAdapter.addFrag(editorial, "Editorial");
        viewPagerAdapter.addFrag(news, "News Stories");
        viewPagerAdapter.addFrag(cubtalk, "CUB Talk");
        viewPager.setAdapter(viewPagerAdapter);
    }*/

    public class ImagePagerAdapter extends PagerAdapter {

        public ArrayList<PostPreview> posts = new ArrayList<>();

        @Override
        public int getCount() {
            return posts.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final Context context = MainActivity.this;
            View imagePagerItem = getLayoutInflater().inflate(R.layout.image_pager_layout, container, false);
            ImageView imageView = (ImageView) imagePagerItem.findViewById(R.id.image_pager_picture);
            TextView textView = (TextView) imagePagerItem.findViewById(R.id.image_pager_text);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imagePagerItem.setTag("image_pager");
            if(!posts.get(position).imageURL.equals(""))
                Glide.with(context)
                    .load(posts.get(position).imageURL)
                    .fitCenter()
                    .into(imageView);
            else
                Glide.with(context)
                        .load(R.drawable.thumbnail_default)
                        .fitCenter()
                        .into(imageView);
            (container).addView(imagePagerItem, 0);
            textView.setText(posts.get(position).title);
            imagePagerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1=new Intent(context,ViewPostActivity.class);
                    i1.putExtra(BackendHelper.POST_ID_KEY_INTENT, posts.get(position).ID);
                    context.startActivity(i1);
                }
            });
            return imagePagerItem;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }
}
