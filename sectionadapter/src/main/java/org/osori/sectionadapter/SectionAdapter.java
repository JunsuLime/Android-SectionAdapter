package org.osori.sectionadapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by junsu on 2017-05-08.
 *
 * SectionAdapter
 * It works Swift TableView
 *
 * Constraint1: Grid item must have distinguished viewType between other grid item or non-grid item
 * Constraint2: Any section of item can have only one view type
 * Constraint3: If you return NONE_VIEW_TYPE -1, this adapter will recognize this return value as there is no view in section
 */

public abstract class SectionAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SectionAdapter_";

    // Key is viewType and value is viewOption
    private Hashtable<Integer, ViewOption> mViewOptionTable = new Hashtable<>();

    public static final int NONE_VIEW_TYPE = -1;
    public static final int DEFAULT_GRID = 1;

    private Context context;

    public SectionAdapter(Context context) {
        this.context = context;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewOption viewOption = mViewOptionTable.get(viewType);

        if (viewOption == null || viewOption.numberOfGrid == DEFAULT_GRID) {
            return onCreateItemHolder(parent, viewType);
        }
        else {
            // Grid Item 이 들어갈 view holder
            // vertical orientation 만 상정하고 짜져있음
            // TODO: horizontal 도 고려한 코딩을 하자
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            // TODO: row 의 rowLayoutParams 가 안먹히는 문제
            // 현재는 그냥 복사해서 쓰는방식 채택
            if (viewOption.gridItemLayoutParam != null) {
                Log.d(TAG, "set layout params......");
                RecyclerView.LayoutParams holderLp = viewOption.gridItemLayoutParam;
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(holderLp.width, holderLp.height);
                lp.setMargins(holderLp.leftMargin, holderLp.topMargin, holderLp.rightMargin, holderLp.bottomMargin);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    lp.setMarginStart(holderLp.getMarginStart());
                    lp.setMarginEnd(holderLp.getMarginEnd());
                }
                layout.setLayoutParams(lp);
            }
            else {
                layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            int numberOfGrid = viewOption.numberOfGrid;
            List<RecyclerView.ViewHolder> holders = new ArrayList<>();
            for (int i = 0; i < numberOfGrid; i++) {
                RecyclerView.ViewHolder itemHolder = onCreateItemHolder(layout, viewType);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemHolder.itemView.getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.weight = 1f;

                itemHolder.itemView.setLayoutParams(params);
                layout.addView(itemHolder.itemView);
                holders.add(itemHolder);
            }
            return new GridViewHolder(context, layout, holders);
        }
    }

    public abstract RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindItemHolder is called with holder: " + holder);
        IndexPath indexPath = buildIndexPath(position);

        if (holder instanceof GridViewHolder) {
            ((GridViewHolder) holder).bind(indexPath);
        } else {
            onBindItemHolder(holder, indexPath);
        }
    }

    public abstract void onBindItemHolder(RecyclerView.ViewHolder holder, IndexPath indexPath);

    @Override
    public int getItemCount() {
        int itemCount = 0;

        int sectionCount = getSectionCount();
        for (int i = 0; i < sectionCount; i++) {

            // check header
            if (getSectionHeaderViewType(i) != NONE_VIEW_TYPE) itemCount++;

            itemCount += getRowCountInSection(i);

            // check footer
            if (getSectionFooterViewType(i) != NONE_VIEW_TYPE) itemCount++;
        }
        return itemCount;
    }

    public abstract int getSectionCount();

    public abstract int getSectionItemCount(int sectionIndex);

    @Override
    public final int getItemViewType(int position) {
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
            viewType = getSectionItemViewType(sectionIndex);
        }

        ViewOption viewOption = getItemViewOption(viewType);
        if (viewOption != null) {
            // put ViewOption on view type
            if (mViewOptionTable.get(viewType) == null) {
                mViewOptionTable.put(viewType, viewOption);
            }
        }
        return viewType;
    }

    public int getSectionItemViewType(int sectionIndex) {
        return 0;
    }

    public int getSectionHeaderViewType(int sectionIndex) {
        return NONE_VIEW_TYPE;
    }

    public int getSectionFooterViewType(int sectionIndex) {
        return NONE_VIEW_TYPE;
    }

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
            int sectionRowCount = getRowCountInSection(sectionIndex);
            if (cur <= position && position < cur + sectionRowCount) {
                pathSection = sectionIndex;
                ViewOption viewOption = getItemViewOption(getSectionItemViewType(sectionIndex));
                int numberOfGrid = DEFAULT_GRID;
                if (viewOption != null)  numberOfGrid = viewOption.numberOfGrid;
                // Case default grid option
                if (numberOfGrid == DEFAULT_GRID) {
                    pathItem = position - cur;
                }
                // Case ItemHolder has grid option
                else {
                    pathItem = (position - cur) * numberOfGrid;
                }
                break;
            }
            cur = cur + sectionRowCount;

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

    private int getRowCountInSection(int sectionIndex) {
        int itemCount = getSectionItemCount(sectionIndex);
        if (itemCount == 0) return 0;

        // Get view holder info of ItemHolder
        ViewOption viewOption = getItemViewOption(getSectionItemViewType(sectionIndex));
        if (viewOption == null || viewOption.numberOfGrid == DEFAULT_GRID) return itemCount;

        int gridCount = viewOption.numberOfGrid;

        return ((itemCount - 1) / gridCount) + 1;
    }

    public final int getRowPosition(IndexPath indexPath) {
        int sectionCount = getSectionCount();
        int rowCount = 0;
        // wrong indexPath
        if (indexPath.section >= sectionCount) {
            return -1;
        }

        for (int i = 0; i < sectionCount; i++) {

            if (indexPath.section == i) {
                if (getSectionHeaderViewType(i) != NONE_VIEW_TYPE) {
                    if (indexPath.item == SectionAdapter.IndexPath.HEADER) {
                        return rowCount;
                    }
                    rowCount++;
                }

                int itemCount = getSectionItemCount(i);
                // wrong indexPath
                if (indexPath.item >= itemCount) {
                    return -1;
                }

                // Get view holder info of ItemHolder
                ViewOption viewOption = getItemViewOption(getSectionItemViewType(i));
                int numberOfGrid = DEFAULT_GRID;
                if (viewOption != null) numberOfGrid = viewOption.numberOfGrid;

                if (indexPath.item >= 0 && indexPath.item < itemCount) {
                    rowCount += (indexPath.item / numberOfGrid);
                    return rowCount;
                } else if (itemCount != 0) {

                    // Get view holder info of ItemHolder
                    if (numberOfGrid == DEFAULT_GRID) {
                        rowCount += itemCount;
                    }
                    else {
                        rowCount += ((itemCount - 1) / numberOfGrid) + 1;
                    }
                }

                // check footer
                if (getSectionFooterViewType(i) != NONE_VIEW_TYPE) {
                    if (indexPath.item == IndexPath.FOOTER) {
                        return rowCount;
                    }
                    rowCount++;
                }
            }

            // check header
            if (getSectionHeaderViewType(i) != NONE_VIEW_TYPE) rowCount++;

            rowCount += getRowCountInSection(i);

            // check footer
            if (getSectionFooterViewType(i) != NONE_VIEW_TYPE) rowCount++;
        }

        // wrong indexPath
        return -1;
    }

    public ViewOption getItemViewOption(int viewType) {
        return null;
    }

    public static class IndexPath {
        public int section;
        public int item;

        public static int HEADER = -1;
        public static int FOOTER = -2;

        public IndexPath(int section, int item) {
            this.section = section;
            this.item = item;
        }
    }

    /**
     * For selective grid item!
     */

    private class GridViewHolder extends RecyclerView.ViewHolder {

        private List<RecyclerView.ViewHolder> mHolders;

        public GridViewHolder(Context context, View mergedView, List<RecyclerView.ViewHolder> holders) {
            super(mergedView);
            mHolders = holders;
        }

        /**
         * Not used function, onBindItemHolder is created because this class's parent is ItemHolder
         * and all of this adapters view holder must extend ItemHolder
         *
         * @param startIndexPath: first item's indexPath will be passed into this function.
         */
        public void bind(IndexPath startIndexPath) {
            for (RecyclerView.ViewHolder viewHolder : mHolders) {
                if (startIndexPath.item < getSectionItemCount(startIndexPath.section)) {
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                    SectionAdapter.this.onBindItemHolder(viewHolder, startIndexPath);
                    startIndexPath.item += 1;
                } else {
                    viewHolder.itemView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public class ViewOption {
        public int numberOfGrid;
        public RecyclerView.LayoutParams gridItemLayoutParam;

        public ViewOption(int numberOfGrid) {
            this(numberOfGrid, null);
        }

        public ViewOption(int numberOfGrid, RecyclerView.LayoutParams gridItemLayoutParam) {
            this.numberOfGrid = numberOfGrid;
            this.gridItemLayoutParam = gridItemLayoutParam;
        }
    }
}
