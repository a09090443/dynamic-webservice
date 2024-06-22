package com.dynamicwebservice.constants;

/**
 * 錯誤訊息 代碼(code)
 * <p/>
 * Package: com.tcb.constants <br>
 * File Name: ErrorCode <br>
 * Origin: com.tcb.twnb.Constants
 *
 * @ClassName: com.tcb.constants.ErrorCode
 * @Description: 錯誤訊息 代碼(code)
 * @Copyright: Copyright (c) TCB Corp. 2023. All Rights Reserved.
 * @Company: WebComm Technology.
 * @author RUEI.JIANG
 * @version 1.0, 2023 年 11 月 1 日
 */
public class ErrorCode {
    public static final String ERROR_CODE_TWN_RA_LOGIN_FAIL = "1014"; // RA 系統錯誤

    public static final String ERROR_CODE_TWN_RA_VERIFY_FAIL = "1019"; // RA 驗章錯誤

    public static final String ERROR_CODE_TWN_SYSTEM_ERROR = "9999"; // 其它內部錯誤

    private ErrorCode() {}
}
