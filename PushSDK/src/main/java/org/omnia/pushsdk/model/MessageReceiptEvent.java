package org.omnia.pushsdk.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.omnia.pushsdk.database.DatabaseConstants;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MessageReceiptEvent extends EventBase {

    public static class Columns {
        public static final String DATA = "data";
    }
    public static final String TYPE = "event_push_received";

    @SerializedName(Columns.DATA)
    private MessageReceiptData data;
    
    public MessageReceiptEvent() {
        super();
    }

    public MessageReceiptEvent(Cursor cursor) {
        int columnIndex;

        columnIndex = cursor.getColumnIndex(BaseColumns._ID);
        if (columnIndex >= 0) {
            setId(cursor.getInt(columnIndex));
        } else {
            setId(0);
        }

        columnIndex = cursor.getColumnIndex(EventBase.Columns.STATUS);
        if (columnIndex >= 0) {
            setStatus(cursor.getInt(columnIndex));
        } else {
            setStatus(Status.NOT_POSTED);
        }

        columnIndex = cursor.getColumnIndex(EventBase.Columns.EVENT_UUID);
        if (columnIndex >= 0) {
            setEventId(cursor.getString(columnIndex));
        }

        columnIndex = cursor.getColumnIndex(EventBase.Columns.VARIANT_UUID);
        if (columnIndex >= 0) {
            setVariantUuid(cursor.getString(columnIndex));
        }

        columnIndex = cursor.getColumnIndex(EventBase.Columns.TIME);
        if (columnIndex >= 0) {
            setTime(cursor.getString(columnIndex));
        }

        columnIndex = cursor.getColumnIndex(MessageReceiptData.Columns.MESSAGE_UUID);
        if (columnIndex >= 0) {
            setData(new MessageReceiptData());
            getData().setMessageUuid(cursor.getString(columnIndex));
        }
    }

    @Override
    protected String getEventType() {
        return TYPE;
    }

    @Override
    public ContentValues getContentValues() {
        // NOTE - do not save the 'id' field to the ContentValues. Let the database
        // figure out the 'id' itself.
        final ContentValues cv = new ContentValues();
        cv.put(EventBase.Columns.EVENT_UUID, getEventId());
        cv.put(EventBase.Columns.VARIANT_UUID, getVariantUuid());
        cv.put(EventBase.Columns.TIME, getTime());
        cv.put(EventBase.Columns.STATUS, getStatus());
        if (data != null) {
            cv.put(MessageReceiptData.Columns.MESSAGE_UUID, data.getMessageUuid());
        } else {
            cv.put(MessageReceiptData.Columns.MESSAGE_UUID, (String) null);
        }
        return cv;
    }

    public MessageReceiptData getData() {
        return data;
    }

    public void setData(MessageReceiptData data) {
        this.data = data;
    }

    public static MessageReceiptEvent getMessageReceiptEvent(String variantUuid, String messageUuid) {
        final String eventId = UUID.randomUUID().toString();
        final Date time = new Date();
        return getMessageReceiptEvent(eventId, variantUuid, messageUuid, time);
    }

    public static MessageReceiptEvent getMessageReceiptEvent(String variantUuid, String messageUuid, Date time) {
        final String eventId = UUID.randomUUID().toString();
        return getMessageReceiptEvent(eventId, variantUuid, messageUuid, time);
    }

    public static MessageReceiptEvent getMessageReceiptEvent(String eventId, String variantUuid, String messageUuid, Date time) {
        final MessageReceiptEvent event = new MessageReceiptEvent();
        event.setEventId(eventId);
        event.setVariantUuid(variantUuid);
        event.setTime(time);
        event.setData(new MessageReceiptData());
        event.getData().setMessageUuid(messageUuid);
        event.setStatus(Status.NOT_POSTED);
        return event;
    }


    public static String getCreateTableSqlStatement() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append('\'');
        sb.append(DatabaseConstants.MESSAGE_RECEIPTS_TABLE_NAME);
        sb.append("\' ('");
        sb.append(BaseColumns._ID);
        sb.append("' INTEGER PRIMARY KEY AUTOINCREMENT, '");
        sb.append(EventBase.Columns.EVENT_UUID);
        sb.append("' TEXT, '");
        sb.append(EventBase.Columns.VARIANT_UUID);
        sb.append("' TEXT, '");
        sb.append(EventBase.Columns.TIME);
        sb.append("' INT, '");
        sb.append(EventBase.Columns.STATUS);
        sb.append("' INT, '");
        sb.append(MessageReceiptData.Columns.MESSAGE_UUID);
        sb.append("' TEXT);");
        return sb.toString();
    }

    public static String getDropTableSqlStatement() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS '");
        sb.append(DatabaseConstants.MESSAGE_RECEIPTS_TABLE_NAME);
        sb.append("';");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (!super.equals(o))
            return false;

        final MessageReceiptEvent other = (MessageReceiptEvent) o;

        if (other.data == null && data != null) {
            return false;
        }
        if (other.data != null && data == null) {
            return false;
        }
        if (other.data != null && data != null && !data.equals(other.data)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = (result * 31) + (data == null ? 0 : data.hashCode());
        result = (result * 31) + super.hashCode();
        return result;
    }

    public static List<MessageReceiptEvent> jsonStringToList(String str) {
        final Gson gson = new Gson();
        final Type type = getTypeToken();
        final List list = gson.fromJson(str, type);
        return list;
    }


    public static String listToJsonString(List<MessageReceiptEvent> list) {
        if (list == null) {
            return null;
        } else {
            final Gson gson = new Gson();
            final Type type = getTypeToken();
            final String str = gson.toJson(list, type);
            return str;
        }
    }

    private static Type getTypeToken() {
        return new TypeToken<List<MessageReceiptEvent>>(){}.getType();
    }
}
