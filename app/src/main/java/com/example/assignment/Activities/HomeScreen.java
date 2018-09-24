package com.example.assignment.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignment.CommonClass.CircleTransform;
import com.example.assignment.CommonClass.Constant;
import com.example.assignment.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG;
    private String quote, length, author, category,
            date, permalink, title, background, id;
    ImageView imageview;
    TextView tv_name, tv_email, tv_quote, tv_author, tv_titile, tv_category, tv_date, tv_id, tv_permalink, tv_background, tv_length, tv_more_info;
    String name, email, image;
    View header;
    LinearLayout ll_moreinfo;
    ArrayList<String> tags_array = new ArrayList<>();
    RecyclerView mrecyclerview;
    TagsAdapter madapter;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        TAG = HomeScreen.class.getName();

        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("userName");
            email = intent.getStringExtra("emailId");
            image = intent.getStringExtra("imageURL");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_home);
        navigationView.setNavigationItemSelectedListener(this);

        header = LayoutInflater.from(this).inflate(R.layout.nav_header_home_screen, null);

        header = navigationView.getHeaderView(0);

        imageview = header.findViewById(R.id.iv_image);
        tv_name = header.findViewById(R.id.tv_title);
        tv_email = header.findViewById(R.id.tv_email);

        tv_quote = findViewById(R.id.quote_value);
        tv_author = findViewById(R.id.author_value);
        tv_titile = findViewById(R.id.title_value);
        tv_category = findViewById(R.id.category_value);
        tv_id = findViewById(R.id.id_value);
        tv_date = findViewById(R.id.date_value);
        tv_permalink = findViewById(R.id.permalink_value);
        tv_length = findViewById(R.id.length_value);
        tv_background = findViewById(R.id.background_value);
        tv_more_info = findViewById(R.id.tv_more_info);
        ll_moreinfo = findViewById(R.id.more_info);
        mrecyclerview = findViewById(R.id.mrecyclerview);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mrecyclerview.setHasFixedSize(true);
        madapter = new TagsAdapter(tags_array, getApplicationContext());
        mrecyclerview.setAdapter(madapter);
        madapter.notifyDataSetChanged();


        tv_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_more_info.setVisibility(View.GONE);
                ll_moreinfo.setVisibility(View.VISIBLE);
            }
        });

        Log.v("params", name + "\n" + email + "\n" + image);

        tv_name.setText(name);
        tv_email.setText(email);
        Glide.with(this).load(image)
                .crossFade()
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_placeholder)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageview);

       /* Glide.with(this).load(image)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageview);
*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        Constant.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Constant.mGoogleApiClient.connect();

        sendAndRequestResponse();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            sendAndRequestResponse();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_home);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            SingOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_home);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SingOut() {
        Auth.GoogleSignInApi.signOut(Constant.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {

                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), LoginScreen.class);
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void sendAndRequestResponse() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);

        String url = "http://quotes.rest/qod.json";
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {

                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONObject jsonObject1 = jsonObject.optJSONObject("contents");

                        JSONArray jsonArray = jsonObject1.optJSONArray("quotes");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject quotes = jsonArray.optJSONObject(i);

                            quote = quotes.optString("quote");
                            length = quotes.optString("length");
                            author = quotes.optString("author");
                            category = quotes.optString("category");
                            date = quotes.optString("date");
                            permalink = quotes.optString("permalink");
                            title = quotes.optString("title");
                            background = quotes.optString("background");
                            id = quotes.optString("id");

                            JSONArray tags = quotes.optJSONArray("tags");

                            tv_quote.setText("\n" + quote);
                            tv_titile.setText("\n" + title);
                            tv_author.setText("\n" + author);
                            tv_background.setText("\n" + background);
                            tv_permalink.setText("\n" + permalink);
                            tv_date.setText("\n" + date);
                            tv_category.setText("\n" + category);
                            tv_id.setText("\n" + id);
                            tv_length.setText("\n" + length);
                            for (int j = 0; j < tags.length(); j++) {
                                String str_tag = tags.get(j).toString();
                                tags_array.add(str_tag);
                            }
                            madapter = new TagsAdapter(tags_array, getApplicationContext());
                            mrecyclerview.setAdapter(madapter);
                            madapter.notifyDataSetChanged();
                            Log.v("tag", tags_array.size() + "\n Length");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i(TAG, "Error :" + error.toString());
            }
        });
        mRequestQueue.add(mStringRequest);
    }

    private class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
        Context context;
        ArrayList<String> tags_list;

        TagsAdapter(ArrayList<String> tags_array, Context applicationContext) {
            this.tags_list = tags_array;
            this.context = applicationContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.tags_layout, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(tags_list.get(position));
        }

        @Override
        public int getItemCount() {
            return tags_list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tags_value);

            }
        }
    }
}