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

package org.wso2.carbon.identity.keyrotation.service;

import org.apache.log4j.Logger;
import org.wso2.carbon.identity.keyrotation.config.model.KeyRotationConfig;
import org.wso2.carbon.identity.keyrotation.util.ConfigFileUtil;
import org.wso2.carbon.identity.keyrotation.util.KeyRotationConstants;
import org.wso2.carbon.identity.keyrotation.util.KeyRotationException;

import java.io.File;
import java.util.List;

import static org.wso2.carbon.identity.keyrotation.util.ConfigFileUtil.getFilePaths;
import static org.wso2.carbon.identity.keyrotation.util.ConfigFileUtil.getFolderPaths;
import static org.wso2.carbon.identity.keyrotation.util.ConfigFileUtil.updateConfigFile;

/**
 * This class holds the config file re-encryption service.
 */
public class ConfigFileKeyRotator {

    private static final Logger log = Logger.getLogger(ConfigFileKeyRotator.class);
    private static final ConfigFileKeyRotator instance = new ConfigFileKeyRotator();

    public static ConfigFileKeyRotator getInstance() {

        return instance;
    }

    /**
     * Re-encryption of the configuration file data.
     *
     * @param keyRotationConfig Configuration data needed to perform the task.
     * @throws KeyRotationException Exception thrown while re-encrypting configuration files.
     */
    public void configFileReEncryptor(KeyRotationConfig keyRotationConfig) throws KeyRotationException {

        log.info("Started re-encrypting configuration file data...");
        reEncryptSuperTenantUserStore(keyRotationConfig);
        log.info("Successfully updated super tenant secondary userstore configuration files: " +
                ConfigFileUtil.updateCount);
        log.info("Failed super tenant secondary userstore configuration files: " + ConfigFileUtil.failedUpdateCount);
        reEncryptTenantUserStore(keyRotationConfig);
        log.info("Successfully updated tenant secondary userstore configuration files: " + ConfigFileUtil.updateCount);
        log.info("Failed tenant secondary userstore configuration files: " + ConfigFileUtil.failedUpdateCount);
        reEncryptEventPublishers(keyRotationConfig);
        log.info("Successfully updated event publisher configuration files: " + ConfigFileUtil.updateCount);
        log.info("Failed event publisher configuration files: " + ConfigFileUtil.failedUpdateCount);
        log.info("Finished re-encrypting configuration file data completed...\n");
    }

    /**
     * Re-encryption of the passwords in configuration files.
     *
     * @param keyRotationConfig Configuration data needed to perform the task.
     * @param newIsHomePath     New Is Home absolute path.
     * @param tenant            The tenant folder.
     * @param config            The property value to identify the corresponding config file.
     * @throws KeyRotationException Exception thrown while updating the configuration file.
     */
    private void getConfigsAndUpdate(KeyRotationConfig keyRotationConfig, String newIsHomePath, String tenant,
                                     String config) throws KeyRotationException {

        String[] paths = null;
        String property = null;
        switch (config) {
            case KeyRotationConstants.SUPER_TENANT:
                paths = new String[]{KeyRotationConstants.REPOSITORY, KeyRotationConstants.DEPLOYMENT,
                        KeyRotationConstants.SERVER, KeyRotationConstants.USERSTORES};
                property = KeyRotationConstants.USERSTORE_PROPERTY;
                break;
            case KeyRotationConstants.TENANT:
                paths = new String[]{KeyRotationConstants.REPOSITORY, KeyRotationConstants.TENANTS, tenant,
                        KeyRotationConstants.USERSTORES};
                property = KeyRotationConstants.USERSTORE_PROPERTY;
                break;
            case KeyRotationConstants.EVENT_PUBLISHER:
                paths = new String[]{KeyRotationConstants.REPOSITORY, KeyRotationConstants.DEPLOYMENT,
                        KeyRotationConstants.SERVER, KeyRotationConstants.EVENT_PUBLISHERS};
                property = KeyRotationConstants.PUBLISHER_PROPERTY;
                break;
        }
        File[] configFiles = getFilePaths(newIsHomePath, paths);
        for (File file : configFiles) {
            updateConfigFile(file, keyRotationConfig, property);
        }
    }

    /**
     * Re-encryption of the super tenant user store passwords in configuration files.
     *
     * @param keyRotationConfig Configuration data needed to perform the task.
     * @throws KeyRotationException Exception thrown while re-encrypting super tenant secondary user store configuration
     *                              files.
     */
    private void reEncryptSuperTenantUserStore(KeyRotationConfig keyRotationConfig) throws KeyRotationException {

        ConfigFileUtil.updateCount = 0;
        ConfigFileUtil.failedUpdateCount = 0;
        log.debug("Started re-encryption of the super tenant secondary user store configuration files...");
        getConfigsAndUpdate(keyRotationConfig, keyRotationConfig.getISHome(), null,
                KeyRotationConstants.SUPER_TENANT);
        log.debug("Finished re-encryption of the super tenant secondary user store configuration files...");
    }

    /**
     * Re-encryption of the tenant user store passwords in configuration files.
     *
     * @param keyRotationConfig Configuration data needed to perform the task.
     * @throws KeyRotationException Exception thrown while re-encrypting tenant secondary user store configuration
     *                              files.
     */
    private void reEncryptTenantUserStore(KeyRotationConfig keyRotationConfig) throws KeyRotationException {

        ConfigFileUtil.updateCount = 0;
        ConfigFileUtil.failedUpdateCount = 0;
        List<String> tenants = getFolderPaths(keyRotationConfig.getISHome());
        log.debug("Started re-encryption of the tenant secondary user store configuration files...");
        for (String tenant : tenants) {
            getConfigsAndUpdate(keyRotationConfig, keyRotationConfig.getISHome(), tenant,
                    KeyRotationConstants.TENANT);
        }
        log.debug("Finished re-encryption of the tenant secondary user store configuration files...");
    }

    /**
     * Re-encryption of the event publisher passwords in configuration files.
     *
     * @param keyRotationConfig Configuration data needed to perform the task.
     * @throws KeyRotationException Exception thrown while re-encrypting event publisher configuration files.
     */
    private void reEncryptEventPublishers(KeyRotationConfig keyRotationConfig) throws KeyRotationException {

        ConfigFileUtil.updateCount = 0;
        ConfigFileUtil.failedUpdateCount = 0;
        log.debug("Started re-encryption of the event publisher configuration files...");
        getConfigsAndUpdate(keyRotationConfig, keyRotationConfig.getISHome(), null,
                KeyRotationConstants.EVENT_PUBLISHER);
        log.debug("Finished re-encryption of the event publisher configuration files...");
    }
}
