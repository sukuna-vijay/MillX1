package com.example.millx;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // ================= AUTH (JSON – FIXED) =================
    // PHP uses json_decode(file_get_contents("php://input"))

    @POST("auth/login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/signup.php")
    Call<ApiResponse> signup(@Body SignupRequest request);

    @POST("auth/logout.php")
    Call<ResponseBody> logout();

    // ================= USER (FORM / QUERY) =================

    @GET("user/home.php")
    Call<ResponseBody> getUserHome();

    @GET("user/products_list.php")
    Call<ResponseBody> getProducts();

    @FormUrlEncoded
    @POST("user/order_create.php")
    Call<ResponseBody> createOrder(
            @Field("user_id") int userId,
            @Field("product_id") int productId,
            @Field("quantity") int quantity,
            @Field("total_price") double totalPrice
    );

    @GET("user/order_status.php")
    Call<ResponseBody> getOrderStatus(
            @Query("user_id") int userId
    );

    @GET("user/prices.php")
    Call<ResponseBody> getPrices();

    @FormUrlEncoded
    @POST("user/feedback_submit.php")
    Call<ResponseBody> submitFeedback(
            @Field("user_id") int userId,
            @Field("message") String message
    );

    @GET("user/profile_get.php")
    Call<ResponseBody> getUserProfile(
            @Query("user_id") int userId
    );

    @FormUrlEncoded
    @POST("user/profile_update.php")
    Call<ResponseBody> updateUserProfile(
            @Field("user_id") int userId,
            @Field("name") String name,
            @Field("phone") String phone
    );

    @GET("user/machine_list.php")
    Call<ResponseBody> getUserMachines();

    @GET("user/stock_list.php")
    Call<ResponseBody> getUserStocks();

    // ================= ADMIN =================

    @GET("admin/dashboard.php")
    Call<ResponseBody> getAdminDashboard();

    @GET("admin/machines_list.php")
    Call<ResponseBody> getAdminMachines();

    @FormUrlEncoded
    @POST("admin/machine_add.php")
    Call<ResponseBody> addMachine(
            @Field("name") String name,
            @Field("status") String status
    );

    @FormUrlEncoded
    @POST("admin/machine_status_update.php")
    Call<ResponseBody> updateMachineStatus(
            @Field("id") int id,
            @Field("status") String status
    );

    @GET("admin/orders_list.php")
    Call<ResponseBody> getAdminOrders();

    @GET("admin/stock_list.php")
    Call<ResponseBody> getAdminStocks();

    @FormUrlEncoded
    @POST("admin/stock_add.php")
    Call<ResponseBody> addStock(
            @Field("item_name") String itemName,
            @Field("quantity") int quantity
    );

    @GET("admin/price_list.php")
    Call<ResponseBody> getAdminPrices();

    @GET("admin/feedback_list.php")
    Call<ResponseBody> getAdminFeedbacks();

    // ================= NOTIFICATIONS =================

    @GET("notifications/list.php")
    Call<ResponseBody> getNotifications(
            @Query("user_id") int userId
    );

    @FormUrlEncoded
    @POST("notifications/mark_read.php")
    Call<ResponseBody> markNotificationRead(
            @Field("id") int notificationId
    );
}
