<!--TODO:富文本编辑器组件-->
<template>
  <div style="padding: 50px">
    <div class="editor-container">
      <el-row class="editor-toolbar">
        <!--TODO: 响应式布局-->
        <el-col :sm="16" :md="2">标题：</el-col>
        <el-col :span="22">
          <el-input style="width: 70%;" size="large" type="text" v-model="article.title" placeholder="请输入文章标题" maxlength="20" show-word-limit></el-input>
        </el-col>
        <!--Toolbar 组件提供了编辑器的工具栏，包含各种编辑功能按钮。-->
      </el-row>
      <el-row class="description">
        <el-col :sm="16" :md="2">描述：</el-col>
        <el-col :span="22">
          <el-input style="width: 70%;" size="large" type="textarea" v-model="article.description" placeholder="请输入描述" maxlength="100" show-word-limit/>
          <el-popover
              placement="top-start"
              :width="168"
              trigger="hover"
              content="根据文章内容生成描述"
          >
            <template #reference>
              <el-button :loading="loading" @click="produceDescription" style="margin: 0 0 10px 10px">AI生成</el-button>
            </template>
          </el-popover>
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
        <el-button @click="dialogVisible = true" style="margin: 400px 0 0 10px">AI润色</el-button>
      </el-row>
      <el-row>
        <el-col :sm="16" :md="2">封面：</el-col>
        <el-col :span="18">
          <ArticleCropper :func="getCover" :images="article.cover"></ArticleCropper>
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
        <!--选中的标签-->
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
        <div class="buttonDiv" v-if="params.articleId < 0">
          <Draft></Draft>
          <FuncButton @click="saveArticle(false)" :message="'保存草稿'"></FuncButton>
          <FuncButton @click="saveArticle(true)" :message="'发布文章'"></FuncButton>
        </div>
        <div class="buttonDiv" v-else>
          <Draft></Draft>
          <FuncButton @click="saveArticle(false)" :message="'保存草稿'"></FuncButton>
          <FuncButton @click="updateArticle(true)" :message="'修改文章'"></FuncButton>
        </div>
      </div>

    </div>
    <el-scrollbar :max-height="400">
      <el-dialog
          v-model="dialogVisible"
          title="润色方向"
          width="800"
          :center="true"
      >
        <template #footer>
          <div>
            <div>
              <el-select
                  v-model="option.selected"
                  placeholder="Select"
                  size="large"
                  style="width: 240px"
              >
                <el-option
                    v-for="item in option.options"
                    :key="item"
                    :label="item"
                    :value="item"
                />
              </el-select>
              <el-button :loading="loading" @click="modifyArticle" style="margin-left: 10px">改写</el-button>
            </div>
            <el-divider></el-divider>
            <div style="padding: 20px;background: var(--main-color);margin-bottom: 10px" v-html="article.modifyContent"></div>
            <FuncButton @click="sureModify" :message="'确定修改'"></FuncButton>
          </div>
        </template>
      </el-dialog>
    </el-scrollbar>
  </div>
</template>
<script setup>
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {inject, onBeforeUnmount, onMounted, reactive, ref, shallowRef} from "vue";
import FuncButton from "@/components/common/button/FuncButton.vue";
import axios from "axios";
import {getToken, isEmpty} from "@/util/util.js";
import ArticleCropper from "@/components/common/cropper/ArticleCropper.vue";
import {doGet, doPost, doPut} from "@/http/httpRequest.js";
import {ElMessage} from "element-plus";
import {Validator} from "@/util/validator.js";
import {useRoute} from "vue-router";
import Draft from "@/components/dashboard/article/Draft.vue";
const dialogVisible = ref(false);
//Editor 组件用于创建编辑器实例，并通过 defaultConfig 选项配置默认设置
const editor = shallowRef(null);
//文章数据
let article = reactive({
  id: '',
  title: '',
  content: '',
  description: '',
  cover: [],
  type:'',
  tag:'',
  modifyContent:'',
})
let tags = reactive([{
  name:'',
  id:'',
  status:false,
}])
let onTags = ref([]);
let name = ref('');
//父组件获得的数据
let color = inject('color');
let user = inject('user');
let loading = ref(false);
const route = useRoute();
const params = route.params;
let option = reactive({
  options:[
    '记叙风格','议论风格','描写风格','抒情风格','幽默风格','讽刺风格','古典风格'
  ],
  selected:'记叙风格'
})
const modifyArticle = () => {
  const fd = new FormData();
  fd.append('content',editor.value.getHtml())
  fd.append('type',option.selected)
  // const loadingInstance = ElLoading.service({ fullscreen: true })
  loading.value = true;
  doPost('/gpt/modifyArticle',fd).then((resp) => {
    // loadingInstance.close();
    loading.value = false;
    if(resp.data.code === 1){
      var content = resp.data.data;
      article.modifyContent = content.substring(2,content.length-2);
    }else{
      ElMessage.error("服务端繁忙");
    }
  })
}
const sureModify = () => {
  if(isEmpty(article.modifyContent)){
    ElMessage.error("请先点击改写按钮");
    return;
  }
  article.content = article.modifyContent;
  editor.value.setHtml(article.content);
  dialogVisible.value = false;
  article.modifyContent = '';
  option.selected = option.options[0];
}
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
    },
  },
}
// 隐藏部分工具
const toolbarConfig = {
  excludeKeys: [
    'group-video',
    'fullScreen',
    'color',
    'bgColor'
  ]
}

onMounted(() => {
  getTags();
  getArticle();
})
onBeforeUnmount(() => {
  //实例附加到 editor 引用。
  editor.value.destroy()
})

// 编辑器操作
const handleClose = (index) => {
  onTags.value[index].status = false;
  onTags.value.splice(index,1);
}
const handleEditorCreated = (editorInstance) => {
  //实例附加到 editor 引用。
  editor.value = editorInstance
};

//文章属性操作
const getCover = (url,index) => {
  article.cover[index] = url;
}
const setTagStatus = (index) => {
  if(!tags[index].status){
    tags[index].status = true;
    onTags.value.push(tags[index]);
  }
}
const getTags = async () => {
  let resp = await doGet("/tag",{name:name.value});
  if(resp.data.code === 1){
    const tempTags = resp.data.data;
    tags.splice(0,tags.length);
    tempTags.forEach(tag => {tag.status=false; tags.push(tag);});
  }else{
    ElMessage.error("服务器繁忙");
  }
}

//文章相关操作
const articleValidate = () => {
  const validator = new Validator();
  validator.add(article.title,[{
    strategy: 'isNotEmpty',
    errorMsg: '标题不能为空'
  },{
    strategy: 'maxLength:25',
    errorMsg: '标题长度不能超过25个字符'
  }]);
  validator.add(article.description,[{
    strategy: 'maxLength:100',
    errorMsg: '描述长度不能超过100个字符'
  }]);
  validator.add(editor.value.getText(),[{
    strategy: 'isNotEmpty',
    errorMsg: '内容不能为空'
  },{
    strategy: 'minLength:10',
    errorMsg: '文章内容长度不能少于10个字符'
  },{
    strategy: 'maxLength:5000',
    errorMsg: '文章内容长度不能超过5000个字符'
  }]);
  validator.add(onTags.value,[{
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
  const formData = getArticleFd(isArticle)
  if(isArticle === true){
    //发布文章
    doPost("/article/publish",formData).then((resp) => {
      if(resp.data.code === 1){
        ElMessage.success("发布成功");
      }else{
        ElMessage.error(resp.data.msg);
      }
    })
  }else{
    //保存草稿
    doPost("/draft/",formData).then((resp) => {
      if(resp.data.code === 1){
        ElMessage.success("保存成功");
      }else{
        ElMessage.error(resp.data.msg);
      }
    })
  }
}
const updateArticle = async (isArticle) => {
  const formData = getArticleFd(isArticle)
  doPut("/article",formData).then((resp) => {
    if(resp.data.code === 1){
      ElMessage.success("修改成功");
    }else{
      ElMessage.error(resp.data.msg);
    }
  })
}
const getArticle = () => {
  if(!params.articleId || params.articleId < 0){
    return;
  }
  article.id = params.articleId;
  doGet("article/"+article.id).then((resp) => {
    if(resp.data.code === 1){
      const tempArticle = resp.data.data;
      Object.assign(article,tempArticle);
      article.cover = tempArticle.cover ? tempArticle.cover.split(',') : [];
      // 返回的是标签的名字数组
      tempArticle.tag.forEach((onTag) => {
        tags.forEach((tag) => {
          if(onTag === tag.name){
            onTags.value.push(tag);
            tag.status = true;
          }
        })
      })

      editor.value.setHtml(article.content);
    }else{
      ElMessage.error("服务器繁忙");
    }
  })
}
const produceDescription = () => {
  // const fd = new FormData();
  // fd.append("content",editor.value.getText())
  // const loadingInstance = ElLoading.service({ fullscreen: true })
  loading.value = true;
  doGet("/gpt/description",{
    id:user.id,
    content:editor.value.getText()
  }).then((resp) => {
    loading.value = false;
    // loadingInstance.close();
    if(resp.data.code === 1){
      article.description = resp.data.data;
    }else{
      ElMessage.error(resp.data.msg);
    }
  });

}



//可重用方法
const getArticleFd = (isArticle) => {
  const errorMsg = articleValidate();
  if(errorMsg){
    ElMessage.error(errorMsg);
    return;
  }

  const formData = new FormData();
  formData.append("id",article.id)
  formData.append("title",article.title);
  formData.append("content",editor.value.getHtml());
  // editor.value.getHtml
  formData.append("author",user.username);
  formData.append("description",article.description);
  formData.append("cover",article.cover.join(','));
  formData.append("type",isArticle);
  const onTagIds = onTags.value.map(tag => tag.id);
  formData.append("tag",onTagIds.join(','));
  return formData;
}

</script>
<style scoped>
@import "@/views/css/funcInput.css";

/*.editor-content{*/
/*  display: flex;*/

/*}*/
body{
  font-family: Roboto;
}

.buttonDiv{
  display: flex;
  gap: 20px;
}

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
