package control.lizimumu.www.data;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import control.lizimumu.www.rest.ActionApi;

/**
 * Devices
 * Created by zb on 13/12/2017.
 */

public class DevicesManager {

    private List<String> mDevices;
    private IDeviceChangeListener mListener;
    private ExecutorService mExecutor = Executors.newFixedThreadPool(POOL_SIZE);
    private boolean mIsShutDown;
    private static int POOL_SIZE = 8;
    private static DevicesManager sInstance = new DevicesManager();

    private volatile int mAddressOffset = 255;
    private String mAddressPort = ":9191";
    private String mAddressBase;

    private DevicesManager() {
        mDevices = new ArrayList<>();
    }

    public static DevicesManager getsInstance() {
        return sInstance;
    }

    public void registerListener(IDeviceChangeListener listener) {
        mListener = listener;
    }

    public synchronized void addDevice(String address) {
        if (!mDevices.contains(address)) {
            mDevices.add(address);
            if (mListener != null) mListener.onNewDeviceFound();
        }
    }

    public List<String> getDevices() {
        return mDevices;
    }

    public void clear() {
        mDevices = new ArrayList<>();
    }

    public void startScan(Context context) {
        mAddressOffset = 255;
        mIsShutDown = false;
        final String ip = getIpAddress(context);
        if (ip.length() > 0 && ip.indexOf(".") > 0) {
            mAddressBase = "http://" + ip.substring(0, ip.lastIndexOf("."));
            for (int i = 0; i < POOL_SIZE; i++) {
                scanNext();
            }
        }
    }

    public void stopScan() {
        mIsShutDown = true;
    }

    public synchronized void scanNext() {
        mAddressOffset--;
        if (mAddressOffset <= 1) {
            if (mListener != null) {
                mListener.onScanFinished();
            }
            return;
        }
        if (!mIsShutDown) {
            addTask(new Runnable() {
                @Override
                public void run() {
                    new ActionApi().newScanTask(mAddressBase + "." + mAddressOffset + mAddressPort);
                }
            });
        } else {
            if (mListener != null) {
                mListener.onScanFinished();
            }
        }
    }

    private void addTask(Runnable task) {
        if (!mExecutor.isShutdown() || !mExecutor.isTerminated()) mExecutor.execute(task);
    }

    private String getIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public interface IDeviceChangeListener {
        void onNewDeviceFound();

        void onScanFinished();
    }

}
