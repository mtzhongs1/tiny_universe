<!--TODO:富文本编辑器组件-->
<template>
  <div style="padding: 50px">
    <div class="editor-container">
      <el-row class="editor-toolbar">
      <!--TODO: 响应式布局-->
        <el-col :sm="16" :md="2">标题：</el-col>
        <el-col :span="22">
          <input type="text" v-model="article.title" placeholder="请输入文章标题" class="custom-input">
        </el-col>
        <!--Toolbar 组件提供了编辑器的工具栏，包含各种编辑功能按钮。-->
      </el-row>
      <el-row class="description">
        <el-col :sm="16" :md="2">描述：</el-col>
        <el-col :span="22">
          <textarea type="text" v-model="article.description" placeholder="请输入描述" class="custom-input"/>
        </el-col>
      </el-row>
      <el-row class="editor-content">
        <el-col :sm="16" :md="2">内容：</el-col>
        <el-col :span="18" class="ToolAndEditor">
          <el-col>
            <!--工具栏-->
            <Toolbar :default-config="toolbarConfig" :editor="editor"/>
          </el-col>
          <el-col>
            <!--编辑器-->
            <Editor :defaultConfig="editorConfig" @onCreated="handleEditorCreated"/>
          </el-col>
        </el-col>
      </el-row>
      <el-row>
        <el-col :sm="16" :md="2">封面：</el-col>
        <el-col :span="18">
          <ArticleCropper :func="getCover"></ArticleCropper>
        </el-col>
      </el-row>
      <el-row>
        <el-col :sm="16" :md="2">标签：</el-col>
        <el-col :span="18">
          <el-dropdown size="large">
            <el-button plain>
              标签<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <div style="width: 300px;padding: 20px;">
                <input @input="getTags" v-model="name" type="text" placeholder="请输入标签" class="custom-input"/>
                <el-check-tag
                    v-for="(tag,index) in tags"
                    :key="index"
                    type="primary"
                    size="large"
                    class="tag_class"
                    :color="color.main_color"
                    hit
                    :checked="tag.status"
                    @click="setTagStatus(index)"
                >
                {{ tag.name }}
                </el-check-tag>
              </div>
            </template>
          </el-dropdown>
        </el-col>
      </el-row>
      <el-row>
        <el-tag
            v-for="(tag,index) in onTags"
            :key="index"
            effect="plain"
            size="large"
            closable
            class="tag_class"
            type="primary"
            @close="handleClose(index)"
        >
          {{ tag.name }}
        </el-tag>
      </el-row>
      <div>
        <FuncButton @click="saveArticle(false)" :message="'保存草稿'"></FuncButton>
        <FuncButton @click="saveArticle(true)" :message="'发布文章'"></FuncButton>
      </div>
    </div>
  </div>
</template>
<script setup>
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {inject, onBeforeUnmount, onMounted, reactive, ref, shallowRef} from "vue";
import FuncButton from "@/components/common/button/FuncButton.vue";
import axios from "axios";
import {getBlob, getToken} from "@/util/util.js";
import ArticleCropper from "@/components/common/cropper/ArticleCropper.vue";
import {doGet, doPost, doPostFile} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {Validator} from "@/util/validator.js";
//Editor 组件用于创建编辑器实例，并通过 defaultConfig 选项配置默认设置
const editor = shallowRef(null);
const color = inject('color')

//编辑器配置（即文本框的内容）
const editorConfig = {
  // MENU_CONF:{},
  placeholder: '请输入内容...',
  scroll: true,
  MENU_CONF: {
    uploadImage : {
      server: axios.defaults.baseURL + 'file/articleImg',
      maxNumberOfFiles: 10,
      maxFileSize: 3 * 1024 * 1024,
      timeout: 10 * 1000,
      meta: {
        token: getToken(),
      },
      fieldName: 'image',
    }
  },
}
// 隐藏部分工具
const toolbarConfig = {
  excludeKeys: [
      'group-video',
      'fullScreen'
  ]
}

let article = reactive({
  title: '',
  content: '',
  description: '',
  cover: [],
  type:'',
  tag:'',
})
let tags = reactive([{
  name:'',
  id:'',
  status:false,
}])
let onTags = reactive([]);

let name = ref('');
onMounted(() => {
  getTags();
})

onBeforeUnmount(() => {
  //实例附加到 editor 引用。
  editor.value.destroy()
})

const handleClose = (index) => {
  console.log(index);
  onTags[index].status = false;
  onTags.splice(index,1);
}

const getCover = (url) => {
  article.cover.push(url);
}

const setTagStatus = (index) => {
  if(!tags[index].status){
    tags[index].status = true;
    onTags.push(tags[index]);
  }
}

const getTags = () => {
  doGet("/tag",{name:name.value}).then((resp) => {
    if(resp.data.code === 1){
      const tempTags = resp.data.data;
      tags.splice(0,tags.length);
      tempTags.forEach(tag => {tag.status=false; tags.push(tag);});
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}

const handleEditorCreated = (editorInstance) => {
  //实例附加到 editor 引用。
  editor.value = editorInstance
  console.log(editor.value.getConfig());
};

const articleValidate = () => {
  var validator = new Validator();
  validator.add(article.title,[{
    strategy: 'isNotEmpty',
    errorMsg: '标题不能为空'
  }]);
  validator.add(editor.value.getText(),[{
    strategy: 'isNotEmpty',
    errorMsg: '内容不能为空'
  },{
    strategy: 'minLength:20',
    errorMsg: '文章内容长度不能少于10个字符'
  },{
    strategy: 'maxLength:2000',
    errorMsg: '文章内容长度不能超过2000个字符'
  }]);
  validator.add(onTags,[{
    strategy: 'arrayIsNotEmpty',
    errorMsg: '标签不能为空'
  },{
    strategy: 'maxLength:3',
    errorMsg: '标签不能超过三个'
  }]);
  validator.add(article.cover,[{
    strategy: 'maxLength:3',
    errorMsg: '封面不能超过三张'
  }])
  return validator.start();
}

const saveArticle = async (isArticle) => {
  const errorMsg = articleValidate();
  if(errorMsg){
    ElMessage.error(errorMsg);
    return;
  }

  const formData = new FormData();
  formData.append("title",article.title);
  //存入html内容，得到html的url
  const htmlData = new FormData();
  htmlData.append("html",getBlob(editor.value.getHtml(),"text/html;charset=utf-8"),article.title+".html");
  console.log(htmlData.get("html"))
  const res = await doPostFile("/file/uploadFile",htmlData);
  if(res.data.code===1){
    formData.append("content",res.data.data);
  }else{
    ElMessage.error("上传文章内容失败，服务器繁忙")
    return;
  }
  formData.append("description",article.description);
  formData.append("cover",article.cover.join(','));
  formData.append("type",isArticle);
  const onTagIds = onTags.map(tag => tag.id);
  formData.append("tag",onTagIds.join(','));
  doPost("/article/publish",formData).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("发布成功");
    }else{
      ElMessage.error(resp.data.msg);
    }
  })
}

</script>
<style scoped>
@import "@/views/css/funcInput.css";

/*.editor-content{*/
/*  display: flex;*/

/*}*/

:deep(.w-e-modal button){
  padding: 4px;
  font-size: 15px;
}

.editor-container {
  background-color: var(--main-beside-color);
  margin: auto;
  display: flex;
  flex-direction: column;
  gap: 60px;
  padding: 20px;
}

.ToolAndEditor{
  box-shadow: var(--common-shadow-color);
}

.tag_class{
  margin: 10px;
  cursor: pointer;
}
.tag_class:hover{
  transform: scale(1.1);
}

:deep(.w-e-text-container){
  height: 300px !important;
  width: 100% !important;
}

:deep(.el-tag--plain.el-tag--primary){
  background-color: var(--main-beside-color);
}

:deep(.el-button.is-plain){
  background-color: var(--main-beside-color);
  color: var(--text-color);
  border-color: var(--common-color);
}
:deep(.el-tag){
  font-size: 22px;
}
</style>
