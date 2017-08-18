# WindowManager
对实现悬浮View进行了封装，可以很方便的实现View悬浮在Window上面，有的机子需要有悬浮窗权限

## 效果图
![](http://thumbsnap.com/i/NWunPUJZ.gif?0818)

## 使用方法
```java
SDFloatHelper floatHelper = new SDFloatHelper(); //创建SDFloatHelper对象
floatHelper.setContentView(findViewById(R.id.btn_float)); //设置要悬浮的view
floatHelper.addToWindow(true); //true-添加到Window，false-从Window移除
floatHelper.restoreContentView(); //还原到原xml布局
//更多方法请参考源码...
```
