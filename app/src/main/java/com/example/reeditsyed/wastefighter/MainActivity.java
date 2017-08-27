package com.example.reeditsyed.wastefighter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private List<String> pNums;
    private int shift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ref = FirebaseDatabase.getInstance().getReference();
        pNums = new ArrayList<>();
        shift = 0;
    }

    public void goToOfferScreen(View v){
        setContentView(R.layout.activity_offer);
    }

    public void goToNextOption(View v){
        shift += 3;
        goToGetScreen(v);
    }

    public void goToPhoneScreen(View v){

        String pNum = "";

        RadioButton[] opt = new RadioButton[3];
        opt[0] = (RadioButton)findViewById(R.id.foodOption1);
        opt[1] = (RadioButton)findViewById(R.id.foodOption2);
        opt[2] = (RadioButton)findViewById(R.id.foodOption3);

        if (opt[0].isChecked()) {
            setContentView(R.layout.activity_phone);
            pNum = pNums.get(0);
            ((TextView) (findViewById(R.id.phoneField))).setText(pNums.get(0));
        }else if (opt[1].isChecked()) {
            setContentView(R.layout.activity_phone);
            pNum = pNums.get(1);
            ((TextView) (findViewById(R.id.phoneField))).setText(pNums.get(1));
        } else if (opt[2].isChecked()) {
            setContentView(R.layout.activity_phone);
            pNum = pNums.get(2);
            ((TextView) (findViewById(R.id.phoneField))).setText(pNums.get(2));
        }else
            Toast.makeText(this,"Please select an option",Toast.LENGTH_LONG).show();

        Uri number = Uri.parse("tel:"+pNum);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);

    }

    public void goToGetScreen(View v){
        setContentView(R.layout.activity_get);

        final RadioButton[] opt = new RadioButton[3];
        opt[0] = (RadioButton)findViewById(R.id.foodOption1);
        opt[1] = (RadioButton)findViewById(R.id.foodOption2);
        opt[2] = (RadioButton)findViewById(R.id.foodOption3);

        opt[0].setVisibility(View.VISIBLE);
        opt[1].setVisibility(View.VISIBLE);
        opt[2].setVisibility(View.VISIBLE);

        pNums = new ArrayList<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot){

                int i=shift;

                for (DataSnapshot child:snapshot.getChildren()){
                    if (shift <= i && i < shift+3) {
                        opt[i-shift].setText(dataSnapToString(child));
                        i++;
                    }
                    pNums.add(child.child("phone").getValue(String.class));
                }

                for (i=i;i<shift+3;i++){
                    opt[i-shift].setVisibility(View.INVISIBLE);
                    findViewById(R.id.nextButton).setVisibility(View.INVISIBLE);
                }

            }

            public void onCancelled(DatabaseError e){}
        });

    }

    public void finishGiving(View v){
        EditText phone,pickUp,itemDesc,name;

        phone = (EditText) findViewById(R.id.phone);
        pickUp = (EditText) findViewById(R.id.pickUp);
        itemDesc = (EditText) findViewById(R.id.itemDesc);
        name = (EditText) findViewById(R.id.name);

        if (phone.getText().toString().equals("") || pickUp.getText().toString().equals("") || itemDesc.getText().toString().equals(""))
            Toast.makeText(this,"Please fill out all non-optional fields.",Toast.LENGTH_LONG).show();
        else {
            DatabaseReference postRef = ref.push();
            String postID = postRef.getKey();

            ref.child(postID).child("phone").setValue(phone.getText().toString());
            ref.child(postID).child("pickUp").setValue(pickUp.getText().toString());
            ref.child(postID).child("itemDesc").setValue(itemDesc.getText().toString());

            if (!name.getText().toString().equals(""))
                ref.child(postID).child("name").setValue(name.getText().toString());

            setContentView(R.layout.activity_done);
        }
    }

    public void goBackToMenu(View v){
        setContentView(R.layout.activity_main);
    }

    public String dataSnapToString(DataSnapshot s){
        String out = s.child("itemDesc").getValue()+" at "+s.child("pickUp").getValue();
        return out;
    }

}
