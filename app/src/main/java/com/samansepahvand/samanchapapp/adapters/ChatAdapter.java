package com.samansepahvand.samanchapapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samansepahvand.samanchapapp.customView.ShowImageDialog;
import com.samansepahvand.samanchapapp.databinding.ItemContainerReceivedMessageBinding;
import com.samansepahvand.samanchapapp.databinding.ItemContainerSentMessageBinding;
import com.samansepahvand.samanchapapp.databinding.ItemContainerUserBinding;
import com.samansepahvand.samanchapapp.listeners.UserListener;
import com.samansepahvand.samanchapapp.metamodel.PhotoViewMetaModel;
import com.samansepahvand.samanchapapp.models.ChatMessage;
import com.samansepahvand.samanchapapp.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<ChatMessage> chatMessages = new ArrayList<>();
    private Bitmap receiverProfileImage;
    private String senderId;

    public static final int VIEW_TYPE_SENT=1;
    public static final int VIEW_TYPE_RECEIVED=2;

    public Context context;


    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage=bitmap;

    }

    public IGetPhotoInfo iGetPhotoInfo;

    public ChatAdapter(Context context,IGetPhotoInfo info,List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
        this.context=context;
        this.iGetPhotoInfo=info;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent, false
            ));

        } else {

            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent, false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));

        }else{
            ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

     class SentMessageViewHolder extends RecyclerView.ViewHolder {
        ItemContainerSentMessageBinding binding;

        public SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            if (chatMessage.imageMessage!=null){
                binding.imageMessage.setVisibility(View.VISIBLE);
                binding.imageMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.imageMessage));
                if (chatMessage.message==null){
                    binding.textMessage.setVisibility(View.GONE);
                }
            }



            binding.imageMessage.setOnClickListener(view -> {
                PhotoViewMetaModel model =new PhotoViewMetaModel();

                model.setTitle(chatMessage.message);
                model.setDescription(chatMessage.message);
                model.setDate(chatMessage.dataObject.toString());
                model.setImageUrl(chatMessage.imageMessage);

                iGetPhotoInfo.ShowDialogImage(model);


            });

        }
        private Bitmap getBitmapFromEncodedString(String encodeImage) {

            if (encodeImage != null) {
                byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } else {
                return null;
            }
        }
    }

     class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        ItemContainerReceivedMessageBinding binding;

        public ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;


        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);

            if (receiverProfileImage!=null){
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }

            if (chatMessage.imageMessage!=null){
                binding.imageMessage.setVisibility(View.VISIBLE);
                binding.imageMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.imageMessage));
                if (chatMessage.message==null){
                    binding.textMessage.setVisibility(View.GONE);
                }
            }




            binding.imageMessage.setOnClickListener(view -> {
                PhotoViewMetaModel model =new PhotoViewMetaModel();

                model.setTitle(chatMessage.message);
                model.setDescription(chatMessage.message);
                model.setDate(chatMessage.dataObject.toString());
                model.setImageUrl(chatMessage.imageMessage);

                iGetPhotoInfo.ShowDialogImage(model);


            });


        }
        private Bitmap getBitmapFromEncodedString(String encodeImage) {

            if (encodeImage != null) {
                byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } else {
                return null;
            }
        }
    }

    public interface IGetPhotoInfo{
        void ShowDialogImage(PhotoViewMetaModel photoViewMetaModel );
    }

}