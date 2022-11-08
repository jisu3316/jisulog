<script setup lang="ts">
// import TheWelcome from "@/components/TheWelcome.vue";
import axios from "axios";
import {ref} from "vue";
import {useRouter} from "vue-router";

const posts = ref([]);


axios.get("/api/posts?page=1&size=5").then((response) => {
  response.data.forEach(r => {
    posts.value.push(r);
  })
  console.log(response)
})

</script>

<template>
  <ul>
    <li v-for="post in posts" :key="post.id">
      <div>
        <router-link :to="{ name: 'read', params: {postId: post.id } }">{{ post.title }}</router-link>
      </div>

      <div>
        {{ post.content }}
      </div>
    </li>
  </ul>
</template>

<style scoped>
li {
  margin-bottom: 1rem;
}

li:last-child {
  margin-bottom: 0;
}
</style>
