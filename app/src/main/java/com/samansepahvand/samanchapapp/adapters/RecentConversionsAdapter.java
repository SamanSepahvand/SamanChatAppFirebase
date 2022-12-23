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
import com.samansepahvand.samanchapapp.listeners.ConversionListener;
import com.samansepahvand.samanchapapp.models.ChatMessage;
import com.samansepahvand.samanchapapp.models.User;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionsViewHolder> {

    private List<ChatMessage> chatMessages;

    private ConversionListener conversionListener;


    public RecentConversionsAdapter(List<ChatMessage> chatMessages, ConversionListener _conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = _conversionListener;
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


    class ConversionsViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding binding;

        public ConversionsViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.textName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            binding.textDateMessage.setText(new SimpleDateFormat("MM/dd/yy").format(chatMessage.dataObject));
            binding.getRoot().setOnClickListener(view -> {
                User user = new User();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;

                conversionListener.onConversionCLicked(user);

            });
        }


        private Bitmap getConversionImage(String encodeImage) {
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        }
    }
}
