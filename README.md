# About
对实现悬浮View进行了封装，可以很方便的实现View悬浮在Window上面，有的机子需要有悬浮窗权限

## Gradle
[![](https://jitpack.io/v/zj565061763/windowmanager.svg)](https://jitpack.io/#zj565061763/windowmanager)

## 效果图
![](http://thumbsnap.com/i/qzYljsOJ.gif?0820)

## 使用方法
```java
FFloatView floatView = new FFloatView(context);
floatView.setContentView(btn); //设置要悬浮的view
floatView.addToWindow(true); //true-添加到Window，false-从Window移除
floatView.restoreContentView(); //还原到原xml布局
//更多方法请参考源码...
```
