<template>
  <div class="min-h-screen flex items-center justify-center">
    <div class="bg-white rounded-2xl shadow-xl flex flex-col md:flex-row w-full max-w-4xl overflow-hidden">
      <!-- Left Section: Open Opinion Feedback -->
      <div class="w-full md:w-1/2 p-8 bg-gradient-to-br from-blue-50 to-purple-100">
        <h2 class="text-2xl font-bold mb-6 text-indigo-800">意见反馈</h2>
        <textarea
            v-model="feedback"
            placeholder="在这里填写您的意见..."
            class="w-full h-40 p-4 border border-indigo-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-400 transition duration-300 ease-in-out"
        ></textarea>
        <button
            @click="submitFeedback"
            class="mt-6 bg-indigo-600 text-white py-3 px-6 rounded-lg hover:bg-indigo-700 transition duration-300 ease-in-out transform hover:scale-105"
        >
          Submit Feedback
        </button>
      </div>
      <!-- Right Section: Star Rating -->
      <div class="w-full md:w-1/2 p-8 bg-gradient-to-br from-pink-50 to-orange-100 flex flex-col items-center justify-center">
        <h2 class="text-3xl font-bold mb-8 text-pink-800">感谢您的反馈!</h2>
        <div class="flex space-x-2 mb-6">
          <span
              v-for="star in 5"
              :key="star"
              @click="rate(star)"
              @mouseover="hoverRating = star"
              @mouseleave="hoverRating = rating"
              class="cursor-pointer text-5xl transition-all duration-300 ease-in-out transform hover:scale-110"
              :class="[
              star <= (hoverRating || rating) ? 'text-yellow-400' : 'text-gray-300',
              {'animate-pulse': star === hoverRating && star > rating}
            ]"
          >
            ★
          </span>
        </div>
        <p class="text-xl font-medium text-pink-700">
          Your Rating: {{ rating }} Star{{ rating !== 1 ? 's' : '' }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {ElMessage} from "element-plus";

const feedback = ref('')
const rating = ref(0)
const hoverRating = ref(0)

function submitFeedback() {
  feedback.value = ''
  rating.value = 0
  ElMessage.success('反馈成功')
}

function rate(star) {
  rating.value = star
}
</script>

<style>
body {
  font-family: 'Arial', sans-serif;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.animate-pulse {
  animation: pulse 0.5s ease-in-out infinite;
}
</style>
