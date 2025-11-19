package com.example.gym_app.data.user;

import androidx.annotation.NonNull;
import com.example.gym_app.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserServiceFactory {

    private static final long DEFAULT_TIMEOUT_SECONDS = 30L;
    private static Retrofit retrofit;

    private UserServiceFactory() {
        // No instances
    }

    @NonNull
    public static synchronized UserApiService createService() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(BuildConfig.DEBUG
                    ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);
            builder.addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(UserApiService.class);
    }
}