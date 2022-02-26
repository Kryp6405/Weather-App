package com.example.weatherproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends ArrayAdapter<Weather> {
    List<Weather> list;
    Context context;
    int resource;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Weather> list){
        super(context, resource, list);
        this.list = list;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(R.layout.output, null);

        ImageView image = adapterLayout.findViewById(R.id.image);
        TextView time = adapterLayout.findViewById(R.id.time);
        TextView desc = adapterLayout.findViewById(R.id.desc);
        TextView temp = adapterLayout.findViewById(R.id.temp);

        String dTFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new java.util.Date((list.get(position).getTime())*1000));
        time.setText(""+dTFormat);
        temp.setText(""+kToF(list.get(position).getTemperature()) + " °F");
        desc.setText(list.get(position).getDescription().toUpperCase(Locale.ROOT));
        String url = list.get(position).getImage();
        url = url.substring(0,4)+"s"+url.substring(4);
        Picasso.with(context).load(url).into(image);

        return adapterLayout;
    }

    public double kToF(double k){
        return Math.round((k*9/5 - 459.67)*100)/100.0;
    }
}
