import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
    // 解决多次点击左侧菜单报错问题
    const VueRouterPush = VueRouter.prototype.push
    VueRouter.prototype.push = function push (to) {
    return VueRouterPush.call(this, to).catch(err => err)
    }
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'
import beifen from '@/views/modules/databaseBackup/beifen'
import huanyuan from '@/views/modules/databaseBackup/huanyuan'

     import users from '@/views/modules/users/list'
    import dictionary from '@/views/modules/dictionary/list'
    import forum from '@/views/modules/forum/list'
    import gonggao from '@/views/modules/gonggao/list'
    import jiankangrizhi from '@/views/modules/jiankangrizhi/list'
    import news from '@/views/modules/news/list'
    import newsCollection from '@/views/modules/newsCollection/list'
    import newsLiuyan from '@/views/modules/newsLiuyan/list'
    import yonghu from '@/views/modules/yonghu/list'
    import config from '@/views/modules/config/list'
    import dictionaryForum from '@/views/modules/dictionaryForum/list'
    import dictionaryForumState from '@/views/modules/dictionaryForumState/list'
    import dictionaryGonggao from '@/views/modules/dictionaryGonggao/list'
    import dictionaryJiankangrizhi from '@/views/modules/dictionaryJiankangrizhi/list'
    import dictionaryNews from '@/views/modules/dictionaryNews/list'
    import dictionaryNewsCollection from '@/views/modules/dictionaryNewsCollection/list'
    import dictionarySex from '@/views/modules/dictionarySex/list'
    import dictionaryShangxia from '@/views/modules/dictionaryShangxia/list'
    import dictionaryShuimian from '@/views/modules/dictionaryShuimian/list'
    import dictionaryTuijian from '@/views/modules/dictionaryTuijian/list'
    import dictionaryXiyan from '@/views/modules/dictionaryXiyan/list'
    import dictionaryYinjiu from '@/views/modules/dictionaryYinjiu/list'
    import dictionaryZhuangtai from '@/views/modules/dictionaryZhuangtai/list'





//2.配置路由   注意：名字
const routes = [{
    path: '/index',
    name: '首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '首页',
      component: Home,
      meta: {icon:'', title:'center'}
    }, {
      path: '/updatePassword',
      name: '修改密码',
      component: UpdatePassword,
      meta: {icon:'', title:'updatePassword'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    }, {
        path: '/huanyuan',
        name: '数据还原',
        component: huanyuan
    }, {
        path: '/beifen',
        name: '数据备份',
        component: beifen
    }, {
        path: '/users',
        name: '管理信息',
        component: users
    }
    ,{
        path: '/dictionaryForum',
        name: '知识类型',
        component: dictionaryForum
    }
    ,{
        path: '/dictionaryForumState',
        name: '帖子状态',
        component: dictionaryForumState
    }
    ,{
        path: '/dictionaryGonggao',
        name: '健康公告类型',
        component: dictionaryGonggao
    }
    ,{
        path: '/dictionaryJiankangrizhi',
        name: '健康日志类型',
        component: dictionaryJiankangrizhi
    }
    ,{
        path: '/dictionaryNews',
        name: '健康资讯类型',
        component: dictionaryNews
    }
    ,{
        path: '/dictionaryNewsCollection',
        name: '收藏表类型',
        component: dictionaryNewsCollection
    }
    ,{
        path: '/dictionarySex',
        name: '性别类型',
        component: dictionarySex
    }
    ,{
        path: '/dictionaryShangxia',
        name: '上下架',
        component: dictionaryShangxia
    }
    ,{
        path: '/dictionaryShuimian',
        name: '睡眠情况',
        component: dictionaryShuimian
    }
    ,{
        path: '/dictionaryTuijian',
        name: '推荐饭食',
        component: dictionaryTuijian
    }
    ,{
        path: '/dictionaryXiyan',
        name: '吸烟',
        component: dictionaryXiyan
    }
    ,{
        path: '/dictionaryYinjiu',
        name: '饮酒',
        component: dictionaryYinjiu
    }
    ,{
        path: '/dictionaryZhuangtai',
        name: '状态',
        component: dictionaryZhuangtai
    }
    ,{
        path: '/config',
        name: '轮播图',
        component: config
    }


    ,{
        path: '/dictionary',
        name: '字典',
        component: dictionary
      }
    ,{
        path: '/forum',
        name: '健康知识交流区',
        component: forum
      }
    ,{
        path: '/gonggao',
        name: '健康公告',
        component: gonggao
      }
    ,{
        path: '/jiankangrizhi',
        name: '健康日志',
        component: jiankangrizhi
      }
    ,{
        path: '/news',
        name: '健康资讯',
        component: news
      }
    ,{
        path: '/newsCollection',
        name: '健康资讯收藏',
        component: newsCollection
      }
    ,{
        path: '/newsLiuyan',
        name: '健康资讯留言',
        component: newsLiuyan
      }
    ,{
        path: '/yonghu',
        name: '用户',
        component: yonghu
      }


    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon:'', title:'register'}
  },
  {
    path: '/',
    name: '首页',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})

export default router;
