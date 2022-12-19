package com.samansepahvand.samanchapapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.samansepahvand.samanchapapp.R;
import com.samansepahvand.samanchapapp.adapters.ChatAdapter;
import com.samansepahvand.samanchapapp.databinding.ActivityChatBinding;
import com.samansepahvand.samanchapapp.models.ChatMessage;
import com.samansepahvand.samanchapapp.models.User;
import com.samansepahvand.samanchapapp.network.ApiClient;
import com.samansepahvand.samanchapapp.network.ApiService;
import com.samansepahvand.samanchapapp.utilities.Constants;
import com.samansepahvand.samanchapapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {


    private ActivityChatBinding binding;
    private User receiverUser;


    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    private String conversationId=null;

    private Boolean isReceiverAvailable=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
        loadReceiverDetails();
        init();
        listenMessage();

    }


    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter=new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)

        );
        binding.recyclerView.setAdapter(chatAdapter);
        database=FirebaseFirestore.getInstance();
    }


    private void    listenAvailabilityOfReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this,(value,error)->{
            if (error!=null){
                return;
            }
            if (value!=null){
                if (value.getLong(Constants.KEY_AVAILABILITY)!=null){
                    int availability= Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();
                    isReceiverAvailable=availability==1;
                }
                receiverUser.token=value.getString(Constants.KEY_FCM_TOKEN);
            }
            if (isReceiverAvailable){
                binding.textAvailability.setVisibility(View.VISIBLE);
            }else{
                binding.textAvailability.setVisibility(View.GONE);
            }
        });
    }
    private void  sendMessage(){
        HashMap<String,Object> message=new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversationId!=null){
            updateConversion(binding.inputMessage.getText().toString());
        }else{
            HashMap<String,Object> conversion=new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE,preferenceManager.getString(Constants.KEY_IMAGE));

            conversion.put(Constants.KEY_RECEIVER_ID,receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME,receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE,receiverUser.image);


            conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP,new Date());

            addConversion(conversion);
        }


        if (!isReceiverAvailable){
            try{

                JSONArray  token=new JSONArray();
                token.put(receiverUser.token);

                JSONObject  data=new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());

                JSONObject  body=new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,token);

                sendNotification(body.toString());


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        binding.inputMessage.setText(null);

    }





    private Bitmap getBitmapFromEncodedString(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);

    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v->sendMessage());

    }

    private String  getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }


    private void listenMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);



    }

    private final EventListener<QuerySnapshot> eventListener=(value,error)->{
        if (error!=null){
            return;
        }
        if (value!=null){
            int count=chatMessages.size();
            for (DocumentChange documentChange:value.getDocumentChanges()){
                if (documentChange.getType()== DocumentChange.Type.ADDED){
                    ChatMessage chatMessage=new ChatMessage();

                    chatMessage.senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receivedId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message=documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime=getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dataObject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);

                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1,obj2)->obj1.dataObject.compareTo(obj2.dataObject));
            if (count==0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.recyclerView.setAdapter(chatAdapter);

            }

            binding.recyclerView.setVisibility(View.VISIBLE);

        }
       binding.progressBar.setVisibility(View.GONE);
        if (conversationId==null){
            checkForConversion();
        }
    };



    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener=task ->{
        if (task.isSuccessful() && task.getResult()!=null&&task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversationId=documentSnapshot.getId();
        }
    };
    private void checkForConversationRemotely(String senderId,String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);

    }

    private void checkForConversion(){
        if (chatMessages.size()!=0){
            checkForConversationRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversationRemotely(

                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void addConversion(HashMap<String,Object> conversion){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversationId=documentReference.getId());
    }

    private void  updateConversion(String message){

        DocumentReference documentReference=
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE,message,
                Constants.KEY_TIMESTAMP ,new Date()
        );


    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }



    private void showToast(String message){
        Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.isSuccessful()){
                    try{

                        if (response.body()!=null){
                            JSONObject responseJson=new JSONObject(response.body());
                            JSONArray result=responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure")==1){
                                JSONObject error=(JSONObject) result.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    showToast("Notification send successfully");
                }else{
                    showToast("error:"+response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                showToast("onFailure:"+t.getMessage());
            }
        });

    }

}