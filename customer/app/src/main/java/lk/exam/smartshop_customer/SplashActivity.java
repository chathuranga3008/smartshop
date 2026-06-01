package lk.exam.smartshop_customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SmartShopCustomer_Fullscreen);
        setContentView(R.layout.activity_splash);


        ImageView imageView = findViewById(R.id.splashLog);

        Picasso.get().load(R.drawable.download)
                .resize(300,300)
                .into(imageView);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.splashProgressBar).setVisibility(View.VISIBLE);
            }
        },1000);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.splashProgressBar).setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },5000);
    }
}