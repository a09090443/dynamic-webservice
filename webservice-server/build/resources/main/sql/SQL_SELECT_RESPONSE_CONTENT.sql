SELECT
    mr.RESPONSE_CONTENT AS content
FROM
	MOCK_RESPONSE mr
WHERE
    mr.PUBLISH_URL = :publishUrl
  AND mr.METHOD = :method
  AND mr.CONDITION = :condition
  AND mr.IS_ACTIVE = TRUE
