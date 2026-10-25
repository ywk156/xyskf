<template>
  <div>
    <el-card class="filter-card">
      <div class="filter-bar">
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>记录今日数据
        </el-button>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <el-table :data="records" stripe>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="steps" label="步数" width="120">
          <template #default="{ row }">{{ row.steps?.toLocaleString() || '-' }}</template>
        </el-table-column>
        <el-table-column prop="calories" label="卡路里(kcal)" width="130" />
        <el-table-column prop="distance" label="距离(km)" width="110" />
        <el-table-column prop="heartRate" label="心率(bpm)" width="110" />
        <el-table-column prop="sleepDuration" label="睡眠(h)" width="100" />
        <el-table-column prop="waterIntake" label="饮水(ml)" width="110" />
        <el-table-column prop="weight" label="体重(kg)" width="100" />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑健康记录' : '新增健康记录'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="日期">
          <el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="步数">
          <el-input-number v-model="form.steps" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="卡路里">
          <el-input-number v-model="form.calories" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="距离(km)">
          <el-input-number v-model="form.distance" :min="0" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="心率">
          <el-input-number v-model="form.heartRate" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="睡眠时长">
          <el-input-number v-model="form.sleepDuration" :min="0" :precision="1" :step="0.5" style="width: 100%" />
        </el-form-item>
        <el-form-item label="饮水量(ml)">
          <el-input-number v-model="form.waterIntake" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="0" :precision="1" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const records = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  recordDate: '',
  steps: 0,
  calories: 0,
  distance: 0,
  heartRate: null,
  sleepDuration: null,
  sleepQuality: null,
  waterIntake: 0,
  weight: null,
  remark: ''
})

const loadRecords = async () => {
  const res = await request.get('/health/recent?days=30')
  records.value = res.data
}

const showDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, {
      id: null,
      recordDate: new Date().toISOString().slice(0, 10),
      steps: 0,
      calories: 0,
      distance: 0,
      heartRate: null,
      sleepDuration: null,
      sleepQuality: null,
      waterIntake: 0,
      weight: null,
      remark: ''
    })
  }
  dialogVisible.value = true
}

const saveRecord = async () => {
  await request.post('/health/save', form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadRecords()
}

const deleteRecord = async (row) => {
  await ElMessageBox.confirm('确定删除这条记录吗？', '提示', { type: 'warning' })
  await request.delete(`/health/${row.id}`)
  ElMessage.success('删除成功')
  loadRecords()
}

onMounted(loadRecords)
</script>

<style scoped>
.filter-card {
  margin-bottom: 20px;
}
.filter-bar {
  display: flex;
  justify-content: flex-end;
}
</style>
