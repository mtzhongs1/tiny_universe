import axios from "axios"
import { getTokenName, removeToken } from "../util/util";
import { ElMessageBox } from "element-plus";

//TODO:配置后端接口路径前缀
axios.defaults.baseURL = "http://localhost:8888/";
// axios.defaults.baseURL = "/api";

//TODO:封装axios的http异步请求
export function doGet(url,params){
    return axios({
        method: "get",
        url: url,
        params: params,
        dataType: "json",
    });
}

//TODO:处理@RequestBody的数据
export function doPost(url,data){
    return axios({
        method: "post",
        url: url,
        data: data,
        dataType: "json",
        // TODO:后端参数有@RequestBody时，要用json的形式
        headers: {
            "Content-Type": "application/json;charset=UTF-8"
        }
    });
}
export function doPut(url,data){
    return axios({
        method: "put",
        url: url,
        data: data,
        dataType: "json",
        headers: {
            "Content-Type": "application/json;charset=UTF-8"
        }
    });
}


//TODO：处理@RequestParam的数据
export function doPostxwww(url,data){
    return axios({
        method: "post",
        url: url,
        params : data,
    });
}

//TODO:上传文件的axios请求
export function doPostFile(url,data){
    return axios({
        method: "post",
        url: url,
        data: data,
        dataType: "json",
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}
export function doDelete(url,data){
    return axios({
        method: "delete",
        url: url,
        data: data,
        dataType: "json",
        // TODO:后端参数有@RequestBody时，要用json的形式
        headers: {
            "Content-Type": "application/json;charset=UTF-8"
        }
    });
}

//TODO：处理@RequestParam的数据
export function doDeletexwww(url,data){
    return axios({
        method: "delete",
        url: url,
        params : data,
    });
}

// TODO:添加请求拦截器(将token传给后端)，在axios官网有
axios.interceptors.request.use(function (config) {
    // TODO在请求头放token(Vue.prototype是js文件获取Vue属性的方法，而Vue组件则直接this.即可)
    let token = window.sessionStorage.getItem(getTokenName());
    if(!token){
        token = window.localStorage.getItem(getTokenName());
    }
    config.headers['token'] = token;
    return config;
  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

  // 添加响应拦截器
axios.interceptors.response.use(function (response) {
    // 2xx 范围内的状态码都会触发该函数。
    if(response.data.code === 900){
        // TODO:token有错误，删除token
        removeToken();
        // TODO:确认消息框
        ElMessageBox.confirm(
            '登录失效，请重新登录',
            'Warning',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        ).then(() => {
                window.location.href="/"
        })
    }
    return response;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });
