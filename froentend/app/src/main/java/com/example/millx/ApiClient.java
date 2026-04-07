package com.example.millx;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // For Android Emulator use "http://10.0.2.2/millx_api/hello"
    // For Real Device (Same Wi-Fi) use "http://192.168.x.x/millx_api/" (Your PC's
    // IP)
    // "https://l2fss106-80.inc1.devtunnels.ms/millx_api/dvxdvxdvxdvz";
    //34567
    public static final String BASE_URL = "https://18fz39mg-80.inc1.devtunnels.ms/millx_api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();

                        SessionManager sessionManager = new SessionManager(App.getContext());
                        String token = sessionManager.getAuthToken();

                        if (token != null && !token.isEmpty()) {
                            okhttp3.Request request = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(request);
                        }
                        return chain.proceed(original);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
