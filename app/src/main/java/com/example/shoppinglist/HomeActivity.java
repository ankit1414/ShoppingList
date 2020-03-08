package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    private FloatingActionButton fab_btn;
    private FirebaseAuth mauth;
    DatabaseReference mdatabasereference;
    String uId;

    FirebaseRecyclerAdapter<Data , MyViewHolder> adapter;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Shopping List");

        recyclerView = findViewById(R.id.recyclerview_home);
        fab_btn = findViewById(R.id.fab_add);


        mauth = FirebaseAuth.getInstance();
        FirebaseUser muser = mauth.getCurrentUser();
        uId = muser.getUid();
        Snackbar.make(recyclerView , "snackbar message" , Snackbar.LENGTH_LONG).show();

        mdatabasereference = FirebaseDatabase.getInstance().getReference().child("shopping List");

        mdatabasereference.keepSynced(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fetch();





        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customdialog();
            }
        });
    }
    private void customdialog(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data,null);

        final AlertDialog dialog = mydialog.create();
        dialog.setView(myview);
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
                String mId = mdatabasereference.push().getKey();

                String mdate = DateFormat.getInstance().format(new Date());

                Data data = new Data(mtype , Intamount , mnote,mdate,mId);

                mdatabasereference.child(uId).child(mId).setValue(data);

                Toast.makeText(getApplicationContext() , "added successfully" , Toast.LENGTH_SHORT).show();



                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }
    public void fetch(){

        Query query = FirebaseDatabase.getInstance()
                .getReference().child("shopping List").child(uId);


        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(query, new SnapshotParser<Data>() {
                    @NonNull
                    @Override
                    public Data parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Data(snapshot.child("type").getValue().toString() ,
                                Integer.parseInt(snapshot.child("amount").getValue().toString()),
                                snapshot.child("note").getValue().toString() ,
                                snapshot.child("date").getValue().toString(),
                                snapshot.child("id").getValue().toString());
                    }
                })
                .build();




        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i, @NonNull Data data) {

                myViewHolder.setType(data.getType());
                myViewHolder.setDate(data.getDate());
                myViewHolder.setAmount(data.getAmount());
                myViewHolder.setNote(data.getNote());

//                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast.makeText(HomeActivity.this, String.valueOf(i), Toast.LENGTH_SHORT).show();
//                    }
//                });


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_data , parent , false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View myView;
        public static TextView type_tv ,note_tv , date_tv , amount_tv;

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

            amount_tv.setText(amount);
        }
    }
}
