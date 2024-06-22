# ENDPOINT

- 開發環境
  - JAVA 17
- 說明
  - 本專案為 webservice 可動態呼叫所建立的 jar 檔, 建立好 webservice 所需的內容後, 需註冊到 webservice server 上
- 程式架構
  - com.scb.dto : 為 webservice request, response 的資料結構, 需使用 
    - @XmlElement webservice 設定欄位名稱, 為轉換成 java bean
    - @JacksonXmlProperty 設定欄位名稱及 namespace, 為程式將 xml 轉換成 java bean, 其中 namespace 重要的設定內容在 package-info.java
  - com.scb.webservice : 為 webservice 的實作內容, 需使用
    - @WebService webservice 設定, name 為 webservice 名稱, serviceName 為 webservice 服務名稱, targetNamespace 為 webservice namespace
    - @WebMethod webservice 方法設定, operationName 為 webservice 方法名稱
    - @WebParam webservice 方法參數設定, name 為 webservice 方法參數名稱
    - @WebResult webservice 方法回傳設定, name 為 webservice 方法回傳名稱
- 使用 wsdl 文件產出 java pojo 方法
    - 下載 [apache cxf](https://cxf.apache.org/download.html)
    - 輸入指令 ${cxf目錄}/bin/wsdl2java -d 輸出目錄 -client WSDL 檔案 或 URL
- 打包指令
  - mvn clean package
- 完成打包後流程:
  - 將 postman 目錄中的 Mock.postman_collection.json 匯入至 postman 中
  - 依序執行 postman 中的 request
    1. Upload_jar_file : 上傳 jar 檔, 會回傳新的 jar 檔名稱
    2. Register : 註冊 jar 檔內的 webservice
    3. Add_new_mock_response : 新增 response 的回傳條件, 可根據 request 的內容自訂 condition
    4. CCMS_getCardListing : 呼叫 webservice, 會回傳自訂的 response
