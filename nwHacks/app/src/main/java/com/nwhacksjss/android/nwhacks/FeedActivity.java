package com.nwhacksjss.android.nwhacks;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    List<Tweet> tweets;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

//        //NOT SURE WHAT THIS DOES YET
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        /*
        // We can possibly use an action button on the feed later on.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        linearLayout = findViewById(R.id.feed_layout);

        Geocode currentLocation = new Geocode(49.2606, 123.2460, 1000, Geocode.Distance.KILOMETERS);

        //Geocode currentLocation = GoogleMapsActivity.getCurrentLocation();

        startAPIClient(currentLocation);
    }

    private void startAPIClient(Geocode currentLocation) {
        TwitterCore twitterCore = TwitterCore.getInstance();
        TwitterApiClient client  = twitterCore.getApiClient();
        SearchService searchService = client.getSearchService();
        Call<Search> call = searchService.tweets("", currentLocation, null, null, "recent", 10, null, null, null, null);

        call.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                parseSearchResponse(response);
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                Toast.makeText(FeedActivity.this, "Can not find tweets near you", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseSearchResponse(Response<Search> response) {
        Search results = response.body();
        List<Tweet> tweets = results.tweets;

        for(Tweet tweet : tweets) {
            // Do something here
            //layout.addView
            TweetView tweetView = new TweetView(FeedActivity.this, tweet);
            linearLayout.addView(tweetView);
        }
    }
}
