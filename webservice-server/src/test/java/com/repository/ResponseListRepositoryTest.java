package com.repository;

import com.dynamicwebservice.dao.MockResponseDao;
import com.dynamicwebservice.repository.MockResponseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tw.com.webcomm.base.TestBase;

class ResponseListRepositoryTest extends TestBase {

    @Autowired
    public MockResponseRepository responseListRepository;

    @Autowired
    public MockResponseDao mockResponseDao;

    @Test
    void testFindByCondition() {
        String content = mockResponseDao.findByPrimaryKey("ccms", "getCardListing", requestJson(), String.class);
        Assertions.assertEquals(mockResponseXml(), content);
    }

    //    @Test
//    void testInsert() {
//        MockResponseEntity responseListEntity = new MockResponseEntity();
//        responseListEntity.setPublishUrl("ccms");
//        responseListEntity.setMethod("getCardListing");
//        responseListEntity.setCondition(requestJson());
//        responseListEntity.setResponseContent(mockResponseXml());
//        responseListRepository.save(responseListEntity);
//    }

    private String requestJson() {
        return """
                {"header":{"messageDetails":{"messageVersion":1.0,"messageType":{"typeName":"CardProfile","subType":{"subTypeName":"getCardListing","subTypeScheme":null}},"multiMessage":null},"originationDetails":{"messageSender":{"messageSender":{"value":"CBT","systemScheme":null},"senderDomain":{"domainName":{"value":"CoreBanking","domainNameScheme":null},"subDomainName":{"subDomainType":"CBT","subdomainNameScheme":null}},"countryCode":"TW"},"messageTimestamp":1704265615304,"initiatedTimestamp":1704265615304,"trackingId":"e536442e-3248-467d-8c55-50fece56d748","correlationID":null,"conversationID":null,"customSearchID":null,"batchID":null,"serviceBusID":null,"validFrom":null,"validTo":null,"timeToLive":null,"priority":null,"checksum":null,"compressionAlgorithm":null,"encoding":null,"possibleDuplicate":false},"captureSystem":"CBT","process":{"processName":"CardProfile","eventType":"getCardListing","lifecycleState":"","workflowState":"","action":null},"metadata":{"tag":[{"key":{"value":"targetRegion","keyNameScheme":null},"value":"S1P","anyValue":null}],"metadataScheme":null},"messageHistory":[],"exceptions":null},"getCardListingReqPayload":{"payloadFormat":"XML","payloadVersion":"1.0","getCardListingReq":{"getCardListingRq":{"docCtrlIn":null,"recCtrlOut":null,"acctSel":{"acctKeys":null,"startPageNum":null,"scbentityType":"05","scborgNum":3,"scbcustNum":4509302000000016,"scbrelId":null,"scbcardList":null},"partyKeys":null,"scbhdr":{"scbentityType":"05","scbfuncCode":null,"scbrecType":null,"scbsourceFlag":"CBT"},"scbcardHeader":null,"scbcustKeys":null,"scbuserDefinedRq":null}}}}
                """;
    }

    private String mockResponseXml() {
        return """
                <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                  <SOAP-ENV:Header/>
                  <SOAP-ENV:Body>
                     <ser-root:getCardListingResponse xmlns:ser-root="http://www.sc.com/SCBML-1">
                        <getCardListingResponse>
                           <ns:header xmlns:ns="http://www.sc.com/SCBML-1">
                              <ns:messageDetails>
                                 <ns:messageVersion>1.0</ns:messageVersion>
                                 <ns:messageType>
                                    <ns:typeName>CardProfile</ns:typeName>
                                    <ns:subType>
                                       <ns:subTypeName>getCardListing</ns:subTypeName>
                                    </ns:subType>
                                 </ns:messageType>
                              </ns:messageDetails>
                              <ns:originationDetails>
                                 <ns:messageSender>
                                    <ns:messageSender>CBT</ns:messageSender>
                                    <ns:senderDomain>
                                       <ns:domainName>CoreBanking</ns:domainName>
                                       <ns:subDomainName>
                                          <ns:subDomainType>CBT</ns:subDomainType>
                                       </ns:subDomainName>
                                    </ns:senderDomain>
                                    <ns:countryCode>TW</ns:countryCode>
                                 </ns:messageSender>
                                 <ns:messageTimestamp>2024-01-03T07:06:55.304Z</ns:messageTimestamp>
                                 <ns:initiatedTimestamp>2024-01-03T07:06:55.304Z</ns:initiatedTimestamp>
                                 <ns:trackingId>e536442e-3248-467d-8c55-50fece56d748</ns:trackingId>
                                 <ns:customSearchID>414d5120484b45434d53533120202020444adf65f1b86e24</ns:customSearchID>
                                 <ns:possibleDuplicate/>
                              </ns:originationDetails>
                              <ns:captureSystem>CBT</ns:captureSystem>
                              <ns:process>
                                 <ns:processName>CardProfile</ns:processName>
                                 <ns:eventType>getCardListing</ns:eventType>
                                 <ns:lifecycleState/>
                                 <ns:workflowState/>
                              </ns:process>
                              <ns:metadata>
                                 <ns:tag>
                                    <ns:key>targetRegion</ns:key>
                                    <ns:value>S1P</ns:value>
                                 </ns:tag>
                              </ns:metadata>
                           </ns:header>
                           <crdprf:getCardListingResPayload xmlns:crdprf="http://www.sc.com/coreBanking/creditCard/v1/creditCardProfile">
                              <ns:payloadFormat xmlns:ns="http://www.sc.com/SCBML-1">XML</ns:payloadFormat>
                              <ns:payloadVersion xmlns:ns="http://www.sc.com/SCBML-1">1.0</ns:payloadVersion>
                              <crdprf:getCardListingRes>
                                 <crdprf:getCardListingRs>
                                    <crdprf:AcctRec>
                                       <crdprf:AcctInfo>
                                          <crdprf:CurCode>
                                             <crdprf:CurCodeValue>901</crdprf:CurCodeValue>
                                          </crdprf:CurCode>
                                          <crdprf:AcctBal>
                                             <crdprf:BalType>
                                                <crdprf:BalTypeValues>CustomerAvailableCash</crdprf:BalTypeValues>
                                             </crdprf:BalType>
                                             <crdprf:CurAmt>
                                                <crdprf:Amt>20000</crdprf:Amt>
                                             </crdprf:CurAmt>
                                          </crdprf:AcctBal>
                                          <crdprf:AcctBal>
                                             <crdprf:BalType>
                                                <crdprf:BalTypeValues>CustomerCreditLimit</crdprf:BalTypeValues>
                                             </crdprf:BalType>
                                             <crdprf:CurAmt>
                                                <crdprf:Amt>20000</crdprf:Amt>
                                             </crdprf:CurAmt>
                                          </crdprf:AcctBal>
                                          <crdprf:AcctBal>
                                             <crdprf:BalType>
                                                <crdprf:BalTypeValues>CustomerPermCreditLimit</crdprf:BalTypeValues>
                                             </crdprf:BalType>
                                             <crdprf:CurAmt>
                                                <crdprf:Amt>20000</crdprf:Amt>
                                             </crdprf:CurAmt>
                                          </crdprf:AcctBal>
                                          <crdprf:AcctBal>
                                             <crdprf:BalType>
                                                <crdprf:BalTypeValues>CustomerAvailableCredit</crdprf:BalTypeValues>
                                             </crdprf:BalType>
                                             <crdprf:CurAmt>
                                                <crdprf:Amt>20000</crdprf:Amt>
                                             </crdprf:CurAmt>
                                          </crdprf:AcctBal>
                                          <crdprf:AcctBal>
                                             <crdprf:BalType>
                                                <crdprf:BalTypeValues>CustomerNumBalPercentage</crdprf:BalTypeValues>
                                             </crdprf:BalType>
                                             <crdprf:SCB_Percentage>0</crdprf:SCB_Percentage>
                                          </crdprf:AcctBal>
                                          <crdprf:AcctPeriodData>
                                             <crdprf:AcctAmtType>LastPayment</crdprf:AcctAmtType>
                                             <crdprf:Amt>0.00</crdprf:Amt>
                                          </crdprf:AcctPeriodData>
                                          <crdprf:AcctPeriodData>
                                             <crdprf:AcctAmtType>RwdReg</crdprf:AcctAmtType>
                                             <crdprf:Count>0.00</crdprf:Count>
                                          </crdprf:AcctPeriodData>
                                          <crdprf:AcctPeriodData>
                                             <crdprf:AcctAmtType>TotalStmtBal</crdprf:AcctAmtType>
                                             <crdprf:Amt>0.00</crdprf:Amt>
                                          </crdprf:AcctPeriodData>
                                          <crdprf:AcctPeriodData>
                                             <crdprf:AcctAmtType>OpenOutstanding</crdprf:AcctAmtType>
                                             <crdprf:Amt>0.00</crdprf:Amt>
                                          </crdprf:AcctPeriodData>
                                          <crdprf:AcctPeriodData>
                                             <crdprf:AcctAmtType>TotalMinAmt</crdprf:AcctAmtType>
                                             <crdprf:Amt>0.00</crdprf:Amt>
                                          </crdprf:AcctPeriodData>
                                          <crdprf:IntRateData>
                                             <crdprf:RateMatrixTier>
                                                <crdprf:Tier>1</crdprf:Tier>
                                                <crdprf:Rate>0.1498000</crdprf:Rate>
                                             </crdprf:RateMatrixTier>
                                             <crdprf:Desc>RetailInterest</crdprf:Desc>
                                          </crdprf:IntRateData>
                                          <crdprf:IntRateData>
                                             <crdprf:RateMatrixTier>
                                                <crdprf:Tier>1</crdprf:Tier>
                                                <crdprf:Rate>0.1498000</crdprf:Rate>
                                             </crdprf:RateMatrixTier>
                                             <crdprf:Desc>CashInterest</crdprf:Desc>
                                          </crdprf:IntRateData>
                                          <crdprf:SCBAcctInfo>
                                             <crdprf:SCBCardInfo>
                                                <crdprf:SCB_CardNum>5523720280001918</crdprf:SCB_CardNum>
                                                <crdprf:SCB_OpenDt>2023-01-01</crdprf:SCB_OpenDt>
                                                <crdprf:SCB_PostingFlag>0</crdprf:SCB_PostingFlag>
                                                <crdprf:SCB_StmtchangeFlag>0</crdprf:SCB_StmtchangeFlag>
                                                <crdprf:SCB_CardBillingCycle>7</crdprf:SCB_CardBillingCycle>
                                                <crdprf:SCB_StatementCycledue>7</crdprf:SCB_StatementCycledue>
                                                <crdprf:SCB_Relationship>SA</crdprf:SCB_Relationship>
                                                <crdprf:SCB_ACHRTnum>8240000</crdprf:SCB_ACHRTnum>
                                                <crdprf:SCB_CardCat>P</crdprf:SCB_CardCat>
                                                <crdprf:SCB_PastdueStatus>N</crdprf:SCB_PastdueStatus>
                                                <crdprf:SCB_CardStatus>1</crdprf:SCB_CardStatus>
                                                <crdprf:SCB_AcknowledgeStatus>N</crdprf:SCB_AcknowledgeStatus>
                                                <crdprf:SCB_BlockCode>A</crdprf:SCB_BlockCode>
                                                <crdprf:SCB_ExpDt>1224</crdprf:SCB_ExpDt>
                                                <crdprf:SCB_Rwdpts>0.00</crdprf:SCB_Rwdpts>
                                                <crdprf:CardBal>
                                                   <crdprf:BalType>
                                                      <crdprf:BalTypeValues>CreditLimit</crdprf:BalTypeValues>
                                                   </crdprf:BalType>
                                                   <crdprf:CurAmt>
                                                      <crdprf:Amt>20000</crdprf:Amt>
                                                   </crdprf:CurAmt>
                                                </crdprf:CardBal>
                                                <crdprf:CardBal>
                                                   <crdprf:BalType>
                                                      <crdprf:BalTypeValues>CashAdvanceLimit</crdprf:BalTypeValues>
                                                   </crdprf:BalType>
                                                   <crdprf:CurAmt>
                                                      <crdprf:Amt>0</crdprf:Amt>
                                                   </crdprf:CurAmt>
                                                </crdprf:CardBal>
                                                <crdprf:CardBal>
                                                   <crdprf:BalType>
                                                      <crdprf:BalTypeValues>CardPermanentCredit Limit</crdprf:BalTypeValues>
                                                   </crdprf:BalType>
                                                   <crdprf:CurAmt>
                                                      <crdprf:Amt>20000</crdprf:Amt>
                                                   </crdprf:CurAmt>
                                                </crdprf:CardBal>
                                                <crdprf:CardBal>
                                                   <crdprf:BalType>
                                                      <crdprf:BalTypeValues>ProbePermanentCreditLimit</crdprf:BalTypeValues>
                                                   </crdprf:BalType>
                                                   <crdprf:CurAmt>
                                                      <crdprf:Amt>0</crdprf:Amt>
                                                   </crdprf:CurAmt>
                                                </crdprf:CardBal>
                                                <crdprf:CardBal>
                                                   <crdprf:BalType>
                                                      <crdprf:BalTypeValues>CardAvailableCredit</crdprf:BalTypeValues>
                                                   </crdprf:BalType>
                                                   <crdprf:CurAmt>
                                                      <crdprf:Amt>0</crdprf:Amt>
                                                   </crdprf:CurAmt>
                                                </crdprf:CardBal>
                                                <crdprf:CardBal>
                                                   <crdprf:BalType>
                                                      <crdprf:BalTypeValues>CashAdvanceAvailableLimit</crdprf:BalTypeValues>
                                                   </crdprf:BalType>
                                                   <crdprf:CurAmt>
                                                      <crdprf:Amt>0</crdprf:Amt>
                                                   </crdprf:CurAmt>
                                                </crdprf:CardBal>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>CurrentBalance</crdprf:CardAmtType>
                                                   <crdprf:CardPeriodType>CTD</crdprf:CardPeriodType>
                                                   <crdprf:Amt>20000.00</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>PendAuthAmt</crdprf:CardAmtType>
                                                   <crdprf:CardPeriodType>CTD</crdprf:CardPeriodType>
                                                   <crdprf:Amt>0</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>Outstanding AuthAmt</crdprf:CardAmtType>
                                                   <crdprf:CardPeriodType>CTD</crdprf:CardPeriodType>
                                                   <crdprf:Amt>0.00</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>StatementDate</crdprf:CardAmtType>
                                                   <crdprf:EffDt>2023-02-09</crdprf:EffDt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>PaymentDueDate</crdprf:CardAmtType>
                                                   <crdprf:EffDt>2023-03-02</crdprf:EffDt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>StatementBal</crdprf:CardAmtType>
                                                   <crdprf:Amt>20000.00</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>MinimumAmount</crdprf:CardAmtType>
                                                   <crdprf:Amt>2000.00</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>LastPayment</crdprf:CardAmtType>
                                                   <crdprf:Amt>0.00</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:CardPeriodData>
                                                   <crdprf:CardAmtType>LastMinimumAmount</crdprf:CardAmtType>
                                                   <crdprf:Amt>0.00</crdprf:Amt>
                                                </crdprf:CardPeriodData>
                                                <crdprf:SCBCardKeys>
                                                   <crdprf:SCB_CardOrg>3</crdprf:SCB_CardOrg>
                                                   <crdprf:SCB_CardType>028</crdprf:SCB_CardType>
                                                </crdprf:SCBCardKeys>
                                             </crdprf:SCBCardInfo>
                                          </crdprf:SCBAcctInfo>
                                          <crdprf:BBAN>111004025647</crdprf:BBAN>
                                          <crdprf:SCB_ShortName>WANGB REDO</crdprf:SCB_ShortName>
                                          <crdprf:SCB_CustBillingCycle>9</crdprf:SCB_CustBillingCycle>
                                          <crdprf:SCB_CashLine>20000</crdprf:SCB_CashLine>
                                          <crdprf:SCB_CustId>K100632913</crdprf:SCB_CustId>
                                       </crdprf:AcctInfo>
                                       <crdprf:AcctStatus>
                                          <crdprf:AcctStatusCode>1</crdprf:AcctStatusCode>
                                       </crdprf:AcctStatus>
                                       <crdprf:SCBCustKeys>
                                          <crdprf:SCB_CustOrg>3</crdprf:SCB_CustOrg>
                                          <crdprf:SCB_CustNum>111006329130</crdprf:SCB_CustNum>
                                       </crdprf:SCBCustKeys>
                                    </crdprf:AcctRec>
                                    <crdprf:AcctRec>
                                       <crdprf:AcctInfo>
                                          <crdprf:SCBAcctInfo>
                                             <crdprf:SCBCardInfo>
                                                <crdprf:SCB_CardNum>3456127891238761</crdprf:SCB_CardNum>
                                                <crdprf:SCB_OpenDt>2024-01-01</crdprf:SCB_OpenDt>
                                                <crdprf:SCB_PostingFlag>0</crdprf:SCB_PostingFlag>
                                                <crdprf:SCB_StmtchangeFlag>0</crdprf:SCB_StmtchangeFlag>
                                                <crdprf:SCB_CardBillingCycle>7</crdprf:SCB_CardBillingCycle>
                                                <crdprf:SCB_StatementCycledue>7</crdprf:SCB_StatementCycledue>
                                                <crdprf:SCB_Relationship>SA</crdprf:SCB_Relationship>
                                                <crdprf:SCB_ACHRTnum>8240000</crdprf:SCB_ACHRTnum>
                                                <crdprf:SCB_CardCat>P</crdprf:SCB_CardCat>
                                                <crdprf:SCB_PastdueStatus>N</crdprf:SCB_PastdueStatus>
                                                <crdprf:SCB_CardStatus>1</crdprf:SCB_CardStatus>
                                                <crdprf:SCB_AcknowledgeStatus>N</crdprf:SCB_AcknowledgeStatus>
                                                <crdprf:SCB_BlockCode>A</crdprf:SCB_BlockCode>
                                                <crdprf:SCB_ExpDt>0928</crdprf:SCB_ExpDt>
                                                <crdprf:SCB_Rwdpts>0.00</crdprf:SCB_Rwdpts>
                                                <crdprf:SCBCardKeys>
                                                   <crdprf:SCB_CardOrg>3</crdprf:SCB_CardOrg>
                                                   <crdprf:SCB_CardType>412</crdprf:SCB_CardType>
                                                </crdprf:SCBCardKeys>
                                             </crdprf:SCBCardInfo>
                                          </crdprf:SCBAcctInfo>
                                          <crdprf:BBAN>111004025647</crdprf:BBAN>
                                          <crdprf:SCB_ShortName>WANGB REDO</crdprf:SCB_ShortName>
                                          <crdprf:SCB_CustBillingCycle>9</crdprf:SCB_CustBillingCycle>
                                          <crdprf:SCB_CashLine>20000</crdprf:SCB_CashLine>
                                          <crdprf:SCB_CustId>K100632913</crdprf:SCB_CustId>
                                       </crdprf:AcctInfo>
                                       <crdprf:SCBCustKeys>
                                          <crdprf:SCB_CustOrg>3</crdprf:SCB_CustOrg>
                                          <crdprf:SCB_CustNum>111006329130</crdprf:SCB_CustNum>
                                       </crdprf:SCBCustKeys>
                                    </crdprf:AcctRec>
                                    <crdprf:AcctRec>
                                       <crdprf:AcctInfo>
                                          <crdprf:SCBAcctInfo>
                                             <crdprf:SCBCardInfo>
                                                <crdprf:SCB_CardNum>4321987614563721</crdprf:SCB_CardNum>
                                                <crdprf:SCB_OpenDt>2024-04-01</crdprf:SCB_OpenDt>
                                                <crdprf:SCB_PostingFlag>0</crdprf:SCB_PostingFlag>
                                                <crdprf:SCB_StmtchangeFlag>0</crdprf:SCB_StmtchangeFlag>
                                                <crdprf:SCB_CardBillingCycle>7</crdprf:SCB_CardBillingCycle>
                                                <crdprf:SCB_StatementCycledue>7</crdprf:SCB_StatementCycledue>
                                                <crdprf:SCB_Relationship>SA</crdprf:SCB_Relationship>
                                                <crdprf:SCB_ACHRTnum>8240000</crdprf:SCB_ACHRTnum>
                                                <crdprf:SCB_CardCat>P</crdprf:SCB_CardCat>
                                                <crdprf:SCB_PastdueStatus>N</crdprf:SCB_PastdueStatus>
                                                <crdprf:SCB_CardStatus>1</crdprf:SCB_CardStatus>
                                                <crdprf:SCB_AcknowledgeStatus>N</crdprf:SCB_AcknowledgeStatus>
                                                <crdprf:SCB_BlockCode>A</crdprf:SCB_BlockCode>
                                                <crdprf:SCB_ExpDt>0726</crdprf:SCB_ExpDt>
                                                <crdprf:SCB_Rwdpts>0.00</crdprf:SCB_Rwdpts>
                                                <crdprf:SCBCardKeys>
                                                   <crdprf:SCB_CardOrg>3</crdprf:SCB_CardOrg>
                                                   <crdprf:SCB_CardType>028</crdprf:SCB_CardType>
                                                </crdprf:SCBCardKeys>
                                             </crdprf:SCBCardInfo>
                                          </crdprf:SCBAcctInfo>
                                          <crdprf:BBAN>111004025647</crdprf:BBAN>
                                          <crdprf:SCB_ShortName>WANGB REDO</crdprf:SCB_ShortName>
                                          <crdprf:SCB_CustBillingCycle>9</crdprf:SCB_CustBillingCycle>
                                          <crdprf:SCB_CashLine>20000</crdprf:SCB_CashLine>
                                          <crdprf:SCB_CustId>K100632913</crdprf:SCB_CustId>
                                       </crdprf:AcctInfo>
                                       <crdprf:SCBCustKeys>
                                          <crdprf:SCB_CustOrg>3</crdprf:SCB_CustOrg>
                                          <crdprf:SCB_CustNum>111006329130</crdprf:SCB_CustNum>
                                       </crdprf:SCBCustKeys>
                                    </crdprf:AcctRec>
                                    <crdprf:SCB_RespCode>00000</crdprf:SCB_RespCode>
                                    <crdprf:SCB_RespDesc>NORMAL COMPLETION</crdprf:SCB_RespDesc>
                                 </crdprf:getCardListingRs>
                              </crdprf:getCardListingRes>
                           </crdprf:getCardListingResPayload>
                        </getCardListingResponse>
                     </ser-root:getCardListingResponse>
                  </SOAP-ENV:Body>
                </SOAP-ENV:Envelope>
                """;
    }

}
