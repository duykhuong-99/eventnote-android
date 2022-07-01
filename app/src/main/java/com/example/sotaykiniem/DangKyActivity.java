package com.example.sotaykiniem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class DangKyActivity extends AppCompatActivity {

    TextView tvDangNhap;
    EditText etHoTen, etDKEmail, etDKMatkhau, getEtDKMatkhau2;
    Button btnDangky;
    CheckBox cbDangKy;
    ProgressBar pbDangKy;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        //
        tvDangNhap = findViewById(R.id.tvDangNhap);
        etHoTen = findViewById(R.id.etHoTen);
        etDKEmail = findViewById(R.id.etDKEmail);
        etDKMatkhau = findViewById(R.id.etDKMatKhau);
        getEtDKMatkhau2 = findViewById(R.id.etDKMatKhau2);
        btnDangky = findViewById(R.id.btnDangKy);
        cbDangKy = findViewById(R.id.cbDangKy);
        pbDangKy = findViewById(R.id.pbDangKy);
        firebaseAuth = FirebaseAuth.getInstance();


        //
        tvDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DangNhapActivity.class));
            }
        });

        //hiện mk
        cbDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbDangKy.isChecked()){
                    etDKMatkhau.setTransformationMethod(null);
                    getEtDKMatkhau2.setTransformationMethod(null);
                }
                else {
                    etDKMatkhau.setTransformationMethod(new PasswordTransformationMethod());
                    getEtDKMatkhau2.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        // đăng ký tài khoản
        btnDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = etHoTen.getText().toString();
                String email = etDKEmail.getText().toString();
                String matKhau = etDKMatkhau.getText().toString();
                String matkhau2 = getEtDKMatkhau2.getText().toString();
                if (hoTen.isEmpty()|| email.isEmpty()||matKhau.isEmpty()|matkhau2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!matKhau.equals(matkhau2)) {
                    getEtDKMatkhau2.setError("Mật khẩu không trùng khớp");
                }
                AuthCredential authCredential = EmailAuthProvider.getCredential(email, matKhau);
                firebaseAuth.getCurrentUser().linkWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getApplicationContext(),"Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        FirebaseUser fu = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(hoTen)
                                .build();
                        fu.updateProfile(request);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //




    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


}