package com.nwhacksjss.android.nwhacks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Coordinates;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private HashMap<Long, LatLng> tweetIdCoordinates= new HashMap<>();
    private ArrayList<LatLng> tweetCoords;
    private LinearLayout linearLayout;
    private Long lastSinceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tweetCoords = new ArrayList<LatLng>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Button goToMap = findViewById(R.id.button_id);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(getApplicationContext(), GoogleMapsActivity.class);
                mapIntent.putExtra("tweet_map", tweetIdCoordinates);
                startActivity(mapIntent);
            }
        });
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

        Geocode currentLocation = new Geocode(49.2606, -123.2460, 100, Geocode.Distance.MILES);

        //Geocode currentLocation = GoogleMapsActivity.getCurrentLocation();

        startAPIClient(currentLocation);
    }

    private void startAPIClient(Geocode currentLocation) {
        TwitterCore twitterCore = TwitterCore.getInstance();
        TwitterApiClient client  = twitterCore.getApiClient();
        SearchService searchService = client.getSearchService();

        //private void makeSearchCall()
        /*
        Call<Search> firstCall = searchService.tweets("", null, null, null, "popular", 1, "2018-01-01", null, null, null);

        firstCall.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                Tweet tweet = response.body().tweets.get(0);
                lastSinceId = tweet.getId();
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                Toast.makeText(FeedActivity.this, "Can not find tweets near you", Toast.LENGTH_SHORT).show();
            }
        });*/

        Call<Search> secondCall = searchService.tweets("", currentLocation, null, null, null, 100, null, null, null, null);

        secondCall.enqueue(new Callback<Search>() {
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


        if (tweets.isEmpty()) {
            Toast.makeText(FeedActivity.this, "No new tweets near you", Toast.LENGTH_SHORT).show();
        } else {
            for (Tweet tweet : tweets) {
                TweetView tweetView = new TweetView(FeedActivity.this, tweet);
                linearLayout.addView(tweetView);

                if (tweet.coordinates != null) {
                    LatLng coords = new LatLng(tweet.coordinates.getLatitude(), tweet.coordinates.getLongitude());

                    tweetIdCoordinates.put(tweet.id, coords);
                }
            }
        }
    }

    public void getTweetWithCoordinates() {
        // TODO
    }
}
