package com.derlars.smsplusplus.RecyclerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.derlars.smsplusplus.R;

import java.util.List;

public class TopMessageRecyclerAdapter extends RecyclerView.Adapter<TopMessageRecyclerAdapter.RecyclerViewHolder>{
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView contactText;
        TextView messageText;
        TextView dateText;

        ImageView repliedImage;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            contactText = itemView.findViewById(R.id.contactText);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);

            repliedImage = itemView.findViewById(R.id.repliedImage);
        }
    }

    private RecyclerViewHolder rViewHolder;

    private List<Message> contacts;

    public TopMessageRecyclerAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void updateContacts(List<Contact> contacts) {
        this.contacts = contacts;

        rViewHolder.notifyAll();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_overview_card,parent,false);
        rViewHolder = new RecyclerViewHolder(v);

        return rViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        final Contact contact = contacts.get(position);

        holder.contactText.setText(contact.getName());
        holder.dateText.setText(contact.getFormattedDate());
        holder.messageText.setText(contact.getMessagePreview());

        holder.repliedImage.setVisibility(contact.isReplied() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
