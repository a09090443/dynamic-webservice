UPDATE MOCK_RESPONSE t
SET t.PUBLISH_URL = :newPublishUrl, t.UPDATED_AT = :updatedAt
WHERE
    t.PUBLISH_URL = :oriPublishUrl

