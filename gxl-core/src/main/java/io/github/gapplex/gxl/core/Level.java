/*
 * Copyright 2026 GapplX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.gapplex.gxl.core;

import static io.github.gapplex.gxl.core.Constants.DEBUG_INT;
import static io.github.gapplex.gxl.core.Constants.INFO_INT;
import static io.github.gapplex.gxl.core.Constants.WARN_INT;
import static io.github.gapplex.gxl.core.Constants.ERROR_INT;
import static io.github.gapplex.gxl.core.Constants.DEBUG_STR;
import static io.github.gapplex.gxl.core.Constants.INFO_STR;
import static io.github.gapplex.gxl.core.Constants.WARN_STR;
import static io.github.gapplex.gxl.core.Constants.ERROR_STR;

public enum Level {
    DEBUG(DEBUG_INT, DEBUG_STR),INFO(INFO_INT, INFO_STR),WARN(WARN_INT, WARN_STR),ERROR(ERROR_INT, ERROR_STR);

    private final int levelInt;
    private final String levelStr;

    Level(int i, String s){
        levelInt = i;
        levelStr = s;
    }

    public int toInt() {
        return levelInt;
    }

    public String toString(){
        return levelStr;
    }

    public static Level intToLevel(int i){
        switch (i){
            case DEBUG_INT: {
                return DEBUG;
            }
            case INFO_INT: {
                return INFO;
            }
            case WARN_INT:{
                return WARN;
            }
            case ERROR_INT:{
                return ERROR;
            }
            default:{
                throw new IllegalArgumentException("Unknown level int:" + i);
            }
        }
    }

    public static Level stringToLevel(String s){
        if (s == null) {
            throw new IllegalArgumentException("Level string cannot be null");
        }
        switch (s.trim().toUpperCase()){
            case DEBUG_STR: {
                return DEBUG;
            }
            case INFO_STR: {
                return INFO;
            }
            case WARN_STR:{
                return WARN;
            }
            case ERROR_STR:{
                return ERROR;
            }
            default:{
                throw new IllegalArgumentException("Unknown level str:" + s);
            }
        }
    }
}
