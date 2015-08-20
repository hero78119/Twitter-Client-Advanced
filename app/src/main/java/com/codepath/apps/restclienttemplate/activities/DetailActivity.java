package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.adapters.ReplyAdapter;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.Media;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.interfaces.Reloadable;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/10.
 */
public class DetailActivity extends BaseActivity implements TweetFragment.PostSuccessDelegator, Reloadable {
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getLong("id");
        initDb();
        createView(id);
    }

    private void createView(Long id) {
        Query query = twitterDao.queryBuilder().where(TwitterDao.Properties.Id.eq(id)).build();
        final Twitter twitter = (Twitter) query.list().get(0);
        final User user = twitter.getUser();


        TextView text = (TextView) findViewById(R.id.text);
        TextView screenname = (TextView) findViewById(R.id.screenname);
        TextView name = (TextView) findViewById(R.id.name);
        TextView time = (TextView) findViewById(R.id.time);
        ImageView image = (ImageView) findViewById(R.id.profile_image);
        ImageView reply = (ImageView) findViewById(R.id.reply);
        ImageView retweet = (ImageView) findViewById(R.id.retweet);
        ImageView favorite = (ImageView) findViewById(R.id.favorite);
        ImageView large_image = (ImageView) findViewById(R.id.large_image);

        text.setText(twitter.getText());
        screenname.setText("@" + user.getScreen_name());
        name.setText(user.getName());
        time.setText(Utils.displayTimeToTweet(twitter.getCreated_at()));


        Query queryReply = twitterDao.queryBuilder().where(TwitterDao.Properties.In_reply_to_user_id.eq(twitter.getId())).build();
        List<Twitter> replyTwitters = (List<Twitter>) queryReply.list();
        if (replyTwitters.size() > 0) {
            ReplyAdapter replyAdapter = new ReplyAdapter(this);
            for (Twitter replyTwitter : replyTwitters) {
                replyAdapter.addItem(replyTwitter);
            }
            ListView listView = (ListView) findViewById(R.id.replies);
            listView.setAdapter(replyAdapter);
            LinearLayout ll = (LinearLayout) findViewById(R.id.replies_section);
            ll.setVisibility(View.VISIBLE);
        }

        Picasso.with(this)
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(image);

        if (twitter.getMediaList().size() > 0) {
            Media media = twitter.getMediaList().get(0);
            large_image.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(media.getMedia_url())
                    .error(R.drawable.images)
                    .placeholder(R.drawable.placeholder)
                    .centerInside()
                    .noFade()
                    .resize(600, 600)
                    .into(large_image);

        }

        reply.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         showReplyDialog(twitter.getId());
                                     }
                                 }

        );

        retweet.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           setBusy();
                                           TwitterRestClient client = RestApplication.getRestClient();
                                           client.retweet(twitter.getId(),
                                                   new MyJsonHttpResponseHandler(DetailActivity.this) {
                                                       @Override
                                                       public void successCallBack(int statusCode, Header[] headers, Object data) {
                                                           setFinish();
                                                           reload();
                                                       }

                                                       @Override
                                                       public void errorCallBack() {
                                                           setFinish();
                                                           reload();
                                                       }
                                                   }
                                           );
                                       }
                                   }

        );

        favorite.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            setBusy();
                                            TwitterRestClient client = RestApplication.getRestClient();
                                            client.createFavorite(twitter.getId(),
                                                    new MyJsonHttpResponseHandler(DetailActivity.this) {
                                                        @Override
                                                        public void successCallBack(int statusCode, Header[] headers, Object data) {
                                                            setFinish();
                                                            reload();
                                                        }

                                                        @Override
                                                        public void errorCallBack() {
                                                            setFinish();
                                                            reload();
                                                        }
                                                    }
                                            );
                                        }
                                    }

        );
    }

    @Override
    public void postSuccess(SaveDataToDB saveDataToDB) {

    }

    @Override
    public void reload() {
        createView(id);
    }
}
