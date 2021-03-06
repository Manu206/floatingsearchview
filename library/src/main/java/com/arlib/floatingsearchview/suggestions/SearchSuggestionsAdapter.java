package com.arlib.floatingsearchview.suggestions;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.R;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SearchSuggestionsAdapter";

    private List<? extends SearchSuggestion> mSearchSuggestions = new ArrayList<>();

    private Listener mListener;

    private Context mContext;

    @LayoutRes
    private int layoutResource = -1;

    private Drawable mRightIconDrawable;
    private boolean mShowRightMoveUpBtn = false;
    private int mBodyTextSizePx;
    private int mTextColor = -1;

    public interface OnBindSuggestionCallback {

        void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, TextView textView2,
                              SearchSuggestion item, int itemPosition);
    }

    private OnBindSuggestionCallback mOnBindSuggestionCallback;

    public interface Listener {

        void onItemSelected(SearchSuggestion item);

        void onMoveItemToSearchClicked(SearchSuggestion item);
    }

    public static class SearchSuggestionViewHolder extends RecyclerView.ViewHolder {

        public TextView body;
        public TextView additional;
        public ImageView leftIcon;

        private Listener mListener;

        public interface Listener {

            void onItemClicked(int adapterPosition);

            void onMoveItemToSearchClicked(int adapterPosition);
        }

        public SearchSuggestionViewHolder(View v, Listener listener) {
            super(v);

            mListener = listener;
            body = (TextView) v.findViewById(R.id.body);
            additional = (TextView) v.findViewById(R.id.additional);
            leftIcon = (ImageView) v.findViewById(R.id.left_icon);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int adapterPosition = getAdapterPosition();
                    if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        mListener.onItemClicked(adapterPosition);
                    }
                }
            });
        }
    }

    public SearchSuggestionsAdapter(Context context, int suggestionTextSize, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mBodyTextSizePx = suggestionTextSize;

        mRightIconDrawable = Util.getWrappedDrawable(mContext, R.drawable.ic_arrow_back_black_24dp);
        DrawableCompat.setTint(mRightIconDrawable, Util.getColor(mContext, R.color.gray_active_icon));
    }

    public void swapData(List<? extends SearchSuggestion> searchSuggestions) {
        mSearchSuggestions = searchSuggestions;
        notifyDataSetChanged();
    }

    public List<? extends SearchSuggestion> getDataSet() {
        return mSearchSuggestions;
    }

    public void setOnBindSuggestionCallback(OnBindSuggestionCallback callback) {
        this.mOnBindSuggestionCallback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int layoutToInflate = layoutResource > -1 ? layoutResource : R.layout.search_suggestion_item;

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutToInflate, viewGroup, false);
        SearchSuggestionViewHolder viewHolder = new SearchSuggestionViewHolder(view,
                new SearchSuggestionViewHolder.Listener() {

                    @Override
                    public void onItemClicked(int adapterPosition) {

                        if (mListener != null) {
                            mListener.onItemSelected(mSearchSuggestions.get(adapterPosition));
                        }
                    }

                    @Override
                    public void onMoveItemToSearchClicked(int adapterPosition) {

                        if (mListener != null) {
                            mListener.onMoveItemToSearchClicked(mSearchSuggestions
                                    .get(adapterPosition));
                        }
                    }

                });

        viewHolder.body.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBodyTextSizePx);
        viewHolder.additional.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBodyTextSizePx);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

        SearchSuggestionViewHolder viewHolder = (SearchSuggestionViewHolder) vh;

        SearchSuggestion suggestionItem = mSearchSuggestions.get(position);
        viewHolder.body.setText(suggestionItem.getBody());
        viewHolder.additional.setText(suggestionItem.getAdditional());

        if(mTextColor != -1){
            viewHolder.body.setTextColor(mTextColor);
            viewHolder.additional.setTextColor(mTextColor);
        }

        if (mOnBindSuggestionCallback != null) {
            mOnBindSuggestionCallback.onBindSuggestion(viewHolder.itemView, viewHolder.leftIcon, viewHolder.body, viewHolder.additional,
                    suggestionItem, position);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchSuggestions != null ? mSearchSuggestions.size() : 0;
    }

    /**
     * Sets a custom layout resource which should be inflated instead of the default one.
     * Be sure to add the views of the default resource:
     ** - {@link ImageView} with id left_icon
     * - {@link TextView} with id body
     * - {@link TextView} with id additional
     * @param layoutResource Id of the custom layout resource
     */
    public void setLayoutResource(@LayoutRes int layoutResource) {
        this.layoutResource = layoutResource;
    }

    public void setTextColor(int color) {

        boolean notify = false;
        if (this.mTextColor != color) {
            notify = true;
        }
        this.mTextColor = color;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void setShowMoveUpIcon(boolean show) {

        boolean notify = false;
        if (this.mShowRightMoveUpBtn != show) {
            notify = true;
        }
        this.mShowRightMoveUpBtn = show;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void reverseList() {
        Collections.reverse(mSearchSuggestions);
        notifyDataSetChanged();
    }
}
