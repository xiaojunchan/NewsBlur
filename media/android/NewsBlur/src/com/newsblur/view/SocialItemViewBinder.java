package com.newsblur.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newsblur.R;
import com.newsblur.activity.NewsBlurApplication;
import com.newsblur.database.DatabaseConstants;
import com.newsblur.domain.Story;
import com.newsblur.util.ImageLoader;

public class SocialItemViewBinder implements ViewBinder {

	private ImageLoader imageLoader;

	public SocialItemViewBinder(final Context context) {
		this.imageLoader = ((NewsBlurApplication) context.getApplicationContext()).getImageLoader();
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		final String columnName = cursor.getColumnName(columnIndex);
		final int hasBeenRead = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_READ));
		if (TextUtils.equals(cursor.getColumnName(columnIndex), DatabaseConstants.FEED_FAVICON_URL)) {
			String faviconUrl = cursor.getString(columnIndex);
			imageLoader.displayImage(faviconUrl, ((ImageView) view), false);
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_INTELLIGENCE_AUTHORS)) {
			int authors = cursor.getInt(columnIndex);
			int tags = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_INTELLIGENCE_TAGS));
			int feed = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_INTELLIGENCE_FEED));
			int title = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.STORY_INTELLIGENCE_TITLE));
			
			int score = Story.getIntelligenceTotal(title, authors, tags, feed);
			
			Drawable icon;
            if (score > 0) {
                icon = view.getResources().getDrawable(R.drawable.g_icn_focus);
			} else if (score == 0) {
                icon = view.getResources().getDrawable(R.drawable.g_icn_unread);
			} else {
                icon = view.getResources().getDrawable(R.drawable.g_icn_hidden);
			}
            icon.mutate().setAlpha(hasBeenRead == 0 ? 255 : 127);
            view.setBackgroundDrawable(icon);

			((TextView) view).setText("");
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_AUTHORS)) {
			String authors = cursor.getString(columnIndex);
			if (!TextUtils.isEmpty(authors)) {
				((TextView) view).setText(authors.toUpperCase());
			}
			return true;
		} else if (TextUtils.equals(columnName, DatabaseConstants.STORY_TITLE)) {
			((TextView) view).setText(Html.fromHtml(cursor.getString(columnIndex)));
			return true;
		}
		return false;
	}

}
