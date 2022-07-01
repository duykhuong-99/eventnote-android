package com.example.sotaykiniem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DangNhapActivity extends AppCompatActivity {
    TextView tvDangKy;
    EditText etDNEmail, etDNMatkhau;
    Button btnDangNhap;
    CheckBox cbDangNhap;
    ProgressBar pbDangNhap;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fGhichu;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        //
        tvDangKy = findViewById(R.id.tvDangKy);
        etDNEmail = findViewById(R.id.etDNEmail);
        etDNMatkhau = findViewById(R.id.etDNMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        cbDangNhap = findViewById(R.id.cbDangNhap);
        pbDangNhap = findViewById(R.id.pbDangNhap);
        firebaseAuth = FirebaseAuth.getInstance();
        fGhichu = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        // đăng ký
        tvDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DangKyActivity.class));
            }
        });

        //Hiện mk
        cbDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbDangNhap.isChecked()){
                    etDNMatkhau.setTransformationMethod(null);
                }
                else {
                    etDNMatkhau.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        thongBaoDangNhap();
        // Đăng nhập
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etDNEmail.getText().toString();
                String matKhau = etDNMatkhau.getText().toString();
                if(email.isEmpty()|| matKhau.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                pbDangNhap.setVisibility(View.VISIBLE);
                // xóa ghi chú trước khi đăng nhập
                if(firebaseAuth.getCurrentUser().isAnonymous()){
                    fGhichu.collection("ghichu").document(fUser.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //
                        }
                    });
                }

                // xóa tài khoản tạm
                fUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //
                    }
                });

                //đăng nhập
                firebaseAuth.signInWithEmailAndPassword(email, matKhau).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getApplicationContext(),"Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                        pbDangNhap.setVisibility(View.GONE);
                    }
                });
            }

        });



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    /// thông báo nếu chưa đăng nhập tài khoản - ghi chú chưa được lưu lại
    private void thongBaoDangNhap() {
        final AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Ghi chú của bạn chưa được lưu, đăng ký tài khoản để lưu lại ? ")
                .setPositiveButton("Lưu ghi chú", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),DangKyActivity.class));
                        finish();
                    }
                }).setNegativeButton("Đăng nhập", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        warning.show();
    }

}