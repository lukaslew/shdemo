package com.example.shdemo.service;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatDtdDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseExport {

    public static void main(String[] args) throws Exception {
        Connection jdbcConnection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/workdb", "sa", "");
        IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
        IDataSet dataSet = databaseConnection.createDataSet();

        FlatDtdDataSet.write(dataSet, new FileOutputStream("src/test/resources/dataSet.dtd"));
        FlatXmlDataSet.write(dataSet, new FileOutputStream("src/test/resources/fullData.xml"));
    }

}
