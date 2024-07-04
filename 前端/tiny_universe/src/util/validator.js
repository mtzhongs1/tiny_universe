const strategies = {
    isNotEmpty(errorMsg) {
        return this === '' ?
            errorMsg : void 0
    },
    minLength(length, errorMsg) {
        return this.length < length ?
            errorMsg : void 0
    },
    maxLength(length, errorMsg) {
        return this.length > length ?
            errorMsg : void 0
    },
    isEmail(errorMsg){
        return !/^\w+([+-.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(this) ?
            errorMsg : void 0
    },
    arrayIsNotEmpty(errorMsg){
        return this.length === 0 ? errorMsg : void 0
    },
}
export class Validator {
    constructor() {
        //保存校验规则
        this.cache = [];
    }
    add(value,rules){
        for(let rule of rules){
           // minLength:6 => ['minLength','6'];
           const strategyArr = rule.strategy.split(":")
           const errorMsg = rule.errorMsg;
           // 闭包：即catch保存的函数本身
           this.cache.push(() => {
                //strategy: 用户选择的
                const strategy = strategyArr.shift();
                //把value值插入到strategyArr的开头
                // strategyArr.unshift(dom.value);

                //把errorMsg添加到strategyArr的末尾,此时strategyArr为，【6，errorMsg】
                strategyArr.push(errorMsg)
                //strategies不是数组，而是对象，调用对应的函数,这里传过去的dom会被作为this对象，strategyArr则是对应的实参
                return strategies[strategy].apply(value,strategyArr)
           })
        }
    }
    start(){

        for(const validatorFunc of this.cache){
            const errorMsg = validatorFunc();
            // 如果有返回errorMsg
            if(errorMsg){
                return errorMsg;
            }
        }
    }
}
