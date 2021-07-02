package idv.tfp10101.iamin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import idv.tfp10101.iamin.merch.Merch;
import idv.tfp10101.iamin.merch.MerchControl;


public class MerchbrowseFragment extends Fragment {
    private Activity activity;
    private View view;
    private int id;
    private List<Merch> localMerchs;
    private RecyclerView recyclerViewMerch;
    private Button but_buy;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_merchbrowse, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(view);

        Bundle bundle = getArguments();
        id = bundle.getInt("GroupID");
        MerchControl.getAllMerchByGroupId(activity,id);
        localMerchs = MerchControl.getLocalMerchs();
        if (localMerchs == null || localMerchs.isEmpty()) {
            Toast.makeText(activity, R.string.textNoGroupsFound, Toast.LENGTH_SHORT).show();
        }
        showMerchs(localMerchs);
    }

    private void findView(View view) {
        recyclerViewMerch = view.findViewById(R.id.recyclerViewMerch);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        recyclerViewMerch.setLayoutManager(linearLayoutManager);

        but_buy = view.findViewById(R.id.but_buy);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.commodity_choose_bottomsheet);
        but_buy.setOnClickListener(v ->{
            bottomSheetDialog.show();
        });
    }

    private void showMerchs(List<Merch> localMerchs) {
        /** RecyclerView */
        // 檢查
        MerchbrowseFragment.MerchAdapter merchAdapter = (MerchbrowseFragment.MerchAdapter) recyclerViewMerch.getAdapter();
        if (merchAdapter == null) {
            recyclerViewMerch.setAdapter(new MerchbrowseFragment.MerchAdapter(activity, localMerchs));
            int px = (int) Constants.convertDpToPixel(8, activity); // 間距 8 dp
            recyclerViewMerch.addItemDecoration(new Constants.SpacesItemDecoration("bottom", px));
        }else{
            // 資訊重新載入刷新
            merchAdapter.setMerchs(localMerchs);
            merchAdapter.notifyDataSetChanged();
        }
    }

    private class MerchAdapter extends RecyclerView.Adapter<MerchAdapter.MyMerchViewHolder>{
        private List<Merch> rsMerchs;
        private LayoutInflater layoutInflater;

        public MerchAdapter(Context context, List<Merch> merchs){
            layoutInflater = LayoutInflater.from(context);
            rsMerchs = merchs;
        }

        public class MyMerchViewHolder extends RecyclerView.ViewHolder{
            TextView txv_merch_name,txv_merch_price,txv_commodity_description;
            EditText edt_amount;
            RecyclerView rvMerchimage;
            Button btn_sub,btn_add;
            public MyMerchViewHolder(@NonNull View itemView) {
                super(itemView);
                txv_merch_name = itemView.findViewById(R.id.txv_merch_name);
                txv_merch_price = itemView.findViewById(R.id.txv_merch_price);
                txv_commodity_description = itemView.findViewById(R.id.txv_commodity_description);
                edt_amount = itemView.findViewById(R.id.edt_amount);
                btn_sub = itemView.findViewById(R.id.btn_sub);
                btn_add = itemView.findViewById(R.id.btn_add);

            }
        }

        public void setMerchs(List<Merch> Merchs) {
            rsMerchs = Merchs;
        }

        @NonNull
        @Override
        public MyMerchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_merch_buyer,parent,false);
            return new MerchbrowseFragment.MerchAdapter.MyMerchViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MerchbrowseFragment.MerchAdapter.MyMerchViewHolder holder, int position) {
        final Merch rsMerch = rsMerchs.get(position);
        AtomicInteger amount = new AtomicInteger(0);
        int merch_id = rsMerch.getMerchId();
        String merch_name = rsMerch.getName();
        int merch_price = rsMerch.getPrice();
        String merch_desc = rsMerch.getMerchDesc();

        holder.txv_merch_name.setText(merch_name);
        holder.txv_merch_price.setText(String.valueOf(merch_price));
        holder.txv_commodity_description.setText("商品說明:\n"+merch_desc);

        holder.btn_sub.setOnClickListener(v ->{
            if (amount.get() > 0) {
                amount.getAndDecrement();
                holder.edt_amount.setText(String.valueOf(amount.get()));
            }else{
                holder.edt_amount.setText(String.valueOf(0));
            }
        });
        holder.btn_add.setOnClickListener(v ->{
            amount.getAndIncrement();
            holder.edt_amount.setText(String.valueOf(amount.get()));
        });

        }
        //設定回傳數量
        @Override
        public int getItemCount() { return rsMerchs == null ? 0 : rsMerchs.size();
        }
    }

    private  void showBottomDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.commodity_choose_bottomsheet,null);

    }
}