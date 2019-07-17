package com.meitianhui.goods.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预售商品信息
 * 
 * @author Tiny
 *
 */
public class PsGoods implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 货品标识 **/
	private String goods_id;
	/** 商品编码 **/
	private String goods_code ;
	/** 标题 **/
	private String title;
	/** SKU **/
	private String sku;
	/** 描述 **/
	private String desc1;
	/** 地区标识 **/
	private String area_id;
	/** 上架时间 **/
	private Date online_date;
	/** 下架时间 **/
	private Date offline_date;
	/** 标签 **/
	private String label;
	/** 促销标签 **/
	private String label_promotion;
	/** 类型，可选值：尾货、新品、预售、试样、二手、其它 **/
	private String category;
	/** 来源，可选值：merchants（合作商家）、taobao（淘宝） **/
	private String data_source;
	/** 推荐展馆**/
	private String display_area;
	/** 联系人 **/
	private String contact_person;
	/** 联系人电话 **/
	private String contact_tel;
	/** 货品图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]} **/
	private String pic_info;
	/** 详情图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]} **/
	private String pic_detail_info;
	/** 规格 **/
	private String specification;
	/** 代理人(分公司) **/
	private String agent;
	/** 包装 **/
	private String pack;
	/** 进价（成本价） **/
	private BigDecimal cost_price;
	/** 市场价 **/
	private BigDecimal market_price;
	/** 优惠价 **/
	private BigDecimal discount_price;
	/** 结算价 **/
	private BigDecimal settled_price;
	/** 参团人数，可选值：2、5、10 **/
	private BigDecimal ts_min_num;
	/** 成团价格 **/
	private BigDecimal ts_price;
	/** 团购时间，单位小时H，可选值：24、48 **/
	private Integer ts_date;
	/** 服务费 **/
	private BigDecimal service_fee;
	/** 阶梯价 **/
	private String ladder_price;
	/** 成本分摊价格 **/
	private BigDecimal cost_allocation;
	/** 商品来源 **/
	private String product_source;
	/** 邮费 **/
	private BigDecimal shipping_fee;
	/** 产地 **/
	private String producer;
	/** 供应商名称 **/
	private String supplier;
	/** 供应商id **/
	private String supplier_id;
	/** 仓库名称 **/
	private String warehouse;
	/** 仓库id **/
	private String warehouse_id;
	/** 生产商 **/
	private String manufacturer;
	/** 起订量 **/
	private Integer min_buy_qty;
	/** 限购量 **/
	private Integer max_buy_qty;
	/** 可销售库存量 **/
	private Integer sale_qty;
	/** 库存量 **/
	private Integer stock_qty;
	/** 单位 **/
	private String goods_unit;
	/** 生效日期 **/
	private String valid_thru;
	/** 配送 **/
	private String delivery;
	/** 配送 **/
	private String delivery_id;
	/** 配送地区 **/
	private String delivery_area;
	/** 配送地区描述 **/
	private String delivery_desc;
	/** 支付方式，可选值：online(在线支付)offline(货到付款) **/
	private String payment_way;
	/** 状态，可选值：normal（正常）delete(删除) **/
	private String status;
	/** 购买商品最低等级 **/
	private Integer good_level;
	/** 购买商品最低积分 **/
	private Integer good_point;
	/** 创建时间 **/
	private Date created_date;
	/** 修改时间 **/
	private Date modified_date;
	/** 备注 **/
	private String remark;
	/** 数据来源类型 **/
	private String data_source_type;
	/** 淘宝价格 **/
	private String taobao_price;
	/** 淘宝优惠劵链接  **/
	private String voucher_link;
	/** 优惠券结束时间 **/
	private Date discount_end;

	private String nowdiffOffline_date;
	
	private String taobao_sales;

	/** 淘客链接 **/
	private String taobao_link;
	/** 花生日记佣金比例 **/
	private String commission_rate;
	private String huiguo_commission;
	private String huiguo_vip;
	private String self_label;
	/**商品佣金分成*/
	private String commission;
	
	private Integer top_catid;
	private Integer two_catid;
	private Integer three_catid;
	
	private Integer goods_type;//1为特卖商品，2为0元购商品
	
	public Integer getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(Integer goods_type) {
		this.goods_type = goods_type;
	}

	/**新旧商品区分标识,1(true)是新自营*/
	private boolean flags;

	public Integer getTop_catid() {
		return top_catid;
	}

	public void setTop_catid(Integer top_catid) {
		this.top_catid = top_catid;
	}

	public Integer getTwo_catid() {
		return two_catid;
	}

	public void setTwo_catid(Integer two_catid) {
		this.two_catid = two_catid;
	}

	public Integer getThree_catid() {
		return three_catid;
	}

	public void setThree_catid(Integer three_catid) {
		this.three_catid = three_catid;
	}


	public boolean getFlags() {
		return flags;
	}

	public void setFlags(boolean b) {
		this.flags = b;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getNowdiffOffline_date() {
		return nowdiffOffline_date;
	}

	public void setNowdiffOffline_date(String nowdiffOffline_date) {
		this.nowdiffOffline_date = nowdiffOffline_date;
	}

	public String getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}
	public String getGoods_code() {
		return goods_code;
	}
	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getDesc1() {
		return desc1;
	}
	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}
	public String getArea_id() {
		return area_id;
	}
	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public Date getOnline_date() {
		return online_date;
	}
	public void setOnline_date(Date online_date) {
		this.online_date = online_date;
	}
	public Date getOffline_date() {
		return offline_date;
	}
	public void setOffline_date(Date offline_date) {
		this.offline_date = offline_date;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel_promotion() {
		return label_promotion;
	}
	public void setLabel_promotion(String label_promotion) {
		this.label_promotion = label_promotion;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getData_source() {
		return data_source;
	}
	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	public String getDisplay_area() {
		return display_area;
	}
	public void setDisplay_area(String display_area) {
		this.display_area = display_area;
	}
	public String getContact_person() {
		return contact_person;
	}
	public void setContact_person(String contact_person) {
		this.contact_person = contact_person;
	}
	public String getContact_tel() {
		return contact_tel;
	}
	public void setContact_tel(String contact_tel) {
		this.contact_tel = contact_tel;
	}
	public String getPic_info() {
		return pic_info;
	}
	public void setPic_info(String pic_info) {
		this.pic_info = pic_info;
	}
	public String getPic_detail_info() {
		return pic_detail_info;
	}
	public void setPic_detail_info(String pic_detail_info) {
		this.pic_detail_info = pic_detail_info;
	}
	public String getSpecification() {
		return specification;
	}
	public void setSpecification(String specification) {
		this.specification = specification;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getPack() {
		return pack;
	}
	public void setPack(String pack) {
		this.pack = pack;
	}
	public BigDecimal getCost_price() {
		return cost_price;
	}
	public void setCost_price(BigDecimal cost_price) {
		this.cost_price = cost_price;
	}
	public BigDecimal getMarket_price() {
		return market_price;
	}
	public void setMarket_price(BigDecimal market_price) {
		this.market_price = market_price;
	}
	public BigDecimal getDiscount_price() {
		return discount_price;
	}
	public void setDiscount_price(BigDecimal discount_price) {
		this.discount_price = discount_price;
	}
	public BigDecimal getSettled_price() {
		return settled_price;
	}
	public void setSettled_price(BigDecimal settled_price) {
		this.settled_price = settled_price;
	}
	public BigDecimal getTs_min_num() {
		return ts_min_num;
	}
	public void setTs_min_num(BigDecimal ts_min_num) {
		this.ts_min_num = ts_min_num;
	}
	public BigDecimal getTs_price() {
		return ts_price;
	}
	public void setTs_price(BigDecimal ts_price) {
		this.ts_price = ts_price;
	}
	public Integer getTs_date() {
		return ts_date;
	}
	public void setTs_date(Integer ts_date) {
		this.ts_date = ts_date;
	}
	public BigDecimal getService_fee() {
		return service_fee;
	}
	public void setService_fee(BigDecimal service_fee) {
		this.service_fee = service_fee;
	}
	public String getLadder_price() {
		return ladder_price;
	}
	public void setLadder_price(String ladder_price) {
		this.ladder_price = ladder_price;
	}
	public BigDecimal getCost_allocation() {
		return cost_allocation;
	}
	public void setCost_allocation(BigDecimal cost_allocation) {
		this.cost_allocation = cost_allocation;
	}
	public String getProduct_source() {
		return product_source;
	}
	public void setProduct_source(String product_source) {
		this.product_source = product_source;
	}
	public BigDecimal getShipping_fee() {
		return shipping_fee;
	}
	public void setShipping_fee(BigDecimal shipping_fee) {
		this.shipping_fee = shipping_fee;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getSupplier_id() {
		return supplier_id;
	}
	public void setSupplier_id(String supplier_id) {
		this.supplier_id = supplier_id;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public String getWarehouse_id() {
		return warehouse_id;
	}
	public void setWarehouse_id(String warehouse_id) {
		this.warehouse_id = warehouse_id;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public Integer getMin_buy_qty() {
		return min_buy_qty;
	}
	public void setMin_buy_qty(Integer min_buy_qty) {
		this.min_buy_qty = min_buy_qty;
	}
	public Integer getMax_buy_qty() {
		return max_buy_qty;
	}
	public void setMax_buy_qty(Integer max_buy_qty) {
		this.max_buy_qty = max_buy_qty;
	}
	public Integer getSale_qty() {
		return sale_qty;
	}
	public void setSale_qty(Integer sale_qty) {
		this.sale_qty = sale_qty;
	}
	public Integer getStock_qty() {
		return stock_qty;
	}
	public void setStock_qty(Integer stock_qty) {
		this.stock_qty = stock_qty;
	}
	public String getGoods_unit() {
		return goods_unit;
	}
	public void setGoods_unit(String goods_unit) {
		this.goods_unit = goods_unit;
	}
	public String getValid_thru() {
		return valid_thru;
	}
	public void setValid_thru(String valid_thru) {
		this.valid_thru = valid_thru;
	}
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}
	public String getDelivery_id() {
		return delivery_id;
	}
	public void setDelivery_id(String delivery_id) {
		this.delivery_id = delivery_id;
	}
	public String getDelivery_area() {
		return delivery_area;
	}
	public void setDelivery_area(String delivery_area) {
		this.delivery_area = delivery_area;
	}
	public String getDelivery_desc() {
		return delivery_desc;
	}
	public void setDelivery_desc(String delivery_desc) {
		this.delivery_desc = delivery_desc;
	}
	public String getPayment_way() {
		return payment_way;
	}
	public void setPayment_way(String payment_way) {
		this.payment_way = payment_way;
	}
	public String getStatus() {
		return status;
	}
	 
	public Integer getGood_level() {
		return good_level;
	}
	public void setGood_level(Integer good_level) {
		this.good_level = good_level;
	}
	public Integer getGood_point() {
		return good_point;
	}
	public void setGood_point(Integer good_point) {
		this.good_point = good_point;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	public Date getModified_date() {
		return modified_date;
	}
	public void setModified_date(Date modified_date) {
		this.modified_date = modified_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getData_source_type() {
		return data_source_type;
	}
	public void setData_source_type(String data_source_type) {
		this.data_source_type = data_source_type;
	}
	public String getTaobao_price() {
		return taobao_price;
	}
	public void setTaobao_price(String taobao_price) {
		this.taobao_price = taobao_price;
	}
	public String getVoucher_link() {
		return voucher_link;
	}
	public void setVoucher_link(String voucher_link) {
		this.voucher_link = voucher_link;
	}
	public Date getDiscount_end() {
		return discount_end;
	}
	public void setDiscount_end(Date discount_end) {
		this.discount_end = discount_end;
	}
	public String getTaobao_link() {
		return taobao_link;
	}
	public void setTaobao_link(String taobao_link) {
		this.taobao_link = taobao_link;
	}
	
	public String getCommission_rate() {
		return commission_rate;
	}
	public void setCommission_rate(String commission_rate) {
		this.commission_rate = commission_rate;
	}

	public String getTaobao_sales() {
		return taobao_sales;
	}

	public void setTaobao_sales(String taobao_sales) {
		this.taobao_sales = taobao_sales;
	}

	public String getHuiguo_commission() {
		return huiguo_commission;
	}

	public void setHuiguo_commission(String huiguo_commission) {
		this.huiguo_commission = huiguo_commission;
	}

	public String getHuiguo_vip() {
		return huiguo_vip;
	}

	public void setHuiguo_vip(String huiguo_vip) {
		this.huiguo_vip = huiguo_vip;
	}

	public String getSelf_label() {
		return self_label;
	}

	public void setSelf_label(String self_label) {
		this.self_label = self_label;
	}
	
	
}
