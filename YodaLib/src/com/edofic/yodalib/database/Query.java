/*
 * Copyright 2012 Andraz Bajt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
