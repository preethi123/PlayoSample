package newreader.playo.com.newreader.rest;



import newreader.playo.com.newreader.model.News;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("search")
    Call<News> getAllNewInfo();

}
