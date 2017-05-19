package org.osori.samples;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.osori.sectionadapter.SectionAdapter;

/**
 * Created by junsu on 2017-05-19.
 */

public class SampleAdapter extends SectionAdapter {

    public SampleAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, IndexPath indexPath) {

    }

    @Override
    public int getSectionCount() {
        return 0;
    }

    @Override
    public int getSectionItemCount(int sectionIndex) {
        return 0;
    }
}
