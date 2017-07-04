package org.osori.samples;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.osori.sectionadapter.IndexPath;
import org.osori.sectionadapter.SectionListViewAdapter;

/**
 * Created by junsu on 2017-07-03.
 */

public class TestAdapter extends SectionListViewAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    private final int VIEW_TYPE_FIRST_BODY = 0;
    private final int VIEW_TYPE_HEADER = 1;

    TestAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindItemHolder(ViewHolder holder, IndexPath indexPath) {
        if (holder instanceof BodyHolder) {
            ((BodyHolder) holder).bind(indexPath.item);
        } else if (holder instanceof HeaderHolder) {
            //
        }
    }

    @Override
    public ViewHolder onCreateItemHolder(ViewGroup container, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FIRST_BODY:
                return new BodyHolder(mInflater.inflate(R.layout.item_body_first, container, false));
            case VIEW_TYPE_HEADER:
                return new HeaderHolder(mInflater.inflate(R.layout.item_header, container, false));
        }
        return null;
    }

    @Override
    public int getSectionCount() {
        return 1;
    }

    @Override
    public int getSectionHeaderViewType(int sectionIndex) {
        return VIEW_TYPE_HEADER;
    }

    @Override
    public int getSectionItemCount(int sectionIndex) {
        return 50;
    }

    @Override
    public int getSectionItemViewType(int sectionIndex) {
        return 0;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    class HeaderHolder extends ViewHolder {
        TextView headerText;
        public HeaderHolder(View itemView) {
            super(itemView);

            headerText = (TextView) itemView.findViewById(R.id.header_text);
            headerText.setTag("test");
        }
    }

    class BodyHolder extends ViewHolder {

        ImageView bodyImage;
        TextView bodyText;

        public BodyHolder(View itemView) {
            super(itemView);
            bodyImage = (ImageView) itemView.findViewById(R.id.body_first_image);
            bodyText = (TextView) itemView.findViewById(R.id.body_first_text);
        }

        public void bind(int itemIndex) {
            bodyImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher_round));
            bodyText.setText(String.valueOf(itemIndex));
        }
    }
}
