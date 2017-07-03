package org.osori.sectionadapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by junsu on 2017-06-09.
 *
 * ViewHolder pattern 의 사용 이유
 * findViewById 의 사용을 줄여서 성능을 높이자!
 */

public abstract class SectionListViewAdapter_tmp<VH extends SectionListViewAdapter_tmp.ViewHolder> extends BaseAdapter {

    private static final String TAG = SectionListViewAdapter_tmp.class.getSimpleName();
    protected final int NONE_VIEW_TYPE = -1;

    // What is it??
    @Override
    public Object getItem(int position) {
        Log.d(TAG, "getItem is called with position: " + position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItmeId is called with position: " + position);
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
        Log.d(TAG, "getView is called with convertView: " + convertView);
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

//        Log.d(TAG, "build indexPath section: " + section + "  item:" + item);
//        Log.d(TAG, "item viewType: " + viewType);

        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            holder = onCreateItemHolder(parent, viewType);
            holder.itemView.setTag(holder);
            view = holder.itemView;
            Log.d(TAG, "convertView null case ... holder: " + holder + "  itemView: " + view);
        }
        else {
            holder = (ViewHolder) view.getTag();
            Log.d(TAG, "convertView exist case ... holder: " + holder + "  itemView: " + view);
        }
//        holder = onCreateItemHolder(parent, viewType);
//        holder.itemView.setTag(holder);
//        view = holder.itemView;

        onBindItemHolder((VH) holder, indexPath);

//        Log.d(TAG, "getView returns view: " + view);
        return view;
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

    public abstract VH onCreateItemHolder(ViewGroup parent, int viewType);

    public abstract void onBindItemHolder(VH holder, IndexPath indexPath);

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
