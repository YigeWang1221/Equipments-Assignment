<script setup lang="ts">
import { VideoPlayer, createVideoViewer } from '@/components/VideoPlayer'
import { ContentWrap } from '@/components/ContentWrap'
import { ElDivider } from 'element-plus'
import Vditor from 'vditor'
import { onMounted, ref } from 'vue'
import EditorRead from '@/views/Function/EditorRead.vue'
const showVideo = () => {
  createVideoViewer({
    url: '//sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-720p.mp4',
    poster: '//lf3-static.bytednsdoc.com/obj/eden-cn/nupenuvpxnuvo/xgplayer_doc/poster.jpg'
  })
}
const vditorInstance = ref<Vditor | null>(null)

const initVditor = (content: string) => {
  if (!vditorInstance.value) {
    vditorInstance.value = new Vditor('vditor', {
      height: '600px',
      toolbar: [],
      mode: 'wysiwyg', // non-editable mode
      value: 'test'
    })
  } else {
    vditorInstance.value.setValue(content)
  }
}
onMounted(() => {
  initVditor('测试')
})
</script>

<template>
  <ContentWrap title="">
    <VideoPlayer
      url="//sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-720p.mp4"
      poster="//lf3-static.bytednsdoc.com/obj/eden-cn/nupenuvpxnuvo/xgplayer_doc/poster.jpg"
    />

    <ElDivider />
    <BaseButton type="primary" @click="showVideo">弹窗展示</BaseButton>
  </ContentWrap>
  <div>
    <EditorRead />
  </div>
</template>
<style>
.vditor-container {
  border: 2px solid blue;
  padding: 10px;
}
</style>
