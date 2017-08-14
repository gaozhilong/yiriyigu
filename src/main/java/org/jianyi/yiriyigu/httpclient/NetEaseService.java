package org.jianyi.yiriyigu.httpclient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NetEaseService {

    @Headers({ "Connection:Keep-Alive",
        "Accept:text/html, application/xhtml+xml, */*",
        "Accept-Language:zh-CN,zh;q=0.8",
        "User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko" })
    @GET("service/chddata.html")
    Call<ResponseBody> getStokData(@Query("code") String code,
            @Query("start") String start, @Query("end") String end,
            @Query("fields") String fields);

}
