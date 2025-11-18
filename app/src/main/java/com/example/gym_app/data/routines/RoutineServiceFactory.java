package com.example.gym_app.data.routines;

import androidx.annotation.NonNull;

import com.example.gym_app.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RoutineServiceFactory {

    private static final long DEFAULT_TIMEOUT_SECONDS = 30L;

    private static Retrofit retrofit;

    private RoutineServiceFactory() {
        // No instances.
    }

    @NonNull
    static synchronized RoutineApiService createService() {
        if (retrofit == null) {
            retrofit = buildRetrofit();
        }
        return retrofit.create(RoutineApiService.class);
    }

    @NonNull
    private static Retrofit buildRetrofit() {
        OkHttpClient client = buildOkHttpClient();
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    private static OkHttpClient buildOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        builder.addInterceptor(loggingInterceptor);

        return builder.build();
    }
}
