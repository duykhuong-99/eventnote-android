package com.example.sotaykiniem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class CapNhatActivity extends AppCompatActivity {
    EditText etCNNoidung, etCNTieude;
    ProgressBar pbDone;
    Intent data;
    FloatingActionButton fabCapNhat;
    FirebaseFirestore fGhiChu;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cap_nhat);

                                /*start*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_back));
        getSupportActionBar().setTitle("Cập nhật nội dung");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //
        etCNTieude = findViewById(R.id.etCapNhatTieuDe);
        etCNNoidung =findViewById(R.id.etCapNhatNoiDung);
        pbDone = findViewById(R.id.pbDone);
        fabCapNhat = findViewById(R.id.fabDone);

        // bind data
        data = getIntent();
        // kiểm tra tiêu đề rỗng
        if (data.getStringExtra("tieude").equals("<Không có tiêu đề>"))
            {
            etCNTieude.setHint("Nhập tiêu đề");
            etCNTieude.setText("");
            }
        else
            etCNTieude.setText(data.getStringExtra("tieude"));
        // kiểm tra nội dung rỗng
        if (data.getStringExtra("noidung").equals("<Chưa nhập nội dung>")){
            etCNNoidung.setHint("Nhập nội dung");
            etCNNoidung.setText("");
        }
        else
            etCNNoidung.setText(data.getStringExtra("noidung"));
        //
        Integer colorCode = data.getIntExtra("color", 0);
        Drawable color = getResources().getDrawable(data.getIntExtra("color", 0));
        getSupportActionBar().setBackgroundDrawable(color);
        // stastus bar color
        getWindow().setStatusBarColor(getResources().getColor(colorCode));


        ////Cập nhật
        fGhiChu = fGhiChu.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        fabCapNhat.setOnClickListener(v -> {
            String nTieude = etCNTieude.getText().toString();
            String nNoidung =  etCNNoidung.getText().toString();
            if(nTieude.isEmpty()&&nNoidung.isEmpty()){
                Toast.makeText(v.getContext(), "Chưa nhập dữ liệu", Toast.LENGTH_SHORT).show();
                etCNTieude.requestFocus();
            }
            if(nTieude.isEmpty()){
                nTieude = "<Không có tiêu đề>";
            }
            if(nNoidung.isEmpty()){
                nNoidung ="<Chưa nhập nội dung>";
            }
            pbDone.setVisibility(View.VISIBLE);

            DocumentReference dRef = fGhiChu.collection("ghichu")
                                            .document(fUser.getUid())
                                            .collection("userGhichu")
                                            .document(data.getStringExtra("id"));
            Map<String, Object> ghichu = new HashMap<>();
            ghichu.put("tieude", nTieude);
            ghichu.put("noidung", nNoidung);

            dRef.update(ghichu).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(v.getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pbDone.setVisibility(View.VISIBLE);
                }
            });

        });



    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ChiTietActivity.class);
        intent.putExtra("color", data.getIntExtra("color",0));
        intent.putExtra("tieude", data.getStringExtra("tieude"));
        intent.putExtra("noidung", data.getStringExtra("noidung"));
        intent.putExtra("id", data.getStringExtra("id"));
        startActivity(intent);
    }
}