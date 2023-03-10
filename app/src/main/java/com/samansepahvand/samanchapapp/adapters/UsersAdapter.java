package com.samansepahvand.samanchapapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samansepahvand.samanchapapp.databinding.ItemContainerUserBinding;
import com.samansepahvand.samanchapapp.listeners.UserListener;
import com.samansepahvand.samanchapapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{


    public List<User> users = new ArrayList<>();
private UserListener _userListener;

    public UsersAdapter(List<User> users,UserListener userListener) {
        this.users = users;
        this._userListener=userListener;
    }


    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding=ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,false);
        return new UsersViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {

        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    class UsersViewHolder  extends RecyclerView.ViewHolder {

    ItemContainerUserBinding binding;

    public UsersViewHolder( ItemContainerUserBinding itemContainerUserBinding) {
        super(itemContainerUserBinding.getRoot());
        binding=itemContainerUserBinding;

    }
    void setUserData(User user){
        binding.textName.setText(user.name);
        binding.textEmail.setText(user.email);
        binding.imageProfile.setImageBitmap(getUserImage(user.image));
        binding.getRoot().setOnClickListener(view -> _userListener.onUserCLicked(user));
    }


    private Bitmap getUserImage(String encodeImage){
        byte[] bytes= Base64.decode(encodeImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);

    }
}

}