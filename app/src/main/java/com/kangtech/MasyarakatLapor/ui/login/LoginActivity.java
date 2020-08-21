package com.kangtech.MasyarakatLapor.ui.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.kangtech.MasyarakatLapor.ui.MainActivity;
import com.kangtech.MasyarakatLapor.R;
import com.kangtech.MasyarakatLapor.controller.AppController;
import com.kangtech.MasyarakatLapor.model.ProfileModel;
import com.kangtech.MasyarakatLapor.model.ProfilePetugasModel;
import com.kangtech.MasyarakatLapor.util.RequestHandler;
import com.kangtech.MasyarakatLapor.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kangtech.MasyarakatLapor.util.Server.URL;


public class LoginActivity extends AppCompatActivity {

    ProgressDialog pDialog;

    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "login.php";
    private String url_petugas = Server.URL + "login_petugas.php";

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String id, username;
    public static final String maspor_preferences = "login_shared";
    public static final String session_status = "session_status";

    private List<ProfileModel> profileList;
    private List<ProfilePetugasModel> profilepetugasList;
    private String setTipe;
    private String usernamepetugas;

    private static final String url_reg = URL + "registrasi_masyarakat.php";

    TextInputEditText reg_nik,reg_name,reg_username,reg_password,reg_telp;
    Button btnreg;
    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //Info Tidak ada Jaringan
        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada Koneksi Internet",
                        Toast.LENGTH_LONG).show();
            }
        } //end Info Jaringan


        //Pidah dari Login ke Register dan sebaliknya
        viewFlipper = findViewById(R.id.vf_login);

        //animation untuk viewfliper
        Animation slide_in_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        Animation slide_out_left = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

        //set Anim
        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_left);

        //Pindah ke Register
        TextView btn_register = findViewById(R.id.btn_register_vf);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1);
            }
        });

        //Pindah ke Login
        TextView btn_login = findViewById(R.id.btn_login_vf);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        //Pindah ke Login Petugas
        TextView btn_login_petugas = findViewById(R.id.btn_masukpetugas_vf);
        btn_login_petugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(2);
            }
        });

        //Pindah ke Login Masyarakat dari Log Petugas
        TextView btn_login_masyarakat = findViewById(R.id.btn_loginmasyarakat_vf);
        btn_login_masyarakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(0);
            }
        });


        // Variable Login
        final EditText usernameEditText = findViewById(R.id.editTextUsername);
        final EditText passwordEditText = findViewById(R.id.editTextPassword);
        final Button loginButton = findViewById(R.id.btn_login);

        // Variable Login Petugas
        final EditText usernameEditTextPetugas = findViewById(R.id.editTextUsernamePetugas);
        final EditText passwordEditTextPetugas = findViewById(R.id.editTextPasswordPetugas);
        final Button loginButtonPetugas = findViewById(R.id.btn_login_petugas);

        // Regsiter
        reg_nik = findViewById(R.id.tie_reg_nik);
        reg_name = findViewById(R.id.tie_reg_name);
        reg_username = findViewById(R.id.tie_reg_username);
        reg_password = findViewById(R.id.tie_reg_password);
        reg_telp = findViewById(R.id.tie_reg_telp);

        btnreg = findViewById(R.id.btn_register);
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regMasyarakat();
            }
        });


        // Cek session login jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(maspor_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        id = sharedpreferences.getString(TAG_ID, null);
        username = sharedpreferences.getString(TAG_USERNAME, null);

        if (session) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }

        // Button Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // mengecek kolom yang kosong
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password);
                    } else {
                        Toast.makeText(getApplicationContext(), "Tidak ada Koneksi Internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Button Login Petugas
        loginButtonPetugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                usernamepetugas = usernameEditTextPetugas.getText().toString();
                String passwordpetugas = passwordEditTextPetugas.getText().toString();


                // mengecek kolom yang kosong
                if (usernamepetugas.trim().length() > 0 && passwordpetugas.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                        checkLoginPetugas(usernamepetugas, passwordpetugas);
                    } else {
                        Toast.makeText(getApplicationContext(), "Tidak ada Koneksi Internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void regMasyarakat() {

        String regnik = reg_nik.getText().toString();
        String regname = reg_name.getText().toString();
        String regusername = reg_username.getText().toString();
        String regpassword = reg_password.getText().toString();
        String regtelp = reg_telp.getText().toString();


        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Mendaftarkan...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();

                // keambali ke Login Masyarakat
                viewFlipper.setDisplayedChild(0);
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                //params.put("id", null);
                params.put("nik_app", regnik);
                params.put("nama_app", regname);
                params.put("username_app", regusername);
                params.put("password_app", regpassword);
                params.put("telp_app", regtelp);
                params.put("foto_profile_app", "default.png");


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_reg, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
    }


    private void checkLogin ( final String username, final String password){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memasuki ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);
                        String foto = jObj.getString("foto");

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, username);
                        editor.putString("petugas", "bukanpetugas");
                        editor.putString("foto", foto);
                        editor.apply();

                        // Memanggil main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, username);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // tambah permintaan ke request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    private void checkLoginPetugas ( final String usernamepetugas, final String passwordpetugas){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Memasuki ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_petugas, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        String usernamepetugas = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);
                        String tipenya = jObj.getString("tipep");
                        String fotonya = jObj.getString("fotop");

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, usernamepetugas);
                        editor.putString("petugas", tipenya);
                        editor.putString("foto", fotonya);
                        editor.apply();

                        // Memanggil main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, usernamepetugas);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", usernamepetugas);
                params.put("password", passwordpetugas);

                return params;
            }

        };

        // tambah permintaan ke request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog () {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog () {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
