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

        @POST("auth/verify_signup_otp.php")
        Call<ResponseBody> verifySignupOtp(@Body java.util.HashMap<String, String> body);

        @POST("auth/forgot_password.php")
        Call<ResponseBody> forgotPassword(@Body java.util.HashMap<String, String> body);

        @POST("auth/verify_otp.php")
        Call<ResponseBody> verifyOtp(@Body java.util.HashMap<String, String> body);

        @POST("auth/reset_password.php")
        Call<ResponseBody> resetPassword(@Body java.util.HashMap<String, String> body);

        // ================= USER (FORM / QUERY) =================

        @GET("user/home.php")
        Call<ResponseBody> getUserHome();

        @GET("user/products_list.php")
        Call<ResponseBody> getProducts();

        @POST("user/order_create.php")
        Call<okhttp3.ResponseBody> createOrder(@retrofit2.http.Body OrderRequest request);

        @GET("user/order_status.php")
        Call<OrderResponse> getOrderStatus();

        @POST("user/order_cancel.php")
        Call<okhttp3.ResponseBody> cancelOrder(@retrofit2.http.Body java.util.Map<String, Integer> body);

        @GET("user/prices.php")
        Call<java.util.List<Product>> getPrices();

        @FormUrlEncoded
        @POST("user/feedback_submit.php")
        Call<ResponseBody> submitFeedback(
                        @Field("user_id") int userId,
                        @Field("message") String message);

        @GET("user/profile_get.php")
        Call<ProfileResponse> getUserProfile(
                        @Query("user_id") int userId);

        @retrofit2.http.Multipart
        @POST("user/profile_update.php")
        Call<ProfileResponse> updateUserProfile(
                        @retrofit2.http.Part("user_id") okhttp3.RequestBody userId,
                        @retrofit2.http.Part("name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("phone") okhttp3.RequestBody phone,
                        @retrofit2.http.Part("address") okhttp3.RequestBody address,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @GET("user/machine_list.php")
        Call<java.util.List<Machine>> getUserMachines();

        @GET("user/stock_list.php")
        Call<java.util.List<Stock>> getUserStocks();

        // ================= ADMIN =================

        @GET("admin/dashboard.php")
        Call<DashboardResponse> getAdminDashboard();

        @GET("admin/machines_list.php")
        Call<java.util.List<Machine>> getAdminMachines();

        @retrofit2.http.Multipart
        @POST("admin/machine_add.php")
        Call<ResponseBody> addMachine(
                        @retrofit2.http.Part("name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("status") okhttp3.RequestBody status,
                        @retrofit2.http.Part("min") okhttp3.RequestBody min,
                        @retrofit2.http.Part("max") okhttp3.RequestBody max,
                        @retrofit2.http.Part("unit") okhttp3.RequestBody unit,
                        @retrofit2.http.Part("description") okhttp3.RequestBody description,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @POST("admin/machine_status_update.php")
        Call<ResponseBody> updateMachineStatus(@Body MachineRequest request);

        @retrofit2.http.Multipart
        @POST("admin/machine_update.php")
        Call<ResponseBody> updateMachineDetails(
                        @retrofit2.http.Part("id") okhttp3.RequestBody id,
                        @retrofit2.http.Part("name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("status") okhttp3.RequestBody status,
                        @retrofit2.http.Part("min") okhttp3.RequestBody min,
                        @retrofit2.http.Part("max") okhttp3.RequestBody max,
                        @retrofit2.http.Part("unit") okhttp3.RequestBody unit,
                        @retrofit2.http.Part("description") okhttp3.RequestBody description,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @POST("admin/machine_delete.php")
        Call<ResponseBody> deleteMachine(@Body MachineRequest request);

        @GET("admin/orders_list.php")
        Call<AdminOrderResponse> getAdminOrders();

        @GET("admin/stock_list.php")
        Call<java.util.List<Stock>> getAdminStocks();

        @retrofit2.http.Multipart
        @POST("admin/stock_add.php")
        Call<ResponseBody> addStock(
                        @retrofit2.http.Part("product_name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("product_quantity") okhttp3.RequestBody quantity,
                        @retrofit2.http.Part("unit") okhttp3.RequestBody unit,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @retrofit2.http.Multipart
        @POST("admin/stock_update.php")
        Call<ResponseBody> updateStock(
                        @retrofit2.http.Part("product_id") okhttp3.RequestBody id,
                        @retrofit2.http.Part("product_name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("product_quantity") okhttp3.RequestBody quantity,
                        @retrofit2.http.Part("unit") okhttp3.RequestBody unit,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @POST("admin/stock_delete.php")
        Call<ResponseBody> deleteStock(@Body StockRequest request);

        @GET("admin/price_list.php")
        Call<java.util.List<Product>> getAdminPrices();

        @retrofit2.http.Multipart
        @POST("admin/price_add.php")
        Call<ResponseBody> addProduct(
                        @retrofit2.http.Part("name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("price") okhttp3.RequestBody price,
                        @retrofit2.http.Part("unit") okhttp3.RequestBody unit,
                        @retrofit2.http.Part("description") okhttp3.RequestBody description,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @retrofit2.http.Multipart
        @POST("admin/price_update.php")
        Call<ResponseBody> updateProduct(
                        @retrofit2.http.Part("id") okhttp3.RequestBody id,
                        @retrofit2.http.Part("name") okhttp3.RequestBody name,
                        @retrofit2.http.Part("price") okhttp3.RequestBody price,
                        @retrofit2.http.Part("unit") okhttp3.RequestBody unit,
                        @retrofit2.http.Part("description") okhttp3.RequestBody description,
                        @retrofit2.http.Part okhttp3.MultipartBody.Part image);

        @POST("admin/price_delete.php")
        Call<ResponseBody> deleteProduct(@Body ProductRequest request);

        @GET("admin/feedback_list.php")
        Call<ResponseBody> getAdminFeedbacks();

        // ================= NOTIFICATIONS =================

        @GET("notifications/list.php")
        Call<ResponseBody> getNotifications(
                        @Query("user_id") int userId);

        @FormUrlEncoded
        @POST("notifications/mark_read.php")
        Call<ResponseBody> markNotificationRead(
                        @Field("id") int notificationId);
}
