<template>
  <div class="toolbar">
    <!-- breadcrumb -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }" style="font-size: 20px;">home</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/' }" style="font-size: 20px;">
        promotion management</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- right -->
    <div>
      <!-- avatar img -->
      <el-avatar fit="cover" :src="userStore.avatarUrl" />

      <el-dropdown style="margin-right: 3rem;">
        <el-button type="primary">
          {{ userStore.nickname }}
          <el-icon class="el-icon--right"><arrow-down /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>Account Information</el-dropdown-item>
            <el-dropdown-item @click="signOut">Sign out</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from "../store/user"
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const signOut = () => {
  ElMessage.success("Logout Successfully")
  userStore.logout();
  router.push('/login')
}

</script>

<style>
.el-header {
  width: 100%;
  background-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}

.toolbar {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: space-between
}
</style>
