
package com.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;

import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.alibaba.fastjson.*;

/**
 * 用户
 * 后端接口
 */
@RestController
@Controller
@RequestMapping("/yonghu")
public class YonghuController {
    private static final Logger logger = LoggerFactory.getLogger(YonghuController.class);

    private static final String TABLE_NAME = "yonghu";

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private DictionaryService dictionaryService;// 字典
    @Autowired
    private ForumService forumService;// 论坛
    @Autowired
    private GonggaoService gonggaoService;// 公告
    @Autowired
    private JiankangrizhiService jiankangrizhiService;// 健康日志
    @Autowired
    private NewsService newsService;// 新闻
    @Autowired
    private NewsCollectionService newsCollectionService;// 新闻收藏
    @Autowired
    private NewsLiuyanService newsLiuyanService;// 新闻留言
    @Autowired
    private UsersService usersService;// 管理员

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        logger.debug("page方法:,,Controller:{},,params:{}", this.getClass().getName(), JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if (false)
            return R.error(511, "永不会进入");
        else if ("用户".equals(role))
            params.put("yonghuId", request.getSession().getAttribute("userId"));
        CommonUtil.checkMap(params);
        PageUtils page = yonghuService.queryPage(params);

        // 字典表数据转换
        List<YonghuView> list = (List<YonghuView>) page.getList();
        for (YonghuView c : list) {
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.debug("info方法:,,Controller:{},,id:{}", this.getClass().getName(), id);
        YonghuEntity yonghu = yonghuService.selectById(id);
        if (yonghu != null) {
            // entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties(yonghu, view);// 把实体数据重构到view中
            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }

    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody YonghuEntity yonghu, HttpServletRequest request) {
        logger.debug("save方法:,,Controller:{},,yonghu:{}", this.getClass().getName(), yonghu.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if (false)
            return R.error(511, "永远不会进入");

        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", yonghu.getUsername())
                .or()
                .eq("yonghu_phone", yonghu.getYonghuPhone())
                .or()
                .eq("yonghu_id_number", yonghu.getYonghuIdNumber());

        logger.info("sql语句:" + queryWrapper.getSqlSegment());
        YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
        if (yonghuEntity == null) {
            yonghu.setCreateTime(new Date());
            yonghu.setPassword("123456");
            yonghuService.insert(yonghu);
            return R.ok();
        } else {
            return R.error(511, "账户或者用户手机号或者用户身份证号已经被使用");
        }
    }

    /**
     * 后端修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody YonghuEntity yonghu, HttpServletRequest request)
            throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.debug("update方法:,,Controller:{},,yonghu:{}", this.getClass().getName(), yonghu.toString());
        YonghuEntity oldYonghuEntity = yonghuService.selectById(yonghu.getId());// 查询原先数据

        String role = String.valueOf(request.getSession().getAttribute("role"));
        // if(false)
        // return R.error(511,"永远不会进入");
        if ("".equals(yonghu.getYonghuPhoto()) || "null".equals(yonghu.getYonghuPhoto())) {
            yonghu.setYonghuPhoto(null);
        }

        yonghuService.updateById(yonghu);// 根据id更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        logger.debug("delete:,,Controller:{},,ids:{}", this.getClass().getName(), ids.toString());
        List<YonghuEntity> oldYonghuList = yonghuService.selectBatchIds(Arrays.asList(ids));// 要删除的数据
        yonghuService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save(String fileName, HttpServletRequest request) {
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}", this.getClass().getName(), fileName);
        Integer yonghuId = Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId")));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            List<YonghuEntity> yonghuList = new ArrayList<>();// 上传的东西
            Map<String, List<String>> seachFields = new HashMap<>();// 要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if (lastIndexOf == -1) {
                return R.error(511, "该文件没有后缀");
            } else {
                String suffix = fileName.substring(lastIndexOf);
                if (!".xls".equals(suffix)) {
                    return R.error(511, "只支持后缀为xls的excel文件");
                } else {
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);// 获取文件路径
                    File file = new File(resource.getFile());
                    if (!file.exists()) {
                        return R.error(511, "找不到上传文件，请联系管理员");
                    } else {
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());// 读取xls文件
                        dataList.remove(0);// 删除第一行，因为第一行是提示
                        for (List<String> data : dataList) {
                            // 循环
                            YonghuEntity yonghuEntity = new YonghuEntity();
                            yonghuList.add(yonghuEntity);

                            // 把要查询是否重复的字段放入map中
                            // 账户
                            if (seachFields.containsKey("username")) {
                                List<String> username = seachFields.get("username");
                                username.add(data.get(0));// 要改的
                            } else {
                                List<String> username = new ArrayList<>();
                                username.add(data.get(0));// 要改的
                                seachFields.put("username", username);
                            }
                            // 用户手机号
                            if (seachFields.containsKey("yonghuPhone")) {
                                List<String> yonghuPhone = seachFields.get("yonghuPhone");
                                yonghuPhone.add(data.get(0));// 要改的
                            } else {
                                List<String> yonghuPhone = new ArrayList<>();
                                yonghuPhone.add(data.get(0));// 要改的
                                seachFields.put("yonghuPhone", yonghuPhone);
                            }
                            // 用户身份证号
                            if (seachFields.containsKey("yonghuIdNumber")) {
                                List<String> yonghuIdNumber = seachFields.get("yonghuIdNumber");
                                yonghuIdNumber.add(data.get(0));// 要改的
                            } else {
                                List<String> yonghuIdNumber = new ArrayList<>();
                                yonghuIdNumber.add(data.get(0));// 要改的
                                seachFields.put("yonghuIdNumber", yonghuIdNumber);
                            }
                        }

                        // 查询是否重复
                        // 账户
                        List<YonghuEntity> yonghuEntities_username = yonghuService.selectList(
                                new EntityWrapper<YonghuEntity>().in("username", seachFields.get("username")));
                        if (yonghuEntities_username.size() > 0) {
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for (YonghuEntity s : yonghuEntities_username) {
                                repeatFields.add(s.getUsername());
                            }
                            return R.error(511, "数据库的该表中的 [账户] 字段已经存在 存在数据为:" + repeatFields.toString());
                        }
                        // 用户手机号
                        List<YonghuEntity> yonghuEntities_yonghuPhone = yonghuService.selectList(
                                new EntityWrapper<YonghuEntity>().in("yonghu_phone", seachFields.get("yonghuPhone")));
                        if (yonghuEntities_yonghuPhone.size() > 0) {
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for (YonghuEntity s : yonghuEntities_yonghuPhone) {
                                repeatFields.add(s.getYonghuPhone());
                            }
                            return R.error(511, "数据库的该表中的 [用户手机号] 字段已经存在 存在数据为:" + repeatFields.toString());
                        }
                        // 用户身份证号
                        List<YonghuEntity> yonghuEntities_yonghuIdNumber = yonghuService
                                .selectList(new EntityWrapper<YonghuEntity>().in("yonghu_id_number",
                                        seachFields.get("yonghuIdNumber")));
                        if (yonghuEntities_yonghuIdNumber.size() > 0) {
                            ArrayList<String> repeatFields = new ArrayList<>();
                            for (YonghuEntity s : yonghuEntities_yonghuIdNumber) {
                                repeatFields.add(s.getYonghuIdNumber());
                            }
                            return R.error(511, "数据库的该表中的 [用户身份证号] 字段已经存在 存在数据为:" + repeatFields.toString());
                        }
                        yonghuService.insertBatch(yonghuList);
                        return R.ok();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(511, "批量插入数据异常，请联系管理员");
        }
    }

    /**
     * 登录
     */
    @IgnoreAuth
    @RequestMapping(value = "/login")
    public R login(String username, String password, String captcha, HttpServletRequest request) {
        YonghuEntity yonghu = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("username", username));
        if (yonghu == null || !yonghu.getPassword().equals(password))
            return R.error("账号或密码不正确");
        String token = tokenService.generateToken(yonghu.getId(), username, "yonghu", "用户");
        R r = R.ok();
        r.put("token", token);
        r.put("role", "用户");
        r.put("username", yonghu.getYonghuName());
        r.put("tableName", "yonghu");
        r.put("userId", yonghu.getId());
        return r;
    }
    /**
     * 根据用户名获取邮箱
     */
    @IgnoreAuth
    @GetMapping("/getEmailByUsername")
    public R getEmailByUsername(@RequestParam String username) {
        logger.debug("getEmailByUsername方法,,username:{}",username);

        // 构建查询条件
        Wrapper<YonghuEntity> wrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", username);

        // 执行查询
        YonghuEntity yonghu = yonghuService.selectOne(wrapper);

        if (yonghu == null) {
            return R.error("用户不存在");
        }

        // 返回邮箱信息（根据实际字段名称调整）
        return R.ok().put("data", Collections.singletonMap("email", yonghu.getYonghuEmail()));
    }


    // /**
    // * 注册
    // */
    // @IgnoreAuth
    // @PostMapping(value = "/register")
    // public R register(@RequestBody YonghuEntity yonghu, HttpServletRequest
    // request) {
    // // ValidatorUtils.validateEntity(user);
    // Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
    // .eq("username", yonghu.getUsername())
    // .or()
    // .eq("yonghu_phone", yonghu.getYonghuPhone())
    // .or()
    // .eq("yonghu_id_number", yonghu.getYonghuIdNumber())
    // ;
    // YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
    // if(yonghuEntity != null)
    // return R.error("账户或者用户手机号或者用户身份证号已经被使用");
    // yonghu.setCreateTime(new Date());
    // yonghuService.insert(yonghu);

    // return R.ok();
    // }

    /**
     * 注册
     */
    @IgnoreAuth
    @PostMapping(value = "/register")
    public R register(@RequestBody YonghuEntity yonghu, HttpServletRequest request) {
        // 检查用户名是否已被占用
        YonghuEntity usernameExists = yonghuService
                .selectOne(new EntityWrapper<YonghuEntity>().eq("username", yonghu.getUsername()));
        if (usernameExists != null) {
            return R.error("用户名已被占用");
        }

        // 检查手机号是否已被占用
        YonghuEntity phoneExists = yonghuService
                .selectOne(new EntityWrapper<YonghuEntity>().eq("yonghu_phone", yonghu.getYonghuPhone()));
        if (phoneExists != null) {
            return R.error("手机号已被占用");
        }

        // 检查身份证号是否已被占用
        YonghuEntity idNumberExists = yonghuService
                .selectOne(new EntityWrapper<YonghuEntity>().eq("yonghu_id_number", yonghu.getYonghuIdNumber()));
        if (idNumberExists != null) {
            return R.error("身份证号已被占用");
        }

        // 设置创建时间并插入用户数据
        yonghu.setCreateTime(new Date());
        yonghuService.insert(yonghu);

        return R.ok();
    }

    /**
     * 重置密码  id
     */
    @IgnoreAuth
    @GetMapping(value = "/resetPassword")
    public R resetPassword(Integer id, HttpServletRequest request) {
        YonghuEntity yonghu = yonghuService.selectById(id);
        yonghu.setPassword("123456");
        yonghuService.updateById(yonghu);
        return R.ok();
    }

    /**
     * 重置密码  name
     */
    @IgnoreAuth
    @GetMapping(value = "/resetPasswordByUsername")
    public R resetPasswordByUsername(@RequestParam("username") String username, HttpServletRequest request) {
        // 根据用户名查询用户
        YonghuEntity yonghu = yonghuService.selectByUserName(username);
        if (yonghu == null) {
            return R.error("用户不存在");
        }
        // 重置密码为123456
        yonghu.setPassword("123456");
        yonghuService.updateById(yonghu);
        return R.ok("密码已重置为123456");
    }

    /**
     * 发送邮件
     */
    @IgnoreAuth
    @GetMapping("/sendMail")
    public R sendMail(@RequestParam("toEmail") String toEmail,
                      @RequestParam("subject") String subject,
                      @RequestParam("text") String text,
                      HttpServletRequest request) {
        String apiKey = "fybjagiuzrqsbajf"; // 替换为你的 API 密钥
        String fromEmail = "758274530@qq.com"; // 替换为你的发件人邮箱
        String name = "发信昵称"; // 替换为你的发信昵称

        // 尝试 HTTPS
        String httpsUrl = "https://api.mmp.cc/api/mail?email=" + fromEmail + "&key=" + apiKey +
                "&mail=" + toEmail + "&title=" + subject +
                "&name=" + name + "&text=" + text;

        try {
            String response = callExternalApi(httpsUrl);
            return R.ok(response);
        } catch (Exception e) {
            // 如果 HTTPS 失败，尝试 HTTP
            String httpUrl = "http://api.mmp.cc/api/mail?email=" + fromEmail + "&key=" + apiKey +
                    "&mail=" + toEmail + "&title=" + subject +
                    "&name=" + name + "&text=" + text;

            try {
                String response = callExternalApi(httpUrl);
                return R.ok(response);
            } catch (Exception ex) {
                return R.error("邮件发送失败: " + ex.getMessage());
            }
        }
    }

    /**
     * 调用外部 API
     * @param url API 的完整 URL
     * @return API 的响应内容
     * @throws Exception 如果发生异常
     */
    private String callExternalApi(String url) throws IOException {
        // 使用 HttpURLConnection 调用外部 API
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 读取响应内容
            java.util.Scanner s = new java.util.Scanner(con.getInputStream()).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } else {
            throw new IOException("API 请求失败，响应码: " + responseCode);
        }
    }



    /**
     * 修改密码
     */
    @GetMapping(value = "/updatePassword")
    public R updatePassword(String oldPassword, String newPassword, HttpServletRequest request) {
        YonghuEntity yonghu = yonghuService.selectById((Integer) request.getSession().getAttribute("userId"));
        if (newPassword == null) {
            return R.error("新密码不能为空");
        }
        if (!oldPassword.equals(yonghu.getPassword())) {
            return R.error("原密码输入错误");
        }
        if (newPassword.equals(yonghu.getPassword())) {
            return R.error("新密码不能和原密码一致");
        }
        yonghu.setPassword(newPassword);
        yonghuService.updateById(yonghu);
        return R.ok();
    }

    /**
     * 忘记密码
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request) {
        YonghuEntity yonghu = yonghuService.selectOne(new EntityWrapper<YonghuEntity>().eq("username", username));
        if (yonghu != null) {
            yonghu.setPassword("123456");
            yonghuService.updateById(yonghu);
            return R.ok();
        } else {
            return R.error("账号不存在");
        }
    }

    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrYonghu(HttpServletRequest request) {
        Integer id = (Integer) request.getSession().getAttribute("userId");
        YonghuEntity yonghu = yonghuService.selectById(id);
        if (yonghu != null) {
            // entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties(yonghu, view);// 把实体数据重构到view中

            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 退出
     */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        logger.debug("list方法:,,Controller:{},,params:{}", this.getClass().getName(), JSONObject.toJSONString(params));

        CommonUtil.checkMap(params);
        PageUtils page = yonghuService.queryPage(params);

        // 字典表数据转换
        List<YonghuView> list = (List<YonghuView>) page.getList();
        for (YonghuView c : list)
            dictionaryService.dictionaryConvert(c, request); // 修改对应字典表字段

        return R.ok().put("data", page);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request) {
        logger.debug("detail方法:,,Controller:{},,id:{}", this.getClass().getName(), id);
        YonghuEntity yonghu = yonghuService.selectById(id);
        if (yonghu != null) {

            // entity转view
            YonghuView view = new YonghuView();
            BeanUtils.copyProperties(yonghu, view);// 把实体数据重构到view中

            // 修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        } else {
            return R.error(511, "查不到数据");
        }
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody YonghuEntity yonghu, HttpServletRequest request) {
        logger.debug("add方法:,,Controller:{},,yonghu:{}", this.getClass().getName(), yonghu.toString());
        Wrapper<YonghuEntity> queryWrapper = new EntityWrapper<YonghuEntity>()
                .eq("username", yonghu.getUsername())
                .or()
                .eq("yonghu_phone", yonghu.getYonghuPhone())
                .or()
                .eq("yonghu_id_number", yonghu.getYonghuIdNumber())
        // .notIn("yonghu_types", new Integer[]{102})
        ;
        logger.info("sql语句:" + queryWrapper.getSqlSegment());
        YonghuEntity yonghuEntity = yonghuService.selectOne(queryWrapper);
        if (yonghuEntity == null) {
            yonghu.setCreateTime(new Date());
            yonghu.setPassword("123456");
            yonghuService.insert(yonghu);

            return R.ok();
        } else {
            return R.error(511, "账户或者用户手机号或者用户身份证号已经被使用");
        }
    }

}
