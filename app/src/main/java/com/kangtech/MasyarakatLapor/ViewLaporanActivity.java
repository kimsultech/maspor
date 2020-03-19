package com.kangtech.MasyarakatLapor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kangtech.MasyarakatLapor.data_model.ViewLaporanModel;
import com.kangtech.MasyarakatLapor.data_model.ViewTanggapanModel;
import com.kangtech.MasyarakatLapor.util.Server;
import com.ortiz.touchview.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewLaporanActivity extends AppCompatActivity {

    private TextView namaprofile, tgllaporan, isilaporan, statuslaporan;
    private TouchImageView lampiranlaporan;

    private TextView namapetugas, isitanggapan, waktutanggapan;

    List<ViewLaporanModel> viewLaporan;

    List<ViewTanggapanModel> viewTanggapan;

    Bundle extras;

    private NestedScrollView nestedscrollview;
    private TextView fabtitle;
    private LinearLayout fab_full;
    private FloatingActionButton fab_icon;

    CardView cvtanggapan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_laporan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        extras = getIntent().getExtras();


        namaprofile = findViewById(R.id.tv_namapengaduan_view);
        tgllaporan = findViewById(R.id.tv_tanggalpengaduan_view);

        isilaporan = findViewById(R.id.tv_isilaporan_view);
        lampiranlaporan = findViewById(R.id.iv_lampiran_view);

        statuslaporan = findViewById(R.id.tv_statuspengaduan_view);

        // tanggapan
        namapetugas = findViewById(R.id.tv_namapengaduan_view11);
        isitanggapan = findViewById(R.id.tv_isitanggapan);
        waktutanggapan = findViewById(R.id.tv_waktutanggapan);

        cvtanggapan = findViewById(R.id.cv_tanggapan_view);

        // add Floating untuk Add Pengaduan
        nestedscrollview = findViewById(R.id.nsview_pengaduan_view);
        fabtitle = findViewById(R.id.fab_text_br_tanggapan);
        fab_full = findViewById(R.id.fab_full_br_tanggapan);
        fab_icon = findViewById(R.id.fab_icon_br_tanggapan);

        // Ambil Laporan dari method
        getLaporan();

        // Ambil Tanggapan
        getTanggapan();

        // Floating Scroll Animated
        fab_handle();


    }

    private void getLaporan() {
        final String url_profile = Server.URL + "view_pengaduan.php?idpv=" + extras.getString("idpengaduanview");
        final String url_image = Server.URL_IMG;
        viewLaporan = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_profile, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject profile = array.getJSONObject(i);

                                viewLaporan.add(new ViewLaporanModel(
                                        profile.getString("id_nik"),
                                        profile.getString("nama"),
                                        profile.getString("tgl_pengaduan"),
                                        profile.getString("isi_laporan"),
                                        profile.getString("foto"),
                                        profile.getString("status")
                                ));
                            }

                            namaprofile.setText(viewLaporan.get(0).getNama());
                            tgllaporan.setText(viewLaporan.get(0).getTgl());
                            isilaporan.setText(viewLaporan.get(0).getIsilaporan());
                            Glide.with(lampiranlaporan)
                                    .load(url_image + viewLaporan.get(0).getFotolaporan())
                                    .transition(new DrawableTransitionOptions()
                                            .crossFade())
                                    .placeholder(R.mipmap.ic_launcher)
                                    .into(lampiranlaporan);


                            String nofoto = "tanpagambar";
                            if (nofoto.equals(viewLaporan.get(0).getFotolaporan())) {
                                lampiranlaporan.setVisibility(View.GONE);
                            } else {
                                //biarkan eweuh
                            }

                            String statustanggapi = "selesai";
                            if (statustanggapi.equals(viewLaporan.get(0).getStatus())) {
                                statuslaporan.setText("SELESAI");
                            }

                            String statusproses = "proses";
                            if (statusproses.equals(viewLaporan.get(0).getStatus())) {
                                statuslaporan.setText("SEDANG DIPROSES");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void getTanggapan() {
        final String url_profile = Server.URL + "view_tanggapan.php?idpvt=" + extras.getString("idpengaduanview");
        //final String url_image = Server.URL_IMG;
        viewTanggapan = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_profile, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject profile = array.getJSONObject(i);

                                viewTanggapan.add(new ViewTanggapanModel(
                                        profile.getString("id_tanggapan"),
                                        profile.getString("id_pengaduan"),
                                        profile.getString("tgl_tanggapan"),
                                        profile.getString("tanggapan"),
                                        profile.getString("id_petugas"),
                                        profile.getString("nama_petugas")
                                ));
                            }
                            Log.d("huyuyuy", response);

                            response = response.trim();
                            if (response.equals("[]")) {
                                cvtanggapan.setVisibility(View.GONE);
                            } else {
                                cvtanggapan.setVisibility(View.VISIBLE);
                                namapetugas.setText(viewTanggapan.get(0).getNamapetugas());
                                isitanggapan.setText(viewTanggapan.get(0).getTanggapan());
                                waktutanggapan.setText(viewTanggapan.get(0).getTgltanggapan());
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(this).add(stringRequest);
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
}
