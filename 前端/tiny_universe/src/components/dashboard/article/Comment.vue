<template>
  <div class="commentDiv">
    <div>
      评论：#{comment.count}}
    </div>
    <div>
      <CommentEditor></CommentEditor>
    </div>

    <div>
    </div>
  </div>
</template>
<script setup>

import CommentEditor from "@/components/common/editor/CommentEditor.vue";
import {doPost} from "@/http/httpRequest.js";

</script>
<style scoped>
.commentDiv{
  background: var(--main-beside-color);
  padding: 50px;
  display: flex;
  flex-direction: column;
}
.commentDiv > *{
  flex: 0 0 auto;
  align-self: flex-start;
}
</style>
