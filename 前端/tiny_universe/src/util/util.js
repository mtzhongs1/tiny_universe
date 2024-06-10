
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
