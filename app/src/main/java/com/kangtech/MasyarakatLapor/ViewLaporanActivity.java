package com.kangtech.MasyarakatLapor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputEditText;
import com.kangtech.MasyarakatLapor.model.ViewLaporanModel;
import com.kangtech.MasyarakatLapor.model.ViewTanggapanModel;
import com.kangtech.MasyarakatLapor.util.RequestHandler;
import com.kangtech.MasyarakatLapor.util.Server;
import com.ortiz.touchview.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kangtech.MasyarakatLapor.LoginActivity.maspor_preferences;
import static com.kangtech.MasyarakatLapor.util.Server.URL;

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

    private Dialog tanggapanDialog;
    private TextInputEditText tanggapan;
    RadioButton radiobtn;
    RadioGroup statusprosel;
    private Button tanggapankirim;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    String date;

    SharedPreferences sharedpreferences;

    CircleImageView civfoto;

    CircleImageView civpetugas;

    private static final String url_foto = Server.URL_FOTO;

    Button btn_laporanselesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_laporan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        extras = getIntent().getExtras();


        namaprofile = findViewById(R.id.tv_namapengaduan_view);
        tgllaporan = findViewById(R.id.tv_tanggalpengaduan_view);
        civfoto = findViewById(R.id.civ_fotopengaduan_view);

        isilaporan = findViewById(R.id.tv_isilaporan_view);
        lampiranlaporan = findViewById(R.id.iv_lampiran_view);

        statuslaporan = findViewById(R.id.tv_statuspengaduan_view);

        sharedpreferences = getSharedPreferences(maspor_preferences, Context.MODE_PRIVATE);

        // tanggapan
        namapetugas = findViewById(R.id.tv_namapengaduan_view11);
        isitanggapan = findViewById(R.id.tv_isitanggapan);
        waktutanggapan = findViewById(R.id.tv_waktutanggapan);

        cvtanggapan = findViewById(R.id.cv_tanggapan_view);

        civpetugas = findViewById(R.id.civ_fotopengaduan_view11);


        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(calendar.getTime());

        // add Floating untuk Add Pengaduan
        nestedscrollview = findViewById(R.id.nsview_pengaduan_view);
        fabtitle = findViewById(R.id.fab_text_br_tanggapan);
        fab_full = findViewById(R.id.fab_full_br_tanggapan);
        fab_icon = findViewById(R.id.fab_icon_br_tanggapan);

        String tipe = "bukanpetugas";
        String tipe2 = "admin";
        if (tipe.equals(sharedpreferences.getString("petugas", ""))) {
            fab_full.setVisibility(View.GONE);
            fab_icon.setVisibility(View.GONE);
        } else if (tipe2.equals(sharedpreferences.getString("petugas", ""))) {
            fab_full.setVisibility(View.GONE);
            fab_icon.setVisibility(View.GONE);
        } else {
            // kosong
        }
        fab_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggapanDialog.show();
            }
        });
        fab_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggapanDialog.show();
            }
        });

        /* --------------------------------- */

        // Ambil Laporan dari method
        getLaporan();

        // Ambil Tanggapan
        getTanggapan();

        // Menampilkan Dialog add Tanggapan
        customTanggapanDialog();

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
                                        profile.getString("status"),
                                        profile.getString("fotop")
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

                            Glide.with(civfoto)
                                    .load(url_foto + viewLaporan.get(0).getFotop())
                                    .onlyRetrieveFromCache(true)
                                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                                    .dontAnimate()
                                    .into(civfoto);


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
                                        profile.getString("nama_petugas"),
                                        profile.getString("fotopetugas")
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

                                Glide.with(civpetugas)
                                        .load(url_foto + viewTanggapan.get(0).getFotopetugas())
                                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                                        .into(civpetugas);

                                fab_full.setVisibility(View.GONE);
                                fab_icon.setVisibility(View.GONE);
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

    private void customTanggapanDialog() {
        tanggapanDialog = new Dialog(this);
        tanggapanDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tanggapanDialog.setContentView(R.layout.dialog_addtanggapan);
        tanggapanDialog.setCancelable(true);

        statusprosel = tanggapanDialog.findViewById(R.id.rb_grouptanggapan);


        tanggapan = tanggapanDialog.findViewById(R.id.tiet_tanggapan);

        tanggapankirim = tanggapanDialog.findViewById(R.id.btn_tanggapi_kirim);
        tanggapankirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTanggapan();
                tanggapanDialog.dismiss();
            }
        });
    }


    private void sendTanggapan() {
        final String url_kirim_tanggapan = URL + "add_tanggapan.php";

        int selectedId = statusprosel.getCheckedRadioButtonId();
        radiobtn = tanggapanDialog.findViewById(selectedId);
        final String tatus = radiobtn.getText().toString().trim();

        String tanggapannn = tanggapan.getText().toString();

        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ViewLaporanActivity.this, "Mengirim Tanggapan...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ViewLaporanActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                //params.put("id_tanggapan", null);
                params.put("id_pengaduan_app", extras.getString("idpengaduanview"));
                params.put("tgl_tanggapan_app", date);
                params.put("tanggapan_app", tanggapannn);
                params.put("id_petugas_app", sharedpreferences.getString("id", ""));

                params.put("status_app", tatus);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_kirim_tanggapan, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
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
