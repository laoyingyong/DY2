package com.example.demo2;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Util
{
    /**
     * 给定指定的坐标点，计算总距离
     * @param list
     * @return
     */
    public static float getTotalDistance(List<LatLng> list)
    {
        float sum=0.0f;
        for (int i = 0; i < list.size()-1; i++)
        {
            LatLng latLng1 = list.get(i);
            LatLng latLng2 = list.get(i + 1);
            float d = AMapUtils.calculateLineDistance(latLng1, latLng2);//计算两点之间的距离
            sum+=d;
        }
        return sum;
    }

    /**
     * 获取当前位置距离终点的距离
     * @param totalDis 总距离
     * @param toalTime 规定的运动的总时间
     * @param pastTime 飞机飞了多长时间
     * @return
     */
    public static float getLeftDistance(float totalDis,float toalTime,float pastTime)
    {
        float speed=totalDis/toalTime;//飞机飞行的速度
        float pastDis=speed*pastTime;//飞机已经飞了多长的距离
        return  totalDis-pastDis;
    }

    /**
     * 获取飞机当前的经纬度信息
     * @param list 飞行经过的经纬度数组
     * @param totalDis 路程的总距离
     * @param toalTime 飞完整个路程所需要的时间
     * @param pastTime 已经飞行了多长时间
     * @return
     */
    public static ArrayList<String> getCurPos(List<LatLng> list,float totalDis,float toalTime,float pastTime)
    {
        float speed=totalDis/toalTime;//飞机飞行的速度
        float pastDis=speed*pastTime;//飞机已经飞了多长的距离
        ArrayList<Float> disLi=new ArrayList<>();
        float sum=0.0f;
        disLi.add(sum);
        for (int i = 0; i < list.size()-1; i++)
        {
            LatLng latLng1 = list.get(i);
            LatLng latLng2 = list.get(i + 1);
            float d = AMapUtils.calculateLineDistance(latLng1, latLng2);//计算两点之间的距离
            sum+=d;
            disLi.add(sum);
        }

        int where=0;
        for (int i = 0; i < disLi.size()-1; i++)
        {
            Float aFloat1 = disLi.get(i);
            Float aFloat2 = disLi.get(i + 1);
            if(pastDis>=aFloat1&&pastDis<=aFloat2)
            {
                where=i;
            }

        }
        LatLng latLng1 = list.get(where);
        LatLng latLng2 = list.get(where + 1);

        float v = AMapUtils.calculateLineDistance(latLng1, latLng2);//计算两点之间的距离
        float a=disLi.get(where)/speed;//前面所花费的时间
        float smallTime=pastTime-a;

        float s=smallTime*speed;//斜边

        float bilv=s/v;


        double latitude1 = latLng1.latitude;
        double longitude1 = latLng1.longitude;


        double latitude2 = latLng2.latitude;
        double longitude2 = latLng2.longitude;


        double jingdu=0.0;
        if(longitude2>longitude1)//往右边飞
        {
            jingdu=Math.abs(longitude1-longitude2)*bilv+longitude1;
        }
        else //往左边飞
        {
            jingdu=longitude1-Math.abs(longitude1-longitude2)*bilv;
        }

        double weidu=0.0;
        if(latitude2>latitude1)//往上边飞
        {
            weidu=latitude1+Math.abs(latitude1-latitude2)*bilv;
        }
        else //往下边飞
        {
            weidu=latitude1-Math.abs(latitude1-latitude2)*bilv;
        }


        DecimalFormat format=new DecimalFormat("#.0000");//保留4位小数
        String format1 = format.format(jingdu);
        String format2 = format.format(weidu);

        ArrayList<String> posL=new ArrayList<>();
        posL.add(format1);
        posL.add(format2);
        return  posL;

    }


    public static ArrayList<String> getPosStr(LatLng latLng)
    {
        DecimalFormat format=new DecimalFormat("#.0000");//保留4位小数
        double longitude = latLng.longitude;
        double latitude = latLng.latitude;
        String format1 = format.format(longitude);
        String format2 = format.format(latitude);
        ArrayList<String> li=new ArrayList<>();
        li.add(format1);
        li.add((format2));
        return  li;

    }
}
