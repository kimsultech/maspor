package com.kangtech.MasyarakatLapor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.kangtech.MasyarakatLapor.R;
import com.kangtech.MasyarakatLapor.ViewLaporanActivity;
import com.kangtech.MasyarakatLapor.util.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class PengaduanListAdapter extends RecyclerView.Adapter<PengaduanListAdapter.ViewHolder>{
    ArrayList<HashMap<String, String>> list_data;

    private static final String url_image = Server.URL_IMG;
    private static final String url_foto = Server.URL_FOTO;

    private Context activity;

    public PengaduanListAdapter(Context activity, ArrayList<HashMap<String, String>> list_data) {
        this.activity = activity;
        this.list_data = list_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pengaduan,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String nofoto = "tanpagambar";
        if (nofoto.equals(list_data.get(position).get("lampiranfoto"))) {
            holder.lampiran_pic.setVisibility(View.GONE);
        } else {
            //nothung
        }

        String statustanggapi = "selesai";
        if (statustanggapi.equals(list_data.get(position).get("status"))) {
            holder.statuspengaduan.setText("SELESAI");
        }

        String statusproses = "proses";
        if (statusproses.equals(list_data.get(position).get("status"))) {
            holder.statuspengaduan.setText("sedang di Proses");
        }

        Glide.with(holder.lampiran_pic)
                .load(url_image + list_data.get(position).get("lampiranfoto"))
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.lampiran_pic);
        holder.nama.setText(list_data.get(position).get("nama"));
        holder.tanggal.setText(list_data.get(position).get("tanggal"));
        holder.isilaporan.setText(list_data.get(position).get("isilaporan"));

        Glide.with(holder.profile_pic)
                .load(url_foto + list_data.get(position).get("fotop"))
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(holder.profile_pic);

        holder.cvlaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ViewLaporanActivity.class);
                i.putExtra("idpengaduanview", list_data.get(position).get("idpengaduan"));
                activity.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama,tanggal;
        ImageView profile_pic;

        TextView isilaporan;
        ImageView lampiran_pic;

        Button statuspengaduan;

        CardView cvlaporan;

        public ViewHolder(View itemView) {
            super(itemView);
            activity = itemView.getContext();


            nama = itemView.findViewById(R.id.tv_namapengaduan);
            tanggal = itemView.findViewById(R.id.tv_tanggalpengaduan);
            profile_pic = itemView.findViewById(R.id.civ_fotopengaduan);

            isilaporan = itemView.findViewById(R.id.tv_isilaporan);
            lampiran_pic = itemView.findViewById(R.id.iv_lampiranpengaduan);

            statuspengaduan = itemView.findViewById(R.id.btn_statuspengaduan);

            cvlaporan = itemView.findViewById(R.id.cv_listpengaduan);
        }
    }
}
