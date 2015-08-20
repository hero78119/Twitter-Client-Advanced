package com.codepath.apps.restclienttemplate.lib;

import android.content.Context;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jonaswu on 2015/8/11.
 */
public abstract class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {

    private Context context;

    public MyJsonHttpResponseHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray data) {
        successCallBack(statusCode, headers, data);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject data) {
        successCallBack(statusCode, headers, data);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, JSONObject data) {
        Toast.makeText(context, context.getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();
        errorCallBack();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, JSONArray data) {
        Toast.makeText(context, context.getResources().getString(R.string.networkerror), Toast.LENGTH_LONG).show();
        errorCallBack();
    }

    public abstract void successCallBack(int statusCode, Header[] headers, Object data);

    public abstract void errorCallBack();
}
