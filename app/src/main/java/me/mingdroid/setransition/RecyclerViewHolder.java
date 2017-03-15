package me.mingdroid.setransition;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by huang on 3/14/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private SourceActivity.OnSharedViewListener sharedViewListener;
    private Context context;
    private TextView titleView;
    private ImageView[] photoViews = new ImageView[6];
    private ArrayList<Uri> uris;

    public RecyclerViewHolder(View itemView, SourceActivity.OnSharedViewListener sharedViewListener) {
        super(itemView);
        this.sharedViewListener = sharedViewListener;
        context = itemView.getContext();
        titleView = (TextView) itemView.findViewById(R.id.title);
        photoViews[0] = (ImageView) itemView.findViewById(R.id.photo0);
        photoViews[0].setOnClickListener(this);
        photoViews[1] = (ImageView) itemView.findViewById(R.id.photo1);
        photoViews[1].setOnClickListener(this);
        photoViews[2] = (ImageView) itemView.findViewById(R.id.photo2);
        photoViews[2].setOnClickListener(this);
        photoViews[3] = (ImageView) itemView.findViewById(R.id.photo3);
        photoViews[3].setOnClickListener(this);
        photoViews[4] = (ImageView) itemView.findViewById(R.id.photo4);
        photoViews[4].setOnClickListener(this);
        photoViews[5] = (ImageView) itemView.findViewById(R.id.photo5);
        photoViews[5].setOnClickListener(this);
    }

    public void bindView(ArrayList<Uri> uris) {
        this.uris = uris;
        titleView.setText("item" + getAdapterPosition());
        for (int i = 0; i < uris.size() && i < photoViews.length; i++) {
            photoViews[i].setImageURI(uris.get(i));
            photoViews[i].setTag(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                photoViews[i].setTransitionName(
                        context.getString(R.string.transition_name, getAdapterPosition(), i));
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, DestinationActivity.class);
        intent.putParcelableArrayListExtra("uris", uris);
        intent.putExtra("adapter_position", getAdapterPosition());
        intent.putExtra("current", (int) v.getTag());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    (AppCompatActivity) context, v, v.getTransitionName());
            ((AppCompatActivity) context).startActivityForResult(intent, 0, options.toBundle());
            sharedViewListener.onSharedViewListener(photoViews, (int) v.getTag());
        } else {
            context.startActivity(intent);
        }
    }

}
