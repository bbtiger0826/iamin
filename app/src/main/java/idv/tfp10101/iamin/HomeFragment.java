package idv.tfp10101.iamin;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import idv.tfp10101.iamin.Data.HomeDataControl;
import idv.tfp10101.iamin.group.Group;
import idv.tfp10101.iamin.group.GroupControl;
import idv.tfp10101.iamin.merch.Merch;
import idv.tfp10101.iamin.merch.MerchControl;


public class HomeFragment extends Fragment {
    private Activity activity;
    private View view;
    private BottomNavigationView bottomNavigationView;
    private ExecutorService executor;
    private RecyclerView recyclerViewGroup;
    private List<Group> localGroups;
    private List<Merch> localMerchs;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 需要開啟多個執行緒取得各景點圖片，使用執行緒池功能
        int numProcs = Runtime.getRuntime().availableProcessors();
        Log.d("TAG", "JVM可用的處理器數量: " + numProcs);
        // 建立固定量的執行緒放入執行緒池內並重複利用它們來執行任務
        executor = Executors.newFixedThreadPool(numProcs);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 取得Activity參考
        activity = getActivity();
        activity.setTitle("首頁");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);

        //bottomNavigationView.getMenu().setGroupCheckable(0,false,false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //bottombar監聽事件
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.food:
                        Toast.makeText(activity, "美食", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.theerc:
                        Toast.makeText(activity, "3C", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.life:
                        Toast.makeText(activity, "生活用品", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.other:
                        Toast.makeText(activity, "其他", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        //呼叫
        HomeDataControl.getAllHomeData(activity);


//        MerchControl.getAllMerchByGroupId(activity,);
//        localGroups = GroupControl.getLocalGroup();
//        if (localGroups == null || localGroups.isEmpty()) {
//            Toast.makeText(activity, R.string.textNoGroupsFound, Toast.LENGTH_SHORT).show();
//        }


    }

    private void findView(View view) {
        bottomNavigationView = view.findViewById(R.id.nv_bar);
        recyclerViewGroup = view.findViewById(R.id.rv_groups);
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(activity));
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

}