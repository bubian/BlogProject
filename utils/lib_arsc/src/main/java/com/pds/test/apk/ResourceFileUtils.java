/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pds.test.apk;

import com.pds.arsc.apk.ResourceFile;
import com.pds.arsc.common.ApkUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public final class ResourceFileUtils {

  private static final String APK_FILE_PATH =
      "/Users/pengdaosong/pds/BlogProject/utils/lib_arsc/src/main/java/com/pds/test/data/Topeka.apk";
  private static final File apkFile = new File(APK_FILE_PATH);
  private static final String ARSC = "resources.arsc";

  public static void parseApk() throws Exception {
    String regex = "(.*?\\.arsc)|(AndroidManifest\\.xml)|(res/.*?\\.xml)";
    Map<String, byte[]> resourceFiles = ApkUtils.getFiles(apkFile, regex);
    for (Entry<String, byte[]> entry : resourceFiles.entrySet()) {
      String name = entry.getKey();
      byte[] fileBytes = entry.getValue();
      if (!name.startsWith("res/raw/")) {  // xml files in res/raw/ are not compact XML
        ResourceFile file = new ResourceFile(fileBytes);
        System.out.println("file:"+file);
      }
    }
  }

  /**
   * 获取resources.arsc文件
   * @return
   * @throws IOException
   */
  public static byte[] getArscFile() throws IOException {
    return ApkUtils.getFile(apkFile,ARSC);
  }
}
