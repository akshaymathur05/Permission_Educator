package com.example.permissioneducator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PermissionInfo extends AppCompatActivity {

    TextView permName, permInfo, permCategory;
    String pn, pi, pc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_info);

        permName = findViewById(R.id.perm_name);
        permInfo = findViewById(R.id.perm_info);
        permCategory = findViewById(R.id.perm_category);

        Intent intent = getIntent();
        pn = intent.getStringExtra("permName");
        pi = intent.getStringExtra("permAbout");
        pc = intent.getStringExtra("permCategory");

        permName.setText(pn);
        permInfo.setText(pi);
        permCategory.setText(pc);

    }
}