<template>
  <div>
    <!-- 运动统计 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
            <el-icon :size="24"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ exerciseStats.totalDuration || 0 }}<span class="unit">分钟</span></div>
            <div class="stat-label">30天总时长</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
            <el-icon :size="24"><Flame /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ exerciseStats.totalCalories || 0 }}<span class="unit">kcal</span></div>
            <div class="stat-label">30天消耗卡路里</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f0f9eb; color: #67c23a">
            <el-icon :size="24"><Position /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ exerciseStats.totalDistance || 0 }}<span class="unit">km</span></div>
            <div class="stat-label">30天总距离</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
            <el-icon :size="24"><Trophy /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ exerciseStats.totalCount || 0 }}<span class="unit">次</span></div>
            <div class="stat-label">30天运动次数</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-card>
      <div class="filter-bar">
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>添加运动记录
        </el-button>
      </div>
      <el-table :data="records" stripe style="margin-top: 16px">
        <el-table-column prop="exerciseDate" label="日期" width="110" />
        <el-table-column label="运动类型" width="100">
          <template #default="{ row }">{{ typeMap[row.exerciseType] }}</template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分)" width="100" />
        <el-table-column prop="distance" label="距离(km)" width="100" />
        <el-table-column prop="calories" label="卡路里" width="100" />
        <el-table-column prop="heartRateAvg" label="平均心率" width="100" />
        <el-table-column label="强度" width="100">
          <template #default="{ row }">
            <el-tag :type="intensityType[row.intensity]">{{ intensityText[row.intensity] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑运动记录' : '添加运动记录'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="运动类型">
          <el-select v-model="form.exerciseType" style="width: 100%">
            <el-option v-for="(label, value) in typeMap" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.exerciseDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="时长(分)">
          <el-input-number v-model="form.duration" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="距离(km)">
          <el-input-number v-model="form.distance" :min="0" :precision="2" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="卡路里">
          <el-input-number v-model="form.calories" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="平均心率">
          <el-input-number v-model="form.heartRateAvg" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大心率">
          <el-input-number v-model="form.heartRateMax" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="强度">
          <el-select v-model="form.intensity" style="width: 100%">
            <el-option label="轻松" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="高强度" :value="3" />
          </el-select>
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

const typeMap = {
  running: '跑步', cycling: '骑行', swimming: '游泳',
  walking: '步行', yoga: '瑜伽', gym: '健身', other: '其他'
}
const intensityText = { 1: '轻松', 2: '中等', 3: '高强度' }
const intensityType = { 1: 'success', 2: 'warning', 3: 'danger' }

const records = ref([])
const exerciseStats = ref({})
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  exerciseType: 'running',
  exerciseDate: '',
  duration: 30,
  distance: 0,
  calories: 0,
  heartRateAvg: null,
  heartRateMax: null,
  intensity: 2,
  remark: ''
})

const loadData = async () => {
  const [res1, res2] = await Promise.all([
    request.get('/exercise/recent?days=30'),
    request.get('/exercise/stats')
  ])
  records.value = res1.data
  exerciseStats.value = res2.data
}

const showDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, {
      id: null,
      exerciseType: 'running',
      exerciseDate: new Date().toISOString().slice(0, 10),
      duration: 30,
      distance: 0,
      calories: 0,
      heartRateAvg: null,
      heartRateMax: null,
      intensity: 2,
      remark: ''
    })
  }
  dialogVisible.value = true
}

const saveRecord = async () => {
  await request.post('/exercise/save', form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

const deleteRecord = async (row) => {
  await ElMessageBox.confirm('确定删除这条记录吗？', '提示', { type: 'warning' })
  await request.delete(`/exercise/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
}
.unit {
  font-size: 13px;
  color: #909399;
  font-weight: normal;
  margin-left: 4px;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.filter-bar {
  display: flex;
  justify-content: flex-end;
}
</style>
