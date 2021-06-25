package idv.tfp10101.iamin.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.FutureTask;

import idv.tfp10101.iamin.Constants;


/**
 * Server連線 Action總表
 */
public class RemoteAccess {
    // 根網址
    public static String URL_SERVER = "http://192.168.1.101:8080/iamin_JavaServlet/";
    //  test
    /**
     * (Json)抓取server資料
     * @param url
     * @param requst
     * @return
     */
    public static String getRometeData(String url, String requst) {
        JsonCallable jsonCallable = new JsonCallable(url, requst);
        // callable 轉 Runnable (FutureTask<> -> Runnable的子代)
        FutureTask<String> task = new FutureTask<>(jsonCallable);
        Thread thread = new Thread(task);
        thread.start();
        try {
            return task.get();
        } catch (Exception e) {
            Log.e(Constants.TAG, "getRemoteData(): " + e.toString());
            task.cancel(true);
            return null;
        }
    }

    /**
     * (Images)抓取server資料
     * @param url
     * @param requst
     * @return
     */
//    public static List<byte[]> getRemoteImages(String url, String requst) {
//
//    }

    /**
     * 檢查是否有網路連線
     * API 23 以上：
     * ConnectivityManager -> Network -> NetworkCapabilities
     *
     * API 23 下：
     * ConnectivityManager -> NetworkInfo
     * @param context
     * @return
     */
    public static boolean networkConnected(Context context) {
        // Connectivity(連接性)
        ConnectivityManager CM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (CM != null) {
            // VERSION > API 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = CM.getActiveNetwork(); /** 需要權限 */
                // NetworkCapabilities(網絡能力)
                NetworkCapabilities networkCapabilities = CM.getNetworkCapabilities(network);
                // (Wifi 行動網路 有線網路)
                if (networkCapabilities != null) {
                    boolean wifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    boolean cellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    boolean ethernet = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                    Log.d(Constants.TAG, "TRANSPORT_WIFI: " + String.valueOf(wifi));
                    Log.d(Constants.TAG, "TRANSPORT_CELLULAR: " + String.valueOf(cellular));
                    Log.d(Constants.TAG, "TRANSPORT_ETHERNET: " + String.valueOf(ethernet));
                    return wifi || cellular || ethernet;
                }
            }else {
                NetworkInfo networkInfo = CM.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        Log.e(Constants.TAG, "ConnectivityManager Is Null");
        return false;
    }
}
