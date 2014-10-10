package cn.koolcloud.ipos.appstore.pay;

import java.net.URLEncoder;

import cn.koolcloud.ipos.appstore.entity.Pay;
import cn.koolcloud.ipos.appstore.utils.MyLog;

 

public class PayUtils {

	public static String getNewOrderInfo(Pay pay) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(pay.getList().get(0).getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append(pay.getList().get(0).getSubject());
		sb.append("\"&body=\"");
		sb.append(pay.getList().get(0).getBody());
		sb.append("\"&total_fee=\"");
		sb.append(pay.getList().get(0).getFee());
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode("http://appstore.koolyun.com/asapi/appstore/notify/alipaySecureNotify"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		MyLog.i(" 支付宝 === "+sb.toString());
		return new String(sb);
	}

	public static  String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
