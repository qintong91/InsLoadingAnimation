[English doc](https://github.com/qintong91/InsLoadingAnimation/blob/master/README.md)<br/>
# InsLoadingAnimation
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/qintong000/maven/insLoadingAnimation/images/download.svg)](https://bintray.com/qintong000/maven/insLoadingAnimation/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-InsLoadingAnimation-red.svg?style=flat)](https://android-arsenal.com/details/1/5789)

## 简介
InsLoadingAnimation 是仿instagram和微博的头像点击进行加载的Android动画。更多信息以及代码分析请见相关的博文：http://www.jianshu.com/p/a0e2dbeef88a

## Demo
![avi](screenshots/demo.gif)

## 使用

### Step 1

在build.gradle增加依赖：

```
dependencies {
  compile 'com.qintong:insLoadingAnimation:1.1.0'
}
```

### Step 2

InsLoadingView继承自ImageView, 所以最基本的，可以按照ImageView的用法使用InsLoadingView：

```xml
<com.qintong.library.InsLoadingView
    android:layout_centerInParent="true"
    android:id="@+id/loading_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@mipmap/pink"/>
```

### Step 3

#### 设置状态：
您可以手动设置其状态，来对应在您应用中的当前状态。InsLoadingView的状态有：
LOADING: 表示InsLoadingView被点击之后正在加载内容(未加载完毕之前)，该状态下动画正在执行。
UNCLICKED: 该InsLoadingView被点击之前的状态，此状态下动画停止。
CLICKED: 表示InsLoadingView被点击和加载过，此状态下动画停止切圆圈的颜色为灰色。
默认的状态是LOADING。

可以通过一下代码设置状态：
xml:
```xml
  app:status="loading" //or "clicked",or "clicked"
```

java:
```java
  mInsLoadingView.setStatus(InsLoadingView.Status.LOADING); //Or InsLoadingView.Status.CLICKED, InsLoadingView.Status.UNCLICKED
```

#### 设置颜色
设置start color和start color，InsLoadingView的圆圈会显示两个颜色间的过渡。
可以按如下代码设置：

xml:
```xml
  app:start_color="#FFF700C2" //or your color
  app:end_color="#FFFFD900" //or your color
```

java:
```java
  mInsLoadingView.setStartColor(Color.YELLOW); //or your color
  mInsLoadingView.setEndColor(Color.BLUE); //or your color
```
默认的start color和start color为#FFF700C2和#FFFFD900。

#### 设置速度
通过设置环绕动画的时间和整体旋转的时间来改变速度：

xml:
```xml
  app:circle_duration="2000"
  app:rotate_duration="10000"
```

java:
```java
  mInsLoadingView.setCircleDuration(2000);
  mInsLoadingView.setRotateDuration(10000);
```
默认的时间为2000ms和10000ms。

## 关于我

[Email](mailto:qintong5900@163.com)

[My Blog](http://www.jianshu.com/u/d2b8b611095d)

### 许可
```
Copyright 2017 Qin Tong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
