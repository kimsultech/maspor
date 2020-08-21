package com.kangtech.MasyarakatLapor.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kangtech.MasyarakatLapor.R;
import com.kangtech.MasyarakatLapor.adapter.PengaduanListAdapter;
import com.kangtech.MasyarakatLapor.ui.laporan.TambahLaporanActivity;
import com.kangtech.MasyarakatLapor.ui.profile.ProfileActivity;
import com.kangtech.MasyarakatLapor.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kangtech.MasyarakatLapor.ui.login.LoginActivity.maspor_preferences;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ArrayList<HashMap<String, String>> list_data;

    private RecyclerView listpengaduan;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    LinearLayoutManager linearLayoutManager;

    SharedPreferences sharedpreferences;

    SwipeRefreshLayout pullTOpengaduan;

    NestedScrollView nestedscrollview;
    TextView fabtitle;
    LinearLayout fab_full;
    private FloatingActionButton fab_icon;

    private static final String url_foto = Server.URL_FOTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        listpengaduan = findViewById(R.id.rv_mainlistpengaduan);

        requestQueue = Volley.newRequestQueue(this);

        list_data = new ArrayList<HashMap<String, String>>();

        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // tambah refresh
        pullTOpengaduan = findViewById(R.id.refreshpull_pengaduan);
        pullTOpengaduan.setOnRefreshListener(MainActivity.this);
        //pullTORefreshBarang.setRefreshing(true);
        pullTOpengaduan.setColorSchemeResources(R.color.purple_kim,R.color.red_kim, R.color.grey_kim);

        // get local data saved
        sharedpreferences = getSharedPreferences(maspor_preferences, Context.MODE_PRIVATE);

        TextView tv_uname = findViewById(R.id.tv_mainuname);
        tv_uname.setText(sharedpreferences.getString("username", "nama pengguna"));

        // Mengambil data Pengaduan dari Server
        String statusnya = "petugas";
        String statusnya2 = "admin";
        if (statusnya.equals(sharedpreferences.getString("petugas", "apaan"))) {
            getPengaduanPetugas();
        } else if (statusnya2.equals(sharedpreferences.getString("petugas", "apaan"))) {
            getPengaduanPetugas();
        } else {
            getPengaduan();
        }

        // Profil gambar set Klik
        CircleImageView profilepic = findViewById(R.id.profile_image);
        Glide.with(profilepic)
                .load(url_foto + sharedpreferences.getString("foto", "nullfoto"))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(profilepic);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this , ProfileActivity.class);
                startActivity(i);
            }
        });

        // add Floating untuk Add Pengaduan
        nestedscrollview = findViewById(R.id.nsview_pengaduan);
        fabtitle = findViewById(R.id.fab_text_br);
        fab_full = findViewById(R.id.fab_full_br);
        fab_icon = findViewById(R.id.fab_icon_br);

        listpengaduan.setNestedScrollingEnabled(false);

        String tipe = "petugas";
        String tipe2 = "admin";
        if (tipe.equals(sharedpreferences.getString("petugas", ""))) {
            fab_full.setVisibility(View.GONE);
            fab_icon.setVisibility(View.GONE);
        } if (tipe2.equals(sharedpreferences.getString("petugas", ""))) {
            fab_full.setVisibility(View.GONE);
            fab_icon.setVisibility(View.GONE);
        } else {
            // kosong
        }
        fab_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_tambah_pengaduan();
            }
        });
        fab_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_tambah_pengaduan();
            }
        });

        fab_handle();

    }

    private void activity_tambah_pengaduan() {
        Intent intent = new Intent(MainActivity.this, TambahLaporanActivity.class);
        startActivity(intent);
    }

    private void getPengaduanPetugas() {
        final String url_pengaduan = Server.URL + "get_pengaduan_petugas.php";
        stringRequest = new StringRequest(Request.Method.GET, url_pengaduan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                list_data.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("listpengaduanpetugas");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("idpengaduan", json.getString("id_pengaduan"));
                        map.put("nama", json.getString("nama"));
                        map.put("isilaporan", json.getString("isi_laporan"));
                        map.put("tanggal", json.getString("tgl_pengaduan"));
                        map.put("lampiranfoto", json.getString("foto"));
                        map.put("status", json.getString("status"));
                        map.put("fotop", json.getString("foto_profile"));
                        list_data.add(map);
                        PengaduanListAdapter adapter = new PengaduanListAdapter(MainActivity.this, list_data);
                        listpengaduan.setLayoutManager(linearLayoutManager);
                        listpengaduan.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    private void getPengaduan() {
        final String url_pengaduan = Server.URL + "get_pengaduan.php?idpm=" + sharedpreferences.getString("id", "id nya");
        stringRequest = new StringRequest(Request.Method.GET, url_pengaduan, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                list_data.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("listpengaduan");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("idpengaduan", json.getString("id_pengaduan"));
                        map.put("nama", json.getString("nama"));
                        map.put("isilaporan", json.getString("isi_laporan"));
                        map.put("tanggal", json.getString("tgl_pengaduan"));
                        map.put("lampiranfoto", json.getString("foto"));
                        map.put("status", json.getString("status"));
                        map.put("fotop", json.getString("foto_profile"));
                        list_data.add(map);
                        PengaduanListAdapter adapter = new PengaduanListAdapter(MainActivity.this, list_data);
                        listpengaduan.setLayoutManager(linearLayoutManager);
                        listpengaduan.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    private void fab_handle() {
        nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fabtitle.setVisibility(View.GONE);
                } else if (scrollX == scrollY) {
                    fabtitle.setVisibility(View.VISIBLE);
                } else {
                    fabtitle.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullTOpengaduan.setRefreshing(false);
                String statusnya = "petugas";
                String statusnya2 = "admin";
                if (statusnya.equals(sharedpreferences.getString("petugas", "apaan"))) {
                    getPengaduanPetugas();
                } else if (statusnya2.equals(sharedpreferences.getString("petugas", "apaan"))) {
                    getPengaduanPetugas();
                } else {
                    getPengaduan();
                }
            }
        }, 1500);
    }
}
