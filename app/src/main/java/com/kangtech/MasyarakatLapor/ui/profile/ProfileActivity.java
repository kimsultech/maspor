package com.kangtech.MasyarakatLapor.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kangtech.MasyarakatLapor.LoginActivity;
import com.kangtech.MasyarakatLapor.R;
import com.kangtech.MasyarakatLapor.model.ProfileModel;
import com.kangtech.MasyarakatLapor.model.ProfilePetugasModel;
import com.kangtech.MasyarakatLapor.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kangtech.MasyarakatLapor.LoginActivity.maspor_preferences;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();


    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";

    public static final String TAG_NAMA     = "nama";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    List<ProfileModel> profileList;
    List<ProfilePetugasModel> profilepetugasList;

    SharedPreferences sharedpreferences;

    TextView txtusername,txtnama,txttelp,txtnik;
    TextView txtidpetugas, txttipe;

    String status_petugas;
    String id;
    private String status_petugas2;

    private static final String url_foto = Server.URL_FOTO;

    CircleImageView fotoprofilemasyrakat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        txtnama = findViewById(R.id.tv_pnama);
        txtusername = findViewById(R.id.tv_pusername);
        txttelp = findViewById(R.id.tv_ptelp);
        txtnik = findViewById(R.id.tv_pnik);
        // Petugas
        txttipe = findViewById(R.id.tv_tipe);
        txtidpetugas = findViewById(R.id.tv_idpetugas);

        fotoprofilemasyrakat = findViewById(R.id.civ_profile_foto);

        LinearLayout llidpetugas = findViewById(R.id.ll_idpetugas);
        LinearLayout llnik = findViewById(R.id.ll_nik);

        ConstraintLayout cltipe = findViewById(R.id.cl_ptipe);



        sharedpreferences = getSharedPreferences(maspor_preferences, Context.MODE_PRIVATE);

        // Cek status petugas jika TRUE maka info untuk petugas
        status_petugas = "petugas";
        status_petugas2 = "admin";
        id = sharedpreferences.getString(TAG_ID, null);

        if (status_petugas.equals(sharedpreferences.getString("petugas", "apaan"))) {
            // set Visibilitas
            llnik.setVisibility(View.GONE);
            llidpetugas.setVisibility(View.VISIBLE);
            cltipe.setVisibility(View.VISIBLE);

            getProfilePetugas();
        } else if (status_petugas2.equals(sharedpreferences.getString("petugas", "adminnn"))) {
            // set Visibilitas
            llnik.setVisibility(View.GONE);
            llidpetugas.setVisibility(View.VISIBLE);
            cltipe.setVisibility(View.VISIBLE);

            getProfilePetugas();
        } else {
            // Masyarakat
            getProfile();
        }




        // Keluar dan Menghapus info Data Login
        Button btnexit = findViewById(R.id.btn_exit);
        btnexit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(LoginActivity.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });

    }
    private void getProfile() {
        final String url_profile = Server.URL + "profile.php?idm=" + sharedpreferences.getString("id", "apaan");
        profileList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_profile, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject profile = array.getJSONObject(i);

                                profileList.add(new ProfileModel(
                                        profile.getString("id_nik"),
                                        profile.getString("nama"),
                                        profile.getString("username"),
                                        profile.getString("telp"),
                                        profile.getString("fotop")
                                ));
                            }

                            txtusername.setText(profileList.get(0).getUsername());
                            txtnama.setText(profileList.get(0).getNama());
                            txttelp.setText(profileList.get(0).getTelp());
                            txtnik.setText(String.valueOf(profileList.get(0).getId_nik()));

                            Glide.with(fotoprofilemasyrakat)
                                    .load(url_foto + profileList.get(0).getFotop())
                                    .onlyRetrieveFromCache(true)
                                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                                    .dontAnimate()
                                    .into(fotoprofilemasyrakat);

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

    private void getProfilePetugas() {
        final String url_profile_petugas = Server.URL + "profile_petugas.php?idp=" + sharedpreferences.getString("id", "apaan");
        profilepetugasList = new ArrayList<>();
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url_profile_petugas, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject profilepetugas = array.getJSONObject(i);

                                profilepetugasList.add(new ProfilePetugasModel(
                                        profilepetugas.getString("id_petugas"),
                                        profilepetugas.getString("nama"),
                                        profilepetugas.getString("username"),
                                        profilepetugas.getString("telp"),
                                        profilepetugas.getString("tipe"),
                                        profilepetugas.getString("fotopetugas")

                                ));
                            }

                            txtusername.setText(profilepetugasList.get(0).getUsername());
                            txtnama.setText(profilepetugasList.get(0).getNama());
                            txttelp.setText(profilepetugasList.get(0).getTelp());
                            txttipe.setText(profilepetugasList.get(0).getTipe());
                            txtidpetugas.setText(String.valueOf(profilepetugasList.get(0).getId_petugas()));

                            Glide.with(fotoprofilemasyrakat)
                                    .load(url_foto + profilepetugasList.get(0).getFotopetugas())
                                    .onlyRetrieveFromCache(true)
                                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                                    .dontAnimate()
                                    .into(fotoprofilemasyrakat);

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

        Volley.newRequestQueue(this).add(stringRequest2);
    }
}
