package com.example.antonioposabella.reactivextest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Subscription subscription;
    private String user = "{\n" +
             "    \"id\":\"6253282\",\n" +
             "    \"name\":\"Twitter API\",\n" +
             "    \"screen_name\":\"name1\",\n" +
             "    \"url\":\"http://dev.twitter.com\",\n" +
             "    \"followers_count\":1004641,\n" +
             "    \"friends_count\":33,\n" +
             "    \"favourites_count\":24,\n" +
             "    \"statuses_count\":3277\n" +
             "  },\n" +
             "  {\n" +
             "    \"id\":\"15082387\",\n" +
             "    \"name\":\"Anthony\",\n" +
             "    \"screen_name\":\"Nicepose\",\n" +
             "    \"url\":\"www.test.com\",\n" +
             "    \"followers_count\":241,\n" +
             "    \"friends_count\":302,\n" +
             "    \"favourites_count\":41,\n" +
             "    \"statuses_count\":1876\n" +
             "  }";

    private static final String TAG = "MainActivity";

    //To avoid memory leak
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
           subscription = getGistObservable()
                   .subscribeOn(Schedulers.io())// To use with http request
                   .observeOn(AndroidSchedulers.mainThread())// To use with http request
                   .subscribe(new Subscriber<TwitterUsers>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "onError: ",  e);

                }

                @Override
                public void onNext(TwitterUsers twitterUsers) {
                    Log.d(TAG, "onNext: " + twitterUsers.toString());

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private TwitterUsers getGist() throws IOException {

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(user));
        reader.setLenient(true);
        TwitterUsers users = gson.fromJson(reader,TwitterUsers.class);
        return users;
    }

    public Observable<TwitterUsers> getGistObservable() throws IOException {
        //return Observable.just(getGist()); // using only this with http request can block ui
        return Observable.defer(new Func0<Observable<TwitterUsers>>() {
            @Override
            public Observable<TwitterUsers> call() {
                try {
                    return Observable.just(getGist());
                } catch (IOException e) {
                    return null;
                }
            }
        });
    }

}

