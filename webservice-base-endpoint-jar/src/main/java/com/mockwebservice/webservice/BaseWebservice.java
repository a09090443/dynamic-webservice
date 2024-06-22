package com.mockwebservice.webservice;

import com.mockwebservice.dao.MockResponseDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseWebservice {
    protected MockResponseDao mockResponseDao;

    @Autowired
    public final void setMockResponseDao(MockResponseDao mockResponseDao) {
        this.mockResponseDao = mockResponseDao;
    }
}
