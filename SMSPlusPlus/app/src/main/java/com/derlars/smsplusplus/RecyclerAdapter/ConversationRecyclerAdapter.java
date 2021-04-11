package com.derlars.smsplusplus.RecyclerAdapter;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.derlars.smsplusplus.Message;
import com.derlars.smsplusplus.R;

import java.util.List;

public class TopMessageRecyclerAdapter extends RecyclerView.Adapter<TopMessageRecyclerAdapter.RecyclerViewHolder>{
    public static final String TAG = "LARS";

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView contactText;
        TextView messageText;
        TextView dateText;

        ImageView repliedImage;

        ConstraintLayout layout;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            contactText = itemView.findViewById(R.id.contactText);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);

            repliedImage = itemView.findViewById(R.id.repliedImage);

            layout = itemView.findViewById(R.id.layout);
        }
    }

    public interface OnItemClick {
        void onItemClick(int position, String sender);
    }

    private OnItemClick onItemClick;

    private RecyclerViewHolder rViewHolder;

    private List<Message> messages;

    public TopMessageRecyclerAdapter(List<Message> messages, OnItemClick onItemClick) {
        this.messages = messages;
        this.onItemClick = onItemClick;
    }

    public void updateContacts(List<Message> messages) {
        this.messages = messages;

        rViewHolder.notifyAll();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_topmessage,parent,false);
        rViewHolder = new RecyclerViewHolder(v);

        return rViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
        final Message message = messages.get(position);

        holder.contactText.setText(message.sender);
        holder.dateText.setText(message.getFormattedDate());
        holder.messageText.setText(message.getReducedMessage());

        holder.repliedImage.setVisibility(message.reply ? View.VISIBLE : View.INVISIBLE);

        if(message.read) {
            holder.messageText.setTypeface(null, Typeface.NORMAL);
            holder.dateText.setTypeface(null, Typeface.NORMAL);
        }else{
            holder.messageText.setTypeface(holder.messageText.getTypeface(), Typeface.BOLD_ITALIC);
            holder.dateText.setTypeface(holder.dateText.getTypeface(), Typeface.BOLD_ITALIC);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopMessageRecyclerAdapter.this.onItemClick.onItemClick(position,message.sender);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
