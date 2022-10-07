package com.SmartWatchVoice.bestapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.action.system.TimeZoneChangedAction;
import com.SmartWatchVoice.bestapp.databinding.FragmentSettingTimeZoneBinding;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;

import java.util.List;

import okhttp3.Response;

public class SettingTimeZoneFragment extends Fragment {
    public static class Adapter extends RecyclerView.Adapter<Adapter.TimeZoneViewHolder> {

        public interface OnItemClickListener {
            void OnItemClick(View view, int position);
        }

        public static class TimeZoneViewHolder extends RecyclerView.ViewHolder {
            private final TextView text;
            private final ImageView image;

            private OnItemClickListener onItemClickListener;

            public TimeZoneViewHolder(@NonNull View view) {
                super(view);

                this.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.OnItemClick(view, getBindingAdapterPosition());
                    }
                });

                text = (TextView) view.findViewById(R.id.text_setting_item_time_zone);
                image = (ImageView) view.findViewById(R.id.image_setting_item_time_zone);
            }

            private TextView getText() {
                return text;
            }

            private ImageView getImage() {
                return image;
            }

            public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
                this.onItemClickListener = onItemClickListener;
            }
        }

        private final List<String> data;
        private String selected;
        private OnItemClickListener onItemClickListener;

        public Adapter(List<String> data, String selected) {
            this.data = data;
            this.selected = selected;
        }

        @NonNull
        @Override
        public TimeZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_setting_time_zone_item, parent, false);
            return new TimeZoneViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TimeZoneViewHolder holder, int position) {
            holder.setOnItemClickListener(onItemClickListener);

            String zone = this.data.get(position);
            holder.getText().setText(zone);
            if (zone.equals(selected)) {
                holder.getImage().setVisibility(View.VISIBLE);
            } else {
                holder.getImage().setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    private FragmentSettingTimeZoneBinding binding = null;
    private Adapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingTimeZoneBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new Adapter(DeviceInfo.SupportedTimeZone, SettingInfo.getInstance().timeZone);
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                String timezone = DeviceInfo.SupportedTimeZone.get(position);
                onChanged(timezone);
            }
        });
        binding.settingItemZoneRecycle.setAdapter(adapter);
        binding.settingItemZoneRecycle.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        binding.settingItemZoneRecycle.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
    }

    private void onChanged(String timezone) {
        new TimeZoneChangedAction(timezone).create().post(new EventAction.OnChannelResponse() {
            @Override
            public void OnResponse(@NonNull Response response) {
                SettingInfo.getInstance().timeZone = timezone;
                SettingInfo.getInstance().flush();;

                adapter.setSelected(timezone);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        NavHostFragment.findNavController(SettingTimeZoneFragment.this).navigate(R.id.action_settingTimeZoneFragment_to_homeFragment);
                    }
                });
            }
        });
    }
}