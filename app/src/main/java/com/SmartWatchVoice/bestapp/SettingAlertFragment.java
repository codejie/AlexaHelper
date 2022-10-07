package com.SmartWatchVoice.bestapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartWatchVoice.bestapp.databinding.FragmentSettingAlertBinding;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.setting.AlertInfo;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class SettingAlertFragment extends Fragment {
    public static class Adapter extends RecyclerView.Adapter<Adapter.AlertViewHolder> {

        public interface OnItemClickListener {
            void OnItemClick(View view, int position);
        }

        public static class AlertViewHolder extends RecyclerView.ViewHolder {
            private final TextView time;
            private final TextView type;
            private final ImageView image;

            private SettingAlertFragment.Adapter.OnItemClickListener onItemClickListener;

            public AlertViewHolder(@NonNull View view) {
                super(view);

                time = (TextView) view.findViewById(R.id.text_alert_time);
                type = (TextView) view.findViewById(R.id.text_alert_type);
                image = (ImageView) view.findViewById(R.id.image_remove);

                this.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.OnItemClick(view, getBindingAdapterPosition());
                    }
                });
            }

            private TextView getTime() {
                return time;
            }

            private TextView getType() {
                return type;
            }

            private ImageView getImage() {
                return image;
            }

            public void setOnItemClickListener(SettingAlertFragment.Adapter.OnItemClickListener onItemClickListener) {
                this.onItemClickListener = onItemClickListener;
            }
        }

        private final List<AlertInfo.Alert> data;
        private SettingAlertFragment.Adapter.OnItemClickListener onItemClickListener;

        public Adapter(List<AlertInfo.Alert> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_setting_alert_item, parent, false);
            return new SettingAlertFragment.Adapter.AlertViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
            holder.setOnItemClickListener(onItemClickListener);

            final AlertInfo.Alert alert = this.data.get(position);
            holder.getTime().setText(Utils.dateToLocalDateTime(Utils.stringToDate(alert.scheduledTime)));
            holder.getType().setText(alert.type);
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public void setOnItemClickListener(SettingAlertFragment.Adapter.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    private FragmentSettingAlertBinding binding;
    private SettingAlertFragment.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingAlertBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SettingAlertFragment.Adapter(SettingInfo.getInstance().alertInfo.getAlerts());
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                onRemove(position);
            }
        });

        binding.listAlerts.setAdapter(adapter);
        binding.listAlerts.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        binding.listAlerts.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
    }

    private void onRemove(int position) {
    }
}