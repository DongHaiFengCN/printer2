package com.ydd.consumer;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeviceService {

    @GET("v1/device/printer")
    Observable<DeviceResponse> getDevice(@Query("chan_id") String chan_id);

}
