# 飞机飞行轨迹
## 一、说明
1.  使用的是高德地图SDK,里面提供了点标记的绘制、点平滑移动等功能
2.  大概完成了所有的功能，主要不同点是我这里没有用数字来标号而是直接用城市名称来作为标记点

## 二、过程中遇到的难题
1. 实现前两个功能比较轻松，但轮到第三个功能时，一开始不知道怎么去除已经绘制完的路线，后来查看源码尝试了几个可能的方法，发现clear()方法就是去除标记、线路的方法。后来又看了一下API，最终确认了。
2. 第三个按钮出现之后，没有及时意识到前面两个按钮的监听事件也要做相应的修改

## 三 调试版app下载链接
蓝奏云[https://www.lanzous.com/i9t1h5g](https://www.lanzous.com/i9t1h5g "蓝奏云")
