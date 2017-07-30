package com.apps.koru.star8_video_app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.apps.koru.star8_video_app.R;
import com.apps.koru.star8_video_app.objects.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    Model  appModel = Model.getInstance();
    String email = "";
    String pass = "";

    EditText inpMail;EditText inpPaas;
    Button login ;Button msg;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appModel.mAuth = FirebaseAuth.getInstance();

        inpMail = (EditText) findViewById(R.id.inpmail);
        inpPaas = (EditText) findViewById(R.id.inppass);
        msg = (Button)findViewById(R.id.msg);
        msg.setVisibility(View.INVISIBLE);
        login = (Button) findViewById(R.id.loginButoon);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email  = inpMail.getText().toString();
                pass = inpPaas.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()){
                    System.out.println(email + " " + pass );
                    connection();
                }
                else {
                    msg.setVisibility(View.VISIBLE);
                    msg.setText(" ERROR - mail or password is incorrect!");
                }
            }
        });
    }
    public void connection(){
        appModel.mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = appModel.mAuth.getCurrentUser();
                    msg.setText("ok");
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    msg.setVisibility(View.VISIBLE);
                    msg.setText(" ERROR - mail or password is incorrect!");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!(appModel.mAuth.getCurrentUser() == null)){
            if (!(appModel.mAuth.getCurrentUser().getUid().isEmpty())){
                Intent intent = new Intent(context,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }
}
