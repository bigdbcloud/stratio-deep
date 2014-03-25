/*
 * Copyright 2014, Stratio.
 *
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

package com.stratio.deep.cql;

import com.stratio.deep.util.Constants;
import org.apache.hadoop.conf.Configuration;

/**
 * Stratio Deep utility class used to get/save specific properties to
 * Hadoop' configuration object.
 *
 * @author Luca Rosellini <luca@stratio.com>
 */
public final class DeepConfigHelper {

    public static final String OUTPUT_BATCH_SIZE = "output.batch.size";
    public static final String CF_METADATA = "cassandra.cf.metadata";

    public static int getOutputBatchSize(Configuration conf) {
        return conf.getInt(OUTPUT_BATCH_SIZE, Constants.DEFAULT_BATCH_SIZE);
    }

    /**
     * sets the batch size used to write to cassandra. Defaults to 100.
     *
     * @param conf
     * @param batchSize
     */
    public static void setOutputBatchSize(Configuration conf, int batchSize) {
        if (batchSize > 0) {
            conf.setInt(OUTPUT_BATCH_SIZE, batchSize);
        }
    }
}
