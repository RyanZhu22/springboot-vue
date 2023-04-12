// store/user.js
import { defineStore } from "pinia";
import { useRouter } from 'vue-router'
const router = useRouter()

// 创建 store
export const useUserStore = defineStore("user", {
  // 状态持久化
  persist: true,
  // 定义状态：一个函数，返回一个对象
  state: () => ({
    managerInfo: {},
  }),

  // 定义 getters，等同于组件的计算属性
  getters: {
    // getter 函数接收 state 作为参数，推荐使用箭头函数
    getUserId() {
      return this.managerInfo.user ? this.managerInfo.user.id : 0;
    },
    getUser() {
      return this.managerInfo.user || {};
    },
    getBearerToken() {
      // TODO 不懂！！！！！！！！！！！！！
      return this.managerInfo.token ? "Bearer " + this.managerInfo.token : "";
    },
    getToken() {
      return this.managerInfo.token || "";
    },
  },

  // 定义 actions，有同步和异步两种类型
  actions: {
    setManagerInfo(managerInfo) {
      this.managerInfo = managerInfo;
    },
    setUser(user) {
      this.managerInfo.user = JSON.parse(JSON.stringify(user));
    },
    logout() {
      localStorage.removeItem("user");
    },
  },
});
