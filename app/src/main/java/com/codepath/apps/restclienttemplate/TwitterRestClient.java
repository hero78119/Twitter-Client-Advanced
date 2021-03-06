package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterRestClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "hfM1SijslGZKX4ahAUXIYF2zi";       // Change this
    public static final String REST_CONSUMER_SECRET = "qBgjDCNRiUBQ8m7AYOAZm2shUwNR0JK81PpVzLDK2nT2nCcznO"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://jonasimplements"; // Change this (here and in manifest)

    public TwitterRestClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    public void getInterestingnessList(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("format", "json");
        client.get(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */


    public void getHomeTimeline(Long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            Log.e("max_id", String.valueOf(maxId));
            params.put("max_id", String.valueOf(maxId));
        }
        params.put("count", String.valueOf(20));
        getClient().get(apiUrl, params, handler);
    }


    public void getMentionTimeline(Long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            Log.e("max_id", String.valueOf(maxId));
            params.put("max_id", String.valueOf(maxId));
        }
        params.put("count", String.valueOf(20));
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(Long userId, String screenName, Long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        if (maxId != null) {
            Log.e("max_id", String.valueOf(maxId));
            params.put("max_id", String.valueOf(maxId));
        }
        params.put("user_id", userId);
        params.put("screen_name", screenName);
        params.put("count", String.valueOf(20));
        getClient().get(apiUrl, params, handler);
    }

    public void getUser(Long userId, String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);
    }

    public void getcredentials(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

    // RestClient.java
    public void postTweet(String body, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", body);
        getClient().post(apiUrl, params, handler);
    }

    // RestClient.java
    public void postReply(String body, Long in_reply_to_status_id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", body);
        params.put("in_reply_to_status_id", String.valueOf(in_reply_to_status_id));
        getClient().post(apiUrl, params, handler);
    }


    // RestClient.java
    public void createFavorite(Long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(apiUrl, params, handler);
    }


    // RestClient.java
    public void retweet(Long id, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/retweet/" + String.valueOf(id) + ".json");
        RequestParams params = new RequestParams();
        getClient().post(apiUrl, params, handler);
    }


    // RestClient.java
    public void search(String q, Long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        params.put("q", q);
        params.put("maxId", String.valueOf(maxId));
        params.put("count", String.valueOf(20));
        getClient().get(apiUrl, params, handler);
    }

    // RestClient.java
    public void getDirectMessages(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("direct_messages.json");
        RequestParams params = new RequestParams();
        getClient().get(apiUrl, params, handler);
    }

}