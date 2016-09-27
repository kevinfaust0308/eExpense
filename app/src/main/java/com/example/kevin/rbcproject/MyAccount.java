package com.example.kevin.rbcproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MyAccount extends AppCompatActivity {

    private TextView mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info);

        mSave = (TextView) findViewById(R.id.save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update user details
                finish();
            }
        });

    }
}
