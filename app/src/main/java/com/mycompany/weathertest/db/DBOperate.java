package com.mycompany.weathertest.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mycompany.weathertest.model.City;
import com.mycompany.weathertest.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zqs12 on 2016/3/16.
 * 数据库操作封装
 */
public class DBOperate {

    private Context context;

    public DBOperate(Context context) {
        this.context = context;
    }

    public List<Province> loadProvinces(SQLiteDatabase db) {
        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setProSort(cursor.getInt(cursor.getColumnIndex("ProSort")));
                province.setProName(cursor.getString(cursor.getColumnIndex("ProName")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<City> loadCities(SQLiteDatabase db, int ProID) {
        List<City> list = new ArrayList<>();

        Cursor cursor = db.query("T_City", null, "ProID = ?", new String[]{String.valueOf(ProID)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setProID(ProID);
                city.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
