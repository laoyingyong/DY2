package com.example.demo2;


import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private MapView mapView;

    private AMap aMap;

    private LatLng firstPosition;

    private Button mStartButton;
    private Button resetBtn;//重置按钮
    private Button resetLineBtn;//重置航线
    private TextView tv;
    private AlertDialog alertDialog;//重置导航线的对话框
    private AlertDialog.Builder builder;
    private SmoothMoveMarker moveMarker;

    private List<LatLng> list=null;//地点的集合


    private boolean [] array;


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
        resetLineBtn=findViewById(R.id.resetLineBtn);
        mStartButton.setOnClickListener(this);
        resetBtn.setOnClickListener(this);//给重置按钮设置监听器
        resetLineBtn.setOnClickListener(this);//给重置航线按钮设置监听器


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
        final List<LatLng> points = readLatLngs();
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < points.size(); i++)
        {
            b.include(points.get(i));
            aMap.addMarker(new MarkerOptions().position(points.get(i)).title("北京").snippet("DefaultMarker"));
        }
        LatLngBounds bounds = b.build();
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        moveMarker = new SmoothMoveMarker(aMap);

        moveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.plane2));// 设置图标为无人机图标



        moveMarker.setPoints(points);//设置平滑移动的轨迹list
        moveMarker.setTotalDuration(10);//设置平滑移动的总时间

        listener();
    }//初始化move end


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


    private List<LatLng> readLatLngs()
    {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < coords.length; i += 2)
        {
            points.add(new LatLng(coords[i + 1], coords[i]));//索引为奇数的是纬度，偶数的是经度
        }
        return points;
    }



           //北京、天津、石家庄、济南、郑州
           private double[] coords = {116.3499049793749, 39.97617053371078,
            117.1993700000, 39.0851000000,
            114.5143000000, 38.0427600000,
            117.1334838900, 36.6331621000,
            113.6249300000, 34.7472500000};

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
                    resetLineBtn.setEnabled(false);//重置航线按钮不可用
                    resetLineBtn.setBackgroundResource(R.drawable.background);//重置航线按钮设为灰色

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
                    //List<LatLng> points = readLatLngs();
                    if(list==null)
                    {
                        list=readLatLngs();
                    }
                    moveMarker.setPoints(list);
                    moveMarker.getMarker().showInfoWindow();
                    moveMarker.startSmoothMove();

                    mMarkerStatus = MOVE_STATUS;
                    mStartButton.setText("飞行中");

                    resetLineBtn.setEnabled(false);//重置航线按钮设为不可用
                    resetLineBtn.setBackgroundResource(R.drawable.background);//重置航线按钮设为灰色

                }

                break;

            case R.id.resetBtn://重置按钮
                mStartButton.setEnabled(true);
                mStartButton.setBackgroundResource(R.drawable.boder);//改变背景

                if(list!=null&&list.size()!=0)
                {
                    firstPosition=list.get(0);
                    moveMarker.setPosition(firstPosition);//将飞机放回开始的位置
                }
                else
                {
                   List<LatLng> l=readLatLngs();
                   firstPosition=l.get(0);
                    moveMarker.setPosition(firstPosition);//将飞机放回开始的位置
                }

                moveMarker.stopMove();//停止运动
                mMarkerStatus = FINISH_STATUS;//将状态设为结束状态
                mStartButton.setText("开始");
                double longitude = firstPosition.longitude;
                double latitude = firstPosition.latitude;
                DecimalFormat format=new DecimalFormat("#.0000");//保留4位小数
                String format1 = format.format(latitude);
                String format2 = format.format(longitude);

                tv.setText("经度:"+format2+"纬度:"+format1+"海拔:0m");

                resetLineBtn.setEnabled(true);//重置航线按钮设为可用
                resetLineBtn.setBackgroundResource(R.drawable.boder);//重置航线按钮设为白色

                break;

            case R.id.resetLineBtn:
                alertDialog=null;
                final String[] menu = new String[]{"北京", "天津", "石家庄", "济南","郑州"};
                //定义一个用来记录个列表项状态的boolean数组
                array = new boolean[]{false, false, false, false,false};

                final int [] arr=new int[5];//记录选择顺序的数组
                builder=new AlertDialog.Builder(MainActivity.this);
                alertDialog=builder.setTitle("重置航线").
                        setMultiChoiceItems(menu, array, new DialogInterface.OnMultiChoiceClickListener()
                        {
                            int count=0;
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked)
                            {

                                count++;//每点击一次计数器就加1
                                array[which] = isChecked;
                                arr[which]=count;//把计数器的值赋给指定的元素

                            }
                        }).

                        setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) //确认按钮的点击事件
                    {
                        String result = "";
                        for (int i = 0; i < array.length; i++)
                        {
                            if (array[i])
                            {
                                if(i==array.length-1)//如果是最后一个元素就不加空格
                                {
                                    result += menu[i];
                                }
                                else
                                {
                                    result += menu[i] + " ";
                                }

                            }

                        }

                        String[] split=null;

                        try {
                            if(result.contains(" "))
                            {
                                split = result.split(" ");
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                        if(split!=null&&split.length==5)//如果五个选项全部选择了,就进行排序操作
                        {
                            int num=arr[0];
                            split[num-1]="北京";
                            int num2=arr[1];
                            split[num2-1]="天津";
                            int num3=arr[2];
                            split[num3-1]="石家庄";
                            int num4=arr[3];
                            split[num4-1]="济南";
                            int num5=arr[4];
                            split[num5-1]="郑州";
                        }


                        list = new ArrayList<LatLng>();
                        for (int i = 0; split!=null&&i < split.length; i++)
                        {
                            String s=split[i];

                            if(s.equals("天津"))
                            {
                                list.add(new LatLng(39.0851000000,117.1993700000));
                            }
                            if(s.equals("北京"))
                            {
                                list.add(new LatLng(39.97617053371078,116.3499049793749));

                            }
                            if(s.equals("石家庄"))
                            {
                                list.add(new LatLng(38.0427600000,114.5143000000));
                            }
                            if(s.equals("济南"))
                            {
                                list.add(new LatLng( 36.6331621000,117.1334838900));
                            }
                            if(s.equals("郑州"))
                            {
                                list.add(new LatLng( 34.7472500000, 113.6249300000));
                            }
                        }



                        aMap.clear();//从地图上删除所有的overlay（marker，circle，polyline 等对象）
                        aMap.removecache();//删除地图缓存

                        for (LatLng latLng : list)
                        {
                            aMap.addMarker(new MarkerOptions().position(latLng).title("北京").snippet("DefaultMarker"));
                        }
                        //在地图上添加一个折线对象（polyline）对象
                        aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.blueiconfour)) //setCustomTextureList(bitmapDescriptors)
                                .addAll(list)
                                .useGradient(true)
                                .width(18));


                        LatLngBounds.Builder b = LatLngBounds.builder();
                        for (int i = 0; i < list.size(); i++)
                        {
                            b.include(list.get(i));
                        }
                        LatLngBounds bounds = b.build();
                        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                        moveMarker=new SmoothMoveMarker(aMap);
                        moveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.plane2));// 设置图标为无人机图标
                        moveMarker.setPoints(list);//设置平滑移动的轨迹list
                        moveMarker.setTotalDuration(10);//设置平滑移动的总时间
                        listener();
                        if(list.size()!=5)
                        {
                            Toast.makeText(getApplicationContext(), "重置失败，因为选项没选完！", Toast.LENGTH_LONG).show();
                        }


                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) //取消按钮的点击事件
                    {

                    }
                }).create();
                alertDialog.show();
                break;

                default:
        }


    }





    private void listener()
    {
        aMap.setInfoWindowAdapter(infoWindowAdapter);
        moveMarker.setMoveListener(//给飞机设置移动事件监听
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

                                    resetLineBtn.setEnabled(true);//重置航线按钮设为可用
                                    resetLineBtn.setBackgroundResource(R.drawable.boder);//重置航线按钮设为白色


                                }
                            }
                        });

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                LatLng position = moveMarker.getPosition();
                                double latitude = position.latitude;//纬度
                                double longitude = position.longitude;//经度
                                DecimalFormat format=new DecimalFormat("#.0000");//保留4位小数
                                String format1 = format.format(latitude);
                                String format2 = format.format(longitude);

                                //海拔用随机数来模拟
                                tv.setText("经度:"+format2+"纬度:"+format1+"海拔:"+format.format(Math.random()*6000+5000)+"m");

                            }
                        });


                    }//move end~


                });
        moveMarker.getMarker().showInfoWindow();
    }



}
