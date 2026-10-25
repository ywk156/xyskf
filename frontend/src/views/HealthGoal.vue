<template>
  <div>
    <div style="margin-bottom: 20px">
      <el-button type="primary" @click="showDialog()">
        <el-icon><Plus /></el-icon>新建目标
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="8" v-for="goal in goals" :key="goal.id">
        <el-card class="goal-card">
          <div class="goal-header">
            <span class="goal-type">{{ goalTypeMap[goal.goalType] }}</span>
            <el-tag :type="statusType[goal.status]" size="small">{{ statusText[goal.status] }}</el-tag>
          </div>
          <div class="goal-value">
            <span class="current">{{ goal.currentValue }}</span>
            <span class="divider">/</span>
            <span class="target">{{ goal.targetValue }}</span>
            <span class="unit">{{ goal.unit }}</span>
          </div>
          <el-progress
            :percentage="getPercentage(goal)"
            :stroke-width="12"
            :color="getProgressColor(goal)"
            style="margin: 12px 0"
          />
          <div class="goal-date">
            {{ goal.startDate }} ~ {{ goal.endDate }}
          </div>
          <div class="goal-actions">
            <el-button size="small" @click="showDialog(goal)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteGoal(goal)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑目标' : '新建目标'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="目标类型">
          <el-select v-model="form.goalType" style="width: 100%">
            <el-option v-for="(label, value) in goalTypeMap" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标值">
          <el-input-number v-model="form.targetValue" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="当前值">
          <el-input-number v-model="form.currentValue" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如：步、kcal、kg" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGoal">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const goalTypeMap = {
  steps: '每日步数', calories: '每日卡路里', distance: '每日距离',
  sleep: '每日睡眠', weight: '目标体重', exercise: '每周运动次数'
}
const statusText = { 1: '进行中', 2: '已完成', 3: '已取消' }
const statusType = { 1: '', 2: 'success', 3: 'info' }

const goals = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  goalType: 'steps',
  targetValue: 10000,
  currentValue: 0,
  unit: '步',
  startDate: '',
  endDate: '',
  status: 1
})

const loadGoals = async () => {
  const res = await request.get('/goal/list')
  goals.value = res.data
}

const getPercentage = (goal) => {
  if (!goal.targetValue || goal.targetValue === 0) return 0
  const pct = (goal.currentValue / goal.targetValue) * 100
  return Math.min(100, Math.round(pct))
}

const getProgressColor = (goal) => {
  const pct = getPercentage(goal)
  if (pct >= 100) return '#67c23a'
  if (pct >= 60) return '#409eff'
  if (pct >= 30) return '#e6a23c'
  return '#f56c6c'
}

const showDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, {
      id: null,
      goalType: 'steps',
      targetValue: 10000,
      currentValue: 0,
      unit: '步',
      startDate: new Date().toISOString().slice(0, 10),
      endDate: new Date(Date.now() + 30 * 24 * 3600 * 1000).toISOString().slice(0, 10),
      status: 1
    })
  }
  dialogVisible.value = true
}

const saveGoal = async () => {
  if (form.id) {
    await request.put('/goal/update', form)
  } else {
    await request.post('/goal/save', form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadGoals()
}

const deleteGoal = async (goal) => {
  await ElMessageBox.confirm('确定删除这个目标吗？', '提示', { type: 'warning' })
  await request.delete(`/goal/${goal.id}`)
  ElMessage.success('删除成功')
  loadGoals()
}

onMounted(loadGoals)
</script>

<style scoped>
.goal-card {
  margin-bottom: 20px;
  border-radius: 12px;
}
.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.goal-type {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.goal-value {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.current {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}
.divider {
  color: #c0c4cc;
  font-size: 18px;
}
.target {
  font-size: 18px;
  color: #909399;
}
.unit {
  font-size: 14px;
  color: #909399;
}
.goal-date {
  font-size: 12px;
  color: #c0c4cc;
  margin-bottom: 12px;
}
.goal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
