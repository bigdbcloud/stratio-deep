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

package com.stratio.deep.rdd;

import com.stratio.deep.config.IDeepJobConfig;
import com.stratio.deep.context.AbstractDeepSparkContextTest;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.spark.Partition;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import scala.collection.Seq;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.CharacterCodingException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Abstract class defining the common test structure that all concrete subclasses should respect.
 *
 * @param <W>
 */
public abstract class CassandraRDDTest<W> extends AbstractDeepSparkContextTest {
    private Logger logger = Logger.getLogger(getClass());

    protected CassandraRDD<W> rdd;
    private IDeepJobConfig<W> rddConfig;
    private IDeepJobConfig<W> writeConfig;

    protected int testBisectFactor = 8;

    protected abstract void checkComputedData(W[] entities);

    protected abstract void checkSimpleTestData();

    protected CassandraRDD<W> getRDD() {
        return this.rdd;
    }

    protected IDeepJobConfig<W> getReadConfig() {
        return rddConfig;
    }

    protected IDeepJobConfig<W> getWriteConfig() {
        return writeConfig;
    }

    protected abstract CassandraRDD<W> initRDD();

    protected abstract IDeepJobConfig<W> initReadConfig();

    @BeforeClass
    protected void initServerAndRDD() throws IOException, URISyntaxException, ConfigurationException,
            InterruptedException {

        rddConfig = initReadConfig();
        writeConfig = initWriteConfig();
        rdd = initRDD();
    }

    protected abstract IDeepJobConfig<W> initWriteConfig();

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "testGetPreferredLocations")
    public void testCompute() throws CharacterCodingException {

        logger.info("testCompute()");
        Object obj = getRDD().collect();

        assertNotNull(obj);

        W[] entities = (W[]) obj;

        checkComputedData(entities);
    }

    @Test(dependsOnMethods = "testRDDInstantiation")
    public void testGetPartitions() {
        logger.info("testGetPartitions()");
        Partition[] partitions = getRDD().partitions();

        assertNotNull(partitions);
        assertEquals(partitions.length, getReadConfig().getBisectFactor() * (8 + 1));
    }

    @Test(dependsOnMethods = "testGetPartitions")
    public void testGetPreferredLocations() {
        logger.info("testGetPreferredLocations()");
        Partition[] partitions = getRDD().partitions();

        Seq<String> locations = getRDD().getPreferredLocations(partitions[0]);

        assertNotNull(locations);
    }

    @Test
    public void testRDDInstantiation() {
        logger.info("testRDDInstantiation()");
        assertNotNull(getRDD());


    }

    @Test(dependsOnMethods = "testSimpleSaveToCassandra")
    public abstract void testSaveToCassandra();

    @Test(dependsOnMethods = "testCompute")
    public abstract void testSimpleSaveToCassandra();

    protected static void truncateCf(String keyspace, String cf) {
        executeCustomCQL("TRUNCATE  " + keyspace + "." + cf);

    }

    @Test(dependsOnMethods = "testSaveToCassandra")
    public abstract void testCql3SaveToCassandra();
}
