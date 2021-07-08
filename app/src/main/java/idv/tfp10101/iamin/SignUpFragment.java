package idv.tfp10101.iamin;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import idv.tfp10101.iamin.member.Member;

import static idv.tfp10101.iamin.member.MemberControl.memberRemoteAccess;

public class SignUpFragment extends Fragment {
    private final static String TAG = "TAG_RegisterFragment";
    private Activity activity;
    private FirebaseAuth auth;
    private EditText etEmail,etPassword,etNickname,etPhoneNumber;
    private Member member;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
        member = new Member();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail = view.findViewById(R.id.etRegisterEmail);
        etPassword = view.findViewById(R.id.etRegisterPassword);
        etNickname = view.findViewById(R.id.etRegisterNickname);
        etPhoneNumber = view.findViewById(R.id.etRegisterPhoneNumber);



        //to RegisterFragment
        view.findViewById(R.id.btSignUp).setOnClickListener(v ->{

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            //確定email 跟 password格式
            if (!validateForm(email,password)){
                return;
            }

            if(!TextUtils.isEmpty(nickname)){
                member.setNickname(nickname);
            }

            if(!TextUtils.isEmpty(phoneNumber)){
                member.setPhoneNumber(phoneNumber);
            }

            member.setEmail(email);
            member.setPassword(password);
            //firebase創帳號
            createAccount(member);

            //移動到首頁
            Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_homeFragment);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void createAccount(Member member) {
        Log.d(TAG, "createAccount:" + member.getEmail());
        auth.createUserWithEmailAndPassword(member.getEmail(), member.getPassword())
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "createUserWithEmail:success");
                        //firebase創帳號
                        createMemberDataInfirebase(member);
                    } else {
                        Log.d(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createMemberDataInfirebase(Member member) {
            db.collection("Members").document(auth.getCurrentUser().getUid()).set(member)
                    .addOnCompleteListener(task -> {
                        Toast.makeText(activity, auth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {
                            String message = "Upload success with ID: " + auth.getCurrentUser().getUid();
                            Log.d(TAG, auth.getCurrentUser().getUid() + "<--------- UUID");

                            //mysql創帳號
                            member.setuUId(auth.getCurrentUser().getUid());
                            Log.d(TAG, member.getuUId());
                            memberRemoteAccess(activity, member, "signup");

                        } else {
                            String message = task.getException() == null ?
                                    "Upload failed" :
                                    task.getException().getMessage();
                            Log.d(TAG, "message: " + message);
                        }
                    });
    }

    private boolean validateForm(String email, String password) {
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(activity, "Email/Password can't not be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }



}