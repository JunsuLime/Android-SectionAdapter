package org.osori.samples;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.osori.sectionadapter.SectionAdapter;

import java.util.List;

/**
 * Created by junsu
 */

public class SampleAdapter extends SectionAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    // first section's item list
    // it is define at MainActivity.
    private List<String> mItemList;

    // for convenience section index is defined as final variable
    private final int SECTION_FIRST = 0;
    private final int SECTION_SECOND = 1;

    // for convenience view type is defined as final variable
    private final int ITEM_HEADER = 0;
    private final int ITEM_FOOTER = 1;
    private final int ITEM_BODY_FIRST = 2;
    private final int ITEM_BODY_SECOND = 3;

    public SampleAdapter(Context context, List<String> itemList) {
        super(context);
        mContext = context;

        // get inflater to inflate in onCreateItemHolder.
        mInflater = LayoutInflater.from(context);

        mItemList = itemList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_BODY_FIRST:
                return new FirstBodyViewHolder(mInflater.inflate(R.layout.item_body_first, parent ,false));
            case ITEM_BODY_SECOND:
                return new SecondBodyViewHolder(mInflater.inflate(R.layout.item_body_second, parent, false));
            case ITEM_HEADER:
                return new HeaderViewHolder(mInflater.inflate(R.layout.item_header, parent, false));
            case ITEM_FOOTER:
                return new FooterViewHolder(mInflater.inflate(R.layout.item_footer, parent, false));
        }
        return null;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, IndexPath indexPath) {
        int section = indexPath.section;
        int item = indexPath.item;

        if (holder instanceof FirstBodyViewHolder) {
            ((FirstBodyViewHolder) holder).bind(mItemList.get(item));
        }
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(section);
        }
    }

    @Override
    public int getSectionCount() {
        return 2;
    }

    @Override
    public int getSectionItemViewType(int sectionIndex) {
        switch (sectionIndex) {
            case SECTION_FIRST:
                return ITEM_BODY_FIRST;
            case SECTION_SECOND:
                return ITEM_BODY_SECOND;
        }
        return NONE_VIEW_TYPE;
    }

    @Override
    public int getSectionHeaderViewType(int sectionIndex) {
        switch (sectionIndex) {
            case SECTION_FIRST:
                return ITEM_HEADER;
            case SECTION_SECOND:
                return ITEM_HEADER;
        }
        return NONE_VIEW_TYPE;
    }

    @Override
    public int getSectionFooterViewType(int sectionIndex) {
        switch (sectionIndex) {
            case SECTION_FIRST:
                return NONE_VIEW_TYPE;
            case SECTION_SECOND:
                return ITEM_FOOTER;
        }
        return NONE_VIEW_TYPE;
    }

    @Override
    public int getSectionItemCount(int sectionIndex) {
        switch (sectionIndex) {
            case SECTION_FIRST:
                return mItemList.size();
            case SECTION_SECOND:
                return 1;
        }
        return 0;
    }

    @Override
    public ViewOption getItemViewOption(int viewType) {
        if (viewType == ITEM_BODY_FIRST) {
            return new ViewOption(2);
        }
        return null;
    }

    private class FirstBodyViewHolder extends RecyclerView.ViewHolder {

        TextView bodyText;
        ImageView bodyImage;
        public FirstBodyViewHolder(View itemView) {
            super(itemView);
            bodyText = (TextView) itemView.findViewById(R.id.body_first_text);
            bodyImage = (ImageView) itemView.findViewById(R.id.body_first_image);
        }

        public void bind(String stringValue) {
            bodyText.setText(stringValue);
            bodyImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher_round));
        }
    }

    private class SecondBodyViewHolder extends RecyclerView.ViewHolder {

        public SecondBodyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.header_text);
        }

        public void bind(int position) {
            headerText.setText(String.format("%d section header", position));
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
