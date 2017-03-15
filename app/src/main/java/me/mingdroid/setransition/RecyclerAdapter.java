package me.mingdroid.setransition;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 3/14/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<ArrayList<Uri>> urisList;
    private SourceActivity.OnSharedViewListener sharedViewListener;

    public RecyclerAdapter(List<ArrayList<Uri>> uriList, SourceActivity.OnSharedViewListener sharedViewListener) {
        this.urisList = uriList;
        this.sharedViewListener = sharedViewListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_view, parent, false);
        return new RecyclerViewHolder(view, sharedViewListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.bindView(urisList.get(position));
    }

    @Override
    public int getItemCount() {
        return urisList.size();
    }

}
