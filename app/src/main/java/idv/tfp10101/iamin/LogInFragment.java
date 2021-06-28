package idv.tfp10101.iamin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;
import java.util.Objects;

import idv.tfp10101.iamin.member.Member;

import static idv.tfp10101.iamin.member.MemberControl.memberRemoteAccess;
import static idv.tfp10101.iamin.member.MemberControl.storeMemberIdSharedPreference;

public class LogInFragment extends Fragment {
    private final static String TAG = "TAG_LoginFragment";
    private Activity activity;
    private FirebaseAuth auth;
    private GoogleSignInClient client;
    private CallbackManager callbackManager;
    private Member member;
    private EditText etEmail, etPassword;
    private TextView textView;
    private byte[] image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
        member = new Member();
        fireBaseLogin();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail = view.findViewById(R.id.etLoginEmail);
        etPassword = view.findViewById(R.id.etLoginPassword);

        //快速填寫
        view.findViewById(R.id.idforalign).setOnClickListener(v -> {
            etEmail.setText("mysql99@test.com");
            etPassword.setText("password");
        });

        //一般信箱密碼登入
        view.findViewById(R.id.btLogIn).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            firebaseLogIn(email, password);
            member.setEmail(email);
            member.setPassword(password);
            memberRemoteAccess(activity, member, "login");
        });

        //google登入
        view.findViewById(R.id.btSignInGoogle).setOnClickListener(v -> {
            Intent signInIntent = client.getSignInIntent();
            // 跳出Google登入畫面
            signInGoogleLauncher.launch(signInIntent);
        });

        //fb登入
        view.findViewById(R.id.btSignInFacebook).setOnClickListener(v -> {
            signInFB();
        });

        //註冊
        view.findViewById(R.id.btToSignUp).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_logInFragment_to_signUpFragment);
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        LoginManager.getInstance().logOut();
    }

    private void fireBaseLogin() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // 由google-services.json轉出
                .requestIdToken(getString(R.string.default_web_client_id))
                // 要求輸入email
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(activity, options);
        callbackManager = CallbackManager.Factory.create();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // get the unique ID for the Google account
//        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                //  監聽器
                .addOnCompleteListener(activity, task -> {
                    // 登入成功轉至下頁；失敗則顯示錯誤訊息
                    if (task.isSuccessful()) {

                        Toast.makeText(activity, "Goolge 登入成功", Toast.LENGTH_SHORT).show();

                        //取得google登入的EMAIL firebase的auth.getCurrentUser().getEmail會return null
                        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
                        String personEmail = "";
                        if(signInAccount != null){
                            personEmail = signInAccount.getEmail();
                        }

                        member.setEmail(personEmail);
                        member.setPassword("google_Login");
                        member.setuUId(auth.getCurrentUser().getUid());

                        //用singup 因為要在mysql新增帳號資訊
                        String temp = memberRemoteAccess(activity,member, "signup");
                        storeMemberIdSharedPreference(activity,temp);

                        Navigation.findNavController(getView())
                                .navigate(R.id.action_logInFragment_to_homeFragment);
                    } else {
                        Exception exception = task.getException();
                        String message = exception == null ? "Sign in fail." : exception.getMessage();
                        textView.setText(message);
                    }
                });
    }

    ActivityResultLauncher<Intent> signInGoogleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {

                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        //註冊帳號到fireBase
                        firebaseAuthWithGoogle(account);
                    } else {
                        Log.e(TAG, "GoogleSignInAccount is null");
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.e(TAG, "Google sign in failed");
                }
            }
    );

    private void signInFB() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "signInFirebase: " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    // 登入成功轉至下頁；失敗則顯示錯誤訊息
                    if (task.isSuccessful()) {
                        Navigation.findNavController(getView())
                                .navigate(R.id.action_logInFragment_to_homeFragment);
                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(activity, error + "<-----------facebook login", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, error);
                        TextView debug = getView().findViewById(R.id.debugTv);
                        debug.setText(error);
                    }
                });
    }

    private void firebaseLogIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(activity, "Email/Password can't not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        //firebse login
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Navigation.findNavController(etEmail)
                                .navigate(R.id.action_logInFragment_to_homeFragment);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}