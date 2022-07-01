package com.example.sotaykiniem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sotaykiniem.model.Ghichu;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navView;
    FloatingActionButton fabThem;
    RecyclerView rvDanhSach;
    FirebaseFirestore fGhichu;
    FirestoreRecyclerAdapter<Ghichu, GhichuViewHolder> ghichuAdapter;
    FirebaseUser fUser;
    FirebaseAuth fAuth;
    View navHeader;
    TextView tvNavHoTen, tvNavEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navView = findViewById(R.id.nav_menu);
        navView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        // status bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.appcolor));

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        // lấy dữ liệu từ firebase
        fGhichu = FirebaseFirestore.getInstance();
        Query query = fGhichu.collection("ghichu").document(fUser.getUid()).collection("userGhichu").orderBy("tieude", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Ghichu> aGhichu = new FirestoreRecyclerOptions.Builder<Ghichu>()
                .setQuery(query, Ghichu.class)
                .build();

        ghichuAdapter = new FirestoreRecyclerAdapter<Ghichu, GhichuViewHolder>(aGhichu) {
            @Override
            protected void onBindViewHolder(@NonNull GhichuViewHolder holder, int position, @NonNull Ghichu model) {
                holder.tvTieuDe.setText(model.getTieude());
                holder.tvNoidung.setText(model.getNoidung());
                int color = randomColor();
                holder.cardView.setCardBackgroundColor(holder.view.getResources().getColor(color, null));
                String id = ghichuAdapter.getSnapshots().getSnapshot(position).getId();
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ChiTietActivity.class);
                        intent.putExtra("color", color);
                        intent.putExtra("tieude", model.getTieude());
                        intent.putExtra("noidung", model.getNoidung());
                        intent.putExtra("id", id);
                        v.getContext().startActivity(intent);
                    }
                });

                /// popup menu  --- xóa+cập nhật ghi chú
                ImageView ivPopupmenu = holder.view.findViewById(R.id.ivPopupMenu);
                ivPopupmenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.getMenu().add("Cập nhật").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), CapNhatActivity.class);
                                intent.putExtra("color", color);
                                intent.putExtra("tieude", model.getTieude());
                                intent.putExtra("noidung", model.getNoidung());
                                intent.putExtra("id", id);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Xóa").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                DocumentReference dr = fGhichu.collection("ghichu")
                                                              .document(fUser.getUid())
                                                              .collection("userGhichu").document(id);
                                dr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // note deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });

            }
            @NonNull
            @Override
            public GhichuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view, parent, false);
                return new GhichuViewHolder(view);
            }
        };


        // Hiện thông tin trên menu
        navHeader = navView.getHeaderView(0);
        tvNavHoTen = navHeader.findViewById(R.id.tvNavHoTen);
        tvNavEmail = navHeader.findViewById(R.id.tvNavEmail);

        if(fUser.isAnonymous()){
            tvNavHoTen.setText("Bạn chưa đăng nhập");
            tvNavEmail.setText("");
        }
        else {
            tvNavHoTen.setText(fUser.getDisplayName());
            tvNavEmail.setText(fUser.getEmail());
        }

        // hiện danh sách ghi chú
        rvDanhSach = findViewById(R.id.rvDanhSach);
        rvDanhSach.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rvDanhSach.setAdapter(ghichuAdapter);

        // fab thêm
        fabThem = findViewById(R.id.fabThem);
        fabThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), ThemActivity.class));
            }
        });
    }

    // chọn menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.mnNavThem:
                startActivity(new Intent(this, ThemActivity.class));
                break;
            case R.id.nNavDangNhap:
                if(fUser.isAnonymous()){
                    startActivity(new Intent(this, DangNhapActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(),"Bạn đã đăng nhập", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.nNavDangXuat:
                CheckUser();
                break;
            default:
                Toast.makeText(this,item.getTitle(),Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /// các ghi chú
    public class GhichuViewHolder extends RecyclerView.ViewHolder{
        TextView tvTieuDe, tvNoidung;
        View view;
        CardView cardView;
        public GhichuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvNoidung = itemView.findViewById(R.id.tvNoiDung);
            cardView = itemView.findViewById(R.id.cardView);
            view = itemView;
        }
    }

    /// Màu ghi chú
    private int randomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);
        Random ranColor = new Random();
        int number = ranColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }
    // Kiểm tra tài khoản
    private void  CheckUser(){
        if (fUser.isAnonymous()){
            thongBao();
        }
        else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
            finish();
        }
    }
    // thông báo khi đăng xuất
    private void thongBao(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn chưa đăng nhập, tất cả ghi chú sẽ bị xóa")
                .setPositiveButton("Lưu lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), DangKyActivity.class));
                    }
                }).setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
                                finish();
                            }
                        });
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ghichuAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(ghichuAdapter != null){
            ghichuAdapter.stopListening();
        }
    }


}