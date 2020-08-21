package com.kangtech.MasyarakatLapor.ui.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.kangtech.MasyarakatLapor.ui.ImagePickerActivity;
import com.kangtech.MasyarakatLapor.ui.login.LoginActivity;
import com.kangtech.MasyarakatLapor.R;
import com.kangtech.MasyarakatLapor.model.ProfileModel;
import com.kangtech.MasyarakatLapor.model.ProfilePetugasModel;
import com.kangtech.MasyarakatLapor.util.RequestHandler;
import com.kangtech.MasyarakatLapor.util.Server;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kangtech.MasyarakatLapor.ui.login.LoginActivity.maspor_preferences;
import static com.kangtech.MasyarakatLapor.util.Server.URL;

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

    private Dialog ubahpasswordDialog;
    private TextInputEditText oldpw,newpw,new2pw;

    private Dialog ubahProfilDialog;
    private TextInputEditText namaubah,nikubah,telpubah;

    private TextView infoubahprofil;

    Bitmap bitmap;
    private String kimbitmap;
    public static final int REQUEST_IMAGE = 100;

    private String imageString;

    private static final String url_kirim_fotop = URL + "change_foto_profil.php";
    private static final String url_kirim_fotop_petugas = URL + "change_foto_profil_petugas.php";
    String res;

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

        Button btnubahpw = findViewById(R.id.btn_ubahpassword);
        btnubahpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubahpasswordDialog.show();
            }
        });


        Button btnubahprofil = findViewById(R.id.btn_ubahprofil);
        btnubahprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubahProfilDialog.show();

                if (status_petugas.equals(sharedpreferences.getString("petugas", "apaan"))) {
                    // set Visibilitas
                    nikubah.setVisibility(View.GONE);

                    namaubah.setText(txtnama.getText());
                    telpubah.setText(txttelp.getText());


                } else if (status_petugas2.equals(sharedpreferences.getString("petugas", "adminnn"))) {
                    // set Visibilitas
                    nikubah.setVisibility(View.GONE);

                    namaubah.setText(txtnama.getText());
                    telpubah.setText(txttelp.getText());


                } else {
                    // Masyarakat
                    namaubah.setText(txtnama.getText());
                    nikubah.setText(txtnik.getText());
                    telpubah.setText(txttelp.getText());


                    //infoubahprofil.setVisibility(View.VISIBLE);
                    nikubah.setVisibility(View.GONE);

                }

            }
        });


        // Menampilkan Dialog Ubah Password
        customUbahpasswordDialog();

        // Menampilkan Dialog Ubah Profil
        customUbahProfilDialog();


        //ganti foto profil
        ImageView gantifotop = findViewById(R.id.iv_kamera_profil);
        gantifotop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddImageClick();
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


    private void customUbahpasswordDialog() {
        ubahpasswordDialog = new Dialog(this);
        ubahpasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ubahpasswordDialog.setContentView(R.layout.dialog_ubahpassword);
        ubahpasswordDialog.setCancelable(true);

        oldpw = ubahpasswordDialog.findViewById(R.id.editTextOldpassword);
        newpw = ubahpasswordDialog.findViewById(R.id.editTextNewpassword);

        final String getoldpw = oldpw.getText().toString();
        final String getnewpw = newpw.getText().toString();

        Button btnubah = ubahpasswordDialog.findViewById(R.id.btn_ubah_pw);

        btnubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    changePassword();
            }
        });


    }


    private void changePassword() {
        final String url_change_password = URL + "change_password.php";

        String get_oldpw = oldpw.getText().toString();
        String get_newpw = newpw.getText().toString();

        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Mengubah Kata Sandi...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                params.put("id_app", sharedpreferences.getString("id", "apaan"));
                params.put("oldpw_app", get_oldpw);
                params.put("newpw", get_newpw);

                //params.put("newpw2", get_newpw2);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_change_password, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
    }


    private void customUbahProfilDialog() {
        ubahProfilDialog = new Dialog(this);
        ubahProfilDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ubahProfilDialog.setContentView(R.layout.dialog_ubahprofil);
        ubahProfilDialog.setCancelable(true);

        namaubah = ubahProfilDialog.findViewById(R.id.editTextUbahNama);
        nikubah = ubahProfilDialog.findViewById(R.id.editTextUbahNik);
        telpubah = ubahProfilDialog.findViewById(R.id.editTextUbahTelp);

        infoubahprofil = ubahProfilDialog.findViewById(R.id.tv_infoubahprofil);


        Button btnubahprofil = ubahProfilDialog.findViewById(R.id.btn_ubah_profil);

        if (status_petugas.equals(sharedpreferences.getString("petugas", "apaan"))) {

            btnubahprofil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeProfilPetugas();
                    finish();
                }
            });

        } else if (status_petugas2.equals(sharedpreferences.getString("petugas", "adminnn"))) {

            btnubahprofil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeProfilPetugas();
                    finish();
                }
            });

        } else {
            // Masyarakat

            btnubahprofil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeProfilMasyarakat();

                    // update login session ke FALSE dan mengosongkan nilai id dan username
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(LoginActivity.session_status, false);
                    editor.putString(TAG_USERNAME, null);
                    editor.apply();

                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    finishAffinity();
                    startActivity(intent);
                }
            });
        }


    }

    private void changeProfilMasyarakat() {
        final String url_change_profil_m = URL + "change_profil_masyarakat.php";

        String get_namaubah = namaubah.getText().toString();
        String get_nikubah = nikubah.getText().toString();
        String get_telpubah = telpubah.getText().toString();

        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Mengubah Profil...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                params.put("id_app", sharedpreferences.getString("id", "apaan"));
                params.put("namaubah_app", get_namaubah);
                params.put("nikubah_app", get_nikubah);

                params.put("telpubah_app", get_telpubah);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_change_profil_m, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
    }

    private void changeProfilPetugas() {
        final String url_change_profil_p = URL + "change_profil_petugas.php";

        String get_namaubah = namaubah.getText().toString();
        //String get_nikubah = nikubah.getText().toString();
        String get_telpubah = telpubah.getText().toString();

        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Mengubah Profil...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                params.put("id_app", sharedpreferences.getString("id", "apaan"));
                params.put("namaubah_app", get_namaubah);
                //params.put("nikubah_app", get_nikubah);

                params.put("telpubah_app", get_telpubah);


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_change_profil_p, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
    }


    // untuk foto profil ganti
    private void kirimFotop() {

        Random random = new Random(9);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);




        @SuppressLint("StaticFieldLeak")
        class kirimFotop extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfileActivity.this, "Mengganti Foto Profil...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                params.put("id_app", sharedpreferences.getString("id", ""));
                params.put("fotop_file_app", imageString);
                params.put("fotop_app", "foto_" + System.currentTimeMillis());


                RequestHandler rh = new RequestHandler();

                if (status_petugas.equals(sharedpreferences.getString("petugas", "apaan"))) {
                    res = rh.sendPostRequest(url_kirim_fotop_petugas, params);


                } else if (status_petugas2.equals(sharedpreferences.getString("petugas", "adminnn"))) {
                    res = rh.sendPostRequest(url_kirim_fotop_petugas, params);


                } else {
                    // Masyarakat
                    res = rh.sendPostRequest(url_kirim_fotop, params);

                }

                return res;
            }
        }
        kirimFotop ae = new kirimFotop();
        ae.execute();
    }


    //untuk ambil gambar
    private void loadImage(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(fotoprofilemasyrakat);
        fotoprofilemasyrakat.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void onAddImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(ProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(ProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    //iniygbikinpusing = uri;

                    // loading profile image from local cache
                   loadImage(uri.toString());
                    kimbitmap = uri.toString();

                    //mengurim ke database
                    kirimFotop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
