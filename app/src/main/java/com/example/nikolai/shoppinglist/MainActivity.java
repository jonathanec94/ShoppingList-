package com.example.nikolai.shoppinglist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.nikolai.shoppinglist.domain.Facade;
import com.example.nikolai.shoppinglist.entity.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MenuActivity implements AdapterView.OnItemClickListener {
    List<String> shoppingLists;
    ListView list;
    Cursor cursor;
    Adapter adapter;
    ArrayList<ShoppingList> loadedShoppingLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Facade.getInstance().setContext(this);
        Facade.getInstance().openDB();
        shoppingLists = new ArrayList<>();

        list = (ListView)findViewById(R.id.listView_detail);

        loadShoppingLists();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public  void loadShoppingLists()
    {
        Facade.getInstance().loadShoppingLists();
        shoppingLists = new ArrayList<>();
        loadedShoppingLists = Facade.getInstance().getShoppingLists();
        for(int i = 0; i < loadedShoppingLists.size(); i++)
        {
            shoppingLists.add(loadedShoppingLists.get(i).getName());
        }
        updateList();
    }

    private void updateList()
    {list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, shoppingLists));
        list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ShoppingListDetailActivity.class);

        Facade.getInstance().setSelectedShoppingList(loadedShoppingLists.get(position).getId());
        startActivity(intent);
    }

    public void createShoppingList(View view)
    {
        EditText mEdit;
        mEdit = (EditText)findViewById(R.id.text_shoppingList);
        Facade.getInstance().createShoppingList(mEdit.getText().toString());
        loadShoppingLists();
        mEdit.setText("");
    }

}