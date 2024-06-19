import {ElMessageBox} from "element-plus";

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
    ElMessageBox.confirm(
        '确定要退出吗？',
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
    }else{
        //夜晚
        color.header_color = getCssVariable('--header-black-color');
        color.main_color = getCssVariable('--main-black-color');
        color.main_beside_color = getCssVariable('--main-beside-black-color');
        color.text_color = getCssVariable('--text-black-color');
        color.message_color = getCssVariable('--message-black-color');
        color.message_text_color = getCssVariable('--message-text-black-color');
        color.shadow_color = getCssVariable('--shadow-black-color');
    }
    setProperty('--header-color',color.header_color);
    setProperty('--main-color',color.main_color);
    setProperty('--main-beside-color',color.main_beside_color);
    setProperty('--text-color',color.text_color);
    setProperty('--message-color',color.message_color);
    setProperty('--message-text-color',color.message_text_color);
    setProperty('--shadow-color',color.shadow_color);
}
