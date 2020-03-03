package com.example.demo2;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

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
}
