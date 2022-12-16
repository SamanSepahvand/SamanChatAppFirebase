    package com.samansepahvand.samanchapapp.activities;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.util.Patterns;
    import android.view.View;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.samansepahvand.samanchapapp.utilities.PreferenceManager;
    import com.samansepahvand.samanchapapp.databinding.ActivitySignInBinding;
    import com.samansepahvand.samanchapapp.utilities.Constants;


    public class SignInActivity extends AppCompatActivity {

        private static final String TAG = "FCM";
        String newToken="null";
        private ActivitySignInBinding binding;

        private PreferenceManager preferenceManager;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            preferenceManager=new PreferenceManager(getApplicationContext());
            if (preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
            binding=ActivitySignInBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            setListeners();
        }
        private void setListeners(){
            binding.textCreateNewAccount.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            });

            binding.buttonSignIn.setOnClickListener(view -> {
              if (isValidSignInDetail()){
                  SignIn();
              }
            });

        }


        private void SignIn(){

            isLoading(true);
            FirebaseFirestore database=FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                    .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() &&task.getResult()!=null
                         && task.getResult().getDocuments().size()>0){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN,true);
                            preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));

                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }else{
                            isLoading(false);
                            shoToast("Unable to sign in !");
                            Log.e(TAG, "SignIn: "+task.getException().getMessage());
                        }
                    });
        }


        private void isLoading(Boolean isLoading) {
            if (isLoading) {
                binding.buttonSignIn.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.buttonSignIn.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

        }


        private void shoToast(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }


        private Boolean isValidSignInDetail() {


             if (binding.inputEmail.getText().toString().trim().isEmpty()) {
                shoToast("Enter name");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
                shoToast("Enter valid  email");
                return false;
            } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
                shoToast("Enter password.");
                return false;
            } else {
                return true;
            }
        }



    }