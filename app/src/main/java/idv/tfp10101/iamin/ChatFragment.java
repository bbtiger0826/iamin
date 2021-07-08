package idv.tfp10101.iamin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import idv.tfp10101.iamin.member.Member;

public class ChatFragment extends Fragment {
    private static final String TAG = "TAG_ChatFragment";
    private AppCompatActivity activity;
    private RecyclerView rvSellers;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;
    private List<Member> members;
    private Member member;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        members = new ArrayList<>();
        listenToSellers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvSellers = view.findViewById(R.id.rvSellers);
        rvSellers.setLayoutManager(new LinearLayoutManager(activity));
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                if (newText.isEmpty()) {
                    showSellers(members);
                } else {
                    List<Member> searchSellers = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Member member : members) {

                        if (member.getNickname().toUpperCase().contains(newText.toUpperCase())) {
                            searchSellers.add(member);
                        }
                    }
                    showSellers(searchSellers);
                }
                return true;
            }


            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        showAllSellers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除異動監聽器
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }

    private void showAllSellers() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        // .whereEqualTo("phoneNumber", firebaseUser.getPhoneNumber())
        db.collection("Members").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                members.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    members.add(document.toObject(Member.class));
                }
                showSellers(members);
            } else {
                String message = task.getException() == null ?
                        getString(R.string.textNoSellerFound) :
                        task.getException().getMessage();
                Log.e(TAG, "exception message: " + message);
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSellers(List<Member> members) {
        Log.d(TAG, "showSellers() called");
        SellerAdapter sellerAdapter = (SellerAdapter) rvSellers.getAdapter();
        if (sellerAdapter == null) {
            rvSellers.setAdapter(new SellerAdapter(activity, members));
        } else {
            sellerAdapter.setUsers(members);
            sellerAdapter.notifyDataSetChanged();
        }
    }

    private class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.SellerViewHolder> {
        Context context;
        List<Member> members;

        SellerAdapter(Context context, List<Member> memberList) {
            this.context = context;
            this.members = memberList;
        }

        public void setUsers(List<Member> users) {
            this.members = users;
        }

        public class SellerViewHolder extends RecyclerView.ViewHolder{
            CircleImageView ivSeller;
            TextView tvName;

            public SellerViewHolder(@NonNull View itemView) {
                super(itemView);
                ivSeller = itemView.findViewById(R.id.ivSeller);
                tvName = itemView.findViewById(R.id.tvName);
            }
        }


        @Override
        public int getItemCount() {
            return members.size();
        }

        @NonNull
        @Override
        public SellerAdapter.SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.item_view_seller, parent, false);
            return new SellerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SellerAdapter.SellerViewHolder holder, int position) {
            FirebaseUser fUser = auth.getCurrentUser();
            // user means select user
            final Member member = members.get(position);
            if (member.getImage() == null) {
                holder.ivSeller.setImageResource(R.drawable.avatar);
            } else {
//                showImage(holder.ivSeller, member.getImage());
            }
            holder.tvName.setText(member.getNickname());
            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("member", member);


                Navigation.findNavController(v).navigate(R.id.action_chatFragment_to_messageFragment, bundle);
            });
        }
    }

    private void showImage(final CircleImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        String message = task.getException() == null ?
                                getString(R.string.textImageDownloadFail) + ": " + path :
                                task.getException().getMessage() + ": " + path;
                        Log.e(TAG, message);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void listenToSellers() {
        FirebaseUser fUser = auth.getCurrentUser();
        if (registration == null) {
            // .whereEqualTo("phoneNumber", fUser.getPhoneNumber())
            registration = db.collection("Members")
                    .addSnapshotListener((snapshots, e) -> {
                Log.d(TAG, "event happened");
                if (e == null) {
                    List<Member> members = new ArrayList<>();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Member member = dc.getDocument().toObject(Member.class);
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "Added seller: " + member.getEmail());
                                break;
                            case MODIFIED:
                                Log.d(TAG, "MODIFIED seller: " + member.getEmail());
                                break;
                            case REMOVED:
                                Log.d(TAG, "REMOVED seller: " + member.getEmail());
                                break;
                            default:
                                break;
                        }
                    }

                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        members.add(document.toObject(Member.class));
                    }
                    showSellers(members);
                } else {
                    Log.e(TAG, e.getMessage(), e);
                }
            });
        }

    }
}