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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: andraz
 * Date: 4/28/12
 * Time: 1:20 PM
 * Specifies the following field as a column in a table
 * Name defaults to field name, primary key and autoIncrement default to false
 * and position defaults to 1 for all columns which means alphabetic sorting.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * specifiy column name for use in queries
     *
     * @return column name
     */
    public String name() default "";

    /**
     * toggles the "primary key" in sql create
     * defaults to false
     *
     * @return is this column primary key
     */
    public boolean primaryKey() default false;

    /**
     * toggles the "autoincrement" in sql create
     * is used to determine if insertion is an update or new insertion
     * defaults to false
     *
     * @return is this column primary key
     */
    public boolean autoIncrement() default false;

    /**
     * columns in table are sorted by this value
     *
     * @return column position
     */
    public int position() default 1;
}
