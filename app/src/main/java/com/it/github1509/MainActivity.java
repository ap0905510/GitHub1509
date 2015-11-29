package com.it.github1509;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;

import static android.widget.LinearLayout.LayoutParams.*;

public class MainActivity extends AppCompatActivity {

    private boolean isFirst = true;
    private TextView tv2;

    private Button btn;
    private TextView locationInfoTextView = null;
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView text1 = (TextView) findViewById(R.id.tv_text1);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_);
        final ImageView iv1 = (ImageView) findViewById(R.id.iv1);
        tv2 = (TextView) findViewById(R.id.text2);
        locationInfoTextView = (TextView) findViewById(R.id.text3);
        btn = (Button) findViewById(R.id.btn);

        final LinearLayout ll = (LinearLayout) findViewById(R.id.fl_contaier);
        final LinearLayout ll2 = (LinearLayout) findViewById(R.id.fl_contaier2);


        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirst) {
                    iv1.setImageBitmap(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_surrounding_arrow_up));
                    ll.setVisibility(View.VISIBLE);
                    isFirst = false;
                } else {
                    iv1.setImageBitmap(BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_surrounding_arrow_down));
                    ll.setVisibility(View.GONE);
                    isFirst = true;
                    ll.setMinimumHeight(0);

                }
            }
        });

        //获取经纬度
        //locate();

        locationClient = new LocationClient(this);
        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        //是否打开GPS
        option.setOpenGps(true);
        //设置返回值的坐标类型
        option.setCoorType("bd0911");
        //设置定位优先级
//        option.setPriority(LocationClientOption.MIN_SCAN_SPAN_NETWORK);  //设置定位优先级
        option.setProdName("LocationDemo"); //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME);    //设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);

        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }
                StringBuffer sb = new StringBuffer(256);
                sb.append("Time : ");
                sb.append(location.getTime());
                sb.append("\nError code : ");
                sb.append(location.getLocType());
                sb.append("\nLatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nLontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nRadius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    sb.append("\nSpeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nSatellite : ");
                    sb.append(location.getSatelliteNumber());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\nAddress : ");
                    sb.append(location.getAddrStr());
                }
                LOCATION_COUTNS++;
                sb.append("\n检查位置更新次数：");
                sb.append(String.valueOf(LOCATION_COUTNS));
                locationInfoTextView.setText(sb.toString());
            }

        });

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (locationClient == null) {
                    return;
                }
                if (locationClient.isStarted()) {
                    btn.setText("Start");
                    locationClient.stop();
                } else {
                    btn.setText("Stop");
                    locationClient.start();
                    /*
                     *当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
                     *调用requestLocation( )后，每隔设定的时间，定位SDK就会进行一次定位。
                     *如果定位SDK根据定位依据发现位置没有发生变化，就不会发起网络请求，
                     *返回上一次定位的结果；如果发现位置改变，就进行网络请求进行定位，得到新的定位结果。
                     *定时定位时，调用一次requestLocation，会定时监听到定位结果。
                     */
                    locationClient.requestLocation();
                }
            }
        });
    }

    private void locate() {
        //获取定位管理对象
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //获取所有的位置的提供者，一般三种
        List<String> names = manager.getAllProviders();

        //查询条件，如果设置了海拔，则定位方式为GPS
        Criteria criteria = new Criteria();
        //是否产生花销，比如流量
        criteria.setCostAllowed(true);
        //获取最好的位置提供者，第二个参数为ture,表示只获取那些被打开的位置提供者
        String provider = manager.getBestProvider(criteria, true);
        //获取位置。第二个参数表示每隔多少时间返回一次数据，第三个参数表示被定位的物体移动每次多少米返回一次次数据
        manager.requestLocationUpdates(provider, 0, 0, new MyLocationListener());

    }

    double latitude;
    double longitude;
    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            System.out.print("服务中位置监听发送了变化");
            float accuracy = location.getAccuracy();//精确度
            double altitude = location.getAltitude();//海拔
            latitude = location.getLatitude();//纬度
            longitude = location.getLongitude();//精度
            //打印我的精准的地理位置 -- 存入SharePreferences -- 给百度地图
            String locationInfo = "jingdu:" + longitude + ",weidu:" + latitude + ",haiba:" + altitude + ",jingquedu:" + accuracy;
            tv2.setText(locationInfo);
//
//            SharedPreferences sp;
//            sp = getPreferences(MODE_PRIVATE);
//            SharedPreferences.Editor edit = sp.edit();
//            edit.putString("location",locationInfo);
//            edit.commit();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }
}
