package org.techtown.myvolly;

import android.app.FragmentManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.lang.String;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    //새로운 Point 배열 class를 만들어줌, 배열안에 x,y값을 갖도록
    public Point[] pointArr;
    //새로운 PolygonOptions 배열을 만들어줌, PolygonOptions는 원래 있던 Class임.
    public PolygonOptions[] fieldArr;

    //url 정의하기
    final String url = "https://api.myjson.com/bins/19nnce";
    TextView textView;
    MapFragment mapFragment;
    Double pointX, pointY;
    String pointName;
    MarkerOptions marker;
    GoogleMap map;
    LatLng pointValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        Button button = (Button) findViewById(R.id.button);


        RequestQueue rq = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println("$ AP_Project >>");
                        println(response);
                        dataMining(response);
                        //processResponce(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println("에러 -> " + error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        request.setShouldCache(false);
        rq.add(request);
        println("@@@@@@@@@@@@@요청 보냄@@@@@@@@@@@@@"+"\n");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendRequest();

            }
        });

        /*
        if(AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        */

        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });

    }


    public void dataMining(String response){
        Gson gson = new Gson();
        FieldResult field = gson.fromJson(response, FieldResult.class);

        // 도형이 몇개 있는 지
        int fieldNum = field.fieldResult.size();

        fieldArr = new PolygonOptions[fieldNum];

        for (int j = 0; j< fieldNum; j++){
            //도형안에 점이 몇개 있는 지
            String fieldName = field.fieldResult.get(j).fieldName;
            int pointNum = field.fieldResult.get(j).numbersOfPoint;

            pointArr =new Point[pointNum];
            for(int i = 0; i<pointNum; i++) {
                pointName = field.fieldResult.get(j).fieldArea.get(i).pointName;
                pointX = Double.parseDouble(field.fieldResult.get(j).fieldArea.get(i).pointX);
                pointY = Double.parseDouble(field.fieldResult.get(j).fieldArea.get(i).pointY);
                println(pointName + " : " + "("+ pointX+" , " + pointY+")");

                LatLng pointValue = new LatLng(pointX,pointY);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(pointValue);
                markerOptions.title(fieldName);
                markerOptions.snippet(pointName);
                map.addMarker(markerOptions);

                //polygon 좌표 세팅하기
                Point point = new Point();
                point.x = pointX;
                point.y = pointY;
                pointArr[i] = point; //i는 for문에서 쓰는 int 변수
            }

            PolygonOptions rectOptions = new PolygonOptions()
                    .strokeColor(Color.RED)
                    .strokeWidth(5);
            for(int i=0; i<pointNum; i++){
                rectOptions.add(new LatLng(pointArr[i].x, pointArr[i].y));
            }
            rectOptions.add(new LatLng(pointArr[0].x, pointArr[0].y));
            map.addPolygon(rectOptions);
            fieldArr[j] = rectOptions;
        }

        println("@@@@@@@@@@@@@요청 끝남@@@@@@@@@@@@@"+"\n");
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(pointX,pointY)));
        map.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void println(String data){
        textView.append(data + "\n");
    }

    public class Point{
        public double x;
        public double y;
    }

}
