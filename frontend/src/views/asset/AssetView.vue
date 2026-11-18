<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>行政与资产</h2>
        <p>维护资产分类、资产台账和员工领用关系。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="资产分类" :value="categories.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="资产台账" :value="assets.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="已分配资产" :value="assignedAssetCount" />
      </el-card>
    </div>

    <div class="panel-grid panel-grid--equal">
      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增资产分类</h3>
          <span>定义分类折旧年限</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="分类编码">
              <el-input v-model="categoryForm.code" placeholder="例如 PHONE" />
            </el-form-item>
            <el-form-item label="分类名称">
              <el-input v-model="categoryForm.name" placeholder="例如 手机" />
            </el-form-item>
            <el-form-item class="span-2" label="折旧年限">
              <el-input-number
                v-model="categoryForm.depreciationYears"
                :min="1"
                style="width: 100%"
              />
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitCategory">保存分类</el-button>
        </el-form>
      </el-card>

      <el-card class="form-card" shadow="never">
        <div class="card-title">
          <h3>新增资产</h3>
          <span>登记编号、分类和领用人</span>
        </div>
        <el-form label-position="top">
          <div class="form-grid-2">
            <el-form-item label="资产编号">
              <el-input v-model="assetForm.assetNo" placeholder="例如 AST-2026-002" />
            </el-form-item>
            <el-form-item label="资产名称">
              <el-input v-model="assetForm.assetName" placeholder="例如 MacBook Pro" />
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="assetForm.categoryId" placeholder="请选择分类">
                <el-option
                  v-for="item in categories"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="领用员工">
              <el-select v-model="assetForm.ownerEmployeeId" clearable placeholder="可不分配">
                <el-option
                  v-for="item in employees"
                  :key="item.id"
                  :label="item.fullName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="存放位置">
              <el-input v-model="assetForm.storageLocation" placeholder="例如 HQ-IT-02" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="assetForm.assetStatus">
                <el-option label="使用中" value="IN_USE" />
                <el-option label="闲置" value="IDLE" />
                <el-option label="维修中" value="REPAIR" />
              </el-select>
            </el-form-item>
          </div>
          <el-button type="primary" @click="submitAsset">保存资产</el-button>
        </el-form>
      </el-card>
    </div>

    <div class="panel-grid panel-grid--equal">
      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>资产分类</h3>
          <span>当前系统资产分类配置</span>
        </div>
        <el-table :data="categories" empty-text="暂无资产分类">
          <el-table-column label="编码" min-width="140">
            <template #default="{ row }">
              <span class="table-code">{{ row.code }}</span>
            </template>
          </el-table-column>
          <el-table-column label="名称" min-width="140" prop="name" />
          <el-table-column label="折旧年限" min-width="110" prop="depreciationYears" />
        </el-table>
      </el-card>

      <el-card class="table-card" shadow="never">
        <div class="card-title">
          <h3>资产台账</h3>
          <span>资产和员工领用明细</span>
        </div>
        <el-table :data="assets" empty-text="暂无资产">
          <el-table-column label="编号" min-width="150">
            <template #default="{ row }">
              <span class="table-code">{{ row.assetNo }}</span>
            </template>
          </el-table-column>
          <el-table-column label="名称" min-width="160" prop="assetName" />
          <el-table-column label="分类" min-width="120">
            <template #default="{ row }">
              {{ categoryNameMap[row.categoryId] ?? row.categoryId }}
            </template>
          </el-table-column>
          <el-table-column label="领用人" min-width="120">
            <template #default="{ row }">
              {{ employeeNameMap[row.ownerEmployeeId ?? -1] ?? '-' }}
            </template>
          </el-table-column>
          <el-table-column label="状态" min-width="100" prop="assetStatus" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createAsset,
  createAssetCategory,
  fetchAssetCategories,
  fetchAssets,
  type AssetCategory,
  type AssetItem
} from '../../api/asset';
import { fetchEmployees, type Employee } from '../../api/employee';

const categories = ref<AssetCategory[]>([]);
const assets = ref<AssetItem[]>([]);
const employees = ref<Employee[]>([]);

const categoryForm = reactive({
  code: '',
  name: '',
  depreciationYears: 3
});

const assetForm = reactive({
  assetNo: '',
  categoryId: undefined as number | undefined,
  assetName: '',
  ownerEmployeeId: undefined as number | undefined,
  storageLocation: '',
  assetStatus: 'IN_USE'
});

const categoryNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(categories.value.map((item) => [item.id, item.name]))
);

const employeeNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(employees.value.map((item) => [item.id, item.fullName]))
);

const assignedAssetCount = computed(() =>
  assets.value.filter((item) => item.ownerEmployeeId != null).length
);

async function loadData() {
  const [categoryList, assetList, employeeList] = await Promise.all([
    fetchAssetCategories(),
    fetchAssets(),
    fetchEmployees()
  ]);
  categories.value = categoryList;
  assets.value = assetList;
  employees.value = employeeList;
}

async function submitCategory() {
  if (!categoryForm.code || !categoryForm.name) {
    ElMessage.warning('请填写完整分类信息');
    return;
  }
  await createAssetCategory(categoryForm);
  categoryForm.code = '';
  categoryForm.name = '';
  categoryForm.depreciationYears = 3;
  ElMessage.success('资产分类已保存');
  await loadData();
}

async function submitAsset() {
  if (!assetForm.assetNo || !assetForm.categoryId || !assetForm.assetName) {
    ElMessage.warning('请填写完整资产信息');
    return;
  }
  await createAsset({
    assetNo: assetForm.assetNo,
    categoryId: assetForm.categoryId,
    assetName: assetForm.assetName,
    ownerEmployeeId: assetForm.ownerEmployeeId,
    storageLocation: assetForm.storageLocation || undefined,
    assetStatus: assetForm.assetStatus
  });
  assetForm.assetNo = '';
  assetForm.categoryId = undefined;
  assetForm.assetName = '';
  assetForm.ownerEmployeeId = undefined;
  assetForm.storageLocation = '';
  assetForm.assetStatus = 'IN_USE';
  ElMessage.success('资产已保存');
  await loadData();
}

onMounted(loadData);
</script>
