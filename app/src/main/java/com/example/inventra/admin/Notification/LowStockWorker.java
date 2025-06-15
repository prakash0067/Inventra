package com.example.inventra.admin.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.R;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class LowStockWorker extends Worker {

    public LowStockWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        try {
            Response<List<LowStockProduct>> response = apiService.getLowStockProducts().execute();
            if (response.isSuccessful() && response.body() != null) {
                for (LowStockProduct product : response.body()) {
                    int quantity = product.getQuantityAsInt();
                    if (quantity < 10) {
                        sendNotification(product.getProductName(), quantity);
                        Log.d(" ==== Notification API ======",product.getProductName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    private void sendNotification(String productName, int quantity) {
        String channelId = "low_stock_channel";
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Low Stock Alerts", NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.notification_13_svgrepo_com) // make sure this icon exists
                .setContentTitle("Low Stock Alert")
                .setContentText(productName + " is low: only " + quantity + " left")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(productName.hashCode(), builder.build());
    }
}
