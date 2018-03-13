<template>
    <input type="text" :value="value" @input="input($event.target.value)" @keyup.enter="enter">
</template>

<script lang="ts" src="./autocomplete.vue.ts"></script>

<style src="awesomplete/awesomplete.css"></style>
