<template>
  <section class="page-view">
    <div class="page-heading">
      <div>
        <h2>采购与库存</h2>
        <p>覆盖供应商、采购、收货入库、库存出库、调拨和盘点的基础链路。</p>
      </div>
      <div class="page-actions">
        <el-button @click="loadData">刷新数据</el-button>
      </div>
    </div>

    <div class="stats-grid stats-grid--three">
      <el-card class="info-card" shadow="never">
        <el-statistic title="供应商" :value="suppliers.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="采购订单" :value="orders.length" />
      </el-card>
      <el-card class="info-card" shadow="never">
        <el-statistic title="库存 SKU" :value="stocks.length" />
      </el-card>
    </div>

    <el-tabs>
      <el-tab-pane label="供应商">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增供应商</h3>
              <span>维护采购合作方基础信息</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="供应商编码">
                  <el-input v-model="supplierForm.code" placeholder="例如 SUP-002" />
                </el-form-item>
                <el-form-item label="供应商名称">
                  <el-input v-model="supplierForm.name" placeholder="例如 华东办公设备商" />
                </el-form-item>
                <el-form-item label="联系人">
                  <el-input v-model="supplierForm.contactPerson" />
                </el-form-item>
                <el-form-item label="联系电话">
                  <el-input v-model="supplierForm.contactPhone" />
                </el-form-item>
                <el-form-item label="付款条款">
                  <el-input v-model="supplierForm.paymentTerms" placeholder="例如 Net 30" />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="supplierForm.status">
                    <el-option label="启用" value="ACTIVE" />
                    <el-option label="停用" value="INACTIVE" />
                  </el-select>
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitSupplier">保存供应商</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>供应商列表</h3>
              <span>采购合作伙伴清单</span>
            </div>
            <el-table :data="suppliers" empty-text="暂无供应商">
              <el-table-column label="编码" min-width="130">
                <template #default="{ row }">
                  <span class="table-code">{{ row.code }}</span>
                </template>
              </el-table-column>
              <el-table-column label="名称" min-width="180" prop="name" />
              <el-table-column label="联系人" min-width="120" prop="contactPerson" />
              <el-table-column label="电话" min-width="130" prop="contactPhone" />
              <el-table-column label="条款" min-width="120" prop="paymentTerms" />
              <el-table-column label="状态" min-width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" effect="plain">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="采购订单">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>新增采购订单</h3>
              <span>按物料与仓库维度下单</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="订单号">
                  <el-input v-model="orderForm.orderNo" placeholder="例如 PO-202604-002" />
                </el-form-item>
                <el-form-item label="供应商">
                  <el-select v-model="orderForm.supplierId" placeholder="请选择供应商">
                    <el-option
                      v-for="item in suppliers"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="SKU 编码">
                  <el-input v-model="orderForm.skuCode" placeholder="例如 NB-14-001" />
                </el-form-item>
                <el-form-item label="SKU 名称">
                  <el-input v-model="orderForm.skuName" placeholder="例如 商务笔记本 14" />
                </el-form-item>
                <el-form-item label="数量">
                  <el-input-number v-model="orderForm.quantity" :min="1" style="width: 100%" />
                </el-form-item>
                <el-form-item label="单价">
                  <el-input-number v-model="orderForm.unitPrice" :min="0.01" :precision="2" style="width: 100%" />
                </el-form-item>
                <el-form-item label="仓库">
                  <el-input v-model="orderForm.warehouseName" placeholder="例如 Main Warehouse" />
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="orderForm.status">
                    <el-option label="已创建" value="CREATED" />
                    <el-option label="已下单" value="ORDERED" />
                  </el-select>
                </el-form-item>
                <el-form-item label="下单日期">
                  <el-date-picker v-model="orderForm.orderDate" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="期望到货">
                  <el-date-picker v-model="orderForm.expectedDate" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item class="span-2" label="备注">
                  <el-input v-model="orderForm.remark" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitOrder">保存采购订单</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>采购订单列表</h3>
              <span>当前采购执行情况</span>
            </div>
            <el-table :data="orders" empty-text="暂无采购订单">
              <el-table-column label="订单号" min-width="150">
                <template #default="{ row }">
                  <span class="table-code">{{ row.orderNo }}</span>
                </template>
              </el-table-column>
              <el-table-column label="供应商" min-width="160">
                <template #default="{ row }">
                  {{ supplierNameMap[row.supplierId] ?? row.supplierId }}
                </template>
              </el-table-column>
              <el-table-column label="SKU" min-width="180">
                <template #default="{ row }">
                  {{ row.skuName }} / {{ row.skuCode }}
                </template>
              </el-table-column>
              <el-table-column label="数量" min-width="90" prop="quantity" />
              <el-table-column label="金额" min-width="110" prop="totalAmount" />
              <el-table-column label="仓库" min-width="140" prop="warehouseName" />
              <el-table-column label="状态" min-width="130">
                <template #default="{ row }">
                  <el-tag :type="orderStatusType(row.status)" effect="plain">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="收货入库">
        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>登记收货</h3>
              <span>收货后自动更新采购状态和库存台账</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="收货单号">
                  <el-input v-model="receiptForm.receiptNo" placeholder="例如 RCV-202604-002" />
                </el-form-item>
                <el-form-item label="采购订单">
                  <el-select v-model="receiptForm.orderId" placeholder="请选择采购订单">
                    <el-option
                      v-for="item in receivableOrders"
                      :key="item.id"
                      :label="`${item.orderNo} / ${item.skuName}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="收货数量">
                  <el-input-number v-model="receiptForm.receivedQty" :min="1" style="width: 100%" />
                </el-form-item>
                <el-form-item label="仓库">
                  <el-input v-model="receiptForm.warehouseName" placeholder="例如 Main Warehouse" />
                </el-form-item>
                <el-form-item label="收货日期">
                  <el-date-picker v-model="receiptForm.receivedDate" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="收货人">
                  <el-input v-model="receiptForm.receivedBy" placeholder="例如 admin" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitReceipt">保存收货</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>收货记录</h3>
              <span>到货入库流水</span>
            </div>
            <el-table :data="receipts" empty-text="暂无收货记录">
              <el-table-column label="收货单号" min-width="150">
                <template #default="{ row }">
                  <span class="table-code">{{ row.receiptNo }}</span>
                </template>
              </el-table-column>
              <el-table-column label="订单号" min-width="150">
                <template #default="{ row }">
                  {{ orderNoMap[row.orderId] ?? row.orderId }}
                </template>
              </el-table-column>
              <el-table-column label="SKU" min-width="180">
                <template #default="{ row }">
                  {{ row.skuName }} / {{ row.skuCode }}
                </template>
              </el-table-column>
              <el-table-column label="数量" min-width="90" prop="receivedQty" />
              <el-table-column label="仓库" min-width="140" prop="warehouseName" />
              <el-table-column label="收货日期" min-width="120" prop="receivedDate" />
              <el-table-column label="收货人" min-width="120" prop="receivedBy" />
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="库存操作">
        <div class="panel-grid panel-grid--equal">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>出库登记</h3>
              <span>减少现有库存并形成出库记录</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="出库单号">
                  <el-input v-model="outboundForm.outboundNo" placeholder="例如 OUT-202604-002" />
                </el-form-item>
                <el-form-item label="库存项">
                  <el-select v-model="outboundForm.stockId" placeholder="请选择库存">
                    <el-option
                      v-for="item in stocks"
                      :key="item.id"
                      :label="`${item.skuName} / ${item.warehouseName} / 可用:${item.availableQty}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="出库数量">
                  <el-input-number v-model="outboundForm.outboundQty" :min="1" style="width: 100%" />
                </el-form-item>
                <el-form-item label="出库日期">
                  <el-date-picker v-model="outboundForm.outboundDate" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="出库人">
                  <el-input v-model="outboundForm.outboundBy" />
                </el-form-item>
                <el-form-item label="用途">
                  <el-input v-model="outboundForm.purpose" placeholder="例如 办公领用" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitOutbound">保存出库</el-button>
            </el-form>
          </el-card>

          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>仓库调拨</h3>
              <span>在不同仓库之间移动库存</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="调拨单号">
                  <el-input v-model="transferForm.transferNo" placeholder="例如 TRF-202604-002" />
                </el-form-item>
                <el-form-item label="来源库存">
                  <el-select v-model="transferForm.stockId" placeholder="请选择来源库存">
                    <el-option
                      v-for="item in stocks"
                      :key="item.id"
                      :label="`${item.skuName} / ${item.warehouseName} / 可用:${item.availableQty}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="目标仓库">
                  <el-input v-model="transferForm.toWarehouse" placeholder="例如 Branch Warehouse" />
                </el-form-item>
                <el-form-item label="调拨数量">
                  <el-input-number v-model="transferForm.transferQty" :min="1" style="width: 100%" />
                </el-form-item>
                <el-form-item label="调拨日期">
                  <el-date-picker v-model="transferForm.transferDate" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="调拨人">
                  <el-input v-model="transferForm.transferBy" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitTransfer">保存调拨</el-button>
            </el-form>
          </el-card>
        </div>

        <div class="panel-grid">
          <el-card class="form-card" shadow="never">
            <div class="card-title">
              <h3>库存盘点</h3>
              <span>记录实际数量并校正库存台账</span>
            </div>
            <el-form label-position="top">
              <div class="form-grid-2">
                <el-form-item label="盘点单号">
                  <el-input v-model="checkForm.checkNo" placeholder="例如 CHK-202604-002" />
                </el-form-item>
                <el-form-item label="库存项">
                  <el-select v-model="checkForm.stockId" placeholder="请选择库存">
                    <el-option
                      v-for="item in stocks"
                      :key="item.id"
                      :label="`${item.skuName} / ${item.warehouseName} / 在库:${item.onHandQty}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item label="实际数量">
                  <el-input-number v-model="checkForm.actualQty" :min="0" style="width: 100%" />
                </el-form-item>
                <el-form-item label="盘点日期">
                  <el-date-picker v-model="checkForm.checkDate" type="date" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="盘点人">
                  <el-input v-model="checkForm.checkedBy" />
                </el-form-item>
                <el-form-item label="备注">
                  <el-input v-model="checkForm.remark" />
                </el-form-item>
              </div>
              <el-button type="primary" @click="submitCheck">保存盘点</el-button>
            </el-form>
          </el-card>

          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>出库 / 调拨 / 盘点记录</h3>
              <span>库存操作流水</span>
            </div>
            <el-tabs>
              <el-tab-pane label="出库">
                <el-table :data="outbounds" empty-text="暂无出库记录">
                  <el-table-column label="单号" min-width="140">
                    <template #default="{ row }">
                      <span class="table-code">{{ row.outboundNo }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="SKU" min-width="180">
                    <template #default="{ row }">
                      {{ row.skuName }} / {{ row.skuCode }}
                    </template>
                  </el-table-column>
                  <el-table-column label="仓库" min-width="140" prop="warehouseName" />
                  <el-table-column label="数量" min-width="90" prop="outboundQty" />
                  <el-table-column label="日期" min-width="120" prop="outboundDate" />
                  <el-table-column label="用途" min-width="180" prop="purpose" />
                </el-table>
              </el-tab-pane>
              <el-tab-pane label="调拨">
                <el-table :data="transfers" empty-text="暂无调拨记录">
                  <el-table-column label="单号" min-width="140">
                    <template #default="{ row }">
                      <span class="table-code">{{ row.transferNo }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="SKU" min-width="180">
                    <template #default="{ row }">
                      {{ row.skuName }} / {{ row.skuCode }}
                    </template>
                  </el-table-column>
                  <el-table-column label="来源仓库" min-width="140" prop="fromWarehouse" />
                  <el-table-column label="目标仓库" min-width="140" prop="toWarehouse" />
                  <el-table-column label="数量" min-width="90" prop="transferQty" />
                  <el-table-column label="日期" min-width="120" prop="transferDate" />
                </el-table>
              </el-tab-pane>
              <el-tab-pane label="盘点">
                <el-table :data="checks" empty-text="暂无盘点记录">
                  <el-table-column label="单号" min-width="140">
                    <template #default="{ row }">
                      <span class="table-code">{{ row.checkNo }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="SKU" min-width="180">
                    <template #default="{ row }">
                      {{ row.skuName }} / {{ row.skuCode }}
                    </template>
                  </el-table-column>
                  <el-table-column label="仓库" min-width="140" prop="warehouseName" />
                  <el-table-column label="账面" min-width="90" prop="systemQty" />
                  <el-table-column label="实际" min-width="90" prop="actualQty" />
                  <el-table-column label="差异" min-width="90" prop="varianceQty" />
                  <el-table-column label="日期" min-width="120" prop="checkDate" />
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="库存台账">
        <div class="panel-grid">
          <el-card class="table-card" shadow="never">
            <div class="card-title">
              <h3>库存列表</h3>
              <span>按 SKU、仓库和供应商维度统计</span>
            </div>
            <el-table :data="stocks" empty-text="暂无库存">
              <el-table-column label="SKU 编码" min-width="130">
                <template #default="{ row }">
                  <span class="table-code">{{ row.skuCode }}</span>
                </template>
              </el-table-column>
              <el-table-column label="SKU 名称" min-width="180" prop="skuName" />
              <el-table-column label="仓库" min-width="160" prop="warehouseName" />
              <el-table-column label="供应商" min-width="160">
                <template #default="{ row }">
                  {{ supplierNameMap[row.supplierId ?? -1] ?? '-' }}
                </template>
              </el-table-column>
              <el-table-column label="在库" min-width="90" prop="onHandQty" />
              <el-table-column label="预留" min-width="90" prop="reservedQty" />
              <el-table-column label="可用" min-width="90" prop="availableQty" />
              <el-table-column label="最近入库" min-width="120" prop="lastReceiptDate" />
            </el-table>
          </el-card>

          <el-card class="info-card" shadow="never">
            <div class="card-title">
              <h3>外部集成预留</h3>
              <span>当前只保留扩展位，不启用真实第三方对接</span>
            </div>
            <div class="list-chip-group">
              <span class="list-chip">飞书通知：预留</span>
              <span class="list-chip">企业微信通知：预留</span>
              <span class="list-chip">邮件通知：预留</span>
              <span class="list-chip">飞书/企微组织同步：预留</span>
            </div>
            <p class="panel-hint">
              现阶段不要求准备应用、回调地址、密钥或企业账号。后续接入时直接在既有通知和身份模型上扩展。
            </p>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref } from 'vue';
import {
  createPurchaseOrder,
  createPurchaseReceipt,
  createStockCheck,
  createStockOutbound,
  createStockTransfer,
  createSupplier,
  fetchInventoryStocks,
  fetchPurchaseOrders,
  fetchPurchaseReceipts,
  fetchStockChecks,
  fetchStockOutbounds,
  fetchStockTransfers,
  fetchSuppliers,
  type InventoryStock,
  type PurchaseOrder,
  type PurchaseReceipt,
  type StockCheck,
  type StockOutbound,
  type StockTransfer,
  type Supplier
} from '../../api/procurement';

const suppliers = ref<Supplier[]>([]);
const orders = ref<PurchaseOrder[]>([]);
const receipts = ref<PurchaseReceipt[]>([]);
const stocks = ref<InventoryStock[]>([]);
const outbounds = ref<StockOutbound[]>([]);
const transfers = ref<StockTransfer[]>([]);
const checks = ref<StockCheck[]>([]);

const supplierForm = reactive({
  code: '',
  name: '',
  contactPerson: '',
  contactPhone: '',
  paymentTerms: '',
  status: 'ACTIVE'
});

const orderForm = reactive({
  orderNo: '',
  supplierId: undefined as number | undefined,
  skuCode: '',
  skuName: '',
  quantity: 1,
  unitPrice: 100,
  warehouseName: 'Main Warehouse',
  orderDate: new Date().toISOString().slice(0, 10),
  expectedDate: '',
  status: 'CREATED',
  remark: ''
});

const receiptForm = reactive({
  receiptNo: '',
  orderId: undefined as number | undefined,
  receivedQty: 1,
  warehouseName: 'Main Warehouse',
  receivedDate: new Date().toISOString().slice(0, 10),
  receivedBy: 'admin'
});

const outboundForm = reactive({
  outboundNo: '',
  stockId: undefined as number | undefined,
  outboundQty: 1,
  outboundDate: new Date().toISOString().slice(0, 10),
  outboundBy: 'admin',
  purpose: ''
});

const transferForm = reactive({
  transferNo: '',
  stockId: undefined as number | undefined,
  toWarehouse: 'Branch Warehouse',
  transferQty: 1,
  transferDate: new Date().toISOString().slice(0, 10),
  transferBy: 'admin'
});

const checkForm = reactive({
  checkNo: '',
  stockId: undefined as number | undefined,
  actualQty: 0,
  checkDate: new Date().toISOString().slice(0, 10),
  checkedBy: 'admin',
  remark: ''
});

const supplierNameMap = computed<Record<number, string>>(() =>
  Object.fromEntries(suppliers.value.map((item) => [item.id, item.name]))
);

const orderNoMap = computed<Record<number, string>>(() =>
  Object.fromEntries(orders.value.map((item) => [item.id, item.orderNo]))
);

const receivableOrders = computed(() =>
  orders.value.filter((item) => item.status !== 'RECEIVED')
);

function orderStatusType(status: string) {
  if (status === 'RECEIVED') return 'success';
  if (status === 'PARTIAL_RECEIVED') return 'warning';
  if (status === 'ORDERED') return 'primary';
  return 'info';
}

async function loadData() {
  const [supplierList, orderList, receiptList, stockList, outboundList, transferList, checkList] = await Promise.all([
    fetchSuppliers(),
    fetchPurchaseOrders(),
    fetchPurchaseReceipts(),
    fetchInventoryStocks(),
    fetchStockOutbounds(),
    fetchStockTransfers(),
    fetchStockChecks()
  ]);
  suppliers.value = supplierList;
  orders.value = orderList;
  receipts.value = receiptList;
  stocks.value = stockList;
  outbounds.value = outboundList;
  transfers.value = transferList;
  checks.value = checkList;
}

async function submitSupplier() {
  if (!supplierForm.code || !supplierForm.name) {
    ElMessage.warning('请填写供应商编码和名称');
    return;
  }
  await createSupplier({
    code: supplierForm.code,
    name: supplierForm.name,
    contactPerson: supplierForm.contactPerson || undefined,
    contactPhone: supplierForm.contactPhone || undefined,
    paymentTerms: supplierForm.paymentTerms || undefined,
    status: supplierForm.status
  });
  supplierForm.code = '';
  supplierForm.name = '';
  supplierForm.contactPerson = '';
  supplierForm.contactPhone = '';
  supplierForm.paymentTerms = '';
  supplierForm.status = 'ACTIVE';
  ElMessage.success('供应商已保存');
  await loadData();
}

async function submitOrder() {
  if (!orderForm.orderNo || !orderForm.supplierId || !orderForm.skuCode || !orderForm.skuName) {
    ElMessage.warning('请填写完整采购订单信息');
    return;
  }
  await createPurchaseOrder({
    orderNo: orderForm.orderNo,
    supplierId: orderForm.supplierId,
    skuCode: orderForm.skuCode,
    skuName: orderForm.skuName,
    quantity: orderForm.quantity,
    unitPrice: orderForm.unitPrice,
    warehouseName: orderForm.warehouseName,
    orderDate: orderForm.orderDate,
    expectedDate: orderForm.expectedDate || undefined,
    status: orderForm.status,
    remark: orderForm.remark || undefined
  });
  orderForm.orderNo = '';
  orderForm.supplierId = undefined;
  orderForm.skuCode = '';
  orderForm.skuName = '';
  orderForm.quantity = 1;
  orderForm.unitPrice = 100;
  orderForm.warehouseName = 'Main Warehouse';
  orderForm.orderDate = new Date().toISOString().slice(0, 10);
  orderForm.expectedDate = '';
  orderForm.status = 'CREATED';
  orderForm.remark = '';
  ElMessage.success('采购订单已保存');
  await loadData();
}

async function submitReceipt() {
  if (!receiptForm.receiptNo || !receiptForm.orderId || !receiptForm.receivedDate || !receiptForm.receivedBy) {
    ElMessage.warning('请填写完整收货信息');
    return;
  }
  await createPurchaseReceipt({
    receiptNo: receiptForm.receiptNo,
    orderId: receiptForm.orderId,
    receivedQty: receiptForm.receivedQty,
    warehouseName: receiptForm.warehouseName,
    receivedDate: receiptForm.receivedDate,
    receivedBy: receiptForm.receivedBy
  });
  receiptForm.receiptNo = '';
  receiptForm.orderId = undefined;
  receiptForm.receivedQty = 1;
  receiptForm.warehouseName = 'Main Warehouse';
  receiptForm.receivedDate = new Date().toISOString().slice(0, 10);
  receiptForm.receivedBy = 'admin';
  ElMessage.success('收货记录已保存');
  await loadData();
}

async function submitOutbound() {
  if (!outboundForm.outboundNo || !outboundForm.stockId || !outboundForm.outboundDate || !outboundForm.outboundBy) {
    ElMessage.warning('请填写完整出库信息');
    return;
  }
  await createStockOutbound({
    outboundNo: outboundForm.outboundNo,
    stockId: outboundForm.stockId,
    outboundQty: outboundForm.outboundQty,
    outboundDate: outboundForm.outboundDate,
    outboundBy: outboundForm.outboundBy,
    purpose: outboundForm.purpose || undefined
  });
  outboundForm.outboundNo = '';
  outboundForm.stockId = undefined;
  outboundForm.outboundQty = 1;
  outboundForm.outboundDate = new Date().toISOString().slice(0, 10);
  outboundForm.outboundBy = 'admin';
  outboundForm.purpose = '';
  ElMessage.success('出库记录已保存');
  await loadData();
}

async function submitTransfer() {
  if (!transferForm.transferNo || !transferForm.stockId || !transferForm.toWarehouse || !transferForm.transferDate || !transferForm.transferBy) {
    ElMessage.warning('请填写完整调拨信息');
    return;
  }
  await createStockTransfer({
    transferNo: transferForm.transferNo,
    stockId: transferForm.stockId,
    toWarehouse: transferForm.toWarehouse,
    transferQty: transferForm.transferQty,
    transferDate: transferForm.transferDate,
    transferBy: transferForm.transferBy
  });
  transferForm.transferNo = '';
  transferForm.stockId = undefined;
  transferForm.toWarehouse = 'Branch Warehouse';
  transferForm.transferQty = 1;
  transferForm.transferDate = new Date().toISOString().slice(0, 10);
  transferForm.transferBy = 'admin';
  ElMessage.success('调拨记录已保存');
  await loadData();
}

async function submitCheck() {
  if (!checkForm.checkNo || !checkForm.stockId || !checkForm.checkDate || !checkForm.checkedBy) {
    ElMessage.warning('请填写完整盘点信息');
    return;
  }
  await createStockCheck({
    checkNo: checkForm.checkNo,
    stockId: checkForm.stockId,
    actualQty: checkForm.actualQty,
    checkDate: checkForm.checkDate,
    checkedBy: checkForm.checkedBy,
    remark: checkForm.remark || undefined
  });
  checkForm.checkNo = '';
  checkForm.stockId = undefined;
  checkForm.actualQty = 0;
  checkForm.checkDate = new Date().toISOString().slice(0, 10);
  checkForm.checkedBy = 'admin';
  checkForm.remark = '';
  ElMessage.success('盘点记录已保存');
  await loadData();
}

onMounted(loadData);
</script>
