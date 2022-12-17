package com.samansepahvand.samanchapapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samansepahvand.samanchapapp.databinding.ItemContainerRecentConversionBinding;
import com.samansepahvand.samanchapapp.databinding.ItemContainerSentMessageBinding;
import com.samansepahvand.samanchapapp.databinding.ItemContainerUserBinding;
import com.samansepahvand.samanchapapp.models.ChatMessage;
import com.samansepahvand.samanchapapp.models.User;

import java.util.List;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionsViewHolder> {

    private List<ChatMessage> chatMessages;

    public RecentConversionsAdapter( List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }


    @NonNull
    @Override
    public ConversionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecentConversionsAdapter.ConversionsViewHolder(ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionsViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    class ConversionsViewHolder  extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding binding;

        public ConversionsViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));

        }


        private Bitmap getConversionImage(String encodeImage) {
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        }
    }
}
