package com.entity.model;

import com.entity.NewsEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;


/**
 * 健康资讯
 * 接收传参的实体类
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了）
 * 取自ModelAndView 的model名称
 */
public class NewsModel implements Serializable {
    private static final long serialVersionUID = 1L;




    /**
     * 主键
     */
    private Integer id;


    /**
     * 健康资讯名称
     */
    private String newsName;


    /**
     * 健康资讯编号
     */
    private String newsUuidNumber;


    /**
     * 健康资讯照片
     */
    private String newsPhoto;


    /**
     * 健康资讯视频
     */
    private String newsVideo;


    /**
     * 赞
     */
    private Integer zanNumber;


    /**
     * 踩
     */
    private Integer caiNumber;


    /**
     * 健康资讯类型
     */
    private Integer newsTypes;


    /**
     * 健康资讯热度
     */
    private Integer newsClicknum;


    /**
     * 发布时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    private Date fabuTime;


    /**
     * 健康资讯详情
     */
    private String newsContent;


    /**
     * 是否上架
     */
    private Integer shangxiaTypes;


    /**
     * 逻辑删除
     */
    private Integer newsDelete;


    /**
     * 录入时间
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    private Date insertTime;


    /**
     * 创建时间  show1 show2 photoShow
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
    private Date createTime;


    /**
	 * 获取：主键
	 */
    public Integer getId() {
        return id;
    }


    /**
	 * 设置：主键
	 */
    public void setId(Integer id) {
        this.id = id;
    }
    /**
	 * 获取：健康资讯名称
	 */
    public String getNewsName() {
        return newsName;
    }


    /**
	 * 设置：健康资讯名称
	 */
    public void setNewsName(String newsName) {
        this.newsName = newsName;
    }
    /**
	 * 获取：健康资讯编号
	 */
    public String getNewsUuidNumber() {
        return newsUuidNumber;
    }


    /**
	 * 设置：健康资讯编号
	 */
    public void setNewsUuidNumber(String newsUuidNumber) {
        this.newsUuidNumber = newsUuidNumber;
    }
    /**
	 * 获取：健康资讯照片
	 */
    public String getNewsPhoto() {
        return newsPhoto;
    }


    /**
	 * 设置：健康资讯照片
	 */
    public void setNewsPhoto(String newsPhoto) {
        this.newsPhoto = newsPhoto;
    }
    /**
	 * 获取：健康资讯视频
	 */
    public String getNewsVideo() {
        return newsVideo;
    }


    /**
	 * 设置：健康资讯视频
	 */
    public void setNewsVideo(String newsVideo) {
        this.newsVideo = newsVideo;
    }
    /**
	 * 获取：赞
	 */
    public Integer getZanNumber() {
        return zanNumber;
    }


    /**
	 * 设置：赞
	 */
    public void setZanNumber(Integer zanNumber) {
        this.zanNumber = zanNumber;
    }
    /**
	 * 获取：踩
	 */
    public Integer getCaiNumber() {
        return caiNumber;
    }


    /**
	 * 设置：踩
	 */
    public void setCaiNumber(Integer caiNumber) {
        this.caiNumber = caiNumber;
    }
    /**
	 * 获取：健康资讯类型
	 */
    public Integer getNewsTypes() {
        return newsTypes;
    }


    /**
	 * 设置：健康资讯类型
	 */
    public void setNewsTypes(Integer newsTypes) {
        this.newsTypes = newsTypes;
    }
    /**
	 * 获取：健康资讯热度
	 */
    public Integer getNewsClicknum() {
        return newsClicknum;
    }


    /**
	 * 设置：健康资讯热度
	 */
    public void setNewsClicknum(Integer newsClicknum) {
        this.newsClicknum = newsClicknum;
    }
    /**
	 * 获取：发布时间
	 */
    public Date getFabuTime() {
        return fabuTime;
    }


    /**
	 * 设置：发布时间
	 */
    public void setFabuTime(Date fabuTime) {
        this.fabuTime = fabuTime;
    }
    /**
	 * 获取：健康资讯详情
	 */
    public String getNewsContent() {
        return newsContent;
    }


    /**
	 * 设置：健康资讯详情
	 */
    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
    /**
	 * 获取：是否上架
	 */
    public Integer getShangxiaTypes() {
        return shangxiaTypes;
    }


    /**
	 * 设置：是否上架
	 */
    public void setShangxiaTypes(Integer shangxiaTypes) {
        this.shangxiaTypes = shangxiaTypes;
    }
    /**
	 * 获取：逻辑删除
	 */
    public Integer getNewsDelete() {
        return newsDelete;
    }


    /**
	 * 设置：逻辑删除
	 */
    public void setNewsDelete(Integer newsDelete) {
        this.newsDelete = newsDelete;
    }
    /**
	 * 获取：录入时间
	 */
    public Date getInsertTime() {
        return insertTime;
    }


    /**
	 * 设置：录入时间
	 */
    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    /**
	 * 获取：创建时间  show1 show2 photoShow
	 */
    public Date getCreateTime() {
        return createTime;
    }


    /**
	 * 设置：创建时间  show1 show2 photoShow
	 */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    }
