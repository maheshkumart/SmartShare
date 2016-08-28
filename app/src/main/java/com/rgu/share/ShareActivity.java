package com.rgu.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.rgu.share.adapter.ShareItemsAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ShareActivity extends AppCompatActivity implements ShareItemsAdapter.OnItemSelectListener {

    private RecyclerView mRecyclerView;
    private ShareItemsAdapter mShareItemsAdapter;
    private ArrayList<Uri> mShareItems = new ArrayList<>();

    private static String FILENAME = "share_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //load data before to append with new data
        loadShareDataFromFile();

        if (getIntent().getExtras() != null && getIntent().getExtras().get(Intent.EXTRA_STREAM) != null) {
            Object shareData = getIntent().getExtras().get(Intent.EXTRA_STREAM);

            if (shareData instanceof Uri) {
                mShareItems.add((Uri) shareData);
            } else {
                mShareItems.addAll((Collection<Uri>) shareData);
            }

            storeShareData();
        }

        mShareItemsAdapter = new ShareItemsAdapter(this, mShareItems);
        mRecyclerView.setAdapter(mShareItemsAdapter);
    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.share_recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteMenuItem = menu.findItem(R.id.delete_item);
        MenuItem shareMenuItem = menu.findItem(R.id.share_item);

        if (mShareItemsAdapter != null && mShareItemsAdapter.getIsDeleteModeActivated()) {
            deleteMenuItem.setVisible(true);
            shareMenuItem.setVisible(false);
        } else {
            deleteMenuItem.setVisible(false);
            shareMenuItem.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.share_item:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("image/jpeg");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mShareItems);
                startActivity(intent);
                break;
            case R.id.clear_all:
                clearAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadShareDataFromFile() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            ArrayList<String> itemList = (ArrayList<String>) ois.readObject();

            for (String item : itemList) {
                mShareItems.add(Uri.parse(item));
            }

            ois.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeShareData() {
        FileOutputStream fos;
        try {

            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            ArrayList<String> itemList = new ArrayList<>();

            for (Uri item : mShareItems) {
                itemList.add(item.toString());
            }

            oos.writeObject(itemList);

            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAll() {
        mShareItems.clear();
        mShareItemsAdapter.clearAllItems();
        storeShareData();
    }

    @Override
    public void onDeleteModeActivated() {
        invalidateOptionsMenu();
    }

    private boolean isDeleteModeActivated() {
        return mShareItemsAdapter != null && mShareItemsAdapter.getIsDeleteModeActivated();
    }

    @Override
    public void onBackPressed() {
        if (isDeleteModeActivated()) {
            mShareItemsAdapter.setIsDeleteModeActivated(false);
            mShareItemsAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }
}
