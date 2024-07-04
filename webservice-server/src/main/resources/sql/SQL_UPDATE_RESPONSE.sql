UPDATE PUBLIC.MOCK_RESPONSE t
SET t.PUBLISH_URL = :publishUrl, t.METHOD = :method, t.CONDITION = :condition, t.RESPONSE_CONTENT = :responseContent, t.UPDATED_AT = :updatedAt
WHERE
    t.ID = :id

