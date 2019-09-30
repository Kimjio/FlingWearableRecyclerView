package com.kimjio.sample.flingwearablerecyclerview;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kimjio.flingwearablerecyclerview.FlingWearableRecyclerView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlingWearableRecyclerView recyclerView = findViewById(R.id.list);

        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false)) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TextView) holder.itemView.findViewById(R.id.text)).setText(String.valueOf(position + 1));
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }
}
