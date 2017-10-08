package newreader.playo.com.newreader.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import newreader.playo.com.newreader.Config;
import newreader.playo.com.newreader.MyApplication;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestAdapter {

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor()
    {
        @Override
        public Response intercept(Chain chain) throws IOException, IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (isNetworkAvailable()) {
                int maxAge = 60; // read from cache for 1 minute
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28 * 12; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    public static ApiInterface createAPI() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.ADMIN_PANEL_URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(ApiInterface.class);

    }

    public static boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.mInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null)&&(activeNetworkInfo.isConnected())){
            return true;
        }else{
            return false;
        }
    }
}
