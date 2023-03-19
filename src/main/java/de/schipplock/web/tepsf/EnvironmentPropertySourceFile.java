/*
 * Copyright 2023 Andreas Schipplock
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schipplock.web.tepsf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;

import org.apache.tomcat.util.security.PermissionCheck;
import org.apache.tomcat.util.digester.EnvironmentPropertySource;

public class EnvironmentPropertySourceFile extends EnvironmentPropertySource {
    
    @Override
    public String getProperty(String key) {
        if (key.endsWith("_FILE")) {
            try {
                return Files.readString(Path.of(System.getenv(key)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return System.getenv(key);
    }

    @Override
    public String getProperty(String key, ClassLoader classLoader) {
        if (classLoader instanceof PermissionCheck) {
            Permission p = new RuntimePermission("getenv." + key, null);
            if (!((PermissionCheck) classLoader).check(p)) {
                return null;
            }
        }
        return getProperty(key);
    }
}