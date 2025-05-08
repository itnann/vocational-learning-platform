package com.yaojiuye.orders.service;

import com.yaojiuye.base.model.PageParams;
import com.yaojiuye.base.model.PageResult;
import com.yaojiuye.messagesdk.model.po.MqMessage;
import com.yaojiuye.orders.model.dto.AddOrderDto;
import com.yaojiuye.orders.model.dto.PayRecordDto;
import com.yaojiuye.orders.model.dto.PayStatusDto;
import com.yaojiuye.orders.model.po.XcOrders;
import com.yaojiuye.orders.model.po.XcPayRecord;

import java.util.List;

public interface OrderService {

    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付记录(包括二维码)
     * @author itnan
     * @date 2022/10/4 11:02
     */
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * @description 查询支付记录
     * @param payNo  交易记录号
     * @return com.xuecheng.orders.model.po.XcPayRecord
     * @author itnan
     * @date 2022/10/20 23:38
     */
    public XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * 主动请求支付宝查询支付结果
     * @param payNo 支付记录id
     * @return 支付记录信息
     */
    public PayRecordDto queryPayResult(String payNo);

    /**
     * @description 保存支付宝支付结果
     * @param payStatusDto  支付结果信息
     * @return void
     * @author itnan
     * @date 2022/10/4 16:52
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);

    /**
     * 发送通知结果
     * @param message
     */
    public void notifyPayResult(MqMessage message);


    /**
     *
     * 查询用户的订单信息
     * @param userId
     * @return
     */
    public PageResult<XcOrders> queryOrders(String userId, PageParams pageParams);
}
