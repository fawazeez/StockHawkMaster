package com.sam_chordas.android.stockhawk.rest;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;



/**
 * Created by fawaz on 5/8/2016.
 */
public interface RetroService {
    @GET("/v1/public/yql")
    Call<QueryList> getquotes(@retrofit2.http.Query("q") String q,@retrofit2.http.Query("env") String env,@retrofit2.http.Query("format") String format);

}
