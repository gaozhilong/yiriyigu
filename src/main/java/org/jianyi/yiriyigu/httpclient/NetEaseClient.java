package org.jianyi.yiriyigu.httpclient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class NetEaseClient {

    private static final String NETEASE_SERVICE_URL = "http://quotes.money.163.com/";

    private static final String FIELDS = "TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER";

    private OkHttpClient client;

    private NetEaseService netEaseService;

    public NetEaseClient() {
        super();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);

        ConnectionPool connectionPool = new ConnectionPool(5, 60,
            TimeUnit.SECONDS);

        this.client = builder.retryOnConnectionFailure(false)
            .connectionPool(connectionPool).addInterceptor(logging)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder().client(this.client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(NetEaseClient.NETEASE_SERVICE_URL).build();
        this.netEaseService = retrofit.create(NetEaseService.class);
    }

    public byte[] getData(String code, String start, String end) {
        byte[] result = null;
        Call<ResponseBody> call = this.netEaseService.getStokData(code, start,
            end, NetEaseClient.FIELDS);

        try {
            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()) {
                result = response.body().bytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
