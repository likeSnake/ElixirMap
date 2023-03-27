package net.map.elixirmap.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.geojson.Point;

import net.map.elixirmap.R;
import net.map.elixirmap.bean.ResultsBean;
import net.map.elixirmap.act.Result_Act;


import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

    private List<ResultsBean> infos;
    private Context context;

    public InfoAdapter(List<ResultsBean> list, Context context) {
        this.infos = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView aaa;
        private TextView bbb;
        private TextView ccc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            aaa = itemView.findViewById(R.id.tv_name);
            bbb = itemView.findViewById(R.id.tv_address);
            ccc = itemView.findViewById(R.id.tv_distance);


        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultsBean destinationInfo = infos.get(position);
        String address = destinationInfo.getAddress();
        String distance = destinationInfo.getDistance();
        Point point = destinationInfo.getPoint();
        String name = destinationInfo.getName();
        holder.bbb.setText(address);
        holder.ccc.setText(distance);
        holder.aaa.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Result_Act.class);
                intent.putExtra("longitude",String.valueOf(point.longitude()));
                intent.putExtra("latitude",String.valueOf(point.latitude()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return infos.size();
    }


}
