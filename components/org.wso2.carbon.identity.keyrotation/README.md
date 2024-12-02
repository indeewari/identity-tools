# Symmetric Key Rotation Tool

## Why use this tool?
This tool will help to perform key rotation on a symmetric encryption enabled Identity server(IS 5.11.0 and above).

## What this tool gives?
This tool will perform re-encryption of the,

* Identity Database,
    * TOTP secrets
    * OAuth2 authorization codes
    * OAuth2 access and refresh tokens
    * OAuth consumer secrets
    * BPS profile passwords
    * Workflow request


* Registry Database,
    * Super tenant secondary user store files
    * Tenant secondary user store files
    * Event publishers


* Configuration Files,
    * Tenant keystore passwords
    * Policy subscriber password


## How To Run The Tool
Follow the steps below.
   1. Clone the repository [identity-tools](https://github.com/wso2/identity-tools).
     

   2. Build it using maven by running the below command. 

                  mvn clean install

   3. Go to the `identity-tools/components/org.wso2.carbon.identity.keyrotation/target` folder and copy the 
     `keyrotation-tool-<version>-SNAPSHOT.jar` jar file and the `identity-tools/components/org.wso2.carbon.identity.keyrotation/target/lib` folder to a different location[1]. Get the `properties.yaml` 
     file, `keyrotation.sh` file and the `triggers` folder from `identity-tools/components/org.wso2.carbon.identity.keyrotation/src/main/resources` and copy to the same location[1]


   4. Edit the configurations in the `properties.yaml` accordingly.
  

   5. Open a terminal from location[1] and run the `./keyrotation.sh keyrotation-tool-<version>-SNAPSHOT.jar 
     properties.yaml` command.

## Inputs To The Tool
1. **oldSecretKey** : The plain symmetric encryption key used in the existing(old) IS pack.
2. **newSecretKey** : The plain new symmetric encryption key.
3. **ishome** : The absolute path of the copied(new) IS pack.
4. **idnDBUrl** : New IS pack identity database URL.
5. **idnUsername** : New IS pack identity database username.
6. **idnPassword** : Base64 encoded new IS pack identity database password.
7. **regDBUrl** : New IS pack registry database URL.
8. **regUsername** : New IS pack registry database username.
9. **regPassword** : Base64 encoded new IS pack registry database password.
10. **enableDBMigrator** : Enable/disable re-encryption for the identity and registry databases.
11. **enableConfigMigrator** : Enable/disable re-encryption for the configuration files.
12. **enableWorkflowMigrator** : Enable/disable re-encryption for the workflows related secrets.
