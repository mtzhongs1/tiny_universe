<template>
  <div v-if="fols.length > 0">
    <div class="followers" v-for="(fol,index) in fols" :key="index" >
        <el-space style="margin: 20px">
          <el-avatar @click="toUser(fol.id)" fit="cover" :size="80" :src="fol.avatar" />
          <p>{{fol.username}}</p>
        </el-space>

      <div @click="follow(fol)" class="guan">
        <el-button v-if="!fol.isFollow" class="follow-btn" type="primary">关注</el-button>
        <el-button v-else class="qu-guan" type="success">已关注</el-button>
      </div>
    </div>
    <el-pagination style="padding-left: 45%"
                   v-model:current-page="page.pageNum"
                   :page-size="page.pageSize"
                   :size="fols.length"
                   :total = "page.total"
                   layout="total, prev, pager, next"
                   @current-change="getFols"
                   v-show="fols.length > 0"
    />
  </div>
  <div v-else style="height: 400px;display:flex;justify-content: center;align-items: center">
    <el-empty description="这里很冷清呢（┬_┬）" />
  </div>
</template>

<script setup>
import {inject, onMounted, reactive, ref, watch} from "vue";
import {doGet, doPostxwww} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {newRoute} from "@/util/router.js";
import {useRouter} from "vue-router";
let userId = inject("userId");
let fols = ref([]);
let page = reactive(
    {
      pageNum: 1,
      pageSize: 10,
      total: 1
    }
)
let router = useRouter();
onMounted(() => {
  getFols();
})
const toUser = (id) => {
  console.log(id);
  newRoute("/dashboard/user_detail/"+id,router);
}
const getFols = () => {
  if(userId === undefined || userId === null){
    return;
  }
  doGet("/user_active/follow/page",{
    pageNum: page.pageNum,
    pageSize: page.pageSize,
    userId: userId
  }).then((resp) => {
    if(resp.data.code === 1){
      page.total = resp.data.data.total;
      fols.value = resp.data.data.records;
    }else{
      ElMessage.error("服务器繁忙")
    }
  })
}
const follow = (fol) => {
  doPostxwww("/user_active/follow",{
    toUserId:fol.id,
    isFollow:!!fol.isFollow,
  }).then((resp) => {
    if(resp.data.code === 1){
      fol.isFollow = !fol.isFollow;
      //关注成功
      if(fol.isFollow){
        ElMessage.success("取关成功");
      }
      //取关成功
      else{
        ElMessage.success("关注成功");
      }
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
</script>

<style scoped>
.followers{
  position: relative;
}
.guan{
  position: absolute;
  right: 100px;
  top: 50%;
}
.qu-guan,.follow-btn{
  color: #ffffff;
}
.qu-guan:hover{
  color: #ffffff;
}
.follow-btn:hover{
  color: #ffffff;
  background: var(--common-color);
}
</style>
