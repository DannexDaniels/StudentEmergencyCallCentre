package com.dannextech.apps.studentemergencycallcentre;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

public class RegisterUser extends AppCompatActivity {

    private static final String TAG = "SIGN_UP_LOG";

    com.google.firebase.database.FirebaseDatabase database;
    DatabaseReference databaseReference;

    FirebaseAuth mAuth;

    EditText etName, etRegNo, etEmail, etPhone, etPassword, etPasswordConfirm;
    Button btnSignUp;
    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();

        //Check if the user is signed in and update accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            android.util.Log.e(TAG, "onStart: user "+currentUser.getUid()+" with email address "+currentUser.getEmail()+" is already signed in");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        etName = findViewById(R.id.etNames);
        etRegNo = findViewById(R.id.etRegNo);
        etEmail = findViewById(R.id.etEmailAddress);
        etPhone = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        btnSignUp = findViewById(R.id.btRegisterUser);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if (checkPassword(etPassword.getText().toString().trim(),etPasswordConfirm.getText().toString().trim()) && validateEntries(etEmail.getText().toString().trim(),etPassword.getText().toString().trim(),etName.getText().toString().trim(), etPhone.getText().toString().trim(), etRegNo.getText().toString().trim())){
                        createUser(etEmail.getText().toString().trim(),etPassword.getText().toString().trim());
                    }
                }else
                    Snackbar.make(v,"Failed: Check your internet Connection",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validateEntries(String email, String password, String name, String phone, String regNo) {
        if (email.isEmpty()){
            etEmail.setError("required");
            return false;
        }else if (password.isEmpty()){
            etPassword.setError("required");
            return false;
        }else if (name.isEmpty()){
            etName.setError("required");
            return false;
        }else if (phone.isEmpty()){
            etPhone.setError("required");
            return false;
        }else if (regNo.isEmpty()){
            etRegNo.setError("required");
            return false;
        }
        android.util.Log.e(TAG, "validateEntries: all valid");
        return true;
    }

    private boolean checkPassword(String pass, String passconf) {
        if (!pass.equals(passconf)){
            etPasswordConfirm.setError("Do not match with Password");
            Toast.makeText(getApplicationContext(),"Password do not match the Confirm Password",Toast.LENGTH_SHORT).show();
            return false;
        }else if (pass.length()<6){
            etPassword.setError("Password should be more than 6 characters");
            Toast.makeText(getApplicationContext(),"Password should be more than 6 characters",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            Log.e(TAG, "checkPassword: password is authentic");
            return true;
        }
    }

    private void createUser(String email, String password){
        showProgressDialog();
        final FirebaseUser[] user = new FirebaseUser[1];
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterUser.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.e(TAG, "onComplete: user created successfully");
                    user[0] = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                    uploadUserDetails(user[0],etName.getText().toString(), etPhone.getText().toString(), etRegNo.getText().toString());
                }else {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"User Creation Failed"+task.getException(),Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onComplete: user creation failed",task.getException());
                }
            }
        });
    }
    private void uploadUserDetails(FirebaseUser user, String name, String phone, String regNo) {
        //creating a reference to the folder users where the details will be saved
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users/"+regNo);

        //creating references to where specific details will be saved
        DatabaseReference nameRef = databaseReference.child("Name");
        DatabaseReference emailRef = databaseReference.child("Email");
        DatabaseReference phoneRef = databaseReference.child("Phone");
        DatabaseReference regNoRef = databaseReference.child("regNo");

        nameRef.setValue(name);
        emailRef.setValue(user.getEmail());
        phoneRef.setValue(phone);
        regNoRef.setValue(regNo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(),"User Saved Successfully",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(RegisterUser.this,"Creating Account","Please Wait",true);
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
