package com.edofic.yodalib.database;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: andraz
 * Date: 5/18/12
 * Time: 9:56 AM
 */
public class Query<T> {
    private TableMetaData metaData;

    public Query(Class c) {
        metaData = MetaDataFactory.get(c);
    }

    public List<T> execute(Datasource datasource, boolean distinct, String[] columns, String selection, String groupBy,
                           String having, String orderBy, String limit) {
        Cursor cursor = datasource.query(distinct, columns, selection, groupBy, having, orderBy, limit);
        cursor.moveToFirst();
        ArrayList<T> items = new ArrayList<T>();
        while (!cursor.isAfterLast()) {
            //noinspection unchecked
            //cursor to object generates object from constructor
            //of T, so we are in fact type safe
            items.add((T) metaData.cursorToObject(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return items;
    }
}
