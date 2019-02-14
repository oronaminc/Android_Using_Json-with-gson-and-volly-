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

    public Point[] pointArr;
    final String url = "https://api.myjson.com/bins/seoem";
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

        if(AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

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

        int pointNum = field.fieldResult.fieldArea.size();
        String fieldName = field.fieldResult.fieldName;

        pointArr = new Point[pointNum];

        for(int i = 0; i<pointNum; i++) {
            pointName = field.fieldResult.fieldArea.get(i).pointName;
            pointX = Double.parseDouble(field.fieldResult.fieldArea.get(i).pointX);
            pointY = Double.parseDouble(field.fieldResult.fieldArea.get(i).pointY);
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


        //Polygon Setup
        PolygonOptions rectOptions = new PolygonOptions()
                .strokeColor(Color.RED)
                .strokeWidth(5);

        for(int i=0; i<pointNum; i++){
            rectOptions.add(new LatLng(pointArr[i].x, pointArr[i].y));
        }
        rectOptions.add(new LatLng(pointArr[0].x, pointArr[0].y));

        Polygon polygon = map.addPolygon(rectOptions);

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

    /*
    @Override
    public void onMapReady(final GoogleMap map) {
        Toast.makeText(getApplicationContext(),"onMapReady()먼저", Toast.LENGTH_SHORT).show();

    }
    */

    /*

        LatLng SEOUL = new LatLng(37.494462, 127.070654);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(20));
    */



    /*
    public void processResponce(String response){
        Gson gson = new Gson();
        FieldResult field = gson.fromJson(response, FieldResult.class);
        final int pointX = Integer.parseInt(field.fieldResult.fieldArea.get(1).pointX);
        final int pointY = Integer.parseInt(field.fieldResult.fieldArea.get(1).pointY);
        if(field != null){
            int pointSize = field.fieldResult.fieldArea.size();
            //int countMovie = movieList.boxOfficeResult.dailyBoxOfficeList.size();
            //println("응답받은 영화 갯수 : " + countMovie);
            //println("박스오피스 타입 : " + movieList.boxOfficeResult.boxofficeType);
            for(int i = 0; i<pointSize; i++){
                println(field.fieldResult.fieldArea.get(i).pointName);
            }
        }
    }
    */


    /*다른 예제 입니다.
    //FieldArea, FieldResult, Point class는 다
    public void processResponce(String response){
        Gson gson = new Gson();
        FieldResult fieldResult = gson.fromJson(response, FieldResult.class);

        if(fieldResult != null){
            int countPoint = fieldResult.fieldResult.fieldArea.size();
            println("응답 받은 점의 갯수 :" + countPoint);
            println("필지 제목 : " + fieldResult.fieldResult.fieldName);

        }
    }
    */


}
