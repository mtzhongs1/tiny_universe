<template>
    <!-- 添加tupe是为了防止提交表单 -->
    <button type="button" class = "btn">{{ message }}</button>
</template>
<script setup>
// 父组件传递内容
defineProps(['message'])
</script>
<style scoped>

.btn {
    margin-top: 30px;
    color: #ae00ff94;
    position: relative;
    overflow: hidden;
    text-transform: uppercase;
    letter-spacing: 2px;
    background: transparent;
    border: 0;
    outline: none;
    cursor: pointer;
}



.btn:hover {
    border-radius: 5px;
    color: #fff;
    background: #ae00ff94;
    box-shadow: 0 0 5px 0 #ae00ff94,
        0 0 25px 0 #ae00ff94,
        0 0 50px 0 #ae00ff94,
        0 0 100px 0 #ae00ff94;
    transition: all 1s linear;
}
.btn>span {
    position: absolute;
}

.btn>span:nth-child(1) {
    width: 100%;
    height: 2px;
    background: -webkit-linear-gradient(left, transparent, #03e9f4);
    left: -100%;
    top: 0px;
    animation: line1 1s linear infinite;
}

@keyframes line1 {

    50%,
    100% {
        left: 100%;
    }
}

.btn>span:nth-child(2) {
    width: 2px;
    height: 100%;
    background: -webkit-linear-gradient(top, transparent, #03e9f4);
    right: 0px;
    top: -100%;
    animation: line2 1s 0.25s linear infinite;
}

@keyframes line2 {

    50%,
    100% {
        top: 100%;
    }
}

.btn>span:nth-child(3) {
    width: 100%;
    height: 2px;
    background: -webkit-linear-gradient(left, #03e9f4, transparent);
    left: 100%;
    bottom: 0px;
    animation: line3 1s 0.75s linear infinite;
}

@keyframes line3 {

    50%,
    100% {
        left: -100%;
    }
}

.btn>span:nth-child(4) {
    width: 2px;
    height: 100%;
    background: -webkit-linear-gradient(top, transparent, #03e9f4);
    left: 0px;
    top: 100%;
    animation: line4 1s 1s linear infinite;
}

@keyframes line4 {

    50%,
    100% {
        top: -100%;
    }
}

</style>
