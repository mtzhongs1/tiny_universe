<template>
	<div class="center">
		<div class="logon">
			<div :class="overlaylong">
				<form :ref="LoginFormRef" style="text-align: center" v-if="disfiex === 0">
					<h2 class="overlaylongH2">Sign in</h2>
          <div class="overlaylong-Signin">
            <label v-show="false" for="username"></label>
            <input name="username" id=“username” v-model = "user.username" type="text" placeholder="username">
            <label v-show="false" for="password"></label>
            <input name="password" id="password" v-model = "user.password" type="text" placeholder="password">
            <h3>Forgot your password?</h3>
            <!-- <button @click="login" class="btn">Sign in</button> -->
          </div>
            <!-- TODO:自定义按钮组件 -->
            <WowButton message="Sign in" @click = "login"></WowButton>
				</form>
				<form :ref="RegisterFormRef" style="text-align: center" v-if="disfiex === 1">
					<h2 class="overlaylongH2">Sign up</h2>
          <div class="overlaylong-Signup">
            <label v-show="false" for="username"></label>
            <input name="username" id="username" v-model = "user.username" type="text" placeholder="username">
            <label v-show="false" for="password"></label>
            <input name="password" id="password" v-model = "user.password" type="text" placeholder="password">
            <label v-show="false" for="email"></label>
            <input name="email" id="email" v-model = "user.email" type="text" placeholder="email">
            <span>
            <label v-show="false" for="code"></label>
						<input name="code" id="code" v-model = "user.code" style="width: 55px;" type="text" placeholder="code">
						<button :disabled="codeProp.isTimerActive" @click="sendCode" class="sendBtn">
							<span class="button_top"> {{ codeProp.msg }} </span>
						</button>
					</span>
          </div>
            <WowButton message="Sign up" @click = "register"></WowButton>
            <!-- <button @click = "register" class="btn">Sign up</button> -->

				</form>

			</div>
			<div :class="overlaytitle">
				<div class="overlaytitle-Signin" v-show="disfiex === 0">
					<h2 class="overlaytitleH2">欢迎回来，朋友!</h2>
					<p class="overlaytitleP">
						在这里，拥有独属于自己的自由天地！
					</p>
					<div class="buttongohs" @click="Signin">Sign up</div>
				</div>
				<div class="overlaytitle-Signup" v-show="disfiex === 1">
					<h2 class="overlaytitleH2">你好，陌生人!</h2>
					<p class="overlaytitleP">点击注册，开始旅途。</p>
					<div class="buttongohs" @click="Signup">Sign in</div>
				</div>
			</div>

		</div>

	</div>
</template>

<script setup>

import { doPost, doPostxwww } from '@/http/httpRequest';
import { ElMessage } from 'element-plus';
import { reactive, ref} from 'vue';
import WowButton from '@/components/common/button/WowButton.vue';
import { useRouter } from 'vue-router';
import { setToken } from '@/util/util';
import {Validator} from "@/util/validator.js";
let overlaylong = ref('overlaylong');
let overlaytitle = ref('overlaytitle');
let disfiex = ref(0);
let user = reactive({
	username: '',
	password: '',
	email: '',
	code: ''
})
let codeProp = reactive({
	msg: "发送验证码",
	isTimerActive: false
})
const router = useRouter();
let LoginFormRef = ref(null);
let RegisterFormRef = ref(null);
const validateLogin = () => {
  var validator = new Validator();
  validator.add(user.username,[{
    strategy: 'isNotEmpty',
    errorMsg: '用户名不能为空'
  },{
    strategy: 'minLength:2',
    errorMsg: '用户名长度不能小于2位'
  }])

  validator.add(user.password,[{
    strategy: 'isNotEmpty',
    errorMsg: '密码不能为空'
  },{
    strategy: 'minLength:6',
    errorMsg: '密码长度不能小于6位'
  }])
  return validator.start();
}
const validateRegister = () => {
  var validator = new Validator();
  validator.add(user.username,[{
    strategy: 'isNotEmpty',
    errorMsg: '用户名不能为空'
  },{
    strategy: 'minLength:2',
    errorMsg: '用户名长度不能小于2位'
  }])
  validator.add(user.password,[{
    strategy: 'isNotEmpty',
    errorMsg: '密码不能为空'
  },{
    strategy: 'minLength:6',
    errorMsg: '密码长度不能小于6位'
  }])
  validator.add(user.email,[{
   strategy: 'isNotEmpty',
   errorMsg: '邮箱不能为空'
  },{
    strategy: 'isEmail',
    errorMsg: '邮箱格式不正确'
  }])
  validator.add(user.code,[{
    strategy: 'isNotEmpty',
    errorMsg: '验证码不能为空'
  }])
  return validator.start();
}
function Signin() {
	overlaylong.value = "overlaylongleft"
	overlaytitle.value = "overlaytitleright"
	setTimeout(() => {
		disfiex.value = 1
	}, 200)
}
function Signup() {
	overlaylong.value = "overlaylongright"
	overlaytitle.value = "overlaytitleleft"

	setTimeout(() => {
		disfiex.value = 0
	}, 200)
}
function sendCode() {
	doPostxwww('/email/sendMailCode',{to : user.email}).then((resp) => {
		if(resp.data.code === 1){
			ElMessage.success('发送成功');
		}else{
			ElMessage.error('服务器繁忙');
		}
	})
	codeProp.isTimerActive = true;
	var time = 60;
	const timer = setInterval(() => {
		time--;
		codeProp.msg = time + 's后发送';
		if(time <= 0){
			codeProp.msg = '发送验证码';
			codeProp.isTimerActive = false;
			clearInterval(timer);
		}
	},1000);

}

function login(){
  const errorMsg = validateLogin();
  if(errorMsg){
    ElMessage.error(errorMsg);
    return;
  }
	let formData = new FormData();
	formData.append('username',user.username);
	formData.append('password',user.password);
	doPost("/user/login",formData).then((resp) => {
		if(resp.data.code === 1){
			setToken(resp.data.data);
			ElMessage.success('登录成功');
			router.push('/dashboard');
		}else{
			ElMessage.error('用户名或密码错误');
		}
	})
}
function register(){
  const errorMsg = validateRegister();
  if(errorMsg){
    ElMessage.error(errorMsg);
    return;
  }
	let formData = new FormData();
	formData.append('username',user.username);
	formData.append('password',user.password);
	formData.append('code',user.code);
	formData.append('email',user.email);
	doPost("/user/register",formData).then((resp) => {
		if(resp.data.code === 1){
			ElMessage.success('注册成功');
			const temp = {username: '',password: '',email:'',code:''};
			Object.assign(user,temp);
			Signin();
		}else{
			ElMessage.error(resp.data.msg);
		}
	})
}

</script>
<style scoped>
/* 导入css样式 */
@import url('@/views/css/button.css');
@import url('@/views/css/input.css');

.center {
	height: 100vh;
	background-image: url('@/assets/花海.jpg');
	background-size: cover;
	background-position: center;
	background-repeat: no-repeat;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;
}

h1 {
	font-size: 30px;
	color: rgb(0, 0, 0);
}


.logon {

	border-radius: 10px;
	box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
	/* position: relative;
		overflow: hidden; */
	width: 768px;
	max-width: 100%;

	height: 480px;
	display: flex;
	box-shadow: 0 0 5px 1px rgb(0, 0, 0);
	background: transparent;
	overflow: hidden;
}

.overlaylong {
	border-radius: 10px 0 0 10px;
	width: 50%;
	height: 100%;
	background-image: linear-gradient(180deg, #8a2be2, #9370db, #e6e6fa);

	display: flex;
	align-items: center;
	justify-content: center;
}

.overlaylongleft {
	border-radius: 0px 10px 10px 0px;
	width: 50%;
	height: 100%;
	background-image: linear-gradient(180deg, #8a2be2, #9370db, #e6e6fa);
	transform: translateX(100%);
	transition: transform 0.6s ease-in-out;
	display: flex;
	align-items: center;
	justify-content: center;
}

.overlaylongright {
	border-radius: 10px 0 0 10px;
	width: 50%;
	height: 100%;
	background-image: linear-gradient(180deg, #8a2be2, #9370db, #e6e6fa);
	transform: translateX(0%);
	transition: transform 0.6s ease-in-out;
	display: flex;
	align-items: center;
	justify-content: center;
}

.overlaytitle {
	border-radius: 0px 10px 10px 0px;
	width: 50%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0);
	display: flex;
	align-items: center;
	justify-content: center;
}


.overlaytitleH2 {
	font-size: 30px;
	color: #fff;
	margin-top: 20px;
}

.overlaytitleP {
	font-size: 15px;
	color: #fff;
	margin-top: 20px;
}

.overlaytitleleft {
	border-radius: 0px 10px 10px 0px;
	width: 50%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0);
	display: flex;
	align-items: center;
	justify-content: center;
	transform: translateX(0%);
	transition: transform 0.6s ease-in-out;
}

.overlaytitleright {
	border-radius: 0px 10px 10px 0px;
	width: 50%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0);
	display: flex;
	align-items: center;
	justify-content: center;
	transform: translateX(-100%);
	transition: transform 0.6s ease-in-out;
}

.overlaytitle-Signin {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;
}

.overlaytitle-Signup {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;
}

.overlaylong-Signup > *{
  flex: 0 0 auto;
  align-self: flex-start;
}

.buttongohs {
	width: 180px;
	height: 40px;
	border-radius: 50px;
	border: 1px solid #fff;
	color: #fff;
	font-size: 15px;
	text-align: center;
	line-height: 40px;
	margin-top: 40px;
	cursor: pointer;
}

.buttongohs:hover {
	/* 放大 */
	transform: scale(1.05);
}

.overlaylongH2 {
	font-size: 25px;
	color: black;
	/* width: 250px; */
}

.overlaylong-Signin {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: flex-start;
  flex-direction: column;
}

.overlaylong-Signup {
	display: flex;
  flex-wrap: nowrap;
	align-items: center;
	justify-content: flex-start;
	flex-direction: column;
}

/* input {
		background-color: #eee;
		border: none;
		padding: 12px 15px;
		margin: 10px 0;
		width: 180px;
	} */
h3 {
	font-size: 10px;
	margin-top: 10px;
	cursor: pointer;
}
</style>
