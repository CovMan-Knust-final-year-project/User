package com.tekdevisal.covman.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tekdevisal.covman.Helpers.Accessories;
import com.tekdevisal.covman.Pick_call_Dialer;

import java.util.Objects;

@SuppressLint("Registered")
public class Incoming_calls_service extends Service {

    FirebaseAuth myauth = FirebaseAuth.getInstance();
    String caller_id = "", caller_name, token="";
    Accessories serviceAccessor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseReference my_calls = FirebaseDatabase.getInstance().getReference("calls")
                .child(Objects.requireNonNull(myauth.getCurrentUser()).getUid());
        my_calls.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("ringing")) {
                        caller_id = dataSnapshot.child("ringing").getValue().toString();
                        token = dataSnapshot.child("token").getValue().toString();
                        DatabaseReference getname = FirebaseDatabase.getInstance().getReference("users")
                                .child(caller_id);
                        getname.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    caller_name = dataSnapshot.child("name").getValue().toString();
                                    Intent gotodialer = new Intent(getApplicationContext(), Pick_call_Dialer.class);
                                    gotodialer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    serviceAccessor = new Accessories(getApplicationContext());
                                    serviceAccessor.put("doc_id", caller_id);
                                    serviceAccessor.put("doc_name", caller_name);
                                    serviceAccessor.put("token", token);
                                    serviceAccessor.put("action", "accepting_call");
                                    startActivity(gotodialer);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    sendBroadcast(new Intent("to_close"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
