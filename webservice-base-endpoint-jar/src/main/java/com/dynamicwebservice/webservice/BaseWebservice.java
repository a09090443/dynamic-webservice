package com.dynamicwebservice.webservice;

import com.dynamicwebservice.dao.MockResponseDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseWebservice {
    protected MockResponseDao mockResponseDao;

    @Autowired
    public final void setMockResponseDao(MockResponseDao mockResponseDao) {
        this.mockResponseDao = mockResponseDao;
    }
}
