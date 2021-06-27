package idv.tfp10101.iamin.member;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import idv.tfp10101.iamin.network.RemoteAccess;

import static android.content.Context.MODE_PRIVATE;

public class MemberControl {
    private static String TAG = "TAG_MemberControl";

    public static void memberRemoteAccess(Context context, Member member, String value) {
        Log.d(TAG,member.getEmail());
        if (RemoteAccess.networkConnected(context)) {
            String url = RemoteAccess.URL_SERVER + "memberServelt";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", value);
            jsonObject.addProperty("member", new Gson().toJson(member));
            //TODO logIn
//            // 有圖才上傳
//            if (image != null) {
//                //byte to string
//                jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
//            }
            String result = RemoteAccess.getRometeData(url, jsonObject.toString());
            memberIdSharedPreference(context, result);
        } else {
            Toast.makeText(context, "No network", Toast.LENGTH_SHORT).show();
        }
    }

    //存mebmer_ID到app裡
    public static void memberIdSharedPreference(Context context, String result) {
        int mySqlMemberId;
        try {
            mySqlMemberId = Integer.parseInt(result);
            SharedPreferences pref = context.getSharedPreferences("member_ID",
                    MODE_PRIVATE);
            pref.edit()
                    .putInt("member_ID", mySqlMemberId)
                    .apply();
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            mySqlMemberId = 0;
        }
        Log.d(TAG, mySqlMemberId + "");
    }

    public static void memberLog(Context context, int mySqlMemberId, String value) {
        if (mySqlMemberId == 0) {
            Toast.makeText(context, " failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, " success", Toast.LENGTH_SHORT).show();
        }
    }
}
