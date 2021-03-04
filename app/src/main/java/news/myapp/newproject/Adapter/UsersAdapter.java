    package news.myapp.newproject.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import news.myapp.newproject.Models.Users;
import news.myapp.newproject.R;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    ArrayList<Users> list;
    ArrayList<String> keyList = new ArrayList<>();
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_users, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = list.get(position);

        String[] dob = users.getDateOfBirth().split("/");
        String age = getAge(dob[2],dob[1],dob[0]);

        Picasso.get().load(users.getProfilepic()).placeholder(R.drawable.avatar).into(holder.imageView);
        holder.userName.setText(users.getFirstName()+" "+users.getLastName());
        holder.description.setText(users.getGender()+" | "+age+" | "+users.getState());

        FirebaseDatabase.getInstance().getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    keyList.add(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("User").child(keyList.get(keyList.size()-1-position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "User removed successfully!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView,delete;
        TextView userName, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile_image);
            delete = itemView.findViewById(R.id.delete);
            userName = itemView.findViewById(R.id.userName);
            description = itemView.findViewById(R.id.description);
        }
    }


    private String getAge(String Year, String Month, String Day){

        int year = Integer.parseInt(Year);
        int month = Integer.parseInt(Month);
        int day = Integer.parseInt(Day);

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if(age == 0)
        {
            if(today.get(Calendar.MONTH)+1 == dob.get(Calendar.MONTH))
                if(today.get(Calendar.DATE) < dob.get(Calendar.DATE))
                    return "-1";
        }
        if(today.get(Calendar.MONTH)+1 < dob.get(Calendar.MONTH) )
            age--;

        if(today.get(Calendar.MONTH)+1 == dob.get(Calendar.MONTH))
            if(today.get(Calendar.DATE) < dob.get(Calendar.DATE))
                age--;


        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}
