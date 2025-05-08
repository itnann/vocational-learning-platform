package com.yaojiuye.orders.model.dto;

import com.yaojiuye.orders.model.po.XcPayRecord;
import lombok.Data;
import lombok.ToString;

/**
 * @author itnan
 * @version 1.0
 * @description 支付记录dto
 * @date 2022/10/4 11:30
 */
@Data
@ToString
public class PayRecordDto extends XcPayRecord {

    //二维码
    private String qrcode;

}
