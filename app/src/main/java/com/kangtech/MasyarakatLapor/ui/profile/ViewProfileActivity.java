package com.kangtech.MasyarakatLapor.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kangtech.MasyarakatLapor.R;
import com.kangtech.MasyarakatLapor.model.ProfileModel;
import com.kangtech.MasyarakatLapor.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    private List<ProfileModel> profileList;

    private static final String url_foto = Server.URL_FOTO;

    private TextView txtusername,txtnama,txttelp,txtnik;
    private CircleImageView fotoprofilemasyrakat;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        extras = getIntent().getExtras();

        ImageView backbutton = findViewById(R.id.iv_back_viewprofile);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        txtnama = findViewById(R.id.view_tv_pnama);
        txtusername = findViewById(R.id.view_tv_pusername);
        txttelp = findViewById(R.id.view_tv_ptelp);
        txtnik = findViewById(R.id.view_tv_pnik);

        fotoprofilemasyrakat = findViewById(R.id.view_civ_profile_foto);

        getProfile();

    }

    private void getProfile() {
        final String url_profile = Server.URL + "profile.php?idm=" + extras.getString("idnik_view");
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
}
