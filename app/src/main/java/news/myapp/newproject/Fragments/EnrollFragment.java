package news.myapp.newproject.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.MultiFactor;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import news.myapp.newproject.Models.Users;
import news.myapp.newproject.R;
import news.myapp.newproject.databinding.FragmentEnrollBinding;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link //Fragment} subclass.
 * Use the {@link //EnrollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class EnrollFragment extends Fragment {

    FragmentEnrollBinding fragmentEnrollBinding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    private final int PICK_IMAGE_REQUEST = 22;
    StorageReference storageReference;

    Uri uri = Uri.parse("android.resource://" + "news.myapp.newproject" + "/" + R.drawable.avatar);

    String id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentEnrollBinding = FragmentEnrollBinding.inflate(inflater,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        fragmentEnrollBinding.editTextFirstName.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextLastName.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextDate.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextGender.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextCountry.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextState.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextHomeTown.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextPhoneNumber.addTextChangedListener(checkTextWatcher);
        fragmentEnrollBinding.editTextTelephoneNumber.addTextChangedListener(checkTextWatcher);


        fragmentEnrollBinding.profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //startActivityForResult(intent, 33);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        PICK_IMAGE_REQUEST);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.keepSynced(true);

        fragmentEnrollBinding.addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                id = databaseReference.child("User").push().getKey();
                upload(id);

                //to hide the keyboard
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fragmentEnrollBinding.addUserButton.getWindowToken(), 0);


            }
        });
        return fragmentEnrollBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            if (data.getData() != null) {
                uri = data.getData();
                fragmentEnrollBinding.profilePicButton.setImageURI(uri);
            }
        }

    }

    private void upload(String id)
    {
        if(isNetworkConnected()) {
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child("Profile_Pics").child(id);
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    //  Toast.makeText(getContext(), "Upload Complete!!", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String fname = fragmentEnrollBinding.editTextFirstName.getText().toString();
                            String lname = fragmentEnrollBinding.editTextLastName.getText().toString();
                            String dob = fragmentEnrollBinding.editTextDate.getText().toString();
                            String gender = fragmentEnrollBinding.editTextGender.getText().toString();
                            String country = fragmentEnrollBinding.editTextCountry.getText().toString();
                            String propic = uri.toString();
                            String state = fragmentEnrollBinding.editTextState.getText().toString();
                            String town = fragmentEnrollBinding.editTextHomeTown.getText().toString();
                            String phone = fragmentEnrollBinding.editTextPhoneNumber.getText().toString();
                            String tPhone = fragmentEnrollBinding.editTextTelephoneNumber.getText().toString();

                            if (isValid(phone)) {
                                if (isValid(tPhone)) {

                                    databaseReference = FirebaseDatabase.getInstance().getReference();

                                    Query query = databaseReference.child("User").orderByChild("phone").equalTo(phone);

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if (snapshot.getChildrenCount() == 0) {
                                                Query query = databaseReference.child("User").orderByChild("telePhone").equalTo(tPhone);

                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        if (snapshot.getChildrenCount() == 0) {
                                                            if (isDateFormatCorrect(dob)) {
                                                                if (isDateValid(dob)) {

                                                                    String dobs[] = dob.split("/");
                                                                    int age = Integer.parseInt(getAge(dobs[2], dobs[1], dobs[0]));

                                                                    if (age >= 0) {
                                                                        writeUser(fname, lname, dob, gender, country, propic, state, town, phone, tPhone);
                                                                        wipeAll();
                                                                    } else
                                                                        fragmentEnrollBinding.editTextDate.setError("Date of Birth must be greater than today's date");
                                                                } else
                                                                    fragmentEnrollBinding.editTextDate.setError("Please enter a valid date");
                                                            } else
                                                                fragmentEnrollBinding.editTextDate.setError("Date of Birth Format must be DD/MM/YYYY");
                                                        } else
                                                            fragmentEnrollBinding.editTextTelephoneNumber.setError("Telephone number already exists!!");
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            } else
                                                fragmentEnrollBinding.editTextPhoneNumber.setError("Phone number already exists!!");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else
                                    fragmentEnrollBinding.editTextTelephoneNumber.setError("Telephone number is invalid!!");
                            } else
                                fragmentEnrollBinding.editTextPhoneNumber.setError("Phone number is invalid!!");
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    float dataUploadAmount = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Please wait while the data is uploading...");
                }
            });
        }
        else
            Toast.makeText(getContext(), "Please check for Internet COnnection!!", Toast.LENGTH_SHORT).show();
    }

    private void wipeAll()
    {
        fragmentEnrollBinding.editTextFirstName.setText("");
        fragmentEnrollBinding.editTextLastName.setText("");
        fragmentEnrollBinding.editTextGender.setText("");
        fragmentEnrollBinding.editTextCountry.setText("");
        fragmentEnrollBinding.editTextHomeTown.setText("");
        fragmentEnrollBinding.editTextTelephoneNumber.setText("");
        fragmentEnrollBinding.editTextPhoneNumber.setText("");
        fragmentEnrollBinding.editTextState.setText("");
        fragmentEnrollBinding.editTextDate.setText("");
        fragmentEnrollBinding.profilePicButton.setImageDrawable(getResources().getDrawable(R.drawable.propic1));

    }

    public void writeUser(String firstName, String lastName, String dateOfBirth, String gender, String country, String profilepic,String state, String homeTown, String phone, String telePhone)
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("User");

        HashMap<String, String> map = new HashMap<>();

        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("dateOfBirth", dateOfBirth);
        map.put("gender", gender);
        map.put("country", country);
        map.put("profilepic", profilepic);
        map.put("state", state);
        map.put("homeTown", homeTown);
        map.put("phone", phone);
        map.put("telePhone", telePhone);

        databaseReference.push().setValue(map).addOnCompleteListener((task -> {
            Toast.makeText(getContext(), "Upload Complete!!", Toast.LENGTH_SHORT).show();
        }));
    }

    private TextWatcher checkTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String fname = fragmentEnrollBinding.editTextFirstName.getText().toString();
            String lname = fragmentEnrollBinding.editTextLastName.getText().toString();
            String dob = fragmentEnrollBinding.editTextDate.getText().toString();
            String gender = fragmentEnrollBinding.editTextGender.getText().toString();
            String country = fragmentEnrollBinding.editTextCountry.getText().toString();
            String state  = fragmentEnrollBinding.editTextState.getText().toString();
            String town = fragmentEnrollBinding.editTextHomeTown.getText().toString();
            String phone = fragmentEnrollBinding.editTextPhoneNumber.getText().toString();
            String tPhone = fragmentEnrollBinding.editTextTelephoneNumber.getText().toString();

            fragmentEnrollBinding.addUserButton.setEnabled(!fname.isEmpty() && !lname.isEmpty() && !dob.isEmpty() && !gender.isEmpty() && !country.isEmpty() && !state.isEmpty() && !town.isEmpty() &&
                    !phone.isEmpty() && !tPhone.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean isValid(String number)
    {
        Pattern pattern = Pattern.compile("[6-9][0-9]{9}");
        Matcher matcher = pattern.matcher(number);

        if(matcher.find() && matcher.group().equals(number))
            return true;

        return false;
    }

    private boolean isDateFormatCorrect(String date)
    {
        String str[] = date.split("/");

        if(str.length == 3)
            return true;

        return false;
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

    private boolean isDateValid(String date)
    {
        String[] str = date.split("/");

        int day = Integer.parseInt(str[0]);
        int month = Integer.parseInt(str[1]);
        int year = Integer.parseInt(str[2]);

        ArrayList<Integer> month_31 = new ArrayList<>();
        ArrayList<Integer> month_30 = new ArrayList<>();

        month_31.add(1);
        month_31.add(3);
        month_31.add(5);
        month_31.add(7);
        month_31.add(8);
        month_31.add(10);
        month_31.add(12);
        month_30.add(4);
        month_30.add(6);
        month_30.add(9);
        month_30.add(11);

        if(1 <= month && month <= 12)
        {
            if(month_30.contains(month)) {
                if (1 <= day && day <= 30)
                    return true;
            }
            else if(month_31.contains(month)) {
                if (1 <= day && day <= 31)
                    return true;
            }
            else
            {
                if(isLeapYear(year))
                {
                    if(1 <= day && day <= 29)
                        return true;
                    return false;
                }
                else
                {
                    if(1 <= day && day <= 28)
                        return true;
                    return false;
                }
            }

        }

         return false;
    }

    private boolean isLeapYear(int year)
    {
        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}