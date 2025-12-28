<script setup>
import { ref, reactive, computed } from 'vue'
import { listStation } from '@/api/business/station'

defineProps({
  modelValue: {
    type: [String, null],
    required: true
  },
  placeholder: {
    type: String,
    default: '请输入站名/拼音/拼音首字母搜索'
  }
})

const emit = defineEmits(['update:modelValue'])

// 车站选项数据
const stationOptions = ref([])
const loading = ref(false)

// 分页数据
const pagination = reactive({
  page: 1,
  size: 5,
  total: 0
})

// 当前搜索关键字
const currentKeyword = ref('')

// 远程搜索车站的方法
const handleSearch = async (query) => {
  currentKeyword.value = query
  pagination.page = 1  // 重置页码
  await searchStations()
}

// 搜索车站
const searchStations = async () => {
  if (!currentKeyword.value) {
    stationOptions.value = []
    return
  }

  loading.value = true
  try {
    const res = await listStation({
      page: pagination.page,
      size: pagination.size,
      keyword: currentKeyword.value
    })
    if (res.code === 200) {
      const newOptions = res.data.list.map(item => ({
        value: item.name,
        label: `${item.name} (${item.namePinyin})`,
        pinyin: item.namePinyin
      }))

      // 第一页替换，其他页追加
      if (pagination.page === 1) {
        stationOptions.value = newOptions
      } else {
        stationOptions.value = [...stationOptions.value, ...newOptions]
      }

      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取车站列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多
const handleLoadMore = async (e) => {
  e.stopPropagation() // 阻止事件冒泡，防止关闭下拉框
  pagination.page++
  await searchStations()
}

// 计算是否还有更多数据
const hasMore = computed(() => {
  return stationOptions.value.length < pagination.total
})
</script>

<template>
  <el-select
    :model-value="modelValue"
    filterable
    remote
    reserve-keyword
    :placeholder="placeholder"
    :remote-method="handleSearch"
    :loading="loading"
    @update:modelValue="newValue => emit('update:modelValue', newValue)"
    style="width: 100%"
  >
    <el-option
      v-for="item in stationOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    >
      <span style="float: left">{{ item.value }}</span>
      <span style="float: right; color: #8492a6; font-size: 13px">
        {{ item.pinyin }}
      </span>
    </el-option>
    <div v-if="hasMore" class="load-more">
      <el-button
        link
        type="primary"
        size="small"
        :loading="loading"
        @click="handleLoadMore"
      >
        加载更多
      </el-button>
    </div>
  </el-select>
</template>

<style scoped>
.load-more {
  padding: 5px 0;
  text-align: center;
  border-top: 1px solid #EBEEF5;
  cursor: pointer;
}
</style>
