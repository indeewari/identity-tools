/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.keyrotation.dao;

import org.apache.log4j.Logger;
import org.wso2.carbon.identity.keyrotation.config.model.KeyRotationConfig;
import org.wso2.carbon.identity.keyrotation.util.KeyRotationException;
import org.wso2.carbon.identity.workflow.mgt.dto.WorkflowRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds implementations needed to re-encrypt the WorkFlow data in DB.
 */
public class WorkFlowDAO {

    private static final Logger log = Logger.getLogger(WorkFlowDAO.class);
    private static final WorkFlowDAO instance = new WorkFlowDAO();
    public static int updateCount = 0;
    public static int failedUpdateCount = 0;

    public WorkFlowDAO() {

    }

    public static WorkFlowDAO getInstance() {

        return instance;
    }

    /**
     * To retrieve the list of data in WF_REQUEST as chunks.
     *
     * @param startIndex        The start index of the data chunk.
     * @param keyRotationConfig Configuration data needed to perform the task.
     * @return List comprising of the records in the table.
     * @throws KeyRotationException Exception thrown while retrieving data from WF_REQUEST.
     */
    public List<WorkflowRequest> getWFRequestChunks(int startIndex, KeyRotationConfig keyRotationConfig) throws
            KeyRotationException {

        List<WorkflowRequest> wfRequestList = new ArrayList<>();
        String query = DBConstants.GET_WF_REQUEST;
        int firstIndex = startIndex;
        int secIndex = keyRotationConfig.getChunkSize();
        try (Connection connection = DriverManager
                .getConnection(keyRotationConfig.getIdnDBUrl(), keyRotationConfig.getIdnUsername(),
                        keyRotationConfig.getIdnPassword())) {
            connection.setAutoCommit(false);
            if (connection.getMetaData().getDriverName().contains(DBConstants.POSTGRESQL)) {
                query = DBConstants.GET_WF_REQUEST_POSTGRE;
                firstIndex = keyRotationConfig.getChunkSize();
                secIndex = startIndex;
            } else if (connection.getMetaData().getDriverName().contains(DBConstants.MSSQL) ||
                    connection.getMetaData().getDriverName().contains(DBConstants.ORACLE)) {
                query = DBConstants.GET_WF_REQUEST_OTHER;
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, firstIndex);
                preparedStatement.setInt(2, secIndex);
                ResultSet resultSet = preparedStatement.executeQuery();
                connection.commit();
                while (resultSet.next()) {
                    byte[] requestBytes = resultSet.getBytes(DBConstants.REQUEST);
                    WorkflowRequest wfRequest = deserializeWFRequest(requestBytes);
                    wfRequestList.add(wfRequest);
                }
            } catch (SQLException | IOException | ClassNotFoundException e) {
                connection.rollback();
                log.error("Error while retrieving requests from WF_REQUEST.", e);
            }
        } catch (SQLException e) {
            throw new KeyRotationException("Error while connecting to new identity DB.", e);
        }
        return wfRequestList;
    }

    /**
     * To reEncrypt the requests in WF_REQUEST using the new key.
     *
     * @param updateWfRequestList The list containing records that should be re-encrypted.
     * @param keyRotationConfig   Configuration data needed to perform the task.
     * @throws KeyRotationException Exception thrown while updating data from WF_REQUEST.
     */
    public void updateWFRequestChunks(List<WorkflowRequest> updateWfRequestList, KeyRotationConfig keyRotationConfig)
            throws KeyRotationException {

        try (Connection connection = DriverManager
                .getConnection(keyRotationConfig.getIdnDBUrl(), keyRotationConfig.getIdnUsername(),
                        keyRotationConfig.getIdnPassword())) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.UPDATE_WF_REQUEST)) {
                for (WorkflowRequest wfRequest : updateWfRequestList) {
                    preparedStatement.setBytes(1, serializeWFRequest(wfRequest));
                    preparedStatement.setString(2, wfRequest.getUuid());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
                connection.commit();
                updateCount += updateWfRequestList.size();
            } catch (SQLException | IOException e) {
                connection.rollback();
                log.error("Error while updating requests in WF_REQUEST, trying the chunk row by row again. ", e);
                retryOnRequestUpdate(updateWfRequestList, connection);
            }
        } catch (SQLException e) {
            throw new KeyRotationException("Error while connecting to new identity DB.", e);
        }
    }

    /**
     * To retry upon a failure in updating request chunk in WF_REQUEST.
     *
     * @param updateWfRequestList The list containing records that should be re-encrypted.
     * @param connection          Connection with the new identity DB.
     * @throws KeyRotationException Exception thrown while accessing new identity DB data.
     */
    private void retryOnRequestUpdate(List<WorkflowRequest> updateWfRequestList, Connection connection)
            throws KeyRotationException {

        WorkflowRequest faulty = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.UPDATE_WF_REQUEST)) {
            for (WorkflowRequest wfRequest : updateWfRequestList) {
                try {
                    faulty = wfRequest;
                    preparedStatement.setBytes(1, serializeWFRequest(wfRequest));
                    preparedStatement.setString(2, wfRequest.getUuid());
                    preparedStatement.executeUpdate();
                    connection.commit();
                    updateCount++;
                } catch (SQLException | IOException err) {
                    connection.rollback();
                    log.error("Error while updating requests in WF_REQUEST of record with uuid: " +
                            faulty.getUuid() + " ," + err);
                    failedUpdateCount++;
                }
            }
        } catch (SQLException e) {
            throw new KeyRotationException("Error while accessing new identity DB.", e);
        }
    }

    /**
     * To convert the stored byte stream to a WorkFlowRequest object.
     *
     * @param serializedData The byte stream.
     * @return WorkFlowRequest object.
     * @throws IOException            Exception thrown during I/O operations.
     * @throws ClassNotFoundException Exception thrown while loading the class.
     */
    private WorkflowRequest deserializeWFRequest(byte[] serializedData) throws IOException,
            ClassNotFoundException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedData);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object objectRead = objectInputStream.readObject();
        return (WorkflowRequest) objectRead;
    }

    /**
     * To convert the passed WorkFlowRequest object to a byte stream.
     *
     * @param wfRequest WorkFlowRequest object.
     * @return The byte stream.
     * @throws IOException Exception thrown during I/O operations.
     */
    private byte[] serializeWFRequest(WorkflowRequest wfRequest) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(wfRequest);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }
}
