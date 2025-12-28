<script setup>
import { ref, reactive, computed } from 'vue'
import { listTrain } from '@/api/business/train'

defineProps({
  modelValue: {
    type: [String, null],
    required: true
  },
  placeholder: {
    type: String,
    default: '不知道车次？可以输入起始站或终点站搜索车次'
  }
})

const emit = defineEmits(['update:modelValue'])

// 车次选项数据
const trainOptions = ref([])
const loading = ref(false)

// 分页数据
const pagination = reactive({
  page: 1,
  size: 5,
  total: 0
})

// 当前搜索关键字
const currentKeyword = ref('')

// 远程搜索车次的方法
const handleSearch = async (query) => {
  currentKeyword.value = query
  pagination.page = 1  // 重置页码
  await searchTrains()
}

// 搜索车次
const searchTrains = async () => {
  if (!currentKeyword.value) {
    trainOptions.value = []
    return
  }

  loading.value = true
  try {
    const res = await listTrain({
      page: pagination.page,
      size: pagination.size,
      keyword: currentKeyword.value
    })
    if (res.code === 200) {
      const newOptions = res.data.list.map(item => ({
        value: item.code,
        label: `${item.code} (${item.start} -> ${item.end})`,
        start: item.start,
        end: item.end
      }))

      // 第一页替换，其他页追加
      if (pagination.page === 1) {
        trainOptions.value = newOptions
      } else {
        trainOptions.value = [...trainOptions.value, ...newOptions]
      }

      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取车次列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载更多
const handleLoadMore = async (e) => {
  e.stopPropagation() // 阻止事件冒泡，防止关闭下拉框
  pagination.page++
  await searchTrains()
}

// 计算是否还有更多数据
const hasMore = computed(() => {
  return trainOptions.value.length < pagination.total
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
      v-for="item in trainOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    >
      <span style="float: left">{{ item.value }}</span>
      <span style="float: right; color: #8492a6; font-size: 13px">
        {{ item.start }} -> {{ item.end }}
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
