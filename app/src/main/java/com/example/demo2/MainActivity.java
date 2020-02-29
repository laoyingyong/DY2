package com.example.demo2;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mapView;

    private AMap aMap;

    private Button mStartButton;
    private Button resetBtn;//重置按钮
    private TextView tv;
    private SmoothMoveMarker moveMarker;


    private static final int START_STATUS = 0;

    private static final int MOVE_STATUS = 1;

    private static final int PAUSE_STATUS = 2;
    private static final int FINISH_STATUS = 3;

    private int mMarkerStatus = START_STATUS;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map);
        mStartButton =findViewById(R.id.startBtn);
        tv=findViewById(R.id.tv);//显示经纬度信息的文本框
        resetBtn=findViewById(R.id.resetBtn);//重置按钮实例化
        mStartButton.setOnClickListener(this);
        resetBtn.setOnClickListener(this);//给重置按钮设置监听器

        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initMoveMarker();

    }


    private void init() //初始化AMap对象
    {
        if (aMap == null)
        {
            aMap = mapView.getMap();
        }

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();

    }


    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }


    private void initMoveMarker()
    {
        addPolylineInPlayGround();
        // 获取轨迹坐标点
        List<LatLng> points = readLatLngs();
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < points.size(); i++)
        {
            b.include(points.get(i));
        }
        LatLngBounds bounds = b.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        moveMarker = new SmoothMoveMarker(aMap);
        // 设置滑动的图标
        moveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.plane2));

        /*
        //当移动Marker的当前位置不在轨迹起点，先从当前位置移动到轨迹上，再开始平滑移动
        // LatLng drivePoint = points.get(0);//设置小车当前位置，可以是任意点，这里直接设置为轨迹起点
        LatLng drivePoint = new LatLng(39.980521,116.351905);//设置小车当前位置，可以是任意点
        Pair<Integer, LatLng> pair = PointsUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());
        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);*/

        moveMarker.setPoints(points);//设置平滑移动的轨迹list
        moveMarker.setTotalDuration(10);//设置平滑移动的总时间

        aMap.setInfoWindowAdapter(infoWindowAdapter);
        moveMarker.setMoveListener(//移动事件监听
                new SmoothMoveMarker.MoveListener()
                {
                    @Override
                    public void move(final double distance)
                    {

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (infoWindowLayout != null && title != null && moveMarker.getMarker().isInfoWindowShown())
                                {
                                    title.setText("还有" + (int) distance + "米到达终点");
                                }
                                if (distance == 0)
                                {
                                    moveMarker.getMarker().hideInfoWindow();
                                    mMarkerStatus = FINISH_STATUS;
                                    mStartButton.setText("开始");
                                    mStartButton.setEnabled(true);//启用按钮
                                    mStartButton.setBackgroundResource(R.drawable.boder);//改变背景


                                }
                            }
                        });
                    }
                });
        moveMarker.getMarker().showInfoWindow();
    }


    public void move(View view)
    {

        moveMarker.startSmoothMove();
    }

    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter()
    {
        @Override
        public View getInfoWindow(Marker marker)
        {

            return getInfoWindowView(marker);
        }

        @Override
        public View getInfoContents(Marker marker)
        {


            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    private View getInfoWindowView(Marker marker)
    {
        if (infoWindowLayout == null)
        {
            infoWindowLayout = new LinearLayout(MainActivity.this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(MainActivity.this);
            snippet = new TextView(MainActivity.this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            //infoWindowLayout.setBackgroundResource(R.drawable.blueiconfour);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    private void addPolylineInPlayGround()
    {
        List<LatLng> list = readLatLngs();
        List<Integer> colorList = new ArrayList<Integer>();

        aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.blueiconfour)) //setCustomTextureList(bitmapDescriptors)
                .addAll(list)
                .useGradient(true)
                .width(18));
    }

    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        return points;
    }

    private double[] coords = {116.3499049793749, 39.97617053371078,
            114.34978804908442, 38.97619854213431,
            113.349674596623, 37.97623045687959,
            115.34955525200917, 36.97626931100656,
            112.34943728748914, 35.976285626595036



    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.startBtn:

                if (mMarkerStatus == START_STATUS)
                {
                    mStartButton.setEnabled(false);//禁用按钮
                    mStartButton.setBackgroundResource(R.drawable.background);//改变背景
                    moveMarker.startSmoothMove();
                    mMarkerStatus = MOVE_STATUS;
                    mStartButton.setText("飞行中");

                } else if (mMarkerStatus == MOVE_STATUS)
                {
                    moveMarker.stopMove();
                    mMarkerStatus = PAUSE_STATUS;
                    mStartButton.setText("继续");
                } else if (mMarkerStatus == PAUSE_STATUS)
                {
                    moveMarker.startSmoothMove();
                    mMarkerStatus = MOVE_STATUS;
                    mStartButton.setText("飞行中");
                } else if (mMarkerStatus == FINISH_STATUS)//到达终点
                {

                    mStartButton.setEnabled(false);//禁用按钮
                    mStartButton.setBackgroundResource(R.drawable.background);//改变背景
                    moveMarker.setPosition(new LatLng(39.97617053371078, 116.3499049793749));//开始的位置
                    List<LatLng> points = readLatLngs();
                    moveMarker.setPoints(points);
                    moveMarker.getMarker().showInfoWindow();
                    moveMarker.startSmoothMove();

                    mMarkerStatus = MOVE_STATUS;
                    mStartButton.setText("飞行中");

                }

                break;

            case R.id.resetBtn://重置按钮
                mStartButton.setEnabled(true);
                mStartButton.setBackgroundResource(R.drawable.boder);//改变背景
                moveMarker.setPosition(new LatLng(39.97617053371078, 116.3499049793749));//将飞机放回开始的位置
                moveMarker.stopMove();//停止运动
                mMarkerStatus = FINISH_STATUS;//将状态设为结束状态
                mStartButton.setText("开始");

                break;

                default:
        }





    }

}
