/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.linkbubble.MainApplication;
import com.linkbubble.R;
import com.linkbubble.util.Util;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class SearchURLCustomAdapter extends ArrayAdapter<SearchURLSuggestions> {

    private static final float DROP_DOWN_TEXT_SIZE = 16;

    public String mRealUrlBarConstraint = "";

    private int mControlSize;
    private Resources mResources;
    private HashMap<SearchURLSuggestions.SearchEngine, Integer> mMaxCharsCountToUse =
            new HashMap<SearchURLSuggestions.SearchEngine, Integer>();

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((SearchURLSuggestions)resultValue).Name;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraintInCome) {
            FilterResults results = new FilterResults();

            String constraint = Util.getUrlWithoutHttpHttpsWww(getContext(), mRealUrlBarConstraint);
            MainApplication.sSearchURLSuggestionsContainer.loadSuggestions(getContext(), mResources);
            if (constraint != null && constraint.length() != 0) {
                CopyOnWriteArrayList<SearchURLSuggestions> suggestions = new CopyOnWriteArrayList<SearchURLSuggestions>();
                boolean showSearchEngines = true;
                for (SearchURLSuggestions suggestion : MainApplication.sSearchURLSuggestionsContainer.mSuggestions) {
                    // Note: change the "startsWith" to "contains" if you only want starting matches
                    if (suggestion.Name.toLowerCase().startsWith(constraint.toLowerCase())) {
                        suggestions.add(suggestion);
                        if (suggestion.Name.length() == constraint.length()) {
                            showSearchEngines = false;
                        }
                    }
                }

                // For search engines
                if (showSearchEngines && !Util.isValidURL(getContext(), mRealUrlBarConstraint)) {
                    SearchURLSuggestions searchSuggestion1 = new SearchURLSuggestions();
                    SearchURLSuggestions searchSuggestion2 = new SearchURLSuggestions();
                    SearchURLSuggestions searchSuggestion3 = new SearchURLSuggestions();
                    SearchURLSuggestions searchSuggestion4 = new SearchURLSuggestions();

                    searchSuggestion1.Name = constraint.toString();
                    searchSuggestion1.EngineToUse = SearchURLSuggestions.SearchEngine.GOOGLE;

                    searchSuggestion2.Name = constraint.toString();
                    searchSuggestion2.EngineToUse = SearchURLSuggestions.SearchEngine.DUCKDUCKGO;

                    searchSuggestion3.Name = constraint.toString();
                    searchSuggestion3.EngineToUse = SearchURLSuggestions.SearchEngine.YAHOO;

                    searchSuggestion4.Name = constraint.toString();
                    searchSuggestion4.EngineToUse = SearchURLSuggestions.SearchEngine.AMAZON;

                    suggestions.add(searchSuggestion1);
                    suggestions.add(searchSuggestion2);
                    suggestions.add(searchSuggestion3);
                    suggestions.add(searchSuggestion4);
                }
                //
                results.values = suggestions;
                results.count = suggestions.size();
            }
            else {
                results.values = MainApplication.sSearchURLSuggestionsContainer.mSuggestions;
                results.count = MainApplication.sSearchURLSuggestionsContainer.mSuggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // We have filtered results
                addAll((CopyOnWriteArrayList<SearchURLSuggestions>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    public SearchURLCustomAdapter(Context context, int textViewResourceId, Resources resources,
                                  int controlSize) {
        super(context, textViewResourceId);
        mResources = resources;
        setDropDownWidth(controlSize);
        mMaxCharsCountToUse.put(SearchURLSuggestions.SearchEngine.GOOGLE, 0);
        mMaxCharsCountToUse.put(SearchURLSuggestions.SearchEngine.DUCKDUCKGO, 0);
        mMaxCharsCountToUse.put(SearchURLSuggestions.SearchEngine.YAHOO, 0);
        mMaxCharsCountToUse.put(SearchURLSuggestions.SearchEngine.AMAZON, 0);
    }

    public void setDropDownWidth(int controlSize) {
        mControlSize = controlSize - 130;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView name = (TextView) view;
        name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, DROP_DOWN_TEXT_SIZE);
        SearchURLSuggestions suggestion = getItem(position);
        if (SearchURLSuggestions.SearchEngine.NONE == suggestion.EngineToUse) {
            name.setText(Html.fromHtml(getContext().getString(R.string.top_500_prepend) + " <font color=" +
                    getContext().getString(R.string.url_bar_constraint_text_color) + ">" + suggestion.Name + "</font>"));
        }
        else {

            String valueToSet = "";
            if (SearchURLSuggestions.SearchEngine.GOOGLE == suggestion.EngineToUse) {
                valueToSet = String.format(getContext().getString(R.string.search_for_with),
                        getContext().getString(R.string.google),
                        "<font color=" + getContext().getString(R.string.url_bar_constraint_text_color) + ">" + suggestion.Name);
            }
            else if (SearchURLSuggestions.SearchEngine.DUCKDUCKGO == suggestion.EngineToUse) {
                valueToSet = String.format(getContext().getString(R.string.search_for_with),
                        getContext().getString(R.string.duck_duck_go),
                        "<font color=" + getContext().getString(R.string.url_bar_constraint_text_color) + ">" + suggestion.Name);
            }
            else if (SearchURLSuggestions.SearchEngine.YAHOO == suggestion.EngineToUse) {
                valueToSet = String.format(getContext().getString(R.string.search_for_with),
                        getContext().getString(R.string.yahoo),
                        "<font color=" + getContext().getString(R.string.url_bar_constraint_text_color) + ">" + suggestion.Name);
            }
            else if (SearchURLSuggestions.SearchEngine.AMAZON == suggestion.EngineToUse) {
                valueToSet = String.format(getContext().getString(R.string.search_for_with),
                        getContext().getString(R.string.amazon),
                        "<font color=" + getContext().getString(R.string.url_bar_constraint_text_color) + ">" + suggestion.Name);
            }
            Paint textPaint = name.getPaint();
            String toAdd = "</font>";
            float textWidth = textPaint.measureText(Html.fromHtml(valueToSet + toAdd).toString());
            if (textWidth > mControlSize) {
                int charactersToLeft = mMaxCharsCountToUse.get(suggestion.EngineToUse);
                if (charactersToLeft >= valueToSet.length()) {
                    charactersToLeft = valueToSet.length() - 1;
                }
                if (0 == charactersToLeft) {
                    float percentToShow = (float)mControlSize * 100 / textWidth;
                    charactersToLeft =  (int)(valueToSet.length() * percentToShow / 100);
                }

                valueToSet = valueToSet.substring(0, charactersToLeft);
                toAdd = "..." + toAdd;
            }
            else {
                toAdd += "\"";
                mMaxCharsCountToUse.put(suggestion.EngineToUse, valueToSet.length());
            }

            valueToSet += toAdd;
            name.setText(Html.fromHtml(valueToSet));
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void addUrlToAutoSuggestion(String urlToAdd) {
        MainApplication.sSearchURLSuggestionsContainer.addUrlToAutoSuggestion(urlToAdd, getContext(), mResources);
    }
}
