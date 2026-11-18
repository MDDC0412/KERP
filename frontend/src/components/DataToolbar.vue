<template>
  <div class="data-toolbar">
    <div class="data-toolbar__main">
      <label class="toolbar-search">
        <input
          :value="search"
          type="text"
          :placeholder="placeholder"
          @input="handleInput"
        />
      </label>
      <slot name="filters" />
    </div>
    <div class="data-toolbar__side">
      <span class="toolbar-count">{{ countLabel }}</span>
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  search: string;
  placeholder?: string;
  countLabel: string;
}>();

const emit = defineEmits<{
  'update:search': [value: string];
}>();

function handleInput(event: Event) {
  emit('update:search', (event.target as HTMLInputElement).value);
}
</script>
