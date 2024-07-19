UPDATE MOCK_RESPONSE t
SET t.PUBLISH_URI = :newPublishUri, t.UPDATED_AT = :updatedAt
WHERE
    t.PUBLISH_URI = :oriPublishUri

