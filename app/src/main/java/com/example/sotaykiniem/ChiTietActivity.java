package com.example.sotaykiniem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChiTietActivity extends AppCompatActivity {

    FloatingActionButton fabCapNhat;
    TextView tvCTTieuDe, tvCTNoiDung;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_back));
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //
        fabCapNhat = findViewById(R.id.fabCapNhat);
        tvCTTieuDe = findViewById(R.id.tvCTTieuDe);
        tvCTTieuDe.setMovementMethod(new ScrollingMovementMethod());
        tvCTNoiDung = findViewById(R.id.tvCTNoiDung);
        toolbar = findViewById(R.id.toolbar);

        /// bind data
        Intent intent = getIntent();
        tvCTTieuDe.setText(intent.getStringExtra("tieude"));
        tvCTNoiDung.setText(intent.getStringExtra("noidung"));

        Integer colorCode = intent.getIntExtra("color", 0);
        Drawable color = getResources().getDrawable(intent.getIntExtra("color", 0));
        getSupportActionBar().setBackgroundDrawable(color);
        // stastus bar color
        getWindow().setStatusBarColor(getResources().getColor(colorCode));



        // fab cập nhật
        fabCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), CapNhatActivity.class);
                i.putExtra("color", intent.getIntExtra("color", 0));
                i.putExtra("tieude", intent.getStringExtra("tieude"));
                i.putExtra("noidung", intent.getStringExtra("noidung"));
                i.putExtra("id", intent.getStringExtra("id"));
                startActivity(i);
            }
        });

        //


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}