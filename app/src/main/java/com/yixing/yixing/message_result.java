package com.yixing.yixing;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import java.util.List;
import overlayutil.OverlayManager;
import overlayutil.TransitRouteOverlay;
public class message_result extends AppCompatActivity implements
        BaiduMap.OnMapClickListener,OnGetRoutePlanResultListener, OnGetGeoCoderResultListener {
    //private EditText m_start;
    private EditText m_end;
    Button mBtnPre = null;
    Button mBtnNext = null;
    int nodeIndex = -1;
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null;
    MapView mMapView = null;
    BaiduMap mBaidumap = null;
    RoutePlanSearch mSearch = null;
    TransitRouteResult nowResultransit = null;
    GeoCoder mSearch1 = null; // 搜索模块，也可去掉地图模块独立使用
    int nowSearchType = -1;
    //String startNodeStr = null;
    String endNodeStr = null;
    LatLng fromLl;
    LatLng endLl;
    boolean hasShownDialogue = false;
    private LocationClient mLocationClient = null;
    private message_result.MyLocationListener myListener = new
            message_result.MyLocationListener();
    private boolean isFirstLoc = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_result);
        CharSequence titleLable = "路线规划功能";
        setTitle(titleLable);
        mMapView = (MapView) findViewById(R.id.map);
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        mBaidumap = mMapView.getMap();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.setOnMapClickListener(this);

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch1 = GeoCoder.newInstance();
        mSearch1.setOnGetGeoCodeResultListener(this);

        //m_start = (EditText)findViewById(R.id.start);
        m_end = (EditText)findViewById(R.id.end);
        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mBaidumap.setMyLocationEnabled(true);
        MyLocationConfiguration config = new
                MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true,
                BitmapDescriptorFactory.fromResource(R.drawable.loction));
        mBaidumap.setMyLocationConfiguration(config);
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(message_result.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude,
                result.getLocation().longitude);
        endLl = new LatLng(result.getLocation().latitude,result.getLocation().longitude);

        Log.e("GeoCodeDemo", "onGetGeoCodeResult = " + result.toString());
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            fromLl = new LatLng(location.getLatitude(),location.getLongitude());
            if(location != null){
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaidumap.setMyLocationData(locData);
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaidumap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
    public void searchButtonProcess(View v) {
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        //startNodeStr = m_start.getText().toString();
        endNodeStr = m_end.getText().toString();
        // Geo搜索
        mSearch1.geocode(new GeoCodeOption()
                .city("西安")
                .address(endNodeStr));
        //LatLng fromLl = new LatLng(39.931867, 116.395645);
        //LatLng endLl = new LatLng(39.915267, 116.40355);
        PlanNode stNode = PlanNode.withLocation(fromLl);
        PlanNode enNode = PlanNode.withLocation(endLl);
        //PlanNode stNode = PlanNode.withCityNameAndPlaceName("西安", startNodeStr);
        //PlanNode enNode = PlanNode.withCityNameAndPlaceName("西安", endNodeStr);
        //PlanNode stNode = PlanNode.withLocation(new LatLng(39.931867, 116.395645));
        //PlanNode enNode = PlanNode.withLocation(new LatLng(39.915267, 116.40355));
        Log.i("Location_s2", String.valueOf(stNode.getLocation()));
        Log.i("Location_e2", String.valueOf(enNode.getLocation()));
        if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode).city("西安").to(enNode));
            nowSearchType = 2;
        }
    }
    public void nodeClick(View v) {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;
        if (nowSearchType != 0 && nowSearchType != -1) {
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            step = route.getAllStep().get(nodeIndex);
            if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep)
                        step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep)
                        step).getInstructions();
            }
        }
        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        popupText = new TextView(message_result.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(message_result.this, "抱歉，未找到结果",
                    Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            result.getSuggestAddrInfo();
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            if (result.getRouteLines().size() > 1) {
                nowResultransit = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(message_result.this, result.getRouteLines(), RouteLineAdapter.Type.TRANSIT_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultransit.getRouteLines().get(position);
                            TransitRouteOverlay overlay = new
                                    MyTransitRouteOverlay(mBaidumap);
                            mBaidumap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultransit.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }
                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }
        }
    }
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
    }
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
    }
    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
    }
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
    }
    private class MyTransitRouteOverlay extends TransitRouteOverlay {
        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }
        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }
    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }
    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        if (mSearch != null) {
            mSearch.destroy();
        }
        mMapView.onDestroy();
        super.onDestroy();
    }
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }
    class MyTransitDlg extends Dialog {
        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;
        OnItemInDlgClickListener onItemInDlgClickListener;
        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }
        public MyTransitDlg(Context context, List<? extends RouteLine>
                transitRouteLines, RouteLineAdapter.Type
                                    type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);
            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);
            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    mBtnPre.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }
        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener
                                                       itemListener) {
            onItemInDlgClickListener = itemListener;
        }
    }
}
