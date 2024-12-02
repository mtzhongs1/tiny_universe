<template>
  <div class="class-room">
    <h1 @click="startLearn">
      开始学习
    </h1>
    <div class="elx-dialog">
      <el-dialog v-model="isDialogAlive" width="800" center @close="closeDialog">
        <div v-if="isObject === 0">
          <h2 style="text-align: center">请选择科目学习</h2>
          <el-space :size="10">
            <el-card @click="toProblem('语文')" shadow="hover">
              <template #header>语文</template>
              <img
                  src="@/assets/object/语文.png"
                  style="width: 100%"
                  alt=""/>
            </el-card>
            <el-card @click="toProblem('数学')" shadow="hover">
              <template #header>数学</template>
              <img
                  src="@/assets/object/数学.png"
                  style="width: 100%"
                  alt=""/>
            </el-card>
            <el-card @click="toProblem('英语')" shadow="hover">
              <template #header>英语</template>
              <img
                  src="@/assets/object/英语.png"
                  style="width: 100%"
                  alt=""/>
            </el-card>
          </el-space>
        </div>
        <div v-else-if="isObject === 1">
          <div class="problem-list">
            <p style="margin-right: 50px">题目：{{problem.content}}</p>
            <p style="margin-top: 20px">A：{{problem.A}}</p>
            <p>B：{{problem.B}}</p>
            <p>C：{{problem.C}}</p>
          </div>
          <div class="select-option">
            <div style="white-space: nowrap">选择:</div>
            <el-select
                v-model="option.selected"
                placeholder="Select"
                popper-class="intelligent-select1"
            >
              <el-option
                  v-for="item in option.options"
                  :key="item"
                  :label="item"
                  :value="item"
              />
            </el-select>
            <el-button type="success" @click="commit">提交</el-button>
            <p class="next-item" @click="nextProblem">{{index === 4 ? '完成' : '下一题'}}</p>
          </div>
          <div v-show="isCommited" style="margin-top: 100px;padding: 50px">
            <el-icon :color="'red'" class="redI" v-if="option.selected !== problem.T"><CloseBold /></el-icon>
            <el-icon :color="'yellow'" class="redI" v-else><Select /></el-icon>
            <p>答案解析：{{problem.P}}</p>
          </div>
        </div>
        <div v-else>
          <div style="text-align: center">
            <p style="font-size: 20px">恭喜你，学习成功！</p>
            <p style="font-size: 18px">你的成绩为：{{final.score}}分</p>
            <p>{{final.evaluate}}</p>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>

</template>

<script setup>
import {reactive, ref} from "vue";
import {ElLoading, ElMessage} from "element-plus";
import {getTokenName} from "@/util/util.js";

let isObject = ref(0);
const isDialogAlive = ref(false);
let isCommited = ref(false);
let final = reactive({
  score:0,
  evaluate: ''
})
let option = reactive({
  options:[
    'A','B','C'
  ],
  selected:'A'
})
let scores = reactive({
  score:0
})
let index = ref(0);
let problem = reactive({
  content: '',
  A:'',
  B:'',
  C:'',
  T:'',
  P:'',
  type:'',
})
const evaluate = {
  '0': '虽然这次没有获得分数，但请不要灰心。每个人的学习旅程都有起起落落，重要的是从中汲取经验，找到前进的方向。你的勇气尝试本身就值得肯定，接下来的路，我们一起加油！',
  '25': '你迈出了坚实的一步，这25分不仅代表了你的努力，更是你勇于探索未知、挑战自我的证明。或许还有一些地方需要加深理解，但请记住，每一次小小的进步都是通往成功的宝贵基石',
  '50': '恭喜你，已经达到了一半的里程碑！这说明你已经掌握了部分要点，具备良好的学习基础和潜力。接下来，可以针对自己的薄弱环节进行针对性提升，相信不久的将来，你会收获更加耀眼的成绩。',
  '75': '你的表现非常出色，75分是一个令人瞩目的成绩，展现了你对知识的深刻理解和扎实掌握。你不仅具备解决问题的能力，还展现了良好的学习态度和习惯。继续保持这份热情和努力，未来定能更上一层楼。',
  '100': '完美无瑕，你以满分的成绩证明了自己的实力与才华！你的努力、专注和坚持赢得了这份荣耀。请享受这份成就感，同时也要意识到，这只是你众多成功中的一个起点。保持谦逊，继续探索未知，未来的你会更加辉煌！'
};
const commit = () => {
  isCommited.value = true;
  scores[index.value] = option.selected !== problem.T ? 0 : 25;
}
const closeDialog = () => {
  isDialogAlive.value = false;
  isObject.value = 0;
  index.value = 0;
  closeProblem();
}
const closeProblem = () => {
  Object.assign(problem,{content:'',A:'',B:'',C:'',T:'',P:''})
  option.selected = 'A';
  problemData = '';
  isCommited.value = false;
}
const startLearn = () => {
  isDialogAlive.value = true;
}
const nextProblem = () => {
  if(!isCommited.value){
    ElMessage.error('请先提交答案');
    return;
  }
  if(index.value === 4){
    for (let i = 0; i < 5; i++){
      final.score += scores[i];
    }
    final.evaluate = evaluate[final.score];
    isObject.value = 2;
    return;
  }

  closeProblem();
  index.value++;
  toProblem(problem.type);
}
let problemData = '';
const toProblem = (type) => {
  const loadingInstance = ElLoading.service();
  isObject.value = 1;

  // const eventSource = new EventSource(
  //     // todo 手动填写完整的后端地址
  //     `http://localhost:8082/gpt/sse/produceProblem/${type}`
  // );
  let token = window.sessionStorage.getItem(getTokenName());
  if(!token){
    token = window.localStorage.getItem(getTokenName());
  }
  // const eventSource = new EventSource("http://120.46.95.186:8888/gpt/sse/produceProblem"+
  //     `?type=${type}`)
  const eventSource = new EventSource("http://localhost:8888/gpt/sse/produceProblem"+
      `?type=${type}`)
  // eventSource.addEventListener('produceProblem', function (event) {
  //   alert(1)
  //   Object.assign(problem,JSON.parse(event.data))
  // })
  eventSource.onmessage = function (event) {
    problemData += event.data.replace(/"/g, '');
    const matches = problemData.match(/\[\[(\w+):([^\]]+)\]\]/g);
    const result = {};
    if(matches != null){
      matches.forEach(match => {
        // 去掉外面的 [[]]
        const cleanMatch = match.slice(2, -2);
        // 分割关键字和值
        const [key, value] = cleanMatch.split(':');
        // 存储到结果对象中
        result[key] = value;
      })
    }
    console.log(result);
    Object.assign(problem,result);

    // var data = event.data.replace(/"/g, '');
    // problemData += data;
    //
    // let split = data.split(":");
    // switch (split[0]){
    //   case 'content':
    //     problem.content = split[1];
    //     break;
    //   case 'A':
    //     problem.a = split[1];
    //     break;
    //   case 'B':
    //     problem.b = split[1];
    //     break;
    //   case 'C':
    //     problem.c = split[1];
    //     break;
    //   case 'T':
    //     problem.t = split[1];
    //     break;
    //   case 'P':
    //     problem.p = split[1];
    //     break;
    // }
  }
  eventSource.onopen = function () {
    console.log("连接成功");
  };
  eventSource.onerror = function (event) {
    if (event.eventPhase === EventSource.CLOSED) {
      console.log("关闭连接");
      eventSource.close();
    } else {
      eventSource.close();
    }
  };
  problem.type = type;
  loadingInstance.close();
  // doGet('/sse/gpt/produceProblem',{type:type}).then((resp) => {
  //   loadingInstance.close();
  //   if(resp.data.code === 1){
  //     Object.assign(problem,resp.data.data);
  //     problem.type = type;
  //   }else{
  //     ElMessage.error('服务器繁忙');
  //   }
  // })
}
</script>

<style scoped>
.class-room{
  div{
    color: #fff;
  }
  display: flex;
  width: 100vw;
  height: 100vh;
  margin-top: -50px;
  background: url("@/assets/教室.png");
  background-size: cover;
  justify-content: center;
  align-items: center;
  font-size: 18px;
}
:deep(.el-tooltip__trigger){
  box-shadow: 0 0 3px #fff;
}
:deep(.el-select__placeholder){
  color: #fff;
}
.next-item{
  margin-left: 150px;
}
.next-item:hover{
  color: var(--common-color)!important;
  cursor: pointer;
}
.problem-list{
  position: absolute;
  top: 100px;
  left: 100px;
}
.select-option{
  position: absolute;
  top: 70%;
  left: 40%;
  display: flex;
  gap: 20px;
  align-items: center;
}
:deep(.el-button--success){
  color: #fff;
}
:deep(.el-select){
  width: 70px;
  background: transparent;
}
/*深度子样式修饰*/
.el-select ::v-deep(.el-select__wrapper){
  background: transparent;
}
:deep(.el-card){
  width: 200px;
  background: rgba(0, 255, 208, 0.1);
  border: none;
  text-align: center;
  cursor: pointer;
  border-radius: 14.06px 14.06px 14.06px 14.06px; /* 转换 rpx 到 px */
}
:deep(.el-dialog){
  background: url("@/assets/黑板.png");
  background-size: cover;
  height: 500px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}
:deep(.el-dialog__title){
  color: #e7cb55!important;
  font-size: 20px;
}
:deep(.el-dialog__headerbtn .el-dialog__close){
  color: #ffca00!important;
  font-size: 30px;
  font-weight: bold;
}
h1 {
  background: linear-gradient(to right, #a29c24, #19ead9, #9964d7);
  -webkit-background-clip: text;
  color: transparent!important;
  font-size: 48px;
  font-weight: 600;
  animation: rotate 0.3s ease infinite;
  /* 注意，要开启绝对定位哦 */
  position: absolute;
  cursor: pointer;
}

@keyframes rotate {
  0% {
    transform: rotate(0);
  }

  20% {
    transform: rotate(-2deg);
  }

  60% {
    transform: rotate(0);
  }

  80% {
    transform: rotate(2deg);
  }

  100% {
    transform: rotate(0);
  }
}

h1:nth-child(2) {
  margin-left: 108px;
}

h1:nth-child(3) {
  margin-left: 216px;
}
</style>
