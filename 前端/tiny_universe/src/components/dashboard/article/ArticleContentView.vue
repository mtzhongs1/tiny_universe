<template>
  <div class="editor-content-view line-numbers" id="editor-content-view" v-html="props.content">
  </div>
</template>
<script setup>
import {onMounted} from "vue";

import Prism from "prismjs"//导入代码高亮插件
import "prismjs/themes/prism-tomorrow.min.css"
import "prismjs/plugins/line-numbers/prism-line-numbers.min.js" //代码行号插件
import "prismjs/plugins/line-numbers/prism-line-numbers.min.css"
onMounted(() => {
  setTimeout(() => {
    Prism.highlightAll()// 全局代码高亮
  }, 100)
})

let props = defineProps(['content'])
</script>
<style scoped>
.editor-content-view {
  /*不需要border*/
  /*border: 3px solid #ccc;*/
  /*border-radius: 5px;*/
  padding: 0 10px;
  margin-top: 20px;
  overflow-x: auto;
}
.editor-content-view ::v-deep(p),
.editor-content-view ::v-deep(li) {
  white-space: pre-wrap; /* 保留空格 */
}

.editor-content-view ::v-deep(blockquote) {
  border-left: 8px solid #d0e5f2;
  padding: 10px 10px;
  margin: 10px 0;
  background-color: #f1f1f1;
}

.editor-content-view ::v-deep(code) {
  font-family: monospace;
  background-color: #2d2d2d;
  padding: 3px;
  border-radius: 3px;
}
/*
.editor-content-view ::v-deep(pre > code) {
  display: block;
  padding: 10px;
}
*/

.editor-content-view ::v-deep(v-table) {
  border-collapse: collapse;
}
.editor-content-view ::v-deep(td),
.editor-content-view ::v-deep(th) {
  border: 1px solid #ccc;
  min-width: 50px;
  height: 20px;
}
.editor-content-view ::v-deep(th) {
  background-color: var(--main-beside-color);
}

.editor-content-view ::v-deep(ul),
.editor-content-view ::v-deep(ol) {
  padding-left: 20px;
}

.editor-content-view ::v-deep(input[type="checkbox"]) {
  margin-right: 5px;
}
</style>
