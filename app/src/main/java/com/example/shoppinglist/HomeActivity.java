package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppinglist.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    private FloatingActionButton fab_btn;
    private FirebaseAuth mauth;
    DatabaseReference mdatabasereference;
    String uId;
    FirebaseRecyclerAdapter<Data , MyViewHolder> adapter;
    private CoordinatorLayout coordinatorLayout;
    private  TextView totalAmout_tv;
    private RecyclerView recyclerView;

    // for update and delete purpose
    private String amount , note, type,postKey,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Shopping List");


        fab_btn = findViewById(R.id.fab_add);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        totalAmout_tv = findViewById(R.id.totalamount_tv);

        mauth = FirebaseAuth.getInstance();
        FirebaseUser muser = mauth.getCurrentUser();
        uId = muser.getUid();

        mdatabasereference = FirebaseDatabase.getInstance().getReference().child("shopping List").child(uId);
        mdatabasereference.keepSynced(true);

        recyclerView = findViewById(R.id.recyclerview_home);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        Snackbar.make(coordinatorLayout,"Loading list items....",Snackbar.LENGTH_LONG).show();
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customdialog();
            }
        });
        mdatabasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long totalAmount = 0;
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Data temp = snap.getValue(Data.class);
                    totalAmount += temp.getAmount();
                }
                totalAmout_tv.setText(totalAmount+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen_manu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_btn:
                mauth.signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void customdialog(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data,null);

        final AlertDialog dialog = mydialog.create();
        dialog.setView(myview);
        dialog.show();
        final EditText type = myview.findViewById(R.id.type_ed);
        final EditText amount = myview.findViewById(R.id.amount_ed);
        final EditText note = myview.findViewById(R.id.note_ed);
        Button save = myview.findViewById(R.id.save_btn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mtype = type.getText().toString().trim();
                String mamount = amount.getText().toString().trim();
                String mnote = note.getText().toString().trim();

                int Intamount = Integer.parseInt(mamount);
                if(TextUtils.isEmpty(mtype)){
                    type.setError("Field cant be empty");
                    return;
                }
                if(TextUtils.isEmpty(mamount)){
                    amount.setError("Field cant be empty");
                    return;
                }
                if(TextUtils.isEmpty(mnote)){
                    note.setError("Field cant be empty");
                    return;
                }
                // check here///
                String mId = mdatabasereference.push().getKey();

                String mdate = DateFormat.getInstance().format(new Date());

                Data data = new Data(mtype , Intamount , mnote,mdate,mId);

                mdatabasereference.child(mId).setValue(data);

                dialog.dismiss();
                Snackbar.make(coordinatorLayout,"Item added successfully!",Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mdatabasereference, Data.class)
                        .build();
        adapter= new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        options
                ) {
            @Override
            protected void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i, @NonNull final Data data) {
                myViewHolder.setAmount(data.getAmount());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setType(data.getType());
                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postKey = getRef(myViewHolder.getAdapterPosition()).getKey();

                        type = data.getType();
                        amount = String.valueOf(data.getAmount());
                        note = data.getNote();
                        date = data.getDate();
                        updateField();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data,parent,false);
                return new MyViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void updateField() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_update,null);

        final AlertDialog dialog = mydialog.create();
        dialog.setView(myview);
        dialog.show();

        final EditText type_et = myview.findViewById(R.id.type_ued);
        final EditText amout_et = myview.findViewById(R.id.amount_ued);
        final EditText note_et = myview.findViewById(R.id.note_ued);
        Button update_btn = myview.findViewById(R.id.update_btn);
        Button delete_btn = myview.findViewById(R.id.delete_btn);
        type_et.setText(type);
        type_et.setSelection(type.length());
        amout_et.setText(amount);
        amout_et.setSelection(amount.length());
        note_et.setText(note);
        note_et.setSelection(note.length());

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtype,mamount,mnote;
                mtype = type_et.getText().toString().trim();
                mamount = amout_et.getText().toString().trim();
                mnote = note_et.getText().toString().trim();

                Data temp = new Data(mtype,Integer.parseInt(mamount),mnote,date,postKey);
                mdatabasereference.child(postKey).setValue(temp);
                dialog.dismiss();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdatabasereference.child(postKey).removeValue();
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public TextView type_tv ,note_tv , date_tv , amount_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.myView = itemView;
            type_tv = myView.findViewById(R.id.type_tv);
            note_tv = myView.findViewById(R.id.note_tv);
            date_tv = myView.findViewById(R.id.date_tv);
            amount_tv = myView.findViewById(R.id.amount_tv);

        }

        public  void setType(String type){

            type_tv.setText(type);
        }

        public  void setNote(String note){

            note_tv.setText(note);
        }

        public  void setDate(String date){

            date_tv.setText(date);
        }

        public  void setAmount(int amount){

            amount_tv.setText(amount+"");
        }
    }
}
