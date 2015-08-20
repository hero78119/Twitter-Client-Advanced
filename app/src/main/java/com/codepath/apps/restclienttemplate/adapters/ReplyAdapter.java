package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by jonaswu on 2015/8/11.
 */
public class ReplyAdapter extends CustomizedAdapter {
    public ReplyAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Twitter twitter = (Twitter) getItem(position);
        User user = twitter.getUser();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.retweet, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.screenname = (TextView) convertView.findViewById(R.id.screenname);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.createTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.createTime.setText(Utils.displayTimeToTweet(twitter.getCreated_at()));
        viewHolder.name.setText(Html.fromHtml(user.getName()));
        viewHolder.screenname.setText("@" + user.getScreen_name());
        viewHolder.text.setText(twitter.getText());
        Picasso.with(context)
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(viewHolder.profile_image);

        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        public TextView text;
        public TextView screenname;
        public TextView name;
        public TextView createTime;
        public ImageView profile_image;
    }
}
