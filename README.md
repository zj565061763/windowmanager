# About
对实现悬浮View进行了封装，可以很方便的实现View悬浮在Window上面，有的机子需要有悬浮窗权限

## Gradle
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
        compile 'com.github.zj565061763:windowmanager:1.0.5'
}

```

## 效果图
![](http://thumbsnap.com/i/qzYljsOJ.gif?0820)

## 使用方法
```java
SDFloatView floatView = new SDFloatView(context);
floatView.setFloatView(btn); //设置要悬浮的view
floatView.addToWindow(true); //true-添加到Window，false-从Window移除
floatView.restoreFloatView(); //还原到原xml布局
//更多方法请参考源码...
```
