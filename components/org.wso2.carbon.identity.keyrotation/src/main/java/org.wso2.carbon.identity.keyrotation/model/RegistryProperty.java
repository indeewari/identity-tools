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

package org.wso2.carbon.identity.keyrotation.model;

/**
 * This class holds the registry properties stored in REG_PROPERTY.
 */
public class RegistryProperty {

    private final String regId;
    private final String regName;
    private final String regValue;
    private final String regTenantId;
    private String newRegValue;

    /**
     * RegistryProperty class constructor.
     *
     * @param regId       Registry id field in REG_PROPERTY table.
     * @param regName     Registry name field in REG_PROPERTY table.
     * @param regValue    Registry value field in REG_PROPERTY table.
     * @param regTenantId Registry tenant id field in REG_PROPERTY table.
     */
    public RegistryProperty(String regId, String regName, String regValue, String regTenantId) {

        this.regId = regId;
        this.regName = regName;
        this.regValue = regValue;
        this.regTenantId = regTenantId;
    }

    /**
     * Get for the registry id.
     *
     * @return Registry id.
     */
    public String getRegId() {

        return regId;
    }

    /**
     * Get for the registry name.
     *
     * @return Registry name.
     */
    public String getRegName() {

        return regName;
    }

    /**
     * Get for the registry value.
     *
     * @return Registry value.
     */
    public String getRegValue() {

        return regValue;
    }

    /**
     * Get for the registry tenant id.
     *
     * @return Registry tenant id.
     */
    public String getRegTenantId() {

        return regTenantId;
    }

    public String getNewRegValue() {

        return newRegValue;
    }

    public void setNewRegValue(String newRegValue) {

        this.newRegValue = newRegValue;
    }
}
