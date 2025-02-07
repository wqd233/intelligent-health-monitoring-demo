const base = {
    get() {
        return {
            url : "http://localhost:8080/intelligent-health-monitoring-demo/",
            name: "intelligent-health-monitoring-demo",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/intelligent-health-monitoring-demo/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "健康系统"
        } 
    }
}
export default base
