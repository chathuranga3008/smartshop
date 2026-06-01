package lk.exam.smartshop_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import lk.exam.smartshop_admin.model.AuthResponce;
import lk.exam.smartshop_admin.service.RetrofitClient;
import lk.exam.smartshop_admin.service.SmartShopService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        Retrofit retrofit = RetrofitClient.getClient();

        SmartShopService loginService = retrofit.create(SmartShopService.class);


        TextView vcodeContent = findViewById(R.id.vcode);
        TextView emailContent = findViewById(R.id.adminemailLogin);
        TextView vTitle = findViewById(R.id.textView11);
        TextView vlogin = findViewById(R.id.login);

        emailContent.setVisibility(View.VISIBLE);
        vcodeContent.setVisibility(View.GONE);
        vTitle.setVisibility(View.GONE);
        vlogin.setVisibility(View.GONE);

        findViewById(R.id.sendVCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailView = findViewById(R.id.adminemailLogin);
                String emailAddress = emailView.getText().toString();

                Call<AuthResponce> call = loginService.sendVcode(emailAddress);
                call.enqueue(new Callback<AuthResponce>() {
                    @Override
                    public void onResponse(Call<AuthResponce> call, Response<AuthResponce> response) {
                        if (response.isSuccessful()) {
                            AuthResponce responce = response.body();
                            if (responce != null) {
                                String status = responce.getStatus();

                                if (status.equals("success")) {

                                    vTitle.setVisibility(View.VISIBLE);
                                    vcodeContent.setVisibility(View.VISIBLE);
                                    vlogin.setVisibility(View.VISIBLE);

                                    Toast.makeText(getApplicationContext(), "Check your email inbox and enter verification code", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                                }


                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Try again later.1", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponce> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

                    }

                });
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailView = findViewById(R.id.adminemailLogin);

                EditText vcodeView = findViewById(R.id.vcode);
                String vcode = vcodeView.getText().toString();

                Call<AuthResponce> call = loginService.login(vcode);
                call.enqueue(new Callback<AuthResponce>() {
                    @Override
                    public void onResponse(Call<AuthResponce> call, Response<AuthResponce> response) {
                        if (response.isSuccessful()) {
                            AuthResponce responce = response.body();
                            if (responce != null) {
                                String status = responce.getStatus();
                                if (status.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                                    vcodeView.setText("");
                                    emailView.setText("");

                                    emailContent.setVisibility(View.VISIBLE);
                                    vcodeContent.setVisibility(View.GONE);
                                }

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                            vcodeView.setText("");
                            emailView.setText("");

                            emailContent.setVisibility(View.VISIBLE);
                            vcodeContent.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponce> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                        vcodeView.setText("");
                        emailView.setText("");

                        emailContent.setVisibility(View.VISIBLE);
                        vcodeContent.setVisibility(View.GONE);

                    }

                });

            }
        });

    }
}