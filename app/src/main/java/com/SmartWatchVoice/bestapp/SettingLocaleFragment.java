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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.action.system.LocalesChangedAction;
import com.SmartWatchVoice.bestapp.action.system.TimeZoneChangedAction;
import com.SmartWatchVoice.bestapp.databinding.FragmentSettingLocaleBinding;
import com.SmartWatchVoice.bestapp.sdk.SDKAction;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;

import java.util.Arrays;
import java.util.List;

import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback;
import okhttp3.Response;


public class SettingLocaleFragment extends Fragment {

    public static class Adapter extends RecyclerView.Adapter<SettingLocaleFragment.Adapter.LocaleViewHolder> {

        public interface OnItemClickListener {
            void OnItemClick(View view, int position);
        }

        public static class LocaleViewHolder extends RecyclerView.ViewHolder {
            private final TextView text;
            private final ImageView image;

            private SettingTimeZoneFragment.Adapter.OnItemClickListener onItemClickListener;

            public LocaleViewHolder(@NonNull View view) {
                super(view);

                this.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.OnItemClick(view, getBindingAdapterPosition());
                    }
                });

                text = (TextView) view.findViewById(R.id.text_setting_item_locale);
                image = (ImageView) view.findViewById(R.id.image_setting_item_locale);
            }

            private TextView getText() {
                return text;
            }

            private ImageView getImage() {
                return image;
            }

            public void setOnItemClickListener(SettingTimeZoneFragment.Adapter.OnItemClickListener onItemClickListener) {
                this.onItemClickListener = onItemClickListener;
            }
        }


        private final List<String> data;
        private String selected;
        private SettingTimeZoneFragment.Adapter.OnItemClickListener onItemClickListener;

        public Adapter(List<String> data, String selected) {
            this.data = data;
            this.selected = selected;
        }

        @NonNull
        @Override
        public SettingLocaleFragment.Adapter.LocaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_setting_locale_item, parent, false);
            return new SettingLocaleFragment.Adapter.LocaleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SettingLocaleFragment.Adapter.LocaleViewHolder holder, int position) {
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

        public void setOnItemClickListener(SettingTimeZoneFragment.Adapter.OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    private FragmentSettingLocaleBinding binding = null;
    private SettingLocaleFragment.Adapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingLocaleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SettingLocaleFragment.Adapter(DeviceInfo.SupportedLocales, SettingInfo.getInstance().locales.get(0));
        adapter.setOnItemClickListener(new SettingTimeZoneFragment.Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                String locale = DeviceInfo.SupportedLocales.get(position);
                onChanged(locale);
            }
        });
        binding.settingItemLocaleRecycle.setAdapter(adapter);
        binding.settingItemLocaleRecycle.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        binding.settingItemLocaleRecycle.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));
    }

    private void onChanged(String locale) {

        SDKAction.setLocales(locale, new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                SettingInfo.getInstance().locales = Arrays.asList(locale);
                SettingInfo.getInstance().flush();;

                adapter.setSelected(locale);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        NavHostFragment.findNavController(SettingLocaleFragment.this).navigate(R.id.action_settingLocaleFragment_to_homeFragment);
                    }
                });
            }
        });

//        new LocalesChangedAction(Arrays.asList(locale)).create().post(new EventAction.OnChannelResponse() {
//            @Override
//            public void OnResponse(@NonNull Response response) {
//                SettingInfo.getInstance().locales = Arrays.asList(locale);
//                SettingInfo.getInstance().flush();;
//
//                adapter.setSelected(locale);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyDataSetChanged();
//                        NavHostFragment.findNavController(SettingLocaleFragment.this).navigate(R.id.action_settingLocaleFragment_to_homeFragment);
//                    }
//                });
//            }
//        });
    }
}