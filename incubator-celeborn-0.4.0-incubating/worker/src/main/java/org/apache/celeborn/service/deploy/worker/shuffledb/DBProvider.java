/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.celeborn.service.deploy.worker.shuffledb;

import java.io.File;
import java.io.IOException;

/** Note: code copied from Apache Spark. */
public class DBProvider {
  public static DB initDB(DBBackend dbBackend, File dbFile, StoreVersion version)
      throws IOException {
    if (dbFile != null) {
      switch (dbBackend) {
        case LEVELDB:
          org.iq80.leveldb.DB levelDB = LevelDBProvider.initLevelDB(dbFile, version);
          return levelDB != null ? new LevelDB(levelDB) : null;
        case ROCKSDB:
          org.rocksdb.RocksDB rocksDB = RocksDBProvider.initRockDB(dbFile, version);
          return rocksDB != null ? new RocksDB(rocksDB) : null;
        default:
          throw new IllegalArgumentException("Unsupported DBBackend: " + dbBackend);
      }
    }
    return null;
  }
}
