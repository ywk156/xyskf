<template>
  <div>
    <!-- 数据统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #ecf5ff; color: #409eff">
            <el-icon :size="28"><Odometer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalSteps || 0 }}</div>
            <div class="stat-label">本周总步数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #fef0f0; color: #f56c6c">
            <el-icon :size="28"><Flame /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalCalories || 0 }}</div>
            <div class="stat-label">本周消耗卡路里(kcal)</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f0f9eb; color: #67c23a">
            <el-icon :size="28"><Basketball /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ exerciseStats.weekCount || 0 }}</div>
            <div class="stat-label">本周运动次数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #fdf6ec; color: #e6a23c">
            <el-icon :size="28"><Moon /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.avgSleep || 0 }}h</div>
            <div class="stat-label">本周平均睡眠</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="chart-card">
          <div class="chart-title">近7天步数与卡路里趋势</div>
          <div ref="stepsChart" style="height: 320px"></div>
        </div>
        <div class="chart-card">
          <div class="chart-title">近7天睡眠时长</div>
          <div ref="sleepChart" style="height: 280px"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="chart-title">今日健康数据</div>
          <div ref="todayChart" style="height: 320px"></div>
        </div>
        <div class="chart-card">
          <div class="chart-title">运动类型分布</div>
          <div ref="typeChart" style="height: 280px"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'

const stats = ref({})
const exerciseStats = ref({})
const weekRecords = ref([])
const exerciseTypeStats = ref([])
const today = ref(null)

const stepsChart = ref()
const sleepChart = ref()
const todayChart = ref()
const typeChart = ref()

let stepsChartInstance = null
let sleepChartInstance = null
let todayChartInstance = null
let typeChartInstance = null

const loadData = async () => {
  const res = await request.get('/dashboard/overview')
  stats.value = res.data.healthStats
  exerciseStats.value = res.data.exerciseStats
  weekRecords.value = res.data.weekRecords
  exerciseTypeStats.value = res.data.exerciseTypeStats
  today.value = res.data.today

  await nextTick()
  initCharts()
}

const initCharts = () => {
  // 步数与卡路里趋势图
  if (stepsChart.value) {
    if (stepsChartInstance) stepsChartInstance.dispose()
    stepsChartInstance = echarts.init(stepsChart.value)
    const dates = weekRecords.value.map(r => r.recordDate.slice(5))
    stepsChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['步数', '卡路里'] },
      grid: { left: 50, right: 50, bottom: 30, top: 40 },
      xAxis: { type: 'category', data: dates },
      yAxis: [
        { type: 'value', name: '步数' },
        { type: 'value', name: '卡路里' }
      ],
      series: [
        {
          name: '步数',
          type: 'bar',
          data: weekRecords.value.map(r => r.steps),
          itemStyle: { color: '#409eff' }
        },
        {
          name: '卡路里',
          type: 'line',
          yAxisIndex: 1,
          data: weekRecords.value.map(r => r.calories),
          smooth: true,
          itemStyle: { color: '#f56c6c' }
        }
      ]
    })
  }

  // 睡眠时长图
  if (sleepChart.value) {
    if (sleepChartInstance) sleepChartInstance.dispose()
    sleepChartInstance = echarts.init(sleepChart.value)
    const dates = weekRecords.value.map(r => r.recordDate.slice(5))
    sleepChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 50, right: 20, bottom: 30, top: 20 },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value', name: '小时' },
      series: [{
        type: 'line',
        data: weekRecords.value.map(r => r.sleepDuration),
        smooth: true,
        areaStyle: { color: 'rgba(103, 194, 58, 0.2)' },
        itemStyle: { color: '#67c23a' }
      }]
    })
  }

  // 今日健康数据环形图
  if (todayChart.value) {
    if (todayChartInstance) todayChartInstance.dispose()
    todayChartInstance = echarts.init(todayChart.value)
    const t = today.value
    todayChartInstance.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['50%', '70%'],
        data: [
          { value: t?.steps || 0, name: '步数' },
          { value: t?.calories || 0, name: '卡路里' },
          { value: t?.waterIntake || 0, name: '饮水量(ml)' }
        ],
        label: { formatter: '{b}: {c}' }
      }]
    })
  }

  // 运动类型分布
  if (typeChart.value) {
    if (typeChartInstance) typeChartInstance.dispose()
    typeChartInstance = echarts.init(typeChart.value)
    const typeNameMap = {
      running: '跑步', cycling: '骑行', swimming: '游泳',
      walking: '步行', yoga: '瑜伽', gym: '健身', other: '其他'
    }
    typeChartInstance.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c}次' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: exerciseTypeStats.value.map(item => ({
          name: typeNameMap[item.type] || item.type,
          value: item.count
        }))
      }]
    })
  }
}

const handleResize = () => {
  stepsChartInstance?.resize()
  sleepChartInstance?.resize()
  todayChartInstance?.resize()
  typeChartInstance?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  stepsChartInstance?.dispose()
  sleepChartInstance?.dispose()
  todayChartInstance?.dispose()
  typeChartInstance?.dispose()
})
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
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}
.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}
</style>
