package com.pratham.mockker2;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    Snackbar noInternetSnackbar;
    ConnectivityManager.NetworkCallback networkCallback;
    ConnectivityManager connectivityManager;
    NetworkCapabilities capabilities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {

                runOnUiThread(() -> {
                    if (noInternetSnackbar != null && noInternetSnackbar.isShown()) {
                        noInternetSnackbar.dismiss();
                    }
                });
            }
            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(() -> {
                    showNoInternetBar();
                });
            }
        };
    }

    private boolean isNetworkAvailable() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;

        capabilities =connectivityManager.getNetworkCapabilities(network);

        if(capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))){
            return  true;
        }else{
            return false;
        }
    }

    private void showNoInternetBar() {
        noInternetSnackbar = Snackbar.make(findViewById(android.R.id.content),
                "No Internet Connection",
                Snackbar.LENGTH_INDEFINITE);

        noInternetSnackbar.setBackgroundTint(
                getResources().getColor(android.R.color.holo_red_dark));
        noInternetSnackbar.setTextColor(Color.WHITE);
        noInternetSnackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest request = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
        if (!isNetworkAvailable()) {
            showNoInternetBar();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (Exception ignored) {}
    }
}
