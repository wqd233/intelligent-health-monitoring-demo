package com.dao;

import com.entity.NewsLiuyanEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.entity.view.NewsLiuyanView;

/**
 * 健康资讯留言 Dao 接口
 *
 * @author 
 */
public interface NewsLiuyanDao extends BaseMapper<NewsLiuyanEntity> {

   List<NewsLiuyanView> selectListView(Pagination page,@Param("params")Map<String,Object> params);

}
