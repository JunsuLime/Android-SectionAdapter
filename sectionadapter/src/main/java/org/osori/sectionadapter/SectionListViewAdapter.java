package org.osori.sectionadapter;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Created by junsu on 2017-06-11.
 */

public abstract class SectionListViewAdapter<VH extends SectionListViewAdapter.ViewHolder> implements ListAdapter {

    private static final String TAG = SectionListViewAdapter.class.getSimpleName();

    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    protected final int NONE_VIEW_TYPE = -1;

    /**
     * Enable means that item is selectable and clickable.
     * @return True is all items are enable, false then otherwise
     */

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    /**
     * hmm ...
     * @param position
     * @return
     */

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * IndexPath is main concept of this library.
     * This function build IndexPath by rowPosition.
     *
     * @param position: Row position
     * @return: IndexPath of rowPosition
     */
    public final IndexPath buildIndexPath(int position) {
        int sectionCount = getSectionCount();
        int cur = 0;    // current ref position

        int pathSection = -1;
        int pathItem = -1;

        for (int sectionIndex = 0; sectionIndex < sectionCount; sectionIndex++) {

            // check header
            if (getSectionHeaderViewType(sectionIndex) != NONE_VIEW_TYPE) {
                if (cur == position) {
                    pathSection = sectionIndex;
                    pathItem = IndexPath.HEADER;
                    break;
                }
                cur++;
            }
            // check body
            int sectionItemCount = getSectionItemCount(sectionIndex);
            if (cur <= position && position < cur + sectionItemCount) {
                pathSection = sectionIndex;
                pathItem = position - cur;
                break;
            }
            cur = cur + sectionItemCount;

            // check footer
            if (getSectionFooterViewType(sectionIndex) != NONE_VIEW_TYPE) {
                if (cur == position) {
                    pathSection = sectionIndex;
                    pathItem = IndexPath.FOOTER;
                    break;
                }
                cur++;
            }
        }
        return new IndexPath(pathSection, pathItem);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IndexPath indexPath = buildIndexPath(position);
        int viewType;

        int section = indexPath.section;
        int item = indexPath.item;

        if (item == IndexPath.HEADER) {
            viewType = getSectionHeaderViewType(section);
        }
        else if (item == IndexPath.FOOTER) {
            viewType = getSectionFooterViewType(section);
        }
        else {
            viewType = getSectionItemViewType(section);
        }

        View view = convertView;
        VH holder;

        if (view == null) {
            holder = onCreateItemHolder(parent, viewType);
            holder.itemView.setTag(holder);
            view = holder.itemView;
        }
        else {
            holder = (VH) view.getTag();
        }

        holder.itemView.setTag(holder);
        view = holder.itemView;

        onBindItemHolder((VH) holder, indexPath);

//        Log.d(TAG, "getView returns view: " + view);
        return view;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        IndexPath indexPath = buildIndexPath(position);
        int sectionIndex = indexPath.section;
        int viewType;

        if (getSectionHeaderViewType(sectionIndex) != NONE_VIEW_TYPE
                && indexPath.item == IndexPath.HEADER) {
            viewType = getSectionHeaderViewType(sectionIndex);
        }
        else if (getSectionFooterViewType(sectionIndex) != NONE_VIEW_TYPE
                && indexPath.item == IndexPath.FOOTER) {
            viewType = getSectionFooterViewType(sectionIndex);
        }
        else {
            viewType = getSectionItemViewTypeInternal(sectionIndex);
        }

        return viewType;
    }

    private int getSectionItemViewTypeInternal(int sectionIndex) {
        int viewType = getSectionItemViewType(sectionIndex);
        if (viewType == NONE_VIEW_TYPE) {
            throw new IllegalStateException("Item's view type cannot be NONE_VIEW_TYPE, -1");
        }
        else {
            return viewType;
        }
    }

    @Override
    public final int getCount() {
        int itemCount = 0;

        int sectionCount = getSectionCount();
        for (int i = 0; i < sectionCount; i++) {

            // check header
            if (getSectionHeaderViewType(i) != NONE_VIEW_TYPE) itemCount++;

            itemCount += getSectionItemCount(i);

            // check footer
            if (getSectionFooterViewType(i) != NONE_VIEW_TYPE) itemCount++;
        }
        return itemCount;
    }

    public abstract void onBindItemHolder(VH holder, IndexPath indexPath);

    public abstract VH onCreateItemHolder(ViewGroup container, int viewType);

    public abstract int getSectionCount();

    public abstract int getSectionItemCount(int sectionIndex);

    public abstract int getSectionItemViewType(int sectionIndex);

    public int getSectionHeaderViewType(int sectionIndex) {
        return NONE_VIEW_TYPE;
    }

    public int getSectionFooterViewType(int sectionIndex) {
        return NONE_VIEW_TYPE;
    }


    public abstract class ViewHolder {
        public View itemView;
        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
