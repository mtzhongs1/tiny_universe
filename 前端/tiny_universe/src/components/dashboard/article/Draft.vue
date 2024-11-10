<template>
  <FuncButton @click="getDrafts" :message="'草稿箱'"></FuncButton>
  <el-dialog
      v-model="dialogVisible"
      title="草稿箱"
      width="1000"
      @close="closeDialog"
      :center="true"
  >
    <template #footer>
      <el-row justify="center" class="dialog-content">
        <div style="display: flex;gap: 20px;margin-bottom: 10px">
          <el-input v-model="draftName" placeholder="请输入标题"></el-input>
          <el-icon class="selectIcon" @click="getDrafts"><Search /></el-icon>
        </div>
        <el-col v-if="drafts.length > 0" class="drafts">
          <div class="draft-item" v-for="(draft,index) in drafts" :key="index" @click="toArticle(draft.id)">
            <div>
              <p>{{draft.title}}</p>
              <div style="opacity: 0.5">
                <p>创建时间：{{draft.createTime}}</p>
                <p>更新时间：{{draft.updateTime}}</p>
              </div>
              <el-icon class="deleteIcon" @click.stop="deleteDraft(draft.id)"><Delete /></el-icon>
            </div>
          </div>
          <el-pagination
              v-model:current-page="pageNum"
              :page-size="pageSize"
              :size="drafts.length"
              :total = "total"
              layout="total, prev, pager, next"
              @current-change="handleCurrentChange"
              style="margin:10px;text-align: center"
          />
        </el-col>
        <el-col v-else style="height: 500px;display:flex;justify-content: center;align-items: center">
          <el-empty description="空空空空空如也~" />
        </el-col>
      </el-row>

    </template>
  </el-dialog>
</template>
<script setup>
import FuncButton from "@/components/common/button/FuncButton.vue";
import {doDelete, doGet} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {inject, ref} from "vue";
import {useRouter} from "vue-router";
import {newRoute} from "@/util/router.js";
let router = useRouter();
let drafts = ref([]);
let total = ref(0);
let pageSize = ref(3);
let pageNum = ref(1);
let draftName = ref('')

let dialogVisible = ref(false);
let user = inject("user");

//分页相关操作
const handleCurrentChange = (val) => {
  pageNum.value = val;
  getDrafts();
}

//草稿相关操作
const getDrafts = () => {
  doGet("/draft/page",{
    userId:user.id,
    pageNum:pageNum.value,
    pageSize:pageSize.value,
    name:draftName.value,
  }).then((resp) => {
    if(resp.data.code === 1){
      total.value = resp.data.data.total;
      drafts.value = resp.data.data.records;
      dialogVisible.value = true;
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const deleteDraft = (id) => {
  const ids = [id];
  doDelete("/draft",JSON.stringify(ids)).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("删除成功");
      getDrafts();
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const closeDialog = () => {
  dialogVisible.value = false;
  draftName.value = ''
}
//草稿转为文章
const toArticle = (id) => {
  newRoute("/dashboard/article_editor/"+id,router);
}

</script>
<style scoped>
.drafts{
  display: flex;
  flex-direction: column;
  place-items: center;
}
.draft-item {
  z-index: 1;
  cursor: pointer;
  padding: 5px 0;
  width: 100%;
}
.draft-item:hover{
  background: var(--main-color);
}
.deleteIcon :hover,.selectIcon :hover{
  z-index: 10;
  color: var(--common-color);
  cursor: pointer;
}

</style>
