<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" :icon="UserFilled" />
            <h2 class="nickname">{{ userInfo?.nickname || userInfo?.username }}</h2>
            <p class="username">@{{ userInfo?.username }}</p>
          </div>
          <el-divider />
          <div class="info-list">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>性别：{{ genderText[userInfo?.gender] || '未设置' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>年龄：{{ userInfo?.age || '未设置' }} 岁</span>
            </div>
            <div class="info-item">
              <el-icon><Top /></el-icon>
              <span>身高：{{ userInfo?.height || '未设置' }} cm</span>
            </div>
            <div class="info-item">
              <el-icon><Weight /></el-icon>
              <span>体重：{{ userInfo?.weight || '未设置' }} kg</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>手机：{{ userInfo?.phone || '未设置' }}</span>
            </div>
          </div>
          <div v-if="userInfo?.height && userInfo?.weight" class="bmi-section">
            <el-divider />
            <div class="bmi-value">BMI: {{ bmi }}</div>
            <el-tag :type="bmiType">{{ bmiStatus }}</el-tag>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>编辑个人信息</template>
          <el-form :model="form" label-width="100px" style="max-width: 500px">
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="form.gender">
                <el-option label="男" :value="1" />
                <el-option label="女" :value="2" />
                <el-option label="未知" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input-number v-model="form.age" :min="0" :max="120" />
            </el-form-item>
            <el-form-item label="身高(cm)">
              <el-input-number v-model="form.height" :min="0" :max="250" :precision="1" />
            </el-form-item>
            <el-form-item label="体重(kg)">
              <el-input-number v-model="form.weight" :min="0" :max="300" :precision="1" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const userInfo = ref(null)
const form = reactive({
  id: null,
  nickname: '',
  gender: 0,
  age: null,
  height: null,
  weight: null,
  phone: '',
  email: ''
})

const genderText = { 0: '未知', 1: '男', 2: '女' }

const bmi = computed(() => {
  if (form.height && form.weight) {
    const h = form.height / 100
    return (form.weight / (h * h)).toFixed(1)
  }
  return '-'
})

const bmiStatus = computed(() => {
  const v = parseFloat(bmi.value)
  if (isNaN(v)) return ''
  if (v < 18.5) return '偏瘦'
  if (v < 24) return '正常'
  if (v < 28) return '偏胖'
  return '肥胖'
})

const bmiType = computed(() => {
  const v = parseFloat(bmi.value)
  if (isNaN(v)) return 'info'
  if (v < 18.5) return 'warning'
  if (v < 24) return 'success'
  if (v < 28) return 'warning'
  return 'danger'
})

const loadProfile = async () => {
  const res = await request.get('/user/info')
  userInfo.value = res.data
  Object.assign(form, res.data)
}

const saveProfile = async () => {
  const res = await request.put('/user/profile', form)
  userStore.setUserInfo(res.data)
  userInfo.value = res.data
  ElMessage.success('保存成功')
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-card {
  text-align: center;
}
.avatar-section {
  padding: 20px 0;
}
.nickname {
  margin-top: 16px;
  font-size: 22px;
  color: #303133;
}
.username {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}
.info-list {
  text-align: left;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  color: #606266;
  font-size: 14px;
}
.bmi-section {
  text-align: center;
}
.bmi-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}
</style>
