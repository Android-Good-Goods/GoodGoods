package com.myapp.haowu.contoller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.myapp.haowu.R;
import com.myapp.haowu.contoller.adapter.MypublistAdapter;
import com.myapp.haowu.model.Model;
import com.myapp.haowu.model.db.User;
import com.myapp.haowu.utils.HttpUtils;
import com.myapp.haowu.utils.UserUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyfreeActivity extends AppCompatActivity {

    //定义变量
    private ImageView iv_myfree_back;

    private ListView lv_myfree_list;

    private MyfreeActivity.onClickListener listener;

    //进度条
    private ProgressDialog pd;

    //商品列表
    private ArrayList<Map<String,Object>> mData= new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfree);

        //初始化变量
        initView();
        //获取数据
        getGoodsData();
    }

    //初始化视图
    private void initView() {
        //初始化进度条
        pd = new ProgressDialog(MyfreeActivity.this);
        pd.setMessage("数据加载中...");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        lv_myfree_list = (ListView)findViewById(R.id.lv_myfree_list);
        iv_myfree_back = (ImageView) findViewById(R.id.iv_myfree_back);
        //返回按钮的监听
        listener = new onClickListener();
        iv_myfree_back.setOnClickListener(listener);
    }

    //handler处理
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    int goodsBack;
                    if(!msg.obj.toString().trim().isEmpty()&&!msg.obj.toString().trim().equals("-1"))
                    {
                        pd.cancel();
                        try{
                            JSONArray data = new JSONArray();
                            JSONObject goodsResult = new JSONObject(msg.obj.toString().trim());
                            goodsBack = goodsResult.getInt("code");
                            if(goodsBack == 1){
                                data = goodsResult.getJSONArray("data");
                                for(int i=0;i<data.length();i++){
                                    String goodsimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("goodsdata").getString("gimage"), "utf-8");
                                    String userimageurl = URLEncoder.encode(data.getJSONObject(i).getJSONObject("userdata").getString("headphoto"), "utf-8");
                                    //recycleview显示
                                    Map<String,Object> item = new HashMap<String,Object>();
                                    item.put("uid",data.getJSONObject(i).getJSONObject("userdata").getInt("uid"));
                                    item.put("account",data.getJSONObject(i).getJSONObject("userdata").getString("account"));
                                    item.put("nickname",data.getJSONObject(i).getJSONObject("userdata").getString("nickname"));
                                    item.put("reputation",data.getJSONObject(i).getJSONObject("userdata").getString("reputation"));
                                    item.put("tel",data.getJSONObject(i).getJSONObject("userdata").getString("tel"));
                                    item.put("hxid",data.getJSONObject(i).getJSONObject("userdata").getString("hxid"));
                                    item.put("gid",data.getJSONObject(i).getJSONObject("goodsdata").getInt("gid"));
                                    item.put("gname",data.getJSONObject(i).getJSONObject("goodsdata").getString("gname"));
                                    item.put("ghownew",data.getJSONObject(i).getJSONObject("goodsdata").getString("ghownew"));
                                    item.put("gprice",Double.toString(data.getJSONObject(i).getJSONObject("goodsdata").getDouble("gprice")));
                                    item.put("ggetway",data.getJSONObject(i).getJSONObject("goodsdata").getString("ggetway"));
                                    item.put("goprice",data.getJSONObject(i).getJSONObject("goodsdata").getString("goprice"));
                                    item.put("gdetail",data.getJSONObject(i).getJSONObject("goodsdata").getString("gdetail"));
                                    item.put("gaddress",data.getJSONObject(i).getJSONObject("goodsdata").getString("gaddress"));
                                    item.put("gscannum",data.getJSONObject(i).getJSONObject("goodsdata").getString("gscannum"));
                                    item.put("gtype",data.getJSONObject(i).getJSONObject("goodsdata").getString("gtype"));
                                    item.put("gstate",data.getJSONObject(i).getJSONObject("goodsdata").getString("gstate"));
                                    item.put("gimage",getResources().getString(R.string.burl)+"Image_Servlet?" + goodsimageurl);
                                    item.put("headphoto",getResources().getString(R.string.burl)+"Image_Servlet?" + userimageurl);
                                    mData.add(item);
                                }
                                //设置适配器
                                MypublistAdapter adapter = new MypublistAdapter(MyfreeActivity.this,mData, lv_myfree_list);
                                lv_myfree_list.setAdapter(adapter);
                                //适配器的点击监听
                                lv_myfree_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                            long id) {
                                        Intent intent = new Intent(view.getContext(), GoodsdetailActivity.class);
                                        intent.putExtra("data",(Serializable)mData.get(position));
                                        view.getContext().startActivity(intent);
                                    }
                                });
                            }
                            else {
                                pd.cancel();
                                Toast.makeText(MyfreeActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            pd.cancel();
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            pd.cancel();
                            e.printStackTrace();
                        }
                    }else {
                        pd.cancel();
                        Toast.makeText(MyfreeActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //点击监听
    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_myfree_back:
                    finish();
                    break;
            }
        }
    }

    //获取数据
    private void getGoodsData(){
        User user = UserUtils.getCurrentUser();
        pd.show();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            HttpUtils httpUtils = new HttpUtils();
            @Override
            public void run() {
                mData.clear();
                Map<String, String> params = new HashMap<String, String>();
                params.put("requesttop","myfree");
                params.put("account",user.getAccount().trim());
                params.put("state","1");
                String strUrlpath = getResources().getString(R.string.burl) + "Goodslist_Servlet";
                String Result = httpUtils.AsubmitPostData(strUrlpath, params, "utf-8");
                System.out.println("获取的结果为：" + Result);
                Message message = new Message();
                message.what = 1;
                message.obj = Result;
                handler.sendMessage(message);
            }
        });
    }
}
