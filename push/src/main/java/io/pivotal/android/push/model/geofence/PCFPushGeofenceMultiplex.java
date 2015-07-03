package io.pivotal.android.push.model.geofence;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class PCFPushGeofenceMultiplex {

    @SerializedName("multiplex_id")
    private String multiplexId;

    @SerializedName("request_ids")
    private List<String> requestIds;

    public String getMultiplexId() {
        return multiplexId;
    }

    public void setMultiplexId(String multiplexId) {
        this.multiplexId = multiplexId;
    }

    public List<String> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<String> requestIds) {
        this.requestIds = requestIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PCFPushGeofenceMultiplex that = (PCFPushGeofenceMultiplex) o;

        if (multiplexId != null ? !multiplexId.equals(that.multiplexId) : that.multiplexId != null)
            return false;
        return !(requestIds != null ? !requestIds.equals(that.requestIds) : that.requestIds != null);

    }

    @Override
    public int hashCode() {
        int result = multiplexId != null ? multiplexId.hashCode() : 0;
        result = 31 * result + (requestIds != null ? requestIds.hashCode() : 0);
        return result;
    }
}
