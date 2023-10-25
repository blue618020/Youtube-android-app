package com.blue.youtubeapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blue.youtubeapi.adapter.PostAdapter;
import com.blue.youtubeapi.config.Config;
import com.blue.youtubeapi.model.Post;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    ImageView imgSearch;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<Post> postArrayList = new ArrayList<Post>();
    PostAdapter adapter;
    String url;
    String searchURL;
    String keyword;
    String maxResults;
    String type;
    String pageToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        imgSearch = findViewById(R.id.imgSearch);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // 스크롤 리스너 사용하기
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 맨 마지막 데이터가 화면에 나타나게 되면 네트워크를 통해서 추가로 데이터 받아오기
                // 위에 setLayoutManager 를 get 으로 받아오는거래
                // 함수로 빼서 만듬
                addNetworkData();

            }
        });


        // 돋보기 이미지 눌렀을 때, 검색 결과 가져오기
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리스트 초기화
                postArrayList.clear();

                // 노출 방지를 위해 Config 파일에 주소와 키를 넣어서 가져오기
                searchURL = Config.HOST + Config.PATH + "?key=" + Config.GCP_API_KEY + "&part=snippet";
                keyword = "&q=" + editText.getText().toString().trim();
                maxResults = "&maxResults=" + 20;
                type = "&type=" + "video";

                url = searchURL + keyword + maxResults + type;

                // 로딩아이콘
                progressBar.setVisibility(View.INVISIBLE);

                // volley 사용
                // 너무 길어서 함수로 빼놨음
                getNetworkData();
            }
        });
    }

    private void getNetworkData() {
        // volley 사용
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // 순서대로 들어가서 파싱해야함 (이중 JSON 파싱)
        // items > id > videoId
        // items > snippet > title, description, thumbnails
        // thumbnails > medium > url(중간 썸네일)
        // thumbnails > high > url(큰 썸네일)

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // 로딩아이콘
                        progressBar.setVisibility(View.GONE);

                        try {
                            // 스크롤 리스너에서 사용할 페이지토큰을 가져옴
                            pageToken = response.getString("nextPageToken");

                            JSONArray items = response.getJSONArray("items");

                            for (int i=0; i<items.length(); i++){
                                JSONObject jsonObject = items.getJSONObject(i); // index 순서대로 가져옴

                                // 동영상 주소 가져오기
                                JSONObject id = jsonObject.getJSONObject("id");
                                String videoId = id.getString("videoId");

                                // snippet 안의 제목, 내용 가져오기
                                JSONObject snippet = jsonObject.getJSONObject("snippet");
                                String title = snippet.getString("title");
                                String body = snippet.getString("description");

                                // thumbnails 안의 중간 썸네일 이미지 가져오기
                                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                JSONObject medium = thumbnails.getJSONObject("medium");
                                String thumbnailUrl = medium.getString("url");

                                // thumbnails 안의 큰 썸네일 이미지 가져오기
                                JSONObject high = thumbnails.getJSONObject("high");
                                String highUrl = high.getString("url");

                                // 메모리에 저장
                                Post post = new Post(title, body, thumbnailUrl, highUrl, videoId);

                                // 리스트에 저장
                                postArrayList.add(post);
                            }
                        } catch (JSONException e) {
                            return;
                        }
                        // 화면에 보여주기
                        adapter = new PostAdapter(MainActivity.this, postArrayList);
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 로딩아이콘
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
        queue.add(request);
    }


    private void addNetworkData() {
        progressBar.setVisibility(View.VISIBLE);
        int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
        int totalCount = recyclerView.getAdapter().getItemCount(); // PostAdapter에서 가져옴

        if (lastPosition+1 == totalCount){
            // 스크롤을 데이터 맨 끝까지 한 상태이므로, 네트워크를 통해서 데이터를 추가로 받아오기
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    searchURL + keyword + maxResults + type + "&pageToken=" + pageToken,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // 로딩아이콘
                            progressBar.setVisibility(View.GONE);

                            try {
                                // 스크롤 리스너에서 사용할 페이지토큰을 가져옴
                                pageToken = response.getString("nextPageToken");

                                JSONArray items = response.getJSONArray("items");

                                for (int i=0; i<items.length(); i++){
                                    JSONObject jsonObject = items.getJSONObject(i); // index 순서대로 가져옴

                                    // 동영상 주소 가져오기
                                    JSONObject id = jsonObject.getJSONObject("id");
                                    String videoId = id.getString("videoId");

                                    // snippet 안의 제목, 내용 가져오기
                                    JSONObject snippet = jsonObject.getJSONObject("snippet");
                                    String title = snippet.getString("title");
                                    String body = snippet.getString("description");

                                    // thumbnails 안의 중간 썸네일 이미지 가져오기
                                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                                    JSONObject medium = thumbnails.getJSONObject("medium");
                                    String thumbnailUrl = medium.getString("url");

                                    // thumbnails 안의 큰 썸네일 이미지 가져오기
                                    JSONObject high = thumbnails.getJSONObject("high");
                                    String highUrl = high.getString("url");

                                    // 메모리에 저장
                                    Post post = new Post(title, body, thumbnailUrl, highUrl, videoId);

                                    // 리스트에 저장
                                    postArrayList.add(post);
                                }
                            } catch (JSONException e) {
                                return;
                            }
                            // 화면갱신
                            adapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 로딩아이콘
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
            queue.add(request);
        }
    }

}