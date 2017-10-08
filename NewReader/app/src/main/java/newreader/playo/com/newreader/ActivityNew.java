package newreader.playo.com.newreader;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import newreader.playo.com.newreader.adapter.AdapterNews;
import newreader.playo.com.newreader.model.Hits;
import newreader.playo.com.newreader.model.News;
import newreader.playo.com.newreader.rest.ApiInterface;
import newreader.playo.com.newreader.rest.RestAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityNew extends AppCompatActivity {


    View view;
    private RecyclerView recyclerView;
    private AdapterNews adapterChannel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<News> callbackCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);
        view = findViewById(android.R.id.content);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        adapterChannel = new AdapterNews(this, new ArrayList<Hits>());
        recyclerView.setAdapter(adapterChannel);

        // on item list clicked
        adapterChannel.setOnItemClickListener(new AdapterNews.OnItemClickListener() {
            @Override
            public void onItemClick(Hits obj) {
                Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                intent.putExtra("url", obj.url);
                startActivity(intent);
            }


        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callbackCall != null && callbackCall.isExecuted()) {
                    callbackCall.cancel();
                }
                requestAction();
            }
        });

        requestAction();
        setupToolbar();

    }

    public void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void displayApiResult(final List<Hits> channels) {
        adapterChannel.insertData(channels);
        swipeProgress(false);
        if (channels.size() == 0) {
            showNoItemView(true);
        }
    }

    private void requestPostApi() {
        ApiInterface apiInterface = RestAdapter.createAPI();
        callbackCall = apiInterface.getAllNewInfo();
        callbackCall.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                News resp = response.body();
                if (resp != null) {
                    displayApiResult(resp.hits);
                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {


            }

        });
    }


    private void requestAction() {
        showFailedView(false, "");
        showNoItemView(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPostApi();
            }
        }, Config.DELAY_TIME);
    }

    private void showFailedView(boolean show, String message) {
        View view = (View) findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

    private void showNoItemView(boolean show) {
        View view = (View) findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_message)).setText(R.string.no_post_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            return;
        }
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(show);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }


}
