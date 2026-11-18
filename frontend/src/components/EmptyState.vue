<template>
  <div class="empty-state">
    <AppIcon :name="icon" tone="ink" />
    <strong>{{ title }}</strong>
    <span>{{ description }}</span>
  </div>
</template>

<script setup lang="ts">
import AppIcon from './AppIcon.vue';

withDefaults(defineProps<{
  icon?: string;
  title: string;
  description: string;
}>(), {
  icon: 'spark'
});
</script>
