package com.example.nikolai.shoppinglist.domain;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ListView;

import com.example.nikolai.shoppinglist.R;
import com.example.nikolai.shoppinglist.dataSourceLayer.ShoppingListDb;
import com.example.nikolai.shoppinglist.dataSourceLayer.server.ServerDb;
import com.example.nikolai.shoppinglist.entity.ShoppingList;
import com.example.nikolai.shoppinglist.entity.ShoppingListDetail;
import com.example.nikolai.shoppinglist.entity.User;

import java.util.ArrayList;

import static com.example.nikolai.shoppinglist.dataSourceLayer.ShoppingListDbHelper.*;


/**
 * Created by Jonathan on 04-12-2015.
 */
public class Facade {
    private static Facade instance;
    private ShoppingListDb db;
    ArrayList<ShoppingList> shoppingLists;
    ArrayList<ShoppingListDetail> shoppingListDetail;
    Cursor cursor;
    Context context;
    int selectedShoppingList;
    User userLoggedOn = null;
    private ServerDb serverDb;
    private Facade(){}
    public void setContext(Context context)
    {
        this.context = context;
        serverDb = new ServerDb(context);
        db = new ShoppingListDb(context);
        //remove database
        //context.deleteDatabase("datastorage");
    }

    public void setSelectedShoppingList(int id)
    {
        selectedShoppingList = id;
    }
    public int getSelectedShoppingList()
    {
        return selectedShoppingList;
    }

    public ShoppingList findShoppingList(int id)
    {
        for(int i = 0; i < shoppingLists.size(); i++)
        {
            if(shoppingLists.get(i).getId() == (id))
            {
                return shoppingLists.get(i);
            }
        }
        return null;
    }


    public ShoppingListDetail findShoppingListItem(String id)
    {
        for(int i = 0; i < shoppingListDetail.size(); i++)
        {
            if(shoppingListDetail.get(i)._id.equalsIgnoreCase(id))
            {
                return shoppingListDetail.get(i);
            }
        }
        return null;
    }

    public void openDB(){
        db.open();
    }

    public void loadShoppingLists()
    {
        //        db.removeAll();
        shoppingLists = new ArrayList<>();
        cursor = db.getShoppingLists();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            shoppingLists.add(new ShoppingList(cursor.getInt(cursor.getColumnIndex(list_ID_COLUMN)),cursor.getString(cursor.getColumnIndex(list_NAME_COLUMN)),cursor.getString(cursor.getColumnIndex(list_DATO_COLUMN)),cursor.getString(cursor.getColumnIndex(list_user_fk_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        serverDb.getListsFromUsername("a", shoppingLists);
   // return shoppingLists;
    }
    public  ArrayList<ShoppingListDetail> LoadshoppingListDetail()
    {

        shoppingListDetail = new ArrayList<>();
       cursor = db.getDetails(selectedShoppingList);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            shoppingListDetail.add(new ShoppingListDetail(cursor.getString(cursor.getColumnIndex(detail_ID_COLUMN)),cursor.getString(cursor.getColumnIndex(detail_product_COLUMN)),cursor.getInt(cursor.getColumnIndex(detail_list_fk_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return  shoppingListDetail;
    }




    public ArrayList<ShoppingList> getShoppingLists()
    {
        return shoppingLists;
    }


    public void createShoppingList(String name)
    {
        if(userLoggedOn != null) {
            db.createShoppingList(name, "04-12-2015", userLoggedOn.getUserName());
            serverDb.createList(userLoggedOn.getUserName(),name);
        }
        else{
            db.createShoppingList(name, "04-12-2015", "");
        }
    }

    public void createDetail(String product)
    {
        if(userLoggedOn != null) {
            serverDb.createItemInList(findShoppingList(selectedShoppingList).getName(), userLoggedOn.getUserName(),product);
        }
        db.createDetail(product, selectedShoppingList);

    }

    public void deleteShoppinglist()
    {
        if(userLoggedOn != null) {
            serverDb.deleteList(findShoppingList(selectedShoppingList).getName(), userLoggedOn.getUserName());
        }
        db.deleteShoppinglist(selectedShoppingList);
    }

    public boolean createUser(String userName, String password) {
        boolean created = db.createUser(userName, password);

        if (created){userLoggedOn = new User(userName, password);}

        return created;
    }

    public  User getUser()
    {
        return userLoggedOn;
    }

    public boolean userLogon(String userName, String password)
    {
       boolean logon = db.userLogon(userName, password);
        if(logon)
        {
            userLoggedOn = new User(userName, password);
            return true;
        }
        return false;
    }

    public boolean deleteShoppinglistDetail(String id){
        if(userLoggedOn != null) {
            serverDb.deleteItemInList(findShoppingList(selectedShoppingList).getName(), userLoggedOn.getUserName(),findShoppingListItem(id).getProduct());
        }
        return db.deleteShoppinglistDetail(id);
    }



    public static Facade getInstance() {
        if (instance == null ) {
            synchronized (Facade.class) {
                if (instance == null) {
                    instance = new Facade();
                }
            }
        }
        return instance;
    }

}
