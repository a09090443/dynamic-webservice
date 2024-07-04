SELECT e.ID          AS id,
       e.PUBLISH_URL AS publishUrl,
       e.BEAN_NAME   AS beanName,
       e.CLASS_PATH  AS classPath,
       e.IS_ACTIVE   AS isActive,
       jf.ID         AS jarFileId,
       jf.NAME       AS jarFileName,
       jf.STATUS     AS fileStatus
FROM ENDPOINT e
         INNER JOIN JAR_FILE jf ON e.JAR_FILE_ID = jf.ID
WHERE 1 = 1
    ${CONDITIONS}
