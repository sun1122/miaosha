package com.flysun.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flysun.miaosha.dao.GoodsDao;
import com.flysun.miaosha.vo.GoodsVo;

@Service
public class GoodsService {

	@Autowired
	GoodsDao goodsDao;

	public List<GoodsVo> listGoodsVo() {
		return goodsDao.listGoodsVo();
	}

}
