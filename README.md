# SectionAdapter

***Recycler Adapter to control section item.***

<img src="https://cloud.githubusercontent.com/assets/17852124/26761634/47823b14-496e-11e7-91c3-0713d3c42893.png"/>


## index

* Motivation
* How to use it?

### Motivation

When I develop application with RecyclerView, I got problem happend from NestedScrollView and RecyclerView.

I deal with several list data and use NestedScrollView and some RecyclerViews. I dosen't work at some devices.  So I use RecyclerView viewType to create various view.

But when I deal with serveral list data on one RecyclerView Adapter, It's very hard to convert view position to list data index.

My coworker, IOS develop introduce Swift TableView to me. TableView control list and it is divided with section.

Swift TableView is motivation of this library.


### How to use it?

1) Make Adapter that extends SectionAdapter

```java
public class SampleAdapter extends SectionAdapter {
    public SampleAdpater(Context context) {
        super(context);
    }
}
```

2) Override unimplemented method (must)
    * getSectionItemViewType: return viewType of section item, return value must not be NONE_VIEW_TYPE, -1.
        ```java
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
        ```
    
    * onCreateItemHolder: same as onCreateViewHolder
        ```java
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
        ```
    
    * onBindItemHolder: same as onBindViewHolder
        ```java
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
        ```
    
    * getSectionCount: return section count you want to create
        ```java
        @Override
        public int getSectionCount() {
            return 2;
        }
        ```
        
    * getSectionItemCount: return section's item count you want to create
        ```java
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
        ```
3) Override method (optional)
    * getSectionHeaderViewType: return viewType want you want to create. If you don't want to create header in section, return NONE_VIEW_TYPE.
        ```java
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
        ```
    
    * getSectionFooterViewType: return viewType want you want to create. If you don't want to create footer in section, return NONE_VIEW_TYPE.
        ```java
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
        ```
    * getItemViewOption: return ViewOption object. ViewOption has attribute, numberOfGrid and rowLayoutParams. If you do not want any viewOption, return null.
        ```java
        @Override
        public ViewOption getItemViewOption(int viewType) {
            if (viewType == ITEM_BODY_FIRST) {
                return new ViewOption(2);
            }
            return null;
        }
        ```

### License

```
MIT License

Copyright (c) 2017 JunsuLime

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
