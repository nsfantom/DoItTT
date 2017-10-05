package tm.fantom.doittesttask.api;

import io.reactivex.Maybe;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tm.fantom.doittesttask.api.model.Gif;
import tm.fantom.doittesttask.api.model.ImageList;
import tm.fantom.doittesttask.api.model.SignIn;


/**
 * Created by fantom on 22-May-17.
 */

public interface ApiService {
//    @GET("data.php?id=1")
//    Maybe<DataResponse> getInitData();

    // https://api.github.com/search/repositories?q=org:facebook

    // https://api.github.com/orgs/facebook
//
//    @GET("orgs/{name}")
//    Maybe<OrgResponse> getOrg(@Path("name") String name);
//
//    @GET("search/repositories")
//    Maybe<ReposResponse> getPublicRepos(@Query("q") String name, @Query("page") int page, @Query("per_page") int perPage);

    @Multipart
    @POST("create")
    Maybe<Response<SignIn>> createUser(@Part("username") RequestBody userName,
                                       @Part("email") RequestBody email,
                                       @Part("password") RequestBody password,
                                       @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("login")
    Maybe<Response<SignIn>> login(@Field("email") String email,
                                               @Field("password") String password);

    @GET("all")
    Maybe<Response<ImageList>> getAllImages(@Header("token") String token);

    @GET("gif")
    Maybe<Response<Gif>> getGif(@Header("token") String token);

    @Multipart
    @POST("image")
    Maybe<Response<ResponseBody>> uploadImage(@Header("token") String token,
                                              @Part MultipartBody.Part file,
                                              @Part("description") RequestBody description,
                                              @Part("hashtag") RequestBody hashTag,
                                              @Part("latitude") RequestBody latitude,
                                              @Part("longitude") RequestBody longitude);
}
