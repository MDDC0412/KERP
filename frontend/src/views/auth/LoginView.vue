<template>
  <section class="login-page">
    <div class="login-card">
      <div class="login-side">
        <div class="login-side__badge">
          <el-icon><Grid /></el-icon>
          <span>KERP 本地测试环境</span>
        </div>
        <h1>企业 ERP 控制台</h1>
        <p>面向组织、人事、权限、审批、考勤、通知和资产管理的统一后台入口。</p>

        <div class="login-side__grid">
          <div class="login-side__panel">
            <strong>8+</strong>
            <span>核心业务域已接入</span>
          </div>
          <div class="login-side__panel">
            <strong>8080</strong>
            <span>本地服务默认端口</span>
          </div>
          <div class="login-side__panel">
            <strong>MySQL</strong>
            <span>Flyway 迁移与业务数据</span>
          </div>
          <div class="login-side__panel">
            <strong>JDK 17</strong>
            <span>当前推荐运行环境</span>
          </div>
        </div>
      </div>

      <div class="login-form-wrap">
        <h2>登录系统</h2>
        <p>输入系统账号后进入后台工作台。</p>

        <el-form class="login-form" label-position="top" @submit.prevent="submitLogin">
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入账号" size="large" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="form.password"
              placeholder="请输入密码"
              show-password
              size="large"
              type="password"
            />
          </el-form-item>
          <el-button :loading="submitting" native-type="submit" size="large" type="primary">
            登录 KERP
          </el-button>
        </el-form>

        <div class="login-demo">
          <strong>默认本地管理员</strong>
          <div>账号：`admin`</div>
          <div>密码：`password`</div>
        </div>

        <div class="login-note">
          当前页面只用于本机测试，后续可继续接入短信、邮件、微信或飞书登录能力。
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { Grid } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '../../api/auth';
import { saveSession } from '../../utils/session';

const router = useRouter();
const submitting = ref(false);

const form = reactive({
  username: 'admin',
  password: 'password'
});

async function submitLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请填写账号和密码');
    return;
  }

  submitting.value = true;
  try {
    const session = await login(form);
    saveSession(session);
    ElMessage.success('登录成功');
    await router.push('/');
  } catch (error) {
    ElMessage.error((error as Error).message);
  } finally {
    submitting.value = false;
  }
}
</script>
