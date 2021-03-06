/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.payment.impl;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.wechat.constant.ConstantWeChat;
import com.yumu.hexie.integration.wechat.entity.common.CloseOrderResp;
import com.yumu.hexie.integration.wechat.entity.common.JsSign;
import com.yumu.hexie.integration.wechat.entity.common.PaymentOrderResult;
import com.yumu.hexie.integration.wechat.entity.common.PrePaymentOrder;
import com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder;
import com.yumu.hexie.integration.wechat.util.WeixinUtil;
import com.yumu.hexie.integration.wuye.WuyeUtil;
import com.yumu.hexie.integration.wuye.vo.Guangming;
import com.yumu.hexie.model.localservice.basemodel.BaseO2OService;
import com.yumu.hexie.model.market.ServiceOrder;
import com.yumu.hexie.model.payment.PaymentConstant;
import com.yumu.hexie.model.payment.PaymentOrder;
import com.yumu.hexie.model.payment.PaymentOrderRepository;
import com.yumu.hexie.model.payment.RefundOrder;
import com.yumu.hexie.service.common.WechatCoreService;
import com.yumu.hexie.service.exception.BizValidateException;
import com.yumu.hexie.service.payment.PaymentService;
import com.yumu.hexie.service.payment.RefundService;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: PaymentServiceImpl.java, v 0.1 2016年4月1日 下午3:51:22  Exp $
 */
@Service("paymentService")
public class PaymentServiceImpl implements PaymentService {


    protected static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Inject
    protected PaymentOrderRepository paymentOrderRepository;
    @Inject
    protected WechatCoreService wechatCoreService;
    @Inject
    protected RefundService refundService;
    /** 
     * @param order
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#fetchPaymentOrder(com.yumu.hexie.model.market.ServiceOrder)
     */
    @Override
    public PaymentOrder fetchPaymentOrder(ServiceOrder order) {
        List<PaymentOrder> pos = paymentOrderRepository.findByOrderTypeAndOrderId(PaymentConstant.TYPE_MARKET_ORDER,order.getId());
        if(pos != null && pos.size()>0) {
            return pos.get(0);
        } else {
            return new PaymentOrder(order);
        }
    }

    /** 
     * @param order
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#fetchPaymentOrder(com.yumu.hexie.model.localservice.basemodel.BaseO2OService)
     */
    @Override
    public PaymentOrder fetchPaymentOrder(BaseO2OService order,String openId) {
        List<PaymentOrder> pos = paymentOrderRepository.findByOrderTypeAndOrderIdAndOpenId(order.getPaymentOrderType(),order.getId(),openId);
        if(pos != null && pos.size()>0) {
        	PaymentOrder pay = pos.get(0);
        	pay.setPayNoIs("1"); //返回已有订单   因为付费通支付不允许终端流水id重复 所以需要判断是否为第一次
            return pay;
        } else {
            return paymentOrderRepository.save(new PaymentOrder(order,openId));
        }
    }

    /** 
     * @param orderType
     * @param orderId
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#queryPaymentOrder(int, long)
     */
    @Override
    public PaymentOrder queryPaymentOrder(int orderType, long orderId) {
        List<PaymentOrder> pos = paymentOrderRepository.findByOrderTypeAndOrderId(orderType, orderId);
        if(pos == null || pos.size() > 0){
            for(PaymentOrder p : pos) {
                if(p.getStatus() != PaymentConstant.PAYMENT_STATUS_FAIL){
                    return p;
                }
            }
        }
        return null;
    }

    /** 
     * @param order
     * @see com.yumu.hexie.service.payment.PaymentService#payOffline(com.yumu.hexie.model.localservice.basemodel.BaseO2OService)
     */
    @Override
    public void payOffline(BaseO2OService order) {
    }

    /** 
     * @param payment
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#requestPay(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @Override
    public JsSign requestPay(PaymentOrder pay) {
    	
        if(checkPaySuccess(pay.getPaymentNo())){
            throw new BizValidateException(pay.getId(),"订单已支付成功，勿重复提交！").setError();
        }
        validatePayRequest(pay);
        log.warn("[Payment-req]["+pay.getPaymentNo()+"]["+pay.getOrderId()+"]["+pay.getOrderType()+"]");
        //支付然后没继续的情景=----校验所需时间较长，是否需要如此操作
        
//		3. 从微信获取签名   此方法注释 因为现在是用付费通支付 从付费通哪里拿到签名 appid 等  wyw 2018-11-13
//        PrePaymentOrder preWechatOrder = wechatCoreService.createOrder(pay);
//        log.warn("[Payment-req]Saved["+pay.getPaymentNo()+"]["+pay.getOrderId()+"]["+pay.getOrderType()+"]");
//        JsSign sign = wechatCoreService.getPrepareSign(preWechatOrder.getPrepay_id());
        
		DecimalFormat decimalFormat=new DecimalFormat("0");
		String price = decimalFormat.format(pay.getPrice()*100);
		JsSign r = new JsSign();
        try {
		Guangming guang = WuyeUtil.getPayInfo(pay.getPaymentNo(),price, "0101", pay.getOpenId()).getData();//发起支付
		log.warn("[Payment-req]sign["+guang.getPaySign()+"]");
		r.setAppId(guang.getAppId());
		r.setTimestamp(guang.getTimeStamp());
		r.setNonceStr(guang.getNonceStr());
		r.setPkgStr(guang.getPackage_str());
		r.setSignature(guang.getPaySign());
		r.setSignType(guang.getSignType());
		
		pay.setPrepayId(guang.getPackage_str());
        paymentOrderRepository.save(pay);
        
        
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return r;
    }
    
    public static void main(String[] args) {
    	try {
			Guangming guang = WuyeUtil.getPayOrderInfo("201812051433P58098").getData();
			System.out.println(guang.getAppId());
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//发起支付
	}
    
    private void validatePayRequest(PaymentOrder pay) {
        log.error("validatePayRequest:paymentNo:" +pay.getPaymentNo());
        if(PaymentConstant.PAYMENT_STATUS_SUCCESS==pay.getStatus()
                ||PaymentConstant.PAYMENT_STATUS_REFUNDED==pay.getStatus()
                ||PaymentConstant.PAYMENT_STATUS_REFUNDING==pay.getStatus()){
            throw new BizValidateException(pay.getOrderId(),"订单状态已成功支付或已退款，请重新查询确认订单状态！").setError();
        } else if(PaymentConstant.PAYMENT_STATUS_CANCEL==pay.getStatus()
                ||PaymentConstant.PAYMENT_STATUS_FAIL==pay.getStatus()){
            pay.refreshOrder();
        } else if(PaymentConstant.PAY_STATUS_REPEATEDLY.equals(pay.getPayNoIs())) {//多次请求刷新
        	pay.refreshOrder();
        }
    }

    private boolean checkPaySuccess(String paymentNo){
        log.warn("[Payment-check]begin["+paymentNo+"]");
        Guangming guang = null;
		try {
			guang = WuyeUtil.getPayOrderInfo(paymentNo).getData();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return guang.isPaySuccess();
    }
    /** 
     * @param payment
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#refreshStatus(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @Override
    public PaymentOrder refreshStatus(PaymentOrder payment) {
        log.warn("[Payment-refreshStatus]begin["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
        if(payment.getStatus() != PaymentConstant.PAYMENT_STATUS_INIT){
            return payment;
        }
//        PaymentOrderResult poResult = wechatCoreService.queryOrder(payment.getPaymentNo());
        try {
			Guangming guang = WuyeUtil.getPayOrderInfo(payment.getPaymentNo()).getData();
			if(!"00".equals(guang.getResCode())) {
				log.error("错误提示："+guang.getResMsg());
				throw new BizValidateException("错误提示："+guang.getResMsg());
			}
	        if(guang.isPaying()) {//1. 支付中
	            log.warn("[Payment-refreshStatus]isPaying["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            return payment;
	        } else if(guang.isPayFail()) {//2. 失败
	            log.warn("[Payment-refreshStatus]isPayFail["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            payment.fail();
	        } else if(guang.isPayInvalid()) {//3. 失效
	            log.warn("[Payment-refreshStatus]isPayInvalid["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            payment.invalid();
	        } else if(guang.isPayUnpaid()) {//4. 未支付
	            log.warn("[Payment-refreshStatus]isPayUnpaid["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            payment.unpaid();
	        } else if (guang.isPaySuccess()) {//5. 成功
	            log.warn("[Payment-refreshStatus]isPaySuccess["+payment.getOrderType()+"]["+payment.getOrderId()+"]");
	            payment.paySuccess(guang.getOrderId());
	        }
        } catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return paymentOrderRepository.save(payment);
    }

    @Override
    public PaymentOrder cancelPayment(int orderType,long orderId) {
        log.warn("[Payment-cancelPayment]begin["+orderType+"]["+orderId+"]");
        PaymentOrder po = this.queryPaymentOrder(orderType, orderId);
        if(po == null) {
            return null;
        }
        if(po.getStatus() == PaymentConstant.PAYMENT_STATUS_SUCCESS
                ||po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDING
                ||po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDED) {
            throw new BizValidateException(po.getOrderId(),"该订单已支付成功，无法取消！").setError();
        }
//        CloseOrderResp c = wechatCoreService.closeOrder(po);
        try {
			Guangming guang = WuyeUtil.getPayOrderInfo(po.getPaymentNo()).getData();

			if (guang==null) {
				throw new BizValidateException(po.getOrderId(), "网络异常，无法查询支付状态！").setError();
			}
			if(!"00".equals(guang.getResCode())) {
				log.error("错误提示："+guang.getResMsg());
				throw new BizValidateException("错误提示："+guang.getResMsg());
			}
	        if(guang.isPaying()){
	            throw new BizValidateException(po.getOrderId(),"该订单支付中，无法取消！").setError();
	        } else if(guang.isPaySuccess()) {
	            po.setStatus(PaymentConstant.PAYMENT_STATUS_SUCCESS);
	            po.setUpdateDate(System.currentTimeMillis());
	            paymentOrderRepository.save(po);
	
	            //FIXME 是否事务会回滚，需要注意
	            throw new BizValidateException(po.getOrderId(),"该订单已支付成功，无法取消！").setError();
	        }
	        po.setStatus(PaymentConstant.PAYMENT_STATUS_CANCEL);
	        po.setUpdateDate(System.currentTimeMillis());
	        log.warn("[Payment-cancelPayment]end["+orderType+"]["+orderId+"]");
        } catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return paymentOrderRepository.save(po);
    }

    /** 
     * @param paymentId
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#refundApply(long)
     */
    @Override
    public boolean refundApply(PaymentOrder po) {
        log.warn("[Payment-refundApply]begin["+po.getOrderType()+"]["+po.getId()+"]");
        if(po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDED
                || po.getStatus() == PaymentConstant.PAYMENT_STATUS_REFUNDING){
            return true;
        }
        validateRefundPayment(po);
        try{
            if(!wechatCoreService.queryOrder(po.getPaymentNo()).isPaySuccess()){
                log.warn("[Payment-refundApply]notsuccess["+po.getOrderType()+"]["+po.getId()+"]");
                po.setStatus(PaymentConstant.PAYMENT_STATUS_CANCEL);
                po.setUpdateDate(System.currentTimeMillis());
                paymentOrderRepository.save(po);
            }
        }catch(Exception e) {
            po.setStatus(PaymentConstant.PAYMENT_STATUS_CANCEL);
            po.setUpdateDate(System.currentTimeMillis());
            paymentOrderRepository.save(po);
        }
        RefundOrder ro = new RefundOrder(po);
        log.warn("[Payment-refundApply]end["+po.getOrderType()+"]["+po.getId()+"]");
        return refundService.refundApply(ro);
    }
    private void validateRefundPayment(PaymentOrder po) {
        if(PaymentConstant.PAYMENT_STATUS_SUCCESS != po.getStatus()
                &&PaymentConstant.PAYMENT_STATUS_INIT != po.getStatus()) {
            throw new BizValidateException(po.getOrderId(),"该支付记录无法退款！").setError();
        }
    }

    /** 
     * @param wxRefundOrder
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#updateRefundStatus(com.yumu.hexie.integration.wechat.entity.common.WxRefundOrder)
     */
    @Override
    public PaymentOrder updateRefundStatus(WxRefundOrder wxRefundOrder) {
        log.warn("[Payment-updateRefundStatus]begin["+wxRefundOrder.getOut_trade_no()+"]");
        RefundOrder ro = refundService.updateRefundStatus(wxRefundOrder);
        if(ro == null) {
            return null;
        }
        PaymentOrder po = paymentOrderRepository.findOne(ro.getPaymentId());
        if (ro.getRefundStatus() == PaymentConstant.REFUND_STATUS_SUCCESS) {
            po.refunded();
        } else if (ro.getRefundStatus() == PaymentConstant.REFUND_STATUS_APPLYED) {
            po.setStatus(PaymentConstant.PAYMENT_STATUS_REFUNDING);
        }
        return paymentOrderRepository.save(po);
    }

    /** 
     * @param paymentNo
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#findByPaymentNo(java.lang.String)
     */
    @Override
    public PaymentOrder findByPaymentNo(String paymentNo) {
        List<PaymentOrder> pos = paymentOrderRepository.findAllByPaymentNo(paymentNo);
        if(pos != null && pos.size()>=0) {
            return pos.get(0);
        }
        return null;
    }

    /** 
     * @param pay
     * @return
     * @see com.yumu.hexie.service.payment.PaymentService#save(com.yumu.hexie.model.payment.PaymentOrder)
     */
    @Override
    public PaymentOrder save(PaymentOrder pay) {
        return paymentOrderRepository.save(pay);
    }

}
