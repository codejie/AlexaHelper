package com.SmartWatchVoice.bestapp;

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

import com.SmartWatchVoice.bestapp.databinding.FragmentTemplateListBinding;
import com.SmartWatchVoice.bestapp.sdk.TemplateListActionData;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.setting.AlertInfo;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

public class TemplateListFragment extends Fragment {

    public static class Adapter extends RecyclerView.Adapter<TemplateListFragment.Adapter.ListViewHolder> {

        public static class ListViewHolder extends RecyclerView.ViewHolder {
            private final TextView left;
            private final TextView right;

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
        public TemplateListFragment.Adapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_template_list_item, parent, false);
            return new TemplateListFragment.Adapter.ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TemplateListFragment.Adapter.ListViewHolder holder, int position) {
            final TemplateListActionData.Item item = this.data.get(position);
            holder.getLeft().setText(item.left);
            holder.getRight().setText(item.right);
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }
    }

    private FragmentTemplateListBinding binding;
    private TemplateListFragment.Adapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTemplateListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TemplateListFragment.Adapter(SettingInfo.getInstance().templateListData.items);
        binding.listTemplateList.setAdapter(adapter);
        binding.listTemplateList.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
        binding.listTemplateList.addItemDecoration(new DividerItemDecoration(this.getContext(), RecyclerView.VERTICAL));

        binding.textTemplateListMain.setText(SettingInfo.getInstance().templateListData.mainTitle);
        binding.textTemplateListSub.setText(SettingInfo.getInstance().templateListData.subTitle);

    }
}