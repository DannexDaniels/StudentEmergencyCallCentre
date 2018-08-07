package com.dannextech.apps.studentemergencycallcentre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton ivbHealthCall,ivbSecurityCall,ivbAdminCall,ivbHealthMail,ivbSecurityMail,ivbAdminMail;
    Button btHealthMsg, btAdminMsg, btSecurityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivbAdminCall = findViewById(R.id.ivbAdminCall);
        ivbAdminMail = findViewById(R.id.ivbAdminMail);
        ivbSecurityCall = findViewById(R.id.ivbSecurityCall);
        ivbSecurityMail = findViewById(R.id.ivbSecurityMail);
        ivbHealthCall = findViewById(R.id.ivbHealthCall);
        ivbHealthMail = findViewById(R.id.ivbHealthMail);
        btAdminMsg = findViewById(R.id.btAdminMsg);
        btHealthMsg = findViewById(R.id.btHealthMsg);
        btSecurityMsg = findViewById(R.id.btSecurityMsg);

        ivbHealthMail.setOnClickListener(this);
        ivbHealthCall.setOnClickListener(this);
        ivbSecurityMail.setOnClickListener(this);
        ivbSecurityCall.setOnClickListener(this);
        ivbHealthCall.setOnClickListener(this);
        ivbHealthMail.setOnClickListener(this);
        btSecurityMsg.setOnClickListener(this);
        btHealthMsg.setOnClickListener(this);
        btAdminMsg.setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        switch (v.getId()){
            case R.id.ivbAdminCall:
                Uri numberAdmin = Uri.parse("tel:0799119911");
                startActivity(new Intent(Intent.ACTION_DIAL,numberAdmin));
                break;
            case R.id.ivbAdminMail:
                Intent intentAdmin = new Intent(Intent.ACTION_SEND);
                intentAdmin.setType("text/plain");
                intentAdmin.putExtra(Intent.EXTRA_EMAIL,new String[]{"admin@mmust.ac.ke"});
                intentAdmin.putExtra(Intent.EXTRA_SUBJECT,"REPORTING AN ISSUE");
                intentAdmin.putExtra(Intent.EXTRA_TEXT,"I am reporting an issue that ...");
                try{
                    startActivity(Intent.createChooser(intentAdmin,"Send mail..."));
                }catch (android.content.ActivityNotFoundException exception){
                    Snackbar.make(v,"There is no email clients installed",Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivbHealthCall:
                Uri numberHealth = Uri.parse("tel:0787654321");
                startActivity(new Intent(Intent.ACTION_DIAL,numberHealth));
                break;
            case R.id.ivbHealthMail:
                Intent intentHealth = new Intent(Intent.ACTION_SEND);
                intentHealth.setType("text/plain");
                intentHealth.putExtra(Intent.EXTRA_EMAIL,new String[]{"health@mmust.ac.ke"});
                intentHealth.putExtra(Intent.EXTRA_SUBJECT,"REPORTING AN ISSUE");
                intentHealth.putExtra(Intent.EXTRA_TEXT,"I am reporting an issue that ...");
                try{
                    startActivity(Intent.createChooser(intentHealth,"Send mail..."));
                }catch (android.content.ActivityNotFoundException exception){
                    Snackbar.make(v,"There is no email clients installed",Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivbSecurityCall:
                Uri numberSecurity = Uri.parse("tel:0712345678");
                startActivity(new Intent(Intent.ACTION_DIAL,numberSecurity));
                break;
            case R.id.ivbSecurityMail:
                Intent intentSecurity = new Intent(Intent.ACTION_SEND);
                intentSecurity.setType("text/plain");
                intentSecurity.putExtra(Intent.EXTRA_EMAIL,new String[]{"security@mmust.ac.ke"});
                intentSecurity.putExtra(Intent.EXTRA_SUBJECT,"REPORTING AN ISSUE");
                intentSecurity.putExtra(Intent.EXTRA_TEXT,"I am reporting an issue that ...");
                try{
                    startActivity(Intent.createChooser(intentSecurity,"Send mail..."));
                }catch (android.content.ActivityNotFoundException exception){
                    Snackbar.make(v,"There is no email clients installed",Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.btAdminMsg:
                startActivity(new Intent(getApplicationContext(),SendMessage.class));
                editor.putString("phone","0799119911");
                editor.apply();
                break;
            case R.id.btHealthMsg:
                startActivity(new Intent(getApplicationContext(),SendMessage.class));
                editor.putString("phone","0787654321");
                editor.apply();
                break;
            case R.id.btSecurityMsg:
                startActivity(new Intent(getApplicationContext(),SendMessage.class));
                editor.putString("phone","0712345678");
                editor.apply();
                break;
                default:
                    Snackbar.make(v,"Function not working",Snackbar.LENGTH_SHORT).show();
        }
    }
}
