UPDATE PUBLIC.MOCK_RESPONSE t
SET t.PUBLISH_URI = :publishUri, t.METHOD = :method, t.CONDITION = :condition, t.RESPONSE_CONTENT = :responseContent, t.UPDATED_AT = :updatedAt
WHERE
    t.ID = :id

