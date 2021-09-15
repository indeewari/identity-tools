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

DROP TABLE IDN_IDENTITY_USER_DATA_TEMP;

DROP TRIGGER totp_sync_insert;

DROP TRIGGER totp_sync_update;

DROP TRIGGER totp_sync_delete;

DROP TABLE IDN_OAUTH2_AUTHORIZATION_CODE_TEMP;

DROP TRIGGER oauth2_code_sync_insert;

DROP TRIGGER oauth2_code_sync_update;

DROP TRIGGER oauth2_code_sync_delete;

DROP TABLE IDN_OAUTH2_ACCESS_TOKEN_TEMP;

DROP TRIGGER token_sync_insert;

DROP TRIGGER token_sync_update;

DROP TRIGGER token_sync_delete;

DROP TABLE IDN_OAUTH2_ACCESS_TOKEN_SCOPE_TEMP;

DROP TRIGGER scope_sync_insert;

DROP TRIGGER scope_sync_update;

DROP TRIGGER scope_sync_delete;
