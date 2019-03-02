package com.locus.locusassignment;

import com.locus.locusassignment.model.Item;

public interface OnItemClickListener {

    void onItemClick(int position, Item item);

    void onCrossClick(int position, Item item);

}
