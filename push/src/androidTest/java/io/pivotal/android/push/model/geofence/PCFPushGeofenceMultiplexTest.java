package io.pivotal.android.push.model.geofence;

import android.test.AndroidTestCase;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import io.pivotal.android.push.util.GsonUtil;
import io.pivotal.android.push.util.ModelUtil;

public class PCFPushGeofenceMultiplexTest extends AndroidTestCase {

    public void testLoadsEmptyMap() throws IOException {
        final Type type = new TypeToken<PCFPushGeofenceMultiplexMap>(){}.getType();
        final PCFPushGeofenceMultiplexMap map = GsonUtil.getGsonAndSerializeNulls().fromJson("[]", type);
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    public void testLoadsTwoItemsIntoMap() throws IOException {
        final TypeToken<PCFPushGeofenceMultiplexMap> typeToken = new TypeToken<PCFPushGeofenceMultiplexMap>(){};
        final PCFPushGeofenceMultiplexMap map = ModelUtil.getJson(getContext(), "geofence_multiplex_two_items.json", typeToken);
        assertNotNull(map);
        assertEquals(2, map.size());
        assertTrue(map.containsKey("guid1"));
        assertEquals(2, map.get("guid1").getRequestIds().size());
        assertEquals("PCF_1_2", map.get("guid1").getRequestIds().get(0));
        assertEquals("PCF_4_2", map.get("guid1").getRequestIds().get(1));
        assertTrue(map.containsKey("guid2"));
        assertEquals(3, map.get("guid2").getRequestIds().size());
        assertEquals("PCF_2_1", map.get("guid2").getRequestIds().get(0));
        assertEquals("PCF_3_1", map.get("guid2").getRequestIds().get(1));
        assertEquals("PCF_5_1", map.get("guid2").getRequestIds().get(2));
    }

    public void testWriteTwoItemsFromMap() throws IOException {
        final TypeToken<PCFPushGeofenceMultiplexMap> typeToken = new TypeToken<PCFPushGeofenceMultiplexMap>(){};
        final PCFPushGeofenceMultiplexMap map = ModelUtil.getJson(getContext(), "geofence_multiplex_two_items.json", typeToken);
        final String json = GsonUtil.getGsonAndSerializeNulls().toJson(map);
        assertTrue(json.startsWith("["));
        assertTrue(json.contains("{\"multiplex_id\":\"guid1\",\"request_ids\":[\"PCF_1_2\",\"PCF_4_2\"]}"));
        assertTrue(json.contains("{\"multiplex_id\":\"guid2\",\"request_ids\":[\"PCF_2_1\",\"PCF_3_1\",\"PCF_5_1\"]}"));
        assertTrue(json.endsWith("]"));
    }

    public void testWritesNullMap() throws IOException {
        final String json = GsonUtil.getGsonAndSerializeNulls().toJson(null);
        assertEquals("null", json);
    }

    public void testWritesEmptyMap() throws IOException {
        final String json = GsonUtil.getGsonAndSerializeNulls().toJson(new PCFPushGeofenceMultiplexMap());
        assertEquals("[]", json);
    }
}
