package control.lizimumu.www.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import control.lizimumu.www.R;
import control.lizimumu.www.data.DevicesManager;

public class MainActivity extends AppCompatActivity implements DevicesManager.IDeviceChangeListener {

    private MyRecycleViewAdapter mAdapter;
    private ProgressBar mProgress;
    private Button mConnect;
    private String mAddress;
    private TextView mEmpty;
    private boolean mCanScanAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView mRefresh = findViewById(R.id.refresh);

        final DevicesManager manager = DevicesManager.getsInstance();
        manager.registerListener(this);
        manager.startScan(this);


        RecyclerView list = findViewById(R.id.list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecycleViewAdapter(manager.getDevices());
        list.setAdapter(mAdapter);

        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mAddress) && mAddress.length() > 0) {
                    Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                    intent.putExtra(ControlActivity.CHOSEN_ADDRESS, mAddress);
                    startActivityForResult(intent, 0);
                    manager.stopScan();
                } else {
                    if (mCanScanAgain) {
                        mRefresh.performClick();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.no_device_chose, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress.getVisibility() != View.VISIBLE) {
                    manager.clear();
                    mAdapter.clearData();
                    mAdapter.notifyDataSetChanged();
                    manager.startScan(MainActivity.this);
                    mProgress.setVisibility(View.VISIBLE);
                    mConnect.setAlpha(0.8f);
                    mConnect.setText(R.string.scan_text);
                    mEmpty.setVisibility(View.GONE);
                }
            }
        });

        mProgress = findViewById(R.id.progressBar2);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        mConnect = findViewById(R.id.scan);
        mEmpty = findViewById(R.id.empty);
    }

    @Override
    public void onNewDeviceFound() {
        mAdapter.setData(DevicesManager.getsInstance().getDevices());
        mAdapter.notifyDataSetChanged();
        mCanScanAgain = false;
    }

    @Override
    public void onScanFinished() {
        mProgress.setVisibility(View.INVISIBLE);
        if (DevicesManager.getsInstance().getDevices().size() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
            mConnect.setAlpha(1);
            mConnect.setText(R.string.scan_again);
            mCanScanAgain = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAddress = "";
        mAdapter.notifyDataSetChanged();
    }

    class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder> {

        private List<String> mDataSet;

        MyRecycleViewAdapter(List<String> data) {
            mDataSet = data;
        }

        void setData(List<String> data) {
            mDataSet = data;
        }

        void clearData() {
            mDataSet = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(mDataSet.get(position).split("\\|")[1]);
            holder.mRadio.setChecked(false);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;
            RadioButton mRadio;

            ViewHolder(View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.device_name);
                mRadio = itemView.findViewById(R.id.device_radio);
                mRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mConnect.setAlpha(1f);
                            mConnect.setText(R.string.connect);
                            mAddress = mDataSet.get(getAdapterPosition());
                        } else {
                            mConnect.setAlpha(0.8f);
                            mConnect.setText(R.string.scan_text);
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRadio.performClick();
                    }
                });
            }
        }

    }

}
