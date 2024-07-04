<template>
  <div>

    <el-container class="chat-room-list">
      <el-header height="100px">
        <h2>聊天房间列表</h2>
      </el-header>
      <el-main>
        <el-scrollbar>
          <el-card v-for="room in rooms" :key="room.id" class="room-card">
            <div class="room-info">
              <h3>{{ room.name }}</h3>
              <p v-show = "false">ID: {{ room.id }}</p>
              <p>限制人数: {{ room.person }}</p>
              <FuncButton type="primary" size="large" :message="'加入房间'" @click="joinRoom(room)"></FuncButton>
            </div>
          </el-card>
        </el-scrollbar>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import FuncButton from "@/components/common/button/FuncButton.vue";

const rooms = ref([
  { id: '1', name: '房间A', person: 10 },
  { id: '2', name: '房间B', person: 8 },
  { id: '3', name: '房间C', person: 12 },
]);

const joinRoom = (room) => {
  ElMessage.success(`您已加入房间 ${room.name}`);
  // 在这里添加实际的加入房间的逻辑
};
</script>

<style scoped>
.chat-room-list {
  height: 100%;
}
.el-header {
  text-align: center;
}
.room-card {
  margin-bottom: 20px;
  cursor: pointer;
}
.room-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
</style>
