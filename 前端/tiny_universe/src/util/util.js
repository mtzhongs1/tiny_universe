import {ElMessageBox} from "element-plus";
import {nextTick} from "vue";

export function getTokenName(){
    return "token";
}

export function setToken(token){
    window.localStorage.setItem(getTokenName(),token)
}

// 移除浏览器存储的token
export function removeToken(){
    window.localStorage.removeItem(getTokenName())
}

export function getToken(){
    return window.localStorage.getItem(getTokenName())
}

export function getUserId(){
    return window.localStorage.getItem("userId");
}

export function getSystemError(){
    return "系统繁忙,请稍后再试";
}

export function getBlob(file,type,fileName){
    var blob = new Blob([file], {type: type});
    return new File([blob],fileName+new Date()+".html");
}

export function confirm(){
    return ElMessageBox.confirm(
        '你决定好了吗？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    );
}

export function setProperty(key, value){
    document.documentElement.style.setProperty(key,value);
}

export function getCssVariable(key){
    const rootStyle = window.getComputedStyle(document.documentElement);
    return rootStyle.getPropertyValue(key);
}



export function setCssVariable(theme,color){
    if(!theme){
        //白天
        color.header_color = getCssVariable('--header-white-color');
        color.main_color = getCssVariable('--main-white-color');
        color.main_beside_color = getCssVariable('--main-beside-white-color');
        color.text_color = getCssVariable('--text-white-color');
        color.message_color = getCssVariable('--message-white-color');
        color.message_text_color = getCssVariable('--message-text-white-color');
        color.shadow_color = getCssVariable('--shadow-white-color');
        color.comment_color = getCssVariable('--comment-white-color');
        color.compare_color = getCssVariable('--compare-white-color');
    }else{
        //夜晚
        color.header_color = getCssVariable('--header-black-color');
        color.main_color = getCssVariable('--main-black-color');
        color.main_beside_color = getCssVariable('--main-beside-black-color');
        color.text_color = getCssVariable('--text-black-color');
        color.message_color = getCssVariable('--message-black-color');
        color.message_text_color = getCssVariable('--message-text-black-color');
        color.shadow_color = getCssVariable('--shadow-black-color');
        color.comment_color = getCssVariable('--comment-black-color');
        color.compare_color = getCssVariable('--compare-black-color');
    }
    setProperty('--header-color',color.header_color);
    setProperty('--main-color',color.main_color);
    setProperty('--main-beside-color',color.main_beside_color);
    setProperty('--text-color',color.text_color);
    setProperty('--message-color',color.message_color);
    setProperty('--message-text-color',color.message_text_color);
    setProperty('--shadow-color',color.shadow_color);
    setProperty('--comment-color',color.comment_color);
    setProperty('--compare-color',color.compare_color)
}

export const reloadUtil = (isRouterAlive) => {
    isRouterAlive.value = false;
    nextTick(() => {
        isRouterAlive.value = true;
    });
};

export function isEmpty(obj) {
    // 数字判空
    if (isNumber(obj)) {
        return false;
    }
    // 字符串和数组判空
    if (obj && obj.length > 0) {
        return false;
    }
    // 按照对应的数据类型进行数据判空
    let objType = Object.prototype.toString.call(obj);
    // 字符串和数组判空
    if (objType === '[object Array]' || objType === '[object String]') {
        if (obj && obj.length > 0) {
            return false;
        }
    }
    // 如果是对象
    if (objType === '[object Object]' && !(JSON.stringify(obj) == "{}")) {
        return false;
    }
    return true;
}

export function isNumber(obj) {
    if (parseFloat(obj).toString() == "NaN") {
        return false;
    }
    return true;
}
