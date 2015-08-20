package com.codepath.apps.restclienttemplate.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.DaoMaster;
import com.codepath.apps.restclienttemplate.dao.DaoSession;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.interfaces.Progressable;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import de.greenrobot.dao.query.Query;


public class TweetFragment extends DialogFragment implements TextView.OnKeyListener {

    private TwitterDao twitterDao;
    private UserDao userDao;
    private CurrentUserDao currentUserDao;
    private MediaDao mediaDao;

    public PostSuccessDelegator getPostSuccessDelegator() {
        return postSuccessDelegator;
    }

    public void setPostSuccessDelegator(PostSuccessDelegator postSuccessDelegator) {
        this.postSuccessDelegator = postSuccessDelegator;
    }

    public Progressable getProgressableDelegator() {
        return ProgressableDelegator;
    }

    public void setProgressableDelegator(Progressable progressableDelegator) {
        ProgressableDelegator = progressableDelegator;
    }

    public interface PostSuccessDelegator {
        public void postSuccess(SaveDataToDB saveDataToDB);
    }

    private CurrentUser currentUser;
    private Long replyTo;
    private TextView screenname;
    private TextView name;
    private EditText body;
    private TextView length;
    private ImageView image;
    private PostSuccessDelegator postSuccessDelegator;
    private Progressable ProgressableDelegator;


    public static TweetFragment newInstanceAsReply(CurrentUser currentUser, PostSuccessDelegator postSuccessDelegator, Long replyTo) {
        TweetFragment frag = new TweetFragment();
        frag.setCurrentUser(currentUser);
        frag.setPostSuccessDelegator(postSuccessDelegator);
        frag.setProgressableDelegator((Progressable) (postSuccessDelegator));
        frag.setReplyTo(replyTo);
        return frag;
    }

    public static TweetFragment newInstanceAsPostNewTweet(CurrentUser currentUser, PostSuccessDelegator postSuccessDelegator) {
        TweetFragment frag = new TweetFragment();
        frag.setCurrentUser(currentUser);
        frag.setPostSuccessDelegator(postSuccessDelegator);
        frag.setProgressableDelegator((Progressable) (postSuccessDelegator));
        return frag;

    }

    protected void initDb() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "greendao", null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        twitterDao = daoSession.getTwitterDao();
        userDao = daoSession.getUserDao();
        currentUserDao = daoSession.getCurrentUserDao();
        mediaDao = daoSession.getMediaDao();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Query<Twitter> query;
        final Twitter twitter;
        final User user;

        initDb();
        // getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_new_tweet, null);
        CurrentUser currentUser = getCurrentUser();

        screenname = (TextView) view.findViewById(R.id.screenname);
        name = (TextView) view.findViewById(R.id.name);
        length = (TextView) view.findViewById(R.id.length);
        body = (EditText) view.findViewById(R.id.body);
        image = (ImageView) view.findViewById(R.id.profile_image);

        screenname.setText("@" + currentUser.getScreen_name());
        name.setText(currentUser.getName());
        body.setOnKeyListener(this);
        Picasso.with(getActivity())
                .load(currentUser.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(image);

        if (replyTo != null) {
            query = twitterDao.queryBuilder().where(TwitterDao.Properties.Id.eq(replyTo)).build();
            twitter = (Twitter) query.list().get(0);
            user = twitter.getUser();
            body.setText("@" + user.getScreen_name() + " ");
            body.setSelection(body.getText().length());
        }

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle("Write down your feeling")
                .setPositiveButton("Tweet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                TwitterRestClient client = RestApplication.getRestClient();
                                ProgressableDelegator.setBusy();
                                if (replyTo == null) {

                                    client.postTweet(body.getText().toString(),
                                            new MyJsonHttpResponseHandler(getActivity()) {
                                                @Override
                                                public void successCallBack(int statusCode, Header[] headers, Object data) {
                                                    postSuccessDelegator.postSuccess(new SaveDataToDB(true, null, null));
                                                    ProgressableDelegator.setFinish();
                                                }

                                                @Override
                                                public void errorCallBack() {
                                                    ProgressableDelegator.setFinish();
                                                }
                                            }
                                    );
                                } else {

                                    client.postReply(body.getText().toString(), getReplyTo(),
                                            new MyJsonHttpResponseHandler(getActivity()) {

                                                @Override
                                                public void successCallBack(int statusCode, Header[] headers, Object data) {
                                                    postSuccessDelegator.postSuccess(new SaveDataToDB(true, null, null));
                                                    ProgressableDelegator.setFinish();
                                                }

                                                @Override
                                                public void errorCallBack() {
                                                    ProgressableDelegator.setFinish();

                                                }
                                            }
                                    );
                                }
                            }
                        }
                )
                .setView(view);

        return b.create();
    }


    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        length.setText(String.valueOf(body.getText().toString().length()) + "/" + getActivity().getResources().getString(R.string.maxlenghtofatweet));
        return false;
    }
}