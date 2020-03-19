package com.kangtech.MasyarakatLapor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.kangtech.MasyarakatLapor.util.RequestHandler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.kangtech.MasyarakatLapor.LoginActivity.maspor_preferences;
import static com.kangtech.MasyarakatLapor.util.Server.URL;

public class TambahLaporanActivity extends AppCompatActivity {

    Context context;

    private static final String TAG = MainActivity.class.getSimpleName();

    Bitmap bitmap;
    private String kimbitmap;
    public static final int REQUEST_IMAGE = 100;

    ImageView lampiranfoto;
    private Uri iniygbikinpusing;
    private Button btn_addgambarhiden, btn_addbatalgambarhiden;

    private String nogambar;

    private static final String url_kirim_laporan = URL + "add_laporan.php";
    private static final String url_kirim_laporan_nogambar = URL + "add_laporan_nogambar.php";

    public RequestQueue requestQueue;
    private StringRequest stringRequest;

    Bundle extras;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    String date;

    SharedPreferences sharedpreferences;
    TextInputEditText isilaporan;
    private String imageString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_laporan);

        Toolbar toolbarap = findViewById(R.id.toolbar_addlampiran);
        setSupportActionBar(toolbarap);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarap.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lampiranfoto = findViewById(R.id.iv_tambahLlampiranfoto);

        sharedpreferences = getSharedPreferences(maspor_preferences, Context.MODE_PRIVATE);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(calendar.getTime());

        Bundle extras = getIntent().getExtras();

        isilaporan = findViewById(R.id.input_isilaporan);

        nogambar = "tanpagambar";


        //TextView textView = findViewById(R.id.tv_isitest);


        context = this;
        assert extras != null;
        //textView.setText(extras.getString("isilaporan", "").toString());

        //loadImageDefault();

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this);

        Button btnkirim = findViewById(R.id.btn_isilaporan_kirim);
        btnkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String kirimtanpagambar = "tanpagambar";
                String isikosong = isilaporan.getText().toString();


                if (kirimtanpagambar.equals(nogambar)) {
                    if (isikosong.matches("")) {
                        new AlertDialog.Builder(context)
                                .setTitle("Isi Laporan Kosong")
                                .setMessage("Anda belum menulis laporan, Apa yang ingin anda Laporkan?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        addEmployeeTanpaGambar();
                    }
                } else {
                    if (bitmap == null) {
                        new AlertDialog.Builder(context)
                                .setTitle("Gambar Kosong")
                                .setMessage("Anda belum memilih Gambar, Silakan klik Batal Tambah Gambar bila ingin Melapor tanpa Gambar")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        addEmployee();
                    }

                }

            }
        });

        btn_addgambarhiden = findViewById(R.id.btn_isilaporan_addgambar);
        btn_addgambarhiden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lampiranfoto.setVisibility(View.VISIBLE);

                btn_addbatalgambarhiden.setVisibility(View.VISIBLE);

                btn_addgambarhiden.setVisibility(View.GONE);

                nogambar = "dengangambar";

                isilaporan.setMaxLines(19);
            }
        });

        btn_addbatalgambarhiden = findViewById(R.id.btn_isilaporan_addgambarbatal);
        btn_addbatalgambarhiden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lampiranfoto.setVisibility(View.GONE);

                btn_addbatalgambarhiden.setVisibility(View.GONE);

                btn_addgambarhiden.setVisibility(View.VISIBLE);

                nogambar = "tanpagambar";

            }
        });


        lampiranfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddImageClick();
            }
        });



    }
    private void addEmployee() {

        String isiisi = isilaporan.getText().toString().trim();
        String idid = String.valueOf(System.currentTimeMillis());
        String niknik = sharedpreferences.getString("id", "");

        Random random = new Random(9);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);




        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TambahLaporanActivity.this, "Mengirim Laporan...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(TambahLaporanActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                //params.put("id_pengaduan_app", null);
                params.put("tgl_pengaduan_app", date);
                params.put("nik_app", niknik);
                params.put("isi_laporan_app", isiisi);
                params.put("foto_file_app", imageString);
                params.put("foto_app", "gambar_" + System.currentTimeMillis());
                params.put("status", "0");


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_kirim_laporan, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
    }


    private void addEmployeeTanpaGambar() {

        String isiisi = isilaporan.getText().toString().trim();
        String idid = String.valueOf(System.currentTimeMillis());
        String niknik = sharedpreferences.getString("id", "");

        Random random = new Random(9);



        @SuppressLint("StaticFieldLeak")
        class AddEmployee extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TambahLaporanActivity.this, "Mengirim Laporan...", "Tunggu Sebentar...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(TambahLaporanActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {


                HashMap<String, String> params = new HashMap<>();
                //params.put("id_pengaduan_app", null);
                params.put("tgl_pengaduan_app", date);
                params.put("nik_app", niknik);
                params.put("isi_laporan_app", isiisi);
                //params.put("foto_file_app", imageString);
                params.put("foto_app", "tanpagambar");
                params.put("status", "0");


                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(url_kirim_laporan_nogambar, params);
                return res;
            }
        }
        AddEmployee ae = new AddEmployee();
        ae.execute();
    }

    private void loadImage(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(lampiranfoto);
        lampiranfoto.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }


    /* private void loadImageDefault() {
        Glide.with(this).load(R.drawable.ic_add_image)
                .into(lampiranfoto);
    } */

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
        Intent intent = new Intent(TambahLaporanActivity.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(TambahLaporanActivity.this, ImagePickerActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TambahLaporanActivity.this);
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
