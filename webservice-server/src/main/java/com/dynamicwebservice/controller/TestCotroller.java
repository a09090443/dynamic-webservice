package com.dynamicwebservice.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "http://localhost:4200") // 允許來自 http://localhost:4200 的請求
public class TestCotroller {

    @GetMapping("/data")
    public String data() {
        return """
                [
                  {
                    "rowId": "1",
                    "name": "抱枕",
                    "location": "一碼",
                    "price": 10000,
                    "status": "借出",
                    "purchase_date": "2014-02-09",
                    "size": "100 x 100"
                  },
                  {
                    "rowId": "2",
                    "name": "檯燈",
                    "location": "師大",
                    "price": 100,
                    "status": "空閒",
                    "purchase_date": "2024-06-12",
                    "size": "30 x 50"
                  },
                  {
                    "rowId": "3",
                    "name": "風扇",
                    "location": "師大",
                    "price": 100,
                    "status": "空閒",
                    "purchase_date": "2020-04-01",
                    "size": "10 x 50"
                  },
                  {
                    "rowId": "4",
                    "name": "掃把",
                    "location": "一碼",
                    "price": 50,
                    "status": "空閒",
                    "purchase_date": "2022-01-01",
                    "size": "100"
                  },
                  {
                    "rowId": "5",
                    "name": "Samsung 24吋螢幕",
                    "location": "一碼",
                    "price": 2000,
                    "status": "空閒",
                    "purchase_date": "2020-10-08",
                    "size": "500 x 250"
                  },
                  {
                    "rowId": "6",
                    "name": "Samsung 32吋螢幕",
                    "location": "一碼",
                    "price": 5500,
                    "status": "空閒",
                    "purchase_date": "2020-10-08",
                    "size": "500 x 250"
                  },
                  {
                    "rowId": "7",
                    "name": "水桶",
                    "location": "一碼",
                    "price": 100,
                    "status": "空閒",
                    "purchase_date": "2021-11-11",
                    "size": "50公升"
                  },
                  {
                    "rowId": "8",
                    "name": "投影機",
                    "location": "一碼",
                    "price": 11000,
                    "status": "空閒",
                    "purchase_date": "2020-02-21",
                    "size": ""
                  },
                  {
                    "rowId": "9",
                    "name": "MacBook Pro 13吋",
                    "location": "一碼",
                    "price": 45000,
                    "status": "空閒",
                    "purchase_date": "2021-12-11",
                    "size": "13吋"
                  },
                  {
                    "rowId": "10",
                    "name": "電鑽",
                    "location": "一碼",
                    "price": 5000,
                    "status": "空閒",
                    "purchase_date": "2024-01-01",
                    "size": ""
                  },
                  {
                    "rowId": "11",
                    "name": "電源延長線",
                    "location": "一碼",
                    "price": 250,
                    "status": " 空閒",
                    "purchase_date": "2023-08-13",
                    "size": ""
                  }
                ]
                """;
    }
}
