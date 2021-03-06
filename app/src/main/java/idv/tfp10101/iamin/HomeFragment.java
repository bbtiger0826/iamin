package idv.tfp10101.iamin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.protobuf.Empty;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import idv.tfp10101.iamin.Data.HomeData;
import idv.tfp10101.iamin.Data.HomeDataControl;
import idv.tfp10101.iamin.group.Group;
import idv.tfp10101.iamin.group.GroupControl;
import idv.tfp10101.iamin.merch.Merch;
import idv.tfp10101.iamin.merch.MerchControl;

import static android.media.CamcorderProfile.get;


public class HomeFragment extends Fragment {
    private Activity activity;
    private View view;
    private BottomNavigationView bottomNavigationView;
    private ExecutorService executor;
    private RecyclerView recyclerViewGroup;
    private List<HomeData> localHomeDatas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
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

        //呼叫
        HomeDataControl.getAllHomeData(activity);
        localHomeDatas = HomeDataControl.getLocalHomeDatas();
        if (localHomeDatas == null || localHomeDatas.isEmpty()) {
            Toast.makeText(activity, R.string.textNoGroupsFound, Toast.LENGTH_SHORT).show();
        }

        showHomeData(localHomeDatas);
        //輸入監聽
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<HomeData> searchHomdData = new ArrayList<>();
                if (newText.equals("")){
                    showHomeData(localHomeDatas);
                }else {
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (HomeData homeData : localHomeDatas) {
                        if (homeData.getName().toUpperCase().contains(newText.toUpperCase())) {
                            searchHomdData.add(homeData);
                        }
                    }
                    showHomeData(searchHomdData);
                }
                return true;
            }
        });

        //bottomNavigationView.getMenu().setGroupCheckable(0,false,false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //bottombar監聽事件
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.no:
                        searchView.setQuery("",false);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                List<HomeData> searchHomdData = new ArrayList<>();
                                if (newText.equals("")){
                                    showHomeData(localHomeDatas);
                                }else {
                                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                                    for (HomeData homeData : localHomeDatas) {
                                        if (homeData.getName().toUpperCase().contains(newText.toUpperCase())) {
                                            searchHomdData.add(homeData);
                                        }
                                    }
                                    showHomeData(searchHomdData);
                                }
                                return true;
                            }
                        });
                        swipeRefreshLayout.setOnRefreshListener(() -> {
                            //開啟動畫
                            swipeRefreshLayout.setRefreshing(true);
                            showHomeData(localHomeDatas);
                            swipeRefreshLayout.setRefreshing(false);
                        });
                        Toast.makeText(activity, "未分類", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.food:
                         updata(1,localHomeDatas);
                        Toast.makeText(activity, "美食", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.life:
                        updata(2,localHomeDatas);
                        Toast.makeText(activity, "生活用品", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.theerc:
                        updata(3,localHomeDatas);
                        Toast.makeText(activity, "3C", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.other:
                        updata(4,localHomeDatas);
                        Toast.makeText(activity, "其他", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }
    //根據所選的分類下拉更新
    private void updata(int category_Id,List<HomeData> categoryHomeData){
        searchView.setQuery("",false);
        List<HomeData> selectHomeData = new ArrayList<>();
        for (HomeData category : categoryHomeData){
            if (category.getGroup_category_Id() == category_Id){
                selectHomeData.add(category);
            }
        }
        List<HomeData> searchHomdData = new ArrayList<>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    showHomeData(selectHomeData);
                }else {
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (HomeData homeData : selectHomeData) {
                        if (homeData.getName().toUpperCase().contains(newText.toUpperCase())) {
                            searchHomdData.add(homeData);
                        }
                    }
                    showHomeData(searchHomdData);
                }
                return true;
            }
        });
        showHomeData(selectHomeData);

        swipeRefreshLayout.setOnRefreshListener(() ->{
            swipeRefreshLayout.setRefreshing(true);
            searchView.setQuery("",false);
            showHomeData(selectHomeData);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void showHomeData(List<HomeData> localHomeDatas) {
        /** RecyclerView */
        // 檢查
        HomeFragment.HomeAdapter groupAdapter = (HomeFragment.HomeAdapter) recyclerViewGroup.getAdapter();
        if (groupAdapter == null) {
            recyclerViewGroup.setAdapter(new HomeFragment.HomeAdapter(activity, localHomeDatas));
            int px = (int) Constants.convertDpToPixel(8, activity); // 間距 8 dp
            recyclerViewGroup.addItemDecoration(new Constants.SpacesItemDecoration("bottom", px));
        }else{
            // 資訊重新載入刷新
            groupAdapter.setHomeDatas(localHomeDatas);
            groupAdapter.notifyDataSetChanged();
        }
    }

    private void findView(View view) {
        bottomNavigationView = view.findViewById(R.id.nv_bar);
        recyclerViewGroup = view.findViewById(R.id.rv_groups);
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(activity));
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        searchView = view.findViewById(R.id.searchview);
    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyHomeDataViewHolder>{
        private List<HomeData> rsHomeDatas;
        private LayoutInflater layoutInflater;

        public HomeAdapter(Context context, List<HomeData> homedatas){
            layoutInflater = LayoutInflater.from(context);
            rsHomeDatas = homedatas;
        }

        public class MyHomeDataViewHolder extends RecyclerView.ViewHolder{
            TextView txv_group_name,txv_group_price,txv_group_conditionTime,txv_progress;
            ImageView imv_group;
            ProgressBar pr_bar;
            public MyHomeDataViewHolder(@NonNull View itemView) {
                super(itemView);
                txv_group_name = itemView.findViewById(R.id.txv_group_name);
                txv_group_price = itemView.findViewById(R.id.txv_group_price);
                txv_group_conditionTime = itemView.findViewById(R.id.txv_group_conditionTime);
                txv_progress = itemView.findViewById(R.id.txv_progress);
                pr_bar = itemView.findViewById(R.id.pr_bar);
            }
        }
        public void setHomeDatas(List<HomeData> HomeDatas) {
            rsHomeDatas = HomeDatas;
        }

        @NonNull
        @Override
        public MyHomeDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_group_buyer, parent, false);
            return new HomeFragment.HomeAdapter.MyHomeDataViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeFragment.HomeAdapter.MyHomeDataViewHolder holder, int position) {
        final HomeData rsHomeData = rsHomeDatas.get(position);
        int GroupID = rsHomeData.getGroupId();
        holder.txv_group_name.setText(rsHomeData.getName());
        Timestamp ts = rsHomeData.getConditionTime();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        holder.txv_group_conditionTime.setText("結單日期:"+"\n"+sdf.format(ts));
        holder.pr_bar.setMax(rsHomeData.getGoal());
        holder.pr_bar.setProgress(rsHomeData.getProgress());
        holder.txv_progress.setText("("+String.valueOf(rsHomeData.getProgress())+"/"+String.valueOf(rsHomeData.getGoal())+")");
            //發送商品價格請求
            HomeDataControl.getAllGroupPrice(activity,GroupID);
            List<HomeData> prices = HomeDataControl.getLocalHomeDatas();
            List<Integer> price = prices.get(0).getPrice();
            int min = price.get(0);
            int max = price.get(price.size()-1);
            holder.txv_group_price.setText("價格:"+String.valueOf(min)+"~"+String.valueOf(max));

            //設定點擊商品觸發
            holder.itemView.setOnClickListener(v ->{
                // Toast.makeText(activity, String.valueOf(id), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putInt("GroupID",GroupID);
//                Navigation.findNavController(v).navigate(R.id.merchbrowseFragment,bundle);
            });
        }

        @Override
        public int getItemCount() {
            return rsHomeDatas == null ? 0 : rsHomeDatas.size();
        }
    }

}