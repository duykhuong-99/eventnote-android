package com.example.sotaykiniem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ThemActivity extends AppCompatActivity {

    FloatingActionButton fabLuu;
    FirebaseFirestore ffGhiChu;
    EditText etThemTieuDe, etThemNoiDung;
    ProgressBar pbThem;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_back));
        getSupportActionBar().setTitle("Thêm mới");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //
        etThemTieuDe = findViewById(R.id.etThemTieuDe);
        etThemNoiDung = findViewById(R.id.etThemNoiDung);
        fabLuu = findViewById(R.id.fabLuu);
        pbThem = findViewById(R.id.pbThem);

        // Lưu ghi chú
        ffGhiChu = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();


        fabLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTieude = etThemTieuDe.getText().toString();
                String nNoidung =  etThemNoiDung.getText().toString();
                if(nTieude.isEmpty()&&nNoidung.isEmpty()){
                    Toast.makeText(v.getContext(), "Chưa nhập dữ liệu", Toast.LENGTH_SHORT).show();
                    etThemTieuDe.requestFocus();
                }
                if(nTieude.isEmpty()){
                    nTieude = "<Không có tiêu đề>";
                }
                if(nNoidung.isEmpty()){
                    nNoidung ="<Chưa nhập nội dung>";
                }

                pbThem.setVisibility(View.VISIBLE);
                DocumentReference dRef = ffGhiChu.collection("ghichu")
                                                 .document(fUser.getUid())
                                                 .collection("userGhichu").document();
                Map<String, Object> ghichu = new HashMap<>();
                ghichu.put("tieude", nTieude);
                ghichu.put("noidung", nNoidung);
                dRef.set(ghichu).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getContext(), "Đã lưu", Toast.LENGTH_SHORT).show();
                        onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        pbThem.setVisibility(View.VISIBLE);
                    }
                });
            }
        });



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}