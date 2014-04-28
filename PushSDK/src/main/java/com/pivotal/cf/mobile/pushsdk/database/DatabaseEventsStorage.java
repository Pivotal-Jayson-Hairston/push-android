package com.pivotal.cf.mobile.pushsdk.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.pivotal.cf.mobile.pushsdk.model.BaseEvent;

import java.util.LinkedList;
import java.util.List;

public class DatabaseEventsStorage implements EventsStorage {

	public DatabaseEventsStorage() {
	}

	@Override
	public Uri saveEvent(BaseEvent event) {
		final ContentValues contentValues = event.getContentValues();
		final Uri uri = EventsDatabaseWrapper.insert(DatabaseConstants.EVENTS_CONTENT_URI, contentValues);
		return uri;
	}

	@Override
	public List<Uri> getEventUris() {
        return getGeneralQuery(null, null, null, null);
	}

	public List<Uri> getEventUrisWithStatus(int status) {
        return getGeneralQuery(null, "status = ?", new String[] { String.valueOf(status) }, null);
	}

	private List<Uri> getGeneralQuery(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		try {
			cursor = EventsDatabaseWrapper.query(DatabaseConstants.EVENTS_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
			return getEventUrisFromCursor(cursor);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private List<Uri> getEventUrisFromCursor(final Cursor cursor) {
		final List<Uri> uris = new LinkedList<Uri>();
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				final int id = BaseEvent.getRowIdFromCursor(cursor);
				final Uri uri = Uri.withAppendedPath(DatabaseConstants.EVENTS_CONTENT_URI, String.valueOf(id));
				uris.add(uri);
			}
		}
		return uris;
	}

	@Override
	public int getNumberOfEvents() {
		Cursor cursor = null;
		try {
			cursor = EventsDatabaseWrapper.query(DatabaseConstants.EVENTS_CONTENT_URI, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				return cursor.getCount();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	@Override
	public BaseEvent readEvent(Uri uri) {
		Cursor cursor = null;
		try {
			cursor = EventsDatabaseWrapper.query(uri, null, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getCount() > 0) {
					final BaseEvent event = new BaseEvent(cursor);
					return event;
				}
			}
			throw new IllegalArgumentException("Could not find event with Uri " + uri.getPath());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public void deleteEvents(List<Uri> eventUris) {
		EventsDatabaseWrapper.delete(eventUris, null, null);
	}

	@Override
	public void reset() {
        EventsDatabaseWrapper.delete(DatabaseConstants.EVENTS_CONTENT_URI, null, null);
	}

	@Override
	public void setEventStatus(Uri eventUri, int status) {
		final ContentValues values = new ContentValues();
		values.put(BaseEvent.Columns.STATUS, status);
		final int numberOfRowsUpdated = EventsDatabaseWrapper.update(eventUri, values, null, null);
		if (numberOfRowsUpdated == 0) {
			throw new IllegalArgumentException("Could not find event with Uri " + eventUri.getPath());
		}
	}
}
