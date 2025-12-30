package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class VerifyCodeActivity extends AppCompatActivity {

    private TextView textResend;
    private CountDownTimer countDownTimer;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        role = getIntent().getStringExtra("role");

        MaterialButton btnVerify = findViewById(R.id.btn_verify);
        textResend = findViewById(R.id.text_resend);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerifyCodeActivity.this, ResetPasswordActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeLeft = String.format(Locale.getDefault(), "Resend in %d:%02d", minutes, seconds);
                textResend.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                textResend.setText("Resend now");
                textResend.setOnClickListener(v -> {
                    startTimer();
                });
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
