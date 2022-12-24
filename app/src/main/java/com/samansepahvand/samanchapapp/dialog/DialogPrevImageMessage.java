package com.samansepahvand.samanchapapp.dialog;


import android.app.Dialog;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;


import androidx.annotation.NonNull;

import com.samansepahvand.samanchapapp.R;
import com.samansepahvand.samanchapapp.models.ChatMessage;


public class DialogPrevImageMessage extends Dialog implements View.OnClickListener {

    private Context mContext;
    private Button btnSendMessage;
    private ImageView imgCloseDialog;
    private OnAcceptInterface acceptListener;
    private OnCancelInterface cancelListener;
private ImageView imgMessage;
    private FrameLayout layoutSend;

    private ChatMessage chatMessage;

    private EditText edtCaption;

    public DialogPrevImageMessage(@NonNull Context context,ChatMessage chatMessage ) {
        super(context);
        this.mContext = context;
        setContentView(R.layout.dialog_image_message);
      //  this.getWindow().getAttributes().windowAnimations = R.style.AlertDialogAnimation;
        this.chatMessage=chatMessage;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();


    }


    private void initView() {

        layoutSend = findViewById(R.id.layoutSend);
       // btnSendMessage = findViewById(R.id.btnSendImageMeesage);
        imgCloseDialog = findViewById(R.id.imageBack);

        imgMessage=findViewById(R.id.imageMessage);
edtCaption=findViewById(R.id.inputMessage);

        layoutSend.setOnClickListener(this);
        imgCloseDialog.setOnClickListener(this);

        try {
            imgMessage.setImageBitmap(getBitmapFromEncodedString(chatMessage.imageMessage));

        }catch (Exception e){
            e.printStackTrace();
            Log.e("TAG", "initView: "+e.getMessage());
        }

    }

    private Bitmap getBitmapFromEncodedString(String encodeImage) {

        if (encodeImage != null) {
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }
    public Dialog setAcceptButton(OnAcceptInterface acceptListener) {
        this.acceptListener = acceptListener;
        //btnCancel.setVisibility(View.VISIBLE);
        return this;
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.layoutSend:
              if (acceptListener != null)

                  chatMessage.message=edtCaption.getText().toString();

                    acceptListener.accept(chatMessage);

                dismiss();
                break;
        }
        // dismiss();
    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


//    private void animOpenCloseDialog() {
//        imgCloseDialog.startAnimation(MainApplication.SetAnimation("Rotate"));
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dismiss();
//            }
//        }, Constants.DelayTimeDialogAnimation);
//    }



    public interface OnAcceptInterface {
        void accept(ChatMessage chatMessage);
    }

    public interface OnCancelInterface {
        void cancel();
    }
}
