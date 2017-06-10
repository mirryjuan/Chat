package com.example.mirry.chat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.mirry.chat.R;
import com.example.mirry.chat.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private GridView mGridView;
    private ArrayList<String> imgUrls = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        imgUrls = getIntent().getStringArrayListExtra("imgUrls");
        mGridView = (GridView) findViewById(R.id.gridView1);
        mGridView.setAdapter(new GridAdapter());
        mGridView.setOnItemClickListener(this);

    }


    public class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return imgUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return imgUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getApplication()).inflate(R.layout.item_pic, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String url = imgUrls.get(position);
            viewHolder.imageView.setImageBitmap(ImageUtil.getImageThumbnail(url,200,200));
            return convertView;
        }


        class ViewHolder {
            public ImageView imageView;
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("imgUrls",imgUrls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(getApplication(), ShowPhotoActivity.class);
        startActivity(intent);
    }
}
