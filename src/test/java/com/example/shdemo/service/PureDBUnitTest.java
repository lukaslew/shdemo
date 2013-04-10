package com.example.shdemo.service;

import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static java.sql.DriverManager.getConnection;
import static org.dbunit.dataset.filter.DefaultColumnFilter.excludedColumnsTable;

public class PureDBUnitTest {
    private static final String JDBC_DRIVER_CLASS = "org.hsqldb.jdbcDriver";
    private static final String JDBC_URL = "jdbc:hsqldb:hsql://localhost/workdb";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    private static final String FULL_DATA_PATH = "src/test/resources/fullData.xml";
    private static final String PERSON_DATA_PATH = "src/test/resources/personData.xml";

    private IDatabaseConnection databaseConnection;
    private IDatabaseTester databaseTester;

    @Before
    public void setUp() throws Exception {
        databaseConnection = new DatabaseConnection(getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD));
        databaseTester = new JdbcDatabaseTester(JDBC_DRIVER_CLASS, JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(new File(FULL_DATA_PATH)));

        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
        // org.dbunit.operation.DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
    }

    @After
    public void tearDown() throws Exception {
        databaseTester.onTearDown();
    }

    @Test
    public void test() throws Exception {
        // operations

        IDataSet fileDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(new File(PERSON_DATA_PATH)));
        ITable expectedTable = fileDataSet.getTable("PERSON");

        IDataSet dbDataSet = databaseConnection.createDataSet();
        ITable actualTable = dbDataSet.getTable("PERSON");
        ITable filteredActualTable = excludedColumnsTable(actualTable, new String[]{"ID"});

        Assertion.assertEquals(expectedTable, filteredActualTable);
    }
}
