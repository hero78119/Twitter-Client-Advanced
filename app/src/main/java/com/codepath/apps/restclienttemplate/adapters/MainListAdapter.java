package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.BaseActivity;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/10.
 */
public class MainListAdapter extends CustomizedAdapter {


    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView screenname;
        TextView text;
        TextView retweet_count;
        ImageView profile_image;
        TextView createTime;
        public TextView favorite_count;
        public ImageView reply;
    }

    public MainListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Twitter twitter = (Twitter) getItem(position);
        final User user = twitter.getUser();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.post, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.screenname = (TextView) convertView.findViewById(R.id.screenname);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.createTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            viewHolder.retweet_count = (TextView) convertView.findViewById(R.id.retweet_count);
            viewHolder.favorite_count = (TextView) convertView.findViewById(R.id.favorite_count);
            viewHolder.reply = (ImageView) convertView.findViewById(R.id.reply);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Date createTime = twitter.getCreated_at();
        viewHolder.createTime.setText(Utils.getBestTimeDiff(createTime));
        viewHolder.name.setText(Html.fromHtml(user.getName()));
        viewHolder.screenname.setText("@" + user.getScreen_name());
        viewHolder.text.setText(twitter.getText());
        viewHolder.text.setMovementMethod(null);
        viewHolder.retweet_count.setText(String.valueOf(twitter.getRetweet_count()));
        viewHolder.favorite_count.setText(String.valueOf(twitter.getFavorite_count()));
        Picasso.with(context)
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(viewHolder.profile_image);
        viewHolder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("userId", user.getId());
                bundle.putString("screenName", user.getScreen_name());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        viewHolder.reply.setOnClickListener(new View.OnClickListener()

                                            {
                                                @Override
                                                public void onClick(View v) {
                                                    ((BaseActivity) context).
                                                            showReplyDialog(twitter.getId());
                                                }
                                            }

        );
        // Return the completed view to render on screen
        return convertView;
    }
}
