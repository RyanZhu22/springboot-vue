<template>
  <el-row class="row-bg" justify="center" id="wrapper">
    <el-col :span="6" v-if="showLogin">
      <div class="container_login">
        <div class="header" style="text-align: -webkit-center">LOGIN</div>
        <el-form status-icon label-width="70px" style="margin-top: 20px;" ref="loginFormRef" :model="loginForm"
          :rules="loginRules">
          <el-form-item label="Username" prop="username" style="margin-top: 40px;">
            <el-input v-model="loginForm.username" type="username" autocomplete="off" />
          </el-form-item>

          <el-form-item label="Password" prop="password" style="margin-top: 20px;">
            <el-input v-model="loginForm.password" type="password" autocomplete="off" />
          </el-form-item>
          <el-form-item style="margin-top: 40px;">
            <el-button type="primary" @click="submitLoginForm(loginFormRef)">Login</el-button>
            <el-button type="warning" @click="reset(loginFormRef)">Reset</el-button>
            <el-button type="success" @click="toRegister">Register</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-col>

    <el-col :span="6" v-else>
      <div class="container_register">
        <div class="header" style="text-align: -webkit-center">LOGIN</div>
        <el-form status-icon label-width="70px" style="margin-top: 20px;" ref="registerFormRef" :model="registerForm"
          :rules="registerRules">
          <el-form-item label="Username" prop="username" style="margin-top: 40px;">
            <el-input v-model="registerForm.username" type="username" autocomplete="off" />
          </el-form-item>

          <el-form-item label="Password" prop="password" style="margin-top: 20px;">
            <el-input v-model="registerForm.password" type="password" autocomplete="off" />
          </el-form-item>

          <el-form-item label="ConfirmPassword" prop="password" style="margin-top: 20px;">
            <el-input v-model="registerForm.confirmPassword" type="password" autocomplete="off" />
          </el-form-item>

          <el-form-item label="Name" prop="name" style="margin-top: 20px;">
            <el-input v-model="registerForm.name" type="text" autocomplete="off" />
          </el-form-item>

          <el-form-item label="Email" prop="Email" style="margin-top: 20px;">
            <el-input v-model="registerForm.email" type="email" autocomplete="off" />
          </el-form-item>

          <el-form-item style="margin-top: 40px;">
            <el-button type="primary" @click="submitRegisterForm(registerFormRef)">Submit</el-button>
            <el-button type="warning" @click="reset(registerFormRef)">Reset</el-button>
            <el-button type="success" @click="toLogin">Login</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-col>
  </el-row>
</template>
  
<script setup>
import { reactive, ref, inject } from 'vue';
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from "../store/user"
const $axios = inject('$axios')

const loginFormRef = ref()
const registerFormRef = ref()
const showLogin = ref(true)

// 实例化 store
const userStore = useUserStore()

const loginForm = reactive({
  username: '',
  password: '',
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
})

// username validation
const loginUsernameValidate = (rule, value, callback) => {
  // If username do not input the value
  if (value === '') {
    callback(new Error('Please input the password'))
  } else if (value !== loginForm.username) {
    callback(new Error("Input value is mismatch!"))
  } else {
    callback()
  }
}

// password validation
const loginPasswordValidate = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please input the password'))
  } else if (value !== loginForm.password) {
    callback(new Error("Input value is mismatch!"))
  } else {
    callback()
  }
}

// username validation
const registerUsernameValidate = (rule, value, callback) => {
  // If username do not input the value
  if (value === '') {
    callback(new Error('Please input the password'))
  } else if (value !== registerForm.username) {
    callback(new Error("Input value is mismatch!"))
  } else {
    callback()
  }
}

// password validation
const registerPasswordValidate = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please input the password'))
  } else if (value !== registerForm.password) {
    callback(new Error("Input value is mismatch!"))
  } else {
    callback()
  }
}

const confirmValidate = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please input the password'))
  } else if (value !== registerForm.password) {
    callback(new Error("Input value is mismatch!"))
  } else if (registerForm.password !== registerForm.confirmPassword) {
    callback(new Error("Two input password is mismatch!"))
  } else {
    callback()
  }
}

const loginRules = reactive({
  username: [{ validator: loginUsernameValidate, trigger: 'blur' }],
  password: [{ validator: loginPasswordValidate, trigger: 'blur' }],
})

const registerRules = reactive({
  username: [{ validator: registerUsernameValidate, trigger: 'blur' }],
  password: [{ validator: registerPasswordValidate, trigger: 'blur' }],
  confirmPassword: [{ validator: confirmValidate, trigger: 'blur' }],
})

const validatation = (formEl) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!')
    } else {
      console.log('error submit!')
      return false
    }
  })
}

const submitLoginForm = (formEl) => {
  // Front-end validate
  validatation(formEl)

  // await userStore.login(loginForm)
  console.log(loginForm);
  $axios.post('/api/login', loginForm).then(res => {
    console.log(res);
    if (res.code === "200") {
      // pinia save userInfor
      userStore.setUser(res.result.user)
      ElMessage.success("Login Successfully");
      router.push('/home')
    } else {
      console.log("ERROR" + err);
      ElMessage.error("Login Fail")
    }
  }).catch(err => console.log("ERROR" + err))
}

const toRegister = () => {
  showLogin.value = false
}

const toLogin = () => {
  showLogin.value = true
}

const submitRegisterForm = (formEl) => {
  // Front-end validate
  validatation(formEl)
  console.log(registerForm);
  // Post request to Back-end validate
  $axios.post('/api/register', registerForm).then(res => {
    console.log(res);
    if (res.code === "200") {
      ElMessage({
        message: 'Register Successfully',
        type: 'success',
      })
      loginForm.username = registerForm.username
      showLogin.value = true
    } else {
      ElMessage.error('Register Failed')

    }
  }).catch(err => console.log(err))
}

const reset = (formEl) => {
  if (!formEl) return
  formEl.resetFields()
}

</script>
  
<style>
#wrapper {
  height: 100vh;
  background-image: linear-gradient(to bottom right, #FC466B, #3F5EFB);
  overflow: hidden;
}

.container_login {
  width: 350px;
  height: 280px;
  background-color: #fff;
  margin: 200px auto;

  padding: 20px;
  border-radius: 10px
}

.container_register {
  width: 400px;
  height: 500px;
  background-color: #fff;
  margin: 200px auto;
  padding: 20px;
  border-radius: 10px
}
</style>