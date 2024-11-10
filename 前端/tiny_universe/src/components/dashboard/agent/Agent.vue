<template>
  <div class="agent-list">
    <ul>
      <li class="agent-item">
        创建智能体
      </li>
      <li v-for="(agent, index) in agents" :key="index" class="agent-item">
        <img :src="agent.avatar" :alt="agent.name" class="agent-avatar" />
        <div class="agent-info">
          <h2>{{ agent.name }}</h2>
          <p>{{ agent.description }}</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const agents = ref([
  { name: '智能体 Alpha', description: '高级人工智能助手', avatar: '' },
  { name: '智能体 Beta', description: '数据处理专家', avatar: '' },
  { name: '智能体 Gamma', description: '安全监控系统', avatar: '' }
]);
</script>

<style scoped>
.agent-list {
  font-family: Arial, sans-serif;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.agent-list h1 {
  text-align: center;
  color: #333;
}

.agent-list ul {
  list-style-type: none;
  padding: 0;
}

.agent-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ddd;
}

.agent-item:last-child {
  border-bottom: none;
}

.agent-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 20px;
}

.agent-info h2 {
  margin: 0;
  color: #444;
}

.agent-info p {
  margin: 5px 0 0;
  color: #777;
}
</style>
