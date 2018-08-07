package com.dannextech.apps.studentemergencycallcentre;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private static final String TAG = "LOGIN_LOG";

    EditText etEmail,etPassword;
    Button btSignIn,btSignUp;

    FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        //Check if the user is signed in and update accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            Log.e(TAG, "onStart: user "+currentUser.getUid()+" with email address "+currentUser.getEmail()+" is already signed in");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        etEmail = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPasswordLogin);

        btSignIn = (Button) findViewById(R.id.btnLogin);
        btSignUp = (Button) findViewById(R.id.btnSignUp);

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    showProgressDialog();
                    mAuth.signInWithEmailAndPassword(etEmail.getText().toString(),etPassword.getText().toString()).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.e(TAG, "onComplete: user created successfully");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                                retrieveUserDetails(user.getUid());
                            }else {
                                hideProgressDialog();
                                Toast.makeText(getApplicationContext(),"Authentication failed: "+task.getException(),Toast.LENGTH_LONG).show();
                                Log.e(TAG, "onComplete: user creation failed",task.getException());
                            }
                        }
                    });
                }else {
                    Snackbar.make(v,"Failed: Check your internet Connection",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterUser.class));
            }
        });
    }

    private void retrieveUserDetails(String uid) {

        //creating a reference to the folder users where the details will be saved
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("Users/"+uid);

        DatabaseReference categoryRef = databaseRef.getRef().child("Category");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: details are "+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*String category = dataSnapshot.getValue().toString();
                Log.e(TAG, "onDataChange: "+category);*/
                hideProgressDialog();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
                Toast.makeText(getApplicationContext(),"Something went wrong. Please try again"+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(Login.this,"Authenticating","Please Wait",true);
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
