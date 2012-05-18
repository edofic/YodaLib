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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 1:00 PM
 * Meta data about table
 * contains all the data needed to construct queries and methods to fill objects with results
 * mapping to columns is with respect to alphabetic order of fields
 */
public class TableMetaData {
    private Class c;
    private String tableName;
    private int version;
    private final ArrayList<ColumnMetaData> columns = new ArrayList<ColumnMetaData>();
    private final String[] columnNames;
    private final ArrayList<ColumnMetaData> columnsNoIncrement = new ArrayList<ColumnMetaData>();
    private final ArrayList<ColumnMetaData> columnsAutoincrement = new ArrayList<ColumnMetaData>();
    private Constructor constructor;

    TableMetaData(Class c) {
        @SuppressWarnings(value = "unchecked")
        Table table = (Table) c.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("must provide annotated class");
        }
        this.c = c;
        tableName = table.name();
        if (tableName.equals("")) {
            tableName = c.getSimpleName();
            if (tableName.equals("")) {
                throw new IllegalArgumentException("if you are using anonymous classes you must provide name in the annotation");
            }
        }

        version = table.version();

        try {
            constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Provided class does not have a parameterless constructor");
        }

        Field[] fields = c.getDeclaredFields();
        Arrays.sort(fields, new FieldComparator());
        for (Field f : fields) {
            @SuppressWarnings(value = "unchecked")
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                final ColumnMetaData data = new ColumnMetaData(columns.size(), f);
                columns.add(data);
                if (column.autoIncrement()) {
                    columnsAutoincrement.add(data);
                } else {
                    columnsNoIncrement.add(data);
                }
            }
        }

        columnNames = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            columnNames[i] = columns.get(i).getName();
        }
    }

    public Object cursorToObject(Cursor cursor) {
        try {
            Object o = constructor.newInstance((Object[]) null);
            for (ColumnMetaData column : columns) {
                column.set(cursor, o);
            }
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public ArrayList<ColumnMetaData> getColumns() {
        return columns;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public ArrayList<ColumnMetaData> getColumnsNoIncrement() {
        return columnsNoIncrement;
    }

    public ArrayList<ColumnMetaData> getColumnsAutoincrement() {
        return columnsAutoincrement;
    }

    public int getVersion() {
        return version;
    }

    /**
     * sort fields by position attribute in column annotation
     * or if not available or equal, alphabetically.
     */
    private class FieldComparator implements Comparator<Field> {
        @Override
        public int compare(Field field1, Field field2) {
            Column c1 = field1.getAnnotation(Column.class);
            Column c2 = field2.getAnnotation(Column.class);
            if (c1 != null && c2 != null) {
                int delta = c1.position() - c2.position();
                if (delta < 0) return -1;
                if (delta > 0) return +1;
            }

            return field1.getName().compareTo(field2.getName());
        }
    }
}
