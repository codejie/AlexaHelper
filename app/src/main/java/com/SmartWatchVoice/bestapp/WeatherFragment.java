package com.SmartWatchVoice.bestapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.SmartWatchVoice.bestapp.sdk.TemplateListActionData;
import com.SmartWatchVoice.bestapp.utils.Logger;

import java.io.InputStream;
import java.util.List;


public class WeatherFragment extends Fragment {

//    new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
//            .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

    public static class Adapter extends RecyclerView.Adapter<WeatherFragment.Adapter.ListViewHolder> {

        public static class ListViewHolder extends RecyclerView.ViewHolder {
            private final TextView day;
            private final TextView date;
            private final ImageView icon;
            private final TextView code;
            private final TextView high;
            private final TextView low;

            public ListViewHolder(@NonNull View view) {
                super(view);

                left = (TextView) view.findViewById(R.id.text_template_list_item_left);
                right = (TextView) view.findViewById(R.id.text_template_list_item_right);
            }

            private TextView getLeft() {
                return left;
            }

            private TextView getRight() {
                return right;
            }
        }

        private final List<TemplateListActionData.Item> data;

        public Adapter(List<TemplateListActionData.Item> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public WeatherFragment.Adapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_template_list_item, parent, false);
            return new WeatherFragment.Adapter.ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherFragment.Adapter.ListViewHolder holder, int position) {
            final TemplateListActionData.Item item = this.data.get(position);
            holder.getLeft().setText(item.left);
            holder.getRight().setText(item.right);
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}