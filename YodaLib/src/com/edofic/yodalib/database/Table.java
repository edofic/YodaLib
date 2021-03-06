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
 * Time: 1:09 PM
 * Specify the following class as a table in database.
 * Class must be public and provide no-parameter constructor,
 * or else operations will silently fail at runtime
 * columns in the table are sorted by the position attribute
 * or alphabetically if not provided
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * specifiy table name for use in queries (mandatory)
     *
     * @return table name
     */
    public String name() default "";

    /**
     * specifiy database version for open helper, so db upgrades
     * can be performed
     *
     * @return database version
     */
    public int version() default 1;
}
