package com.example.inventra.DBConnect;

import com.example.inventra.ProductSyncResponse;
import com.example.inventra.Staff;
import com.example.inventra.UserResponse;
import com.example.inventra.admin.AddInventory.ProductSearchResponse;
import com.example.inventra.admin.AddInventory.StockUpdateRequest;
import com.example.inventra.admin.AddInventory.StockUpdateResponse;
import com.example.inventra.admin.BarGraph.SalesResponseBar;
import com.example.inventra.admin.BarcodeScan.ScannedProductResponse;
import com.example.inventra.admin.CartDetails.CartAddProductResponse;
import com.example.inventra.admin.CartDetails.CartQtyUpdateResponse;
import com.example.inventra.admin.CartDetails.CartResponse;
import com.example.inventra.admin.CartDetails.CartTotalResponse;
import com.example.inventra.admin.CartDetails.PlaceOrderResponse;
import com.example.inventra.admin.Invoice.ItemsInvoiceResponse;
import com.example.inventra.admin.Invoice.SalesInvoiceResponse;
import com.example.inventra.admin.Notification.LowStockProduct;
import com.example.inventra.admin.ProductDetail.ProductDetailResponse;
import com.example.inventra.admin.ProductImage.ProductImageResponse;
import com.example.inventra.admin.Profile.UploadResponse;
import com.example.inventra.admin.Profile.UserProfileResponse;
import com.example.inventra.admin.RecentInvoice.RecentInvoiceResponse;
import com.example.inventra.admin.Sales.BillingResponse;
import com.example.inventra.admin.Sales.SaleRequest;
import com.example.inventra.admin.Sales.SalesGrowthResponse;
import com.example.inventra.admin.Sales.SalesResponse2;
import com.example.inventra.admin.SearchProducts.SearchResponse;
import com.example.inventra.admin.Staff.DeleteResponse;
import com.example.inventra.admin.Staff.InsertResponse;
import com.example.inventra.admin.Staff.StaffResponse;
import com.example.inventra.admin.products.DashboardStatsResponse;
import com.example.inventra.admin.products.ProductResponse2;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint to get total number of products
    @GET("get_total_products.php")
    Call<DashboardProductResponse> getTotalProducts();

    // Endpoint to get total sales amount
    @GET("get_sales_amount.php")
    Call<SalesResponse> getSalesAmount();

    // Endpoint to get total yearly sales amount
    @GET("get_yearly_sales.php")
    Call<YearlySalesResponse> getYearlySalesAmount();

    // for other dashboard data
    @GET("get_dashboard_data.php")
    Call<JsonObject> getDashboardData(@Query("type") String type);

    // Endpoint to get product categories and their percentages
    @GET("getProductCategories.php")
    Call<List<CategoryResponse>> getProductCategories();

    @POST("product/add_new_product.php")
    Call<ProductResponse> createProduct(@Body RequestBody product);

    @Multipart
    @POST("product/upload_product_image.php")
    Call<ProductResponse> uploadImage(
            @Part("product_id") RequestBody productId,
            @Part MultipartBody.Part image
    );

    @GET("category/get_all_category.php")
    Call<CategoryResponse2> getCategories(); // Fetch categories from the API

    // Method to add a category
    @POST("category/add_category.php")
    Call<CategoryResponse3> addCategory(@Body CategoryRequest category);

    // Method to get all products list
    @GET("product/get_all_products.php")
    Call<ProductResponse2> getAllProducts();

    // to add new inventory
    @Headers("Content-Type: application/json")
    @POST("product/search_products.php")
    Call<ProductSearchResponse> searchProducts(@Body Map<String, String> body);

    @POST("product/update_product_quantity.php")
    Call<StockUpdateResponse> updateProductStock(@Body StockUpdateRequest request);


    // searching functionality
    @POST("product/search_products.php")
    Call<SearchResponse> searchProducts(@Body JsonObject body);

    // to process sales
    @POST("sales/process_sale.php")
    Call<SalesResponse2> processSale(@Body SaleRequest request);

    // to get sales history
    @GET("sales/get_sales_history.php")
    Call<BillingResponse> getSalesHistory();

    // fetch all products for sync
    @GET("product/sync_products.php")
    Call<ProductSyncResponse> getAllProductsSync();

    // login process
    @FormUrlEncoded
    @POST("login/login.php")
    Call<UserResponse> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    // staff management
    // Fetch all staff
    @GET("staff/get_staff.php")  // PHP script to fetch all staff records
    Call<StaffResponse> getAllStaff();

    // Delete a staff member by ID
    @FormUrlEncoded
    @POST("staff/delete_staff.php")
    Call<DeleteResponse> deleteStaff(@Field("id") int staffId);

    // == to add new staff ===
    @POST("staff/add_staff.php")
    Call<InsertResponse> insertStaff(@Body Staff newStaff);

    // ====== to get growth percentage from previous year ======
    @GET("sales/get_sales_growth.php")
    Call<SalesGrowthResponse> getSalesGrowth();

    // ==== API for getting inventory data like product count and total quantity ====
    @POST("product/get_product_count.php")
    Call<DashboardStatsResponse> getDashboardStats();

    // API to get Invoice details
    @FormUrlEncoded
    @POST("sales/get_sales_detail.php")
    Call<SalesInvoiceResponse> getSalesInvoiceDetails(
            @Field("sales_id") int salesId,
            @Field("data_type") String dataType
    );

    @FormUrlEncoded
    @POST("sales/get_sales_detail.php")
    Call<ItemsInvoiceResponse> getItemInvoiceDetails(
            @Field("sales_id") int salesId,
            @Field("data_type") String dataType
    );

    // API to get product details from it's barcode text
    @POST("product/get_product_by_barcode.php")
    @FormUrlEncoded
    Call<ScannedProductResponse> getProductByBarcode(@Field("barcode") String barcode);

    // for profile details
    @GET("staff/get_user_profile.php")
    Call<UserProfileResponse> getUserProfile(@Query("user_id") int userId);

    // saving user's profile picture
    @Multipart
    @POST("staff/upload_profile_image.php")
    Call<UploadResponse> uploadProfileImage(
            @Part MultipartBody.Part profile_image,
            @Part("user_id") RequestBody userId
    );

    @Multipart
    @POST("product/new_product_image.php")
    Call<ProductImageResponse> uploadProductImage(
            @Part MultipartBody.Part product_image,
            @Part("product_id") RequestBody productId
    );

    // for bar graph
    @POST("sales/get_sales_summary.php")
    Call<SalesResponseBar> getSalesSummary(@Body Map<String, String> rangeMap);

    // for recent invoices
    @GET("sales/get_recent_sales.php")
    Call<RecentInvoiceResponse> getRecentInvoices();

    // Api to fetch all cart data for particular user
    @FormUrlEncoded
    @POST("cart/get_cart_data.php")
    Call<CartResponse> getCartItems(@Field("user_id") int userId);

    // api for increase and decrease product qty in cart
    @FormUrlEncoded
    @POST("cart/update_cart.php")
    Call<CartQtyUpdateResponse> updateCartQuantity(
            @Field("user_id") int userId,
            @Field("product_id") int productId,
            @Field("cart_id") int cartId,
            @Field("action") String action
    );

    // api to add product to cart
    @FormUrlEncoded
    @POST("cart/add_to_cart.php")
    Call<CartAddProductResponse> addToCart(
            @Field("user_id") int userId,
            @Field("product_id") int productId,
            @Field("quantity") int quantity
    );

    // api to get cart's total amount
    @FormUrlEncoded
    @POST("cart/get_cart_total.php")
    Call<CartTotalResponse> getCartTotal(@Field("user_id") int userId);

    // api to get order summary
    @FormUrlEncoded
    @POST("cart/get_cart_data.php")
    Call<CartResponse> getCartItemsOrderSummary(@Field("user_id") int userId);

    // api for placing order
    @FormUrlEncoded
    @POST("cart/place_order.php")
    Call<PlaceOrderResponse> placeOrder(
            @Field("user_id") int userId,
            @Field("customer_name") String name,
            @Field("phone") String phone,
            @Field("payment_method") String paymentMethod,
            @Field("payment_status") String paymentStatus
    );

    // api to get product data
    @FormUrlEncoded
    @POST("product/get_product_by_id.php")
    Call<ProductDetailResponse> getProductById(@Field("product_id") int productId);

    // api for low stock product notification
    @GET("notification/get_low_stock_products.php")
    Call<List<LowStockProduct>> getLowStockProducts();
}
