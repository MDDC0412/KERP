<template>
  <span class="app-icon" :class="[`app-icon--${tone}`]" aria-hidden="true">
    <svg v-if="name === 'dashboard'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M4 13h6V5H4zM14 19h6v-8h-6zM14 5h6v4h-6zM4 19h6v-2H4z" />
    </svg>
    <svg v-else-if="name === 'department'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M4 20V8l8-4 8 4v12" />
      <path d="M9 20v-4h6v4M8 10h.01M12 10h.01M16 10h.01M8 13h.01M12 13h.01M16 13h.01" />
    </svg>
    <svg v-else-if="name === 'employee'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M12 12a4 4 0 1 0-4-4 4 4 0 0 0 4 4Z" />
      <path d="M5 20a7 7 0 0 1 14 0" />
    </svg>
    <svg v-else-if="name === 'role'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="m7 14 5-10 5 10" />
      <path d="M5 14h14" />
      <path d="M12 14v6" />
      <path d="M8 20h8" />
    </svg>
    <svg v-else-if="name === 'user'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M16 21v-2a4 4 0 0 0-4-4H7a4 4 0 0 0-4 4v2" />
      <circle cx="9.5" cy="7" r="4" />
      <path d="M17 11.5a3.5 3.5 0 1 0 0 7h2.5" />
    </svg>
    <svg v-else-if="name === 'approval'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <rect x="5" y="4" width="14" height="16" rx="2" />
      <path d="M8 9h8M8 13h5M8 17h4" />
      <path d="m16 16 1.5 1.5L20 15" />
    </svg>
    <svg v-else-if="name === 'workflow'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <circle cx="6" cy="6" r="2" />
      <circle cx="18" cy="6" r="2" />
      <circle cx="18" cy="18" r="2" />
      <circle cx="6" cy="18" r="2" />
      <path d="M8 6h8M18 8v8M16 18H8M6 16V8" />
    </svg>
    <svg v-else-if="name === 'attendance'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M7 2v4M17 2v4M4 8h16" />
      <rect x="4" y="4" width="16" height="16" rx="2" />
      <path d="m9 14 2 2 4-4" />
    </svg>
    <svg v-else-if="name === 'notification'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M15 17h5l-1.4-1.4A2 2 0 0 1 18 14.2V11a6 6 0 1 0-12 0v3.2a2 2 0 0 1-.6 1.4L4 17h5" />
      <path d="M10 20a2 2 0 0 0 4 0" />
    </svg>
    <svg v-else-if="name === 'audit'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M5 3h11l3 3v15H5z" />
      <path d="M14 3v4h4M8 11h8M8 15h8" />
    </svg>
    <svg v-else-if="name === 'asset'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M4 7.5 12 3l8 4.5v9L12 21l-8-4.5z" />
      <path d="M12 12 4 7.5M12 12l8-4.5M12 12v9" />
    </svg>
    <svg v-else-if="name === 'shield'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="M12 3 5 6v6c0 5 3.4 8.7 7 10 3.6-1.3 7-5 7-10V6Z" />
      <path d="m9.5 12 1.6 1.6 3.8-3.8" />
    </svg>
    <svg v-else-if="name === 'spark'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <path d="m12 3 1.8 4.8L19 9.6l-4.2 2.1L13 17l-1.8-5.3L7 9.6l5.2-1.8Z" />
    </svg>
    <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
      <circle cx="12" cy="12" r="8" />
      <path d="M12 8v4l2.5 2.5" />
    </svg>
  </span>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  name: string;
  tone?: 'sand' | 'ink' | 'blue' | 'green' | 'rose';
}>(), {
  tone: 'sand'
});
</script>
