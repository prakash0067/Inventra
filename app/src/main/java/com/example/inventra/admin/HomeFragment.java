package com.example.inventra.admin;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBConnect.CategoryResponse;
import com.example.inventra.DBConnect.DashboardProductResponse;
import com.example.inventra.DBConnect.ProductResponse;
import com.example.inventra.DBConnect.SalesResponse;
import com.example.inventra.DBConnect.YearlySalesResponse;
import com.example.inventra.R;
import com.example.inventra.admin.BarGraph.SalesBarEntry;
import com.example.inventra.admin.BarGraph.SalesResponseBar;
import com.example.inventra.admin.Invoice.InvoiceAdapter;
import com.example.inventra.admin.Notification.LowStockWorker;
import com.example.inventra.admin.RecentInvoice.RecentInvoice;
import com.example.inventra.admin.RecentInvoice.RecentInvoiceAdapter;
import com.example.inventra.admin.RecentInvoice.RecentInvoiceResponse;
import com.example.inventra.admin.Sales.SalesGrowthResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.NetworkType;
import androidx.work.ExistingPeriodicWorkPolicy;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private PieChart pieChart;
    BarChart barChart;
    Button btnDay, btnMonth, btnYear;
    private TextView totalProductsTextView, monthlySalesTextView, yearlySalesAmount, currentStockView, totalProductCategories, todaysSales, totalOrdersCurrentMonth, totalUsersView, previousYearIncrement;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        totalProductsTextView = view.findViewById(R.id.totalProducts);
        monthlySalesTextView = view.findViewById(R.id.monthlySales);
        yearlySalesAmount = view.findViewById(R.id.profitAmount);
        currentStockView = view.findViewById(R.id.currentStock);
        totalProductCategories = view.findViewById(R.id.productCategory);
        todaysSales = view.findViewById(R.id.totalSold);
        totalOrdersCurrentMonth = view.findViewById(R.id.monthlyOrders);
        totalUsersView = view.findViewById(R.id.totalUsers);
        previousYearIncrement = view.findViewById(R.id.previous_year_increment);
        barChart = view.findViewById(R.id.barChart);

        // Fetch data from API
        fetchTotalProducts();
        fetchSalesAmount();
        fetchYearlySalesAmount();

        // function to show sales growth percentage
        fetchSalesGrowth();

        // Fetch data from API
        fetchDashboardData("current_stock");
        fetchDashboardData("total_categories");
        fetchDashboardData("todays_sales");
        fetchDashboardData("total_orders_current_month");
        fetchDashboardData("total_users");

        // Initialize PieChart
        pieChart = view.findViewById(R.id.pieChart);

        // Fetch product category data
        fetchProductCategories();

        // for bar chart
        btnDay = view.findViewById(R.id.btnDay);
        btnMonth = view.findViewById(R.id.btnMonth);
        btnYear = view.findViewById(R.id.btnYear);

        btnDay.setOnClickListener(v -> updateChart("day", btnDay));
        btnMonth.setOnClickListener(v -> updateChart("month", btnMonth));
        btnYear.setOnClickListener(v -> updateChart("year", btnYear));

        // Initially select day
        updateChart("day", btnDay);
        fetchRecentInvoices();

        // notification thing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            } else {
                // scheduleLowStockNotificationWork();
            }
        } else {
                // scheduleLowStockNotificationWork();
        }

        return view;
    }

    private void scheduleLowStockNotificationWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest lowStockWorkRequest =
                new PeriodicWorkRequest.Builder(LowStockWorker.class, 12, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "LowStockCheck",
                ExistingPeriodicWorkPolicy.KEEP,
                lowStockWorkRequest
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // scheduleLowStockNotificationWork();
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchTotalProducts() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<DashboardProductResponse> call = apiService.getTotalProducts();
        call.enqueue(new Callback<DashboardProductResponse>() {
            @Override
            public void onResponse(Call<DashboardProductResponse> call, Response<DashboardProductResponse> response) {
                if (response.isSuccessful()) {
                    int totalProducts = response.body().getTotalProducts();
                    totalProductsTextView.setText(""+totalProducts);
                }
            }

            @Override
            public void onFailure(Call<DashboardProductResponse> call, Throwable t) {
                // Handle failure
                totalProductsTextView.setText("-");
                t.printStackTrace();
            }
        });
    }

    private void fetchSalesAmount() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<SalesResponse> call = apiService.getSalesAmount();
        call.enqueue(new Callback<SalesResponse>() {
            @Override
            public void onResponse(Call<SalesResponse> call, Response<SalesResponse> response) {
                if (response.isSuccessful()) {
                    double totalSales = response.body().getTotalSales();
                    monthlySalesTextView.setText("₹"+totalSales+"");
                }
            }

            @Override
            public void onFailure(Call<SalesResponse> call, Throwable t) {
                // Handle failure
                monthlySalesTextView.setText("-");
                t.printStackTrace();
            }
        });
    }

    private void fetchYearlySalesAmount() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<YearlySalesResponse> call = apiService.getYearlySalesAmount();
        call.enqueue(new Callback<YearlySalesResponse>() {
            @Override
            public void onResponse(Call<YearlySalesResponse> call, Response<YearlySalesResponse> response) {
                if (response.isSuccessful()) {
                    double totalSales = response.body().getYearlySalesAmount();
                    Log.d("---------------------",String.valueOf(totalSales));
                    yearlySalesAmount.setText("Rs " + totalSales);
                }
            }

            @Override
            public void onFailure(Call<YearlySalesResponse> call, Throwable t) {
                // Handle failure
                yearlySalesAmount.setText("-");
                t.printStackTrace();
            }
        });
    }

    // other dashboard data
    private void fetchDashboardData(String type) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<JsonObject> call = apiService.getDashboardData(type);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject data = response.body();
                    if (data != null) {
                        // Handle the response based on the 'type' field
                        switch (type) {
                            case "current_stock":
                                double currentStock = getSafeDouble(data, "currentStock");
                                currentStockView.setText(String.valueOf(currentStock));
                                break;
                            case "total_categories":
                                int totalCategories = getSafeInt(data, "totalCategories");
                                totalProductCategories.setText(String.valueOf(totalCategories));
                                break;
                            case "todays_sales":
                                double totalSales = getSafeDouble(data, "totalSales");
                                todaysSales.setText("₹"+totalSales);
                                break;
                            case "total_orders_current_month":
                                int totalOrders = getSafeInt(data, "totalOrders");
                                totalOrdersCurrentMonth.setText(String.valueOf(totalOrders));
                                break;
                            case "total_users":
                                int totalUsers = getSafeInt(data, "totalUsers");
                                totalUsersView.setText(String.valueOf(totalUsers));
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                // Handle failure
            }
        });
    }

    // Helper method to get a safe double value
    private double getSafeDouble(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && !element.isJsonNull()) {
            return element.getAsDouble();
        } else {
            return 0.0; // Return default value if null
        }
    }

    // Helper method to get a safe int value
    private int getSafeInt(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && !element.isJsonNull()) {
            return element.getAsInt();
        } else {
            return 0; // Return default value if null
        }
    }


    private void fetchProductCategories() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<CategoryResponse>> call = apiService.getProductCategories();
        call.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful()) {
                    List<CategoryResponse> categoryList = response.body();
                    setUpPieChart(categoryList);
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
            }
        });
    }

    private void setUpPieChart(List<CategoryResponse> categoryList) {
        // Prepare data for PieChart
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (CategoryResponse category : categoryList) {
            // Parse the percentage string and add it to PieChart
            float percentage = Float.parseFloat(category.getPercentage());
            entries.add(new PieEntry(percentage, category.getCategory_name()));
        }

        // Create PieDataSet
        PieDataSet dataSet = new PieDataSet(entries, "Product Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Create PieData object
        PieData data = new PieData(dataSet);

        // Set PieChart properties
        pieChart.setData(data);

        // Customize the legend
        pieChart.getLegend().setEnabled(true); // Show the legend
        pieChart.getLegend().setFormSize(12f); // Adjust legend size

        // Set slice label formatter to show both percentage and category name
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Show the category name and percentage
                return String.format("%.1f%%", value);
            }
        });

        // Set the description to empty (if you want to hide it)
        pieChart.getDescription().setEnabled(false);

        // Adjust label size and position
        pieChart.setExtraOffsets(5f, 5f, 5f, 5f); // Adjust padding around chart
        pieChart.setCenterTextSize(10f); // Adjust center text size
        pieChart.setDrawHoleEnabled(false); // If you don't want a hole in the middle

        // Adjust the text size for the category labels
        dataSet.setValueTextSize(12f); // Adjust text size for slice labels
        pieChart.setEntryLabelTextSize(12f); // Set text size for entry labels (category names)

        pieChart.invalidate(); // refresh the chart
    }


    // ==== method to get sales growth percent from previous year ====
    private void fetchSalesGrowth() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<SalesGrowthResponse> call = apiService.getSalesGrowth();
        call.enqueue(new Callback<SalesGrowthResponse>() {
            @Override
            public void onResponse(Call<SalesGrowthResponse> call, Response<SalesGrowthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    double growth = response.body().getSalesGrowthPercent();
                    String percentText = (growth >= 0 ? "+" : "") + String.format("%.2f", growth) + "%";
                    String fullText = percentText + " From the previous year";

                    SpannableString spannable = new SpannableString(fullText);
                    int color = growth >= 0 ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");
                    spannable.setSpan(new ForegroundColorSpan(color), 0, percentText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    previousYearIncrement.setText(spannable);
                } else {
                    previousYearIncrement.setText("- % From the previous year");
                }
            }

            @Override
            public void onFailure(Call<SalesGrowthResponse> call, Throwable t) {
                previousYearIncrement.setText("- % From the previous year");
                t.printStackTrace();
            }
        });
    }


    // bar chart methods
    private void updateChart(String range, Button selectedButton) {
        resetButtonStyles();
        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.lavender));
        selectedButton.setTextColor(Color.WHITE);

        Map<String, String> body = new HashMap<>();
        body.put("range", range);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getSalesSummary(body).enqueue(new Callback<SalesResponseBar>() {
            @Override
            public void onResponse(Call<SalesResponseBar> call, Response<SalesResponseBar> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showBarChart(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<SalesResponseBar> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetButtonStyles() {
        int white = Color.WHITE;
        int black = Color.BLACK;
        btnDay.setBackgroundTintList(ColorStateList.valueOf(white));
        btnMonth.setBackgroundTintList(ColorStateList.valueOf(white));
        btnYear.setBackgroundTintList(ColorStateList.valueOf(white));

        btnDay.setTextColor(black);
        btnMonth.setTextColor(black);
        btnYear.setTextColor(black);
    }

    private void showBarChart(List<SalesBarEntry> data) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            SalesBarEntry item = data.get(i);
            entries.add(new BarEntry(i, item.getTotal()));
            labels.add(item.getLabel());

            // Assign different color to each bar
            colors.add(Color.rgb(
                    (int)(Math.random() * 256),
                    (int)(Math.random() * 256),
                    (int)(Math.random() * 256)
            ));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Sales");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    // recent invoices method
    private void fetchRecentInvoices() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<RecentInvoiceResponse> call = apiService.getRecentInvoices();

        call.enqueue(new Callback<RecentInvoiceResponse>() {
            @Override
            public void onResponse(Call<RecentInvoiceResponse> call, Response<RecentInvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<RecentInvoice> invoiceList = response.body().getData();
                    displayInvoices(invoiceList);
                } else {
                    Toast.makeText(getContext(), "Failed to load invoices", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecentInvoiceResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayInvoices(List<RecentInvoice> invoiceList) {
        RecyclerView recyclerView = getView().findViewById(R.id.recentInvoicesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RecentInvoiceAdapter(getContext(), invoiceList));
    }

}