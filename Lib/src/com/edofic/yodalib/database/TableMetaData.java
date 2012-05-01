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
class TableMetaData {
    private String tableName;
    private int version;
    private ArrayList<ColumnMetaData> columns = new ArrayList<ColumnMetaData>();
    private ArrayList<ColumnMetaData> columnsNoIncrement = new ArrayList<ColumnMetaData>();
    private ArrayList<ColumnMetaData> columnsAutoincrement = new ArrayList<ColumnMetaData>();

    TableMetaData(Class c) {
        @SuppressWarnings(value = "unchecked")
        Table table = (Table) c.getAnnotation(Table.class);
        if (table == null) {
            return;
        }

        tableName = table.name();
        version = table.version();

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
    }

    public String getTableName() {
        return tableName;
    }

    public ArrayList<ColumnMetaData> getColumns() {
        return columns;
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

    private class FieldComparator implements Comparator<Field> {
        @Override
        public int compare(Field field, Field field1) {
            return field.getName().compareTo(field1.getName());
        }
    }
}
