package com.SmartWatchVoice.bestapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.SmartWatchVoice.bestapp.databinding.FragmentTemplateWeatherBinding;
import com.SmartWatchVoice.bestapp.sdk.TemplateWeatherActionData;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.utils.Logger;

import java.io.InputStream;
import java.util.List;


public class TemplateWeatherFragment extends Fragment {

//    new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
//            .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
                Logger.w(e.getMessage());
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public static class Adapter extends RecyclerView.Adapter<TemplateWeatherFragment.Adapter.ListViewHolder> {

        public static class ListViewHolder extends RecyclerView.ViewHolder {
            public final TextView day;
            public final TextView date;
            public final ImageView icon;
            public final TextView code;
            public final TextView high;
            public final TextView low;

            public ListViewHolder(@NonNull View view) {
                super(view);

                day = (TextView) view.findViewById(R.id.text_template_weather_item_day);
                date = (TextView) view.findViewById(R.id.text_template_weather_item_date);
                icon = (ImageView) view.findViewById(R.id.image_template_weather_item_icon);
                code = (TextView) view.findViewById(R.id.text_template_weather_item_code);
                high = (TextView) view.findViewById(R.id.text_template_weather_item_high);
                low = (TextView) view.findViewById(R.id.text_template_weather_item_low);
            }
        }

        private final List<TemplateWeatherActionData.Item> data;

        public Adapter(List<TemplateWeatherActionData.Item> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public TemplateWeatherFragment.Adapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_template_weather_item, parent, false);
            return new TemplateWeatherFragment.Adapter.ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TemplateWeatherFragment.Adapter.ListViewHolder holder, int position) {
            final TemplateWeatherActionData.Item item = this.data.get(position);

            holder.day.setText(item.day);
            holder.date.setText(item.date);
            holder.code.setText(item.code);
            holder.high.setText(item.high);
            holder.low.setText(item.low);

            new DownloadImageTask(holder.icon).execute(item.iconUrl);
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }
    }

    private FragmentTemplateWeatherBinding binding;
    private TemplateWeatherFragment.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTemplateWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TemplateWeatherActionData data = SettingInfo.getInstance().templateWeatherActionData;

        binding.textTemplateWeatherTitle.setText(data.mainTitle);
        binding.textTemplateWeatherSubtitle.setText(data.subTitle);
        binding.textTemplateWeatherDescription.setText(data.description);
        binding.textTemplateWeatherValue.setText(data.value);
        binding.textTemplateWeatherCode.setText(data.code);
        binding.textTemplateWeatherHigh.setText(data.high);
        binding.textTemplateWeatherLow.setText(data.low);

        new DownloadImageTask(binding.imageTemplateWeatherIcon).execute(data.iconUrl);
        new DownloadImageTask(binding.imageTemplateWeatherHigh).execute(data.highUrl);
        new DownloadImageTask(binding.imageTemplateWeatherLow).execute(data.lowUrl);

        adapter = new TemplateWeatherFragment.Adapter(data.items);
        binding.listTemplateWeather.setAdapter(adapter);
        binding.listTemplateWeather.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        binding.listTemplateWeather.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
    }
}