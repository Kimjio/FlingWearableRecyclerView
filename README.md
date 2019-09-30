# FlingWearableRecyclerView
[ ![Download](https://api.bintray.com/packages/kimji/maven/FlingWearableRecyclerView/images/download.svg) ](https://bintray.com/kimji/maven/FlingWearableRecyclerView/_latestVersion)

로터리 fling 이 구현 된 WearableRecyclerView

### Usage

On your build.gradle, add the library to the dependencies section:
```gradle
dependencies {
  // ...
  implementation 'com.kimjio:flingwearablerecyclerview:1.0'
  // ...
}
```

Code:
```java
FlingWearableRecyclerView recycler = findViewById(R.id.recycler);
recycler.setStrength(/*STRENGTH_LOW|STRENGTH_NORMAL|STRENGTH_HIGH*/ FlingWearableRecyclerView.STRENGTH_NORMAL);
recycler.setStrength(/*(float)*/ 1.25f);
```
```xml
<com.kimjio.flingwearablerecyclerview.FlingWearableRecyclerView
    android:id="@+id/recycler"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:flingStrength="low|normal|high" />
```
