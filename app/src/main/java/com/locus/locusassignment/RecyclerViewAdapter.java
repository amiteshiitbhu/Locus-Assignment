package com.locus.locusassignment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.locus.locusassignment.model.Item;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.BaseViewHolder> {
    private static final int TYPE_IMAGE = 0;
    private static final int SINGLE_CHOICE = 1;
    private static final int TYPE_COMMENT = 2;
    private List<Item> items = new ArrayList<Item>();
    private OnItemClickListener listener;

    public RecyclerViewAdapter(List<Item> items, @NonNull OnItemClickListener listener) {
        this.listener = listener;
        if (items != null) {
            this.items.addAll(items);
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == TYPE_IMAGE) {
            return new TypeImageViewHolder(layoutInflater.inflate(R.layout.item_image, viewGroup, false), listener);
        }
        if (viewType == SINGLE_CHOICE) {
            return new TypeOptionViewHolder(layoutInflater.inflate(R.layout.item_option, viewGroup, false), listener);
        }

        if (viewType == TYPE_COMMENT) {
            return new TypeCommentViewHolder(layoutInflater.inflate(R.layout.item_comment, viewGroup, false), listener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.BaseViewHolder viewHolder, int position) {
        viewHolder.bindView(items.get(position));
    }

    @Override
    public int getItemCount() {
//        int itemCount = 0;
//        for (int i = 0; i < items.size(); i++) {
//            itemCount += items.get(i).getItemCount();
//        }
//        return itemCount;
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Item item = items.get(position);
        switch (item.getType()) {
            case "PHOTO":
                return TYPE_IMAGE;
            case "SINGLE_CHOICE":
                return SINGLE_CHOICE;
            case "COMMENT":
                return TYPE_COMMENT;
        }
        return TYPE_IMAGE;
    }

    static abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        protected OnItemClickListener listener;


        public BaseViewHolder(@NonNull View itemView, @NonNull OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;
        }

        abstract void bindView(Item item);

    }

    private static class TypeImageViewHolder extends BaseViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ImageButton clearImage;
        private Item item;

        TypeImageViewHolder(@NonNull View itemView, @NonNull OnItemClickListener listener) {
            super(itemView, listener);
            this.imageView = itemView.findViewById(R.id.image_view);
            this.clearImage = itemView.findViewById(R.id.clear_image);
            imageView.setOnClickListener(this);
            clearImage.setOnClickListener(this);
        }

        @Override
        void bindView(Item item) {
            this.item = item;
            String uri = item.getDataMap().has("imagePath") ?
                    item.getDataMap().get("imagePath").getAsString() : "null";
            Picasso.get().load(new File(uri)).into(imageView);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.image_view) {
                this.listener.onItemClick(getAdapterPosition(), item);
            } else if (v.getId() == R.id.clear_image) {
                this.listener.onCrossClick(getAdapterPosition(), item);
            }
        }

    }

    private static class TypeOptionViewHolder extends BaseViewHolder implements RadioGroup.OnCheckedChangeListener {

        private RadioGroup radioGroup;
        private TextView title;
        private Item item;

        TypeOptionViewHolder(@NonNull View itemView, @NonNull OnItemClickListener listener) {
            super(itemView, listener);
            title = itemView.findViewById(R.id.title);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioGroup.setOnCheckedChangeListener(this);
        }

        @Override
        void bindView(Item item) {
            this.item = item;
            title.setText(item.getTitle());
            JsonObject dataMap = item.getDataMap();
            JsonArray options = dataMap.getAsJsonArray("options");
            radioGroup.removeAllViews();

            int selected = -1;
            if (item.getDataMap().has("selectedOption")) {
                selected = item.getDataMap().get("selectedOption").getAsInt();
            }
            radioGroup.clearCheck();
            if (options != null) {
                for (int i = 0; i < options.size(); i++) {
                    RadioButton radioButton = new RadioButton(itemView.getContext());
                    radioButton.setText(options.get(i).toString());
                    radioButton.setTag(i);
                    radioGroup.addView(radioButton, new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setChecked(i == selected);
                }
            }

        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton == null) {
                return;
            }
            int tag = (int) radioButton.getTag();
            this.item.getDataMap().addProperty("selectedOption", tag);
        }
    }

    private static class TypeCommentViewHolder extends BaseViewHolder implements CompoundButton.OnCheckedChangeListener, TextWatcher {

        private EditText comment;
        private Switch switchButton;
        private Item item;

        TypeCommentViewHolder(@NonNull View itemView, @NonNull OnItemClickListener listener) {
            super(itemView, listener);
            switchButton = itemView.findViewById(R.id.switch_button);
            comment = itemView.findViewById(R.id.comment);
            switchButton.setOnCheckedChangeListener(this);

            comment.addTextChangedListener(this);
        }

        @Override
        void bindView(Item item) {
            this.item = item;
            if (item.getDataMap().has("text")) {
                comment.setText(item.getDataMap().get("text").getAsString());
            } else {
                comment.setText(null);
            }
            if (item.getDataMap().has("checked")) {
                switchButton.setChecked(item.getDataMap().get("checked").getAsBoolean());
            } else {
                switchButton.setChecked(false);
            }
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            this.item.getDataMap().addProperty("checked", isChecked);
            if (isChecked) {
                comment.setVisibility(View.VISIBLE);
            } else {
                comment.setVisibility(View.GONE);
            }

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            this.item.getDataMap().addProperty("text", s.toString());
        }
    }
}
