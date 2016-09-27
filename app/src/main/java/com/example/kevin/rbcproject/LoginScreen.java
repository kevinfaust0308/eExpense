package com.example.kevin.rbcproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;
    private TextView mGuestButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mGuestButton = (TextView) findViewById(R.id.guestLogin);

        //NOT FUNCTIONAL
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                Boolean loginSuccess = processLoginRequest(username, password);
                if (loginSuccess) {
                    Intent i = new Intent(LoginScreen.this, MainActivity.class);
                    i.putExtra(SharedPreferenceFields.LOGGED_IN, true);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else {
                    findViewById(R.id.failedLoginText).setVisibility(View.VISIBLE);
                }
            }
        });

        mGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginScreen.this, MainActivity.class);
                i.putExtra(SharedPreferenceFields.LOGGED_IN, false);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
    }


    public boolean processLoginRequest(String username, String password) {
        //connect user to RBC if they have valid RBC credentials
        return true;
    }

}
