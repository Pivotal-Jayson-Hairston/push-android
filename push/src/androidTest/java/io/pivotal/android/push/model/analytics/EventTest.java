package io.pivotal.android.push.model.analytics;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class EventTest extends AndroidTestCase {

    private static final String TEST_RECEIPT_ID_1 = "RECEIPT-ID-1";
    private static final String TEST_MESSAGE_UUID_1 = "SOME-MESSAGE-UUID-1";
    private static final int TEST_YEAR_1 = 1969;
    private static final int TEST_MONTH_1 = 6;
    private static final int TEST_DAY_1 = 20;
    private static final int TEST_HOUR_1 = 20;
    private static final int TEST_MINUTE_1 = 17;
    private static final int TEST_SECOND_1 = 40;

    private static final String TEST_RECEIPT_ID_2 = "RECEIPT-ID-2";
    private static final String TEST_MESSAGE_UUID_2 = "ANOTHER-MESSAGE-UUID";
    private static final int TEST_YEAR_2 = 1957;
    private static final int TEST_MONTH_2 = 9;
    private static final int TEST_DAY_2 = 4;
    private static final int TEST_HOUR_2 = 19;
    private static final int TEST_MINUTE_2 = 28;
    private static final int TEST_SECOND_2 = 34;
    private static final String SOME_OTHER_EVENT_TYPE = "SOME_OTHER_EVENT_TYPE";

    private static final String TEST_RECEIPT_ID_3 = "RECEIPT-ID-3";
    private static final String TEST_EVENT_TYPE_3 = "EVENT_OF_AWESOME";

    public void testEquals1() {
        final Event model1 = getEvent1();
        final Event model2 = getEvent1();
        assertEquals(model1, model2);
        assertEquals(model2, model1);
    }

    public void testNotEquals() {
        final Event model1 = getEvent1();
        final Event model2 = getEvent2();
        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);
    }

    public void testCopyConstructor1() {
        final Event model1 = new Event();
        final Event model2 = new Event(model1);
        assertEquals(model1, model2);
        assertFalse(model1 == model2);
    }

    public void testCopyConstructor2() {
        final Event model1 = getEvent1();
        final Event model2 = new Event(model1);
        assertEquals(model1, model2);
        assertFalse(model1 == model2);
    }

    public void testNotEqualsWithDates() {

        final Event model1 = new Event();
        model1.setEventTime(getTestDate1());

        final Event model2 = new Event();
        model2.setEventTime(getTestDate2());

        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);

        model1.setEventTime((String) null);
        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);

        model1.setEventTime((Date) null);
        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);

        model2.setEventTime((String) null);
        assertEquals(model1, model2);
    }

    public void testNotEqualsWithIds() {

        final Event model1 = new Event();
        model1.setReceiptId(TEST_RECEIPT_ID_1);

        final Event model2 = new Event();
        model2.setReceiptId(TEST_RECEIPT_ID_2);

        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);

        model1.setReceiptId(null);
        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);

        model2.setReceiptId(null);
        assertEquals(model1, model2);
    }

    public void testNotEqualsWithStatuses() {

        final Event model1 = new Event();
        model1.setStatus(Event.Status.POSTING_ERROR);

        final Event model2 = new Event();
        model1.setStatus(Event.Status.POSTING);

        MoreAsserts.assertNotEqual(model1, model2);
        MoreAsserts.assertNotEqual(model2, model1);
    }

    public void testNotEqualsNull() {
        final Event model1 = getEvent1();
        assertFalse(model1.equals(null));
    }

    public void testNotEqualsOtherObject() {
        final Event model1 = getEvent1();
        assertFalse(model1.equals("INTERLOPER STRING"));
    }

    public void testHashCode() {
        final Event model1 = getEvent1();
        final Event model2 = getEvent1();
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    public void testToJson() {
        final Event event = getEvent1();

        final Gson gson = new Gson();
        final String json = gson.toJson(event);
        assertTrue(json.contains("\"receiptId\":"));
        assertTrue(json.contains("\"" + TEST_RECEIPT_ID_1 + "\""));
        assertTrue(json.contains("\"eventType\":\"event_dummy\""));
        assertTrue(json.contains("\"eventTime\":"));
        assertTrue(json.contains("\"" + (getTestDate1().getTime() / 1000L) + "\""));
    }

    public void testConvertStringToListEmpty1() {
        final List<Event> list = Event.jsonStringToList("");
        assertNull(list);
    }

    public void testConvertStringToListEmpty2() {
        final List<Event> list = Event.jsonStringToList("[]");
        assertEquals(0, list.size());
    }

    public void testConvertStringToListNull() {
        final List<Event> list = Event.jsonStringToList(null);
        assertNull(list);
    }

    public void testConvertListToString() {
        List<Event> list = new LinkedList<>();
        list.add(getEvent1());
        list.add(getEvent2());
        final String str = Event.listToJsonString(list);
        assertTrue(str.contains("\"eventType\":\"" + DummyEvent.EVENT_TYPE + "\""));
        assertTrue(str.contains("\"eventType\":\"" + SOME_OTHER_EVENT_TYPE + "\""));
    }

    public void testConvertListToStringEmpty() {
        List<Event> list = new LinkedList<>();
        final String str = Event.listToJsonString(list);
        assertEquals("[]", str);
    }

    public void testConvertListToStringNull() {
        final String str = Event.listToJsonString(null);
        assertNull(str);
    }

    public static Event getEvent1() {
        final Event event = new Event();
        event.setReceiptId(TEST_RECEIPT_ID_1);
        event.setEventTime(getTestDate1());
        event.setEventType(DummyEvent.EVENT_TYPE);
        return event;
    }

    public static Event getEvent2() {
        final Event event = new Event();
        event.setReceiptId(TEST_RECEIPT_ID_2);
        event.setEventTime(getTestDate1());
        event.setEventType(SOME_OTHER_EVENT_TYPE);
        return event;
    }

    public static Event getEvent3() {
        final Event event = new Event();
        event.setReceiptId(TEST_RECEIPT_ID_3);
        event.setEventTime(getTestDate2());
        event.setEventType(TEST_EVENT_TYPE_3);
        return event;
    }

    private static Date getTestDate1() {
        final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(TEST_YEAR_1, TEST_MONTH_1, TEST_DAY_1, TEST_HOUR_1, TEST_MINUTE_1, TEST_SECOND_1);
        return cal.getTime();
    }

    private static Date getTestDate2() {
        final Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(TEST_YEAR_2, TEST_MONTH_2, TEST_DAY_2, TEST_HOUR_2, TEST_MINUTE_2, TEST_SECOND_2);
        return cal.getTime();
    }
}
