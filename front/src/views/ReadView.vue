<script setup lang="ts">
import {defineProps, onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router";
import {useRouter} from "vue-router";

const props = defineProps({
  postId: {
    type: [Number, String],
    require: true,
  },
});

const router = useRouter();

const moveToEdit = () => {
  router.push({name: "edit"});
}

const post = ref({
  id: 0,
  title: "",
  content: "",
});

onMounted(() => {
  axios.get(`/api/posts/${props.postId}`).then((response) => {
    post.value = response.data;
    });
});
</script>

<template>
  <div>
  <h2>{{ post.title }}</h2></div>
  <div>{{ post.content }}</div>
  <el-button type="waring" @click="moveToEdit()">수정</el-button>
</template>