package org.osori.sectionadapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by junsuLime
 *
 * SectionAdapter
 * Concept of section is came from Swift TableView.
 *
 * Concept of Row and Item
 *
 * Row: Raw position at RecyclerView.Adapter
 * Item: Item what you deal with
 * Section: Section contains item.
 *
 * Constraint1: Grid item must have distinguished viewType between other grid item or non-grid item
 * Constraint2: Any section of item can have only one view type
 * Constraint3: If you return NONE_VIEW_TYPE -1, this adapter will recognize this return value as there is no view in section
 * Constraint4: Current version of SectionAdapter can be adapted at orientation vertical
 */

public abstract class SectionAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SectionAdapter_";

    // Key is viewType and value is viewOption
    private Hashtable<Integer, ViewOption> mViewOptionTable = new Hashtable<>();

    // Header or footer view type can be NONE_VIEW_TYPE, if you want not to put header or footer.
    public static final int NONE_VIEW_TYPE = -1;
    public static final int DEFAULT_GRID = 1;

    // Context to create inflate GridViewHolder
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
            // # Constraint4
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            // copy viewOption's layout params
            if (viewOption.rowItemLayoutParam != null) {
                RecyclerView.LayoutParams holderLp = viewOption.rowItemLayoutParam;
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
                params.weight = 1f;

                itemHolder.itemView.setLayoutParams(params);
                layout.addView(itemHolder.itemView);
                holders.add(itemHolder);
            }
            return new GridViewHolder(context, layout, holders);
        }
    }

    /**
     * As RecyclerView.Adapter, onCreateItemHolder's param is parent and viewType
     * Usage of this function is same as RecyclerView.Adapter's onCreateViewHolder
     *
     * @param parent: Container of itemView
     * @param viewType: Same as RecyclerView viewType. It's defined by user
     * @return ViewHolder what you want to return
     */
    public abstract RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IndexPath indexPath = buildIndexPath(position);

        // Case of GridViewHolder, onBindItemHolder is called inside of GridViewHolder

        if (holder instanceof GridViewHolder) {
            ((GridViewHolder) holder).bind(indexPath);
        } else {
            onBindItemHolder(holder, indexPath);
        }
    }

    /**
     * It's similar to RecyclerView.Adapter's onBindViewHolder.
     * But this function pass parameter IndexPath, not position.
     *
     * IndexPath has two attributes
     * 1) section
     * 2) item
     *
     * @param holder: Holder that you made at onCreateItemHolder
     * @param indexPath: holder's position
     */
    public abstract void onBindItemHolder(RecyclerView.ViewHolder holder, IndexPath indexPath);

    @Override
    public final void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof GridViewHolder) {
            for (RecyclerView.ViewHolder itemHolder : ((GridViewHolder) holder).mHolders) {
                onItemRecycled(itemHolder);
            }
        }
        else {
            onItemRecycled(holder);
        }
    }

    /**
     * It's same as RecyclerView.Adapter's onViewRecycled.
     * @param holder: Holder that recycled.
     */
    public void onItemRecycled(RecyclerView.ViewHolder holder) {
        return;
    }

    /**
     * All row count in RecyclerView, not item count.
     * Its name is getItemCount, because RecyclerView.Adapter's original function name is getItemCount ..
     *
     * @return: Row count
     */
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

    /**
     * Number of section what you want to create
     * @return: Section count
     */
    public abstract int getSectionCount();

    /**
     * Get item count of section
     * @param sectionIndex: Section index
     * @return: Section's item count
     */
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
            viewType = getSectionItemViewTypeInternal(sectionIndex);
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

    /**
     * Internal function to get section item type.
     * This function check item view type which view type is NONE_VIEW_TYPE.
     * If it is NONE_VIEW_TYPE, throw Exception.
     */
    private int getSectionItemViewTypeInternal(int sectionIndex) {
        int viewType = getSectionItemViewType(sectionIndex);
        if (viewType == NONE_VIEW_TYPE) {
            throw new IllegalStateException("Item's view type cannot be NONE_VIEW_TYPE, -1");
        }
        else {
            return viewType;
        }
    }

    /**
     * Section Item's viewType is restricted to only one viewType.
     * Define sectionItem's viewType in here.
     * This value will be transferred to onCreateItemHolder.
     * @param sectionIndex: Section index what you want to define viewType
     * @return: Item viewType
     */
    public abstract int getSectionItemViewType(int sectionIndex);

    /**
     * Section Header's viewType is restricted to only one viewType.
     * Define sectionItem's viewType in here.
     * This value will be transferred to onCreateItemHolder.
     * @param sectionIndex: Section index what you want to define viewType
     * @return: Header viewType
     */
    public int getSectionHeaderViewType(int sectionIndex) {
        return NONE_VIEW_TYPE;
    }

    /**
     * Section Item's viewType is restricted to only one viewType.
     * Define sectionItem's viewType in here.
     * This value will be transferred to onCreateItemHolder.
     * @param sectionIndex: Section index what you want to define viewType
     * @return: Footer viewType
     */
    public int getSectionFooterViewType(int sectionIndex) {
        return NONE_VIEW_TYPE;
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
            int sectionRowCount = getRowCountInSection(sectionIndex);
            if (cur <= position && position < cur + sectionRowCount) {
                pathSection = sectionIndex;
                ViewOption viewOption = getItemViewOption(getSectionItemViewTypeInternal(sectionIndex));
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
        ViewOption viewOption = getItemViewOption(getSectionItemViewTypeInternal(sectionIndex));
        if (viewOption == null || viewOption.numberOfGrid == DEFAULT_GRID) return itemCount;

        int gridCount = viewOption.numberOfGrid;

        return ((itemCount - 1) / gridCount) + 1;
    }

    /**
     * To provide row index for user, make this function.
     * When user user LayoutManager, row count is needed
     *
     * @param indexPath indexPath that you want to access
     * @return row index for indexPath
     */
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
                ViewOption viewOption = getItemViewOption(getSectionItemViewTypeInternal(i));
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

    /**
     * This option is related with grid attribute.
     * You can select section that have grid option by using this function.
     *
     * ViewType has two attribute,
     * 1) numberOfGrid
     * 2) Row layout params
     *
     * @param viewType: The viewType what you want to modify
     * @return: ViewOption of that viewType
     */
    public ViewOption getItemViewOption(int viewType) {
        return null;
    }

    /**
     * IndexPath
     *
     * Concept of IndexPath is came from Swift TableView.
     * It have big Section in table and
     * Table contains Item.
     *
     * section is index of section and
     * item is index of item.
     */
    public static class IndexPath {
        public int section;
        public int item;

        // Header's item value
        public static int HEADER = -1;
        // Footer's item value
        public static int FOOTER = -2;

        public IndexPath(int section, int item) {
            this.section = section;
            this.item = item;
        }
    }

    /**
     * For selective grid item!
     * numberOfGrid != 1 (DEFAULT_GRID)
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


    /**
     * To handle grid option of item
     *
     * numberOfGrid: How many items will be contained at one row
     * rowLayoutParam: row's layoutParams, not item...
     */
    public class ViewOption {
        public int numberOfGrid;
        public RecyclerView.LayoutParams rowItemLayoutParam;

        public ViewOption(int numberOfGrid) {
            this(numberOfGrid, null);
        }

        public ViewOption(int numberOfGrid, RecyclerView.LayoutParams rowLayoutParam) {
            this.numberOfGrid = numberOfGrid;
            this.rowItemLayoutParam = rowLayoutParam;
        }
    }
}

