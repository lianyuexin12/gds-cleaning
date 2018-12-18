package com.cloud.gds.cleaning.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.cloud.dips.common.core.util.Query;
import com.cloud.dips.common.core.util.R;
import com.cloud.gds.cleaning.api.constant.DataCleanConstant;
import com.cloud.gds.cleaning.api.entity.DataFieldValue;
import com.cloud.gds.cleaning.api.vo.DataPoolVo;
import com.cloud.gds.cleaning.service.DataFieldValueService;
import com.cloud.gds.cleaning.utils.CommonUtils;
import com.cloud.gds.cleaning.utils.DataPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 导入数据的内容
 *
 * @author lolilijve
 * @date 2018-11-27 09:43:46
 */
@RestController
@RequestMapping("/data_pool")
public class DataPoolController {

	private final DataFieldValueService dataFieldValueService;

	@Autowired
	public DataPoolController(
		DataFieldValueService dataFieldValueService) {this.dataFieldValueService = dataFieldValueService;}

	/**
	 * 分页
	 * 参数要求：page、limit、fieldId
	 * @param params
	 */
	@GetMapping("/page")
	public R page(@RequestParam Map<String, Object> params) {
		CommonUtils.PiPei pp = CommonUtils.createPP();
		List<String> eqList = new ArrayList<>();
		eqList.add("fieldId");
		eqList.add("remark");
		pp.setEq(eqList);
//		List<String> likelist = new ArrayList<>();
//		likelist.add("");
//		pp.setLike(likelist);
		Wrapper<DataFieldValue> wrapper = CommonUtils.pagePart(params,pp,new DataFieldValue());
		Page page = dataFieldValueService.pagePo2Vo(dataFieldValueService.selectPage(new Query<>(CommonUtils.map2map(params)),wrapper));
		return new R<>(page);
	}

	/**
	 * 根据id查询
	 * @param id
	 * @return R
	 */
	@GetMapping("/{id}")
	public R info(@PathVariable("id") Long id) {
		DataFieldValue dataFieldValue = dataFieldValueService.selectOne(new EntityWrapper<DataFieldValue>().eq("id", id).eq("is_deleted", DataCleanConstant.NO));
		DataPoolVo dataPoolVo = DataPoolUtils.entity2Vo(dataFieldValue);
		return new R<>(dataPoolVo);
	}

	/**
	 * 保存
	 * @param params
	 * @param id
	 * @return
	 */
	@PostMapping("/create/{id}")
	public R save(@RequestBody JSONObject params, @RequestParam("fieldId") Long id) {
		return new R<>(dataFieldValueService.save(id,params));
	}

	/**
	 * 修改
	 * @param dataPoolVo
	 * @return R
	 */
	@PostMapping("/update")
	public R update(@RequestBody DataPoolVo dataPoolVo) {
		DataFieldValue dataFieldValue = DataPoolUtils.vo2Entity(dataPoolVo);
		dataFieldValueService.update(dataFieldValue);
		return new R<>(Boolean.TRUE);
	}

	/**
	 * 单独删除
	 * @param id
	 * @return
	 */
	@PostMapping("/delete/{id}")
	public R delete(@PathVariable("id") Long id){
		return new R(dataFieldValueService.deleteById(id));
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@PostMapping("/ids")
	public R deleteT(@RequestBody Set<Long> ids){
		dataFieldValueService.deleteByIds(ids);
		return new R<>();
	}

}