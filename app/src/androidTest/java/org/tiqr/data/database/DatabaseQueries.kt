package org.tiqr.data.database

object DatabaseQueries {
    const val V8_FIRST_PROVIDER =
        """INSERT INTO identityprovider(_id,displayName,identifier,authenticationUrl,ocraSuite,infoUrl,logo)
VALUES (0, "provider 0","v8 provider identifier", "url","ocra","infoUrl", "logoimage");"""
    const val V8_SECOND_PROVIDER =
        """INSERT INTO identityprovider(_id,displayName,identifier,authenticationUrl,ocraSuite,infoUrl,logo)
VALUES (1, "provider 1","v8 provider identifier", "url","ocra","infoUrl", "logoimage");"""

    const val V8_IDENTITY1_ON_FIRST_PROVIDER =
        """INSERT INTO identity(displayName,identifier,identityprovider,blocked,sortIndex,showFingerPrintUpgrade,useFingerPrint)
VALUES("v8 first identity from provider 0", "v8 identity identifier",0,0,1,1,0);"""

    const val V8_IDENTITY2_ON_FIRST_PROVIDER =
        """INSERT INTO identity(displayName,identifier,identityprovider,blocked,sortIndex,showFingerPrintUpgrade,useFingerPrint)
VALUES ("v8 second identity from provider 0", "v8 identity identifier",0,0,1,1,0);"""

    const val V8_IDENTITY1_ON_SECOND_PROVIDER =
        """INSERT INTO identity(displayName,identifier,identityprovider,blocked,sortIndex,showFingerPrintUpgrade,useFingerPrint)
VALUES ("v8 first identity from provider 1", "v8 identity identifier",1,0,1,1,0);"""

    const val V8_IDENTITY2_ON_SECOND_PROVIDER =
        """INSERT INTO identity(displayName,identifier,identityprovider,blocked,sortIndex,showFingerPrintUpgrade,useFingerPrint)
VALUES("v8 second identity from provider 1", "v8 identity identifier",1,0,1,1,0);"""

    const val V9_FIRST_PROVIDER = V8_FIRST_PROVIDER
    const val V9_IDENTITY1_ON_FIRST_PROVIDER =
        """INSERT INTO identity(displayName,identifier,identityprovider,blocked,sortIndex,biometricInUse,biometricOfferUpgrade)
VALUES("v9 first identity from provider 0", "v9 identity identifier",0,0,1,1,0);"""
    const val V9_IDENTITY2_ON_FIRST_PROVIDER =
        """INSERT INTO identity(displayName,identifier,identityprovider,blocked,sortIndex,biometricInUse,biometricOfferUpgrade)
VALUES("v9 first identity from provider 0", "v9 identity identifier",0,0,1,1,0);"""
}