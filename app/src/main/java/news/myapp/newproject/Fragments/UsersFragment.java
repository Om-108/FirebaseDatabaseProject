package news.myapp.newproject.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import news.myapp.newproject.Adapter.UsersAdapter;
import news.myapp.newproject.Models.Users;
import news.myapp.newproject.R;
import news.myapp.newproject.databinding.FragmentUsers2Binding;

public class UsersFragment extends Fragment {

    public UsersFragment() {
        // Required empty public constructor
    }

    FragmentUsers2Binding fragmentUsers2Binding;
    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentUsers2Binding=FragmentUsers2Binding.inflate(inflater, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        UsersAdapter usersAdapter = new UsersAdapter(list, getContext());

        fragmentUsers2Binding.usersRecyclerView.setAdapter(usersAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        fragmentUsers2Binding.usersRecyclerView.setLayoutManager(linearLayoutManager);
        firebaseDatabase.getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);

                    list.add(0,users);
                }
                usersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return fragmentUsers2Binding.getRoot();
    }
}