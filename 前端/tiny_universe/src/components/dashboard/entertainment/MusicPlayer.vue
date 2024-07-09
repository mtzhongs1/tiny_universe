<template>
  <div id="aplayer"></div>
</template>

<script setup>
import 'APlayer/dist/APlayer.min.css';
// import 'APlayer/dist/APlayer.min.js'
import APlayer from 'APlayer';
import {onMounted} from "vue";
// eslint-disable-next-line no-unused-vars
let ap;


onMounted(() => {

  //TODO:"TypeError: Cannot read properties of null (reading 'classList')":在DOM还没创建时就获取所报的错
  ap = new APlayer({
    container: document.getElementById('aplayer'),
    mini: false,
    autoplay: false,
    theme: '#FADFA3',
    loop: 'all',
    order: 'random',
    preload: 'auto',
    volume: 0.7,
    mutex: true,
    listFolded: false,
    listMaxHeight: 90,
    lrcType: 3,
    audio: [
      {
        name: '圣诞快乐',
        artist: 'artist1',
        url: 'https://m804.music.126.net/20240702000013/083d2779130aa09947cc4b541ece7fe2/jdyyaac/obj/w5rDlsOJwrLDjj7CmsOj/33488190746/efee/07bf/4bca/a2d08522588b6e6e04b2bb455f4e7938.m4a',
        cover: 'cover1.jpg',
        lrc: 'lrc1.lrc',
        theme: '#00bbff'
      },
    ]
  });
})


</script>

<style lang='scss' scoped>

</style>
