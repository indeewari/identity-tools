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
 * This class holds the cipher and initialization vector metadata.
 */
public class CipherInitializationVector {

    private String cipher;
    private String initializationVector;
    private String keyId;

    /**
     * Get for the cipher.
     *
     * @return Cipher The ciphertext.
     */
    public String getCipher() {

        return cipher;
    }

    /**
     * Set for the cipher.
     *
     * @param cipher The ciphertext.
     */
    public void setCipher(String cipher) {

        this.cipher = cipher;
    }

    /**
     * Get for the initialization vector.
     *
     * @return IV The initialization vector.
     */
    public String getInitializationVector() {

        return initializationVector;
    }

    /**
     * Set for the initialization vector.
     *
     * @param initializationVector The initialization vector.
     */
    public void setInitializationVector(String initializationVector) {

        this.initializationVector = initializationVector;
    }

    public String getKeyId() {

        return keyId;
    }

    public void setKeyId(String keyId) {

        this.keyId = keyId;
    }
}
